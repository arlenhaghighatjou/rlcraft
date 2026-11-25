package goblinbob.mobends.standard.client.renderer.entity.layers;

import goblinbob.mobends.core.asset.AssetLocation;
import goblinbob.mobends.core.asset.AssetModels;
import goblinbob.mobends.core.client.model.IModelPart;
import goblinbob.mobends.core.supporters.AccessoryDetails;
import goblinbob.mobends.core.supporters.AccessoryPart;
import goblinbob.mobends.core.supporters.AccessorySettings;
import goblinbob.mobends.core.supporters.BindPoint;
import goblinbob.mobends.core.supporters.SupporterContent;
import goblinbob.mobends.core.util.BenderHelper;
import goblinbob.mobends.core.util.Color;
import goblinbob.mobends.standard.data.PlayerData;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

public class LayerPlayerAccessories implements LayerRenderer<AbstractClientPlayer> {
   private final RenderLivingBase<? extends AbstractClientPlayer> renderPlayer;
   private final RenderItem itemRenderer;
   private final ModelManager modelManager;
   private final TextureManager textureManager;
   private final ItemStack emptyItemStack = new ItemStack(Items.AIR);

   public LayerPlayerAccessories(RenderLivingBase<? extends AbstractClientPlayer> renderPlayer) {
      Minecraft mc = Minecraft.getMinecraft();
      this.renderPlayer = renderPlayer;
      this.itemRenderer = mc.getRenderItem();
      this.modelManager = mc.getRenderItem().getItemModelMesher().getModelManager();
      this.textureManager = mc.getTextureManager();
   }

   public void doRenderLayer(AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      Set<Entry<String, AccessoryDetails>> accessories = SupporterContent.getAccessories();
      PlayerData data = (PlayerData)BenderHelper.getData(player, this.renderPlayer);

      assert data != null;

      Map<String, AccessorySettings> settingsMap = SupporterContent.getAccessorySettingsMapFor(player);

      for(Entry<String, AccessoryDetails> entry : accessories) {
         AccessorySettings accessorySettings = (AccessorySettings)settingsMap.getOrDefault(entry.getKey(), AccessorySettings.DEFAULT);
         this.renderAccessory(player, data, (AccessoryDetails)entry.getValue(), accessorySettings, scale);
      }

   }

   private void renderAccessory(AbstractClientPlayer player, PlayerData data, AccessoryDetails accessory, AccessorySettings settings, float scale) {
      if (settings.isUnlocked() && !settings.isHidden()) {
         for(AccessoryPart part : accessory.getParts()) {
            this.renderPart(player, data, part, settings, scale);
         }

      }
   }

   private void renderPart(AbstractClientPlayer player, PlayerData data, AccessoryPart part, AccessorySettings settings, float scale) {
      SimpleBakedModel simpleBakedModel = AssetModels.INSTANCE.getModel(part.getModelPath());
      if (simpleBakedModel != null) {
         GlStateManager.pushMatrix();
         if (player.isSneaking()) {
            if (player.capabilities.isFlying) {
               GlStateManager.translate(0.0F, 4.0F * scale, 0.0F);
            } else {
               GlStateManager.translate(0.0F, 3.0F * scale, 0.0F);
            }
         }

         this.applyBindPointTransform(data, part.getBindPoint(), scale);
         ItemTransformVec3f transformVec3f = simpleBakedModel.getItemCameraTransforms().head;
         GlStateManager.translate(transformVec3f.translation.x, transformVec3f.translation.y, transformVec3f.translation.z);
         GlStateManager.rotate(transformVec3f.rotation.x, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(transformVec3f.rotation.y, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(transformVec3f.rotation.z, 0.0F, 0.0F, 1.0F);
         GlStateManager.scale(transformVec3f.scale.x, transformVec3f.scale.y, transformVec3f.scale.z);
         this.textureManager.bindTexture(part.getDiffuseTexturePath());
         this.drawModel(simpleBakedModel, -1);
         AssetLocation inkedLocation = part.getInkedTexturePath();
         if (inkedLocation != null) {
            this.textureManager.bindTexture(inkedLocation);
            GlStateManager.color(1.0F, 0.0F, 0.0F);
            this.drawModel(simpleBakedModel, Color.asHex(settings.getColor()));
            GlStateManager.color(1.0F, 1.0F, 0.0F);
         }

         GlStateManager.popMatrix();
      }
   }

   private void drawModel(IBakedModel model, int color) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.ITEM);

      for(EnumFacing enumfacing : EnumFacing.values()) {
         this.itemRenderer.renderQuads(bufferbuilder, model.getQuads((IBlockState)null, enumfacing, 0L), color, this.emptyItemStack);
      }

      this.itemRenderer.renderQuads(bufferbuilder, model.getQuads((IBlockState)null, (EnumFacing)null, 0L), color, this.emptyItemStack);
      tessellator.draw();
   }

   private void applyBindPointTransform(PlayerData data, BindPoint bindPoint, float scale) {
      IModelPart modelPart = (IModelPart)bindPoint.getPartSelector().apply(data);
      if (modelPart != null) {
         modelPart.applyCharacterTransform(scale);
      }

   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
