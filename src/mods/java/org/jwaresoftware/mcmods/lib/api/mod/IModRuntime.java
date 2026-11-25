package org.jwaresoftware.mcmods.lib.api.mod;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public interface IModRuntime extends IModInventoryRenderingHelper {
   String UI = "ui";

   void beginStartUp(FMLStateEvent var1);

   default void midStartUp(FMLStateEvent event) {
   }

   void finishStartUp(FMLStateEvent var1);

   Boolean isEnabled(String var1);

   boolean hasUI();

   IModConfig getConfig();

   Logger getLog();

   CreativeTabs getOwnedTab();

   void setOwnedTab(CreativeTabs var1);

   void shutDown(FMLServerStoppingEvent var1);

   default void reloadConfig() {
   }

   EaglercraftRandom getPRNG();

   default void setupModdedLootTables(WorldServer world) {
   }

   default void sendPlayerMessage(EntityPlayer player, boolean important, String formattedText, Object... args) {
   }

   IThreadListener getThreadListener(MessageContext var1);

   EntityPlayer getPlayer(MessageContext var1);

   @Nullable
   EntityPlayer getClientPlayer();

   @Nullable
   World getClientWorld();

   public static class WrongSideException extends RuntimeException {
      public WrongSideException(String message) {
         super(message);
      }

      public WrongSideException(String message, Throwable cause) {
         super(message, cause);
      }
   }
}
