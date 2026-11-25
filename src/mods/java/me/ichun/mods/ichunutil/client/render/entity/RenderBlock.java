package me.ichun.mods.ichunutil.client.render.entity;

import java.util.HashSet;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.entity.EntityBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderBlock extends Render<EntityBlock> {
   public HashSet<Class<? extends TileEntity>> classesNotToRender = new HashSet();

   public RenderBlock(RenderManager renderManager) {
      super(renderManager);
   }

   protected ResourceLocation getEntityTexture(EntityBlock entity) {
      return TextureMap.LOCATION_BLOCKS_TEXTURE;
   }

   public void doRender(EntityBlock entBlock, double x, double y, double z, float entityYaw, float partialTicks) {
      if (entBlock.setup) {
         GlStateManager.pushMatrix();
         GlStateManager.translate(x, y, z);
         if (entBlock.getCanRotate()) {
            GlStateManager.translate(0.0D, (double)entBlock.height / 2.0D, 0.0D);
            GlStateManager.rotate(EntityHelper.interpolateRotation(entBlock.prevRotYaw, entBlock.rotYaw, partialTicks), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(EntityHelper.interpolateRotation(entBlock.prevRotPitch, entBlock.rotPitch, partialTicks), 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0D, -((double)entBlock.height / 2.0D), 0.0D);
         }

         GlStateManager.translate(0.5D * (double)(entBlock.blocks.length - 1), 1.0D * (double)(entBlock.blocks[0].length - 1), 0.5D * (double)(entBlock.blocks[0][0].length - 1));
         GlStateManager.disableLighting();
         BlockPos blockpos = new BlockPos(entBlock);
         BlockPos blockposMaxY = new BlockPos(entBlock.posX, entBlock.getEntityBoundingBox().maxY, entBlock.posZ);
         World world = entBlock.world;

         for(int ii = 0; ii < entBlock.blocks.length; ++ii) {
            for(int jj = 0; jj < entBlock.blocks[ii].length; ++jj) {
               for(int kk = 0; kk < entBlock.blocks[ii][jj].length; ++kk) {
                  if (entBlock.blocks[ii][jj][kk] != null) {
                     IBlockState iblockstate = entBlock.blocks[ii][jj][kk];
                     Block block = iblockstate.getBlock();
                     GlStateManager.pushMatrix();
                     GlStateManager.translate((float)(-ii), (float)(-jj), (float)(-kk));
                     if (iblockstate.getRenderType() == EnumBlockRenderType.MODEL && iblockstate != world.getBlockState(blockpos) && iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE) {
                        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                        GlStateManager.pushMatrix();
                        GlStateManager.disableLighting();
                        Tessellator tessellator = Tessellator.getInstance();
                        WorldRenderer bufferbuilder = tessellator.getBuffer();
                        if (this.renderOutlines) {
                           GlStateManager.enableColorMaterial();
                           GlStateManager.enableOutlineMode(this.getTeamColor(entBlock));
                        }

                        bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
                        GlStateManager.translate((float)(-((double)blockposMaxY.getX()) - 0.5D), (float)(-((double)blockposMaxY.getY())), (float)(-((double)blockposMaxY.getZ()) - 0.5D));
                        BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
                        blockrendererdispatcher.getBlockModelRenderer().renderModel(world, blockrendererdispatcher.getModelForState(iblockstate), iblockstate, blockposMaxY, bufferbuilder, false, MathHelper.getPositionRandom(entBlock.getOrigin().add(ii, jj, kk)));
                        tessellator.draw();
                        if (this.renderOutlines) {
                           GlStateManager.disableOutlineMode();
                           GlStateManager.disableColorMaterial();
                        }

                        GlStateManager.enableLighting();
                        GlStateManager.popMatrix();
                     }

                     if (entBlock.tileEntityNBTs[ii][jj][kk] != null && block.hasTileEntity(iblockstate)) {
                        if (entBlock.renderingTileEntities == null) {
                           entBlock.renderingTileEntities = new TileEntity[entBlock.blocks.length][entBlock.blocks[ii].length][entBlock.blocks[ii][jj].length];
                        }

                        if (entBlock.renderingTileEntities[ii][jj][kk] == null) {
                           TileEntity te = block.createTileEntity(entBlock.world, iblockstate);
                           if (te != null) {
                              te.setWorld(entBlock.world);
                              te.readFromNBT(entBlock.tileEntityNBTs[ii][jj][kk].copy());
                              entBlock.renderingTileEntities[ii][jj][kk] = te;
                           }
                        }

                        if (!this.classesNotToRender.contains(entBlock.renderingTileEntities[ii][jj][kk].getClass())) {
                           try {
                              TileEntityRendererDispatcher.instance.render(entBlock.renderingTileEntities[ii][jj][kk], -0.5D, 0.0D, -0.5D, partialTicks);
                           } catch (ReportedException var21) {
                              this.classesNotToRender.add(entBlock.renderingTileEntities[ii][jj][kk].getClass());
                           }
                        }
                     }

                     GlStateManager.popMatrix();
                  }
               }
            }
         }

         GlStateManager.enableLighting();
         GlStateManager.popMatrix();
      }
   }

   public static class RenderFactory implements IRenderFactory<EntityBlock> {
      public RenderFactory() {
      }

      public Render<EntityBlock> createRenderFor(RenderManager manager) {
         return new RenderBlock(manager);
      }
   }
}
