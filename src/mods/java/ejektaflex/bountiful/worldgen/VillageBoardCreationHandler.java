package ejektaflex.bountiful.worldgen;

import ejektaflex.bountiful.Bountiful;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002Jb\u0010\u0003\u001a\u0004\u0018\u00010\u00042\b\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\b2\u000e\u0010\t\u001a\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\n2\b\u0010\f\u001a\u0004\u0018\u00010\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\u0011\u001a\u00020\u000f2\b\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0014\u001a\u00020\u000fH\u0016J\u000e\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00170\u0016H\u0016J\u0018\u0010\u0018\u001a\u00020\u00062\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u0019\u001a\u00020\u000fH\u0016¨\u0006\u001a"},
   d2 = {"Lejektaflex/bountiful/worldgen/VillageBoardCreationHandler;", "Lnet/minecraftforge/fml/common/registry/VillagerRegistry$IVillageCreationHandler;", "()V", "buildComponent", "Lnet/minecraft/world/gen/structure/StructureVillagePieces$Village;", "villagePiece", "Lnet/minecraft/world/gen/structure/StructureVillagePieces$PieceWeight;", "startPiece", "Lnet/minecraft/world/gen/structure/StructureVillagePieces$Start;", "pieces", "", "Lnet/minecraft/world/gen/structure/StructureComponent;", "random", "Ljava/util/EaglercraftRandom;", "x", "", "y", "z", "facing", "Lnet/minecraft/util/EnumFacing;", "type", "getComponentClass", "Ljava/lang/Class;", "Lejektaflex/bountiful/worldgen/VillageBoardComponent;", "getVillagePieceWeight", "size", "Bountiful"}
)
public final class VillageBoardCreationHandler implements IVillageCreationHandler {
   @Nullable
   public Village buildComponent(@Nullable PieceWeight villagePiece, @Nullable Start startPiece, @Nullable List<StructureComponent> pieces, @Nullable EaglercraftRandom random, int x, int y, int z, @Nullable EnumFacing facing, int type) {
      return VillageBoardComponent.Companion.buildComponent(villagePiece, startPiece, pieces, random, x, y, z, facing, type);
   }

   @NotNull
   public Class<VillageBoardComponent> getComponentClass() {
      return VillageBoardComponent.class;
   }

   @NotNull
   public PieceWeight getVillagePieceWeight(@NotNull EaglercraftRandom random, int size) {
      Intrinsics.checkParameterIsNotNull(random, "random");
      return random.nextFloat() <= (float)Bountiful.INSTANCE.getConfig().getVillageGenerationWeight() && Bountiful.INSTANCE.getConfig().getVillageGeneration() ? new PieceWeight(VillageBoardComponent.class, 3, 1) : new PieceWeight(VillageBoardComponent.class, 0, 0);
   }

   public VillageBoardCreationHandler() {
   }
}
