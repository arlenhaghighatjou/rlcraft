package com.creativemd.creativecore.common.gui.controls.gui.text;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.controls.gui.GuiFocusControl;
import com.creativemd.creativecore.common.gui.event.gui.GuiControlChangedEvent;
import com.creativemd.creativecore.common.utils.mc.ColorUtils;
import com.creativemd.creativecore.common.utils.type.Pair;
import java.awt.Toolkit;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.LogicOp;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;

public class GuiTextarea extends GuiFocusControl {
   private static final int cursorBlinkRate = getBlinkRate();
   public static final int DEFAULT_LINE_SPACING = 2;
   public static final int DEFAULT_TEXT_PADDING = 8;
   public static final Consumer<TextareaChange> DEFAULT_CONTENT_FORMATTER = (x) -> {
   };
   protected GuiTextareaContent content;
   protected GuiTextareaSelection selection;
   protected int lineSpacing;
   protected IWordWrapAlgorithm wordWrapAlgorithm;
   protected IWordMatcher wordMatcher;
   protected Consumer<TextareaChange> contentFormatter;
   protected boolean allowTabs;
   protected boolean allowIndentation;
   protected boolean replaceModeActive;
   protected int enabledColor;
   protected int disabledColor;

   private static int getBlinkRate() {
      Integer br = (Integer)Toolkit.getDefaultToolkit().getDesktopProperty("awt.cursorBlinkRate");
      return br != null ? br.intValue() : 350;
   }

   public GuiTextarea(String text, int x, int y, int width) {
      this(text, x, y, width, 16);
   }

   public GuiTextarea(String text, int x, int y, int width, int height) {
      this(text, text, x, y, width, height);
   }

   public GuiTextarea(String name, String text, int x, int y, int width, int height) {
      super(name, x, y, width, height);
      this.lineSpacing = 2;
      this.wordWrapAlgorithm = IWordWrapAlgorithm.DEFAULT_NEWLINE_WORDWRAP_ALGORITHM;
      this.wordMatcher = IWordMatcher.DEFAULT_MATCHER;
      this.contentFormatter = DEFAULT_CONTENT_FORMATTER;
      this.allowTabs = false;
      this.allowIndentation = false;
      this.replaceModeActive = false;
      this.enabledColor = 14737632;
      this.disabledColor = 7368816;
      this.selection = new GuiTextareaSelection(this);
      this.content = new GuiTextareaContent(this, text);
   }

   public GuiTextareaContent getContent() {
      return this.content;
   }

   public GuiTextareaSelection getSelection() {
      return this.selection;
   }

   public String getText() {
      return this.content.getText();
   }

   public boolean hasSelectedText() {
      return this.selection.hasSelectedText();
   }

   public String getSelectedText() {
      return this.selection.getSelectedText();
   }

   public IWordWrapAlgorithm getWordWrapAlgorithm() {
      return this.wordWrapAlgorithm != null ? this.wordWrapAlgorithm : IWordWrapAlgorithm.DEFAULT_NEWLINE_WORDWRAP_ALGORITHM;
   }

   public IWordMatcher getWordMatcher() {
      return this.wordMatcher != null ? this.wordMatcher : IWordMatcher.DEFAULT_MATCHER;
   }

   public Consumer<TextareaChange> getContentFormatter() {
      return this.contentFormatter != null ? this.contentFormatter : DEFAULT_CONTENT_FORMATTER;
   }

   public void setContentFormatter(Consumer<TextareaChange> formatter) {
      this.contentFormatter = formatter;
      this.onContentChange(new TextareaChange(this, this.content.getText(), 0, this.content.getText().length(), this.selection.getCursorOffset(), this.selection.getAnchorOffset()));
   }

   public boolean mousePressed(int posX, int posY, int button) {
      boolean wasFocused = this.focused;
      super.mousePressed(posX, posY, button);
      TextareaVec vec = this.rayTrace(posX, posY);
      if (GuiScreen.isShiftKeyDown()) {
         this.selection.selectTo(vec.row, vec.column);
      } else {
         this.selection.setCursorPosition(vec.row, vec.column);
      }

      return true;
   }

