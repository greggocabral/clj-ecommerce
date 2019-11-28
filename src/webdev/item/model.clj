(ns webdev.item.model
  (:require [clojure.java.jdbc :as db]))

(defn create-table! [db]
  (db/execute!
    db
    ["create extension if not exists \"uuid-ossp\""])
  (db/execute!
    db
    ["CREATE TABLE IF NOT EXISTS items
        (id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
         name TEXT NOT NULL,
         description TEXT NOT NULL,
         stock INTEGER NOT NULL DEFAULT 10,
         price INTEGER NOT NULL DEFAULT 0,
         img_url TEXT,
         date_created TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now())"]))

(defn read-items [db]
  (db/query
    db
    ["SELECT id, name, description, stock, price, img_url
      FROM items
      ORDER BY date_created"]))

(defn read-item-by-id [db id]
  (-> (db/query
        db
        ["SELECT id, name, description, stock, price, img_url
          FROM items
          WHERE id = ?"
         id])
      first))

(defn create-item! [db name description stock price img_url]
  (-> (db/query
        db
        ["INSERT INTO items (name, description, stock, price, img_url)
          VALUES (?, ?, ?, ?, ?)
          RETURNING id"
         name
         description
         stock
         price
         img_url])
      first
      :id))

(defn update-item! [db id checked?]
  (= [1]
     (db/execute!
       db
       ["UPDATE items
         SET checked = ?
         WHERE id = ?"
        checked?
        id])))

(defn decrease-item-stock! [db id]
  (db/with-db-transaction [txn db]
    (let [item (read-item-by-id txn id)
          stock (:stock item)]
      (when (> stock 0)
        (= [1]
           (db/execute!
             txn
             ["UPDATE items
               SET stock = ?
               WHERE id = ?"
              (dec stock)
              id]))))))

(defn increase-item-stock! [db id]
  (db/with-db-transaction [txn db]
    (let [item (read-item-by-id txn id)
          stock (:stock item)]
      (= [1]
         (db/execute!
           txn
           ["UPDATE items
             SET stock = ?
             WHERE id = ?"
            (inc stock)
            id])))))
