package net.blay09.mods.waystones;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class GlobalWaystones extends WorldSavedData {
   private static final String DATA_NAME = "waystones_GlobalWaystones";
   private static final String TAG_LIST_NAME = "GlobalWaystones";
   private final Map<String, WaystoneEntry> globalWaystones = Maps.newHashMap();

   public GlobalWaystones() {
      super("waystones_GlobalWaystones");
   }

   public GlobalWaystones(String name) {
      super(name);
   }

   public void addGlobalWaystone(WaystoneEntry entry) {
      this.globalWaystones.put(entry.getName(), entry);
      this.markDirty();

      for(EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
         WaystoneManager.addPlayerWaystone(player, entry);
      }

   }

   public void removeGlobalWaystone(WaystoneEntry entry) {
      this.globalWaystones.remove(entry.getName());
      this.markDirty();

      for(EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()) {
         WaystoneManager.removePlayerWaystone(player, entry);
      }

   }

   public Collection<WaystoneEntry> getGlobalWaystones() {
      return this.globalWaystones.values();
   }

   @Nullable
   public WaystoneEntry getGlobalWaystone(String name) {
      return (WaystoneEntry)this.globalWaystones.get(name);
   }

   public void readFromNBT(NBTTagCompound tagCompound) {
      NBTTagList tagList = tagCompound.getTagList("GlobalWaystones", 10);

      for(int i = 0; i < tagList.tagCount(); ++i) {
         WaystoneEntry entry = WaystoneEntry.read((NBTTagCompound)tagList.get(i));
         this.globalWaystones.put(entry.getName(), entry);
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
      NBTTagList tagList = new NBTTagList();

      for(WaystoneEntry entry : this.globalWaystones.values()) {
         tagList.appendTag(entry.writeToNBT());
      }

      tagCompound.setTag("GlobalWaystones", tagList);
      return tagCompound;
   }

   public static GlobalWaystones get(World world) {
      MapStorage storage = world.getMapStorage();
      if (storage != null) {
         GlobalWaystones instance = (GlobalWaystones)storage.getOrLoadData(GlobalWaystones.class, "waystones_GlobalWaystones");
         if (instance == null) {
            instance = new GlobalWaystones();
            storage.setData("waystones_GlobalWaystones", instance);
         }

         return instance;
      } else {
         return new GlobalWaystones();
      }
   }
}
