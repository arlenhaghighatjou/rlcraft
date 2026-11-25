package me.ichun.mods.ichunutil.client.gui;

import java.util.ArrayList;
import me.ichun.mods.ichunutil.client.gui.window.IWorkspace;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementButton;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementNumberInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.vector.Vector3f;

public class GuiItemRendererSetter extends IWorkspace {
   public int oriScale;
   public GuiItemRendererSetter.WindowItemRendererSetter windowItemRendererSetter;
   public static ItemTransformVec3f itemTransformVec3f = ItemTransformVec3f.DEFAULT;

   public GuiItemRendererSetter(int scale) {
      this.VARIABLE_LEVEL = 0;
      this.oriScale = scale;
   }

   public void initGui() {
      super.initGui();
      this.levels.clear();
      ArrayList<Window> level = new ArrayList();
      this.windowItemRendererSetter = new GuiItemRendererSetter.WindowItemRendererSetter(this, 0, 0, 160, 110);
      level.add(this.windowItemRendererSetter);
      this.levels.add(level);
      this.levels.add(new ArrayList());
   }

   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
      Minecraft.getMinecraft().gameSettings.guiScale = this.oriScale;
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public boolean canClickOnElement(Window window, Element element) {
      return true;
   }

   public void drawScreen(int mouseX, int mouseY, float renderTick) {
      if (this.mc != null) {
         Minecraft mc = Minecraft.getMinecraft();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         ScaledResolution resolution = new ScaledResolution(mc);
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0D, resolution.getScaledWidth_double(), resolution.getScaledHeight_double(), 0.0D, -5000.0D, 5000.0D);
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.pushMatrix();
         this.drawWindows(mouseX, mouseY);
         int scroll = Mouse.getDWheel();
         this.updateElementHovered(mouseX, mouseY, scroll);
         GlStateManager.popMatrix();
         this.updateKeyStates();
         this.updateWindowDragged(mouseX, mouseY);
         this.updateElementDragged(mouseX, mouseY);
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0D, resolution.getScaledWidth_double(), resolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
      }
   }

   public void keyTyped(char c, int key) {
      if (key == 1) {
         this.mc.displayGuiScreen((GuiScreen)null);
         if (this.mc.currentScreen == null) {
            this.mc.setIngameFocus();
         }
      } else if (this.elementSelected != null) {
         this.elementSelected.keyInput(c, key);
      }

   }

   public class WindowItemRendererSetter extends Window {
      public ElementNumberInput rotation;
      public ElementNumberInput position;
      public ElementNumberInput scale;
      public ElementButton printout;

      public WindowItemRendererSetter(IWorkspace parent, int x, int y, int w, int h) {
         super(parent, x, y, w, h, 40, 40, "Item Renderer", true);
         this.rotation = new ElementNumberInput(this, 10, 20, 140, 12, 0, "Rotation", 3, true, -360.0D, 360.0D, new double[]{(double)GuiItemRendererSetter.itemTransformVec3f.rotation.getX(), (double)GuiItemRendererSetter.itemTransformVec3f.rotation.getY(), (double)GuiItemRendererSetter.itemTransformVec3f.rotation.getZ()});
         this.position = new ElementNumberInput(this, 10, 40, 140, 12, 1, "Position", 3, true, -2.147483648E9D, 2.147483647E9D, new double[]{(double)GuiItemRendererSetter.itemTransformVec3f.translation.getX(), (double)GuiItemRendererSetter.itemTransformVec3f.translation.getY(), (double)GuiItemRendererSetter.itemTransformVec3f.translation.getZ()});
         this.scale = new ElementNumberInput(this, 10, 60, 140, 12, 2, "Scale", 3, true, -2.147483648E9D, 2.147483647E9D, new double[]{(double)GuiItemRendererSetter.itemTransformVec3f.scale.getX(), (double)GuiItemRendererSetter.itemTransformVec3f.scale.getY(), (double)GuiItemRendererSetter.itemTransformVec3f.scale.getZ()});
         this.printout = new ElementButton(this, 10, 80, 80, 16, -1, false, 0, 0, "PrintOut");
         this.elements.add(this.rotation);
         this.elements.add(this.position);
         this.elements.add(this.scale);
         this.elements.add(this.printout);
      }

      public void elementTriggered(Element element) {
         super.elementTriggered(element);
         if (element.id == -1) {
            try {
               System.out.println(String.format("new ItemTransformVec3f(new Vector3f(%.2fF, %.2fF, %.2fF), new Vector3f(%.2fF, %.2fF, %.2fF), new Vector3f(%.2fF, %.2fF, %.2fF))", Double.parseDouble(((GuiTextField)this.rotation.textFields.get(0)).getText()), Double.parseDouble(((GuiTextField)this.rotation.textFields.get(1)).getText()), Double.parseDouble(((GuiTextField)this.rotation.textFields.get(2)).getText()), Double.parseDouble(((GuiTextField)this.position.textFields.get(0)).getText()), Double.parseDouble(((GuiTextField)this.position.textFields.get(1)).getText()), Double.parseDouble(((GuiTextField)this.position.textFields.get(2)).getText()), Double.parseDouble(((GuiTextField)this.scale.textFields.get(0)).getText()), Double.parseDouble(((GuiTextField)this.scale.textFields.get(1)).getText()), Double.parseDouble(((GuiTextField)this.scale.textFields.get(2)).getText())));
            } catch (Exception var3) {
               ;
            }
         }

      }

      public void update() {
         super.update();

         try {
            ItemTransformVec3f vec = new ItemTransformVec3f(new Vector3f((float)Double.parseDouble(((GuiTextField)this.rotation.textFields.get(0)).getText()), (float)Double.parseDouble(((GuiTextField)this.rotation.textFields.get(1)).getText()), (float)Double.parseDouble(((GuiTextField)this.rotation.textFields.get(2)).getText())), new Vector3f((float)Double.parseDouble(((GuiTextField)this.position.textFields.get(0)).getText()), (float)Double.parseDouble(((GuiTextField)this.position.textFields.get(1)).getText()), (float)Double.parseDouble(((GuiTextField)this.position.textFields.get(2)).getText())), new Vector3f((float)Double.parseDouble(((GuiTextField)this.scale.textFields.get(0)).getText()), (float)Double.parseDouble(((GuiTextField)this.scale.textFields.get(1)).getText()), (float)Double.parseDouble(((GuiTextField)this.scale.textFields.get(2)).getText())));
            GuiItemRendererSetter.itemTransformVec3f = vec;
         } catch (Exception var2) {
            ;
         }

      }
   }
}
