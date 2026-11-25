package betterquesting.api2.client.gui.controls;

import betterquesting.api.misc.ICallback;
import betterquesting.api.utils.RenderUtils;
import betterquesting.api2.client.gui.controls.io.FloatSimpleIO;
import betterquesting.api2.client.gui.misc.GuiRectangle;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.client.gui.resources.colors.IGuiColor;
import betterquesting.api2.client.gui.resources.textures.IGuiTexture;
import betterquesting.api2.client.gui.themes.presets.PresetColor;
import betterquesting.api2.client.gui.themes.presets.PresetTexture;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

public class PanelTextField<T> implements IGuiPanel {
   private final IGuiRect transform;
   private boolean enabled = true;
   private IGuiTexture[] texState = new IGuiTexture[3];
   private IGuiColor[] colStates = new IGuiColor[3];
   private IGuiColor colHighlight;
   private IGuiColor colWatermark;
   private boolean lockFocus = false;
   private boolean isFocused = false;
   private boolean isActive = true;
   private boolean canWrap = false;
   private int maxLength = 32;
   private String text;
   private String watermark = "";
   private int selectStart = 0;
   private int selectEnd = 0;
   private boolean dragging = false;
   private GuiRectangle cursorLine = new GuiRectangle(4, 4, 1, 8);
   private IValueIO<Float> scrollX;
   private IValueIO<Float> scrollY;
   private int scrollWidth = 0;
   private int scrollHeight = 0;
   private final IFieldFilter<T> filter;
   private ICallback<T> callback;

   public PanelTextField(IGuiRect rect, String text, IFieldFilter<T> filter) {
      this.transform = rect;
      this.cursorLine.setParent(this.transform);
      this.filter = filter;
      this.setTextures(PresetTexture.TEXT_BOX_0.getTexture(), PresetTexture.TEXT_BOX_1.getTexture(), PresetTexture.TEXT_BOX_2.getTexture());
      this.setMainColors(PresetColor.TEXT_AUX_0.getColor(), PresetColor.TEXT_AUX_0.getColor(), PresetColor.TEXT_AUX_0.getColor());
      this.setAuxColors(PresetColor.TEXT_WATERMARK.getColor(), PresetColor.TEXT_HIGHLIGHT.getColor());
      this.scrollX = new FloatSimpleIO();
      this.scrollY = new FloatSimpleIO();
      this.setText(text);
   }

   public PanelTextField<T> setCallback(ICallback<T> callback) {
      this.callback = callback;
      return this;
   }

   public PanelTextField<T> setTextures(IGuiTexture disabled, IGuiTexture idle, IGuiTexture focused) {
      this.texState[0] = disabled;
      this.texState[1] = idle;
      this.texState[2] = focused;
      return this;
   }

   public PanelTextField<T> setMainColors(IGuiColor disabled, IGuiColor idle, IGuiColor focused) {
      this.colStates[0] = disabled;
      this.colStates[1] = idle;
      this.colStates[2] = focused;
      return this;
   }

   public PanelTextField<T> setAuxColors(IGuiColor watermark, IGuiColor highlight) {
      this.colWatermark = watermark;
      this.colHighlight = highlight;
      return this;
   }

   public PanelTextField<T> setMaxLength(int size) {
      this.maxLength = size;
      return this;
   }

   public PanelTextField<T> enableWrapping(boolean state) {
      this.canWrap = state;
      this.updateScrollBounds();
      return this;
   }

   public void lockFocus(boolean state) {
      this.lockFocus = state;
      if (state) {
         this.isFocused = true;
      }

   }

   public PanelTextField<T> setScrollDriverX(IValueIO<Float> driver) {
      this.scrollX = driver;
      return this;
   }

   public PanelTextField<T> setScrollDriverY(IValueIO<Float> driver) {
      this.scrollY = driver;
      return this;
   }

   public int getScrollX() {
      return this.scrollWidth <= 0 ? 0 : (int)((float)this.scrollWidth * ((Float)this.scrollX.readValue()).floatValue());
   }

   public int getScrollY() {
      return this.scrollHeight <= 0 ? 0 : (int)((float)this.scrollHeight * ((Float)this.scrollY.readValue()).floatValue());
   }

   public void setScrollX(int value) {
      if (this.scrollWidth <= 0) {
         this.scrollX.writeValue(Float.valueOf(0.0F));
      } else {
         this.scrollX.writeValue(Float.valueOf((float)MathHelper.clamp(value, 0, this.scrollWidth) / (float)this.scrollWidth));
      }
   }

