package codersafterdark.reskillable.api.requirement;

import codersafterdark.reskillable.api.data.PlayerData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class UnobtainableRequirement extends Requirement {
   public UnobtainableRequirement() {
      this.tooltip = TextFormatting.RED + (new TextComponentTranslation("reskillable.requirements.format.unobtainable", new Object[0])).getUnformattedComponentText();
   }

   public boolean achievedByPlayer(EntityPlayer entityPlayerMP) {
      return false;
   }

   public String getToolTip(PlayerData data) {
      return this.tooltip;
   }

   public RequirementComparision matches(Requirement other) {
      return other instanceof UnobtainableRequirement ? RequirementComparision.EQUAL_TO : RequirementComparision.NOT_EQUAL;
   }

   public boolean equals(Object o) {
      return o instanceof UnobtainableRequirement;
   }

   public int hashCode() {
      return -1;
   }
}
