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
import androidx.lifecycle.ViewModelProvider
import com.namangulati.studenthub.LiveDataViewModel
import com.namangulati.studenthub.databinding.FragmentUploadBinding
import com.namangulati.studenthub.models.NotConfirmedPapersModel
import com.namangulati.studenthub.models.PapersModel
import com.namangulati.studenthub.models.UserDetailsModel
import com.namangulati.studenthub.utils.FirebasePapersDatabaseUtils
import com.namangulati.studenthub.utils.MediaUploadUtils.uploadDocumentToServer

class UploadFragment : Fragment() {
    var uploadedUrl=""
    lateinit var profile:UserDetailsModel
    private lateinit var binding: FragmentUploadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel = ViewModelProvider(requireActivity()).get(LiveDataViewModel::class.java)
        viewModel.user.observe(viewLifecycleOwner) { user ->
            profile=user
        }

        binding = FragmentUploadBinding.inflate(inflater, container, false)
        val subject=binding.tvSubject
        val semester=binding.tvSemester
        val year=binding.tvYear
        val exam=binding.tvExam

        binding.UploadDoc.setOnClickListener{
            binding.progressBar.visibility=View.VISIBLE
            if (subject.text.toString().isNotEmpty() && semester.text.toString().isNotEmpty() && year.text.toString().isNotEmpty() && exam.text.toString().isNotEmpty()) {
                try{
                    pickDocument.launch("*/*")
                } catch (error:Error){
                    Toast.makeText(requireContext(),"Unable to Upload",Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility=View.GONE
                }
            } else {
                Toast.makeText(requireContext(),"Please fill all the details",Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility=View.GONE
                return@setOnClickListener
            }
        }

        binding.Submit.setOnClickListener{
            if(!uploadedUrl.isNullOrEmpty()){
                val paper= NotConfirmedPapersModel(subject.text.toString(),semester.text.toString().toIntOrNull(),year.text.toString().toIntOrNull(),exam.text.toString(),uploadedUrl,profile.email)
                try{
                FirebasePapersDatabaseUtils.addPapers(requireContext(),paper){
                    Toast.makeText(requireContext(),"Paper Submitted",Toast.LENGTH_SHORT).show()
                    binding.tvSubject.text.clear()
                    binding.tvSemester.text.clear()
                    binding.tvYear.text.clear()
                    binding.tvExam.text.clear()
                    uploadedUrl=""
                } } catch (error:Error){
                    Toast.makeText(requireContext(),"Paper Not Submitted",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            } else {
                Toast.makeText(requireContext(),"Please Upload Document",Toast.LENGTH_SHORT).show()
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