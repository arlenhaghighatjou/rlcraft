package lykrast.defiledlands.common.compat.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import lykrast.defiledlands.common.util.CorruptionRecipes;
import mezz.jei.api.IModRegistry;
import net.minecraft.block.state.IBlockState;

public class DefilementMaker {
   public DefilementMaker() {
   }

   public static void registerRecipes(IModRegistry registry) {
      List<DefilementWrapper> list = new ArrayList();

      for(Entry<IBlockState, IBlockState> entry : CorruptionRecipes.getMap().entrySet()) {
         list.add(new DefilementWrapper((IBlockState)entry.getKey(), (IBlockState)entry.getValue()));
      }

      registry.addRecipes(list, "dldefilement");
   }
}
