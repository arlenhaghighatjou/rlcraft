package goblinbob.mobends.standard.previewer;

import goblinbob.mobends.core.bender.BoneMetadata;
import goblinbob.mobends.core.bender.IPreviewer;
import goblinbob.mobends.core.client.event.DataUpdateHandler;
import goblinbob.mobends.standard.data.SpiderData;
import java.util.Map;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntitySpider;

public class SpiderPreviewer implements IPreviewer<SpiderData> {
   public SpiderPreviewer() {
   }

   public void prePreview(SpiderData data, String animationToPreview) {
      data.limbSwingAmount.override(Float.valueOf(0.0F));
      byte var4 = -1;
      switch(animationToPreview.hashCode()) {
      case 3273774:
         if (animationToPreview.equals("jump")) {
            var4 = 0;
         }
         break;
      case 3357649:
         if (animationToPreview.equals("move")) {
            var4 = 1;
         }
      }

      switch(var4) {
      case 0:
         float ticks = DataUpdateHandler.getTicks();
         float JUMP_DURATION = 10.0F;
         float WAIT_DURATION = 10.0F;
         float TOTAL_DURATION = 20.0F;
         float t = ticks % 20.0F;
         if (t <= 10.0F) {
            data.overrideOnGroundState(false);
            double yOffset = Math.sin((double)(t / 10.0F) * 3.141592653589793D) * 1.5D;
            GlStateManager.translate(0.0D, yOffset, 0.0D);
         } else {
            data.overrideOnGroundState(true);
         }

         data.limbSwingAmount.override(Float.valueOf(0.0F));
         data.overrideStillness(true);
         break;
      case 1:
         float ticks = DataUpdateHandler.getTicks();
         EntitySpider var10000 = (EntitySpider)data.getEntity();
         var10000.posZ += (double)(DataUpdateHandler.ticksPerFrame * 0.1F);
         ((EntitySpider)data.getEntity()).prevPosZ = ((EntitySpider)data.getEntity()).posZ;
         ((EntitySpider)data.getEntity()).noClip = true;
         data.limbSwing.override(Float.valueOf(ticks * 0.6F));
         data.overrideOnGroundState(true);
         data.limbSwingAmount.override(Float.valueOf(1.0F));
         data.overrideStillness(false);
         break;
      default:
         data.overrideOnGroundState(true);
         data.overrideStillness(true);
      }

   }

   public void postPreview(SpiderData data, String animationToPreview) {
   }

   public Map<String, BoneMetadata> getBoneMetadata() {
      return null;
   }
}
