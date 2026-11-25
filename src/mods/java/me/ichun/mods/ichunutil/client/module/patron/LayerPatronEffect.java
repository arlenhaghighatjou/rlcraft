package me.ichun.mods.ichunutil.client.module.patron;

import me.ichun.mods.ichunutil.client.model.ModelBee;
import me.ichun.mods.ichunutil.client.model.ModelSnout;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.core.util.EventCalendar;
import me.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import me.ichun.mods.ichunutil.common.module.patron.PatronInfo;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class LayerPatronEffect implements LayerRenderer<EntityPlayer> {
   public ModelSnout modelSnout = new ModelSnout();
   public ModelBee modelBee = new ModelBee();
   public RenderPlayer parentRenderer;
   public static final ResourceLocation texBee = new ResourceLocation("ichunutil", "textures/model/bee.png");

   public LayerPatronEffect(RenderPlayer render) {
      this.parentRenderer = render;
   }

   public void doRenderLayer(EntityPlayer player, float f, float f1, float renderTick, float f2, float f3, float f4, float f5) {
      PatronInfo info = PatronEffectRenderer.getPatronInfo(player);
      if (!player.isInvisible() && !player.isSpectator()) {
         if (EventCalendar.isAFDay() || info != null && info.showEffect && info.effectType == PatronEffectRenderer.EnumEffect.PIG_SNOUT.getId()) {
            this.parentRenderer.bindTexture(ResourceHelper.texPig);
            GlStateManager.pushMatrix();
            if (player.isSneaking()) {
               GlStateManager.translate(0.0F, 0.265F, 0.0F);
            }

            GlStateManager.rotate(EntityHelper.interpolateValues(player.prevRotationYawHead, player.rotationYawHead, renderTick) - EntityHelper.interpolateValues(player.prevRenderYawOffset, player.renderYawOffset, renderTick), 0.0F, 1.0F, 0.0F);
            if (player.getTicksElytraFlying() > 4) {
               GlStateManager.rotate((float)Math.toDegrees(-0.7853981852531433D), 1.0F, 0.0F, 0.0F);
            } else {
               GlStateManager.rotate(EntityHelper.interpolateValues(player.prevRotationPitch, player.rotationPitch, renderTick), 1.0F, 0.0F, 0.0F);
            }

            this.modelSnout.render(0.0625F);
            GlStateManager.popMatrix();
         }

         if (info != null && info.showEffect) {
            PatronEffectRenderer.EnumEffect effect = PatronEffectRenderer.EnumEffect.getById(info.effectType);
            switch(effect) {
            case BEE:
               this.parentRenderer.bindTexture(texBee);
               GlStateManager.pushMatrix();
               if (player.isSneaking()) {
                  GlStateManager.translate(0.0F, 0.265F, 0.0F);
               }

               float sinProg1 = (float)Math.sin(Math.toRadians((double)((float)player.ticksExisted / 30.0F * 360.0F)));
               float sinProg2 = (float)Math.sin(Math.toRadians((double)((float)player.ticksExisted / 20.0F * 360.0F)));
               float pitchOffset = (player.getTicksElytraFlying() > 4 ? 60.0F : Math.abs(EntityHelper.interpolateValues(player.prevRotationPitch, player.rotationPitch, renderTick))) / 90.0F;
               GlStateManager.rotate(((float)player.ticksExisted + renderTick) / 40.0F * 360.0F, 0.0F, 1.0F, 0.0F);
               GlStateManager.translate(0.4F + pitchOffset * 0.25F + sinProg1 * 0.05F, -0.25F + pitchOffset * 0.15F + sinProg2 * 0.05F, 0.0F);
               this.modelBee.render(0.0625F);
               GlStateManager.popMatrix();
            }
         }
      }

   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
