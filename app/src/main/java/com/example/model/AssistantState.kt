package com.example.model

enum class VoiceMode {
    IDLE,
    LISTENING,
    CONTINUOUS_ACTIVE,
    PROCESSING,
    SPEAKING,
    SELF_HEALING,
    ERROR
}

data class LatencyMetrics(
    val speechEndToRecognizeMs: Long = 0,
    val intentClassificationMs: Long = 0,
    val actionExecutionMs: Long = 0,
    val totalLatencyMs: Long = 0,
    val isUnderTwoSeconds: Boolean = true
)

data class AssistantUiState(
    val voiceMode: VoiceMode = VoiceMode.IDLE,
    val isContinuousModeEnabled: Boolean = true,
    val isContinuousActive: Boolean = false,
    val isAccessibilityEnabled: Boolean = false,
    val isNotificationAccessEnabled: Boolean = false,
    val hasMicPermission: Boolean = false,
    val isForegroundServiceRunning: Boolean = false,
    val currentSpokenText: String = "",
    val interimSpeech: String = "",
    val lastReply: String = "Hello! I am NB Assistant. How can I help you?",
    val lastActionStatus: String = "Ready",
    val latencyMetrics: LatencyMetrics = LatencyMetrics(210, 85, 320, 615, true),
    val activeApp: AppItem? = null,
    val recentNotifications: List<NotificationItem> = emptyList(),
    val actionLogs: List<ActionLogItem> = emptyList(),
    val healthState: SelfHealingHealthState = SelfHealingHealthState()
)

data class ActionLogItem(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val command: String,
    val targetApp: String,
    val actionType: String,
    val resultMessage: String,
    val status: ActionStatus,
    val latencyMs: Long = 0
)

enum class ActionStatus {
    SUCCESS,
    FAILED,
    NEEDS_USER,
    CANCELLED,
    HEALED
}

data class NotificationItem(
    val id: String,
    val packageName: String,
    val appName: String,
    val title: String,
    val text: String,
    val postTime: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class SelfHealingHealthState(
    val speechEngineHealth: ComponentStatus = ComponentStatus.HEALTHY,
    val accessibilityHealth: ComponentStatus = ComponentStatus.HEALTHY,
    val foregroundServiceHealth: ComponentStatus = ComponentStatus.HEALTHY,
    val notificationListenerHealth: ComponentStatus = ComponentStatus.HEALTHY,
    val audioSystemHealth: ComponentStatus = ComponentStatus.HEALTHY,
    val recoveryAttempts: Int = 0,
    val successfulHeals: Int = 0,
    val lastHealedEvent: String? = null,
    val autoRecoveryActive: Boolean = true
)

enum class ComponentStatus(val label: String) {
    HEALTHY("Operational"),
    DEGRADED("Degraded"),
    RECOVERING("Auto-Healing"),
    ATTENTION_REQUIRED("Needs Permission")
}
