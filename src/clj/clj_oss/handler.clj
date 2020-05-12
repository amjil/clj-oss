(ns clj-oss.handler
  (:require
    [clj-oss.middleware :as middleware]
    [clj-oss.routes.services :refer [service-routes]]
    [reitit.swagger-ui :as swagger-ui]
    [reitit.ring :as ring]
    [clj-oss.env :refer [defaults]]
    [mount.core :as mount]))

(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

(mount/defstate app-routes
  :start
  (ring/ring-handler
    (ring/router
      [["/" {:get
             {:handler (constantly {:status 301 :headers {"Location" "/api/api-docs/index.html"}})}}]
       (service-routes)])))

(defn app []
  (middleware/wrap-base #'app-routes))
