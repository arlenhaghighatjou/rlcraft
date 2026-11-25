package com.dhanantry.scapeandrunparasites.world;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.ArrayList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class SRPSaveData extends WorldSavedData {
   private static final String DATA_NAME = "srparasites_global_data";
   private static SRPSaveData instance;
   private ArrayList<Integer> dimNumberID = new ArrayList();
   private ArrayList<Integer> dimNumberPoint = new ArrayList();
   private ArrayList<Integer> lockedParasites = new ArrayList();

   public SRPSaveData() {
      super("srparasites_global_data");
   }

   public SRPSaveData(String name) {
      super(name);
   }

   public static SRPSaveData get(World world) {
      MapStorage storage = world.getMapStorage();
      instance = (SRPSaveData)storage.getOrLoadData(SRPSaveData.class, "srparasites_global_data");
      if (instance == null) {
         instance = new SRPSaveData();
         storage.setData("srparasites_global_data", instance);
         String[] here = new String[3];
         int id = 0;

         for(int i = 0; i < SRPConfigSystems.evolutionParasiteLock.length; ++i) {
            if (SRPConfigSystems.evolutionParasiteLock[i] != null) {
               here = SRPConfigSystems.evolutionParasiteLock[i].split(";");
               id = Integer.parseInt(here[2]);
               instance.lockedParasites.add(Integer.valueOf(id));
            }
         }
      }

      return instance;
   }

   public void readFromNBT(NBTTagCompound compound) {
      if (compound.hasKey("srplockedparasites")) {
         NBTTagList tagListX = compound.getTagList("srplockedparasites", 10);

         for(int i = 0; i < tagListX.tagCount(); ++i) {
            NBTTagCompound tagX = tagListX.getCompoundTagAt(i);
            int id = tagX.getInteger("parasiteid" + i);
            this.lockedParasites.add(i, Integer.valueOf(id));
         }
      }

      if (compound.hasKey("dimnumberid")) {
         NBTTagList tagListX = compound.getTagList("dimnumberid", 10);
         NBTTagList tagListY = compound.getTagList("dimnumberpoint", 10);
         if (tagListX.tagCount() != tagListY.tagCount()) {
            SRPMain.logger.log(Level.ERROR, "Problem while reading dims");
         } else {
            for(int i = 0; i < tagListX.tagCount(); ++i) {
               NBTTagCompound tagX = tagListX.getCompoundTagAt(i);
               int coordx = tagX.getInteger("dimid" + i);
               this.dimNumberID.add(i, Integer.valueOf(coordx));
               NBTTagCompound tagY = tagListY.getCompoundTagAt(i);
               int coordy = tagY.getInteger("dimpt" + i);
               this.dimNumberPoint.add(i, Integer.valueOf(coordy));
            }
         }
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      NBTTagList tagListXX = new NBTTagList();

      for(int i = 0; i < this.lockedParasites.size(); ++i) {
         int id = ((Integer)this.lockedParasites.get(i)).intValue();
         NBTTagCompound tagX = new NBTTagCompound();
         tagX.setInteger("parasiteid" + i, id);
         tagListXX.appendTag(tagX);
      }

      compound.setTag("srplockedparasites", tagListXX);
      if (this.dimNumberID.size() != this.dimNumberPoint.size()) {
         SRPMain.logger.log(Level.ERROR, "Problem while writing dims");
      } else {
         NBTTagList tagListX = new NBTTagList();
         NBTTagList tagListY = new NBTTagList();

         for(int i = 0; i < this.dimNumberID.size(); ++i) {
            int coordx = ((Integer)this.dimNumberID.get(i)).intValue();
            int coordy = ((Integer)this.dimNumberPoint.get(i)).intValue();
            NBTTagCompound tagX = new NBTTagCompound();
            tagX.setInteger("dimid" + i, coordx);
            tagListX.appendTag(tagX);
            NBTTagCompound tagY = new NBTTagCompound();
            tagY.setInteger("dimpt" + i, coordy);
            tagListY.appendTag(tagY);
         }

         compound.setTag("dimnumberid", tagListX);
         compound.setTag("dimnumberpoint", tagListY);
      }

      return compound;
   }

   public boolean checkParasiteID(int in) {
      for(int i = 0; i < this.lockedParasites.size(); ++i) {
         if (((Integer)this.lockedParasites.get(i)).intValue() == in) {
            return true;
         }
      }

      return false;
   }

   public ArrayList<Integer> getLockedList() {
      return this.lockedParasites;
   }

   public void unlockParasite(int in) {
      for(int i = 0; i < this.lockedParasites.size(); ++i) {
         if (((Integer)this.lockedParasites.get(i)).intValue() == in) {
            this.lockedParasites.remove(i);
            this.markDirty();
            return;
         }
      }

   }

   public void clearNodeList() {
      this.dimNumberID = new ArrayList();
      this.dimNumberPoint = new ArrayList();
      this.markDirty();
   }

   public void resetLock() {
      ArrayList<Integer> aaa = new ArrayList();
      String[] here = new String[3];
      int id = 0;

      for(int i = 0; i < SRPConfigSystems.evolutionParasiteLock.length; ++i) {
         if (SRPConfigSystems.evolutionParasiteLock[i] != null) {
            here = SRPConfigSystems.evolutionParasiteLock[i].split(";");
            id = Integer.parseInt(here[2]);
            aaa.add(Integer.valueOf(id));
         }
      }

      this.lockedParasites = aaa;
      this.markDirty();
   }

   public void saveDimPoints(int phase) {
      for(int i = 0; i < this.dimNumberID.size(); ++i) {
         if (((Integer)this.dimNumberID.get(i)).intValue() == phase) {
            int points = ((Integer)this.dimNumberPoint.get(i)).intValue() + 1;
            this.dimNumberPoint.set(i, Integer.valueOf(points));
            this.markDirty();
            return;
         }
      }

      this.dimNumberPoint.add(Integer.valueOf(1));
      this.dimNumberID.add(Integer.valueOf(phase));
      this.markDirty();
   }

   public void removeDimPoints(int id) {
      for(int i = 0; i < this.dimNumberID.size(); ++i) {
         if (((Integer)this.dimNumberID.get(i)).intValue() == id) {
            this.dimNumberPoint.remove(i);
            this.dimNumberID.remove(i);
            this.markDirty();
            return;
         }
      }

   }

   public int getDimPoints(int id, boolean in) {
      for(int i = 0; i < this.dimNumberID.size(); ++i) {
         if (((Integer)this.dimNumberID.get(i)).intValue() == id) {
            int r = ((Integer)this.dimNumberPoint.get(i)).intValue();
            if (in) {
               this.dimNumberPoint.remove(i);
               this.dimNumberID.remove(i);
            }

            this.markDirty();
            return r;
         }
      }

      return 0;
   }
}
