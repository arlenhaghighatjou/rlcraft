package com.lycanitesmobs.core.info;

public class CreatureKnowledge {
   public Beastiary beastiary;
   public String creatureName;
   public int rank;
   public int experience = 0;

   public CreatureKnowledge(Beastiary beastiary, String creatureName, int rank, int experience) {
      this.beastiary = beastiary;
      this.creatureName = creatureName;
      this.rank = rank;
      this.experience = experience;
   }

   public CreatureInfo getCreatureInfo() {
      return CreatureManager.getInstance().getCreature(this.creatureName);
   }

   public int addExperience(int experience) {
      int maxExperience = this.getMaxExperience();
      if (maxExperience <= 0) {
         return 0;
      } else {
         this.experience += experience;
         int remainingExperience = this.experience - maxExperience;
         if (remainingExperience >= 0) {
            ++this.rank;
            this.experience = 0;
            this.addExperience(remainingExperience);
            this.beastiary.sendAddedMessage(this);
         }

         return remainingExperience;
      }
   }

   public int getMaxExperience() {
      if (this.rank == 1) {
         return 1000;
      } else {
         return this.rank >= 2 ? 0 : 1;
      }
   }
}
