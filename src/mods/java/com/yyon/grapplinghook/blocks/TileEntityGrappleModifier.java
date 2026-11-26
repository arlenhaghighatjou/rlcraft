package com.yyon.grapplinghook.blocks;

import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.network.GrappleModifierMessage;
import java.util.HashMap;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import com.arlenh7.eaglercraft.network.INetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityGrappleModifier extends TileEntity {
   public HashMap<grapplemod.upgradeCategories, Boolean> unlockedCategories = new HashMap();
   public GrappleCustomization customization = new GrappleCustomization();

   public TileEntityGrappleModifier() {
   }

   public void unlockCategory(grapplemod.upgradeCategories category) {
      this.unlockedCategories.put(category, Boolean.valueOf(true));
      this.sendUpdates();
   }

   public void setCustomizationClient(GrappleCustomization customization) {
      this.customization = customization;
      grapplemod.network.sendToServer(new GrappleModifierMessage(this.pos, this.customization));
      this.sendUpdates();
   }

   public void setCustomizationServer(GrappleCustomization customization) {
      this.customization = customization;
      this.sendUpdates();
   }

   private void sendUpdates() {
      this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
      this.markDirty();
   }

   public boolean isUnlocked(grapplemod.upgradeCategories category) {
      return this.unlockedCategories.containsKey(category) && ((Boolean)this.unlockedCategories.get(category)).booleanValue();
   }

   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      this.writeToNBT(nbtTagCompound);
      int metadata = this.getBlockMetadata();
      return new SPacketUpdateTileEntity(this.pos, metadata, nbtTagCompound);
   }

   public void onDataPacket(INetworkManager net, SPacketUpdateTileEntity pkt) {
      this.readFromNBT(pkt.getNbtCompound());
   }

   public NBTTagCompound getUpdateTag() {
      NBTTagCompound nbtTagCompound = new NBTTagCompound();
      this.writeToNBT(nbtTagCompound);
      return nbtTagCompound;
   }

   public void handleUpdateTag(NBTTagCompound tag) {
      this.readFromNBT(tag);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound parentNBTTagCompound) {
      super.writeToNBT(parentNBTTagCompound);
      NBTTagCompound unlockedNBT = parentNBTTagCompound.getCompoundTag("unlocked");

      for(grapplemod.upgradeCategories category : grapplemod.upgradeCategories.values()) {
         String num = String.valueOf(category.toInt());
         boolean unlocked = this.isUnlocked(category);
         unlockedNBT.setBoolean(num, unlocked);
      }

      parentNBTTagCompound.setTag("unlocked", unlockedNBT);
      parentNBTTagCompound.setTag("customization", this.customization.writeNBT());
      return parentNBTTagCompound;
   }

   public void readFromNBT(NBTTagCompound parentNBTTagCompound) {
      super.readFromNBT(parentNBTTagCompound);
      NBTTagCompound unlockedNBT = parentNBTTagCompound.getCompoundTag("unlocked");

      for(grapplemod.upgradeCategories category : grapplemod.upgradeCategories.values()) {
         String num = String.valueOf(category.toInt());
         boolean unlocked = unlockedNBT.getBoolean(num);
         this.unlockedCategories.put(category, Boolean.valueOf(unlocked));
      }

      NBTTagCompound custom = parentNBTTagCompound.getCompoundTag("customization");
      this.customization.loadNBT(custom);
   }
}
