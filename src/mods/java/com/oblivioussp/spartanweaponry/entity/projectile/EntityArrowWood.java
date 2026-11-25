package com.oblivioussp.spartanweaponry.entity.projectile;

import com.oblivioussp.spartanweaponry.init.ItemRegistrySW;
import com.oblivioussp.spartanweaponry.item.ItemArrowSW;
import com.oblivioussp.spartanweaponry.util.ConfigHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class EntityArrowWood extends EntityArrowBase {
   public EntityArrowWood(World worldIn) {
      super(worldIn);
   }

   public EntityArrowWood(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   public EntityArrowWood(World worldIn, EntityLivingBase shooter) {
      super(worldIn, shooter, ConfigHandler.baseDamageArrowWood);
   }

   public EntityArrowBase.ArrowType getArrowType() {
      return EntityArrowBase.ArrowType.WOOD;
   }

   protected ItemArrowSW getNormalArrowItem() {
      return ItemRegistrySW.arrowWood;
   }

   protected ItemArrowSW getTippedArrowItem() {
      return ItemRegistrySW.arrowTippedWood;
   }
}
