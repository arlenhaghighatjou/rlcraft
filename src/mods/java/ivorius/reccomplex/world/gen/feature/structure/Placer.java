package ivorius.reccomplex.world.gen.feature.structure;

import ivorius.ivtoolkit.blocks.IvBlockCollection;
import ivorius.reccomplex.world.gen.feature.structure.generic.placement.StructurePlaceContext;

public interface Placer {
   int DONT_GENERATE = -1;

   int place(StructurePlaceContext var1, IvBlockCollection var2);
}
