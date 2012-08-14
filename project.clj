(defproject misaki-clostache "0.0.1-alpha"
  :description "misaki compiler using Clostache."
  :url "http://liquidz.github.com/misaki/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.4.0"]
                 [misaki "0.2.0-beta"]
                 [de.ubercode.clostache/clostache "1.3.0"]]

  :main misaki.compiler.clostache.core)
