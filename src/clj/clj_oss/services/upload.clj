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
  (let [data (db/select-one [models/OssApplication :apisecret :id :user_id] :apikey (:apikey params))
        secret (:apisecret data)
        sign (-> (str (:apikey params) (:uuid params) secret)
                 (hash/sha256)
                 (codecs/bytes->hex))]

    (if (not= sign (:sign params))
      (throw (ex-info "check" {:type ::exception/check :msg (get-config :no-auth)})))

    ;; Todo check space configured

    (if (= "local" (env :storage))
      (let [path (str "public/" (:id data))
            file-suffix (last (str/split (:filename file) #"\."))
            uuid (:uuid params)
            ;; new filename public/1/A0/43/A043xxxxxxxxx.jpg
            filename (str path "/" (subs uuid 0 2) "/" (subs uuid 2 4) "/" uuid "." file-suffix)]
        ;; move file to public folder
        (io/make-parents filename)
        (io/copy (:tempfile file) (io/file filename))
        ;;insert db
        (let [model {:app_id (:id data) :user_id (:user_id data)
                     :file_size (:size file)
                     :content_type (:content-type file)
                     :original_name (:filename file)
                     :file_name uuid}]
          (db/insert! models/OssResource model))))))
