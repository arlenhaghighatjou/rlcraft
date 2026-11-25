package codersafterdark.reskillable.api.requirement.logic;

import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementCache;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

public abstract class DoubleRequirement extends Requirement implements OuterRequirement {
   private final Requirement left;
   private final Requirement right;

   protected DoubleRequirement(Requirement left, Requirement right) {
      this.left = left;
      this.right = right;
   }

   public Requirement getLeft() {
      return this.left;
   }

   public Requirement getRight() {
      return this.right;
   }

   protected abstract String getFormat();

   protected boolean leftAchieved(EntityPlayer player) {
      return RequirementCache.requirementAchieved(player, this.getLeft());
   }

   protected boolean rightAchieved(EntityPlayer player) {
      return RequirementCache.requirementAchieved(player, this.getRight());
   }

   public String getToolTip(PlayerData data) {
      TextFormatting color = data != null && data.requirementAchieved(this) ? TextFormatting.GREEN : TextFormatting.RED;
      return TextFormatting.GRAY + " - " + this.getToolTipPart(data, this.getLeft()) + ' ' + color + this.getFormat() + ' ' + this.getToolTipPart(data, this.getRight());
   }

   private String getToolTipPart(PlayerData data, Requirement side) {
      String tooltip = side.getToolTip(data);
      if (tooltip != null && tooltip.startsWith(TextFormatting.GRAY + " - ")) {
         tooltip = tooltip.replaceFirst(TextFormatting.GRAY + " - ", "");
      }

      if (side instanceof DoubleRequirement) {
         tooltip = TextFormatting.GOLD + "(" + TextFormatting.RESET + tooltip + TextFormatting.GOLD + ')';
      } else {
         tooltip = TextFormatting.RESET + tooltip;
      }

      return tooltip;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof DoubleRequirement)) {
         return false;
      } else {
         DoubleRequirement dreq = (DoubleRequirement)o;
         return this.getRight().equals(dreq.getRight()) && this.getLeft().equals(dreq.getLeft()) || this.getRight().equals(dreq.getLeft()) && this.getLeft().equals(dreq.getRight());
      }
   }

   public int hashCode() {
      long leftHash = (long)this.getLeft().hashCode();
      long rightHash = (long)this.getRight().hashCode();
      return (int)((leftHash + rightHash) / 2L);
   }

   @Nonnull
   public List<Class<? extends Requirement>> getInternalTypes() {
      List<Class<? extends Requirement>> types = new ArrayList();
      Requirement lReq = this.getLeft();
      Requirement rReq = this.getRight();
      if (lReq instanceof OuterRequirement) {
         types.addAll(((OuterRequirement)lReq).getInternalTypes());
      } else {
         types.add(lReq.getClass());
      }

      if (rReq instanceof OuterRequirement) {
         types.addAll(((OuterRequirement)rReq).getInternalTypes());
      } else {
         types.add(rReq.getClass());
      }

      return types;
   }

   public boolean isCacheable() {
      return this.getLeft().isCacheable() && this.getRight().isCacheable();
   }
}
