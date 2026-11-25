package pw.prok.realbench;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;

public class WorkbenchRenderer extends TileEntitySpecialRenderer<WorkbenchTile> {
   Map<WorkbenchTile, WorkbenchRenderer.RenderingState> states = new WeakHashMap();
   static final int ANIMATION_DURATION = 1000;

   public WorkbenchRenderer() {
   }

   public void render(WorkbenchTile tile, double xOffset, double yOffset, double zOffset, float partialTicks, int destroyStage, float alpha) {
      super.render(tile, xOffset, yOffset, zOffset, partialTicks, destroyStage, alpha);

      for(int i = 0; i < tile.mCraftMatrix.size(); ++i) {
         ItemStack itemStack = (ItemStack)tile.mCraftMatrix.get(i);
         if (itemStack != ItemStack.EMPTY) {
            WorkbenchRenderer.RenderingState state = (WorkbenchRenderer.RenderingState)this.states.computeIfAbsent(tile, (k) -> {
               return new WorkbenchRenderer.RenderingState();
            });
            double playerAngle = (Math.atan2(xOffset + 0.5D, zOffset + 0.5D) + 3.9269908169872414D) % 6.283185307179586D;
            byte sector = (byte)((int)(playerAngle * 2.0D / 3.141592653589793D));
            long time = System.currentTimeMillis();
            if (state.sector != sector) {
               state.animating = true;
               state.animationAngleStart = state.currentAngle;
               float delta1 = (float)sector * 90.0F - state.currentAngle;
               float abs1 = Math.abs(delta1);
               float delta2 = delta1 + 360.0F;
               float abs2 = Math.abs(delta2);
               float delta3 = delta1 - 360.0F;
               float abs3 = Math.abs(delta3);
               if (abs3 < abs1 && abs3 < abs2) {
                  state.animationAngleEnd = delta3 + state.currentAngle;
               } else if (abs2 < abs1 && abs2 < abs3) {
                  state.animationAngleEnd = delta2 + state.currentAngle;
               } else {
                  state.animationAngleEnd = delta1 + state.currentAngle;
               }

               state.startTime = time;
               state.sector = sector;
            }

            if (state.animating) {
               if (time >= state.startTime + 1000L) {
                  state.animating = false;
                  state.currentAngle = (state.animationAngleEnd + 360.0F) % 360.0F;
               } else {
                  state.currentAngle = (easeOutQuad(time - state.startTime, state.animationAngleStart, state.animationAngleEnd - state.animationAngleStart, 1000) + 360.0F) % 360.0F;
               }
            }

            Item item = itemStack.getItem();
            Block block;
            if (item instanceof ItemBlock) {
               block = ((ItemBlock)item).getBlock();
            } else if (item instanceof ItemBlockSpecial) {
               block = ((ItemBlockSpecial)item).block;
            } else {
               block = null;
            }

            boolean normalBlock = block != null && block.getDefaultState().getMaterial().isSolid();
            float shift = (float)Math.abs((time + (long)(i * 1000)) % 5000L - 2500L) / 200000.0F;
            GlStateManager.pushMatrix();
            GlStateManager.translate(xOffset + 0.5D, yOffset + (double)shift, zOffset + 0.5D);
            GlStateManager.rotate(state.currentAngle, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate((double)(i % 3) * 3.0D / 16.0D + 0.3125D - 0.5D, 1.09375D, (double)(i / 3) * 3.0D / 16.0D + 0.3125D - 0.5D);
            if (!normalBlock) {
               GlStateManager.rotate(-this.rendererDispatcher.entityPitch, 1.0F, 0.0F, 0.0F);
            }

            GlStateManager.scale(0.125F, 0.125F, 0.125F);
            Minecraft.getMinecraft().getRenderItem().renderItem(itemStack, TransformType.NONE);
            GlStateManager.popMatrix();
         }
      }

   }

   private static float easeOutQuad(long t, float b, float c, int d) {
      float z = (float)t / (float)d;
      return -c * z * (z - 2.0F) + b;
   }

   static class RenderingState {
      byte sector;
      float currentAngle;
      boolean animating;
      float animationAngleStart;
      float animationAngleEnd;
      long startTime;

      RenderingState() {
      }
   }
}
