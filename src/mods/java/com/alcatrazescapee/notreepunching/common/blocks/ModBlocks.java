package com.alcatrazescapee.notreepunching.common.blocks;

import com.alcatrazescapee.alcatrazcore.util.CoreHelpers;
import com.alcatrazescapee.alcatrazcore.util.OreDictionaryHelper;
import com.alcatrazescapee.alcatrazcore.util.RegistryHelper;
import com.alcatrazescapee.notreepunching.client.ModTabs;
import com.alcatrazescapee.notreepunching.common.tile.TileFirePit;
import com.alcatrazescapee.notreepunching.common.tile.TileLargeVessel;
import com.alcatrazescapee.notreepunching.util.types.Pottery;
import com.alcatrazescapee.notreepunching.util.types.Stone;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder("notreepunching")
public final class ModBlocks {
   public static final Block CERAMIC_LARGE_VESSEL = (Block)CoreHelpers.getNull();
   public static final Block FIRE_PIT = (Block)CoreHelpers.getNull();

   public ModBlocks() {
   }

   public static void preInit() {
      RegistryHelper r = RegistryHelper.get("notreepunching");

      for(Stone type : Stone.values()) {
         if (type.isEnabled()) {
            if (type.hasCobblestone()) {
               r.registerBlock(new BlockCobble(type), "cobblestone/" + type.name(), ModTabs.TAB_ITEMS);
            }

            r.registerBlock(new BlockRock(type), (Function)null, "loose_rock/" + type.name());
         }
      }

      for(Pottery type : Pottery.values()) {
         Block block;
         r.registerBlock(block = new BlockPottery(type), type == Pottery.WORKED ? null : ItemBlock::new, "pottery/" + type.name(), ModTabs.TAB_ITEMS);
         if (type != Pottery.WORKED) {
            OreDictionaryHelper.register(block, new Object[]{"clay", type.name()});
         }
      }

      r.registerBlock(new BlockLargeVessel(), "ceramic_large_vessel", ModTabs.TAB_ITEMS);
      r.registerBlock(new BlockFirePit(), (Function)null, "fire_pit");
      r.registerTile(TileLargeVessel.class, "ceramic_large_vessel");
      r.registerTile(TileFirePit.class, "fire_pit");
   }
}
