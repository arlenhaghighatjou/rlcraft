package org.orecruncher.dsurround.client.renderer;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;
import org.orecruncher.dsurround.capabilities.CapabilitySpeechData;
import org.orecruncher.dsurround.capabilities.speech.ISpeechData;
import org.orecruncher.dsurround.capabilities.speech.RenderContext;
import org.orecruncher.dsurround.client.handlers.EnvironStateHandler;
import org.orecruncher.lib.Color;
import org.orecruncher.lib.WorldUtils;
import org.orecruncher.lib.collections.ObjectArray;
import org.orecruncher.lib.gfx.OpenGlState;

@EventBusSubscriber(
   value = {Side.CLIENT},
   modid = "dsurround"
)
public class SpeechDataRenderer {
   private static final Color B_COLOR = Color.getColor(TextFormatting.BLACK);
   private static final float B_COLOR_ALPHA = 0.5F;
   private static final Color F_COLOR = Color.getColor(TextFormatting.GOLD);
   private static final float F_COLOR_ALPHA = 0.99F;
   private static final Color F_COLOR_DEPTH = Color.getColor(TextFormatting.GRAY);

   public SpeechDataRenderer() {
   }

   private static RenderManager getRenderManager() {
      return Minecraft.getMinecraft().getRenderManager();
   }

   private static double interpX() {
      return getRenderManager().viewerPosX;
   }

   private static double interpY() {
      return getRenderManager().viewerPosY;
   }

   private static double interpZ() {
      return getRenderManager().viewerPosZ;
   }

   private static boolean isThirdPersonView() {
      GameSettings settings = getRenderManager().options;
      return settings != null && settings.thirdPersonView == 2;
   }

   private static void doRender(@Nonnull Entity entity, @Nonnull ISpeechData data, float partialTicks) {
      RenderContext ctx = data.getRenderContext();
      if (ctx != null && ctx.numberOfMessages != 0) {
         FontRenderer font = Minecraft.getMinecraft().fontRenderer;
         float pitch = getRenderManager().playerViewX * (float)(isThirdPersonView() ? -1 : 1);
         float yaw = -getRenderManager().playerViewY;
         float locX = (float)(entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks - interpX());
         float locY = (float)(entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks - interpY());
         float locZ = (float)(entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks - interpZ());
         OpenGlState glState = OpenGlState.push();
         GlStateManager.translate(locX, locY + entity.height + 0.25F, locZ);
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(yaw, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
         float scale = -1.0F;
         GlStateManager.scale(-0.015D, -0.014999999664723873D, -0.015D);
         GlStateManager.disableTexture2D();
         GlStateManager.depthMask(false);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         GlStateManager.enableAlpha();
         float red = B_COLOR.red;
         float green = B_COLOR.green;
         float blue = B_COLOR.blue;
         float alpha = 0.5F;
         BufferBuilder buffer = Tessellator.getInstance().getBuffer();
         buffer.begin(5, DefaultVertexFormats.POSITION_COLOR);
         buffer.pos(ctx.left, ctx.top, 0.0D).color(red, green, blue, 0.5F).endVertex();
         buffer.pos(ctx.left, ctx.bottom, 0.0D).color(red, green, blue, 0.5F).endVertex();
         buffer.pos(ctx.right, ctx.top, 0.0D).color(red, green, blue, 0.5F).endVertex();
         buffer.pos(ctx.right, ctx.bottom, 0.0D).color(red, green, blue, 0.5F).endVertex();
         Tessellator.getInstance().draw();
         GlStateManager.enableTexture2D();
         GlStateManager.depthMask(true);
         GlStateManager.translate(0.0F, 0.0F, -0.05F);
         ObjectArray<String> text = data.getText();
         int lines = ctx.numberOfMessages;

         for(int t = 0; t < ctx.numberOfMessages; ++t) {
            String str = (String)text.get(t);
            int offset = -lines * 9;
            int margin = -font.getStringWidth(str) / 2;
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            font.drawString(str, margin, offset, F_COLOR_DEPTH.rgbWithAlpha(0.99F));
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            font.drawString(str, margin, offset, F_COLOR.rgbWithAlpha(0.99F));
            --lines;
         }

         OpenGlState.pop(glState);
      }
   }

   private static boolean canBeSeen(@Nonnull EntityPlayer player, @Nonnull Entity subject) {
      return subject.isInvisibleToPlayer(player) ? false : player.canEntityBeSeen(subject);
   }

   @SubscribeEvent
   public static void onRenderWorldLast(@Nonnull RenderWorldLastEvent event) {
      EntityPlayer player = EnvironStateHandler.EnvironState.getPlayer();
      if (player != null) {
         int range = 256;

         for(Entity e : WorldUtils.gatherEntitiesInView(player, 256, event.getPartialTicks())) {
            ISpeechData data = CapabilitySpeechData.getCapability(e);
            if (data != null && canBeSeen(player, e)) {
               doRender(e, data, event.getPartialTicks());
            }
         }
      }

   }
}
