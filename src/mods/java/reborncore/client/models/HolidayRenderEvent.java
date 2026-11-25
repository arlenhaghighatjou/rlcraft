package reborncore.client.models;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import reborncore.common.RebornCoreConfig;
import reborncore.common.util.CalenderUtils;

public class HolidayRenderEvent {
   static ModelSantaHat santaHat = new ModelSantaHat();
   private static final ResourceLocation TEXTURE = new ResourceLocation("reborncore", "textures/models/santa_hat.png");
   static List<RenderPlayer> renderPlayerList = new ArrayList();

   public HolidayRenderEvent() {
   }

   @SubscribeEvent
   public static void holidayRender(Pre event) {
      if (CalenderUtils.christmas && RebornCoreConfig.easterEggs) {
         Render<?> render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(event.getEntityPlayer());
         if (render instanceof RenderPlayer) {
            RenderPlayer renderPlayer = (RenderPlayer)render;
            if (!renderPlayerList.contains(renderPlayer)) {
               renderPlayer.addLayer(new HolidayRenderEvent.LayerRender());
               renderPlayerList.add(renderPlayer);
            }
         }

      }
   }

   private static class LayerRender implements LayerRenderer<AbstractClientPlayer> {
      private LayerRender() {
      }

      public void doRenderLayer(AbstractClientPlayer abstractClientPlayer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
         float yaw = abstractClientPlayer.prevRotationYaw + (abstractClientPlayer.rotationYaw - abstractClientPlayer.prevRotationYaw) * partialTicks - (abstractClientPlayer.prevRenderYawOffset + (abstractClientPlayer.renderYawOffset - abstractClientPlayer.prevRenderYawOffset) * partialTicks);
         float pitch = abstractClientPlayer.prevRotationPitch + (abstractClientPlayer.rotationPitch - abstractClientPlayer.prevRotationPitch) * partialTicks;
         Minecraft.getMinecraft().renderEngine.bindTexture(HolidayRenderEvent.TEXTURE);
         GlStateManager.pushMatrix();
         GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
         if (abstractClientPlayer.isSneaking()) {
            GlStateManager.translate(0.0F, 0.26F, 0.0F);
         }

         float scale2 = 1.0F;
         GlStateManager.scale(scale2, scale2, scale2);
         HolidayRenderEvent.santaHat.render(0.0625F);
         GlStateManager.popMatrix();
      }

      public boolean shouldCombineTextures() {
         return true;
      }
   }
}
