package codersafterdark.compatskills.common.compats.bloodmagic;

import WayofTime.bloodmagic.event.ItemBindEvent;
import codersafterdark.compatskills.utils.CompatSkillConstants;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.base.LevelLockHandler;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BindHandler {
   public BindHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void bindEvent(ItemBindEvent event) {
      EntityPlayer player = event.getNewOwner();
      if (!Utils.skipPlayer(player)) {
         ItemStack stack = event.getBindingStack();
         PlayerData data = PlayerDataHandler.get(player);
         RequirementHolder requirementHolder = LevelLockHandler.getSkillLock(stack);
         if (!requirementHolder.equals(LevelLockHandler.EMPTY_LOCK) && !data.matchStats(requirementHolder)) {
            event.setCanceled(true);
            TextComponentTranslation error = new TextComponentTranslation("compatskills.error.bloodmagic.binding", new Object[0]);
            String reqs = (String)requirementHolder.getRequirements().stream().map((req) -> {
               return '\n' + req.getToolTip(data);
            }).collect(Collectors.joining());
            player.sendStatusMessage(new TextComponentString(error.getUnformattedComponentText() + CompatSkillConstants.REQUIREMENT_STRING + reqs), false);
         }

      }
   }
}
