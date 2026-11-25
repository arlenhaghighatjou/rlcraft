package codersafterdark.compatskills.common.compats.tinkersconstruct;

import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.base.LevelLockHandler;
import java.util.function.Consumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent.ToolCraftingEvent;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent.ToolModifyEvent;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent.ToolPartCraftingEvent;
import slimeknights.tconstruct.library.events.TinkerCraftingEvent.ToolPartReplaceEvent;

public class TinkerLockHandler {
   public TinkerLockHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onCraftingMaterial(ToolPartCraftingEvent event) {
      this.handleTinkerEvent(event, "compatskills.error.tconstruct.craft.part");
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onModifierAttached(ToolModifyEvent event) {
      this.handleTinkerEvent(event, "compatskills.error.tconstruct.modify");
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onPartReplaced(ToolPartReplaceEvent event) {
      this.handleTinkerEvent(event, "compatskills.error.tconstruct.modify");
   }

   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public void onToolCrafted(ToolCraftingEvent event) {
      if (event.getPlayer() == null) {
         event.setCanceled(true);
      } else {
         this.handleTinkerEvent(event, "compatskills.error.tconstruct.craft.tool");
      }
   }

   private void handleTinkerEvent(TinkerCraftingEvent event, String translationKey) {
      EntityPlayer player = event.getPlayer();
      if (!Utils.skipPlayer(player)) {
         PlayerData data = PlayerDataHandler.get(player);
         if (data != null) {
            RequirementHolder holder = LevelLockHandler.getSkillLock(event.getItemStack());
            if (!holder.equals(LevelLockHandler.EMPTY_LOCK) && !data.matchStats(holder)) {
               StringBuilder reqs = new StringBuilder((new TextComponentTranslation(translationKey, new Object[0])).getUnformattedComponentText());
               reqs.append("\n\n").append((new TextComponentTranslation("compatskills.misc.requirements", new Object[0])).getUnformattedComponentText());
               holder.getRequirements().forEach((req) -> {
                  reqs.append('\n').append(req.getToolTip(data));
               });
               event.setCanceled(reqs.toString());
            }
         }
      }
   }
}
