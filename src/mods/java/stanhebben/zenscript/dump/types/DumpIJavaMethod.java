package stanhebben.zenscript.dump.types;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import stanhebben.zenscript.dump.IDumpable;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.JavaMethodGenerated;

public class DumpIJavaMethod implements IDumpable {
   private transient IJavaMethod method;
   private boolean staticOverride = false;

   public DumpIJavaMethod(IJavaMethod method) {
      this.method = method;
   }

   public JsonObject serialize(JsonSerializationContext context) {
      JsonObject obj = new JsonObject();
      obj.addProperty("static", Boolean.valueOf(this.method.isStatic() || this.staticOverride));
      obj.addProperty("returnClass", this.method.getReturnType().toJavaClass().getCanonicalName());
      obj.addProperty("name", this.method.toString());
      if (this.method instanceof JavaMethodGenerated) {
         obj.addProperty("isSynthetic", Boolean.valueOf(true));
      }

      return obj;
   }

   public DumpIJavaMethod withStaticOverride(boolean override) {
      this.staticOverride = override;
      return this;
   }
}
