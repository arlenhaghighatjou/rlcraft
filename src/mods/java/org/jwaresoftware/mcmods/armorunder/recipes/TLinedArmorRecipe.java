package org.jwaresoftware.mcmods.armorunder.recipes;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.registry.GameRegistry.ItemStackHolder;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.jwaresoftware.mcmods.armorunder.ArmorUnderwear;
import org.jwaresoftware.mcmods.armorunder.ModConfig;
import org.jwaresoftware.mcmods.lib.Armory;
import org.jwaresoftware.mcmods.lib.ItemStacks;
import org.jwaresoftware.mcmods.lib.ModIntegration;
import org.jwaresoftware.mcmods.lib.SharedGlue;
import org.jwaresoftware.mcmods.lib.Armory.TLining;
import org.jwaresoftware.mcmods.lib.Armory.XLining;
import org.jwaresoftware.mcmods.lib.recipes.JR.LegacyShapelessOreRecipe;

public final class TLinedArmorRecipe extends Impl<IRecipe> implements IRecipe {
   private static final String _TAN = "toughasnails:";
   private static final String _MOD = "armorunder:";
   @ItemStackHolder("toughasnails:wool_helmet")
   public static final ItemStack TAN_WARMER_LINING_HELMET = null;
   @ItemStackHolder("toughasnails:wool_chestplate")
   public static final ItemStack TAN_WARMER_LINING_CHESTPLATE = null;
   @ItemStackHolder("toughasnails:wool_leggings")
   public static final ItemStack TAN_WARMER_LINING_LEGGINGS = null;
   @ItemStackHolder("toughasnails:wool_boots")
   public static final ItemStack TAN_WARMER_LINING_BOOTS = null;
   @ItemStackHolder("toughasnails:jelled_slime_helmet")
   public static final ItemStack TAN_COOLER_LINING_HELMET = null;
   @ItemStackHolder("toughasnails:jelled_slime_chestplate")
   public static final ItemStack TAN_COOLER_LINING_CHESTPLATE = null;
   @ItemStackHolder("toughasnails:jelled_slime_leggings")
   public static final ItemStack TAN_COOLER_LINING_LEGGINGS = null;
   @ItemStackHolder("toughasnails:jelled_slime_boots")
   public static final ItemStack TAN_COOLER_LINING_BOOTS = null;
   @ItemStackHolder("armorunder:warm_helmet_liner")
   public static final ItemStack WARMER_LINING_HELMET = null;
   @ItemStackHolder("armorunder:warm_chestplate_liner")
   public static final ItemStack WARMER_LINING_CHESTPLATE = null;
   @ItemStackHolder("armorunder:warm_leggings_liner")
   public static final ItemStack WARMER_LINING_LEGGINGS = null;
   @ItemStackHolder("armorunder:warm_boots_liner")
   public static final ItemStack WARMER_LINING_BOOTS = null;
   @ItemStackHolder("armorunder:cool_helmet_liner")
   public static final ItemStack COOLER_LINING_HELMET = null;
   @ItemStackHolder("armorunder:cool_chestplate_liner")
   public static final ItemStack COOLER_LINING_CHESTPLATE = null;
   @ItemStackHolder("armorunder:cool_leggings_liner")
   public static final ItemStack COOLER_LINING_LEGGINGS = null;
   @ItemStackHolder("armorunder:cool_boots_liner")
   public static final ItemStack COOLER_LINING_BOOTS = null;
   private static final List<Item> _LININGS = new ArrayList();
   private static final List<Item> _LININGS_WARMUP = new ArrayList();
   private static boolean ENABLED = false;
   private static boolean TAN_ENABLED = false;
   private final IRecipe _impl;
   private final int _max_thickness;

   private static boolean check(ItemStack ingredient, boolean warm) {
      boolean ok = false;
      if (!ItemStacks.isEmpty(ingredient)) {
         Item key = ingredient.getItem();
         if (warm) {
            _LININGS_WARMUP.add(key);
         }

         ok = _LININGS.add(key);
      }

      return ok;
   }

