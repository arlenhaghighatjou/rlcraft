package de.Whitedraco.switchbow.entity.arrow;

import de.Whitedraco.switchbow.ItemInit;
import de.Whitedraco.switchbow.Config.ArrowConfig;
import de.Whitedraco.switchbow.entity.EntityArrowBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityArrowDiamond extends EntityArrowBase implements IThrowableEntity {
   private double damagesetter = ArrowConfig.DamageArrowDiamond.getDoub();

   public EntityArrowDiamond(World worldIn) {
      super(worldIn);
      this.damage = this.damagesetter;
   }

   public EntityArrowDiamond(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
      this.damage = this.damagesetter;
   }

   public EntityArrowDiamond(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter);
      this.damage = this.damagesetter;
   }

   protected void arrowHitGround(BlockPos blockpos) {
   }

   protected ItemStack getArrowStack() {
      return new ItemStack(ItemInit.ArrowDiamond);
   }

   public Entity getThrower() {
      return this.shootingEntity;
   }

   public void setThrower(Entity entity) {
      this.shootingEntity = entity;
   }
}
