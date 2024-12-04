package com.example.puttiponggrinpoon

data class CoffeeSize(
    val id: Int,
    val name: String,
    val priceModifier: Double
)

data class SweetnessLevel(
    val id: Int,
    val name: String,
    val percentage: Int
)

data class CoffeeShot(
    val extraShots: Int,
    val pricePerShot: Double = 10.0
)

data class CustomizedCoffee(
    val coffee: Coffee,
    val size: CoffeeSize,
    val sweetnessLevel: SweetnessLevel,
    val extraShots: CoffeeShot,
    val specialInstructions: String = ""
) {
    fun calculateTotalPrice(): Double {
        val basePrice = coffee.price
        val sizePrice = size.priceModifier
        val extraShotsPrice = extraShots.extraShots * extraShots.pricePerShot
        return basePrice + sizePrice + extraShotsPrice
    }
}