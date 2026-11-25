package vazkii.quark.experimental.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.client.ClientReflectiveAccessor;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

public class BetterNausea extends Feature {
   private float timeInPortal;
   private float prevTimeInPortal;
   private boolean viewBobbing;
   protected static final ResourceLocation VIGNETTE_TEX_PATH = new ResourceLocation("textures/misc/vignette.png");
   private static final float TIME_CONST = 0.07853982F;

   public BetterNausea() {
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void renderTick(RenderTickEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      EntityPlayerSP player = mc.player;
      if (player != null && player.isPotionActive(MobEffects.NAUSEA)) {
         if (event.phase == Phase.START) {
            this.timeInPortal = player.timeInPortal;
            this.prevTimeInPortal = player.prevTimeInPortal;
            this.viewBobbing = mc.gameSettings.viewBobbing;
            player.timeInPortal = 0.0F;
            player.prevTimeInPortal = 0.0F;
            mc.gameSettings.viewBobbing = true;
         } else {
            player.timeInPortal = this.timeInPortal;
            player.prevTimeInPortal = this.prevTimeInPortal;
            mc.gameSettings.viewBobbing = this.viewBobbing;
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void renderTick(Pre event) {
      Minecraft mc = Minecraft.getMinecraft();
      EntityPlayerSP player = mc.player;
      if (player != null && player.isPotionActive(MobEffects.NAUSEA) && event.getType() == ElementType.ALL) {
         float time = (float)ClientReflectiveAccessor.getUpdateCount(mc.entityRenderer) + event.getPartialTicks();
         float sinAmount = MathHelper.sin(time * 1.5F * 0.07853982F) * 0.0625F;
         ScaledResolution res = event.getResolution();
         mc.entityRenderer.setupOverlayRendering();
         GlStateManager.enableBlend();
         GlStateManager.disableDepth();
         GlStateManager.depthMask(false);
         GlStateManager.tryBlendFuncSeparate(SourceFactor.ZERO, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
         GlStateManager.color(0.9375F + sinAmount, 0.9375F + sinAmount, 0.9375F + sinAmount);
         int w = res.getScaledWidth();
         int h = res.getScaledHeight();
         mc.getTextureManager().bindTexture(VIGNETTE_TEX_PATH);

         for(int i = 0; i < 2; ++i) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(0.0D, (double)h, -90.0D).tex(0.0D, 1.0D).endVertex();
            bufferbuilder.pos((double)w, (double)h, -90.0D).tex(1.0D, 1.0D).endVertex();
            bufferbuilder.pos((double)w, 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
            tessellator.draw();
         }

         GlStateManager.depthMask(true);
         GlStateManager.enableDepth();
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
      }

   }

   @SideOnly(Side.CLIENT)
   public static void renderNausea(EntityRenderer renderer, float partialTicks) {
      if (ModuleLoader.isFeatureEnabled(BetterNausea.class)) {
         Minecraft mc = Minecraft.getMinecraft();
         EntityPlayer player = mc.player;
         if (player != null) {
            PotionEffect nausea = player.getActivePotionEffect(MobEffects.NAUSEA);
            if (nausea != null) {
               float time = (float)ClientReflectiveAccessor.getUpdateCount(renderer) + partialTicks;
               float sinAmount = MathHelper.sin(time * 0.07853982F) / 2.0F;
               float yaw = player.cameraYaw;
               float prevYaw = player.prevCameraYaw;
               double distX = player.posX - player.prevPosX;
               double distY = player.posY - player.prevPosY;
               double distZ = player.posZ - player.prevPosZ;
               float dist = MathHelper.sqrt(distX * distX + distY * distY + distZ * distZ);
               dist = Math.min(0.15F, Math.max(0.1F, dist));
               player.cameraYaw += dist * sinAmount;
               player.prevCameraYaw += dist * sinAmount;
               ClientReflectiveAccessor.applyBobbing(renderer, partialTicks);
               player.cameraYaw = yaw;
               player.prevCameraYaw = prevYaw;
            }
         }

      }
   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }
}
