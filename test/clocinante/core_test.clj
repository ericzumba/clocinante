(ns clocinante.core-test
  (:require [midje.sweet :refer :all]
            [clocinante.core :refer :all]
            [clojure.java.io :refer :all]))

(def mappings
  (let [dir (System/getenv "MAPPINGS_DIR")]
    (println "fetching mappings")
    (println dir)
    (file-seq (file dir))))

(facts "first test"
       (fact "it tests"
             mappings => "map"))
