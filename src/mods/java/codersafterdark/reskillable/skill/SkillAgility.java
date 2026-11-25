package codersafterdark.reskillable.skill;

import codersafterdark.reskillable.api.skill.Skill;
import net.minecraft.util.ResourceLocation;

public class SkillAgility extends Skill {
   public SkillAgility() {
      super(new ResourceLocation("reskillable", "agility"), new ResourceLocation("textures/blocks/gravel.png"));
   }
}
