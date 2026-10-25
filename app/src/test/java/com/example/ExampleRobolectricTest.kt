package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.CommandResult
import com.example.engine.FastCommandRouter
import com.example.model.AppRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("NB Assistant", appName)
    }

    @Test
    fun `app registry contains essential apps`() {
        val wa = AppRegistry.findApp("whatsapp")
        assertNotNull(wa)
        assertEquals("com.whatsapp", wa?.packageName)

        val yt = AppRegistry.findApp("youtube")
        assertNotNull(yt)
        assertEquals("com.google.android.youtube", yt?.packageName)
    }

    @Test
    fun `fast command router extracts command and routes volume`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val router = FastCommandRouter(context)

        val extracted = router.extractCommandText("NB, volume barao", isContinuousModeActive = false)
        assertEquals("volume barao", extracted)

        val result = router.routeCommandLocally("volume barao")
        assertTrue(result is CommandResult.Handled)
        assertEquals("System Audio", (result as CommandResult.Handled).targetApp)
    }
}
