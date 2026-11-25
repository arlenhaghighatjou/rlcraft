package ivorius.reccomplex.operation;

import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.rendering.grid.AreaRenderer;
import ivorius.reccomplex.client.rendering.SelectionRenderer;
import ivorius.reccomplex.utils.ItemHandlers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class OperationClearArea implements Operation {
   public BlockArea sourceArea;

   public OperationClearArea() {
   }

   public OperationClearArea(BlockArea sourceArea) {
      this.sourceArea = sourceArea;
   }

   public static void setBlockToAirClean(World world, BlockPos pos) {
      emptyOut(world, pos);
      world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
   }

   public static void emptyOut(World world, BlockPos pos) {
      TileEntity tileEntity = world.getTileEntity(pos);
      if (tileEntity != null && ItemHandlers.hasModifiable(tileEntity)) {
         ItemHandlers.clear(ItemHandlers.getModifiable(tileEntity));
      }

   }

   public void writeToNBT(NBTTagCompound compound) {
      if (this.sourceArea != null) {
         BlockPositions.writeToNBT("sourcePoint1", this.sourceArea.getPoint1(), compound);
         BlockPositions.writeToNBT("sourcePoint2", this.sourceArea.getPoint2(), compound);
      }

   }

   public void readFromNBT(NBTTagCompound compound) {
      this.sourceArea = blockAreaFrom(BlockPositions.readFromNBT("sourcePoint1", compound), BlockPositions.readFromNBT("sourcePoint2", compound));
   }

   public static BlockArea blockAreaFrom(BlockPos left, BlockPos right) {
      return left != null && right != null ? new BlockArea(left, right) : null;
   }

   public void perform(WorldServer world) {
      if (this.sourceArea != null) {
         for(BlockPos coord : this.sourceArea) {
            setBlockToAirClean(world, coord);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void renderPreview(Operation.PreviewType previewType, World world, int ticks, float partialTicks) {
      if (this.sourceArea != null && (previewType == Operation.PreviewType.BOUNDING_BOX || previewType == Operation.PreviewType.SHAPE)) {
         GL11.glLineWidth(3.0F);
         GlStateManager.color(0.5F, 0.5F, 1.0F);
         AreaRenderer.renderAreaLined(this.sourceArea, 0.0212F);
         GlStateManager.enableBlend();
         OpenGlHelper.glBlendFunc(770, 771, 1, 0);
         GlStateManager.alphaFunc(516, 1.0E-4F);
         ResourceLocation curTex = SelectionRenderer.TEXTURE[MathHelper.floor(((float)ticks + partialTicks) * 0.75F) % SelectionRenderer.TEXTURE.length];
         Minecraft.getMinecraft().renderEngine.bindTexture(curTex);
         GlStateManager.color(0.3F, 0.3F, 0.4F, 0.2F);
         AreaRenderer.renderArea(this.sourceArea, false, true, 0.0112F);
         GlStateManager.color(0.4F, 0.4F, 0.5F, 0.35F);
         AreaRenderer.renderArea(this.sourceArea, false, false, 0.0112F);
         GlStateManager.alphaFunc(516, 0.002F);
         GlStateManager.disableBlend();
      }

   }
}
