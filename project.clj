(defproject clojure-repl-uberjar "0.1.1-SNAPSHOT"
  :description "A uberjar that provides Clojure REPL and some conveniences."
  :url "http://github.com/pw4ever/clojure-repl-uberjar"
  :license {:name "MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [org.clojure/tools.nrepl "0.2.12"]
                 [org.clojure/tools.cli "0.3.3"]
                 [cider/cider-nrepl "0.10.1"]
                 [refactor-nrepl "2.0.0-SNAPSHOT"]
                 
                 ;; utilities

                 ;; https://github.com/zcaudate/hara
                 ;; http://docs.caudate.me/hara/
                 [im.chit/hara.reflect "2.2.11"] ; JVM reflections

                 ;; https://github.com/weavejester/hiccup
                 [hiccup "1.0.5"] ; edn => HTML

                 ;; https://github.com/noprompt/garden
                 [garden "1.3.0"] ; edn => CSS
                 
                 ;; https://github.com/pallet/alembic
                 [alembic "0.3.2"] ; JVM classpath reloading
                 
                 ;; for ClojureScript REPL

                 ;; https://github.com/bhauman/lein-figwheel#scripting-figwheel
                 [figwheel-sidecar "0.5.0-2"]

                 ;; https://github.com/cemerick/piggieback
                 ;; nREPL middleware for ClojureScript
                 ;; (cemerick.piggieback/cljs-repl (cljs.repl.rhino/repl-env))
                 ;; ; type :cljs/quit to quit REPL
                 [com.cemerick/piggieback "0.2.1"]

                 ;; https://github.com/tomjakubowski/weasel
                 ;; Web-Socket-based channel over piggieback
                 ;; Piggieback the Weasel REPL environment onto the nREPL session, optionally specifying a port (defaults to 9001) and an address to bind to (defaults to "127.0.0.1")
                 ;; (cemerick.piggieback/cljs-repl
                 ;;  (weasel.repl.websocket/repl-env :ip "0.0.0.0" :port 9001))
                 ;; Weasel will block the REPL, waiting for a client to connect.
                 ;; In your project's ClojureScript source, require the Weasel client namespace and connect to the REPL.
                 ;; (when-not (weasel.repl/alive?)
                 ;;   (repl/connect "ws://localhost:9001"
                 ;;                 :verbose true
                 ;;                 :print #{:repl :console}
                 ;;                 :on-error #(print "Error! " %)))
                 [weasel "0.7.0" :exclusions [org.clojure/clojurescript]]
                 
                 ]
  :main ^:skip-aot clojure-repl-uberjar.core
  :target-path "target/%s"
  :uberjar-name "clojure-repl.jar"
  :profiles {:uberjar {:aot :all}})
