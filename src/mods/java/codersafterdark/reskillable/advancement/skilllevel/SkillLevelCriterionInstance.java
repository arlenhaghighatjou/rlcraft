package codersafterdark.reskillable.advancement.skilllevel;

import codersafterdark.reskillable.api.skill.Skill;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.util.ResourceLocation;

public class SkillLevelCriterionInstance extends AbstractCriterionInstance {
   private final Skill skill;
   private final int level;

   public SkillLevelCriterionInstance(@Nullable Skill skill, int level) {
      super(new ResourceLocation("reskillable", "skill_level"));
      this.skill = skill;
      this.level = level;
   }

   public boolean test(Skill skill, int level) {
      return (this.skill == null || this.skill == skill) && level >= this.level;
   }
}
