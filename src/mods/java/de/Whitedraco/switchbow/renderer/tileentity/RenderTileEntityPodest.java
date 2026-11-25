package de.Whitedraco.switchbow.renderer.tileentity;

import de.Whitedraco.switchbow.tileentity.TileEntityPodest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileEntityPodest extends TileEntitySpecialRenderer<TileEntityPodest> {
   public RenderTileEntityPodest() {
   }

   public void render(TileEntityPodest te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
      ItemStack stack = te.getStackInSlot(0);
      if (stack != ItemStack.EMPTY) {
         GlStateManager.enableRescaleNormal();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.enableBlend();
         RenderHelper.enableStandardItemLighting();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.pushMatrix();
         GlStateManager.translate(x + 0.5D, y + 0.75D, z + 0.5D);
         GlStateManager.rotate(((float)te.getWorld().getTotalWorldTime() + partialTicks) * 4.0F, 0.0F, 1.0F, 0.0F);
         IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, te.getWorld(), (EntityLivingBase)null);
         model = ForgeHooksClient.handleCameraTransforms(model, TransformType.GROUND, false);
         Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);
         GlStateManager.popMatrix();
         GlStateManager.disableRescaleNormal();
         GlStateManager.disableBlend();
      }

   }
}
