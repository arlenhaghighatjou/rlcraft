package de.Whitedraco.switchbow.dispense;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityArrow.PickupStatus;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DispenseArrows extends BehaviorProjectileDispense {
   Class<? extends Entity> entityClass = EntityArrow.class;

   public DispenseArrows(Class<? extends Entity> entityClass) {
      this.entityClass = entityClass;
   }

   protected IProjectile getProjectileEntity(World worldIn, IPosition position, ItemStack stackIn) {
      try {
         Constructor ctor = this.entityClass.getDeclaredConstructor(World.class, Double.TYPE, Double.TYPE, Double.TYPE);
         ctor.setAccessible(true);
         EntityArrow arrow = (EntityArrow)ctor.newInstance(worldIn, position.getX(), position.getY(), position.getZ());
         arrow.pickupStatus = PickupStatus.ALLOWED;
         return arrow;
      } catch (NoSuchMethodException var6) {
         var6.printStackTrace();
      } catch (SecurityException var7) {
         var7.printStackTrace();
      } catch (InstantiationException var8) {
         var8.printStackTrace();
      } catch (IllegalAccessException var9) {
         var9.printStackTrace();
      } catch (IllegalArgumentException var10) {
         var10.printStackTrace();
      } catch (InvocationTargetException var11) {
         var11.printStackTrace();
      }

      return null;
   }
}