   public boolean onKeyPressed(char character, int key) {
      if (!this.focused) {
         return false;
      } else if (GuiScreen.isKeyComboCtrlA(key)) {
         this.selection.selectAll();
         return true;
      } else if (GuiScreen.isKeyComboCtrlC(key)) {
         if (this.selection.hasSelectedText()) {
            GuiScreen.setClipboardString(this.getSelectedText());
         } else {
            GuiScreen.setClipboardString(this.content.getLine(this.selection.getCursorPosition().row));
         }

         return true;
      } else {
         if (GuiScreen.isKeyComboCtrlX(key)) {
            if (this.selection.hasSelectedText()) {
               GuiScreen.setClipboardString(this.getSelectedText());
            } else {
               GuiScreen.setClipboardString(this.content.getLine(this.selection.getCursorPosition().row));
            }

            if (this.enabled) {
               this.content.replaceSelection("");
            }
         } else if (GuiScreen.isKeyComboCtrlV(key)) {
            if (this.enabled) {
               this.content.replaceSelection(GuiScreen.getClipboardString());
            }

            return true;
         }

         switch(key) {
         case 0:
            return false;
         case 14:
            if (this.enabled) {
               if (GuiScreen.isCtrlKeyDown()) {
                  this.content.backspaceWordAtCursor();
               } else {
                  this.content.backspaceAtCursor();
               }
            }

            return true;
         case 15:
            if (this.enabled) {
               if (this.allowIndentation) {
                  if (GuiScreen.isShiftKeyDown()) {
                     this.content.outdentSelection();
                     return true;
                  }

                  Pair<TextareaVec, TextareaVec> sel = this.selection.getSelectionPositions();
                  if (((TextareaVec)sel.key).row != ((TextareaVec)sel.value).row || ((TextareaVec)sel.key).column == 0 && ((TextareaVec)sel.value).column == this.content.getLine(((TextareaVec)sel.value).row).length()) {
                     this.content.indentSelection();
                     return true;
                  }
               }

               if (this.allowTabs) {
                  this.content.typeText(character);
                  return true;
               }
            }

            return false;
         case 30:
            if (GuiScreen.isCtrlKeyDown()) {
               this.selection.selectAll();
               return true;
            }
         default:
            if (ChatAllowedCharacters.isAllowedCharacter(character)) {
               this.content.typeText(character);
               return true;
            }

            return false;
         case 199:
            if (GuiScreen.isCtrlKeyDown()) {
               if (GuiScreen.isShiftKeyDown()) {
                  this.selection.selectToStart();
               } else {
                  this.selection.moveCursorToStart();
               }
            } else if (GuiScreen.isShiftKeyDown()) {
               this.selection.selectToLineStart();
            } else {
               this.selection.moveCursorToLineStart();
            }

            return true;
         case 200:
            if (GuiScreen.isCtrlKeyDown()) {
               if (GuiScreen.isShiftKeyDown()) {
                  this.selection.selectToLineEnd();
               } else {
                  this.selection.moveCursorToLineEnd();
               }
            } else if (GuiScreen.isShiftKeyDown()) {
               this.selection.selectUp();
            } else {
               this.selection.moveCursorUp();
            }

            return true;
         case 201:
            if (GuiScreen.isShiftKeyDown()) {
               this.selection.selectToStart();
            } else {
               this.selection.moveCursorToStart();
            }

            return true;
         case 203:
            if (GuiScreen.isCtrlKeyDown()) {
               if (GuiScreen.isShiftKeyDown()) {
                  this.selection.selectToPreviousWord();
               } else {
                  this.selection.moveCursorToPreviousWordStart();
               }
            } else if (GuiScreen.isShiftKeyDown()) {
               this.selection.selectLeft();
            } else if (this.selection.hasSelectedText()) {
               this.selection.moveCursorToSelectionStart();
            } else {
               this.selection.moveCursorLeft();
            }

            return true;
         case 205:
            if (GuiScreen.isCtrlKeyDown()) {
               if (GuiScreen.isShiftKeyDown()) {
                  this.selection.selectToNextWord();
               } else {
                  this.selection.moveCursorToNextWordEnd();
               }
            } else if (GuiScreen.isShiftKeyDown()) {
               this.selection.selectRight();
            } else if (this.selection.hasSelectedText()) {
               this.selection.moveCursorToSelectionEnd();
            } else {
               this.selection.moveCursorRight();
            }

            return true;
         case 207:
            if (GuiScreen.isCtrlKeyDown()) {
               if (GuiScreen.isShiftKeyDown()) {
                  this.selection.selectToEnd();
               } else {
                  this.selection.moveCursorToEnd();
               }
            } else if (GuiScreen.isShiftKeyDown()) {
               this.selection.selectToLineEnd();
            } else {
               this.selection.moveCursorToLineEnd();
            }

            return true;
         case 208:
            if (GuiScreen.isCtrlKeyDown()) {
               if (GuiScreen.isShiftKeyDown()) {
                  this.selection.selectToLineStart();
               } else {
                  this.selection.moveCursorToLineStart();
               }
            } else if (GuiScreen.isShiftKeyDown()) {
               this.selection.selectDown();
            } else {
               this.selection.moveCursorDown();
            }

            return true;
         case 209:
            if (GuiScreen.isShiftKeyDown()) {
               this.selection.selectToEnd();
            } else {
               this.selection.moveCursorToEnd();
            }

            return true;
         case 210:
            this.replaceModeActive = !this.replaceModeActive;
            return true;
         case 211:
            if (this.enabled) {
               if (GuiScreen.isCtrlKeyDown()) {
                  this.content.deleteWordAtCursor();
               } else {
                  this.content.deleteAtCursor();
               }
            }

            return true;
         }
      }
   }

