package org.stepik.android.exams.jsonHelpers


import com.google.gson.*
import org.stepik.android.model.attempts.Dataset
import org.stepik.android.model.attempts.DatasetWrapper
import java.lang.reflect.Type

class DatasetDeserializer : JsonDeserializer<DatasetWrapper> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): DatasetWrapper {
        return if (json !is JsonObject) {
            try {
                val o = context.deserialize<String>(json, String::class.java)
                val dataset = Dataset(someStringValueFromServer = o)
                DatasetWrapper(dataset)
            } catch (e: Exception) {
                //if it is primitive, but not string.
                DatasetWrapper()
            }

        } else {
            DatasetWrapper(context.deserialize<Dataset>(json, Dataset::class.java))
        }
    }
}
