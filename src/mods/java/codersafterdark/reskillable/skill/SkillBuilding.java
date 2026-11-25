package codersafterdark.reskillable.skill;

import codersafterdark.reskillable.api.skill.Skill;
import net.minecraft.util.ResourceLocation;

public class SkillBuilding extends Skill {
   public SkillBuilding() {
      super(new ResourceLocation("reskillable", "building"), new ResourceLocation("textures/blocks/brick.png"));
   }
}
