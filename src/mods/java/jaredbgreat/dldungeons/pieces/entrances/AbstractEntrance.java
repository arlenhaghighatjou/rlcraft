package jaredbgreat.dldungeons.pieces.entrances;

import jaredbgreat.dldungeons.planner.Dungeon;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public abstract class AbstractEntrance {
   protected static final Block LADDER = Block.getBlockFromName("ladder");
   protected static final Block STAIR_SLAB = Block.getBlockFromName("stone_slab");
   public final int x;
   public final int z;

   public AbstractEntrance(int x, int z) {
      this.x = x;
      this.z = z;
   }

   public abstract void build(Dungeon var1, World var2);
}