   static final boolean initFinal(ModConfig config) {
      if (!check(WARMER_LINING_HELMET, true)) {
         return false;
      } else if (!check(WARMER_LINING_CHESTPLATE, true)) {
         return false;
      } else if (!check(WARMER_LINING_LEGGINGS, true)) {
         return false;
      } else if (!check(WARMER_LINING_BOOTS, true)) {
         return false;
      } else if (!check(COOLER_LINING_HELMET, false)) {
         return false;
      } else if (!check(COOLER_LINING_CHESTPLATE, false)) {
         return false;
      } else if (!check(COOLER_LINING_LEGGINGS, false)) {
         return false;
      } else if (!check(COOLER_LINING_BOOTS, false)) {
         return false;
      } else {
         ENABLED = true;
         if (!config.isSuperCompatibilityMode() && ModIntegration.TOUGH_AS_NAILS.isLoaded()) {
            check(TAN_WARMER_LINING_HELMET, true);
            check(TAN_WARMER_LINING_CHESTPLATE, true);
            check(TAN_WARMER_LINING_LEGGINGS, true);
            check(TAN_WARMER_LINING_BOOTS, true);
            check(TAN_COOLER_LINING_HELMET, false);
            check(TAN_COOLER_LINING_CHESTPLATE, false);
            check(TAN_COOLER_LINING_LEGGINGS, false);
            check(TAN_COOLER_LINING_BOOTS, false);
            TAN_ENABLED = TAN_COOLER_LINING_BOOTS != null && TAN_WARMER_LINING_BOOTS != null;
         }

         Map<String, Item[]> others = config.getUnderwearReadyArmorAll();

         for(Entry<String, Item[]> e : others.entrySet()) {
            for(Item item : (Item[])e.getValue()) {
               if (SharedGlue.isDefined(item)) {
                  ItemStack any_durability = new ItemStack(item, 1, 32767);
                  OreDictionary.registerOre((String)e.getKey(), any_durability);
                  OreDictionary.registerOre("itemArmor", any_durability);
               }
            }
         }

         if (config.isDebugMode()) {
            dumpDict();
         }

         return ENABLED;
      }
   }

   static final List<IRecipe> addSampleRecipes(List<IRecipe> recipes) {
      if (ENABLED) {
         addRecipes(WARMER_LINING_HELMET, "itemHelmet", TLining.WARM, 2, recipes);
         addRecipes(COOLER_LINING_HELMET, "itemHelmet", TLining.COOL, 2, recipes);
         addRecipes(WARMER_LINING_BOOTS, "itemBoots", TLining.WARM, 1, recipes);
         addRecipes(COOLER_LINING_BOOTS, "itemBoots", TLining.COOL, 1, recipes);
         addRecipes(WARMER_LINING_CHESTPLATE, "itemChestplate", TLining.WARM, 3, recipes);
         addRecipes(COOLER_LINING_CHESTPLATE, "itemChestplate", TLining.COOL, 3, recipes);
         addRecipes(WARMER_LINING_LEGGINGS, "itemLeggings", TLining.WARM, 3, recipes);
         addRecipes(COOLER_LINING_LEGGINGS, "itemLeggings", TLining.COOL, 3, recipes);
         if (TAN_ENABLED) {
            addRecipes(TAN_WARMER_LINING_HELMET, "itemHelmet", TLining.WARM, 1, recipes);
            addRecipes(TAN_COOLER_LINING_HELMET, "itemHelmet", TLining.COOL, 1, recipes);
            addRecipes(TAN_WARMER_LINING_BOOTS, "itemBoots", TLining.WARM, 1, recipes);
            addRecipes(TAN_COOLER_LINING_BOOTS, "itemBoots", TLining.COOL, 1, recipes);
            addRecipes(TAN_WARMER_LINING_CHESTPLATE, "itemChestplate", TLining.WARM, 1, recipes);
            addRecipes(TAN_COOLER_LINING_CHESTPLATE, "itemChestplate", TLining.COOL, 1, recipes);
            addRecipes(TAN_WARMER_LINING_LEGGINGS, "itemLeggings", TLining.WARM, 1, recipes);
            addRecipes(TAN_COOLER_LINING_LEGGINGS, "itemLeggings", TLining.COOL, 1, recipes);
         }
      }

      return recipes;
   }

