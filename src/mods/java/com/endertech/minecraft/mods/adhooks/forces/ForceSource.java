package com.endertech.minecraft.mods.adhooks.forces;

import com.endertech.common.IntBounds;
import com.endertech.minecraft.forge.math.ForgeMath;
import com.endertech.minecraft.forge.math.Vect3d;
import net.minecraft.entity.Entity;

public class ForceSource extends ForceNode {
   protected static final IntBounds SIN_DEGREES = IntBounds.from(Integer.valueOf(0), Integer.valueOf(180));
   protected final IntBounds ticksToLive;
   protected final Vect3d vect;
   protected int ticksExisted;

   public ForceSource(Entity entity, Vect3d vect, int ticksToLive) {
      super(entity);
      this.vect = vect;
      this.ticksToLive = IntBounds.from(Integer.valueOf(0), Integer.valueOf(ticksToLive));
   }

   public void tick() {
      ++this.ticksExisted;
   }

   public Vect3d getVect() {
      IntBounds angleBounds = IntBounds.from(Integer.valueOf(0), Integer.valueOf(180));
      int degrees = ((Integer)this.ticksToLive.approxTo(angleBounds).straight(Integer.valueOf(this.ticksExisted))).intValue();
      double scale = (double)ForgeMath.sin((float)degrees);
      return this.vect.scale(scale);
   }

   public boolean exist() {
      return this.entity.isEntityAlive() && this.ticksToLive.encloses(Integer.valueOf(this.ticksExisted)) && this.vect.notZero();
   }
}
