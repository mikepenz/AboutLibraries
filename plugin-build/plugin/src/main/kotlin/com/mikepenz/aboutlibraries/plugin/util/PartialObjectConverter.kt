package com.mikepenz.aboutlibraries.plugin.util

import groovy.json.DefaultJsonGenerator
import groovy.json.JsonGenerator
import org.codehaus.groovy.runtime.DefaultGroovyMethods

/**
 * A converter for [JsonGenerator], which allows properties to be excluded from the output.
 * The way it works is identical to the serialization of objects in [DefaultJsonGenerator].
 * @property excludedQualifiedPropertyNames The qualified name (class name + property name)
 * of the properties that should be excluded from serialization.
 */
class PartialObjectConverter(
    private val excludedQualifiedPropertyNames: Set<String>,
) : JsonGenerator.Converter {

    private val targetClassNames: Set<String> = excludedQualifiedPropertyNames.mapTo(mutableSetOf()) { field ->
        field.substringBeforeLast('.')
    }

    private val excludedPropertyNames = setOf("class", "declaringClass", "metaClass")

    override fun handles(type: Class<*>?): Boolean {
        return type != null && targetClassNames.contains(type.simpleName)
    }

    override fun convert(value: Any, key: String?): Any {
        return DefaultGroovyMethods.getProperties(value).filterKeys { propertyName ->
            propertyName !in excludedPropertyNames && "${value::class.simpleName}.$propertyName" !in excludedQualifiedPropertyNames
        }
    }
}