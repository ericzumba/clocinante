(ns clocinante.core-test
  (:require [midje.sweet :refer :all]
            [clocinante.core :refer :all]
            [clojure.java.io :as io]
            [clojure.data.json :as json]))

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

(defn make-case
  [mapping-file]
  (let [case {:url (:url (:request mapping-file))}
        body (:bodyFileName (:response mapping-file))]
    (into case {:expected (from-json-file (body-path body))})))

(def mappings
  (map make-case mappings-files))

(facts
  "all urls match expectations"
  (doseq [case mappings]
    (fact {:midje/description "opa"}
          (:url case) => (:expected case))))