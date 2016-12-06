(ns clocinante.core-test
  (:require [midje.sweet :refer :all]
            [clocinante.core :refer :all]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [org.httpkit.client :as http]))

(def test-host (System/getenv "TEST_HOST"))
(def cano-host (System/getenv "CANO_HOST"))

(def samples (System/getenv "SAMPLES"))

(defn lines
  [filename]
    (line-seq
      (io/reader filename)))

(def sample-urls
  (map
    #(io/as-url %)
    (lines samples)))

(defn request
  [host path]
  (str host "/" path))

(defn perform-request
  [uri]
    (json/read-str
      (:body @(http/get uri))
      :key-fn keyword))

(defn make-case
  [url]
  (let [expected (perform-request (request cano-host (.getPath url)))
        actual (perform-request (request test-host (.getPath url)))]
    {:url url
     :expected expected
     :actual actual}))

(def mappings
  (map make-case sample-urls))

(facts "all urls match expectations"
  (doseq [case mappings]
    (fact {:midje/description "test" }
          (:actual case)  => (:expected case))))