package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val responseMimeType: String? = null,
    val responseSchema: ResponseSchema? = null,
    val temperature: Float? = null
)

@JsonClass(generateAdapter = true)
data class ResponseSchema(
    val type: String, // "OBJECT", "ARRAY", "STRING", etc.
    val description: String? = null,
    val properties: Map<String, ResponseSchema>? = null,
    val required: List<String>? = null,
    val items: ResponseSchema? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content? = null
)

// Helper models for B2B parsed lead generation
@JsonClass(generateAdapter = true)
data class TargetBusiness(
    val name: String,
    val industry: String,
    val instagram: String,
    val whatsapp: String,
    val size: String
)

@JsonClass(generateAdapter = true)
data class LeadListContainer(
    val leads: List<TargetBusiness>
)
