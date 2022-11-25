package com.example.team14_sns_project

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment

// 토스트 메시지만 사용할 경우 삭제
class WarningDialog(mode : Int): DialogFragment() {
    private var textMode: Int = mode
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.dialog_warning, container, false)

        // 다이얼로그 text
        val dialogText = view.findViewById<TextView>(R.id.warningText)
        // 다이얼로그 닫기 버튼
        val okBtn = view.findViewById<Button>(R.id.warningOkBtn)

        when (textMode) {
            // 회원가입 이메일 입력 안했을 시
            0 -> {dialogText.text = "    이메일을 입력하시오.    "}
            // 회원가입 이메일 입력 안했을 시
            1 -> {dialogText.text = "  이메일 아이디만 입력하시오.  "}
            // 회원가입 이메일 입력이 대문자일시
            2 -> {dialogText.text = "이메일은 영어 소문자로만 입력하시오."}
            // 회원가입 실패
            3 -> {dialogText.text = " 회원가입 실패. 다시 시도하시오. "}
            // 로그인 실패
            4 -> {dialogText.text = " 로그인 실패. 다시 시도하시오. "}
            // 오류 발생
            99 -> {dialogText.text = " 오류 발생. 다시 시도하시오. "}
        }

        okBtn.setOnClickListener {
            // 다이얼로그 닫기
            dismiss()
        }
        return view
    }
}