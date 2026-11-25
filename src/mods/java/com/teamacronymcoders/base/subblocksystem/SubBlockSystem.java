package com.teamacronymcoders.base.subblocksystem;

import com.google.common.collect.Maps;
import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.registrysystem.BlockRegistry;
import com.teamacronymcoders.base.savesystem.SaveLoader;
import com.teamacronymcoders.base.subblocksystem.blocks.BlockSubBlockHolder;
import com.teamacronymcoders.base.subblocksystem.blocks.ISubBlock;
import com.teamacronymcoders.base.subblocksystem.blocks.MissingSubBlock;
import com.teamacronymcoders.base.subblocksystem.blocks.SubBlockInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class SubBlockSystem {
   private IBaseMod mod;
   private Map<String, ISubBlock> subBlocks = Maps.newHashMap();
   public static final ISubBlock MISSING_SUB_BLOCK = new MissingSubBlock();

   public SubBlockSystem(IBaseMod mod) {
      this.mod = mod;
   }

   public void registerSubBlock(ISubBlock iSubBlock) {
      this.subBlocks.put(iSubBlock.getName(), iSubBlock);
   }

   public void createBlocks() {
      Map<String, ISubBlock> subBlocksToCreate = Maps.newHashMap(this.subBlocks);
      SubBlockInfo subBlockInfo = (SubBlockInfo)SaveLoader.getSavedObject("saved_sub_blocks_" + this.mod.getID(), SubBlockInfo.class);
      Map<Integer, Map<Integer, String>> listOfBlockNames = subBlockInfo.getSavedSubBlockNames();
      Map<Integer, Map<Integer, ISubBlock>> listOfSubBlocks = new HashMap();
      if (listOfBlockNames.size() > 0) {
         for(Entry<Integer, Map<Integer, String>> blockNames : listOfBlockNames.entrySet()) {
            Map<Integer, ISubBlock> subBlocksForBlock = new HashMap();

            for(Entry<Integer, String> subBlockName : ((Map)blockNames.getValue()).entrySet()) {
               ISubBlock subBlock = (ISubBlock)subBlocksToCreate.remove(subBlockName.getValue());
               if (subBlock == null) {
                  this.mod.getLogger().error("Could not find sub-block: " + (String)subBlockName.getValue());
               }

               subBlocksForBlock.put(subBlockName.getKey(), subBlock);
            }

            listOfSubBlocks.put(blockNames.getKey(), subBlocksForBlock);
         }
      }

      int blockToCheck = 0;
      int blockMetaToCheck = 0;
      List<ISubBlock> remainingSubBlocks = new ArrayList(subBlocksToCreate.values());

      while(remainingSubBlocks.size() > 0) {
         listOfSubBlocks.computeIfAbsent(Integer.valueOf(blockToCheck), (value) -> {
            return new HashMap();
         });
         ((Map)listOfSubBlocks.get(Integer.valueOf(blockToCheck))).computeIfAbsent(Integer.valueOf(blockMetaToCheck), (value) -> {
            return (ISubBlock)remainingSubBlocks.remove(0);
         });
         ++blockMetaToCheck;
         if (blockMetaToCheck > 15) {
            blockMetaToCheck = 0;
            ++blockToCheck;
         }
      }

      subBlockInfo.setSavedSubBlocks(listOfSubBlocks);
      SaveLoader.saveObject("saved_sub_blocks_" + this.mod.getID(), subBlockInfo);
      BlockRegistry blockRegistry = (BlockRegistry)this.mod.getRegistryHolder().getRegistry(BlockRegistry.class, "BLOCK");
      listOfSubBlocks.forEach((id, subBlocks) -> {
         blockRegistry.register(new BlockSubBlockHolder(id.intValue(), subBlocks));
      });
   }

   public ISubBlock getSubBlock(String name) {
      return (ISubBlock)this.subBlocks.get(name.toLowerCase(Locale.US));
   }
}
