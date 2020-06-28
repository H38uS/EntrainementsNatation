package com.mosioj.entrainements.utils.date;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime> {

    @Override
    public JsonElement serialize(LocalDateTime date, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(date.format(DateUtils.DATETIME_FORMATTER)); // "dd/mm/yyyy à hh:mm:ss"
    }

}
