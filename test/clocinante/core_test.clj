(ns clocinante.core-test
  (:require [midje.sweet :refer :all]
            [clocinante.core :refer :all]
            [clojure.java.io :as io]
            [clojure.data.json :as json]))

(defn json-from-file
  [file]
  (do
    (println file)
    (json/read-str (slurp file))))

(def mappings
  (let [dir (System/getenv "MAPPINGS_DIR")]
    (println "fetching mappings")
    (map
      json-from-file
      (filter
        #(not (.isDirectory %))
        (file-seq (io/file dir))))))

(facts "first test"
       (fact "it tests"
             mappings => "map"))
