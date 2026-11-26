package de.Whitedraco.switchbow.dispense;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DispenseArrows extends BehaviorProjectileDispense {
   private final DispenseArrows.ArrowFactory factory;

   public DispenseArrows(DispenseArrows.ArrowFactory factory) {
      this.factory = factory;
   }

   protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
      EntityArrow arrow = this.factory.create(worldIn, position.getX(), position.getY(), position.getZ());
      arrow.pickupStatus = PickupStatus.ALLOWED;
      return arrow;
   }

   public interface ArrowFactory {
      EntityArrow create(World worldIn, double x, double y, double z);
   }
}
