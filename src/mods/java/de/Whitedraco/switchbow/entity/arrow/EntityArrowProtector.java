package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import de.Whitedraco.switchbow.entity.living.EntityLittleIronGolem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowProtector extends EntityArrowBase implements IThrowableEntity {
   private double damagesetter = ArrowConfig.DamageArrowProtector.getDoub();

   public EntityArrowProtector(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowProtector(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowProtector(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowProtector);
   }

   protected void arrowHit(EntityLivingBase living) {
      if (!living.world.isRemote) {
         EntityLittleIronGolem golem = new EntityLittleIronGolem(living.world);
         golem.setPlayerCreated(true);
         golem.setPosition(living.posX, living.posY, living.posZ);
         golem.setAttackTarget(living);
         living.world.spawnEntity(golem);
         living.setLastAttackedEntity(golem);
         this.setDead();
      }

   }

   protected void arrowHitGround(BlockPos blockpos) {
      if (!this.world.isRemote) {
         ;
      }

   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
