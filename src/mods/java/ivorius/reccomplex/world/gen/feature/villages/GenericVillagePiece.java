package ivorius.reccomplex.world.gen.feature.villages;

import ivorius.ivtoolkit.blocks.Directions;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.ivtoolkit.world.chunk.gen.StructureBoundingBoxes;
import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.utils.RCAxisAlignedTransform;
import ivorius.reccomplex.world.gen.feature.StructureGenerator;
import ivorius.reccomplex.world.gen.feature.WorldStructureGenerationData;
import ivorius.reccomplex.world.gen.feature.structure.Environment;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.VanillaGeneration;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraft.world.gen.structure.template.TemplateManager;

public class GenericVillagePiece extends Village {
   public String structureID;
   public String generationID;
   public AxisAlignedTransform2D transform;
   public long seed;
   public boolean startedGeneration;
   public NBTBase instanceData;

   public GenericVillagePiece() {
   }

   public GenericVillagePiece(Start start, int generationDepth) {
      super(start, generationDepth);
   }

   public static AxisAlignedTransform2D getTransform(@Nonnull EnumFacing front, boolean mirrorX, @Nonnull EnumFacing toFront) {
      return AxisAlignedTransform2D.from(getRotations(front, mirrorX, toFront), mirrorX);
   }

   @Nullable
   public static AxisAlignedTransform2D getTransform(@Nullable EnumFacing front, boolean canMirror, boolean canRotate, EnumFacing toFront, EaglercraftRandom random) {
      boolean mirror = canMirror && random.nextBoolean();
      AxisAlignedTransform2D transform = front != null ? getTransform(front, mirror, toFront) : AxisAlignedTransform2D.from(canRotate ? random.nextInt(4) : 0, mirror);
      return !canRotate && transform.getRotation() != 0 ? null : transform;
   }

   public static int getRotations(EnumFacing front, boolean mirrorX, EnumFacing toFront) {
      Integer rotations = Directions.getHorizontalClockwiseRotations(front, toFront, mirrorX);
      return rotations == null ? 0 : rotations.intValue();
   }

   @Nullable
   public static GenericVillagePiece create(String structureID, String generationID) {
      return VanillaGenerationClassFactory.instance().create(structureID, generationID);
   }

   @Nullable
   public static GenericVillagePiece create(String structureID, String generationID, Start start, int generationDepth) {
      return VanillaGenerationClassFactory.instance().create(structureID, generationID, start, generationDepth);
   }

   public static boolean canVillageGoDeeperC(StructureBoundingBox box) {
      return canVillageGoDeeper(box);
   }

   public void setIds(String structureID, String generationID) {
      this.structureID = structureID;
      this.generationID = generationID;
   }

   public void setOrientation(EnumFacing front, AxisAlignedTransform2D transform, StructureBoundingBox boundingBox) {
      this.setCoordBaseMode(front);
      this.transform = transform;
      this.boundingBox = boundingBox;
   }

   @Nonnull
   private Biome biome(WorldServer world) {
      return this.startPiece != null ? this.startPiece.biome : Environment.getBiome(world, this.boundingBox);
   }

   @Nonnull
   protected Environment environment(WorldServer world, GenerationType generationType) {
      return new Environment(world, this.biome(world), this.structureType, generationType);
   }

   public void prepare(WorldServer world) {
      Structure<?> structure = (Structure)StructureRegistry.INSTANCE.get(this.structureID);
      if (structure != null) {
         GenerationType generationType = structure.generationType(this.generationID);
         if (generationType instanceof VanillaGeneration) {
            VanillaGeneration vanillaGenInfo = (VanillaGeneration)generationType;
            this.instanceData = (NBTBase)(new StructureGenerator(structure)).seed(Long.valueOf(this.seed)).environment(this.environment(world, generationType)).transform(this.transform).boundingBox(this.boundingBox).instanceData().map(NBTStorable::writeToNBT).orElse((Object)null);
         }
      }
   }

   @ParametersAreNonnullByDefault
   public boolean addComponentParts(World world, EaglercraftRandom random, StructureBoundingBox boundingBox) {
      Structure<?> structure = (Structure)StructureRegistry.INSTANCE.get(this.structureID);
      if (structure == null) {
         return false;
      } else {
         GenerationType generationType = structure.generationType(this.generationID);
         if (!(generationType instanceof VanillaGeneration)) {
            return false;
         } else {
            VanillaGeneration vanillaGenInfo = (VanillaGeneration)generationType;
            BlockPos structureShift = this.transform.apply(vanillaGenInfo.spawnShift, new int[]{1, 1, 1});
            if (this.averageGroundLvl < 0) {
               this.averageGroundLvl = this.getAverageGroundLevel(world, boundingBox);
               if (this.averageGroundLvl < 0) {
                  return true;
               }

               this.boundingBox.offset(0, this.averageGroundLvl - this.boundingBox.minY + structureShift.getY(), 0);
            }

            if (world instanceof WorldServer) {
               this.generate((WorldServer)world, boundingBox, structure, generationType);
            }

            return true;
         }
      }
   }

   protected <T extends NBTStorable> void generate(WorldServer world, StructureBoundingBox generationBB, Structure<T> structure, GenerationType generationType) {
      if (!this.startedGeneration) {
         this.prepare(world);
      }

      boolean firstTime = !this.startedGeneration;
      StructureGenerator.GenerationResult result = (new StructureGenerator(structure)).environment(this.environment(world, generationType)).seed(Long.valueOf(this.seed)).lowerCoord(StructureBoundingBoxes.min(this.boundingBox)).transform(this.transform).generationBB(StructureBoundingBoxes.wholeHeightBoundingBox(world, generationBB)).generationLayer(this.componentType).structureID(this.structureID).maturity(firstTime ? StructureSpawnContext.GenerateMaturity.FIRST : StructureSpawnContext.GenerateMaturity.COMPLEMENT).instanceData(this.instanceData).generate();
      if (result instanceof StructureGenerator.GenerationResult.Success.New) {
         WorldStructureGenerationData.StructureEntry sight = ((StructureGenerator.GenerationResult.Success.New)result).sight;
         sight.setPreventComplementation(true);
      }

      this.startedGeneration = true;
   }

   protected void writeStructureToNBT(NBTTagCompound compound) {
      super.writeStructureToNBT(compound);
      compound.setString("RcSId", this.structureID);
      compound.setString("RcGtId", this.generationID);
      RCAxisAlignedTransform.write(compound, this.transform, "RcRotation", "RcMirror");
      compound.setLong("seed", this.seed);
      compound.setBoolean("RcStartGen", this.startedGeneration);
      if (this.instanceData != null) {
         compound.setTag("RcInstDat", this.instanceData);
      }

   }

   protected void readStructureFromNBT(NBTTagCompound compound, TemplateManager manager) {
      super.readStructureFromNBT(compound, manager);
      this.structureID = compound.getString("RcSId");
      this.generationID = compound.getString("RcGtId");
      this.seed = compound.hasKey("seed") ? compound.getLong("seed") : (new EaglercraftRandom()).nextLong();
      this.transform = RCAxisAlignedTransform.read(compound, "RcRotation", "RcMirror");
      this.startedGeneration = compound.getBoolean("RcStartGen");
      this.instanceData = compound.hasKey("RcInstDat") ? compound.getTag("RcInstDat") : null;
   }
}
