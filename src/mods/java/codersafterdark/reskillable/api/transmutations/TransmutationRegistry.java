package codersafterdark.reskillable.api.transmutations;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.block.BlockSponge;
import net.minecraft.block.BlockPrismarine.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;

public class TransmutationRegistry {
   private static Map<Item, Map<IBlockState, IBlockState>> reagentStateMap = new HashMap();

   public TransmutationRegistry() {
   }

   public static Map<Item, Map<IBlockState, IBlockState>> getReagentStateMap() {
      return reagentStateMap;
   }

   public static Map<IBlockState, IBlockState> getStateMapByReagent(Item item) {
      return (Map)reagentStateMap.get(item);
   }

   public static void initDefaultMap() {
      Item item = Items.CHORUS_FRUIT;
      addEntryToReagentByBlockDefaultState(item, Blocks.MELON_BLOCK, Blocks.PUMPKIN);
      addEntryToReagentByBlockDefaultState(item, Blocks.MAGMA, Blocks.ICE);
      addEntryToReagentByBlockDefaultState(item, Blocks.ICE, Blocks.MAGMA);
      addEntryToReagentByBlockDefaultState(item, Blocks.STONE, Blocks.END_STONE);
      addEntryToReagentByBlockDefaultState(item, Blocks.END_STONE, Blocks.STONE);
      addEntryToReagentByBlockDefaultState(item, Blocks.CLAY, Blocks.HARDENED_CLAY);
      addEntryToReagentByBlockDefaultState(item, Blocks.HARDENED_CLAY, Blocks.CLAY);
      addEntryToReagent(item, Blocks.SPONGE.getDefaultState().withProperty(BlockSponge.WET, Boolean.valueOf(true)), Blocks.SPONGE.getDefaultState());
      addEntryToReagent(item, Blocks.SPONGE.getDefaultState(), Blocks.SPONGE.getDefaultState().withProperty(BlockSponge.WET, Boolean.valueOf(true)));
      addEntryToReagent(item, Blocks.PRISMARINE.getDefaultState(), Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, EnumType.BRICKS));
      addEntryToReagent(item, Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, EnumType.BRICKS), Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, EnumType.DARK));
      addEntryToReagent(item, Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, EnumType.DARK), Blocks.PRISMARINE.getDefaultState());

      for(EnumFacing face : EnumFacing.HORIZONTALS) {
         addEntryToReagent(item, Blocks.PUMPKIN.getDefaultState().withProperty(BlockPumpkin.FACING, face), Blocks.MELON_BLOCK.getDefaultState());
      }

      for(int i = 0; i < 6; ++i) {
         addEntryToReagent(item, Blocks.SAPLING.getStateFromMeta(i), Blocks.SAPLING.getStateFromMeta(i == 5 ? 0 : i + 1));
      }

      for(int i = 0; i < 16; ++i) {
         addEntryToReagent(item, Blocks.WOOL.getStateFromMeta(i), Blocks.WOOL.getStateFromMeta(i == 15 ? 0 : i + 1));
         addEntryToReagent(item, Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(i), Blocks.STAINED_HARDENED_CLAY.getStateFromMeta(i == 15 ? 0 : i + 1));
         addEntryToReagent(item, Blocks.STAINED_GLASS.getStateFromMeta(i), Blocks.STAINED_GLASS.getStateFromMeta(i == 15 ? 0 : i + 1));
         addEntryToReagent(item, Blocks.STAINED_GLASS_PANE.getStateFromMeta(i), Blocks.STAINED_GLASS_PANE.getStateFromMeta(i == 15 ? 0 : i + 1));
         addEntryToReagent(item, Blocks.CARPET.getStateFromMeta(i), Blocks.CARPET.getStateFromMeta(i == 15 ? 0 : i + 1));
      }

   }

   public static boolean doesStateMapContainKeyState(IBlockState state, Map<IBlockState, IBlockState> stateMap) {
      return stateMap.containsKey(state);
   }

   public static boolean doesReagentStateMapContainReagentItem(Item item) {
      return reagentStateMap.containsKey(item);
   }

   public static void addEntryToReagentByBlockDefaultState(Item item, Block block1, Block block2) {
      if (reagentStateMap.containsKey(item)) {
         Map<IBlockState, IBlockState> map = (Map)reagentStateMap.get(item);
         map.put(block1.getDefaultState(), block2.getDefaultState());
      } else {
         Map<IBlockState, IBlockState> stateMap = new HashMap();
         stateMap.put(block1.getDefaultState(), block2.getDefaultState());
         reagentStateMap.put(item, stateMap);
      }

   }

   public static void addEntryToReagent(Item item, IBlockState state1, IBlockState state2) {
      if (reagentStateMap.containsKey(item)) {
         Map<IBlockState, IBlockState> map = (Map)reagentStateMap.get(item);
         map.put(state1, state2);
      } else {
         Map<IBlockState, IBlockState> stateMap = new HashMap();
         stateMap.put(state1, state2);
         reagentStateMap.put(item, stateMap);
      }

   }

   public static void addEntriesToReagent(Item item, Map<IBlockState, IBlockState> map) {
      if (reagentStateMap.containsKey(item)) {
         Map<IBlockState, IBlockState> mapster = (Map)reagentStateMap.get(item);

         for(Entry<IBlockState, IBlockState> entry : map.entrySet()) {
            mapster.put(entry.getKey(), entry.getValue());
         }
      } else {
         Map<IBlockState, IBlockState> stateMap = new HashMap();

         for(Entry<IBlockState, IBlockState> entry : map.entrySet()) {
            stateMap.put(entry.getKey(), entry.getValue());
         }

         reagentStateMap.put(item, stateMap);
      }

   }

   public static void addEntryReagentAgnostic(IBlockState state1, IBlockState state2) {
      for(Item key : reagentStateMap.keySet()) {
         Map<IBlockState, IBlockState> map = (Map)reagentStateMap.get(key);
         map.put(state1, state2);
      }

   }

   public static void addEntriesReagentAgnostic(Map<IBlockState, IBlockState> mapster) {
      for(Item key : reagentStateMap.keySet()) {
         Map<IBlockState, IBlockState> map = (Map)reagentStateMap.get(key);

         for(Entry<IBlockState, IBlockState> entry : mapster.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
         }
      }

   }

   public static void removeStartStateFromReagentAgnostic(IBlockState state) {
      for(Item reagent : reagentStateMap.keySet()) {
         ((Map)reagentStateMap.get(reagent)).remove(state);
      }

   }

   public static void removeEndStateFromReagentAgnostic(IBlockState state) {
      for(Item reagent : reagentStateMap.keySet()) {
         for(Entry<IBlockState, IBlockState> stateEntry : ((Map)reagentStateMap.get(reagent)).entrySet()) {
            if (stateEntry.getValue() == state) {
               ((Map)reagentStateMap.get(reagent)).remove(stateEntry.getKey());
            }
         }
      }

   }

   public static void removeStartStateFromReagent(Item reagent, IBlockState state) {
      ((Map)reagentStateMap.get(reagent)).remove(state);
   }

   public static void removeEndStateFromReagent(Item reagent, IBlockState state) {
      for(Entry<IBlockState, IBlockState> stateEntry : ((Map)reagentStateMap.get(reagent)).entrySet()) {
         if (stateEntry.getValue() == state) {
            ((Map)reagentStateMap.get(reagent)).remove(stateEntry.getKey());
         }
      }

   }

   public static void clearMapOfReagent(Item stack) {
      reagentStateMap.remove(stack);
   }

   public static void clearReagentOfEntries(Item stack) {
      ((Map)reagentStateMap.get(stack)).clear();
   }

   public static void clearReagentMap() {
      reagentStateMap.clear();
   }
}
