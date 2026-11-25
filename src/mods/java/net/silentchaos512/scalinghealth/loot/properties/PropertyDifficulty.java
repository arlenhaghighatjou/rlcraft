package net.silentchaos512.scalinghealth.loot.properties;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import net.silentchaos512.scalinghealth.api.ScalingHealthAPI;
import net.silentchaos512.scalinghealth.config.Config;

public class PropertyDifficulty implements EntityProperty {
   private final int min;
   private final int max;

   private PropertyDifficulty(int min, int max) {
      this.min = min;
      this.max = max;
   }

   public boolean testProperty(Random random, Entity entityIn) {
      if (!(entityIn instanceof EntityLivingBase)) {
         return false;
      } else {
         double difficulty = ScalingHealthAPI.getEntityDifficulty((EntityLivingBase)entityIn);
         difficulty = difficulty / (double)Config.Mob.Blight.difficultyMultiplier;
         return difficulty >= (double)this.min && difficulty <= (double)this.max;
      }
   }

   public static class Serializer extends net.minecraft.world.storage.loot.properties.EntityProperty.Serializer<PropertyDifficulty> {
      public Serializer() {
         super(new ResourceLocation("scalinghealth", "difficulty"), PropertyDifficulty.class);
      }

      public JsonElement serialize(PropertyDifficulty property, JsonSerializationContext serializationContext) {
         JsonObject json = new JsonObject();
         json.addProperty("min", Integer.valueOf(property.min));
         json.addProperty("max", Integer.valueOf(property.max));
         return json;
      }

      public PropertyDifficulty deserialize(JsonElement element, JsonDeserializationContext deserializationContext) {
         JsonObject json = element.getAsJsonObject();
         int min = JsonUtils.getInt(json, "min", 0);
         int max = JsonUtils.getInt(json, "max", Integer.MAX_VALUE);
         return new PropertyDifficulty(min, max);
      }
   }
}
