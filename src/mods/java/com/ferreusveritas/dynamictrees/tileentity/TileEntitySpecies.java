package com.ferreusveritas.dynamictrees.tileentity;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.trees.Species;
import net.minecraft.nbt.NBTTagCompound;
import com.arlenh7.eaglercraft.network.INetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntitySpecies extends TileEntity {
   Species species = Species.NULLSPECIES;
   ResourceLocation speciesName;

   public TileEntitySpecies() {
      this.speciesName = this.species.getRegistryName();
   }

   public Species getSpecies() {
      if (this.species == Species.NULLSPECIES) {
         this.species = TreeRegistry.findSpecies(this.speciesName);
      }

      return this.species;
   }

   public void setSpecies(Species species) {
      this.species = species;
      this.speciesName = species.getRegistryName();
      this.markDirty();
   }

   private void read(NBTTagCompound tag) {
      if (tag.hasKey("species")) {
         this.speciesName = new ResourceLocation(tag.getString("species"));
         this.species = TreeRegistry.findSpecies(this.speciesName);
      }

   }

   private void write(NBTTagCompound tag) {
      tag.setString("species", this.speciesName.toString());
   }

   public void readFromNBT(NBTTagCompound tag) {
      super.readFromNBT(tag);
      this.read(tag);
   }

   public NBTTagCompound writeToNBT(NBTTagCompound tag) {
      super.writeToNBT(tag);
      this.write(tag);
      return tag;
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound syncData = new NBTTagCompound();
      this.write(syncData);
      return new SPacketUpdateTileEntity(this.pos, 1, syncData);
   }

   public void onDataPacket(INetworkManager net, SPacketUpdateTileEntity pkt) {
      this.read(pkt.getNbtCompound());
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }
}
