package de.Whitedraco.switchbow.entity.arrow.upgrade;

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

public class EntityArrowSplitUpgrade extends EntityArrowBase implements IThrowableEntity {
   private double damagesetter = ArrowConfig.DamageArrowSplitUpgrade.getDoub();

   public EntityArrowSplitUpgrade(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowSplitUpgrade(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowSplitUpgrade(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowSplitUpgrade);
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
      double Umkreis = 2.5D;
      List Mobliste = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(posx - Umkreis, posy - 1.5D, posz - Umkreis, posx + Umkreis, posy + 1.5D, posz + Umkreis));
      if (Mobliste.size() > 0) {
         for(int a = 0; a < Mobliste.size(); ++a) {
            EntityLivingBase Mob = (EntityLivingBase)Mobliste.get(a);
            Mob.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 350, 2, true, true));
            DamageSource damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity);
            Mob.attackEntityFrom(damagesource, 4.5F);
         }
      }

      if (!this.world.isRemote) {
         double posXSpawn = posx - 2.0D;
         double posYSpawn = posy;
         double posZSpawn = posz - 2.0D;

         for(int x = 0; x < 29; ++x) {
            for(int i = 0; i < 5; ++i) {
               for(int j = 0; j < 5; ++j) {
                  this.Spawn(this.world, posXSpawn + (double)i, posYSpawn + 0.5D, posZSpawn + (double)j);
               }
            }
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
