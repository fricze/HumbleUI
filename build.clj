(ns build
  (:require [clojure.tools.build.api :as b]))

(defn compile-java [& _]
  (b/delete {:path "target"})
  (b/javac {:src-dirs  ["java"]
            :class-dir "target/classes"}))

