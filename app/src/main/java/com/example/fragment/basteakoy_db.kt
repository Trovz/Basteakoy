package com.example.fragment

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class basteakoy_db(context: MainActivity, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, "basteakoydb", factory, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = "CREATE TABLE users (id INTEGER PRIMARY KEY, name TEXT);"
        db.execSQL(query)
        val queryOrder = """
        CREATE TABLE order (
            id INTEGER PRIMARY KEY,
            uname TEXT,
            user_id INTEGER,
            FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
        );
    """.trimIndent()
        db.execSQL(queryOrder)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS users")
        db.execSQL("DROP TABLE IF EXISTS customers")
        onCreate(db)
    }


    fun addProduct(name: String) {
        val values = ContentValues()
        values.put("name", name)
        val db = this.writableDatabase
        db.insert("users", null, values)

        values.clear()
        values.put("uname", "customerName")
        db.insert("customers", null, values)

        db.close()
    }

    fun delete(id: Int){
        val db = this.writableDatabase
        db.execSQL("DELETE FROM users WHERE id = ?", arrayOf(id.toString()))
        db.close()
    }

    fun update(id: Int, name: String){
        val values = ContentValues()
        values.put("name", name)

        val db = this.writableDatabase
        db.update(
            "users",
            values,
            "id = ?",
            arrayOf(id.toString())
        )
        db.close()
    }
    fun getOrder(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM users", null)
    }

    fun getCustomer(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM customers", null)
    }
}