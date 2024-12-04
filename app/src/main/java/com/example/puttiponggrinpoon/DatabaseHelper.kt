package com.example.puttiponggrinpoon

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        // ค่าคงที่สำหรับฐานข้อมูล
        private const val DATABASE_NAME = "CoffeeAppDB.db"
        private const val DATABASE_VERSION = 1

        // ค่าคงที่สำหรับตารางกาแฟ
        private const val TABLE_COFFEE = "coffee"
        private const val COLUMN_COFFEE_ID = "id"
        private const val COLUMN_COFFEE_NAME = "name"
        private const val COLUMN_COFFEE_PRICE = "price"
        private const val COLUMN_COFFEE_DESC = "description"

        // ค่าคงที่สำหรับตารางผู้ใช้
        private const val TABLE_USERS = "users"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"

        // ค่าคงที่สำหรับตารางประวัติการสั่งซื้อ
        private const val TABLE_ORDER_HISTORY = "order_history"
        private const val COLUMN_ORDER_ID = "order_id"
        private const val COLUMN_USER = "username"
        private const val COLUMN_ORDER_DATE = "order_date"
        private const val COLUMN_TOTAL_AMOUNT = "total_amount"
        private const val COLUMN_ORDER_ITEMS = "order_items"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // สร้างตารางกาแฟ
        val createCoffeeTable = """
            CREATE TABLE $TABLE_COFFEE (
                $COLUMN_COFFEE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_COFFEE_NAME TEXT NOT NULL,
                $COLUMN_COFFEE_PRICE REAL NOT NULL,
                $COLUMN_COFFEE_DESC TEXT NOT NULL
            )
        """.trimIndent()

        // สร้างตารางผู้ใช้
        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USERNAME TEXT PRIMARY KEY,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

        // สร้างตารางประวัติการสั่งซื้อ
        val createOrderHistoryTable = """
            CREATE TABLE $TABLE_ORDER_HISTORY (
                $COLUMN_ORDER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER TEXT NOT NULL,
                $COLUMN_ORDER_DATE TEXT NOT NULL,
                $COLUMN_TOTAL_AMOUNT REAL NOT NULL,
                $COLUMN_ORDER_ITEMS TEXT NOT NULL,
                FOREIGN KEY($COLUMN_USER) REFERENCES $TABLE_USERS($COLUMN_USERNAME)
            )
        """.trimIndent()

        // สร้างตารางทั้งหมด
        db.execSQL(createCoffeeTable)
        db.execSQL(createUsersTable)
        db.execSQL(createOrderHistoryTable)

        // เพิ่มข้อมูลกาแฟเริ่มต้น
        insertInitialCoffees(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // ลบตารางเก่าทั้งหมด
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDER_HISTORY")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_COFFEE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        // สร้างตารางใหม่
        onCreate(db)
    }

    private fun insertInitialCoffees(db: SQLiteDatabase) {
        val coffees = listOf(
            Coffee(1, "เอสเพรสโซ่", 60.0, "กาแฟดำเข้มข้น"),
            Coffee(2, "ลาเต้", 75.0, "เอสเพรสโซ่ผสมนมสตีม"),
            Coffee(3, "คาปูชิโน่", 75.0, "เอสเพรสโซ่ผสมฟองนม"),
            Coffee(4, "มอคค่า", 85.0, "เอสเพรสโซ่ผสมช็อคโกแลตและนม"),
            Coffee(5, "อเมริกาโน่", 65.0, "เอสเพรสโซ่ผสมน้ำร้อน")
        )

        coffees.forEach { coffee ->
            val values = ContentValues().apply {
                put(COLUMN_COFFEE_NAME, coffee.name)
                put(COLUMN_COFFEE_PRICE, coffee.price)
                put(COLUMN_COFFEE_DESC, coffee.description)
            }
            db.insert(TABLE_COFFEE, null, values)
        }
    }

    // เมธอดสำหรับผู้ใช้
    fun addUser(username: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USERNAME, username)
        values.put(COLUMN_PASSWORD, password)

        return try {
            db.insertOrThrow(TABLE_USERS, null, values)
            true
        } catch (e: SQLiteConstraintException) {
            false
        }
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COLUMN_USERNAME),
            "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password),
            null, null, null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // เมธอดสำหรับกาแฟ
    fun getAllCoffees(): List<Coffee> {
        val coffees = mutableListOf<Coffee>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_COFFEE, null, null, null, null, null, null)

        with(cursor) {
            while (moveToNext()) {
                val coffee = Coffee(
                    getInt(getColumnIndexOrThrow(COLUMN_COFFEE_ID)),
                    getString(getColumnIndexOrThrow(COLUMN_COFFEE_NAME)),
                    getDouble(getColumnIndexOrThrow(COLUMN_COFFEE_PRICE)),
                    getString(getColumnIndexOrThrow(COLUMN_COFFEE_DESC))
                )
                coffees.add(coffee)
            }
        }
        cursor.close()
        return coffees
    }

    // เมธอดสำหรับประวัติการสั่งซื้อ
    fun addOrderToHistory(username: String, orderItems: List<OrderItem>, totalAmount: Double) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER, username)
            put(COLUMN_ORDER_DATE, System.currentTimeMillis())
            put(COLUMN_TOTAL_AMOUNT, totalAmount)
            put(COLUMN_ORDER_ITEMS, convertOrderItemsToJson(orderItems))
        }
        db.insert(TABLE_ORDER_HISTORY, null, values)
    }

    fun getOrderHistory(username: String): List<OrderHistory> {
        val orderList = mutableListOf<OrderHistory>()
        val db = this.readableDatabase

        try {
            Log.d("Database", "Querying order history for user: $username")

            val cursor = db.query(
                TABLE_ORDER_HISTORY,
                null,
                "$COLUMN_USER = ?",
                arrayOf(username),
                null,
                null,
                "$COLUMN_ORDER_DATE DESC"
            )

            Log.d("Database", "Found ${cursor.count} orders")

            cursor.use {
                while (it.moveToNext()) {
                    try {
                        val orderId = it.getInt(it.getColumnIndexOrThrow(COLUMN_ORDER_ID))
                        val date = it.getLong(it.getColumnIndexOrThrow(COLUMN_ORDER_DATE))
                        val totalAmount = it.getDouble(it.getColumnIndexOrThrow(COLUMN_TOTAL_AMOUNT))
                        val itemsJson = it.getString(it.getColumnIndexOrThrow(COLUMN_ORDER_ITEMS))

                        Log.d("Database", "Processing order $orderId with $itemsJson")

                        val items = parseOrderItemsFromJson(itemsJson)

                        orderList.add(
                            OrderHistory(
                                id = orderId,
                                date = date,
                                totalAmount = totalAmount,
                                items = items
                            )
                        )

                        Log.d("Database", "Successfully added order $orderId to list")
                    } catch (e: Exception) {
                        Log.e("Database", "Error processing order row", e)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("Database", "Error getting order history", e)
        }

        Log.d("Database", "Returning ${orderList.size} orders")
        return orderList
    }

    private fun convertOrderItemsToJson(items: List<OrderItem>): String {
        val jsonArray = JSONArray()
        items.forEach { item ->
            val jsonObject = JSONObject().apply {
                put("name", item.name)
                put("price", item.price)
                put("quantity", item.quantity)
                put("total", item.total)
                put("description", item.description)
            }
            jsonArray.put(jsonObject)
        }
        return jsonArray.toString()
    }

    private fun parseOrderItemsFromJson(jsonString: String): List<OrderItem> {
        val items = mutableListOf<OrderItem>()
        try {
            Log.d("Database", "Parsing JSON: $jsonString")

            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                items.add(
                    OrderItem(
                        name = jsonObject.getString("name"),
                        price = jsonObject.getDouble("price"),
                        quantity = jsonObject.getInt("quantity"),
                        total = jsonObject.getDouble("total"),
                        description = jsonObject.optString("description", "")
                    )
                )
            }
            Log.d("Database", "Successfully parsed ${items.size} items from JSON")
        } catch (e: Exception) {
            Log.e("Database", "Error parsing JSON", e)
        }
        return items
    }

    // Data classes
    data class OrderHistory(
        val id: Int,
        val date: Long,
        val totalAmount: Double,
        val items: List<OrderItem>
    )
}