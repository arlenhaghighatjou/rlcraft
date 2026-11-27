package mezz.jei.gui.recipes;

import com.google.common.collect.ImmutableList;
import com.arlenh7.eaglercraft.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.gui.PageNavigation;
import mezz.jei.gui.TooltipRenderer;
import mezz.jei.input.IMouseHandler;
import mezz.jei.input.IPaged;
import mezz.jei.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class RecipeGuiTabs implements IMouseHandler, IPaged {
   private final IRecipeGuiLogic recipeGuiLogic;
   private final List<RecipeGuiTab> tabs = new ArrayList();
   private final PageNavigation pageNavigation;
   private final Rectangle area = new Rectangle();
   private int pageCount = 1;
   private int pageNumber = 0;
   private int categoriesPerPage = 1;

   public RecipeGuiTabs(IRecipeGuiLogic recipeGuiLogic) {
      this.recipeGuiLogic = recipeGuiLogic;
      this.pageNavigation = new PageNavigation(this, true);
   }

   public void initLayout(RecipesGui recipesGui) {
      ImmutableList<IRecipeCategory> categories = this.recipeGuiLogic.getRecipeCategories();
      if (!categories.isEmpty()) {
         int totalWidth = 0;
         this.categoriesPerPage = 0;

         for(int i = 0; i < categories.size() && totalWidth + 24 <= recipesGui.getXSize() - 4; ++i) {
            totalWidth += 24;
            ++this.categoriesPerPage;
         }

         this.area.width = totalWidth;
         this.area.height = 24;
         this.area.x = recipesGui.getGuiLeft() + 2;
         this.area.y = recipesGui.getGuiTop() - 24 + 3;
         this.pageCount = MathUtil.divideCeil(categories.size(), this.categoriesPerPage);
         IRecipeCategory currentCategory = this.recipeGuiLogic.getSelectedRecipeCategory();
         int categoryIndex = categories.indexOf(currentCategory);
         this.pageNumber = categoryIndex / this.categoriesPerPage;
         Rectangle navigationArea = new Rectangle(this.area);
         navigationArea.height = 20;
         navigationArea.translate(0, -(2 + navigationArea.height));
         this.pageNavigation.updateBounds(navigationArea);
         this.updateLayout();
      }

   }

   private void updateLayout() {
      this.tabs.clear();
      ImmutableList<IRecipeCategory> categories = this.recipeGuiLogic.getRecipeCategories();
      int tabX = this.area.x;
      int startIndex = this.pageNumber * this.categoriesPerPage;

      for(int i = 0; i < this.categoriesPerPage; ++i) {
         int index = i + startIndex;
         if (index >= categories.size()) {
            break;
         }

         IRecipeCategory category = (IRecipeCategory)categories.get(index);
         RecipeGuiTab tab = new RecipeCategoryTab(this.recipeGuiLogic, category, tabX, this.area.y);
         this.tabs.add(tab);
         tabX += 24;
      }

      this.pageNavigation.updatePageState();
   }

   public void draw(Minecraft minecraft, int mouseX, int mouseY) {
      IRecipeCategory selectedCategory = this.recipeGuiLogic.getSelectedRecipeCategory();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      RecipeGuiTab hovered = null;
      GlStateManager.disableDepth();
      GlStateManager.enableAlpha();

      for(RecipeGuiTab tab : this.tabs) {
         boolean selected = tab.isSelected(selectedCategory);
         tab.draw(minecraft, selected, mouseX, mouseY);
         if (tab.isMouseOver(mouseX, mouseY)) {
            hovered = tab;
         }
      }

      GlStateManager.disableAlpha();
      GlStateManager.enableDepth();
      this.pageNavigation.draw(minecraft, mouseX, mouseY, minecraft.getRenderPartialTicks());
      if (hovered != null) {
         List<String> tooltip = hovered.getTooltip();
         TooltipRenderer.drawHoveringText(minecraft, tooltip, mouseX, mouseY);
      }

   }

   public boolean isMouseOver(int mouseX, int mouseY) {
      return this.area.contains(mouseX, mouseY) || this.pageNavigation.isMouseOver();
   }

   public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 0) {
         for(RecipeGuiTab tab : this.tabs) {
            if (tab.isMouseOver(mouseX, mouseY)) {
               tab.handleMouseClicked(mouseX, mouseY, mouseButton);
               return true;
            }
         }

         if (this.pageNavigation.isMouseOver()) {
            return this.pageNavigation.handleMouseClickedButtons(mouseX, mouseY);
         }
      }

      return false;
   }

   public boolean handleMouseScrolled(int mouseX, int mouseY, int scrollDelta) {
      return false;
   }

   public boolean nextPage() {
      if (this.hasNext()) {
         ++this.pageNumber;
      } else {
         this.pageNumber = 0;
      }

      this.updateLayout();
      return true;
   }

   public boolean hasNext() {
      return this.pageNumber + 1 < this.pageCount;
   }

   public boolean previousPage() {
      if (this.hasPrevious()) {
         --this.pageNumber;
      } else {
         this.pageNumber = this.pageCount - 1;
      }

      this.updateLayout();
      return true;
   }

   public boolean hasPrevious() {
      return this.pageNumber > 0;
   }

   public int getPageCount() {
      return this.pageCount;
   }

   public int getPageNumber() {
      return this.pageNumber;
   }
}
