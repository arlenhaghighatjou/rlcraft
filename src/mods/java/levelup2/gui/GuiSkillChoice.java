package levelup2.gui;

import java.util.Map;
import levelup2.api.IPlayerSkill;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiSkillChoice extends GuiScreen {
   private IPlayerSkill skill;
   private GuiSkills parent;
   private int skillLevel;
   private boolean canSpendLevels;
   private boolean canDowngrade;

   public GuiSkillChoice(IPlayerSkill skill, int skillLevel, GuiSkills parent) {
      this.skill = skill;
      this.parent = parent;
      this.skillLevel = skillLevel;
      this.canDowngrade = parent.player.getSkillLevel(skill.getSkillName()) < skillLevel && this.isDowngradable(skill, parent);
      this.canSpendLevels = skill.getLevelCost(skillLevel) <= parent.availableLevels - parent.levelSpend && skill.getLevelCost(skillLevel) > -1 && parent.canUnlock(skill);
   }

   private boolean isDowngradable(IPlayerSkill skill, GuiSkills parent) {
      Map<String, Integer> skillList = parent.skills;

      for(String st : skillList.keySet()) {
         IPlayerSkill check = parent.player.getSkillFromName(st);
         if (((Integer)skillList.get(check.getSkillName())).intValue() > 0 && check.getPrerequisites() != null && check.getPrerequisites().length > 0) {
            for(String checkStr : check.getPrerequisites()) {
               if (checkStr.equals(skill.getSkillName()) && ((Integer)skillList.get(skill.getSkillName())).intValue() - 1 == 0) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public void initGui() {
      this.buttonList.clear();
      GuiButton button = new GuiButton(0, this.width / 2 - 65, this.height / 2 + 35, 130, 20, I18n.format("levelup.skill.increase", new Object[0]));
      button.enabled = this.canSpendLevels;
      this.buttonList.add(button);
      button = new GuiButton(1, this.width / 2 - 65, this.height / 2 + 60, 130, 20, I18n.format("levelup.skill.decrease", new Object[0]));
      button.enabled = this.canDowngrade;
      this.buttonList.add(button);
      this.buttonList.add(new GuiButton(2, this.width / 2 - 65, this.height / 2 + 85, 130, 20, I18n.format("gui.cancel", new Object[0])));
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(I18n.format("skill." + this.skill.getSkillName() + ".name", new Object[0]), this.width / 2, this.height / 2 - 100, -8355712);
      if (this.skill.isActive()) {
         this.drawCenteredString(I18n.format(this.skill.getSkillName() + ".desc", new Object[0]), this.width / 2, this.height / 2 - 82, -8355712);
         int lineIncrement = 0;
         if (!I18n.format(this.skill.getSkillName() + ".desc.1", new Object[0]).equals(this.skill.getSkillName() + ".desc.1")) {
            ++lineIncrement;
            this.drawCenteredString(I18n.format(this.skill.getSkillName() + ".desc.1", new Object[0]), this.width / 2, this.height / 2 - 64, -8355712);
            if (!I18n.format(this.skill.getSkillName() + ".desc.2", new Object[0]).equals(this.skill.getSkillName() + ".desc.2")) {
               ++lineIncrement;
               this.drawCenteredString(I18n.format(this.skill.getSkillName() + ".desc.2", new Object[0]), this.width / 2, this.height / 2 - 46, -8355712);
            }
         }

         if (this.skill.getLevelCost(this.skillLevel) > -1) {
            int offset = 64 - 18 * lineIncrement;
            this.drawCenteredString(I18n.format("levelup.cost", new Object[]{this.skill.getLevelCost(this.skillLevel)}), this.width / 2, this.height / 2 - offset, -8355712);
         }
      } else {
         this.drawCenteredString(I18n.format("skill.inactive", new Object[0]), this.width / 2, this.height / 2 - 82, -8355712);
         if (this.skill.getLevelCost(this.skillLevel) > -1) {
            this.drawCenteredString(I18n.format("levelup.cost", new Object[]{this.skill.getLevelCost(this.skillLevel)}), this.width / 2, this.height / 2 - 64, -8355712);
         }
      }

      this.drawCenteredString(I18n.format("levelup.levels.track", new Object[]{this.skillLevel, this.parent.player.getSkillLevel(this.skill.getSkillName())}), this.width / 2, this.height / 2 + 15, -8355712);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   protected void actionPerformed(GuiButton button) {
      if (button.id == 0) {
         this.changeSkillLevel(1);
         this.parent.levelSpend += this.skill.getLevelCost(this.skillLevel);
      } else if (button.id == 1) {
         this.changeSkillLevel(-1);
         this.parent.levelSpend -= this.skill.getLevelCost(this.skillLevel - 1);
      }

      this.mc.displayGuiScreen(this.parent);
   }

   private void drawCenteredString(String str, int posX, int posY, int color) {
      int centerWidth = this.fontRenderer.getStringWidth(str) / 2;
      this.fontRenderer.drawString(str, posX - centerWidth, posY, color);
   }

   private void changeSkillLevel(int add) {
      int orig = ((Integer)this.parent.skills.get(this.skill.getSkillName())).intValue();
      this.parent.skills.put(this.skill.getSkillName(), Integer.valueOf(orig + add));
   }
}
