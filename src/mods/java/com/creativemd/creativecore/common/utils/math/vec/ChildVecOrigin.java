package com.creativemd.creativecore.common.utils.math.vec;

import javax.vecmath.Vector3d;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing.Axis;

public class ChildVecOrigin extends VecOrigin {
   public IVecOrigin parent;

   public ChildVecOrigin(IVecOrigin parent, Vector3d center) {
      super(center);
      this.parent = parent;
   }

   public void onlyRotateWithoutCenter(Vector3d vec) {
      super.onlyRotateWithoutCenter(vec);
      this.parent.onlyRotateWithoutCenter(vec);
   }

   public void transformPointToWorld(Vector3d vec) {
      super.transformPointToWorld(vec);
      this.parent.transformPointToWorld(vec);
   }

   public void transformPointToFakeWorld(Vector3d vec) {
      this.parent.transformPointToFakeWorld(vec);
      super.transformPointToFakeWorld(vec);
   }

   public void setupRenderingInternal(Entity entity, float partialTicks) {
      this.parent.setupRenderingInternal(entity, partialTicks);
      super.setupRenderingInternal(entity, partialTicks);
   }

   public double translationCombined(Axis axis) {
      return this.parent.translationCombined(axis) + super.translationCombined(axis);
   }

   public boolean hasChanged() {
      return super.hasChanged() || this.parent.hasChanged();
   }

   public IVecOrigin getParent() {
      return this.parent;
   }
}
