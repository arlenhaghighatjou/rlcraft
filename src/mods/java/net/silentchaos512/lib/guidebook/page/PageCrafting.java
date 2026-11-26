package net.silentchaos512.lib.guidebook.page;

import java.util.Arrays;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.silentchaos512.lib.SilentLib;
import net.silentchaos512.lib.guidebook.GuideBook;
import net.silentchaos512.lib.guidebook.internal.GuiGuideBase;

public class PageCrafting extends GuidePage {
   private final List<IRecipe> recipes;
   private int recipeAt;
   private String recipeTypeLocKey;
   private boolean isWildcard;

   public PageCrafting(GuideBook book, int localizationKey, int priority, List<IRecipe> recipes) {
      super(book, localizationKey, priority);
      this.recipes = recipes;
   }

   public PageCrafting(GuideBook book, int localizationKey, List<IRecipe> recipes) {
      this(book, localizationKey, 0, recipes);
   }

   public PageCrafting(GuideBook book, int localizationKey, IRecipe... recipes) {
      this(book, localizationKey, 0, recipes);
   }

   public PageCrafting(GuideBook book, int localizationKey, int priority, IRecipe... recipes) {
      this(book, localizationKey, priority, Arrays.asList(recipes));
   }

   public GuidePage setWildcard() {
      this.isWildcard = true;
      return this;
   }

   @SideOnly(Side.CLIENT)
   public void drawScreenPre(GuiGuideBase gui, int startX, int startY, int mouseX, int mouseY, float partialTicks) {
      super.drawScreenPre(gui, startX, startY, mouseX, mouseY, partialTicks);
      gui.mc.getTextureManager().bindTexture(this.book.getResourceGadgets());
      GuiUtils.drawTexturedModalRect(startX + 5, startY + 6, 20, 0, 116, 54, 0.0F);
      gui.renderScaledAsciiString("(" + this.book.i18n.translate(this.recipeTypeLocKey) + ")", startX + 6, startY + 65, 0, false, gui.getMediumFontSize());
      PageTextOnly.renderTextToPage(gui, this, startX + 6, startY + 80);
   }

   @SideOnly(Side.CLIENT)
   public void updateScreen(GuiGuideBase gui, int startX, int startY, int pageTimer) {
      super.updateScreen(gui, startX, startY, pageTimer);
      if (pageTimer % 20 == 0) {
         this.findRecipe(gui, startX, startY);
      }

   }

   private void findRecipe(GuiGuideBase gui, int startX, int startY) {
      if (!this.recipes.isEmpty()) {
         IRecipe recipe = (IRecipe)this.recipes.get(this.recipeAt);
         if (recipe != null) {
            this.setupRecipe(gui, recipe, startX, startY);
         }

         ++this.recipeAt;
         if (this.recipeAt >= this.recipes.size()) {
            this.recipeAt = 0;
         }
      }

   }

   public void initGui(GuiGuideBase gui, int startX, int startY) {
      super.initGui(gui, startX, startY);
      this.findRecipe(gui, startX, startY);
   }

   public void getItemStacksForPage(List<ItemStack> list) {
      super.getItemStacksForPage(list);
      if (!this.recipes.isEmpty()) {
         for(IRecipe recipe : this.recipes) {
            if (recipe != null) {
               ItemStack output = recipe.getRecipeOutput();
               if (!output.isEmpty()) {
                  ItemStack copy = output.copy();
                  if (this.isWildcard) {
                     copy.setItemDamage(32767);
                  }

                  list.add(copy);
               }
            }
         }
      }

   }

   private void setupRecipe(GuiGuideBase gui, IRecipe recipe, int startX, int startY) {
      NonNullList<ItemStack> stacks = NonNullList.withSize(9, ItemStack.EMPTY);
      int width = 3;
      int height = 3;
      if (recipe instanceof ShapedRecipes) {
         ShapedRecipes shaped = (ShapedRecipes)recipe;
         width = shaped.recipeWidth;
         height = shaped.recipeHeight;

         for(int i = 0; i < shaped.recipeItems.size(); ++i) {
            Ingredient ing = (Ingredient)shaped.recipeItems.get(i);
            stacks.set(i, ing.getMatchingStacks().length > 0 ? ing.getMatchingStacks()[0] : ItemStack.EMPTY);
         }

         this.recipeTypeLocKey = SilentLib.i18n.getKey("guide", "shapedRecipe");
      } else if (recipe instanceof ShapelessRecipes) {
         ShapelessRecipes shapeless = (ShapelessRecipes)recipe;

         for(int i = 0; i < shapeless.recipeItems.size(); ++i) {
            Ingredient ing = (Ingredient)shapeless.recipeItems.get(i);
            stacks.set(i, ing.getMatchingStacks().length > 0 ? ing.getMatchingStacks()[0] : ItemStack.EMPTY);
         }

         this.recipeTypeLocKey = SilentLib.i18n.getKey("guide", "shapelessRecipe");
      } else if (recipe instanceof ShapedOreRecipe) {
         ShapedOreRecipe shaped = (ShapedOreRecipe)recipe;

         try {
            width = shaped.getRecipeWidth();
            height = shaped.getRecipeHeight();
         } catch (Exception var12) {
            SilentLib.LOGGER.warn("Something went wrong trying to get the Crafting Recipe in the booklet to display!");
            var12.printStackTrace();
         }

         for(int i = 0; i < shaped.getIngredients().size(); ++i) {
            Ingredient input = (Ingredient)shaped.getIngredients().get(i);
            stacks.set(i, input.getMatchingStacks().length > 0 ? input.getMatchingStacks()[0] : ItemStack.EMPTY);
         }

         this.recipeTypeLocKey = SilentLib.i18n.getKey("guide", "shapedOreRecipe");
      } else if (recipe instanceof ShapelessOreRecipe) {
         ShapelessOreRecipe shapeless = (ShapelessOreRecipe)recipe;

         for(int i = 0; i < shapeless.getIngredients().size(); ++i) {
            Ingredient input = (Ingredient)shapeless.getIngredients().get(i);
            stacks.set(i, input.getMatchingStacks().length > 0 ? input.getMatchingStacks()[0] : ItemStack.EMPTY);
         }

         this.recipeTypeLocKey = SilentLib.i18n.getKey("guide", "shapelessOreRecipe");
      }

      for(int x = 0; x < width; ++x) {
         for(int y = 0; y < height; ++y) {
            ItemStack stack = (ItemStack)stacks.get(y * width + x);
            if (!stack.isEmpty()) {
               ItemStack copy = stack.copy();
               copy.setCount(1);
               if (copy.getItemDamage() == 32767) {
                  copy.setItemDamage(0);
               }

               gui.addOrModifyItemRenderer(copy, startX + 6 + x * 18, startY + 7 + y * 18, 1.0F, true);
            }
         }
      }

      gui.addOrModifyItemRenderer(recipe.getRecipeOutput(), startX + 100, startY + 25, 1.0F, false);
   }
}
