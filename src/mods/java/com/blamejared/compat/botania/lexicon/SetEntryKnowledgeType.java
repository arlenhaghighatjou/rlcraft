package com.blamejared.compat.botania.lexicon;

import com.blamejared.compat.botania.BotaniaHelper;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import vazkii.botania.api.lexicon.KnowledgeType;
import vazkii.botania.api.lexicon.LexiconEntry;

public class SetEntryKnowledgeType implements IAction {
   LexiconEntry lexEntry;
   KnowledgeType newType;
   KnowledgeType oldType;
   final String entry;
   final String knowledgeType;

   public SetEntryKnowledgeType(String entry, String knowledgeType) {
      this.entry = entry;
      this.knowledgeType = knowledgeType;
   }

   public void apply() {
      this.lexEntry = BotaniaHelper.findEntry(this.entry);
      this.newType = BotaniaHelper.findKnowledgeType(this.knowledgeType);
      if (this.lexEntry == null) {
         CraftTweakerAPI.getLogger().logError("Cannot find lexicon entry " + this.entry);
      } else if (this.newType == null) {
         CraftTweakerAPI.getLogger().logError("Cannot find knowledge type " + this.knowledgeType);
      } else {
         this.oldType = this.lexEntry.getKnowledgeType();
         this.lexEntry.setKnowledgeType(this.newType);
         CraftTweakerAPI.getLogger().logInfo("Setting Knowledge type for: " + this.lexEntry.getUnlocalizedName());
      }
   }

   public String describe() {
      return "Attempting to set the knowledge type for Lexicon entry " + this.entry + " to " + this.knowledgeType;
   }
}
