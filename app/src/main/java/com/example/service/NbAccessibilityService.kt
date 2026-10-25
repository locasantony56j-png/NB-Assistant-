package com.example.service

import android.accessibilityservice.AccessibilityService
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NbAccessibilityService : AccessibilityService() {

    companion object {
        var instance: NbAccessibilityService? = null
            private set

        private val _isServiceConnected = MutableStateFlow(false)
        val isServiceConnected = _isServiceConnected.asStateFlow()

        private val _activePackage = MutableStateFlow("")
        val activePackage = _activePackage.asStateFlow()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        _isServiceConnected.value = true
    }

    override fun onDestroy() {
        instance = null
        _isServiceConnected.value = false
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val pkg = event.packageName?.toString() ?: ""
        if (pkg.isNotEmpty() && pkg != packageName) {
            _activePackage.value = pkg
        }
    }

    override fun onInterrupt() {}

    fun currentPackageName(): String {
        return rootInActiveWindow?.packageName?.toString() ?: _activePackage.value
    }

    /**
     * Deep vertical scroll with verification:
     * direction: "down" (forward) or "up" (backward)
     * times: 1 to 20
     */
    fun performScroll(direction: String = "down", times: Int = 1): Boolean {
        val root = rootInActiveWindow ?: return false
        val action = if (direction.equals("up", ignoreCase = true) || direction.equals("back", ignoreCase = true)) {
            AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
        } else {
            AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
        }

        var anyMoved = false
        val boundedTimes = times.coerceIn(1, 25)
        for (i in 0 until boundedTimes) {
            val scrollableNode = findFirstScrollableNode(root) ?: break
            val result = scrollableNode.performAction(action)
            if (result) {
                anyMoved = true
            } else {
                break
            }
            if (boundedTimes > 1) {
                try {
                    Thread.sleep(120)
                } catch (_: InterruptedException) {}
            }
        }
        return anyMoved
    }

    private fun findFirstScrollableNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.isScrollable) return node
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            val found = findFirstScrollableNode(child)
            if (found != null) return found
        }
        return null
    }

    /**
     * Click a node matching the specified label or text.
     */
    fun clickByText(label: String): Boolean {
        val root = rootInActiveWindow ?: return false
        val cleanLabel = label.trim()
        if (cleanLabel.isEmpty()) return false

        val matchingNodes = root.findAccessibilityNodeInfosByText(cleanLabel)
        val visibleNode = matchingNodes.firstOrNull { it.isVisibleToUser } ?: return false

        var targetNode: AccessibilityNodeInfo? = visibleNode
        while (targetNode != null && !targetNode.isClickable) {
            targetNode = targetNode.parent
        }

        return targetNode?.performAction(AccessibilityNodeInfo.ACTION_CLICK) ?: false
    }

    /**
     * Type text into the currently focused editable input field.
     */
    fun typeTextIntoFocused(text: String): Boolean {
        val root = rootInActiveWindow ?: return false
        val field = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT) ?: return false
        val args = Bundle().apply {
            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text)
        }
        val performed = field.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
        field.refresh()
        return performed
    }

    /**
     * Global Back button press.
     */
    fun performBack(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_BACK)
    }

    /**
     * Global Home button press.
     */
    fun performHome(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_HOME)
    }

    /**
     * Collects visible text on screen (for read-aloud functionality).
     */
    fun collectVisibleScreenText(maxChars: Int = 3000): String {
        val root = rootInActiveWindow ?: return ""
        val sb = StringBuilder()

        fun traverse(node: AccessibilityNodeInfo?) {
            if (node == null || sb.length >= maxChars) return
            if (node.isVisibleToUser) {
                val text = node.text ?: node.contentDescription
                if (!text.isNullOrBlank()) {
                    sb.append(text).append("\n")
                }
            }
            for (i in 0 until node.childCount) {
                traverse(node.getChild(i))
            }
        }

        traverse(root)
        return sb.toString().take(maxChars).trim()
    }
}
