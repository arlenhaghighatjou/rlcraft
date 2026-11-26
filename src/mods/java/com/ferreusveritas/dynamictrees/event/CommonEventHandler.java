package com.ferreusveritas.dynamictrees.event;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.ferreusveritas.dynamictrees.api.WorldGenRegistry;
import com.ferreusveritas.dynamictrees.client.TooltipHandler;
import com.ferreusveritas.dynamictrees.seasons.SeasonHelper;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.Type;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CommonEventHandler {
   public CommonEventHandler() {
   }

   public void register(EventBus bus) {
      bus.register(WorldTickEvent.class, this::onWorldTick);
      bus.register(Load.class, this::onWorldLoad);
      bus.register(ItemTooltipEvent.class, this::onItemTooltipAdded);
   }

   public void onWorldTick(WorldTickEvent event) {
      if (event.side == Side.SERVER) {
         FutureBreak.process(event.world);
      }

      if (event.type == Type.WORLD && event.phase == Phase.START) {
         SeasonHelper.updateTick(event.world, event.world.getWorldTime());
      }

   }

   public void onWorldLoad(Load event) {
      if (WorldGenRegistry.isWorldGenEnabled() && !event.getWorld().isRemote && !WorldGenRegistry.validateBiomeDataBases()) {
         WorldGenRegistry.populateDataBase();
      }

      event.getWorld().addEventListener(new WorldListener(event.getWorld(), event.getWorld().getMinecraftServer()));
   }

   @SideOnly(Side.CLIENT)
   public void onItemTooltipAdded(ItemTooltipEvent event) {
      TooltipHandler.setupTooltips(event);
   }
}
