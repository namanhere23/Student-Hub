package com.namangulati.studenthub.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.vector.Group
import androidx.lifecycle.ViewModelProvider
import com.namangulati.studenthub.LiveDataViewModel
import com.namangulati.studenthub.R
import com.namangulati.studenthub.databinding.FragmentNewGroupBinding
import com.namangulati.studenthub.databinding.FragmentUploadBinding
import com.namangulati.studenthub.models.Groups
import com.namangulati.studenthub.models.NotConfirmedPapersModel
import com.namangulati.studenthub.models.UserDetailsModel
import com.namangulati.studenthub.utils.FirebasePapersDatabaseUtils
import com.namangulati.studenthub.utils.FirebaseUserDatabaseUtils.addGroups
import com.namangulati.studenthub.utils.FirebaseUserDatabaseUtils.saveUser
import com.namangulati.studenthub.utils.MediaUploadUtils.uploadDocumentToServer

class NewGroupFragment : Fragment() {
    var uploadedUrl=""
    lateinit var profile: UserDetailsModel
    private lateinit var binding: FragmentNewGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewGroupBinding.inflate(inflater, container, false)
        val name=binding.tvName

        binding.UploadDoc.setOnClickListener{
            binding.progressBar.visibility=View.VISIBLE
            if (name.text.toString().isNotEmpty()) {
                try{
                    pickDocument.launch("image/*")
                } catch (error:Error){
                    Toast.makeText(requireContext(),"Unable to Upload", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility=View.GONE
                }
            } else {
                Toast.makeText(requireContext(),"Please fill all the details", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility=View.GONE
                return@setOnClickListener
            }
        }

        binding.Submit.setOnClickListener{
            val group=binding.tvGroup.text
            val groups=arrayListOf<String>()
            var groupsString=""
            for(ele in group){
                if(ele==','){
                    groups.add(groupsString)
                    groupsString=""
                } else {
                    groupsString+=ele
                }
            }
            groups.add(groupsString)

            if(!uploadedUrl.isNullOrEmpty()){
                try{
                    addGroups(requireContext(),name.text.toString(),uploadedUrl,groups){
                            name.text.clear()
                            binding.tvGroup.text.clear()
                            uploadedUrl=""
                            Toast.makeText(requireContext(),"Group Created",Toast.LENGTH_SHORT).show()
                    }
                     } catch (error:Error){
                    Toast.makeText(requireContext(),"Group Not Created",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } else {
                Toast.makeText(requireContext(),"Please Upload Picture",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }

        return binding.root


    }

    private val pickDocument = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            uploadDocumentToServer(requireContext(), uri) { media ->
                binding.progressBar.visibility=View.GONE
                if (media != null) {
                    uploadedUrl = media.data.url
                } else {
                    Toast.makeText(requireContext(), "Failed to upload document", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.d("UploadFragment", "No file selected")
        }
    }

}