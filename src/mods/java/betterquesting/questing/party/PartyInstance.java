package betterquesting.questing.party;

import betterquesting.api.enums.EnumPartyStatus;
import betterquesting.api.properties.IPropertyContainer;
import betterquesting.api.properties.IPropertyType;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.party.IParty;
import betterquesting.core.BetterQuesting;
import betterquesting.storage.PropertyContainer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class PartyInstance implements IParty {
   private final HashMap<UUID, EnumPartyStatus> members = new HashMap();
   private List<UUID> memCache = null;
   private final PropertyContainer pInfo = new PropertyContainer();

   public PartyInstance() {
      this.setupProps();
   }

   private void setupProps() {
      this.setupValue(NativeProps.NAME, "New Party");
   }

   private <T> void setupValue(IPropertyType<T> prop) {
      this.setupValue(prop, prop.getDefault());
   }

   private <T> void setupValue(IPropertyType<T> prop, T def) {
      this.pInfo.setProperty(prop, this.pInfo.getProperty(prop, def));
   }

   private void refreshCache() {
      this.memCache = Collections.unmodifiableList(new ArrayList(this.members.keySet()));
   }

   public IPropertyContainer getProperties() {
      return this.pInfo;
   }

   public void kickUser(@Nonnull UUID uuid) {
      if (this.members.containsKey(uuid)) {
         EnumPartyStatus old = (EnumPartyStatus)this.members.get(uuid);
         this.members.remove(uuid);
         if (old == EnumPartyStatus.OWNER && this.members.size() > 0) {
            this.hostMigrate();
         }

         this.refreshCache();
      }
   }

   public void setStatus(@Nonnull UUID uuid, @Nonnull EnumPartyStatus priv) {
      EnumPartyStatus old = (EnumPartyStatus)this.members.get(uuid);
      if (old != priv) {
         this.members.put(uuid, priv);
         if (priv == EnumPartyStatus.OWNER) {
            for(UUID mem : this.getMembers()) {
               if (mem != uuid && this.members.get(mem) == EnumPartyStatus.OWNER) {
                  this.members.put(mem, EnumPartyStatus.ADMIN);
               }
            }
         } else if (old == EnumPartyStatus.OWNER) {
            UUID migrate = null;

            for(UUID mem : this.getMembers()) {
               if (mem != uuid) {
                  if (this.members.get(mem) == EnumPartyStatus.ADMIN) {
                     migrate = mem;
                     break;
                  }

                  if (migrate == null) {
                     migrate = mem;
                  }
               }
            }

            if (migrate == null) {
               this.members.put(uuid, old);
               return;
            }

            this.members.put(migrate, EnumPartyStatus.OWNER);
         }

         this.refreshCache();
      }
   }

   public EnumPartyStatus getStatus(@Nonnull UUID uuid) {
      return (EnumPartyStatus)this.members.get(uuid);
   }

   public List<UUID> getMembers() {
      if (this.memCache == null) {
         this.refreshCache();
      }

      return this.memCache;
   }

   private void hostMigrate() {
      for(Entry<UUID, EnumPartyStatus> entry : this.members.entrySet()) {
         if (entry.getValue() == EnumPartyStatus.OWNER) {
            return;
         }
      }

      UUID migrate = null;

      for(Entry<UUID, EnumPartyStatus> entry : this.members.entrySet()) {
         EnumPartyStatus status = (EnumPartyStatus)entry.getValue();
         if (status == EnumPartyStatus.ADMIN || status == EnumPartyStatus.OWNER) {
            migrate = (UUID)entry.getKey();
            break;
         }

         if (migrate == null) {
            migrate = (UUID)entry.getKey();
         }
      }

      if (migrate != null) {
         this.members.put(migrate, EnumPartyStatus.OWNER);
      } else {
         BetterQuesting.logger.error("Failed to find suitable host to migrate party " + (String)this.pInfo.getProperty(NativeProps.NAME));
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound json) {
      NBTTagList memJson = new NBTTagList();

      for(Entry<UUID, EnumPartyStatus> mem : this.members.entrySet()) {
         NBTTagCompound jm = new NBTTagCompound();
         jm.setString("uuid", ((UUID)mem.getKey()).toString());
         jm.setString("status", ((EnumPartyStatus)mem.getValue()).toString());
         memJson.appendTag(jm);
      }

      json.setTag("members", memJson);
      json.setTag("properties", this.pInfo.writeToNBT(new NBTTagCompound()));
      return json;
   }

   public void readFromNBT(NBTTagCompound jObj) {
      if (jObj.hasKey("properties", 10)) {
         this.pInfo.readFromNBT(jObj.getCompoundTag("properties"));
      } else {
         this.pInfo.readFromNBT(new NBTTagCompound());
         this.pInfo.setProperty(NativeProps.NAME, jObj.getString("name"));
      }

      this.members.clear();
      NBTTagList memList = jObj.getTagList("members", 10);

      for(int i = 0; i < memList.tagCount(); ++i) {
         try {
            NBTTagCompound jMem = memList.getCompoundTagAt(i);
            if (jMem.hasKey("uuid", 8) && jMem.hasKey("status")) {
               UUID uuid = UUID.fromString(jMem.getString("uuid"));
               EnumPartyStatus priv = EnumPartyStatus.valueOf(jMem.getString("status"));
               this.members.put(uuid, priv);
            }
         } catch (Exception var7) {
            ;
         }
      }

      this.refreshCache();
      this.setupProps();
   }

   public NBTTagCompound writeProperties(NBTTagCompound nbt) {
      return this.pInfo.writeToNBT(nbt);
   }

   public void readProperties(NBTTagCompound nbt) {
      this.pInfo.readFromNBT(nbt);
   }
}
