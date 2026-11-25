package noppes.vc.shared.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiTextFieldNop extends GuiTextField {
   public boolean enabled;
   public boolean inMenu;
   public boolean numbersOnly;
   private GuiTextFieldNop.ITextfieldListener listener;
   public int id;
   public int min;
   public int max;
   public int def;
   private static GuiTextFieldNop activeTextfield = null;
   public boolean canEdit;
   private final int[] allowedSpecialChars;

   public GuiTextFieldNop(int id, GuiScreen parent, FontRenderer fontRenderer, int i, int j, int k, int l, String s) {
      super(id, fontRenderer, i, j, k, l);
      this.enabled = true;
      this.inMenu = true;
      this.numbersOnly = false;
      this.min = 0;
      this.max = Integer.MAX_VALUE;
      this.def = 0;
      this.canEdit = true;
      this.allowedSpecialChars = new int[]{14, 211, 203, 205};
      this.setMaxStringLength(500);
      this.setText(s == null ? "" : s);
      this.id = id;
      if (parent instanceof GuiTextFieldNop.ITextfieldListener) {
         this.listener = (GuiTextFieldNop.ITextfieldListener)parent;
      }

   }

   public static boolean isActive() {
      return activeTextfield != null;
   }

   public GuiTextFieldNop(int id, GuiScreen parent, int i, int j, int k, int l, String s) {
      this(id, parent, Minecraft.getMinecraft().fontRenderer, i, j, k, l, s);
   }

   private boolean charAllowed(char c, int i) {
      if (this.numbersOnly && !Character.isDigit(c) && (c != '-' || this.getText().length() != 0)) {
         for(int j : this.allowedSpecialChars) {
            if (j == i) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public boolean textboxKeyTyped(char c, int i) {
      return this.charAllowed(c, i) && this.canEdit ? super.textboxKeyTyped(c, i) : false;
   }

   public boolean isEmpty() {
      return this.getText().trim().length() == 0;
   }

   public int getInteger() {
      return Integer.parseInt(this.getText());
   }

   public boolean isInteger() {
      try {
         Integer.parseInt(this.getText());
         return true;
      } catch (NumberFormatException var2) {
         return false;
      }
   }

   public boolean mouseClicked(int i, int j, int k) {
      if (!this.canEdit) {
         return false;
      } else {
         boolean wasFocused = this.isFocused();
         boolean clicked = super.mouseClicked(i, j, k);
         if (wasFocused != this.isFocused() && wasFocused) {
            this.unFocused();
         }

         if (this.isFocused()) {
            activeTextfield = this;
         }

         return clicked;
      }
   }

   public void unFocused() {
      if (this.numbersOnly) {
         if (!this.isEmpty() && this.isInteger()) {
            if (this.getInteger() < this.min) {
               this.setText(this.min + "");
            } else if (this.getInteger() > this.max) {
               this.setText(this.max + "");
            }
         } else {
            this.setText(this.def + "");
         }
      }

      if (this.listener != null) {
         this.listener.unFocused(this);
      }

      if (this == activeTextfield) {
         activeTextfield = null;
      }

   }

   public void drawTextBox() {
      if (this.enabled) {
         super.drawTextBox();
      }

   }

   public void setMinMaxDefault(int i, int j, int k) {
      this.min = i;
      this.max = j;
      this.def = k;
   }

   public static void unfocus() {
      GuiTextFieldNop prev = activeTextfield;
      activeTextfield = null;
      if (prev != null) {
         prev.unFocused();
      }

   }

   public void drawTextBox(int mousX, int mousY) {
      this.drawTextBox();
   }

   public GuiTextFieldNop setNumbersOnly() {
      this.numbersOnly = true;
      return this;
   }

   public interface ITextfieldListener {
      void unFocused(GuiTextFieldNop var1);
   }
}
