package stanhebben.zenscript.dump;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonSerializationContext;

public interface IDumpable {
   default JsonElement serialize(JsonSerializationContext context) {
      return JsonNull.INSTANCE;
   }
}
