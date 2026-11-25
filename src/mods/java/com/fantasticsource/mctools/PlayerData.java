package com.fantasticsource.mctools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public class PlayerData {
   public static LinkedHashMap<EaglercraftUUID, PlayerData> playerData = new LinkedHashMap();
   private static String modDir = Loader.instance().getConfigDir().getAbsolutePath() + File.separator + "fantasticlib" + File.separator;
   private static String referenceDir = modDir + "reference" + File.separator;
   public String name;
   public EaglercraftUUID id;
   public EntityPlayer player;

   public PlayerData(String name, EaglercraftUUID id) {
      this.player = null;
      this.name = name;
      this.id = id;
   }

   public PlayerData(EntityPlayer player) {
      this.player = player;
      this.name = player.getName();
      this.id = player.getPersistentID();
   }

   public static PlayerData get(EaglercraftUUID id) {
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
      PlayerList playerList = server == null ? null : server.getPlayerList();
      EntityPlayer player = playerList == null ? null : playerList.getPlayerByUUID(id);
      if (player != null) {
         PlayerData data = new PlayerData(player);
         playerData.put(player.getPersistentID(), data);
         return data;
      } else {
         PlayerData data = (PlayerData)playerData.get(id);
         if (data != null) {
            data.player = player;
         }

         return data;
      }
   }

   public static PlayerData get(String name) {
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
      PlayerList playerList = server == null ? null : server.getPlayerList();
      EntityPlayer player = playerList == null ? null : playerList.getPlayerByUsername(name);
      if (player != null) {
         PlayerData data = new PlayerData(player);
         playerData.put(player.getPersistentID(), data);
         return data;
      } else {
         for(PlayerData data : playerData.values()) {
            if (data.name.equals(name)) {
               return data;
            }
         }

         return null;
      }
   }

   public static String getName(EaglercraftUUID id) {
      PlayerData data = get(id);
      return data == null ? null : data.name;
   }

   public static EaglercraftUUID getID(String name) {
      PlayerData data = get(name);
      return data == null ? null : data.id;
   }

   public static EntityPlayer getEntity(EaglercraftUUID id) {
      PlayerData data = get(id);
      return data == null ? null : data.player;
   }

   public static EntityPlayer getEntity(String name) {
      PlayerData data = get(name);
      return data == null ? null : data.player;
   }

   public static void save() {
      File file = new File(modDir);
      if (!file.exists()) {
         file.mkdir();
      }

      file = new File(referenceDir);
      if (!file.exists()) {
         file.mkdir();
      }

      try {
         BufferedWriter writer = new BufferedWriter(new FileWriter(new File(referenceDir + "players.txt")));

         for(EaglercraftUUID id : playerData.keySet()) {
            PlayerData data = get(id);
            if (data != null && data.name != null) {
               writer.write(id + " = " + data.name + "\r\n");
            }
         }

         writer.close();
      } catch (IOException var5) {
         var5.printStackTrace();
      }

   }

   public static void load() {
      File file = new File(modDir);
      if (!file.exists()) {
         file.mkdir();
      }

      file = new File(referenceDir);
      if (!file.exists()) {
         file.mkdir();
      }

      file = new File(referenceDir + "players.txt");
      if (!file.exists()) {
         save();
      } else {
         try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            for(String line = reader.readLine(); line != null && !line.trim().equals(""); line = reader.readLine()) {
               String[] tokens = line.split("=");
               EaglercraftUUID id = EaglercraftUUID.fromString(tokens[0].trim());
               playerData.put(id, new PlayerData(tokens[1].trim(), id));
            }

            reader.close();
         } catch (IOException var5) {
            var5.printStackTrace();
         }

      }
   }

   @SubscribeEvent
   public static void playerLogon(EntityJoinWorldEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof EntityPlayerMP) {
         EntityPlayerMP player = (EntityPlayerMP)entity;
         playerData.put(player.getPersistentID(), new PlayerData(player));
         save();
      }

   }

   @SubscribeEvent
   public static void playerLogoff(PlayerLoggedOutEvent event) {
      if (event.player != null) {
         EaglercraftUUID id = event.player.getPersistentID();
         if (playerData.containsKey(id)) {
            ((PlayerData)playerData.get(id)).player = null;
         }
      }

   }
}
