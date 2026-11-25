package com.creativemd.creativecore.common.gui.controls.gui;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import java.util.ArrayList;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.LogicOp;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.ArrayUtils;

public class GuiTextfield extends GuiFocusControl {
   public String text;
   public int cursorPosition;
   private int scrollOffset;
   public int maxLength;
   public int selEnd;
   public char[] allowedChars;
   private int cursorCounter;
   private int enabledColor;
   private int disabledColor;

   public GuiTextfield(String text, int x, int y, int width) {
      this(text, x, y, width, 16);
   }

   public GuiTextfield(String text, int x, int y, int width, int height) {
      this(text, text, x, y, width, height);
   }

   public GuiTextfield(String name, String text, int x, int y, int width, int height) {
      super(name, x, y, width, height);
      this.text = "";
      this.scrollOffset = 0;
      this.maxLength = 32;
      this.selEnd = 0;
      this.enabledColor = 14737632;
      this.disabledColor = 7368816;
      this.text = text;
   }

   public GuiTextfield setFloatOnly() {
      this.allowedChars = new char[]{'-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.'};
      return this;
   }

   public GuiTextfield setNumbersIncludingNegativeOnly() {
      this.allowedChars = new char[]{'-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
      return this;
   }

   public GuiTextfield setNumbersOnly() {
      this.allowedChars = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
      return this;
   }

   public int getScrollOffset() {
      return this.scrollOffset;
   }

   public boolean hasSelectedText() {
      return this.selEnd != -1 && this.selEnd != this.cursorPosition;
   }

   public String getSelectedText() {
      return this.hasSelectedText() && this.text != null ? this.text.substring(Math.min(this.cursorPosition, this.selEnd), Math.max(this.cursorPosition, this.selEnd)) : "";
   }

   public boolean mousePressed(int posX, int posY, int button) {
      boolean wasFocused = this.focused;
      super.mousePressed(posX, posY, button);
      if (this.focused && !wasFocused) {
         this.cursorCounter = 0;
      }

      int l = posX - (this.posX - this.width / 2);
      String s = GuiRenderHelper.instance.font.trimStringToWidth(this.text.substring(this.scrollOffset), this.getWidth());
      this.setCursorPosition(GuiRenderHelper.instance.font.trimStringToWidth(s, l).length() + this.scrollOffset);
      return true;
   }

   public void writeText(String text) {
      String s1 = "";
      String s2 = ChatAllowedCharacters.filterAllowedCharacters(text);
      String s3 = "";
      char[] chars = s2.toCharArray();
      if (this.allowedChars != null) {
         for(int i = 0; i < chars.length; ++i) {
            if (ArrayUtils.contains(this.allowedChars, chars[i])) {
               s3 = s3 + chars[i];
            }
         }

         s2 = s3;
      }

      int i = this.cursorPosition < this.selEnd ? this.cursorPosition : this.selEnd;
      int j = this.cursorPosition < this.selEnd ? this.selEnd : this.cursorPosition;
      int k = this.maxLength - this.text.length() - (i - this.selEnd);
      boolean flag = false;
      if (k < 0 && this.maxLength < this.text.length()) {
         k = 0;
      }

      if (this.text.length() > 0) {
         s1 = s1 + this.text.substring(0, i);
      }

      int l;
      if (k < s2.length()) {
         s1 = s1 + s2.substring(0, k);
         l = k;
      } else {
         s1 = s1 + s2;
         l = s2.length();
      }

      if (this.text.length() > 0 && j < this.text.length()) {
         s1 = s1 + this.text.substring(j);
      }

      this.text = s1;
      this.moveCursorBy(i - this.selEnd + l);
      this.raiseEvent(new GuiControlChangedEvent(this));
   }

   public void moveCursorBy(int offset) {
      this.setCursorPosition(this.selEnd + offset);
   }

   public int getNthWordFromCursor(int pos) {
      return this.getNthWordFromPos(pos, this.cursorPosition);
   }

   public int getNthWordFromPos(int pos, int cursorPos) {
      return this.getNthWordFromPosWS(pos, this.cursorPosition, true);
   }

   public int getNthWordFromPosWS(int p_146197_1_, int p_146197_2_, boolean p_146197_3_) {
      int k = p_146197_2_;
      boolean flag1 = p_146197_1_ < 0;
      int l = Math.abs(p_146197_1_);

      for(int i1 = 0; i1 < l; ++i1) {
         if (!flag1) {
            int j1 = this.text.length();
            k = this.text.indexOf(32, k);
            if (k == -1) {
               k = j1;
            } else {
               while(p_146197_3_ && k < j1 && this.text.charAt(k) == ' ') {
                  ++k;
               }
            }
         } else {
            while(p_146197_3_ && k > 0 && this.text.charAt(k - 1) == ' ') {
               --k;
            }

            while(k > 0 && this.text.charAt(k - 1) != ' ') {
               --k;
            }
         }
      }

      return k;
   }

   public void deleteWords(int pos) {
      if (this.text.length() != 0) {
         if (this.selEnd != this.cursorPosition) {
            this.writeText("");
         } else {
            this.deleteFromCursor(this.getNthWordFromCursor(pos) - this.cursorPosition);
         }
      }

      this.raiseEvent(new GuiControlChangedEvent(this));
   }

   public ArrayList<String> getTooltip() {
      ArrayList<String> strings = new ArrayList();
      return strings;
   }

   public void deleteFromCursor(int pos) {
      if (this.text.length() != 0) {
         if (this.selEnd != this.cursorPosition) {
            this.writeText("");
         } else {
            boolean flag = pos < 0;
            int j = flag ? this.cursorPosition + pos : this.cursorPosition;
            int k = flag ? this.cursorPosition : this.cursorPosition + pos;
            String s = "";
            if (j >= 0) {
               s = this.text.substring(0, j);
            }

            if (k < this.text.length()) {
               s = s + this.text.substring(k);
            }

            this.text = s;
            if (flag) {
               this.moveCursorBy(pos);
            }
         }
      }

      this.raiseEvent(new GuiControlChangedEvent(this));
   }

   public int getWidth() {
      return this.width - 8;
   }

   public void setSelectionPos(int p_146199_1_) {
      int j = this.text.length();
      p_146199_1_ = MathHelper.clamp(p_146199_1_, 0, j);
      this.selEnd = p_146199_1_;
      if (GuiRenderHelper.instance.font != null) {
         if (this.scrollOffset > j) {
            this.scrollOffset = j;
         }

         int k = this.getWidth();
         String s = GuiRenderHelper.instance.font.trimStringToWidth(this.text.substring(this.scrollOffset), k);
         int l = s.length() + this.scrollOffset;
         if (p_146199_1_ == this.scrollOffset) {
            this.scrollOffset -= GuiRenderHelper.instance.font.trimStringToWidth(this.text, k, true).length();
         }

         if (p_146199_1_ > l) {
            this.scrollOffset += p_146199_1_ - l;
         } else if (p_146199_1_ <= this.scrollOffset) {
            this.scrollOffset -= this.scrollOffset - p_146199_1_;
         }

         this.scrollOffset = MathHelper.clamp(this.scrollOffset, 0, j);
      }

   }

   public void setCursorPosition(int pos) {
      this.cursorPosition = pos;
      this.cursorPosition = MathHelper.clamp(this.cursorPosition, 0, this.text.length());
      this.setSelectionPos(this.cursorPosition);
   }

   public void setCursorPositionZero() {
      this.setCursorPosition(0);
   }

   public void setCursorPositionEnd() {
      this.setCursorPosition(this.text.length());
   }

   public boolean onKeyPressed(char character, int key) {
      if (!this.focused) {
         return false;
      } else if (GuiScreen.isKeyComboCtrlA(key)) {
         this.setCursorPositionEnd();
         this.setSelectionPos(0);
         return true;
      } else if (GuiScreen.isKeyComboCtrlC(key)) {
         GuiScreen.setClipboardString(this.getSelectedText());
         return true;
      } else if (GuiScreen.isKeyComboCtrlV(key)) {
         if (this.enabled) {
            this.writeText(GuiScreen.getClipboardString());
         }

         return true;
      } else if (GuiScreen.isKeyComboCtrlX(key)) {
         GuiScreen.setClipboardString(this.getSelectedText());
         if (this.enabled) {
            this.writeText("");
         }

         return true;
      } else {
         switch(key) {
         case 14:
            if (GuiScreen.isCtrlKeyDown()) {
               if (this.enabled) {
                  this.deleteWords(-1);
               }
            } else if (this.enabled) {
               this.deleteFromCursor(-1);
            }

            return true;
         case 199:
            if (GuiScreen.isShiftKeyDown()) {
               this.setSelectionPos(0);
            } else {
               this.setCursorPositionZero();
            }

            return true;
         case 203:
            if (GuiScreen.isShiftKeyDown()) {
               if (GuiScreen.isCtrlKeyDown()) {
                  this.setSelectionPos(this.getNthWordFromPos(-1, this.selEnd));
               } else {
                  this.setSelectionPos(this.selEnd - 1);
               }
            } else if (GuiScreen.isCtrlKeyDown()) {
               this.setCursorPosition(this.getNthWordFromCursor(-1));
            } else {
               this.moveCursorBy(-1);
            }

            return true;
         case 205:
            if (GuiScreen.isShiftKeyDown()) {
               if (GuiScreen.isCtrlKeyDown()) {
                  this.setSelectionPos(this.getNthWordFromPos(1, this.selEnd));
               } else {
                  this.setSelectionPos(this.selEnd + 1);
               }
            } else if (GuiScreen.isCtrlKeyDown()) {
               this.setCursorPosition(this.getNthWordFromCursor(1));
            } else {
               this.moveCursorBy(1);
            }

            return true;
         case 207:
            if (GuiScreen.isShiftKeyDown()) {
               this.setSelectionPos(this.text.length());
            } else {
               this.setCursorPositionEnd();
            }

            return true;
         case 211:
            if (GuiScreen.isCtrlKeyDown()) {
               if (this.enabled) {
                  this.deleteWords(1);
               }
            } else if (this.enabled) {
               this.deleteFromCursor(1);
            }

            return true;
         default:
            if (ChatAllowedCharacters.isAllowedCharacter(character)) {
               if (this.enabled) {
                  this.writeText(Character.toString(character));
               }

               return true;
            } else {
               return false;
            }
         }
      }
   }

   public void updateCursorCounter() {
      ++this.cursorCounter;
   }

   public void onTextChange() {
   }

   private void drawCursorVertical(int startX, int startY, int endX, int endY) {
      if (startX < endX) {
         int i = startX;
         startX = endX;
         endX = i;
      }

      if (startY < endY) {
         int j = startY;
         startY = endY;
         endY = j;
      }

      if (endX > this.posX + this.width) {
         endX = this.posX + this.width;
      }

      if (startX > this.posX + this.width) {
         startX = this.posX + this.width;
      }

      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer vertexbuffer = tessellator.getBuffer();
      GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
      GlStateManager.disableTexture2D();
      GlStateManager.enableColorLogic();
      GlStateManager.colorLogicOp(LogicOp.OR_REVERSE);
      vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
      vertexbuffer.pos((double)startX, (double)endY, 0.0D).endVertex();
      vertexbuffer.pos((double)endX, (double)endY, 0.0D).endVertex();
      vertexbuffer.pos((double)endX, (double)startY, 0.0D).endVertex();
      vertexbuffer.pos((double)startX, (double)startY, 0.0D).endVertex();
      tessellator.draw();
      GlStateManager.disableColorLogic();
      GlStateManager.enableTexture2D();
   }

   protected void renderContent(GuiRenderHelper helper, Style style, int width, int height) {
      this.scrollOffset = MathHelper.clamp(this.scrollOffset, 0, this.text.length());
      int i = this.enabled ? this.enabledColor : this.disabledColor;
      int j = this.cursorPosition - this.scrollOffset;
      int k = this.selEnd - this.scrollOffset;
      String s = GuiRenderHelper.instance.font.trimStringToWidth(this.text.substring(this.scrollOffset), this.getWidth());
      boolean flag = j >= 0 && j <= s.length();
      boolean flag1 = this.focused && this.cursorCounter / 6 % 2 == 0 && flag;
      int l = 4;
      int i1 = (this.height - this.getContentOffset() - GuiRenderHelper.instance.getFontHeight()) / 2;
      int j1 = l;
      if (k > s.length()) {
         k = s.length();
      }

      if (s.length() > 0) {
         String s1 = flag ? s.substring(0, j) : s;
         j1 = GuiRenderHelper.instance.font.drawStringWithShadow(s1, (float)l, (float)i1, i);
      }

      boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.maxLength;
      int k1 = j1;
      if (!flag) {
         k1 = j > 0 ? l + this.width : l;
      } else if (flag2) {
         k1 = j1 - 1;
         --j1;
      }

      if (s.length() > 0 && flag && j < s.length()) {
         GuiRenderHelper.instance.font.drawStringWithShadow(s.substring(j), (float)j1, (float)i1, i);
      }

      if (flag1) {
         if (flag2) {
            Gui.drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + GuiRenderHelper.instance.getFontHeight(), -3092272);
         } else {
            GuiRenderHelper.instance.font.drawStringWithShadow("_", (float)k1, (float)i1, i);
         }
      }

      if (k != j) {
         int l1 = l + GuiRenderHelper.instance.font.getStringWidth(s.substring(0, k));
         this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + GuiRenderHelper.instance.getFontHeight());
      }

   }

   public float parseFloat() {
      try {
         return Float.parseFloat(this.text);
      } catch (NumberFormatException var2) {
         return 0.0F;
      }
   }

   public int parseInteger() {
      try {
         return Integer.parseInt(this.text);
      } catch (NumberFormatException var2) {
         return 0;
      }
   }
}
