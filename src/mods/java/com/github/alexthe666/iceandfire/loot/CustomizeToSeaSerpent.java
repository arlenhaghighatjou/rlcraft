package com.github.alexthe666.iceandfire.loot;

import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.entity.EntitySeaSerpent;
import com.github.alexthe666.iceandfire.item.ItemSeaSerpentScales;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class CustomizeToSeaSerpent extends LootFunction {
   public CustomizeToSeaSerpent(LootCondition[] conditionsIn) {
      super(conditionsIn);
   }

   public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
      if (!stack.isEmpty() && context.getLootedEntity() instanceof EntitySeaSerpent) {
         Random random = new Random();
         EntitySeaSerpent seaSerpent = (EntitySeaSerpent)context.getLootedEntity();
         int ancientModifier = seaSerpent.isAncient() ? 2 : 1;
         if (stack.getItem() instanceof ItemSeaSerpentScales) {
            stack.setCount(1 + random.nextInt(1 + (int)Math.ceil((double)(seaSerpent.getSeaSerpentScale() * 3.0F * (float)ancientModifier))));
            return new ItemStack(seaSerpent.getEnum().scale, stack.getCount(), stack.getMetadata());
         }

         if (stack.getItem() == ModItems.sea_serpent_fang) {
            stack.setCount(1 + random.nextInt(1 + (int)Math.ceil((double)(seaSerpent.getSeaSerpentScale() * 2.0F * (float)ancientModifier))));
            return stack;
         }
      }

      return stack;
   }

   public static class Serializer extends net.minecraft.world.storage.loot.functions.LootFunction.Serializer<CustomizeToSeaSerpent> {
      public Serializer() {
         super(new ResourceLocation("iceandfire:customize_to_sea_serpent"), CustomizeToSeaSerpent.class);
      }

      public void serialize(JsonObject object, CustomizeToSeaSerpent functionClazz, JsonSerializationContext serializationContext) {
      }

      public CustomizeToSeaSerpent deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
         return new CustomizeToSeaSerpent(conditionsIn);
      }
   }
}
