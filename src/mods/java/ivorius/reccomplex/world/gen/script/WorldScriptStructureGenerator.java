package ivorius.reccomplex.world.gen.script;

import ivorius.ivtoolkit.blocks.BlockPositions;
import ivorius.ivtoolkit.blocks.Directions;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.random.WeightedSelector;
import ivorius.ivtoolkit.tools.IvCollections;
import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.gui.table.TableDelegate;
import ivorius.reccomplex.gui.table.TableNavigator;
import ivorius.reccomplex.gui.table.datasource.TableDataSource;
import ivorius.reccomplex.gui.worldscripts.structuregenerator.TableDataSourceWorldScriptStructureGenerator;
import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.utils.RCAxisAlignedTransform;
import ivorius.reccomplex.world.gen.feature.StructureGenerator;
import ivorius.reccomplex.world.gen.feature.structure.ReadableInstanceData;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.ListGeneration;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.RunTransformer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

public class WorldScriptStructureGenerator implements WorldScript<WorldScriptStructureGenerator.InstanceData> {
   protected boolean simpleMode;
   protected BlockPos structureShift = BlockPos.ORIGIN;
   protected String structureListID = "";
   protected EnumFacing front;
   protected List<String> structureNames = new ArrayList();
   protected Integer structureRotation;
   protected Boolean structureMirror;

   public WorldScriptStructureGenerator() {
   }

   public boolean isSimpleMode() {
      return this.simpleMode;
   }

   public void setSimpleMode(boolean simpleMode) {
      this.simpleMode = simpleMode;
   }

   public BlockPos getStructureShift() {
      return this.structureShift;
   }

   public void setStructureShift(BlockPos structureShift) {
      this.structureShift = structureShift;
   }

   public String getStructureListID() {
      return this.structureListID;
   }

   public void setStructureListID(String structureListID) {
      this.structureListID = structureListID;
   }

   public EnumFacing getFront() {
      return this.front;
   }

   public void setFront(EnumFacing front) {
      this.front = front;
   }

   public List<String> getStructureNames() {
      return Collections.unmodifiableList(this.structureNames);
   }

   public void setStructureNames(List<String> structureNames) {
      IvCollections.setContentsOfList(this.structureNames, structureNames);
   }

   public Integer getStructureRotation() {
      return this.structureRotation;
   }

   public void setStructureRotation(Integer structureRotation) {
      this.structureRotation = structureRotation;
   }

   public Boolean getStructureMirror() {
      return this.structureMirror;
   }

   public void setStructureMirror(Boolean structureMirror) {
      this.structureMirror = structureMirror;
   }

   public void readFromNBT(NBTTagCompound nbtTagCompound) {
      this.structureShift = BlockPositions.readFromNBT("structureShift", nbtTagCompound);
      this.simpleMode = !nbtTagCompound.hasKey("simpleMode", 1) || nbtTagCompound.getBoolean("simpleMode");
      this.structureListID = nbtTagCompound.getString("structureListID");
      this.front = nbtTagCompound.hasKey("front", 8) ? Directions.deserialize(nbtTagCompound.getString("front")) : null;
      List<String> structureList = new ArrayList();
      NBTTagList structureNBTList = nbtTagCompound.getTagList("structures", 8);

      for(int i = 0; i < structureNBTList.tagCount(); ++i) {
         structureList.add(structureNBTList.getStringTagAt(i));
      }

      IvCollections.setContentsOfList(this.structureNames, structureList);
      this.structureRotation = nbtTagCompound.hasKey("structureRotation") ? nbtTagCompound.getInteger("structureRotation") : null;
      this.structureMirror = nbtTagCompound.hasKey("structureMirror") ? nbtTagCompound.getBoolean("structureMirror") : null;
   }

