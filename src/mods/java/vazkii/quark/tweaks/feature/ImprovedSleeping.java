package vazkii.quark.tweaks.feature;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Pre;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.message.MessageSpamlessChat;
import vazkii.quark.base.network.message.MessageUpdateAfk;

public class ImprovedSleeping extends Feature {
   private int timeSinceKeystroke;
   private static List<String> sleepingPlayers = new ArrayList();
   private static boolean enableAfk;
   private static int afkTime;
   private static int percentReq;
   private static final String TAG_JUST_SLEPT = "quark:slept";
   private static final String TAG_AFK = "quark:afk";
   private static final int AFK_MSG = "quark afk".hashCode();
   private static final int SLEEP_MSG = "quark sleep".hashCode();

   public ImprovedSleeping() {
   }

   public void setupConfig() {
      enableAfk = this.loadPropBool("Enable AFK", "", true);
      afkTime = this.loadPropInt("Time for AFK", "How many ticks are required for a player to be marked AFK", 2400);
      percentReq = this.loadPropInt("Required Percentage", "The percentage of the (non-afk) server that needs to be sleeping for the time to change.", 100);
   }

   public static void updateAfk(EntityPlayer player, boolean afk) {
      if (ModuleLoader.isFeatureEnabled(ImprovedSleeping.class) && enableAfk) {
         if (player.world.playerEntities.size() != 1) {
            if (afk) {
               player.getEntityData().setBoolean("quark:afk", true);
               TextComponentTranslation text = new TextComponentTranslation("quarkmisc.nowAfk", new Object[0]);
               text.getStyle().setColor(TextFormatting.AQUA);
               MessageSpamlessChat.sendToPlayer(player, AFK_MSG, text);
            } else {
               player.getEntityData().setBoolean("quark:afk", false);
               TextComponentTranslation text = new TextComponentTranslation("quarkmisc.leftAfk", new Object[0]);
               text.getStyle().setColor(TextFormatting.AQUA);
               MessageSpamlessChat.sendToPlayer(player, AFK_MSG, text);
            }
         }

      }
   }

   public static int isEveryoneAsleep(World world) {
      if (!ModuleLoader.isFeatureEnabled(ImprovedSleeping.class)) {
         return 0;
      } else {
         Pair<Integer, Integer> counts = getPlayerCounts(world);
         int legitPlayers = ((Integer)counts.getLeft()).intValue();
         int sleepingPlayers = ((Integer)counts.getRight()).intValue();
         int reqPlayers = (int)((float)percentReq / 100.0F * (float)legitPlayers);
         boolean everybody = legitPlayers > 0 && (float)sleepingPlayers / (float)reqPlayers >= 1.0F;
         return everybody ? 2 : 1;
      }
   }

   public static void whenNightPasses(WorldServer world) {
      if (ModuleLoader.isFeatureEnabled(ImprovedSleeping.class)) {
         MinecraftServer server = world.getMinecraftServer();
         if (server != null) {
            if (world.playerEntities.size() != 1) {
               boolean isDay = (double)world.getCelestialAngle(0.0F) < 0.5D;
               List<String> sleepingPlayers = new ArrayList();
               List<String> nonSleepingPlayers = new ArrayList();

               for(EntityPlayer player : world.playerEntities) {
                  if (doesPlayerCountForSleeping(player)) {
                     String name = player.getName();
                     if (isPlayerSleeping(player)) {
                        sleepingPlayers.add(name);
                        player.getEntityData().setBoolean("quark:slept", true);
                     } else {
                        nonSleepingPlayers.add(name);
                     }
                  }
               }

               ITextComponent sleepingList = new TextComponentString("");

               for(String s : sleepingPlayers) {
                  sleepingList.appendSibling((new TextComponentString("\n\u2714 " + s)).setStyle((new Style()).setColor(TextFormatting.GREEN)));
               }

               for(String s : nonSleepingPlayers) {
                  sleepingList.appendSibling((new TextComponentString("\n\u2718 " + s)).setStyle((new Style()).setColor(TextFormatting.RED)));
               }

               ITextComponent hoverText = new TextComponentTranslation(isDay ? "quarkmisc.nappingListHeader" : "quarkmisc.sleepingListHeader", new Object[]{sleepingList});
               ITextComponent sibling = new TextComponentString("(" + sleepingPlayers.size() + "/" + sleepingPlayers.size() + ")");
               sibling.getStyle().setHoverEvent(new HoverEvent(Action.SHOW_TEXT, hoverText.createCopy()));
               ITextComponent message = new TextComponentTranslation(isDay ? "quarkmisc.dayHasPassed" : "quarkmisc.nightHasPassed", new Object[0]);
               message.appendText(" ").appendSibling(sibling);
               message.getStyle().setColor(TextFormatting.GOLD);

               for(EntityPlayerMP player : server.getPlayerList().getPlayers()) {
                  MessageSpamlessChat.sendToPlayer(player, SLEEP_MSG, message);
               }

            }
         }
      }
   }

   private static boolean doesPlayerCountForSleeping(EntityPlayer player) {
      return !player.isSpectator() && !player.getEntityData().getBoolean("quark:afk");
   }

   private static boolean isPlayerSleeping(EntityPlayer player) {
      return player.isPlayerFullyAsleep();
   }

