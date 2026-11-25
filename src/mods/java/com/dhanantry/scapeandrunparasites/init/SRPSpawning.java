package com.dhanantry.scapeandrunparasites.init;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityBanoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityCanraAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityEmanaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityHullAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityNoglaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityRanracAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityShycoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOronco;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityTerla;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityCrux;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHeed;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHost;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHostII;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooS;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityButhol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityKol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityLodo;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityNuuh;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityRathol;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityDorpa;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfBear;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfCow;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfDragonE;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfEnderman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHorse;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHuman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPig;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPlayer;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfSheep;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfVillager;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfWolf;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityBano;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityCanra;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityEmana;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityHull;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityNogla;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityRanrac;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityShyco;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityAlafha;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityAnged;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityEsor;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityFlog;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityGanro;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityOmboo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityElvia;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityJinjo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityLencia;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityPheon;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityVesta;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasite;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public class SRPSpawning {
   private static List<SpawnListEntry> PHASEZERO;
   private static List<SpawnListEntry> PHASEONE;
   private static List<SpawnListEntry> PHASETWO;
   private static List<SpawnListEntry> PHASETHREE;
   private static List<SpawnListEntry> PHASEFOUR;
   private static List<SpawnListEntry> PHASEFIVE;
   private static List<SpawnListEntry> PHASESIX;
   private static List<SpawnListEntry> PHASESEVEN;
   private static List<SpawnListEntry> PHASEEIGHT;

   public SRPSpawning() {
   }

   public static void init() {
      if (SRPConfig.allowMobs) {
         if (!SRPConfigSystems.useEvolution || SRPConfigSystems.useEvolution && !SRPConfigSystems.phaseCustomSpawner) {
            for(Biome biome : Biome.REGISTRY) {
               addSpawn(0, EntityShyco.class, 1, 1, biome, SRPConfigMobs.shycoSpawnRate, SRPConfigMobs.shycoEnabled);
               addSpawn(0, EntityShycoAdapted.class, 1, 1, biome, SRPConfigMobs.shycoASpawnRate, SRPConfigMobs.shycoEnabled);
               addSpawn(0, EntityEmana.class, 2, 3, biome, SRPConfigMobs.emanaSpawnRate, SRPConfigMobs.emanaEnabled);
               addSpawn(0, EntityEmanaAdapted.class, 2, 3, biome, SRPConfigMobs.emanaASpawnRate, SRPConfigMobs.emanaEnabled);
               addSpawn(0, EntityHull.class, 4, 6, biome, SRPConfigMobs.hullSpawnRate, SRPConfigMobs.hullEnabled);
               addSpawn(0, EntityHullAdapted.class, 4, 6, biome, SRPConfigMobs.hullASpawnRate, SRPConfigMobs.hullEnabled);
               addSpawn(0, EntityCanra.class, 1, 1, biome, SRPConfigMobs.canraSpawnRate, SRPConfigMobs.canraEnabled);
               addSpawn(0, EntityCanraAdapted.class, 1, 1, biome, SRPConfigMobs.canraASpawnRate, SRPConfigMobs.canraEnabled);
               addSpawn(0, EntityNogla.class, 1, 2, biome, SRPConfigMobs.noglaSpawnRate, SRPConfigMobs.noglaEnabled);
               addSpawn(0, EntityNoglaAdapted.class, 1, 2, biome, SRPConfigMobs.noglaASpawnRate, SRPConfigMobs.noglaEnabled);
               addSpawn(0, EntityBano.class, 1, 1, biome, SRPConfigMobs.zetmoSpawnRate, SRPConfigMobs.zetmoEnabled);
               addSpawn(0, EntityBanoAdapted.class, 1, 1, biome, SRPConfigMobs.zetmoASpawnRate, SRPConfigMobs.zetmoEnabled);
               addSpawn(0, EntityRanrac.class, 1, 1, biome, SRPConfigMobs.arachnidaSpawnRate, SRPConfigMobs.arachnidaEnabled);
               addSpawn(0, EntityRanracAdapted.class, 1, 1, biome, SRPConfigMobs.arachnidaASpawnRate, SRPConfigMobs.arachnidaEnabled);
               addSpawn(0, EntityRathol.class, 2, 2, biome, SRPConfigMobs.ratholSpawnRate, SRPConfigMobs.ratholEnabled);
               addSpawn(0, EntityButhol.class, 1, 2, biome, SRPConfigMobs.butholSpawnRate, SRPConfigMobs.butholEnabled);
               addSpawn(0, EntityMudo.class, 3, 6, biome, SRPConfigMobs.mudoSpawnRate, SRPConfigMobs.mudoEnabled);
               addSpawn(0, EntityLodo.class, 2, 5, biome, SRPConfigMobs.lodoSpawnRate, SRPConfigMobs.lodoEnabled);
               addSpawn(0, EntityKol.class, 1, 1, biome, SRPConfigMobs.kolSpawnRate, SRPConfigMobs.kolEnabled);
               addSpawn(0, EntityNuuh.class, 1, 1, biome, SRPConfigMobs.nuuhSpawnRate, SRPConfigMobs.nuuhEnabled);
               addSpawn(0, EntityInfBear.class, 1, 1, biome, SRPConfigMobs.infbearSpawnRate, SRPConfigMobs.infbearEnabled);
               addSpawn(0, EntityDorpa.class, 1, 1, biome, SRPConfigMobs.dorpaSpawnRate, SRPConfigMobs.dorpaEnabled);
               addSpawn(0, EntityInfEnderman.class, 1, 1, biome, SRPConfigMobs.infendermanSpawnRate, SRPConfigMobs.infendermanEnabled);
               addSpawn(0, EntityInfHuman.class, 3, 5, biome, SRPConfigMobs.infhumanSpawnRate, SRPConfigMobs.infhumanEnabled);
               addSpawn(0, EntityInfCow.class, 1, 3, biome, SRPConfigMobs.infcowSpawnRate, SRPConfigMobs.infcowEnabled);
               addSpawn(0, EntityInfSheep.class, 1, 3, biome, SRPConfigMobs.infsheepSpawnRate, SRPConfigMobs.infsheepEnabled);
               addSpawn(0, EntityInfWolf.class, 3, 6, biome, SRPConfigMobs.infwolfSpawnRate, SRPConfigMobs.infwolfEnabled);
               addSpawn(0, EntityInfPig.class, 3, 6, biome, SRPConfigMobs.infpigSpawnRate, SRPConfigMobs.infpigEnabled);
               addSpawn(0, EntityInfVillager.class, 3, 6, biome, SRPConfigMobs.infvillagerSpawnRate, SRPConfigMobs.infvillagerEnabled);
               addSpawn(0, EntityInfHorse.class, 3, 6, biome, SRPConfigMobs.infhorseSpawnRate, SRPConfigMobs.infhorseEnabled);
               addSpawn(0, EntityInfPlayer.class, 3, 6, biome, SRPConfigMobs.infadventurerSpawnRate, SRPConfigMobs.infadventurerEnabled);
               addSpawn(0, EntityInfDragonE.class, 3, 6, biome, SRPConfigMobs.infdragoneSpawnRate, SRPConfigMobs.infdragoneEnabled);
               addSpawn(0, EntityHost.class, 1, 1, biome, SRPConfigMobs.hostSpawnRate, SRPConfigMobs.hostEnabled);
               addSpawn(0, EntityHostII.class, 1, 1, biome, SRPConfigMobs.herdSpawnRate, SRPConfigMobs.herdEnabled);
               addSpawn(0, EntityHeed.class, 1, 1, biome, SRPConfigMobs.heedSpawnRate, SRPConfigMobs.heedEnabled);
               addSpawn(0, EntityCrux.class, 1, 1, biome, SRPConfigMobs.cruxaSpawnRate, SRPConfigMobs.cruxaEnabled);
               addSpawn(0, EntityInhooS.class, 2, 5, biome, SRPConfigMobs.inhooSSpawnRate, SRPConfigMobs.inhooSEnabled);
               addSpawn(0, EntityInhooM.class, 2, 5, biome, SRPConfigMobs.inhooMSpawnRate, SRPConfigMobs.inhooMEnabled);
               addSpawn(0, EntityAlafha.class, 1, 1, biome, SRPConfigMobs.alafhaSpawnRate, SRPConfigMobs.alafhaEnabled);
               addSpawn(0, EntityGanro.class, 1, 1, biome, SRPConfigMobs.ganroSpawnRate, SRPConfigMobs.ganroEnabled);
               addSpawn(0, EntityAnged.class, 2, 2, biome, SRPConfigMobs.angedSpawnRate, SRPConfigMobs.angedEnabled);
               addSpawn(0, EntityEsor.class, 1, 1, biome, SRPConfigMobs.esorSpawnRate, SRPConfigMobs.esorEnabled);
               addSpawn(0, EntityOmboo.class, 2, 2, biome, SRPConfigMobs.ombooSpawnRate, SRPConfigMobs.ombooEnabled);
               addSpawn(0, EntityFlog.class, 3, 6, biome, SRPConfigMobs.flogSpawnRate, SRPConfigMobs.flogEnabled);
               addSpawn(0, EntityJinjo.class, 3, 6, biome, SRPConfigMobs.jinjoSpawnRate, SRPConfigMobs.jinjoEnabled);
               addSpawn(0, EntityElvia.class, 3, 6, biome, SRPConfigMobs.elviaSpawnRate, SRPConfigMobs.elviaEnabled);
               addSpawn(0, EntityLencia.class, 3, 6, biome, SRPConfigMobs.lenciaSpawnRate, SRPConfigMobs.lenciaEnabled);
               addSpawn(0, EntityPheon.class, 3, 6, biome, SRPConfigMobs.pheonSpawnRate, SRPConfigMobs.pheonEnabled);
               addSpawn(0, EntityVesta.class, 3, 6, biome, SRPConfigMobs.vestaSpawnRate, SRPConfigMobs.vestaEnabled);
               addSpawn(0, EntityOronco.class, 1, 1, biome, SRPConfigMobs.oroncoSpawnRate, SRPConfigMobs.oroncoEnabled);
               addSpawn(0, EntityTerla.class, 1, 1, biome, SRPConfigMobs.terlaSpawnRate, SRPConfigMobs.terlaEnabled);
               addSpawn(1, EntityVenkrol.class, 1, 1, biome, SRPConfigMobs.venkrolSpawnrate, SRPConfigSystems.rsEnabled);
            }
         } else {
            String[] here = new String[4];
            PHASEZERO = new ArrayList();
            PHASEONE = new ArrayList();
            PHASETWO = new ArrayList();
            PHASETHREE = new ArrayList();
            PHASEFOUR = new ArrayList();
            PHASEFIVE = new ArrayList();
            PHASESIX = new ArrayList();
            PHASESEVEN = new ArrayList();
            PHASEEIGHT = new ArrayList();
            String[] entry = SRPConfigSystems.phaseSpawnEntryZero;

            for(int i = 0; i < entry.length; ++i) {
               if (entry[i] != null) {
                  here = entry[i].split(";");
                  int min = Integer.parseInt(here[1]);
                  int max = Integer.parseInt(here[2]);
                  int weight = Integer.parseInt(here[3]);
                  PHASEZERO.add(new SpawnListEntry(EntityList.getClass(new ResourceLocation(here[0])), weight, min, max));
               }
            }

            entry = SRPConfigSystems.phaseSpawnEntryOne;

            for(int i = 0; i < entry.length; ++i) {
               if (entry[i] != null) {
                  here = entry[i].split(";");
                  int min = Integer.parseInt(here[1]);
                  int max = Integer.parseInt(here[2]);
                  int weight = Integer.parseInt(here[3]);
                  PHASEONE.add(new SpawnListEntry(EntityList.getClass(new ResourceLocation(here[0])), weight, min, max));
               }
            }

            entry = SRPConfigSystems.phaseSpawnEntryTwo;

            for(int i = 0; i < entry.length; ++i) {
               if (entry[i] != null) {
                  here = entry[i].split(";");
                  int min = Integer.parseInt(here[1]);
                  int max = Integer.parseInt(here[2]);
                  int weight = Integer.parseInt(here[3]);
                  PHASETWO.add(new SpawnListEntry(EntityList.getClass(new ResourceLocation(here[0])), weight, min, max));
               }
            }

            entry = SRPConfigSystems.phaseSpawnEntryThree;

            for(int i = 0; i < entry.length; ++i) {
               if (entry[i] != null) {
                  here = entry[i].split(";");
                  int min = Integer.parseInt(here[1]);
                  int max = Integer.parseInt(here[2]);
                  int weight = Integer.parseInt(here[3]);
                  PHASETHREE.add(new SpawnListEntry(EntityList.getClass(new ResourceLocation(here[0])), weight, min, max));
               }
            }

            entry = SRPConfigSystems.phaseSpawnEntryFour;

            for(int i = 0; i < entry.length; ++i) {
               if (entry[i] != null) {
                  here = entry[i].split(";");
                  int min = Integer.parseInt(here[1]);
                  int max = Integer.parseInt(here[2]);
                  int weight = Integer.parseInt(here[3]);
                  PHASEFOUR.add(new SpawnListEntry(EntityList.getClass(new ResourceLocation(here[0])), weight, min, max));
               }
            }

            entry = SRPConfigSystems.phaseSpawnEntryFive;

            for(int i = 0; i < entry.length; ++i) {
               if (entry[i] != null) {
                  here = entry[i].split(";");
                  int min = Integer.parseInt(here[1]);
                  int max = Integer.parseInt(here[2]);
                  int weight = Integer.parseInt(here[3]);
                  PHASEFIVE.add(new SpawnListEntry(EntityList.getClass(new ResourceLocation(here[0])), weight, min, max));
               }
            }

            entry = SRPConfigSystems.phaseSpawnEntrySix;

            for(int i = 0; i < entry.length; ++i) {
               if (entry[i] != null) {
                  here = entry[i].split(";");
                  int min = Integer.parseInt(here[1]);
                  int max = Integer.parseInt(here[2]);
                  int weight = Integer.parseInt(here[3]);
                  PHASESIX.add(new SpawnListEntry(EntityList.getClass(new ResourceLocation(here[0])), weight, min, max));
               }
            }

            entry = SRPConfigSystems.phaseSpawnEntrySeven;

            for(int i = 0; i < entry.length; ++i) {
               if (entry[i] != null) {
                  here = entry[i].split(";");
                  int min = Integer.parseInt(here[1]);
                  int max = Integer.parseInt(here[2]);
                  int weight = Integer.parseInt(here[3]);
                  PHASESEVEN.add(new SpawnListEntry(EntityList.getClass(new ResourceLocation(here[0])), weight, min, max));
               }
            }

            entry = SRPConfigSystems.phaseSpawnEntryEight;

            for(int i = 0; i < entry.length; ++i) {
               if (entry[i] != null) {
                  here = entry[i].split(";");
                  int min = Integer.parseInt(here[1]);
                  int max = Integer.parseInt(here[2]);
                  int weight = Integer.parseInt(here[3]);
                  PHASEEIGHT.add(new SpawnListEntry(EntityList.getClass(new ResourceLocation(here[0])), weight, min, max));
               }
            }
         }

      }
   }

   public static void removeInit() {
      if (!SRPConfigSystems.useEvolution) {
         for(Biome biome : Biome.REGISTRY) {
            boolean rem = true;
            boolean gg = true;

            while(gg) {
               for(int i = 0; i < biome.getSpawnableList(EnumCreatureType.MONSTER).size(); ++i) {
                  String eee = ((SpawnListEntry)biome.getSpawnableList(EnumCreatureType.MONSTER).get(i)).entityClass.toString();
                  if (eee.contains("scapeandrunparasites") && rem) {
                     gg = true;
                     biome.getSpawnableList(EnumCreatureType.MONSTER).remove(i);
                  } else {
                     gg = false;
                  }
               }

               for(int i = 0; i < biome.getSpawnableList(EnumCreatureType.MONSTER).size(); ++i) {
                  String eee = ((SpawnListEntry)biome.getSpawnableList(EnumCreatureType.MONSTER).get(i)).entityClass.toString();
                  if (eee.contains("scapeandrunparasites") && rem) {
                     gg = true;
                     biome.getSpawnableList(EnumCreatureType.MONSTER).remove(i);
                  }
               }
            }
         }

      }
   }

   public static List<SpawnListEntry> getSpawns(byte phase) {
      switch(phase) {
      case 0:
         return PHASEZERO;
      case 1:
         return PHASEONE;
      case 2:
         return PHASETWO;
      case 3:
         return PHASETHREE;
      case 4:
         return PHASEFOUR;
      case 5:
         return PHASEFIVE;
      case 6:
         return PHASESIX;
      case 7:
         return PHASESEVEN;
      case 8:
         return PHASEEIGHT;
      default:
         return null;
      }
   }

   public static void initBiome() {
      Biome biome = SRPBiomes.biomeInfested;
      String[] here = new String[5];

      for(int i = 0; i < SRPConfigWorld.biomeSpawnEntry.length; ++i) {
         if (SRPConfigWorld.biomeSpawnEntry[i] != null) {
            here = SRPConfigWorld.biomeSpawnEntry[i].split(";");
            int min = Integer.parseInt(here[1]);
            int max = Integer.parseInt(here[2]);
            int weight = Integer.parseInt(here[3]);
            int type = Integer.parseInt(here[4]);
            addSpawn(type, EntityList.getClass(new ResourceLocation(here[0])), min, max, biome, weight, true);
         }
      }

   }

   public static void addSpawn(int type, Class<? extends EntityLiving> entity, int groupMin, int groupMax, Biome biome, int weight, boolean addSpawn) {
      if (weight > 0 && addSpawn && type == 0) {
         biome.getSpawnableList(EnumCreatureType.MONSTER).add(new SpawnListEntry(entity, weight, groupMin, groupMax));
      } else if (weight > 0 && addSpawn && type == 1) {
         biome.getSpawnableList(EnumCreatureType.CREATURE).add(new SpawnListEntry(entity, weight, groupMin, groupMax));
      }

   }

   @EventBusSubscriber(
      modid = "srparasites"
   )
   public static class DimensionHandler {
      public DimensionHandler() {
      }

      @SubscribeEvent
      public static void onSpawn(CheckSpawn event) {
         if (event.getEntity() instanceof EntityParasiteBase) {
            if (!SRPConfigSystems.useEvolution) {
               boolean inv = false;
               boolean flagI = false;
               if (SRPConfig.blackListedDimensionsWhite) {
                  inv = true;
               }

               int[] origin = SRPConfig.blackListedDimensions;
               int kol = origin.length;
               int var5 = 0;

               while(true) {
                  label75: {
                     if (var5 < kol) {
                        int i = origin[var5];
                        if (!SRPConfig.blackListedDimensionsWhite) {
                           if (i == event.getWorld().provider.getDimension()) {
                              event.setResult(Result.DENY);
                              return;
                           }
                           break label75;
                        }

                        if (i != event.getWorld().provider.getDimension()) {
                           break label75;
                        }

                        flagI = true;
                     }

                     if (inv && !flagI) {
                        event.setResult(Result.DENY);
                        return;
                     }
                     break;
                  }

                  ++var5;
               }
            }

            if (SRPConfigSystems.useEvolution || SRPConfigWorld.coloniesActivated) {
               EntityParasiteBase parasite = (EntityParasiteBase)event.getEntity();
               if (!parasite.canSpawnSpawn) {
                  event.setResult(Result.DENY);
                  return;
               }

               SRPWorldData data = SRPWorldData.get(parasite.world);
               parasite.setCreatedPhase(data.getEvolutionPhase());
               if (data.getEvolutionPhase() <= -1) {
                  event.setResult(Result.DENY);
                  return;
               }

               if (parasite.getParasiteType() == 7) {
                  if (!SRPConfigWorld.coloniesActivated) {
                     event.setResult(Result.DENY);
                  }

                  BlockPos origin = data.nearestColonyPosition(parasite.getPosition(), 0, false);
                  if (origin != null) {
                     EntityKol kol = (EntityKol)parasite;
                     kol.setTask(origin, data.getColonyDistanceSpreadByPosition(origin, false, 0));
                     return;
                  }

                  event.setResult(Result.DENY);
                  return;
               }

               if (SRPConfigSystems.useEvolution && !SRPConfigSystems.phaseCustomSpawner && !canSpawninPhase(data.getEvolutionPhase(), parasite)) {
                  event.setResult(Result.DENY);
                  return;
               }

               if (checkEvoLock(parasite.getParasiteIDRegister(), SRPSaveData.get(parasite.world)) || checkColoLock(parasite.getParasiteIDRegister(), data, parasite)) {
                  event.setResult(Result.DENY);
                  return;
               }
            }
         }

      }

      private static boolean canSpawninPhase(byte evPhase, EntityParasiteBase parasite) {
         byte type = parasite.getParasiteType();
         switch(evPhase) {
         case -1:
            return false;
         case 0:
            if (type >= SRPConfigSystems.phaseMaxParasiteIDZero || type <= SRPConfigSystems.phaseCancelParasiteIDZero) {
               return false;
            }
            break;
         case 1:
            if (type >= SRPConfigSystems.phaseMaxParasiteIDOne || type <= SRPConfigSystems.phaseCancelParasiteIDOne) {
               return false;
            }
            break;
         case 2:
            if (type >= SRPConfigSystems.phaseMaxParasiteIDTwo || type <= SRPConfigSystems.phaseCancelParasiteIDTwo) {
               return false;
            }
            break;
         case 3:
            if (type >= SRPConfigSystems.phaseMaxParasiteIDThree || type <= SRPConfigSystems.phaseCancelParasiteIDThree) {
               return false;
            }
            break;
         case 4:
            if (type >= SRPConfigSystems.phaseMaxParasiteIDFour || type <= SRPConfigSystems.phaseCancelParasiteIDFour) {
               return false;
            }
            break;
         case 5:
            if (type >= SRPConfigSystems.phaseMaxParasiteIDFive || type <= SRPConfigSystems.phaseCancelParasiteIDFive) {
               return false;
            }
            break;
         case 6:
            if (type >= SRPConfigSystems.phaseMaxParasiteIDSix || type <= SRPConfigSystems.phaseCancelParasiteIDSix) {
               return false;
            }
            break;
         case 7:
            if (type >= SRPConfigSystems.phaseMaxParasiteIDSeven || type <= SRPConfigSystems.phaseCancelParasiteIDSeven) {
               return false;
            }
            break;
         case 8:
            if (type >= SRPConfigSystems.phaseMaxParasiteIDEight || type <= SRPConfigSystems.phaseCancelParasiteIDEight) {
               return false;
            }
         }

         parasite.setCreatedPhase(evPhase);
         return true;
      }

      private static boolean checkEvoLock(int in, SRPSaveData data) {
         return data.checkParasiteID(in);
      }

      private static boolean checkColoLock(int in, SRPWorldData data, EntityParasiteBase para) {
         int points = data.totalColonyPoints(0);
         String[] here = new String[2];
         int id = 0;
         int req = 0;

         for(int i = 0; i < SRPConfigWorld.preeValues.length; ++i) {
            here = SRPConfigWorld.preeValues[i].split(";");
            id = Integer.parseInt(here[0]);
            if (id == in) {
               if (para.world.getBiome(para.getPosition()) instanceof BiomeParasite) {
                  return true;
               }

               req = Integer.parseInt(here[1]);
               if (points >= req) {
                  return false;
               }

               return true;
            }
         }

         return false;
      }
   }
}
