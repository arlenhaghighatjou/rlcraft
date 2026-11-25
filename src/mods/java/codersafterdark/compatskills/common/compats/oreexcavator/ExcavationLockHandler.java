package codersafterdark.compatskills.common.compats.oreexcavator;

import codersafterdark.compatskills.common.compats.oreexcavator.filter.ExcavateRequirementFilter;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.base.LevelLockHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import oreexcavation.events.EventExcavate.Pre;
import oreexcavation.handlers.MiningAgent;

public class ExcavationLockHandler {
   public ExcavationLockHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onExcavation(Pre event) {
      MiningAgent agent = event.getAgent();
      EntityPlayer player = agent.player;
      PlayerData data = PlayerDataHandler.get(player);
      RequirementHolder holder = OreExcavationCompatHandler.getHolder();
      if (!holder.equals(LevelLockHandler.EMPTY_LOCK) && !data.matchStats(holder)) {
         TextComponentTranslation error = new TextComponentTranslation("compatskills.error.ore_excavation", new Object[0]);
         player.sendStatusMessage(Utils.getError(holder, data, error), false);
         event.setCanceled(true);
      } else {
         agent.addFilter(new ExcavateRequirementFilter());
      }

   }
}
