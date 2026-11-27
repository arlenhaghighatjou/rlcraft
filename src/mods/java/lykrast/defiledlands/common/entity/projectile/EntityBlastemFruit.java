package lykrast.defiledlands.common.entity.projectile;

import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.ExplosionEvent.Detonate;

public class EntityBlastemFruit extends EntityThrowable {
   protected float damage;
   protected float explosion;
   protected boolean destructive;

   public EntityBlastemFruit(World worldIn) {
      super(worldIn);
      this.damage = 7.0F;
      this.explosion = 1.0F;
      this.destructive = true;
   }

   public EntityBlastemFruit(World worldIn, EntityLivingBase throwerIn) {
      super(worldIn, throwerIn);
      this.damage = 7.0F;
      this.explosion = 1.0F;
      this.destructive = true;
   }

   public EntityBlastemFruit(World worldIn, double x, double y, double z) {
      this(worldIn);
      this.setPosition(x, y, z);
   }

   protected void onImpact(RayTraceResult result) {
      if (result.entityHit != null) {
         result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), this.damage);
      }

      if (!this.world.isRemote) {
         this.explosion(false);
         this.setDead();
      }

   }

   protected void explosion(boolean isFlaming) {
      final Explosion explosion = new EntityBlastemFruit.ExplosionBlastem(this.world, this, this.posX, this.posY, this.posZ, this.explosion, isFlaming, this.destructive);
      if (!ForgeEventFactory.onExplosionStart(this.world, explosion)) {
         Consumer<Detonate> listener = (event) -> {
            if (event.getExplosion() == explosion) {
               event.getAffectedEntities().removeIf((ent) -> {
                  return ent instanceof EntityItem;
               });
            }

         };

         try {
            MinecraftForge.EVENT_BUS.register(Detonate.class, listener);
            explosion.doExplosionA();
         } finally {
            MinecraftForge.EVENT_BUS.unregister(listener);
         }

         explosion.doExplosionB(false);
      }

      if (!this.destructive) {
         explosion.clearAffectedBlockPositions();
      }

      for(EntityPlayer entityplayer : this.world.playerEntities) {
         if (entityplayer.getDistanceSq(this.posX, this.posY, this.posZ) < 4096.0D) {
            ((EntityPlayerMP)entityplayer).connection.sendPacket(new SPacketExplosion(this.posX, this.posY, this.posZ, this.explosion, explosion.getAffectedBlockPositions(), (Vec3d)explosion.getPlayerKnockbackMap().get(entityplayer)));
         }
      }

   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setFloat("damage", this.damage);
      compound.setFloat("explosion", this.explosion);
      compound.setBoolean("destructive", this.destructive);
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      if (compound.hasKey("damage", 99)) {
         this.damage = compound.getFloat("damage");
      }

      if (compound.hasKey("explosion", 99)) {
         this.explosion = compound.getFloat("explosion");
      }

      if (compound.hasKey("destructive", 99)) {
         this.destructive = compound.getBoolean("destructive");
      }

   }

   public void setDamage(float damage) {
      this.damage = damage;
   }

   public float getDamage() {
      return this.damage;
   }

   public void setExplosionStrength(float explosion) {
      this.explosion = explosion;
   }

   public float getExplosionStrength() {
      return this.explosion;
   }

   public void setDestructive(boolean destructive) {
      this.destructive = destructive;
   }

   public boolean isDestructive() {
      return this.destructive;
   }

   public static class ExplosionBlastem extends Explosion {
      private EntityBlastemFruit projectile;

      public ExplosionBlastem(World worldIn, EntityBlastemFruit entityIn, double x, double y, double z, float size, boolean flaming, boolean damagesTerrain) {
         super(worldIn, entityIn, x, y, z, size, flaming, damagesTerrain);
         this.projectile = entityIn;
      }

      public EntityLivingBase getExplosivePlacedBy() {
         return this.projectile.getThrower();
      }

      public void doExplosionB(boolean spawnParticles) {
         super.doExplosionB(spawnParticles);
      }
   }
}
