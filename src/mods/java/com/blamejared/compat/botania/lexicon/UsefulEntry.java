package com.blamejared.compat.botania.lexicon;

import vazkii.botania.api.lexicon.LexiconCategory;
import vazkii.botania.api.lexicon.LexiconEntry;

public class UsefulEntry extends LexiconEntry {
   public UsefulEntry(String unlocalizedName, LexiconCategory category) {
      super(unlocalizedName, category);
   }

   public String getTagline() {
      return "botania.tagline." + super.getUnlocalizedName();
   }
}
