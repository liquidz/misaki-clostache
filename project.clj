(defproject misaki-clostache "0.0.4-alpha"
  :description "misaki compiler using Clostache."
  :url "http://liquidz.github.com/misaki/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [misaki "0.2.6.2-beta"]
                 [de.ubercode.clostache/clostache "1.3.1"]]

  :main misaki.compiler.clostache.core)
