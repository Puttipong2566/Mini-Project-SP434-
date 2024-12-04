package com.example.puttiponggrinpoon

import android.content.Context

object CartManager {
    private val items = mutableListOf<CartItem>()
    private val customizedItems = mutableListOf<CustomizedCartItem>()

    // เพิ่มเมธอด initialize
    fun initialize(context: Context) {
        // เคลียร์ข้อมูลเก่าเมื่อเริ่มต้นแอป
        items.clear()
        customizedItems.clear()
    }

    data class CartItem(
        val coffee: Coffee,
        var quantity: Int = 1
    )

    data class CustomizedCartItem(
        val customizedCoffee: CustomizedCoffee,
        var quantity: Int = 1
    )

    fun addItem(coffee: Coffee) {
        val existingItem = items.find { it.coffee.id == coffee.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            items.add(CartItem(coffee))
        }
    }

    fun addCustomizedItem(customizedCoffee: CustomizedCoffee) {
        // เพิ่มลงในรายการสินค้าที่ปรับแต่ง
        customizedItems.add(CustomizedCartItem(customizedCoffee))
    }

    fun getItems(): List<CartItem> = items.toList()

    fun getCustomizedItems(): List<CustomizedCartItem> = customizedItems.toList()

    fun getTotalPrice(): Double {
        val regularItemsTotal = items.sumOf { it.coffee.price * it.quantity }
        val customizedItemsTotal = customizedItems.sumOf {
            it.customizedCoffee.calculateTotalPrice() * it.quantity
        }
        return regularItemsTotal + customizedItemsTotal
    }

    fun clearCart() {
        items.clear()
        customizedItems.clear()
    }
}
data class CartItem(val coffee: Coffee, var quantity: Int)