   protected void drawInverted(int startX, int startY, int endX, int endY) {
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
      List<String> ls = this.content.getLines();
      Pair<TextareaVec, TextareaVec> sel = this.selection.getSelectionRangePositions();
      boolean hasSelection = this.selection.hasSelectedText();
      TextareaVec cur = this.selection.getCursorPosition();
      int color = this.enabled ? this.enabledColor : this.disabledColor;
      int lh = GuiRenderHelper.instance.getFontHeight();
      int y = 0 - this.lineSpacing / 2;

      for(int i = 0; i < ls.size(); ++i) {
         String l = (String)ls.get(i);
         GuiRenderHelper.instance.font.drawString(l, 0, y, color);
         if (hasSelection && i >= ((TextareaVec)sel.key).row && i <= ((TextareaVec)sel.value).row) {
            int ss = GuiRenderHelper.instance.font.getStringWidth(l.substring(0, ((TextareaVec)sel.key).row == i ? ((TextareaVec)sel.key).column : 0));
            int se = GuiRenderHelper.instance.font.getStringWidth(l.substring(0, ((TextareaVec)sel.value).row == i ? ((TextareaVec)sel.value).column : l.length()));
            Gui.drawRect(ss - 1, y - 1, se + 1, y + 1 + lh, this.focused ? ColorUtils.RGBAToInt(208, 208, 255, 128) : ColorUtils.RGBAToInt(208, 208, 208, 208));
         }

         if (this.focused && System.currentTimeMillis() / (long)cursorBlinkRate % 2L == 0L && i == cur.row) {
            int cs = GuiRenderHelper.instance.font.getStringWidth(l.substring(0, cur.column));
            if (this.replaceModeActive) {
               int ce = cs + GuiRenderHelper.instance.font.getCharWidth(l.charAt(cur.column));
               this.drawInverted(cs - 1, y - 1, ce + 1, y + 1 + lh);
            } else if (this.selection.getCursorOffset() == this.content.getText().length()) {
               int nlw = GuiRenderHelper.instance.font.getStringWidth(l + "_");
               if (nlw > this.width - this.getContentOffset() * 2) {
                  GuiRenderHelper.instance.font.drawStringWithShadow("_", 0.0F, (float)(y + lh + this.lineSpacing), color);
               } else {
                  GuiRenderHelper.instance.font.drawStringWithShadow("_", (float)(nlw - GuiRenderHelper.instance.font.getCharWidth('_')), (float)y, color);
               }
            } else {
               this.drawInverted(cs, y - 1, cs + 1, y + 1 + lh);
            }
         }

         y += lh + this.lineSpacing;
      }

   }

   protected void onContentChange(TextareaChange change) {
      if (change != null) {
         this.getContentFormatter().accept(change);
         change.validate();
         this.content.doContentChanged(change);
         this.selection.doSelectionChanged(change);
         this.raiseEvent(new GuiControlChangedEvent(this));
      }

   }

   protected TextareaVec rayTrace(int posX, int posY) {
      int row = this.rayTraceLine(posY);
      int column = this.rayTraceColumn(row, posX);
      return new TextareaVec(column, row);
   }

   protected int rayTraceLine(int posY) {
      int y = posY - this.posY;
      int lh = GuiRenderHelper.instance.getFontHeight();
      return y < this.marginWidth ? 0 : Math.min((y - this.lineSpacing / 2) / (lh + this.lineSpacing), this.getContent().getLineCount() - 1);
   }

   protected int rayTraceColumn(int row, int posX) {
      int x = posX - this.posX;
      if (x < this.marginWidth) {
         return 0;
      } else {
         String rs = this.getContent().getLine(row);
         String s = GuiRenderHelper.instance.font.trimStringToWidth(rs, x - this.marginWidth);
         return Math.min(s.length(), rs.length() - (this.replaceModeActive && row != this.content.getLineCount() - 1 ? 1 : 0));
      }
   }

   protected TextareaVec positionToCoordinates(int row, int column) {
      return new TextareaVec(this.columnToXpos(row, column), this.rowToYpos(row));
   }

   protected int rowToYpos(int row) {
      int lh = GuiRenderHelper.instance.getFontHeight();
      return this.marginWidth + row * (lh + this.lineSpacing);
   }

   protected int columnToXpos(int row, int column) {
      String s = this.content.getLine(row).substring(0, column);
      return this.marginWidth + GuiRenderHelper.instance.font.getStringWidth(s);
   }
}
