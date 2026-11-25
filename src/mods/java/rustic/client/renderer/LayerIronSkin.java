package rustic.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPlayer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.potions.PotionsRustic;

@SideOnly(Side.CLIENT)
public class LayerIronSkin<T extends ModelBase> implements LayerRenderer<EntityLivingBase> {
   private RenderLivingBase renderer;
   private T model;

   public LayerIronSkin(RenderLivingBase renderer, T model) {
      this.renderer = renderer;
      this.model = model;
   }

   public boolean shouldCombineTextures() {
      return false;
   }

   public void doRenderLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (entityLivingBaseIn.isPotionActive(PotionsRustic.IRON_SKIN_POTION) && !entityLivingBaseIn.isInvisible()) {
         int amplifier = entityLivingBaseIn.getActivePotionEffect(PotionsRustic.IRON_SKIN_POTION).getAmplifier();
         GlStateManager.enableBlend();
         GlStateManager.disableLighting();
         this.renderer.bindTexture(new ResourceLocation("textures/blocks/iron_block.png"));
         GlStateManager.matrixMode(5890);
         GlStateManager.color(0.9F, 0.9F, 0.9F, amplifier < 5 ? 0.75F + (float)amplifier * 0.05F : 0.9F);
         GlStateManager.scale((float)(this.model.textureWidth / 16), (float)(this.model.textureHeight / 16), 0.0F);
         GlStateManager.matrixMode(5888);
         float renderScale = 1.02F;
         GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
         GlStateManager.scale(1.001D * (double)renderScale, 0.999D * (double)renderScale, 0.999D * (double)renderScale);
         this.model.setModelAttributes(this.renderer.getMainModel());
         this.model.setLivingAnimations(entityLivingBaseIn, limbSwing, limbSwingAmount, partialTicks);
         Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
         if (this.model instanceof ModelPlayer) {
            ModelPlayer modelPlayer = (ModelPlayer)this.model;
            modelPlayer.bipedBody.isHidden = true;
            modelPlayer.bipedHead.isHidden = true;
            modelPlayer.bipedLeftArm.isHidden = true;
            modelPlayer.bipedRightArm.isHidden = true;
            modelPlayer.bipedLeftLeg.isHidden = true;
            modelPlayer.bipedRightLeg.isHidden = true;
            modelPlayer.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale / renderScale);
            modelPlayer.bipedBody.isHidden = false;
            modelPlayer.bipedHead.isHidden = false;
            modelPlayer.bipedLeftArm.isHidden = false;
            modelPlayer.bipedRightArm.isHidden = false;
            modelPlayer.bipedLeftLeg.isHidden = false;
            modelPlayer.bipedRightLeg.isHidden = false;
         } else {
            this.model.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale / renderScale);
         }

         Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
         GlStateManager.matrixMode(5890);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.loadIdentity();
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.enableLighting();
         GlStateManager.disableBlend();
      }

   }
}
