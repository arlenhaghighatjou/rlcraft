package codersafterdark.compatskills.common.compats.reskillable.skillchange;

import codersafterdark.reskillable.api.skill.Skill;
import java.util.Objects;

public class SkillLevel implements SkillChange {
   private final Skill skill;
   private final int level;

   public SkillLevel(Skill skill, int level) {
      this.skill = skill;
      this.level = level;
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.skill, this.level});
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof SkillLevel)) {
         return false;
      } else {
         SkillLevel change = (SkillLevel)o;
         return this.level == change.level && this.skill.equals(change.skill);
      }
   }
}
