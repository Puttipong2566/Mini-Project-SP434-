package com.example.puttiponggrinpoon

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrderReviewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_review)

        val recyclerView = findViewById<RecyclerView>(R.id.rvOrderItems)
        val tvTotal = findViewById<TextView>(R.id.tvTotal)
        val btnConfirmOrder = findViewById<Button>(R.id.btnConfirmOrder)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // แปลงข้อมูลจาก CartManager เป็น OrderItem สำหรับแสดงผล
        val orderItems = mutableListOf<OrderItem>()

        // ดึงข้อมูลและแสดง log เพื่อตรวจสอบ
        val regularItems = CartManager.getItems()
        val customizedItems = CartManager.getCustomizedItems()

        println("Regular items: ${regularItems.size}")
        println("Customized items: ${customizedItems.size}")

        // เพิ่มรายการกาแฟปกติ
        regularItems.forEach { cartItem ->
            println("Adding regular item: ${cartItem.coffee.name}")
            orderItems.add(OrderItem(
                name = cartItem.coffee.name,
                price = cartItem.coffee.price,
                quantity = cartItem.quantity,
                total = cartItem.coffee.price * cartItem.quantity,
                description = ""
            ))
        }

        // เพิ่มรายการกาแฟที่ปรับแต่ง
        customizedItems.forEach { customizedItem ->
            val coffee = customizedItem.customizedCoffee
            println("Adding customized item: ${coffee.coffee.name}")
            val description = buildString {
                append("ขนาด: ${coffee.size.name}, ")
                append("ความหวาน: ${coffee.sweetnessLevel.percentage}%, ")
                if (coffee.extraShots.extraShots > 0) {
                    append("เพิ่ม Shot: ${coffee.extraShots.extraShots}, ")
                }
                if (coffee.specialInstructions.isNotEmpty()) {
                    append("พิเศษ: ${coffee.specialInstructions}")
                }
            }

            orderItems.add(OrderItem(
                name = coffee.coffee.name,
                price = coffee.calculateTotalPrice(),
                quantity = customizedItem.quantity,
                total = coffee.calculateTotalPrice() * customizedItem.quantity,
                description = description
            ))
        }

        println("Total order items: ${orderItems.size}")
        recyclerView.adapter = OrderItemAdapter(orderItems)

        tvTotal.text = "รวมทั้งหมด: ฿${String.format("%.2f", CartManager.getTotalPrice())}"

        btnConfirmOrder.setOnClickListener {
            // บันทึกประวัติการสั่งซื้อ
            val dbHelper = DatabaseHelper(this)
            // TODO: ควรรับ username จาก shared preferences หรือระบบจัดการ session
            val username = "current_user"

            dbHelper.addOrderToHistory(
                username = username,
                orderItems = orderItems,
                totalAmount = CartManager.getTotalPrice()
            )

            Toast.makeText(this, "สั่งซื้อสำเร็จ!", Toast.LENGTH_SHORT).show()
            CartManager.clearCart()
            finish()
        }
    }
}