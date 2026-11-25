package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.Initial;
import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import de.Whitedraco.switchbow.proxy.packets.ParticleSplittingPacket;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowSplit extends EntityArrowBase implements IThrowableEntity {
   private double damagesetter = ArrowConfig.DamageArrowSplit.getDoub();

   public EntityArrowSplit(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowSplit(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowSplit(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowSplit);
   }

   protected void arrowHit(EntityLivingBase living) {
      this.AOEEffekt(living.posX, living.posY, living.posZ);
   }

   protected void arrowHitGround(BlockPos blockpos) {
      this.AOEEffekt(this.posX, this.posY, this.posZ);
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }

   private void AOEEffekt(double posx, double posy, double posz) {
      double Umkreis = 1.5D;
      List Mobliste = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(posx - Umkreis, posy - 1.5D, posz - Umkreis, posx + Umkreis, posy + 1.5D, posz + Umkreis));
      if (Mobliste.size() > 0) {
         for(int a = 0; a < Mobliste.size(); ++a) {
            EntityLivingBase Mob = (EntityLivingBase)Mobliste.get(a);
            Mob.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 400, 1, true, true));
            DamageSource damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity);
            Mob.attackEntityFrom(damagesource, 2.5F);
         }
      }

      if (!this.world.isRemote) {
         for(int x = 0; x < 29; ++x) {
            this.Spawn(this.world, posx, posy + 0.5D, posz);
            this.Spawn(this.world, posx - 1.0D, posy + 0.5D, posz - 1.0D);
            this.Spawn(this.world, posx, posy + 0.5D, posz - 1.0D);
            this.Spawn(this.world, posx + 1.0D, posy + 0.5D, posz - 1.0D);
            this.Spawn(this.world, posx - 1.0D, posy + 0.5D, posz);
            this.Spawn(this.world, posx + 1.0D, posy + 0.5D, posz);
            this.Spawn(this.world, posx - 1.0D, posy + 0.5D, posz + 1.0D);
            this.Spawn(this.world, posx, posy + 0.5D, posz + 0.5D);
            this.Spawn(this.world, posx + 1.0D, posy + 0.5D, posz + 1.0D);
         }
      }

      this.setDead();
   }

   private void Spawn(World worldObj, double posX, double posY, double posZ) {
      double motionX = this.rand.nextDouble() * 0.02D;
      double motionY = this.rand.nextDouble() * 0.02D;
      double motionZ = this.rand.nextDouble() * 0.02D;
      Initial.network.sendToAll(new ParticleSplittingPacket(posX, posY, posZ, motionX, motionY, motionZ));
   }
}
