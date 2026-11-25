package levelup2.skills.mining;

import levelup2.skills.BaseSkill;

public abstract class MiningSpeedBonus extends BaseSkill {
   public MiningSpeedBonus() {
   }

   public boolean hasSubscription() {
      return true;
   }

   public byte getSkillType() {
      return 0;
   }
}
