package stanhebben.zenscript.dump.types;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import stanhebben.zenscript.dump.IDumpable;
import stanhebben.zenscript.type.natives.IJavaMethod;
import stanhebben.zenscript.type.natives.ZenNativeCaster;
import stanhebben.zenscript.type.natives.ZenNativeMember;
import stanhebben.zenscript.type.natives.ZenNativeOperator;

public class DumpZenTypeNative extends DumpZenType {
   private final Map<String, ZenNativeMember> members;
   private final Map<String, ZenNativeMember> staticMembers;
   private final List<ZenNativeCaster> casters;
   private final List<ZenNativeOperator> trinaryOperators;
   private final List<ZenNativeOperator> binaryOperators;
   private final List<ZenNativeOperator> unaryOperators;

   public DumpZenTypeNative(Class<?> clazz, String zsAliasPath, Map<String, ZenNativeMember> members, Map<String, ZenNativeMember> staticMembers, List<ZenNativeCaster> casters, List<ZenNativeOperator> trinaryOperators, List<ZenNativeOperator> binaryOperators, List<ZenNativeOperator> unaryOperators) {
      super(clazz, zsAliasPath);
      this.members = members;
      this.staticMembers = staticMembers;
      this.casters = casters;
      this.trinaryOperators = trinaryOperators;
      this.binaryOperators = binaryOperators;
      this.unaryOperators = unaryOperators;
   }

   public JsonObject serialize(JsonSerializationContext context) {
      JsonObject obj = super.serialize(context);
      JsonObject memberMap = new JsonObject();
      JsonArray castersArray = new JsonArray();
      this.members.forEach((name, zenNativeMember) -> {
         this.zenTypeNativeMemberHelper(memberMap, name, zenNativeMember, false, context);
      });
      this.staticMembers.forEach((name, zenNativeMember) -> {
         this.zenTypeNativeMemberHelper(memberMap, name, zenNativeMember, true, context);
      });
      obj.add("members", memberMap);
      if (!this.casters.isEmpty()) {
         for(ZenNativeCaster caster : this.casters) {
            castersArray.add(((IDumpable)caster.asDumpedObject().get(0)).serialize(context));
         }

         obj.add("casters", castersArray);
      }

      JsonArray array = new JsonArray();
      if (this.operatorHelper(array, this.unaryOperators, context) || this.operatorHelper(array, this.binaryOperators, context) || this.operatorHelper(array, this.trinaryOperators, context)) {
         obj.add("operators", array);
      }

      return obj;
   }

   private void zenTypeNativeMemberHelper(JsonObject memberMap, String name, ZenNativeMember zenNativeMember, boolean isStatic, JsonSerializationContext context) {
      JsonObject jsonMember = new JsonObject();
      if (zenNativeMember.getGetter() != null) {
         jsonMember.add("getter", (new DumpIJavaMethod(zenNativeMember.getGetter())).withStaticOverride(isStatic).serialize(context));
      }

      if (zenNativeMember.getSetter() != null) {
         jsonMember.add("setter", (new DumpIJavaMethod(zenNativeMember.getSetter())).withStaticOverride(isStatic).serialize(context));
      }

      List<IJavaMethod> methodList = zenNativeMember.getMethods();
      if (!methodList.isEmpty()) {
         JsonArray methodArray = new JsonArray();

         for(IJavaMethod iJavaMethod : zenNativeMember.getMethods()) {
            methodArray.add((new DumpIJavaMethod(iJavaMethod)).serialize(context));
         }
      }

      memberMap.add(name, jsonMember);
   }

   private boolean operatorHelper(JsonArray obj, List<ZenNativeOperator> operators, JsonSerializationContext context) {
      if (operators.isEmpty()) {
         return false;
      } else {
         for(ZenNativeOperator unaryOperator : operators) {
            obj.add(unaryOperator.serialize(context));
         }

         return true;
      }
   }
}
