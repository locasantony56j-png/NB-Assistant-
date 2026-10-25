package com.example.engine

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.provider.AlarmClock
import android.provider.MediaStore
import android.view.KeyEvent
import com.example.model.ActionStatus
import com.example.model.AppItem
import com.example.model.AppRegistry
import com.example.model.MemoryStore
import com.example.service.NbAccessibilityService
import com.example.service.NbNotificationListenerService

sealed class CommandResult {
    data class Handled(
        val replyBangla: String,
        val targetApp: String,
        val actionType: String,
        val status: ActionStatus = ActionStatus.SUCCESS,
        val shouldContinueListening: Boolean = true
    ) : CommandResult()

    data class MultiStep(
        val steps: List<Handled>,
        val summaryReply: String
    ) : CommandResult()

    data class NeedsAiOrConfirmation(
        val prompt: String,
        val suggestedReply: String,
        val targetApp: String = "NB"
    ) : CommandResult()

    data class StopRequested(val reply: String = "Listening stopped. Say NB to awaken me.") : CommandResult()
}

class FastCommandRouter(private val context: Context) {

    private val wakeWordRegex = Regex(
        """^(?:.*?\b)?(?:hey\s+|hi\s+|ok\s+|hello\s+|হেই\s+|ওকে\s+)?(?:n\.?\s?b\.?|en\s?bee|enbi|এনবি|এন\s?বি)\b[\s,.!?।:;"'-]*(.*)$""",
        RegexOption.IGNORE_CASE
    )

    fun extractCommandText(rawText: String, isContinuousModeActive: Boolean): String? {
        val trimmed = rawText.trim()
        val match = wakeWordRegex.find(trimmed)
        if (match != null) {
            val cmd = match.groupValues.getOrNull(1)?.trim() ?: ""
            return cmd // "" means user just called "NB"
        }
        if (isContinuousModeActive && trimmed.length >= 2) {
            return trimmed
        }
        return null
    }

    /**
     * Evaluates multi-step commands (separated by 'তারপর', 'then', or ',') or single commands.
     */
    fun routeCommand(command: String): CommandResult {
        val delimiters = Regex("""\b(?:তারপর|and then|then)\b|,""")
        val segments = command.split(delimiters).map { it.trim() }.filter { it.isNotBlank() }

        if (segments.size > 1) {
            val results = mutableListOf<CommandResult.Handled>()
            for (seg in segments) {
                val res = routeSingleCommand(seg)
                if (res is CommandResult.Handled) {
                    results.add(res)
                } else if (res is CommandResult.StopRequested) {
                    return res
                }
            }
            if (results.isNotEmpty()) {
                val combinedReply = results.joinToString(". ") { it.replyBangla }
                return CommandResult.MultiStep(results, combinedReply)
            }
        }

        return routeSingleCommand(command)
    }

    private fun routeSingleCommand(command: String): CommandResult {
        val t = command.lowercase().trim().replace(Regex("""[।.!?]+$"""), "").trim()

        // 1. Stop / Sleep commands
        if (t.matches(Regex("""^(stop|cancel|sleep|thamo|bondho|bondho hao|থামো|বন্ধ হও|অফ হও|থেমে যাও)$"""))) {
            return CommandResult.StopRequested("জি, লিসেনিং বন্ধ করলাম। আবার ডাকলে শুনব।")
        }

        // 2. Direct Math & Calculator Parser (offline, instant < 1ms)
        val mathResult = tryEvaluateMath(t)
        if (mathResult != null) {
            return CommandResult.Handled(mathResult, "Calculator", "Math Evaluation")
        }

        // 3. Routines, Schedules & Memory query
        if (t.contains("কাজ কি") || t.contains("আজকে কি") || t.contains("রুটিন") || t.contains("schedule") || t.contains("ক্যালেন্ডার")) {
            val routines = MemoryStore.defaultRoutines.joinToString(", ") { "${it.timeLabel}-এ ${it.title}" }
            return CommandResult.Handled("আজকের শিডিউল: $routines", "Memory & Schedule", "Read Routine")
        }

        // 4. Volume controls
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        if (t.contains("volume") || t.contains("sound") || t.contains("awaj") || t.contains("ভলিউম") || t.contains("আওয়াজ")) {
            if (t.contains("barao") || t.contains("baraw") || t.contains("bariye") || t.contains("up") || t.contains("বাড়াও") || t.contains("beshi") || t.contains("উচ্চ")) {
                audioManager?.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
                return CommandResult.Handled("ভলিউম বাড়িয়ে দিলাম।", "System Audio", "Volume Up")
            }
            if (t.contains("komao") || t.contains("kamao") || t.contains("down") || t.contains("কমাও") || t.contains("কমিয়ে") || t.contains("kom")) {
                audioManager?.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
                return CommandResult.Handled("ভলিউম কমিয়ে দিলাম।", "System Audio", "Volume Down")
            }
        }
        if (t.matches(Regex("""\b(mute|silent)\b|শব্দ বন্ধ|মিউট করো"""))) {
            audioManager?.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI)
            return CommandResult.Handled("মিউট করলাম।", "System Audio", "Mute")
        }

