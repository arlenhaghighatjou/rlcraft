package org.jwaresoftware.mcmods.lib.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.jwaresoftware.mcmods.lib.LibInfo;

public final class RandomChanceWithLuckCondition implements LootCondition {
   final float _chance;
   final float _luckMultiplier;

   public RandomChanceWithLuckCondition(float chance, float luckMultiplier) {
      this._chance = chance;
      this._luckMultiplier = luckMultiplier;
   }

   public boolean testCondition(Random rand, LootContext context) {
      float luck = context.getLuck();
      return rand.nextFloat() < this._chance + luck * this._luckMultiplier;
   }

   public static class Serializer extends net.minecraft.world.storage.loot.conditions.LootCondition.Serializer<RandomChanceWithLuckCondition> {
      public Serializer() {
         super(new ResourceLocation(LibInfo.MOD_ID(), "random_chance_with_luck"), RandomChanceWithLuckCondition.class);
      }

      public RandomChanceWithLuckCondition deserialize(JsonObject json, JsonDeserializationContext context) {
         return new RandomChanceWithLuckCondition(JsonUtils.getFloat(json, "chance"), JsonUtils.getFloat(json, "luck_multiplier"));
      }

      public void serialize(JsonObject json, RandomChanceWithLuckCondition lcond, JsonSerializationContext context) {
         json.addProperty("chance", Float.valueOf(lcond._chance));
         json.addProperty("luck_multiplier", Float.valueOf(lcond._luckMultiplier));
      }
   }
}
