package org.jwaresoftware.mcmods.armorunder.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import org.jwaresoftware.mcmods.armorunder.ModItems;
import org.jwaresoftware.mcmods.lib.ItemStacks;
import org.jwaresoftware.mcmods.lib.SharedGlue;

public final class BuffAutoregLiningRecipe extends Impl<IRecipe> implements IRecipe {
   private final IRecipe _impl;
   private final int _modifier;

   static final List<IRecipe> addSampleRecipes(List<IRecipe> recipes) {
      if (ArmorRecipes.ENABLED) {
         for(ItemStack ozzy_liner : OreDictionary.getOres("itemAutoregLining", false)) {
            addRecipes(ozzy_liner, recipes);
         }
      }

      return recipes;
   }

   BuffAutoregLiningRecipe(int modifier, IRecipe template) {
      this._impl = template;
      this._modifier = modifier;
   }

   public boolean canFit(int width, int height) {
      return this._impl.canFit(width, height);
   }

   public ItemStack getRecipeOutput() {
      return ItemStacks.NULLSTACK();
   }

   public boolean matches(InventoryCrafting grid, World world) {
      return ArmorRecipes.ENABLED && this._impl.matches(grid, world);
   }

   private int getTargetModifier(ItemStack lining) {
      return AutoregLinedArmorRecipe.getTargetModifier(lining, 0);
   }

   public ItemStack getCraftingResult(InventoryCrafting grid) {
      ItemStack out = ItemStacks.NULLSTACK();
      if (ArmorRecipes.ENABLED) {
         ItemStack lining = null;

         for(int i = 0; i < grid.getSizeInventory(); ++i) {
            ItemStack next = grid.getStackInSlot(i);
            if (!ItemStacks.isEmpty(next) && AutoregLinedArmorRecipe.isAutoregLining(next.getItem())) {
               lining = next;
               break;
            }
         }

         if (lining != null) {
            int cur_target = this.getTargetModifier(lining);
            int target = AutoregLinedArmorRecipe.normalizeOverride(cur_target + this._modifier);
            if (target != cur_target) {
               out = ItemStacks.copyItemStackSingle(lining);
               AutoregLinedArmorRecipe.setTargetModifier(out, target);
            }
         }
      }

      return out;
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting grid) {
      return SharedGlue.emptyNonNullItemStackList(grid.getSizeInventory());
   }

   private static final void addRecipes(ItemStack plain_liner, List<IRecipe> recipes) {
      ItemStack buffed_liner = ItemStacks.copyItemStackSingle(plain_liner);
      AutoregLinedArmorRecipe.setTargetModifier(buffed_liner, 1);
      recipes.add(new ShapedOreRecipe((ResourceLocation)null, buffed_liner, new Object[]{"xxx", "xLx", "xxx", 'x', ModItems.heating_goo, 'L', plain_liner}));
      buffed_liner = ItemStacks.copyItemStackSingle(plain_liner);
      AutoregLinedArmorRecipe.setTargetModifier(buffed_liner, -1);
      recipes.add(new ShapedOreRecipe((ResourceLocation)null, buffed_liner, new Object[]{"xxx", "xLx", "xxx", 'x', ModItems.cooling_goo, 'L', plain_liner}));
   }

   public static final class Factory implements IRecipeFactory {
      public Factory() {
      }

      public IRecipe parse(JsonContext context, JsonObject json) {
         int modifier = JsonUtils.getInt(json, "modifier", 0);
         if (modifier > 0) {
            modifier = 1;
         } else {
            if (modifier >= 0) {
               throw new JsonParseException("Lining modifier must by either -1 or +1");
            }

            modifier = -1;
         }

         ShapedOreRecipe impl = ShapedOreRecipe.factory(context, json);
         return new BuffAutoregLiningRecipe(modifier, impl);
      }
   }
}
