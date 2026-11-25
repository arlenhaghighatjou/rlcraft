package reborncore.client.multiblock;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL14;
import reborncore.client.multiblock.component.MultiblockComponent;

public class MultiblockRenderEvent {
   public static BlockPos anchor;
   public MultiblockSet currentMultiblock;
   public BlockPos parent;
   RebornFluidRenderer fluidRenderer = new RebornFluidRenderer();
   private ICamera camera;

   public MultiblockRenderEvent() {
   }

   public void setMultiblock(MultiblockSet set) {
      this.currentMultiblock = set;
      anchor = null;
      this.parent = null;
   }

   @SubscribeEvent
   public void onWorldRenderLast(RenderWorldLastEvent event) throws Throwable {
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.player != null && mc.objectMouseOver != null && !mc.player.isSneaking() && this.currentMultiblock != null) {
         BlockPos anchorPos = anchor != null ? anchor : mc.objectMouseOver.getBlockPos();
         Multiblock mb = this.currentMultiblock.getForIndex(0);

         for(MultiblockComponent comp : mb.getComponents()) {
            if (comp.state.getRenderType() == EnumBlockRenderType.LIQUID) {
               this.renderComponent(comp, anchorPos.up(), event.getPartialTicks(), mc.player);
            }
         }

         for(MultiblockComponent comp : mb.getComponents()) {
            if (comp.state.getRenderType() != EnumBlockRenderType.LIQUID) {
               this.renderComponent(comp, anchorPos.up(), event.getPartialTicks(), mc.player);
            }
         }
      }

   }

   private void renderComponent(MultiblockComponent comp, BlockPos anchor, float partialTicks, EntityPlayerSP player) {
      double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
      double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
      double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;
      if (this.camera == null) {
         this.camera = new Frustum();
      }

      this.camera.setPosition(dx, dy, dz);
      BlockPos pos = anchor.add(comp.getRelativePosition());
      if (this.camera.isBoundingBoxInFrustum(new AxisAlignedBB(pos))) {
         Minecraft minecraft = Minecraft.getMinecraft();
         World world = player.world;
         minecraft.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         BlockRenderLayer originalLayer = MinecraftForgeClient.getRenderLayer();
         ForgeHooksClient.setRenderLayer(BlockRenderLayer.CUTOUT);
         GlStateManager.pushMatrix();
         GlStateManager.translate(-dx, -dy, -dz);
         GlStateManager.translate((float)pos.getX(), (float)pos.getY(), (float)pos.getZ());
         GlStateManager.scale(0.8D, 0.8D, 0.8D);
         GlStateManager.translate(0.2D, 0.2D, 0.2D);
         RenderHelper.disableStandardItemLighting();
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.CONSTANT_ALPHA);
         GL14.glBlendColor(1.0F, 1.0F, 1.0F, 0.35F);
         this.renderModel(world, pos, comp.state);
         GlStateManager.disableBlend();
         GlStateManager.popMatrix();
         ForgeHooksClient.setRenderLayer(originalLayer);
      }
   }

   private void renderModel(World world, BlockPos pos, IBlockState state) {
      BlockRendererDispatcher blockRendererDispatcher = Minecraft.getMinecraft().blockRenderDispatcher;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder buffer = tessellator.getBuffer();
      GlStateManager.translate((float)(-pos.getX()), (float)(-pos.getY()), (float)(-pos.getZ()));
      buffer.begin(7, DefaultVertexFormats.BLOCK);
      if (state.getRenderType() == EnumBlockRenderType.LIQUID) {
         this.fluidRenderer.renderFluid(world, state, pos, buffer);
      } else {
         blockRendererDispatcher.renderBlock(state, pos, world, buffer);
      }

      tessellator.draw();
   }

   @SubscribeEvent
   public void breakBlock(BreakEvent event) {
      if (this.parent != null && event.getPos().getX() == this.parent.getX() && event.getPos().getY() == this.parent.getY() && event.getPos().getZ() == this.parent.getZ()) {
         this.setMultiblock((MultiblockSet)null);
      }

   }

   @SubscribeEvent
   public void worldUnloaded(Unload event) {
      this.setMultiblock((MultiblockSet)null);
   }
}
