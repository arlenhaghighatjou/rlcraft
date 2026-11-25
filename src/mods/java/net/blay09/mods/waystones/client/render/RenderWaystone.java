package net.blay09.mods.waystones.client.render;

import net.blay09.mods.waystones.WaystoneConfig;
import net.blay09.mods.waystones.WaystoneManager;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.BlockWaystone;
import net.blay09.mods.waystones.block.TileWaystone;
import net.blay09.mods.waystones.client.ClientWaystones;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class RenderWaystone extends TileEntitySpecialRenderer<TileWaystone> {
   private static final ResourceLocation texture = new ResourceLocation("waystones", "textures/entity/waystone.png");
   private static final ResourceLocation textureMossy = new ResourceLocation("waystones", "textures/entity/waystone_mossy.png");
   private static final ResourceLocation textureActive = new ResourceLocation("waystones", "textures/entity/waystone_active.png");
   private final ModelWaystone model = new ModelWaystone();

   public RenderWaystone() {
   }

   public void render(TileWaystone tileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
      IBlockState state = tileEntity != null && tileEntity.hasWorld() ? tileEntity.getWorld().getBlockState(tileEntity.getPos()) : null;
      if (state == null || state.getBlock() == Waystones.blockWaystone) {
         boolean isDummy = state != null && !((Boolean)state.getValue(BlockWaystone.BASE)).booleanValue();
         if (!isDummy || destroyStage >= 0) {
            if (destroyStage >= 0) {
               this.bindTexture(DESTROY_STAGES[destroyStage]);
               GlStateManager.matrixMode(5890);
               GlStateManager.pushMatrix();
               GlStateManager.scale(4.0F, 8.0F, 1.0F);
               GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
               GlStateManager.matrixMode(5888);
            } else {
               boolean isMossy = tileEntity != null && tileEntity.isMossy() && WaystoneConfig.client.randomlySpawnedLookMossy;
               this.bindTexture(isMossy ? textureMossy : texture);
            }

            float angle = state != null ? WaystoneManager.getRotationYaw((EnumFacing)state.getValue(BlockWaystone.FACING)) : 0.0F;
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.translate(x + 0.5D, y + (double)(isDummy ? -1 : 0), z + 0.5D);
            GlStateManager.rotate(angle, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-180.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            this.model.renderAll();
            if (tileEntity != null && tileEntity.hasWorld() && ClientWaystones.getKnownWaystone(tileEntity.getWaystoneName()) != null) {
               this.bindTexture(textureActive);
               GlStateManager.scale(1.05F, 1.05F, 1.05F);
               if (!WaystoneConfig.client.disableTextGlow) {
                  Minecraft.getMinecraft().entityRenderer.disableLightmap();
               }

               this.model.renderPillar();
               if (!WaystoneConfig.client.disableTextGlow) {
                  Minecraft.getMinecraft().entityRenderer.enableLightmap();
               }
            }

            GlStateManager.popMatrix();
            if (destroyStage >= 0) {
               GlStateManager.matrixMode(5890);
               GlStateManager.popMatrix();
               GlStateManager.matrixMode(5888);
            }

         }
      }
   }
}
