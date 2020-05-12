(ns clj-oss.middleware
  (:require
    [clj-oss.env :refer [defaults]]
    [clj-oss.config :refer [env]]
    [ring.middleware.defaults :refer [site-defaults wrap-defaults]]))

(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      (wrap-defaults
        (-> site-defaults
            (assoc-in [:security :anti-forgery] false)))))
