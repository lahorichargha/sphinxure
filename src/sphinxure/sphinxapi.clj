(ns sphinxure.sphinxapi
  (:import [org.sphx.api SphinxClient]))

(def ^{:dynamic true} *client* (SphinxClient.))

;;; General API functions

(defn get-last-error []
  (.GetLastError *client*))

(defn get-last-warning []
  (.GetLastWarning *client*))

(defn set-server!
  ([host port]
     (.SetServer *client* host port))
  ([]
     (set-server! *client* "localhost" 9312)))

(defn set-retries
  "Sets distributed retry count and delay.

On temporary failures searchd will attempt up to $count retries per
agent. $delay is the delay between the retries, in
milliseconds. Retries are disabled by default. Note that this call
will not make the API itself retry on temporary failure; it only tells
searchd to do so. Currently, the list of temporary failures includes
all kinds of connect() failures and maxed out (too busy) remote
agents."
  ([count delay]
     (.SetRetries *client* count delay)))

(defn set-connect-timeout
  "Sets the time allowed to spend connecting to the server before giving up.

Under some circumstances, the server can be delayed in responding,
either due to network delays, or a query backlog. In either instance,
this allows the client application programmer some degree of control
over how their program interacts with searchd when not available, and
can ensure that the client application does not fail due to exceeding
the script execution limits (especially in PHP).

In the event of a failure to connect, an appropriate error code should
  be returned back to the application in order for application-level
  error handling to advise the user."  [timeout]
  (.SetConnnectTimeout *client* timeout))

(defn set-array-result [])

(defn connect-error? []
  (.IsConnectError *client*))


;;; General Query Settings

(defn set-limits [offset limit max cutoff]
  (.SetLimits *client* offset limit max cutoff))

(defn set-max-query-time
  ([msec]
     (.SetMaxQueryTime *client* msec))
  ([] (set-max-query-time 0)))

(defn set-override [attrname attrtype values]
  (.SetOverride *client* attrname attrtype values))

(defn set-select [clause]
  (.SetSelect *client* clause))


;;; Full-text Search Query Settings

(defn set-match-mode [mode]
  (.SetMatchMode *client* mode))

(defn set-ranking-mode [ranker]
  (.SetRankingMode *client* ranker))

(defn set-sort-mode [mode sort-by]
  (.SetSortMode *client* mode sort-by))

(defn set-field-weights [weights]
  (.SetFieldWeights *client* weights))

(defn set-index-weights [weights]
  (.SetIndexWeights *client* weights))


;;; Result Set Filtering Settings

(defn set-id-range
  "Sets an accepted range of document IDs. Parameters must be integers.
  Defaults are 0 and 0; that combination means to not limit by range.

  After this call, only those records that have document ID between $min
  and $max (including IDs exactly equal to $min or $max) will be matched."
  [min max]
  (.SetIDRange *client* min max))

(defn set-filter
  ([attribute values exclude]
     (.SetFilter *client* attribute values exclude))
  ([attribute values]
     (set-filter attribute values false)))

(defn set-filter-range
  ([attribute min max exclude]
     (.SetFilterRange *client* attribute min max exclude))
  ([attribute min max]
     (set-filter-range attribute min max false)))

(defn set-filter-float-range
  ([attribute min max exclude?]
     (.SetFilterFloatRange *client* attribute min max exclude?))
  ([attribute min max]
     (set-filter-float-range attribute min max false)))

(defn set-geo-anchor
  "Sets anchor point for and geosphere distance (geodistance) calculations,
  and enable them."
  [attrlat attrlong latitude longitude]
  {:pre [(string? attrlat)
         (string? attrlong)
         (float? latitude)
         (float? longitude)]}
  (.SetGeoAnchor *client* attrlat attrlong latitude longitude))


;;; GROUP BY Settings

(defn set-group-by
  [attribute fn groupsort]
  (.SetGroupBy *client* attribute fn groupsort))

(defn set-group-distinct [attribute]
  {:pre [(string? attribute)]}
  (.SetGroupDistinct *client* attribute))


;;; Querying

(defn query
  ([a-query index comment]
     (.Query *client* a-query index comment))
  ([a-query]
     (query a-query "*" "")))

(defn add-query
  ([query index comment]
     (.AddQuery *client* query index comment))
  ([query]
     (add-query query "*" "")))

(defn run-queries []
  (.RunQueries *client*))

(defn reset-filters []
  (.ResetFilters *client*))

(defn reset-group-by []
  (.ResetGroupBy *client*))


;;; Additional Functionality

(defn build-excerpts
  [docs index words opts]
  (.BuildExcerpts *client* docs index words opts))

(defn update-attributes
  [index attrs values]
  (.UpdateAttributes *client* index attrs values))

(defn build-keywords
  [query index hits]
  (.BuildKeywords *client* query index hits))

(defn escape-string
  [string] (.EscapeString *client* string))

(defn flush-attributes []
  (.FlushAttributes *client*))

;;
;;; Persistent Connections
;;
;; Persistent connections allow to use single network connection to
;; run multiple commands that would otherwise require reconnects.

(defn open
  "Opens persistent connection to the server."
  []
  (.Open *client*))

(defn close
  "Closes previously opened persistent connection."
  []
  (.Close *client*))

(defn result-ids [matches]
  (map #(.docId %) matches))

(defn result-info [words]
  (map #(.word %) words))

;; (defn result [query]
;;   (let [r (.Query sphx query)]
;;     {:matches (result-ids (.matches r))
;;      :total (.total r)
;;      :words (result-info (.words r))}))

(defn set-match [client keyword]
  (.SetMatchMode
   client 
   (condp = keyword
     :all 0
     :any 1
     :boolean 3
     :extended 4
     :extended2 6
     :fullscan 5
     :phrase 2)))
