package codersafterdark.reskillable.skill;

import codersafterdark.reskillable.api.skill.Skill;
import net.minecraft.util.ResourceLocation;

public class SkillFarming extends Skill {
   public SkillFarming() {
      super(new ResourceLocation("reskillable", "farming"), new ResourceLocation("textures/blocks/dirt.png"));
   }
}
