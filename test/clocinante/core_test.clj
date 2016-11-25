(ns clocinante.core-test
  (:require [midje.sweet :refer :all]
            [clocinante.core :refer :all]
            [clojure.java.io :as io]
            [clojure.data.json :as json]))

(defn from-json-file
  [file]
  (do
    (println file)
    (json/read-str
      (slurp file)
      :key-fn keyword)))

(def mappings-files
  (let [dir (System/getenv "MAPPINGS_DIR")]
    (map
      from-json-file
      (filter
        #(not (.isDirectory %))
        (file-seq (io/file dir))))))

(defn extract-url
  [mapping-file]
  (:url (:request mapping-file)))

(def mappings
  (map extract-url mappings-files))

(facts "first test"
       (fact "it tests"
             mappings => "map"))
