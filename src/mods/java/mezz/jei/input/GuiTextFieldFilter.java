package mezz.jei.input;

import com.arlenh7.eaglercraft.awt.Color;
import com.arlenh7.eaglercraft.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;
import mezz.jei.Internal;
import mezz.jei.config.Config;
import mezz.jei.config.KeyBindings;
import mezz.jei.gui.elements.DrawableNineSliceTexture;
import mezz.jei.gui.ingredients.IIngredientListElement;
import mezz.jei.ingredients.IngredientFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraftforge.fml.client.config.HoverChecker;
import net.lax1dude.eaglercraft.v1_8.Keyboard;

public class GuiTextFieldFilter extends GuiTextField {
   private static final int MAX_HISTORY = 100;
   private static final int maxSearchLength = 128;
   private static final List<String> history = new LinkedList();
   private final HoverChecker hoverChecker;
   private final IngredientFilter ingredientFilter;
   private boolean previousKeyboardRepeatEnabled;
   private final DrawableNineSliceTexture background;
   private boolean isDrawing = false;

   public GuiTextFieldFilter(int componentId, IngredientFilter ingredientFilter) {
      super(componentId, Minecraft.getMinecraft().fontRenderer, 0, 0, 0, 0);
      this.setMaxStringLength(128);
      this.hoverChecker = new HoverChecker(0, 0, 0, 0, 0);
      this.ingredientFilter = ingredientFilter;
      this.background = Internal.getHelpers().getGuiHelper().getSearchBackground();
   }

   public void updateBounds(Rectangle area) {
      this.x = area.x;
      this.y = area.y;
      this.width = area.width;
      this.height = area.height;
      this.hoverChecker.updateBounds(area.y, area.y + area.height, area.x, area.x + area.width);
      this.setSelectionPos(this.getCursorPosition());
   }

   public void update() {
      String filterText = Config.getFilterText();
      if (!filterText.equals(this.getText())) {
         this.setText(filterText);
      }

      List<IIngredientListElement> ingredientList = this.ingredientFilter.getIngredientList();
      if (ingredientList.size() == 0) {
         this.setTextColor(Color.red.getRGB());
      } else {
         this.setTextColor(Color.white.getRGB());
      }

   }

   public boolean textboxKeyTyped(char typedChar, int keyCode) {
      boolean handled = super.textboxKeyTyped(typedChar, keyCode);
      if (!handled && !history.isEmpty()) {
         if (keyCode == 200) {
            String currentText = this.getText();
            int historyIndex = history.indexOf(currentText);
            if (historyIndex < 0) {
               if (this.saveHistory()) {
                  historyIndex = history.size() - 1;
               } else {
                  historyIndex = history.size();
               }
            }

            if (historyIndex > 0) {
               String historyString = (String)history.get(historyIndex - 1);
               this.setText(historyString);
               handled = true;
            }
         } else if (keyCode == 208) {
            String currentText = this.getText();
            int historyIndex = history.indexOf(currentText);
            if (historyIndex >= 0) {
               String historyString;
               if (historyIndex + 1 < history.size()) {
                  historyString = (String)history.get(historyIndex + 1);
               } else {
                  historyString = "";
               }

               this.setText(historyString);
               handled = true;
            }
         } else if (KeyBindings.isEnterKey(keyCode)) {
            this.saveHistory();
         }
      }

      return handled;
   }

   public boolean isMouseOver(int mouseX, int mouseY) {
      return this.hoverChecker.checkHover(mouseX, mouseY);
   }

   public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
      if (mouseButton == 1) {
         this.setText("");
         return Config.setFilterText("");
      } else {
         super.mouseClicked(mouseX, mouseY, mouseButton);
         return false;
      }
   }

   public void setFocused(boolean keyboardFocus) {
      boolean previousFocus = this.isFocused();
      super.setFocused(keyboardFocus);
      if (previousFocus != keyboardFocus) {
         if (keyboardFocus) {
            this.previousKeyboardRepeatEnabled = Keyboard.areRepeatEventsEnabled();
            Keyboard.enableRepeatEvents(true);
         } else {
            Keyboard.enableRepeatEvents(this.previousKeyboardRepeatEnabled);
         }

         this.saveHistory();
      }

   }

   private boolean saveHistory() {
      String text = this.getText();
      if (text.length() > 0) {
         history.remove(text);
         history.add(text);
         if (history.size() > 100) {
            history.remove(0);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean getEnableBackgroundDrawing() {
      if (this.isDrawing) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         Minecraft minecraft = Minecraft.getMinecraft();
         this.background.draw(minecraft, this.x, this.y, this.width, this.height);
      }

      return false;
   }

   public int getWidth() {
      return this.width - 8;
   }

   public void drawTextBox() {
      this.isDrawing = true;
      super.drawTextBox();
      this.isDrawing = false;
   }
}
