package com.nb.assistant

import android.os.Bundle
import com.getcapacitor.BridgeActivity

class MainActivity : BridgeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        registerPlugin(NativeActionsPlugin::class.java)
        super.onCreate(savedInstanceState)
    }
}
