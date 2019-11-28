(ns webdev.item.handler
  (:require [webdev.item.model :as items]
            [webdev.item.view :as render-items]
            [cheshire.core :as json]
            [clj-http.client :as http]))

(defn handle-index-items [req]
  (let [db (:webdev/db req)
        items (items/read-items db)]
    {:status 200
     :headers {}
     :body (render-items/items-page items)}))

(defn handle-details-item [req]
  (let [db (:webdev/db req)
        item-id (some-> (get-in req [:params :item-id])
                        java.util.UUID/fromString)
        item (items/read-item-by-id db item-id)]
    {:status 200
     :headers {}
     :body (render-items/item-page item)}))

(defn handle-checkout-item [req]
  (let [db (:webdev/db req)
        item-id (some-> (get-in req [:params :item-id])
                        java.util.UUID/fromString)
        item (items/read-item-by-id db item-id)]
    {:status 200
     :headers {}
     :body (render-items/checkout-page item)}))

(defn handle-pay! [req]
  (let [db (:webdev/db req)
        item-id (some-> (get-in req [:params "id"])
                        java.util.UUID/fromString)
        name (get-in req [:params "name"])
        email (get-in req [:params "email"])
        address (get-in req [:params "address"])
        pmId (get-in req [:params "pmId"])
        stripe-private-key (System/getenv "STRIPE_PRIVATE_KEY")]
    (if-not (and name email address stripe-private-key)
      {:status 404
       :headers {}
       :body (render-items/payment-error-page "Missing parameters" nil)}
      (let [item (items/read-item-by-id db item-id)
            result (items/decrease-item-stock! db item-id)]
        (if result
          (let [req-params {"amount"               (:price item)
                            "currency"             "usd"
                            "confirm"              true
                            "payment_method"       pmId
                            "payment_method_options" {:card {:on_requires_action "error"}}}
                req        {:basic-auth       [stripe-private-key]
                            :query-params     req-params
                            :throw-exceptions false}
                payment-result (try
                                 (-> "https://api.stripe.com/v1/payment_intents"
                                     (http/post req)
                                     (update :body json/parse-string))
                                 (catch Exception e {:error e}))]
            (if-let [error (:error payment-result)]
              (do
                (items/increase-item-stock! db item-id)
                (prn "stripe payment error" error)
                {:status 404
                 :headers {}
                 :body (render-items/payment-error-page "server error" item)})
              (if-let [payment-error (get-in payment-result [:body "error"])]
                (let [error-type (get payment-error "type")
                      error-code (get payment-error "code")
                      decline-code (get payment-error "decline_code")
                      error-string (str error-type " > " error-code " > " decline-code)]
                  (items/increase-item-stock! db item-id)
                  {:status 404
                   :headers {}
                   :body (render-items/payment-error-page error-string item)})
                {:status 200
                 :headers {}
                 :body (render-items/payment-success-page item name address)}))))))))
