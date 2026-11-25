package codersafterdark.reskillable.advancement.skilllevel;

import codersafterdark.reskillable.advancement.CriterionListeners;
import codersafterdark.reskillable.api.skill.Skill;
import java.util.function.Predicate;
import net.minecraft.advancements.PlayerAdvancements;

public class SkillLevelListeners extends CriterionListeners<SkillLevelCriterionInstance> {
   public SkillLevelListeners(PlayerAdvancements playerAdvancements) {
      super(playerAdvancements);
   }

   public void trigger(Skill skill, int level) {
      this.trigger((instance) -> {
         return instance.test(skill, level);
      });
   }
}
