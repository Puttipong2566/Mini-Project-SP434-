package com.example.puttiponggrinpoon

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class CoffeeAdapter(private val coffees: List<Coffee>) : RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder>() {

    class CoffeeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvCoffeeName)
        val price: TextView = view.findViewById(R.id.tvCoffeePrice)
        val description: TextView = view.findViewById(R.id.tvCoffeeDescription)
        val addToCartButton: Button = view.findViewById(R.id.btnAddToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coffee, parent, false)
        return CoffeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoffeeViewHolder, position: Int) {
        val coffee = coffees[position]
        holder.name.text = coffee.name
        holder.price.text = "฿${coffee.price}"
        holder.description.text = coffee.description

        holder.addToCartButton.setOnClickListener {
            // เพิ่มลงตะกร้าโดยตรง
            CartManager.addItem(coffee)
            // แสดง Toast แจ้งเตือน
            Toast.makeText(
                holder.itemView.context,
                "${coffee.name} เพิ่มลงตะกร้าแล้ว",
                Toast.LENGTH_SHORT
            ).show()
        }

        // เพิ่ม OnLongClickListener สำหรับการปรับแต่ง
        holder.itemView.setOnLongClickListener {
            val intent = Intent(holder.itemView.context, CustomizeCoffeeActivity::class.java).apply {
                putExtra("coffee", coffee)
            }
            holder.itemView.context.startActivity(intent)
            true
        }
    }

    override fun getItemCount() = coffees.size
}

