package com.example.team14_sns_project

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.team14_sns_project.FollowerActivity
import com.example.team14_sns_project.FollowingHome
import com.example.team14_sns_project.databinding.ActivityFollowerBinding
import com.example.team14_sns_project.databinding.ActivityMainBinding

class FollowingActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityFollowerBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        replaceFragment(FollowingHome())

        binding.follower.setOnClickListener {
            val nextIntent = Intent(this, FollowerActivity::class.java)
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