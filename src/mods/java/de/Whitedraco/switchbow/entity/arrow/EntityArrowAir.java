package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowAir extends EntityArrowBase implements IThrowableEntity {
   private double damagesetter = ArrowConfig.DamageArrowAir.getDoub();

   public EntityArrowAir(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowAir(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowAir(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowAir);
   }

   protected void arrowHit(EntityLivingBase living) {
      if (!living.world.isRemote) {
         living.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 80, 1, true, true));
      }

   }

   protected void arrowHitGround(BlockPos blockpos) {
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
