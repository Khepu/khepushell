(ns khepushell.base-commands
  (:require [khepushell.state :only state])
  (:use [khepushell.utilities]))


(defn exists?
  [path]
  {:last-command 'exists?
   :exit-code 0
   :output (.exists (to-file path))})

(defn file?
  [path]
  (let [valid-path? (:output (exists? path))]
    {:last-command 'file?
     :exit-code (if valid-path? 0 1)
     :output (if valid-path?
               (.isFile (to-file path))
               '(error invalid path))}))

(defn directory?
  [path]
  (let [valid-path? (:output (exists? path))]
    {:last-command 'file?
     :exit-code (if valid-path? 0 1)
     :output (if valid-path?
               (.isDirectory (to-file path))
               '(error invalid path))}))


(defn current-directory
  []
  )


(defn go-to
  [path]
  (let [state {:last-command 'go-to}
        file-targeted? (file? path)
        valid-subpath? (exists? (str (:current-directory state) path))
        valid-path? (exists? path)]
    (if (or (:output valid-path?) (:output valid-subpath?))
      (assoc state
             :current-directory (if (:output valid-subpath?)
                                  (print "TRUE")#_(str (:current-directory state) path)
                                  path)
             :exit-code (if (-> file-targeted?
                                :output
                                true?)
                          2
                          0)
             :output (if (not file-targeted?)
                       nil
                       '(error target is a file)))
      (assoc valid-path?
             :exit-code 1))))

(defn contents
  ([] (contents "./"))

  ([path]
   (let [content (.list (to-file path))
         files (filter file? content)
         directories (filter directory? content)
         state {:last-command 'contents}]

     (if (nil? content)
       (assoc state
              :exit-code 1
              :output '(directory expected but file was given))
       (assoc state
              :exit-code 0
              :output (concat directories files))))))
