FROM clojure:lein-2.7.1-alpine 

WORKDIR /usr/app

COPY .midje.clj  .midje.clj 
COPY project.clj project.clj
COPY src src
COPY test test 

VOLUME ["/samples"]