   private static Pair<Integer, Integer> getPlayerCounts(World world) {
      int legitPlayers = 0;
      int sleepingPlayers = 0;

      for(EntityPlayer player : world.playerEntities) {
         if (doesPlayerCountForSleeping(player)) {
            ++legitPlayers;
            if (isPlayerSleeping(player)) {
               ++sleepingPlayers;
            }
         }
      }

      return Pair.of(Integer.valueOf(legitPlayers), Integer.valueOf(sleepingPlayers));
   }

   @SubscribeEvent
   public void onWorldTick(WorldTickEvent event) {
      World world = event.world;
      MinecraftServer server = world.getMinecraftServer();
      if (event.side != Side.CLIENT && world.provider.getDimension() == 0 && event.phase == Phase.END && server != null) {
         List<String> sleepingPlayers = new ArrayList();
         List<String> newSleepingPlayers = new ArrayList();
         List<String> wasSleepingPlayers = new ArrayList();
         List<String> nonSleepingPlayers = new ArrayList();
         int legitPlayers = 0;

         for(EntityPlayer player : world.playerEntities) {
            if (doesPlayerCountForSleeping(player)) {
               String name = player.getName();
               if (isPlayerSleeping(player)) {
                  if (!sleepingPlayers.contains(name)) {
                     newSleepingPlayers.add(name);
                  }

                  sleepingPlayers.add(name);
               } else {
                  if (sleepingPlayers.contains(name) && player.getEntityData().getBoolean("quark:slept")) {
                     wasSleepingPlayers.add(name);
                  }

                  nonSleepingPlayers.add(name);
               }

               ++legitPlayers;
            }

            player.getEntityData().removeTag("quark:slept");
         }

         sleepingPlayers = sleepingPlayers;
         if ((!newSleepingPlayers.isEmpty() || !wasSleepingPlayers.isEmpty()) && world.playerEntities.size() != 1) {
            boolean isDay = (double)world.getCelestialAngle(0.0F) < 0.5D;
            int requiredPlayers = Math.max((int)Math.ceil((double)((float)(legitPlayers * percentReq) / 100.0F)), 0);
            ITextComponent sibling = new TextComponentString("(" + sleepingPlayers.size() + "/" + requiredPlayers + ")");
            ITextComponent sleepingList = new TextComponentString("");

            for(String s : sleepingPlayers) {
               sleepingList.appendSibling((new TextComponentString("\n\u2714 " + s)).setStyle((new Style()).setColor(TextFormatting.GREEN)));
            }

            for(String s : nonSleepingPlayers) {
               sleepingList.appendSibling((new TextComponentString("\n\u2718 " + s)).setStyle((new Style()).setColor(TextFormatting.RED)));
            }

            ITextComponent hoverText = new TextComponentTranslation("quarkmisc.sleepingListHeader", new Object[]{sleepingList});
            HoverEvent hover = new HoverEvent(Action.SHOW_TEXT, hoverText.createCopy());
            sibling.getStyle().setHoverEvent(hover);
            sibling.getStyle().setUnderlined(Boolean.valueOf(true));
            String newPlayer = newSleepingPlayers.isEmpty() ? (String)wasSleepingPlayers.get(0) : (String)newSleepingPlayers.get(0);
            String translationKey = isDay ? (newSleepingPlayers.isEmpty() ? "quarkmisc.personNotNapping" : "quarkmisc.personNapping") : (newSleepingPlayers.isEmpty() ? "quarkmisc.personNotSleeping" : "quarkmisc.personSleeping");
            ITextComponent message = new TextComponentTranslation(translationKey, new Object[]{newPlayer});
            message.getStyle().setColor(TextFormatting.GOLD);
            message.appendText(" ");
            message.appendSibling(sibling.createCopy());

            for(EntityPlayerMP player : server.getPlayerList().getPlayers()) {
               MessageSpamlessChat.sendToPlayer(player, SLEEP_MSG, message);
            }
         }

      }
   }

   @SubscribeEvent
   public void onPlayerLogout(PlayerLoggedOutEvent event) {
      World logoutWorld = event.player.world;
      List<EntityPlayer> players = logoutWorld.playerEntities;
      if (players.size() == 1) {
         EntityPlayer lastPlayer = (EntityPlayer)players.get(0);
         if (lastPlayer.getEntityData().getBoolean("quark:afk")) {
            lastPlayer.getEntityData().setBoolean("quark:afk", false);
            TextComponentTranslation text = new TextComponentTranslation("quarkmisc.leftAfk", new Object[0]);
            text.getStyle().setColor(TextFormatting.AQUA);
            if (lastPlayer instanceof EntityPlayerMP) {
               MessageSpamlessChat.sendToPlayer(lastPlayer, AFK_MSG, text);
            }
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         ++this.timeSinceKeystroke;
         if (this.timeSinceKeystroke == afkTime) {
            NetworkHandler.INSTANCE.sendToServer(new MessageUpdateAfk(true));
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onKeystroke(KeyInputEvent event) {
      this.registerPress();
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onKeystroke(Pre event) {
      this.registerPress();
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onPlayerClick(PlayerInteractEvent event) {
      this.registerPress();
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onMousePress(MouseInputEvent event) {
      this.registerPress();
   }

   private void registerPress() {
      if (this.timeSinceKeystroke >= afkTime) {
         NetworkHandler.INSTANCE.sendToServer(new MessageUpdateAfk(false));
      }

      this.timeSinceKeystroke = 0;
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public String[] getIncompatibleMods() {
      return new String[]{"morpheus", "sleepingoverhaul"};
   }
}
