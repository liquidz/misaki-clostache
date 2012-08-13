(ns misaki.compiler.clostache.core-test
  (:use clojure.test
        misaki.compiler.clostache.core
        [misaki.util file]
        misaki.tester)
  (:require [misaki.config :as cnf]
            [clojure.string :as str]
            [clojure.java.io :as io])
  )

(set-base-dir! "test/")

(defn- slurp-template [filename]
  (slurp (path cnf/*template-dir* filename)))


;;; get-template-option
(deftest* get-template-option-test
  (testing "with option"
    (let [data   (slurp-template "index.html")
          option (get-template-option data)]
      (are [x y] (= x y)
        "sample title" (:title option)
        "default"      (:layout option)
        nil            (:unknown option))))

  (testing "no option"
    (let [data   (slurp-template "no-opt.html")
          option (get-template-option data)]
      (are [x y] (= x y)
        {} option
        {} (get-template-option "")
        {} (get-template-option nil)))))

;;; remove-option-lines
(deftest* remove-option-lines-test
  (testing "with option"
    (is (= "index page"
           (str/trim (remove-option-lines (slurp-template "index.html"))))))

  (testing "no option"
    (is (= "no option page"
           (str/trim (remove-option-lines (slurp-template "no-opt.html")))))))


;;; load-layout
(deftest* load-layout-test
  (binding [*config* (get-config)]
    (is (= "default {{title}} {{&content}}"
           (str/trim (remove-option-lines (load-layout "default")))))))



;;; get-tempaltes
(deftest* get-templates-test
  (binding [*config* (get-config)]
    (testing "single layout"
      (let [data  (slurp-template "index.html")
            tmpls (get-templates data)]
        (are [x y] (= x y)
          2 (count tmpls)
          "index page" (-> tmpls first  first  str/trim)
          "default {{title}} {{&content}}" (-> tmpls second first  str/trim)
          "sample title" (-> tmpls first  second :title)
          "default title" (-> tmpls second second :title))))

    (testing "multi layout"
      (let [data  (slurp-template "multi.html")
            tmpls (get-templates data)]
        (are [x y] (= x y)
          3 (count tmpls)
          "multi page" (-> tmpls first  first  str/trim)
          "multilayout {{&content}}" (-> tmpls second  first  str/trim)
          "default {{title}} {{&content}}" (-> tmpls (nth 2) first  str/trim)
          "multi test" (-> tmpls first  second :title)
          "multilayout title" (-> tmpls second  second :title)
          "default title" (-> tmpls (nth 2) second :title))))))

;;; render-template
(deftest* render-template-test
  (binding [*config* (get-config)]
    (testing "with layout, no variable"
      (let [file (io/file (path cnf/*template-dir* "index.html"))]
        (is (= "default sample title index page"
               (str/trim (render-template file {}))))))

    (testing "with layout, not allow layout"
      (let [file (io/file (path cnf/*template-dir* "index.html"))]
        (is (= "index page"
               (str/trim (render-template file {} :allow-layout? false))))))

    (testing "with variable"
      (let [file (io/file (path cnf/*template-dir* "var.html"))]
        (is (= "msg hello"
               (str/trim (render-template file {:msg "hello"}))))
        (is (= "msg"
               (str/trim (render-template file {}))))))

    (testing "no layout"
      (let [file (io/file (path cnf/*template-dir* "no-opt.html"))]
        (is (= "no option page"
               (str/trim (render-template file {}))))
        (is (= "no option page"
               (str/trim (render-template file {} :allow-layout? false))))))))

;;; get-post-data
(deftest* get-post-data-test
  (binding [*config* (get-config)]
    (testing "default sort"
      (let [[a b c :as posts] (get-post-data)]
        (are [x y] (= x y)
          3 (count posts)

          "post baz" (:title a)
          "post bar" (:title b)
          "post foo" (:title c)

          "02 Feb 2022" (:date a)
          "01 Jan 2011" (:date b)
          "01 Jan 2000" (:date c)

          "/2022-02/baz.html" (:url a)
          "/2011-01/bar.html" (:url b)
          "/2000-01/foo.html" (:url c)


          "baz" (:content a)
          "bar" (:content b)
          "foo" (:content c)
          )))

    (testing "custom sort"
      (binding [cnf/*post-sort-type* :name]
        (let [[a b c] (get-post-data)]
          (are [x y] (= x y)
            "post foo" (:title a)
            "post bar" (:title b)
            "post baz" (:title c)))))))


;;; -compile
(deftest* -compile-test
  (letfn [(pub-file  [name] (io/file (path cnf/*public-dir* name)))
          (is-file-exists [file] (is (.exists file)) (.delete file))]
    (testing "index.html compile"
      (is (test-compile (io/file (path cnf/*template-dir* "index.html"))))
      (is-file-exists (pub-file "index.html")))

    (testing "post compile"
      (is (test-compile (io/file (path cnf/*post-dir* "2000-01-01-foo.html"))))
      (is-file-exists (pub-file "2000-01/foo.html")))

    (testing "layout compile(all compile)"
      (let [config (get-config)]
        (is (test-compile (io/file (path (:layout-dir config) "default.html"))))
        (is-file-exists (pub-file "index.html"))
        (is-file-exists (pub-file "no-opt.html"))
        (is-file-exists (pub-file "multi.html"))
        (is-file-exists (pub-file "var.html"))
        (is-file-exists (pub-file "2000-01/foo.html"))
        (is-file-exists (pub-file "2011-01/bar.html"))
        (is-file-exists (pub-file "2022-02/baz.html"))
        (.delete (pub-file "2000-01"))
        (.delete (pub-file "2011-01"))
        (.delete (pub-file "2022-02"))))))
