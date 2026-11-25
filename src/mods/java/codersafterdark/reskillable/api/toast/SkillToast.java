package codersafterdark.reskillable.api.toast;

import codersafterdark.reskillable.api.skill.Skill;
import codersafterdark.reskillable.client.base.RenderHelper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

@SideOnly(Side.CLIENT)
public class SkillToast extends AbstractToast {
   private final Skill skill;
   private final int rank;

   public SkillToast(Skill skill, int level) {
      super(skill.getName(), (new TextComponentTranslation("reskillable.toast.skill_desc", new Object[]{level})).getUnformattedComponentText());
      this.skill = skill;
      this.rank = this.skill.getRank(level);
   }

   protected void renderImage(GuiToast guiToast) {
      if (this.skill.hasCustomSprites()) {
         ResourceLocation sprite = this.skill.getSpriteLocation(this.rank);
         if (sprite != null) {
            this.bindImage(guiToast, sprite);
            Gui.drawModalRectWithCustomSizedTexture(this.x, this.y, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
         }
      } else {
         this.bindImage(guiToast, this.skill.getSpriteLocation());
         Pair<Integer, Integer> pair = this.skill.getSpriteFromRank(this.rank);
         RenderHelper.drawTexturedModalRect(this.x, this.y, 1.0F, ((Integer)pair.getKey()).intValue(), ((Integer)pair.getValue()).intValue(), 16, 16, 0.015625F, 0.015625F);
      }

   }

   protected boolean hasImage() {
      return !this.skill.hasCustomSprites() || this.skill.getSpriteLocation(this.rank) != null;
   }
}
