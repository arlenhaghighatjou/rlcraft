package me.ichun.mods.ichunutil.client.gui.window.element;

import java.util.ArrayList;
import java.util.Locale;
import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

public class ElementNumberInput extends Element implements ITextInput {
   public ArrayList<GuiTextField> textFields;
   public int selectedTextField;
   public boolean allowDecimal;
   public String tooltip;
   public int spaceL;
   public int spaceR;
   public double min;
   public double max;

   public ElementNumberInput(Window window, int x, int y, int w, int h, int ID, String tip, int fieldCount, boolean allowDec, double minn, double maxx, double... args) {
      super(window, x, y, w, h, ID, false);
      this.textFields = new ArrayList();
      this.selectedTextField = -1;

      for(int i = 0; i < fieldCount; ++i) {
         GuiTextField textField = new GuiTextField(0, this.parent.workspace.getFontRenderer(), this.parent.posX + this.posX + 2 + this.width / fieldCount * i, this.parent.posY + this.posY + 2, this.width / fieldCount - 18, this.parent.workspace.getFontRenderer().FONT_HEIGHT);
         textField.setMaxStringLength(20);
         textField.setEnableBackgroundDrawing(false);
         textField.setTextColor(Theme.getAsHex(this.parent.workspace.currentTheme.font));
         textField.setCanLoseFocus(false);
         if (i < args.length) {
            textField.setText(allowDec ? Double.toString(args[i]) : Integer.toString((int)args[i]));
         }

         this.textFields.add(textField);
      }

      this.allowDecimal = allowDec;
      this.tooltip = tip;
      this.spaceL = this.posX;
      this.spaceR = this.parent.width - this.posX - this.width;
      this.min = minn;
      this.max = maxx;
   }

   public ElementNumberInput(Window window, int x, int y, int w, int h, int ID, String tip, int fieldCount, boolean allowDec, double minn, double maxx) {
      this(window, x, y, w, h, ID, tip, fieldCount, allowDec, minn, maxx);
   }

   public ElementNumberInput(Window window, int x, int y, int w, int h, int ID, String tip, int fieldCount, boolean allowDec) {
      this(window, x, y, w, h, ID, tip, fieldCount, allowDec, -2.147483648E9D, 2.147483647E9D);
   }

   public void update() {
      if (this.selectedTextField != -1) {
         ((GuiTextField)this.textFields.get(this.selectedTextField)).updateCursorCounter();
      }

   }

   public boolean mouseScroll(int mouseX, int mouseY, int k) {
      for(int i = 0; i < this.textFields.size(); ++i) {
         int x1 = this.posX + this.width / this.textFields.size() * i;
         int x2 = this.posX + this.width / this.textFields.size() * (i + 1);
         int y1 = this.posY;
         int y2 = this.posY + this.height;
         if (mouseX >= x1 && mouseX < x2 && mouseY >= y1 && mouseY < y2) {
            if (this.selectedTextField != -1) {
               ((GuiTextField)this.textFields.get(this.selectedTextField)).setFocused(false);
            }

            this.selectedTextField = i;
            ((GuiTextField)this.textFields.get(this.selectedTextField)).setFocused(true);
            this.changeValue(k > 0);
            break;
         }
      }

      return true;
   }

