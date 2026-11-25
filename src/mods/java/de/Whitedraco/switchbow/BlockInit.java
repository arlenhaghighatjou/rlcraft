package de.Whitedraco.switchbow;

import de.Whitedraco.switchbow.block.BlockArrowDispenser;
import de.Whitedraco.switchbow.block.BlockMiniLight;
import de.Whitedraco.switchbow.block.BlockPodest;
import de.Whitedraco.switchbow.block.BlockRedstonePiece;
import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent.Register;

public class BlockInit {
   public static Block RedstonePiece;
   public static Block ArrowDispenser;
   public static Block Podest;
   public static Block MiniLight;
   public static ArrayList<Block> blockList = new ArrayList();

   public BlockInit() {
   }

   public static void prepareBlocks() {
      RedstonePiece = new BlockRedstonePiece("RedstonePiece");
      blockList.add(RedstonePiece);
      ArrowDispenser = new BlockArrowDispenser("ArrowDispenser");
      ItemInit.addItemBlockToItems(ArrowDispenser);
      blockList.add(ArrowDispenser);
      Podest = new BlockPodest("Podest");
      ItemInit.addItemBlockToItems(Podest);
      blockList.add(Podest);
      MiniLight = new BlockMiniLight("MiniLight");
      blockList.add(MiniLight);
   }

   public static void registerBlocks(Register<Block> event) {
      for(Block block : blockList) {
         event.getRegistry().register(block);
      }

   }
}
