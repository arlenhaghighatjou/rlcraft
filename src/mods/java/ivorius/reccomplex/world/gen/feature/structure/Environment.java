package ivorius.reccomplex.world.gen.feature.structure;

import ivorius.reccomplex.utils.RCStructureBoundingBoxes;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.StructureBoundingBox;

public class Environment {
   @Nonnull
   public final WorldServer world;
   @Nonnull
   public final Biome biome;
   @Nullable
   public final Integer villageType;
   @Nullable
   public final GenerationType generationType;
   public VariableDomain variables = new VariableDomain();

   public Environment(@Nonnull WorldServer world, @Nonnull Biome biome, @Nullable Integer villageType, @Nullable GenerationType generationType) {
      this.world = world;
      this.biome = biome;
      this.villageType = villageType;
      this.generationType = generationType;
   }

   @Nonnull
   public static Environment inNature(@Nonnull WorldServer world, @Nonnull StructureBoundingBox boundingBox, GenerationType generationType) {
      return new Environment(world, getBiome(world, boundingBox), (Integer)null, generationType);
   }

   @Nonnull
   public static Environment inNature(@Nonnull WorldServer world, @Nonnull StructureBoundingBox boundingBox) {
      return inNature(world, boundingBox, (GenerationType)null);
   }

   public static Biome getBiome(World world, StructureBoundingBox boundingBox) {
      return world.getBiome(RCStructureBoundingBoxes.getCenter(boundingBox));
   }

   public Environment withGeneration(GenerationType generation) {
      return new Environment(this.world, this.biome, this.villageType, generation);
   }

   public Environment child() {
      return new Environment(this.world, this.biome, this.villageType, this.generationType);
   }

   public Environment copy(VariableDomain domain) {
      Environment copy = this.child();
      if (domain != null) {
         domain.fill(copy.variables);
      }

      return copy;
   }
}
