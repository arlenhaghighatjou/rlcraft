package net.blay09.mods.waystones.worldgen;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.blay09.mods.waystones.WaystoneConfig;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;

public class VillageWaystoneCreationHandler implements IVillageCreationHandler {
   public VillageWaystoneCreationHandler() {
   }

   public PieceWeight getVillagePieceWeight(Random random, int size) {
      return random.nextFloat() > WaystoneConfig.worldGen.villageChance ? new PieceWeight(this.getComponentClass(), 0, 0) : new PieceWeight(ComponentVillageWaystone.class, 3, 1);
   }

   public Class<? extends Village> getComponentClass() {
      return ComponentVillageWaystone.class;
   }

   @Nullable
   public Village buildComponent(PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces, Random random, int x, int y, int z, EnumFacing facing, int type) {
      return ComponentVillageWaystone.buildComponent(villagePiece, startPiece, pieces, random, x, y, z, facing, type);
   }
}
