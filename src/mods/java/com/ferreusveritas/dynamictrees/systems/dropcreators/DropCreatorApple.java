package com.ferreusveritas.dynamictrees.systems.dropcreators;

import com.ferreusveritas.dynamictrees.trees.Species;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class DropCreatorApple extends DropCreator {
   public static final DropCreatorApple instance = new DropCreatorApple();

   public DropCreatorApple() {
      super(new ResourceLocation("dynamictrees", "apple"));
   }

   public List<ItemStack> getLeavesDrop(IBlockAccess access, Species species, BlockPos breakPos, Random random, List<ItemStack> dropList, int fortune) {
      int chance = 200;
      if (fortune > 0) {
         chance -= 10 << fortune;
         if (chance < 40) {
            chance = 40;
         }
      }

      if (random.nextInt(chance) == 0) {
         dropList.add(new ItemStack(Items.APPLE));
      }

      return dropList;
   }
}
