package com.example.puttiponggrinpoon

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrderHistoryActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        try {
            dbHelper = DatabaseHelper(this)
            val recyclerView = findViewById<RecyclerView>(R.id.rvOrderHistory)
            recyclerView.layoutManager = LinearLayoutManager(this)

            val username = "current_user"

            // เพิ่ม log ก่อนเรียก getOrderHistory
            Log.d("OrderHistory", "Fetching order history for user: $username")

            val orderHistory = dbHelper.getOrderHistory(username)

            // เพิ่ม log หลังได้ข้อมูล
            Log.d("OrderHistory", "Retrieved ${orderHistory.size} orders")

            val adapter = OrderHistoryAdapter(orderHistory) { order: DatabaseHelper.OrderHistory ->
                try {
                    // เพิ่ม log เมื่อกดปุ่มสั่งซ้ำ
                    Log.d("OrderHistory", "Reordering: ${order.items.size} items")

                    CartManager.clearCart()

                    order.items.forEach { item ->
                        val coffee = Coffee(
                            id = -1,
                            name = item.name,
                            price = item.price,
                            description = item.description
                        )
                        repeat(item.quantity) {
                            CartManager.addItem(coffee)
                        }
                        // เพิ่ม log สำหรับแต่ละ item ที่เพิ่มในตะกร้า
                        Log.d("OrderHistory", "Added to cart: ${item.name} x${item.quantity}")
                    }

                    startActivity(Intent(this, OrderReviewActivity::class.java))
                } catch (e: Exception) {
                    Log.e("OrderHistory", "Error during reorder", e)
                    Toast.makeText(this, "เกิดข้อผิดพลาด: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            recyclerView.adapter = adapter

        } catch (e: Exception) {
            // เพิ่ม log error หลัก
            Log.e("OrderHistory", "Error in onCreate", e)
            Toast.makeText(this, "เกิดข้อผิดพลาดในการโหลดประวัติ: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onDestroy() {
        try {
            dbHelper.close()
        } catch (e: Exception) {
            Log.e("OrderHistory", "Error closing database", e)
        }
        super.onDestroy()
    }
}