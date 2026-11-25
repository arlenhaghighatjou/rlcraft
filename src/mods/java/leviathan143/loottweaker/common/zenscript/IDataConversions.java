package leviathan143.loottweaker.common.zenscript;

import com.google.common.annotations.VisibleForTesting;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import java.util.Collections;
import java.util.Map;
import leviathan143.loottweaker.common.LootTweaker;
import leviathan143.loottweaker.common.zenscript.wrapper.ZenLootConditionWrapper;
import leviathan143.loottweaker.common.zenscript.wrapper.ZenLootFunctionWrapper;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

@ZenRegister
@ZenExpansion("crafttweaker.data.IData")
public class IDataConversions {
   private static IDataConversions.Impl IMPLEMENTATION = new IDataConversions.Impl(JsonMapConversions.IMPLEMENTATION, LootTweaker.CONTEXT);

   public IDataConversions() {
   }

   @ZenCaster
   public static ZenLootConditionWrapper asLootCondition(IData json) {
      return IMPLEMENTATION.asLootCondition(json);
   }

   @ZenCaster
   public static ZenLootFunctionWrapper asLootFunction(IData json) {
      return IMPLEMENTATION.asLootFunction(json);
   }

   @VisibleForTesting
   public static class Impl {
      private final JsonMapConversions.Impl jsonMapConversions;
      private final LootTweakerContext context;

      public Impl(JsonMapConversions.Impl jsonMapConversions, LootTweakerContext context) {
         this.jsonMapConversions = jsonMapConversions;
         this.context = context;
      }

      @VisibleForTesting
      public ZenLootConditionWrapper asLootCondition(IData json) {
         return this.jsonMapConversions.asLootCondition(this.asStringKeyedMap(json));
      }

      @VisibleForTesting
      public ZenLootFunctionWrapper asLootFunction(IData json) {
         return this.jsonMapConversions.asLootFunction(this.asStringKeyedMap(json));
      }

      private Map<String, IData> asStringKeyedMap(IData json) {
         Map<String, IData> map = json.asMap();
         if (map == null) {
            this.context.getErrorHandler().error("%s > Expected map, got %s", CraftTweakerAPI.getScriptFileAndLine(), json);
            return Collections.emptyMap();
         } else {
            return map;
         }
      }
   }
}
