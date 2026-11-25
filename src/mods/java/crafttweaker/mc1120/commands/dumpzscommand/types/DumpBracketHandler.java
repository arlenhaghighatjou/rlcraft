package crafttweaker.mc1120.commands.dumpzscommand.types;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import crafttweaker.zenscript.IBracketHandler;
import stanhebben.zenscript.dump.IDumpable;

public class DumpBracketHandler implements IDumpable {
   private final int priority;
   private final String javaPath;
   private final String regex;
   private final String returnedClass;

   public DumpBracketHandler(IBracketHandler bracketHandler, int priority) {
      this.priority = priority;
      this.javaPath = bracketHandler.getClass().getName();
      this.regex = bracketHandler.getRegexMatchingString();
      this.returnedClass = bracketHandler.getReturnedClass() != null ? bracketHandler.getReturnedClass().getCanonicalName() : "NO CLASS DEFINED";
   }

   public int getPriority() {
      return this.priority;
   }

   public String getJavaPath() {
      return this.javaPath;
   }

   public String getRegex() {
      return this.regex;
   }

   public String getReturnedClass() {
      return this.returnedClass;
   }

   public JsonElement serialize(JsonSerializationContext context) {
      JsonObject object = new JsonObject();
      object.addProperty("javaPath", this.javaPath);
      object.addProperty("priority", Integer.valueOf(this.priority));
      object.addProperty("regex", this.regex);
      object.addProperty("returnedClass", this.returnedClass);
      return object;
   }
}
