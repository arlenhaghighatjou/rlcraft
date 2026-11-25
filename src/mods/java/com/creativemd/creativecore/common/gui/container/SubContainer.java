package com.creativemd.creativecore.common.gui.container;

import com.creativemd.creativecore.common.event.CreativeCoreEventBus;
import com.creativemd.creativecore.common.gui.ContainerControl;
import com.creativemd.creativecore.common.gui.GuiControl;
import com.creativemd.creativecore.common.gui.event.ControlEvent;
import com.creativemd.creativecore.common.gui.mc.ContainerSub;
import com.creativemd.creativecore.common.gui.premade.SubContainerEmpty;
import com.creativemd.creativecore.common.packet.PacketHandler;
import com.creativemd.creativecore.common.packet.gui.ContainerControlUpdatePacket;
import com.creativemd.creativecore.common.packet.gui.GuiLayerPacket;
import com.creativemd.creativecore.common.packet.gui.GuiNBTPacket;
import com.creativemd.creativecore.common.packet.gui.GuiUpdatePacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class SubContainer extends ContainerParent {
   public EntityPlayer player;
   public ContainerSub container;
   private CreativeCoreEventBus eventBus;

   public SubContainer(EntityPlayer player) {
      this("container", player);
   }

   public SubContainer(String name, EntityPlayer player) {
      super(name);
      this.player = player;
      this.eventBus = new CreativeCoreEventBus(player.getEntityWorld().isRemote);
      this.addListener(this);
   }

   public boolean isTopLayer() {
      return this.container.isTopLayer(this);
   }

   public int getLayerID() {
      return this.container.layers.indexOf(this);
   }

   public SubContainer createLayerFromPacket(World world, EntityPlayer player, NBTTagCompound nbt) {
      return nbt.getBoolean("dialog") ? new SubContainerEmpty(player) : null;
   }

   public void closeLayer(NBTTagCompound nbt) {
      this.closeLayer(nbt, false);
   }

   public void closeLayer(NBTTagCompound nbt, boolean isPacket) {
      if (!isPacket) {
         PacketHandler.sendPacketToServer(new GuiLayerPacket(nbt, this.getLayerID(), true));
      }

      this.onClosed();
      this.onLayerClosed(nbt, this);
      this.container.layers.remove(this);
   }

   public void onLayerClosed(NBTTagCompound nbt, SubContainer container) {
   }

   public void openNewLayer(NBTTagCompound nbt) {
      this.openNewLayer(nbt, false);
   }

   public void openNewLayer(NBTTagCompound nbt, boolean isPacket) {
      SubContainer Subcontainer = this.createLayerFromPacket(this.player.world, this.player, nbt);
      Subcontainer.container = this.container;
      this.container.layers.add(Subcontainer);
      if (!isPacket) {
         PacketHandler.sendPacketToServer(new GuiLayerPacket(nbt, this.getLayerID(), false));
      }

   }

   public boolean isInteractable() {
      return super.isInteractable() && this.isTopLayer();
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

   public void onClosed() {
      for(int i = 0; i < this.controls.size(); ++i) {
         ((ContainerControl)this.controls.get(i)).onClosed();
      }

      this.eventBus.removeAllEventListeners();
   }

   public void onOpened() {
      this.createControls();

      for(int i = 0; i < this.controls.size(); ++i) {
         ((ContainerControl)this.controls.get(i)).parent = this;
         ((ContainerControl)this.controls.get(i)).onOpened();
         ((ContainerControl)this.controls.get(i)).setID(i);
      }

      if (!this.player.getEntityWorld().isRemote) {
         NBTTagCompound nbt = new NBTTagCompound();
         this.writeOpeningNBT(nbt);
         if (!nbt.hasNoTags()) {
            PacketHandler.sendPacketToPlayer(new GuiUpdatePacket(nbt, true, this.getLayerID()), (EntityPlayerMP)this.player);
         }
      }

   }

   public void receivePacket(NBTTagCompound nbt) {
      if (nbt.getBoolean("newNonSyncedLayer")) {
         SubContainer newContainer = new SubContainerEmpty(this.player);
         newContainer.container = this.container;
         this.container.layers.add(newContainer);
      } else {
         super.receivePacket(nbt);
      }
   }

   public EntityPlayer getPlayer() {
      return this.player;
   }

   public abstract void createControls();

   public void updateEqualContainers() {
   }

   public void sendNBTToGui(NBTTagCompound nbt) {
      if (this.player instanceof EntityPlayerMP) {
         PacketHandler.sendPacketToPlayer(new GuiNBTPacket(nbt), (EntityPlayerMP)this.player);
      }

   }

   public void sendNBTUpdate(NBTTagCompound nbt) {
      this.sendNBTUpdate((ContainerControl)null, nbt);
   }

   public void sendNBTUpdate(ContainerControl control, NBTTagCompound nbt) {
      String name = control != null ? control.name : null;
      if (this.player.getEntityWorld().isRemote) {
         PacketHandler.sendPacketToServer(new ContainerControlUpdatePacket(this.getLayerID(), name, nbt));
      } else {
         PacketHandler.sendPacketToPlayer(new ContainerControlUpdatePacket(this.getLayerID(), name, nbt), (EntityPlayerMP)this.player);
      }

   }

   public void writeOpeningNBT(NBTTagCompound nbt) {
   }

   public void writeToNBTUpdate(NBTTagCompound nbt) {
   }

   @SideOnly(Side.CLIENT)
   protected GuiControl createGuiControl() {
      return null;
   }
}
