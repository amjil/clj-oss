(ns clj-oss.db.models
  (:require
    [toucan.models :refer [defmodel]]))

(defmodel OssDeveloper :oss_developer)

(defmodel OssApplication :oss_application)

(defmodel OssResource :oss_resource)
