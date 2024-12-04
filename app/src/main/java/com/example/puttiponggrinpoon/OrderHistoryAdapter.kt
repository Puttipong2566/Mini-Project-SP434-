package com.example.puttiponggrinpoon

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderHistoryAdapter(
    private val orders: List<DatabaseHelper.OrderHistory>,
    private val onReorder: (DatabaseHelper.OrderHistory) -> Unit
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.tvOrderDate)
        val items: TextView = view.findViewById(R.id.tvOrderItems)
        val total: TextView = view.findViewById(R.id.tvOrderTotal)
        val reorderButton: Button = view.findViewById(R.id.btnReorder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_history, parent, false)
        return OrderHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val order = orders[position]
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        holder.date.text = dateFormat.format(Date(order.date))

        // สร้างข้อความแสดงรายการสินค้า
        val itemsText = order.items.joinToString("\n") {
            "${it.name} x${it.quantity} (฿${String.format("%.2f", it.total)})"
        }
        holder.items.text = itemsText

        holder.total.text = "รวม: ฿${String.format("%.2f", order.totalAmount)}"

        holder.reorderButton.setOnClickListener {
            onReorder(order)
        }
    }

    override fun getItemCount() = orders.size
}