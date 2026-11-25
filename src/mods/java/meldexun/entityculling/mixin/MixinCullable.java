package meldexun.entityculling.mixin;

import meldexun.entityculling.util.ICullable;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({Entity.class, TileEntity.class})
public class MixinCullable implements ICullable {
   @Unique
   private final ICullable.CullInfo cullInfo = new ICullable.CullInfo();
   @Unique
   private final ICullable.CullInfo shadowCullInfo = new ICullable.CullInfo();
   @Unique
   private boolean culled;
   @Unique
   private boolean shadowCulled;
   @Unique
   private boolean canBeOcclusionCulled;

   public MixinCullable() {
   }

   @Unique
   public ICullable.CullInfo getCullInfo() {
      return this.cullInfo;
   }

   @Unique
   public ICullable.CullInfo getShadowCullInfo() {
      return this.shadowCullInfo;
   }

   @Unique
   public boolean isCulled() {
      return this.culled;
   }

   @Unique
   public void setCulled(boolean culled) {
      this.culled = culled;
   }

   @Unique
   public boolean isShadowCulled() {
      return this.shadowCulled;
   }

   @Unique
   public void setShadowCulled(boolean shadowCulled) {
      this.shadowCulled = shadowCulled;
   }

   @Unique
   public boolean canBeOcclusionCulled() {
      return this.canBeOcclusionCulled;
   }

   @Unique
   public void setCanBeOcclusionCulled(boolean canBeOcclusionCulled) {
      this.canBeOcclusionCulled = canBeOcclusionCulled;
   }
}
