package paulevs.betternether.entities;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityTracker;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class NetherCreature extends EntityLiving {
   public NetherCreature(World worldIn) {
      super(worldIn);
   }

   private void dataChanged() {
      if (!this.world.isRemote) {
         EntityTracker var1 = ((WorldServer)this.world).getEntityTracker();
      }

   }

   public void entitySpawned() {
      this.dataChanged();
   }
}
