package meldexun.renderlib.mixin.caching.boundingbox;

import java.util.Random;
import meldexun.randomutil.FastRandom;
import meldexun.renderlib.RenderLib;
import meldexun.renderlib.api.IBoundingBoxCache;
import meldexun.renderlib.config.RenderLibConfig;
import meldexun.renderlib.integration.ValkyrienSkies;
import meldexun.renderlib.util.MutableAABB;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({TileEntity.class})
public class MixinTileEntity implements IBoundingBoxCache {
   @Unique
   private static final Random RAND = new FastRandom();
   @Unique
   private final MutableAABB cachedBoundingBox = new MutableAABB();
   @Unique
   private boolean initialized;

   public MixinTileEntity() {
   }

   @Unique
   public void updateCachedBoundingBox(double partialTicks) {
      if (!this.initialized || !RenderLibConfig.tileEntityCachedBoundingBoxEnabled || ((Boolean)RenderLibConfig.tileEntityCachedBoundingBoxBlacklistImpl.get((TileEntity)this)).booleanValue() || RenderLibConfig.tileEntityCachedBoundingBoxUpdateInterval == 1 || RAND.nextInt(RenderLibConfig.tileEntityCachedBoundingBoxUpdateInterval) == 0 || RenderLib.isValkyrienSkiesInstalled && ValkyrienSkies.isOnShip((TileEntity)this)) {
         this.cachedBoundingBox.set(RenderLib.isValkyrienSkiesInstalled ? ValkyrienSkies.getAABB((TileEntity)this) : ((TileEntity)this).getRenderBoundingBox());
         Vec3d v = RenderLibConfig.tileEntityBoundingBoxGrowthListImpl.get((TileEntity)this);
         if (v != null) {
            this.cachedBoundingBox.grow(v.x, v.y, v.x, v.x, v.z, v.x);
         }

         this.initialized = true;
      }

   }

   @Unique
   public MutableAABB getCachedBoundingBox() {
      return this.cachedBoundingBox;
   }
}
