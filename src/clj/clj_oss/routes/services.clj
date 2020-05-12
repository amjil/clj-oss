(ns clj-oss.routes.services
  (:require
    [reitit.swagger :as swagger]
    [reitit.swagger-ui :as swagger-ui]
    [reitit.ring.coercion :as coercion]
    [reitit.coercion.spec :as spec-coercion]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.multipart :as multipart]
    [reitit.ring.middleware.parameters :as parameters]
    [clj-oss.middleware.formats :as formats]
    [clj-oss.middleware.exception :as exception]
    [ring.util.http-response :refer :all]
    [clojure.java.io :as io]
    [clojure.tools.logging :as log]
    [spec-tools.data-spec :as ds]

    [clj-oss.services.service :as service]))


(defn service-routes []
  ["/api"
   {:coercion spec-coercion/coercion
    :muuntaja formats/instance
    :swagger {:id ::api}
    :middleware [;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 exception/exception-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware
                 ;; multipart
                 multipart/multipart-middleware]}

   ;; swagger documentation
   ["" {:no-doc true
        :swagger {:info {:title "my-api"
                         :description "https://cljdoc.org/d/metosin/reitit"}}}

    ["/swagger.json"
     {:get (swagger/create-swagger-handler)}]

    ["/api-docs/*"
     {:get (swagger-ui/create-swagger-ui-handler
             {:url "/api/swagger.json"
              :config {:validator-url nil}})}]]

   ["/ping"
    {:get (constantly (ok {:message "pong"}))}]

   ["/files"
    {:swagger {:tags ["files"]}}

    ["/upload"
     {:post {:summary "upload a file"
             :parameters {:multipart {:file multipart/temp-file-part}
                          :query {:apikey string? :path string? :uuid string? :sign string?}}
             :responses {200 {:body {:name string?, :size int?, :url string?}}}
             :handler (fn [{{{:keys [file]} :multipart params :query} :parameters}]
                        (let [filename (service/save-file file)]
                          {:status 200
                           :body {:name (:filename file)
                                  :url filename
                                  :size (:size file)}}))}}]

    ["/download"
     {:get {:summary "downloads a file"
            ; :swagger {:produces ["image/png"]}
            :parameters {:query {:filename string?}}
            :handler (fn [{{{:keys [filename]} :query} :parameters}]
                       {:status 200
                        :headers {"Content-Type" "image/jpeg"}
                        :body (io/input-stream filename)})}}]]])
