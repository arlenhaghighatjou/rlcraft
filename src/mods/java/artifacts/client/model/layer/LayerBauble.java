package artifacts.client.model.layer;

import baubles.common.Config;
import javax.annotation.Nonnull;
import net.minecraft.client.model.ModelPlayer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;

public abstract class LayerBauble implements LayerRenderer<EntityPlayer> {
   protected RenderPlayer renderPlayer;
   protected ModelPlayer modelPlayer;
   protected boolean slim;

   public LayerBauble(RenderPlayer renderPlayer) {
      this(renderPlayer, false);
   }

   public LayerBauble(RenderPlayer renderPlayer, boolean slim) {
      this.renderPlayer = renderPlayer;
      this.modelPlayer = renderPlayer.getMainModel();
      this.slim = slim;
   }

   public final void doRenderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (Config.renderBaubles && player.getActivePotionEffect(MobEffects.INVISIBILITY) == null) {
         GlStateManager.enableLighting();
         GlStateManager.enableRescaleNormal();
         GlStateManager.pushMatrix();
         this.renderLayer(player, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
         GlStateManager.popMatrix();
      }
   }

   protected abstract void renderLayer(@Nonnull EntityPlayer var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8);

   public boolean shouldCombineTextures() {
      return false;
   }
}
