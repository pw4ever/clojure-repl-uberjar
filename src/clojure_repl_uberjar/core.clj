(ns clojure-repl-uberjar.core
  (:require [clojure.string :as str]
            [clojure.set :as set]
            [clojure.pprint :refer [pprint]])
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.tools.nrepl.server :refer [start-server stop-server
                                                default-handler]]
            [clojure.tools.nrepl :as repl]
            [cider.nrepl :refer [cider-middleware cider-nrepl-handler]]
            [refactor-nrepl.middleware :refer [wrap-refactor]])
  (:gen-class))


(def cli-options
  ;; An option with a required argument
  [["-a" "--nrepl-address ADDR" "nREPL address."
    :default "127.0.0.1"]
   ["-p" "--nrepl-port PORT" "nREPL port."
    :default 12321
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ;; A non-idempotent option
   ["-v" nil "Verbosity level"
    :id :verbosity
    :default 0
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ;; A boolean option defaulting to nil
   ["-h" "--help"]])

(def nrepl-server-finished (promise))

(defn -main
  "Main entry."
  [& args]
  (let [{:keys [options arguments summary errors]} (parse-opts args cli-options)]
    (if errors
      (do
        ;; errors
        (binding [*out* *err*]
          (println (str/join "\n" errors))
          (println summary)
          (System/exit 1))
        )
      (do
        ;; no errors
        (let [{:keys [nrepl-address
                      nrepl-port
                      verbosity
                      help]} options]
          (if help
            (do
              ;; help
              (println summary)
              (System/exit 0))
            (do
              ;; not help
              (with-open [server (start-server :bind nrepl-address
                                               :port nrepl-port
                                               ;;:handler cider-nrepl-handler
                                               :handler (apply default-handler
                                                               (conj (map resolve cider-middleware)
                                                                     #'wrap-refactor)))]
                (System/setProperty "clojure.nrepl.address" (str nrepl-address))
                (when (> verbosity 0)
                  (println (format "(System/setProperty \"clojure.nrepl.address\" \"%s\")"
                                   (str nrepl-address))))
                (System/setProperty "clojure.nrepl.port" (str nrepl-port))
                (when (> verbosity 0)
                  (println (format "(System/setProperty \"clojure.nrepl.port\" \"%s\")"
                                   (str nrepl-port))))
                (let [init-code '[(do
                                    (use 'clojure.pprint ; pprint print-table
                                         'clojure.repl ; source doc apropos root-cause pst
                                         'hara.reflect ; query-class query-instance
                                         )
                                    (require '[alembic.still
                                               :refer [distill load-project]]
                                             '[hiccup.core :refer [html]]
                                             '[garden.core :refer [css]]
                                             ;; piggieback/cljs-repl
                                             '[cemerick.piggieback
                                               :as piggieback]
                                             ;; weasel.repl-env
                                             '[weasel.repl.websocket
                                               :as weasel]
                                             ;; https://github.com/bhauman/lein-figwheel/wiki/Using-the-Figwheel-REPL-within-NRepl
                                             ;;(use 'figwheel-sidecar.repl-api)
                                             ;;(start-figwheel!)
                                             '[figwheel-sidecar.repl-api
                                               :as figwheel]
                                             ;; https://github.com/stuartsierra/component
                                             '[com.stuartsierra.component
                                               :as component]))]]
                  (doseq [c init-code]
                    (try
                      (with-open [conn (repl/connect :host nrepl-address
                                                     :port nrepl-port)]
                        (-> (repl/client conn 1000)
                            (repl/message {:op :eval :code (str c)})
                            dorun))
                      (when (> verbosity 0)
                        (pprint c))
                      (catch Exception e))))
                (when @nrepl-server-finished
                  (when (> verbosity 0)
                    (println (format "nREPL shutting down.")))
                  (shutdown-agents))))))))))
