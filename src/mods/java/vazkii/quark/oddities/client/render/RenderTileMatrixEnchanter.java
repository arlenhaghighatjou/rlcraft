package vazkii.quark.oddities.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import vazkii.quark.oddities.tile.TileMatrixEnchanter;

public class RenderTileMatrixEnchanter extends TileEntitySpecialRenderer<TileMatrixEnchanter> {
   private static final ResourceLocation TEXTURE_BOOK = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private final ModelBook modelBook = new ModelBook();

   public RenderTileMatrixEnchanter() {
   }

   public void render(TileMatrixEnchanter te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, z);
      float time = (float)te.tickCount + partialTicks;

      float f1;
      for(f1 = te.bookRotation - te.bookRotationPrev; (double)f1 >= 3.141592653589793D; f1 = (float)((double)f1 - 6.283185307179586D)) {
         ;
      }

      while((double)f1 < -3.141592653589793D) {
         f1 = (float)((double)f1 + 6.283185307179586D);
      }

      float rot = te.bookRotationPrev + f1 * partialTicks;
      float bookOpen = te.bookSpreadPrev + (te.bookSpread - te.bookSpreadPrev) * partialTicks;
      this.renderBook(te, time, rot, partialTicks);
      ItemStack item = te.getStackInSlot(0);
      if (!item.isEmpty()) {
         this.renderItem(item, time, bookOpen, rot);
      }

      GlStateManager.popMatrix();
   }

   private void renderItem(ItemStack item, float time, float bookOpen, float rot) {
      GlStateManager.pushMatrix();
      RenderHelper.enableStandardItemLighting();
      GlStateManager.translate(0.5F, 0.8F, 0.5F);
      GlStateManager.scale(0.6F, 0.6F, 0.6F);
      rot = rot * -57.295776F;
      rot = rot + 90.0F;
      rot = rot * bookOpen;
      GlStateManager.rotate(rot, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(0.0D, (double)(bookOpen * 1.4F), Math.sin((double)bookOpen * 3.141592653589793D));
      GlStateManager.rotate(-90.0F * (bookOpen - 1.0F), 1.0F, 0.0F, 0.0F);
      float trans = (float)Math.sin((double)time * 0.06D) * bookOpen * 0.2F;
      GlStateManager.translate(0.0F, trans, 0.0F);
      RenderItem render = Minecraft.getMinecraft().getRenderItem();
      render.renderItem(item, TransformType.FIXED);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.popMatrix();
   }

   private void renderBook(TileMatrixEnchanter te, float time, float bookRot, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(0.5F, 0.85F + MathHelper.sin(time * 0.1F) * 0.01F, 0.5F);
      GlStateManager.rotate(-bookRot * 57.295776F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(80.0F, 0.0F, 0.0F, 1.0F);
      this.bindTexture(TEXTURE_BOOK);
      float f3 = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTicks + 0.25F;
      float f4 = te.pageFlipPrev + (te.pageFlip - te.pageFlipPrev) * partialTicks + 0.75F;
      f3 = (f3 - (float)MathHelper.fastFloor((double)f3)) * 1.6F - 0.3F;
      f4 = (f4 - (float)MathHelper.fastFloor((double)f4)) * 1.6F - 0.3F;
      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      if (f4 < 0.0F) {
         f4 = 0.0F;
      }

      if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      if (f4 > 1.0F) {
         f4 = 1.0F;
      }

      float f5 = te.bookSpreadPrev + (te.bookSpread - te.bookSpreadPrev) * partialTicks;
      GlStateManager.enableCull();
      this.modelBook.render((Entity)null, time, f3, f4, f5, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
   }
}
