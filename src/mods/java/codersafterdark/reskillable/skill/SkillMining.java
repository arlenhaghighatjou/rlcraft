package codersafterdark.reskillable.skill;

import codersafterdark.reskillable.api.skill.Skill;
import net.minecraft.util.ResourceLocation;

public class SkillMining extends Skill {
   public SkillMining() {
      super(new ResourceLocation("reskillable", "mining"), new ResourceLocation("textures/blocks/stone.png"));
   }
}
