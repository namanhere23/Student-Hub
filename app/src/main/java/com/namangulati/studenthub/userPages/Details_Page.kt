package com.namangulati.studenthub.userPages

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.namangulati.studenthub.MainActivity
import com.namangulati.studenthub.R
import com.namangulati.studenthub.models.UserDetailsModel
import com.namangulati.studenthub.utils.FirebaseUserDatabaseUtils
import com.namangulati.studenthub.utils.MediaUploadUtils.uploadDocumentToServer
import com.namangulati.studenthub.utils.PermissionsUtils.requestExternalStoragePermission

class Details_Page : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")

    private lateinit var profileImage: ImageView
    private var profileImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val etemail=findViewById<EditText>(R.id.etemail)
        val person = intent.getSerializableExtra("EXTRA_USER_DETAILS") as? UserDetailsModel
        val etName=findViewById<EditText>(R.id.etName)
        profileImage=findViewById<ImageView>(R.id.profileImage)

        val etMobileDetails=findViewById<EditText>(R.id.etMobileDetails)
        val back=findViewById<ImageView>(R.id.back)

        person?.uid?.let { uid ->
            FirebaseUserDatabaseUtils.loadUserByUid(this,uid) { user ->
                if (user != null) {
                    person.name = user.name
                    person.email = user.email
                    person.mobile = user.mobile
                    person.uid=user.uid
                    person.groups=user.groups
                    etName.setText(user.name ?: "")
                    etemail.setText(user.email ?: "")
                    etMobileDetails.setText(user.mobile ?: "")

                    if (!user.photo.isNullOrEmpty()) {
                        Glide.with(this)
                            .load(user.photo)
                            .into(profileImage)
                        profileImageUrl=user.photo
                    } else {
                        profileImage.setImageResource(R.drawable.ic_profile_pic)
                        profileImageUrl= null
                    }
                }
            }
        }

        back.setOnClickListener()
        {
            finish()
        }

        person?.let {
            etName.setText(it.name)
            etemail.setText(it.email)
            etMobileDetails.setText(it.mobile)
        }

        val btn=findViewById<com.google.android.material.button.MaterialButton>(R.id.cont2)

        etemail.setOnClickListener()
        {
            finish()
        }

        btn.setOnClickListener{
            var mobileText = etMobileDetails.text.toString().trim()

            if (mobileText.isEmpty()) {
                Toast.makeText(this, "Please enter mobile number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            mobileText = mobileText.trimStart('0')

            if (mobileText.length != 10 || !mobileText.all { it.isDigit() }) {

                Toast.makeText(this, "Invalid mobile number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (etName.text.toString().isEmpty()) {
                Toast.makeText(this, "Please enter Name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userDetails= UserDetailsModel(etName.text.toString(),etemail.text.toString(), etMobileDetails.text.toString(),profileImageUrl,person?.uid,person?.groups?: arrayListOf())
            Log.d("Hello7",userDetails.photo.toString())
            FirebaseUserDatabaseUtils.saveUser(this,userDetails) { success ->
                if (success) {
                    Toast.makeText(this, "User saved!", Toast.LENGTH_SHORT).show()
                }

                else {
                    Toast.makeText(this, "Failed to save user!", Toast.LENGTH_SHORT).show()
                }
            }

            Intent(this, Dashboard::class.java).also {
                it.putExtra("EXTRA_USER_DETAILS", userDetails)
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
                finish()
            }

            Toast.makeText(this, "Welcome ${etName.text.toString()} to our App", Toast.LENGTH_LONG).show()
    }
        profileImage.setOnClickListener {
            requestExternalStoragePermission(this)
            Log.d("Hello","Hello")
            pickImageLauncher.launch("image/*")
            Log.d("Hello","Hello")
        }

        val logout=findViewById<com.google.android.material.button.MaterialButton>(R.id.logout)
        logout.setOnClickListener()
        {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }
            val auth = FirebaseAuth.getInstance()
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val url=""
            Log.d("Hello2","Hello2")
            uploadDocumentToServer(this,uri){ media->
                if(media!=null){
                    profileImageUrl = media.data.url
                }
                else{
                    profileImageUrl = null
                }
            }
            profileImage.setImageURI(uri)

        } else{
            profileImage.setImageResource(R.drawable.ic_profile_pic)
            profileImageUrl = null
        }
    }

}