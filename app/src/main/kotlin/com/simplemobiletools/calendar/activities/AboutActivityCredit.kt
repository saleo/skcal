package com.simplemobiletools.calendar.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.inflate
import com.simplemobiletools.calendar.helpers.SKCAL_AS_DEFAULT
import kotlinx.android.synthetic.main.activity_about_credits.*
import com.simplemobiletools.calendar.R
import com.simplemobiletools.calendar.extensions.setupBottomButtonBar
import com.simplemobiletools.commons.helpers.APP_VERSION_NAME
import kotlinx.android.synthetic.main.bottom_contact_copyright.*
import java.util.*

class AboutActivityCredit:SimpleActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_credits)
        setupCopyright()

    }

    override fun onResume() {
        super.onResume()
        setupCopyright()
        setupBottomButtonBar(rl_credit_holder)
    }
    private fun setupCopyright() {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        about_copyright.text = String.format(getString(R.string.copyright), year)
    }
}
