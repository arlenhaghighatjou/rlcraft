package ivorius.reccomplex.operation;

import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.rendering.grid.GridQuadCache;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.client.rendering.OperationRenderer;
import ivorius.reccomplex.client.rendering.SchematicQuadCache;
import ivorius.reccomplex.utils.RCAxisAlignedTransform;
import ivorius.reccomplex.world.gen.feature.structure.schematics.SchematicFile;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OperationGenerateSchematic implements Operation {
   public SchematicFile file;
   public AxisAlignedTransform2D transform;
   public BlockPos lowerCoord;
   protected GridQuadCache cachedShapeGrid;

   public OperationGenerateSchematic() {
   }

   public OperationGenerateSchematic(SchematicFile file, AxisAlignedTransform2D transform, BlockPos lowerCoord) {
      this.file = file;
      this.transform = transform;
      this.lowerCoord = lowerCoord;
   }

   public void perform(WorldServer world) {
      if (this.lowerCoord != null) {
         this.file.generate(world, this.lowerCoord, this.transform);
      }

   }

   public void writeToNBT(NBTTagCompound compound) {
      NBTTagCompound fileCompound = new NBTTagCompound();
      this.file.writeToNBT(fileCompound);
      compound.setTag("schematic", fileCompound);
      RCAxisAlignedTransform.write(compound, this.transform, "rotation", "mirrorX");
      BlockPositions.writeToNBT("lowerCoord", this.lowerCoord, compound);
   }

   public void readFromNBT(NBTTagCompound compound) {
      try {
         this.file = new SchematicFile(compound.getCompoundTag("schematic"));
      } catch (SchematicFile.UnsupportedSchematicFormatException var3) {
         RecurrentComplex.logger.error(var3);
         this.file = new SchematicFile((short)0, (short)0, (short)0);
      }

      this.transform = RCAxisAlignedTransform.read(compound, "rotation", "mirrorX");
      this.lowerCoord = BlockPositions.readFromNBT("lowerCoord", compound);
   }

   public void invalidateCache() {
      this.cachedShapeGrid = null;
   }

   @SideOnly(Side.CLIENT)
   public void renderPreview(Operation.PreviewType previewType, World world, int ticks, float partialTicks) {
      if (this.lowerCoord != null) {
         int[] size = new int[]{this.file.width, this.file.height, this.file.length};
         if (previewType == Operation.PreviewType.SHAPE) {
            GlStateManager.color(0.8F, 0.75F, 1.0F);
            OperationRenderer.renderGridQuadCache(this.cachedShapeGrid != null ? this.cachedShapeGrid : (this.cachedShapeGrid = SchematicQuadCache.createQuadCache(this.file, new float[]{1.0F, 1.0F, 1.0F})), this.transform, this.lowerCoord, ticks, partialTicks);
         }

         if (previewType == Operation.PreviewType.BOUNDING_BOX || previewType == Operation.PreviewType.SHAPE) {
            OperationRenderer.renderBoundingBox(OperationRenderer.blockAreaFromSize(this.lowerCoord, RCAxisAlignedTransform.applySize(this.transform, size)), ticks, partialTicks);
         }
      }

   }
}
