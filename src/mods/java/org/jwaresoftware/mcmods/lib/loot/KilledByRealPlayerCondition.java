package org.jwaresoftware.mcmods.lib.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.entity.Entity;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.jwaresoftware.mcmods.lib.LibInfo;
import org.jwaresoftware.mcmods.lib.SharedGlue;

public final class KilledByRealPlayerCondition implements LootCondition {
   final boolean _is_not;
   final boolean _thorough;

   public KilledByRealPlayerCondition(boolean inverse, boolean universal) {
      this._is_not = inverse;
      this._thorough = universal;
   }

   public boolean testCondition(EaglercraftRandom rand, LootContext context) {
      Entity e = context.getKillerPlayer();
      if (e == null && this._thorough) {
         e = context.getKiller();
      }

      boolean is = SharedGlue.isRealNonSpectatingPlayer(e);
      return is == !this._is_not;
   }

   public static class Serializer extends net.minecraft.world.storage.loot.conditions.LootCondition.Serializer<KilledByRealPlayerCondition> {
      public Serializer() {
         super(new ResourceLocation(LibInfo.MOD_ID(), "killed_by_realplayer"), KilledByRealPlayerCondition.class);
      }

      public KilledByRealPlayerCondition deserialize(JsonObject json, JsonDeserializationContext context) {
         return new KilledByRealPlayerCondition(JsonUtils.getBoolean(json, "inverse", false), JsonUtils.getBoolean(json, "any", false));
      }

      public void serialize(JsonObject json, KilledByRealPlayerCondition lcond, JsonSerializationContext context) {
         json.addProperty("inverse", Boolean.valueOf(lcond._is_not));
         json.addProperty("any", Boolean.valueOf(lcond._thorough));
      }
   }
}
