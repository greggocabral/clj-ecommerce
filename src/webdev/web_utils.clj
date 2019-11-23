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

(defn base-page [title body]
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
         [:script {:src "/bootstrap/css/bootstrap.min.js"}]
         [:script {:src "/js/scripts.js"}]))
