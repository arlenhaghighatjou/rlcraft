package net.silentchaos512.scalinghealth.utils;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.common.base.Predicate;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.silentchaos512.lib.util.AttributeHelper;
import net.silentchaos512.lib.util.EntityHelper;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.config.Config;
import net.silentchaos512.scalinghealth.lib.EnumAreaDifficultyMode;
import net.silentchaos512.scalinghealth.network.NetworkHandler;
import net.silentchaos512.scalinghealth.network.message.MessageDataSync;
import net.silentchaos512.scalinghealth.network.message.MessageWorldDataSync;
import net.silentchaos512.scalinghealth.scoreboard.SHScoreCriteria;
import net.silentchaos512.scalinghealth.world.ScalingHealthSavedData;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class SHPlayerDataHandler {
   private static final String NBT_ROOT = "scalinghealth_data";
   private static final Map<Integer, SHPlayerDataHandler.PlayerData> playerData = new ConcurrentHashMap();

   private SHPlayerDataHandler() {
   }

   @Nullable
   public static SHPlayerDataHandler.PlayerData get(EntityPlayer player) {
      if (player instanceof FakePlayer && !Config.FakePlayer.haveDifficulty) {
         return null;
      } else {
         int key = getKey(player);
         if (!playerData.containsKey(Integer.valueOf(key))) {
            playerData.put(Integer.valueOf(key), new SHPlayerDataHandler.PlayerData(player));
         }

         SHPlayerDataHandler.PlayerData data = (SHPlayerDataHandler.PlayerData)playerData.get(Integer.valueOf(key));
         if (data != null && data.playerWR.get() != player) {
            NBTTagCompound tags = new NBTTagCompound();
            data.writeToNBT(tags);
            playerData.remove(Integer.valueOf(key));
            data = get(player);

            assert data != null;

            data.readFromNBT(tags);
         }

         return data;
      }
   }

   private static void cleanup() {
      List<Integer> remove = new ArrayList();
      Iterator var1 = playerData.keySet().iterator();

      while(var1.hasNext()) {
         int i = ((Integer)var1.next()).intValue();
         SHPlayerDataHandler.PlayerData d = (SHPlayerDataHandler.PlayerData)playerData.get(Integer.valueOf(i));
         if (d != null && d.playerWR.get() == null) {
            remove.add(Integer.valueOf(i));
         }
      }

      var1 = remove.iterator();

      while(var1.hasNext()) {
         int i = ((Integer)var1.next()).intValue();
         playerData.remove(Integer.valueOf(i));
      }

   }

   private static int getKey(EntityPlayer player) {
      return player.hashCode() << 1 + (player.world.isRemote ? 1 : 0);
   }

   private static NBTTagCompound getDataCompoundForPlayer(EntityPlayer player) {
      NBTTagCompound forgeData = player.getEntityData();
      if (!forgeData.hasKey("PlayerPersisted")) {
         forgeData.setTag("PlayerPersisted", new NBTTagCompound());
      }

      NBTTagCompound persistentData = forgeData.getCompoundTag("PlayerPersisted");
      if (!persistentData.hasKey("scalinghealth_data")) {
         persistentData.setTag("scalinghealth_data", new NBTTagCompound());
      }

      return persistentData.getCompoundTag("scalinghealth_data");
   }

   public static class EventHandler {
      public EventHandler() {
      }

      public void register(EventBus bus) {
         bus.register(ServerTickEvent.class, this::onServerTick);
         bus.register(LivingUpdateEvent.class, this::onPlayerTick);
         bus.register(PlayerLoggedInEvent.class, this::onPlayerLogin);
      }

      public void onServerTick(ServerTickEvent event) {
         if (event.phase == Phase.END) {
            SHPlayerDataHandler.cleanup();
         }

      }

      public void onPlayerTick(LivingUpdateEvent event) {
         if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();
            SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
            if (data != null) {
               data.tick();
            }

            if (!player.world.isRemote && player.world.getTotalWorldTime() % 5L * (long)Config.PACKET_DELAY == 0L) {
               for(EntityPlayer p : getNearbyPlayers(player)) {
                  SHPlayerDataHandler.PlayerData data1 = SHPlayerDataHandler.get(p);
                  if (data1 != null) {
                     IMessage message = new MessageDataSync(data1, p);
                     NetworkHandler.INSTANCE.sendTo(message, (EntityPlayerMP)player);
                  }
               }
            }
         }

      }

      private static Collection<EntityPlayer> getNearbyPlayers(EntityPlayer center) {
         int radius = Config.Difficulty.searchRadius;
         int radiusSquared = radius <= 0 ? Integer.MAX_VALUE : radius * radius;
         BlockPos centerPos = center.getPosition();
         return center.world.getPlayers(EntityPlayer.class, (p) -> {
            return p != null && !p.equals(center) && p.getDistanceSq(centerPos) < (double)radiusSquared;
         });
      }

      public void onPlayerLogin(PlayerLoggedInEvent event) {
         if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP)event.player;
            SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(event.player);
            if (data != null) {
               IMessage message = new MessageDataSync(data, event.player);
               NetworkHandler.INSTANCE.sendTo(message, playerMP);
            } else {
               ScalingHealth.LOGGER.error("Player data for '{}' not found!", event.player.getName());
            }

            if (Config.Difficulty.AREA_DIFFICULTY_MODE == EnumAreaDifficultyMode.SERVER_WIDE) {
               IMessage message2 = new MessageWorldDataSync(ScalingHealthSavedData.get(event.player.world));
               NetworkHandler.INSTANCE.sendTo(message2, playerMP);
            }
         }

      }
   }

   public static class PlayerData {
      static final String NBT_DIFFICULTY = "difficulty";
      static final String NBT_HEALTH = "health";
      static final String NBT_MAX_HEALTH = "max_health";
      static final String NBT_LAST_LOGIN = "last_login";
      private static final EaglercraftUUID UUID_XP_HEALTH_BONUS = EaglercraftUUID.fromString("3d3cb1b5-03b0-496a-aaac-60bf63ba139b");
      double difficulty = 0.0D;
      float health = 20.0F;
      float maxHealth = (float)Config.Player.Health.startingHealth;
      Calendar lastTimePlayed = Calendar.getInstance();
      WeakReference<EntityPlayer> playerWR;
      private final boolean client;
      private int lastPosX = 0;
      private int lastPosY = 0;
      private int lastPosZ = 0;

      public PlayerData(EntityPlayer player) {
         this.playerWR = new WeakReference(player);
         this.client = player.world.isRemote;
         this.load();
      }

      public double getDifficulty() {
         return this.difficulty;
      }

      public void setDifficulty(double value) {
         EntityPlayer player = (EntityPlayer)this.playerWR.get();
         if (Config.Difficulty.DIFFICULTY_EXEMPT_PLAYERS.contains(player)) {
            this.difficulty = 0.0D;
         } else {
            this.difficulty = MathHelper.clamp(value, (double)Config.Difficulty.minValue, (double)Config.Difficulty.maxValue);
         }

         if (player != null) {
            SHScoreCriteria.updateScore(player, (int)this.difficulty);
         }

      }

      public void incrementDifficulty(double amount) {
         this.incrementDifficulty(amount, false);
      }

      public void incrementDifficulty(double amount, boolean alsoAffectWorldDifficulty) {
         EntityPlayer player = (EntityPlayer)this.playerWR.get();
         if (player != null) {
            if (!player.world.getGameRules().getBoolean("ScalingHealthDifficulty")) {
               return;
            }

            if (Config.Difficulty.DIMENSION_INCREASE_MULTIPLIER.containsKey(Integer.valueOf(player.dimension))) {
               amount *= (double)((Float)Config.Difficulty.DIMENSION_INCREASE_MULTIPLIER.get(Integer.valueOf(player.dimension))).floatValue();
            }
         }

         this.setDifficulty(this.difficulty + amount);
         if (alsoAffectWorldDifficulty && player != null) {
            ScalingHealthSavedData data = ScalingHealthSavedData.get(player.world);
            if (data != null) {
               data.difficulty += amount;
               data.markDirty();
            }
         }

      }

      public float getHealth() {
         return this.health;
      }

      public float getMaxHealth() {
         if (this.maxHealth < 2.0F) {
            this.maxHealth = (float)Config.Player.Health.startingHealth;
         }

         return this.maxHealth;
      }

      public void setMaxHealth(float value) {
         if (Config.Player.Health.allowModify) {
            int configMax = Config.Player.Health.maxHealth <= 0 ? Integer.MAX_VALUE : Config.Player.Health.maxHealth;
            this.maxHealth = MathHelper.clamp(value, 2.0F, (float)configMax);
            EntityPlayer player = (EntityPlayer)this.playerWR.get();
            if (player != null) {
               ModifierHandler.setMaxHealth(player, (double)this.maxHealth, 0);
            }

            this.save();
            this.sendUpdateMessage();
         }
      }

      public void incrementMaxHealth(float amount) {
         this.setMaxHealth(this.maxHealth + amount);
         EntityPlayer player = (EntityPlayer)this.playerWR.get();
         if (player != null) {
            int current = (int)player.getHealth();
            EntityHelper.heal(player, amount, Config.Items.Heart.healingEvent);
            int newHealth = (int)player.getHealth();
            if (current + (int)amount != newHealth) {
               ScalingHealth.logHelper.warn("Another mod seems to have canceled healing from a max health increase (player {})", new Object[]{player.getName()});
            }
         }

      }

      public Calendar getLastTimePlayed() {
         return this.lastTimePlayed;
      }

      private void tick() {
         if (!this.client) {
            EntityPlayer player = (EntityPlayer)this.playerWR.get();
            if (player == null) {
               return;
            }

            if (player.world.getTotalWorldTime() % 20L == 0L) {
               float amount = Config.Difficulty.perSecond;
               if (this.lastPosX == (int)player.posX && this.lastPosZ == (int)player.posZ) {
                  amount *= Config.Difficulty.idleMulti;
               }

               this.incrementDifficulty((double)amount, false);
               this.lastPosX = (int)player.posX;
               this.lastPosY = (int)player.posY;
               this.lastPosZ = (int)player.posZ;
               if (!Config.Player.Health.byXP.isEmpty()) {
                  int highestLevel = 0;
                  Iterator iter = Config.Player.Health.byXP.keySet().iterator();

                  while(iter.hasNext()) {
                     int key = ((Integer)iter.next()).intValue();
                     if (key > highestLevel && key <= player.experienceLevel) {
                        highestLevel = key;
                     }
                  }

                  if (Config.Player.Health.byXP.containsKey(Integer.valueOf(highestLevel))) {
                     float modAmount = (float)(((Integer)Config.Player.Health.byXP.get(Integer.valueOf(highestLevel))).intValue() - Config.Player.Health.startingHealth);
                     AttributeHelper.apply(player, SharedMonsterAttributes.MAX_HEALTH, UUID_XP_HEALTH_BONUS, "health_from_xp", (double)modAmount, 0);
                  } else {
                     AttributeHelper.remove(player, SharedMonsterAttributes.MAX_HEALTH, UUID_XP_HEALTH_BONUS);
                  }
               }
            }

            this.health = player.getHealth();
            if (player.world.getTotalWorldTime() % (long)Config.PACKET_DELAY == 0L) {
               this.save();
               this.sendUpdateMessage();
            }
         }

      }

      private void sendUpdateMessage() {
         if (!this.client) {
            EntityPlayer player = (EntityPlayer)this.playerWR.get();
            if (player != null) {
               EntityPlayerMP playerMP = (EntityPlayerMP)player;
               SHPlayerDataHandler.PlayerData data = SHPlayerDataHandler.get(player);
               if (data != null) {
                  IMessage message = new MessageDataSync(data, player);
                  NetworkHandler.INSTANCE.sendTo(message, playerMP);
                  if (Config.Difficulty.AREA_DIFFICULTY_MODE == EnumAreaDifficultyMode.SERVER_WIDE) {
                     IMessage message2 = new MessageWorldDataSync(ScalingHealthSavedData.get(player.world));
                     NetworkHandler.INSTANCE.sendTo(message2, playerMP);
                  }
               }
            }
         }

      }

      public void save() {
         if (!this.client) {
            EntityPlayer player = (EntityPlayer)this.playerWR.get();
            if (player != null) {
               NBTTagCompound tags = SHPlayerDataHandler.getDataCompoundForPlayer(player);
               this.writeToNBT(tags);
            }
         }

      }

      public void writeToNBT(NBTTagCompound tags) {
         tags.setDouble("difficulty", this.difficulty);
         tags.setFloat("health", this.health);
         tags.setFloat("max_health", this.maxHealth);
         int year = this.lastTimePlayed.get(1);
         int month = this.lastTimePlayed.get(2) + 1;
         int date = this.lastTimePlayed.get(5);
         String dateString = year + "/" + month + "/" + date;
         tags.setString("last_login", dateString);
      }

      public void load() {
         if (!this.client) {
            EntityPlayer player = (EntityPlayer)this.playerWR.get();
            if (player != null) {
               NBTTagCompound tags = SHPlayerDataHandler.getDataCompoundForPlayer(player);
               this.readFromNBT(tags);
            }
         }

      }

      public void readFromNBT(NBTTagCompound tags) {
         this.difficulty = tags.getDouble("difficulty");
         this.health = tags.getFloat("health");
         this.maxHealth = tags.getFloat("max_health");
         String lastDatePlayed = tags.getString("last_login");
         String[] dateParts = lastDatePlayed.split("/");
         if (dateParts.length >= 3) {
            try {
               int year = Integer.parseInt(dateParts[0]);
               int month = Integer.parseInt(dateParts[1]) - 1;
               int date = Integer.parseInt(dateParts[2]);
               this.lastTimePlayed.set(year, month, date);
            } catch (NumberFormatException var7) {
               ScalingHealth.logHelper.warn("Could not parse player's last login time.", new Object[0]);
               var7.printStackTrace();
            }
         }

      }
   }
}
