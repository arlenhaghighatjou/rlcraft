package de.mennomax.astikorcarts.entity.ai;

import de.mennomax.astikorcarts.capabilities.IPull;
import de.mennomax.astikorcarts.capabilities.PullProvider;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.EnumFacing;

public class EntityAIPullCart extends EntityAIBase {
   private final EntityLiving living;

   public EntityAIPullCart(EntityLiving livingIn) {
      this.living = livingIn;
      this.setMutexBits(3);
   }

   public boolean shouldExecute() {
      if (this.living.hasCapability(PullProvider.PULL, (EnumFacing)null)) {
         return ((IPull)this.living.getCapability(PullProvider.PULL, (EnumFacing)null)).getDrawn() != null;
      } else {
         return false;
      }
   }
}
