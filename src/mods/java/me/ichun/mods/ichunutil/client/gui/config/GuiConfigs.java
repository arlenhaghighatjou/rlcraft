package me.ichun.mods.ichunutil.client.gui.config;

import me.ichun.mods.ichunutil.client.gui.config.window.WindowCats;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowConfigs;
import me.ichun.mods.ichunutil.client.gui.config.window.WindowSetter;
import me.ichun.mods.ichunutil.client.gui.window.IWorkspace;
import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.gui.window.element.Element;
import me.ichun.mods.ichunutil.client.gui.window.element.ElementToggle;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiConfigs extends IWorkspace {
   public int oriScale;
   public GuiScreen oriScreen;
   public WindowConfigs windowConfigs;
   public WindowCats windowCats;
   public WindowSetter windowSetter;
   public boolean needsRestart;
   public int keyBindTimeout;

   public GuiConfigs(GuiScreen screen) {
      this.VARIABLE_LEVEL = 0;
      Minecraft mc = Minecraft.getMinecraft();
      this.oriScale = mc.gameSettings.guiScale;
      mc.gameSettings.guiScale = 2;
      this.oriScreen = screen;
      this.windowConfigs = new WindowConfigs(this, 0, 0, 0, 0, 0, 0);
      this.windowCats = new WindowCats(this, 0, 0, 0, 0, 0, 0);
      this.windowSetter = new WindowSetter(this, 0, 0, 0, 0, 0, 0);
      this.addWindowOnTop(this.windowConfigs);
      this.addWindowOnTop(this.windowCats);
      this.addWindowOnTop(this.windowSetter);

      for(ConfigBase config : ConfigHandler.configs) {
         config.enterConfigScreen();
      }

      this.tooltipTime = 0;
   }

   public GuiConfigs(int scale, GuiScreen screen) {
      this(screen);
      this.oriScale = scale;
      this.oriScreen = screen;
   }

   public void initGui() {
      super.initGui();
   }

   public void onGuiClosed() {
      for(ConfigBase config : ConfigHandler.configs) {
         config.exitConfigScreen();
      }

      Keyboard.enableRepeatEvents(false);
      Minecraft.getMinecraft().gameSettings.guiScale = this.oriScale;
   }

   public void updateScreen() {
      super.updateScreen();
      if (this.keyBindTimeout > 0) {
         --this.keyBindTimeout;
      }

   }

   public void addToDock(int i, Window w) {
   }

   public boolean canClickOnElement(Window window, Element element) {
      return true;
   }

   public void needsRestart() {
      if (!this.needsRestart) {
         this.windowConfigs.elements.add(new ElementToggle(this.windowConfigs, this.windowConfigs.width - 16 - 10, this.windowConfigs.height - 22, 16, 16, -100, true, 1, 1, "!", "ichunutil.config.gui.needsRestart", true));
         this.windowConfigs.resized();
      }

      this.needsRestart = true;
   }

   public void drawScreen(int mouseX, int mouseY, float renderTick) {
      Minecraft mc = Minecraft.getMinecraft();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      ScaledResolution resolution = new ScaledResolution(mc);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0D, resolution.getScaledWidth_double(), resolution.getScaledHeight_double(), 0.0D, -5000.0D, 5000.0D);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      GlStateManager.pushMatrix();
      GlStateManager.clearColor((float)this.currentTheme.workspaceBackground[0] / 255.0F, (float)this.currentTheme.workspaceBackground[1] / 255.0F, (float)this.currentTheme.workspaceBackground[2] / 255.0F, 255.0F);
      GlStateManager.clear(16640);
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

   public void keyTyped(char c, int key) {
      if (key == 1) {
         this.mc.displayGuiScreen(this.oriScreen);
         if (this.mc.currentScreen == null) {
            this.mc.setIngameFocus();
         }
      } else if (this.elementSelected != null) {
         this.elementSelected.keyInput(c, key);
      }

   }
}