        // 5. Media Controls (play/pause/next/previous)
        if (t.matches(Regex("""^(pause|thamao|থামাও|stop (the )?(song|video|gan)|ভিডিও থামাও|গান থামাও)$"""))) {
            sendMediaKeyEvent(audioManager, KeyEvent.KEYCODE_MEDIA_PAUSE)
            return CommandResult.Handled("মিডিয়া পজ করলাম।", "Media Player", "Pause")
        }
        if (t.matches(Regex("""^(play|resume|chalao|চালাও|abar chalao|গান চালাও)$"""))) {
            sendMediaKeyEvent(audioManager, KeyEvent.KEYCODE_MEDIA_PLAY)
            return CommandResult.Handled("প্লে করছি।", "Media Player", "Play")
        }
        if (t.contains("next") || t.contains("porer") || t.contains("পরের")) {
            sendMediaKeyEvent(audioManager, KeyEvent.KEYCODE_MEDIA_NEXT)
            return CommandResult.Handled("পরের গান চালালাম।", "Media Player", "Next Track")
        }
        if (t.contains("previous") || t.contains("ager") || t.contains("আগের")) {
            sendMediaKeyEvent(audioManager, KeyEvent.KEYCODE_MEDIA_PREVIOUS)
            return CommandResult.Handled("আগের গান চালু করলাম।", "Media Player", "Previous Track")
        }

        // 6. Deep Scrolling (Accessibility)
        val accessibility = NbAccessibilityService.instance
        if (t.contains("scroll") || t.contains("স্ক্রল") || t.contains("নিচে") || t.contains("উপরে") || t.contains("নামো")) {
            if (t.contains("ekdom niche") || t.contains("একদম নিচে") || t.contains("bottom")) {
                val scrolled = accessibility?.performScroll("down", 15) ?: false
                val msg = if (scrolled) "একদম নিচে স্ক্রল করেছি।" else "অ্যাক্সেসিবিলিটি সার্ভিস অন থাকলে স্ক্রল করব।"
                return CommandResult.Handled(msg, "Active Screen", "Scroll to Bottom", if (scrolled) ActionStatus.SUCCESS else ActionStatus.NEEDS_USER)
            }
            if (t.contains("ekdom upore") || t.contains("একদম উপরে") || t.contains("top")) {
                val scrolled = accessibility?.performScroll("up", 15) ?: false
                val msg = if (scrolled) "একদম উপরে স্ক্রল করেছি।" else "অ্যাক্সেসিবিলিটি সার্ভিস অন থাকলে স্ক্রল করব।"
                return CommandResult.Handled(msg, "Active Screen", "Scroll to Top", if (scrolled) ActionStatus.SUCCESS else ActionStatus.NEEDS_USER)
            }
            if (t.contains("upore") || t.contains("উপরে") || t.contains("up") || t.contains("back")) {
                val scrolled = accessibility?.performScroll("up", 2) ?: false
                val msg = if (scrolled) "উপরে স্ক্রল করলাম।" else "অ্যাক্সেসিবিলিটি সার্ভিস চালু করুন।"
                return CommandResult.Handled(msg, "Active Screen", "Scroll Up", if (scrolled) ActionStatus.SUCCESS else ActionStatus.NEEDS_USER)
            }
            if (t.contains("niche") || t.contains("নিচে") || t.contains("down") || t.contains("aro post") || t.contains("আরও")) {
                val scrolled = accessibility?.performScroll("down", 2) ?: false
                val msg = if (scrolled) "নিচে স্ক্রল করলাম।" else "অ্যাক্সেসিবিলিটি সার্ভিস চালু করুন।"
                return CommandResult.Handled(msg, "Active Screen", "Scroll Down", if (scrolled) ActionStatus.SUCCESS else ActionStatus.NEEDS_USER)
            }
        }

