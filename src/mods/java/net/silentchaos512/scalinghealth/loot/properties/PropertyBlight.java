package net.silentchaos512.scalinghealth.loot.properties;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import net.silentchaos512.scalinghealth.event.BlightHandler;

public class PropertyBlight implements EntityProperty {
   private final boolean isBlight;

   private PropertyBlight(boolean isBlight) {
      this.isBlight = isBlight;
   }

   public boolean testProperty(EaglercraftRandom random, Entity entityIn) {
      return entityIn instanceof EntityLivingBase && this.isBlight == BlightHandler.isBlight((EntityLivingBase)entityIn);
   }

   public static class Serializer extends net.minecraft.world.storage.loot.properties.EntityProperty.Serializer<PropertyBlight> {
      static final ResourceLocation NAME = new ResourceLocation("scalinghealth", "is_blight");

      public Serializer() {
         super(NAME, PropertyBlight.class);
      }

      public JsonElement serialize(PropertyBlight property, JsonSerializationContext serializationContext) {
         return new JsonPrimitive(property.isBlight);
      }

      public PropertyBlight deserialize(JsonElement element, JsonDeserializationContext deserializationContext) {
         return new PropertyBlight(JsonUtils.getBoolean(element, NAME.toString()));
      }
   }
}
