package codersafterdark.compatskills.common.compats.electroblob;

import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.base.LevelLockHandler;
import electroblob.wizardry.event.SpellBindEvent;
import electroblob.wizardry.tileentity.ContainerArcaneWorkbench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WizardyEventHandler {
   public WizardyEventHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onSpellBind(SpellBindEvent event) {
      if (!event.isCanceled() && event.getContainer() instanceof ContainerArcaneWorkbench) {
         EntityPlayer player = event.getEntityPlayer();
         if (!Utils.skipPlayer(player)) {
            PlayerData data = PlayerDataHandler.get(player);
            ContainerArcaneWorkbench workbench = (ContainerArcaneWorkbench)event.getContainer();
            if (this.testSlot(data, player, workbench.getSlot(8), "compatskills.error.wizardry.crystal")) {
               event.setCanceled(true);
            } else if (this.testSlot(data, player, workbench.getSlot(9), "compatskills.error.wizardry.center")) {
               event.setCanceled(true);
            } else if (this.testSlot(data, player, workbench.getSlot(10), "compatskills.error.wizardry.upgrade")) {
               event.setCanceled(true);
            } else {
               for(int i = 0; i < 8; ++i) {
                  Slot spellSlot = workbench.getSlot(i);
                  if (spellSlot.xPos != -999 && spellSlot.yPos != -999 && this.testSlot(data, player, spellSlot, "compatskills.error.wizardry.spell")) {
                     event.setCanceled(true);
                     break;
                  }
               }
            }

         }
      }
   }

   private boolean testSlot(PlayerData data, EntityPlayer player, Slot slot, String translationKeyError) {
      ItemStack stack = slot.getStack();
      if (stack.isEmpty()) {
         return false;
      } else {
         RequirementHolder requirementHolder = LevelLockHandler.getSkillLock(stack);
         if (!requirementHolder.equals(LevelLockHandler.EMPTY_LOCK) && !data.matchStats(requirementHolder)) {
            TextComponentTranslation error = new TextComponentTranslation(translationKeyError, new Object[0]);
            player.sendStatusMessage(Utils.getError(requirementHolder, data, error), false);
            return true;
         } else {
            return false;
         }
      }
   }
}
