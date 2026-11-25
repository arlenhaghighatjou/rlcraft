package leviathan143.loottweaker.common.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import crafttweaker.api.data.DataBool;
import crafttweaker.api.data.DataByte;
import crafttweaker.api.data.DataByteArray;
import crafttweaker.api.data.DataDouble;
import crafttweaker.api.data.DataFloat;
import crafttweaker.api.data.DataInt;
import crafttweaker.api.data.DataIntArray;
import crafttweaker.api.data.DataList;
import crafttweaker.api.data.DataLong;
import crafttweaker.api.data.DataMap;
import crafttweaker.api.data.DataShort;
import crafttweaker.api.data.DataString;
import crafttweaker.api.data.IData;
import crafttweaker.api.data.IDataConverter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class JsonConverter implements IDataConverter<JsonElement> {
   private static final JsonConverter INSTANCE = new JsonConverter();
   private static final Gson SERIALISER = (new GsonBuilder()).registerTypeAdapter(DataBool.class, serialiser((src) -> {
      return new JsonPrimitive(src.asBool());
   })).registerTypeAdapter(DataByte.class, serialiser((src) -> {
      return new JsonPrimitive(src.asByte());
   })).registerTypeAdapter(DataShort.class, serialiser((src) -> {
      return new JsonPrimitive(src.asShort());
   })).registerTypeAdapter(DataInt.class, serialiser((src) -> {
      return new JsonPrimitive(src.asInt());
   })).registerTypeAdapter(DataLong.class, serialiser((src) -> {
      return new JsonPrimitive(src.asLong());
   })).registerTypeAdapter(DataFloat.class, serialiser((src) -> {
      return new JsonPrimitive(src.asFloat());
   })).registerTypeAdapter(DataDouble.class, serialiser((src) -> {
      return new JsonPrimitive(src.asDouble());
   })).registerTypeAdapter(DataString.class, serialiser((src) -> {
      return new JsonPrimitive(src.asString());
   })).registerTypeAdapter(DataByteArray.class, contextSerialiser(IData::asByteArray)).registerTypeAdapter(DataIntArray.class, contextSerialiser(IData::asIntArray)).registerTypeAdapter(DataList.class, contextSerialiser(IData::asList)).registerTypeAdapter(DataMap.class, contextSerialiser(IData::asMap)).create();

   public JsonConverter() {
   }

   private static JsonSerializer<IData> serialiser(Function<IData, JsonElement> serialiser) {
      return (src, srcType, context) -> {
         return (JsonElement)serialiser.apply(src);
      };
   }

   private static JsonSerializer<IData> contextSerialiser(Function<IData, Object> serialiser) {
      return (src, srcType, context) -> {
         return context.serialize(serialiser.apply(src));
      };
   }

   public static JsonElement from(IData data) {
      return (JsonElement)data.convert(INSTANCE);
   }

   public JsonElement fromBool(boolean bool) {
      return new JsonPrimitive(bool);
   }

   public JsonElement fromByte(byte b) {
      return new JsonPrimitive(b);
   }

   public JsonElement fromByteArray(byte[] b) {
      return SERIALISER.toJsonTree(b);
   }

   public JsonElement fromDouble(double d) {
      return new JsonPrimitive(d);
   }

   public JsonElement fromFloat(float f) {
      return new JsonPrimitive(f);
   }

   public JsonElement fromInt(int i) {
      return new JsonPrimitive(i);
   }

   public JsonElement fromIntArray(int[] i) {
      return SERIALISER.toJsonTree(i);
   }

   public JsonElement fromList(List<IData> list) {
      return SERIALISER.toJsonTree(list);
   }

   public JsonElement fromLong(long l) {
      return new JsonPrimitive(l);
   }

   public JsonElement fromMap(Map<String, IData> map) {
      return SERIALISER.toJsonTree(map);
   }

   public JsonElement fromShort(short s) {
      return new JsonPrimitive(s);
   }

   public JsonElement fromString(String str) {
      return new JsonPrimitive(str);
   }
}
