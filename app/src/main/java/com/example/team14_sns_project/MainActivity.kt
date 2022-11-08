package com.example.team14_sns_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.team14_sns_project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textView.text = intent.getStringExtra("email")
        binding.textView2.text = intent.getStringExtra("password")
    }


}