package codersafterdark.reskillable.api.requirement;

import codersafterdark.reskillable.api.data.PlayerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public final class NoneRequirement extends Requirement {
   public NoneRequirement() {
      this.tooltip = TextFormatting.GREEN + (new TextComponentTranslation("reskillable.requirements.format.unobtainable", new Object[0])).getUnformattedComponentText();
   }

   public boolean achievedByPlayer(EntityPlayer entityPlayerMP) {
      return true;
   }

   public String getToolTip(PlayerData data) {
      return this.tooltip;
   }

   public RequirementComparision matches(Requirement other) {
      return other instanceof NoneRequirement ? RequirementComparision.EQUAL_TO : RequirementComparision.NOT_EQUAL;
   }

   public boolean equals(Object o) {
      return o instanceof NoneRequirement;
   }

   public int hashCode() {
      return 2;
   }
}
