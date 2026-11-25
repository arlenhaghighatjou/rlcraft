package levelup2.skills;

import levelup2.api.IPlayerSkill;
import net.minecraft.util.ResourceLocation;

public abstract class BaseSkill implements IPlayerSkill {
   private int[] skillLevels = new int[0];
   private String[] prereqs = new String[0];
   private int column = 0;
   private int row = 0;
   private boolean enabled = true;
   private boolean active = true;

   public BaseSkill() {
   }

   public void setLevelCosts(int[] levelCosts) {
      this.skillLevels = levelCosts;
   }

   public int getLevelCost(int currentLevel) {
      return currentLevel >= 0 && currentLevel < this.getMaxLevel() ? this.skillLevels[currentLevel] : -1;
   }

   public void setPrerequisites(String[] prerequisites) {
      this.prereqs = prerequisites;
   }

   public String[] getPrerequisites() {
      return this.prereqs;
   }

   public void setSkillColumn(int column) {
      this.column = column;
   }

   public int getSkillColumn() {
      return this.column;
   }

   public void setSkillRow(int row) {
      this.row = row;
   }

   public int getSkillRow() {
      return this.row;
   }

   public int getMaxLevel() {
      return this.skillLevels.length;
   }

   public void setEnabled(boolean isEnabled) {
      this.enabled = isEnabled;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public boolean isMaxLevel(int level) {
      return level == this.getMaxLevel();
   }

   public String getJsonLocation() {
      String category = this.getSkillType() == 0 ? "mining" : (this.getSkillType() == 1 ? "crafting" : "combat");
      ResourceLocation location = new ResourceLocation(this.getSkillName());
      return category + "/" + location.getResourcePath();
   }

   public boolean hasExternalJson() {
      return true;
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean isActive) {
      this.active = isActive;
   }
}
