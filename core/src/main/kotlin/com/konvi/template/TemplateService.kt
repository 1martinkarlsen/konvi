package com.konvi.template

import io.pebbletemplates.pebble.PebbleEngine
import me.tatarka.inject.annotations.Inject
import java.io.StringWriter

class TemplateService @Inject constructor(
    private val templateEngine: PebbleEngine
) {

    fun getTemplateAsString(
        templateName: String,
        data: Map<String, Any> = emptyMap()
    ): String {
        val writer = StringWriter()

        val template = templateEngine.getTemplate(templateName)
        template.evaluate(writer, data)
        return writer.toString()
    }
}
