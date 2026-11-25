package org.jwaresoftware.mcmods.armorunder.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.commons.lang3.Validate;
import org.jwaresoftware.mcmods.armorunder.ModItems;
import org.jwaresoftware.mcmods.lib.Armory;
import org.jwaresoftware.mcmods.lib.ItemStacks;
import org.jwaresoftware.mcmods.lib.Armory.XLining;
import org.jwaresoftware.mcmods.lib.Armory.XLining.Def;
import org.jwaresoftware.mcmods.lib.recipes.JR.LegacyShapelessOreRecipe;

public final class BarrierReinforcedArmorRecipe extends LinedArmorRecipeSkeleton {
   private final boolean _heat_shielding;

   BarrierReinforcedArmorRecipe(IRecipe template, boolean heat_protection) {
      super(template);
      this._heat_shielding = heat_protection;
   }

   public static final boolean isBarrierLining(Item item, boolean heat_shielding) {
      return heat_shielding ? item == ModItems.heatbarrier_boots_liner || item == ModItems.heatbarrier_chestplate_liner || item == ModItems.heatbarrier_helmet_liner || item == ModItems.heatbarrier_leggings_liner : item == ModItems.antifreeze_boots_liner || item == ModItems.antifreeze_chestplate_liner || item == ModItems.antifreeze_helmet_liner || item == ModItems.antifreeze_leggings_liner;
   }

   protected boolean isLining(Item item) {
      return isBarrierLining(item, this._heat_shielding);
   }

   protected boolean canAddLining(ItemStack armorpiece) {
      return !Armory.hasXLining(armorpiece);
   }

   protected Def getXLining(ItemStack lining) {
      return new Def(this._heat_shielding ? XLining.OBSIDIAN_SHIELD : XLining.ANTIFREEZE_SHIELD);
   }

   protected boolean isEnabled() {
      return ArmorRecipes.SPECIAL_SETS && super.isEnabled();
   }

   public static final class Factory implements IRecipeFactory {
      public Factory() {
      }

      public IRecipe parse(JsonContext context, JsonObject json) {
         boolean heat_protection = true;
         ShapelessOreRecipe impl = ShapelessOreRecipe.factory(context, json);
         ItemStack out = impl.getRecipeOutput();
         if (!ItemStacks.isEmpty(out)) {
            Def defn = Armory.getXLining(out);
            Validate.isTrue(defn.name == XLining.OBSIDIAN_SHIELD || defn.name == XLining.ANTIFREEZE_SHIELD, "Adding obsidian or antifreeze barrier liner", new Object[0]);
            heat_protection = defn.name == XLining.OBSIDIAN_SHIELD;
         }

         ShapelessOreRecipe var7 = new LegacyShapelessOreRecipe((ResourceLocation)null, impl);
         return new BarrierReinforcedArmorRecipe(var7, heat_protection);
      }
   }
}
