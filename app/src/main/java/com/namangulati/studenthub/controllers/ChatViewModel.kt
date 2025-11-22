package com.namangulati.studenthub.controllers

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson
import com.namangulati.studenthub.API.Constants
import com.namangulati.studenthub.models.Message
import com.namangulati.studenthub.models.UserDetailsModel
import kotlinx.coroutines.launch

class ChatViewModel: ViewModel() {
    val messageList = mutableStateListOf<Message>()

    private val gson = Gson()
    private var chat: com.google.ai.client.generativeai.Chat? = null

    private val systemPrompt = """
         You are the "Student Hub" AI assistant. Always reply in plain, natural conversational text — like you are a helpful and knowledgeable senior on campus.
         Rules:
         You can also access https://iiitl.ac.in/ website for more information
         Do not use Bold text anywhere.
         Use ONLY the information that exists in the given knowledge base (e.g., {academicSchedules}, {clubEvents}, {facultyDirectory}, {messMenu}, {adminNotices}).
         Never invent or suggest information, deadlines, or events outside this provided data.
         When providing specific info, be clear. For events, mention the name, date, time, and venue. For academic info, give the relevant summary or link.
         Keep the tone friendly, reliable, and empathetic, like a senior guiding a fellow student.
         Do not reply in JSON or code format. Only use normal human-style text.
         You will also be given the student's past query history; please use that to give more relevant answers (e.g., if they often ask about the CSE department, prioritize that info).
         Restrictions:
         Stick strictly to IIIT Lucknow-related topics (academics, campus life, events, administration, clubs).
         Never provide personal opinions, gossip, or information about other colleges.
         Politely redirect any off-topic queries back to the campus context. For example: "I can't help with that, but I can tell you what's on the mess menu tonight if you're interested!"
         If you don't have the answer, be honest. Say something like, "I don't have that specific information right now. You might want to check the official college website or ask the student council."
    """.trimIndent()


    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = Constants.API_KEY_GEMINI
    )

    init {
        chat = generativeModel.startChat(
            history = listOf(
                content(role = "user") { text(systemPrompt) },
                content(role = "model") { text("Understood. I will only answer about Technical Studies.") }
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun sendMessage(question: String) {
        viewModelScope.launch {
            try {
                messageList.add(Message(question, "user"))
                messageList.add(Message("Typing...", "model"))
                val fullPrompt = systemPrompt
                val chat = generativeModel.startChat(
                    history = buildList {
                        add(content(role = "user") { text(fullPrompt) })
                        add(content(role = "model") { text("Understood. I will only answer about Technical Studies.") })
                        addAll(messageList.mapNotNull { msg ->
                            msg.message?.let { nonNullMessage ->
                                val role = if (msg.senderId == "user") "user" else "model"
                                content(role = role) { text(nonNullMessage) }
                            }
                        })
                    }
                )
                val response = chat.sendMessage(question)
                messageList.removeLast()
                messageList.add(Message(response.text.toString(), "model"))
            } catch (e: Exception) {
                messageList.removeLast()
                messageList.add(Message("Error: ${e.message}", "model"))
            }
        }

    }

}