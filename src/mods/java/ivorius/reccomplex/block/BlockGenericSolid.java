package ivorius.reccomplex.block;

import ivorius.reccomplex.block.materials.RCMaterials;
import ivorius.reccomplex.utils.UnstableBlock;

public class BlockGenericSolid extends BlockTyped implements UnstableBlock {
   public BlockGenericSolid() {
      super(RCMaterials.materialGenericSolid);
   }
}
