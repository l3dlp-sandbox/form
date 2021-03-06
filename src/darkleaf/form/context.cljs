(ns darkleaf.form.context)

(def errors-key ::errors)

(defprotocol PrivateProtocol
  (nested [this k])
  (get-data [this])
  (get-own-errors [this])
  (get-errors-subtree [this])
  (get-i18n [this])
  (get-path [this])
  (update-data [this f]))

(defn set-data [ctx val]
  (update-data ctx (fn [_old] val)))

(deftype Context [path data errors update i18n]
  PrivateProtocol
  (get-data [_]
    data)

  (get-own-errors [_]
    (get errors errors-key '()))

  (get-errors-subtree [_]
    errors)

  (get-i18n [_]
    i18n)

  (get-path [_]
    path)

  (update-data [_ f]
    (update path f))

  (nested [_ k]
    (Context. (conj path k)
              (get data k)
              (get errors k)
              update
              i18n))

  IEquiv
  (-equiv [this other]
    (and
     (= (get-path this)
        (get-path other))
     (= (get-data this)
        (get-data other))
     (= (get-errors-subtree this)
        (get-errors-subtree other))))

  ISeqable
  (-seq [this]
    (let [data (get-data this)
          update-acc (fn [acc k _]
                       (conj acc [k (nested this k)]))]
      (seq (reduce-kv update-acc [] data)))))

(defn build
  ([data errors update i18n]
   (Context. []
          data
          errors
          update
          i18n))
  ([data errors update]
   (build data errors update {})))
