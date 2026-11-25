package crafttweaker.mc1120.dispenser;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.dispenser.IBlockSource;
import crafttweaker.api.dispenser.IDispenserBehavior;
import crafttweaker.api.entity.IEntity;
import crafttweaker.api.entity.IEntityDefinition;
import crafttweaker.api.entity.IProjectile;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.mc1120.util.MCPosition3f;
import net.minecraft.util.EnumFacing;

public class ShootingProjectileBehavior implements IDispenserBehavior {
   private final IEntityDefinition projectile;
   private final float inaccuracy;
   private final float velocity;

   public ShootingProjectileBehavior(IEntityDefinition projectile, float inaccuracy, float velocity) {
      this.projectile = projectile;
      this.inaccuracy = inaccuracy;
      this.velocity = velocity;
   }

   public IItemStack apply(IBlockSource source, IItemStack stack) {
      stack.mutable().shrink(1);
      EnumFacing facing = CraftTweakerMC.getFacing(source.getFacing());
      float x = (float)(source.getX() + (double)(0.7F * (float)facing.getFrontOffsetX()));
      float y = (float)(source.getY() + (double)(0.7F * (float)facing.getFrontOffsetY()));
      float z = (float)(source.getZ() + (double)(0.7F * (float)facing.getFrontOffsetZ()));
      IEntity entity = this.projectile.createEntity(source.getWorld());
      entity.setPosition3f(new MCPosition3f(x, y, z));
      if (entity instanceof IProjectile) {
         ((IProjectile)entity).shoot((double)facing.getFrontOffsetX(), (double)facing.getFrontOffsetY() + 0.1D, (double)facing.getFrontOffsetZ(), this.velocity, this.inaccuracy);
      } else {
         CraftTweakerAPI.logError(this.projectile.getId() + " is not a projectile.");
      }

      source.getWorld().spawnEntity(entity);
      return stack;
   }
}
