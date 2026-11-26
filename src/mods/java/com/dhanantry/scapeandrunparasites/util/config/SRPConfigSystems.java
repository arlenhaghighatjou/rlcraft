package com.dhanantry.scapeandrunparasites.util.config;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class SRPConfigSystems {
   private static final String CATEGORY_GENERAL_SISTEMS = "configuration_systems";
   private static final String REINFORCEMENT_CATEGORY = "reinforcement_system";
   public static boolean rsEnabled = true;
   public static boolean rsSounds = true;
   public static float rschance = 0.045F;
   public static boolean rsBlockI = true;
   public static float rsBlockIMaxH = 4.5F;
   public static boolean rsPlayer = false;
   public static int rsBlockLight = 16;
   public static int rsMaxVenkrols = 100;
   public static int rsCooldown = 5;
   public static int rsVenkrolChance = 20000;
   public static boolean rsIgnoreCooldownAtSpawn = true;
   public static String optionalBlock = "minecraft:dirt";
   public static String optionalBlockRub = "minecraft:mossy_cobblestone";
   public static String[] equipment = new String[]{"head;minecraft:leather_helmet;0.5", "head;minecraft:iron_helmet;0.3", "head;minecraft:diamond_helmet;0.3"};
   public static String[] blockBList = new String[0];
   public static boolean blockBListWhite = false;
   public static float rsBlockParticleS = 0.05F;
   public static float rsBlockParticleF = 0.05F;
   public static String[] maximumStageList = new String[]{"1;3"};
   public static boolean rsSky = false;
   public static boolean rsSkyResidue = false;
   public static int rsResidueY = 0;
   public static double rsVenkrolEmpty = 0.05D;
   public static int rsMaxVenkrolChunk = 2;
   public static int rsBlockRevertStage = 1;
   private static final String MERGE_CATEGORY = "merge_System";
   public static boolean mergeRandom = true;
   public static double mergeHealth = 0.5D;
   public static String[] mergeMobTable = new String[]{"srparasites:pri_summoner;0", "srparasites:pri_longarms;0", "srparasites:pri_reeker;0", "srparasites:pri_manducater;0", "srparasites:pri_bolster;0", "srparasites:pri_yelloweye;0", "srparasites:pri_arachnida;0"};
   public static String[] mergeInfValues = new String[]{"srparasites:sim_human;1", "srparasites:sim_cow;1", "srparasites:sim_sheep;1", "srparasites:sim_wolf;1", "srparasites:sim_pig;1", "srparasites:sim_villager;1"};
   private static final String STATUSEFFECTS_CATEGORY = "status_effects";
   public static boolean fearActive = true;
   public static float fearBlockChance = 0.0F;
   public static float fearItemChance = 0.0F;
   public static float fearInvChance = 0.0F;
   public static String[] fearItemBlackList = new String[0];
   public static boolean fearItemBlackListWhite = false;
   public static float fearFallDamage = 1.5F;
   public static float fearAirDamage = 1.5F;
   public static boolean cothActive = true;
   public static boolean cothLootDisable = true;
   public static int cothAura = 3;
   public static float cothConvert = 0.3F;
   public static float cothInfected = 0.7F;
   public static float cothPrimitive = 0.6F;
   public static float cothAdapted = 0.5F;
   public static float cothPure = 0.4F;
   public static float cothUnhide = 0.3F;
   public static String[] COTHVictimParasite = new String[]{"minecraft:pig;srparasites:sim_pig", "minecraft:sheep;srparasites:sim_sheep", "minecraft:cow;srparasites:sim_cow", "minecraft:wolf;srparasites:sim_wolf", "minecraft:horse;srparasites:sim_horse", "minecraft:zombie;srparasites:sim_human", "minecraft:husk;srparasites:sim_human", "minecraft:zombie_villager;srparasites:sim_villager", "minecraft:villager;srparasites:sim_villager", "minecraft:polar_bear;srparasites:sim_bear", "minecraft:enderman;srparasites:sim_enderman"};
   public static String[] COTHItemPrevent = new String[]{"minecraft:golden_apple;0.1;300", "minecraft:golden_carrot;0.03;150"};
   public static String[] COTHImmuneList = new String[]{"minecraft:villager_golem", "minecraft:vex", "minecraft:creeper", "minecraft:slime", "minecraft:blaze", "minecraft:guardian", "minecraft:elder_guardian", "minecraft:stray", "minecraft:skeleton", "minecraft:skeleton_horse", "minecraft:wither_skeleton", "minecraft:magma_cube", "minecraft:ghast", "minecraft:shulker", "minecraft:snowman", "wyrmsofnyrus", "srrevenants"};
   public static boolean COTHImmuneListWhite = false;
   public static float bleedingDamage = 0.02F;
   public static float bleedingDamageCap = 100.0F;
   public static int corroValue = 3;
   public static double corrNot = 0.1D;
   public static boolean viralEnable = true;
   public static float viralAmount = 0.5F;
   public static boolean rageEnable = true;
   public static double rageDamage = 0.1D;
   public static double rageSpeed = 0.1D;
   public static double epelChanceCOTH = 0.7D;
   public static float needlerDamage = 0.4F;
   public static int needlerTerminal = 7;
   public static String[] needlerImmuneList = new String[0];
   public static boolean needlerImmuneListWhite = false;
   public static double adapsChance = 0.2D;
   private static final String EVOLUTION_CATEGORY = "parasite_evolution_phases";
   public static boolean useEvolution = true;
   public static int valueKill = 1;
   public static int valueCOTH = 6;
   public static int valueBlock = 3;
   public static int valueMerge = 10;
   public static int valueEvolutionDespawn = 100;
   public static byte evolutionNodeUnlock = 5;
   public static byte evolutionColonyUnlock = 4;
   public static byte evolutionCothStopLoot = 3;
   public static byte evolutionSleepDenied = 5;
   public static byte evolutionAssimilatedDehiding = 5;
   public static byte evolutionMudoAttack = 2;
   public static byte evolutionBeckonIgnoreCooldown = 5;
   public static byte evolutionSpawningIgnoreSunlight = 7;
   public static byte evolutionStopFishing = 4;
   public static byte evolutionOneMind = 3;
   public static byte evolutionTotalKill = 7;
   public static int valueLossBlockStain = 1;
   public static int valueLossBlockTrunk = 1;
   public static int valueLossBlockRubble = 1;
   public static String[] evolutionDimStart = new String[]{"-1;-1;50", "0;0;0", "1;-1;100"};
   public static boolean evolutionDimGainInverted = false;
   public static int[] evolutionDimGain = new int[0];
   public static boolean evolutionDimLossInverted = false;
   public static int[] evolutionDimLoss = new int[0];
   public static String[] evolutionParasiteLock = new String[]{"1;3;64"};
   public static int[] blackListedDimensionsEPP = new int[]{0, 1, -1};
   public static String phaseWarningZero = "ZERO";
   public static int sleepPenaltyZero = 3;
   public static byte phaseMaxParasiteIDZero = 5;
   public static byte phaseCancelParasiteIDZero = 0;
   public static int phaseScentBonusZero = -200;
   public static byte phaseScentReactionZero = 11;
   public static String[] phaseSpawnEntryZero = new String[]{"srparasites:buglin;2;6;30"};
   public static int phaseKillsOne = 400;
   public static byte phaseMaxParasiteIDOne = 11;
   public static byte phaseCancelParasiteIDOne = 0;
   public static double phaseKillCountPlusOne = 0.0D;
   public static double reinforcementSystemChanceOne = 0.0D;
   public static double beckonStageIGrowPenaltyOne = 1.0D;
   public static double beckonStageIIGrowPenaltyOne = 1.0D;
   public static double beckonStageIIIGrowPenaltyOne = 1.0D;
   public static double mobSpawningCOTHChanceOne = 0.0D;
   public static double cropGrowStunnedOne = 0.0D;
   public static int luredValueOne = 2;
   public static String phaseWarningOne = "One";
   public static int sleepPenaltyOne = 5;
   public static int phaseScentBonusOne = 5;
   public static byte phaseScentReactionOne = 10;
   public static int phaseDelayTicksOne = 1200;
   public static int phaseResidueOne = 0;
   public static String[] phaseSpawnEntryOne = new String[]{"srparasites:buglin;2;6;30", "srparasites:rupter;3;6;30"};
   public static int phaseKillsTwo = 800;
   public static byte phaseMaxParasiteIDTwo = 31;
   public static byte phaseCancelParasiteIDTwo = 0;
   public static double phaseKillCountPlusTwo = 0.0D;
   public static double reinforcementSystemChanceTwo = 0.0D;
   public static double beckonStageIGrowPenaltyTwo = 1.0D;
   public static double beckonStageIIGrowPenaltyTwo = 1.0D;
   public static double beckonStageIIIGrowPenaltyTwo = 1.0D;
   public static double mobSpawningCOTHChanceTwo = 0.0D;
   public static double cropGrowStunnedTwo = 0.0D;
   public static int luredValueTwo = 5;
   public static String phaseWarningTwo = "Two";
   public static int sleepPenaltyTwo = 50;
   public static int phaseScentBonusTwo = 20;
   public static byte phaseScentReactionTwo = 9;
   public static int phaseDelayTicksTwo = 1200;
   public static int phaseResidueTwo = 0;
   public static String[] phaseSpawnEntryTwo = new String[]{"srparasites:buglin;2;6;30", "srparasites:rupter;3;6;30", "srparasites:sim_bigspider;3;5;25", "srparasites:sim_human;3;5;25", "srparasites:sim_cow;3;5;25", "srparasites:sim_sheep;3;5;25", "srparasites:sim_wolf;3;5;25", "srparasites:sim_pig;3;5;25", "srparasites:sim_villager;3;5;25", "srparasites:sim_adventurer;3;5;25", "srparasites:sim_horse;3;5;25", "srparasites:sim_bear;3;5;25", "srparasites:sim_dragone;1;1;1"};
   public static int phaseKillsThree = 1800;
   public static byte phaseMaxParasiteIDThree = 31;
   public static byte phaseCancelParasiteIDThree = 0;
   public static double phaseKillCountPlusThree = 0.05D;
   public static double reinforcementSystemChanceThree = 0.02D;
   public static double beckonStageIGrowPenaltyThree = 0.95D;
   public static double beckonStageIIGrowPenaltyThree = 0.95D;
   public static double beckonStageIIIGrowPenaltyThree = 0.95D;
   public static double mobSpawningCOTHChanceThree = 0.0D;
   public static double cropGrowStunnedThree = 0.0D;
   public static int luredValueThree = 50;
   public static String phaseWarningThree = "Three";
   public static int sleepPenaltyThree = 5000;
   public static int phaseScentBonusThree = 50;
   public static byte phaseScentReactionThree = 9;
   public static int phaseDelayTicksThree = 1200;
   public static int phaseResidueThree = 0;
   public static String[] phaseSpawnEntryThree = new String[]{"srparasites:rupter;3;6;30", "srparasites:sim_bigspider;3;5;25", "srparasites:sim_human;3;5;25", "srparasites:sim_cow;3;5;25", "srparasites:sim_sheep;3;5;25", "srparasites:sim_wolf;3;5;25", "srparasites:sim_pig;3;5;25", "srparasites:sim_villager;3;5;25", "srparasites:sim_adventurer;3;5;25", "srparasites:sim_horse;3;5;25", "srparasites:sim_bear;3;5;25", "srparasites:sim_enderman;1;1;1", "srparasites:sim_dragone;1;1;1", "srparasites:bomber_heavy;1;1;1", "srparasites:wraith;1;1;1", "srparasites:bogle;1;1;1", "srparasites:haunter;1;1;1", "srparasites:carrier_colony;1;1;1"};
   public static int phaseKillsFour = 100000;
   public static byte phaseMaxParasiteIDFour = 41;
   public static byte phaseCancelParasiteIDFour = 4;
   public static double phaseKillCountPlusFour = 0.075D;
   public static double reinforcementSystemChanceFour = 0.03D;
   public static double beckonStageIGrowPenaltyFour = 0.0D;
   public static double beckonStageIIGrowPenaltyFour = 0.95D;
   public static double beckonStageIIIGrowPenaltyFour = 0.95D;
   public static double mobSpawningCOTHChanceFour = 0.0D;
   public static double cropGrowStunnedFour = 0.0D;
   public static int luredValueFour = 5000;
   public static String phaseWarningFour = "Four";
   public static int sleepPenaltyFour = 50000;
   public static int phaseScentBonusFour = 90;
   public static byte phaseScentReactionFour = 8;
   public static int phaseDelayTicksFour = 1200;
   public static int phaseResidueFour = 0;
   public static String[] phaseSpawnEntryFour = new String[]{"srparasites:rupter;3;6;30", "srparasites:sim_bigspider;3;5;25", "srparasites:sim_human;3;5;25", "srparasites:sim_cow;3;5;25", "srparasites:sim_sheep;3;5;25", "srparasites:sim_wolf;3;5;25", "srparasites:sim_pig;3;5;25", "srparasites:sim_villager;3;5;25", "srparasites:sim_adventurer;3;5;25", "srparasites:sim_horse;3;5;25", "srparasites:sim_bear;3;5;25", "srparasites:sim_enderman;1;1;1", "srparasites:sim_dragone;1;1;1", "srparasites:pri_longarms;2;3;15", "srparasites:pri_manducater;2;3;15", "srparasites:pri_reeker;2;3;15", "srparasites:pri_yelloweye;2;3;15", "srparasites:pri_summoner;2;3;15", "srparasites:pri_bolster;2;3;15", "srparasites:pri_arachnida;2;3;15", "srparasites:thrall;3;5;25", "srparasites:bomber_heavy;1;1;1", "srparasites:wraith;1;1;1", "srparasites:bogle;1;1;1", "srparasites:haunter;1;1;1", "srparasites:carrier_colony;1;1;1"};
   public static int phaseKillsFive = 1000000;
   public static byte phaseMaxParasiteIDFive = 41;
   public static byte phaseCancelParasiteIDFive = 4;
   public static double phaseKillCountPlusFive = 0.1D;
   public static double reinforcementSystemChanceFive = 0.04D;
   public static double beckonStageIGrowPenaltyFive = 0.0D;
   public static double beckonStageIIGrowPenaltyFive = 0.0D;
   public static double beckonStageIIIGrowPenaltyFive = 0.95D;
   public static double mobSpawningCOTHChanceFive = 0.0D;
   public static double cropGrowStunnedFive = 0.0D;
   public static int luredValueFive = 50000;
   public static String phaseWarningFive = "Five";
   public static int sleepPenaltyFive = 1500000;
   public static int phaseScentBonusFive = 150;
   public static byte phaseScentReactionFive = 6;
   public static int phaseDelayTicksFive = 1200;
   public static int phaseResidueFive = 0;
   public static String[] phaseSpawnEntryFive = new String[]{"srparasites:rupter;3;6;30", "srparasites:sim_bigspider;3;5;25", "srparasites:sim_human;3;5;25", "srparasites:sim_cow;3;5;25", "srparasites:sim_sheep;3;5;25", "srparasites:sim_wolf;3;5;25", "srparasites:sim_pig;3;5;25", "srparasites:sim_villager;3;5;25", "srparasites:sim_adventurer;3;5;25", "srparasites:sim_horse;3;5;25", "srparasites:sim_bear;3;5;25", "srparasites:sim_enderman;3;5;25", "srparasites:sim_dragone;3;5;25", "srparasites:pri_longarms;2;3;15", "srparasites:pri_manducater;2;3;15", "srparasites:pri_reeker;2;3;15", "srparasites:pri_yelloweye;2;3;15", "srparasites:pri_summoner;2;3;15", "srparasites:pri_bolster;2;3;15", "srparasites:pri_arachnida;2;3;15", "srparasites:thrall;3;5;25", "srparasites:bomber_heavy;1;1;1", "srparasites:wraith;1;1;1", "srparasites:bogle;1;1;1", "srparasites:haunter;1;1;1", "srparasites:carrier_colony;1;1;1"};
   public static int phaseKillsSix = 30000000;
   public static byte phaseMaxParasiteIDSix = 51;
   public static byte phaseCancelParasiteIDSix = 30;
   public static double phaseKillCountPlusSix = 0.15D;
   public static double reinforcementSystemChanceSix = 0.05D;
   public static double beckonStageIGrowPenaltySix = 0.0D;
   public static double beckonStageIIGrowPenaltySix = 0.0D;
   public static double beckonStageIIIGrowPenaltySix = 0.0D;
   public static double mobSpawningCOTHChanceSix = 0.2D;
   public static double cropGrowStunnedSix = 0.1D;
   public static int luredValueSix = 1500000;
   public static String phaseWarningSix = "Six";
   public static int sleepPenaltySix = 5000000;
   public static int phaseScentBonusSix = 240;
   public static byte phaseScentReactionSix = 5;
   public static int phaseDelayTicksSix = 1200;
   public static int phaseResidueSix = 30000;
   public static String[] phaseSpawnEntrySix = new String[]{"srparasites:mangler;3;6;30", "srparasites:pri_longarms;2;3;15", "srparasites:pri_manducater;2;3;15", "srparasites:pri_reeker;2;3;15", "srparasites:pri_yelloweye;2;3;15", "srparasites:pri_summoner;2;3;15", "srparasites:pri_bolster;2;3;15", "srparasites:pri_arachnida;2;3;15", "srparasites:thrall;3;5;25", "srparasites:ada_longarms;2;3;15", "srparasites:ada_manducater;2;3;15", "srparasites:ada_reeker;2;3;15", "srparasites:ada_yelloweye;2;3;15", "srparasites:ada_summoner;2;3;15", "srparasites:ada_bolster;2;3;15", "srparasites:ada_arachnida;2;3;15", "srparasites:bomber_heavy;1;1;1", "srparasites:wraith;1;1;1", "srparasites:bogle;1;1;1", "srparasites:haunter;1;1;1", "srparasites:carrier_colony;1;1;1"};
   public static int phaseKillsSeven = 100000000;
   public static byte phaseMaxParasiteIDSeven = 61;
   public static byte phaseCancelParasiteIDSeven = 40;
   public static double phaseKillCountPlusSeven = 0.25D;
   public static double reinforcementSystemChanceSeven = 0.07D;
   public static double beckonStageIGrowPenaltySeven = 0.0D;
   public static double beckonStageIIGrowPenaltySeven = 0.0D;
   public static double beckonStageIIIGrowPenaltySeven = 0.0D;
   public static double mobSpawningCOTHChanceSeven = 0.4D;
   public static double cropGrowStunnedSeven = 0.3D;
   public static int luredValueSeven = 5000000;
   public static String phaseWarningSeven = "Seven";
   public static int sleepPenaltySeven = 30000000;
   public static int phaseScentBonusSeven = 360;
   public static byte phaseScentReactionSeven = 4;
   public static int phaseDelayTicksSeven = 1200;
   public static int phaseResidueSeven = 60000;
   public static String[] phaseSpawnEntrySeven = new String[]{"srparasites:mangler;3;6;30", "srparasites:ada_longarms;2;3;15", "srparasites:ada_manducater;2;3;15", "srparasites:ada_reeker;2;3;15", "srparasites:ada_yelloweye;2;3;15", "srparasites:ada_summoner;2;3;15", "srparasites:ada_bolster;2;3;15", "srparasites:ada_arachnida;2;3;15", "srparasites:grunt;2;3;20", "srparasites:monarch;1;2;15", "srparasites:bomber_heavy;1;1;1", "srparasites:wraith;1;1;1", "srparasites:bogle;1;1;1", "srparasites:haunter;1;1;1", "srparasites:carrier_colony;1;1;1"};
   public static int phaseKillsEight = 600000000;
   public static byte phaseMaxParasiteIDEight = 64;
   public static byte phaseCancelParasiteIDEight = 40;
   public static double phaseKillCountPlusEight = 0.35D;
   public static double reinforcementSystemChanceEight = 0.09D;
   public static double beckonStageIGrowPenaltyEight = 0.0D;
   public static double beckonStageIIGrowPenaltyEight = 0.0D;
   public static double beckonStageIIIGrowPenaltyEight = 0.0D;
   public static double mobSpawningCOTHChanceEight = 0.8D;
   public static double cropGrowStunnedEight = 0.6D;
   public static int luredValueEight = 30000000;
   public static String phaseWarningEight = "Eight";
   public static int sleepPenaltyEight = 1;
   public static int phaseScentBonusEight = 500;
   public static byte phaseScentReactionEight = 2;
   public static int phaseDelayTicksEight = 1200;
   public static int phaseResidueEight = 100000;
   public static int phaseEightTotalPoints = 2000000000;
   public static String[] phaseSpawnEntryEight = new String[]{"srparasites:mangler;3;6;30", "srparasites:ada_longarms;2;3;15", "srparasites:ada_manducater;2;3;15", "srparasites:ada_reeker;2;3;15", "srparasites:ada_yelloweye;2;3;15", "srparasites:ada_summoner;2;3;15", "srparasites:ada_bolster;2;3;15", "srparasites:ada_arachnida;2;3;15", "srparasites:grunt;2;3;20", "srparasites:monarch;1;2;15", "srparasites:bomber_heavy;1;1;1", "srparasites:wraith;1;1;1", "srparasites:bogle;1;1;1", "srparasites:haunter;1;1;1", "srparasites:carrier_colony;1;1;1"};
   public static byte defaultEvoPhase = -1;
   public static int defaultEvoPoints = -100;
   public static boolean phaseCustomSpawner = true;
   private static final String EVOLUTION_CATEGORY0 = "parasite_evolution_phases_0";
   private static final String EVOLUTION_CATEGORY1 = "parasite_evolution_phases_1";
   private static final String EVOLUTION_CATEGORY2 = "parasite_evolution_phases_2";
   private static final String EVOLUTION_CATEGORY3 = "parasite_evolution_phases_3";
   private static final String EVOLUTION_CATEGORY4 = "parasite_evolution_phases_4";
   private static final String EVOLUTION_CATEGORY5 = "parasite_evolution_phases_5";
   private static final String EVOLUTION_CATEGORY6 = "parasite_evolution_phases_6";
   private static final String EVOLUTION_CATEGORY7 = "parasite_evolution_phases_7";
   private static final String EVOLUTION_CATEGORY8 = "parasite_evolution_phases_8";
   private static final String HIVEMIND_CATEGORY = "parasite_collective_consciousness";
   public static boolean useOneMind = false;
   public static int oneMinRangeCap = 64;
   public static boolean oneMindPlayer = false;
   public static boolean oneMindDebug = false;
   public static byte scentGoActive = 5;
   public static int scentSpawnWaves = 12;
   public static int scentMiniDis = 15;
   public static int scentMaxDis = 32;
   public static int scentMobLimit = 30;
   public static int scentLifeDeath = 5;
   public static int scentLifeObserver = 60;
   public static int scentLifeTactical = 300;
   public static String[] scentLevelZero = new String[]{"srparasites:rupter"};
   public static int scentWaveMinMobWaveZero = 2;
   public static int scentWaveMaxMobWaveZero = 5;
   public static int scentWaveMinimumZero = 2;
   public static int scentWaveMaximumZero = 5;
   public static String[] scentLevelOne = new String[]{"srparasites:rupter"};
   public static int scentLevelPointsOne = 5;
   public static int scentWaveMinMobWaveOne = 2;
   public static int scentWaveMaxMobWaveOne = 5;
   public static int scentWaveMinimumOne = 2;
   public static int scentWaveMaximumOne = 5;
   public static String[] scentLevelTwo = new String[]{"srparasites:rupter"};
   public static int scentLevelPointsTwo = 20;
   public static int scentWaveMinMobWaveTwo = 2;
   public static int scentWaveMaxMobWaveTwo = 5;
   public static int scentWaveMinimumTwo = 2;
   public static int scentWaveMaximumTwo = 5;
   public static String[] scentLevelThree = new String[]{"srparasites:rupter", "srparasites:sim_human"};
   public static int scentLevelPointsThree = 50;
   public static int scentWaveMinMobWaveThree = 2;
   public static int scentWaveMaxMobWaveThree = 5;
   public static int scentWaveMinimumThree = 2;
   public static int scentWaveMaximumThree = 5;
   public static String[] scentLevelFour = new String[]{"srparasites:rupter", "srparasites:sim_human", "srparasites:pri_longarms"};
   public static int scentLevelPointsFour = 90;
   public static int scentWaveMinMobWaveFour = 2;
   public static int scentWaveMaxMobWaveFour = 5;
   public static int scentWaveMinimumFour = 2;
   public static int scentWaveMaximumFour = 5;
   public static String[] scentLevelFive = new String[]{"srparasites:rupter", "srparasites:pri_longarms", "srparasites:pri_yelloweye"};
   public static int scentLevelPointsFive = 150;
   public static int scentWaveMinMobWaveFive = 2;
   public static int scentWaveMaxMobWaveFive = 5;
   public static int scentWaveMinimumFive = 2;
   public static int scentWaveMaximumFive = 5;
   public static String[] scentLevelSix = new String[]{"srparasites:rupter", "srparasites:ada_longarms"};
   public static int scentLevelPointsSix = 240;
   public static int scentWaveMinMobWaveSix = 2;
   public static int scentWaveMaxMobWaveSix = 5;
   public static int scentWaveMinimumSix = 2;
   public static int scentWaveMaximumSix = 5;
   public static String[] scentLevelSeven = new String[]{"srparasites:rupter", "srparasites:marauder"};
   public static int scentLevelPointsSeven = 360;
   public static int scentWaveMinMobWaveSeven = 2;
   public static int scentWaveMaxMobWaveSeven = 5;
   public static int scentWaveMinimumSeven = 2;
   public static int scentWaveMaximumSeven = 5;
   public static String[] scentLevelEight = new String[]{"srparasites:rupter", "srparasites:marauder", "srparasites:vigilante"};
   public static int scentLevelPointsEight = 500;
   public static int scentWaveMinMobWaveEight = 2;
   public static int scentWaveMaxMobWaveEight = 5;
   public static int scentWaveMinimumEight = 2;
   public static int scentWaveMaximumEight = 5;
   private static final String HIVEMIND_CATEGORY0 = "parasite_collective_consciousness_scent_lvl_0";
   private static final String HIVEMIND_CATEGORY1 = "parasite_collective_consciousness_scent_lvl_1";
   private static final String HIVEMIND_CATEGORY2 = "parasite_collective_consciousness_scent_lvl_2";
   private static final String HIVEMIND_CATEGORY3 = "parasite_collective_consciousness_scent_lvl_3";
   private static final String HIVEMIND_CATEGORY4 = "parasite_collective_consciousness_scent_lvl_4";
   private static final String HIVEMIND_CATEGORY5 = "parasite_collective_consciousness_scent_lvl_5";
   private static final String HIVEMIND_CATEGORY6 = "parasite_collective_consciousness_scent_lvl_6";
   private static final String HIVEMIND_CATEGORY7 = "parasite_collective_consciousness_scent_lvl_7";
   private static final String HIVEMIND_CATEGORY8 = "parasite_collective_consciousness_scent_lvl_8";
   private static final String TOTALDEVELOPMENT_CATEGORY = "parasite_ubiquitous_development";

   public SRPConfigSystems() {
   }

   private static void initGeneralSistemsConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("configuration_systems", "System configuration \nVersion:1.9.11\n \nPotions IDs \nsrparasites:coth \nsrparasites:fear \nsrparasites:antimall \nsrparasites:bleed \nsrparasites:corrosive \nsrparasites:viral \nsrparasites:rage \nsrparasites:repel \nsrparasites:senses \nsrparasites:prey \nsrparasites:debar \nsrparasites:needler \nsrparasites:foster \nsrparasites:link \n ");
   }

   private static void initreinforcementSystemConfig(Configuration cfg) {
      String RSdescription = " \n When a parasite is killed, there is a chance for it to call reinforcements, this means that\n a Beckon will spawn nearby and will help in the fight.\n \n The Beckon has different stages and that will tell how many and which parasites it will spawn.";
      String equipS = " Ex. \"head;minecraft:iron_helmet;0.1\"  Where: \n \"head\" is for the slot (head, chest, legs, feet), \n \"minecraft:iron_helmet\" is for the item, \n \"0.1\" is for the chance to ignore block damage (1 = 100%). \n";
      String growingBeckon = " Ex. \"1;3\"  Where: \n \"1\" is for the dimension, \n \"3\" is for the stage it cannot grow up. \n";
      cfg.addCustomCategoryComment("reinforcement_system", "Reinforcement System" + RSdescription);
      rsEnabled = cfg.getBoolean("Reinforcement System Enabled", "reinforcement_system", rsEnabled, "Set to false if you want to disable the Reinforcement System.");
      rschance = cfg.getFloat("Reinforcement System Chance", "reinforcement_system", rschance, 0.0F, 1.0F, "Chance (1 = 100%) to spawn a Beckon (This is ignored if Evolution Phases are enabled, it has its own option).");
      rsSounds = cfg.getBoolean("Reinforcement System Sounds", "reinforcement_system", rsSounds, "Set to false if you want to disable the Reinforcement System Sounds \n (A bell sound is played when a Beckon is summoned or when it grew up to the next stage).");
      rsBlockI = cfg.getBoolean("Reinforcement System Infestation", "reinforcement_system", rsBlockI, "Set to false if you want to disable the Reinforcement System Block Infestation.");
      rsBlockIMaxH = cfg.getFloat("Reinforcement System Hardness", "reinforcement_system", rsBlockIMaxH, 0.01F, 100.0F, "Max Hardness of the block it can infest.");
      rsPlayer = cfg.getBoolean("Reinforcement System Player Only", "reinforcement_system", rsPlayer, "Set to true if you want the Reinforcement System to only be activated by the Players.");
      equipment = cfg.getStringList("Reinforcement System Equipment", "reinforcement_system", equipment, "Equipment that can protect you from the blocks from doing damage." + equipS);
      blockBList = cfg.getStringList("Reinforcement System Blocks BlackList", "reinforcement_system", blockBList, "List of block that can't be infested, Ex: \"minecraft:stonebrick\" or just \"minecraft\" for a whole mod");
      blockBListWhite = cfg.getBoolean("Reinforcement System Blocks BlackList Inverted", "reinforcement_system", blockBListWhite, "Set to true if you want to use the list as a WhiteList.");
      optionalBlock = cfg.getString("Reinforcement System Revert Block", "reinforcement_system", optionalBlock, "What the infested block turns into when the Beckons die");
      optionalBlockRub = cfg.getString("Reinforcement System Revert Block 2", "reinforcement_system", optionalBlockRub, "What the infested block 2 turns into when the Beckons die");
      rsBlockLight = cfg.getInt("Reinforcement System Block Light Level", "reinforcement_system", rsBlockLight, 0, 16, "If the light level is higher than this, the block will not spread.");
      rsVenkrolChance = cfg.getInt("Reinforcement System Block Residue Chance", "reinforcement_system", rsVenkrolChance, 0, 2147483640, "One in X to spawn a Beckon on the Infested Block Residue (random tick) (This value is ignored if Evolution Phases are enabled, it has its own option).");
      rsBlockParticleS = cfg.getFloat("Reinforcement System Particle Spore Chance", "reinforcement_system", rsBlockParticleS, 0.0F, 1.0F, "Chance (1 = 100%) to spawn spore particle in an infested block (if available, every tick).");
      rsBlockParticleF = cfg.getFloat("Reinforcement System Particle Fog Chance", "reinforcement_system", rsBlockParticleF, 0.0F, 1.0F, "Chance (1 = 100%) to spawn fog particle in an infested block ((if available, every tick).");
      rsMaxVenkrols = cfg.getInt("Reinforcement System Maximum Beckons", "reinforcement_system", rsMaxVenkrols, 1, 2147483640, "Maximum number of Beckons that can spawn from the RS (per dimension).");
      rsCooldown = cfg.getInt("Reinforcement System Cooldown", "reinforcement_system", rsCooldown, 1, 2147483640, "Cooldown (in seconds) for the Reinforcement System spawning Beckons.");
      maximumStageList = cfg.getStringList("Reinforcement System Maximum Stage", "reinforcement_system", maximumStageList, "Maximum Stage a Beckon can grow up in a Dimension." + growingBeckon);
      rsIgnoreCooldownAtSpawn = cfg.getBoolean("Reinforcement System Ignore Cooldown", "reinforcement_system", rsIgnoreCooldownAtSpawn, "Set to false if you don't want the Beckons to ignore their summoning cooldown.when spawning.");
      rsSky = cfg.getBoolean("Reinforcement System Sky", "reinforcement_system", rsSky, "Set to true if you want Beckons to only spawn if the position can see the sky (only those who are called by the death of a parasite).");
      rsSkyResidue = cfg.getBoolean("Reinforcement System Sky Residue", "reinforcement_system", rsSkyResidue, "Set to true if you want Beckons to only spawn if the position can see the sky (from residue blocks).");
      rsResidueY = cfg.getInt("Reinforcement System Block Residue Y", "reinforcement_system", rsResidueY, 0, 300, "If the y value of the block is below this, Beckons will not spawn from it.");
      rsVenkrolEmpty = (double)cfg.getFloat("Reinforcement System Infestation Spawning Beckons", "reinforcement_system", (float)rsVenkrolEmpty, 0.0F, 1.0F, "Chance (1 = 100%) to spawn a Beckon in Infested Blocks (S!=1) if no Beckon is around, to further infest the land.");
      rsMaxVenkrolChunk = cfg.getInt("Reinforcement System Maximum Beckon Chunk", "reinforcement_system", rsMaxVenkrolChunk, 0, 50, "If the number of Beckons in a chunk is equal of greater than this then a Beckon will not spawn form the RS/Residue Block.");
      rsBlockRevertStage = cfg.getInt("Reinforcement System Block Revert Stage", "reinforcement_system", rsBlockRevertStage, 1, 3, "If a Beckon is killed on an Infested block, the block will start a chain reaction and revert back all connected blocks if the meta of the block is less than or equal to this value AND the stage of the Beckon is greater than or equal to the meta value .");
   }

   private static void initmergeConfig(Configuration cfg) {
      String MergeDescription = " \n The Assimilated have the ability to learn from their battles and if the conditions are met, they will \n start to melt until they're some Moving Flesh \n If 2 of these things meet, they will fuse together and grow, if they get big enough they will spawn \n a mob from the Mob List.\n \n Conditions for the Assimilated to turn into Moving Flesh: \n \n -The first condition: the Assimilated needs to reach the primitive killcount. \n -The second condition: it needs to be next to 3 more Assimilated, regardless of their killcount/specie \n OR it needs to be next to a Moving Flesh. \n If the Assimilated with the killcount is next to atleast 3 more Assimilated, the four of them will start \n to melt BUT if it is next to a Moving flesh, only it will start to melt.";
      String mergeS = " Ex. \"minecraft:zombie;11\"  Where: \n \"minecraft:zombie\" is for the entity, \n \"11\" is for the value the entity has (value must be an integer). \n Each Assimilated has a value, the value then passes to the Moving Flesh and when 2 Moving Flesh merge together both values will add up\n If the value matches one in the list, the corresponding entity will spawn, else it will be a random one. \n";
      String mergeI = " Ex. \"srparasites:infhuman;22\"  Where: \n \"srparasites:infhuman\" is for the entity (only works for Assimilated versions minus Big Spider, Enderdragon), \n \"22\" is for the value the Moving Flesh will have (value must be an integer). \n";
      cfg.addCustomCategoryComment("merge_System", "Merge System" + MergeDescription);
      mergeRandom = cfg.getBoolean("Merge System Random", "merge_System", mergeRandom, "Set to false if you don't want to spawn random mobs from the Mob List (values will be used).");
      mergeHealth = (double)cfg.getFloat("Merge System Mob Health", "merge_System", (float)mergeHealth, 0.0F, 1.0F, "Amount of health (1 = 100%) the mob spawns with.");
      mergeMobTable = cfg.getStringList("Merge System Mob List", "merge_System", mergeMobTable, "Mob list used in the merge system to spawn mobs." + mergeS);
      mergeInfValues = cfg.getStringList("Merge System Assimilated Values", "merge_System", mergeInfValues, "Assimilated mob list with their values used in the merge system to spawn mobs." + mergeI);
   }

   private static void initstatuseffectsConfig(Configuration cfg) {
      String helper = " Where: \n \"minecraft:stone\" is the item, \n \"0.1\" is the chance to apply the effect (1=100%), \n \"200\" is the duration of the effect in seconds. \n Right click an entity to give it Camouflage. \n";
      String description = " \n Fear.\n \n COTH.\n \n Needler.\n \n Bleeding.\n \n Camouflage";
      cfg.addCustomCategoryComment("status_effects", "Status effects of the mod" + description);
      fearActive = cfg.getBoolean("Fear Acivated", "status_effects", fearActive, "Set to false if you dont want the Fear effect to work");
      fearFallDamage = cfg.getFloat("Fear Fall Damage Multiplier", "status_effects", fearFallDamage, 0.0F, 100.0F, "Fear fall damage multiplier, multiplied by the amp.");
      fearAirDamage = cfg.getFloat("Fear Air Damage Multiplier", "status_effects", fearAirDamage, 0.0F, 100.0F, "Fear air damage multiplier, multiplied by the amp.");
      fearBlockChance = cfg.getFloat("Fear Block Fail Chance", "status_effects", fearBlockChance, 0.0F, 1.0F, "Chance (1 = 100%) to fail using a block under the Fear status effect (with an amplifier of 2 and up) - WARNING - EXPERIMENTAL.");
      fearItemChance = cfg.getFloat("Fear Item Fail Chance", "status_effects", fearItemChance, 0.0F, 1.0F, "Chance (1 = 100%) to to fail using an item under the Fear status effect (with an amplifier of 3 and up) - WARNING - EXPERIMENTAL.");
      fearInvChance = cfg.getFloat("Fear Inventory Fail Chance", "status_effects", fearInvChance, 0.0F, 1.0F, "Chance (1 = 100%) to to fail to open the inventory under the Fear status effect (with an amplifier of 4 and up) - WARNING - EXPERIMENTAL.");
      fearItemBlackList = cfg.getStringList("Fear Item BlackList", "status_effects", fearItemBlackList, "Items that wont be affected by this effect, Ex: \"minecraft:flint_and_steel\" or just \"minecraft\" for a whole mod");
      fearItemBlackListWhite = cfg.getBoolean("Fear Item BlackList Inverted", "status_effects", fearItemBlackListWhite, "Set to true if you want to use the list as a WhiteList.");
      cothActive = cfg.getBoolean("COTH Acivated", "status_effects", cothActive, "Set to false if you dont want the COTH effect to work");
      COTHVictimParasite = cfg.getStringList("COTH Assimilated Transformation", "status_effects", COTHVictimParasite, "Table that will be used for converting mobs, the Rupter will also be using this");
      COTHImmuneList = cfg.getStringList("COTH Mob Immune Mob List", "status_effects", COTHImmuneList, "Mobs that are immune to the COTH effect, Ex: \"minecraft:zombie\" or just \"minecraft\" for a whole mod");
      COTHImmuneListWhite = cfg.getBoolean("COTH Mob Immune Mob List Inverted", "status_effects", COTHImmuneListWhite, "Set to true if you want to use the list as a WhiteList.");
      cothAura = cfg.getInt("COTH Aura", "status_effects", cothAura, 0, 10, "Set to false if you dont want the effect to spread from mob to mob");
      cothConvert = cfg.getFloat("COTH Convert At Kill", "status_effects", cothConvert, 0.0F, 1.0F, "Chance (1 = 100%) for any parasite to convert its victim when killed (only if the victim is under COTH effect).");
      cothInfected = cfg.getFloat("Version Assimilated COTH", "status_effects", cothInfected, 0.0F, 1.0F, "Chance (1 = 100%) to infect with COTH per hit.");
      cothPrimitive = cfg.getFloat("Version Primitive COTH", "status_effects", cothPrimitive, 0.0F, 1.0F, "Chance (1 = 100%) to infect with COTH per hit.");
      cothAdapted = cfg.getFloat("Version Adapted COTH", "status_effects", cothAdapted, 0.0F, 1.0F, "Chance (1 = 100%) to infect with COTH per hit.");
      cothPure = cfg.getFloat("Version Pure COTH", "status_effects", cothPure, 0.0F, 1.0F, "Chance (1 = 100%) to infect with COTH per hit.");
      cothUnhide = cfg.getFloat("COTH Health Threshold", "status_effects", cothUnhide, 0.0F, 1.0F, "Below this health threshold, the victim will attempt to unhide (if available).");
      cothLootDisable = cfg.getBoolean("COTH Looting", "status_effects", cothLootDisable, "Set to false if you want mobs with COTH to drop loot (This is ignored if Evolution Phases are enabled, it has its own option)");
      bleedingDamage = cfg.getFloat("Bleeding Damage", "status_effects", bleedingDamage, 0.0F, 1.0F, "Damage (1 = 100%) this effect will deal (victims total health).");
      bleedingDamageCap = cfg.getFloat("Bleeding Damage Limit", "status_effects", bleedingDamageCap, 0.0F, 1000.0F, "Maximum damage that this effect will inflict.");
      corroValue = cfg.getInt("Corrosive Damage Value", "status_effects", corroValue, 0, 100000, "Amount of damage your armor will receive under this effect.");
      corrNot = (double)cfg.getFloat("Corrosive Percentage To Work", "status_effects", (float)corrNot, 0.0F, 1.0F, "If the durability of the armor is below this threshold, the effect will not work.");
      viralEnable = cfg.getBoolean("Viral Acivated", "status_effects", viralEnable, "Set to false if you dont want the viral effect");
      viralAmount = cfg.getFloat("Viral Multiplier", "status_effects", viralAmount, 0.0F, 100.0F, "This value will be multiplied by the amplifier and then multiplied by the damage which will be added to the total damage.");
      rageEnable = cfg.getBoolean("Rage Acivated", "status_effects", rageEnable, "Set to false if you dont want parasites to spawn/give the rage effect");
      rageDamage = (double)cfg.getFloat("Enraged Damage", "status_effects", (float)rageDamage, 0.0F, 100.0F, "(1 = 100%) How much this effect will increase damage.");
      rageSpeed = (double)cfg.getFloat("Enraged Speed", "status_effects", (float)rageSpeed, 0.0F, 100.0F, "(1 = 100%) How much this effect will increase speed.");
      COTHItemPrevent = cfg.getStringList("Camouflage Item List", "status_effects", COTHItemPrevent, "List of items that can prevent an entity form getting COTH. Ex. \"minecraft:stone;0.1;200\" " + helper);
      epelChanceCOTH = (double)cfg.getFloat("Camouflage Chance", "status_effects", (float)epelChanceCOTH, 0.0F, 1.0F, "(1 = 100%) Chance to avoid getting infected by COTH.");
      needlerDamage = cfg.getFloat("Needler Damage", "status_effects", needlerDamage, 0.0F, 1.0F, "Damage (1 = 100%) this effect will deal (victims total health) by reaching high amplifier.");
      needlerTerminal = cfg.getInt("Needler Terminal Amplifier", "status_effects", needlerTerminal, 0, 100, "Mobs reaching this amplfifier will explode.");
      needlerImmuneList = cfg.getStringList("Needler Immune Mob List", "status_effects", needlerImmuneList, "Mobs that are immune to the Needler effect, Ex: \"minecraft:zombie\" or just \"minecraft\" for a whole mod");
      needlerImmuneListWhite = cfg.getBoolean("Needler Immune Mob List Inverted", "status_effects", needlerImmuneListWhite, "Set to true if you want to use the list as a WhiteList.");
      adapsChance = (double)cfg.getFloat("Link Chance", "status_effects", (float)adapsChance, 0.0F, 1.0F, "(1 = 100%) Cahnce to send their adaptation on death (multiplied by the amp + 1).");
   }

   private static void initevolutionConfig(Configuration cfg) {
      String evolutionDescription = " \n If Evolution Phases is true, the current phase will dictate if a parasite can spawn naturally \n in the world and by reaching some phases, they will unlock some bonuses\n *Spawnrates are not affected by the Phases\n  \n -> Phase 0:  - Points required: 0\n    -Buglins spawn naturally\n  \n -> Phase 1:  - Points required: 50\n    -Rupters spawn naturally, with a hit and run behavior\n    -Animals and mobs infected with COTH will not mutate unless forced to low health\n    \n -> Phase 2:  - Points required: 100\n    -Assimilated versions spawn naturally\n    -Rupters will now attack as normal\n    \n -> Phase 3:  - Points required: 1,000\n    -Reinforcement system is unlocked\n    -Beckons will slowly grow to Stage II and beyond\n    -The passive point gaining is now active\n \t -Mobs with COTH will stop dropping loot\n    \n -> Phase 4:  - Points required: 100,000\n    -Primitive versions spawn naturally\n    -Normal growth to Beckons Stage II, but will slowly grow to Stage III\n    -Passive point gaining recieves a buff to the number gaining\n    -Reinforcement system chance increased\n    -Colonies now unlocked\n \t -Buglins will stop spawning naturally\n\t -No more fishing\n    \n -> Phase 5:  - Points required: 1,000,000\n    -Passive point gaining recieves a buff to the number gaining\n    -Players can't sleep anymore\n    -Normal growth to Beckons Stage III but will slowly grow  to Stage IV\n    -Nodes now unlocked\n    -COTH hidden mobs are now dropping their disguise\n    -Beckons will ignore summoning cooldown when spawning\n    -Reinforcement system chance increased\n    \n -> Phase 6:  - Points required: 30,000,000\n    -Normal growth to Beckons Stage IV\n    -Passive point gaining recieves a buff to the number gaining\n    -Assimilated versions and lower tier parasites will stop spawning naturally\n    -Adapted versions spawn naturally\n    -Mobs have a chance to spawn with COTH now\n    -Crop growth is now partially stunted\n    -Reinforcement system chance increased\n    \n -> Phase 7:  - Points required: 100,000,000\n    -Primitive versions and lower tier parasites will stop spawning naturally\n    -Pure versions spawn naturally\n    -Chance for a mob to spawn with COTH increased\n    -Passive point gaining recieves a buff to the number gaining\n    -Crop growth is now partially stunted\n    -Reinforcement system chance increased\n    -Parasites will ignore sunlight when spawning naturally\n    \n -> Phase 8:  - Points required: 600,000,000\n    -Ancient versions spawn naturally\n    -Passive point gaining recieves a buff to the number gaining\n    -Chance for a mob to spawn with COTH increased\n    -Crop growth is now partially stunted\n    -Reinforcement system chance increased\n    \n    \n \n To reduce their evolution phase you need to craft Lure Blocks";
      cfg.addCustomCategoryComment("parasite_evolution_phases", "Evolution Phases" + evolutionDescription);
      String exa = " Where: \n \"1\" is the dimension, \n \"8\" is the evolution phase,\n \"10\" is the number of points parasites will have, if the phase is -1 these points will be negative (Set the points above 0 if the phase is -1 or -2)\n Parasites will not spawn and can't earn points if the phase is -2";
      String exa22 = " Where: \n \"1\" is the dimension, \n \"8\" is the evolution phase,\n \"10\" is the parasite ID \n\n List of IDs:\n Longarms 1, Big Spider 2, Heavy Carrier 3. Yelloweye 4, Buglin 5, Sim Human 6, \n Manducater 7, Summoner 8, Overseer 9, Reeker 10, Flying Carrier 11, Rupter 12, \n Sim Cow 13, Sim Sheep 14, Sim Wolf 15, Beckon SI 16, Bolster 17, Beckon SII 18, \n Beckon SIII 19, Overlord 20, Sim Wolf H 21, Sim Sheep H 22, Moving Flesh 23, Dreadnaut 24,\n Vigilante 25, Sim Pig 26, Sim Villager 27, Sim Cow H 28, Kyphosis 29, Sentry 30, \n Sim Pig H 31, Sim Villager H 32, Warden 33, Ancient Pod 34, Dreadnaut Tentacle 35, \n Worker 36, XXXXX 37, Arachnida 38, Incomplete Small 39, Sim Adventurer 40, Beckon SIV 41, \n XXXXXX 42, Incomplete Medium 43, Sim Horse 44, Sim Horse H 45, Sim Human H 46, \n Light Bomber 47, Host 48, Sim Bear 49, Marauder 50, Ada Longarms 51, Ada Manducater 52, \n Ada Summoner 53, Ada Reeker 54, Ada Yelloweye 55, Ada Bolster 56, XXXXX 57, Ada Arachnida 58,\n Sim Enderman 59, Grunt 60, XXXXX 61, Crux 62, Heed 63, Sim Ender Dragon 64, Heavy Carrier 65,\n Devourer 66";
      useEvolution = cfg.getBoolean("Evolution Phases", "parasite_evolution_phases", useEvolution, "Set to false if you dont want to use evolution phases.");
      phaseCustomSpawner = cfg.getBoolean("Evolution Custom Spawner", "parasite_evolution_phases", phaseCustomSpawner, "Set to false if you want to use Vanilla Spawner (parasite mob cap will not work if set to false).");
      valueKill = cfg.getInt("Value for kills", "parasite_evolution_phases", valueKill, 0, 1000000, "Number of Points gained each kill.");
      valueCOTH = cfg.getInt("Value for COTH", "parasite_evolution_phases", valueCOTH, 0, 1000000, "Number of Points gained when a mob with COTH reaches amp +2.");
      valueBlock = cfg.getInt("Value for blocks", "parasite_evolution_phases", valueBlock, 0, 1000000, "Number of Points gained when converting a block.");
      valueMerge = cfg.getInt("Value for Merging", "parasite_evolution_phases", valueMerge, 0, 1000000, "Number of Points gained when using the merge system to spawn Primitive Parasites.");
      valueEvolutionDespawn = cfg.getInt("Value for Despawning", "parasite_evolution_phases", valueEvolutionDespawn, 0, 1000000, "Number of Points gained when Adapted versions spawned from moving flesh despawn, set this value to 0 in you dont want adapted to despawn.");
      evolutionNodeUnlock = (byte)cfg.getInt("Phase Node Unlock", "parasite_evolution_phases", evolutionNodeUnlock, 0, 9, "From this phase on, Nodes are unlocked.");
      evolutionColonyUnlock = (byte)cfg.getInt("Phase Colony Unlock", "parasite_evolution_phases", evolutionColonyUnlock, 0, 9, "From this phase on, Colonies are unlocked.");
      evolutionSleepDenied = (byte)cfg.getInt("Phase Sleep Point Multiplier", "parasite_evolution_phases", evolutionSleepDenied, 0, 9, "From this phase on, each time you sleep, the points obtained by sleeping will be multiplied by 5.");
      evolutionAssimilatedDehiding = (byte)cfg.getInt("Phase Assimilation Stop Hiding", "parasite_evolution_phases", evolutionAssimilatedDehiding, 0, 9, "From this phase on, Assimilated versions will stop hiding.");
      evolutionMudoAttack = (byte)cfg.getInt("Phase Rupter Stop Hiding", "parasite_evolution_phases", evolutionMudoAttack, 0, 9, "From this phase on, Rupters will stop running away from other mobs and they will directly covert.");
      evolutionBeckonIgnoreCooldown = (byte)cfg.getInt("Phase Beckon Summoning Cooldown", "parasite_evolution_phases", evolutionBeckonIgnoreCooldown, 0, 9, "From this phase on, Beckons will ignore their summoning cooldown when spawning.");
      evolutionSpawningIgnoreSunlight = (byte)cfg.getInt("Phase Parasites Ignore Sunlight", "parasite_evolution_phases", evolutionSpawningIgnoreSunlight, 0, 9, "From this phase on, Parasites will ignore sunlight when  spawning naturally.");
      evolutionDimStart = cfg.getStringList("Evolution Phases Dimension Starting Phase List", "parasite_evolution_phases", evolutionDimStart, "List of dimensions that will start at a specific Evolution phase. Ex. \"1;8;10\"" + exa);
      evolutionDimGain = cfg.get("parasite_evolution_phases", "Evolution Phases Point Gain Blacklist", evolutionDimGain, "Parasites cannot earn points in these dimensions").getIntList();
      evolutionDimGainInverted = cfg.getBoolean("Evolution Phases Point Gain Blacklist Inverted", "parasite_evolution_phases", evolutionDimGainInverted, "Set to true if you want to use the list as a WhiteList.");
      evolutionDimLoss = cfg.get("parasite_evolution_phases", "Evolution Phases Point Loss Blacklist", evolutionDimLoss, "Parasites cannot lose points in these dimensions").getIntList();
      evolutionDimLossInverted = cfg.getBoolean("Evolution Phases Point Loss Blacklist Inverted", "parasite_evolution_phases", evolutionDimLossInverted, "Set to true if you want to use the list as a WhiteList.");
      evolutionCothStopLoot = (byte)cfg.getInt("Phase Loot Denied", "parasite_evolution_phases", evolutionCothStopLoot, 0, 9, "From this phase on, mobs with COTH will not drop loot anymore.");
      evolutionStopFishing = (byte)cfg.getInt("Phase Fishing Denied", "parasite_evolution_phases", evolutionStopFishing, 0, 9, "From this phase on, fishing will not give you loot anymore.");
      evolutionParasiteLock = cfg.getStringList("Evolution Parasite Lock List", "parasite_evolution_phases", evolutionParasiteLock, "List of parasites that are unlocked when an evolution phase is reached. Ex. \"1;8;10\"" + exa22);
      defaultEvoPhase = (byte)cfg.getInt("Default Phase Start", "parasite_evolution_phases", defaultEvoPhase, -2, 8, "Default initial phase value for dimensions.");
      defaultEvoPoints = cfg.getInt("Default Points Start", "parasite_evolution_phases", defaultEvoPoints, -2147483640, 2147483640, "Default initial points for dimensions.");
      evolutionOneMind = (byte)cfg.getInt("Phase Collective Consciousness", "parasite_evolution_phases", evolutionOneMind, -1, 9, "From this phase on, One Mind will be active.");
      valueLossBlockStain = cfg.getInt("Value Loss Block Stain", "parasite_evolution_phases", valueLossBlockStain, 0, 1000000, "How many points parasites will lose when Infested Blocks type stain are mined.");
      valueLossBlockTrunk = cfg.getInt("Value Loss Block Trunk", "parasite_evolution_phases", valueLossBlockTrunk, 0, 1000000, "How many points parasites will lose when Infested Blocks type trunk are mined.");
      valueLossBlockRubble = cfg.getInt("Value Loss Block Rubble", "parasite_evolution_phases", valueLossBlockRubble, 0, 1000000, "How many points parasites will lose when Infested Blocks type rubble are mined.");
      blackListedDimensionsEPP = cfg.get("parasite_evolution_phases", "Points Overtime WhiteList Dimensions", blackListedDimensionsEPP, "Parasites can only gain points overtime in these dimensions").getIntList();
      evolutionTotalKill = (byte)cfg.getInt("Phase Total Slaughter", "parasite_evolution_phases", evolutionTotalKill, -1, 9, "From this phase on, parasites will kill everything (not ignoring config options).");
   }

   private static void initevolution0Config(Configuration cfg) {
      String entry = " Ex. \"minecraft:zombie;1;3;90;0\"  Where: \n \"minecraft:zombie\" is the entity, \n \"1\" is the minimum group count. \n \"3\" is the maximum group count. \n \"90\" is spawn weight. \n";
      phaseWarningZero = cfg.getString("Phase 0 Warning Message", "parasite_evolution_phases_0", phaseWarningZero, "Message sent to all players in the current world when parasites reach this Phase");
      sleepPenaltyZero = cfg.getInt("Phase 0 Sleep Penalty", "parasite_evolution_phases_0", sleepPenaltyZero, 0, 2147483640, "Number of Points gained when skipping the night.");
      phaseMaxParasiteIDZero = (byte)cfg.getInt("Phase 0 Maximum Parasite ID", "parasite_evolution_phases_0", phaseMaxParasiteIDZero, 0, 100, "If a parasite ID is equal to or greater than this number, the parasite will not spawn.");
      phaseCancelParasiteIDZero = (byte)cfg.getInt("Phase 0 Minimum Parasite ID", "parasite_evolution_phases_0", phaseCancelParasiteIDZero, 0, 100, "If a parasite ID is equal to or less than this number, the parasite will not spawn.");
      phaseScentBonusZero = cfg.getInt("Phase 0 Scent Death Bonus", "parasite_evolution_phases_0", phaseScentBonusZero, -100000, 1000, "Death bonus value used if parasite_collective_consciousness is enabled.");
      phaseScentReactionZero = (byte)cfg.getInt("Phase 0 Scent Reaction Bonus", "parasite_evolution_phases_0", phaseScentReactionZero, 0, 100, "Reaction bonus value used for a scent to go active.");
      phaseSpawnEntryZero = cfg.getStringList("Phase 0 Spawn Entity List", "parasite_evolution_phases_0", phaseSpawnEntryZero, "Entity List that will spawn at phase 0." + entry);
   }

   private static void initevolution1Config(Configuration cfg) {
      String entry = " Ex. \"minecraft:zombie;1;3;90;0\"  Where: \n \"minecraft:zombie\" is the entity, \n \"1\" is the minimum group count. \n \"3\" is the maximum group count. \n \"90\" is spawn weight. \n";
      phaseKillsOne = cfg.getInt("Phase 1 Points", "parasite_evolution_phases_1", phaseKillsOne, 0, 2147483640, "Number of Points required to reach Phase 1.");
      phaseKillCountPlusOne = (double)cfg.getFloat("Phase 1 Killcount Plus", "parasite_evolution_phases_1", (float)phaseKillCountPlusOne, 0.0F, 100.0F, "Each second the killcount will go up by this amount.");
      phaseMaxParasiteIDOne = (byte)cfg.getInt("Phase 1 Maximum Parasite ID", "parasite_evolution_phases_1", phaseMaxParasiteIDOne, 0, 100, "If a parasite ID is equal to or greater than this number, the parasite will not spawn.");
      phaseCancelParasiteIDOne = (byte)cfg.getInt("Phase 1 Minimum Parasite ID", "parasite_evolution_phases_1", phaseCancelParasiteIDOne, 0, 100, "If a parasite ID is equal to or less than this number, the parasite will not spawn.");
      reinforcementSystemChanceOne = (double)cfg.getFloat("Phase 1 Reinforcement System Chance", "parasite_evolution_phases_1", (float)reinforcementSystemChanceOne, 0.0F, 1.0F, "Chance to spawn a Beckon when a parasite is killed.");
      beckonStageIGrowPenaltyOne = (double)cfg.getFloat("Phase 1 Nexus Stage I Grow Stunned", "parasite_evolution_phases_1", (float)beckonStageIGrowPenaltyOne, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SI.");
      beckonStageIIGrowPenaltyOne = (double)cfg.getFloat("Phase 1 Nexus Stage II Grow Stunned", "parasite_evolution_phases_1", (float)beckonStageIIGrowPenaltyOne, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SII.");
      beckonStageIIIGrowPenaltyOne = (double)cfg.getFloat("Phase 1 Nexus Stage III Grow Stunned", "parasite_evolution_phases_1", (float)beckonStageIIIGrowPenaltyOne, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SIII.");
      mobSpawningCOTHChanceOne = (double)cfg.getFloat("Phase 1 Mob Spawn With COTH", "parasite_evolution_phases_1", (float)mobSpawningCOTHChanceOne, 0.0F, 1.0F, "Chance (1=100%) for an entity to spawn with COTH (amp 0).");
      cropGrowStunnedOne = (double)cfg.getFloat("Phase 1 Crop Grow Stunned", "parasite_evolution_phases_1", (float)cropGrowStunnedOne, 0.0F, 1.0F, "Chance (1=100%) for crop grow to be stunned.");
      luredValueOne = cfg.getInt("Phase 1 Lure Value", "parasite_evolution_phases_1", luredValueOne, 0, 2147483640, "Number of Points the parasites will lose when using a Lure Block.");
      phaseWarningOne = cfg.getString("Phase 1 Warning Message", "parasite_evolution_phases_1", phaseWarningOne, "Message sent to all players in the current world when parasites reach this Phase");
      sleepPenaltyOne = cfg.getInt("Phase 1 Sleep Penalty", "parasite_evolution_phases_1", sleepPenaltyOne, 0, 2147483640, "Number of Points gained when skipping the night.");
      phaseScentBonusOne = cfg.getInt("Phase 1 Scent Death Bonus", "parasite_evolution_phases_1", phaseScentBonusOne, 0, 1000, "Death bonus value used if parasite_collective_consciousness is enabled.");
      phaseScentReactionOne = (byte)cfg.getInt("Phase 1 Scent Reaction Bonus", "parasite_evolution_phases_1", phaseScentReactionOne, 0, 100, "Reaction bonus value used for a scent to go active.");
      phaseDelayTicksOne = cfg.getInt("Phase 1 Delay", "parasite_evolution_phases_1", phaseDelayTicksOne, 0, 2147483640, "Parasites will not be able to earn points until this time (seconds) has passed.");
      phaseResidueOne = cfg.getInt("Phase 1 Residue", "parasite_evolution_phases_1", phaseResidueOne, 0, 2147483640, "One in X to spawn a Beckon on the Infested Block Residue (random tick).");
      phaseSpawnEntryOne = cfg.getStringList("Phase 1 Spawn Entity List", "parasite_evolution_phases_1", phaseSpawnEntryOne, "Entity List that will spawn at phase 1." + entry);
   }

   private static void initevolution2Config(Configuration cfg) {
      String entry = " Ex. \"minecraft:zombie;1;3;90;0\"  Where: \n \"minecraft:zombie\" is the entity, \n \"1\" is the minimum group count. \n \"3\" is the maximum group count. \n \"90\" is spawn weight. \n";
      phaseKillsTwo = cfg.getInt("Phase 2 Points", "parasite_evolution_phases_2", phaseKillsTwo, 0, 2147483640, "Number of Points required to reach Phase 2.");
      phaseKillCountPlusTwo = (double)cfg.getFloat("Phase 2 Killcount Plus", "parasite_evolution_phases_2", (float)phaseKillCountPlusTwo, 0.0F, 100.0F, "Each second the killcount will go up by this amount.");
      phaseMaxParasiteIDTwo = (byte)cfg.getInt("Phase 2 Maximum Parasite ID", "parasite_evolution_phases_2", phaseMaxParasiteIDTwo, 0, 100, "If a parasite ID is equal to or greater than this number, the parasite will not spawn.");
      phaseCancelParasiteIDTwo = (byte)cfg.getInt("Phase 2 Minimum Parasite ID", "parasite_evolution_phases_2", phaseCancelParasiteIDTwo, 0, 100, "If a parasite ID is equal to or less than this number, the parasite will not spawn.");
      reinforcementSystemChanceTwo = (double)cfg.getFloat("Phase 2 Reinforcement System Chance", "parasite_evolution_phases_2", (float)reinforcementSystemChanceTwo, 0.0F, 1.0F, "Chance to spawn a Beckon when a parasite is killed.");
      beckonStageIGrowPenaltyTwo = (double)cfg.getFloat("Phase 2 Nexus Stage I Grow Stunned", "parasite_evolution_phases_2", (float)beckonStageIGrowPenaltyTwo, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SI.");
      beckonStageIIGrowPenaltyTwo = (double)cfg.getFloat("Phase 2 Nexus Stage II Grow Stunned", "parasite_evolution_phases_2", (float)beckonStageIIGrowPenaltyTwo, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SII.");
      beckonStageIIIGrowPenaltyTwo = (double)cfg.getFloat("Phase 2 Nexus Stage III Grow Stunned", "parasite_evolution_phases_2", (float)beckonStageIIIGrowPenaltyTwo, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SIII.");
      mobSpawningCOTHChanceTwo = (double)cfg.getFloat("Phase 2 Mob Spawn With COTH", "parasite_evolution_phases_2", (float)mobSpawningCOTHChanceTwo, 0.0F, 1.0F, "Chance (1=100%) for an entity to spawn with COTH (amp 0).");
      cropGrowStunnedTwo = (double)cfg.getFloat("Phase 2 Crop Grow Stunned", "parasite_evolution_phases_2", (float)cropGrowStunnedTwo, 0.0F, 1.0F, "Chance (1=100%) for crop grow to be stunned.");
      luredValueTwo = cfg.getInt("Phase 2 Lure Value", "parasite_evolution_phases_2", luredValueTwo, 0, 2147483640, "Number of Points the parasites will lose when using a Lure Block.");
      phaseWarningTwo = cfg.getString("Phase 2 Warning Message", "parasite_evolution_phases_2", phaseWarningTwo, "Message sent to all players in the current world when parasites reach this Phase");
      sleepPenaltyTwo = cfg.getInt("Phase 2 Sleep Penalty", "parasite_evolution_phases_2", sleepPenaltyTwo, 0, 2147483640, "Number of Points gained when skipping the night.");
      phaseScentBonusTwo = cfg.getInt("Phase 2 Scent Death Bonus", "parasite_evolution_phases_2", phaseScentBonusTwo, 0, 1000, "Death bonus value used if parasite_collective_consciousness is enabled.");
      phaseScentReactionTwo = (byte)cfg.getInt("Phase 2 Scent Reaction Bonus", "parasite_evolution_phases_2", phaseScentReactionTwo, 0, 100, "Reaction bonus value used for a scent to go active.");
      phaseDelayTicksTwo = cfg.getInt("Phase 2 Delay", "parasite_evolution_phases_2", phaseDelayTicksTwo, 0, 2147483640, "Parasites will not be able to earn points until this time (seconds) has passed.");
      phaseResidueTwo = cfg.getInt("Phase 2 Residue", "parasite_evolution_phases_2", phaseResidueTwo, 0, 2147483640, "One in X to spawn a Beckon on the Infested Block Residue (random tick).");
      phaseSpawnEntryTwo = cfg.getStringList("Phase 2 Spawn Entity List", "parasite_evolution_phases_2", phaseSpawnEntryTwo, "Entity List that will spawn at phase 2." + entry);
   }

   private static void initevolution3Config(Configuration cfg) {
      String entry = " Ex. \"minecraft:zombie;1;3;90;0\"  Where: \n \"minecraft:zombie\" is the entity, \n \"1\" is the minimum group count. \n \"3\" is the maximum group count. \n \"90\" is spawn weight. \n";
      phaseKillsThree = cfg.getInt("Phase 3 Points", "parasite_evolution_phases_3", phaseKillsThree, 0, 2147483640, "Number of Points required to reach Phase 3.");
      phaseKillCountPlusThree = (double)cfg.getFloat("Phase 3 Killcount Plus", "parasite_evolution_phases_3", (float)phaseKillCountPlusThree, 0.0F, 100.0F, "Each second the killcount will go up by this amount.");
      phaseMaxParasiteIDThree = (byte)cfg.getInt("Phase 3 Maximum Parasite ID", "parasite_evolution_phases_3", phaseMaxParasiteIDThree, 0, 100, "If a parasite ID is equal to or greater than this number, the parasite will not spawn.");
      phaseCancelParasiteIDThree = (byte)cfg.getInt("Phase 3 Minimum Parasite ID", "parasite_evolution_phases_3", phaseCancelParasiteIDThree, 0, 100, "If a parasite ID is equal to or less than this number, the parasite will not spawn.");
      reinforcementSystemChanceThree = (double)cfg.getFloat("Phase 3 Reinforcement System Chance", "parasite_evolution_phases_3", (float)reinforcementSystemChanceThree, 0.0F, 1.0F, "Chance to spawn a Beckon when a parasite is killed.");
      beckonStageIGrowPenaltyThree = (double)cfg.getFloat("Phase 3 Nexus Stage I Grow Stunned", "parasite_evolution_phases_3", (float)beckonStageIGrowPenaltyThree, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SI.");
      beckonStageIIGrowPenaltyThree = (double)cfg.getFloat("Phase 3 Nexus Stage II Grow Stunned", "parasite_evolution_phases_3", (float)beckonStageIIGrowPenaltyThree, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SII.");
      beckonStageIIIGrowPenaltyThree = (double)cfg.getFloat("Phase 3 Nexus Stage III Grow Stunned", "parasite_evolution_phases_3", (float)beckonStageIIIGrowPenaltyThree, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SIII.");
      mobSpawningCOTHChanceThree = (double)cfg.getFloat("Phase 3 Mob Spawn With COTH", "parasite_evolution_phases_3", (float)mobSpawningCOTHChanceThree, 0.0F, 1.0F, "Chance (1=100%) for an entity to spawn with COTH (amp 0).");
      cropGrowStunnedThree = (double)cfg.getFloat("Phase 3 Crop Grow Stunned", "parasite_evolution_phases_3", (float)cropGrowStunnedThree, 0.0F, 1.0F, "Chance (1=100%) for crop grow to be stunned.");
      luredValueThree = cfg.getInt("Phase 3 Lure Value", "parasite_evolution_phases_3", luredValueThree, 0, 2147483640, "Number of Points the parasites will lose when using a Lure Block.");
      phaseWarningThree = cfg.getString("Phase 3 Warning Message", "parasite_evolution_phases_3", phaseWarningThree, "Message sent to all players in the current world when parasites reach this Phase");
      sleepPenaltyThree = cfg.getInt("Phase 3 Sleep Penalty", "parasite_evolution_phases_3", sleepPenaltyThree, 0, 2147483640, "Number of Points gained when skipping the night.");
      phaseScentBonusThree = cfg.getInt("Phase 3 Scent Death Bonus", "parasite_evolution_phases_3", phaseScentBonusThree, 0, 1000, "Death bonus value used if parasite_collective_consciousness is enabled.");
      phaseScentReactionThree = (byte)cfg.getInt("Phase 3 Scent Reaction Bonus", "parasite_evolution_phases_3", phaseScentReactionThree, 0, 100, "Reaction bonus value used for a scent to go active.");
      phaseDelayTicksThree = cfg.getInt("Phase 3 Delay", "parasite_evolution_phases_3", phaseDelayTicksThree, 0, 2147483640, "Parasites will not be able to earn points until this time (seconds) has passed.");
      phaseResidueThree = cfg.getInt("Phase 3 Residue", "parasite_evolution_phases_3", phaseResidueThree, 0, 2147483640, "One in X to spawn a Beckon on the Infested Block Residue (random tick).");
      phaseSpawnEntryThree = cfg.getStringList("Phase 3 Spawn Entity List", "parasite_evolution_phases_3", phaseSpawnEntryThree, "Entity List that will spawn at phase 3." + entry);
   }

   private static void initevolution4Config(Configuration cfg) {
      String entry = " Ex. \"minecraft:zombie;1;3;90;0\"  Where: \n \"minecraft:zombie\" is the entity, \n \"1\" is the minimum group count. \n \"3\" is the maximum group count. \n \"90\" is spawn weight. \n";
      phaseKillsFour = cfg.getInt("Phase 4 Points", "parasite_evolution_phases_4", phaseKillsFour, 0, 2147483640, "Number of Points required to reach Phase 4.");
      phaseKillCountPlusFour = (double)cfg.getFloat("Phase 4 Killcount Plus", "parasite_evolution_phases_4", (float)phaseKillCountPlusFour, 0.0F, 100.0F, "Each second the killcount will go up by this amount.");
      phaseMaxParasiteIDFour = (byte)cfg.getInt("Phase 4 Maximum Parasite ID", "parasite_evolution_phases_4", phaseMaxParasiteIDFour, 0, 100, "If a parasite ID is equal to or greater than this number, the parasite will not spawn.");
      phaseCancelParasiteIDFour = (byte)cfg.getInt("Phase 4 Minimum Parasite ID", "parasite_evolution_phases_4", phaseCancelParasiteIDFour, 0, 100, "If a parasite ID is equal to or less than this number, the parasite will not spawn.");
      reinforcementSystemChanceFour = (double)cfg.getFloat("Phase 4 Reinforcement System Chance", "parasite_evolution_phases_4", (float)reinforcementSystemChanceFour, 0.0F, 1.0F, "Chance to spawn a Beckon when a parasite is killed.");
      beckonStageIGrowPenaltyFour = (double)cfg.getFloat("Phase 4 Nexus Stage I Grow Stunned", "parasite_evolution_phases_4", (float)beckonStageIGrowPenaltyFour, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SI.");
      beckonStageIIGrowPenaltyFour = (double)cfg.getFloat("Phase 4 Nexus Stage II Grow Stunned", "parasite_evolution_phases_4", (float)beckonStageIIGrowPenaltyFour, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SII.");
      beckonStageIIIGrowPenaltyFour = (double)cfg.getFloat("Phase 4 Nexus Stage III Grow Stunned", "parasite_evolution_phases_4", (float)beckonStageIIIGrowPenaltyFour, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SIII.");
      mobSpawningCOTHChanceFour = (double)cfg.getFloat("Phase 4 Mob Spawn With COTH", "parasite_evolution_phases_4", (float)mobSpawningCOTHChanceFour, 0.0F, 1.0F, "Chance (1=100%) for an entity to spawn with COTH (amp 0).");
      cropGrowStunnedFour = (double)cfg.getFloat("Phase 4 Crop Grow Stunned", "parasite_evolution_phases_4", (float)cropGrowStunnedFour, 0.0F, 1.0F, "Chance (1=100%) for crop grow to be stunned.");
      luredValueFour = cfg.getInt("Phase 4 Lure Value", "parasite_evolution_phases_4", luredValueFour, 0, 2147483640, "Number of Points the parasites will lose when using a Lure Block.");
      phaseWarningFour = cfg.getString("Phase 4 Warning Message", "parasite_evolution_phases_4", phaseWarningFour, "Message sent to all players in the current world when parasites reach this Phase");
      sleepPenaltyFour = cfg.getInt("Phase 4 Sleep Penalty", "parasite_evolution_phases_4", sleepPenaltyFour, 0, 2147483640, "Number of Points gained when skipping the night.");
      phaseScentBonusFour = cfg.getInt("Phase 4 Scent Death Bonus", "parasite_evolution_phases_4", phaseScentBonusFour, 0, 1000, "Death bonus value used if parasite_collective_consciousness is enabled.");
      phaseScentReactionFour = (byte)cfg.getInt("Phase 4 Scent Reaction Bonus", "parasite_evolution_phases_4", phaseScentReactionFour, 0, 100, "Reaction bonus value used for a scent to go active.");
      phaseDelayTicksFour = cfg.getInt("Phase 4 Delay", "parasite_evolution_phases_4", phaseDelayTicksFour, 0, 2147483640, "Parasites will not be able to earn points until this time (seconds) has passed.");
      phaseResidueFour = cfg.getInt("Phase 4 Residue", "parasite_evolution_phases_4", phaseResidueFour, 0, 2147483640, "One in X to spawn a Beckon on the Infested Block Residue (random tick).");
      phaseSpawnEntryFour = cfg.getStringList("Phase 4 Spawn Entity List", "parasite_evolution_phases_4", phaseSpawnEntryFour, "Entity List that will spawn at phase 4." + entry);
   }

   private static void initevolution5Config(Configuration cfg) {
      String entry = " Ex. \"minecraft:zombie;1;3;90;0\"  Where: \n \"minecraft:zombie\" is the entity, \n \"1\" is the minimum group count. \n \"3\" is the maximum group count. \n \"90\" is spawn weight. \n";
      phaseKillsFive = cfg.getInt("Phase 5 Points", "parasite_evolution_phases_5", phaseKillsFive, 0, 2147483640, "Number of Points required to reach Phase 5.");
      phaseKillCountPlusFive = (double)cfg.getFloat("Phase 5 Killcount Plus", "parasite_evolution_phases_5", (float)phaseKillCountPlusFive, 0.0F, 100.0F, "Each second the killcount will go up by this amount.");
      phaseMaxParasiteIDFive = (byte)cfg.getInt("Phase 5 Maximum Parasite ID", "parasite_evolution_phases_5", phaseMaxParasiteIDFive, 0, 100, "If a parasite ID is equal to or greater than this number, the parasite will not spawn.");
      phaseCancelParasiteIDFive = (byte)cfg.getInt("Phase 5 Minimum Parasite ID", "parasite_evolution_phases_5", phaseCancelParasiteIDFive, 0, 100, "If a parasite ID is equal to or less than this number, the parasite will not spawn.");
      reinforcementSystemChanceFive = (double)cfg.getFloat("Phase 5 Reinforcement System Chance", "parasite_evolution_phases_5", (float)reinforcementSystemChanceFive, 0.0F, 1.0F, "Chance to spawn a Beckon when a parasite is killed.");
      beckonStageIGrowPenaltyFive = (double)cfg.getFloat("Phase 5 Nexus Stage I Grow Stunned", "parasite_evolution_phases_5", (float)beckonStageIGrowPenaltyFive, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SI.");
      beckonStageIIGrowPenaltyFive = (double)cfg.getFloat("Phase 5 Nexus Stage II Grow Stunned", "parasite_evolution_phases_5", (float)beckonStageIIGrowPenaltyFive, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SII.");
      beckonStageIIIGrowPenaltyFive = (double)cfg.getFloat("Phase 5 Nexus Stage III Grow Stunned", "parasite_evolution_phases_5", (float)beckonStageIIIGrowPenaltyFive, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SIII.");
      mobSpawningCOTHChanceFive = (double)cfg.getFloat("Phase 5 Mob Spawn With COTH", "parasite_evolution_phases_5", (float)mobSpawningCOTHChanceFive, 0.0F, 1.0F, "Chance (1=100%) for an entity to spawn with COTH (amp 0).");
      cropGrowStunnedFive = (double)cfg.getFloat("Phase 5 Crop Grow Stunned", "parasite_evolution_phases_5", (float)cropGrowStunnedFive, 0.0F, 1.0F, "Chance (1=100%) for crop grow to be stunned.");
      luredValueFive = cfg.getInt("Phase 5 Lure Value", "parasite_evolution_phases_5", luredValueFive, 0, 2147483640, "Number of Points the parasites will lose when using a Lure Block.");
      phaseWarningFive = cfg.getString("Phase 5 Warning Message", "parasite_evolution_phases_5", phaseWarningFive, "Message sent to all players in the current world when parasites reach this Phase");
      sleepPenaltyFive = cfg.getInt("Phase 5 Sleep Penalty", "parasite_evolution_phases_5", sleepPenaltyFive, 0, 2147483640, "Number of Points gained when skipping the night.");
      phaseScentBonusFive = cfg.getInt("Phase 5 Scent Death Bonus", "parasite_evolution_phases_5", phaseScentBonusFive, 0, 1000, "Death bonus value used if parasite_collective_consciousness is enabled.");
      phaseScentReactionFive = (byte)cfg.getInt("Phase 5 Scent Reaction Bonus", "parasite_evolution_phases_5", phaseScentReactionFive, 0, 100, "Reaction bonus value used for a scent to go active.");
      phaseDelayTicksFive = cfg.getInt("Phase 5 Delay", "parasite_evolution_phases_5", phaseDelayTicksFive, 0, 2147483640, "Parasites will not be able to earn points until this time (seconds) has passed.");
      phaseResidueFive = cfg.getInt("Phase 5 Residue", "parasite_evolution_phases_5", phaseResidueFive, 0, 2147483640, "One in X to spawn a Beckon on the Infested Block Residue (random tick).");
      phaseSpawnEntryFive = cfg.getStringList("Phase 5 Spawn Entity List", "parasite_evolution_phases_5", phaseSpawnEntryFive, "Entity List that will spawn at phase 5." + entry);
   }

   private static void initevolution6Config(Configuration cfg) {
      String entry = " Ex. \"minecraft:zombie;1;3;90;0\"  Where: \n \"minecraft:zombie\" is the entity, \n \"1\" is the minimum group count. \n \"3\" is the maximum group count. \n \"90\" is spawn weight. \n";
      phaseKillsSix = cfg.getInt("Phase 6 Points", "parasite_evolution_phases_6", phaseKillsSix, 0, 2147483640, "Number of Points required to reach Phase 6.");
      phaseKillCountPlusSix = (double)cfg.getFloat("Phase 6 Killcount Plus", "parasite_evolution_phases_6", (float)phaseKillCountPlusSix, 0.0F, 100.0F, "Each second the killcount will go up by this amount.");
      phaseMaxParasiteIDSix = (byte)cfg.getInt("Phase 6 Maximum Parasite ID", "parasite_evolution_phases_6", phaseMaxParasiteIDSix, 0, 100, "If a parasite ID is equal to or greater than this number, the parasite will not spawn.");
      phaseCancelParasiteIDSix = (byte)cfg.getInt("Phase 6 Minimum Parasite ID", "parasite_evolution_phases_6", phaseCancelParasiteIDSix, 0, 100, "If a parasite ID is equal to or less than this number, the parasite will not spawn.");
      reinforcementSystemChanceSix = (double)cfg.getFloat("Phase 6 Reinforcement System Chance", "parasite_evolution_phases_6", (float)reinforcementSystemChanceSix, 0.0F, 1.0F, "Chance to spawn a Beckon when a parasite is killed.");
      beckonStageIGrowPenaltySix = (double)cfg.getFloat("Phase 6 Nexus Stage I Grow Stunned", "parasite_evolution_phases_6", (float)beckonStageIGrowPenaltySix, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SI.");
      beckonStageIIGrowPenaltySix = (double)cfg.getFloat("Phase 6 Nexus Stage II Grow Stunned", "parasite_evolution_phases_6", (float)beckonStageIIGrowPenaltySix, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SII.");
      beckonStageIIIGrowPenaltySix = (double)cfg.getFloat("Phase 6 Nexus Stage III Grow Stunned", "parasite_evolution_phases_6", (float)beckonStageIIIGrowPenaltySix, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SIII.");
      mobSpawningCOTHChanceSix = (double)cfg.getFloat("Phase 6 Mob Spawn With COTH", "parasite_evolution_phases_6", (float)mobSpawningCOTHChanceSix, 0.0F, 1.0F, "Chance (1=100%) for an entity to spawn with COTH (amp 0).");
      cropGrowStunnedSix = (double)cfg.getFloat("Phase 6 Crop Grow Stunned", "parasite_evolution_phases_6", (float)cropGrowStunnedSix, 0.0F, 1.0F, "Chance (1=100%) for crop grow to be stunned.");
      luredValueSix = cfg.getInt("Phase 6 Lure Value", "parasite_evolution_phases_6", luredValueSix, 0, 2147483640, "Number of Points the parasites will lose when using a Lure Block.");
      phaseWarningSix = cfg.getString("Phase 6 Warning Message", "parasite_evolution_phases_6", phaseWarningSix, "Message sent to all players in the current world when parasites reach this Phase");
      sleepPenaltySix = cfg.getInt("Phase 6 Sleep Penalty", "parasite_evolution_phases_6", sleepPenaltySix, 0, 2147483640, "Number of Points gained when skipping the night.");
      phaseScentBonusSix = cfg.getInt("Phase 6 Scent Death Bonus", "parasite_evolution_phases_6", phaseScentBonusSix, 0, 1000, "Death bonus value used if parasite_collective_consciousness is enabled.");
      phaseScentReactionSix = (byte)cfg.getInt("Phase 6 Scent Reaction Bonus", "parasite_evolution_phases_6", phaseScentReactionSix, 0, 100, "Reaction bonus value used for a scent to go active.");
      phaseDelayTicksSix = cfg.getInt("Phase 6 Delay", "parasite_evolution_phases_6", phaseDelayTicksSix, 0, 2147483640, "Parasites will not be able to earn points until this time (seconds) has passed.");
      phaseResidueSix = cfg.getInt("Phase 6 Residue", "parasite_evolution_phases_6", phaseResidueSix, 0, 2147483640, "One in X to spawn a Beckon on the Infested Block Residue (random tick).");
      phaseSpawnEntrySix = cfg.getStringList("Phase 6 Spawn Entity List", "parasite_evolution_phases_6", phaseSpawnEntrySix, "Entity List that will spawn at phase 6." + entry);
   }

   private static void initevolution7Config(Configuration cfg) {
      String entry = " Ex. \"minecraft:zombie;1;3;90;0\"  Where: \n \"minecraft:zombie\" is the entity, \n \"1\" is the minimum group count. \n \"3\" is the maximum group count. \n \"90\" is spawn weight. \n";
      phaseKillsSeven = cfg.getInt("Phase 7 Points", "parasite_evolution_phases_7", phaseKillsSeven, 0, 2147483640, "Number of Points required to reach Phase 7.");
      phaseKillCountPlusSeven = (double)cfg.getFloat("Phase 7 Killcount Plus", "parasite_evolution_phases_7", (float)phaseKillCountPlusSeven, 0.0F, 100.0F, "Each second the killcount will go up by this amount.");
      phaseMaxParasiteIDSeven = (byte)cfg.getInt("Phase 7 Maximum Parasite ID", "parasite_evolution_phases_7", phaseMaxParasiteIDSeven, 0, 100, "If a parasite ID is equal to or greater than this number, the parasite will not spawn.");
      phaseCancelParasiteIDSeven = (byte)cfg.getInt("Phase 7 Minimum Parasite ID", "parasite_evolution_phases_7", phaseCancelParasiteIDSeven, 0, 100, "If a parasite ID is equal to or less than this number, the parasite will not spawn.");
      reinforcementSystemChanceSeven = (double)cfg.getFloat("Phase 7 Reinforcement System Chance", "parasite_evolution_phases_7", (float)reinforcementSystemChanceSeven, 0.0F, 1.0F, "Chance to spawn a Beckon when a parasite is killed.");
      beckonStageIGrowPenaltySeven = (double)cfg.getFloat("Phase 7 Nexus Stage I Grow Stunned", "parasite_evolution_phases_7", (float)beckonStageIGrowPenaltySeven, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SI.");
      beckonStageIIGrowPenaltySeven = (double)cfg.getFloat("Phase 7 Nexus Stage II Grow Stunned", "parasite_evolution_phases_7", (float)beckonStageIIGrowPenaltySeven, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SII.");
      beckonStageIIIGrowPenaltySeven = (double)cfg.getFloat("Phase 7 Nexus Stage III Grow Stunned", "parasite_evolution_phases_7", (float)beckonStageIIIGrowPenaltySeven, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SIII.");
      mobSpawningCOTHChanceSeven = (double)cfg.getFloat("Phase 7 Mob Spawn With COTH", "parasite_evolution_phases_7", (float)mobSpawningCOTHChanceSeven, 0.0F, 1.0F, "Chance (1=100%) for an entity to spawn with COTH (amp 0).");
      cropGrowStunnedSeven = (double)cfg.getFloat("Phase 7 Crop Grow Stunned", "parasite_evolution_phases_7", (float)cropGrowStunnedSeven, 0.0F, 1.0F, "Chance (1=100%) for crop grow to be stunned.");
      luredValueSeven = cfg.getInt("Phase 7 Lure Value", "parasite_evolution_phases_7", luredValueSeven, 0, 2147483640, "Number of Points the parasites will lose when using a Lure Block.");
      phaseWarningSeven = cfg.getString("Phase 7 Warning Message", "parasite_evolution_phases_7", phaseWarningSeven, "Message sent to all players in the current world when parasites reach this Phase");
      sleepPenaltySeven = cfg.getInt("Phase 7 Sleep Penalty", "parasite_evolution_phases_7", sleepPenaltySeven, 0, 2147483640, "Number of Points gained when skipping the night.");
      phaseScentBonusSeven = cfg.getInt("Phase 7 Scent Death Bonus", "parasite_evolution_phases_7", phaseScentBonusSeven, 0, 1000, "Death bonus value used if parasite_collective_consciousness is enabled.");
      phaseScentReactionSeven = (byte)cfg.getInt("Phase 7 Scent Reaction Bonus", "parasite_evolution_phases_7", phaseScentReactionSeven, 0, 100, "Reaction bonus value used for a scent to go active.");
      phaseDelayTicksSeven = cfg.getInt("Phase 7 Delay", "parasite_evolution_phases_7", phaseDelayTicksSeven, 0, 2147483640, "Parasites will not be able to earn points until this time (seconds) has passed.");
      phaseResidueSeven = cfg.getInt("Phase 7 Residue", "parasite_evolution_phases_7", phaseResidueSeven, 0, 2147483640, "One in X to spawn a Beckon on the Infested Block Residue (random tick).");
      phaseSpawnEntrySeven = cfg.getStringList("Phase 7 Spawn Entity List", "parasite_evolution_phases_7", phaseSpawnEntrySeven, "Entity List that will spawn at phase 7." + entry);
   }

   private static void initevolution8Config(Configuration cfg) {
      String entry = " Ex. \"minecraft:zombie;1;3;90;0\"  Where: \n \"minecraft:zombie\" is the entity, \n \"1\" is the minimum group count. \n \"3\" is the maximum group count. \n \"90\" is spawn weight. \n";
      phaseKillsEight = cfg.getInt("Phase 8 Points", "parasite_evolution_phases_8", phaseKillsEight, 0, 2147483640, "Number of Points required to reach Phase 8.");
      phaseKillCountPlusEight = (double)cfg.getFloat("Phase 8 Killcount Plus", "parasite_evolution_phases_8", (float)phaseKillCountPlusEight, 0.0F, 100.0F, "Each second the killcount will go up by this amount.");
      phaseMaxParasiteIDEight = (byte)cfg.getInt("Phase 8 Maximum Parasite ID", "parasite_evolution_phases_8", phaseMaxParasiteIDEight, 0, 100, "If a parasite ID is equal to or greater than this number, the parasite will not spawn.");
      phaseCancelParasiteIDEight = (byte)cfg.getInt("Phase 8 Minimum Parasite ID", "parasite_evolution_phases_8", phaseCancelParasiteIDEight, 0, 100, "If a parasite ID is equal to or less than this number, the parasite will not spawn.");
      reinforcementSystemChanceEight = (double)cfg.getFloat("Phase 8 Reinforcement System Chance", "parasite_evolution_phases_8", (float)reinforcementSystemChanceEight, 0.0F, 1.0F, "Chance to spawn a Beckon when a parasite is killed.");
      beckonStageIGrowPenaltyEight = (double)cfg.getFloat("Phase 8 Nexus Stage I Grow Stunned", "parasite_evolution_phases_8", (float)beckonStageIGrowPenaltyEight, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SI.");
      beckonStageIIGrowPenaltyEight = (double)cfg.getFloat("Phase 8 Nexus Stage II Grow Stunned", "parasite_evolution_phases_8", (float)beckonStageIIGrowPenaltyEight, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SII.");
      beckonStageIIIGrowPenaltyEight = (double)cfg.getFloat("Phase 8 Nexus Stage III Grow Stunned", "parasite_evolution_phases_8", (float)beckonStageIIIGrowPenaltyEight, 0.0F, 1.0F, "Chance (1=100%) to failed to grow for Nexus SIII.");
      mobSpawningCOTHChanceEight = (double)cfg.getFloat("Phase 8 Mob Spawn With COTH", "parasite_evolution_phases_8", (float)mobSpawningCOTHChanceEight, 0.0F, 1.0F, "Chance (1=100%) for an entity to spawn with COTH (amp 0).");
      cropGrowStunnedEight = (double)cfg.getFloat("Phase 8 Crop Grow Stunned", "parasite_evolution_phases_8", (float)cropGrowStunnedEight, 0.0F, 1.0F, "Chance (1=100%) for crop grow to be stunned.");
      luredValueEight = cfg.getInt("Phase 8 Lure Value", "parasite_evolution_phases_8", luredValueEight, 0, 2147483640, "Number of Points the parasites will lose when using a Lure Block.");
      phaseWarningEight = cfg.getString("Phase 8 Warning Message", "parasite_evolution_phases_8", phaseWarningEight, "Message sent to all players in the current world when parasites reach this Phase");
      sleepPenaltyEight = cfg.getInt("Phase 8 Sleep Penalty", "parasite_evolution_phases_8", sleepPenaltyEight, 0, 2147483640, "Number of Points gained when skipping the night.");
      phaseScentBonusEight = cfg.getInt("Phase 8 Scent Death Bonus", "parasite_evolution_phases_8", phaseScentBonusEight, 0, 1000, "Death bonus value used if parasite_collective_consciousness is enabled.");
      phaseScentReactionEight = (byte)cfg.getInt("Phase 8 Scent Reaction Bonus", "parasite_evolution_phases_8", phaseScentReactionEight, 0, 100, "Reaction bonus value used for a scent to go active.");
      phaseDelayTicksEight = cfg.getInt("Phase 8 Delay", "parasite_evolution_phases_8", phaseDelayTicksEight, 0, 2147483640, "Parasites will not be able to earn points until this time (seconds) has passed.");
      phaseResidueEight = cfg.getInt("Phase 8 Residue", "parasite_evolution_phases_8", phaseResidueEight, 0, 2147483640, "One in X to spawn a Beckon on the Infested Block Residue (random tick).");
      phaseSpawnEntryEight = cfg.getStringList("Phase 8 Spawn Entity List", "parasite_evolution_phases_8", phaseSpawnEntryEight, "Entity List that will spawn at phase 8." + entry);
      phaseEightTotalPoints = cfg.getInt("Phase 8 Total Points", "parasite_evolution_phases_8", phaseEightTotalPoints, 0, 2147483640, "Parasites can't earn more points than this.");
   }

   private static void inithivemindConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("parasite_collective_consciousness", "Uwu");
      useOneMind = cfg.getBoolean("Collective Consciousness", "parasite_collective_consciousness", useOneMind, "Set to false if you want to disable CC");
      oneMinRangeCap = cfg.getInt("Follow Range Cap", "parasite_collective_consciousness", oneMinRangeCap, 0, 100, ".");
      oneMindPlayer = cfg.getBoolean("CC Player Only", "parasite_collective_consciousness", oneMindPlayer, "Set to true for CC to only work for players.");
      oneMindDebug = cfg.getBoolean("Scent Debug", "parasite_collective_consciousness", oneMindDebug, "Set to true if you want to see Scent entities");
      scentGoActive = (byte)cfg.getInt("Scent Death Point", "parasite_collective_consciousness", scentGoActive, 0, 10000000, "(This list is ignored if Evolution Phases are enabled, it has its own option).");
      scentSpawnWaves = cfg.getInt("Scent Wave Point", "parasite_collective_consciousness", scentSpawnWaves, 0, 10000000, ".");
      scentMiniDis = cfg.getInt("Scent Wave Minimum Distance", "parasite_collective_consciousness", scentMiniDis, 0, 10000000, ".");
      scentMaxDis = cfg.getInt("Scent Wave Maximum Distance", "parasite_collective_consciousness", scentMaxDis, 0, 10000000, ".");
      scentLifeDeath = cfg.getInt("Scent Added Lifespan With Death", "parasite_collective_consciousness", scentLifeDeath, 0, 10000000, "seconds.");
      scentLifeObserver = cfg.getInt("Scent Observer Mode LifeSpan", "parasite_collective_consciousness", scentLifeObserver, 0, 10000000, "seconds.");
      scentLifeTactical = cfg.getInt("Scent Tactical Mode Lifespan", "parasite_collective_consciousness", scentLifeTactical, 0, 10000000, "seconds.");
      scentMobLimit = cfg.getInt("Scent Mob Cap", "parasite_collective_consciousness", scentMobLimit, 0, 100, ".");
   }

   private static void inithivemindscentlvl0Config(Configuration cfg) {
      scentLevelZero = cfg.getStringList("Scent Level 0 Mob Table", "parasite_collective_consciousness_scent_lvl_0", scentLevelZero, "Mobs the Scent will spawn at level 0");
      scentWaveMinimumZero = cfg.getInt("Scent Level 0 Minimum Wave Number", "parasite_collective_consciousness_scent_lvl_0", scentWaveMinimumZero, 0, 100, ".");
      scentWaveMaximumZero = cfg.getInt("Scent Level 0 Maximum Wave Number", "parasite_collective_consciousness_scent_lvl_0", scentWaveMaximumZero, 0, 100, ".");
      scentWaveMinMobWaveZero = cfg.getInt("Scent Level 0 Minimum Mob Wave Number", "parasite_collective_consciousness_scent_lvl_0", scentWaveMinMobWaveZero, 0, 100, ".");
      scentWaveMaxMobWaveZero = cfg.getInt("Scent Level 0 Maximum Mob Wave Number", "parasite_collective_consciousness_scent_lvl_0", scentWaveMaxMobWaveZero, 0, 100, ".");
   }

   private static void inithivemindscentlvl1Config(Configuration cfg) {
      scentLevelOne = cfg.getStringList("Scent Level 1 Mob Table", "parasite_collective_consciousness_scent_lvl_1", scentLevelOne, "Mobs the Scent will spawn at level 0");
      scentWaveMinimumOne = cfg.getInt("Scent Level 1 Minimum Wave Number", "parasite_collective_consciousness_scent_lvl_1", scentWaveMinimumOne, 0, 100, ".");
      scentWaveMaximumOne = cfg.getInt("Scent Level 1 Maximum Wave Number", "parasite_collective_consciousness_scent_lvl_1", scentWaveMaximumOne, 0, 100, ".");
      scentWaveMinMobWaveOne = cfg.getInt("Scent Level 1 Minimum Mob Wave Number", "parasite_collective_consciousness_scent_lvl_1", scentWaveMinMobWaveOne, 0, 100, ".");
      scentWaveMaxMobWaveOne = cfg.getInt("Scent Level 1 Maximum Mob Wave Number", "parasite_collective_consciousness_scent_lvl_1", scentWaveMaxMobWaveOne, 0, 100, ".");
      scentLevelPointsOne = cfg.getInt("Scent Level 1 Points Required", "parasite_collective_consciousness_scent_lvl_1", scentLevelPointsOne, 0, 1000000, ".");
   }

   private static void inithivemindscentlvl2Config(Configuration cfg) {
      scentLevelTwo = cfg.getStringList("Scent Level 2 Mob Table", "parasite_collective_consciousness_scent_lvl_2", scentLevelTwo, "Mobs the Scent will spawn at level 0");
      scentWaveMinimumTwo = cfg.getInt("Scent Level 2 Minimum Wave Number", "parasite_collective_consciousness_scent_lvl_2", scentWaveMinimumTwo, 0, 100, ".");
      scentWaveMaximumTwo = cfg.getInt("Scent Level 2 Maximum Wave Number", "parasite_collective_consciousness_scent_lvl_2", scentWaveMaximumTwo, 0, 100, ".");
      scentWaveMinMobWaveTwo = cfg.getInt("Scent Level 2 Minimum Mob Wave Number", "parasite_collective_consciousness_scent_lvl_2", scentWaveMinMobWaveTwo, 0, 100, ".");
      scentWaveMaxMobWaveTwo = cfg.getInt("Scent Level 2 Maximum Mob Wave Number", "parasite_collective_consciousness_scent_lvl_2", scentWaveMaxMobWaveTwo, 0, 100, ".");
      scentLevelPointsTwo = cfg.getInt("Scent Level 2 Points Required", "parasite_collective_consciousness_scent_lvl_2", scentLevelPointsTwo, 0, 1000000, ".");
   }

   private static void inithivemindscentlvl3Config(Configuration cfg) {
      scentLevelThree = cfg.getStringList("Scent Level 3 Mob Table", "parasite_collective_consciousness_scent_lvl_3", scentLevelThree, "Mobs the Scent will spawn at level 0");
      scentWaveMinimumThree = cfg.getInt("Scent Level 3 Minimum Wave Number", "parasite_collective_consciousness_scent_lvl_3", scentWaveMinimumThree, 0, 100, ".");
      scentWaveMaximumThree = cfg.getInt("Scent Level 3 Maximum Wave Number", "parasite_collective_consciousness_scent_lvl_3", scentWaveMaximumThree, 0, 100, ".");
      scentWaveMinMobWaveThree = cfg.getInt("Scent Level 3 Minimum Mob Wave Number", "parasite_collective_consciousness_scent_lvl_3", scentWaveMinMobWaveThree, 0, 100, ".");
      scentWaveMaxMobWaveThree = cfg.getInt("Scent Level 3 Maximum Mob Wave Number", "parasite_collective_consciousness_scent_lvl_3", scentWaveMaxMobWaveThree, 0, 100, ".");
      scentLevelPointsThree = cfg.getInt("Scent Level 3 Points Required", "parasite_collective_consciousness_scent_lvl_3", scentLevelPointsThree, 0, 1000000, ".");
   }

   private static void inithivemindscentlvl4Config(Configuration cfg) {
      scentLevelFour = cfg.getStringList("Scent Level 4 Mob Table", "parasite_collective_consciousness_scent_lvl_4", scentLevelFour, "Mobs the Scent will spawn at level 0");
      scentWaveMinimumFour = cfg.getInt("Scent Level 4 Minimum Wave Number", "parasite_collective_consciousness_scent_lvl_4", scentWaveMinimumFour, 0, 100, ".");
      scentWaveMaximumFour = cfg.getInt("Scent Level 4 Maximum Wave Number", "parasite_collective_consciousness_scent_lvl_4", scentWaveMaximumFour, 0, 100, ".");
      scentWaveMinMobWaveFour = cfg.getInt("Scent Level 4 Minimum Mob Wave Number", "parasite_collective_consciousness_scent_lvl_4", scentWaveMinMobWaveFour, 0, 100, ".");
      scentWaveMaxMobWaveFour = cfg.getInt("Scent Level 4 Maximum Mob Wave Number", "parasite_collective_consciousness_scent_lvl_4", scentWaveMaxMobWaveFour, 0, 100, ".");
      scentLevelPointsFour = cfg.getInt("Scent Level 4 Points Required", "parasite_collective_consciousness_scent_lvl_4", scentLevelPointsFour, 0, 1000000, ".");
   }

   private static void inithivemindscentlvl5Config(Configuration cfg) {
      scentLevelFive = cfg.getStringList("Scent Level 5 Mob Table", "parasite_collective_consciousness_scent_lvl_5", scentLevelFive, "Mobs the Scent will spawn at level 0");
      scentWaveMinimumFive = cfg.getInt("Scent Level 5 Minimum Wave Number", "parasite_collective_consciousness_scent_lvl_5", scentWaveMinimumFive, 0, 100, ".");
      scentWaveMaximumFive = cfg.getInt("Scent Level 5 Maximum Wave Number", "parasite_collective_consciousness_scent_lvl_5", scentWaveMaximumFive, 0, 100, ".");
      scentWaveMinMobWaveFive = cfg.getInt("Scent Level 5 Minimum Mob Wave Number", "parasite_collective_consciousness_scent_lvl_5", scentWaveMinMobWaveFive, 0, 100, ".");
      scentWaveMaxMobWaveFive = cfg.getInt("Scent Level 5 Maximum Mob Wave Number", "parasite_collective_consciousness_scent_lvl_5", scentWaveMaxMobWaveFive, 0, 100, ".");
      scentLevelPointsFive = cfg.getInt("Scent Level 5 Points Required", "parasite_collective_consciousness_scent_lvl_5", scentLevelPointsFive, 0, 1000000, ".");
   }

   private static void inithivemindscentlvl6Config(Configuration cfg) {
      scentLevelSix = cfg.getStringList("Scent Level 6 Mob Table", "parasite_collective_consciousness_scent_lvl_6", scentLevelSix, "Mobs the Scent will spawn at level 0");
      scentWaveMinimumSix = cfg.getInt("Scent Level 6 Minimum Wave Number", "parasite_collective_consciousness_scent_lvl_6", scentWaveMinimumSix, 0, 100, ".");
      scentWaveMaximumSix = cfg.getInt("Scent Level 6 Maximum Wave Number", "parasite_collective_consciousness_scent_lvl_6", scentWaveMaximumSix, 0, 100, ".");
      scentWaveMinMobWaveSix = cfg.getInt("Scent Level 6 Minimum Mob Wave Number", "parasite_collective_consciousness_scent_lvl_6", scentWaveMinMobWaveSix, 0, 100, ".");
      scentWaveMaxMobWaveSix = cfg.getInt("Scent Level 6 Maximum Mob Wave Number", "parasite_collective_consciousness_scent_lvl_6", scentWaveMaxMobWaveSix, 0, 100, ".");
      scentLevelPointsSix = cfg.getInt("Scent Level 6 Points Required", "parasite_collective_consciousness_scent_lvl_6", scentLevelPointsSix, 0, 1000000, ".");
   }

   private static void inithivemindscentlvl7Config(Configuration cfg) {
      scentLevelSeven = cfg.getStringList("Scent Level 7 Mob Table", "parasite_collective_consciousness_scent_lvl_7", scentLevelSeven, "Mobs the Scent will spawn at level 0");
      scentWaveMinimumSeven = cfg.getInt("Scent Level 7 Minimum Wave Number", "parasite_collective_consciousness_scent_lvl_7", scentWaveMinimumSeven, 0, 100, ".");
      scentWaveMaximumSeven = cfg.getInt("Scent Level 7 Maximum Wave Number", "parasite_collective_consciousness_scent_lvl_7", scentWaveMaximumSeven, 0, 100, ".");
      scentWaveMinMobWaveSeven = cfg.getInt("Scent Level 7 Minimum Mob Wave Number", "parasite_collective_consciousness_scent_lvl_7", scentWaveMinMobWaveSeven, 0, 100, ".");
      scentWaveMaxMobWaveSeven = cfg.getInt("Scent Level 7 Maximum Mob Wave Number", "parasite_collective_consciousness_scent_lvl_7", scentWaveMaxMobWaveSeven, 0, 100, ".");
      scentLevelPointsSeven = cfg.getInt("Scent Level 7 Points Required", "parasite_collective_consciousness_scent_lvl_7", scentLevelPointsSeven, 0, 1000000, ".");
   }

   private static void inithivemindscentlvl8Config(Configuration cfg) {
      scentLevelEight = cfg.getStringList("Scent Level 8 Mob Table", "parasite_collective_consciousness_scent_lvl_8", scentLevelEight, "Mobs the Scent will spawn at level 0");
      scentWaveMinimumEight = cfg.getInt("Scent Level 8 Minimum Wave Number", "parasite_collective_consciousness_scent_lvl_8", scentWaveMinimumEight, 0, 100, ".");
      scentWaveMaximumEight = cfg.getInt("Scent Level 8 Maximum Wave Number", "parasite_collective_consciousness_scent_lvl_8", scentWaveMaximumEight, 0, 100, ".");
      scentWaveMinMobWaveEight = cfg.getInt("Scent Level 8 Minimum Mob Wave Number", "parasite_collective_consciousness_scent_lvl_8", scentWaveMinMobWaveEight, 0, 100, ".");
      scentWaveMaxMobWaveEight = cfg.getInt("Scent Level 8 Maximum Mob Wave Number", "parasite_collective_consciousness_scent_lvl_8", scentWaveMaxMobWaveEight, 0, 100, ".");
      scentLevelPointsEight = cfg.getInt("Scent Level 8 Points Required", "parasite_collective_consciousness_scent_lvl_8", scentLevelPointsEight, 0, 1000000, ".");
   }

   private static void inittotaldevelopmentConfig(Configuration cfg) {
   }

   public static void initConfig(FMLPreInitializationEvent e) {
      VFile2 directory = e.getModConfigurationDirectory();
      CommonProxy.configSistems = new Configuration(new VFile2(directory, "srparasites/SRParasitesSystems.cfg"));
      readConfig();
   }

   public static boolean readConfig() {
      Configuration cfgS = CommonProxy.configSistems;

      try {
         cfgS.load();
         initstatuseffectsConfig(cfgS);
         initGeneralSistemsConfig(cfgS);
         initmergeConfig(cfgS);
         initevolutionConfig(cfgS);
         initevolution0Config(cfgS);
         initevolution1Config(cfgS);
         initevolution2Config(cfgS);
         initevolution3Config(cfgS);
         initevolution4Config(cfgS);
         initevolution5Config(cfgS);
         initevolution6Config(cfgS);
         initevolution7Config(cfgS);
         initevolution8Config(cfgS);
         initreinforcementSystemConfig(cfgS);
         inithivemindConfig(cfgS);
         inithivemindscentlvl0Config(cfgS);
         inithivemindscentlvl1Config(cfgS);
         inithivemindscentlvl2Config(cfgS);
         inithivemindscentlvl3Config(cfgS);
         inithivemindscentlvl4Config(cfgS);
         inithivemindscentlvl5Config(cfgS);
         inithivemindscentlvl6Config(cfgS);
         inithivemindscentlvl7Config(cfgS);
         inithivemindscentlvl8Config(cfgS);
         boolean e = true;
         return e;
      } catch (Exception var5) {
         SRPMain.logger.log(Level.ERROR, "Problem loading configuration file", var5);
      } finally {
         if (cfgS.hasChanged()) {
            cfgS.save();
         }

      }

      return false;
   }
}
