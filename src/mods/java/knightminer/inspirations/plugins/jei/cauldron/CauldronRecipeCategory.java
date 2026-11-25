package knightminer.inspirations.plugins.jei.cauldron;

import java.util.List;
import javax.annotation.Nonnull;
import knightminer.inspirations.library.InspirationsRegistry;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.plugins.jei.JEIPlugin;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.DyeIngredient;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.DyeIngredientRenderer;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.PotionIngredient;
import knightminer.inspirations.plugins.jei.cauldron.ingredient.PotionIngredientRenderer;
import knightminer.inspirations.recipes.InspirationsRecipes;
import knightminer.inspirations.recipes.block.BlockEnhancedCauldron;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IFocus.Mode;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

public class CauldronRecipeCategory implements IRecipeCategory<ICauldronRecipeWrapper> {
   public static final String CATEGORY = Util.prefix("cauldron");
   public static final ResourceLocation BACKGROUND_LOC = Util.getResource("textures/gui/jei/cauldron.png");
   private final IDrawable background;
   public final IDrawable fire;

   public CauldronRecipeCategory(IGuiHelper guiHelper) {
      this.background = guiHelper.createDrawable(BACKGROUND_LOC, 0, 0, 160, 50, 0, 0, 0, 0);
      this.fire = guiHelper.createDrawable(BACKGROUND_LOC, 160, 0, 14, 14);
   }

   @Nonnull
   public String getUid() {
      return CATEGORY;
   }

   @Nonnull
   public String getTitle() {
      return Util.translate("gui.jei.cauldron.title");
   }

   @Nonnull
   public IDrawable getBackground() {
      return this.background;
   }

   public void setRecipe(IRecipeLayout recipeLayout, ICauldronRecipeWrapper recipe, IIngredients ingredients) {
      IGuiItemStackGroup items = recipeLayout.getItemStacks();
      IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
      fluids.addTooltipCallback(CauldronRecipeCategory::onFluidTooltip);
      IGuiIngredientGroup<DyeIngredient> dyes = recipeLayout.getIngredientsGroup(DyeIngredient.class);
      IGuiIngredientGroup<PotionIngredient> potions = recipeLayout.getIngredientsGroup(PotionIngredient.class);
      IFocus<?> focus = recipeLayout.getFocus();
      if (focus != null) {
         Mode mode = focus.getMode() == Mode.INPUT ? Mode.OUTPUT : Mode.INPUT;
         Object value = focus.getValue();
         if (value instanceof ItemStack) {
            ItemStack stack = (ItemStack)value;
            if (stack.getItem() == InspirationsRecipes.dyedWaterBottle) {
               int meta = stack.getMetadata();
               if (meta < 16) {
                  dyes.setOverrideDisplayFocus(JEIPlugin.recipeRegistry.createFocus(mode, new DyeIngredient(EnumDyeColor.byDyeDamage(meta))));
               }
            } else {
               PotionType potion = PotionUtils.getPotionFromItem(stack);
               if (potion != PotionTypes.EMPTY) {
                  potions.setOverrideDisplayFocus(JEIPlugin.recipeRegistry.createFocus(mode, new PotionIngredient(potion)));
               }
            }
         } else if (value instanceof PotionIngredient) {
            ItemStack item = recipe.getPotionItem();
            if (!item.isEmpty()) {
               PotionType potion = ((PotionIngredient)value).getPotion();
               items.setOverrideDisplayFocus(JEIPlugin.recipeRegistry.createFocus(mode, PotionUtils.addPotionToItemStack(item, potion)));
            }
         } else if (value instanceof DyeIngredient) {
            int meta = ((DyeIngredient)value).getDye().getDyeDamage();
            items.setOverrideDisplayFocus(JEIPlugin.recipeRegistry.createFocus(mode, new ItemStack(InspirationsRecipes.dyedWaterBottle, 1, meta)));
         }
      }

      items.init(0, true, 43, 0);
      items.set(ingredients);
      items.init(1, false, 97, 0);
      items.set(ingredients);
      init(fluids, dyes, potions, ingredients, true, 47, 19, recipe.getInputType(), recipe.getInputLevel());
      init(fluids, dyes, potions, ingredients, false, 101, 19, recipe.getOutputType(), recipe.getOutputLevel());
   }

   private static void init(IGuiFluidStackGroup fluids, IGuiIngredientGroup<DyeIngredient> dyes, IGuiIngredientGroup<PotionIngredient> potions, IIngredients ingredients, boolean input, int x, int y, BlockEnhancedCauldron.CauldronContents type, int level) {
      if (type != null) {
         int index = input ? 0 : 1;
         switch(type) {
         case FLUID:
            fluids.init(index, input, x, y, 10, 10, InspirationsRegistry.getCauldronMax(), false, (IDrawable)null);
            fluids.set(ingredients);
            break;
         case DYE:
            dyes.init(index, input, DyeIngredientRenderer.forLevel(level), x, y, 10, 10, 0, 0);
            dyes.set(ingredients);
            break;
         case POTION:
            potions.init(index, input, PotionIngredientRenderer.forLevel(level), x, y, 10, 10, 0, 0);
            potions.set(ingredients);
         }

      }
   }

   private static void onFluidTooltip(int slotIndex, boolean input, FluidStack ingredient, List<String> tooltip) {
      String modName = (String)tooltip.get(tooltip.size() - 1);
      tooltip.clear();
      tooltip.add(ingredient.getLocalizedName());
      addLevelTooltip(ingredient.amount, tooltip);
      tooltip.add(modName);
   }

   public static void addLevelTooltip(int level, List<String> tooltip) {
      if (level == 1) {
         tooltip.add(TextFormatting.GRAY + Util.translateFormatted("gui.jei.cauldron.level.singular"));
      } else if (level > 1) {
         tooltip.add(TextFormatting.GRAY + Util.translateFormatted("gui.jei.cauldron.level", level));
      }

   }

   public String getModName() {
      return "Inspirations";
   }
}
