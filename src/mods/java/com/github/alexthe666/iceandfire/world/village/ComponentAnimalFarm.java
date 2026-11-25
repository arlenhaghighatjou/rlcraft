package com.github.alexthe666.iceandfire.world.village;

import com.github.alexthe666.iceandfire.world.WorldGenAnimalFarm;
import java.util.List;
import java.util.Random;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;

public class ComponentAnimalFarm extends Village {
   private int averageGroundLevel = -1;

   public ComponentAnimalFarm() {
   }

   public ComponentAnimalFarm(Start startPiece, int p2, Random random, StructureBoundingBox structureBox, EnumFacing facing) {
      this.setCoordBaseMode(facing);
      this.boundingBox = structureBox;
   }

   public static ComponentAnimalFarm buildComponent(Start startPiece, List<StructureComponent> pieces, Random random, int x, int y, int z, EnumFacing facing, int p5) {
      StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(x, y, z, 0, 0, 0, 8, 7, 8, facing);
      return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null ? new ComponentAnimalFarm(startPiece, p5, random, structureboundingbox, facing) : null;
   }

   public boolean addComponentParts(World world, Random random, StructureBoundingBox sbb) {
      if (this.averageGroundLevel < 0) {
         this.averageGroundLevel = this.getAverageGroundLevel(world, sbb);
         if (this.averageGroundLevel < 0) {
            return false;
         }

         this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 4, 0);
      }

      BlockPos blockpos = new BlockPos(this.getXWithOffset(4, 4), this.getYWithOffset(0), this.getZWithOffset(4, 4));
      return (new WorldGenAnimalFarm()).generate(world, random, blockpos.up());
   }
}