   public void setScrollY(int value) {
      if (this.scrollHeight <= 0) {
         this.scrollY.writeValue(Float.valueOf(0.0F));
      } else {
         this.scrollY.writeValue(Float.valueOf((float)MathHelper.clamp(value, 0, this.scrollHeight) / (float)this.scrollHeight));
      }
   }

   public void setActive(boolean state) {
      this.isActive = state;
   }

   public boolean isActive() {
      return this.isActive;
   }

   public boolean isFocused() {
      return this.isFocused;
   }

   public PanelTextField<T> setWatermark(String text) {
      this.watermark = text;
      return this;
   }

   public void setText(String text) {
      this.text = this.filter.filterText(text);
      this.updateScrollBounds();
      this.setCursorPosition(0);
   }

   public String getRawText() {
      return this.text;
   }

   public T getValue() {
      return this.filter.parseValue(this.getRawText());
   }

   public String getSelectedText() {
      int l = Math.min(this.selectStart, this.selectEnd);
      int r = Math.max(this.selectStart, this.selectEnd);
      return this.text.substring(l, r);
   }

   public void writeText(String in) {
      StringBuilder out = new StringBuilder();
      int l = Math.min(this.selectStart, this.selectEnd);
      int r = Math.max(this.selectStart, this.selectEnd);
      int space = this.maxLength - this.text.length() - (l - r);
      if (!this.text.isEmpty()) {
         out.append(this.text, 0, l);
      }

      int used;
      if (space < 0) {
         used = 0;
      } else if (space < in.length()) {
         out.append(in, 0, space);
         used = space;
      } else {
         out.append(in);
         used = in.length();
      }

      if (!this.text.isEmpty() && r < this.text.length()) {
         out.append(this.text, r, this.text.length());
      }

      this.text = this.filter.filterText(out.toString());
      this.updateScrollBounds();
      this.moveCursorBy(l - this.selectEnd + used);
      if (this.callback != null) {
         this.callback.setValue(this.filter.parseValue(this.text));
      }

   }

   public void deleteWords(int num) {
      if (!this.text.isEmpty()) {
         if (this.selectEnd != this.selectStart) {
            this.writeText("");
         } else {
            this.deleteFromCursor(this.getNthWordFromCursor(num) - this.selectStart);
         }
      }

   }

   public void deleteFromCursor(int num) {
      if (!this.text.isEmpty()) {
         if (this.selectEnd != this.selectStart) {
            this.writeText("");
         } else {
            boolean flag = num < 0;
            int i = flag ? this.selectStart + num : this.selectStart;
            int j = flag ? this.selectStart : this.selectStart + num;
            String s = "";
            if (i >= 0) {
               s = this.text.substring(0, i);
            }

            if (j < this.text.length()) {
               s = s + this.text.substring(j);
            }

            this.text = this.filter.filterText(s);
            this.updateScrollBounds();
            if (flag) {
               this.moveCursorBy(num);
            }

            if (this.callback != null) {
               this.callback.setValue(this.filter.parseValue(this.text));
            }
         }
      }

   }

   public int getNthWordFromCursor(int numWords) {
      return this.getNthWordFromPos(numWords, this.selectStart);
   }

   public int getNthWordFromPos(int n, int pos) {
      return this.getNthWordFromPosWS(n, pos, true);
   }

   public int getNthWordFromPosWS(int n, int pos, boolean skipWs) {
      int i = pos;
      boolean flag = n < 0;
      int j = Math.abs(n);

      for(int k = 0; k < j; ++k) {
         if (!flag) {
            int l = this.text.length();
            i = this.text.indexOf(32, i);
            if (i == -1) {
               i = l;
            } else {
               while(skipWs && i < l && this.text.charAt(i) == ' ') {
                  ++i;
               }
            }
         } else {
            while(skipWs && i > 0 && this.text.charAt(i - 1) == ' ') {
               --i;
            }

            while(i > 0 && this.text.charAt(i - 1) != ' ') {
               --i;
            }
         }
      }

      return i;
   }

   public void moveCursorBy(int num) {
      this.setCursorPosition(this.selectEnd + num);
   }

   public void setCursorPosition(int pos) {
      this.selectStart = pos;
      int i = this.text.length();
      this.selectStart = MathHelper.clamp(this.selectStart, 0, i);
      this.setSelectionPos(this.selectStart);
   }

