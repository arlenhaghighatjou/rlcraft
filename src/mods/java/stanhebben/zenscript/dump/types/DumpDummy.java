package stanhebben.zenscript.dump.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import stanhebben.zenscript.dump.IDumpable;

public class DumpDummy implements IDumpable {
   private String className;
   private String toString;

   public DumpDummy(Object self) {
      this.className = self.getClass().getCanonicalName();
      this.toString = self.toString();
   }

   public JsonElement serialize(JsonSerializationContext context) {
      return new JsonPrimitive(this.className + " : {" + this.toString + "}");
   }
}
