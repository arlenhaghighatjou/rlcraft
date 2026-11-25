package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.capability.TemperatureProvider;
import com.charles445.simpledifficulty.capability.ThirstProvider;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.network.MessageUpdateTemperature;
import com.charles445.simpledifficulty.network.MessageUpdateThirst;
import com.charles445.simpledifficulty.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class CapabilityHandler {
   public CapabilityHandler() {
   }

   @SubscribeEvent
   public void onAttachCapability(AttachCapabilitiesEvent event) {
      if (event.getObject() instanceof EntityPlayer) {
         event.addCapability(new ResourceLocation("simpledifficulty", "temperature"), new TemperatureProvider(SDCapabilities.TEMPERATURE));
         event.addCapability(new ResourceLocation("simpledifficulty", "thirst"), new ThirstProvider(SDCapabilities.THIRST));
      }

   }

   @SubscribeEvent
   public void onPlayerTick(PlayerTickEvent event) {
      EntityPlayer player = event.player;
      World world = player.world;
      if (!world.isRemote) {
         int packetTimerThreshold = ModConfig.server.miscellaneous.routinePacketDelay;
         if (QuickConfig.isTemperatureEnabled()) {
            ITemperatureCapability temperatureCapability = SDCapabilities.getTemperatureData(player);
            temperatureCapability.tickUpdate(player, world, event.phase);
            if (event.phase == Phase.START && (temperatureCapability.isDirty() || temperatureCapability.getPacketTimer() % packetTimerThreshold == 0)) {
               temperatureCapability.setClean();
               this.sendTemperatureUpdate((EntityPlayerMP)player);
            }
         }

         if (QuickConfig.isThirstEnabled() && !this.shouldPlayerSkipThirst(player)) {
            IThirstCapability thirstCapability = SDCapabilities.getThirstData(player);
            thirstCapability.tickUpdate(player, world, event.phase);
            if (event.phase == Phase.START && (thirstCapability.isDirty() || thirstCapability.getPacketTimer() % packetTimerThreshold == 0)) {
               thirstCapability.setClean();
               this.sendThirstUpdate((EntityPlayerMP)player);
            }
         }

      }
   }

   private boolean shouldPlayerSkipThirst(EntityPlayer player) {
      return player.isSpectator() || player.isCreative();
   }

   private void sendTemperatureUpdate(EntityPlayerMP player) {
      Capability capability = SDCapabilities.TEMPERATURE;
      MessageUpdateTemperature message = new MessageUpdateTemperature(capability.getStorage().writeNBT(capability, SDCapabilities.getTemperatureData(player), (EnumFacing)null));
      PacketHandler.instance.sendTo(message, player);
   }

   private void sendThirstUpdate(EntityPlayerMP player) {
      Capability capability = SDCapabilities.THIRST;
      MessageUpdateThirst message = new MessageUpdateThirst(capability.getStorage().writeNBT(capability, SDCapabilities.getThirstData(player), (EnumFacing)null));
      PacketHandler.instance.sendTo(message, player);
   }
}
