package com.creativemd.creativecore.common.gui.mc;

import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.container.SubContainer;
import com.creativemd.creativecore.common.gui.container.SubGui;
import com.creativemd.creativecore.common.gui.event.gui.GuiToolTipEvent;
import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.GuiScreenEvent.BackgroundDrawnEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiContainerSub extends GuiContainer implements IVanillaGUI {
   protected ArrayList<SubGui> layers;
   public boolean isOpened = false;

   public GuiContainerSub(EntityPlayer player, SubGui gui, SubContainer container) {
      super(new ContainerSub(player, container));
      ((ContainerSub)this.inventorySlots).gui = this;
      Minecraft.getMinecraft().player.openContainer = this.inventorySlots;
      this.layers = new ArrayList();
      gui.container = container;
      gui.gui = this;
      container.addListener(gui);
      this.layers.add(gui);
      container.onOpened();
      this.resize();
   }

   public void initGui() {
      super.initGui();
      if (!this.isOpened) {
         for(int i = 0; i < this.layers.size(); ++i) {
            ((SubGui)this.layers.get(i)).onOpened();
         }

         this.isOpened = true;
      }

   }

   public ArrayList<SubGui> getLayers() {
      return this.layers;
   }

   public void removeLayer(SubGui layer) {
      this.layers.remove(layer);
      ((ContainerSub)this.inventorySlots).layers.remove(layer.container);
      this.resize();
   }

   public void addLayer(SubGui layer) {
      this.layers.add(layer);
      ((ContainerSub)this.inventorySlots).layers.add(layer.container);
      this.resize();
   }

   public void resize() {
      this.xSize = 0;
      this.ySize = 0;

      for(int i = 0; i < this.layers.size(); ++i) {
         if (((SubGui)this.layers.get(i)).width > this.xSize) {
            this.xSize = ((SubGui)this.layers.get(i)).width;
         }

         if (((SubGui)this.layers.get(i)).height > this.ySize) {
            this.ySize = ((SubGui)this.layers.get(i)).height;
         }
      }

   }

   public int getWidth() {
      return this.xSize;
   }

   public int getHeight() {
      return this.ySize;
   }

   public int getMaxScale(Minecraft mc) {
      int k = 1000;

      int scaleFactor;
      for(scaleFactor = 1; scaleFactor < k && this.xSize * (scaleFactor + 1) <= mc.displayWidth && this.ySize * (scaleFactor + 1) <= mc.displayHeight; ++scaleFactor) {
         ;
      }

      return scaleFactor;
   }

   public boolean hasTopLayer() {
      return this.layers.size() > 0;
   }

   public SubGui getTopLayer() {
      return this.hasTopLayer() ? (SubGui)this.layers.get(this.layers.size() - 1) : null;
   }

   public boolean isTopLayer(SubGui gui) {
      return this.getTopLayer() == gui;
   }

   public boolean isOpen(Class<? extends SubGui> clazz) {
      for(int i = 0; i < this.layers.size(); ++i) {
         if (clazz.isInstance(this.layers.get(i))) {
            return true;
         }
      }

      return false;
   }

   public <T extends SubGui> T get(Class<T> clazz) {
      for(int i = 0; i < this.layers.size(); ++i) {
         if (clazz.isInstance(this.layers.get(i))) {
            return (T)(this.layers.get(i));
         }
      }

      return (T)null;
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
   }

   public void drawDefaultBackground() {
   }

   public void onTick() {
      for(int i = 0; i < this.layers.size(); ++i) {
         ((SubGui)this.layers.get(i)).onTick();
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (!this.layers.isEmpty()) {
         int i = this.layers.size() - 1;
         GlStateManager.pushMatrix();
         int k = this.guiLeft;
         int l = this.guiTop;
         int offX = (this.width - ((SubGui)this.layers.get(i)).width) / 2 - k;
         int offY = (this.height - ((SubGui)this.layers.get(i)).height) / 2 - l;
         GlStateManager.translate((float)k, (float)l, 0.0F);
         GlStateManager.translate((float)offX, (float)offY, 0.0F);
         Vec3d mouse = ((SubGui)this.layers.get(i)).getMousePos();
         GuiToolTipEvent event = ((SubGui)this.layers.get(i)).getToolTipEvent();
         if (event != null && ((SubGui)this.layers.get(i)).raiseEvent(event)) {
            this.drawHoveringText(event.tooltip, (int)mouse.x, (int)mouse.y, GuiRenderHelper.instance.font);
         }

         GlStateManager.popMatrix();
         GL11.glClear(1024);
      }
   }

   public void drawGuiContainerForegroundLayer(int par1, int par2) {
      for(int i = 0; i < this.layers.size(); ++i) {
         GL11.glDisable(2960);
         GL11.glStencilMask(-1);
         GL11.glClear(1024);
         GL11.glStencilFunc(519, 1, 1);
         GL11.glStencilOp(7681, 7681, 7681);
         GlStateManager.pushMatrix();
         int k = this.guiLeft;
         int l = this.guiTop;
         GlStateManager.translate((float)(-k), (float)(-l), 0.0F);
         if (((SubGui)this.layers.get(i)).hasGrayBackground()) {
            this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
         }

         if (i == this.layers.size() - 1) {
            MinecraftForge.EVENT_BUS.post(new BackgroundDrawnEvent(this));
         }

         int offX = (this.width - ((SubGui)this.layers.get(i)).width) / 2 - k;
         int offY = (this.height - ((SubGui)this.layers.get(i)).height) / 2 - l;
         GlStateManager.translate((float)k, (float)l, 0.0F);
         GlStateManager.translate((float)offX, (float)offY, 0.0F);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         ((SubGui)this.layers.get(i)).renderControl(GuiRenderHelper.instance, 1.0F, GuiControl.getScreenRect());
         GlStateManager.popMatrix();
      }

   }

   public void onGuiClosed() {
      for(int i = 0; i < this.layers.size(); ++i) {
         ((SubGui)this.layers.get(i)).onClosed();
      }

   }

   public void keyTyped(char character, int key) throws IOException {
      if (this.getTopLayer() != null && !this.getTopLayer().onKeyPressed(character, key)) {
         super.keyTyped(character, key);
      }

   }

   public void handleInput() throws IOException {
      if (Mouse.isCreated()) {
         this.handleScrolling();
         Vec3d mouse = this.getTopLayer().getMousePos();
         this.getTopLayer().mouseMove((int)mouse.x, (int)mouse.y, 0);
      }

      super.handleInput();
   }

   public void handleScrolling() {
      int j = Mouse.getDWheel();
      if (j != 0) {
         Vec3d mouse = this.getTopLayer().getMousePos();
         this.getTopLayer().mouseScrolled((int)mouse.x, (int)mouse.y, j > 0 ? 1 : -1);
      }

   }

   public void mouseClicked(int x, int y, int button) throws IOException {
      super.mouseClicked(x, y, button);
      this.getTopLayer().mousePressed(x, y, button);
   }

   public void mouseClickMove(int x, int y, int button, long time) {
      super.mouseClickMove(x, y, button, time);
      this.getTopLayer().mouseDragged(x, y, button, time);
   }

   protected void mouseReleased(int x, int y, int button) {
      super.mouseReleased(x, y, button);
      if (this.hasTopLayer()) {
         this.onMouseReleased(x, y, button);
      }

   }

   public void onMouseReleased(int x, int y, int button) {
      this.getTopLayer().mouseReleased(x, y, button);
   }

   public void onLayerClosed() {
      if (this.layers.isEmpty()) {
         this.mc.player.closeScreen();
      }

   }

   public void sendChat(String msg) {
      this.sendChatMessage(msg);
   }
}
