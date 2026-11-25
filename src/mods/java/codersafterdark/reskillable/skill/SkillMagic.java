package codersafterdark.reskillable.skill;

import codersafterdark.reskillable.api.skill.Skill;
import net.minecraft.util.ResourceLocation;

public class SkillMagic extends Skill {
   public SkillMagic() {
      super(new ResourceLocation("reskillable", "magic"), new ResourceLocation("textures/blocks/end_stone.png"));
   }
}
