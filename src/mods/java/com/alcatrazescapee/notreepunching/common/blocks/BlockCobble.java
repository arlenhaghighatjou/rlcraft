package com.alcatrazescapee.notreepunching.common.blocks;

import com.alcatrazescapee.alcatrazcore.block.BlockCore;
import com.alcatrazescapee.alcatrazcore.util.OreDictionaryHelper;
import com.alcatrazescapee.notreepunching.util.types.Stone;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public class BlockCobble extends BlockCore {
   private static final BiMap<Stone, Block> MAP = HashBiMap.create();

   public static Block get(Stone type) {
      return (Block)MAP.get(type);
   }

   @Nullable
   public static Stone get(Block block) {
      return (Stone)MAP.inverse().get(block);
   }

   public BlockCobble(Stone type) {
      super(Material.ROCK);
      MAP.put(type, this);
      this.setHardness(2.5F);
      this.setHarvestLevel("pickaxe", 0);
      OreDictionaryHelper.register(this, new Object[]{"cobblestone"});
      OreDictionaryHelper.register(this, new Object[]{"cobblestone", type.name()});
   }

   static {
      MAP.put(Stone.STONE, Blocks.COBBLESTONE);
   }
}
