(ns khepushell.core
  (:use [clojure.java.shell :only [sh]])
  (:require [clojure.string :as s]))


(defn non-empty
  "Removes empty strings from a collection"
  [strings]
  (filter (comp pos? count) strings))


(def path (-> (System/getenv)
              (get "PATH")
              (s/split #";")
              set
              non-empty))


(defn home-dir
  []
  (System/getProperty "user.home"))

(def state {:current-directory (home-dir)})

(defn read-command
  []
  (print "> ")
  (read-string (str "(" (read-line) ")")))


(defn eval-command
  [state command]
  (let [exit-code (:exit-code command)]
    (if (nil? exit-code)
      (assoc state :output command)
      (merge state command))))


(defn print-command
  [state]
  state)

(defn -main
  [args]
  (loop [state state]
    (print-command (eval-command state (read-command)))))

