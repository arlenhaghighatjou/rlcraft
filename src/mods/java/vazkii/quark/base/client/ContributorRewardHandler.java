package vazkii.quark.base.client;

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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(
   modid = "quark"
)
public class ContributorRewardHandler {
   private static final ImmutableSet<String> DEV_UUID = ImmutableSet.of("8c826f34-113b-4238-a173-44639c53b6e6", "0d054077-a977-4b19-9df9-8a4d5bf20ec3", "458391f5-6303-4649-b416-e4c0d18f837a");
   private static final Set<String> done = Collections.newSetFromMap(new WeakHashMap());
   private static Thread thread;
   private static String name;
   private static final Map<String, Integer> tiers = new HashMap();
   private static Properties patreonTiers;
   public static int localPatronTier = 0;
   public static String featuredPatron = "";

   public ContributorRewardHandler() {
   }

   @SideOnly(Side.CLIENT)
   public static void setupClient() {
      name = Minecraft.getMinecraft().getSession().getUsername().toLowerCase();
   }

   public static void init() {
      if (thread == null || !thread.isAlive()) {
         thread = new ContributorRewardHandler.ThreadContributorListLoader();
      }
   }

   public static int getTier(EntityPlayer player) {
      return getTier(player.getName());
   }

   public static int getTier(String name) {
      return ((Integer)tiers.getOrDefault(name.toLowerCase(Locale.ROOT), Integer.valueOf(0))).intValue();
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
            ResourceLocation loc = new ResourceLocation("quark", "textures/misc/dev_cape.png");
            textures.put(Type.CAPE, loc);
            textures.put(Type.ELYTRA, loc);
            done.add(uuid);
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.SERVER)
   public static void onPlayerJoin(ClientConnectedToServerEvent event) {
      init();
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
      if (!allPatrons.isEmpty()) {
         featuredPatron = (String)allPatrons.get((int)(Math.random() * (double)allPatrons.size()));
      }

   }

   private static class ThreadContributorListLoader extends Thread {
      public ThreadContributorListLoader() {
         this.setName("Quark Contributor Loading Thread");
         this.setDaemon(true);
         this.start();
      }

      public void run() {
         try {
            URL url = new URL("https://raw.githubusercontent.com/Vazkii/Quark/master/contributors.properties");
            ContributorRewardHandler.patreonTiers = new Properties();
            InputStreamReader reader = new InputStreamReader(url.openStream());
            Throwable var3 = null;

            try {
               ContributorRewardHandler.patreonTiers.load(reader);
               ContributorRewardHandler.load(ContributorRewardHandler.patreonTiers);
            } catch (Throwable var13) {
               var3 = var13;
               throw var13;
            } finally {
               if (reader != null) {
                  if (var3 != null) {
                     try {
                        reader.close();
                     } catch (Throwable var12) {
                        var3.addSuppressed(var12);
                     }
                  } else {
                     reader.close();
                  }
               }

            }
         } catch (IOException var15) {
            var15.printStackTrace();
         }

      }
   }
}
