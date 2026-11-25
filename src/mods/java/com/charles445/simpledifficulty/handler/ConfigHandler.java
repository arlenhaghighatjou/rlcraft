package com.charles445.simpledifficulty.handler;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.config.JsonConfigInternal;
import com.charles445.simpledifficulty.config.ModConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConfigHandler {
   public ConfigHandler() {
   }

   @SubscribeEvent
   public void onPlayerLogin(PlayerLoggedInEvent event) {
      EntityPlayer player = event.player;
      World world = player.world;
      if (!world.isRemote) {
         ModConfig.sendServerConfigToPlayer((EntityPlayerMP)player);
      }
   }

   @SubscribeEvent
   public void onWorldEventUnload(Unload event) {
      if (event.getWorld().isRemote) {
         Boolean connectedToServer = SimpleDifficulty.proxy.isClientConnectedToServer();
         if (connectedToServer == null) {
            SimpleDifficulty.logger.error("Server proxy called isClientConnectedToServer while world was remote!");
            return;
         }

         if (!connectedToServer.booleanValue()) {
            ModConfig.sendLocalClientConfigToAPI();
            ModConfig.sendLocalServerConfigToAPI();
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onPlayerJoin(PlayerLoggedInEvent event) {
      for(String s : JsonConfigInternal.jsonErrors) {
         event.player.sendMessage(new TextComponentString(s));
      }

   }
}
