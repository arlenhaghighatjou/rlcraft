package jaredbgreat.dldungeons.pieces.chests;

import jaredbgreat.dldungeons.builder.AbstractBlock;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WeakChest extends BasicChest {
   public WeakChest(int x, int y, int z, LootCategory category) {
      super(x, y, z, 0, category);
   }

   public void place(World world, int x, int y, int z, EaglercraftRandom random) {
      BlockPos pos = new BlockPos(x, y, z);
      TileEntityChest contents = (TileEntityChest)world.getTileEntity(pos);
      if (world.getBlockState(pos).getBlock() != AbstractBlock.chest) {
         System.err.println("[DLDUNGEONS] ERROR! Trying to put loot into non-chest at " + x + ", " + y + ", " + z + " (basic chest).");
      } else {
         if (random.nextBoolean()) {
            if (random.nextBoolean()) {
               this.fillChest(contents, LootType.GEAR, random);
            } else {
               this.fillChest(contents, LootType.HEAL, random);
            }
         } else {
            this.fillChest(contents, LootType.GEAR, random);
            this.fillChest(contents, LootType.HEAL, random);
         }

      }
   }
}
