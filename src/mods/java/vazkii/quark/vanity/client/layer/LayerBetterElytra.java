package vazkii.quark.vanity.client.layer;

import java.awt.Color;
import javax.annotation.Nonnull;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelElytra;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.misc.feature.ColorRunes;

@SideOnly(Side.CLIENT)
public class LayerBetterElytra implements LayerRenderer<AbstractClientPlayer> {
   private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");
   private static final ResourceLocation TEXTURE_ELYTRA_DYED = new ResourceLocation("quark", "textures/misc/elytra_dyed.png");
   private final RenderPlayer renderPlayer;
   private final ModelElytra modelElytra = new ModelElytra();

   public LayerBetterElytra(RenderPlayer renderPlayerIn) {
      this.renderPlayer = renderPlayerIn;
   }

   public void doRenderLayer(@Nonnull AbstractClientPlayer living, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      ItemStack itemstack = living.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
      if (!itemstack.isEmpty() && itemstack.getItem() == Items.ELYTRA) {
         int colorIndex = ItemNBTHelper.getInt(itemstack, "quark:elytraDye", -1);
         boolean dyed = colorIndex != -1 && colorIndex != 15;
         if (!dyed) {
            GlStateManager.color(1.0F, 1.0F, 1.0F);
         } else {
            Color color = new Color(ItemDye.DYE_COLORS[colorIndex]);
            float r = (float)color.getRed() / 255.0F;
            float g = (float)color.getGreen() / 255.0F;
            float b = (float)color.getBlue() / 255.0F;
            GlStateManager.color(r, g, b);
         }

         if (living.isPlayerInfoSet() && living.getLocationElytra() != null) {
            this.renderPlayer.bindTexture(living.getLocationElytra());
         } else if (living.hasPlayerInfo() && living.getLocationCape() != null && living.isWearing(EnumPlayerModelParts.CAPE)) {
            this.renderPlayer.bindTexture(living.getLocationCape());
         } else if (dyed) {
            this.renderPlayer.bindTexture(TEXTURE_ELYTRA_DYED);
         } else {
            this.renderPlayer.bindTexture(TEXTURE_ELYTRA);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, 0.0F, 0.125F);
         this.modelElytra.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, living);
         this.modelElytra.render(living, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
         if (itemstack.isItemEnchanted()) {
            ColorRunes.setTargetStack(itemstack);
            LayerArmorBase.renderEnchantedGlint(this.renderPlayer, living, this.modelElytra, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }

   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
