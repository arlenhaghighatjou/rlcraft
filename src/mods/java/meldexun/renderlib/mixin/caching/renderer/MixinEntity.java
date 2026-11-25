package meldexun.renderlib.mixin.caching.renderer;

import javax.annotation.Nullable;
import meldexun.renderlib.api.IEntityRendererCache;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({Entity.class})
public class MixinEntity implements IEntityRendererCache {
   @Unique
   private Render<Entity> renderer;
   @Unique
   private boolean rendererInitialized;

   public MixinEntity() {
   }

   @Unique
   @Nullable
   public <T extends Entity> Render<T> getRenderer() {
      if (!this.rendererInitialized) {
         if (!((Entity)this instanceof AbstractClientPlayer) || ((AbstractClientPlayer)((Entity)this)).playerInfo != null && ((AbstractClientPlayer)((Entity)this)).playerInfo.skinType != null) {
            this.rendererInitialized = true;
         }

         this.renderer = this.loadRenderer((Entity)this);
      }

      return this.renderer;
   }
}