   public void writeToNBT(NBTTagCompound nbtTagCompound) {
      BlockPositions.writeToNBT("structureShift", this.structureShift, nbtTagCompound);
      nbtTagCompound.setBoolean("simpleMode", this.simpleMode);
      nbtTagCompound.setString("structureListID", this.structureListID);
      if (this.front != null) {
         nbtTagCompound.setString("front", Directions.serialize(this.front));
      }

      NBTTagList structureNBTList = new NBTTagList();

      for(String struc : this.structureNames) {
         structureNBTList.appendTag(new NBTTagString(struc));
      }

      nbtTagCompound.setTag("structures", structureNBTList);
      if (this.structureRotation != null) {
         nbtTagCompound.setInteger("structureRotation", this.structureRotation.intValue());
      }

      if (this.structureMirror != null) {
         nbtTagCompound.setBoolean("structureMirror", this.structureMirror.booleanValue());
      }

   }

   public WorldScriptStructureGenerator.InstanceData loadInstanceData(StructureLoadContext context, NBTBase nbt) {
      return new WorldScriptStructureGenerator.InstanceData(nbt instanceof NBTTagCompound ? (NBTTagCompound)nbt : new NBTTagCompound());
   }

   public WorldScriptStructureGenerator.InstanceData prepareInstanceData(StructurePrepareContext context, BlockPos pos) {
      WorldScriptStructureGenerator.InstanceData instanceData = null;
      EaglercraftRandom random = context.random;
      AxisAlignedTransform2D transform = context.transform;
      if (this.simpleMode) {
         if (this.structureNames.size() > 0) {
            String structureID = (String)this.structureNames.get(random.nextInt(this.structureNames.size()));
            Structure<?> structure = (Structure)StructureRegistry.INSTANCE.get(structureID);
            if (structure != null) {
               int rotations;
               boolean var10000;
               label76: {
                  label75: {
                     rotations = structure.isRotatable() ? (this.structureRotation != null ? transform.getRotation() + this.structureRotation.intValue() : random.nextInt(4)) : 0;
                     if (structure.isMirrorable()) {
                        if (this.structureMirror != null) {
                           if (transform.isMirrorX() != this.structureMirror.booleanValue()) {
                              break label75;
                           }
                        } else if (random.nextBoolean()) {
                           break label75;
                        }
                     }

                     var10000 = false;
                     break label76;
                  }

                  var10000 = true;
               }

               boolean mirrorX = var10000;
               AxisAlignedTransform2D strucTransform = AxisAlignedTransform2D.from(rotations, mirrorX);
               int[] strucSize = structure.size();
               BlockPos strucCoord = transform.apply(this.structureShift, new int[]{1, 1, 1}).subtract(transform.apply(BlockPos.ORIGIN, strucSize)).add(pos);
               instanceData = new WorldScriptStructureGenerator.InstanceData(structureID, (String)null, strucCoord, strucTransform, (NBTStorable)(new StructureGenerator(structure)).seed(Long.valueOf(random.nextLong())).environment(context.environment).transform(strucTransform).lowerCoord(strucCoord).asSource(context.generateAsSource).instanceData().orElse((Object)null));
            }
         }
      } else {
         Collection<Pair<Structure<?>, ListGeneration>> generationInfos = (Collection)ListGeneration.structures(StructureRegistry.INSTANCE, this.structureListID, this.front).collect(Collectors.toList());
         if (generationInfos.size() > 0) {
            Pair<Structure<?>, ListGeneration> pair = (Pair)WeightedSelector.select(random, generationInfos, (item) -> {
               return (double)RCConfig.tweakedSpawnRate(StructureRegistry.INSTANCE.id(item.getLeft())) * ((ListGeneration)item.getRight()).getWeight();
            });
            Structure<?> structure = (Structure)pair.getLeft();
            String structureID = StructureRegistry.INSTANCE.id(structure);
            ListGeneration generationInfo = (ListGeneration)pair.getRight();
            boolean mirrorX;
            int rotations;
            if (this.front != null) {
               EnumFacing curFront = Directions.rotate(this.front, transform);
               mirrorX = structure.isMirrorable() && structure.isRotatable() && random.nextBoolean();
               Integer neededRotations = Directions.getHorizontalClockwiseRotations(generationInfo.front, curFront, mirrorX);
               rotations = neededRotations != null ? neededRotations.intValue() : 0;
            } else {
               mirrorX = structure.isMirrorable() && random.nextBoolean();
               rotations = structure.isRotatable() ? random.nextInt(4) : 0;
            }

            AxisAlignedTransform2D strucTransform = AxisAlignedTransform2D.from(rotations, mirrorX);
            int[] strucSize = structure.size();
            BlockPos strucCoord = transform.apply(this.structureShift.add(generationInfo.shift), new int[]{1, 1, 1}).subtract(transform.apply(BlockPos.ORIGIN, strucSize)).add(pos);
            instanceData = new WorldScriptStructureGenerator.InstanceData(structureID, generationInfo.id(), strucCoord, strucTransform, (NBTStorable)(new StructureGenerator(structure)).seed(Long.valueOf(random.nextLong())).environment(context.environment).transform(strucTransform).asSource(context.generateAsSource).lowerCoord(strucCoord).instanceData().orElse((Object)null));
         }
      }

      return instanceData != null ? instanceData : new WorldScriptStructureGenerator.InstanceData();
   }

