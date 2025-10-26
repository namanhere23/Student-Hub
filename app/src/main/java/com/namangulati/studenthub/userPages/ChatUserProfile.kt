package com.namangulati.studenthub.userPages

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.namangulati.studenthub.R
import com.namangulati.studenthub.models.UserDetailsModel
import com.namangulati.studenthub.uiutils.NavigationMenuLauncher.launchNavigationMenu
import com.namangulati.studenthub.utils.FirebaseUserDatabaseUtils.loadUserByUid

class ChatUserProfile : AppCompatActivity() {
    private lateinit var profileImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat_user_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val person=intent.getSerializableExtra("EXTRA_USER_DETAILS") as UserDetailsModel
        launchNavigationMenu(this,person)

        val etemail=findViewById<TextView>(R.id.etemail)
        val etName=findViewById<TextView>(R.id.etName)
        profileImage=findViewById(R.id.profileImage)
        val etMobileDetails=findViewById<TextView>(R.id.etMobileDetails)
        val back=findViewById<ImageView>(R.id.back)

        val personUid=intent.getStringExtra("uid")
        if (personUid != null) {
            loadUserByUid(this,personUid.toString()){
                if(it!=null){
                    etName.text=it.name
                    etemail.text=it.email
                    etMobileDetails.text=it.mobile
                    if(!(it.photo.isNullOrEmpty())){
                        val imageUrl=it.photo?.replace("http://","https://")
                        Glide.with(this)
                            .load(imageUrl)
                            .into(profileImage)
                    }
                }
            }
        }

        back.setOnClickListener()
        {
            finish()
        }
    }
}