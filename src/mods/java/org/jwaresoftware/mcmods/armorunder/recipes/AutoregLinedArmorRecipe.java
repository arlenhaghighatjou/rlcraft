package org.jwaresoftware.mcmods.armorunder.recipes;

import com.google.gson.JsonObject;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.jwaresoftware.mcmods.armorunder.ModItems;
import org.jwaresoftware.mcmods.lib.Armory;
import org.jwaresoftware.mcmods.lib.ItemStacks;
import org.jwaresoftware.mcmods.lib.Strings;
import org.jwaresoftware.mcmods.lib.Armory.XLining;
import org.jwaresoftware.mcmods.lib.Armory.XLining.Def;
import org.jwaresoftware.mcmods.lib.recipes.JR.LegacyShapelessOreRecipe;

public final class AutoregLinedArmorRecipe extends LinedArmorRecipeSkeleton {
   static final String DICT_KEY_LINERS = "itemAutoregLining";
   private final int _modifier;

   static final void initSharedDictIds() {
      OreDictionary.registerOre("itemAutoregLining", ModItems.auto_chestplate_liner);
      OreDictionary.registerOre("itemAutoregLining", ModItems.auto_leggings_liner);
   }

   static final List<IRecipe> addSampleRecipes(List<IRecipe> recipes) {
      if (ArmorRecipes.ENABLED) {
         addRecipes("itemChestplate", ModItems.auto_chestplate_liner, recipes, "chestplate");
         addRecipes("itemLeggings", ModItems.auto_leggings_liner, recipes, "leggings");
      }

      return recipes;
   }

   AutoregLinedArmorRecipe(int modifier, IRecipe template) {
      super(template);
      this._modifier = modifier;
   }

   public static final boolean isAutoregLining(Item item) {
      return item == ModItems.auto_chestplate_liner || item == ModItems.auto_leggings_liner;
   }

   protected boolean isLining(Item item) {
      return isAutoregLining(item);
   }

   protected boolean canAddLining(ItemStack armorpiece) {
      return !Armory.hasLining(armorpiece);
   }

   static final int normalizeOverride(int override) {
      return override == 0 ? 0 : (override < 0 ? -1 : 1);
   }

   static final int getTargetModifier(ItemStack lining, int defaultModifier) {
      int modifier = defaultModifier;
      if (lining.hasTagCompound() && lining.getTagCompound().hasKey(NBT_MOD)) {
         int override = lining.getTagCompound().getInteger(NBT_MOD);
         modifier = normalizeOverride(override);
      }

      return modifier;
   }

   static final void setTargetModifier(ItemStack lining, int override) {
      int modifier = normalizeOverride(override);
      ItemStacks.getTagCompoundNonNull(lining).setInteger(NBT_MOD, modifier);
   }

   protected Def getXLining(ItemStack lining) {
      int target = getTargetModifier(lining, this._modifier);
      return new Def(XLining.TEMPERATURE_REGULATOR, target);
   }

   @Nullable
   public static final String getLiningModifierTooltip(ItemStack lining) {
      String value = null;
      int modifier = getTargetModifier(lining, 0);
      if (modifier != 0) {
         value = Strings.translate("tooltip.auw.xlining.ozzy." + (modifier < 0 ? "cooler" : "warmer"));
      }

      return value;
   }

   private static final void addRecipes(String shared_key, Item ozzy_liner, List<IRecipe> recipes, String rid) {
      Def defn = new Def(XLining.TEMPERATURE_REGULATOR, 0);
      ItemStack liner = new ItemStack(ozzy_liner);

      for(ItemStack unlined : OreDictionary.getOres(shared_key, false)) {
         ItemStack lined = ItemStacks.copyItemStackSingle(unlined);
         Armory.setXLining(lined, defn);
         recipes.add(new ShapelessOreRecipe((ResourceLocation)null, lined, new Object[]{unlined, liner}));
      }

   }

   public static final class Factory implements IRecipeFactory {
      public Factory() {
      }

      public IRecipe parse(JsonContext context, JsonObject json) {
         int modifier = JsonUtils.getInt(json, "modifier", 0);
         ShapelessOreRecipe impl = ShapelessOreRecipe.factory(context, json);
         ShapelessOreRecipe var5 = new LegacyShapelessOreRecipe((ResourceLocation)null, impl);
         return new AutoregLinedArmorRecipe(modifier, var5);
      }
   }
}
