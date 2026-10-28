package com.nb.assistant

import android.content.Intent
import android.net.Uri
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.CapacitorPlugin

@CapacitorPlugin(name = "NativeActions")
class NativeActionsPlugin : Plugin() {

    @PluginMethod
    fun openDialer(call: PluginCall) {
        val phone = call.getString("phone")?.trim().orEmpty()
        val intent = Intent(Intent.ACTION_DIAL)
        if (phone.isNotBlank()) intent.data = Uri.parse("tel:${Uri.encode(phone)}")
        try {
            activity.startActivity(intent)
            call.resolve()
        } catch (e: Exception) {
            call.reject("Could not open the phone dialer", e)
        }
    }

    @PluginMethod
    fun openApp(call: PluginCall) {
        val packageName = call.getString("packageName")?.trim().orEmpty()
        if (packageName.isBlank()) {
            call.reject("packageName is required")
            return
        }
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent == null) {
                call.reject("App is not installed or cannot be launched")
                return
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            call.resolve()
        } catch (e: Exception) {
            call.reject("Could not launch app", e)
        }
    }

    @PluginMethod
    fun openWhatsApp(call: PluginCall) {
        val packageName = "com.whatsapp"
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent == null) {
            call.reject("WhatsApp is not installed")
            return
        }
        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            call.resolve()
        } catch (e: Exception) {
            call.reject("Could not open WhatsApp", e)
        }
    }
}