   TLinedArmorRecipe(TLining limit, IRecipe template) {
      this._impl = template;
      this._max_thickness = limit != TLining.NONE ? limit.getThickness() : Integer.MAX_VALUE;
   }

   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }

   public ItemStack getRecipeOutput() {
      return ItemStacks.NULLSTACK();
   }

   @Nullable
   private TLining newLining(ItemStack lining, ItemStack armorpiece) {
      TLining cur_lining = Armory.getTLining(armorpiece);
      int modifier = _LININGS_WARMUP.contains(lining.getItem()) ? 1 : -1;
      if (cur_lining != TLining.NONE) {
         boolean warm_lining = cur_lining.getOffsetModifier() > 0;
         if (warm_lining && modifier < 0) {
            return null;
         }

         if (!warm_lining && modifier > 0) {
            return null;
         }
      }

      TLining new_lining = cur_lining.adjust(modifier);
      int max_thickness = Math.min(ModConfig.getInstance().getLiningThicknessLimit(), this._max_thickness);
      return !new_lining.equals(cur_lining) && new_lining.getThickness() <= max_thickness ? new_lining : null;
   }

   public boolean matches(InventoryCrafting grid, World world) {
      boolean match = ENABLED && this._impl.matches(grid, world);
      if (match) {
         boolean found_armor = false;
         boolean found_lining = false;

         for(int i = 0; i < grid.getSizeInventory(); ++i) {
            ItemStack next = grid.getStackInSlot(i);
            if (!ItemStacks.isEmpty(next)) {
               Item item = next.getItem();
               if (_LININGS.contains(item)) {
                  if (found_lining) {
                     return false;
                  }

                  found_lining = true;
               } else if (item instanceof ItemArmor) {
                  if (found_armor) {
                     return false;
                  }

                  found_armor = true;
               }
            }
         }

         if (!found_armor || !found_lining) {
            match = false;
         }
      }

      return match;
   }

   public ItemStack getCraftingResult(InventoryCrafting grid) {
      ItemStack out = ItemStacks.NULLSTACK();
      if (ENABLED) {
         ItemStack lining = null;
         ItemStack armorpiece = null;

         for(int i = 0; i < grid.getSizeInventory(); ++i) {
            ItemStack next = grid.getStackInSlot(i);
            if (!ItemStacks.isEmpty(next)) {
               Item item = next.getItem();
               if (_LININGS.contains(item)) {
                  if (next.isItemDamaged() && ItemStacks.getDurabilityLeft(next) < 0.75F) {
                     return out;
                  }

                  lining = next;
               } else if (item instanceof ItemArmor) {
                  armorpiece = next;
               }
            }
         }

         if (lining != null && armorpiece != null && !Armory.hasXLining(XLining.TEMPERATURE_REGULATOR, armorpiece)) {
            TLining new_lining = this.newLining(lining, armorpiece);
            if (new_lining != null) {
               out = ItemStacks.copyItemStackSingle(armorpiece);
               out = Armory.setTLining(out, new_lining);
            }
         }
      }

      return out;
   }

   public NonNullList<ItemStack> getRemainingItems(InventoryCrafting grid) {
      return SharedGlue.emptyNonNullItemStackList(grid.getSizeInventory());
   }

   private static final void dumpDict() {
      Logger log = ArmorUnderwear.runtime.getLog();
      String[] dictKeys = new String[]{"itemBoots", "itemChestplate", "itemHelmet", "itemLeggings"};

      for(String k : dictKeys) {
         for(ItemStack a : OreDictionary.getOres(k, false)) {
            log.info("Di: {}: {}", k, a.getItem().getRegistryName() != null ? a.getItem().getRegistryName() : a);
         }
      }

   }

   private static final void addRecipes(ItemStack lining, String shared_key, TLining lining_type, int max, List<IRecipe> recipes) {
      int direction = lining_type.getDirection();

      for(ItemStack unlined : OreDictionary.getOres(shared_key, false)) {
         ItemStack input = ItemStacks.copyItemStackSingle(unlined);
         Item item = input.getItem();
         if (!_LININGS.contains(item) && item instanceof ItemArmor) {
            input.setItemDamage(0);
            ItemStack lined_curr = input.copy();
            ItemStack lined_next = Armory.setTLining(input.copy(), lining_type);
            ShapelessOreRecipe r = new ShapelessOreRecipe((ResourceLocation)null, lined_next, new Object[]{lined_curr, lining});
            recipes.add(r);
            if (max != 1) {
               lined_curr = lined_next;
               lined_next = Armory.setTLining(input.copy(), lining_type.adjust(1 * direction));
               r = new ShapelessOreRecipe((ResourceLocation)null, lined_next, new Object[]{lined_curr, lining});
               recipes.add(r);
               if (max != 2) {
                  lined_curr = lined_next;
                  lined_next = Armory.setTLining(input.copy(), lining_type.adjust(2 * direction));
                  r = new ShapelessOreRecipe((ResourceLocation)null, lined_next, new Object[]{lined_curr, lining});
                  recipes.add(r);
               }
            }
         }
      }

   }

   public static final class Factory implements IRecipeFactory {
      public Factory() {
      }

      public IRecipe parse(JsonContext context, JsonObject json) {
         TLining limit = TLining.NONE;
         String modifiername = JsonUtils.getString(json, "limit", "");
         if (!modifiername.isEmpty()) {
            limit = TLining.findOrNone(modifiername);
         }

         ShapelessOreRecipe impl = ShapelessOreRecipe.factory(context, json);
         ShapelessOreRecipe var6 = new LegacyShapelessOreRecipe((ResourceLocation)null, impl);
         return new TLinedArmorRecipe(limit, var6);
      }
   }
}