   public void generate(StructureSpawnContext context, RunTransformer transformer, WorldScriptStructureGenerator.InstanceData instanceData, BlockPos pos) {
      this.generate_(context, instanceData);
   }

   protected <T extends NBTStorable> void generate_(StructureSpawnContext context, WorldScriptStructureGenerator.InstanceData instanceData) {
      Structure<T> structure = (Structure)StructureRegistry.INSTANCE.get(instanceData.structureID);
      ReadableInstanceData<T> structureData = instanceData.structureData;
      if (structure != null && structureData.exists()) {
         this.generate(context, instanceData, structure, structureData, instanceData.generationInfoID);
      }
   }

   @Nonnull
   protected <T extends NBTStorable> StructureGenerator.GenerationResult generate(StructureSpawnContext context, WorldScriptStructureGenerator.InstanceData instanceData, Structure<T> structure, ReadableInstanceData<T> structureData, String generationInfo) {
      StructureGenerator<T> generator = (new StructureGenerator(structure)).structureID(instanceData.structureID).asChild(context).generationInfo(generationInfo).lowerCoord(instanceData.lowerCoord).transform(instanceData.structureTransform);
      structureData.load(generator);
      return generator.generate();
   }

   public String getDisplayString() {
      return IvTranslations.get("reccomplex.worldscript.strucGen");
   }

   @SideOnly(Side.CLIENT)
   public TableDataSource tableDataSource(BlockPos realWorldPos, TableNavigator navigator, TableDelegate tableDelegate) {
      return new TableDataSourceWorldScriptStructureGenerator(this, navigator, tableDelegate);
   }

   public static class InstanceData implements NBTStorable {
      public String structureID = "";
      public String generationInfoID;
      public BlockPos lowerCoord;
      public AxisAlignedTransform2D structureTransform = AxisAlignedTransform2D.ORIGINAL;
      public final ReadableInstanceData<?> structureData = new ReadableInstanceData();

      public InstanceData() {
      }

      public InstanceData(String structureID, String generationInfoID, BlockPos lowerCoord, AxisAlignedTransform2D structureTransform, NBTStorable structureData) {
         this.structureID = structureID;
         this.generationInfoID = generationInfoID;
         this.lowerCoord = lowerCoord;
         this.structureTransform = structureTransform;
         this.structureData.setInstanceData(structureData);
      }

      public InstanceData(NBTTagCompound compound) {
         this.structureID = compound.getString("structureID");
         this.generationInfoID = compound.hasKey(this.generationInfoID, 8) ? compound.getString("generationInfoID") : null;
         this.lowerCoord = BlockPositions.readFromNBT("lowerCoord", compound);
         this.structureTransform = RCAxisAlignedTransform.read(compound, "rotation", "mirrorX");
         this.structureData.readFromNBT("structureData", compound);
      }

      public NBTBase writeToNBT() {
         NBTTagCompound compound = new NBTTagCompound();
         compound.setString("structureID", this.structureID);
         if (this.generationInfoID != null) {
            compound.setString("generationInfoID", this.generationInfoID);
         }

         BlockPositions.writeToNBT("lowerCoord", this.lowerCoord, compound);
         RCAxisAlignedTransform.write(compound, this.structureTransform, "rotation", "mirrorX");
         this.structureData.writeToNBT("structureData", compound);
         return compound;
      }
   }
}
