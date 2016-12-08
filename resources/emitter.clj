(ns ^{:doc "Adding an emitter that brags about fact results"}
    example.pass-emitter
      (:require [midje.emission.plugins.util :as util]
                [midje.data.fact :as fact]
                [midje.emission.plugins.default :as default]
                [midje.emission.plugins.default-failure-lines :as lines]
                [midje.emission.state :as state]))

(defmethod lines/messy-lines :actual-result-did-not-match-expected-value [m]
  (let [expected (:expected-result m)
        actual (:actual m)]
    (list
      (#'lines/diffs [actual expected])
      (#'lines/notes m))))

(defn finishing-top-level-fact
  [fact]
  (util/emit-one-line
    (format "Dude! `%s`!"
            (fact/name fact))))

(def emission-map
  (assoc default/emission-map
    :finishing-top-level-fact finishing-top-level-fact))

;; Here's where the installation happens.
(state/install-emission-map emission-map)
