package com.example.data.model

/**
 * All available and supported Gemini and generative models as defined by the official Gemini API specification.
 *
 * NOTE: Legacy models (such as gemini-1.5-flash, gemini-1.5-pro, gemini-2.0-flash, gemini-2.0-pro)
 * are strictly prohibited and omitted to guarantee requests are accepted by current API endpoints.
 */
enum class GeminiModel(
    val modelId: String,
    val displayName: String,
    val category: ModelCategory,
    val badge: String,
    val description: String,
    val speed: String,
    val contextInfo: String
) {
    // --- 1. Core Text & Reasoning Models ---
    GEMINI_3_5_FLASH(
        modelId = "gemini-3.5-flash",
        displayName = "Gemini 3.5 Flash",
        category = ModelCategory.TEXT_AND_REASONING,
        badge = "Default • Recommended",
        description = "Next-generation multimodal model balanced for rapid coding, threat triage, DevSecOps reasoning, and general Q&A.",
        speed = "Very Fast",
        contextInfo = "General Text, Code, & Multimodal"
    ),

    GEMINI_3_1_PRO(
        modelId = "gemini-3.1-pro-preview",
        displayName = "Gemini 3.1 Pro Preview",
        category = ModelCategory.TEXT_AND_REASONING,
        badge = "Frontier Reasoning",
        description = "Advanced reasoning engine optimized for complex vulnerability analysis, reverse engineering logic, STEM, and deep code auditing.",
        speed = "Standard",
        contextInfo = "Complex Reasoning, Math, & STEM"
    ),

    GEMINI_3_1_FLASH_LITE(
        modelId = "gemini-3.1-flash-lite-preview",
        displayName = "Gemini 3.1 Flash Lite",
        category = ModelCategory.TEXT_AND_REASONING,
        badge = "Ultra Low Latency",
        description = "High-throughput lightweight model tailored for sub-second alert triage, threat summarization, and rapid querying.",
        speed = "Ultra-Fast",
        contextInfo = "Lightweight Text & Real-Time Summaries"
    ),

    GEMINI_FLASH_LATEST(
        modelId = "gemini-flash-latest",
        displayName = "Gemini Flash Latest",
        category = ModelCategory.TEXT_AND_REASONING,
        badge = "Stable Channel",
        description = "Alias pointing dynamically to the latest stable production release of the Gemini Flash family.",
        speed = "Fast",
        contextInfo = "Production Stable Multimodal"
    ),

    // --- 2. Vision & Image Generation Models ---
    GEMINI_2_5_FLASH_IMAGE(
        modelId = "gemini-2.5-flash-image",
        displayName = "Gemini 2.5 Flash Image",
        category = ModelCategory.IMAGE_AND_VISION,
        badge = "Diagram & Vision",
        description = "Specialized for generative architecture diagrams, threat topology visualization, and visual payload analysis.",
        speed = "Fast",
        contextInfo = "Image Gen & Visual Editing"
    ),

    GEMINI_3_1_FLASH_IMAGE(
        modelId = "gemini-3.1-flash-image-preview",
        displayName = "Gemini 3.1 Flash Image Preview",
        category = ModelCategory.IMAGE_AND_VISION,
        badge = "High-Res 4K Vision",
        description = "High-fidelity visual generation supporting 512px, 1K, 2K, and 4K resolution architectural renderings.",
        speed = "Standard",
        contextInfo = "High-Res Visual Artifacts (up to 4K)"
    ),

    GEMINI_3_PRO_IMAGE(
        modelId = "gemini-3-pro-image-preview",
        displayName = "Gemini 3 Pro Image Preview",
        category = ModelCategory.IMAGE_AND_VISION,
        badge = "Pro Vision",
        description = "Professional visual reasoning and diagram synthesis for complex attack chains and multi-stage exploit graphs.",
        speed = "Deep Reasoning",
        contextInfo = "Pro Visual Intelligence & Reasoning"
    ),

    // --- 3. Speech & Audio Models ---
    GEMINI_2_5_FLASH_TTS(
        modelId = "gemini-2.5-flash-preview-tts",
        displayName = "Gemini 2.5 Flash TTS",
        category = ModelCategory.AUDIO_AND_SPEECH,
        badge = "Speech Synthesis",
        description = "High-fidelity text-to-speech engine for spoken security briefings, morning podcast debriefs, and audio alerts.",
        speed = "Fast",
        contextInfo = "Text-to-Speech Voice Generation"
    ),

    GEMINI_2_5_FLASH_AUDIO(
        modelId = "gemini-2.5-flash-native-audio-preview-12-2025",
        displayName = "Gemini 2.5 Native Audio Preview",
        category = ModelCategory.AUDIO_AND_SPEECH,
        badge = "Native Audio",
        description = "Native audio perception and conversational understanding for voice-driven hands-free incident response.",
        speed = "Real-Time",
        contextInfo = "Real-Time Spoken Audio Dialog"
    ),

    // --- 4. Generative Video Models ---
    VEO_3_1_FAST(
        modelId = "veo-3.1-fast-generate-preview",
        displayName = "Veo 3.1 Fast Video Preview",
        category = ModelCategory.VIDEO_GENERATION,
        badge = "Fast Video Gen",
        description = "Generates high-speed procedural video simulations depicting security breach flows and network attack replays.",
        speed = "Fast Gen",
        contextInfo = "General Video Generation"
    ),

    VEO_3_1_GENERATE(
        modelId = "veo-3.1-generate-preview",
        displayName = "Veo 3.1 High-Quality Video",
        category = ModelCategory.VIDEO_GENERATION,
        badge = "Cinema Video Gen",
        description = "Cinema-grade 1080p generative video simulation for executive post-incident briefings and visual retrospectives.",
        speed = "Deep Gen",
        contextInfo = "High-Quality 1080p Video Generation"
    ),

    // --- 5. Semantic Vector Embeddings ---
    GEMINI_EMBEDDING_2(
        modelId = "gemini-embedding-2-preview",
        displayName = "Gemini Embedding 2 Preview",
        category = ModelCategory.EMBEDDINGS,
        badge = "Dense Vector Embeddings",
        description = "High-density vector embeddings for retrieval-augmented generation (RAG) and threat similarity analysis.",
        speed = "Ultra-Fast",
        contextInfo = "Vector Embedding & Semantic Indexing"
    );

    companion object {
        fun defaultModel(): GeminiModel = GEMINI_3_5_FLASH

        fun fromModelId(id: String?): GeminiModel {
            return entries.firstOrNull { it.modelId == id } ?: defaultModel()
        }
    }
}

enum class ModelCategory(val categoryName: String, val iconEmoji: String) {
    TEXT_AND_REASONING("Text & Reasoning", "🧠"),
    IMAGE_AND_VISION("Image & Vision", "🎨"),
    AUDIO_AND_SPEECH("Audio & Voice", "🎙️"),
    VIDEO_GENERATION("Video Simulation", "🎬"),
    EMBEDDINGS("Semantic Embeddings", "📐")
}
