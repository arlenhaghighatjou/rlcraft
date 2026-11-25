package com.creativemd.creativecore.common.gui.container;

import com.creativemd.creativecore.common.event.CreativeCoreEventBus;
import com.creativemd.creativecore.common.gui.ContainerControl;
import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import com.creativemd.creativecore.common.gui.client.style.Style;
import com.creativemd.creativecore.common.gui.event.ControlEvent;
import com.creativemd.creativecore.common.gui.mc.IVanillaGUI;
import com.creativemd.creativecore.common.gui.premade.SubContainerEmpty;
import com.creativemd.creativecore.common.gui.premade.SubGuiDialog;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.packet.gui.GuiLayerPacket;
import com.creativemd.creativecore.common.packet.gui.GuiUpdatePacket;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class SubGui extends GuiParent {
   @Nullable
   public SubContainer container;
   public IVanillaGUI gui;
   private CreativeCoreEventBus eventBus;

   public SubGui() {
      this(176, 166);
   }

   public SubGui(int width, int height) {
      this("gui", width, height);
   }

   public SubGui(String name, int width, int height) {
      super(name, 0, 0, width, height);
      this.eventBus = new CreativeCoreEventBus(true);
      this.eventBus.RegisterEventListener(this);
   }

   public boolean isTopLayer() {
      return this.gui.isTopLayer(this);
   }

   public int getLayerID() {
      return this.gui.getLayers().indexOf(this);
   }

   public void openClientLayer(SubGui gui) {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setBoolean("dialog", true);
      gui.gui = this.gui;
      PacketHandler.sendPacketToServer(new GuiLayerPacket(nbt, gui.gui.getLayers().size() - 1, false));
      gui.container = new SubContainerEmpty(this.getPlayer());
      gui.gui.addLayer(gui);
      gui.onOpened();
   }

   public void openNewLayer(NBTTagCompound nbt) {
      this.openNewLayer(nbt, false);
   }

   public void openNewLayer(NBTTagCompound nbt, boolean isPacket) {
      this.gui.addLayer(this.createLayer(mc.world, mc.player, nbt));
      if (!isPacket) {
         PacketHandler.sendPacketToServer(new GuiLayerPacket(nbt, this.getLayerID(), false));
      }

   }

   public void closeLayer(NBTTagCompound nbt) {
      this.closeLayer(nbt, false);
   }

   public void closeLayer(NBTTagCompound nbt, boolean isPacket) {
      this.onClosed();
      if (!isPacket) {
         PacketHandler.sendPacketToServer(new GuiLayerPacket(nbt, this.getLayerID(), true));
      }

      this.gui.removeLayer(this);
      if (this.gui.hasTopLayer()) {
         this.gui.getTopLayer().onLayerClosed(this, nbt);
      }

      this.gui.onLayerClosed();
   }

   public void onLayerClosed(SubGui gui, NBTTagCompound nbt) {
      if (nbt.getBoolean("dialog")) {
         String[] buttons = new String[nbt.getInteger("count")];

         for(int i = 0; i < buttons.length; ++i) {
            buttons[i] = nbt.getString("b" + i);
         }

         this.onDialogClosed(nbt.getString("text"), buttons, nbt.getString("clicked"));
      }

   }

   public void closeGui() {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setBoolean("exit", true);
      this.closeLayer(nbt);
      this.gui.onLayerClosed();
   }

   public SubGui createLayer(World world, EntityPlayer player, NBTTagCompound nbt) {
      SubGui layer = this.createLayerFromPacket(world, player, nbt);
      if (this.container != null) {
         layer.container = this.container.createLayerFromPacket(world, player, nbt);
      }

      layer.gui = this.gui;
      layer.onOpened();
      return layer;
   }

   public SubGui createLayerFromPacket(World world, EntityPlayer player, NBTTagCompound nbt) {
      if (!nbt.getBoolean("dialog")) {
         return null;
      } else {
         String[] buttons = new String[nbt.getInteger("count")];

         for(int i = 0; i < buttons.length; ++i) {
            buttons[i] = nbt.getString("b" + i);
         }

         return new SubGuiDialog(nbt.getString("text").split("\n"), buttons);
      }
   }

   public void openYesNoDialog(String text) {
      this.openButtonDialogDialog(text, "Yes", "No");
   }

   public void openButtonDialogDialog(String text, String... buttons) {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setBoolean("dialog", true);
      nbt.setString("text", text);
      nbt.setInteger("count", buttons.length);

      for(int i = 0; i < buttons.length; ++i) {
         nbt.setString("b" + i, buttons[i]);
      }

      this.openNewLayer(nbt);
   }

   public void openSaveDialog(String text) {
      this.openButtonDialogDialog(text, "Yes", "No", "Cancel");
   }

   public void onDialogClosed(String text, String[] buttons, String clicked) {
   }

   public boolean isInteractable() {
      return super.isInteractable() && this.isTopLayer();
   }

   public boolean closeGuiUsingEscape() {
      return true;
   }

   public boolean onKeyPressed(char character, int key) {
      if (key == 1) {
         if (this.closeGuiUsingEscape()) {
            this.closeGui();
         }

         return true;
      } else if (super.onKeyPressed(character, key)) {
         return true;
      } else if (key == mc.gameSettings.keyBindInventory.getKeyCode()) {
         this.closeGui();
         return true;
      } else {
         return false;
      }
   }

   public int getPixelOffsetX() {
      return this.gui.getGuiLeft() + this.getContentOffset();
   }

   public int getPixelOffsetY() {
      return this.gui.getGuiTop() + this.getContentOffset();
   }

   public void receiveContainerPacket(NBTTagCompound nbt) {
   }

   public void sendPacketToServer(NBTTagCompound nbt) {
      PacketHandler.sendPacketToServer(new GuiUpdatePacket(nbt, false, this.getLayerID()));
   }

   public EntityPlayer getPlayer() {
      return this.container.player;
   }

   public abstract void createControls();

   protected void renderBackground(GuiRenderHelper helper, Style style) {
      style.getBorder(this).renderStyle(helper, this.width, this.height);
      GlStateManager.translate((float)this.borderWidth, (float)this.borderWidth, 0.0F);
      style.getFace(this).renderStyle(helper, this.width - this.borderWidth * 2, this.height - this.borderWidth * 2);
   }

   public boolean isMouseOver() {
      return this.parent != null ? super.isMouseOver() : this.isTopLayer();
   }

   public boolean hasGrayBackground() {
      return true;
   }

   public void onTick() {
   }

   public void onClosed() {
      super.onClosed();
      this.eventBus.removeAllEventListeners();
   }

   public void addContainerControls() {
      for(int i = 0; i < this.container.controls.size(); ++i) {
         ((ContainerControl)this.container.controls.get(i)).onOpened();
         this.controls.add(((ContainerControl)this.container.controls.get(i)).getGuiControl());
      }

   }

   public void onOpened() {
      this.createControls();

      for(int i = 0; i < this.controls.size(); ++i) {
         ((GuiControl)this.controls.get(i)).parent = this;
         ((GuiControl)this.controls.get(i)).onOpened();
      }

      if (this.container != null) {
         this.addContainerControls();
      }

      this.refreshControls();
   }

   public boolean raiseEvent(ControlEvent event) {
      return !this.eventBus.raiseEvent(event);
   }

   public void addListener(Object listener) {
      this.eventBus.RegisterEventListener(listener);
   }

   public void removeListener(Object listener) {
      this.eventBus.removeEventListener(listener);
   }
}
