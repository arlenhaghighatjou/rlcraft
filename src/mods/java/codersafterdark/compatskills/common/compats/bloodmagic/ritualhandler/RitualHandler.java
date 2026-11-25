package codersafterdark.compatskills.common.compats.bloodmagic.ritualhandler;

import WayofTime.bloodmagic.event.RitualEvent.RitualActivatedEvent;
import WayofTime.bloodmagic.ritual.Ritual;
import codersafterdark.compatskills.utils.CompatSkillConstants;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.base.LevelLockHandler;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RitualHandler {
   public RitualHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void ritualEvent(RitualActivatedEvent event) {
      EntityPlayer player = event.getPlayer();
      if (!Utils.skipPlayer(player)) {
         PlayerData data = PlayerDataHandler.get(player);
         RequirementHolder requirementHolder = LevelLockHandler.getLocks(Ritual.class, new Ritual[]{event.getRitual()});
         if (!requirementHolder.equals(LevelLockHandler.EMPTY_LOCK) && !data.matchStats(requirementHolder)) {
            event.setCanceled(true);
            TextComponentTranslation error = new TextComponentTranslation("compatskills.error.bloodmagic.ritual", new Object[0]);
            String reqs = (String)requirementHolder.getRequirements().stream().map((req) -> {
               return '\n' + req.getToolTip(data);
            }).collect(Collectors.joining());
            player.sendStatusMessage(new TextComponentString(error.getUnformattedComponentText() + CompatSkillConstants.REQUIREMENT_STRING + reqs), false);
         }

      }
   }
}
