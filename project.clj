(defproject clocinante "0.0.1-SNAPSHOT"
  :description "Cool new project to do things and stuff"
  :dependencies [[org.clojure/clojure "1.7.0"]]
  :profiles {:dev {:dependencies [[midje "1.7.0"]
                                  [org.clojure/data.json "0.2.6"]
                                  [http-kit "2.2.0"]]}
             :midje {:dependencies [[midje "1.7.0"]] }})
