package ivorius.reccomplex.block;

import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.RunTransformer;
import net.minecraft.nbt.NBTBase;

public interface GeneratingTileEntity<S> {
   S prepareInstanceData(StructurePrepareContext var1);

   S loadInstanceData(StructureLoadContext var1, NBTBase var2);

   void generate(StructureSpawnContext var1, RunTransformer var2, S var3);

   default boolean shouldPlaceInWorld(StructurePrepareContext context, S instanceData) {
      return false;
   }
}
