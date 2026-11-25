package leviathan143.loottweaker.common.lib;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import leviathan143.loottweaker.common.darkmagic.LootFunctionManagerAccessors;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager.Serializer;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class RobustLootFunctionSerialiser implements JsonSerializer<LootFunction> {
   private static final Logger SANITY_LOGGER = LogManager.getLogger("loottweaker.sanity_checks");
   private final JsonSerializer<LootFunction> vanilla = new Serializer();

   public RobustLootFunctionSerialiser() {
   }

   public JsonElement serialize(LootFunction function, Type type, JsonSerializationContext context) {
      if (this.checkSerialisable(function)) {
         return this.vanilla.serialize(function, type, context);
      } else {
         JsonObject json = new JsonObject();
         json.addProperty("_comment", "A best effort serialisation of a non-serialisable loot function");
         json.addProperty("class", function.getClass().getName());
         if (function.getConditions() != null && function.getConditions().length > 0) {
            json.add("conditions", context.serialize(function.getConditions()));
         }

         return json;
      }
   }

   private boolean checkSerialisable(LootFunction function) {
      if (LootFunctionManagerAccessors.getClassToSerialiserMap().containsKey(function.getClass())) {
         return true;
      } else {
         SANITY_LOGGER.error("No serialiser registered for loot function {}", function.getClass().getName());
         return false;
      }
   }
}
