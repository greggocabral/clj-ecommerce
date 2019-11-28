(ns webdev.item.view
  (:require [hiccup.core :refer [html h]]
            [hiccup.page :refer [html5]]
            [webdev.web-utils :as wu]))

(defn render-price [price]
  (format "$%s" (/ price 100)))

(defn update-item-form [item]
  (html
    [:form
     {:method "POST" :action (format "/items/%s" (:id item))}
     [:input {:type :hidden
              :name "_method"
              :value "PUT"}]
     [:input {:type :hidden
              :name "checked"
              :value (if (:checked item) "false" "true")}]
     [:div.btn-group
      (if (:checked item)
        [:input.btn.btn-primary.btn-xs
         {:type :submit
          :value "Mark not done"}]
        [:input.btn.btn-primary.btn-xs
         {:type :submit
          :value "Mark done"}])]]))

(defn items-page [items]
  (let [body  [:div
               [:div.container.mb-3
                [:nav
                 {:aria-label "breadcrumb"}
                 [:ol.breadcrumb
                  [:li.breadcrumb-item [:a {:href "/"} "Home"]]
                  [:li.breadcrumb-item.active {:aria-current "page"} "All t-shirts"]]]]
               [:div.container.mb-3
                [:h1 "All t-shirts"]]
               [:div.container
                [:div.row
                 (if (seq items)
                   (map (fn [item]
                          [:div.col-sm-3.col-xs-12.mb-3
                           [:div.card
                            [:img.img-fluid {:alt (:name item), :src (:img_url item)}]
                            [:div.card-body
                             [:h3.card-title (:name item)]
                             [:p (if (> (:stock item) 0) "In stock" "Not in stock")]
                             [:div.row
                              [:div.col-sm-6
                               [:div
                                [:h4 (render-price (:price item))]]]
                              [:div.col-sm-6
                               [:div.text-right
                                [:a.btn.btn-primary {:href (format "/items/%s" (:id item))} "Details"]]]]]]])
                        items)
                   [:div.col-sm-offset-1 "No items in the list"])]]]]
    (wu/base-page "Remerify - Products" body)))

(defn item-page [item]
  (let [body  [:div
               [:div.container.mb-3
                [:nav
                 {:aria-label "breadcrumb"}
                 [:ol.breadcrumb
                  [:li.breadcrumb-item [:a {:href "/"} "Home"]]
                  [:li.breadcrumb-item [:a {:href "/items"} "All t-shirts"]]
                  [:li.breadcrumb-item.active {:aria-current "page"} (:name item)]]]]
               [:div.container.mb-5
                [:h1 (:name item)]]
               [:div.container.mb-5
                [:div.row
                 [:div.col-sm-6.col-xs-12
                   [:img.img-fluid {:alt (:name item), :src (:img_url item)}]]
                 [:div.col-sm-6.col-xs-12
                    [:h3.h3 (render-price (:price item))]
                    [:p
                     [:h6 (format "%s in stock" (:stock item))]]
                    [:p
                     (:description item)]
                    [:a.btn.btn-primary {:href (format "/checkout/%s" (:id item))} "Buy"]]]]]]
    (wu/base-page "Remerify - Product" body)))


