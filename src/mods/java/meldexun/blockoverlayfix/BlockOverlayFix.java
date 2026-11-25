package meldexun.blockoverlayfix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.CullFace;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraftforge.fml.common.Mod;

@Mod(
   modid = "blockoverlayfix"
)
public class BlockOverlayFix {
   public static final String MODID = "blockoverlayfix";

   public BlockOverlayFix() {
   }

   public static void renderNearbyBlocks(float partialTicks) {
      Minecraft mc = Minecraft.getMinecraft();
      if (!mc.player.isPlayerSleeping()) {
         if (!mc.player.isSpectator()) {
            double error = 0.175D;
            double x = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * (double)partialTicks;
            double y = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * (double)partialTicks + (double)mc.player.getEyeHeight();
            double z = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * (double)partialTicks;
            AxisAlignedBB aabb = new AxisAlignedBB(x - error, y - error, z - error, x + error, y + error, z + error);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer bufferBuilder = tessellator.getBuffer();
            boolean[] startedBuilding = new boolean[1];
            forEachNearbyPos(x, y, z, error, (pos) -> {
               IBlockState state = mc.world.getBlockState(pos);
               if (state.getRenderType() != EnumBlockRenderType.INVISIBLE) {
                  if (!Arrays.stream(EnumFacing.VALUES).noneMatch((side) -> {
                     return state.doesSideBlockRendering(mc.world, pos, side);
                  })) {
                     List<AxisAlignedBB> aabbs = new ArrayList();
                     state.getBlock().addCollisionBoxToList(state, mc.world, pos, aabb, aabbs, mc.player, true);
                     Vec3d vec = new Vec3d(x, y, z);
                     if (!aabbs.stream().noneMatch((aabb1) -> {
                        return aabb1.grow(error).contains(vec);
                     })) {
                        if (!startedBuilding[0]) {
                           startedBuilding[0] = true;
                           bufferBuilder.begin(7, DefaultVertexFormats.BLOCK);
                           bufferBuilder.setTranslation(-x, -(y - (double)mc.player.getEyeHeight()), -z);
                        }

                        IBlockState state1 = state.getActualState(mc.world, pos);
                        mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModel(mc.world, mc.getBlockRendererDispatcher().getModelForState(state1), state1, pos, bufferBuilder, false);
                     }
                  }
               }
            });
            if (startedBuilding[0]) {
               bufferBuilder.setTranslation(0.0D, 0.0D, 0.0D);
               GlStateManager.cullFace(CullFace.FRONT);
               tessellator.draw();
               bufferBuilder.reset();
               GlStateManager.cullFace(CullFace.BACK);
               if (OpenGlHelper.useVbo()) {
                  GlStateManager.glEnableClientState(32884);
                  OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                  GlStateManager.glEnableClientState(32888);
                  OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
                  GlStateManager.glEnableClientState(32888);
                  OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                  GlStateManager.glEnableClientState(32886);
               }
            }

         }
      }
   }

   private static void forEachNearbyPos(double x, double y, double z, double error, Consumer<BlockPos> action) {
      int bx = MathHelper.floor(x);
      int by = MathHelper.floor(y);
      int bz = MathHelper.floor(z);
      MutableBlockPos pos = new MutableBlockPos();
      int used = 0;

      for(int i = 0; i < 8; ++i) {
         double x1 = x + (double)((i >> 1 & 2) - 1) * error;
         double y1 = y + (double)((i & 2) - 1) * error;
         double z1 = z + (double)((i << 1 & 2) - 1) * error;
         pos.setPos(x1, y1, z1);
         int mask = 1 << ((pos.getX() + 1 - bx) * 3 + (pos.getY() + 1 - by)) * 3 + (pos.getZ() + 1 - bz);
         if ((used & mask) == 0) {
            used |= mask;
            action.accept(pos);
         }
      }

   }
}
