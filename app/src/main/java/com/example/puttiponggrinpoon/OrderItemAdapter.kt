package com.example.puttiponggrinpoon
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Data class สำหรับแสดงผลในรายการ
data class OrderItem(
    val name: String,
    val price: Double,
    val quantity: Int,
    val total: Double,
    val description: String = ""
)

class OrderItemAdapter(private val items: List<OrderItem>) :
    RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    class OrderItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvItemName)
        val price: TextView = view.findViewById(R.id.tvItemPrice)
        val quantity: TextView = view.findViewById(R.id.tvQuantity)
        val total: TextView = view.findViewById(R.id.tvItemTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val item = items[position]

        // แสดงชื่อพร้อมรายละเอียดถ้ามี
        holder.name.text = item.name
        if (item.description.isNotEmpty()) {
            holder.name.text = "${item.name}\n${item.description}"
        }

        holder.price.text = "฿${String.format("%.2f", item.price)}"
        holder.quantity.text = "จำนวน: ${item.quantity}"
        holder.total.text = "รวม: ฿${String.format("%.2f", item.total)}"
    }

    override fun getItemCount() = items.size
}