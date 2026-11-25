package codersafterdark.compatskills.common.compats.cyberware;

import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.base.LevelLockHandler;
import flaxbeard.cyberware.api.CyberwareSurgeryEvent.Pre;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemStackHandler;

public class CyberwareEventHandler {
   public CyberwareEventHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onSurgeryAttempt(Pre event) {
      if (!event.isCanceled() && event.getEntity() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntity();
         if (!Utils.skipPlayer(player)) {
            PlayerData data = PlayerDataHandler.get(player);

            for(ItemStack stack : this.getStacksForHandler(event.getTargetCyberwares())) {
               RequirementHolder requirementHolder = LevelLockHandler.getSkillLock(stack);
               if (!requirementHolder.equals(LevelLockHandler.EMPTY_LOCK) && !data.matchStats(requirementHolder)) {
                  event.setCanceled(true);
                  TextComponentTranslation error = new TextComponentTranslation("compatskills.error.cyberware.install", new Object[0]);
                  player.sendStatusMessage(Utils.getError(requirementHolder, data, error), false);
                  return;
               }
            }

         }
      }
   }

   private List<ItemStack> getStacksForHandler(ItemStackHandler handler) {
      IntStream var10000 = IntStream.range(0, handler.getSlots());
      handler.getClass();
      return (List)var10000.mapToObj(handler::getStackInSlot).filter((stack) -> {
         return !stack.isEmpty();
      }).collect(Collectors.toList());
   }
}
