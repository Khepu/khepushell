(ns khepushell.utilities
  (:require [clojure.java.io :as io]))

(defn to-file [path]
  (io/file path))

