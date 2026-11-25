package artifacts.client.model.layer;

import artifacts.common.init.ModItems;
import artifacts.common.util.RenderHelper;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;

public class LayerGloves extends LayerBauble {
   public final ResourceLocation feralClawsTextures;
   public final ResourceLocation powerGloveTextures;
   public final ResourceLocation mechanicalGloveTextures;
   public final ResourceLocation fireGauntletTextures;
   public final ResourceLocation fireGauntletOverlayTextures;
   public final ResourceLocation pocketPistonTextures;

   public LayerGloves(boolean smallArms, RenderPlayer renderPlayer) {
      super(renderPlayer, smallArms);
      this.feralClawsTextures = new ResourceLocation("artifacts", "textures/entity/layer/feral_claws_" + (smallArms ? "slim" : "normal") + ".png");
      this.powerGloveTextures = new ResourceLocation("artifacts", "textures/entity/layer/power_glove_" + (smallArms ? "slim" : "normal") + ".png");
      this.mechanicalGloveTextures = new ResourceLocation("artifacts", "textures/entity/layer/mechanical_glove_" + (smallArms ? "slim" : "normal") + ".png");
      this.fireGauntletTextures = new ResourceLocation("artifacts", "textures/entity/layer/fire_gauntlet_" + (smallArms ? "slim" : "normal") + ".png");
      this.fireGauntletOverlayTextures = new ResourceLocation("artifacts", "textures/entity/layer/fire_gauntlet_overlay_" + (smallArms ? "slim" : "normal") + ".png");
      this.pocketPistonTextures = new ResourceLocation("artifacts", "textures/entity/layer/pocket_piston_" + (smallArms ? "slim" : "normal") + ".png");
   }

   protected void renderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      float lastLightmapX = OpenGlHelper.lastBrightnessX;
      float lastLightmapY = OpenGlHelper.lastBrightnessY;
      int light = 15728880;
      int lightmapX = light % 65536;
      int lightmapY = light / 65536;
      this.renderArm(EnumHandSide.LEFT, player, false, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.renderArm(EnumHandSide.RIGHT, player, false, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)lightmapX, (float)lightmapY);
      GlStateManager.disableLighting();
      this.renderArm(EnumHandSide.LEFT, player, true, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      this.renderArm(EnumHandSide.RIGHT, player, true, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
      GlStateManager.enableLighting();
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastLightmapX, lastLightmapY);
   }

   private void renderArm(EnumHandSide hand, @Nonnull EntityPlayer player, boolean overlay, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (this.setTextures(player, hand, overlay)) {
         if (hand == EnumHandSide.LEFT) {
            this.modelPlayer.bipedLeftArm.showModel = true;
            this.modelPlayer.bipedLeftArmwear.showModel = true;
            this.modelPlayer.bipedRightArm.showModel = false;
            this.modelPlayer.bipedRightArmwear.showModel = false;
         } else {
            this.modelPlayer.bipedLeftArm.showModel = false;
            this.modelPlayer.bipedLeftArmwear.showModel = false;
            this.modelPlayer.bipedRightArm.showModel = true;
            this.modelPlayer.bipedRightArmwear.showModel = true;
         }

         this.modelPlayer.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
         if (hand == EnumHandSide.LEFT) {
            this.modelPlayer.bipedLeftArmwear.showModel = false;
            this.modelPlayer.bipedLeftArm.showModel = false;
         } else {
            this.modelPlayer.bipedRightArmwear.showModel = false;
            this.modelPlayer.bipedRightArm.showModel = false;
         }

      }
   }

   private boolean setTextures(EntityPlayer player, EnumHandSide hand, boolean overlay) {
      ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(BaubleType.RING.getValidSlots()[hand == EnumHandSide.LEFT ? 0 : 1]);
      if (!RenderHelper.shouldItemStackRender(player, stack)) {
         return false;
      } else {
         ResourceLocation textures = overlay ? this.getOverlayTextures(stack) : this.getTextures(stack);
         if (textures != null) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(textures);
            return true;
         } else {
            return false;
         }
      }
   }

   @Nullable
   private ResourceLocation getTextures(ItemStack stack) {
      if (stack.getItem() == ModItems.POWER_GLOVE) {
         return this.powerGloveTextures;
      } else if (stack.getItem() == ModItems.FERAL_CLAWS) {
         return this.feralClawsTextures;
      } else if (stack.getItem() == ModItems.MECHANICAL_GLOVE) {
         return this.mechanicalGloveTextures;
      } else if (stack.getItem() == ModItems.FIRE_GAUNTLET) {
         return this.fireGauntletTextures;
      } else {
         return stack.getItem() == ModItems.POCKET_PISTON ? this.pocketPistonTextures : null;
      }
   }

   @Nullable
   private ResourceLocation getOverlayTextures(ItemStack stack) {
      return stack.getItem() != ModItems.FIRE_GAUNTLET && stack.getItem() != ModItems.MAGMA_STONE ? null : this.fireGauntletOverlayTextures;
   }
}
