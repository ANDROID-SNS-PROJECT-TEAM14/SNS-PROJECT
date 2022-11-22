package com.example.team14_sns_project.navi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.team14_sns_project.R

// 임시로 만들어둔 것
// fragment로 할지 Activity로 할지 정할것
// 일단 Activity로 할거라 예상
class dmFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_dm, container, false)
        return view
    }

}