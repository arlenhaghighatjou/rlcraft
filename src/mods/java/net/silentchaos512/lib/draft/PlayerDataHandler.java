package net.silentchaos512.lib.draft;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.silentchaos512.lib.tile.SyncVariable;

public abstract class PlayerDataHandler<Data extends PlayerDataHandler.PlayerData> {
   private final String nbtRootKey;
   private final Map<Integer, Data> playerData = new HashMap();
   private Data emptyData;

   protected PlayerDataHandler(ResourceLocation nbtRootKey) {
      this.nbtRootKey = nbtRootKey.toString().replace(':', '_');
      MinecraftForge.EVENT_BUS.register(new PlayerDataHandler.EventHandler(this));
   }

   public abstract <T extends PlayerDataHandler> T instance();

   protected abstract Data createData(@Nullable EntityPlayer var1);

   public Data get(EntityPlayer player) {
      if (player != null && !(player instanceof FakePlayer)) {
         int key = getKey(player);
         if (!this.playerData.containsKey(Integer.valueOf(key))) {
            this.playerData.put(Integer.valueOf(key), this.createData(player));
         }

         Data data = this.playerData.get(Integer.valueOf(key));
         if (data != null && data.getPlayer() != player) {
            NBTTagCompound tags = new NBTTagCompound();
            data.writeToNBT(tags);
            this.playerData.remove(Integer.valueOf(key));
            data = this.get(player);
            data.readFromNBT(tags);
         }

         return data;
      } else {
         if (this.emptyData == null) {
            this.emptyData = this.createData((EntityPlayer)null);
         }

         return this.emptyData;
      }
   }

   private void cleanup() {
      List<Integer> toRemove = new ArrayList();
      Iterator var2 = this.playerData.keySet().iterator();

      while(var2.hasNext()) {
         int key = ((Integer)var2.next()).intValue();
         Data data = this.playerData.get(Integer.valueOf(key));
         if (data != null && !data.isValid()) {
            toRemove.add(Integer.valueOf(key));
         }
      }

      var2 = toRemove.iterator();

      while(var2.hasNext()) {
         int key = ((Integer)var2.next()).intValue();
         this.playerData.remove(Integer.valueOf(key));
      }

   }

   private static int getKey(EntityPlayer player) {
      return player.hashCode() << 1 + (player.world.isRemote ? 1 : 0);
   }

   private NBTTagCompound getDataCompoundForPlayer(EntityPlayer player) {
      NBTTagCompound forgeData = player.getEntityData();
      if (!forgeData.hasKey("PlayerPersisted")) {
         forgeData.setTag("PlayerPersisted", new NBTTagCompound());
      }

      NBTTagCompound persistentData = forgeData.getCompoundTag("PlayerPersisted");
      if (!persistentData.hasKey(this.nbtRootKey)) {
         persistentData.setTag(this.nbtRootKey, new NBTTagCompound());
      }

      return persistentData.getCompoundTag(this.nbtRootKey);
   }

   public static class EventHandler {
      private final PlayerDataHandler<?> handler;

      private EventHandler(PlayerDataHandler<?> handler) {
         this.handler = handler;
      }

      @SubscribeEvent
      public void onServerTick(ServerTickEvent event) {
         if (event.phase == Phase.END) {
            this.handler.cleanup();
         }

      }

      @SubscribeEvent
      public void onPlayerTick(LivingUpdateEvent event) {
         if (event.getEntityLiving() instanceof EntityPlayer) {
            this.handler.get((EntityPlayer)event.getEntityLiving()).tick();
         }

      }

      @SubscribeEvent
      public void onPlayerLogin(PlayerLoggedInEvent event) {
         if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP var2 = (EntityPlayerMP)event.player;
         }

      }
   }

   public abstract class PlayerData {
      private WeakReference<EntityPlayer> playerWR;
      protected final boolean client;

      protected PlayerData(EntityPlayer player) {
         this.playerWR = new WeakReference(player);
         this.client = player.world.isRemote;
         this.load();
      }

      @Nullable
      public final EntityPlayer getPlayer() {
         return (EntityPlayer)this.playerWR.get();
      }

      public boolean isValid() {
         return this.playerWR != null;
      }

      public abstract void tick();

      protected void sendUpdateMessage() {
         if (!this.client) {
            EntityPlayer var1 = (EntityPlayer)this.playerWR.get();
         }

      }

      public final void save() {
         if (!this.client) {
            EntityPlayer player = (EntityPlayer)this.playerWR.get();
            if (player != null) {
               NBTTagCompound tags = PlayerDataHandler.this.getDataCompoundForPlayer(player);
               this.writeToNBT(tags);
               SyncVariable.Helper.writeSyncVars(this, tags, SyncVariable.Type.WRITE);
            }
         }

      }

      public abstract void writeToNBT(NBTTagCompound var1);

      public final void load() {
         if (!this.client) {
            EntityPlayer player = (EntityPlayer)this.playerWR.get();
            if (player != null) {
               NBTTagCompound tags = PlayerDataHandler.this.getDataCompoundForPlayer(player);
               this.readFromNBT(tags);
               SyncVariable.Helper.readSyncVars(this, tags);
            }
         }

      }

      public abstract void readFromNBT(NBTTagCompound var1);
   }
}
