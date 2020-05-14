(ns clj-oss.services.developer
  (:require
    [toucan.db :as db]
    [clojure.string :as str]
    [clj-oss.db.models :as models]))


(defn create [params]
  ;;
  (let [model {:mobile (:mobile params)
               :password (:password params)}]
    (db/insert! models/OssDeveloper model)))
