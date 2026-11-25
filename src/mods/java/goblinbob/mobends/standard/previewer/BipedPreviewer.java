package goblinbob.mobends.standard.previewer;

import goblinbob.mobends.core.bender.BoneMetadata;
import goblinbob.mobends.core.bender.IPreviewer;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.core.math.vector.IVec3fRead;
import goblinbob.mobends.core.math.vector.Vec3fReadonly;
import goblinbob.mobends.standard.data.BipedEntityData;
import java.util.HashMap;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class BipedPreviewer<D extends BipedEntityData<?>> implements IPreviewer<D> {
   private static final Vec3fReadonly VIEWPORT_ANCHOR = new Vec3fReadonly(0.0F, 1.3F, 0.0F);
   private static final Map<String, BoneMetadata> BONE_METADATA = new HashMap<String, BoneMetadata>() {
      {
         this.put("head", new BoneMetadata(-4.0F, -8.0F, -4.0F, 4.0F, 0.0F, 4.0F));
         this.put("body", new BoneMetadata(-4.0F, -12.0F, -2.0F, 4.0F, 0.0F, 2.0F));
         this.put("leftArm", new BoneMetadata(-1.0F, -2.0F, -2.0F, 3.0F, 4.0F, 2.0F));
         this.put("rightArm", new BoneMetadata(-3.0F, -2.0F, -2.0F, 1.0F, 4.0F, 2.0F));
      }
   };

   public BipedPreviewer() {
   }

   public void prePreview(D data, String animationToPreview) {
      data.headYaw.override(Float.valueOf(0.0F));
      data.headPitch.override(Float.valueOf(0.0F));
      byte var4 = -1;
      switch(animationToPreview.hashCode()) {
      case 3273774:
         if (animationToPreview.equals("jump")) {
            var4 = 1;
         }
         break;
      case 3641801:
         if (animationToPreview.equals("walk")) {
            var4 = 0;
         }
      }

      switch(var4) {
      case 0:
         this.prepareForWalk(data);
         break;
      case 1:
         this.prepareForJump(data);
         break;
      default:
         this.prepareForDefault(data);
      }

   }

   protected void prepareForWalk(D data) {
      float ticks = DataUpdateHandler.getTicks();
      data.limbSwing.override(Float.valueOf(ticks * 0.6F));
      data.overrideOnGroundState(true);
      data.limbSwingAmount.override(Float.valueOf(1.0F));
      data.overrideStillness(false);
   }

   protected void prepareForJump(D data) {
      float ticks = DataUpdateHandler.getTicks();
      float JUMP_DURATION = 10.0F;
      float WAIT_DURATION = 10.0F;
      float TOTAL_DURATION = 20.0F;
      float t = ticks % 20.0F;
      if (t <= 10.0F) {
         data.overrideOnGroundState(false);
         double yOffset = Math.sin((double)(t / 10.0F) * 3.141592653589793D) * 0.8D;
         GlStateManager.translate(0.0D, yOffset, 0.0D);
      } else {
         data.overrideOnGroundState(true);
      }

      data.limbSwingAmount.override(Float.valueOf(0.0F));
      data.overrideStillness(true);
   }

   protected void prepareForDefault(D data) {
      data.overrideOnGroundState(true);
      data.limbSwingAmount.override(Float.valueOf(0.0F));
      data.overrideStillness(true);
   }

   public void postPreview(D data, String animationToPreview) {
   }

   public IVec3fRead getAnchorPoint() {
      return VIEWPORT_ANCHOR;
   }

   public Map<String, BoneMetadata> getBoneMetadata() {
      return BONE_METADATA;
   }
}
