package levelup2.api;

import net.minecraft.item.ItemStack;

public interface IPlayerSkill {
   boolean hasSubscription();

   String getSkillName();

   int getLevelCost(int var1);

   void setLevelCosts(int[] var1);

   byte getSkillType();

   String[] getPrerequisites();

   void setPrerequisites(String[] var1);

   int getSkillColumn();

   void setSkillColumn(int var1);

   int getSkillRow();

   void setSkillRow(int var1);

   ItemStack getRepresentativeStack();

   boolean isMaxLevel(int var1);

   int getMaxLevel();

   boolean isEnabled();

   void setEnabled(boolean var1);

   String getJsonLocation();

   boolean hasExternalJson();

   boolean isActive();

   void setActive(boolean var1);
}
