(ns misaki.compiler.clostache.core
  (:require
    [misaki.util [file   :refer :all]
                 [date   :refer :all]
                 [string :refer :all]]
    [misaki [config   :refer [*config*]]
            [core     :as msk]
            [config   :as cnf]
            [server   :as srv]]
    [clostache.parser :as clostache]
    [clojure.string   :as str]))

;; ## Private Functions

(defn- parse-option-line
  [line]
  (re-seq #"^\s*;+\s*@(\w+)\s+(.+)$" line))

;; ## Utilities

; =layout-file?
(defn layout-file?
  [file]
  (if-let [layout-dir (:layout-dir *config*)]
    (str-contains? (.getAbsolutePath file) layout-dir)
    false))

; =get-template-option
(defn get-template-option
  [slurped-data]
  (if (string? slurped-data)
    (let [lines  (str/split-lines slurped-data)
          params (remove nil? (map parse-option-line lines))]
      (into {} (for [[[_ k v]] params] [(keyword k) v])))
    {}))

; =remove-option-lines
(defn remove-option-lines
  [slurped-data]
  (let [lines  (str/split-lines slurped-data)]
    (str/join "\n" (remove #(parse-option-line %) lines))))

; =load-layout
(defn load-layout
  [layout-name]
  (slurp (path (:layout-dir *config*) (str layout-name ".html"))))

; =get-templates
(defn get-templates
  [slurped-data]
  (letfn [(split [s] ((juxt remove-option-lines get-template-option) s))]
    (take-while
      (comp not nil?)
      (iterate (fn [[_ tmpl-option]]
                 (if-let [layout-name (:layout tmpl-option)]
                   (split (load-layout layout-name))))
               (split slurped-data)))))

; =render-template
(defn render-template [file base-data & {:keys [allow-layout?]
                                    :or   {allow-layout? true}}]
  (let [tmpls (get-templates (slurp file))
        htmls (map first tmpls)
        data  (merge base-data (reduce merge (reverse (map second tmpls))))]

    (if allow-layout?
      (reduce
        (fn [result-html tmpl-html]
          (if tmpl-html
            (clostache/render tmpl-html (merge data {:content result-html}))
            result-html))
        (clostache/render (first htmls) data)
        (rest htmls))
      (clostache/render (first htmls) data))))

; =get-post-data
(defn get-post-data
  [& {:keys [all?] :or {all? false}}]
  (map #(let [date (cnf/get-date-from-file %)]
          (assoc (-> % slurp get-template-option)
                 :date (date->string date)
                 :date-xml-schema (date->xml-schema date)
                 :content (render-template % (:site *config*) :allow-layout? false)
                 :url (cnf/make-output-url %)))
       (msk/get-post-files :sort? true :all? all?)))

;; ## Plugin Definitions

(defn -extension
  []
  (list :htm :html :xml))

(defn -config
  [{:keys [template-dir] :as config}]
  (assoc config
         :layout-dir (path template-dir (:layout-dir config))))

(defn -compile [config file]
  (binding [*config* config]
    (if (layout-file? file)
      {:status 'skip :all-compile? true}
      (let [posts     (get-post-data)
            all-posts (get-post-data :all? true)
            date  (now)]
        (render-template
          file
          (merge (:site config)
                 {:date      (date->string date)
                  :prev-page (:prev-page config)
                  :next-page (:next-page config)
                  :date-xml-schema (date->xml-schema date)
                  :posts     posts
                  :all-posts all-posts}))))))

(defn -main [& args]
  (apply srv/-main args))
