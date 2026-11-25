package crafttweaker.mc1120.block;

import crafttweaker.api.block.IBlockProperties;
import crafttweaker.api.block.IBlockState;
import crafttweaker.api.block.IMaterial;
import crafttweaker.api.block.IMobilityFlag;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IBlockAccess;
import crafttweaker.api.world.IBlockPos;
import crafttweaker.api.world.IFacing;
import crafttweaker.api.world.IWorld;
import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MCBlockProperties implements IBlockProperties {
   private final net.minecraft.block.state.IBlockProperties properties;

   public MCBlockProperties(net.minecraft.block.state.IBlockProperties properties) {
      this.properties = properties;
   }

   public int getLightValue(IBlockAccess access, IBlockPos blockPos) {
      return this.properties.getLightValue((net.minecraft.world.IBlockAccess)access.getInternal(), (BlockPos)blockPos.getInternal());
   }

   public boolean canProvidePower() {
      return this.properties.canProvidePower();
   }

   public int getWeakPower(IBlockAccess access, IBlockPos blockPos, IFacing facing) {
      return this.properties.getWeakPower((net.minecraft.world.IBlockAccess)access.getInternal(), (BlockPos)blockPos.getInternal(), (EnumFacing)facing.getInternal());
   }

   public int getComparatorInputOverride(IWorld world, IBlockPos blockPos) {
      return this.properties.getComparatorInputOverride((World)world.getInternal(), (BlockPos)blockPos.getInternal());
   }

   public IMobilityFlag getMobilityFlag() {
      return new MCMobilityFlag(this.properties.getMobilityFlag());
   }

   public Object getInternal() {
      return this.properties;
   }

   public IMaterial getMaterial() {
      return new MCMaterial(this.properties.getMaterial());
   }

   public boolean canEntitySpawn(IEntity entity) {
      return entity.getInternal() instanceof Entity && this.properties.canEntitySpawn((Entity)entity.getInternal());
   }

   public boolean causesSuffocation() {
      return this.properties.causesSuffocation();
   }

   public boolean doesSideBlockRendering(IBlockAccess access, IBlockPos pos, IFacing facing) {
      return this.properties.doesSideBlockRendering((net.minecraft.world.IBlockAccess)access.getInternal(), (BlockPos)pos.getInternal(), (EnumFacing)facing.getInternal());
   }

   public IBlockState getActualState(IBlockAccess access, IBlockPos pos) {
      return new MCBlockState(this.properties.getActualState((net.minecraft.world.IBlockAccess)access.getInternal(), (BlockPos)pos.getInternal()));
   }

   public float getBlockHardness(IWorld world, IBlockPos pos) {
      return this.properties.getBlockHardness((World)world.getInternal(), (BlockPos)pos.getInternal());
   }

   public int getLightOpacity(IBlockAccess access, IBlockPos pos) {
      return this.properties.getLightOpacity((net.minecraft.world.IBlockAccess)access.getInternal(), (BlockPos)pos.getInternal());
   }

   public float getPlayerRelativeBlockHardness(IPlayer player, IWorld world, IBlockPos pos) {
      return !(player.getInternal() instanceof EntityPlayer) ? 0.0F : this.properties.getPlayerRelativeBlockHardness((EntityPlayer)player.getInternal(), (World)world.getInternal(), (BlockPos)pos.getInternal());
   }

   public int getStrongPower(IBlockAccess access, IBlockPos pos, IFacing facing) {
      return this.properties.getStrongPower((net.minecraft.world.IBlockAccess)access.getInternal(), (BlockPos)pos.getInternal(), (EnumFacing)facing.getInternal());
   }

   public boolean hasCustomBreakingProgress() {
      return this.properties.hasCustomBreakingProgress();
   }

   public boolean isBlockNormalCube() {
      return this.properties.isBlockNormalCube();
   }

   public boolean isFullBlock() {
      return this.properties.isFullBlock();
   }

   public boolean isFullCube() {
      return this.properties.isFullCube();
   }

   public boolean isNormalCube() {
      return this.properties.isNormalCube();
   }

   public boolean isOpaqueCube() {
      return this.properties.isOpaqueCube();
   }

   public boolean isSideSolid(IBlockAccess access, IBlockPos pos, IFacing facing) {
      return this.properties.isSideSolid((net.minecraft.world.IBlockAccess)access.getInternal(), (BlockPos)pos.getInternal(), (EnumFacing)facing.getInternal());
   }

   public boolean useNeighborBrightness() {
      return this.properties.useNeighborBrightness();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         MCBlockProperties that = (MCBlockProperties)o;
         return Objects.equals(this.properties, that.properties);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.properties});
   }
}
