package com.example.team14_sns_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.team14_sns_project.FollowerHome
import com.example.team14_sns_project.FollowerViewModel
import com.example.team14_sns_project.MainActivity
import com.example.team14_sns_project.R
import com.example.team14_sns_project.databinding.ActivityFollowerBinding
import java.nio.file.Files.delete

class FollowerActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityFollowerBinding.inflate(layoutInflater)
    }

    private val FollowerViewModel by viewModels<FollowerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        replaceFragment(FollowerHome())

        binding.following.setOnClickListener {
            val nextIntent = Intent(this, MainActivity::class.java)
            startActivity(nextIntent)

        }

    }
    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()

    }
}