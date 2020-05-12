(ns clj-oss.env
  (:require
    [clojure.tools.logging :as log]
    [clj-oss.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[clj-oss started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[clj-oss has shut down successfully]=-"))
   :middleware wrap-dev})
