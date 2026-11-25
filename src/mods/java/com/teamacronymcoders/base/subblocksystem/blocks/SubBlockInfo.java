package com.teamacronymcoders.base.subblocksystem.blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class SubBlockInfo {
   private Map<Integer, Map<Integer, String>> savedSubBlockNames = new HashMap();

   public SubBlockInfo() {
   }

   public Map<Integer, Map<Integer, String>> getSavedSubBlockNames() {
      return this.savedSubBlockNames;
   }

   public void setSavedSubBlocksName(Map<Integer, Map<Integer, String>> savedSubBlocks) {
      this.savedSubBlockNames = savedSubBlocks;
   }

   public void setSavedSubBlocks(Map<Integer, Map<Integer, ISubBlock>> savedSubBlocks) {
      Map<Integer, Map<Integer, String>> savedNames = new HashMap();
      savedSubBlocks.forEach((blockNumber, subBlocksForBlock) -> {
         Map<Integer, String> subBlockNames = new HashMap();
         subBlocksForBlock.forEach((subBlockNumber, subBlock) -> {
            String var10000 = (String)subBlockNames.put(subBlockNumber, subBlock != null ? subBlock.getName() : null);
         });
         savedNames.put(blockNumber, subBlockNames);
      });
      this.setSavedSubBlocksName(savedNames);
   }
}
