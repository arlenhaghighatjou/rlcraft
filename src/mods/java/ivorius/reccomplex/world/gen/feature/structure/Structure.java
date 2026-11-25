package ivorius.reccomplex.world.gen.feature.structure;

import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.reccomplex.nbt.NBTStorable;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureLoadContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructurePrepareContext;
import ivorius.reccomplex.world.gen.feature.structure.context.StructureSpawnContext;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericStructure;
import ivorius.reccomplex.world.gen.feature.structure.generic.GenericVariableDomain;
import ivorius.reccomplex.world.gen.feature.structure.generic.generation.GenerationType;
import ivorius.reccomplex.world.gen.feature.structure.generic.transformers.TransformerMulti;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.text.TextComponentBase;

public interface Structure<S extends NBTStorable> {
   void generate(@Nonnull StructureSpawnContext var1, @Nonnull S var2, @Nonnull TransformerMulti var3);

   @Nullable
   S prepareInstanceData(@Nonnull StructurePrepareContext var1, @Nonnull TransformerMulti var2);

   @Nonnull
   S loadInstanceData(@Nonnull StructureLoadContext var1, @Nonnull NBTBase var2, @Nonnull TransformerMulti var3);

   @Nonnull
   <I extends GenerationType> List<I> generationTypes(@Nonnull Class<? extends I> var1);

   GenerationType generationType(@Nonnull String var1);

   @Nonnull
   int[] size();

   boolean isRotatable();

   boolean isMirrorable();

   boolean isBlocking();

   @Nullable
   GenericStructure copyAsGenericStructure();

   boolean areDependenciesResolved();

   @Nullable
   IvBlockCollection blockCollection();

   @Nonnull
   GenericVariableDomain declaredVariables();

   default List<TextComponentBase> instanceDataInfo(S s) {
      return Collections.emptyList();
   }
}
