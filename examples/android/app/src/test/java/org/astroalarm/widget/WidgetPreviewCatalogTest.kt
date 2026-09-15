package org.astroalarm.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class WidgetPreviewCatalogTest {
    @Test
    fun everyAppWidgetHasAUniquePreviewDrawable() {
        val infos = xmlDir().listFiles { _, name -> name.endsWith("widget_info.xml") }.orEmpty().sortedBy { it.name }
        assertEquals(7, infos.size)
        val previews = infos.map { info ->
            val text = info.readText()
            val match = PREVIEW.find(text)
            assertTrue("${info.name} missing previewImage", match != null)
            match!!.groupValues[1]
        }
        assertEquals(previews.size, previews.toSet().size)
        previews.forEach { name ->
            val xml = File(drawableDir(), "$name.xml")
            assertTrue("missing drawable $name.xml", xml.isFile)
        }
    }

    @Test
    fun everyManifestWidgetReceiverPointsAtAnInfoFile() {
        val manifest = file(
            "app/src/main/AndroidManifest.xml",
            "src/main/AndroidManifest.xml",
        ).readText()
        val receivers = RECEIVER.findAll(manifest).map { it.groupValues[1] }.toList()
        assertEquals(7, receivers.size)
        receivers.forEach { resource ->
            assertTrue("missing @xml/$resource", File(xmlDir(), "$resource.xml").isFile)
        }
    }

    private fun xmlDir(): File = file("app/src/main/res/xml", "src/main/res/xml")

    private fun drawableDir(): File = file("app/src/main/res/drawable", "src/main/res/drawable")

    private fun file(vararg rel: String): File = rel.map(::File).first { it.isFile || it.isDirectory }

    companion object {
        private val PREVIEW = Regex("""android:previewImage="@drawable/([^"]+)"""")
        private val RECEIVER = Regex(
            """android:name="android.appwidget.provider"\s+android:resource="@xml/([^"]+)"""",
        )
    }
}
