package betterquesting.questing.party;

import betterquesting.api.enums.EnumPartyStatus;
import betterquesting.api.questing.party.IParty;
import betterquesting.api2.storage.INBTPartial;
import betterquesting.core.BetterQuesting;
import betterquesting.network.handlers.NetInviteSync;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class PartyInvitations implements INBTPartial<NBTTagList, EaglercraftUUID> {
   public static final PartyInvitations INSTANCE = new PartyInvitations();
   private final HashMap<EaglercraftUUID, HashMap<Integer, Long>> invites = new HashMap();

   public PartyInvitations() {
   }

   public synchronized void postInvite(@Nonnull EaglercraftUUID uuid, int id, long expiryTime) {
      if (expiryTime <= 0L) {
         BetterQuesting.logger.error("Received an invite that has already expired!");
      } else {
         IParty party = (IParty)PartyManager.INSTANCE.getValue(id);
         if (party != null && party.getStatus(uuid) == null) {
            HashMap<Integer, Long> list = (HashMap)this.invites.computeIfAbsent(uuid, (key) -> {
               return new HashMap();
            });
            list.put(Integer.valueOf(id), Long.valueOf(System.currentTimeMillis() + expiryTime));
         }
      }
   }

   public synchronized boolean acceptInvite(@Nonnull EaglercraftUUID uuid, int id) {
      HashMap<Integer, Long> userInvites = (HashMap)this.invites.get(uuid);
      if (userInvites != null && userInvites.size() > 0) {
         long timestamp = ((Long)userInvites.get(Integer.valueOf(id))).longValue();
         IParty party = (IParty)PartyManager.INSTANCE.getValue(id);
         boolean valid = timestamp > System.currentTimeMillis();
         if (valid && party != null) {
            party.setStatus(uuid, EnumPartyStatus.MEMBER);
         }

         userInvites.remove(Integer.valueOf(id));
         if (userInvites.size() <= 0) {
            this.invites.remove(uuid);
         }

         return valid;
      } else {
         return false;
      }
   }

   public synchronized void revokeInvites(@Nonnull EaglercraftUUID uuid, int... ids) {
      HashMap<Integer, Long> userInvites = (HashMap)this.invites.get(uuid);
      if (userInvites != null && userInvites.size() > 0) {
         for(int i : ids) {
            userInvites.remove(Integer.valueOf(i));
         }

         if (userInvites.size() <= 0) {
            this.invites.remove(uuid);
         }

      }
   }

   public synchronized List<Entry<Integer, Long>> getPartyInvites(@Nonnull EaglercraftUUID uuid) {
      HashMap<Integer, Long> userInvites = (HashMap)this.invites.get(uuid);
      if (userInvites != null && userInvites.size() > 0) {
         List<Entry<Integer, Long>> list = new ArrayList(userInvites.entrySet());
         list.sort(Comparator.comparing(Entry::getValue));
         return list;
      } else {
         return Collections.emptyList();
      }
   }

   public synchronized void purgeInvites(int partyID) {
      this.invites.values().forEach((value) -> {
         Long var10000 = (Long)value.remove(Integer.valueOf(partyID));
      });
   }

   public synchronized void cleanExpired() {
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
      Iterator<Entry<EaglercraftUUID, HashMap<Integer, Long>>> iterA = this.invites.entrySet().iterator();

      while(iterA.hasNext()) {
         Entry<EaglercraftUUID, HashMap<Integer, Long>> userInvites = (Entry)iterA.next();
         List<Integer> revoked = new ArrayList();
         Iterator<Entry<Integer, Long>> iterB = ((HashMap)userInvites.getValue()).entrySet().iterator();

         while(iterB.hasNext()) {
            Entry<Integer, Long> entry = (Entry)iterB.next();
            if (((Long)entry.getValue()).longValue() < System.currentTimeMillis()) {
               revoked.add(entry.getKey());
               iterB.remove();
            }
         }

         EntityPlayerMP player = server.getPlayerList().getPlayerByUUID((EaglercraftUUID)userInvites.getKey());
         if (player != null && revoked.size() >= 0) {
            int[] revAry = new int[revoked.size()];

            for(int i = 0; i < revoked.size(); ++i) {
               revAry[i] = ((Integer)revoked.get(i)).intValue();
            }

            NetInviteSync.sendRevoked(player, revAry);
         }

         if (((HashMap)userInvites.getValue()).size() <= 0) {
            iterA.remove();
         }
      }

   }

   public synchronized void reset() {
      this.invites.clear();
   }

   public synchronized NBTTagList writeToNBT(NBTTagList nbt, @Nullable List<EaglercraftUUID> subset) {
      if (subset != null) {
         subset.forEach((uuid) -> {
            NBTTagCompound userTag = new NBTTagCompound();
            userTag.setString("uuid", uuid.toString());
            Map<Integer, Long> userMap = (Map)this.invites.get(uuid);
            if (userMap == null) {
               userMap = Collections.emptyMap();
            }

            NBTTagList invList = new NBTTagList();

            for(Entry<Integer, Long> invEntry : userMap.entrySet()) {
               NBTTagCompound invTag = new NBTTagCompound();
               invTag.setInteger("partyID", ((Integer)invEntry.getKey()).intValue());
               invTag.setLong("expiry", ((Long)invEntry.getValue()).longValue());
               invList.appendTag(invTag);
            }

            userTag.setTag("invites", invList);
            nbt.appendTag(userTag);
         });
      } else {
         for(Entry<EaglercraftUUID, HashMap<Integer, Long>> userMap : this.invites.entrySet()) {
            NBTTagCompound userTag = new NBTTagCompound();
            userTag.setString("uuid", ((EaglercraftUUID)userMap.getKey()).toString());
            NBTTagList invList = new NBTTagList();

            for(Entry<Integer, Long> invEntry : ((HashMap)userMap.getValue()).entrySet()) {
               NBTTagCompound invTag = new NBTTagCompound();
               invTag.setInteger("partyID", ((Integer)invEntry.getKey()).intValue());
               invTag.setLong("expiry", ((Long)invEntry.getValue()).longValue());
               invList.appendTag(invTag);
            }

            userTag.setTag("invites", invList);
            nbt.appendTag(userTag);
         }
      }

      return nbt;
   }

   public synchronized void readFromNBT(NBTTagList nbt, boolean merge) {
      if (!merge) {
         this.invites.clear();
      }

      for(int i = 0; i < nbt.tagCount(); ++i) {
         NBTTagCompound userEntry = nbt.getCompoundTagAt(i);

         EaglercraftUUID uuid;
         try {
            uuid = EaglercraftUUID.fromString(userEntry.getString("uuid"));
         } catch (Exception var13) {
            continue;
         }

         NBTTagList invList = userEntry.getTagList("invites", 10);
         HashMap<Integer, Long> map = (HashMap)this.invites.compute(uuid, (key, old) -> {
            return new HashMap();
         });
         map.clear();

         for(int n = 0; n < invList.tagCount(); ++n) {
            NBTTagCompound invEntry = invList.getCompoundTagAt(n);
            int partyID = invEntry.hasKey("partyID", 99) ? invEntry.getInteger("partyID") : -1;
            long timestamp = invEntry.hasKey("expiry", 99) ? invEntry.getLong("expiry") : -1L;
            if (partyID >= 0) {
               map.put(Integer.valueOf(partyID), Long.valueOf(timestamp));
            }
         }
      }

   }
}