   public boolean onClick(int mouseX, int mouseY, int id) {
      if (this.selectedTextField != -1 && ((GuiTextField)this.textFields.get(this.selectedTextField)).isFocused()) {
         ((GuiTextField)this.textFields.get(this.selectedTextField)).mouseClicked(this.parent.posX + mouseX + 2, this.parent.posY + mouseY + 2, id);
      }

      for(int i = 0; i < this.textFields.size(); ++i) {
         int x1 = this.posX + this.width / this.textFields.size() * i;
         int x2 = this.posX + this.width / this.textFields.size() * (i + 1) - 11;
         int y1 = this.posY;
         int y2 = this.posY + this.height;
         if (mouseX >= x1 && mouseX < x2 && mouseY >= y1 && mouseY < y2) {
            if (this.selectedTextField != -1) {
               ((GuiTextField)this.textFields.get(this.selectedTextField)).setFocused(false);
            }

            this.selectedTextField = i;
            ((GuiTextField)this.textFields.get(this.selectedTextField)).setFocused(true);
            if (id == 1) {
               ((GuiTextField)this.textFields.get(this.selectedTextField)).setText("");
            }
            break;
         }

         x1 = this.posX + this.width / this.textFields.size() * (i + 1) - 12;
         x2 = this.posX + this.width / this.textFields.size() * (i + 1);
         if (mouseX >= x1 && mouseX < x2 && mouseY >= y1 && mouseY <= y1 + 6 && id == 0) {
            if (this.selectedTextField != -1) {
               ((GuiTextField)this.textFields.get(this.selectedTextField)).setFocused(false);
            }

            this.selectedTextField = i;
            ((GuiTextField)this.textFields.get(this.selectedTextField)).setFocused(true);
            String text = ((GuiTextField)this.textFields.get(this.selectedTextField)).getText();
            if (!text.isEmpty()) {
               if (this.allowDecimal) {
                  String val = String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(text) + (GuiScreen.isShiftKeyDown() ? 1.0D : (GuiScreen.isCtrlKeyDown() ? 0.01D : 0.1D)));
                  if (val.contains(".") && val.length() > val.indexOf(".") + 4) {
                     val = val.substring(0, val.indexOf(".") + 4);
                  }

                  ((GuiTextField)this.textFields.get(this.selectedTextField)).setText(val);
                  if (Double.parseDouble(((GuiTextField)this.textFields.get(this.selectedTextField)).getText()) < this.min) {
                     ((GuiTextField)this.textFields.get(this.selectedTextField)).setText(Double.toString(this.min));
                  }

                  if (Double.parseDouble(((GuiTextField)this.textFields.get(this.selectedTextField)).getText()) > this.max) {
                     ((GuiTextField)this.textFields.get(this.selectedTextField)).setText(Double.toString(this.max));
                  }
               } else {
                  ((GuiTextField)this.textFields.get(this.selectedTextField)).setText(Integer.toString(Integer.parseInt(text) + 1));
                  if ((double)Integer.parseInt(((GuiTextField)this.textFields.get(this.selectedTextField)).getText()) < this.min) {
                     ((GuiTextField)this.textFields.get(this.selectedTextField)).setText(Integer.toString((int)this.min));
                  }

                  if ((double)Integer.parseInt(((GuiTextField)this.textFields.get(this.selectedTextField)).getText()) > this.max) {
                     ((GuiTextField)this.textFields.get(this.selectedTextField)).setText(Integer.toString((int)this.max));
                  }
               }
            }

            this.parent.elementTriggered(this);
            break;
         }

         if (mouseX >= x1 && mouseX < x2 && mouseY >= y1 + 7 && mouseY <= y1 + 12 && id == 0) {
            if (this.selectedTextField != -1) {
               ((GuiTextField)this.textFields.get(this.selectedTextField)).setFocused(false);
            }

            this.selectedTextField = i;
            ((GuiTextField)this.textFields.get(this.selectedTextField)).setFocused(true);
            String text = ((GuiTextField)this.textFields.get(this.selectedTextField)).getText();
            if (!text.isEmpty()) {
               if (this.allowDecimal) {
                  String val = String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(text) - (GuiScreen.isShiftKeyDown() ? 1.0D : (GuiScreen.isCtrlKeyDown() ? 0.01D : 0.1D)));
                  if (val.contains(".") && val.length() > val.indexOf(".") + 4) {
                     val = val.substring(0, val.indexOf(".") + 4);
                  }

                  ((GuiTextField)this.textFields.get(this.selectedTextField)).setText(val);
                  if (Double.parseDouble(((GuiTextField)this.textFields.get(this.selectedTextField)).getText()) < this.min) {
                     ((GuiTextField)this.textFields.get(this.selectedTextField)).setText(Double.toString(this.min));
                  }

                  if (Double.parseDouble(((GuiTextField)this.textFields.get(this.selectedTextField)).getText()) > this.max) {
                     ((GuiTextField)this.textFields.get(this.selectedTextField)).setText(Double.toString(this.max));
                  }
               } else {
                  ((GuiTextField)this.textFields.get(this.selectedTextField)).setText(Integer.toString(Integer.parseInt(text) - 1));
                  if ((double)Integer.parseInt(((GuiTextField)this.textFields.get(this.selectedTextField)).getText()) < this.min) {
                     ((GuiTextField)this.textFields.get(this.selectedTextField)).setText(Integer.toString((int)this.min));
                  }

                  if ((double)Integer.parseInt(((GuiTextField)this.textFields.get(this.selectedTextField)).getText()) > this.max) {
                     ((GuiTextField)this.textFields.get(this.selectedTextField)).setText(Integer.toString((int)this.max));
                  }
               }
            }

            for(GuiTextField text1 : this.textFields) {
               if (text1.getText().isEmpty() || text1.getText().equals("-")) {
                  text1.setText("0");
               }
            }

            this.parent.elementTriggered(this);
            break;
         }
      }

      return true;
   }

   public void draw(int mouseX, int mouseY, boolean hover) {
      RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputBorder[0], this.parent.workspace.currentTheme.elementInputBorder[1], this.parent.workspace.currentTheme.elementInputBorder[2], 255, (double)this.getPosX(), (double)this.getPosY(), (double)(this.width - 2), (double)this.height, 0.0D);
      RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputBackgroundInactive[0], this.parent.workspace.currentTheme.elementInputBackgroundInactive[1], this.parent.workspace.currentTheme.elementInputBackgroundInactive[2], 255, (double)(this.getPosX() + 1), (double)(this.getPosY() + 1), (double)(this.width - 2 - 2), (double)(this.height - 2), 0.0D);

      for(int i = 0; i < this.textFields.size(); ++i) {
         int x1 = this.posX + this.width / this.textFields.size() * i;
         int x2 = this.posX + this.width / this.textFields.size() * (i + 1) - 11;
         int y1 = this.posY;
         int y2 = this.posY + this.height;
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputBorder[0], this.parent.workspace.currentTheme.elementInputBorder[1], this.parent.workspace.currentTheme.elementInputBorder[2], 255, (double)(this.getPosX() + this.width / this.textFields.size() * i), (double)(this.getPosY() + 1), 1.0D, (double)(this.height - 2), 0.0D);
         if (mouseX >= x1 && mouseX < x2 && mouseY >= y1 && mouseY < y2) {
            RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputBackgroundHover[0], this.parent.workspace.currentTheme.elementInputBackgroundHover[1], this.parent.workspace.currentTheme.elementInputBackgroundHover[2], 255, (double)(this.getPosX() + this.width / this.textFields.size() * i + 1), (double)(this.getPosY() + 1), (double)(x2 - x1 - 2), (double)(this.height - 2), 0.0D);
         }

         if (((GuiTextField)this.textFields.get(i)).getVisible()) {
            ((GuiTextField)this.textFields.get(i)).drawTextBox();
         }

         x1 = this.posX + this.width / this.textFields.size() * (i + 1) - 12;
         x2 = this.posX + this.width / this.textFields.size() * (i + 1);
         if (mouseX >= x1 && mouseX < x2 && mouseY >= y1 && mouseY <= y1 + 6) {
            if (Mouse.isButtonDown(0)) {
               RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputUpDownClick[0], this.parent.workspace.currentTheme.elementInputUpDownClick[1], this.parent.workspace.currentTheme.elementInputUpDownClick[2], 255, (double)(this.getPosX() + this.width / this.textFields.size() * (i + 1) - 12), (double)this.getPosY(), 12.0D, 6.0D, 0.0D);
            } else {
               RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputUpDownHover[0], this.parent.workspace.currentTheme.elementInputUpDownHover[1], this.parent.workspace.currentTheme.elementInputUpDownHover[2], 255, (double)(this.getPosX() + this.width / this.textFields.size() * (i + 1) - 12), (double)this.getPosY(), 12.0D, 6.0D, 0.0D);
            }
         } else {
            RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputBorder[0], this.parent.workspace.currentTheme.elementInputBorder[1], this.parent.workspace.currentTheme.elementInputBorder[2], 255, (double)(this.getPosX() + this.width / this.textFields.size() * (i + 1) - 12), (double)this.getPosY(), 12.0D, 6.0D, 0.0D);
         }

         if (mouseX >= x1 && mouseX < x2 && mouseY >= y1 + 7 && mouseY <= y1 + 12) {
            if (Mouse.isButtonDown(0)) {
               RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputUpDownClick[0], this.parent.workspace.currentTheme.elementInputUpDownClick[1], this.parent.workspace.currentTheme.elementInputUpDownClick[2], 255, (double)(this.getPosX() + this.width / this.textFields.size() * (i + 1) - 12), (double)(this.getPosY() + 6), 12.0D, 6.0D, 0.0D);
            } else {
               RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputUpDownHover[0], this.parent.workspace.currentTheme.elementInputUpDownHover[1], this.parent.workspace.currentTheme.elementInputUpDownHover[2], 255, (double)(this.getPosX() + this.width / this.textFields.size() * (i + 1) - 12), (double)(this.getPosY() + 6), 12.0D, 6.0D, 0.0D);
            }
         } else {
            RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementInputBorder[0], this.parent.workspace.currentTheme.elementInputBorder[1], this.parent.workspace.currentTheme.elementInputBorder[2], 255, (double)(this.getPosX() + this.width / this.textFields.size() * (i + 1) - 12), (double)(this.getPosY() + 6), 12.0D, 6.0D, 0.0D);
         }

         GlStateManager.pushMatrix();
         float scale = 1.0F;
         GlStateManager.scale(scale, scale, scale);
         this.parent.workspace.getFontRenderer().drawString("▲", (float)((int)((float)(this.getPosX() + this.width / this.textFields.size() * (i + 1) - 8) / scale)), (float)((int)((float)(this.getPosY() - 1) / scale)), Theme.getAsHex(this.parent.workspace.currentTheme.font), false);
         this.parent.workspace.getFontRenderer().drawString("▼", (float)((int)((float)(this.getPosX() + this.width / this.textFields.size() * (i + 1) - 8) / scale)), (float)((int)((float)(this.getPosY() + 5) / scale)), Theme.getAsHex(this.parent.workspace.currentTheme.font), false);
         GlStateManager.popMatrix();
      }

   }

   public void keyInput(char c, int key) {
      if (key == 15) {
         this.tabHit();
      } else {
         if (this.selectedTextField != -1) {
            if (32 == key) {
               return;
            }

            if (key != 28) {
               if (key == 200 || key == 208) {
                  this.changeValue(key == 200);
               }
            } else {
               for(GuiTextField text : this.textFields) {
                  if (text.getText().isEmpty() || text.getText().equals("-")) {
                     text.setText("0");
                  }
               }

               this.parent.elementTriggered(this);
            }

            String oldText = ((GuiTextField)this.textFields.get(this.selectedTextField)).getText();
            ((GuiTextField)this.textFields.get(this.selectedTextField)).textboxKeyTyped(c, key);
            if (!((GuiTextField)this.textFields.get(this.selectedTextField)).getText().isEmpty() && !(((GuiTextField)this.textFields.get(this.selectedTextField)).getText().startsWith("-") & ((GuiTextField)this.textFields.get(this.selectedTextField)).getText().length() == 1)) {
               try {
                  if (this.allowDecimal) {
                     Double.parseDouble(((GuiTextField)this.textFields.get(this.selectedTextField)).getText());
                  } else {
                     Integer.parseInt(((GuiTextField)this.textFields.get(this.selectedTextField)).getText());
                  }
               } catch (NumberFormatException var6) {
                  int pos = ((GuiTextField)this.textFields.get(this.selectedTextField)).getCursorPosition();
                  ((GuiTextField)this.textFields.get(this.selectedTextField)).setText(oldText);
                  ((GuiTextField)this.textFields.get(this.selectedTextField)).setCursorPosition(pos - 1);
                  return;
               }
            }
         }

      }
   }

   public void changeValue(boolean increment) {
      String text = ((GuiTextField)this.textFields.get(this.selectedTextField)).getText();
      if (!text.isEmpty()) {
         if (this.allowDecimal) {
            String val = String.format(Locale.ENGLISH, "%.2f", Double.parseDouble(text) + (increment ? (GuiScreen.isShiftKeyDown() ? 1.0D : (GuiScreen.isCtrlKeyDown() ? 0.01D : 0.1D)) : -(GuiScreen.isShiftKeyDown() ? 1.0D : (GuiScreen.isCtrlKeyDown() ? 0.01D : 0.1D))));
            if (val.contains(".") && val.length() > val.indexOf(".") + 4) {
               val = val.substring(0, val.indexOf(".") + 4);
            }

            ((GuiTextField)this.textFields.get(this.selectedTextField)).setText(val);
            if (Double.parseDouble(((GuiTextField)this.textFields.get(this.selectedTextField)).getText()) < this.min) {
               ((GuiTextField)this.textFields.get(this.selectedTextField)).setText(Double.toString(this.min));
            }

            if (Double.parseDouble(((GuiTextField)this.textFields.get(this.selectedTextField)).getText()) > this.max) {
               ((GuiTextField)this.textFields.get(this.selectedTextField)).setText(Double.toString(this.max));
            }
         } else {
            ((GuiTextField)this.textFields.get(this.selectedTextField)).setText(Integer.toString(Integer.parseInt(text) + (increment ? 1 : -1)));
            if ((double)Integer.parseInt(((GuiTextField)this.textFields.get(this.selectedTextField)).getText()) < this.min) {
               ((GuiTextField)this.textFields.get(this.selectedTextField)).setText(Integer.toString((int)this.min));
            }

            if ((double)Integer.parseInt(((GuiTextField)this.textFields.get(this.selectedTextField)).getText()) > this.max) {
               ((GuiTextField)this.textFields.get(this.selectedTextField)).setText(Integer.toString((int)this.max));
            }
         }
      } else {
         ((GuiTextField)this.textFields.get(this.selectedTextField)).setText("0");
      }

      this.parent.elementTriggered(this);
   }

   public void selected() {
   }

   public void deselected() {
      if (this.selectedTextField != -1) {
         ((GuiTextField)this.textFields.get(this.selectedTextField)).setFocused(false);
      }

      this.selectedTextField = -1;

      for(int i = 0; i < this.textFields.size(); ++i) {
         if (((GuiTextField)this.textFields.get(i)).getText().isEmpty() || ((GuiTextField)this.textFields.get(i)).getText().equals("-")) {
            ((GuiTextField)this.textFields.get(i)).setText("0");
         }
      }

      this.resized();
   }

   public void resized() {
      this.width = this.parent.width - this.spaceL - this.spaceR;

      for(int i = 0; i < this.textFields.size(); ++i) {
         ((GuiTextField)this.textFields.get(i)).x = this.parent.posX + this.posX + 2 + this.width / this.textFields.size() * i;
         ((GuiTextField)this.textFields.get(i)).y = this.parent.posY + this.posY + 2;
         ((GuiTextField)this.textFields.get(i)).width = this.width / this.textFields.size() - 18;
         ((GuiTextField)this.textFields.get(i)).setCursorPositionZero();
      }

   }

   public String tooltip() {
      return this.tooltip;
   }

   public void tabHit() {
      if (this.selectedTextField < this.textFields.size() - 1) {
         if (this.selectedTextField != -1) {
            ((GuiTextField)this.textFields.get(this.selectedTextField)).setFocused(false);
         }

         ++this.selectedTextField;
         ((GuiTextField)this.textFields.get(this.selectedTextField)).setFocused(true);
      } else {
         if (this.selectedTextField != -1) {
            ((GuiTextField)this.textFields.get(this.selectedTextField)).setFocused(false);
         }

         this.selectedTextField = -1;
         boolean found = false;
         boolean foundSelf = false;

         for(int i = 0; i < this.parent.elements.size(); ++i) {
            Element e = (Element)this.parent.elements.get(i);
            if (e == this) {
               foundSelf = true;
            } else if (e instanceof ITextInput && foundSelf) {
               found = true;
               ((ITextInput)e).cycledTo();
               break;
            }
         }

         if (!found) {
            for(int i = 0; i < this.parent.elements.size(); ++i) {
               Element e = (Element)this.parent.elements.get(i);
               if (e instanceof ITextInput) {
                  ((ITextInput)e).cycledTo();
                  break;
               }
            }
         }
      }

   }

   public void cycledTo() {
      this.parent.workspace.elementSelected = this;
      if (this.selectedTextField != -1) {
         ((GuiTextField)this.textFields.get(this.selectedTextField)).setFocused(false);
      }

      this.selectedTextField = 0;
      ((GuiTextField)this.textFields.get(this.selectedTextField)).setFocused(true);
   }
}
