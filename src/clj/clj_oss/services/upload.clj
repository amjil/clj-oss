(ns clj-oss.services.upload
  (:require
    [clojure.tools.logging :as log]
    [clojure.string :as str]
    [clojure.java.io :as io]
    [clj-oss.config :refer [env]]
    [clj-oss.middleware.exception :as exception]
    [java-time :as time]
    [buddy.core.hash :as hash]
    [buddy.core.codecs :as codecs]
    [toucan.db :as db]
    [clj-oss.db.models :as models]))

(defn- get-config [id]
  (-> env :error-msgs (get id)))

(defn save-file [file params]
  ;; check api
  (let [data (db/select-one [models/OssApplication :appscret] :apikey (:apikey params))
        secret (:secret)
        sign (-> (str (:apikey params) (:path params) (:uuid params) secret)
                 (hash/sha256)
                 (codecs/bytes->hex))]

    (if (not= sign (:sign params))
      (throw (ex-info "check" {:type ::exception/check :msg (get-config :no-auth)}))))

    ;; Todo check space configured

  (let [path (str "public/" (:apikey params) "/" (:path params))
        filename (str path "/" (:filename file))]
    (io/make-parents filename)
    (io/copy (:tempfile file) (io/file filename))
    filename))
