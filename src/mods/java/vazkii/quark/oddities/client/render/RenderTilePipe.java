package vazkii.quark.oddities.client.render;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import vazkii.quark.oddities.tile.TilePipe;

public class RenderTilePipe extends TileEntitySpecialRenderer<TilePipe> {
   private Random random = new Random();

   public RenderTilePipe() {
   }

   public void render(TilePipe te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
      GlStateManager.pushMatrix();
      GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);
      RenderItem render = Minecraft.getMinecraft().getRenderItem();
      Iterator<TilePipe.PipeItem> items = te.getItemIterator();

      while(items.hasNext()) {
         this.renderItem((TilePipe.PipeItem)items.next(), render, partialTicks);
      }

      GlStateManager.popMatrix();
   }

   private void renderItem(TilePipe.PipeItem item, RenderItem render, float partial) {
      GlStateManager.pushMatrix();
      GlStateManager.pushAttrib();
      RenderHelper.enableStandardItemLighting();
      float scale = 0.4F;
      float fract = item.getTimeFract(partial);
      float shiftFract = fract - 0.5F;
      EnumFacing face = item.outgoingFace;
      if ((double)fract < 0.5D) {
         face = item.incomingFace.getOpposite();
      }

      float offX = (float)face.getFrontOffsetX() * 1.0F;
      float offY = (float)face.getFrontOffsetY() * 1.0F;
      float offZ = (float)face.getFrontOffsetZ() * 1.0F;
      GlStateManager.translate(offX * shiftFract, offY * shiftFract, offZ * shiftFract);
      GlStateManager.scale(scale, scale, scale);
      float speed = 4.0F;
      GlStateManager.rotate(((float)item.timeInWorld + partial) * speed, 0.0F, 1.0F, 0.0F);
      int seed = item.stack.isEmpty() ? 187 : Item.getIdFromItem(item.stack.getItem()) + item.stack.getMetadata();
      this.random.setSeed((long)seed);
      int count = this.getModelCount(item.stack);

      for(int i = 0; i < count; ++i) {
         GlStateManager.pushMatrix();
         if (i > 0) {
            float spread = 0.15F;
            float x = (this.random.nextFloat() * 2.0F - 1.0F) * spread;
            float y = (this.random.nextFloat() * 2.0F - 1.0F) * spread;
            float z = (this.random.nextFloat() * 2.0F - 1.0F) * spread;
            GlStateManager.translate(x, y, z);
         }

         render.renderItem(item.stack, TransformType.FIXED);
         GlStateManager.popMatrix();
      }

      RenderHelper.disableStandardItemLighting();
      GlStateManager.popAttrib();
      GlStateManager.popMatrix();
   }

   protected int getModelCount(ItemStack stack) {
      if (stack.getCount() > 48) {
         return 5;
      } else if (stack.getCount() > 32) {
         return 4;
      } else if (stack.getCount() > 16) {
         return 3;
      } else {
         return stack.getCount() > 1 ? 2 : 1;
      }
   }
}
