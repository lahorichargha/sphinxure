(ns sphinxure.core
  (:import [org.sphx.api SphinxClient SphinxMatch SphinxResult SphinxWordInfo]))


(def sphx (doto (SphinxClient.)))

(defn result-ids [matches]
  (map #(.docId %) matches))

(defn result-info [words]
  (map #(.word %) words))

(defn result [query]
  (let [r (.Query sphx query)]
    {:matches (result-ids (.matches r))
     :total (.total r)
     :words (result-info (.words r))}))

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
