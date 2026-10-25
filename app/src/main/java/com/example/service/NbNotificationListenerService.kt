package com.example.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.model.NotificationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class NbNotificationListenerService : NotificationListenerService() {

    companion object {
        private val _isConnected = MutableStateFlow(false)
        val isConnected = _isConnected.asStateFlow()

        private val _recentNotifications = MutableStateFlow<List<NotificationItem>>(emptyList())
        val recentNotifications = _recentNotifications.asStateFlow()

        fun clearNotifications() {
            _recentNotifications.value = emptyList()
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        _isConnected.value = true
    }

    override fun onListenerDisconnected() {
        _isConnected.value = false
        super.onListenerDisconnected()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return
        val pkg = sbn.packageName ?: return
        if (pkg == packageName || sbn.isOngoing) return

        val extras = sbn.notification?.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString() ?: ""
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""

        if (title.isBlank() && text.isBlank()) return

        val appName = when {
            pkg.contains("whatsapp") -> "WhatsApp"
            pkg.contains("orca") -> "Messenger"
            pkg.contains("katana") -> "Facebook"
            pkg.contains("instagram") -> "Instagram"
            pkg.contains("telegram") -> "Telegram"
            pkg.contains("imo") -> "IMO"
            pkg.contains("gm") -> "Gmail"
            pkg.contains("youtube") -> "YouTube"
            else -> pkg.substringAfterLast('.')
        }

        val item = NotificationItem(
            id = UUID.randomUUID().toString(),
            packageName = pkg,
            appName = appName,
            title = title,
            text = text,
            postTime = sbn.postTime
        )

        val currentList = _recentNotifications.value.toMutableList()
        currentList.removeAll { it.packageName == pkg && it.title == title && it.text == text }
        currentList.add(0, item)
        if (currentList.size > 40) {
            _recentNotifications.value = currentList.take(40)
        } else {
            _recentNotifications.value = currentList
        }
    }
}
