package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.Initial;
import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.Config.SwitchBowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import de.Whitedraco.switchbow.proxy.packets.PatricleExplosionPacket;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowTNT extends EntityArrowBase implements IThrowableEntity {
   private boolean spawnExplosion = true;
   private double damagesetter = ArrowConfig.DamageArrowTNT.getDoub();

   public EntityArrowTNT(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowTNT(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowTNT(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowTNT);
   }

   protected void arrowHit(EntityLivingBase living) {
      if (this.spawnExplosion) {
         Explosion explosion = new Explosion(this.world, this, this.posX, this.posY, this.posZ, ArrowConfig.ExplodeRadiusArrowTNT.getFloa(), false, true);
         explosion.doExplosionA();

         for(BlockPos blockpos : explosion.getAffectedBlockPositions()) {
            IBlockState iblockstate = this.world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();
            double d0 = (double)((float)blockpos.getX() + this.world.rand.nextFloat());
            double d1 = (double)((float)blockpos.getY() + this.world.rand.nextFloat());
            double d2 = (double)((float)blockpos.getZ() + this.world.rand.nextFloat());
            double d3 = d0 - explosion.getPosition().x;
            double d4 = d1 - explosion.getPosition().y;
            double d5 = d2 - explosion.getPosition().z;
            double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
            d3 = d3 / d6;
            d4 = d4 / d6;
            d5 = d5 / d6;
            double d7 = 0.5D / (d6 / 3.0D + 0.1D);
            d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
            d3 = d3 * d7;
            d4 = d4 * d7;
            d5 = d5 * d7;
            Initial.network.sendToAll(new PatricleExplosionPacket(explosion.getPosition().x, explosion.getPosition().y, explosion.getPosition().z, d0, d1, d2, d3, d4, d5));
         }

         if (!SwitchBowConfig.destroyBlockswithTNTArrow.getBool()) {
            explosion.clearAffectedBlockPositions();
         }

         explosion.doExplosionB(true);
         this.spawnExplosion = false;
      }

      this.setDead();
   }

   protected void arrowHitGround(BlockPos blockposi) {
      if (!this.world.isRemote && this.spawnExplosion) {
         Explosion explosion = new Explosion(this.world, this, this.posX, this.posY, this.posZ, ArrowConfig.ExplodeRadiusArrowTNT.getFloa(), false, true);
         explosion.doExplosionA();

         for(BlockPos blockpos : explosion.getAffectedBlockPositions()) {
            IBlockState iblockstate = this.world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();
            double d0 = (double)((float)blockpos.getX() + this.world.rand.nextFloat());
            double d1 = (double)((float)blockpos.getY() + this.world.rand.nextFloat());
            double d2 = (double)((float)blockpos.getZ() + this.world.rand.nextFloat());
            double d3 = d0 - explosion.getPosition().x;
            double d4 = d1 - explosion.getPosition().y;
            double d5 = d2 - explosion.getPosition().z;
            double d6 = (double)MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
            d3 = d3 / d6;
            d4 = d4 / d6;
            d5 = d5 / d6;
            double d7 = 0.5D / (d6 / 3.0D + 0.1D);
            d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
            d3 = d3 * d7;
            d4 = d4 * d7;
            d5 = d5 * d7;
            Initial.network.sendToAll(new PatricleExplosionPacket(explosion.getPosition().x, explosion.getPosition().y, explosion.getPosition().z, d0, d1, d2, d3, d4, d5));
         }

         if (!SwitchBowConfig.destroyBlockswithTNTArrow.getBool()) {
            explosion.clearAffectedBlockPositions();
         }

         explosion.doExplosionB(true);
         this.spawnExplosion = false;
      }

      this.setDead();
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      compound.setBoolean("spawnExplosion", this.spawnExplosion);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      this.spawnExplosion = compound.getBoolean("spawnExplosion");
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