   public boolean onKeyTyped(char typedChar, int keyCode) {
      if (!this.isFocused) {
         return false;
      } else if (GuiScreen.isKeyComboCtrlA(keyCode)) {
         this.setCursorPosition(this.text.length());
         this.setSelectionPos(0);
         return true;
      } else if (GuiScreen.isKeyComboCtrlC(keyCode)) {
         GuiScreen.setClipboardString(this.getSelectedText());
         return true;
      } else if (GuiScreen.isKeyComboCtrlV(keyCode)) {
         if (this.isActive) {
            this.writeText(GuiScreen.getClipboardString());
         }

         return true;
      } else if (GuiScreen.isKeyComboCtrlX(keyCode)) {
         GuiScreen.setClipboardString(this.getSelectedText());
         if (this.isActive) {
            this.writeText("");
         }

         return true;
      } else {
         switch(keyCode) {
         case 14:
            if (GuiScreen.isCtrlKeyDown()) {
               if (this.isActive) {
                  this.deleteWords(-1);
               }
            } else if (this.isActive) {
               this.deleteFromCursor(-1);
            }

            return true;
         case 28:
            if (this.canWrap) {
               this.writeText("\n");
            }

            return true;
         case 199:
            if (GuiScreen.isShiftKeyDown()) {
               this.setSelectionPos(0);
            } else {
               this.setCursorPosition(0);
            }

            return true;
         case 200:
            return true;
         case 203:
            if (GuiScreen.isShiftKeyDown()) {
               if (GuiScreen.isCtrlKeyDown()) {
                  this.setSelectionPos(this.getNthWordFromPos(-1, this.selectEnd));
               } else {
                  this.setSelectionPos(this.selectEnd - 1);
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
                  this.setSelectionPos(this.getNthWordFromPos(1, this.selectEnd));
               } else {
                  this.setSelectionPos(this.selectEnd + 1);
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
               this.setCursorPosition(this.text.length());
            }

            return true;
         case 208:
            return true;
         case 211:
            if (GuiScreen.isCtrlKeyDown()) {
               if (this.isActive) {
                  this.deleteWords(1);
               }
            } else if (this.isActive) {
               this.deleteFromCursor(1);
            }

            return true;
         default:
            if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
               if (this.isActive) {
                  this.writeText(Character.toString(typedChar));
               }

               return true;
            } else {
               return false;
            }
         }
      }
   }

   public void setSelectionPos(int position) {
      int i = this.text.length();
      if (position > i) {
         position = i;
      }

      if (position < 0) {
         position = 0;
      }

      if (this.selectEnd != position) {
         this.selectEnd = position;
         FontRenderer font = Minecraft.getMinecraft().fontRenderer;
         if (this.canWrap) {
            List<String> lines = RenderUtils.splitStringWithoutFormat(this.text, this.getTransform().getWidth() - 8, font);
            String lastFormat = "";
            int idx = 0;
            int y = 0;

            int x;
            for(x = 0; y < lines.size(); ++y) {
               String s = (String)lines.get(y);
               if (this.selectEnd >= idx && this.selectEnd < idx + s.length() + (y == lines.size() - 1 ? 1 : 0)) {
                  x = RenderUtils.getStringWidth(lastFormat + s.substring(0, this.selectEnd - idx), font);
                  break;
               }

               idx += s.length();
               lastFormat = FontRenderer.getFormatFromString(lastFormat + s);
            }

            y = y * font.FONT_HEIGHT;
            int sy = this.getScrollY();
            if (y < sy) {
               this.setScrollY(y);
            } else if (y > sy + (this.transform.getHeight() - 8) - font.FONT_HEIGHT) {
               this.setScrollY(y - (this.transform.getHeight() - 8) + font.FONT_HEIGHT);
            }

            this.cursorLine.x = x + 4;
            this.cursorLine.y = y + 4;
            this.cursorLine.w = 1;
            this.cursorLine.h = font.FONT_HEIGHT;
         } else {
            int x = RenderUtils.getStringWidth(this.text.substring(0, this.selectEnd), font);
            int sx = this.getScrollX();
            if (x < sx) {
               this.setScrollX(x);
            } else if (x > sx + (this.transform.getWidth() - 8)) {
               this.setScrollX(x - (this.transform.getWidth() - 8));
            }

            this.cursorLine.x = x + 4;
            this.cursorLine.y = 4;
            this.cursorLine.w = 1;
            this.cursorLine.h = font.FONT_HEIGHT;
         }
      }

   }

