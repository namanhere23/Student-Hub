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
import com.namangulati.studenthub.databinding.FragmentUploadBinding
import com.namangulati.studenthub.models.PapersModel
import com.namangulati.studenthub.utils.FirebasePapersDatabaseUtils
import com.namangulati.studenthub.utils.MediaUploadUtils.uploadDocumentToServer

class UploadFragment : Fragment() {
    var uploadedUrl=""
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
        binding = FragmentUploadBinding.inflate(inflater, container, false)
        val subject=binding.tvSubject.text
        val semester=binding.tvSemester.text
        val year=binding.tvYear.text
        val exam=binding.tvExam.text

        binding.UploadDoc.setOnClickListener{
            binding.progressBar.visibility=View.VISIBLE
            if (subject.toString().isNotEmpty() && semester.toString().isNotEmpty() && year.toString().isNotEmpty() && exam.toString().isNotEmpty()) {
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
                val paper= PapersModel(subject.toString(),semester.toString().toIntOrNull(),year.toString().toIntOrNull(),exam.toString(),uploadedUrl)
                try{
                FirebasePapersDatabaseUtils.addPapers(requireContext(),paper){
                        Toast.makeText(requireContext(),"Paper Submitted",Toast.LENGTH_SHORT).show()
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