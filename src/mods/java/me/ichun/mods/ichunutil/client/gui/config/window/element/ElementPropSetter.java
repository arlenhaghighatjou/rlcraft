package me.ichun.mods.ichunutil.client.gui.config.window.element;

import java.lang.reflect.Field;
import java.util.ArrayList;
import me.ichun.mods.ichunutil.client.gui.Theme;
import me.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowSetIntArray;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowSetKeyBind;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowSetNestedIntArray;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowSetStringArray;
import me.ichun.mods.ichunutil.client.gui.window.IWorkspace;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementNumberInput;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementTextInput;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementToggle;
import me.ichun.mods.ichunutil.client.keybind.KeyBind;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;
import me.ichun.mods.ichunutil.common.core.config.types.Colour;
import me.ichun.mods.ichunutil.common.core.config.types.NestedIntArray;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Mouse;

public class ElementPropSetter extends Element {
   public int spacerL;
   public int spacerR;
   public int spacerU;
   public int spacerD;
   public double sliderProg = 0.0D;
   public ArrayList<ElementPropSetter.Tree> trees = new ArrayList();
   public ElementPropSetter.Tree treeClicked;
   public int clickTimeout;
   public int saveTimeout;
   public boolean lmbDown;
   public boolean rmbDown;
   public int space;
   public int mX;
   public int mY;
   public String selectedIdentifier;

   public ElementPropSetter(Window window, int x, int y, int w, int h, int ID) {
      super(window, x, y, w, h, ID, false);
      this.spacerL = x;
      this.spacerR = this.parent.width - x - this.width;
      this.spacerU = y;
      this.spacerD = this.parent.height - y - this.height;
      this.selectedIdentifier = "";
   }

   public void update() {
      if (this.clickTimeout > 0) {
         --this.clickTimeout;
      }

      if (this.saveTimeout > 0) {
         --this.saveTimeout;
         if (this.saveTimeout == 0) {
            this.save();
         }
      }

      for(ElementPropSetter.Tree tree : this.trees) {
         if (tree.element != null) {
            tree.element.update();
         }
      }

   }

