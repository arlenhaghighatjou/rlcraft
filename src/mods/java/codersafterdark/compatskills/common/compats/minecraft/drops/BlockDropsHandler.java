package codersafterdark.compatskills.common.compats.minecraft.drops;

import codersafterdark.compatskills.utils.CompatSkillsConfig;
import codersafterdark.compatskills.utils.Utils;
import codersafterdark.reskillable.api.data.PlayerData;
import codersafterdark.reskillable.api.data.PlayerDataHandler;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.base.LevelLockHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockDropsHandler {
   public BlockDropsHandler() {
   }

   @SubscribeEvent
   public void onBlockDrops(HarvestDropsEvent event) {
      if (!event.isCanceled()) {
         EntityPlayer player = event.getHarvester();
         if (!Utils.skipPlayer(player)) {
            PlayerData data = PlayerDataHandler.get(player);
            Map<RequirementHolder, ItemStack> stackMap = new HashMap();

            for(ItemStack stack : event.getDrops()) {
               RequirementHolder temp = LevelLockHandler.getLockByKey(new ItemStackDropKey(stack));
               if (!temp.equals(LevelLockHandler.EMPTY_LOCK)) {
                  stackMap.put(temp, stack);
               }
            }

            boolean errored = false;

            for(Entry<RequirementHolder, ItemStack> entry : stackMap.entrySet()) {
               RequirementHolder holder = (RequirementHolder)entry.getKey();
               if (!data.matchStats(holder)) {
                  errored = true;
                  event.getDrops().remove(entry.getValue());
                  if (CompatSkillsConfig.Configs.Minecraft.BlockDropsError) {
                     TextComponentTranslation error = new TextComponentTranslation("compatskills.error.drops", new Object[0]);
                     player.sendStatusMessage(Utils.getError(holder, data, error), false);
                  }
               }
            }

            if (!CompatSkillsConfig.Configs.Minecraft.BlockDropsError && errored) {
               TextComponentTranslation error = new TextComponentTranslation("compatskills.error.drops", new Object[0]);
               player.sendStatusMessage(new TextComponentString(error.getUnformattedComponentText()), true);
            }

         }
      }
   }
}
