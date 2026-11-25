package net.theawesomegem.fishingmadebetter.common.entity;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityFMBLavaFishHook extends EntityFMBCustomFishHook {
   @SideOnly(Side.CLIENT)
   public EntityFMBLavaFishHook(World worldIn, EntityPlayer p_i47290_2_, double x, double y, double z) {
      super(worldIn, p_i47290_2_, x, y, z);
      this.isImmuneToFire = true;
   }

   public EntityFMBLavaFishHook(World worldIn, EntityPlayer fishingPlayer) {
      super(worldIn, fishingPlayer);
      this.isImmuneToFire = true;
   }

   public EntityFMBLavaFishHook(World worldIn) {
      super(worldIn, worldIn.getPlayerEntityByUUID(Minecraft.getMinecraft().getSession().getProfile().getId()));
      this.isImmuneToFire = true;
   }

   public boolean isInWater() {
      return this.isInLava();
   }

   public float getLiquidHeight(World worldIn, BlockPos blockpos) {
      IBlockState iblockstate = worldIn.getBlockState(blockpos);
      return iblockstate.getMaterial() == MaterialLiquid.LAVA ? BlockLiquid.getBlockLiquidHeight(iblockstate, worldIn, blockpos) : 0.0F;
   }

   public double getLiquidMotion() {
      return 0.08D;
   }

   public SoundEvent getSoundEvent() {
      return SoundEvents.ENTITY_ENDEREYE_DEATH;
   }

   public EnumParticleTypes getBubbleParticle() {
      return EnumParticleTypes.SMOKE_NORMAL;
   }

   public EnumParticleTypes getWakeParticle() {
      return EnumParticleTypes.SMOKE_LARGE;
   }

   public EnumParticleTypes getSplashParticle() {
      return EnumParticleTypes.LAVA;
   }
}
