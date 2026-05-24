package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_config")
data class AppConfig(
    @PrimaryKey val id: Int = 1,
    val userEmail: String = "",
    val isLoggedIn: Boolean = false,
    val serviceDescription: String = "",
    val targetIndustry: String = "",
    val targetSize: String = "",
    val targetCount: Int = 5,
    val whatsappApiKey: String = "",
    val instagramApiKey: String = ""
)

@Entity(tableName = "leads")
data class Lead(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val companyName: String,
    val industry: String,
    val instagram: String,
    val whatsapp: String,
    val size: String,
    val outreachStatus: String = "غير متصل", // Arabic translation: "غير متصل", "تجهيز الرسالة", "جاري الإرسال", "تم الإرسال / تفاعل", "تم الرد"
    val customPitchText: String = "",
    val chatHistory: String = "" // Serialized JSON or plain text chat history with Gemini
)
