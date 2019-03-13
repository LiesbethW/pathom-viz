(ns com.wsscode.pathom.viz.ui.kit
  (:require [fulcro.client.primitives :as fp]
            [fulcro.client.localized-dom :as dom]
            [fulcro-css.css :as css]
            [goog.object :as gobj]
            [ghostwheel.core :as g :refer [>defn >defn- >fdef => | <- ?]]))

(declare gc css)

; region helpers

(>defn props [props]
  [map? => map?]
  (into {} (remove (comp qualified-keyword? first)) props))

; endregion

; region basics

(fp/defsc Button
  [this props]
  {:css [[:.container {:border "1px solid #000"}]]}
  (dom/div :.container props (fp/children this)))

(def button (fp/factory Button))

(fp/defsc Column
  [this props]
  {:css [[:.container {:display        "flex"
                       :flex-direction "column"
                       :max-height     "100%"}]]}
  (dom/div :.container props
    (fp/children this)))

(def column (fp/factory Column))

(fp/defsc Row
  [this props]
  {:css [[:.container {:display   "flex"
                       :max-width "100%"}]]}
  (dom/div :.container props (fp/children this)))

(def row (fp/factory Row))

; endregion

; region components

(fp/defsc Panel
  [this {::keys [panel-title panel-tag]}]
  {}
  (dom/div :$panel
    (dom/p :$panel-heading$row-center
      (dom/span (gc :.flex) panel-title)
      (if panel-tag (dom/span :$tag$is-dark panel-tag)))
    (dom/div :$panel-block
      (dom/div :$scrollbars
        (fp/children this)))))

(def panel (fp/factory Panel))

; endregion

(fp/defsc UIKit [_ _]
  {:css         [[:.flex {:flex "1"}]
                 [:.scrollbars {:overflow "auto"}]
                 [:.no-scrollbars {:overflow "hidden"}]]
   :css-include [Panel Column Row]})

(def ui-css (css/get-classnames UIKit))

(defn get-css [map k]
  (or (get map k)
      (get map (keyword (subs (name k) 1)))))

(defn css
  "Get one class from the shared ui kit registry. You can send the class
  name as :some-class or :.some-class."
  [k]
  (get-css ui-css k))

(defn gc
  "Return a map pointing to the given global classes.
  Eg: (kc :.flex :.scrollbars)"
  [& k]
  {:classes (mapv css k)})

(defn ccss [component & k]
  (if-let [css-map (try
                     (some-> component (gobj/get "constructor") css/get-classnames)
                     (catch :default _ nil))]
    (into [] (map (partial get-css css-map)) k)
    []))
