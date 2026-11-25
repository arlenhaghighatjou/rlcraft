package org.orecruncher.dsurround.client.footsteps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.client.footsteps.facade.FacadeHelper;
import org.orecruncher.dsurround.registry.RegistryManager;
import org.orecruncher.lib.WorldUtils;

@SideOnly(Side.CLIENT)
public final class FootStrikeLocation {
   private final EntityLivingBase entity;
   private final Vec3d strike;
   private final BlockPos stepPos;

   public FootStrikeLocation(@Nonnull EntityLivingBase entity, double x, double y, double z) {
      this(entity, new Vec3d(x, y, z));
   }

   public FootStrikeLocation(@Nonnull EntityLivingBase entity, @Nonnull Vec3d loc) {
      this.entity = entity;
      this.strike = loc;
      this.stepPos = new BlockPos(loc);
   }

   protected FootStrikeLocation(@Nonnull EntityLivingBase entity, @Nonnull Vec3d loc, @Nonnull BlockPos pos) {
      this.entity = entity;
      this.strike = loc;
      this.stepPos = pos;
   }

   public FootStrikeLocation rebase(@Nonnull BlockPos pos) {
      return !this.stepPos.equals(pos) ? new FootStrikeLocation(this.entity, this.strike, pos) : this;
   }

   @Nonnull
   public EntityLivingBase getEntity() {
      return this.entity;
   }

   @Nonnull
   public BlockPos getStepPos() {
      return this.stepPos;
   }

   @Nonnull
   public Vec3d getStrikePosition() {
      return this.strike;
   }

   public Vec3d north() {
      return this.offset(EnumFacing.NORTH, 1.0F);
   }

   public Vec3d south() {
      return this.offset(EnumFacing.SOUTH, 1.0F);
   }

   public Vec3d east() {
      return this.offset(EnumFacing.EAST, 1.0F);
   }

   public Vec3d west() {
      return this.offset(EnumFacing.WEST, 1.0F);
   }

   public Vec3d up() {
      return this.offset(EnumFacing.UP, 1.0F);
   }

   public Vec3d down() {
      return this.offset(EnumFacing.DOWN, 1.0F);
   }

   @Nonnull
   public Vec3d offset(@Nonnull EnumFacing facing, float n) {
      return n == 0.0F ? this.strike : new Vec3d(this.strike.x + (double)((float)facing.getFrontOffsetX() * n), this.strike.y + (double)((float)facing.getFrontOffsetY() * n), this.strike.z + (double)((float)facing.getFrontOffsetZ() * n));
   }

   @Nullable
   protected Vec3d footprintPosition() {
      World world = this.entity.getEntityWorld();
      IBlockState state = WorldUtils.getBlockState(world, this.stepPos);
      if (this.hasFootstepImprint(world, state, this.strike)) {
         double entityY = this.entity.getEntityBoundingBox().minY;
         double blockY = this.getBoundingBoxY(entityY, world, state, this.stepPos);
         return new Vec3d(this.strike.x, Math.max(entityY, blockY), this.strike.z);
      } else {
         return null;
      }
   }

   protected double getBoundingBoxY(double baseY, @Nonnull IBlockAccess world, @Nonnull IBlockState state, @Nonnull BlockPos pos) {
      AxisAlignedBB bounding = state.getBoundingBox(world, pos);
      AxisAlignedBB collision = state.getCollisionBoundingBox(world, pos);
      double boundingY = bounding.maxY;
      double collisionY = collision == null ? baseY : collision.maxY;
      return boundingY == collisionY ? baseY : Math.max(baseY, (double)pos.getY() + Math.max(boundingY, collisionY));
   }

   protected boolean hasFootstepImprint(@Nonnull World world, @Nonnull IBlockState state, @Nonnull Vec3d pos) {
      IBlockState footstepState = FacadeHelper.resolveState(this.entity, state, world, pos, EnumFacing.UP);
      return RegistryManager.FOOTSTEPS.hasFootprint(footstepState);
   }
}