   public void updateScrollBounds() {
      FontRenderer font = Minecraft.getMinecraft().fontRenderer;
      int prevX = this.getScrollX();
      int prevY = this.getScrollY();
      if (!this.canWrap) {
         this.scrollHeight = 0;
         this.scrollWidth = Math.max(0, RenderUtils.getStringWidth(this.text, font) - (this.transform.getWidth() - 8));
      } else {
         this.scrollWidth = 0;
         this.scrollHeight = Math.max(0, RenderUtils.splitString(this.text, this.transform.getWidth() - 8, font).size() * font.FONT_HEIGHT - (this.transform.getHeight() - 8));
      }

      this.setScrollX(prevX);
      this.setScrollY(prevY);
   }

   public IGuiRect getTransform() {
      return this.transform;
   }

   public void initPanel() {
   }

   public void setEnabled(boolean state) {
      this.enabled = state;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void drawPanel(int mx, int my, float partialTick) {
      if (this.isActive && this.dragging && Mouse.isButtonDown(0)) {
         if (this.canWrap) {
            this.setSelectionPos(RenderUtils.getCursorPos(this.text, mx - (this.transform.getX() + 4) + this.getScrollX(), my - (this.transform.getY() + 4) + this.getScrollY(), this.transform.getWidth() - 8, Minecraft.getMinecraft().fontRenderer));
         } else {
            this.setSelectionPos(RenderUtils.getCursorPos(this.text, mx - (this.transform.getX() + 4) + this.getScrollX(), Minecraft.getMinecraft().fontRenderer));
         }
      } else if (this.dragging) {
         this.dragging = false;
      }

      IGuiRect bounds = this.getTransform();
      int state = !this.isActive() ? 0 : (this.isFocused ? 2 : 1);
      Minecraft mc = Minecraft.getMinecraft();
      IGuiTexture t = this.texState[state];
      GlStateManager.pushMatrix();
      if (t != null) {
         t.drawTexture(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight(), 0.0F, partialTick);
      }

      RenderUtils.startScissor(bounds);
      GlStateManager.translate((float)(-this.getScrollX()), (float)(-this.getScrollY()), 0.0F);
      if (this.text.length() <= 0) {
         if (!this.isFocused) {
            mc.fontRenderer.drawString(this.watermark, (float)(bounds.getX() + 4), (float)(bounds.getY() + 4), this.colWatermark.getRGB(), false);
         }
      } else {
         IGuiColor c = this.colStates[state];
         if (!this.canWrap) {
            RenderUtils.drawHighlightedString(mc.fontRenderer, this.text, bounds.getX() + 4, bounds.getY() + 4, c.getRGB(), false, this.colHighlight.getRGB(), this.selectStart, this.selectEnd);
         } else {
            RenderUtils.drawHighlightedSplitString(mc.fontRenderer, this.text, bounds.getX() + 4, bounds.getY() + 4, bounds.getWidth() - 8, c.getRGB(), false, this.colHighlight.getRGB(), this.selectStart, this.selectEnd);
         }
      }

      if (this.isFocused && this.selectStart == this.selectEnd && System.currentTimeMillis() / 500L % 2L == 0L) {
         RenderUtils.drawHighlightBox(this.cursorLine, this.colHighlight);
      }

      RenderUtils.endScissor();
      GlStateManager.popMatrix();
   }

   public boolean onMouseClick(int mx, int my, int button) {
      if (this.transform.contains(mx, my)) {
         if (!this.isFocused) {
            this.isFocused = true;
            this.updateScrollBounds();
         }

         if (this.canWrap) {
            this.setCursorPosition(RenderUtils.getCursorPos(this.text, mx - (this.transform.getX() + 4) + this.getScrollX(), my - (this.transform.getY() + 4) + this.getScrollY(), this.transform.getWidth() - 8, Minecraft.getMinecraft().fontRenderer));
         } else {
            this.setCursorPosition(RenderUtils.getCursorPos(this.text, mx - (this.transform.getX() + 4) + this.getScrollX(), Minecraft.getMinecraft().fontRenderer));
         }

         this.dragging = true;
      } else if (this.isFocused && !this.lockFocus) {
         this.isFocused = false;
         this.text = this.filter.parseValue(this.text).toString();
      }

      return false;
   }

   public boolean onMouseRelease(int mx, int my, int button) {
      return this.isFocused && this.dragging;
   }

   public boolean onMouseScroll(int mx, int my, int scroll) {
      if (this.isFocused && this.transform.contains(mx, my)) {
         if (this.canWrap) {
            this.setScrollY(this.getScrollY() + scroll * 4);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public List<String> getTooltip(int mx, int my) {
      return null;
   }
}
