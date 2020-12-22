package com.example.demo.util;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Clob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.boot.json.JsonParseException;
import org.springframework.core.ResolvableType;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import springfox.documentation.spring.web.json.Json;

public abstract class GsonUtils {
    public static Gson DEFAULT_GSON = new GsonBuilder()
            .registerTypeAdapterFactory(new StringTypeAdapterFactory())
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(DateTimeUtils.formatLocalDateTime(src));
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return DateTimeUtils.parseLocalDateTime(json.getAsJsonPrimitive().getAsString());
                }
            })
            .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                @Override
                public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(DateTimeUtils.formatLocalDate(src));
                }
            })
            .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                @Override
                public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return DateTimeUtils.parseLocalDate(json.getAsJsonPrimitive().getAsString());
                }
            })
            .registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
                @Override
                public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                    if (src == null) {
                        return JsonNull.INSTANCE;
                    }

                    return new JsonPrimitive(NumberUtils.toDouble(src));
                }
            })
            .registerTypeAdapter(Json.class, new SpringfoxJsonToGsonAdapter())
            .create();

    public static Gson REDUCED_GSON = new GsonBuilder()
            .registerTypeAdapterFactory(new StringTypeAdapterFactory())
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(DateTimeUtils.formatLocalDateTime(src));
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return DateTimeUtils.parseLocalDateTime(json.getAsJsonPrimitive().getAsString());
                }
            })
            .registerTypeAdapter(LocalDate.class, new JsonSerializer<LocalDate>() {
                @Override
                public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(DateTimeUtils.formatLocalDate(src));
                }
            })
            .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                @Override
                public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return DateTimeUtils.parseLocalDate(json.getAsJsonPrimitive().getAsString());
                }
            })
            .registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
                @Override
                public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                    if (src == null) {
                        return JsonNull.INSTANCE;
                    }

                    return new JsonPrimitive(NumberUtils.toDouble(src));
                }
            }).registerTypeAdapter(String.class, new JsonSerializer<String>() {
                @Override
                public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {
                    if (src == null) {
                        return JsonNull.INSTANCE;
                    }

                    if (src.length() > 128) {
                        return new JsonPrimitive(src.subSequence(0, 125) + "...(more)");
                    } else {
                        return new JsonPrimitive(src);
                    }
                }
            }).registerTypeHierarchyAdapter(ServletRequest.class, new IgnoreJsonSerializer<ServletRequest>())
            .registerTypeHierarchyAdapter(ServletResponse.class, new IgnoreJsonSerializer<ServletResponse>())
            .registerTypeHierarchyAdapter(HttpSession.class, new IgnoreJsonSerializer<HttpSession>())
            .registerTypeHierarchyAdapter(AutoCloseable.class, new IgnoreJsonSerializer<AutoCloseable>())
            .registerTypeHierarchyAdapter(Blob.class, new IgnoreJsonSerializer<Blob>())
            .registerTypeHierarchyAdapter(Clob.class, new IgnoreJsonSerializer<Clob>())
            .registerTypeHierarchyAdapter(byte[].class, new IgnoreJsonSerializer<byte[]>())
            .registerTypeHierarchyAdapter(Byte[].class, new IgnoreJsonSerializer<Byte[]>())
            .registerTypeHierarchyAdapter(Charset.class, new IgnoreJsonSerializer<Charset>())
            .registerTypeHierarchyAdapter(MultipartFile.class, new IgnoreJsonSerializer<MultipartFile>())
            .registerTypeAdapter(Json.class, new SpringfoxJsonToGsonAdapter()).create();

    public static <T> String toReducedJson(T item) {
        return REDUCED_GSON.toJson(item);
    }

    public static <T> String toJson(T item) {
        return DEFAULT_GSON.toJson(item);
    }

    public static <T> T fromJson(String json, Class<T> type) {
        return DEFAULT_GSON.fromJson(json, type);
    }

    public static <T> T fromJson(JsonElement element, Class<T> type) {
        return DEFAULT_GSON.fromJson(element, type);
    }

    public static <T> List<T> listFromJson(String json, Class<T> type) {
        return DEFAULT_GSON.fromJson(json, getListType(type));
    }

    public static <T> List<T> listFromJson(JsonElement element, Class<T> type) {
        return DEFAULT_GSON.fromJson(element, getListType(type));
    }

    public static <T> T fromJson(String json, String memberName, Class<T> type) {
        JsonElement jsonElement = JsonParser.parseString(json);

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        JsonElement memberJsonElement = jsonObject.get(memberName);

        return DEFAULT_GSON.fromJson(memberJsonElement, type);
    }

    public static <T> List<T> listFromJson(String json, String memberName, Class<T> type) {
        JsonElement jsonElement = JsonParser.parseString(json);

        JsonObject jsonObject = jsonElement.getAsJsonObject();

        JsonElement memberJsonElement = jsonObject.get(memberName);

        return DEFAULT_GSON.fromJson(memberJsonElement, getListType(type));
    }

    // ----------------------------------------------------------------------------
    // 私有类和方法
    // ----------------------------------------------------------------------------
    private static class IgnoreJsonSerializer<T> implements JsonSerializer<T> {
        @Override
        public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return JsonNull.INSTANCE;
            }

            return new JsonPrimitive("[...(" + src.getClass() + ")]");
        }
    }
    
    /**
     * Json格式的字符串转成字符串，默认Gson报错
     */
    private static class StringTypeAdapterFactory implements TypeAdapterFactory {
        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (String.class.equals(type.getRawType())) {
                return (TypeAdapter<T>) new TypeAdapter<String>() {
                    @Override
                    public void write(JsonWriter out, String value) throws IOException {
                        if (value == null) {
                            out.nullValue();
                        } else {
                            out.value(value);
                        }
                    }

                    @Override
                    public String read(JsonReader in) throws IOException {
                        JsonElement je = JsonParser.parseReader(in);
                        
                        if (je.isJsonNull()) {
                            return "";
                        } else if (je.isJsonPrimitive()) {
                            return ((JsonPrimitive) je).getAsString();
                        }
                        
                        return je.toString();
                    }
                };
            } else {
                return null;
            }
        }
    }
    
    /**
     * for swagger ui
     */
    private static class SpringfoxJsonToGsonAdapter implements JsonSerializer<Json> {
        @Override
        public JsonElement serialize(Json json, Type type, JsonSerializationContext context) {
            return JsonParser.parseString(json.value());
        }
    }

    private static <T> Type getListType(Class<T> type) {
        return ResolvableType.forClassWithGenerics(List.class, type).getType();
    }
}
