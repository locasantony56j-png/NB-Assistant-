package com.example.model

enum class AppCategory(val title: String) {
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
    val webFallbackUrl: String = "",
    val supportsDeepScroll: Boolean = true,
    val supportsDeepSearch: Boolean = true,
    val supportsMessageRead: Boolean = false,
    val isInstalled: Boolean = false
)

object AppRegistry {
    val allApps: List<AppItem> = listOf(
        // Communication
        AppItem(
            id = "whatsapp",
            name = "WhatsApp",
            packageName = "com.whatsapp",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("whatsapp", "হোয়াটসঅ্যাপ", "whats app"),
            webFallbackUrl = "https://wa.me/",
            supportsMessageRead = true
        ),
        AppItem(
            id = "phone",
            name = "Phone",
            packageName = "com.google.android.dialer",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("phone", "ফোন", "কল", "dialer"),
            webFallbackUrl = "tel:"
        ),
        AppItem(
            id = "sms",
            name = "Messages",
            packageName = "com.google.android.apps.messaging",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("messages", "মেসেজ", "sms"),
            webFallbackUrl = "sms:"
        ),
        AppItem(
            id = "contacts",
            name = "Contacts",
            packageName = "com.google.android.contacts",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("contacts", "কন্টাক্ট")
        ),
        AppItem(
            id = "messenger",
            name = "Messenger",
            packageName = "com.facebook.orca",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("messenger", "মেসেঞ্জার"),
            webFallbackUrl = "https://m.me/",
            supportsMessageRead = true
        ),
        AppItem(
            id = "telegram",
            name = "Telegram",
            packageName = "org.telegram.messenger",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("telegram", "টেলিগ্রাম"),
            webFallbackUrl = "https://t.me/",
            supportsMessageRead = true
        ),
        AppItem(
            id = "imo",
            name = "IMO",
            packageName = "com.imo.android.imoim",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("imo", "ইমো"),
            webFallbackUrl = "https://imo.im/"
        ),
        AppItem(
            id = "gmail",
            name = "Gmail",
            packageName = "com.google.android.gm",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("gmail", "জিমেইল", "email", "ইমেইল"),
            webFallbackUrl = "https://mail.google.com/"
        ),
        AppItem(
            id = "meet",
            name = "Google Meet",
            packageName = "com.google.android.apps.tachyon",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("meet", "মিট", "google meet"),
            webFallbackUrl = "https://meet.google.com/"
        ),
        AppItem(
            id = "zoom",
            name = "Zoom",
            packageName = "us.zoom.videomeetings",
            category = AppCategory.COMMUNICATION,
            aliases = listOf("zoom", "জুম"),
            webFallbackUrl = "https://zoom.us/"
        ),

        // Social Media
        AppItem(
            id = "facebook",
            name = "Facebook",
            packageName = "com.facebook.katana",
            category = AppCategory.SOCIAL,
            aliases = listOf("facebook", "ফেসবুক", "fb"),
            webFallbackUrl = "https://www.facebook.com/"
        ),
        AppItem(
            id = "instagram",
            name = "Instagram",
            packageName = "com.instagram.android",
            category = AppCategory.SOCIAL,
            aliases = listOf("instagram", "ইন্সটাগ্রাম", "insta"),
            webFallbackUrl = "https://www.instagram.com/"
        ),
        AppItem(
            id = "tiktok",
            name = "TikTok",
            packageName = "com.zhiliaoapp.musically",
            category = AppCategory.SOCIAL,
            aliases = listOf("tiktok", "টিকটক"),
            webFallbackUrl = "https://www.tiktok.com/"
        ),
        AppItem(
            id = "snapchat",
            name = "Snapchat",
            packageName = "com.snapchat.android",
            category = AppCategory.SOCIAL,
            aliases = listOf("snapchat", "স্ন্যাপচ্যাট"),
            webFallbackUrl = "https://www.snapchat.com/"
        ),
        AppItem(
            id = "twitter",
            name = "X (Twitter)",
            packageName = "com.twitter.android",
            category = AppCategory.SOCIAL,
            aliases = listOf("twitter", "টুইটার", "x"),
            webFallbackUrl = "https://x.com/"
        ),
        AppItem(
            id = "linkedin",
            name = "LinkedIn",
            packageName = "com.linkedin.android",
            category = AppCategory.SOCIAL,
            aliases = listOf("linkedin", "লিঙ্কডইন"),
            webFallbackUrl = "https://www.linkedin.com/"
        ),

        // Video & Music
        AppItem(
            id = "youtube",
            name = "YouTube",
            packageName = "com.google.android.youtube",
            category = AppCategory.ENTERTAINMENT,
            aliases = listOf("youtube", "ইউটিউব"),
            webFallbackUrl = "https://www.youtube.com/"
        ),
        AppItem(
            id = "spotify",
            name = "Spotify",
            packageName = "com.spotify.music",
            category = AppCategory.ENTERTAINMENT,
            aliases = listOf("spotify", "স্পটিফাই"),
            webFallbackUrl = "https://open.spotify.com/"
        ),
        AppItem(
            id = "netflix",
            name = "Netflix",
            packageName = "com.netflix.mediaclient",
            category = AppCategory.ENTERTAINMENT,
            aliases = listOf("netflix", "নেটফ্লিক্স"),
            webFallbackUrl = "https://www.netflix.com/"
        ),

        // Google & AI
        AppItem(
            id = "chatgpt",
            name = "ChatGPT",
            packageName = "com.openai.chatgpt",
            category = AppCategory.AI_AND_GOOGLE,
            aliases = listOf("chatgpt", "চ্যাটজিপিটি"),
            webFallbackUrl = "https://chatgpt.com/"
        ),
        AppItem(
            id = "gemini",
            name = "Gemini",
            packageName = "com.google.android.apps.bard",
            category = AppCategory.AI_AND_GOOGLE,
            aliases = listOf("gemini", "জেমিনি"),
            webFallbackUrl = "https://gemini.google.com/"
        ),
        AppItem(
            id = "maps",
            name = "Google Maps",
            packageName = "com.google.android.apps.maps",
            category = AppCategory.AI_AND_GOOGLE,
            aliases = listOf("maps", "ম্যাপ", "google maps"),
            webFallbackUrl = "https://maps.google.com/"
        ),
        AppItem(
            id = "chrome",
            name = "Chrome",
            packageName = "com.android.chrome",
            category = AppCategory.AI_AND_GOOGLE,
            aliases = listOf("chrome", "ক্রোম", "browser"),
            webFallbackUrl = "https://www.google.com/"
        ),
        AppItem(
            id = "translate",
            name = "Google Translate",
            packageName = "com.google.android.apps.translate",
            category = AppCategory.AI_AND_GOOGLE,
            aliases = listOf("translate", "অনুবাদ", "translator"),
            webFallbackUrl = "https://translate.google.com/"
        ),
        AppItem(
            id = "drive",
            name = "Google Drive",
            packageName = "com.google.android.apps.docs",
            category = AppCategory.AI_AND_GOOGLE,
            aliases = listOf("drive", "ড্রাইভ"),
            webFallbackUrl = "https://drive.google.com/"
        ),
        AppItem(
            id = "photos",
            name = "Google Photos",
            packageName = "com.google.android.apps.photos",
            category = AppCategory.AI_AND_GOOGLE,
            aliases = listOf("photos", "ফটো", "gallery"),
            webFallbackUrl = "https://photos.google.com/"
        ),
        AppItem(
            id = "calendar",
            name = "Calendar",
            packageName = "com.google.android.calendar",
            category = AppCategory.AI_AND_GOOGLE,
            aliases = listOf("calendar", "ক্যালেন্ডার"),
            webFallbackUrl = "https://calendar.google.com/"
        ),

        // Tools & Payment
        AppItem(
            id = "playstore",
            name = "Play Store",
            packageName = "com.android.vending",
            category = AppCategory.TOOLS_PAYMENT,
            aliases = listOf("play store", "প্লে স্টোর", "install"),
            webFallbackUrl = "https://play.google.com/store"
        ),
        AppItem(
            id = "calculator",
            name = "Calculator",
            packageName = "com.google.android.calculator",
            category = AppCategory.TOOLS_PAYMENT,
            aliases = listOf("calculator", "ক্যালকুলেটর")
        ),
        AppItem(
            id = "clock",
            name = "Clock & Alarms",
            packageName = "com.google.android.deskclock",
            category = AppCategory.TOOLS_PAYMENT,
            aliases = listOf("clock", "ঘড়ি", "alarm", "অ্যালার্ম")
        ),
        AppItem(
            id = "camera",
            name = "Camera",
            packageName = "android.media.action.STILL_IMAGE_CAMERA",
            category = AppCategory.TOOLS_PAYMENT,
            aliases = listOf("camera", "ক্যামেরা")
        ),
        AppItem(
            id = "settings",
            name = "Settings",
            packageName = "android.settings.SETTINGS",
            category = AppCategory.TOOLS_PAYMENT,
            aliases = listOf("settings", "সেটিংস")
        ),
        AppItem(
            id = "files",
            name = "Files / Downloads",
            packageName = "com.google.android.documentsui",
            category = AppCategory.TOOLS_PAYMENT,
            aliases = listOf("files", "ফাইল", "downloads")
        ),
        AppItem(
            id = "bkash",
            name = "bKash",
            packageName = "com.bKash.customerapp",
            category = AppCategory.TOOLS_PAYMENT,
            aliases = listOf("bkash", "বিকাশ"),
            webFallbackUrl = "https://www.bkash.com/"
        ),
        AppItem(
            id = "nagad",
            name = "Nagad",
            packageName = "com.konasl.nagad",
            category = AppCategory.TOOLS_PAYMENT,
            aliases = listOf("nagad", "নগদ"),
            webFallbackUrl = "https://nagad.com.bd/"
        ),
        AppItem(
            id = "daraz",
            name = "Daraz",
            packageName = "com.daraz.android",
            category = AppCategory.TOOLS_PAYMENT,
            aliases = listOf("daraz", "দারাজ"),
            webFallbackUrl = "https://www.daraz.com.bd/"
        )
    )

    fun findApp(query: String): AppItem? {
        val q = query.lowercase().trim()
        return allApps.firstOrNull { it.id == q || it.name.lowercase() == q }
            ?: allApps.firstOrNull { it.aliases.any { alias -> q.contains(alias) || alias.contains(q) } }
    }
}
