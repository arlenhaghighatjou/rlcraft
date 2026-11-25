package com.ferreusveritas.dynamictrees.tileentity;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.trees.Species;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileEntityBonsai extends TileEntity {
   Species species = Species.NULLSPECIES;
   IBlockState potState = Blocks.FLOWER_POT.getDefaultState();
   ResourceLocation speciesName;

   public TileEntityBonsai() {
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

   public IBlockState getPot() {
      return this.potState;
   }

   public void setPot(IBlockState newPotState) {
      if (newPotState.getBlock() instanceof BlockFlowerPot) {
         this.potState = newPotState.getBlock().getDefaultState();
      } else {
         this.potState = Blocks.FLOWER_POT.getDefaultState();
      }

      this.markDirty();
   }

   private void read(NBTTagCompound tag) {
      if (tag.hasKey("species")) {
         this.speciesName = new ResourceLocation(tag.getString("species"));
         this.species = TreeRegistry.findSpecies(this.speciesName);
      }

      if (tag.hasKey("pot")) {
         Block block = (Block)Block.REGISTRY.getObject(new ResourceLocation(tag.getString("pot")));
         this.potState = block != Blocks.AIR ? block.getDefaultState() : Blocks.FLOWER_POT.getDefaultState();
      }

   }

   private void write(NBTTagCompound tag) {
      tag.setString("species", this.speciesName.toString());
      tag.setString("pot", this.potState.getBlock().getRegistryName().toString());
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

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      this.read(pkt.getNbtCompound());
   }

   public NBTTagCompound getUpdateTag() {
      return this.writeToNBT(new NBTTagCompound());
   }
}
