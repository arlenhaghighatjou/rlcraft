package dev.satyrn.wolfarmor.factories.conditions;

import com.google.gson.JsonObject;
import dev.satyrn.wolfarmor.WolfArmorMod;
import java.util.function.BooleanSupplier;
import net.minecraftforge.common.crafting.IConditionFactory;
import net.minecraftforge.common.crafting.JsonContext;

public class EnableCraftingCondition implements IConditionFactory {
   public EnableCraftingCondition() {
   }

   public BooleanSupplier parse(JsonContext jsonContext, JsonObject jsonObject) {
      return () -> {
         return WolfArmorMod.getConfig().getEnableCrafting();
      };
   }
}