(defn checkout-page [item]
  (let [body  [:div
               [:div.container.mb-3
                [:nav
                 {:aria-label "breadcrumb"}
                 [:ol.breadcrumb
                  [:li.breadcrumb-item [:a {:href "/"} "Home"]]
                  [:li.breadcrumb-item [:a {:href "/items"} "All t-shirts"]]
                  [:li.breadcrumb-item [:a {:href (format "/items/%s" (:id item))} (:name item)]]
                  [:li.breadcrumb-item.active {:aria-current "page"} "Checkout"]]]]
               [:div.container.mb-5
                [:h1 "Checkout"]]
               [:div.container.mb-5
                [:div.row
                 [:div.col-sm-1
                  [:img.img-thumbnail {:src (:img_url item)}]]
                 [:div.col-sm-1
                  [:h4 "1"]]
                 [:div.col-sm-8
                  [:h4
                   (str (:name item) " - " (:description item))]]
                 [:div.col-sm-2
                  [:h4 (render-price (:price item))]]]
                [:div.row
                 [:div.col-sm-10.text-right
                  [:h4 "Total"]]
                 [:div.col-sm-2
                  [:h4 (render-price (:price item))]]]]
               [:div.container.mb-5
                [:h2 "Your Information"]
                [:div.row
                 [:div.col-sm-12
                  [:form#paymentForm
                   {:method "POST" :action "/checkout/pay"}
                   [:input {:type :hidden
                            :name "id"
                            :value (:id item)}]
                   [:div.form-group.row
                    [:label.col-sm-2.col-form-label {:for "name"} "Name"]
                    [:div.col-sm-10
                     [:input#name.form-control-plaintext
                      {:type "text"
                       :name "name"
                       :required true
                       :placeholder "John Doe"}]]]
                   [:div.form-group.row
                    [:label.col-sm-2.col-form-label {:for "email"} "Email"]
                    [:div.col-sm-10
                     [:input#email.form-control-plaintext
                      {:type "text"
                       :name "email"
                       :required true
                       :placeholder "example@email.com"}]]]
                   [:div.form-group.row
                    [:label.col-sm-2.col-form-label {:for "address"} "Delivery Address"]
                    [:div.col-sm-10
                     [:input#address.form-control-plaintext
                      {:type "text"
                       :name "address"
                       :required true
                       :placeholder "W836 Mulberry Street, Kalispell, MT 59901"}]]]
                   [:div.form-group.row
                    [:label.col-sm-2.col-form-label {:for "address"} "Cardholder Name"]
                    [:div.col-sm-10
                     [:input#cardholder-name.form-control-plaintext
                      {:type "text"
                       :name "cardholder-name"
                       :required true
                       :placeholder "John Doe"}]]]
                   [:div.form-group.row
                    [:label.col-sm-2.col-form-label {:for "address"} "Card information"]
                    [:div.col-sm-10
                     [:div#card-element "<!-- placeholder for Elements -->"]]]
                   [:button.btn.btn-primary {:id "card-button"}
                    "Submit Payment"]]]]]]]
    (wu/base-page "Remerify - Checkout" body :checkout)))

(defn payment-success-page [item name address]
  (let [body  [:div
               [:div.container.mb-3
                [:nav
                 {:aria-label "breadcrumb"}
                 [:ol.breadcrumb
                  [:li.breadcrumb-item [:a {:href "/"} "Home"]]
                  [:li.breadcrumb-item [:a {:href "/items"} "All t-shirts"]]
                  [:li.breadcrumb-item [:a {:href (format "/items/%s" (:id item))} (:name item)]]
                  [:li.breadcrumb-item.active {:aria-current "page"} "Thank you"]]]]
               [:div.container.mb-3
                [:h1 (format "Thank you %s!" name)]]
               [:div.container.mb-3
                [:p (format "You just bought a %s." (:name item))]
                [:p (format "It will be soon delivered to %s." address)]]]]
    (wu/base-page "Remerify - Thank you" body)))

(defn payment-error-page [error item]
  (let [body  [:div
               [:div.container.mb-3
                [:nav
                 {:aria-label "breadcrumb"}
                 [:ol.breadcrumb
                  [:li.breadcrumb-item [:a {:href "/"} "Home"]]
                  [:li.breadcrumb-item [:a {:href "/items"} "All t-shirts"]]
                  (when item
                    [:li.breadcrumb-item [:a {:href (format "/items/%s" (:id item))} (:name item)]])
                  [:li.breadcrumb-item.active {:aria-current "page"} "Error"]]]]
               [:div.container.mb-3
                [:h1 "There was a problem with your purchase"]]
               [:div.container.mb-3
                [:p (str "The following error ocurred during payment: " error)]]
               (when item
                 [:div.container.mb-3
                  [:p "Please " [:a {:href (format "/checkout/%s" (:id item))} "try again"]]])]]
    (wu/base-page "Remerify - Checkout" body)))
