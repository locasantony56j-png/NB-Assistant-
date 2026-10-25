package com.example.model

import androidx.compose.ui.graphics.Color

enum class AppCategory(val title: String) {
    ALL("All"),
    COMMUNICATION("Communication"),
    SOCIAL("Social Media"),
    ENTERTAINMENT("Video & Music"),
    AI_AND_GOOGLE("Google & AI"),
    TOOLS_PAYMENT("Tools & Services")
}

data class AppItem(
    val id: String,
    val name: String,
    val packageName: String,
    val category: AppCategory,
    val aliases: List<String>,
    val iconColor: Color,
    val badgeBgColor: Color,
    val webFallbackUrl: String = "",
    val subLabel: String? = null,
    val supportsDeepScroll: Boolean = true,
    val supportsDeepSearch: Boolean = true,
    val supportsMessageRead: Boolean = false,
    val isInstalled: Boolean = false
)

object AppRegistry {
    // 36 Apps arranged in the exact order of the user's screenshot
    val allApps: List<AppItem> = listOf(
        // Row 1
        AppItem(
            id = "whatsapp",
            name = "WhatsApp",
            packageName = "com.whatsapp",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("whatsapp", "হোয়াটসঅ্যাপ", "whats app", "হোয়াটস অ্যাপ"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFF25D366),
            webFallbackUrl = "https://wa.me/",
            supportsMessageRead = true
        ),
        AppItem(
            id = "phone",
            name = "Phone",
            packageName = "com.google.android.dialer",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("phone", "ফোন", "কল", "dialer", "কল দাও"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFF1E8E3E),
            webFallbackUrl = "tel:"
        ),
        AppItem(
            id = "sms",
            name = "Messages",
            packageName = "com.google.android.apps.messaging",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("messages", "মেসেজ", "sms", "মেসেজেস"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFF1A73E8),
            webFallbackUrl = "sms:",
            supportsMessageRead = true
        ),
        AppItem(
            id = "camera",
            name = "Camera",
            packageName = "android.media.action.STILL_IMAGE_CAMERA",
            category = AppCategory.TOOLS_PAYMENT,
            aliases = listOf("camera", "ক্যামেরা", "ছবি তোলো", "ভিডিও করো"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFFF29900)
        ),
        AppItem(
            id = "contacts",
            name = "Contacts",
            packageName = "com.google.android.contacts",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("contacts", "কন্টাক্ট", "কন্টাক্টস", "নাম্বার"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFF1A73E8)
        ),
        AppItem(
            id = "chatgpt",
            name = "ChatGPT",
            packageName = "com.openai.chatgpt",
            category = AppCategory.AI_AND_GOOGLE,
            aliases = listOf("chatgpt", "চ্যাটজিপিটি", "chat gpt"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFF10A37F),
            webFallbackUrl = "https://chatgpt.com/"
        ),

        // Row 2
        AppItem(
            id = "youtube",
            name = "YouTube",
            packageName = "com.google.android.youtube",
            category = AppCategory.ENTERTAINMENT,
            aliases = listOf("youtube", "ইউটিউব", "ইউটিউবে"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFFFF0000),
            webFallbackUrl = "https://www.youtube.com/"
        ),
        AppItem(
            id = "facebook",
            name = "Facebook",
            packageName = "com.facebook.katana",
            category = AppCategory.SOCIAL,
            aliases = listOf("facebook", "ফেসবুক", "fb", "এফবি"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFF1877F2),
            webFallbackUrl = "https://www.facebook.com/"
        ),
        AppItem(
            id = "messenger",
            name = "Messenger",
            packageName = "com.facebook.orca",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("messenger", "মেসেঞ্জার"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFF0084FF),
            webFallbackUrl = "https://m.me/",
            supportsMessageRead = true
        ),
        AppItem(
            id = "instagram",
            name = "Instagram",
            packageName = "com.instagram.android",
            category = AppCategory.SOCIAL,
            aliases = listOf("instagram", "ইন্সটাগ্রাম", "insta", "রিলস"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFFE1306C),
            webFallbackUrl = "https://www.instagram.com/"
        ),
        AppItem(
            id = "gmail",
            name = "Gmail",
            packageName = "com.google.android.gm",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("gmail", "জিমেইল", "email", "ইমেইল", "মেইল"),
            iconColor = Color(0xFFEA4335),
            badgeBgColor = Color.White,
            webFallbackUrl = "https://mail.google.com/"
        ),
        AppItem(
            id = "maps",
            name = "Google Maps",
            packageName = "com.google.android.apps.maps",
            category = AppCategory.AI_AND_GOOGLE,
            aliases = listOf("maps", "ম্যাপ", "google maps", "গুগল ম্যাপ", "ম্যাপস"),
            iconColor = Color(0xFF34A853),
            badgeBgColor = Color.White,
            webFallbackUrl = "https://maps.google.com/"
        ),

        // Row 3
        AppItem(
            id = "chrome",
            name = "Chrome",
            packageName = "com.android.chrome",
            category = AppCategory.AI_AND_GOOGLE,
            aliases = listOf("chrome", "ক্রোম", "browser", "ব্রাউজার"),
            iconColor = Color(0xFF4285F4),
            badgeBgColor = Color.White,
            webFallbackUrl = "https://www.google.com/"
        ),
        AppItem(
            id = "playstore",
            name = "Play Store",
            packageName = "com.android.vending",
            category = AppCategory.TOOLS_PAYMENT,
            aliases = listOf("play store", "প্লে স্টোর", "install", "ইনস্টল", "ডাউনলোড"),
            iconColor = Color(0xFF00875A),
            badgeBgColor = Color.White,
            webFallbackUrl = "https://play.google.com/store"
        ),
        AppItem(
            id = "photos",
            name = "Google Photos",
            packageName = "com.google.android.apps.photos",
            category = AppCategory.AI_AND_GOOGLE,
            aliases = listOf("photos", "ফটো", "gallery", "গ্যালারি", "ছবি"),
            iconColor = Color(0xFFFBBC05),
            badgeBgColor = Color.White,
            webFallbackUrl = "https://photos.google.com/"
        ),
        AppItem(
            id = "telegram",
            name = "Telegram",
            packageName = "org.telegram.messenger",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("telegram", "টেলিগ্রাম"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFF24A1DE),
            webFallbackUrl = "https://t.me/",
            supportsMessageRead = true
        ),
        AppItem(
            id = "imo",
            name = "imo",
            packageName = "com.imo.android.imoim",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("imo", "ইমো"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFF00B0FF),
            webFallbackUrl = "https://imo.im/"
        ),
        AppItem(
            id = "gemini",
            name = "Gemini",
            packageName = "com.google.android.apps.bard",
            category = AppCategory.AI_AND_GOOGLE,
            aliases = listOf("gemini", "জেমিনি"),
            iconColor = Color(0xFF8AB4F8),
            badgeBgColor = Color(0xFF1E293B),
            webFallbackUrl = "https://gemini.google.com/"
        ),

        // Row 4
        AppItem(
            id = "tiktok",
            name = "TikTok",
            packageName = "com.zhiliaoapp.musically",
            category = AppCategory.SOCIAL,
            aliases = listOf("tiktok", "টিকটক"),
            iconColor = Color.White,
            badgeBgColor = Color.Black,
            webFallbackUrl = "https://www.tiktok.com/"
        ),
        AppItem(
            id = "calculator",
            name = "Calculator",
            packageName = "com.google.android.calculator",
            category = AppCategory.TOOLS_PAYMENT,
            aliases = listOf("calculator", "ক্যালকুলেটর", "হিসাব", "যোগ", "গুণ", "ভাগ"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFFEA4335)
        ),
        AppItem(
            id = "clock",
            name = "Clock",
            packageName = "com.google.android.deskclock",
            category = AppCategory.TOOLS_PAYMENT,
            aliases = listOf("clock", "ঘড়ি", "alarm", "অ্যালার্ম", "টাইমার"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFF1A73E8),
            subLabel = "Android app only"
        ),
        AppItem(
            id = "calendar",
            name = "Calendar",
            packageName = "com.google.android.calendar",
            category = AppCategory.AI_AND_GOOGLE,
            aliases = listOf("calendar", "ক্যালেন্ডার", "রুটিন", "ইভেন্ট"),
            iconColor = Color(0xFF1A73E8),
            badgeBgColor = Color.White,
            webFallbackUrl = "https://calendar.google.com/"
        ),
        AppItem(
            id = "drive",
            name = "Google Drive",
            packageName = "com.google.android.apps.docs",
            category = AppCategory.AI_AND_GOOGLE,
            aliases = listOf("drive", "ড্রাইভ", "google drive"),
            iconColor = Color(0xFF0F9D58),
            badgeBgColor = Color.White,
            webFallbackUrl = "https://drive.google.com/"
        ),
        AppItem(
            id = "meet",
            name = "Google Meet",
            packageName = "com.google.android.apps.tachyon",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("meet", "মিট", "google meet"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFF00897B),
            webFallbackUrl = "https://meet.google.com/"
        ),

        // Row 5
        AppItem(
            id = "zoom",
            name = "Zoom",
            packageName = "us.zoom.videomeetings",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("zoom", "জুম", "মিটিং"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFF2D8CFF),
            webFallbackUrl = "https://zoom.us/"
        ),
        AppItem(
            id = "translate",
            name = "Google Translate",
            packageName = "com.google.android.apps.translate",
            category = AppCategory.AI_AND_GOOGLE,
            aliases = listOf("translate", "অনুবাদ", "translator", "অর্থ"),
            iconColor = Color(0xFF1A73E8),
            badgeBgColor = Color.White,
            webFallbackUrl = "https://translate.google.com/"
        ),
        AppItem(
            id = "spotify",
            name = "Spotify",
            packageName = "com.spotify.music",
            category = AppCategory.ENTERTAINMENT,
            aliases = listOf("spotify", "স্পটিফাই", "মিউজিক"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFF1DB954),
            webFallbackUrl = "https://open.spotify.com/"
        ),
        AppItem(
            id = "netflix",
            name = "Netflix",
            packageName = "com.netflix.mediaclient",
            category = AppCategory.ENTERTAINMENT,
            aliases = listOf("netflix", "নেটফ্লিক্স", "মুভি"),
            iconColor = Color(0xFFE50914),
            badgeBgColor = Color.Black,
            webFallbackUrl = "https://www.netflix.com/"
        ),
        AppItem(
            id = "twitter",
            name = "X (Twitter)",
            packageName = "com.twitter.android",
            category = AppCategory.SOCIAL,
            aliases = listOf("twitter", "টুইটার", "x", "টুইট"),
            iconColor = Color.White,
            badgeBgColor = Color.Black,
            webFallbackUrl = "https://x.com/"
        ),
        AppItem(
            id = "linkedin",
            name = "LinkedIn",
            packageName = "com.linkedin.android",
            category = AppCategory.SOCIAL,
            aliases = listOf("linkedin", "লিঙ্কডইন"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFF0A66C2),
            webFallbackUrl = "https://www.linkedin.com/"
        ),

        // Row 6
        AppItem(
            id = "snapchat",
            name = "Snapchat",
            packageName = "com.snapchat.android",
            category = AppCategory.SOCIAL,
            aliases = listOf("snapchat", "স্ন্যাপচ্যাট", "snap"),
            iconColor = Color.Black,
            badgeBgColor = Color(0xFFFFFC00),
            webFallbackUrl = "https://www.snapchat.com/"
        ),
        AppItem(
            id = "bkash",
            name = "bKash",
            packageName = "com.bKash.customerapp",
            category = AppCategory.TOOLS_PAYMENT,
            aliases = listOf("bkash", "বিকাশ", "bikash"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFFE2136E),
            webFallbackUrl = "https://www.bkash.com/"
        ),
        AppItem(
            id = "nagad",
            name = "Nagad",
            packageName = "com.konasl.nagad",
            category = AppCategory.TOOLS_PAYMENT,
            aliases = listOf("nagad", "নগদ"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFFF7941D),
            webFallbackUrl = "https://nagad.com.bd/"
        ),
        AppItem(
            id = "daraz",
            name = "Daraz",
            packageName = "com.daraz.android",
            category = AppCategory.TOOLS_PAYMENT,
            aliases = listOf("daraz", "দারাজ", "শপিং"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFFFF6900),
            webFallbackUrl = "https://www.daraz.com.bd/"
        ),
        AppItem(
            id = "files",
            name = "Files / Downloads",
            packageName = "com.google.android.documentsui",
            category = AppCategory.TOOLS_PAYMENT,
            aliases = listOf("files", "ফাইল", "downloads", "ডাউনলোডস", "ডকুমেন্ট"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFFF4B400)
        ),
        AppItem(
            id = "settings",
            name = "Android Settings",
            packageName = "android.settings.SETTINGS",
            category = AppCategory.TOOLS_PAYMENT,
            aliases = listOf("settings", "সেটিংস", "ফোন সেটিংস", "ওয়াইফাই", "ব্লুটুথ"),
            iconColor = Color.White,
            badgeBgColor = Color(0xFF5F6368)
        )
    )

    fun findApp(query: String): AppItem? {
        val q = query.lowercase().trim()
        return allApps.firstOrNull { it.id == q || it.name.lowercase() == q }
            ?: allApps.firstOrNull { it.aliases.any { alias -> q.contains(alias) || alias.contains(q) } }
    }
}
