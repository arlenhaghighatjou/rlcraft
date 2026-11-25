package com.dhanantry.scapeandrunparasites.world;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.network.SRPCommandEvolution;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class SRPWorldData extends WorldSavedData {
   private static final String DATA_NAME = "srparasites_data";
   private static SRPWorldData instance;
   private int currentVenkrol;
   private ArrayList<Integer> nodeX = new ArrayList();
   private ArrayList<Integer> nodeY = new ArrayList();
   private ArrayList<Integer> nodeZ = new ArrayList();
   private ArrayList<Integer> nodeA = new ArrayList();
   private ArrayList<Integer> colonyX = new ArrayList();
   private ArrayList<Integer> colonyY = new ArrayList();
   private ArrayList<Integer> colonyZ = new ArrayList();
   private ArrayList<Integer> colonyA = new ArrayList();
   private ArrayList<Integer> resistanceI = new ArrayList();
   private ArrayList<String> resistanceS = new ArrayList();
   private int totalKills;
   private byte evolution;
   private int timeEvolution;
   private boolean canGainPoints;
   private boolean canLossPoints;

   public SRPWorldData() {
      super("srparasites_data");
   }

   public SRPWorldData(String name) {
      super(name);
   }

   public static SRPWorldData get(World world) {
      MapStorage storage = world.getPerWorldStorage();
      instance = (SRPWorldData)storage.getOrLoadData(SRPWorldData.class, "srparasites_data");
      if (instance == null) {
         instance = create(world, storage);
      }

      return instance;
   }

   private static SRPWorldData create(World world, MapStorage storage) {
      instance = new SRPWorldData();
      storage.setData("srparasites_data", instance);
      int currentDim = world.provider.getDimension();
      instance.setGaining(true);
      instance.setLoss(false);
      instance.setEvolutionPhase(SRPConfigSystems.defaultEvoPhase, true, world, true);
      instance.setTotalKills(SRPConfigSystems.defaultEvoPoints, false, world, true);
      String[] here = new String[3];

      for(int i = 0; i < SRPConfigSystems.evolutionDimStart.length; ++i) {
         here = SRPConfigSystems.evolutionDimStart[i].split(";");
         int dim = Integer.parseInt(here[0]);
         int phase = Integer.parseInt(here[1]);
         int points = Integer.parseInt(here[2]);
         if (currentDim == dim) {
            instance.setEvolutionPhase((byte)phase, true, world, true);
            if (phase == -1) {
               points = Integer.parseInt(here[2]);
               instance.setTotalKills(-points, false, world, true);
            } else {
               if (phase == -2) {
                  instance.setGaining(false);
                  instance.setLoss(true);
                  return instance;
               }

               instance.setTotalKills(points, false, world, true);
            }
            break;
         }
      }

      boolean inv = false;
      boolean flagI = false;
      if (SRPConfigSystems.evolutionDimGainInverted) {
         inv = true;
      }

      for(int i : SRPConfigSystems.evolutionDimGain) {
         if (SRPConfigSystems.evolutionDimGainInverted) {
            if (currentDim == i) {
               flagI = true;
            }
         } else if (currentDim == i) {
            instance.setGaining(false);
         }
      }

      if (inv && !flagI) {
         instance.setGaining(false);
      }

      inv = false;
      flagI = false;
      if (SRPConfigSystems.evolutionDimLossInverted) {
         inv = true;
      }

      for(int i : SRPConfigSystems.evolutionDimLoss) {
         if (SRPConfigSystems.evolutionDimLossInverted) {
            if (currentDim == i) {
               flagI = true;
            }
         } else if (currentDim == i) {
            instance.setLoss(true);
         }
      }

      if (inv && !flagI) {
         instance.setLoss(true);
      }

      return instance;
   }

   public void resetInstance(World world) {
      MapStorage storage = world.getPerWorldStorage();
      instance = create(world, storage);
   }

   public void readFromNBT(NBTTagCompound compound) {
      if (compound.hasKey("srpcurrentbeckons")) {
         this.currentVenkrol = compound.getInteger("srpcurrentbeckons");
      }

      if (compound.hasKey("srpparasitepoints")) {
         this.totalKills = compound.getInteger("srpparasitepoints");
      }

      if (compound.hasKey("srpparasitetimeevoworld")) {
         this.timeEvolution = compound.getInteger("srpparasitetimeevoworld");
      }

      if (compound.hasKey("srpparasiteevolution")) {
         this.evolution = compound.getByte("srpparasiteevolution");
      }

      if (compound.hasKey("srpevolutiongaining")) {
         this.canGainPoints = compound.getBoolean("srpevolutiongaining");
      }

      if (compound.hasKey("srpevolutionloss")) {
         this.canLossPoints = compound.getBoolean("srpevolutionloss");
      }

      if (compound.hasKey("srpnodescoordsx")) {
         NBTTagList tagListX = compound.getTagList("srpnodescoordsx", 10);
         NBTTagList tagListY = compound.getTagList("srpnodescoordsy", 10);
         NBTTagList tagListZ = compound.getTagList("srpnodescoordsz", 10);
         NBTTagList tagListA = compound.getTagList("srpnodesages", 10);
         if (tagListX.tagCount() == tagListY.tagCount() && tagListX.tagCount() == tagListZ.tagCount() && tagListX.tagCount() == tagListA.tagCount()) {
            for(int i = 0; i < tagListX.tagCount(); ++i) {
               NBTTagCompound tagX = tagListX.getCompoundTagAt(i);
               int coordx = tagX.getInteger("nodex" + i);
               this.nodeX.add(i, Integer.valueOf(coordx));
               NBTTagCompound tagY = tagListY.getCompoundTagAt(i);
               int coordy = tagY.getInteger("nodey" + i);
               this.nodeY.add(i, Integer.valueOf(coordy));
               NBTTagCompound tagZ = tagListZ.getCompoundTagAt(i);
               int coordz = tagZ.getInteger("nodez" + i);
               this.nodeZ.add(i, Integer.valueOf(coordz));
               NBTTagCompound tagA = tagListA.getCompoundTagAt(i);
               int age = tagA.getInteger("nodea" + i);
               this.nodeA.add(i, Integer.valueOf(age));
            }
         } else {
            SRPMain.logger.log(Level.ERROR, "Problem while reading nodes coords");
         }
      }

      if (compound.hasKey("srpcoloniescoordsx")) {
         NBTTagList tagListX = compound.getTagList("srpcoloniescoordsx", 10);
         NBTTagList tagListY = compound.getTagList("srpcoloniescoordsy", 10);
         NBTTagList tagListZ = compound.getTagList("srpcoloniescoordsz", 10);
         NBTTagList tagListA = compound.getTagList("srpcoloniesages", 10);
         if (tagListX.tagCount() == tagListY.tagCount() && tagListX.tagCount() == tagListZ.tagCount() && tagListX.tagCount() == tagListA.tagCount()) {
            for(int i = 0; i < tagListX.tagCount(); ++i) {
               NBTTagCompound tagX = tagListX.getCompoundTagAt(i);
               int coordx = tagX.getInteger("colonyx" + i);
               this.colonyX.add(i, Integer.valueOf(coordx));
               NBTTagCompound tagY = tagListY.getCompoundTagAt(i);
               int coordy = tagY.getInteger("colonyy" + i);
               this.colonyY.add(i, Integer.valueOf(coordy));
               NBTTagCompound tagZ = tagListZ.getCompoundTagAt(i);
               int coordz = tagZ.getInteger("colonyz" + i);
               this.colonyZ.add(i, Integer.valueOf(coordz));
               NBTTagCompound tagA = tagListA.getCompoundTagAt(i);
               int age = tagA.getInteger("colonya" + i);
               this.colonyA.add(i, Integer.valueOf(age));
            }
         } else {
            SRPMain.logger.log(Level.ERROR, "Problem while reading colonies coords");
         }
      }

      if (compound.hasKey("srpcolonyresistances")) {
         NBTTagList tagListX = compound.getTagList("srpcolonyresistancei", 10);
         NBTTagList tagListY = compound.getTagList("srpcolonyresistances", 10);
         if (tagListX.tagCount() != tagListY.tagCount()) {
            SRPMain.logger.log(Level.ERROR, "Problem while reading resistance");
         } else {
            for(int i = 0; i < tagListX.tagCount(); ++i) {
               NBTTagCompound tagX = tagListX.getCompoundTagAt(i);
               int coordx = tagX.getInteger("resistance" + i);
               this.resistanceI.add(i, Integer.valueOf(coordx));
               NBTTagCompound tagY = tagListY.getCompoundTagAt(i);
               String coordy = tagY.getString("resistance" + i);
               this.resistanceS.add(i, coordy);
            }
         }
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound compound) {
      compound.setInteger("srpcurrentbeckons", this.currentVenkrol);
      compound.setInteger("srpparasitepoints", this.totalKills);
      compound.setInteger("srpparasitetimeevoworld", this.timeEvolution);
      compound.setByte("srpparasiteevolution", this.evolution);
      compound.setBoolean("srpevolutiongaining", this.canGainPoints);
      compound.setBoolean("srpevolutionloss", this.canLossPoints);
      if (this.nodeX.size() == this.nodeY.size() && this.nodeX.size() == this.nodeZ.size() && this.nodeX.size() == this.nodeA.size()) {
         NBTTagList tagListX = new NBTTagList();
         NBTTagList tagListY = new NBTTagList();
         NBTTagList tagListZ = new NBTTagList();
         NBTTagList tagListA = new NBTTagList();

         for(int i = 0; i < this.nodeX.size(); ++i) {
            int coordx = ((Integer)this.nodeX.get(i)).intValue();
            int coordy = ((Integer)this.nodeY.get(i)).intValue();
            int coordz = ((Integer)this.nodeZ.get(i)).intValue();
            int age = ((Integer)this.nodeA.get(i)).intValue();
            NBTTagCompound tagX = new NBTTagCompound();
            tagX.setInteger("nodex" + i, coordx);
            tagListX.appendTag(tagX);
            NBTTagCompound tagY = new NBTTagCompound();
            tagY.setInteger("nodey" + i, coordy);
            tagListY.appendTag(tagY);
            NBTTagCompound tagZ = new NBTTagCompound();
            tagZ.setInteger("nodez" + i, coordz);
            tagListZ.appendTag(tagZ);
            NBTTagCompound tagA = new NBTTagCompound();
            tagA.setInteger("nodea" + i, age);
            tagListA.appendTag(tagA);
         }

         compound.setTag("srpnodescoordsx", tagListX);
         compound.setTag("srpnodescoordsy", tagListY);
         compound.setTag("srpnodescoordsz", tagListZ);
         compound.setTag("srpnodesages", tagListA);
      } else {
         SRPMain.logger.log(Level.ERROR, "Problem while writing nodes coords");
      }

      if (this.colonyX.size() == this.colonyY.size() && this.colonyX.size() == this.colonyZ.size() && this.colonyX.size() == this.colonyA.size()) {
         NBTTagList tagListX = new NBTTagList();
         NBTTagList tagListY = new NBTTagList();
         NBTTagList tagListZ = new NBTTagList();
         NBTTagList tagListA = new NBTTagList();

         for(int i = 0; i < this.colonyX.size(); ++i) {
            int coordx = ((Integer)this.colonyX.get(i)).intValue();
            int coordy = ((Integer)this.colonyY.get(i)).intValue();
            int coordz = ((Integer)this.colonyZ.get(i)).intValue();
            int age = ((Integer)this.colonyA.get(i)).intValue();
            NBTTagCompound tagX = new NBTTagCompound();
            tagX.setInteger("colonyx" + i, coordx);
            tagListX.appendTag(tagX);
            NBTTagCompound tagY = new NBTTagCompound();
            tagY.setInteger("colonyy" + i, coordy);
            tagListY.appendTag(tagY);
            NBTTagCompound tagZ = new NBTTagCompound();
            tagZ.setInteger("colonyz" + i, coordz);
            tagListZ.appendTag(tagZ);
            NBTTagCompound tagA = new NBTTagCompound();
            tagA.setInteger("colonya" + i, age);
            tagListA.appendTag(tagA);
         }

         compound.setTag("srpcoloniescoordsx", tagListX);
         compound.setTag("srpcoloniescoordsy", tagListY);
         compound.setTag("srpcoloniescoordsz", tagListZ);
         compound.setTag("srpcoloniesages", tagListA);
      } else {
         SRPMain.logger.log(Level.ERROR, "Problem while writing colonies coords");
      }

      if (this.resistanceI.size() != this.resistanceS.size()) {
         SRPMain.logger.log(Level.ERROR, "Problem while writing resistance");
      } else {
         NBTTagList tagListX = new NBTTagList();
         NBTTagList tagListY = new NBTTagList();

         for(int i = 0; i < this.resistanceI.size(); ++i) {
            int coordx = ((Integer)this.resistanceI.get(i)).intValue();
            String coordy = (String)this.resistanceS.get(i);
            NBTTagCompound tagX = new NBTTagCompound();
            tagX.setInteger("resistance" + i, coordx);
            tagListX.appendTag(tagX);
            NBTTagCompound tagY = new NBTTagCompound();
            tagY.setString("resistance" + i, coordy);
            tagListY.appendTag(tagY);
         }

         compound.setTag("srpcolonyresistancei", tagListX);
         compound.setTag("srpcolonyresistances", tagListY);
      }

      return compound;
   }

   public void setGaining(boolean in) {
      this.canGainPoints = in;
      this.markDirty();
   }

   public boolean getCanGain() {
      return this.canGainPoints;
   }

   public void setLoss(boolean in) {
      this.canLossPoints = in;
      this.markDirty();
   }

   public boolean getCanLoss() {
      return this.canLossPoints;
   }

   public void setCurrentV(int in) {
      this.currentVenkrol += in;
      this.markDirty();
   }

   public int getCurrentV() {
      return this.currentVenkrol;
   }

   public byte getEvolutionPhase() {
      return this.evolution;
   }

   private double getDistanceSQ(double rootx, double rooty, double rootz, double standingx, double standingy, double standingz) {
      double d0 = rootx - standingx;
      double d1 = rooty - standingy;
      double d2 = rootz - standingz;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public void setCooldown(int in, World worldIn) {
      int currentWT = (int)worldIn.getWorldTime();
      this.timeEvolution = currentWT - this.getDelay(this.evolution) * 20 + in * 20;
      this.markDirty();
   }

   public int getCooldown(World worldIn) {
      if (this.timeEvolution == 0) {
         return 0;
      } else {
         int currentWT = (int)worldIn.getWorldTime();
         return Math.max((this.getDelay(this.evolution) * 20 - (currentWT - this.timeEvolution)) / 20, 0);
      }
   }

   public int getTotalKills() {
      return this.totalKills;
   }

   public boolean setTotalKills(int in, boolean plus, World worldIn, boolean canChangePhase) {
      if (!this.canGainPoints && plus && in > 0) {
         return false;
      } else if (this.canLossPoints && plus && in < 0) {
         return false;
      } else if (!SRPConfigSystems.useEvolution) {
         return false;
      } else if (this.evolution == -2) {
         return false;
      } else if (this.getDelay(this.evolution) != 0 && this.getCooldown(worldIn) != 0 && in > 0 && plus) {
         return false;
      } else {
         if (plus) {
            int bonus;
            if (in > 0) {
               bonus = (int)((float)this.totalColonyPoints(0) / SRPConfigWorld.colonyPointBoostPoint * SRPConfigWorld.colonyPointBoostValue);
            } else {
               bonus = (int)((float)this.totalColonyPoints(0) / SRPConfigWorld.colonyPointReductionPoint * SRPConfigWorld.colonyPointReductionValue);
            }

            this.totalKills += in + in * bonus;
            if (this.totalKills < 0 && this.evolution >= 0) {
               this.totalKills = 0;
            }

            if (this.totalKills > SRPConfigSystems.phaseEightTotalPoints) {
               this.totalKills = SRPConfigSystems.phaseEightTotalPoints;
            }

            for(boolean ttt = true; ttt; ttt = this.checkKills(this.totalKills, worldIn, canChangePhase)) {
               ;
            }
         } else {
            this.totalKills = in;
         }

         this.markDirty();
         return true;
      }
   }

   private int getDelay(byte in) {
      switch(in) {
      case 1:
         return SRPConfigSystems.phaseDelayTicksOne;
      case 2:
         return SRPConfigSystems.phaseDelayTicksTwo;
      case 3:
         return SRPConfigSystems.phaseDelayTicksThree;
      case 4:
         return SRPConfigSystems.phaseDelayTicksFour;
      case 5:
         return SRPConfigSystems.phaseDelayTicksFive;
      case 6:
         return SRPConfigSystems.phaseDelayTicksSix;
      case 7:
         return SRPConfigSystems.phaseDelayTicksSeven;
      case 8:
         return SRPConfigSystems.phaseDelayTicksEight;
      default:
         return 0;
      }
   }

   private boolean checkKills(int in, World worldIn, boolean canChangePhase) {
      boolean flag = false;
      switch(this.evolution) {
      case -1:
         if (in > 0 && this.setEvolutionPhase((byte)0, false, worldIn, canChangePhase)) {
            ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningZero, 0);
         }
         break;
      case 0:
         if (in > SRPConfigSystems.phaseKillsOne && this.setEvolutionPhase((byte)1, false, worldIn, canChangePhase)) {
            ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningOne, 1);
         }
         break;
      case 1:
         if (in > SRPConfigSystems.phaseKillsTwo && this.setEvolutionPhase((byte)2, false, worldIn, canChangePhase)) {
            ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningTwo, 2);
         }

         if (in < SRPConfigSystems.phaseKillsOne && this.setEvolutionPhase((byte)0, false, worldIn, canChangePhase)) {
            ParasiteEventEntity.alertAllPlayerDim(worldIn, "Phase decreased", -7);
            flag = true;
         }
         break;
      case 2:
         if (in > SRPConfigSystems.phaseKillsThree && this.setEvolutionPhase((byte)3, false, worldIn, canChangePhase)) {
            ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningThree, 3);
         }

         if (in < SRPConfigSystems.phaseKillsTwo && this.setEvolutionPhase((byte)1, false, worldIn, canChangePhase)) {
            ParasiteEventEntity.alertAllPlayerDim(worldIn, "Phase decreased", -7);
            flag = true;
         }
         break;
      case 3:
         if (in > SRPConfigSystems.phaseKillsFour) {
            if (this.setEvolutionPhase((byte)4, false, worldIn, canChangePhase)) {
               ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningFour, 4);
            }
         } else if (in < SRPConfigSystems.phaseKillsThree && this.setEvolutionPhase((byte)2, false, worldIn, canChangePhase)) {
            ParasiteEventEntity.alertAllPlayerDim(worldIn, "Phase decreased", -7);
            flag = true;
         }
         break;
      case 4:
         if (in > SRPConfigSystems.phaseKillsFive && this.setEvolutionPhase((byte)5, false, worldIn, canChangePhase)) {
            ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningFive, 5);
         }

         if (in < SRPConfigSystems.phaseKillsFour && this.setEvolutionPhase((byte)3, false, worldIn, canChangePhase)) {
            ParasiteEventEntity.alertAllPlayerDim(worldIn, "Phase decreased", -7);
            flag = true;
         }
         break;
      case 5:
         if (in > SRPConfigSystems.phaseKillsSix && this.setEvolutionPhase((byte)6, false, worldIn, canChangePhase)) {
            ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningSix, 6);
         }

         if (in < SRPConfigSystems.phaseKillsFive && this.setEvolutionPhase((byte)4, false, worldIn, canChangePhase)) {
            ParasiteEventEntity.alertAllPlayerDim(worldIn, "Phase decreased", -7);
            flag = true;
         }
         break;
      case 6:
         if (in > SRPConfigSystems.phaseKillsSeven && this.setEvolutionPhase((byte)7, false, worldIn, canChangePhase)) {
            ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningSeven, 7);
         }

         if (in < SRPConfigSystems.phaseKillsSix && this.setEvolutionPhase((byte)5, false, worldIn, canChangePhase)) {
            ParasiteEventEntity.alertAllPlayerDim(worldIn, "Phase decreased", -7);
            flag = true;
         }
         break;
      case 7:
         if (in > SRPConfigSystems.phaseKillsEight && this.setEvolutionPhase((byte)8, false, worldIn, canChangePhase)) {
            ParasiteEventEntity.alertAllPlayerDim(worldIn, SRPConfigSystems.phaseWarningEight, 8);
         }

         if (in < SRPConfigSystems.phaseKillsSeven && this.setEvolutionPhase((byte)6, false, worldIn, canChangePhase)) {
            ParasiteEventEntity.alertAllPlayerDim(worldIn, "Phase decreased", -7);
            flag = true;
         }
         break;
      case 8:
         if (in < SRPConfigSystems.phaseKillsEight && this.setEvolutionPhase((byte)7, false, worldIn, canChangePhase)) {
            ParasiteEventEntity.alertAllPlayerDim(worldIn, "Phase decreased", -7);
            flag = true;
         }
      }

      return flag;
   }

   public boolean setEvolutionPhase(byte in, boolean override, World worldIn, boolean canChangePhase) {
      if (!canChangePhase) {
         this.totalKills = SRPCommandEvolution.getNeededPoints(this.evolution);
         this.markDirty();
         return false;
      } else {
         if (override) {
            this.evolution = in;
            this.checkPhase(in, worldIn);
         } else {
            this.evolution = in;
         }

         this.timeEvolution = (int)worldIn.getWorldTime();
         this.checkForUnlock(this.evolution, worldIn);
         List<Entity> serverList = worldIn.loadedEntityList;

         for(int x = 0; x < serverList.size(); ++x) {
            if (serverList.get(x) instanceof EntityParasiteBase) {
               ((EntityParasiteBase)serverList.get(x)).setCreatedPhase(this.evolution);
            }
         }

         this.markDirty();
         return true;
      }
   }

   private void checkForUnlock(byte phase, World in) {
      String[] here = new String[3];
      int currentD = in.provider.getDimension();
      int id = 0;
      int dim = 0;
      byte p = 0;

      for(int i = 0; i < SRPConfigSystems.evolutionParasiteLock.length; ++i) {
         if (SRPConfigSystems.evolutionParasiteLock[i] != null) {
            here = SRPConfigSystems.evolutionParasiteLock[i].split(";");
            dim = Integer.parseInt(here[0]);
            if (currentD == dim) {
               p = Byte.parseByte(here[1]);
               if (p == phase) {
                  id = Integer.parseInt(here[2]);
                  SRPSaveData.get(in).unlockParasite(id);
               }
            }
         }
      }

   }

   private void checkPhase(byte in, World worldIn) {
      switch(in) {
      case -1:
         this.setTotalKills(-10, false, worldIn, true);
         break;
      case 0:
         this.setTotalKills(0, false, worldIn, true);
         break;
      case 1:
         this.setTotalKills(SRPConfigSystems.phaseKillsOne, false, worldIn, true);
         break;
      case 2:
         this.setTotalKills(SRPConfigSystems.phaseKillsTwo, false, worldIn, true);
         break;
      case 3:
         this.setTotalKills(SRPConfigSystems.phaseKillsThree, false, worldIn, true);
         break;
      case 4:
         this.setTotalKills(SRPConfigSystems.phaseKillsFour, false, worldIn, true);
         break;
      case 5:
         this.setTotalKills(SRPConfigSystems.phaseKillsFive, false, worldIn, true);
         break;
      case 6:
         this.setTotalKills(SRPConfigSystems.phaseKillsSix, false, worldIn, true);
         break;
      case 7:
         this.setTotalKills(SRPConfigSystems.phaseKillsSeven, false, worldIn, true);
         break;
      case 8:
         this.setTotalKills(SRPConfigSystems.phaseKillsEight, false, worldIn, true);
      }

   }

   public void clearNodeList() {
      this.nodeX = new ArrayList();
      this.nodeY = new ArrayList();
      this.nodeZ = new ArrayList();
      this.nodeA = new ArrayList();
      this.markDirty();
   }

   public ArrayList<Integer> getNodes(String i) {
      if (i.equals("x")) {
         return this.nodeX;
      } else if (i.equals("y")) {
         return this.nodeY;
      } else if (i.equals("z")) {
         return this.nodeZ;
      } else {
         return i.equals("a") ? this.nodeA : null;
      }
   }

   public int setNode(int x, int y, int z, int currentDay) {
      currentDay = 1;
      if (!this.canNodeBeMade(new BlockPos(x, y, z))) {
         return 8;
      } else if (this.nodeX.size() >= SRPConfigWorld.maximumNumberNodes) {
         return 9;
      } else {
         int canAdd = 1;

         for(int i = 0; i < this.nodeX.size(); ++i) {
            if (((Integer)this.nodeX.get(i)).intValue() == x && ((Integer)this.nodeY.get(i)).intValue() == y && ((Integer)this.nodeZ.get(i)).intValue() == z) {
               if (((Integer)this.nodeA.get(i)).intValue() != currentDay) {
                  this.removeNode(x, y, z);
                  this.setNode(x, y, z, currentDay);
               }

               canAdd = 10;
            }
         }

         if (canAdd == 1) {
            this.nodeX.add(Integer.valueOf(x));
            this.nodeY.add(Integer.valueOf(y));
            this.nodeZ.add(Integer.valueOf(z));
            this.nodeA.add(Integer.valueOf(currentDay));
            this.markDirty();
         }

         return canAdd;
      }
   }

   public boolean canNodeBeMade(BlockPos pos) {
      int distance = SRPConfigWorld.minimumDistanceBetweenNodes * SRPConfigWorld.minimumDistanceBetweenNodes;

      for(int i = 0; i < this.nodeX.size(); ++i) {
         double distanceSQ = this.getDistanceSQ((double)((Integer)this.nodeX.get(i)).intValue(), (double)((Integer)this.nodeY.get(i)).intValue(), (double)((Integer)this.nodeZ.get(i)).intValue(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
         if (distanceSQ <= (double)distance) {
            return false;
         }
      }

      return true;
   }

   public boolean removeNode(int x, int y, int z) {
      for(int i = 0; i < this.nodeX.size(); ++i) {
         if (((Integer)this.nodeX.get(i)).intValue() == x && ((Integer)this.nodeY.get(i)).intValue() == y && ((Integer)this.nodeZ.get(i)).intValue() == z) {
            this.nodeX.remove(i);
            this.nodeY.remove(i);
            this.nodeZ.remove(i);
            this.nodeA.remove(i);
            this.markDirty();
            return true;
         }
      }

      return false;
   }

   public int isInRangeOfHeart(BlockPos pos, double distance) {
      ++distance;

      for(int i = 0; i < this.nodeX.size(); ++i) {
         double distanceSQ = this.getDistanceSQ((double)((Integer)this.nodeX.get(i)).intValue(), (double)((Integer)this.nodeY.get(i)).intValue(), (double)((Integer)this.nodeZ.get(i)).intValue(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
         if (distanceSQ < distance * distance) {
            return (int)distanceSQ;
         }
      }

      return -1;
   }

   public int nearestHeartAge(BlockPos pos, boolean spread, int currentDay) {
      int heart = -1;
      double distance = -1.0D;

      for(int i = 0; i < this.nodeX.size(); ++i) {
         double distanceSQ = this.getDistanceSQ((double)((Integer)this.nodeX.get(i)).intValue(), (double)((Integer)this.nodeY.get(i)).intValue(), (double)((Integer)this.nodeZ.get(i)).intValue(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
         int ageDistance = this.convertDayToAgeNode(currentDay, ((Integer)this.nodeA.get(i)).intValue());
         if (spread) {
            ageDistance = this.getDistanceSpreadByAge(ageDistance, true);
         } else {
            ageDistance = this.getDistanceEffectByAge(ageDistance, true);
         }

         if (distanceSQ <= (double)ageDistance) {
            if (distance == -1.0D) {
               heart = ageDistance;
               distance = distanceSQ;
            } else if (distance <= distanceSQ) {
               heart = ageDistance;
               distance = distanceSQ;
            }
         }
      }

      return heart;
   }

   private int convertDayToAgeNode(int currentDay, int nodeBirthday) {
      if (nodeBirthday >= SRPConfigWorld.timeNeedeToNodeThree) {
         return 3;
      } else {
         return nodeBirthday >= SRPConfigWorld.timeNeedeToNodeTwo ? 2 : 1;
      }
   }

   public int getDistanceSpreadByAge(int age, boolean sq) {
      switch(age) {
      case 1:
         if (sq) {
            return SRPConfigWorld.nodeRangeSpreadOne * SRPConfigWorld.nodeRangeSpreadOne;
         }

         return SRPConfigWorld.nodeRangeSpreadOne;
      case 2:
         if (sq) {
            return SRPConfigWorld.nodeRangeSpreadTwo * SRPConfigWorld.nodeRangeSpreadTwo;
         }

         return SRPConfigWorld.nodeRangeSpreadTwo;
      case 3:
         if (sq) {
            return SRPConfigWorld.nodeRangeSpreadThree * SRPConfigWorld.nodeRangeSpreadThree;
         }

         return SRPConfigWorld.nodeRangeSpreadThree;
      default:
         return -1;
      }
   }

   public int getDistanceEffectByAge(int age, boolean sq) {
      switch(age) {
      case 1:
         if (sq) {
            return SRPConfigWorld.nodeRangeEffectsOne * SRPConfigWorld.nodeRangeEffectsOne;
         }

         return SRPConfigWorld.nodeRangeEffectsOne;
      case 2:
         if (sq) {
            return SRPConfigWorld.nodeRangeEffectsTwo * SRPConfigWorld.nodeRangeEffectsTwo;
         }

         return SRPConfigWorld.nodeRangeEffectsTwo;
      case 3:
         if (sq) {
            return SRPConfigWorld.nodeRangeEffectsThree * SRPConfigWorld.nodeRangeEffectsThree;
         }

         return SRPConfigWorld.nodeRangeEffectsThree;
      default:
         return -1;
      }
   }

   public void checkHeartExistance(World worldIn) {
      for(int i = 0; i < this.nodeX.size(); ++i) {
         BlockPos pos = new BlockPos(((Integer)this.nodeX.get(i)).intValue(), ((Integer)this.nodeY.get(i)).intValue(), ((Integer)this.nodeZ.get(i)).intValue());
         if (worldIn.isAreaLoaded(pos, 3)) {
            if (worldIn.getBlockState(pos).getBlock() != SRPBlocks.BiomeHeart) {
               this.removeNode(((Integer)this.nodeX.get(i)).intValue(), ((Integer)this.nodeY.get(i)).intValue(), ((Integer)this.nodeZ.get(i)).intValue());
            } else {
               int in = ((Integer)this.nodeA.get(i)).intValue() + 1;
               this.nodeA.set(i, Integer.valueOf(in));
            }
         }
      }

   }

   public int getHeartPocition(BlockPos pos, int currentDay) {
      for(int i = 0; i < this.nodeX.size(); ++i) {
         if (((Integer)this.nodeX.get(i)).intValue() == pos.getX() && ((Integer)this.nodeY.get(i)).intValue() == pos.getY() && ((Integer)this.nodeZ.get(i)).intValue() == pos.getZ()) {
            return this.convertDayToAgeNode(currentDay, ((Integer)this.nodeA.get(i)).intValue());
         }
      }

      return -1;
   }

   public int totalNodePoints(int currentDay) {
      int points = 0;

      for(int i = 0; i < this.nodeX.size(); ++i) {
         int age = this.convertDayToAgeNode(currentDay, ((Integer)this.nodeA.get(i)).intValue());
         points += age;
      }

      return points;
   }

   public void clearColonyList() {
      this.colonyX = new ArrayList();
      this.colonyY = new ArrayList();
      this.colonyZ = new ArrayList();
      this.colonyA = new ArrayList();
      this.markDirty();
   }

   public ArrayList<Integer> getColonies(String i) {
      if (i.equals("x")) {
         return this.colonyX;
      } else if (i.equals("y")) {
         return this.colonyY;
      } else if (i.equals("z")) {
         return this.colonyZ;
      } else {
         return i.equals("a") ? this.colonyA : null;
      }
   }

   public int setColony(int x, int y, int z, int currentDay) {
      currentDay = 1;
      if (!this.canColonyBeMade(new BlockPos(x, y, z))) {
         return 6;
      } else if (this.colonyX.size() >= SRPConfigWorld.maximumNumberColonies) {
         return 7;
      } else {
         int canAdd = 1;

         for(int i = 0; i < this.colonyX.size(); ++i) {
            if (((Integer)this.colonyX.get(i)).intValue() == x && ((Integer)this.colonyY.get(i)).intValue() == y && ((Integer)this.colonyZ.get(i)).intValue() == z) {
               if (((Integer)this.colonyA.get(i)).intValue() != currentDay) {
                  this.removeColony(x, y, z);
                  this.setColony(x, y, z, currentDay);
               }

               canAdd = 10;
            }
         }

         if (canAdd == 1) {
            this.colonyX.add(Integer.valueOf(x));
            this.colonyY.add(Integer.valueOf(y));
            this.colonyZ.add(Integer.valueOf(z));
            this.colonyA.add(Integer.valueOf(currentDay));
            this.markDirty();
         }

         return canAdd;
      }
   }

   public boolean canColonyBeMade(BlockPos pos) {
      int distance = SRPConfigWorld.minimumDistanceBetweenColonies * SRPConfigWorld.minimumDistanceBetweenColonies;

      for(int i = 0; i < this.colonyX.size(); ++i) {
         double distanceSQ = this.getDistanceSQ((double)((Integer)this.colonyX.get(i)).intValue(), (double)((Integer)this.colonyY.get(i)).intValue(), (double)((Integer)this.colonyZ.get(i)).intValue(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
         if (distanceSQ <= (double)distance) {
            return false;
         }
      }

      return true;
   }

   public boolean removeColony(int x, int y, int z) {
      for(int i = 0; i < this.colonyX.size(); ++i) {
         if (((Integer)this.colonyX.get(i)).intValue() == x && ((Integer)this.colonyY.get(i)).intValue() == y && ((Integer)this.colonyZ.get(i)).intValue() == z) {
            this.colonyX.remove(i);
            this.colonyY.remove(i);
            this.colonyZ.remove(i);
            this.colonyA.remove(i);
            this.markDirty();
            return true;
         }
      }

      return false;
   }

   public BlockPos nearestColonyPosition(BlockPos pos, int currentDay, boolean effect) {
      BlockPos posColony = null;
      double distance = -1.0D;

      for(int i = 0; i < this.colonyX.size(); ++i) {
         double distanceSQ = this.getDistanceSQ((double)((Integer)this.colonyX.get(i)).intValue(), (double)((Integer)this.colonyY.get(i)).intValue(), (double)((Integer)this.colonyZ.get(i)).intValue(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
         int ageDistance = this.convertDayToPointsColony(currentDay, ((Integer)this.colonyA.get(i)).intValue());
         ageDistance = this.getColonyDistanceSpreadByPoints(ageDistance, true, effect);
         if (distanceSQ <= (double)ageDistance) {
            if (distance == -1.0D) {
               posColony = new BlockPos(((Integer)this.colonyX.get(i)).intValue(), ((Integer)this.colonyY.get(i)).intValue(), ((Integer)this.colonyZ.get(i)).intValue());
               distance = distanceSQ;
            } else if (distance <= distanceSQ) {
               posColony = new BlockPos(((Integer)this.colonyX.get(i)).intValue(), ((Integer)this.colonyY.get(i)).intValue(), ((Integer)this.colonyZ.get(i)).intValue());
               distance = distanceSQ;
            }
         }
      }

      return posColony;
   }

   private int convertDayToPointsColony(int currentDay, int nodeBirthday) {
      return Math.min(nodeBirthday, SRPConfigWorld.colonyPointCap);
   }

   public int colonunumber() {
      return this.colonyA.size();
   }

   public int getColonyDistanceSpreadByPoints(int points, boolean sq, boolean effect) {
      if (effect) {
         int baseRange = SRPConfigWorld.colonyBaseEffectRadiusValue;
         int a = points / SRPConfigWorld.colonySpreadEffectPoint;
         int b = a * SRPConfigWorld.colonySpreadEffectValue + baseRange;
         return sq ? b * b : b;
      } else {
         int baseRange = SRPConfigWorld.colonyBaseRadiusValue;
         int a = points / SRPConfigWorld.colonySpreadPoint;
         int b = a * SRPConfigWorld.colonySpreadValue + baseRange;
         return sq ? b * b : b;
      }
   }

   public int getColonyDistanceSpreadByPosition(BlockPos pos, boolean sq, int currentDay) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();

      for(int i = 0; i < this.colonyX.size(); ++i) {
         if (((Integer)this.colonyX.get(i)).intValue() == x && ((Integer)this.colonyY.get(i)).intValue() == y && ((Integer)this.colonyZ.get(i)).intValue() == z) {
            int ageDistance = this.convertDayToPointsColony(currentDay, ((Integer)this.colonyA.get(i)).intValue());
            ageDistance = this.getColonyDistanceSpreadByPoints(ageDistance, sq, false);
            return ageDistance;
         }
      }

      return 0;
   }

   public BlockPos isInRangeOfColony(BlockPos pos, double distance) {
      ++distance;

      for(int i = 0; i < this.nodeX.size(); ++i) {
         double distanceSQ = this.getDistanceSQ((double)((Integer)this.nodeX.get(i)).intValue(), (double)((Integer)this.nodeY.get(i)).intValue(), (double)((Integer)this.nodeZ.get(i)).intValue(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
         if (distanceSQ < distance * distance) {
            return new BlockPos(((Integer)this.nodeX.get(i)).intValue(), ((Integer)this.nodeY.get(i)).intValue(), ((Integer)this.nodeZ.get(i)).intValue());
         }
      }

      return null;
   }

   public int totalColonyPoints(int currentDay) {
      int points = 0;

      for(int i = 0; i < this.colonyX.size(); ++i) {
         int age = this.convertDayToPointsColony(currentDay, ((Integer)this.colonyA.get(i)).intValue());
         points += age;
      }

      return Math.min(points, SRPConfigWorld.colonyTotalPointCap);
   }

   public void checkColonyExistance(World worldIn) {
      for(int i = 0; i < this.colonyX.size(); ++i) {
         BlockPos pos = new BlockPos(((Integer)this.colonyX.get(i)).intValue(), ((Integer)this.colonyY.get(i)).intValue(), ((Integer)this.colonyZ.get(i)).intValue());
         if (worldIn.isAreaLoaded(pos, 3)) {
            if (worldIn.getBlockState(pos).getBlock() != SRPBlocks.ColonyHeart) {
               this.removeColony(((Integer)this.colonyX.get(i)).intValue(), ((Integer)this.colonyY.get(i)).intValue(), ((Integer)this.colonyZ.get(i)).intValue());
            } else {
               int in = ((Integer)this.colonyA.get(i)).intValue() + 1;
               this.colonyA.set(i, Integer.valueOf(in));
            }
         }
      }

   }

   public void addGlobalResistance(String damage) {
      boolean flag = true;

      for(int i = 0; i < this.resistanceS.size(); ++i) {
         if (((String)this.resistanceS.get(i)).equals(damage)) {
            int iiii = ((Integer)this.resistanceI.get(i)).intValue() + 1;
            this.resistanceI.set(i, Integer.valueOf(iiii));
            flag = false;
            break;
         }
      }

      if (flag) {
         this.resistanceS.add(damage);
         this.resistanceI.add(Integer.valueOf(1));
      }

      this.markDirty();
   }

   public String getMostCommonDamageS() {
      ArrayList<String> resistanceAAAS = this.resistanceS;
      ArrayList<Integer> resistanceAAAI = this.resistanceI;
      String atm = null;
      int times = 0;

      for(int i = 0; i < resistanceAAAI.size(); ++i) {
         if (((Integer)resistanceAAAI.get(i)).intValue() > times) {
            times = ((Integer)resistanceAAAI.get(i)).intValue();
            atm = (String)resistanceAAAS.get(i);
         }
      }

      return atm;
   }

   public int getMostCommonDamageI() {
      ArrayList<Integer> resistanceAAAI = this.resistanceI;
      int times = 0;

      for(int i = 0; i < resistanceAAAI.size(); ++i) {
         if (((Integer)resistanceAAAI.get(i)).intValue() > times) {
            times = ((Integer)resistanceAAAI.get(i)).intValue();
         }
      }

      return times;
   }

   public void resetGlobalAdaptation() {
      this.resistanceI = new ArrayList();
      this.resistanceS = new ArrayList();
      this.markDirty();
   }

   public ArrayList<String> getAdaptationS() {
      return this.resistanceS;
   }

   public ArrayList<Integer> getAdaptationI() {
      return this.resistanceI;
   }
}
