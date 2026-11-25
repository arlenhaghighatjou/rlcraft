package meldexun.blockoverlayfix;

import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.GL_BACK;
import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.GL_FRONT;
import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.GL_QUADS;

@Mod(
   modid = "blockoverlayfix",
   name = "Block Overlay Fix",
   version = "1.0.0",
   clientSideOnly = true
)
public class BlockOverlayFix {
   public static final String MODID = "blockoverlayfix";
   private static final double MARGIN = 0.175D;
   private static final MutableBlockPos POS = new MutableBlockPos();
   private static final List<AxisAlignedBB> COLLISIONS = new ArrayList<>();
   private static boolean enabled;

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         enabled = true;
      }
   }

   public static boolean isEnabled() {
      return enabled;
   }

   public static void renderNearbyBlocks(float partialTicks) {
      Minecraft mc = Minecraft.getMinecraft();
      EntityPlayerSP player = mc.player;
      if (player == null || player.isPlayerSleeping() || player.isSpectator()) {
         return;
      }

      double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
      double feetY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
      double y = feetY + player.getEyeHeight();
      double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
      AxisAlignedBB eyeBox = new AxisAlignedBB(x - MARGIN, y - MARGIN, z - MARGIN, x + MARGIN, y + MARGIN, z + MARGIN);
      BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer buffer = tessellator.getBuffer();
      boolean drawing = false;
      for (int bx = MathHelper.floor(eyeBox.minX); bx <= MathHelper.floor(eyeBox.maxX); ++bx) {
         for (int by = MathHelper.floor(eyeBox.minY); by <= MathHelper.floor(eyeBox.maxY); ++by) {
            for (int bz = MathHelper.floor(eyeBox.minZ); bz <= MathHelper.floor(eyeBox.maxZ); ++bz) {
               POS.setPos(bx, by, bz);
               IBlockState state = mc.world.getBlockState(POS);
               if (state.getRenderType() == EnumBlockRenderType.INVISIBLE || !blocksView(mc, state)) {
                  continue;
               }
               COLLISIONS.clear();
               state.addCollisionBoxToList(mc.world, POS, eyeBox, COLLISIONS, player, true);
               if (COLLISIONS.isEmpty()) {
                  continue;
               }
               if (!drawing) {
                  drawing = true;
                  buffer.begin(GL_QUADS, DefaultVertexFormats.BLOCK);
                  buffer.setTranslation(-x, -feetY, -z);
               }
               IBlockState actualState = state.getActualState(mc.world, POS);
               dispatcher.getBlockModelRenderer().renderModel(mc.world, dispatcher.getModelForState(actualState),
                  actualState, POS, buffer, false);
            }
         }
      }
      COLLISIONS.clear();
      if (drawing) {
         buffer.setTranslation(0.0D, 0.0D, 0.0D);
         GlStateManager.cullFace(GL_FRONT);
         try {
            tessellator.draw();
         } finally {
            GlStateManager.cullFace(GL_BACK);
         }
      }
   }

   private static boolean blocksView(Minecraft mc, IBlockState state) {
      for (EnumFacing side : EnumFacing.VALUES) {
         if (state.doesSideBlockRendering(mc.world, POS, side)) {
            return true;
         }
      }
      return false;
   }
}
