(ns clocinante.core-test
  (:require [midje.sweet :refer :all]
            [clocinante.core :refer :all]
            [clojure.java.io :as io]
            [clojure.data.json :as json]))

(def recordings
  (System/getenv "RECORDINGS"))

(defn from-json-file
  [file]
  (do
    (json/read-str
      (slurp file)
      :key-fn keyword)))

(def mappings-files
  (let [dir (str recordings "/mappings")]
    (map
      from-json-file
      (filter
        #(not (.isDirectory %))
        (file-seq (io/file dir))))))

(defn read-body
  [filename]
  (let [dir (str recordings "/__files")]
    from-json-file (str dir "/" filename)))

(defn make-case
  [mapping-file]
  (let [case {:url (:url (:request mapping-file))}
        body-filename (:bodyFileName (:response mapping-file))]
    (into case {:expected (read-body body-filename)})))

(def mappings
  (map make-case mappings-files))

(facts
  "all urls match expectations"
  (doseq [case mappings]
    (fact {:midje/description "opa"}
          (:expected case) => nil)))