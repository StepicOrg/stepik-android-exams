package org.stepik.android.exams.util.json

import com.google.gson.*
import org.stepik.android.exams.data.model.Dataset
import org.stepik.android.exams.data.model.DatasetWrapper
import java.lang.reflect.Type

class DatasetWrapperDeserializer: JsonDeserializer<DatasetWrapper> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DatasetWrapper {
        return if (json !is JsonObject) {
            try {
                val o = context.deserialize<Any>(json, String::class.java)
                val dataset = Dataset(o as String)
                DatasetWrapper(dataset)
            } catch (e: Exception) {
                //if it is primitive, but not string.
                DatasetWrapper()
            }
        } else {
            DatasetWrapper(context.deserialize<Any>(json, Dataset::class.java) as Dataset)
        }
    }
}