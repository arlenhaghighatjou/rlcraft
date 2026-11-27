package mezz.jei.gui.overlay.bookmarks;

import com.arlenh7.eaglercraft.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import mezz.jei.api.gui.IGuiProperties;
import mezz.jei.gui.GuiScreenHelper;
import mezz.jei.gui.PageNavigation;
import mezz.jei.gui.overlay.GuiProperties;
import mezz.jei.input.IClickedIngredient;
import mezz.jei.input.IPaged;
import mezz.jei.input.IShowsRecipeFocuses;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;

public class LeftAreaDispatcher implements IShowsRecipeFocuses, IPaged {
   private static final int BORDER_PADDING = 2;
   private static final int NAVIGATION_HEIGHT = 20;
   private final List<ILeftAreaContent> contents = new ArrayList();
   private final GuiScreenHelper guiScreenHelper;
   private int current = 0;
   @Nullable
   private IGuiProperties guiProperties;
   private Rectangle naviArea = new Rectangle();
   private Rectangle displayArea = new Rectangle();
   private final PageNavigation navigation;
   private boolean canShow = false;

   public LeftAreaDispatcher(GuiScreenHelper guiScreenHelper) {
      this.guiScreenHelper = guiScreenHelper;
      this.navigation = new PageNavigation(this, false);
   }

   public void addContent(ILeftAreaContent content) {
      this.contents.add(content);
   }

   private boolean hasContent() {
      return this.current >= 0 && this.current < this.contents.size();
   }

   public void drawScreen(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
      if (this.canShow && this.hasContent()) {
         ((ILeftAreaContent)this.contents.get(this.current)).drawScreen(minecraft, mouseX, mouseY, partialTicks);
         if (this.naviArea.height > 0) {
            this.navigation.draw(minecraft, mouseX, mouseY, partialTicks);
         }
      }

   }

   public void drawOnForeground(GuiContainer gui, int mouseX, int mouseY) {
      if (this.canShow && this.hasContent()) {
         ((ILeftAreaContent)this.contents.get(this.current)).drawOnForeground(gui, mouseX, mouseY);
      }

   }

   public void drawTooltips(Minecraft minecraft, int mouseX, int mouseY) {
      if (this.canShow && this.hasContent()) {
         ((ILeftAreaContent)this.contents.get(this.current)).drawTooltips(minecraft, mouseX, mouseY);
      }

   }

   public void updateScreen(@Nullable GuiScreen guiScreen, boolean forceUpdate) {
      this.canShow = false;
      if (this.hasContent()) {
         IGuiProperties currentGuiProperties = this.guiScreenHelper.getGuiProperties(guiScreen);
         if (currentGuiProperties == null) {
            this.guiProperties = null;
         } else {
            ILeftAreaContent content = (ILeftAreaContent)this.contents.get(this.current);
            if (!forceUpdate && GuiProperties.areEqual(this.guiProperties, currentGuiProperties)) {
               this.canShow = true;
            } else {
               Set<Rectangle> guiExclusionAreas = this.guiScreenHelper.getGuiExclusionAreas();
               this.guiProperties = currentGuiProperties;
               this.makeDisplayArea(this.guiProperties);
               content.updateBounds(this.displayArea, guiExclusionAreas);
               this.canShow = true;
            }
         }
      }

   }

   private void makeDisplayArea(IGuiProperties guiProperties) {
      int x = 2;
      int y = 2;
      int width = guiProperties.getGuiLeft() - 2 - 2;
      int height = guiProperties.getScreenHeight() - 2 - 2;
      this.displayArea = new Rectangle(2, 2, width, height);
      if (this.contents.size() > 1) {
         this.naviArea = new Rectangle(this.displayArea);
         this.naviArea.height = 20;
         this.displayArea.y += 22;
         this.displayArea.height -= 22;
         this.navigation.updateBounds(this.naviArea);
      } else {
         this.naviArea = new Rectangle();
      }

   }

   @Nullable
   public IClickedIngredient<?> getIngredientUnderMouse(int mouseX, int mouseY) {
      return this.canShow && this.hasContent() ? ((ILeftAreaContent)this.contents.get(this.current)).getIngredientUnderMouse(mouseX, mouseY) : null;
   }

   public boolean canSetFocusWithMouse() {
      return this.canShow && this.hasContent() ? ((ILeftAreaContent)this.contents.get(this.current)).canSetFocusWithMouse() : false;
   }

   public boolean handleMouseScrolled(int mouseX, int mouseY, int dWheel) {
      if (this.canShow && this.hasContent()) {
         if (this.displayArea.contains(mouseX, mouseY)) {
            return ((ILeftAreaContent)this.contents.get(this.current)).handleMouseScrolled(mouseX, mouseY, dWheel);
         }

         if (this.naviArea.contains(mouseX, mouseY)) {
            if (dWheel < 0) {
               this.nextPage();
            } else {
               this.previousPage();
            }

            return true;
         }
      }

      return false;
   }

   public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (this.canShow && this.hasContent()) {
         if (this.displayArea.contains(mouseX, mouseY)) {
            return ((ILeftAreaContent)this.contents.get(this.current)).handleMouseClicked(mouseX, mouseY, mouseButton);
         }

         if (this.naviArea.contains(mouseX, mouseY)) {
            return this.navigation.handleMouseClickedButtons(mouseX, mouseY);
         }
      }

      return false;
   }

   public boolean nextPage() {
      ++this.current;
      if (this.current >= this.contents.size()) {
         this.current = 0;
      }

      this.navigation.updatePageState();
      return true;
   }

   public boolean previousPage() {
      --this.current;
      if (this.current < 0) {
         this.current = this.contents.size();
      }

      this.navigation.updatePageState();
      return true;
   }

   public boolean hasNext() {
      return this.current < this.contents.size() - 1;
   }

   public boolean hasPrevious() {
      return this.current > 0;
   }

   public int getPageCount() {
      return this.contents.size();
   }

   public int getPageNumber() {
      return this.current;
   }
}
