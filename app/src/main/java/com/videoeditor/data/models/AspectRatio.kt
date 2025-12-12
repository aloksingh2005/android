package com.videoeditor.data.models

enum class AspectRatio(val ratio: String, val width: Int, val height: Int, val description: String) {
    VERTICAL_9_16("9:16", 1080, 1920, "Instagram Reels, TikTok"),
    HORIZONTAL_16_9("16:9", 1920, 1080, "YouTube, Landscape"),
    SQUARE_1_1("1:1", 1080, 1080, "Instagram Post"),
    PORTRAIT_4_5("4:5", 1080, 1350, "Instagram Portrait"),
    CLASSIC_4_3("4:3", 1440, 1080, "Classic"),
    CINEMATIC_21_9("21:9", 2560, 1080, "Cinematic");
    
    fun getAspectRatioFloat(): Float = width.toFloat() / height.toFloat()
}
