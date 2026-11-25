package vazkii.quark.decoration.client.render;

import java.util.Calendar;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.decoration.feature.VariedChests;
import vazkii.quark.decoration.tile.TileCustomChest;

public class RenderTileCustomChest extends TileEntitySpecialRenderer<TileCustomChest> {
   private static final ResourceLocation TEXTURE_CHRISTMAS_DOUBLE = new ResourceLocation("textures/entity/chest/christmas_double.png");
   private static final ResourceLocation TEXTURE_CHRISTMAS = new ResourceLocation("textures/entity/chest/christmas.png");
   private final ModelChest simpleChest = new ModelChest();
   private final ModelChest largeChest = new ModelLargeChest();
   private boolean xmas;

   public RenderTileCustomChest() {
      if (GlobalConfig.enableSeasonalFeatures) {
         Calendar calendar = Calendar.getInstance();
         this.xmas = calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26;
      }

   }

   public void render(TileCustomChest te, double x, double y, double z, float partialTicks, int destroyStage, float something) {
      GlStateManager.enableDepth();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(true);
      int meta;
      if (te.hasWorld()) {
         meta = te.getBlockMetadata();
         te.checkForAdjacentChests();
      } else {
         meta = 3;
      }

      if (te.adjacentChestZNeg == null && te.adjacentChestXNeg == null) {
         ModelChest model;
         if (te.adjacentChestXPos == null && te.adjacentChestZPos == null) {
            model = this.simpleChest;
            if (destroyStage >= 0) {
               this.bindTexture(DESTROY_STAGES[destroyStage]);
               GlStateManager.matrixMode(5890);
               GlStateManager.pushMatrix();
               GlStateManager.scale(4.0F, 4.0F, 1.0F);
               GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
               GlStateManager.matrixMode(5888);
            } else if (this.xmas) {
               this.bindTexture(TEXTURE_CHRISTMAS);
            } else if (te.getChestType() == VariedChests.CUSTOM_TYPE_QUARK_TRAP) {
               this.bindTexture(te.chestType.nrmTrapTex);
            } else {
               this.bindTexture(te.chestType.nrmTex);
            }
         } else {
            model = this.largeChest;
            if (destroyStage >= 0) {
               this.bindTexture(DESTROY_STAGES[destroyStage]);
               GlStateManager.matrixMode(5890);
               GlStateManager.pushMatrix();
               GlStateManager.scale(8.0F, 4.0F, 1.0F);
               GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
               GlStateManager.matrixMode(5888);
            } else if (this.xmas) {
               this.bindTexture(TEXTURE_CHRISTMAS_DOUBLE);
            } else if (te.getChestType() == VariedChests.CUSTOM_TYPE_QUARK_TRAP) {
               this.bindTexture(te.chestType.dblTrapTex);
            } else {
               this.bindTexture(te.chestType.dblTex);
            }
         }

         GlStateManager.pushMatrix();
         GlStateManager.enableRescaleNormal();
         if (destroyStage < 0) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }

         GlStateManager.translate(x, y + 1.0D, z + 1.0D);
         GlStateManager.scale(1.0F, -1.0F, -1.0F);
         GlStateManager.translate(0.5F, 0.5F, 0.5F);
         int angle;
         if (meta == 2) {
            angle = 180;
         } else if (meta == 3) {
            angle = 0;
         } else if (meta == 4) {
            angle = 90;
         } else {
            angle = -90;
         }

         if (meta == 2 && te.adjacentChestXPos != null) {
            GlStateManager.translate(1.0F, 0.0F, 0.0F);
         }

         if (meta == 5 && te.adjacentChestZPos != null) {
            GlStateManager.translate(0.0F, 0.0F, -1.0F);
         }

         GlStateManager.rotate((float)angle, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(-0.5F, -0.5F, -0.5F);
         float lidAngle = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;
         if (te.adjacentChestZNeg != null) {
            float adjLidAngle = te.adjacentChestZNeg.prevLidAngle + (te.adjacentChestZNeg.lidAngle - te.adjacentChestZNeg.prevLidAngle) * partialTicks;
            if (adjLidAngle > lidAngle) {
               lidAngle = adjLidAngle;
            }
         }

         if (te.adjacentChestXNeg != null) {
            float adjLidAngle = te.adjacentChestXNeg.prevLidAngle + (te.adjacentChestXNeg.lidAngle - te.adjacentChestXNeg.prevLidAngle) * partialTicks;
            if (adjLidAngle > lidAngle) {
               lidAngle = adjLidAngle;
            }
         }

         lidAngle = 1.0F - lidAngle;
         lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
         model.chestLid.rotateAngleX = -(lidAngle * 1.5707964F);
         model.renderAll();
         GlStateManager.disableRescaleNormal();
         GlStateManager.popMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         if (destroyStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
         }
      }

   }
}
