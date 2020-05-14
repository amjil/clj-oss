(ns clj-oss.services.application
  (:require
    [toucan.db :as db]
    [honeysql.core :as hsql]
    [clojure.string :as str]
    [buddy.core.hash :as hash]
    [buddy.core.codecs :as codecs]
    [clj-oss.db.models :as models]
    [clj-oss.config :refer [env]]))

(defn uuid []
  (-> (.toString (java.util.UUID/randomUUID))
      (str/replace #"-" "")))

(defn create-application [user-id spaces]
  (let [apikey (uuid)
        apisecret (-> (str apikey (:global-secret env))
                      (hash/sha256)
                      (codecs/bytes->hex))
        model {:user_id user-id
               :apikey apikey :apisecret apisecret
               :total_spaces spaces}]
    (db/insert! models/OssApplication model)))

(defn delete-application [user-id id]
  (db/update-where! {:status 0}
    :user_id user-id :id id))

(defn update-spaces [user-id id spaces]
  (db/update-where! {:total_spaces (hsql/call :+ :total_spaces spaces)}
    :user_id user-id :id id))
