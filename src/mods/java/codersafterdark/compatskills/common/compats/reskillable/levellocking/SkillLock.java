package codersafterdark.compatskills.common.compats.reskillable.levellocking;

import codersafterdark.reskillable.api.data.LockKey;
import codersafterdark.reskillable.api.skill.Skill;
import java.util.Objects;

public class SkillLock implements LockKey {
   private final Skill skill;
   private final int level;

   public SkillLock(Skill skill, int level) {
      this.skill = skill;
      this.level = level;
   }

   public Skill getSkill() {
      return this.skill;
   }

   public int getLevel() {
      return this.level;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SkillLock)) {
         return false;
      } else {
         SkillLock other = (SkillLock)o;
         return this.skill == null ? other.skill == null && this.level == other.level : this.skill.equals(other.skill) && this.level == other.level;
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.skill, this.level});
   }
}
