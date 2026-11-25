package vazkii.quark.client.gui;

import com.google.common.base.Predicate;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.text.TextComponentString;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import vazkii.quark.client.feature.ImprovedSignEdit;

public class GuiBetterEditSign extends GuiScreen {
   public final TileEntitySign sign;
   private static int focusedField = 0;
   private List<GuiTextField> textFields;
   private String[] defaultStrings;

   public GuiBetterEditSign(TileEntitySign sign) {
      this.sign = sign;
   }

   public void initGui() {
      this.buttonList.clear();
      Keyboard.enableRepeatEvents(true);
      this.textFields = new LinkedList();
      this.defaultStrings = new String[4];

      for(int i = 0; i < 4; ++i) {
         GuiTextField field = new GuiTextField(i, this.fontRenderer, this.width / 2 + 4, 75 + i * 24, 120, 20);
         field.setValidator(this::validateText);
         field.setMaxStringLength(100);
         String text = this.sign.signText[i].getUnformattedText();
         this.defaultStrings[i] = text;
         field.setText(text);
         this.textFields.add(field);
      }

      ((GuiTextField)this.textFields.get(focusedField)).setFocused(true);
      if (ImprovedSignEdit.enableCancel) {
         this.addButton(new GuiButton(4, this.width / 2 + 5, this.height / 4 + 120, 120, 20, I18n.format("gui.done", new Object[0])));
         this.addButton(new GuiButton(5, this.width / 2 - 125, this.height / 4 + 120, 120, 20, I18n.format("gui.cancel", new Object[0])));
      } else {
         this.addButton(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 120, I18n.format("gui.done", new Object[0])));
      }

      if (ImprovedSignEdit.enableShift) {
         this.addButton(new GuiButton(6, this.width / 2 - 41, 147, 40, 20, I18n.format("quarkmisc.signShift", new Object[0])));
      }

      if (ImprovedSignEdit.enableClear) {
         this.addButton(new GuiButton(7, this.width / 2 - 41, ImprovedSignEdit.enableShift ? 123 : 147, 40, 20, I18n.format("quarkmisc.signClear", new Object[0])));
      }

      this.sign.setEditable(false);
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      int currentFocus = focusedField;
      this.textFields.forEach((field) -> {
         field.mouseClicked(mouseX, mouseY, mouseButton);
      });
      this.updateFocusField();
      if (focusedField == currentFocus && !((GuiTextField)this.textFields.get(focusedField)).isFocused()) {
         ((GuiTextField)this.textFields.get(focusedField)).setFocused(true);
      }

   }

   protected void keyTyped(char typedChar, int keyCode) {
      switch(keyCode) {
      case 1:
         this.exit();
         break;
      case 15:
         int change = isShiftKeyDown() ? -1 : 1;
         this.tabFocus(change);
         break;
      case 28:
      case 156:
      case 208:
         this.tabFocus(1);
      default:
         this.textFields.forEach((field) -> {
            field.textboxKeyTyped(typedChar, keyCode);
         });
         this.sign.signText[focusedField] = new TextComponentString(((GuiTextField)this.textFields.get(focusedField)).getText());
         break;
      case 200:
         this.tabFocus(-1);
      }

   }

   protected void actionPerformed(GuiButton button) throws IOException {
      super.actionPerformed(button);
      switch(button.id) {
      case 5:
         for(int i = 0; i < 4; ++i) {
            this.sign.signText[i] = new TextComponentString(this.defaultStrings[i]);
         }
      case 4:
         this.exit();
         break;
      case 6:
         String[] replacements = new String[4];

         for(int i = 0; i < 4; ++i) {
            int change = isShiftKeyDown() ? 1 : -1;
            int target = this.wrapLine(i + change);
            replacements[i] = this.sign.signText[target].getUnformattedText();
         }

         this.textFields.forEach((field) -> {
            int id = field.getId();
            field.setText(replacements[id]);
            this.sign.signText[id] = new TextComponentString(replacements[id]);
         });
         break;
      case 7:
         this.textFields.forEach((field) -> {
            int id = field.getId();
            field.setText("");
            this.sign.signText[id] = new TextComponentString("");
         });
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.format("sign.edit", new Object[0]), this.width / 2, 40, 16777215);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)(this.width / 2) - 63.0F, 0.0F, 50.0F);
      float f = 93.75F;
      GlStateManager.scale(-93.75F, -93.75F, -93.75F);
      GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
      Block block = this.sign.getBlockType();
      if (block == Blocks.STANDING_SIGN) {
         float f1 = (float)(this.sign.getBlockMetadata() * 360) / 16.0F;
         GlStateManager.rotate(f1, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0F, -1.0625F, 0.0F);
      } else {
         int i = this.sign.getBlockMetadata();
         float f2 = 0.0F;
         if (i == 2) {
            f2 = 180.0F;
         }

         if (i == 4) {
            f2 = 90.0F;
         }

         if (i == 5) {
            f2 = -90.0F;
         }

         GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0F, -0.7625F, 0.0F);
      }

      this.sign.lineBeingEdited = -1;
      TileEntityRendererDispatcher.instance.render(this.sign, -0.5D, -0.75D, -0.5D, 0.0F);
      GlStateManager.popMatrix();
      this.textFields.forEach(GuiTextField::drawTextBox);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   void updateFocusField() {
      this.textFields.forEach((field) -> {
         if (field.isFocused()) {
            focusedField = field.getId();
         }

      });
   }

   void exit() {
      this.sign.markDirty();
      this.mc.displayGuiScreen((GuiScreen)null);
   }

   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
      NetHandlerPlayClient nethandlerplayclient = this.mc.getConnection();
      if (nethandlerplayclient != null) {
         nethandlerplayclient.sendPacket(new CPacketUpdateSign(this.sign.getPos(), this.sign.signText));
      }

      this.sign.setEditable(true);
   }

   void tabFocus(int change) {
      ((GuiTextField)this.textFields.get(focusedField)).setFocused(false);
      focusedField = this.wrapLine(focusedField + change);
      ((GuiTextField)this.textFields.get(focusedField)).setFocused(true);
   }

   int wrapLine(int line) {
      if (line > 3) {
         return 0;
      } else {
         return line < 0 ? 3 : line;
      }
   }

   boolean validateText(String s) {
      if (this.fontRenderer.getStringWidth(s) > 90) {
         return false;
      } else {
         char[] arr = s.toCharArray();

         for(char c : arr) {
            if (!ChatAllowedCharacters.isAllowedCharacter(c)) {
               return false;
            }
         }

         return true;
      }
   }
}
