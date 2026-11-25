package ivorius.reccomplex.operation;

import ivorius.ivtoolkit.blocks.BlockArea;
import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.rendering.grid.BlockQuadCache;
import ivorius.ivtoolkit.rendering.grid.GridQuadCache;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.client.rendering.OperationRenderer;
import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.utils.RCAxisAlignedTransform;
import ivorius.reccomplex.utils.RCStrings;
import ivorius.reccomplex.world.gen.feature.StructureGenerator;
import ivorius.reccomplex.world.gen.feature.structure.ReadableInstanceData;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import ivorius.reccomplex.world.gen.feature.structure.generic.StructureSaveHandler;
import java.util.Optional;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OperationGenerateStructure implements Operation {
   public GenericStructure structure;
   public AxisAlignedTransform2D transform;
   public BlockPos lowerCoord;
   public boolean generateAsSource;
   public String structureID;
   public String generationInfoID;
   protected GridQuadCache cachedShapeGrid;
   protected String seed;
   protected final ReadableInstanceData<GenericStructure.InstanceData> instanceData = new ReadableInstanceData<GenericStructure.InstanceData>();

   public OperationGenerateStructure() {
   }

   public OperationGenerateStructure(GenericStructure structure, String generationInfoID, AxisAlignedTransform2D transform, BlockPos lowerCoord, boolean generateAsSource) {
      this.structure = structure;
      this.generationInfoID = generationInfoID;
      this.transform = transform;
      this.lowerCoord = lowerCoord;
      this.generateAsSource = generateAsSource;
   }

   public OperationGenerateStructure dummy(GenericStructure structure) {
      OperationGenerateStructure op = (new OperationGenerateStructure(structure, this.generationInfoID, this.transform, this.lowerCoord, this.generateAsSource)).withSeed(this.seed).withStructureID(this.structureID);
      op.instanceData.set(this.instanceData);
      return op;
   }

   public String getStructureID() {
      return this.structureID;
   }

   public void setStructureID(String structureID) {
      this.structureID = structureID;
   }

   public OperationGenerateStructure withStructureID(String structureIDForSaving) {
      this.structureID = structureIDForSaving;
      return this;
   }

   public OperationGenerateStructure withSeed(String seed) {
      this.seed = seed;
      return this;
   }

   public OperationGenerateStructure prepare(WorldServer world) {
      return this.prepare(this.generator(world).instanceData());
   }

   public OperationGenerateStructure prepare(Optional<GenericStructure.InstanceData> instanceData) {
      this.instanceData.setInstanceData((NBTStorable)instanceData.orElse((Object)null));
      return this;
   }

   public void perform(WorldServer world) {
      if (!this.instanceData.exists()) {
         throw new IllegalStateException();
      } else {
         StructureGenerator<GenericStructure.InstanceData> generator = this.generator(world);
         this.instanceData.load(generator);
         generator.generate();
      }
   }

   public StructureGenerator<GenericStructure.InstanceData> generator(WorldServer world) {
      StructureGenerator<GenericStructure.InstanceData> generator = (new StructureGenerator(this.structure)).world(world).generationInfo(this.generationInfoID).seed(RCStrings.seed(this.seed)).transform(this.transform).lowerCoord(this.lowerCoord).maturity(StructureSpawnContext.GenerateMaturity.FIRST).asSource(this.generateAsSource);
      this.instanceData.register(generator);
      if (this.structureID != null) {
         generator.structureID(this.structureID);
      }

      return generator;
   }

   public boolean checkDead(ICommandSender target) {
      if (this.instanceData.exists() && this.lowerCoord != null) {
         return false;
      } else {
         String reason = StructureGenerator.GenerationResult.Failure.placement.description;
         target.sendMessage(RecurrentComplex.translations.format("commands.strucGen.failure", reason));
         return true;
      }
   }

   public void writeToNBT(NBTTagCompound compound) {
      compound.setString("structureInfo", StructureSaveHandler.INSTANCE.toJSON(this.structure));
      compound.setTag("structureData", this.structure.worldDataCompound);
      RCAxisAlignedTransform.write(compound, this.transform, "rotation", "mirrorX");
      BlockPositions.writeToNBT("lowerCoord", this.lowerCoord, compound);
      compound.setBoolean("generateAsSource", this.generateAsSource);
      if (this.structureID != null) {
         compound.setString("structureIDForSaving", this.structureID);
      }

      if (this.seed != null) {
         compound.setString("seed", this.seed);
      }

      this.instanceData.writeToNBT("instanceData", compound);
   }

   public void readFromNBT(NBTTagCompound compound) {
      this.structure = StructureSaveHandler.INSTANCE.fromJSON(compound.getString("structureInfo"), compound.getCompoundTag("structureData"));
      this.transform = RCAxisAlignedTransform.read(compound, "rotation", "mirrorX");
      this.lowerCoord = BlockPositions.readFromNBT("lowerCoord", compound);
      this.generateAsSource = compound.getBoolean("generateAsSource");
      this.structureID = compound.hasKey("structureIDForSaving", 8) ? compound.getString("structureIDForSaving") : null;
      this.seed = compound.hasKey("seed") ? compound.getString("seed") : null;
      this.instanceData.readFromNBT("instanceData", compound);
   }

   public void invalidateCache() {
      this.cachedShapeGrid = null;
   }

   @SideOnly(Side.CLIENT)
   public void renderPreview(Operation.PreviewType previewType, World world, int ticks, float partialTicks) {
      if (previewType == Operation.PreviewType.SHAPE) {
         GlStateManager.color(0.8F, 0.75F, 1.0F);
         OperationRenderer.renderGridQuadCache(this.cachedShapeGrid != null ? this.cachedShapeGrid : (this.cachedShapeGrid = BlockQuadCache.createQuadCache(this.structure.constructWorldData().blockCollection, new float[]{1.0F, 1.0F, 1.0F})), this.transform, this.lowerCoord, ticks, partialTicks);
      }

      if (previewType == Operation.PreviewType.BOUNDING_BOX || previewType == Operation.PreviewType.SHAPE) {
         OperationRenderer.renderBoundingBox(this.generationArea(), ticks, partialTicks);
      }

   }

   @Nullable
   public BlockArea generationArea() {
      if (this.structure == null) {
         return null;
      } else {
         int[] size = this.structure.size();
         return OperationRenderer.blockAreaFromSize(this.lowerCoord, RCAxisAlignedTransform.applySize(this.transform, size));
      }
   }
}
