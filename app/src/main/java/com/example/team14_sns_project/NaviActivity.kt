package com.example.team14_sns_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.team14_sns_project.databinding.ActivityNaviBinding
import com.example.team14_sns_project.navi.*

class NaviActivity : AppCompatActivity() {
    private lateinit var binding : ActivityNaviBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ActivityNaviBinding.inflate(layoutInflater).navigation.selectedItemId = R.id.homeFrag


        setFragment(homeFragment())
        binding.navigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeFrag -> setFragment(homeFragment())
                R.id.directMessage -> setFragment(dmFragment())
                R.id.postFrag-> startActivity(Intent(this, AddPostActivity::class.java))
                R.id.personAddFrag-> setFragment(personAddFragment())
                R.id.myprofileFrag-> setFragment(myprofileFragment())
            }
            true
        }
    }

    private fun setFragment(fragment: Any) {
        supportFragmentManager.beginTransaction().replace(R.id.main_content, fragment as Fragment).commit()
    }
}