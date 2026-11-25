package com.ferreusveritas.dynamictrees.systems.dropcreators;

import com.ferreusveritas.dynamictrees.trees.Species;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DropCreatorLogs extends DropCreator {
   public DropCreatorLogs() {
      super(new ResourceLocation("dynamictrees", "logs"));
   }

   public List<ItemStack> getLogsDrop(World world, Species species, BlockPos breakPos, EaglercraftRandom random, List<ItemStack> dropList, float volume) {
      Species.LogsAndSticks las = species.getLogsAndSticks(volume);

      for(int numLogs = las.logs; numLogs > 0; numLogs -= 64) {
         dropList.add(species.getFamily().getPrimitiveLogItemStack(numLogs >= 64 ? 64 : numLogs));
      }

      int numSticks = las.sticks;
      if (numSticks > 0) {
         dropList.add(species.getFamily().getStick(numSticks));
      }

      return dropList;
   }
}
