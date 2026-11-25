package com.creativemd.creativecore.common.world;

import com.creativemd.creativecore.common.utils.math.vec.IVecOrigin;
import javax.vecmath.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public interface IOrientatedWorld {
   boolean hasParent();

   World getParent();

   World getRealWorld();

   IVecOrigin getOrigin();

   void setOrigin(Vector3d var1);

   Entity getParentEntity();

   default Entity getTopEntity() {
      World world = this.getParent();
      return world instanceof IOrientatedWorld ? ((IOrientatedWorld)world).getTopEntity() : this.getParentEntity();
   }
}
