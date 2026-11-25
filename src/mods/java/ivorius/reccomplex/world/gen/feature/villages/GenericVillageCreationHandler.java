package ivorius.reccomplex.world.gen.feature.villages;

import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.utils.RCAxisAlignedTransform;
import ivorius.reccomplex.world.gen.feature.structure.Structure;
import ivorius.reccomplex.world.gen.feature.structure.StructureRegistry;
import ivorius.reccomplex.world.gen.feature.structure.Structures;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.VanillaGeneration;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;

public class GenericVillageCreationHandler implements IVillageCreationHandler {
   protected String structureID;
   protected String generationID;

   public GenericVillageCreationHandler(String structureID, String generationID) {
      this.structureID = structureID;
      this.generationID = generationID;
   }

   public static GenericVillageCreationHandler forGeneration(String structureID, String generationID) {
      return getPieceClass(structureID, generationID) != null ? new GenericVillageCreationHandler(structureID, generationID) : null;
   }

   public static Class<? extends GenericVillagePiece> getPieceClass(String structureID, String generationID) {
      return VanillaGenerationClassFactory.instance().getClass(structureID, generationID);
   }

   public PieceWeight getVillagePieceWeight(EaglercraftRandom random, int villageSize) {
      Structure structure = StructureRegistry.INSTANCE.hasActive(this.structureID) ? (Structure)StructureRegistry.INSTANCE.get(this.structureID) : null;
      if (structure != null) {
         float tweakedWeight = RCConfig.tweakedSpawnRate(this.structureID);
         GenerationType generationType = structure.generationType(this.generationID);
         if (generationType instanceof VanillaGeneration) {
            VanillaGeneration vanillaGenInfo = (VanillaGeneration)generationType;
            int spawnLimit = MathHelper.floor(MathHelper.nextDouble(random, vanillaGenInfo.minBaseLimit + (double)villageSize * vanillaGenInfo.minScaledLimit, vanillaGenInfo.maxBaseLimit + (double)villageSize * vanillaGenInfo.maxScaledLimit) + 0.5D);
            return new PieceWeight(this.getComponentClass(), vanillaGenInfo.getVanillaWeight((double)tweakedWeight), spawnLimit);
         }
      }

      return new PieceWeight(this.getComponentClass(), 0, 0);
   }

   public Class<? extends Village> getComponentClass() {
      return getPieceClass(this.structureID, this.generationID);
   }

   public Village buildComponent(PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces, EaglercraftRandom random, int x, int y, int z, EnumFacing front, int generationDepth) {
      Structure structure = (Structure)StructureRegistry.INSTANCE.get(this.structureID);
      if (structure == null) {
         return this.kill(villagePiece);
      } else {
         GenerationType generationType = structure.generationType(this.generationID);
         if (!(generationType instanceof VanillaGeneration)) {
            return this.kill(villagePiece);
         } else {
            VanillaGeneration vanillaGenInfo = (VanillaGeneration)generationType;
            AxisAlignedTransform2D transform = GenericVillagePiece.getTransform(vanillaGenInfo.front, structure.isMirrorable(), structure.isRotatable(), front.getOpposite(), random);
            if (vanillaGenInfo.generatesIn(startPiece.biome) && transform != null) {
               int[] structureSize = RCAxisAlignedTransform.applySize(transform, structure.size());
               StructureBoundingBox strucBB = Structures.boundingBox(new BlockPos(x, y, z), structureSize);
               if (GenericVillagePiece.canVillageGoDeeperC(strucBB) && StructureComponent.findIntersecting(pieces, strucBB) == null) {
                  GenericVillagePiece genericVillagePiece = GenericVillagePiece.create(this.structureID, this.generationID, startPiece, generationDepth);
                  genericVillagePiece.seed = random.nextLong();
                  if (genericVillagePiece == null) {
                     return this.kill(villagePiece);
                  } else {
                     genericVillagePiece.setIds(this.structureID, this.generationID);
                     genericVillagePiece.setOrientation(front, transform, strucBB);
                     return genericVillagePiece;
                  }
               } else {
                  return null;
               }
            } else {
               return this.kill(villagePiece);
            }
         }
      }
   }

   public Village kill(PieceWeight piece) {
      piece.villagePiecesSpawned = piece.villagePiecesLimit;
      return null;
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         GenericVillageCreationHandler that = (GenericVillageCreationHandler)o;
         return !this.structureID.equals(that.structureID) ? false : this.generationID.equals(that.generationID);
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = this.structureID.hashCode();
      result = 31 * result + this.generationID.hashCode();
      return result;
   }
}
