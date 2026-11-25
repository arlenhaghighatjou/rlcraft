package codersafterdark.reskillable.skill;

import codersafterdark.reskillable.api.skill.Skill;
import net.minecraft.util.ResourceLocation;

public class SkillAttack extends Skill {
   public SkillAttack() {
      super(new ResourceLocation("reskillable", "attack"), new ResourceLocation("textures/blocks/stonebrick.png"));
   }
}
