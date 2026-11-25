package goblinbob.mobends.standard.client.renderer.entity.layers;

import goblinbob.mobends.core.util.BenderHelper;
import goblinbob.mobends.standard.data.PlayerData;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelElytra;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerCustomElytra implements LayerRenderer<AbstractClientPlayer> {
   private static final ResourceLocation TEXTURE_ELYTRA = new ResourceLocation("textures/entity/elytra.png");
   protected final RenderPlayer renderPlayer;
   private final ModelElytra modelElytra = new ModelElytra();

   public LayerCustomElytra(RenderPlayer p_i47185_1_) {
      this.renderPlayer = p_i47185_1_;
   }

   public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      PlayerData data = (PlayerData)BenderHelper.getData(player, this.renderPlayer);

      assert data != null;

      ItemStack itemstack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
      if (itemstack.getItem() == Items.ELYTRA) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(SourceFactor.ONE, DestFactor.ZERO);
         if (player.isPlayerInfoSet() && player.getLocationElytra() != null) {
            this.renderPlayer.bindTexture(player.getLocationElytra());
         } else if (player.hasPlayerInfo() && player.getLocationCape() != null && player.isWearing(EnumPlayerModelParts.CAPE)) {
            this.renderPlayer.bindTexture(player.getLocationCape());
         } else {
            this.renderPlayer.bindTexture(TEXTURE_ELYTRA);
         }

         GlStateManager.pushMatrix();
         data.body.applyCharacterTransform(0.0625F);
         GlStateManager.translate(0.0F, -12.0F * scale, 0.0F);
         this.modelElytra.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, player);
         this.modelElytra.render(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
         if (itemstack.isItemEnchanted()) {
            LayerArmorBase.renderEnchantedGlint(this.renderPlayer, player, this.modelElytra, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
         }

         GlStateManager.disableBlend();
         GlStateManager.popMatrix();
      }

   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
