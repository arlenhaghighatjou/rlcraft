package codersafterdark.compatskills.common.compats.reskillable.levellocking;

import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.api.event.LevelUpEvent.Pre;
import codersafterdark.reskillable.api.skill.Skill;
import codersafterdark.reskillable.base.LevelLockHandler;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SkillLockHandler {
   public SkillLockHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void levelUpEvent(Pre event) {
      EntityPlayer player = event.getEntityPlayer();
      if (!Utils.skipPlayer(player)) {
         int level = event.getLevel();
         int oldLevel = event.getOldLevel();
         if (level >= oldLevel && level <= oldLevel + 1) {
            PlayerData data = PlayerDataHandler.get(player);
            Skill skill = event.getSkill();
            RequirementHolder requirementHolder = LevelLockHandler.getLockByKey(new SkillLock(skill, level));
            if (!requirementHolder.equals(LevelLockHandler.EMPTY_LOCK) && !data.matchStats(requirementHolder)) {
               event.setCanceled(true);
               TextComponentTranslation error = new TextComponentTranslation("compatskills.error.reskillable.level_lock", new Object[0]);
               TextComponentTranslation error2 = new TextComponentTranslation("compatskills.error.reskillable.level_lock_2", new Object[0]);
               TextComponentTranslation error3 = new TextComponentTranslation("compatskills.misc.requirements", new Object[0]);
               String reqString = (String)requirementHolder.getRequirements().stream().map((requirement) -> {
                  return "\n " + requirement.getToolTip(data) + ' ';
               }).collect(Collectors.joining());
               ITextComponent textComponent = new TextComponentString(error.getUnformattedComponentText() + '\n' + error2.getUnformattedComponentText() + '\n' + error3.getUnformattedComponentText() + ' ' + reqString);
               player.sendStatusMessage(textComponent, false);
            }

         }
      }
   }
}
