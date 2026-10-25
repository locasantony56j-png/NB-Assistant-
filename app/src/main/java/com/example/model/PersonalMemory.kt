package com.example.model

data class UserPreference(
    val id: String,
    val key: String,
    val value: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class RoutineSchedule(
    val id: String,
    val title: String,
    val timeLabel: String,
    val days: String = "Everyday",
    val isActive: Boolean = true
)

data class ReminderItem(
    val id: String,
    val title: String,
    val dueTimestamp: Long,
    val recurrence: String = "Once",
    val isDone: Boolean = false
)

object MemoryStore {
    val defaultRoutines = listOf(
        RoutineSchedule("1", "Morning Class / Review", "8:00 AM", "Weekdays"),
        RoutineSchedule("2", "Office Standup Meeting", "10:30 AM", "Weekdays"),
        RoutineSchedule("3", "Evening Workout & Study", "6:00 PM", "Everyday"),
        RoutineSchedule("4", "Night Routine & Alarms", "11:00 PM", "Everyday")
    )

    val defaultPreferences = listOf(
        UserPreference("1", "Assistant Name", "NB"),
        UserPreference("2", "Primary Language", "Bangla / Banglish"),
        UserPreference("3", "Continuous Mode", "Active (Say NB once)"),
        UserPreference("4", "Audio Feedback", "Natural Bangla Speech")
    )
}
