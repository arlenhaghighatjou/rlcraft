package org.jwaresoftware.mcmods.armorunder.recipes;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import org.jwaresoftware.mcmods.armorunder.ModItems;
import org.jwaresoftware.mcmods.lib.Armory;
import org.jwaresoftware.mcmods.lib.ItemStacks;
import org.jwaresoftware.mcmods.lib.RID;
import org.jwaresoftware.mcmods.lib.SharedGlue;
import org.jwaresoftware.mcmods.lib.Armory.TLining;
import org.jwaresoftware.mcmods.lib.Armory.XLining;
import org.jwaresoftware.mcmods.lib.recipes.JR.LegacyShapelessOreRecipe;

public final class StripArmorTLiningRecipe extends Impl<IRecipe> implements IRecipe {
   private final IRecipe _impl;

   StripArmorTLiningRecipe(IRecipe template) {
      this._impl = template;
   }

   public boolean canFit(int width, int height) {
      return width * height == 2;
   }

   public ItemStack getRecipeOutput() {
      return ItemStacks.NULLSTACK();
   }

   @Nullable
   private TLining newTLining(ItemStack armorpiece) {
      TLining cur_lining = Armory.getTLining(armorpiece);
      if (cur_lining == TLining.NONE) {
         return null;
      } else {
         int modifier = cur_lining.getDirection() > 0 ? -1 : 1;
         return cur_lining.adjust(modifier);
      }
   }

   @Nullable
   private ItemStack oldTLining(ItemStack armorpiece) {
      TLining cur_lining = Armory.getTLining(armorpiece);
      if (cur_lining.isEmpty()) {
         return null;
      } else {
         boolean warming = cur_lining.getDirection() > 0;
         Item lining = null;
         if (RID.matches(armorpiece, "itemChestplate")) {
            lining = warming ? ModItems.warm_chestplate_liner : ModItems.cool_chestplate_liner;
         } else if (RID.matches(armorpiece, "itemLeggings")) {
            lining = warming ? ModItems.warm_leggings_liner : ModItems.cool_leggings_liner;
         } else if (RID.matches(armorpiece, "itemHelmet")) {
            lining = warming ? ModItems.warm_helmet_liner : ModItems.cool_helmet_liner;
         } else if (RID.matches(armorpiece, "itemBoots")) {
            lining = warming ? ModItems.warm_boots_liner : ModItems.cool_boots_liner;
         }

         return lining != null ? new ItemStack(lining) : null;
      }
   }

   public boolean matches(InventoryCrafting grid, World world) {
      return ArmorRecipes.ENABLED && this._impl.matches(grid, world);
   }

   public ItemStack getCraftingResult(InventoryCrafting grid) {
      ItemStack out = ItemStacks.NULLSTACK();
      if (ArmorRecipes.ENABLED) {
         ItemStack armorpiece = null;

         for(int i = 0; i < grid.getSizeInventory(); ++i) {
            ItemStack next = grid.getStackInSlot(i);
            if (!ItemStacks.isEmpty(next)) {
               Item item = next.getItem();
               if (item instanceof ItemArmor) {
                  armorpiece = next;
                  break;
               }
            }
         }

         if (armorpiece != null) {
            TLining new_lining = this.newTLining(armorpiece);
            if (new_lining != null) {
               out = ItemStacks.copyItemStackSingle(armorpiece);
               out = Armory.setTLining(out, new_lining);
            } else if (Armory.hasXLining(armorpiece)) {
               out = ItemStacks.copyItemStackSingle(armorpiece);
               Armory.setXLining(out, XLining.NO_DEF);
            }
         }
      }

      return out;
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting grid) {
      NonNullList<ItemStack> out = SharedGlue.emptyNonNullItemStackList(grid.getSizeInventory());

      for(int i = 0; i < grid.getSizeInventory(); ++i) {
         ItemStack next = grid.getStackInSlot(i);
         if (!ItemStacks.isEmpty(next)) {
            Item item = next.getItem();
            if (item == ModItems.liner_snips) {
               out.set(i, item.getContainerItem(next));
            } else if (item instanceof ItemArmor) {
               ItemStack leftover = this.oldTLining(next);
               if (leftover != null) {
                  out.set(i, leftover);
               }
            }
         }
      }

      return out;
   }

   public static final class Factory implements IRecipeFactory {
      public Factory() {
      }

      public IRecipe parse(JsonContext context, JsonObject json) {
         ShapelessOreRecipe impl = ShapelessOreRecipe.factory(context, json);
         ShapelessOreRecipe var4 = new LegacyShapelessOreRecipe((ResourceLocation)null, impl);
         return new StripArmorTLiningRecipe(var4);
      }
   }
}
