(ns user
  "Userspace functions you can run by default in your local REPL."
  (:require
   [clj-oss.config :refer [env]]
   [clojure.pprint]
   [clojure.spec.alpha :as s]
   [expound.alpha :as expound]
   [mount.core :as mount]
   [clj-oss.core :refer [start-app]]
   [clj-oss.db.core]
   [luminus-migrations.core :as migrations]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(add-tap (bound-fn* clojure.pprint/pprint))

(defn start []
  (mount/start))

(defn stop []
  (mount/stop))

(defn restart
  "Restarts application."
  []
  (mount/stop)
  (mount/start))

(defn restart-db
  "Restarts database."
  []
  (mount/stop #'clj-oss.db.core/*db*)
  (mount/start #'clj-oss.db.core/*db*))

(defn reset-db
  "Resets database."
  []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate
  "Migrates database up for all outstanding migrations."
  []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback
  "Rollback latest database migration."
  []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration
  "Create a new up and down migration file with a generated timestamp and `name`."
  [name]
  (migrations/create name (select-keys env [:database-url])))
