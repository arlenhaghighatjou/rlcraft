package com.teamacronymcoders.contenttweaker.api.ctobjects.world;

import com.teamacronymcoders.contenttweaker.api.ctobjects.blockpos.IBlockPos;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockstate.ICTBlockState;
import com.teamacronymcoders.contenttweaker.api.ctobjects.blockstate.MCBlockState;
import com.teamacronymcoders.contenttweaker.api.ctobjects.random.CTRandom;
import com.teamacronymcoders.contenttweaker.api.ctobjects.tileentity.IMCTileEntity;
import com.teamacronymcoders.contenttweaker.api.ctobjects.tileentity.MCTileEntity;
import crafttweaker.api.world.IBiome;
import crafttweaker.mc1120.world.MCBiome;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MCWorld extends crafttweaker.mc1120.world.MCWorld implements IWorld {
   private World world;

   public MCWorld(World world) {
      super(world);
      this.world = world;
   }

   public boolean isRemote() {
      return this.world.isRemote;
   }

   public boolean isRaining() {
      return this.world.getWorldInfo().isRaining();
   }

   public boolean isThundering() {
      return this.world.getWorldInfo().isThundering();
   }

   public boolean setBlockState(ICTBlockState blockState, IBlockPos blockPos) {
      return this.world.setBlockState((BlockPos)blockPos.getInternal(), (IBlockState)blockState.getInternal(), 3);
   }

   public ICTBlockState getBlockState(IBlockPos blockPos) {
      return new MCBlockState(this.world.getBlockState((BlockPos)blockPos.getInternal()));
   }

   public IBiome getBiome(IBlockPos blockPos) {
      return new MCBiome(this.world.getBiome((BlockPos)blockPos.getInternal()));
   }

   public int getMoonPhase() {
      return this.world.provider.getMoonPhase(this.world.getWorldInfo().getWorldTime());
   }

   public boolean isDayTime() {
      return this.world.isDaytime();
   }

   public long getWorldTime() {
      return this.world.getWorldTime();
   }

   public String getWorldType() {
      return this.world.getWorldType().getName();
   }

   public int getDimension() {
      return this.world.provider.getDimension();
   }

   public boolean isSurfaceWorld() {
      return this.world.provider.isSurfaceWorld();
   }

   public String getDimensionType() {
      return this.world.provider.getDimensionType().getName();
   }

   public CTRandom getRandom() {
      return new CTRandom(this.world.rand);
   }

   public IMCTileEntity getTileEntity(IBlockPos blockPos) {
      return (IMCTileEntity)Optional.ofNullable(this.world.getTileEntity((BlockPos)blockPos.getInternal())).map(MCTileEntity::new).orElse((Object)null);
   }

   public World getInternal() {
      return this.world;
   }
}
