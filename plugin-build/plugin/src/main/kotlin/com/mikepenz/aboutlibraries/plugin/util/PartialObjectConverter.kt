package com.mikepenz.aboutlibraries.plugin.util

import groovy.json.DefaultJsonGenerator
import groovy.json.JsonGenerator
import org.codehaus.groovy.runtime.DefaultGroovyMethods

/**
 * A converter for [JsonGenerator], which allows properties of a certain type to be excluded from the output.
 * The way it works is identical to the serialization of objects in [DefaultJsonGenerator].
 * @property targetClass The class that this converter should handle.
 * @param excludedFields The names of the object properties that should not be included in the output.
 */
class PartialObjectConverter(
    private val targetClass: Class<*>,
    excludedFields: Iterable<String>
) : JsonGenerator.Converter {

    private val excludedPropertyNames: Set<String> = mutableSetOf("class", "declaringClass", "metaClass").apply {
        addAll(excludedFields)
    }

    override fun handles(type: Class<*>?): Boolean {
        return targetClass == type
    }

    override fun convert(value: Any, key: String?): Any {
        return DefaultGroovyMethods.getProperties(value).filterKeys { propertyName ->
            propertyName !in excludedPropertyNames
        }
    }

}