package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIV;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIV;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasite;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAINexusGrow extends EntityAIBase {
   private final EntityPStationaryArchitect parent;
   private byte venkrolCurrentStage;
   private double tickss;
   private byte type;
   private boolean canGrow;
   private int dimLock;

   public EntityAINexusGrow(EntityPStationaryArchitect venkrol, int CURRENTstage) {
      this.parent = venkrol;
      this.tickss = 20.0D;
      this.type = 1;
      this.venkrolCurrentStage = (byte)CURRENTstage;
      this.canGrow = true;
      this.dimLock = -1;
      String[] here = new String[2];

      for(int i = 0; i < SRPConfigSystems.maximumStageList.length; ++i) {
         here = SRPConfigSystems.maximumStageList[i].split(";");
         if (here[0].length() != 0) {
            int dimension = Integer.parseInt(here[0]);
            if (dimension == this.parent.world.provider.getDimension()) {
               this.dimLock = Integer.parseInt(here[1]);
            }
         }
      }

   }

   public EntityAINexusGrow(EntityPStationaryArchitect venkrol, int CURRENTstage, byte in) {
      this(venkrol, CURRENTstage);
      this.type = in;
   }

   public boolean shouldExecute() {
      --this.tickss;
      if (this.tickss < 0.0D) {
         this.tickss = 20.0D;
         this.checkPhase();
         return this.canGrow;
      } else {
         return false;
      }
   }

   public void resetTask() {
   }

   public void updateTask() {
      if (this.venkrolCurrentStage == 3 && this.parent.world.getBiome(this.parent.getPosition()) instanceof BiomeParasite) {
         this.tickss = 20000.0D;
      } else {
         int j = this.parent.getActualT();
         ++j;
         this.parent.setActualT(j);
         if (j > this.parent.neededTime) {
            this.parent.setConvert(false);
            switch(this.type) {
            case 1:
               this.upgradeV();
               break;
            case 2:
               this.upgradeD();
            }
         }
      }

   }

   private void checkPhase() {
      if (this.dimLock >= this.venkrolCurrentStage + 1) {
         this.canGrow = false;
      } else {
         if (SRPConfigSystems.useEvolution) {
            EaglercraftRandom rand = new EaglercraftRandom();
            switch(SRPWorldData.get(this.parent.world).getEvolutionPhase()) {
            case -1:
               this.canGrow = false;
               break;
            case 0:
               this.canGrow = false;
               break;
            case 1:
               switch(this.venkrolCurrentStage) {
               case 1:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIGrowPenaltyOne) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               case 2:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIIGrowPenaltyOne) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               case 3:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIIIGrowPenaltyOne) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               default:
                  return;
               }
            case 2:
               switch(this.venkrolCurrentStage) {
               case 1:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIGrowPenaltyTwo) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               case 2:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIIGrowPenaltyTwo) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               case 3:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIIIGrowPenaltyTwo) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               default:
                  return;
               }
            case 3:
               switch(this.venkrolCurrentStage) {
               case 1:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIGrowPenaltyThree) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               case 2:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIIGrowPenaltyThree) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               case 3:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIIIGrowPenaltyThree) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               default:
                  return;
               }
            case 4:
               switch(this.venkrolCurrentStage) {
               case 1:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIGrowPenaltyFour) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               case 2:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIIGrowPenaltyFour) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               case 3:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIIIGrowPenaltyFour) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               default:
                  return;
               }
            case 5:
               switch(this.venkrolCurrentStage) {
               case 1:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIGrowPenaltyFive) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               case 2:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIIGrowPenaltyFive) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               case 3:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIIIGrowPenaltyFive) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               default:
                  return;
               }
            case 6:
               switch(this.venkrolCurrentStage) {
               case 1:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIGrowPenaltySix) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               case 2:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIIGrowPenaltySix) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               case 3:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIIIGrowPenaltySix) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               default:
                  return;
               }
            case 7:
               switch(this.venkrolCurrentStage) {
               case 1:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIGrowPenaltySeven) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               case 2:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIIGrowPenaltySeven) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               case 3:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIIIGrowPenaltySeven) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }

                  return;
               default:
                  return;
               }
            case 8:
               switch(this.venkrolCurrentStage) {
               case 1:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIGrowPenaltyEight) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }
                  break;
               case 2:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIIGrowPenaltyEight) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }
                  break;
               case 3:
                  if (rand.nextDouble() < SRPConfigSystems.beckonStageIIIGrowPenaltyEight) {
                     this.canGrow = false;
                  } else {
                     this.canGrow = true;
                  }
               }
            }
         }

      }
   }

   private void upgradeV() {
      if (ParasiteEventEntity.canSpawnNext) {
         if (this.venkrolCurrentStage == 1) {
            EntityVenkrolSII out = new EntityVenkrolSII(this.parent.world);
            out.setRSSpawned(this.parent.getRSSpawned());
            out.cannotDespawn(this.parent.canDespawn());
            ParasiteEventEntity.spawnNext(this.parent, out, true, false);
            if (SRPConfigSystems.rsSounds) {
               this.parent.playSound(SRPSounds.VENKROLSII, 10.0F, 1.0F);
            }
         } else if (this.venkrolCurrentStage == 2) {
            EntityVenkrolSIII out = new EntityVenkrolSIII(this.parent.world);
            out.setRSSpawned(this.parent.getRSSpawned());
            out.cannotDespawn(this.parent.canDespawn());
            ParasiteEventEntity.spawnNext(this.parent, out, true, false);
            if (SRPConfigSystems.rsSounds) {
               this.parent.playSound(SRPSounds.VENKROLSIII, 9.0F, 1.0F);
            }
         } else if (this.venkrolCurrentStage == 3) {
            if (ParasiteEventWorld.canBiomeStillExist(this.parent.world, this.parent.getPosition(), false) >= 1) {
               this.tickss = 20000.0D;
               return;
            }

            EntityVenkrolSIV out = new EntityVenkrolSIV(this.parent.world);
            out.setRSSpawned(this.parent.getRSSpawned());
            out.cannotDespawn(this.parent.canDespawn());
            ParasiteEventEntity.spawnNext(this.parent, out, true, false);
            if (SRPConfigSystems.rsSounds) {
               this.parent.playSound(SRPSounds.VENKROLSIV, 10.0F, 1.0F);
            }
         }

      }
   }

   private void upgradeD() {
      if (ParasiteEventEntity.canSpawnNext) {
         if (this.venkrolCurrentStage == 1) {
            EntityDodSII out = new EntityDodSII(this.parent.world);
            out.setRSSpawned(this.parent.getRSSpawned());
            out.cannotDespawn(this.parent.canDespawn());
            ParasiteEventEntity.spawnNext(this.parent, out, true, false);
            if (SRPConfigSystems.rsSounds) {
               this.parent.playSound(SRPSounds.DODSII, 10.0F, 1.0F);
            }
         } else if (this.venkrolCurrentStage == 2) {
            EntityDodSIII out = new EntityDodSIII(this.parent.world);
            out.setRSSpawned(this.parent.getRSSpawned());
            out.cannotDespawn(this.parent.canDespawn());
            ParasiteEventEntity.spawnNext(this.parent, out, true, false);
            if (SRPConfigSystems.rsSounds) {
               this.parent.playSound(SRPSounds.DODSIII, 9.0F, 1.0F);
            }
         } else if (this.venkrolCurrentStage == 3) {
            if (ParasiteEventWorld.rangeOfColony(this.parent.world, this.parent.getPosition(), true) != null) {
               this.tickss = 20000.0D;
               return;
            }

            EntityDodSIV out = new EntityDodSIV(this.parent.world);
            out.setRSSpawned(this.parent.getRSSpawned());
            out.cannotDespawn(this.parent.canDespawn());
            ParasiteEventEntity.spawnNext(this.parent, out, true, false);
            if (SRPConfigSystems.rsSounds) {
               this.parent.playSound(SRPSounds.DODSIV, 10.0F, 1.0F);
            }
         }

      }
   }
}
