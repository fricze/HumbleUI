(ns build
  (:require [clojure.tools.build.api :as b]))

(def basis (delay (b/create-basis {:project "deps.edn"})))

(defn compile-java [& _]
  (b/delete {:path "target"})
  (b/javac {:src-dirs  ["java"]
            :basis     @basis
            :class-dir "target/classes"}))

