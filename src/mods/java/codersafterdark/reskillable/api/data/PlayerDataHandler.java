package codersafterdark.reskillable.api.data;

import codersafterdark.reskillable.api.requirement.RequirementCache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class PlayerDataHandler {
   private static final String DATA_TAG = "SkillableData";
   private static HashMap<Integer, PlayerData> playerData = new HashMap();

   public PlayerDataHandler() {
   }

   public static PlayerData get(EntityPlayer player) {
      if (player == null) {
         return null;
      } else {
         int key = getKey(player);
         if (!playerData.containsKey(Integer.valueOf(key))) {
            playerData.put(Integer.valueOf(key), new PlayerData(player));
         }

         PlayerData data = (PlayerData)playerData.get(Integer.valueOf(key));
         if (data.playerWR.get() != player) {
            NBTTagCompound cmp = new NBTTagCompound();
            data.saveToNBT(cmp);
            RequirementCache.removeCache(player.getUniqueID(), player.getEntityWorld().isRemote);
            playerData.remove(Integer.valueOf(key));
            data = get(player);
            data.loadFromNBT(cmp);
         }

         return data;
      }
   }

   public static void cleanup() {
      List<Integer> removals = new ArrayList();

      for(Entry<Integer, PlayerData> item : playerData.entrySet()) {
         PlayerData d = (PlayerData)item.getValue();
         if (d != null && d.playerWR.get() == null) {
            removals.add(item.getKey());
         }
      }

      removals.forEach((i) -> {
         PlayerData var10000 = (PlayerData)playerData.remove(i);
      });
   }

   private static int getKey(EntityPlayer player) {
      return player == null ? 0 : player.hashCode() << 1 + (player.getEntityWorld().isRemote ? 1 : 0);
   }

   public static NBTTagCompound getDataCompoundForPlayer(EntityPlayer player) {
      NBTTagCompound forgeData = player.getEntityData();
      if (!forgeData.hasKey("PlayerPersisted")) {
         forgeData.setTag("PlayerPersisted", new NBTTagCompound());
      }

      NBTTagCompound persistentData = forgeData.getCompoundTag("PlayerPersisted");
      if (!persistentData.hasKey("SkillableData")) {
         persistentData.setTag("SkillableData", new NBTTagCompound());
      }

      return persistentData.getCompoundTag("SkillableData");
   }

   public static class EventHandler {
      public EventHandler() {
      }

      @SubscribeEvent
      public static void onServerTick(ServerTickEvent event) {
         if (event.phase == Phase.END) {
            PlayerDataHandler.cleanup();
         }

      }

      @SubscribeEvent
      public static void onPlayerLogin(PlayerLoggedInEvent event) {
         PlayerData data = PlayerDataHandler.get(event.player);
         if (data != null) {
            data.sync();
            data.getRequirementCache().forceClear();
         }

      }

      @SubscribeEvent
      public static void onPlayerTick(PlayerTickEvent event) {
         if (event.phase == Phase.END) {
            PlayerData data = PlayerDataHandler.get(event.player);
            if (data != null) {
               data.tickPlayer(event);
            }
         }

      }

      @SubscribeEvent
      public static void onBlockDrops(HarvestDropsEvent event) {
         PlayerData data = PlayerDataHandler.get(event.getHarvester());
         if (data != null) {
            data.blockDrops(event);
         }

      }

      @SubscribeEvent
      public static void onGetBreakSpeed(BreakSpeed event) {
         PlayerData data = PlayerDataHandler.get(event.getEntityPlayer());
         if (data != null) {
            data.breakSpeed(event);
         }

      }

      @SubscribeEvent
      public static void onMobDrops(LivingDropsEvent event) {
         if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            PlayerData data = PlayerDataHandler.get((EntityPlayer)event.getSource().getTrueSource());
            if (data != null) {
               data.mobDrops(event);
            }
         }

      }

      @SubscribeEvent
      public static void onHurt(LivingHurtEvent event) {
         if (event.getEntity() instanceof EntityPlayer) {
            PlayerData data = PlayerDataHandler.get((EntityPlayer)event.getEntity());
            if (data != null) {
               data.hurt(event);
            }
         }

         if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            PlayerData data = PlayerDataHandler.get((EntityPlayer)event.getSource().getTrueSource());
            if (data != null) {
               data.attackMob(event);
            }
         }

      }

      @SubscribeEvent
      public static void onRightClickBlock(RightClickBlock event) {
         PlayerData data = PlayerDataHandler.get(event.getEntityPlayer());
         if (data != null) {
            data.rightClickBlock(event);
         }

      }

      @SubscribeEvent
      public static void onEnderTeleport(EnderTeleportEvent event) {
         if (event.getEntity() instanceof EntityPlayer) {
            PlayerData data = PlayerDataHandler.get((EntityPlayer)event.getEntity());
            if (data != null) {
               data.enderTeleport(event);
            }
         }

      }

      @SubscribeEvent
      public static void onMobDeath(LivingDeathEvent event) {
         if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            PlayerData data = PlayerDataHandler.get((EntityPlayer)event.getSource().getTrueSource());
            if (data != null) {
               data.killMob(event);
            }
         }

      }
   }
}
