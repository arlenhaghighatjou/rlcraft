package com.fantasticsource.mctools.gui;

import com.fantasticsource.fantasticlib.config.FantasticConfig;
import com.fantasticsource.mctools.ClientTickTimer;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Render;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Stack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public abstract class GUIScreen extends GuiScreen {
   protected static final Field CONFIG_MANAGER_CONFIGS_FIELD = ReflectionTool.getField(ConfigManager.class, "CONFIGS");
   public static final Stack<GUIScreen.ScreenEntry> SCREEN_STACK = new Stack();
   public static final FontRenderer FONT_RENDERER;
   public static int[] currentScissor;
   public static double mouseX = 0.5D;
   public static double mouseY = 0.5D;
   public static FloatBuffer mcProjection;
   public static FloatBuffer mcModelView;
   private static boolean ignoreClosure = false;
   public final GUIView root;
   public final GUIView tooltips;
   public ItemStack tooltipStack;
   public final ArrayList<Runnable> onClosedActions;
   public final double textScale;
   public boolean drawStack;
   public int pxWidth;
   public int pxHeight;
   public float xPixel;
   public float yPixel;
   private ArrayList<Integer> mouseButtons;
   private boolean initialized;
   public final LinkedHashMap<String, Namespace> namespaces;

   public GUIScreen() {
      this(1.0D);
   }

   public GUIScreen(double textScale) {
      this.tooltipStack = null;
      this.onClosedActions = new ArrayList();
      this.drawStack = true;
      this.mouseButtons = new ArrayList();
      this.initialized = false;
      this.namespaces = new LinkedHashMap();
      boolean found = false;
      double mul = 1.0D;

      for(String s : FantasticConfig.guiSettings.perGUIScaling) {
         String[] tokens = Tools.fixedSplit(s, ",");
         if (this.getClass().getName().equals(tokens[0])) {
            mul = Double.parseDouble(tokens[1]);
            found = true;
         }
      }

      this.textScale = textScale * mul;
      if (!found) {
         String[] entries = new String[FantasticConfig.guiSettings.perGUIScaling.length + 1];
         System.arraycopy(FantasticConfig.guiSettings.perGUIScaling, 0, entries, 0, FantasticConfig.guiSettings.perGUIScaling.length);
         entries[entries.length - 1] = this.getClass().getName() + ", 1";
         FantasticConfig.guiSettings.perGUIScaling = entries;

         try {
            MCTools.saveConfig("fantasticlib");
         } catch (IllegalAccessException var11) {
            var11.printStackTrace();
         }
      }

      this.pxWidth = Display.getWidth();
      this.pxHeight = Display.getHeight();
      this.xPixel = 1.0F / (float)this.pxWidth;
      this.yPixel = 1.0F / (float)this.pxHeight;
      this.root = new GUIView(this, 1.0D, 1.0D);
      this.tooltips = new GUIView(this, 1.0D, 1.0D);
   }

   public static Color getIdleColor(Color activeColor) {
      return activeColor.copy().setVF(0.5F * activeColor.vf());
   }

   public static Color getHoverColor(Color activeColor) {
      return activeColor.copy().setVF(0.75F * activeColor.vf());
   }

   public void show() {
      if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) {
         this.showStacked();
      } else {
         this.showUnstacked();
      }

   }

   public void showUnstacked() {
      Minecraft.getMinecraft().displayGuiScreen(this);
   }

   public void showStacked() {
      GuiScreen current = Minecraft.getMinecraft().currentScreen;
      if (current instanceof GUIScreen) {
         SCREEN_STACK.push(new GUIScreen.ScreenEntry((GUIScreen)current, mouseX, mouseY));
      }

      ignoreClosure = true;
      Minecraft.getMinecraft().displayGuiScreen(this);
      ignoreClosure = false;
   }

   public static void show(GUIScreen screen) {
      if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) {
         showStacked(screen);
      } else {
         showUnstacked(screen);
      }

   }

   public static void showUnstacked(GUIScreen screen) {
      Minecraft.getMinecraft().displayGuiScreen(screen);
   }

   public static void showStacked(GUIScreen screen) {
      GuiScreen current = Minecraft.getMinecraft().currentScreen;
      if (current instanceof GUIScreen) {
         SCREEN_STACK.push(new GUIScreen.ScreenEntry((GUIScreen)current, mouseX, mouseY));
      }

      ignoreClosure = true;
      Minecraft.getMinecraft().displayGuiScreen(screen);
      ignoreClosure = false;
   }

   public abstract String title();

   public boolean isInitialized() {
      return this.initialized;
   }

   public boolean isVisible() {
      return Minecraft.getMinecraft().currentScreen == this;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      mcProjection = Render.getCurrentProjectionMatrix();
      mcModelView = Render.getCurrentModelViewMatrix();
      if (this.pxWidth != Display.getWidth() || this.pxHeight != Display.getHeight()) {
         this.recalc();
      }

      if (this.drawStack) {
         double mX = mouseX;
         double mY = mouseY;

         for(GUIScreen.ScreenEntry entry : SCREEN_STACK) {
            mouseX = entry.mouseX;
            mouseY = entry.mouseY;
            entry.screen.draw();
         }

         mouseX = mX;
         mouseY = mY;
      }

      this.draw();
   }

   public boolean scissor() {
      int w = currentScissor[2] - currentScissor[0];
      int h = currentScissor[3] - currentScissor[1];
      if (w >= 1 && h >= 1) {
         GL11.glScissor(currentScissor[0], this.pxHeight - currentScissor[3], w, h);
         return true;
      } else {
         return false;
      }
   }

   public void draw() {
      GlStateManager.disableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.shadeModel(7425);
      GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      Render.startOrtho();
      GlStateManager.scale((float)this.pxWidth, (float)this.pxHeight, 1.0F);
      GL11.glEnable(3089);
      currentScissor = new int[]{0, 0, this.pxWidth, this.pxHeight};
      this.root.draw();
      currentScissor = new int[]{0, 0, this.pxWidth, this.pxHeight};
      GlStateManager.disableDepth();
      if (this.isVisible()) {
         this.tooltips.draw();
      }

      this.tooltips.clear();
      GL11.glDisable(3089);
      Render.endOrtho();
      GlStateManager.enableDepth();
      GlStateManager.shadeModel(7424);
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableTexture2D();
      if (this.isVisible() && this.tooltipStack != null) {
         this.renderToolTip(this.tooltipStack);
      }

      this.tooltipStack = null;
   }

   public void initGui() {
      if (!this.initialized) {
         this.initialized = true;
         this.init();
      }

      try {
         Mouse.setCursorPosition((int)(mouseX * (double)Render.getStoredViewportWidth()), (int)((1.0D - mouseY) * (double)Render.getStoredViewportHeight()));
      } catch (IllegalAccessException var2) {
         var2.printStackTrace();
      }

      this.mouseButtons.clear();
      this.root.recalc(0);
      Keyboard.enableRepeatEvents(true);
   }

   protected void init() {
   }

   public void onResize(Minecraft mcIn, int w, int h) {
      if (w != this.width || h != this.height) {
         super.onResize(mcIn, w, h);
         this.recalc();
      }
   }

   protected void recalc() {
      this.pxWidth = Display.getWidth();
      this.pxHeight = Display.getHeight();
      this.xPixel = 1.0F / (float)this.pxWidth;
      this.yPixel = 1.0F / (float)this.pxHeight;
      this.root.recalc(0);
   }

   public void handleMouseInput() {
      if (!Mouse.isInsideWindow()) {
         Mouse.getDWheel();
      } else {
         mouseX = (double)Mouse.getX() / (double)this.mc.displayWidth;
         int displayHeight = this.mc.displayHeight;
         mouseY = (double)(displayHeight - 1 - Mouse.getY()) / (double)displayHeight;
         int delta = Mouse.getDWheel();
         if (delta != 0) {
            this.root.mouseWheel(delta);
         }

         int btn = Mouse.getEventButton();
         if (btn != -1) {
            if (Mouse.isButtonDown(btn)) {
               if (!this.mouseButtons.contains(Integer.valueOf(btn))) {
                  this.mouseButtons.add(Integer.valueOf(btn));
               }

               this.root.mousePressed(btn);
            } else {
               this.mouseButtons.remove(Integer.valueOf(btn));
               this.root.mouseReleased(btn);
            }
         } else {
            Iterator var4 = this.mouseButtons.iterator();

            while(var4.hasNext()) {
               int b = ((Integer)var4.next()).intValue();
               this.root.mouseDrag(b);
            }
         }

      }
   }

   public void close() {
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.currentScreen == this) {
         mc.player.closeScreenAndDropStack();
      }

   }

   public final void onGuiClosed() {
      if (!ignoreClosure) {
         ClientTickTimer.schedule(0, this::onClosed);
      }

   }

   public void onClosed() {
      for(Runnable action : this.onClosedActions) {
         action.run();
      }

      if (SCREEN_STACK.size() > 0) {
         GUIScreen screen = ((GUIScreen.ScreenEntry)SCREEN_STACK.pop()).screen;
         this.mc.displayGuiScreen(screen);
         screen.onResize(Minecraft.getMinecraft(), this.width, this.height);
      }

   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      super.keyTyped(typedChar, keyCode);
      this.root.keyTyped(typedChar, keyCode);
   }

   public GUIScreen addOnClosedActions(Runnable... actions) {
      this.onClosedActions.addAll(Arrays.asList(actions));
      return this;
   }

   public void renderToolTip(ItemStack stack) {
      ScaledResolution sr = new ScaledResolution(this.mc);
      super.renderToolTip(stack, Mouse.getX() * sr.getScaledWidth() / this.mc.displayWidth, sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / this.mc.displayHeight - 1);
   }

   static {
      FONT_RENDERER = Minecraft.getMinecraft().fontRenderer;
   }

   public static class ScreenEntry {
      public final GUIScreen screen;
      public final double mouseX;
      public final double mouseY;

      public ScreenEntry(GUIScreen screen, double mouseX, double mouseY) {
         this.screen = screen;
         this.mouseX = mouseX;
         this.mouseY = mouseY;
      }
   }
}
