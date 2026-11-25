package codersafterdark.reskillable.loot;

import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.RequirementHolder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class LootConditionRequirement implements LootCondition {
   private final RequirementHolder requirementHolder;
   private final boolean requiresPlayer;
   private final String[] requirements;

   public LootConditionRequirement(boolean requiresPlayer, String[] requirements) {
      this.requiresPlayer = requiresPlayer;
      this.requirementHolder = requirements.length > 0 ? RequirementHolder.fromStringList(requirements) : RequirementHolder.realEmpty();
      this.requirements = requirements;
   }

   @ParametersAreNonnullByDefault
   public boolean testCondition(EaglercraftRandom rand, LootContext context) {
      return context.getKillerPlayer() instanceof EntityPlayer ? PlayerDataHandler.get((EntityPlayer)context.getKillerPlayer()).matchStats(this.requirementHolder) : !this.requiresPlayer;
   }

   public static class Serializer extends net.minecraft.world.storage.loot.conditions.LootCondition.Serializer<LootConditionRequirement> {
      public Serializer() {
         super(new ResourceLocation("reskillable", "requirement"), LootConditionRequirement.class);
      }

      @ParametersAreNonnullByDefault
      public void serialize(JsonObject json, LootConditionRequirement value, JsonSerializationContext context) {
         json.addProperty("requiresPlayer", Boolean.valueOf(value.requiresPlayer));
         if (value.requirements.length > 1) {
            JsonArray requirementsJson = new JsonArray();

            for(String requirement : value.requirements) {
               requirementsJson.add(requirement);
            }

            json.add("requirements", requirementsJson);
         } else {
            json.addProperty("requirements", value.requirements[0]);
         }

      }

      @Nonnull
      @ParametersAreNonnullByDefault
      public LootConditionRequirement deserialize(JsonObject json, JsonDeserializationContext context) {
         boolean requiresPlayer = JsonUtils.getBoolean(json, "requiresPlayer", false);
         String[] requirements = new String[0];
         JsonElement requirementsJson = json.get("requirements");
         if (requirementsJson != null) {
            if (requirementsJson.isJsonArray()) {
               List<String> reqs = new ArrayList();
               requirementsJson.getAsJsonArray().forEach((req) -> {
                  reqs.add(req.getAsString());
               });
               requirements = (String[])reqs.toArray(new String[0]);
            } else {
               if (!requirementsJson.isJsonPrimitive() || !requirementsJson.getAsJsonPrimitive().isString()) {
                  throw new JsonParseException("Failed to find Requirements");
               }

               requirements = new String[]{requirementsJson.getAsJsonPrimitive().getAsString()};
            }
         }

         return new LootConditionRequirement(requiresPlayer, requirements);
      }
   }
}
