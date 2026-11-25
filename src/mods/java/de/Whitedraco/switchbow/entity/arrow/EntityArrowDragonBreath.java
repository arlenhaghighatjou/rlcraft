package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowDragonBreath extends EntityArrowBase implements IThrowableEntity {
   private double damagesetter = ArrowConfig.DamageArrowDragonBreath.getDoub();

   public EntityArrowDragonBreath(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowDragonBreath(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowDragonBreath(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowDragonBreath);
   }

   protected void arrowHit(EntityLivingBase living) {
      if (!living.world.isRemote) {
         EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(living.world, living.posX, living.posY, living.posZ);
         entityareaeffectcloud.setParticle(EnumParticleTypes.DRAGON_BREATH);
         entityareaeffectcloud.setRadius(1.75F);
         entityareaeffectcloud.setDuration(250);
         entityareaeffectcloud.setRadiusPerTick((1.7F - entityareaeffectcloud.getRadius()) / (float)entityareaeffectcloud.getDuration());
         entityareaeffectcloud.addEffect(new PotionEffect(MobEffects.INSTANT_DAMAGE, 1, 1));
         this.world.spawnEntity(entityareaeffectcloud);
      }

   }

   protected void arrowHitGround(BlockPos blockpos) {
      if (!this.world.isRemote) {
         EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.world, this.posX, this.posY, this.posZ);
         entityareaeffectcloud.setParticle(EnumParticleTypes.DRAGON_BREATH);
         entityareaeffectcloud.setRadius(1.75F);
         entityareaeffectcloud.setDuration(250);
         entityareaeffectcloud.setRadiusPerTick((1.7F - entityareaeffectcloud.getRadius()) / (float)entityareaeffectcloud.getDuration());
         entityareaeffectcloud.addEffect(new PotionEffect(MobEffects.INSTANT_DAMAGE, 1, 1));
         this.world.spawnEntity(entityareaeffectcloud);
         this.setDead();
      }

   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
