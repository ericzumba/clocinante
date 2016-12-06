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

(def samples (System/getenv "SAMPLES"))

(defn lines
  [filename]
    (line-seq
      (io/reader filename)))

(def sample-urls
  (map
    #(curl/url %)
    (lines samples)))

(defn replace-host
  [host port url]
  (assoc url :host host :port port))

(defn perform-request
  [url]
  (let
    [resp @(http/get (str url) {:headers {"Content-Type" "application/json; charset=utf-8"}})
     status (:status resp)]
    (println (str url " " status))
    resp))

(defn make-case
  [url]
  (let [path (:path url)
        expected (perform-request (replace-host cano-host cano-port url))
        actual (perform-request (replace-host test-host test-port url))]
    {:path path
     :expected expected
     :actual actual}))

(defn resp-json
  [resp transform]
  (json/read-str (transform (:body resp))))

(def mappings
  (map make-case sample-urls))

(defn transform
  [s]
  (string/replace
    s
    (str test-host ":" test-port)
    (str cano-host ":" cano-port)))

(facts "all urls match expectations"
  (doseq [case (filter #(= (:status (:expected %)) 200) mappings)]
    (fact {:midje/description (:path case) }
          (resp-json (:actual case) transform) => (resp-json (:expected case) identity))))