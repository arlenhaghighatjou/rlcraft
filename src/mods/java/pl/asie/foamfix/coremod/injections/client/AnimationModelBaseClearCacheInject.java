package pl.asie.foamfix.coremod.injections.client;

import net.minecraft.entity.Entity;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;

public class AnimationModelBaseClearCacheInject {
   private VertexLighterFlat lighter;

   public AnimationModelBaseClearCacheInject() {
   }

   public void render(Entity entity, float limbSwing, float limbSwingSpeed, float timeAlive, float yawHead, float rotationPitch, float scale) {
      this.render_foamfix_old(entity, limbSwing, limbSwingSpeed, timeAlive, yawHead, rotationPitch, scale);
      this.lighter.resetBlockInfo();
   }

   public void render_foamfix_old(Entity entity, float limbSwing, float limbSwingSpeed, float timeAlive, float yawHead, float rotationPitch, float scale) {
   }
}
