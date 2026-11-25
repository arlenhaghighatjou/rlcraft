package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.Initial;
import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import de.Whitedraco.switchbow.proxy.packets.PatricleWaterDropPacket;
import java.util.List;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowSprinkler extends EntityArrowBase implements IThrowableEntity {
   public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 15);
   private double damagesetter = ArrowConfig.DamageArrowSprinkler.getDoub();

   public EntityArrowSprinkler(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowSprinkler(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowSprinkler(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowSprinkler);
   }

   protected void arrowHit(EntityLivingBase living) {
      BlockPos blockpos = living.getPosition().up();
      this.extinguishFireswithNeighbor(blockpos);
      this.extinguishFireswithNeighbor(blockpos.north());
      this.extinguishFireswithNeighbor(blockpos.east());
      this.extinguishFireswithNeighbor(blockpos.west());
      this.extinguishFireswithNeighbor(blockpos.south());
      this.extinguishFireswithNeighbor(blockpos.up());
      this.extinguishFireswithNeighbor(blockpos.down());
      this.applyWater();
   }

   private void extinguishFires(BlockPos pos) {
      if (!this.world.isRemote) {
         if (this.world.getBlockState(pos).getBlock() == Blocks.FIRE) {
            this.world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
         }

         if (this.world.getBlockState(pos).getBlock() == Blocks.TORCH) {
            this.world.destroyBlock(pos, true);
         }

         if (this.world.getBlockState(pos).getBlock() == Blocks.LAVA && this.world.getBlockState(pos).getBlock() != Blocks.FLOWING_LAVA && ((Integer)this.world.getBlockState(pos).getValue(LEVEL)).intValue() == 0) {
            this.world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
         }

         for(int i = 0; i < 8; ++i) {
            Initial.network.sendToAll(new PatricleWaterDropPacket((double)pos.getX() - 0.5D + Math.random(), (double)pos.getY() + 0.25D, (double)pos.getZ() - 0.5D + Math.random(), 0.0D, 0.0D, 0.0D));
         }
      }

   }

   private void applyWater() {
      AxisAlignedBB axisalignedbb = this.getEntityBoundingBox().expand(4.5D, 3.0D, 4.5D);
      List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
      if (!list.isEmpty()) {
         for(EntityLivingBase entitylivingbase : list) {
            double d0 = this.getDistanceSq(entitylivingbase);
            if (d0 < 16.0D && isWaterSensitiveEntity(entitylivingbase)) {
               entitylivingbase.attackEntityFrom(DamageSource.DROWN, 1.0F);
            }

            if (d0 < 16.0D && entitylivingbase.isBurning()) {
               entitylivingbase.extinguish();
            }
         }
      }

   }

   private static boolean isWaterSensitiveEntity(EntityLivingBase p_190544_0_) {
      return p_190544_0_ instanceof EntityEnderman || p_190544_0_ instanceof EntityBlaze;
   }

   protected void arrowHitGround(BlockPos blockpos) {
      blockpos = blockpos.up();
      this.extinguishFireswithNeighbor(blockpos);
      this.extinguishFireswithNeighbor(blockpos.north());
      this.extinguishFireswithNeighbor(blockpos.east());
      this.extinguishFireswithNeighbor(blockpos.west());
      this.extinguishFireswithNeighbor(blockpos.south());
      this.extinguishFireswithNeighbor(blockpos.up());
      this.extinguishFireswithNeighbor(blockpos.down());
      this.applyWater();
      this.setDead();
   }

   private void extinguishFireswithNeighbor(BlockPos blockpos) {
      this.extinguishFires(blockpos);
      this.extinguishFires(blockpos.up());
      this.extinguishFires(blockpos.down());
      this.extinguishFires(blockpos.north());
      this.extinguishFires(blockpos.east());
      this.extinguishFires(blockpos.south());
      this.extinguishFires(blockpos.west());
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
