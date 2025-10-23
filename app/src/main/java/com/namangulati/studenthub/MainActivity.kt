package com.namangulati.studenthub

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.namangulati.studenthub.models.UserDetailsModel
import com.namangulati.studenthub.userPages.Dashboard
import com.namangulati.studenthub.userPages.Details_Page
import com.namangulati.studenthub.utils.FirebaseLoginAuth
import com.namangulati.studenthub.utils.FirebaseUserDatabaseUtils
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnContinue: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        var isLoading = true

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            var uid= currentUser.uid
                FirebaseUserDatabaseUtils.loadUserByUid(this,uid) { user ->
                    if (user != null) {
                        val userDetails = UserDetailsModel(user.name?: "", currentUser.email ?: "", user.mobile?: "", user.photo?: "", user.uid, user.groups?: arrayListOf())
                        if (user.mobile?.length!=10) {
                            Intent(this, Details_Page::class.java).apply {
                                putExtra("EXTRA_USER_DETAILS", userDetails)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                finish()
                                startActivity(this)
                            }
                        } else {
                            Intent(this, Dashboard::class.java).apply {
                                putExtra("EXTRA_USER_DETAILS", userDetails)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                finish()
                                startActivity(this)
                            }
                        }
                    }

                    else
                    {
                        val userDetails = UserDetailsModel( "",  "", "", "", uid, arrayListOf())
                        Intent(this, Details_Page::class.java).apply {
                            putExtra("EXTRA_USER_DETAILS", userDetails)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            finish()
                            startActivity(this)
                        }
                    }
                }




        }

        else
        {
            Handler(Looper.getMainLooper()).postDelayed({
                isLoading = false
            }, 2000)
            val kk=findViewById<LinearLayout>(R.id.main)
            kk.visibility= View.VISIBLE
        }

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnContinue = findViewById(R.id.cont)

        btnContinue.setOnClickListener {
            val emailText = etEmail.text.toString().trim()

            val iiitlPattern = Regex("^[A-Za-z0-9._%+-]+@iiitl\\.ac\\.in$", RegexOption.IGNORE_CASE)

            if (!iiitlPattern.matches(emailText)) {
                Toast.makeText(this, "Please enter a valid IIITL email (e.g., name@iiitl.ac.in)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (etPassword.text.toString().length < 8) {
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            FirebaseLoginAuth.checkIfUserExists(this,etEmail.text.toString(), etPassword.text.toString())
        }

        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

        tvForgotPassword.setOnClickListener {
            val emailText = etEmail.text.toString().trim()
            val iiitlPattern = Regex("^[A-Za-z0-9._%+-]+@iiitl\\.ac\\.in$", RegexOption.IGNORE_CASE)

            if (!iiitlPattern.matches(emailText)) {
                Toast.makeText(this, "Please enter a valid IIITL email (e.g., name@iiitl.ac.in)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(emailText)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Password reset email sent! Check your inbox.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_LONG).show()}
        }

        val logoGoogle=findViewById<ImageView>(R.id.logo)

        logoGoogle.setOnClickListener {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialManager = CredentialManager.create(this)
            lifecycleScope.launch {
                try {
                    val credential = credentialManager.getCredential(this@MainActivity, request)
                    FirebaseLoginAuth.handleSignIn(this@MainActivity,credential)
                } catch (e: GetCredentialException) {
                    Log.e(TAG, "Google sign-in failed", e)
                    when {
                        e.message?.contains("no credentials available") == true -> {
                            Toast.makeText(
                                this@MainActivity,
                                "No Google accounts found. Please add a Google account to your device.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        e.message?.contains("user_canceled") == true -> {
                        }
                        else -> {
                            Toast.makeText(
                                this@MainActivity,
                                "Google sign-in failed: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }


    }
}