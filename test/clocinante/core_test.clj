(ns clocinante.core-test
  (:require [midje.sweet :refer :all]
            [clocinante.core :refer :all]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [clojure.string :as string]
            [cemerick.url :as curl]
            [org.httpkit.client :as http]))

(def cano-host (System/getenv "CANO_HOST"))
(def cano-port (read-string (System/getenv "CANO_PORT")))
(def test-host (System/getenv "TEST_HOST"))
(def test-port (read-string (System/getenv "TEST_PORT")))

(def sample-urls
  (remove
    nil?
    (map
      #(try
        (curl/url %)
        (catch java.net.MalformedURLException e nil))
      (line-seq (java.io.BufferedReader. *in*)))))

(defn replace-host
  [host port url]
  (assoc url :host host :port port))

(defn perform-request
  [url]
  @(http/get
     (str url)
     {:headers
      {"Content-Type" "application/json; charset=utf-8"}}))

(defn make-case
  [url]
  (let [expected (perform-request (replace-host cano-host cano-port url))
        actual (perform-request (replace-host test-host test-port url))]
    {:url url
     :expected expected
     :actual actual}))

(defn resp-json
  [resp pre-transform]
    (json/read-str
      (pre-transform (:body resp))
      :key-fn keyword))

(def mappings
  (map make-case sample-urls))

(defn pre-transform
  [s]
  (string/replace
    s
    (str test-host ":" test-port)
    (str cano-host ":" cano-port)))

(defn only-string
  [resp]
  (instance? java.lang.String resp))

(facts "all urls match expectations"
  (doseq [case (filter #(and
                         (= (:status (:expected %)) 200)
                         (only-string (:body (:expected %)))) mappings)]
    (fact {:midje/description (replace-host test-host test-port (:url case)) }
          (resp-json
            (:actual case)
            pre-transform)
          =>
          (resp-json
            (:expected case)
            identity))))