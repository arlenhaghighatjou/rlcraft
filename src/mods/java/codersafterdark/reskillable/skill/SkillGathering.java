package codersafterdark.reskillable.skill;

import codersafterdark.reskillable.api.skill.Skill;
import net.minecraft.util.ResourceLocation;

public class SkillGathering extends Skill {
   public SkillGathering() {
      super(new ResourceLocation("reskillable", "gathering"), new ResourceLocation("textures/blocks/log_oak.png"));
   }
}
