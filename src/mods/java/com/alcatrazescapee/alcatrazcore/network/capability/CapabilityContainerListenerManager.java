package com.alcatrazescapee.alcatrazcore.network.capability;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.event.entity.player.PlayerContainerEvent.Open;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;

public class CapabilityContainerListenerManager {
   private static final Set<Function<EntityPlayerMP, CapabilityContainerListener<?>>> containerListenerFactories = new HashSet();

   public CapabilityContainerListenerManager() {
   }

   public static void registerContainerListenerFactory(Function<EntityPlayerMP, CapabilityContainerListener<?>> factory) {
      containerListenerFactories.add(factory);
   }

   private static void addListeners(EntityPlayerMP player, Container container) {
      containerListenerFactories.forEach((factory) -> {
         container.addListener((IContainerListener)factory.apply(player));
      });
   }

   @EventBusSubscriber(
      modid = "alcatrazcore",
      value = {Side.SERVER}
   )
   private static class EventHandler {
      private EventHandler() {
      }

      @SubscribeEvent
      public static void onPlayerLoggedInEvent(PlayerLoggedInEvent event) {
         if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP)event.player;
            CapabilityContainerListenerManager.addListeners(player, player.inventoryContainer);
         }

      }

      @SubscribeEvent
      public static void onPlayerCloneEvent(Clone event) {
         if (event.getEntityPlayer() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP)event.getEntityPlayer();
            CapabilityContainerListenerManager.addListeners(player, player.inventoryContainer);
         }

      }

      @SubscribeEvent
      public static void onContainerOpenEvent(Open event) {
         if (event.getEntityPlayer() instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP)event.getEntityPlayer();
            CapabilityContainerListenerManager.addListeners(player, event.getContainer());
         }

      }
   }
}
