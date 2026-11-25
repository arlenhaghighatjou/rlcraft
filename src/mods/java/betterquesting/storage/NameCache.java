package betterquesting.storage;

import betterquesting.api.storage.INameCache;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public final class NameCache implements INameCache {
   public static final NameCache INSTANCE = new NameCache();
   private final HashMap<UUID, NBTTagCompound> cache = new HashMap();
   private List<String> nameCache = null;

   public NameCache() {
   }

   public synchronized boolean updateName(@Nonnull EntityPlayerMP player) {
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
      NBTTagCompound tag = (NBTTagCompound)this.cache.computeIfAbsent(player.getGameProfile().getId(), (key) -> {
         return new NBTTagCompound();
      });
      String name = player.getGameProfile().getName();
      boolean isOP = server.getPlayerList().canSendCommands(player.getGameProfile());
      if (tag.getString("name").equals(name) && tag.getBoolean("isOP") == isOP) {
         return false;
      } else {
         tag.setString("name", name);
         tag.setBoolean("isOP", isOP);
         return true;
      }
   }

   public synchronized String getName(@Nonnull UUID uuid) {
      NBTTagCompound tag = (NBTTagCompound)this.cache.get(uuid);
      return tag == null ? uuid.toString() : tag.getString("name");
   }

   public synchronized UUID getUUID(@Nonnull String name) {
      for(Entry<UUID, NBTTagCompound> entry : this.cache.entrySet()) {
         if (((NBTTagCompound)entry.getValue()).getString("name").equalsIgnoreCase(name)) {
            return (UUID)entry.getKey();
         }
      }

      return null;
   }

   public synchronized boolean isOP(@Nonnull UUID uuid) {
      NBTTagCompound tag = (NBTTagCompound)this.cache.get(uuid);
      return tag != null && tag.getBoolean("isOP");
   }

   public synchronized int size() {
      return this.cache.size();
   }

   public synchronized NBTTagList writeToNBT(NBTTagList nbt, @Nullable List<UUID> users) {
      for(Entry<UUID, NBTTagCompound> entry : this.cache.entrySet()) {
         if (users == null || users.contains(entry.getKey())) {
            NBTTagCompound jn = new NBTTagCompound();
            jn.setString("uuid", ((UUID)entry.getKey()).toString());
            jn.setString("name", ((NBTTagCompound)entry.getValue()).getString("name"));
            jn.setBoolean("isOP", ((NBTTagCompound)entry.getValue()).getBoolean("isOP"));
            nbt.appendTag(jn);
         }
      }

      return nbt;
   }

   public synchronized void readFromNBT(NBTTagList nbt, boolean merge) {
      if (!merge) {
         this.cache.clear();
      }

      for(int i = 0; i < nbt.tagCount(); ++i) {
         NBTTagCompound jn = nbt.getCompoundTagAt(i);

         try {
            UUID uuid = UUID.fromString(jn.getString("uuid"));
            String name = jn.getString("name");
            boolean isOP = jn.getBoolean("isOP");
            NBTTagCompound j2 = new NBTTagCompound();
            j2.setString("name", name);
            j2.setBoolean("isOP", isOP);
            this.cache.put(uuid, j2);
         } catch (Exception var9) {
            ;
         }
      }

   }

   public synchronized void reset() {
      this.cache.clear();
      this.nameCache = null;
   }

   public synchronized List<String> getAllNames() {
      if (this.nameCache != null) {
         return this.nameCache;
      } else {
         this.nameCache = new ArrayList();

         for(NBTTagCompound tag : this.cache.values()) {
            if (tag != null && tag.hasKey("name", 8)) {
               this.nameCache.add(tag.getString("name"));
            }
         }

         return Collections.unmodifiableList(this.nameCache);
      }
   }
}
