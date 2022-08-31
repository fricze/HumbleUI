(ns user
  (:require
    [clojure.string :as str]
    [examples.settings :as settings]
    [io.github.humbleui.app :as app]
    [io.github.humbleui.canvas :as canvas]
    [io.github.humbleui.core :as core]
    [io.github.humbleui.debug :as debug]
    [io.github.humbleui.paint :as paint]
    [io.github.humbleui.profile :as profile]
    [io.github.humbleui.window :as window]
    [io.github.humbleui.ui :as ui]
    [nrepl.cmdline :as nrepl]
    [user.error :as error])
  (:import
    [io.github.humbleui.skija FontMgr FontStyle Typeface Font]
    [io.github.humbleui.types IRect]))

(set! *warn-on-reflection* true)

(alter-var-root #'core/log-error
  (constantly error/log-error))

(defonce *window (atom nil))

(defn set-floating! [window floating]
  (when window
    (if floating
      (window/set-z-order window :floating)
      (window/set-z-order window :normal))))

(add-watch settings/*floating ::window
  (fn [_ _ _ floating]
    (set-floating! @*window floating)))

(def examples
  ["align"
   "button"
   "calculator"
   "canvas"
   "checkbox"
   "container"
   "errors"
   "event-bubbling"
   "label"
   "scroll"
   "settings"
   "svg"
   "text-field"
   "text-field-debug"
   "tooltip"
   "tree"
   "wordle"])

(defonce *example (atom "text-field"))

(def app
  (ui/default-theme {; :font-size 13
                     ; :cap-height 10
                     ; :leading 100
                     ; :fill-text (paint/fill 0xFFCC3333)
                     ; :hui.text-field/fill-text (paint/fill 0xFFCC3333)
                     }
    (ui/row
     (ui/vscrollbar
       (ui/vscroll
         (ui/column
           (for [name (sort examples)]
             (ui/clickable
               {:on-click (fn [_] (reset! *example name))}
               (ui/dynamic ctx [selected? (= name @*example)
                                hovered?  (:hui/hovered? ctx)]
                 (let [label (ui/padding 20 10
                               (ui/label (-> name
                                           (str/split #"-")
                                           (->> (map str/capitalize)
                                             (str/join " ")))))]
                   (cond
                     selected? (ui/rect (paint/fill 0xFFB2D7FE) label)
                     hovered?  (ui/rect (paint/fill 0xFFE1EFFA) label)
                     :else     label))))))))
      [:stretch 1
       (ui/clip
         (ui/dynamic _ [ui @(requiring-resolve (symbol (str "examples." @*example) "ui"))]
           ui))])))

(defn ctx [window]
  (when-not (window/closed? window)
    {:window window
     :scale  (window/scale window)}))

(defn on-paint [window canvas]
  (canvas/clear canvas 0xFFF6F6F6)
  (let [bounds (window/content-rect window)]
    (profile/reset)
    ; (profile/measure "frame"
    (core/draw app (ctx window) (IRect/makeXYWH 0 0 (:width bounds) (:height bounds)) canvas)
    (profile/log)
    #_(window/request-frame window)))

(defn redraw []
  (some-> @*window window/request-frame))

(add-watch *example ::redraw
  (fn [_ _ _ _]
    (redraw)))

(redraw)

(defn on-event [window event]
  (when-let [result (core/event app (ctx window) event)]
    (window/request-frame window)
    result))

(defn make-window []
  (let [screens (app/screens)
        multi?  (> (count screens) 1)
        screen  (last screens)
        scale   (:scale screen)
        area    (:work-area screen)
        width   (* (if multi? 600 460) scale)
        height  (* (if multi? 400 400) scale)
        x       (if multi?
                  (-> (:x area))
                  (-> (:x area) (+ (:width area)) (- width)))
        y       (-> (:y area) (+ (:height area)) (- height) (/ 2))
        window  (window/make
                  {:on-close #(reset! *window nil)
                   :on-paint #'on-paint
                   :on-event #'on-event})]
    (set-floating! window @settings/*floating)
    (reset! debug/*enabled? true)
    (window/set-title window "Humble UI 🐝")
    (when (= :macos app/platform)
      (window/set-icon window "dev/images/icon.icns"))
    (window/set-window-size window width height)
    (window/set-window-position window x y)
    (window/set-visible window true)))

(defn -main [& args]
  (future (apply nrepl/-main args))
  (app/start #(reset! *window (make-window))))

(comment  
  (do
    (app/doui (some-> @*window window/close))
    (reset! settings/*floating false)
    (reset! *window (app/doui (make-window))))
  
  (app/doui (window/set-z-order @*window :normal))
  (app/doui (window/set-z-order @*window :floating))
  )