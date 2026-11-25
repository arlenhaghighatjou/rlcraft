package stanhebben.zenscript.type.natives;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import stanhebben.zenscript.annotations.OperatorType;
import stanhebben.zenscript.dump.IDumpable;
import stanhebben.zenscript.dump.types.DumpIJavaMethod;

public class ZenNativeOperator implements IDumpable {
   private final OperatorType operator;
   private final IJavaMethod method;

   public ZenNativeOperator(OperatorType operator, IJavaMethod method) {
      this.operator = operator;
      this.method = method;
   }

   public OperatorType getOperator() {
      return this.operator;
   }

   public IJavaMethod getMethod() {
      return this.method;
   }

   public JsonElement serialize(JsonSerializationContext context) {
      JsonObject obj = (new DumpIJavaMethod(this.method)).serialize(context);
      obj.addProperty("operator", this.operator.toString());
      return obj;
   }
}
