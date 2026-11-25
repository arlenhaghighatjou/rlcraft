package com.mujmajnkraft.bettersurvival.tileentities;

import com.mujmajnkraft.bettersurvival.init.ModBlocks;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityCustomCauldron extends TileEntity implements ITickable {
   public PotionType type = PotionTypes.EMPTY;
   public int color = 16253176;
   public List<PotionEffect> effects = new ArrayList();

   public TileEntityCustomCauldron() {
   }

   public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
      return oldState.getBlock() != newSate.getBlock();
   }

   @SideOnly(Side.CLIENT)
   public int getColor() {
      if (this.color == 16253176) {
         return !this.effects.isEmpty() ? PotionUtils.getPotionColorFromEffectList(this.effects) : PotionUtils.getPotionColor(this.type);
      } else {
         return this.color;
      }
   }

   public boolean effectmatches(ItemStack stack) {
      boolean flag1 = true;
      boolean flag2 = true;
      if (stack.hasTagCompound()) {
         NBTTagCompound compound = stack.getTagCompound();
         flag1 = this.type == PotionUtils.getPotionTypeFromNBT(compound);
         if (!this.effects.isEmpty()) {
            flag2 = this.effects.equals(PotionUtils.getFullEffectsFromItem(stack));
         }
      }

      boolean flag = flag1 && flag2;
      return flag;
   }

   public void setEffect(ItemStack stack) {
      if (stack.hasTagCompound()) {
         NBTTagCompound compound = stack.getTagCompound();
         this.type = PotionUtils.getPotionTypeFromNBT(compound);
         this.effects = PotionUtils.getFullEffectsFromTag(compound);
         if (compound.hasKey("CustomPotionColor")) {
            this.color = compound.getInteger("CustomPotionColor");
         }
      }

      this.markDirty();
      this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
   }

   public ItemStack getEffect(ItemStack stack) {
      PotionUtils.addPotionToItemStack(stack, this.type);
      if (!this.effects.isEmpty()) {
         PotionUtils.appendEffects(stack, this.effects);
      }

      if (this.color != 16253176) {
         NBTTagCompound compound = stack.getTagCompound();
         compound.setInteger("CustomPotionColor", this.color);
      }

      this.markDirty();
      this.world.notifyBlockUpdate(this.pos, this.world.getBlockState(this.pos), this.world.getBlockState(this.pos), 3);
      return stack;
   }

   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      if (compound.hasKey("CustomPotionColor")) {
         this.color = compound.getInteger("CustomPotionColor");
      }

      this.type = PotionUtils.getPotionTypeFromNBT(compound);
      if (compound.hasKey("CustomPotionEffects")) {
         this.effects = PotionUtils.getFullEffectsFromTag(compound);
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      if (this.color != 16253176) {
         compound.setInteger("CustomPotionColor", this.color);
      }

      if (this.type != PotionTypes.EMPTY) {
         ResourceLocation resourcelocation = (ResourceLocation)PotionType.REGISTRY.getNameForObject(this.type);
         compound.setString("Potion", resourcelocation.toString());
      }

      if (!this.effects.isEmpty()) {
         new NBTTagList();
         NBTTagList var5;
         if (compound.hasKey("CustomPotionEffects")) {
            var5 = (NBTTagList)compound.getTag("CustomPotionEffects");
         } else {
            var5 = new NBTTagList();
         }

         for(PotionEffect potioneffect : this.effects) {
            var5.appendTag(potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
         }

         compound.setTag("CustomPotionEffects", var5);
      }

      return compound;
   }

   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
      NBTTagCompound compound = pkt.getNbtCompound();
      this.readUpdateTag(compound);
   }

   public SPacketUpdateTileEntity getUpdatePacket() {
      NBTTagCompound compound = new NBTTagCompound();
      this.writeUpdateTag(compound);
      return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), compound);
   }

   public NBTTagCompound getUpdateTag() {
      NBTTagCompound compound = super.getUpdateTag();
      this.writeUpdateTag(compound);
      return compound;
   }

   public void writeUpdateTag(NBTTagCompound compound) {
      if (this.color != 16253176) {
         compound.setInteger("CustomPotionColor", this.color);
      }

      if (this.type != PotionTypes.EMPTY) {
         ResourceLocation resourcelocation = (ResourceLocation)PotionType.REGISTRY.getNameForObject(this.type);
         compound.setString("Potion", resourcelocation.toString());
      }

      if (!this.effects.isEmpty()) {
         new NBTTagList();
         NBTTagList var5;
         if (compound.hasKey("CustomPotionEffects")) {
            var5 = (NBTTagList)compound.getTag("CustomPotionEffects");
         } else {
            var5 = new NBTTagList();
         }

         for(PotionEffect potioneffect : this.effects) {
            var5.appendTag(potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
         }

         compound.setTag("CustomPotionEffects", var5);
      }

   }

   public void readUpdateTag(NBTTagCompound compound) {
      super.readFromNBT(compound);
      if (compound.hasKey("CustomPotionColor")) {
         this.color = compound.getInteger("CustomPotionColor");
      }

      this.type = PotionUtils.getPotionTypeFromNBT(compound);
      if (compound.hasKey("CustomPotionEffects")) {
         this.effects = PotionUtils.getFullEffectsFromTag(compound);
      }

   }

   public void update() {
      if (this.getWorld().getBlockState(this.getPos()).getBlock() == ModBlocks.customcauldron && ((Integer)this.getWorld().getBlockState(this.getPos()).getValue(BlockCauldron.LEVEL)).intValue() == 0) {
         this.getWorld().setBlockState(this.pos, Blocks.CAULDRON.getDefaultState());
         this.markDirty();
      }

   }
}
