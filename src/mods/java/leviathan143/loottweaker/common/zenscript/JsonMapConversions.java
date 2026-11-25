package leviathan143.loottweaker.common.zenscript;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.data.IData;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import leviathan143.loottweaker.common.LootTweaker;
import leviathan143.loottweaker.common.darkmagic.LootTableManagerAccessors;
import leviathan143.loottweaker.common.lib.Arguments;
import leviathan143.loottweaker.common.lib.JsonConverter;
import leviathan143.loottweaker.common.zenscript.wrapper.ZenLootConditionWrapper;
import leviathan143.loottweaker.common.zenscript.wrapper.ZenLootFunctionWrapper;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

@ZenRegister
@ZenExpansion("any[any]")
public class JsonMapConversions {
   public static JsonMapConversions.Impl IMPLEMENTATION = new JsonMapConversions.Impl(LootTweaker.CONTEXT);

   public JsonMapConversions() {
   }

   @ZenCaster
   public static ZenLootConditionWrapper asLootCondition(Map<String, IData> json) {
      return IMPLEMENTATION.asLootCondition(json);
   }

   @ZenCaster
   public static ZenLootFunctionWrapper asLootFunction(Map<String, IData> json) {
      return IMPLEMENTATION.asLootFunction(json);
   }

   @VisibleForTesting
   public static class Impl {
      private final LootTweakerContext context;
      private final Gson lootDeserialiser = LootTableManagerAccessors.getGsonInstance();
      private final Gson jsonElementSerialiser = (new GsonBuilder()).registerTypeHierarchyAdapter(IData.class, (src, type, contextx) -> {
         return JsonConverter.from(src);
      }).create();

      public Impl(LootTweakerContext context) {
         this.context = context;
      }

      @VisibleForTesting
      public ZenLootConditionWrapper asLootCondition(Map<String, ?> json) {
         return (ZenLootConditionWrapper)this.parse(json, LootCondition.class).map(ZenLootConditionWrapper::new).orElse(ZenLootConditionWrapper.INVALID);
      }

      @VisibleForTesting
      public ZenLootFunctionWrapper asLootFunction(Map<String, ?> json) {
         return (ZenLootFunctionWrapper)this.parse(json, LootFunction.class).map((fn) -> {
            return new ZenLootFunctionWrapper(fn, this.context);
         }).orElse(ZenLootFunctionWrapper.INVALID);
      }

      private <T> Optional<T> parse(Map<String, ?> data, Class<T> clazz) {
         if (!Arguments.nonNull(this.context.getErrorHandler(), "json", data)) {
            return Optional.empty();
         } else {
            try {
               return Optional.of(this.lootDeserialiser.fromJson(this.jsonElementSerialiser.toJsonTree(data), clazz));
            } catch (JsonSyntaxException var4) {
               this.context.getErrorHandler().error(var4.getMessage());
               return Optional.empty();
            }
         }
      }
   }
}