        // 7. Back Navigation
        if (t.matches(Regex("""^(back|go back|pichone jao|ফিরে যাও|পিছনে যাও|back jao)$"""))) {
            val backed = accessibility?.performBack() ?: false
            return CommandResult.Handled(if (backed) "পেছনে গেলাম।" else "ব্যাক করতে পারলাম না।", "System", "Back Button")
        }

        // 8. Read Screen text aloud
        if (t.contains("screen e ki") || t.contains("স্ক্রিনে কি") || t.contains("read screen") || t.contains("পোস্ট পড়ে শোনাও") || t.contains("pore shonao")) {
            val text = accessibility?.collectVisibleScreenText(1200) ?: ""
            if (text.isNotBlank()) {
                val shortSummary = text.lines().filter { it.isNotBlank() }.take(5).joinToString(". ")
                return CommandResult.Handled("স্ক্রিনে দেখা যাচ্ছে: $shortSummary", "Active Screen", "Read Screen")
            } else {
                return CommandResult.Handled("স্ক্রিনে পড়ার মতো টেক্সট পাওয়া যায়নি। অ্যাক্সেসিবিলিটি পারমিশন অন আছে কি না দেখে নিন।", "Active Screen", "Read Screen", ActionStatus.NEEDS_USER)
            }
        }

        // 9. Notification Reading (WhatsApp, Messenger, SMS)
        if (t.contains("notification") || t.contains("নোটিফিকেশন") || t.contains("notun message") || t.contains("নতুন মেসেজ") || t.contains("ke message dise")) {
            val notifs = NbNotificationListenerService.recentNotifications.value
            if (notifs.isNotEmpty()) {
                val top = notifs.take(3).joinToString("; ") { "${it.appName}-এ ${it.title}: ${it.text}" }
                return CommandResult.Handled("সর্বশেষ নোটিফিকেশন: $top", "Notifications", "Read Notifications")
            } else {
                return CommandResult.Handled("নতুন কোনো নোটিফিকেশন নেই।", "Notifications", "Read Notifications")
            }
        }

        // 10. YouTube deep search or play
        val ytMatch = Regex("""^(?:youtube|ইউটিউব)[\s-]*(?:e|te|এ|তে)?\s+(.+?)\s*(?:dao|daw|chalao|search koro|khojo|play koro|lagao|দাও|চালাও|খোঁজো|সার্চ করো)?$""").find(t)
            ?: Regex("""^(?:search|play)\s+(.+?)\s+on\s+youtube$""").find(t)
        if (ytMatch != null) {
            val query = ytMatch.groupValues[1].trim()
            if (query.isNotEmpty() && !query.matches(Regex("""^(kholo|khulo|open|খোলো)$"""))) {
                val launched = launchYouTubeSearch(query)
                return CommandResult.Handled("ইউটিউবে '$query' খুঁজছি।", "YouTube", "Search & Play", if (launched) ActionStatus.SUCCESS else ActionStatus.NEEDS_USER)
            }
        }

        // 11. Camera action
        if (t.contains("camera") || t.contains("ক্যামেরা") || t.contains("ছবি তোলো") || t.contains("selfie")) {
            val launched = launchCamera(isFront = t.contains("selfie") || t.contains("সামনের"))
            return CommandResult.Handled("ক্যামেরা চালু করলাম।", "Camera", "Open Camera", if (launched) ActionStatus.SUCCESS else ActionStatus.NEEDS_USER)
        }

        // 12. Alarms & Timers
        if (t.contains("alarm") || t.contains("অ্যালার্ম")) {
            val hour = if (t.contains("shokal") || t.contains("সকাল")) 8 else 7
            launchAlarm(hour, 0, "NB Alarm")
            return CommandResult.Handled("অ্যালার্ম ঘড়িতে সেট করেছি।", "Clock", "Set Alarm")
        }

