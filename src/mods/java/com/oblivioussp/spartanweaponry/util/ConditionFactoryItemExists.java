package com.oblivioussp.spartanweaponry.util;

import com.google.gson.JsonObject;
import java.util.function.BooleanSupplier;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class ConditionFactoryItemExists implements IConditionFactory {
   public ConditionFactoryItemExists() {
   }

   public BooleanSupplier parse(JsonContext context, JsonObject json) {
      String itemName = json.get("item").getAsString();
      boolean result = Item.REGISTRY.containsKey(new ResourceLocation(itemName));
      return () -> {
         return result;
      };
   }
}
