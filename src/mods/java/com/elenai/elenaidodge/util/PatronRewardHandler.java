package com.elenai.elenaidodge.util;

import com.elenai.elenaidodge.ElenaiDodge;
import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent.Post;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(
   modid = "elenaidodge"
)
public class PatronRewardHandler {
   private static final ImmutableSet<String> DEV_UUID = ImmutableSet.of("1f12bbe0-0396-458e-8bb6-49bffdeb46dd", "2436164d-96a1-4fe8-8c0c-a118ff2d9157", "c4c40239-2e88-42d3-a5bb-806ed8c21845", "5084e6f3-8f54-43f1-8df5-1dca109e430f");
   private static final Set<String> done = Collections.newSetFromMap(new WeakHashMap());
   private static Thread thread;
   private static String name;
   private static final Map<String, Integer> tiers = new HashMap();
   public static int localPatronTier = 0;

   public PatronRewardHandler() {
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public static void onRenderPlayer(Post event) {
      EntityPlayer player = event.getEntityPlayer();
      String uuid = EntityPlayer.getUUID(player.getGameProfile()).toString();
      if (player instanceof AbstractClientPlayer && DEV_UUID.contains(uuid) && !done.contains(uuid)) {
         AbstractClientPlayer clientPlayer = (AbstractClientPlayer)player;
         if (clientPlayer.hasPlayerInfo()) {
            NetworkPlayerInfo info = (NetworkPlayerInfo)ObfuscationReflectionHelper.getPrivateValue(AbstractClientPlayer.class, clientPlayer, "playerInfo");
            Map<Type, ResourceLocation> textures = (Map)ObfuscationReflectionHelper.getPrivateValue(NetworkPlayerInfo.class, info, "playerTextures");
            ResourceLocation loc = new ResourceLocation("elenaidodge", "textures/misc/dev_cape.png");
            textures.put(Type.CAPE, loc);
            textures.put(Type.ELYTRA, loc);
            done.add(uuid);
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public static void setupClient() {
      name = Minecraft.getMinecraft().getSession().getUsername().toLowerCase();
   }

   public static void init() {
      if (thread == null || !thread.isAlive()) {
         thread = new PatronRewardHandler.ThreadContributorListLoader();
      }
   }

   public static int getTier(EntityPlayer player) {
      return getTier(player.getName()) == 99 ? 4 : getTier(player.getName());
   }

   public static int getTier(String name) {
      return ((Integer)tiers.getOrDefault(name.toLowerCase(Locale.ROOT), Integer.valueOf(0))).intValue();
   }

   @SubscribeEvent
   public static void onPlayerJoin(EntityJoinWorldEvent event) {
      if (event.getEntity() instanceof EntityPlayer) {
         init();
      }

   }

   private static void load(Properties props) {
      List<String> allPatrons = new ArrayList(props.size());
      props.forEach((k, v) -> {
         String key = (String)k;
         String value = (String)v;
         int tier = Integer.parseInt(value);
         if (tier < 10) {
            allPatrons.add(key);
         }

         tiers.put(key.toLowerCase(Locale.ROOT), Integer.valueOf(tier));
         if (name != null && key.toLowerCase(Locale.ROOT).equals(name)) {
            localPatronTier = tier;
         }

      });
   }

   private static class ThreadContributorListLoader extends Thread {
      public ThreadContributorListLoader() {
         this.setName("Elenai Dodge Patron Loading Thread");
         this.setDaemon(true);
         this.start();
      }

      public void run() {
         try {
            URL url = new URL("https://raw.githubusercontent.com/ElenaiDev/Patrons/master/contributors.properties");
            Properties patreonTiers = new Properties();
            InputStreamReader reader = new InputStreamReader(url.openStream());
            Throwable var4 = null;

            try {
               patreonTiers.load(reader);
               PatronRewardHandler.load(patreonTiers);
            } catch (Throwable var14) {
               var4 = var14;
               throw var14;
            } finally {
               if (reader != null) {
                  if (var4 != null) {
                     try {
                        reader.close();
                     } catch (Throwable var13) {
                        var4.addSuppressed(var13);
                     }
                  } else {
                     reader.close();
                  }
               }

            }
         } catch (IOException var16) {
            ElenaiDodge.LOG.error("Failed to load patreon information", var16);
         }

      }
   }
}
