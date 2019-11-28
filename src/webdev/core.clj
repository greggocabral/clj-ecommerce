(ns webdev.core
  (:require [webdev.item.model :as items]
            [webdev.item.handler :as items-handler]
            [webdev.web-utils :as wu]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [compojure.core :refer [defroutes ANY GET GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]
            [hiccup.core :refer [html h]]
            [hiccup.page :refer [html5]]))

(def db (or (System/getenv "DATABASE_URL")
            "jdbc:postgresql://localhost/webdev01"))

(defn index-page [req]
  (let [body  [:div.container
               [:div.row
                [:div.col-sm-8
                 [:h1 "Welcome!"]
                 [:p
                  [:a {:href "/items"}
                   "See all our t-shirts"]]]]]
        page (wu/base-page "Remerify - Home" body)]
    {:status 200 :body page :headers {}}))

(defn resource-not-found-page [req]
 (let [body [:div.container
             [:div.row
              [:div.col-sm-8
               [:h1 "Resource not found"]
               [:a {:href "/"}
                "Go home"]]]]]
   (wu/base-page "Remerify - Not found" body)))

(defroutes routes
  (GET "/" [] index-page)

  (GET "/items" [] items-handler/handle-index-items)
  (GET "/items/:item-id" [] items-handler/handle-details-item)
  (GET "/checkout/:item-id" [] items-handler/handle-checkout-item)

  (POST "/checkout/pay" [] items-handler/handle-pay!)

  (not-found resource-not-found-page))


(defn wrap-db [handler]
  (fn [req]
    (handler (assoc req :webdev/db db))))

(defn wrap-server [handler]
  (let [server "mymac"]
    (fn [req]
      (assoc-in (handler req) [:headers "Server"] server))))

(def sim-methods {"PUT" :put
                  "DELETE" :delete})

(defn wrap-simulated-methods [handler]
  (fn [req]
    (if-let [method (and (= :post (:request-method req))
                         (sim-methods (get-in req [:params "_method"])))]
      (handler (assoc req :request-method method))
      (handler req))))

(def app
  (wrap-server
    (wrap-file-info
      (wrap-resource
        (wrap-db
          (wrap-params
            (wrap-simulated-methods
              routes)))
        "static"))))

(defn -main [port]
  (items/create-table! db)
  (jetty/run-jetty app
                   {:port (Integer. port)}))

(defn -dev-main [port]
  (items/create-table! db)
  (jetty/run-jetty (wrap-reload (var app))
                   {:port (Integer. port)}))
