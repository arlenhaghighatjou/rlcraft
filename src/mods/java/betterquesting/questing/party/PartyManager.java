package betterquesting.questing.party;

import betterquesting.api.enums.EnumPartyStatus;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.party.IParty;
import betterquesting.api.questing.party.IPartyDatabase;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.storage.SimpleDatabase;
import betterquesting.storage.QuestSettings;
import java.util.HashMap;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class PartyManager extends SimpleDatabase<IParty> implements IPartyDatabase {
   public static final PartyManager INSTANCE = new PartyManager();
   private final HashMap<EaglercraftUUID, Integer> partyCache = new HashMap();

   public PartyManager() {
   }

   public synchronized IParty createNew(int id) {
      IParty party = new PartyInstance();
      if (id >= 0) {
         this.add(id, party);
      }

      return party;
   }

   @Nullable
   public synchronized DBEntry<IParty> getParty(@Nonnull EaglercraftUUID uuid) {
      if (!((Boolean)QuestSettings.INSTANCE.getProperty(NativeProps.PARTY_ENABLE)).booleanValue()) {
         return null;
      } else {
         Integer cachedID = (Integer)this.partyCache.get(uuid);
         IParty cachedParty = cachedID == null ? null : (IParty)this.getValue(cachedID.intValue());
         if (cachedID != null && cachedParty == null) {
            this.partyCache.remove(uuid);
         } else if (cachedParty != null) {
            EnumPartyStatus status = cachedParty.getStatus(uuid);
            if (status != null) {
               return new DBEntry<IParty>(cachedID.intValue(), cachedParty);
            }

            this.partyCache.remove(uuid);
         }

         for(DBEntry<IParty> entry : this.getEntries()) {
            EnumPartyStatus status = ((IParty)entry.getValue()).getStatus(uuid);
            if (status != null) {
               this.partyCache.put(uuid, Integer.valueOf(entry.getID()));
               return entry;
            }
         }

         return null;
      }
   }

   public synchronized NBTTagList writeToNBT(NBTTagList json, List<Integer> subset) {
      for(DBEntry<IParty> entry : this.getEntries()) {
         if (subset == null || subset.contains(Integer.valueOf(entry.getID()))) {
            NBTTagCompound jp = (NBTTagCompound)((IParty)entry.getValue()).writeToNBT(new NBTTagCompound());
            jp.setInteger("partyID", entry.getID());
            json.appendTag(jp);
         }
      }

      return json;
   }

   public synchronized void readFromNBT(NBTTagList json, boolean merge) {
      if (!merge) {
         this.reset();
      }

      for(int i = 0; i < json.tagCount(); ++i) {
         NBTTagCompound jp = json.getCompoundTagAt(i);
         int partyID = jp.hasKey("partyID", 99) ? jp.getInteger("partyID") : -1;
         if (partyID >= 0) {
            IParty party = new PartyInstance();
            party.readFromNBT(jp);
            if (party.getMembers().size() > 0) {
               this.add(partyID, party);
            }
         }
      }

   }

   public synchronized void reset() {
      super.reset();
      this.partyCache.clear();
   }
}
