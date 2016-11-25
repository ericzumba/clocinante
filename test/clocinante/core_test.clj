(ns clocinante.core-test
  (:require [midje.sweet :refer :all]
            [clocinante.core :refer :all]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [org.httpkit.client :as http]))

(def host
  (System/getenv "HOST"))

(def recordings
  (System/getenv "RECORDINGS"))

(defn from-json-file
  [file]
  (json/read-str
    (slurp file)
    :key-fn keyword))

(def mappings-files
  (let [dir (str recordings "/mappings")]
    (map
      from-json-file
      (filter
        #(not (.isDirectory %))
        (file-seq (io/file dir))))))

(defn body-path
  [filename]
  (str recordings "/__files/" filename))

(defn host-path
  [path]
  (str host "/" path))

(defn perform-request
  [uri]
    (json/read-str
      (:body @(http/get uri))
      :key-fn keyword))

(defn make-case
  [mapping-file]
  (let [url (:url (:request mapping-file))
        body (:bodyFileName (:response mapping-file))
        expected (from-json-file (body-path body))
        actual (perform-request (host-path url))]
    {:url url
     :expected expected
     :actual actual}))

(def mappings
  (map make-case mappings-files))

(facts
  "all urls match expectations"
  (doseq [case mappings]
    (fact {:midje/description "test"}
          (:actual case) => (:expected case))))