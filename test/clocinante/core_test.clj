(ns clocinante.core-test
  (:require [midje.sweet :refer :all]
            [clocinante.core :refer :all]
            [clojure.java.io :as io]
            [clojure.data.json :as json]))

(def recordings (System/getenv "RECORDINGS"))

(defn from-json-file
  [file]
  (do
    (println file)
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

(defn extract-url
  [mapping-file]
  (let [case (:url (:request mapping-file))]
    case))

(def mappings
  (map extract-url mappings-files))

(facts "first test"
       (fact "it tests"
             mappings => "map"))
