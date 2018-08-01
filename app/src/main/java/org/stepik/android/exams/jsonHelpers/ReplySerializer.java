package org.stepik.android.exams.jsonHelpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.stepik.android.exams.data.model.Reply;
import org.stepik.android.exams.data.model.ReplyWrapper;

import java.lang.reflect.Type;

public class ReplySerializer implements JsonSerializer<ReplyWrapper> {
    @Override
    public JsonElement serialize(ReplyWrapper src, Type typeOfSrc, JsonSerializationContext context) {
        Reply reply = src.getReply();
        if (reply != null) {
            if (reply.getTableChoices() == null) {
                return context.serialize(reply);
            } else {
                JsonElement tableChoicesJsonElement = context.serialize(reply.getTableChoices(), reply.getTableChoices().getClass());
                reply.setTableChoices(null);
                JsonElement replyJsonElement = context.serialize(reply);
                JsonObject replyJsonObject = replyJsonElement.getAsJsonObject();
                replyJsonObject.add("choices", tableChoicesJsonElement);
                return replyJsonObject;
            }
        }
        return null;
    }
}
