package svenhjol.charm.world.storage;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

public class RunePortalSavedData extends WorldSavedData {
   private static final String ID = "RunePortalSavedData";
   private static final String PORTALS = "portals";
   private static final String LINKS = "links";
   public Map<BlockPos, int[]> portals = new HashMap();
   public Map<BlockPos, BlockPos> links = new HashMap();

   public RunePortalSavedData(String id) {
      super(id);
   }

   public void readFromNBT(NBTTagCompound tag) {
      for(String key : this.getTagLinks(tag).getKeySet()) {
         BlockPos toPos = BlockPos.fromLong(this.getTagLinks(tag).getLong(key));
         this.links.put(BlockPos.fromLong(Long.valueOf(key).longValue()), toPos);
      }

      for(String key : this.getTagPortals(tag).getKeySet()) {
         int[] portalOrder = this.getTagPortals(tag).getIntArray(key);
         this.portals.put(BlockPos.fromLong(Long.valueOf(key).longValue()), portalOrder);
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound tag) {
      for(BlockPos fromPos : this.links.keySet()) {
         BlockPos toPos = (BlockPos)this.links.get(fromPos);
         this.getTagLinks(tag).setLong(Long.toString(fromPos.toLong()), toPos.toLong());
      }

      for(BlockPos pos : this.portals.keySet()) {
         this.getTagPortals(tag).setIntArray(Long.toString(pos.toLong()), (int[])this.portals.get(pos));
      }

      return tag;
   }

   public static RunePortalSavedData get(World world) {
      if (world.getMapStorage() == null) {
         return null;
      } else {
         RunePortalSavedData data = (RunePortalSavedData)world.getMapStorage().getOrLoadData(RunePortalSavedData.class, "RunePortalSavedData");
         if (data == null) {
            data = new RunePortalSavedData("RunePortalSavedData");
            data.markDirty();
            world.getMapStorage().setData("RunePortalSavedData", data);
         }

         return data;
      }
   }

   private NBTTagCompound getTagPortals(NBTTagCompound tag) {
      if (tag.getCompoundTag("portals").hasNoTags()) {
         tag.setTag("portals", new NBTTagCompound());
      }

      return tag.getCompoundTag("portals");
   }

   private NBTTagCompound getTagLinks(NBTTagCompound tag) {
      if (tag.getCompoundTag("links").hasNoTags()) {
         tag.setTag("links", new NBTTagCompound());
      }

      return tag.getCompoundTag("links");
   }
}
