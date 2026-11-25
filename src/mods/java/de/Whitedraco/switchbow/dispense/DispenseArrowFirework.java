package de.Whitedraco.switchbow.dispense;

import de.Whitedraco.switchbow.entity.arrow.EntityArrowFirework;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DispenseArrowFirework extends BehaviorProjectileDispense {
   public DispenseArrowFirework() {
   }

   protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
      EntityArrowFirework arrow = new EntityArrowFirework(worldIn, position.getX(), position.getY(), position.getZ(), stackIn);
      arrow.pickupStatus = PickupStatus.ALLOWED;
      return arrow;
   }
}
