package com.example.puttiponggrinpoon

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

class CustomizeCoffeeActivity : AppCompatActivity() {
    private lateinit var coffee: Coffee
    private var selectedSize: CoffeeSize? = null
    private var selectedSweetness: SweetnessLevel? = null
    private var extraShots: Int = 0

    private val sizes = listOf(
        CoffeeSize(1, "เล็ก", -10.0),
        CoffeeSize(2, "กลาง", 0.0),
        CoffeeSize(3, "ใหญ่", 20.0)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customize_coffee)

        // รับข้อมูลกาแฟจาก intent
        coffee = intent.getSerializableExtra("coffee") as Coffee

        // แสดงชื่อกาแฟ
        findViewById<TextView>(R.id.tvCoffeeName).text = coffee.name

        setupSizeSelection()
        setupSweetnessControl()
        setupShotControls()
        setupAddToCartButton()
        updateTotalPrice()
    }

    private fun setupSizeSelection() {
        val rgSize = findViewById<RadioGroup>(R.id.rgSize)

        rgSize.setOnCheckedChangeListener { _, checkedId ->
            selectedSize = when (checkedId) {
                R.id.rbSmall -> sizes[0]
                R.id.rbMedium -> sizes[1]
                R.id.rbLarge -> sizes[2]
                else -> sizes[1]
            }
            updateTotalPrice()
        }

        // เลือกขนาดกลางเป็นค่าเริ่มต้น
        findViewById<RadioButton>(R.id.rbMedium).isChecked = true
        selectedSize = sizes[1]
    }

    private fun setupSweetnessControl() {
        val sbSweetness = findViewById<SeekBar>(R.id.sbSweetness)

        sbSweetness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                selectedSweetness = SweetnessLevel(1, "ความหวาน ${progress}%", progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // ตั้งค่าเริ่มต้น
        selectedSweetness = SweetnessLevel(1, "ความหวาน 100%", 100)
    }

    private fun setupShotControls() {
        val btnDecrease = findViewById<ImageButton>(R.id.btnDecreaseShot)
        val btnIncrease = findViewById<ImageButton>(R.id.btnIncreaseShot)
        val tvShotCount = findViewById<TextView>(R.id.tvShotCount)

        btnDecrease.setOnClickListener {
            if (extraShots > 0) {
                extraShots--
                tvShotCount.text = extraShots.toString()
                updateTotalPrice()
            }
        }

        btnIncrease.setOnClickListener {
            if (extraShots < 3) {  // จำกัดที่ 3 shots
                extraShots++
                tvShotCount.text = extraShots.toString()
                updateTotalPrice()
            }
        }
    }

    private fun updateTotalPrice() {
        val basePrice = coffee.price
        val sizeModifier = selectedSize?.priceModifier ?: 0.0
        val shotPrice = extraShots * 10.0

        val totalPrice = basePrice + sizeModifier + shotPrice
        findViewById<TextView>(R.id.tvTotalPrice).text =
            "ราคารวม: ฿${String.format("%.2f", totalPrice)}"
    }

    private fun setupAddToCartButton() {
        findViewById<Button>(R.id.btnAddToCart).setOnClickListener {
            val customizedCoffee = CustomizedCoffee(
                coffee = coffee,
                size = selectedSize ?: sizes[1],  // ถ้าไม่ได้เลือกใช้ขนาดกลาง
                sweetnessLevel = selectedSweetness ?: SweetnessLevel(1, "ความหวาน 100%", 100),
                extraShots = CoffeeShot(extraShots),
                specialInstructions = findViewById<EditText>(R.id.etSpecialInstructions).text.toString()
            )

            // เพิ่ม Log เพื่อตรวจสอบ
            println("Adding to cart: ${coffee.name}")
            println("Size: ${selectedSize?.name}")
            println("Sweetness: ${selectedSweetness?.percentage}%")
            println("Extra shots: $extraShots")

            // เพิ่มลงตะกร้า
            CartManager.addCustomizedItem(customizedCoffee)

            Toast.makeText(this, "เพิ่ม ${coffee.name} ลงตะกร้าแล้ว", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}