package stanhebben.zenscript.dump;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class GSONDumpableSerializer implements JsonSerializer<IDumpable> {
   public static final GSONDumpableSerializer INSTANCE = new GSONDumpableSerializer();

   public GSONDumpableSerializer() {
   }

   public JsonElement serialize(IDumpable src, Type typeOfSrc, JsonSerializationContext context) {
      return src.serialize(context);
   }
}
