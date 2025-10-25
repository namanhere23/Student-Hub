package com.namangulati.studenthub.adapters

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.namangulati.studenthub.API.FcmUtilits.sendBroadcastMessage
import com.namangulati.studenthub.databinding.PapersRecyclerBinding
import com.namangulati.studenthub.models.NotConfirmedPapersModel
import com.namangulati.studenthub.models.PapersModel
import com.namangulati.studenthub.utils.FirebasePapersDatabaseUtils

class AcceptOrRejectPapersAdapter (
    private val context: Context,
    private val papers: List<NotConfirmedPapersModel>,
) : RecyclerView.Adapter<AcceptOrRejectPapersAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: PapersRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PapersRecyclerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paper = papers[position]
        holder.binding.tvSubject.text = "Subject: ${paper.subject ?: ""}"
        holder.binding.tvSemester.text = "Semester: ${paper.semester ?: ""}"
        holder.binding.tvYear.text = "Year: ${paper.year ?: ""}"
        holder.binding.tvExam.text = "Exam: ${paper.exam ?: ""}"

        val fileUrl = paper.link ?: ""

        holder.binding.materialButton.setOnClickListener {
            if (fileUrl.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(fileUrl), "application/pdf")
                intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.startActivity(intent)
            }
        }

        holder.binding.materialButton2.setOnClickListener {
            if (fileUrl.isNotEmpty()) {
                val downloadUrl = fileUrl.replace("/upload/", "/upload/fl_attachment/")
                val fileName = fileUrl.substringAfterLast("/")
                val request = DownloadManager.Request(Uri.parse(downloadUrl))
                    .setTitle("Downloading $fileName")
                    .setDescription("Please wait...")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

                val downloadManager =
                    context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                downloadManager.enqueue(request)
            }
        }

        holder.binding.tick.visibility= View.VISIBLE
        holder.binding.cross.visibility=View.VISIBLE

        holder.binding.tick.setOnClickListener(){
            val paperKey = paper.key
            if (paperKey.isNullOrEmpty()) {
                Toast.makeText(context, "Cannot delete: paper key is missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            FirebasePapersDatabaseUtils.confirmPapers(context,paper){
                FirebasePapersDatabaseUtils.deletePaper(context,paper.key!!){
                    holder.itemView.post {
                        sendBroadcastMessage("New Paper Added")
                        val pos = holder.adapterPosition
                        if (pos != RecyclerView.NO_POSITION) {
                            (papers as MutableList).removeAt(pos)
                            notifyItemRemoved(pos)
                            notifyItemRangeChanged(pos, papers.size)
                        }
                    }
                }
            } }

        holder.binding.cross.setOnClickListener(){
            val paperKey = paper.key
            if (paperKey.isNullOrEmpty()) {
                Toast.makeText(context, "Cannot delete: paper key is missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebasePapersDatabaseUtils.deletePaper(context,paper.key!!){
                holder.itemView.post {
                    val pos = holder.adapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        (papers as MutableList).removeAt(pos)
                        notifyItemRemoved(pos)
                        notifyItemRangeChanged(pos, papers.size)
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int = papers.size
}