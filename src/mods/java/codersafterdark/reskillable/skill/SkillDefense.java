package codersafterdark.reskillable.skill;

import codersafterdark.reskillable.api.skill.Skill;
import net.minecraft.util.ResourceLocation;

public class SkillDefense extends Skill {
   public SkillDefense() {
      super(new ResourceLocation("reskillable", "defense"), new ResourceLocation("textures/blocks/quartz_block_side.png"));
   }
}
