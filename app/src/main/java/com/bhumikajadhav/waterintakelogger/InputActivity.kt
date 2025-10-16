package com.bhumikajadhav.waterintakelogger

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class InputActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editText: EditText
    private lateinit var logButton: Button
    private lateinit var totalGlassesText: TextView
    private lateinit var dateText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        sharedPreferences = getSharedPreferences("WaterIntakeLog", MODE_PRIVATE)

        editText = findViewById(R.id.waterInputField)
        logButton = findViewById(R.id.logButton)
        totalGlassesText = findViewById(R.id.totalGlassesText)
        dateText = findViewById(R.id.dateText)

        updateDisplay()

        logButton.setOnClickListener {
            val inputText = editText.text.toString().trim()

            if (inputText.isEmpty()) {
                Toast.makeText(this, "Please enter a number.", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    val waterCount: Int = inputText.toInt()

                    if (waterCount <= 0) {
                        Toast.makeText(this, "Please enter a positive number.", Toast.LENGTH_SHORT).show()
                    } else {
                        val currentDate = getCurrentDate()
                        val currentTotal = getTotalGlassesForToday()
                        val newTotal = currentTotal + waterCount

                        // Save to SharedPreferences
                        with(sharedPreferences.edit()) {
                            putInt("${currentDate}_glasses", newTotal)
                            putLong("lastUpdatedDate", System.currentTimeMillis())
                            apply()
                        }

                        // Pass data to FeedbackActivity
                        val intent = Intent(this, FeedbackActivity::class.java)
                        intent.putExtra("waterCount", waterCount)
                        intent.putExtra("totalGlasses", newTotal)
                        intent.putExtra("currentDate", currentDate)
                        startActivity(intent)
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(this, "Please enter a valid number.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateDisplay()
        editText.text.clear()
    }

    private fun updateDisplay() {
        val dateFromIntent = intent.getStringExtra("currentDate")
        val currentDate = dateFromIntent ?: getCurrentDate()

        val totalGlasses = sharedPreferences.getInt("${currentDate}_glasses", 0)

        dateText.text = "Today: $currentDate"
        totalGlassesText.text = "Total Glasses Today: $totalGlasses 🥤"
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getTotalGlassesForToday(): Int {
        val currentDate = getCurrentDate()
        return sharedPreferences.getInt("${currentDate}_glasses", 0)
    }
}