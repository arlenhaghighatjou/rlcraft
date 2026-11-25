package de.Whitedraco.switchbow.entity.arrow.upgrade;

import de.Whitedraco.switchbow.Initial;
import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import de.Whitedraco.switchbow.proxy.packets.ParticleFreezePacket;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowAirUpgrade extends EntityArrowBase implements IThrowableEntity {
   private double damagesetter = ArrowConfig.DamageArrowAirUpgrade.getDoub();

   public EntityArrowAirUpgrade(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowAirUpgrade(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowAirUpgrade(World worldIn, EntityLivingBase shooter) {
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

   private void AOEEffekt(double posx, double posy, double posz) {
      double Umkreis = 2.0D;
      List Mobliste = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(posx - Umkreis, posy - 1.5D, posz - Umkreis, posx + Umkreis, posy + 1.5D, posz + Umkreis));
      if (Mobliste.size() > 0) {
         for(int a = 0; a < Mobliste.size(); ++a) {
            EntityLivingBase Mob = (EntityLivingBase)Mobliste.get(a);
            Mob.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 80, 1, true, true));
         }
      }

      if (!this.world.isRemote) {
         for(int x = 0; x < 29; ++x) {
            this.Spawn(this.world, posx, posy + 0.5D, posz);
            this.Spawn(this.world, posx - this.rand.nextDouble() - this.rand.nextDouble(), posy + 0.5D, posz - this.rand.nextDouble() - this.rand.nextDouble());
            this.Spawn(this.world, posx, posy + 0.5D, posz - this.rand.nextDouble());
            this.Spawn(this.world, posx + this.rand.nextDouble() + this.rand.nextDouble(), posy + 0.5D, posz - this.rand.nextDouble() - this.rand.nextDouble());
            this.Spawn(this.world, posx - this.rand.nextDouble() - this.rand.nextDouble(), posy + 0.5D, posz);
            this.Spawn(this.world, posx + this.rand.nextDouble() + this.rand.nextDouble(), posy + 0.5D, posz);
            this.Spawn(this.world, posx - this.rand.nextDouble() - this.rand.nextDouble(), posy + 0.5D, posz + this.rand.nextDouble() + this.rand.nextDouble());
            this.Spawn(this.world, posx, posy + 0.5D, posz + this.rand.nextDouble() + this.rand.nextDouble());
            this.Spawn(this.world, posx + this.rand.nextDouble() + this.rand.nextDouble(), posy + 0.5D, posz + this.rand.nextDouble() + this.rand.nextDouble());
         }
      }

      this.setDead();
   }

   private void Spawn(World worldObj, double posX, double posY, double posZ) {
      double d0 = 3.0D;
      double d1 = 10.0D;
      double d2 = 50.0D;
      Initial.network.sendToAll(new ParticleFreezePacket(posX, posY + 0.5D, posZ, d0 + 10.0D, d1, d2));
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
