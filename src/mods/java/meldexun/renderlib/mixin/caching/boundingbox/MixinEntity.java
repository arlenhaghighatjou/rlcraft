package meldexun.renderlib.mixin.caching.boundingbox;

import meldexun.renderlib.api.IBoundingBoxCache;
import meldexun.renderlib.config.RenderLibConfig;
import meldexun.renderlib.util.MutableAABB;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({Entity.class})
public class MixinEntity implements IBoundingBoxCache {
   @Unique
   private final MutableAABB cachedBoundingBox = new MutableAABB();

   public MixinEntity() {
   }

   @Unique
   public void updateCachedBoundingBox(double partialTicks) {
      this.cachedBoundingBox.set(((Entity)this).getRenderBoundingBox());
      this.cachedBoundingBox.grow(0.5D);
      this.cachedBoundingBox.offset(-(((Entity)this).posX - ((Entity)this).lastTickPosX) * (1.0D - partialTicks), -(((Entity)this).posY - ((Entity)this).lastTickPosY) * (1.0D - partialTicks), -(((Entity)this).posZ - ((Entity)this).lastTickPosZ) * (1.0D - partialTicks));
      Vec3d v = RenderLibConfig.entityBoundingBoxGrowthListImpl.get((Entity)this);
      if (v != null) {
         this.cachedBoundingBox.grow(v.x, v.y, v.x, v.x, v.z, v.x);
      }

   }

   @Unique
   public MutableAABB getCachedBoundingBox() {
      return this.cachedBoundingBox;
   }
}