   public void draw(int mouseX, int mouseY, boolean hover) {
      this.mX = mouseX;
      this.mY = mouseY;
      int x1 = this.getPosX();
      int x2 = this.getPosX() + this.width;
      int y1 = this.getPosY();
      int y2 = this.getPosY() + this.height;
      int longestName = 0;
      int treeHeight1 = 0;

      for(int i = 0; i < this.trees.size(); ++i) {
         ElementPropSetter.Tree tree = (ElementPropSetter.Tree)this.trees.get(i);
         treeHeight1 += tree.getHeight();
         if (this.parent.workspace.getFontRenderer().getStringWidth(tree.propInfo.name) > longestName) {
            longestName = this.parent.workspace.getFontRenderer().getStringWidth(tree.propInfo.name);
         }
      }

      this.space = Math.min(200, Math.max(this.width - 40 - longestName, 50));
      RendererHelper.endGlScissor();
      RendererHelper.startGlScissor(this.getPosX(), this.getPosY() - 1, this.width + 2, this.height + 3);
      if (treeHeight1 > this.height) {
         x2 -= 10;
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeScrollBarBorder[0], this.parent.workspace.currentTheme.elementTreeScrollBarBorder[1], this.parent.workspace.currentTheme.elementTreeScrollBarBorder[2], 255, (double)(x2 + 5), (double)(this.getPosY() + this.height / 40), 2.0D, (double)(this.height - this.height / 40 * 2), 0.0D);
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeScrollBarBorder[0], this.parent.workspace.currentTheme.elementTreeScrollBarBorder[1], this.parent.workspace.currentTheme.elementTreeScrollBarBorder[2], 255, (double)(x2 + 1), (double)(this.getPosY() - 1) + (double)(this.height - this.height / 11) * this.sliderProg, 10.0D, (double)(this.height / 10), 0.0D);
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeScrollBar[0], this.parent.workspace.currentTheme.elementTreeScrollBar[1], this.parent.workspace.currentTheme.elementTreeScrollBar[2], 255, (double)(x2 + 2), (double)this.getPosY() + (double)(this.height - this.height / 11) * this.sliderProg, 8.0D, (double)(this.height / 10 - 2), 0.0D);
         int sbx1 = x2 + 1 - this.parent.posX;
         int sbx2 = sbx1 + 10;
         int sby1 = this.getPosY() - 1 - this.parent.posY;
         int sby2 = this.getPosY() + this.height - this.parent.posY;
         if (Mouse.isButtonDown(0) && mouseX >= sbx1 && mouseX <= sbx2 && mouseY >= sby1 && mouseY <= sby2) {
            sby1 = sby1 + 10;
            sby2 = sby2 - 10;
            this.sliderProg = 1.0D - MathHelper.clamp((double)(sby2 - mouseY) / (double)(sby2 - sby1), 0.0D, 1.0D);
         }
      }

      RendererHelper.startGlScissor(this.getPosX(), this.getPosY(), this.width + 2, this.height + 2);
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.0D, -((double)(treeHeight1 - this.height) * this.sliderProg), 0.0D);
      int treeHeight = 0;

      for(int i = 0; i < this.trees.size(); ++i) {
         ElementPropSetter.Tree tree = (ElementPropSetter.Tree)this.trees.get(i);
         tree.draw(mouseX, mouseY, hover, x2 - x1, treeHeight, treeHeight1 > this.height, treeHeight1, Mouse.isButtonDown(0) && !this.lmbDown, Mouse.isButtonDown(1) && !this.rmbDown);
         treeHeight += tree.getHeight();
      }

      GlStateManager.popMatrix();
      RendererHelper.endGlScissor();
      if (this.parent.isTab) {
         RendererHelper.startGlScissor(this.parent.posX + 1, this.parent.posY + 1 + 12, this.parent.getWidth() - 2, this.parent.getHeight() - 2 - 12);
      } else {
         RendererHelper.startGlScissor(this.parent.posX + 1, this.parent.posY + 1, this.parent.getWidth() - 2, this.parent.getHeight() - 2);
      }

      if (this.parent.docked < 0) {
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeBorder[0], this.parent.workspace.currentTheme.elementTreeBorder[1], this.parent.workspace.currentTheme.elementTreeBorder[2], 255, (double)(x1 - 1), (double)(y1 - 1), (double)(x2 - x1 + 1), 1.0D, 0.0D);
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeBorder[0], this.parent.workspace.currentTheme.elementTreeBorder[1], this.parent.workspace.currentTheme.elementTreeBorder[2], 255, (double)(x1 - 1), (double)(y1 - 1), 1.0D, (double)(this.height + 2), 0.0D);
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeBorder[0], this.parent.workspace.currentTheme.elementTreeBorder[1], this.parent.workspace.currentTheme.elementTreeBorder[2], 255, (double)(x1 - 1), (double)(y2 + 1), (double)(x2 - x1 + 2), 1.0D, 0.0D);
         RendererHelper.drawColourOnScreen(this.parent.workspace.currentTheme.elementTreeBorder[0], this.parent.workspace.currentTheme.elementTreeBorder[1], this.parent.workspace.currentTheme.elementTreeBorder[2], 255, (double)x2, (double)(y1 - 1), 1.0D, (double)(this.height + 2), 0.0D);
      }

      this.lmbDown = Mouse.isButtonDown(0);
      this.rmbDown = Mouse.isButtonDown(1);
   }

   public void resized() {
      this.posX = this.spacerL;
      this.width = this.parent.width - this.posX - this.spacerR;
      this.posY = this.spacerU;
      this.height = this.parent.height - this.posY - this.spacerD;
      this.sliderProg = 0.0D;

      for(ElementPropSetter.Tree tree : this.trees) {
         tree.resized();
      }

   }

   public boolean mouseScroll(int mouseX, int mouseY, int k) {
      int treeHeight = 0;
      int treeHeight1 = 0;

      for(int i = 0; i < this.trees.size(); ++i) {
         ElementPropSetter.Tree tree = (ElementPropSetter.Tree)this.trees.get(i);
         treeHeight1 += tree.getHeight();
      }

      int scrollHeight = 0;
      if (treeHeight1 > this.height) {
         scrollHeight = (int)((double)(this.height - treeHeight1) * this.sliderProg);
      }

      boolean reacted = false;

      for(int i = 0; i < this.trees.size(); ++i) {
         ElementPropSetter.Tree tree = (ElementPropSetter.Tree)this.trees.get(i);
         if (this.mX >= this.posX && this.mX < this.posX + this.width + (treeHeight1 > this.height ? 10 : 0) && this.mY >= this.posY + treeHeight + scrollHeight && this.mY < this.posY + treeHeight + scrollHeight + tree.getHeight() && tree.selected) {
            if (tree.element != null) {
               tree.element.mouseScroll(mouseX, mouseY - scrollHeight, k);
               this.saveTimeout = 10;
            }

            reacted = true;
            break;
         }

         treeHeight += tree.getHeight();
      }

      if (!reacted && treeHeight1 > this.height) {
         this.sliderProg += 0.05D * (double)(-k);
         this.sliderProg = MathHelper.clamp(this.sliderProg, 0.0D, 1.0D);
      }

      return false;
   }

   public String tooltip() {
      int treeHeight = 0;
      int treeHeight1 = 0;

      for(int i = 0; i < this.trees.size(); ++i) {
         ElementPropSetter.Tree tree = (ElementPropSetter.Tree)this.trees.get(i);
         treeHeight1 += tree.getHeight();
      }

      int scrollHeight = 0;
      if (treeHeight1 > this.height) {
         scrollHeight = (int)((double)(this.height - treeHeight1) * this.sliderProg);
      }

      for(int i = 0; i < this.trees.size(); ++i) {
         ElementPropSetter.Tree tree = (ElementPropSetter.Tree)this.trees.get(i);
         if (this.mX >= this.posX && this.mX < this.posX + this.width + (treeHeight1 > this.height ? 10 : 0) && this.mY >= this.posY + treeHeight + scrollHeight && this.mY < this.posY + treeHeight + scrollHeight + tree.getHeight()) {
            return tree.propInfo.comment;
         }

         treeHeight += tree.getHeight();
      }

      return null;
   }

   public void createTree(ConfigBase conf, ConfigBase.PropInfo obj, int h) {
      this.trees.add(new ElementPropSetter.Tree(conf, obj, h));
   }

   public void clickElement(Object obj) {
   }

   public void triggerParent() {
      this.parent.elementTriggered(this);
   }

   public void save() {
      ConfigBase config = null;

      for(ElementPropSetter.Tree tree : this.trees) {
         if (tree.element != null) {
            tree.config.save();
            break;
         }
      }

      if (ConfigHandler.configKeybind.hasChanged()) {
         ConfigHandler.configKeybind.save();
      }

   }

   public class Tree {
      public ConfigBase config;
      public ConfigBase.PropInfo propInfo;
      public Element element;
      private int theHeight;
      public boolean selected;

      public Tree(ConfigBase conf, ConfigBase.PropInfo obj, int h) {
         this.config = conf;
         this.propInfo = obj;
         this.theHeight = h;

         try {
            obj.field.setAccessible(true);
            Class clz = obj.field.getType();
            if (!clz.equals(Integer.TYPE) && !clz.equals(Integer.class)) {
               if (clz.equals(Colour.class)) {
                  this.element = new ElementTextInput(ElementPropSetter.this.parent, 0, 0, 40, 12, 0, "", Integer.toHexString(((Colour)obj.field.get(conf)).getColour()));
                  ((ElementTextInput)this.element).textField.setMaxStringLength(6);
               } else if (clz.equals(String.class)) {
                  this.element = new ElementTextInput(ElementPropSetter.this.parent, 0, 0, 40, 12, 0, "", (String)obj.field.get(conf));
               } else if (clz.equals(KeyBind.class)) {
                  this.element = new ElementButton(ElementPropSetter.this.parent, 0, 0, 0, 12, 0, false, 0, 0, "");
               } else if (clz.equals(String[].class) || clz.equals(int[].class) || clz.equals(NestedIntArray.class)) {
                  this.element = new ElementButton(ElementPropSetter.this.parent, 0, 0, 0, 12, 0, false, 0, 0, "Set");
               }
            } else {
               int[] minmax = new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE};
               if (obj.field.isAnnotationPresent(IntMinMax.class)) {
                  IntMinMax minMax = (IntMinMax)obj.field.getAnnotation(IntMinMax.class);
                  minmax[0] = minMax.min();
                  minmax[1] = minMax.max();
                  this.element = new ElementNumberInput(ElementPropSetter.this.parent, 0, 0, 200, 12, 0, "", 1, false, (double)minmax[0], (double)minmax[1], new double[]{(double)obj.field.getInt(conf)});
               } else if (obj.field.isAnnotationPresent(IntBool.class)) {
                  this.element = new ElementToggle(ElementPropSetter.this.parent, 0, 0, 0, 12, 0, false, 0, 0, "gui.yes", (String)null, obj.field.getInt(conf) == 1);
               } else {
                  this.element = new ElementNumberInput(ElementPropSetter.this.parent, 0, 0, 200, 12, 0, "", 1, false, (double)minmax[0], (double)minmax[1], new double[]{(double)obj.field.getInt(conf)});
               }
            }
         } catch (Exception var8) {
            ;
         }

      }

      public void resized() {
      }

      public int getHeight() {
         return this.theHeight;
      }

      public ElementPropSetter.Tree draw(int mouseX, int mouseY, boolean hover, int width, int treeHeight, boolean hasScroll, int totalHeight, boolean clicking, boolean rClicking) {
         double scrollHeight = 0.0D;
         if (hasScroll) {
            scrollHeight = (double)(ElementPropSetter.this.height - totalHeight) * ElementPropSetter.this.sliderProg;
         }

         boolean realBorder = mouseX >= ElementPropSetter.this.posX && mouseX < ElementPropSetter.this.posX + width && (double)mouseY >= (double)(ElementPropSetter.this.posY + treeHeight) + scrollHeight && (double)mouseY < (double)(ElementPropSetter.this.posY + treeHeight) + scrollHeight + (double)this.theHeight;
         int offX = 0;
         int offY = 0;
         if (this.selected) {
            RendererHelper.drawColourOnScreen(ElementPropSetter.this.parent.workspace.currentTheme.elementTreeItemBgSelect[0], ElementPropSetter.this.parent.workspace.currentTheme.elementTreeItemBgSelect[1], ElementPropSetter.this.parent.workspace.currentTheme.elementTreeItemBgSelect[2], 255, (double)(ElementPropSetter.this.getPosX() + offX), (double)(ElementPropSetter.this.getPosY() + offY + treeHeight), (double)width, (double)this.theHeight, 0.0D);
         } else if (realBorder) {
            RendererHelper.drawColourOnScreen(ElementPropSetter.this.parent.workspace.currentTheme.elementTreeItemBgHover[0], ElementPropSetter.this.parent.workspace.currentTheme.elementTreeItemBgHover[1], ElementPropSetter.this.parent.workspace.currentTheme.elementTreeItemBgHover[2], 255, (double)(ElementPropSetter.this.getPosX() + offX), (double)(ElementPropSetter.this.getPosY() + offY + treeHeight), (double)width, (double)this.theHeight, 0.0D);
         } else {
            RendererHelper.drawColourOnScreen(ElementPropSetter.this.parent.workspace.currentTheme.elementTreeItemBorder[0], ElementPropSetter.this.parent.workspace.currentTheme.elementTreeItemBorder[1], ElementPropSetter.this.parent.workspace.currentTheme.elementTreeItemBorder[2], 255, (double)(ElementPropSetter.this.getPosX() + offX), (double)(ElementPropSetter.this.getPosY() + offY + treeHeight), (double)width, (double)this.theHeight, 0.0D);
         }

         if (realBorder && hasScroll && (mouseY > ElementPropSetter.this.height + ElementPropSetter.this.posY || mouseY <= ElementPropSetter.this.posY)) {
            clicking = false;
         }

         ElementPropSetter.this.parent.workspace.getFontRenderer().drawString(ElementPropSetter.this.parent.workspace.reString(this.propInfo.name, width - 60), (float)(ElementPropSetter.this.getPosX() + offX + 4), (float)(ElementPropSetter.this.getPosY() + offY + (this.theHeight - ElementPropSetter.this.parent.workspace.getFontRenderer().FONT_HEIGHT) / 2 + treeHeight), Theme.getAsHex(ElementPropSetter.this.parent.workspace.currentTheme.font), false);
         this.propInfo.field.setAccessible(true);
         Class clz = this.propInfo.field.getType();
         boolean found = false;
         boolean obstructed = false;

         for(int i = ElementPropSetter.this.parent.workspace.levels.size() - 1; i >= 0 && !found; --i) {
            for(int j = 0; j < ((ArrayList)ElementPropSetter.this.parent.workspace.levels.get(i)).size(); ++j) {
               Window window = (Window)((ArrayList)ElementPropSetter.this.parent.workspace.levels.get(i)).get(j);
               if (window == ElementPropSetter.this.parent) {
                  found = true;
                  break;
               }

               if (mouseX >= window.posX && mouseX <= window.posX + window.getWidth() && mouseY >= window.posY && mouseY <= window.posY + window.getHeight()) {
                  obstructed = true;
                  break;
               }
            }
         }

         if (realBorder && clicking && !obstructed) {
            this.selected = true;
            this.deselectOthers(ElementPropSetter.this.trees);
            ElementPropSetter.this.clickElement(this.propInfo);
            ElementPropSetter.this.parent.workspace.elementSelected = this.element;
            ElementPropSetter.this.parent.workspace.elementSelected.selected();
            ElementPropSetter.this.parent.workspace.elementSelected.onClick(mouseX, mouseY - (int)scrollHeight, 0);
            if (clz.equals(KeyBind.class)) {
               if (((GuiConfigs)ElementPropSetter.this.parent.workspace).keyBindTimeout == 0) {
                  ElementPropSetter.this.parent.workspace.addWindowOnTop((new WindowSetKeyBind((GuiConfigs)ElementPropSetter.this.parent.workspace, 300, 200, 180, 80, "ichunutil.config.gui.hitKeys", this.config, this.propInfo)).putInMiddleOfScreen());
               }
            } else if (clz.equals(String[].class)) {
               ElementPropSetter.this.parent.workspace.addWindowOnTop((new WindowSetStringArray((GuiConfigs)ElementPropSetter.this.parent.workspace, 300, 200, 180, 80, this.config, this.propInfo)).putInMiddleOfScreen());
            } else if (clz.equals(int[].class)) {
               ElementPropSetter.this.parent.workspace.addWindowOnTop((new WindowSetIntArray((GuiConfigs)ElementPropSetter.this.parent.workspace, 300, 200, 180, 80, this.config, this.propInfo)).putInMiddleOfScreen());
            } else if (clz.equals(NestedIntArray.class)) {
               ElementPropSetter.this.parent.workspace.addWindowOnTop((new WindowSetNestedIntArray((GuiConfigs)ElementPropSetter.this.parent.workspace, 300, 200, 180, 80, this.config, this.propInfo)).putInMiddleOfScreen());
            }

            if (ElementPropSetter.this.clickTimeout > 0 && ElementPropSetter.this.treeClicked == this) {
               ElementPropSetter.this.triggerParent();
            }

            ElementPropSetter.this.treeClicked = this;
            ElementPropSetter.this.clickTimeout = 10;
         }

         if (this.element != null) {
            int x = ElementPropSetter.this.posX + width - ElementPropSetter.this.space - 3 - 0;
            int y = ElementPropSetter.this.posY + offY + (this.theHeight - ElementPropSetter.this.parent.workspace.getFontRenderer().FONT_HEIGHT) / 2 + treeHeight - 2;
            int w = ElementPropSetter.this.space;
            if (this.element instanceof ElementToggle) {
               ElementToggle toggle = (ElementToggle)this.element;
               toggle.text = toggle.toggledState ? "gui.yes" : "gui.no";
            } else if (this.element instanceof ElementButton) {
               ElementButton button = (ElementButton)this.element;
               if (this.propInfo.field.getType().equals(KeyBind.class)) {
                  w -= 10;
                  x += 10;
                  String keyString = "";

                  try {
                     keyString = ((KeyBind)this.propInfo.field.get(this.config)).serialize();
                     String[] strings = keyString.split(":");
                     int key = 0;
                     if (strings.length > 0) {
                        key = Integer.parseInt(strings[0].trim());
                     }

                     button.text = GameSettings.getKeyDisplayString(key);
                  } catch (Exception var27) {
                     ;
                  }

                  GlStateManager.pushMatrix();
                  float scale = 0.5F;
                  GlStateManager.scale(scale, scale, scale);
                  GlStateManager.translate(0.0F, -1.0F, 0.0F);
                  if (keyString.contains("SHIFT")) {
                     IWorkspace var10000 = ElementPropSetter.this.parent.workspace;
                     FontRenderer var10001 = ElementPropSetter.this.parent.workspace.getFontRenderer();
                     int var10003 = (int)((float)(button.getPosX() - 1) / scale) - ElementPropSetter.this.parent.workspace.getFontRenderer().getStringWidth("Shift");
                     int var10004 = (int)((float)button.getPosY() / scale);
                     Theme var10005 = ElementPropSetter.this.parent.workspace.currentTheme;
                     var10000.drawString(var10001, "Shift", var10003, var10004, Theme.getAsHex(ElementPropSetter.this.parent.workspace.currentTheme.font));
                     GlStateManager.translate(0.0F, 10.0F, 0.0F);
                  }

                  if (keyString.contains("CTRL")) {
                     IWorkspace var49 = ElementPropSetter.this.parent.workspace;
                     FontRenderer var51 = ElementPropSetter.this.parent.workspace.getFontRenderer();
                     int var53 = (int)((float)(button.getPosX() - 1) / scale) - ElementPropSetter.this.parent.workspace.getFontRenderer().getStringWidth("Ctrl");
                     int var55 = (int)((float)button.getPosY() / scale);
                     Theme var57 = ElementPropSetter.this.parent.workspace.currentTheme;
                     var49.drawString(var51, "Ctrl", var53, var55, Theme.getAsHex(ElementPropSetter.this.parent.workspace.currentTheme.font));
                     GlStateManager.translate(0.0F, 10.0F, 0.0F);
                  }

                  if (keyString.contains("ALT")) {
                     IWorkspace var50 = ElementPropSetter.this.parent.workspace;
                     FontRenderer var52 = ElementPropSetter.this.parent.workspace.getFontRenderer();
                     int var54 = (int)((float)(button.getPosX() - 1) / scale) - ElementPropSetter.this.parent.workspace.getFontRenderer().getStringWidth("Alt");
                     int var56 = (int)((float)button.getPosY() / scale);
                     Theme var58 = ElementPropSetter.this.parent.workspace.currentTheme;
                     var50.drawString(var52, "Alt", var54, var56, Theme.getAsHex(ElementPropSetter.this.parent.workspace.currentTheme.font));
                  }

                  GlStateManager.popMatrix();
               }
            } else if (this.element instanceof ElementTextInput && this.propInfo.field.getType().equals(Colour.class)) {
               int clr = 16711680;

               try {
                  clr = Integer.decode("#" + ((ElementTextInput)this.element).textField.getText()).intValue();
               } catch (NumberFormatException var26) {
                  ;
               }

               ElementPropSetter.this.parent.workspace.drawString(ElementPropSetter.this.parent.workspace.getFontRenderer(), "#", x - ElementPropSetter.this.posX + ElementPropSetter.this.getPosX(), y - ElementPropSetter.this.posY + ElementPropSetter.this.getPosY() + 2, clr);
               w -= 6;
               x += 6;
            }

            try {
               if (clz.equals(Integer.TYPE)) {
                  int min = Integer.MIN_VALUE;
                  int max = Integer.MAX_VALUE;
                  if (this.propInfo.field.isAnnotationPresent(IntMinMax.class)) {
                     IntMinMax minMax = (IntMinMax)this.propInfo.field.getAnnotation(IntMinMax.class);
                     min = minMax.min();
                     max = minMax.max();
                  } else if (this.propInfo.field.isAnnotationPresent(IntBool.class)) {
                     min = 0;
                     max = 1;
                  }

                  int val = this.propInfo.field.getInt(this.config);
                  if (this.element instanceof ElementNumberInput) {
                     val = Integer.parseInt(((GuiTextField)((ElementNumberInput)this.element).textFields.get(0)).getText());
                  } else if (this.element instanceof ElementToggle) {
                     val = ((ElementToggle)this.element).toggledState ? 1 : 0;
                  }

                  if (val > max) {
                     val = max;
                  } else if (val < min) {
                     val = min;
                  }

                  int oldVal = this.propInfo.field.getInt(this.config);
                  if (oldVal != val) {
                     ConfigProp propInfo1 = (ConfigProp)this.propInfo.field.getAnnotation(ConfigProp.class);
                     if (!propInfo1.changeable() || propInfo1.useSession()) {
                        ((GuiConfigs)ElementPropSetter.this.parent.workspace).needsRestart();
                     }

                     this.propInfo.field.set(this.config, Integer.valueOf(val));
                     this.updateField(this.propInfo.field, Integer.valueOf(oldVal));
                  }
               } else if (clz.equals(String.class)) {
                  String newText = ((ElementTextInput)this.element).textField.getText();
                  String oldText = (String)this.propInfo.field.get(this.config);
                  if (!newText.equals(oldText)) {
                     ConfigProp propInfo1 = (ConfigProp)this.propInfo.field.getAnnotation(ConfigProp.class);
                     if (!propInfo1.changeable() || propInfo1.useSession()) {
                        ((GuiConfigs)ElementPropSetter.this.parent.workspace).needsRestart();
                     }

                     this.propInfo.field.set(this.config, newText);
                     this.updateField(this.propInfo.field, oldText);
                  }
               } else if (clz.equals(Colour.class)) {
                  try {
                     int val = Integer.decode("#" + ((ElementTextInput)this.element).textField.getText()).intValue();
                     Colour clr = (Colour)this.propInfo.field.get(this.config);
                     if (val != clr.getColour()) {
                        ConfigProp propInfo1 = (ConfigProp)this.propInfo.field.getAnnotation(ConfigProp.class);
                        if (!propInfo1.changeable() || propInfo1.useSession()) {
                           ((GuiConfigs)ElementPropSetter.this.parent.workspace).needsRestart();
                        }

                        int oriClr = clr.getColour();
                        clr.deserialize("#" + ((ElementTextInput)this.element).textField.getText());
                        this.updateField(this.propInfo.field, new Colour(oriClr));
                     }
                  } catch (NumberFormatException var28) {
                     ;
                  }
               }
            } catch (Exception var29) {
               ;
            }

            boolean flag = x != this.element.posX || y != this.element.posY || w != this.element.width;
            this.element.posX = x;
            this.element.posY = y;
            this.element.width = w;
            if (flag) {
               this.element.resized();
            }

            this.element.posX = x;
            this.element.posY = y;
            this.element.width = w;
            this.element.draw(mouseX, mouseY - (int)scrollHeight, realBorder || this.selected);
         }

         return null;
      }

      public void deselectOthers(ArrayList<ElementPropSetter.Tree> trees) {
         for(ElementPropSetter.Tree tree : trees) {
            if (tree != this && tree.selected) {
               tree.selected = false;
            }
         }

      }

      public void updateField(Field field, Object ori) {
         this.config.onConfigChange(field, ori);
         ElementPropSetter.this.saveTimeout = 10;
      }
   }
}
