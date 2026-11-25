package goblinbob.mobends.core.client.gui.popup;

import goblinbob.mobends.core.client.gui.GuiBendsMenu;
import goblinbob.mobends.core.client.gui.elements.GuiCustomButton;
import goblinbob.mobends.core.util.Draw;
import goblinbob.mobends.core.util.GUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class GuiPopUp {
   protected int x;
   protected int y;
   protected int width;
   protected int height;
   private String[] title;
   protected FontRenderer fontRenderer;
   private List<GuiPopUp.Button> buttons;

   public GuiPopUp(String title, GuiPopUp.ButtonProps[] buttonProps) {
      this(title, 120, 60, buttonProps);
   }

   public GuiPopUp(String title, int width, int height, GuiPopUp.ButtonProps[] buttonProps) {
      this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
      this.width = width;
      this.height = height;
      this.title = GUtil.wrapText(this.fontRenderer, title, width - 20);
      this.buttons = new ArrayList();

      for(GuiPopUp.ButtonProps buttonProp : buttonProps) {
         this.buttons.add(new GuiPopUp.Button(this.fontRenderer, buttonProp));
      }

   }

   public void initGui(int x, int y) {
      this.x = x - this.width / 2;
      this.y = y - this.height / 2;
      int offset = this.x + this.width - 5;

      for(GuiPopUp.Button button : this.buttons) {
         button.buttonUI.setPosition(offset - button.buttonUI.width, this.y + this.height - 25);
         offset -= button.buttonUI.width + 5;
      }

   }

   public void update(int mouseX, int mouseY) {
   }

   public void display(int mouseX, int mouseY, float partialTicks) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Minecraft.getMinecraft().getTextureManager().bindTexture(GuiBendsMenu.ICONS_TEXTURE);
      Draw.texturedModalRect(this.x - 4, this.y - 4, 60, 64, 4, 4);
      Draw.texturedModalRect(this.x, this.y - 4, this.width, 4, 64, 64, 1, 4);
      Draw.texturedModalRect(this.x + this.width, this.y - 4, 65, 64, 4, 4);
      Draw.texturedModalRect(this.x + this.width, this.y, 4, this.height, 65, 68, 4, 1);
      Draw.texturedModalRect(this.x + this.width, this.y + this.height, 65, 69, 4, 4);
      Draw.texturedModalRect(this.x, this.y + this.height, this.width, 4, 64, 69, 1, 4);
      Draw.texturedModalRect(this.x - 4, this.y + this.height, 60, 69, 4, 4);
      Draw.texturedModalRect(this.x - 4, this.y, 4, this.height, 60, 68, 4, 1);
      Draw.texturedModalRect(this.x, this.y, this.width, this.height, 64, 68, 1, 1);
      int yOffset = 6;

      for(String line : this.title) {
         this.fontRenderer.drawStringWithShadow(line, (float)(this.x + (this.width - this.fontRenderer.getStringWidth(line)) / 2), (float)(this.y + yOffset), 16777215);
         yOffset += 9;
      }

      for(GuiPopUp.Button button : this.buttons) {
         button.buttonUI.drawButton(mouseX, mouseY, partialTicks);
      }

   }

   public void mouseClicked(int mouseX, int mouseY, int state) {
      for(GuiPopUp.Button button : this.buttons) {
         if (button.buttonUI.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
            button.props.action.performAction();
            return;
         }
      }

   }

   public static class Button {
      public GuiPopUp.ButtonProps props;
      public GuiCustomButton buttonUI;

      public Button(FontRenderer fontRenderer, GuiPopUp.ButtonProps props) {
         this.props = props;
         int labelWidth = fontRenderer.getStringWidth(props.label);
         this.buttonUI = new GuiCustomButton(Math.max(labelWidth + 10, 50), 20, props.label);
      }
   }

   @FunctionalInterface
   public interface ButtonAction {
      void performAction();
   }

   public static class ButtonProps {
      private String label;
      private GuiPopUp.ButtonAction action;

      public ButtonProps(String label, GuiPopUp.ButtonAction action) {
         this.label = label;
         this.action = action;
      }
   }
}
