package net.ilexiconn.llibrary.client.gui.element;

import net.ilexiconn.llibrary.LLibrary;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.LogicOp;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class InputElementBase<T extends IElementGUI> extends Element<T> {
   protected String text;
   private boolean selected;
   private int lineScrollOffset;
   private int cursorPosition;
   private int selectionEnd;
   private int cursorCounter;
   private boolean editable;

   public InputElementBase(T gui, float posX, float posY, int width, boolean editable, String text) {
      super(gui, posX, posY, width, 12);
      this.text = text;
      this.editable = editable;
   }

   protected abstract void onSubmit();

   protected abstract boolean allowKey(int var1);

   public void update() {
      ++this.cursorCounter;
   }

   public void render(float mouseX, float mouseY, float partialTicks) {
      this.drawRectangle((double)this.getPosX(), (double)this.getPosY(), (double)this.getWidth(), (double)this.getHeight(), this.editable ? LLibrary.CONFIG.getSecondaryColor() : LLibrary.CONFIG.getSecondarySubcolor());
      int cursor = this.cursorPosition - this.lineScrollOffset;
      int cursorEnd = this.selectionEnd - this.lineScrollOffset;
      FontRenderer fontRenderer = this.gui.getFontRenderer();
      String displayString = fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
      boolean verticalCursor = cursor >= 0 && cursor <= displayString.length();
      boolean renderCursor = this.selected && this.cursorCounter / 6 % 2 == 0 && verticalCursor;
      float x = this.getPosX();
      float y = this.getPosY();
      float line = x;
      if (cursorEnd > displayString.length()) {
         cursorEnd = displayString.length();
      }

      if (!displayString.isEmpty()) {
         String s = verticalCursor ? displayString.substring(0, cursor) : displayString;
         line = (float)fontRenderer.drawString(s, x + 3.0F, y + 1.0F + (float)(this.getHeight() / 2) - (float)(fontRenderer.FONT_HEIGHT / 2), LLibrary.CONFIG.getTextColor(), false);
      }

      boolean renderVerticalCursor = this.cursorPosition < this.text.length();
      float lineX = line;
      if (!verticalCursor) {
         lineX = cursor > 0 ? x + (float)this.getWidth() : x;
      } else if (renderVerticalCursor) {
         lineX = line - 1.0F;
         --line;
      }

      if (!displayString.isEmpty() && verticalCursor && cursor < displayString.length()) {
         fontRenderer.drawString(displayString.substring(cursor), line + 1.0F, y + 1.0F + (float)(this.getHeight() / 2) - (float)(fontRenderer.FONT_HEIGHT / 2), LLibrary.CONFIG.getTextColor(), false);
      }

      if (renderCursor) {
         if (renderVerticalCursor) {
            this.drawRectangle((double)lineX, (double)y, 1.0D, (double)(this.getHeight() / 2 - fontRenderer.FONT_HEIGHT / 2 + 1 + fontRenderer.FONT_HEIGHT), LLibrary.CONFIG.getPrimaryColor());
         } else {
            fontRenderer.drawString("_", cursor == 0 ? lineX + 3.0F : lineX, y + 1.0F + (float)(this.getHeight() / 2) - (float)(fontRenderer.FONT_HEIGHT / 2), LLibrary.CONFIG.getPrimaryColor(), false);
         }
      }

      if (cursorEnd != cursor) {
         float selectionWidth = x + (float)fontRenderer.getStringWidth(displayString.substring(0, cursorEnd));
         this.drawCursorVertical(lineX + (float)(this.selectionEnd > this.cursorPosition ? 0 : 1), y, selectionWidth + (float)(this.selectionEnd < this.cursorPosition ? 2 : 3), y + (float)(this.getHeight() / 2) - (float)(fontRenderer.FONT_HEIGHT / 2) + 1.0F + (float)fontRenderer.FONT_HEIGHT);
      }

   }

   public boolean mouseClicked(float mouseX, float mouseY, int button) {
      boolean newSelected = this.isMouseSelecting(mouseX, mouseY);
      if (!newSelected && this.selected) {
         this.onSubmit();
      }

      this.selected = newSelected;
      if (this.selected && button == 0 && this.editable) {
         int width = (int)(mouseX - this.getPosX() - 1.0F);
         FontRenderer fontRenderer = this.gui.getFontRenderer();
         String displayString = fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
         this.setCursorPosition(fontRenderer.trimStringToWidth(displayString, width).length() + this.lineScrollOffset);
      }

      return false;
   }

   public boolean keyPressed(char character, int key) {
      if (!this.selected) {
         return false;
      } else if (GuiScreen.isKeyComboCtrlA(key)) {
         this.setCursorPositionEnd();
         this.setSelectionPos(0);
         return true;
      } else if (GuiScreen.isKeyComboCtrlC(key)) {
         GuiScreen.setClipboardString(this.getSelectedText());
         return true;
      } else if (GuiScreen.isKeyComboCtrlV(key)) {
         this.writeText(GuiScreen.getClipboardString());
         return true;
      } else if (GuiScreen.isKeyComboCtrlX(key)) {
         GuiScreen.setClipboardString(this.getSelectedText());
         this.writeText("");
         return true;
      } else if (key == 28) {
         this.onSubmit();
         return true;
      } else {
         switch(key) {
         case 14:
            if (GuiScreen.isCtrlKeyDown()) {
               this.deleteWords(-1);
            } else {
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
                  this.setSelectionPos(this.getNthWordFromPos(-1, this.selectionEnd));
               } else {
                  this.setSelectionPos(this.selectionEnd - 1);
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
                  this.setSelectionPos(this.getNthWordFromPos(1, this.selectionEnd));
               } else {
                  this.setSelectionPos(this.selectionEnd + 1);
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
               this.deleteWords(1);
            } else {
               this.deleteFromCursor(1);
            }

            return true;
         default:
            if (ChatAllowedCharacters.isAllowedCharacter(character) && this.allowKey(key)) {
               this.writeText(Character.toString(character));
               return true;
            } else {
               return false;
            }
         }
      }
   }

   private boolean isMouseSelecting(float mouseX, float mouseY) {
      return mouseX >= this.getPosX() && mouseX < this.getPosX() + (float)this.getWidth() && mouseY >= this.getPosY() && mouseY < this.getPosY() + (float)this.getHeight();
   }

   public void select() {
      this.selected = true;
      this.setCursorPositionEnd();
   }

   public String getText() {
      return this.text;
   }

   public String getSelectedText() {
      int start = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
      int end = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
      return this.text.substring(start, end);
   }

   public void writeText(String text) {
      String newText = "";
      String allowedText = ChatAllowedCharacters.filterAllowedCharacters(text);
      int start = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
      int end = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
      if (!this.text.isEmpty()) {
         newText = newText + this.text.substring(0, start);
      }

      newText = newText + allowedText;
      if (!this.text.isEmpty() && end < this.text.length()) {
         newText = newText + this.text.substring(end);
      }

      this.text = newText;
      this.moveCursorBy(start - this.selectionEnd + allowedText.length());
   }

   public void deleteWords(int amount) {
      if (!this.text.isEmpty()) {
         if (this.selectionEnd != this.cursorPosition) {
            this.writeText("");
         } else {
            this.deleteFromCursor(this.getNthWordFromCursor(amount) - this.cursorPosition);
         }
      }

   }

   public void deleteFromCursor(int amount) {
      if (!this.text.isEmpty()) {
         if (this.selectionEnd != this.cursorPosition) {
            this.writeText("");
         } else {
            boolean delete = amount < 0;
            int start = delete ? this.cursorPosition + amount : this.cursorPosition;
            int end = delete ? this.cursorPosition : this.cursorPosition + amount;
            String nextText = "";
            if (start >= 0) {
               nextText = this.text.substring(0, start);
            }

            if (end < this.text.length()) {
               nextText = nextText + this.text.substring(end);
            }

            this.text = nextText;
            if (delete) {
               this.moveCursorBy(amount);
            }
         }
      }

   }

   public int getNthWordFromCursor(int numWords) {
      return this.getNthWordFromPos(numWords, this.cursorPosition);
   }

   public int getNthWordFromPos(int n, int position) {
      return this.getNthWordFromPosWhitespace(n, position, true);
   }

   public int getNthWordFromPosWhitespace(int n, int position, boolean skipWhitespace) {
      int currentPos = position;
      boolean flag = n < 0;
      int j = Math.abs(n);

      for(int k = 0; k < j; ++k) {
         if (!flag) {
            int l = this.text.length();
            currentPos = this.text.indexOf(32, currentPos);
            if (currentPos == -1) {
               currentPos = l;
            } else {
               while(skipWhitespace && currentPos < l && this.text.charAt(currentPos) == ' ') {
                  ++currentPos;
               }
            }
         } else {
            while(skipWhitespace && currentPos > 0 && this.text.charAt(currentPos - 1) == ' ') {
               --currentPos;
            }

            while(currentPos > 0 && this.text.charAt(currentPos - 1) != ' ') {
               --currentPos;
            }
         }
      }

      return currentPos;
   }

   public void moveCursorBy(int amount) {
      this.setCursorPosition(this.selectionEnd + amount);
   }

   public void setCursorPosition(int position) {
      this.cursorPosition = position;
      this.cursorPosition = MathHelper.clamp(this.cursorPosition, 0, this.text.length());
      this.setSelectionPos(this.cursorPosition);
   }

   public void setCursorPositionZero() {
      this.setCursorPosition(0);
   }

   public void setCursorPositionEnd() {
      this.setCursorPosition(this.text.length());
   }

   public void setSelectionPos(int position) {
      int textLength = this.text.length();
      if (position > textLength) {
         position = textLength;
      } else if (position < 0) {
         position = 0;
      }

      this.selectionEnd = position;
      if (this.lineScrollOffset > textLength) {
         this.lineScrollOffset = textLength;
      }

      FontRenderer fontRenderer = this.gui.getFontRenderer();
      String displayText = fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
      int offset = displayText.length() + this.lineScrollOffset;
      if (position == this.lineScrollOffset) {
         this.lineScrollOffset -= fontRenderer.trimStringToWidth(this.text, this.getWidth(), true).length();
      }

      if (position > offset) {
         this.lineScrollOffset += position - offset;
      } else if (position <= this.lineScrollOffset) {
         this.lineScrollOffset -= this.lineScrollOffset - position;
      }

      this.lineScrollOffset = MathHelper.clamp(this.lineScrollOffset, 0, textLength);
   }

   public void clearText() {
      this.setCursorPositionZero();
      this.text = "";
   }

   public void setText(String text) {
      this.setCursorPosition(text.length());
      this.text = text;
   }

   public void setEditable(boolean editable) {
      this.editable = editable;
   }

   private void drawCursorVertical(float startX, float startY, float endX, float endY) {
      if (startX < endX) {
         float prevStartX = startX;
         startX = endX;
         endX = prevStartX;
      }

      if (startY < endY) {
         float prevStartY = startY;
         startY = endY;
         endY = prevStartY;
      }

      if (endX > this.getPosX() + (float)this.getWidth()) {
         endX = this.getPosY() + (float)this.getWidth();
      }

      if (startX > this.getPosX() + (float)this.getWidth()) {
         startX = this.getPosY() + (float)this.getWidth();
      }

      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer buffer = tessellator.getBuffer();
      GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
      GlStateManager.disableTexture2D();
      GlStateManager.enableColorLogic();
      GlStateManager.colorLogicOp(LogicOp.OR_REVERSE);
      buffer.begin(7, DefaultVertexFormats.POSITION);
      buffer.pos((double)startX, (double)endY, 0.0D).endVertex();
      buffer.pos((double)endX, (double)endY, 0.0D).endVertex();
      buffer.pos((double)endX, (double)startY, 0.0D).endVertex();
      buffer.pos((double)startX, (double)startY, 0.0D).endVertex();
      tessellator.draw();
      GlStateManager.disableColorLogic();
      GlStateManager.enableTexture2D();
   }
}
