(ns webdev.item.handler
  (:require [webdev.item.model :as items]
            [webdev.item.view :as render-items]))

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

(defn handle-update-item! [req]
  (let [db (:webdev/db req)
        item-id (some-> (get-in req [:params :item-id])
                        java.util.UUID/fromString)
        checked? (= "true" (get-in req [:params "checked"]))
        existed? (items/update-item! db item-id checked?)]
    (if existed?
      {:status 302
       :headers {"Location" "/items"}
       :body ""}
      {:status 404
       :headers {}
       :body "List not found "})))

(defn handle-pay! [req]
  (let [db (:webdev/db req)
        item-id (some-> (get-in req [:params "id"])
                        java.util.UUID/fromString)
        name (get-in req [:params "name"])
        email (get-in req [:params "email"])
        address (get-in req [:params "address"])]
    (if-not (and name email address)
      {:status 404
       :headers {}
       :body (str "Missing params pram" (:params req))}
      (let [item (items/read-item-by-id db item-id)
            result (items/decrease-item-stock! db item-id)]
        (if result
          {:status 200
           :headers {}
           :body (render-items/payment-success-page item name address)}
          {:status 404
           :headers {}
           :body (render-items/payment-error-page item)})))))
