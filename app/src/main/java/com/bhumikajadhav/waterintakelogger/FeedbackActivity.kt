package com.bhumikajadhav.waterintakelogger
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FeedbackActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        sharedPreferences = getSharedPreferences("WaterIntakeLog", MODE_PRIVATE)

        val waterCount = intent.getIntExtra("waterCount", 0)
        val totalGlasses = intent.getIntExtra("totalGlasses", 0)
        val currentDate = intent.getStringExtra("currentDate") ?: getCurrentDate()

        val feedbackText: TextView = findViewById(R.id.feedbackMessage)
        val waterIcon: ImageView = findViewById(R.id.waterIcon)
        val dailyProgressText: TextView = findViewById(R.id.dailyProgressText)
        val newDayButton: Button = findViewById(R.id.newDayButton)
        val backButton: Button = findViewById(R.id.backButton)

        val feedbackMessage = "Great job! You logged $waterCount glasses of water.\n\nKeep hydrating! 💪"
        feedbackText.text = feedbackMessage

        // Daily progress - show current total from storage
        val savedTotal = sharedPreferences.getInt("${currentDate}_glasses", 0)
        dailyProgressText.text = "Daily Total: $savedTotal glasses 📊"

        // Change icon based on daily total
        when {
            savedTotal >= 8 -> waterIcon.setImageResource(R.drawable.water_perfect)
            savedTotal >= 5 -> waterIcon.setImageResource(R.drawable.water_good)
            else -> waterIcon.setImageResource(R.drawable.water_start)
        }

        // Back to Input Screen
        backButton.setOnClickListener {
            finish()
        }

        newDayButton.setOnClickListener {
            val nextDate = getNextDate(currentDate)

            // Clear today's total
            with(sharedPreferences.edit()) {
                putInt("${currentDate}_glasses", 0)
                putInt("${nextDate}_glasses", 0)
                putLong("lastUpdatedDate", System.currentTimeMillis())
                apply()
            }

            // Pass new date info to InputActivity
            val intent = Intent(this, InputActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra("currentDate", nextDate)
            startActivity(intent)
            finish()
        }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getNextDate(currentDate: String): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val tomorrow = Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)
        return sdf.format(tomorrow)
    }
}