package com.github.alexthe666.iceandfire.world.village;

import java.util.List;
import java.util.Random;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;

public class VillageAnimalFarmCreator implements IVillageCreationHandler {
   public VillageAnimalFarmCreator() {
   }

   public PieceWeight getVillagePieceWeight(Random random, int i) {
      return new PieceWeight(ComponentAnimalFarm.class, 50, i + random.nextInt(1));
   }

   public Class<?> getComponentClass() {
      return ComponentAnimalFarm.class;
   }

   public Village buildComponent(PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
      return ComponentAnimalFarm.buildComponent(startPiece, pieces, random, p1, p2, p3, facing, p5);
   }
}
