package com.oblivioussp.spartanweaponry.entity.projectile;

import com.oblivioussp.spartanweaponry.init.ItemRegistrySW;
import com.oblivioussp.spartanweaponry.item.ItemArrowSW;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityArrowDiamond extends EntityArrowBase {
   public EntityArrowDiamond(World worldIn) {
      super(worldIn);
   }

   public EntityArrowDiamond(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   public EntityArrowDiamond(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter, ConfigHandler.baseDamageArrowDiamond);
   }

   public EntityArrowBase.ArrowType getArrowType() {
      return EntityArrowBase.ArrowType.DIAMOND;
   }

   protected ItemArrowSW getNormalArrowItem() {
      return ItemRegistrySW.arrowDiamond;
   }

   protected ItemArrowSW getTippedArrowItem() {
      return ItemRegistrySW.arrowTippedDiamond;
   }
}
