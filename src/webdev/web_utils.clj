(ns webdev.web-utils
  (:require [hiccup.core :refer [html h]]
            [hiccup.page :refer [html5]]))

(defn remerify-jumbotron []
  (html5
   [:div.jumbotron.jumbotron-fluid
    [:div.container
     [:h1.display-4 "Remerify"]
     [:p.lead
      "Only cool t-shirts"]]]))

(defn base-page
  ([title body]
   (base-page title body nil))
  ([title body page-js]
   (html5 {:lang :en}
          [:head
           [:title title]
           [:meta {:name :viewport
                   :content "width=device-width, initial-scale=1.0"}]
           [:link {:href "/bootstrap/css/bootstrap.min.css"
                   :rel :stylesheet}]]
          [:body
           (remerify-jumbotron)
           body]
          (case page-js
            :checkout (seq [[:script {:src "/bootstrap/js/bootstrap.min.js"}]
                            [:script {:src "https://js.stripe.com/v3/"}]
                            [:script {:src "/js/scripts.js"}]])
            (seq [[:script {:src "/bootstrap/js/bootstrap.min.js"}]
                  [:script {:src "https://js.stripe.com/v3/"}]])))))
