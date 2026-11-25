package artifacts.client.model.layer;

import artifacts.client.model.ModelCloak;
import artifacts.common.init.ModItems;
import artifacts.common.util.RenderHelper;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LayerCloak extends LayerBauble {
   private static final ResourceLocation CLOAK_NORMAL = new ResourceLocation("artifacts", "textures/entity/layer/star_cloak.png");
   private static final ResourceLocation CLOAK_OVERLAY = new ResourceLocation("artifacts", "textures/entity/layer/star_cloak_overlay.png");
   private static final ModelCloak CLOAK_MODEL = new ModelCloak();

   public LayerCloak(RenderPlayer renderPlayer) {
      super(renderPlayer);
   }

   protected void renderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.renderChest(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
   }

   private void renderChest(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (BaublesApi.isBaubleEquipped(player, ModItems.STAR_CLOAK) != -1 && RenderHelper.shouldRenderInSlot(player, EntityEquipmentSlot.CHEST)) {
         ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(BaubleType.BODY.getValidSlots()[0]);
         if (stack.getItem() == ModItems.STAR_CLOAK && RenderHelper.shouldItemStackRender(player, stack)) {
            if (player.isSneaking()) {
               GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            boolean hoodUp = RenderHelper.shouldRenderInSlot(player, EntityEquipmentSlot.HEAD) && (BaublesApi.isBaubleEquipped(player, ModItems.DRINKING_HAT) == -1 || !RenderHelper.shouldItemStackRender(player, BaublesApi.getBaublesHandler(player).getStackInSlot(BaubleType.HEAD.getValidSlots()[0])));
            Minecraft.getMinecraft().getTextureManager().bindTexture(CLOAK_NORMAL);
            this.renderBody(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, hoodUp);
            this.renderHead(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, hoodUp);
            float lastLightmapX = OpenGlHelper.lastBrightnessX;
            float lastLightmapY = OpenGlHelper.lastBrightnessY;
            int light = 15728880;
            int lightmapX = light % 65536;
            int lightmapY = light / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)lightmapX, (float)lightmapY);
            Minecraft.getMinecraft().getTextureManager().bindTexture(CLOAK_OVERLAY);
            this.renderBody(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, hoodUp);
            this.renderHead(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, hoodUp);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastLightmapX, lastLightmapY);
         }
      }
   }

   private void renderBody(EntityPlayer player, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, boolean hoodUp) {
      GlStateManager.pushMatrix();
      this.modelPlayer.bipedBody.postRender(scale);
      CLOAK_MODEL.renderCloak(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, hoodUp);
      GlStateManager.popMatrix();
   }

   private void renderHead(EntityPlayer player, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, boolean hoodUp) {
      GlStateManager.pushMatrix();
      this.modelPlayer.bipedHead.postRender(scale);
      CLOAK_MODEL.renderHood(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, hoodUp);
      GlStateManager.popMatrix();
   }
}
