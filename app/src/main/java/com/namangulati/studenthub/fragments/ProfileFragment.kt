package com.namangulati.studenthub.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.namangulati.studenthub.R
import com.namangulati.studenthub.databinding.FragmentProfileBinding
import com.namangulati.studenthub.models.UserDetailsModel
import com.namangulati.studenthub.userPages.ChatBot
import com.namangulati.studenthub.userPages.Details_Page
import com.namangulati.studenthub.utils.FirebaseUserDatabaseUtils.loadUserByUid

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val person = arguments?.getSerializable("EXTRA_USER_DETAILS") as? UserDetailsModel
        val profilePic = view.findViewById<ImageView>(R.id.profilePic)
        loadUserByUid(requireContext(), person?.uid!!) { user ->
            if (user != null) {
                if (!user.photo.isNullOrEmpty()) {
                    val imageUrl = user.photo?.replace("http://", "https://")
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .into(profilePic)
                } else {
                    profilePic.setImageResource(R.drawable.ic_profile_pic)
                }
            }

            profilePic.setOnClickListener {
                Intent(requireContext(), Details_Page::class.java).also {
                    it.putExtra("EXTRA_USER_DETAILS", person)
                    startActivity(it)
                }
            }

            val btnChatbot = view?.findViewById<MaterialButton>(R.id.btnChatbot)
            btnChatbot?.setOnClickListener {
                val intent = Intent(requireContext(), ChatBot::class.java)
                intent.putExtra("EXTRA_USER_DETAILS", person)
                startActivity(intent)
            }
        }


    }

}