        // 13. Direct App Launch (checks all 36 apps)
        for (app in AppRegistry.allApps) {
            val names = listOf(app.id, app.name.lowercase()) + app.aliases
            if (names.any { t.contains(it) }) {
                if (t.contains("kholo") || t.contains("open") || t.contains("চালু") || t.contains("যাও")) {
                    val launched = launchApp(app)
                    val status = if (launched) ActionStatus.SUCCESS else ActionStatus.NEEDS_USER
                    val reply = if (launched) "${app.name} খুলেছি।" else "${app.name} ফোনে ইনস্টল নেই, প্লে-স্টোর পেজে নিয়ে যাচ্ছি।"
                    return CommandResult.Handled(reply, app.name, "Launch App", status)
                }
            }
        }

        // 14. Fallback to conversational/structured AI assistance
        return CommandResult.NeedsAiOrConfirmation(
            prompt = command,
            suggestedReply = "আপনার নির্দেশ বুঝতে পেরেছি: $command"
        )
    }

    private fun tryEvaluateMath(text: String): String? {
        val normalized = text
            .replace("০", "0").replace("১", "1").replace("২", "2").replace("৩", "3").replace("৪", "4")
            .replace("৫", "5").replace("৬", "6").replace("৭", "7").replace("৮", "8").replace("৯", "9")
            .replace("গুণ", "*").replace("গুন", "*").replace("times", "*")
            .replace("যোগ", "+").replace("plus", "+")
            .replace("বিয়োগ", "-").replace("বাদ", "-").replace("minus", "-")
            .replace("ভাগ", "/").replace("divided by", "/")

        // Match patterns like "250 * 4" or "1000 - 350"
        val regex = Regex("""(\d+(?:\.\d+)?)\s*([\+\-\*\/])\s*(\d+(?:\.\d+)?)""")
        val match = regex.find(normalized) ?: return null

        val num1 = match.groupValues[1].toDoubleOrNull() ?: return null
        val op = match.groupValues[2]
        val num2 = match.groupValues[3].toDoubleOrNull() ?: return null

        val result = when (op) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "*" -> num1 * num2
            "/" -> if (num2 != 0.0) num1 / num2 else Double.NaN
            else -> return null
        }

        if (result.isNaN()) return "শূন্য দিয়ে ভাগ করা সম্ভব নয়।"

        val formatted = if (result % 1.0 == 0.0) result.toLong().toString() else "%.2f".format(result)
        return "গণনা ফলাফল: $formatted"
    }

    private fun sendMediaKeyEvent(audioManager: AudioManager?, keyCode: Int) {
        val down = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
        val up = KeyEvent(KeyEvent.ACTION_UP, keyCode)
        audioManager?.dispatchMediaKeyEvent(down)
        audioManager?.dispatchMediaKeyEvent(up)
    }

    fun launchApp(app: AppItem): Boolean {
        try {
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage(app.packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return true
            }
            // If native package not installed, open Play Store directly
            val playStoreIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${app.packageName}")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (playStoreIntent.resolveActivity(pm) != null) {
                context.startActivity(playStoreIntent)
                return true
            }
            if (app.webFallbackUrl.isNotEmpty()) {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(app.webFallbackUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
                return true
            }
        } catch (_: Exception) {}
        return false
    }

    fun launchYouTubeSearch(query: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_SEARCH).apply {
                setPackage("com.google.android.youtube")
                putExtra("query", query)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            try {
                val web = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=" + Uri.encode(query))).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(web)
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    fun launchCamera(isFront: Boolean = false): Boolean {
        return try {
            val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                if (isFront) {
                    putExtra("android.intent.extras.CAMERA_FACING", 1)
                    putExtra("android.intent.extra.USE_FRONT_CAMERA", true)
                }
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    fun launchAlarm(hour: Int, minute: Int, message: String = "NB Alarm"): Boolean {
        return try {
            val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                putExtra(AlarmClock.EXTRA_HOUR, hour)
                putExtra(AlarmClock.EXTRA_MINUTES, minute)
                putExtra(AlarmClock.EXTRA_MESSAGE, message)
                putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            false
        }
    }
}
