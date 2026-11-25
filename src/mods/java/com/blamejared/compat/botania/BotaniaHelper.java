package com.blamejared.compat.botania;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.KnowledgeType;
import vazkii.botania.api.lexicon.LexiconCategory;
import vazkii.botania.api.lexicon.LexiconEntry;

public class BotaniaHelper {
   public BotaniaHelper() {
   }

   public static LexiconCategory findCatagory(String name) {
      for(LexiconCategory catagory : BotaniaAPI.getAllCategories()) {
         if (catagory.getUnlocalizedName().equalsIgnoreCase(name)) {
            return catagory;
         }
      }

      return null;
   }

   public static LexiconEntry findEntry(String name) {
      for(LexiconEntry entry : BotaniaAPI.getAllEntries()) {
         if (entry.getUnlocalizedName().equalsIgnoreCase(name)) {
            return entry;
         }
      }

      return null;
   }

   public static KnowledgeType findKnowledgeType(String name) {
      return BotaniaAPI.knowledgeTypes.containsKey(name) ? (KnowledgeType)BotaniaAPI.knowledgeTypes.get(name) : null;
   }
}
