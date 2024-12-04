package com.example.puttiponggrinpoon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MenuActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        dbHelper = DatabaseHelper(this)
        val coffees = dbHelper.getAllCoffees()

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val btnViewCart = findViewById<Button>(R.id.btnViewCart)
        val btnOrderHistory = findViewById<Button>(R.id.btnOrderHistory)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = CoffeeAdapter(coffees)
        recyclerView.adapter = adapter

        btnViewCart.setOnClickListener {
            if (CartManager.getItems().isEmpty() && CartManager.getCustomizedItems().isEmpty()) {
                Toast.makeText(this, "ตะกร้าว่างเปล่า", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, OrderReviewActivity::class.java))
            }
        }

        btnOrderHistory.setOnClickListener {
            startActivity(Intent(this, OrderHistoryActivity::class.java))
        }
    }
}