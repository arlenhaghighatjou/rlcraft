package artifacts.common.entity;

import artifacts.common.ModConfig;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ParametersAreNonnullByDefault
public class EntityHallowStar extends Entity {
   @Nullable
   public EntityLivingBase shootingEntity;
   public int ticksAlive;

   public EntityHallowStar(World world) {
      super(world);
      this.setSize(0.5F, 0.5F);
   }

   public EntityHallowStar(World world, EntityLivingBase shooter) {
      this(world);
      this.shootingEntity = shooter;
      double offSetX = 8.0D * (2.0D * this.rand.nextDouble() - 1.0D);
      double offSetZ = 8.0D * (2.0D * this.rand.nextDouble() - 1.0D);
      double offSetY = 25.0D + this.rand.nextDouble() * 15.0D;
      this.setPosition(shooter.posX + offSetX - 1.0D + (double)(this.rand.nextFloat() * 2.0F), shooter.posY + offSetY, shooter.posZ + offSetZ - 1.0D + (double)(this.rand.nextFloat() * 2.0F));
      double velocity = Math.sqrt(offSetX * offSetX + offSetY * offSetY + offSetZ * offSetZ);
      this.motionX = -offSetX * 1.5D / velocity;
      this.motionY = -offSetY * 1.5D / velocity;
      this.motionZ = -offSetZ * 1.5D / velocity;
   }

   protected void entityInit() {
   }

   @SideOnly(Side.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      double d = this.getEntityBoundingBox().getAverageEdgeLength() * 6.0D;
      if (Double.isNaN(d)) {
         d = 4.0D;
      }

      d = d * 64.0D;
      return distance < d * d;
   }

   public void onUpdate() {
      if (this.world.isRemote || (this.shootingEntity == null || !this.shootingEntity.isDead) && this.world.isBlockLoaded(new BlockPos(this)) && this.ticksAlive < 200) {
         super.onUpdate();
         ++this.ticksAlive;
         RayTraceResult raytraceresult = ProjectileHelper.forwardsRaycast(this, true, true, this.shootingEntity);
         if (raytraceresult != null && !ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
            this.onImpact(raytraceresult);
         }

         this.posX += this.motionX;
         this.posY += this.motionY;
         this.posZ += this.motionZ;
         ProjectileHelper.rotateTowardsMovement(this, 0.2F);
         if (this.isInWater()) {
            for(int i = 0; i < 4; ++i) {
               this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ, new int[0]);
            }
         }

         this.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
         this.setPosition(this.posX, this.posY, this.posZ);
      } else {
         this.setDead();
      }

   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
      this.ticksAlive = compound.getInteger("tickAlive");
      if (compound.hasKey("direction", 9) && compound.getTagList("direction", 6).tagCount() == 3) {
         NBTTagList nbtTagList = compound.getTagList("direction", 6);
         this.motionX = nbtTagList.getDoubleAt(0);
         this.motionY = nbtTagList.getDoubleAt(1);
         this.motionZ = nbtTagList.getDoubleAt(2);
      }

   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
      compound.setTag("direction", this.newDoubleNBTList(new double[]{this.motionX, this.motionY, this.motionZ}));
      compound.setInteger("ticksAlive", this.ticksAlive);
   }

   protected void onImpact(RayTraceResult result) {
      if (!this.world.isRemote && result.entityHit != this.shootingEntity && !(result.entityHit instanceof EntityHallowStar)) {
         if (result.entityHit instanceof EntityLivingBase) {
            result.entityHit.attackEntityFrom((new EntityDamageSourceIndirect("artifacts.hallow_star", this, this.shootingEntity)).setProjectile(), (float)ModConfig.general.starCloakDamage);
         }

         this.world.setEntityState(this, (byte)3);
      }

      this.playSound(SoundEvents.BLOCK_WOOD_PLACE, 1.0F, 1.0F);
      this.setDead();
   }

   public float getBrightness() {
      return 1.0F;
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 3) {
         for(int i = 0; i < 16; ++i) {
            this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + this.rand.nextGaussian(), this.posY - 0.5D * this.rand.nextDouble(), this.posZ + this.rand.nextGaussian(), 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }
}
