package pl.asie.foamfix.coremod.injections;

import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.VertexLighterSmoothAo;
import pl.asie.foamfix.coremod.injections.client.IFoamFixPatchedBlockInfo;

public class VertexLighterSmoothAoCheap extends VertexLighterSmoothAo {
   public VertexLighterSmoothAoCheap(BlockColors colors) {
      super(colors);
   }

   protected void updateLightmap(float[] normal, float[] lightmap, float x, float y, float z) {
      float e1 = 0.99F;
      float e2 = 0.95F;
      boolean full = this.blockInfo.isFullCube();
      EnumFacing side = null;
      if ((full || y < -0.99F) && normal[1] < -0.95F) {
         side = EnumFacing.DOWN;
      } else if ((full || y > 0.99F) && normal[1] > 0.95F) {
         side = EnumFacing.UP;
      } else if ((full || z < -0.99F) && normal[2] < -0.95F) {
         side = EnumFacing.NORTH;
      } else if ((full || z > 0.99F) && normal[2] > 0.95F) {
         side = EnumFacing.SOUTH;
      } else if ((full || x < -0.99F) && normal[0] < -0.95F) {
         side = EnumFacing.WEST;
      } else if ((full || x > 0.99F) && normal[0] > 0.95F) {
         side = EnumFacing.EAST;
      }

      int i = side == null ? 0 : side.ordinal() + 1;
      int brightness = this.blockInfo.getPackedLight()[i];
      lightmap[0] = (float)(brightness >> 4 & 15) * 32.0F / 65535.0F;
      lightmap[1] = (float)(brightness >> 20 & 15) * 32.0F / 65535.0F;
   }

   public void updateBlockInfo() {
      this.blockInfo.updateShift();
      this.blockInfo.updateFlatLighting();
      ((IFoamFixPatchedBlockInfo)this.blockInfo).updateAO();
   }
}
