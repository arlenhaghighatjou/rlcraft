package com.dhanantry.scapeandrunparasites.util.config;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.proxy.CommonProxy;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class SRPConfigMobs {
   private static final String CATEGORY_GENERAL_MOBS = "configuration_mobs";
   private static String health = " Health Multiplier";
   private static String damage = " Damage Multiplier";
   private static String armor = " Armor Multiplier";
   private static String kd = " Knockback Resistance Multiplier";
   private static String lootXp = " Where: \n \"minecraft:nether_star\" is the item, \n \"100\" is the chance to drop, \n \"5\" is the max number of items, \n \"true\" is for the item to always roll, if false the item will be unique and only 1 will be choosen. \n";
   private static final String SHYCO_CATEGORY = "srparasites:longarms";
   public static float shycoHealthMultiplier = 1.0F;
   public static float shycoDamageMultiplier = 1.0F;
   public static float shycoArmorMultiplier = 1.0F;
   public static float shycoKDResistanceMultiplier = 1.0F;
   public static double shycoDamageIncreased = 0.5D;
   public static float shycoadaptedhealth = 50.0F;
   public static float shycoadapteddamage = 12.0F;
   public static float shycoadaptedarmor = 7.0F;
   public static float shycoadaptedkdresistance = 0.3F;
   public static double shycoadapteddamageincrease = 1.0D;
   public static String[] shycoadaptedloot = new String[]{"srparasites:ada_longarms_drop;40;2;true", "srparasites:lurecomponent4;40;1;false", "srparasites:bone;10;2;true"};
   public static String[] shycoadaptedOrbEffects = new String[]{"0;15;2;minecraft:hunger;0;0", "0;25;2;srparasites:needler;0;0", "0;15;2;minecraft:slowness;0;0"};
   public static int shycoSpawnRate = 15;
   public static int shycoASpawnRate = 15;
   public static boolean shycoEnabled = true;
   public static String[] shycoLoot = new String[]{"srparasites:lurecomponent3;30;2;false", "srparasites:ada_longarms_drop;20;2;true", "srparasites:bone;5;1;true"};
   public static String[] shycoOrbEffects = new String[]{"0;15;1;minecraft:hunger;0;0", "0;15;1;minecraft:slowness;0;0"};
   private static final String DORPA_CATEGORY = "srparasites:sim_bigspider";
   public static float dorpaHealthMultiplier = 1.0F;
   public static float dorpaDamageMultiplier = 1.0F;
   public static float dorpaArmorMultiplier = 1.0F;
   public static float dorpaKDResistanceMultiplier = 1.0F;
   public static float dorpaRangedAttackDamageMultiplier = 1.0F;
   public static int dorpaSpawnRate = 10;
   public static boolean dorpaEnabled = true;
   public static boolean dorpaWeb = true;
   public static String[] dorpaLoot = new String[0];
   public static String dorpamob = "srparasites:buglin;5;5";
   private static final String RATHOL_CATEGORY = "srparasites:carrier_heavy";
   public static float ratholHealthMultiplier = 1.0F;
   public static float ratholDamageMultiplier = 1.0F;
   public static float ratholArmorMultiplier = 1.0F;
   public static float ratholKDResistanceMultiplier = 1.0F;
   public static int ratholSpawnRate = 20;
   public static boolean ratholEnabled = true;
   public static boolean ratholGriefing = false;
   public static String[] ratholLoot = new String[0];
   public static String[] ratholMobs = new String[]{"srparasites:rupter;4;4", "srparasites:buglin;5;3", "srparasites:pri_yelloweye;3;3"};
   private static final String EMANA_CATEGORY = "srparasites:yelloweye";
   public static float emanaHealthMultiplier = 1.0F;
   public static float emanaDamageMultiplier = 1.0F;
   public static float emanaArmorMultiplier = 1.0F;
   public static float emanaKDResistanceMultiplier = 1.0F;
   public static int emanaSpawnRate = 10;
   public static int emanaASpawnRate = 10;
   public static boolean emanaEnabled = true;
   public static int emanaPoisonDuration = 3;
   public static int emanaPoisonAmplifier = 1;
   public static String[] emanaLoot = new String[]{"srparasites:lurecomponent3;30;2;false", "srparasites:ada_yelloweye_drop;20;1;true", "srparasites:bone;2;1;true"};
   public static int emanaMaxY = 256;
   public static double emanaGearD = 0.04D;
   public static float emanaadaptedhealth = 25.0F;
   public static float emanaadapteddamage = 5.0F;
   public static float emanaadaptedarmor = 10.0F;
   public static float emanaadaptedkdresistance = 0.4F;
   public static double emanaadaptedgeard = 0.07D;
   public static double emanaadaptedmelee = 30.0D;
   public static String[] emanaadaptedloot = new String[]{"srparasites:ada_yelloweye_drop;40;1;true", "srparasites:lurecomponent4;40;1;false", "srparasites:bone;10;1;true"};
   private static final String LODO_CATEGORY = "srparasites:buglin";
   public static float lodoHealthMultiplier = 1.0F;
   public static float lodoDamageMultiplier = 1.0F;
   public static float lodoArmorMultiplier = 1.0F;
   public static float lodoKDResistanceMultiplier = 1.0F;
   public static int lodoSpawnRate = 30;
   public static boolean lodoEnabled = true;
   public static String[] LodoLoot = new String[0];
   private static final String HULL_CATEGORY = "srparasites:manducater";
   public static float hullHealthMultiplier = 1.0F;
   public static float hullDamageMultiplier = 1.0F;
   public static float hullArmorMultiplier = 1.0F;
   public static float hullKDResistanceMultiplier = 1.0F;
   public static double hullNeededHealth = 0.7D;
   public static float hullNeededTime = 15.0F;
   public static float hullStealthDamageMultiplier = 2.0F;
   public static float hulladaptedhealth = 15.0F;
   public static float hulladapteddamage = 12.0F;
   public static float hulladaptedarmor = 6.0F;
   public static float hulladaptedkdresistance = 0.5F;
   public static double hulladaptedneededhealth = 0.4D;
   public static float hulladaptedneededtime = 7.0F;
   public static float hulladaptedstealthdamage = 2.0F;
   public static String[] hulladaptedloot = new String[]{"srparasites:ada_manducater_drop;30;1;false", "srparasites:lurecomponent4;40;1;false", "srparasites:bone;10;2;true"};
   public static String[] hulladaptedOrbEffects = new String[]{"0;15;2;minecraft:hunger;0;0", "0;25;2;srparasites:needler;0;0", "1;20;2;minecraft:invisibility;2;2"};
   public static int hullSpawnRate = 30;
   public static int hullASpawnRate = 30;
   public static boolean hullEnabled = true;
   public static String[] hullLoot = new String[]{"srparasites:lurecomponent3;20;2;false", "srparasites:ada_manducater_drop;20;1;false", "srparasites:bone;5;1;true"};
   public static String[] hullOrbEffects = new String[]{"0;15;1;minecraft:hunger;0;0", "1;20;1;minecraft:invisibility;2;2"};
   private static final String CANRA_CATEGORY = "srparasites:summoner";
   public static float canraHealthMultiplier = 1.0F;
   public static float canraDamageMultiplier = 1.0F;
   public static float canraArmorMultiplier = 1.0F;
   public static float canraKDResistanceMultiplier = 1.0F;
   public static int canraSummoningCooldown = 10;
   public static int canraTotalActiveMobs = 4;
   public static int canraLimit = 2;
   public static int canraRemainPlus = 1;
   public static float canraRemainHealth = 1.0F;
   public static String[] canraMobList = new String[]{"srparasites:rupter;1;1"};
   public static float canraadaptedhealth = 60.0F;
   public static float canraadapteddamage = 15.0F;
   public static float canraadaptedarmor = 10.0F;
   public static float canraadaptedkdresistance = 0.5F;
   public static int canraadaptedsummoningcooldown = 8;
   public static String[] canraadaptedloot = new String[]{"srparasites:ada_summoner_drop;60;1;false", "srparasites:lurecomponent4;30;1;false", "srparasites:bone;10;2;true"};
   public static String[] canraadaptedOrbEffects = new String[]{"0;15;2;minecraft:hunger;0;0", "0;25;2;srparasites:needler;0;0", "2;30;2;minecraft:speed;0;0"};
   public static int canraadaptedtotalactivemobs = 6;
   public static int canraadaptedlimit = 1;
   public static int canraadaptedremainplus = 3;
   public static float canraadaptedremainhealth = 1.5F;
   public static String[] canraadaptedmoblist = new String[]{"srparasites:rupter;0.1;1", "srparasites:sim_human;0.3;2", "srparasites:sim_cow;0.3;2", "srparasites:sim_wolf;0.3;2"};
   public static int canraSpawnRate = 5;
   public static int canraASpawnRate = 5;
   public static boolean canraEnabled = true;
   public static String[] canraLoot = new String[]{"srparasites:lurecomponent3;30;2;false", "srparasites:ada_summoner_drop;20;1;false", "srparasites:bone;5;1;true"};
   public static String[] canraOrbEffects = new String[]{"0;15;1;minecraft:hunger;0;0", "2;30;1;minecraft:speed;0;0"};
   private static final String ALAFHA_CATEGORY = "srparasites:overseer";
   public static float alafhaHealthMultiplier = 1.0F;
   public static float alafhaDamageMultiplier = 1.0F;
   public static float alafhaArmorMultiplier = 1.0F;
   public static float alafhaKDResistanceMultiplier = 1.0F;
   public static int alafhaSpawnRate = 5;
   public static boolean alafhaEnabled = true;
   public static boolean alafhaGriefing = false;
   public static String[] alafhaLoot = new String[]{"srparasites:lurecomponent5;40;1;false"};
   public static int alafhaSummoningCooldown = 200;
   public static int alafhaTotalActiveMobs = 6;
   public static int alafhaLimit = 6;
   public static String[] alafhaMobList = new String[]{"srparasites:rupter;1;1", "srparasites:grunt;0.5;1"};
   public static int alafhaMaxY = 256;
   public static double alafhaMelee = 45.0D;
   private static final String NOGLA_CATEGORY = "srparasites:reeker";
   public static float noglaHealthMultiplier = 1.0F;
   public static float noglaDamageMultiplier = 1.0F;
   public static float noglaArmorMultiplier = 1.0F;
   public static float noglaKDResistanceMultiplier = 1.0F;
   public static float noglaadaptedhealth = 50.0F;
   public static float noglaadapteddamage = 20.0F;
   public static float noglaadaptedarmor = 15.0F;
   public static float noglaadaptedkdresistance = 0.4F;
   public static String[] noglaadaptedloot = new String[]{"srparasites:ada_reeker_drop;50;3;false", "srparasites:lurecomponent4;50;1;false", "srparasites:bone;10;2;true"};
   public static String[] noglaadaptedOrbEffects = new String[]{"0;15;2;minecraft:hunger;0;0", "0;25;2;srparasites:needler;0;0", "0;15;2;minecraft:nausea;0;0"};
   public static int noglaSpawnRate = 10;
   public static int noglaASpawnRate = 10;
   public static boolean noglaEnabled = true;
   public static String[] noglaLoot = new String[]{"srparasites:lurecomponent3;20;2;false", "srparasites:ada_reeker_drop;20;1;false", "srparasites:bone;5;1;true"};
   public static String[] noglaOrbEffects = new String[]{"0;15;1;minecraft:hunger;0;0", "0;15;1;minecraft:nausea;0;0"};
   private static final String BUTHOL_CATEGORY = "srparasites:carrier_flying";
   public static float butholHealthMultiplier = 1.0F;
   public static float butholDamageMultiplier = 1.0F;
   public static float butholArmorMultiplier = 1.0F;
   public static float butholKDResistanceMultiplier = 1.0F;
   public static int butholSpawnRate = 15;
   public static boolean butholEnabled = true;
   public static boolean ButholGriefing = false;
   public static String[] butholLoot = new String[0];
   public static int butholMaxY = 256;
   public static String[] butholMobs = new String[]{"srparasites:rupter;4;3", "srparasites:buglin;3;2"};
   private static final String MUDO_CATEGORY = "srparasites:rupter";
   public static float mudoHealthMultiplier = 1.0F;
   public static float mudoDamageMultiplier = 1.0F;
   public static float mudoArmorMultiplier = 1.0F;
   public static float mudoKDResistanceMultiplier = 1.0F;
   public static int mudoSpawnRate = 30;
   public static boolean mudoEnabled = true;
   public static float mudoMinDamage = 0.1F;
   public static String[] mudoLoot = new String[]{"srparasites:lurecomponent1;30;2;true"};
   public static boolean mudoAnimalAttacking = true;
   public static int mudoTunnelValue = 5;
   public static byte mudoTunnelPhase = 3;
   public static int mudoMangler = 30;
   private static final String NUUH_CATEGORY = "srparasites:mangler";
   public static float nuuhHealthMultiplier = 1.0F;
   public static float nuuhDamageMultiplier = 1.0F;
   public static float nuuhArmorMultiplier = 1.0F;
   public static float nuuhKDResistanceMultiplier = 1.0F;
   public static int nuuhSpawnRate = 30;
   public static boolean nuuhEnabled = true;
   public static float nuuhMinDamage = 0.3F;
   public static String[] nuuhLoot = new String[]{"srparasites:lurecomponent1;30;2;true"};
   public static boolean nuuhAnimalAttacking = true;
   private static final String HOST_CATEGORY = "srparasites:host";
   public static float hostHealthMultiplier = 1.0F;
   public static float hostDamageMultiplier = 1.0F;
   public static float hostArmorMultiplier = 1.0F;
   public static float hostKDResistanceMultiplier = 1.0F;
   public static float hostDamage = 7.0F;
   public static int hostSpawnRate = 0;
   public static boolean hostEnabled = true;
   public static int hostSkele = 5;
   public static int hostHerd = 40;
   public static String[] hostLoot = new String[]{"srparasites:assimilated_flesh;40;7;false", "srparasites:lurecomponent2;5;4;true"};
   private static final String HERD_CATEGORY = "srparasites:herd";
   public static float herdHealthMultiplier = 1.0F;
   public static float herdDamageMultiplier = 1.0F;
   public static float herdArmorMultiplier = 1.0F;
   public static float herdKDResistanceMultiplier = 1.0F;
   public static float herdDamage = 7.0F;
   public static int herdSpawnRate = 0;
   public static boolean herdEnabled = true;
   public static String[] herdLoot = new String[0];
   private static final String THRALL_CATEGORY = "srparasites:thrall";
   public static float thrallHealthMultiplier = 1.0F;
   public static float thrallDamageMultiplier = 1.0F;
   public static float thrallArmorMultiplier = 1.0F;
   public static float thrallKDResistanceMultiplier = 1.0F;
   public static int thrallSpawnRate = 30;
   public static boolean thrallEnabled = true;
   public static String[] thrallLoot = new String[]{"srparasites:assimilated_flesh;40;1;false", "srparasites:lurecomponent3;5;1;true", "srparasites:bone;5;1;true"};
   private static final String INFBEAR_CATEGORY = "srparasites:sim_bear";
   public static float infbearHealthMultiplier = 1.0F;
   public static float infbearDamageMultiplier = 1.0F;
   public static float infbearArmorMultiplier = 1.0F;
   public static float infbearKDResistanceMultiplier = 1.0F;
   public static int infbearSpawnRate = 30;
   public static boolean infbearEnabled = true;
   public static String[] infbearLoot = new String[]{"srparasites:assimilated_flesh;40;1;false", "srparasites:lurecomponent2;5;1;true"};
   private static final String INFENDERMAN_CATEGORY = "srparasites:sim_enderman";
   public static float infendermanHealthMultiplier = 1.0F;
   public static float infendermanDamageMultiplier = 1.0F;
   public static float infendermanArmorMultiplier = 1.0F;
   public static float infendermanKDResistanceMultiplier = 1.0F;
   public static int infendermanSpawnRate = 5;
   public static boolean infendermanEnabled = true;
   public static int infendermantelefreq = 2;
   public static boolean infendermanteleally = true;
   public static String[] infendermanLoot = new String[0];
   public static String[] infendermanheadLoot = new String[0];
   public static float infendermanHealthHead = 0.3F;
   public static float infendermanDamageHead = 0.3F;
   public static float infendermanheadchance = 0.5F;
   public static int infendermanallyCool = 40;
   public static int infendermansaw = 80;
   public static float infendermanTeleDamage = 2.0F;
   private static final String INFHUMAN_CATEGORY = "srparasites:sim_human";
   public static float infhumanHealthMultiplier = 1.0F;
   public static float infhumanDamageMultiplier = 1.0F;
   public static float infhumanArmorMultiplier = 1.0F;
   public static float infhumanKDResistanceMultiplier = 1.0F;
   public static int infhumanSpawnRate = 30;
   public static boolean infhumanEnabled = true;
   public static String[] infhumanLoot = new String[]{"srparasites:assimilated_flesh;40;1;false", "srparasites:lurecomponent2;5;1;true"};
   public static String[] infhumanheadLoot = new String[0];
   public static float infhumanHealthHead = 0.3F;
   public static float infhumanDamageHead = 0.3F;
   public static float infhumanheadchance = 0.5F;
   private static final String INFCOW_CATEGORY = "srparasites:sim_cow";
   public static float infcowHealthMultiplier = 1.0F;
   public static float infcowDamageMultiplier = 1.0F;
   public static float infcowArmorMultiplier = 1.0F;
   public static float infcowKDResistanceMultiplier = 1.0F;
   public static int infcowSpawnRate = 25;
   public static boolean infcowEnabled = true;
   public static String[] infcowLoot = new String[]{"srparasites:assimilated_flesh;40;2;false", "srparasites:lurecomponent2;5;1;true"};
   public static String[] infcowheadLoot = new String[0];
   public static float infcowHealthHead = 0.3F;
   public static float infcowDamageHead = 0.3F;
   public static float infcowheadchance = 0.5F;
   public static String infcowmob = "srparasites:buglin;4;3";
   private static final String INFSHEEP_CATEGORY = "srparasites:sim_sheep";
   public static float infsheepHealthMultiplier = 1.0F;
   public static float infsheepDamageMultiplier = 1.0F;
   public static float infsheepArmorMultiplier = 1.0F;
   public static float infsheepKDResistanceMultiplier = 1.0F;
   public static int infsheepSpawnRate = 25;
   public static boolean infsheepEnabled = true;
   public static String[] infsheepLoot = new String[]{"srparasites:assimilated_flesh;40;1;false", "srparasites:lurecomponent2;5;1;true"};
   public static String[] infsheepheadLoot = new String[0];
   public static float infsheepHealthHead = 0.3F;
   public static float infsheepDamageHead = 0.3F;
   public static float infsheepheadchance = 0.5F;
   public static String infsheepmob = "srparasites:buglin;3;3";
   private static final String INFWOLF_CATEGORY = "srparasites:sim_wolf";
   public static float infwolfHealthMultiplier = 1.0F;
   public static float infwolfDamageMultiplier = 1.0F;
   public static float infwolfArmorMultiplier = 1.0F;
   public static float infwolfKDResistanceMultiplier = 1.0F;
   public static int infwolfSpawnRate = 25;
   public static boolean infwolfEnabled = true;
   public static String[] infwolfLoot = new String[]{"srparasites:assimilated_flesh;10;1;false", "srparasites:lurecomponent2;5;1;true"};
   public static String[] infwolfheadLoot = new String[0];
   public static float infwolfHealthHead = 0.3F;
   public static float infwolfDamageHead = 0.3F;
   public static float infwolfheadchance = 0.5F;
   public static String infwolfmob = "srparasites:buglin;2;2";
   private static final String INFPIG_CATEGORY = "srparasites:sim_pig";
   public static float infpigHealthMultiplier = 1.0F;
   public static float infpigDamageMultiplier = 1.0F;
   public static float infpigArmorMultiplier = 1.0F;
   public static float infpigKDResistanceMultiplier = 1.0F;
   public static int infpigSpawnRate = 25;
   public static boolean infpigEnabled = true;
   public static String[] infpigLoot = new String[]{"srparasites:assimilated_flesh;40;2;false", "srparasites:lurecomponent2;5;1;true"};
   public static String[] infpigheadLoot = new String[0];
   public static float infpigHealthHead = 0.3F;
   public static float infpigDamageHead = 0.3F;
   public static float infpigheadchance = 0.5F;
   public static String infpigmob = "srparasites:buglin;2;2";
   private static final String INFVILLAGER_CATEGORY = "srparasites:sim_villager";
   public static float infvillagerHealthMultiplier = 1.0F;
   public static float infvillagerDamageMultiplier = 1.0F;
   public static float infvillagerArmorMultiplier = 1.0F;
   public static float infvillagerKDResistanceMultiplier = 1.0F;
   public static int infvillagerSpawnRate = 25;
   public static boolean infvillagerEnabled = true;
   public static String[] infvillagerLoot = new String[]{"srparasites:assimilated_flesh;40;1;false", "srparasites:lurecomponent2;5;1;true"};
   public static String[] infvillagerheadLoot = new String[0];
   public static float infvillagerHealthHead = 0.3F;
   public static float infvillagerDamageHead = 0.3F;
   public static float infvillagerheadchance = 0.5F;
   public static String infvillagermob = "srparasites:buglin;2;2";
   private static final String INFHORSE_CATEGORY = "srparasites:sim_horse";
   public static float infhorseHealthMultiplier = 1.0F;
   public static float infhorseDamageMultiplier = 1.0F;
   public static float infhorseArmorMultiplier = 1.0F;
   public static float infhorseKDResistanceMultiplier = 1.0F;
   public static float infhorseExplotionMult = 2.0F;
   public static int infhorseSpawnRate = 25;
   public static boolean infhorseEnabled = true;
   public static String[] infhorseLoot = new String[]{"srparasites:assimilated_flesh;40;2;false", "srparasites:lurecomponent2;5;1;true"};
   public static String[] infhorseheadLoot = new String[0];
   public static float infhorseHealthHead = 0.3F;
   public static float infhorseDamageHead = 0.3F;
   public static float infhorseheadchance = 0.5F;
   public static String infhorsemob = "srparasites:buglin;2;2";
   private static final String INFADVENTURER_CATEGORY = "srparasites:sim_adventurer";
   public static float infadventurerHealthMultiplier = 1.0F;
   public static float infadventurerDamageMultiplier = 1.0F;
   public static float infadventurerArmorMultiplier = 1.0F;
   public static float infadventurerKDResistanceMultiplier = 1.0F;
   public static int infadventurerSpawnRate = 15;
   public static int infadventurerThrall = 15;
   public static boolean infadventurerEnabled = true;
   public static boolean infadventurerSpawnBy = true;
   public static String infadventurermob = "srparasites:buglin;4;3";
   public static String[] infadventurerLoot = new String[]{"srparasites:assimilated_flesh;40;1;false", "srparasites:lurecomponent2;5;1;true"};
   public static String[] infadventurerheadLoot = new String[0];
   public static float infadventurerHealthHead = 0.3F;
   public static float infadventurerDamageHead = 0.3F;
   public static float infadventurerheadchance = 0.5F;
   private static final String INFDRAGONE_CATEGORY = "srparasites:sim_dragone";
   public static float infdragoneHealthMultiplier = 1.0F;
   public static float infdragoneDamageMultiplier = 1.0F;
   public static float infdragoneArmorMultiplier = 1.0F;
   public static float infdragoneKDResistanceMultiplier = 1.0F;
   public static float infdragoneRangeDamageMultiplier = 1.0F;
   public static int infdragoneSpawnRate = 2;
   public static boolean infdragoneEnabled = true;
   public static String[] infdragoneLoot = new String[0];
   public static String[] infdragoneheadLoot = new String[0];
   public static float infdragoneHealthHead = 0.3F;
   public static float infdragoneDamageHead = 0.3F;
   private static final String ZETMO_CATEGORY = "srparasites:bolster";
   public static float zetmoHealthMultiplier = 1.0F;
   public static float zetmoDamageMultiplier = 1.0F;
   public static float zetmoArmorMultiplier = 1.0F;
   public static float zetmoKDResistanceMultiplier = 1.0F;
   public static int zetmoCD = 30;
   public static int zetmoRange = 16;
   public static float zetmoadaptedhealth = 70.0F;
   public static float zetmoadapteddamage = 30.0F;
   public static float zetmoadaptedarmor = 15.0F;
   public static float zetmoadaptedkdresistance = 0.65F;
   public static int zetmoadaptedcd = 60;
   public static int zetmoadaptedrange = 24;
   public static String[] zetmoadaptedloot = new String[]{"srparasites:lurecomponent4;30;3;false", "srparasites:ada_bolster_drop;40;1;true", "srparasites:bone;10;3;true"};
   public static String[] zetmoadaptedOrbEffects = new String[]{"0;15;2;minecraft:hunger;0;0", "0;25;2;srparasites:needler;0;0", "2;30;2;minecraft:speed;0;0"};
   public static String[] zetmoadaptedeffects = new String[]{"30;3;minecraft:regeneration"};
   public static int zetmoSpawnRate = 10;
   public static int zetmoASpawnRate = 10;
   public static boolean zetmoEnabled = true;
   public static String[] zetmoLoot = new String[]{"srparasites:lurecomponent3;20;2;false", "srparasites:ada_bolster_drop;20;1;true", "srparasites:bone;5;1;true"};
   public static String[] zetmoOrbEffects = new String[]{"0;15;1;minecraft:hunger;0;0", "2;30;1;minecraft:speed;0;0"};
   public static String[] zetmoEffects = new String[]{"30;1;minecraft:regeneration"};
   private static final String ANGED_CATEGORY = "srparasites:vigilante";
   public static float angedHealthMultiplier = 1.0F;
   public static float angedDamageMultiplier = 1.0F;
   public static float angedRangeDamageMultiplier = 1.0F;
   public static float angedArmorMultiplier = 1.0F;
   public static float angedKDResistanceMultiplier = 1.0F;
   public static int angedSpawnRate = 0;
   public static boolean angedEnabled = true;
   public static String[] angedLoot = new String[]{"srparasites:lurecomponent5;40;1;false"};
   public static String[] angedOrbEffects = new String[]{"0;15;3;minecraft:hunger;0;0", "0;25;3;srparasites:needler;0;0", "0;15;3;minecraft:mining_fatigue;0;0", "2;30;3;minecraft:speed;0;0"};
   private static final String TONRO_CATEGORY = "srparasites:kyphosis";
   public static float tonroHealthMultiplier = 1.0F;
   public static float tonroDamageMultiplier = 1.0F;
   public static float tonroSwingDamageMultiplier = 1.0F;
   public static float tonroArmorMultiplier = 1.0F;
   public static boolean tonroEnabled = true;
   public static String[] tonroLoot = new String[0];
   private static final String UNVO_CATEGORY = "srparasites:sentry";
   public static float unvoHealthMultiplier = 1.0F;
   public static float unvoDamageMultiplier = 1.0F;
   public static float unvoRangeDamageMultiplier = 1.0F;
   public static float unvoArmorMultiplier = 1.0F;
   public static boolean unvoEnabled = true;
   public static String[] unvoLoot = new String[0];
   public static double unvoGearD = 0.1D;
   public static int unvoPoisonDuration = 7;
   public static int unvoPoisonAmplifier = 1;
   private static final String GANRO_CATEGORY = "srparasites:warden";
   public static float ganroHealthMultiplier = 1.0F;
   public static float ganroDamageMultiplier = 1.0F;
   public static float ganroArmorMultiplier = 1.0F;
   public static float ganroKDResistanceMultiplier = 1.0F;
   public static int ganroSpawnRate = 0;
   public static boolean ganroEnabled = true;
   public static String[] ganroLoot = new String[]{"srparasites:lurecomponent5;40;1;false"};
   public static String[] ganroOrbEffects = new String[]{"0;15;3;minecraft:hunger;0;0", "0;25;3;srparasites:needler;0;0", "0;15;3;minecraft:mining_fatigue;0;0", "2;30;3;minecraft:absorption;0;0"};
   private static final String ESOR_CATEGORY = "srparasites:marauder";
   public static float esorHealthMultiplier = 1.0F;
   public static float esorDamageMultiplier = 1.0F;
   public static float esorArmorMultiplier = 1.0F;
   public static float esorKDResistanceMultiplier = 1.0F;
   public static int esorSpawnRate = 0;
   public static boolean esorEnabled = true;
   public static String[] esorLoot = new String[]{"srparasites:lurecomponent5;40;1;false"};
   public static String[] esorOrbEffects = new String[]{"0;15;3;minecraft:hunger;0;0", "0;25;3;srparasites:needler;0;0", "0;15;3;minecraft:mining_fatigue;0;0", "0;15;3;minecraft:weakness;0;0"};
   private static final String ORCH_CATEGORY = "srparasites:monarch";
   public static float orchHealthMultiplier = 1.0F;
   public static float orchDamageMultiplier = 1.0F;
   public static float orchArmorMultiplier = 1.0F;
   public static float orchKDResistanceMultiplier = 1.0F;
   public static int orchSpawnRate = 0;
   public static boolean orchEnabled = true;
   public static String[] orchLoot = new String[]{"srparasites:lurecomponent5;40;1;false"};
   public static String[] orchOrbEffects = new String[]{"0;15;3;minecraft:hunger;0;0", "0;25;3;srparasites:needler;0;0", "0;15;3;minecraft:mining_fatigue;0;0", "0;15;3;minecraft:wither;0;0"};
   private static final String FLOG_CATEGORY = "srparasites:grunt";
   public static float flogHealthMultiplier = 1.0F;
   public static float flogDamageMultiplier = 1.0F;
   public static float flogArmorMultiplier = 1.0F;
   public static float flogKDResistanceMultiplier = 1.0F;
   public static int flogSpawnRate = 0;
   public static boolean flogEnabled = true;
   public static String[] flogLoot = new String[]{"srparasites:lurecomponent5;40;1;false"};
   private static final String ORONCO_CATEGORY = "srparasites:anc_dreadnaut";
   public static float oroncoHealthMultiplier = 1.0F;
   public static float oroncoDamageMultiplier = 1.0F;
   public static float oroncoArmorMultiplier = 1.0F;
   public static float oroncoKDResistanceMultiplier = 1.0F;
   public static int oroncoSpawnRate = 1;
   public static boolean oroncoEnabled = true;
   public static int oroncoMaxY = 256;
   public static int oroncoMinY = 7;
   public static String[] oroncoLoot = new String[0];
   public static String[] oroncoMobList = new String[]{"srparasites:rupter;1", "srparasites:rupter;1", "srparasites:rupter;1", "srparasites:rupter;05", "srparasites:rupter;0.5", "srparasites:grunt;0.7"};
   public static boolean oroncoG = true;
   public static int oroncoMaxMobPod = 1;
   public static int oroncoPodNumber = 5;
   public static int oroncoPodCooldown = 12;
   private static final String TERLA_CATEGORY = "srparasites:anc_overlord";
   public static float terlaHealthMultiplier = 1.0F;
   public static float terlaDamageMultiplier = 1.0F;
   public static float terlaArmorMultiplier = 1.0F;
   public static float terlaKDResistanceMultiplier = 1.0F;
   public static int terlaSpawnRate = 1;
   public static boolean terlaEnabled = true;
   public static String[] terlaLoot = new String[0];
   private static final String RANRAC_CATEGORY = "srparasites:arachnida";
   public static float arachnidaHealthMultiplier = 1.0F;
   public static float arachnidaDamageMultiplier = 1.0F;
   public static float arachnidaArmorMultiplier = 1.0F;
   public static float arachnidaKDResistanceMultiplier = 1.0F;
   public static float arachnidaadaptedhealth = 45.0F;
   public static float arachnidaadapteddamage = 15.0F;
   public static float arachnidaadaptedarmor = 10.0F;
   public static float arachnidaadaptedkdresistance = 0.2F;
   public static String[] arachnidaadaptedloot = new String[]{"srparasites:lurecomponent4;30;3;false", "srparasites:ada_arachnida_drop;40;3;true", "srparasites:bone;10;2;true"};
   public static String[] arachnidaadaptedOrbEffects = new String[]{"0;15;2;minecraft:hunger;0;0", "0;25;2;srparasites:needler;0;0", "0;15;2;minecraft:blindness;0;0"};
   public static int arachnidaSpawnRate = 10;
   public static int arachnidaASpawnRate = 10;
   public static boolean arachnidaEnabled = true;
   public static String[] arachnidaLoot = new String[]{"srparasites:lurecomponent3;20;1;false", "srparasites:ada_arachnida_drop;20;2;true", "srparasites:bone;5;1;true"};
   public static String[] arachnidaOrbEffects = new String[]{"0;15;1;minecraft:hunger;0;0", "0;15;1;minecraft:blindness;0;0"};
   private static final String KOL_CATEGORY = "srparasites:worker";
   public static float kolHealthMultiplier = 1.0F;
   public static float kolDamageMultiplier = 1.0F;
   public static float kolArmorMultiplier = 1.0F;
   public static float kolKDResistanceMultiplier = 1.0F;
   public static int kolSpawnRate = 15;
   public static boolean kolEnabled = true;
   public static String[] kolLoot = new String[0];
   private static final String INHOOS_CATEGORY = "srparasites:inhoos";
   public static float inhooSHealthMultiplier = 1.0F;
   public static float inhooSDamageMultiplier = 1.0F;
   public static float inhooSArmorMultiplier = 1.0F;
   public static float inhooSKDResistanceMultiplier = 1.0F;
   public static int inhooSSpawnRate = 15;
   public static boolean inhooSEnabled = true;
   public static String[] inhooSLoot = new String[0];
   private static final String INHOOM_CATEGORY = "srparasites:inhoom";
   public static float inhooMHealthMultiplier = 1.0F;
   public static float inhooMDamageMultiplier = 1.0F;
   public static float inhooMArmorMultiplier = 1.0F;
   public static float inhooMKDResistanceMultiplier = 1.0F;
   public static int inhooMSpawnRate = 15;
   public static boolean inhooMEnabled = true;
   public static String[] inhooMLoot = new String[0];
   private static final String OMBOO_CATEGORY = "srparasites:bomber_light";
   public static float ombooHealthMultiplier = 1.0F;
   public static float ombooDamageMultiplier = 1.0F;
   public static float ombooArmorMultiplier = 1.0F;
   public static float ombooKDResistanceMultiplier = 1.0F;
   public static int ombooSpawnRate = 15;
   public static float ombooDamage = 12.0F;
   public static boolean ombooEnabled = true;
   public static boolean ombooGriefing = true;
   public static String[] ombooLoot = new String[]{"srparasites:lurecomponent5;40;1;false"};
   public static int ombooMaxY = 256;
   private static final String JINJO_CATEGORY = "srparasites:bomber_heavy";
   public static float jinjoHealthMultiplier = 1.0F;
   public static float jinjoDamageMultiplier = 1.0F;
   public static float jinjoArmorMultiplier = 1.0F;
   public static float jinjoKDResistanceMultiplier = 1.0F;
   public static float jinjoExplotionMult = 3.0F;
   public static int jinjoSpawnRate = 15;
   public static float jinjoDamage = 12.0F;
   public static boolean jinjoEnabled = true;
   public static boolean jinjoGriefing = true;
   public static String[] jinjoLoot = new String[]{"srparasites:lurecomponent5;40;1;false"};
   public static String[] jinjoOrbEffects = new String[]{"0;15;4;minecraft:hunger;0;0", "0;25;4;srparasites:needler;0;0", "0;15;4;minecraft:mining_fatigue;0;0", "0;10;4;minecraft:wither;0;0"};
   public static int jinjoMaxY = 256;
   public static String[] jinjoMobs = new String[]{"srparasites:overseer", "srparasites:vigilante", "srparasites:marauder", "srparasites:monarch"};
   private static final String VESTA_CATEGORY = "srparasites:carrier_colony";
   public static float vestaHealthMultiplier = 1.0F;
   public static float vestaDamageMultiplier = 1.0F;
   public static float vestaArmorMultiplier = 1.0F;
   public static float vestaKDResistanceMultiplier = 1.0F;
   public static int vestaSpawnRate = 15;
   public static int vestacd = 30;
   public static int vestarange = 60;
   public static boolean vestaEnabled = true;
   public static String[] vestaLoot = new String[]{"srparasites:lurecomponent5;40;1;false"};
   public static String[] vestaOrbEffects = new String[]{"0;15;4;minecraft:hunger;0;0", "0;25;4;srparasites:needler;0;0", "0;15;4;minecraft:mining_fatigue;0;0", "0;10;4;minecraft:wither;0;0"};
   public static String[] vestaeffects = new String[]{"30;3;minecraft:regeneration", "60;2;srparasites:foster", "10;1;srparasites:link"};
   private static final String ELVIA_CATEGORY = "srparasites:wraith";
   public static float elviaHealthMultiplier = 1.0F;
   public static float elviaDamageMultiplier = 1.0F;
   public static float elviaArmorMultiplier = 1.0F;
   public static float elviaKDResistanceMultiplier = 1.0F;
   public static int elviaSpawnRate = 15;
   public static boolean elviaEnabled = true;
   public static String[] elviaLoot = new String[]{"srparasites:lurecomponent5;40;1;false"};
   public static String[] elviaOrbEffects = new String[]{"0;15;4;minecraft:hunger;0;0", "0;25;4;srparasites:needler;0;0", "0;15;4;minecraft:mining_fatigue;0;0", "0;10;4;minecraft:wither;0;0", "0;15;-20;minecraft:levitation;0;0"};
   private static final String LENCIA_CATEGORY = "srparasites:bogle";
   public static float lenciaHealthMultiplier = 1.0F;
   public static float lenciaDamageMultiplier = 1.0F;
   public static float lenciaArmorMultiplier = 1.0F;
   public static float lenciaKDResistanceMultiplier = 1.0F;
   public static int lenciaSpawnRate = 15;
   public static boolean lenciaGriefing = true;
   public static boolean lenciaEnabled = true;
   public static String[] lenciaLoot = new String[]{"srparasites:lurecomponent5;40;1;false"};
   public static String[] lenciaOrbEffects = new String[]{"0;15;4;minecraft:hunger;0;0", "0;120;6;srparasites:needler;0;0", "0;15;4;minecraft:mining_fatigue;0;0", "0;10;4;minecraft:wither;0;0", "0;15;-20;minecraft:levitation;0;0"};
   private static final String PHEON_CATEGORY = "srparasites:haunter";
   public static float pheonHealthMultiplier = 1.0F;
   public static float pheonDamageMultiplier = 1.0F;
   public static float pheonArmorMultiplier = 1.0F;
   public static float pheonKDResistanceMultiplier = 1.0F;
   public static int pheonSpawnRate = 15;
   public static boolean pheonEnabled = true;
   public static String[] pheonLoot = new String[]{"srparasites:lurecomponent5;40;1;false"};
   public static String[] pheonOrbEffects = new String[]{"0;15;4;minecraft:hunger;0;0", "0;25;4;srparasites:needler;0;0", "0;15;4;minecraft:mining_fatigue;0;0", "0;10;4;minecraft:wither;0;0"};
   private static final String CRUXA_CATEGORY = "srparasites:cruxa";
   public static float cruxaHealthMultiplier = 1.0F;
   public static float cruxaDamageMultiplier = 1.0F;
   public static float cruxaArmorMultiplier = 1.0F;
   public static float cruxaKDResistanceMultiplier = 1.0F;
   public static int cruxaSpawnRate = 0;
   public static boolean cruxaEnabled = true;
   public static String[] cruxaLoot = new String[0];
   public static double cruxaDamageGain = 0.12D;
   public static int cruxaDamageCap = 10;
   private static final String HEED_CATEGORY = "srparasites:heed";
   public static float heedHealthMultiplier = 1.0F;
   public static float heedDamageMultiplier = 1.0F;
   public static float heedArmorMultiplier = 1.0F;
   public static float heedKDResistanceMultiplier = 1.0F;
   public static int heedSpawnRate = 0;
   public static boolean heedEnabled = true;
   public static String[] heedLoot = new String[0];
   private static final String LUM_CATEGORY = "srparasites:devourer";
   public static float lumHealthMultiplier = 1.0F;
   public static float lumDamageMultiplier = 1.0F;
   public static float lumArmorMultiplier = 1.0F;
   public static float lumKDResistanceMultiplier = 1.0F;
   public static float lumadaptedhealth = 0.0F;
   public static float lumadapteddamage = 0.0F;
   public static float lumadaptedarmor = 0.0F;
   public static float lumadaptedkdresistance = 0.0F;
   public static String[] lumadaptedloot = new String[]{"srparasites:ada_devourer_drop;40;4;true", "srparasites:lurecomponent4;30;3;false", "srparasites:bone;10;2;true"};
   public static int lumSpawnRate = 15;
   public static int lumASpawnRate = 0;
   public static boolean lumEnabled = true;
   public static String[] lumLoot = new String[]{"srparasites:ada_devourer_drop;20;2;true", "srparasites:lurecomponent3;20;2;false", "srparasites:bone;5;1;true"};
   private static final String NAK_CATEGORY = "srparasites:seizer";
   public static float nakHealthMultiplier = 1.0F;
   public static float nakDamageMultiplier = 1.0F;
   public static float nakArmorMultiplier = 1.0F;
   public static int nakSpawnRate = 0;
   public static boolean nakEnabled = true;
   public static String[] nakLoot = new String[0];
   private static final String POD1_CATEGORY_RSI = "srparasites:anc_pod";
   public static float pod1HealthMultiplier = 1.0F;
   public static float pod1DamageMultiplier = 1.0F;
   public static float pod1ArmorMultiplier = 1.0F;
   public static String[] pod1Loot = new String[0];
   public static String[] pod1Effects = new String[]{"10;-2;minecraft:saturation"};
   public static float venkrolHealthMultiplier = 1.0F;
   public static float venkrolDamageMultiplier = 1.0F;
   public static float venkrolArmorMultiplier = 1.0F;
   public static int venkrolSpawnrate = 0;
   public static int venkrolRange = 4;
   public static int venkrolCooldown = 10;
   public static int venkrollimit = 4;
   public static int venkrolTotalActiveMobs = 4;
   public static int venkrolBlockTime = 60;
   public static float venkrolBlockDamage = 1.0F;
   public static float venkrolBlockChance = 0.01F;
   public static String[] venkrolmoblist = new String[]{"ground;srparasites:rupter;1;1"};
   public static String[] venkrolLoot = new String[]{"srparasites:beckon_drop;10;3;true"};
   public static int venkrolCAMinimumV = 4;
   public static float venkrolCAExtraM = 0.5F;
   public static int venkrolRangeY = 2;
   public static float venkrolsiiHealthMultiplier = 1.0F;
   public static float venkrolsiiDamageMultiplier = 1.0F;
   public static float venkrolsiiArmorMultiplier = 1.0F;
   public static int venkrolsiiRange = 8;
   public static int venkrolsiiCooldown = 12;
   public static int venkrolsiilimit = 3;
   public static int venkrolsiiTotalActiveMobs = 9;
   public static int venkrolsiiBlockTime = 55;
   public static float venkrolsiiBlockDamage = 1.0F;
   public static float venkrolsiiBlockChance = 0.05F;
   public static String[] venkrolsiimoblist = new String[]{"ground;srparasites:sim_human;0.2;3", "ground;srparasites:sim_cow;0.2;3", "ground;srparasites:sim_sheep;0.2;3", "ground;srparasites:sim_wolf;0.2;3", "ground;srparasites:sim_bigspider;0.2;3", "ground;srparasites:sim_pig;0.2;3", "ground;srparasites:sim_villager;0.2;3", "ground;srparasites:sim_horse;0.2;3", "ground;srparasites:sim_bear;0.2;3", "ground;srparasites:sim_enderman;0.2;3", "ground;srparasites:rupter;0.1;1", "air;srparasites:carrier_flying;0.3;3"};
   public static String[] venkrolsiiLoot = new String[]{"srparasites:beckon_drop;30;7;true"};
   public static int venkrolsiiCAMinimumV = 4;
   public static float venkrolsiiCAExtraM = 0.5F;
   public static int venkrolsiiRangeY = 3;
   public static float venkrolsiiiHealthMultiplier = 1.0F;
   public static float venkrolsiiiDamageMultiplier = 1.0F;
   public static float venkrolsiiiArmorMultiplier = 1.0F;
   public static int venkrolsiiiRange = 16;
   public static int venkrolsiiiCooldown = 14;
   public static int venkrolsiiilimit = 2;
   public static int venkrolsiiiTotalActiveMobs = 12;
   public static int venkrolsiiiBlockTime = 50;
   public static float venkrolsiiiBlockDamage = 1.0F;
   public static float venkrolsiiiBlockChance = 0.1F;
   public static String[] venkrolsiiimoblist = new String[]{"ground;srparasites:pri_summoner;0.2;6", "ground;srparasites:pri_manducater;0.4;6", "ground;srparasites:pri_bolster;0.1;6", "ground;srparasites:pri_reeker;0.4;6", "ground;srparasites:pri_longarms;0.4;6", "ground;srparasites:pri_arachnida;0.4;6", "ground;srparasites:sim_human;0.2;3", "ground;srparasites:sim_cow;0.2;3", "ground;srparasites:sim_sheep;0.2;3", "ground;srparasites:sim_wolf;0.2;3", "ground;srparasites:sim_bigspider;0.2;3", "ground;srparasites:sim_pig;0.2;3", "ground;srparasites:sim_villager;0.2;3", "ground;srparasites:sim_horse;0.2;3", "ground;srparasites:sim_bear;0.2;3", "ground;srparasites:sim_enderman;0.2;3", "ground;srparasites:carrier_heavy;0.2;6", "ground;srparasites:rupter;0.1;1", "ground;srparasites:vigilante;0.2;6", "ground;srparasites:warden;0.2;6", "air;srparasites:overseer;0.2;6", "air;srparasites:bomber_light;0.2;6", "air;srparasites:carrier_flying;0.5;3", "air;srparasites:pri_yelloweye;0.5;6"};
   public static String[] venkrolsiiiLoot = new String[]{"srparasites:beckon_drop;50;10;true"};
   public static int venkrolsiiiCAMinimumV = 4;
   public static float venkrolsiiiCAExtraM = 0.35F;
   public static int venkrolsiiiRangeY = 4;
   public static float venkrolsivHealthMultiplier = 1.0F;
   public static float venkrolsivDamageMultiplier = 1.0F;
   public static float venkrolsivArmorMultiplier = 1.0F;
   public static int venkrolsivCooldown = 8;
   public static int venkrolsivlimit = 4;
   public static int venkrolsivTotalActiveMobs = 12;
   public static String[] venkrolsivmoblist = new String[]{"ground;srparasites:beckon_si;0.3;3", "ground;srparasites:beckon_sii;0.4;3", "ground;srparasites:beckon_siii;0.3;4"};
   public static String[] venkrolsivLoot = new String[]{"srparasites:beckon_drop;90;30;true"};
   public static int venkrolsivCAMinimumV = 4;
   public static float venkrolsivCAExtraM = 0.35F;
   public static float dodsiHealthMultiplier = 1.0F;
   public static float dodsiDamageMultiplier = 1.0F;
   public static float dodsiArmorMultiplier = 1.0F;
   public static int dodsiFollowRangeMult = 2;
   public static String[] dodsiLoot = new String[]{"srparasites:dispatcher_drop;10;7;true"};
   public static String[] dodsiEffects = new String[]{"100;minecraft:speed;1", "100;srparasites:debar;1", "200;srparasites:link;1", "100;srparasites:senses;1"};
   public static float dodsiiHealthMultiplier = 1.0F;
   public static float dodsiiDamageMultiplier = 1.0F;
   public static float dodsiiArmorMultiplier = 1.0F;
   public static int dodsiiFollowRangeMult = 2;
   public static String[] dodsiiLoot = new String[]{"srparasites:dispatcher_drop;45;15;true"};
   public static String[] dodsiiEffects = new String[]{"200;minecraft:speed;2", "200;minecraft:regeneration;2", "200;srparasites:debar;1", "300;srparasites:link;1", "200;srparasites:senses;2"};
   public static float dodsiiiHealthMultiplier = 1.0F;
   public static float dodsiiiDamageMultiplier = 1.0F;
   public static float dodsiiiArmorMultiplier = 1.0F;
   public static int dodsiiiFollowRangeMult = 2;
   public static String[] dodsiiiLoot = new String[]{"srparasites:dispatcher_drop;70;30;true"};
   public static String[] dodsiiiEffects = new String[]{"300;minecraft:speed;3", "300;minecraft:regeneration;3", "300;minecraft:strength;3", "300;srparasites:debar;1", "400;srparasites:link;2", "300;srparasites:senses;3"};
   public static float dodsivHealthMultiplier = 1.0F;
   public static float dodsivDamageMultiplier = 1.0F;
   public static float dodsivArmorMultiplier = 1.0F;
   public static int dodsivFollowRangeMult = 2;
   public static String[] dodsivLoot = new String[]{"srparasites:dispatcher_drop;100;70;true"};
   public static String[] dodsivEffects = new String[]{"400;minecraft:speed;4", "400;minecraft:regeneration;4", "400;minecraft:strength;4", "400;minecraft:absorption;4", "400;srparasites:debar;1", "500;srparasites:link;3", "400;srparasites:senses;4"};
   private static final String REINFORCEMENT_CATEGORY_BSI = "srparasites:beckon_i";
   private static final String REINFORCEMENT_CATEGORY_BSII = "srparasites:beckon_ii";
   private static final String REINFORCEMENT_CATEGORY_BSIII = "srparasites:beckon_iii";
   private static final String REINFORCEMENT_CATEGORY_BSIV = "srparasites:beckon_iv";
   private static final String REINFORCEMENT_CATEGORY_RSI = "srparasites:dispatcher_si";
   private static final String REINFORCEMENT_CATEGORY_RSII = "srparasites:dispatcher_sii";
   private static final String REINFORCEMENT_CATEGORY_RSIII = "srparasites:dispatcher_siii";
   private static final String REINFORCEMENT_CATEGORY_RSIV = "srparasites:dispatcher_siv";

   public SRPConfigMobs() {
   }

   private static void initGeneralMobsConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("configuration_mobs", "Mob configuration \nVersion:1.9.11\n \nEntities IDs: \nsrparasites:sim_bigspider \nsrparasites:sim_human \nsrparasites:sim_cow \nsrparasites:sim_sheep \nsrparasites:sim_wolf \nsrparasites:sim_pig \nsrparasites:sim_villager \nsrparasites:sim_adventurer \nsrparasites:sim_horse \nsrparasites:sim_bear \nsrparasites:sim_enderman \nsrparasites:sim_dragone \nsrparasites:sim_sheephead \nsrparasites:sim_wolfhead \nsrparasites:sim_cowhead \nsrparasites:sim_pighead \nsrparasites:sim_villagerhead \nsrparasites:sim_horsehead \nsrparasites:sim_humanhead \nsrparasites:sim_endermanhead \nsrparasites:sim_dragonehead \nsrparasites:sim_adventurerhead \nsrparasites:incompleteform_small \nsrparasites:incompleteform_medium \nsrparasites:carrier_heavy \nsrparasites:buglin \nsrparasites:carrier_flying \nsrparasites:rupter \nsrparasites:movingflesh \nsrparasites:worker \nsrparasites:mangler \nsrparasites:beckon_si \nsrparasites:beckon_sii \nsrparasites:beckon_siii \nsrparasites:beckon_siv \nsrparasites:dispatcherten \nsrparasites:dispatcher_si \nsrparasites:dispatcher_sii \nsrparasites:dispatcher_siii \nsrparasites:dispatcher_siv \nsrparasites:kyphosis \nsrparasites:sentry \nsrparasites:seizer \nsrparasites:host \nsrparasites:hostii \nsrparasites:heed \nsrparasites:crux \nsrparasites:thrall \nsrparasites:pri_longarms \nsrparasites:pri_manducater \nsrparasites:pri_reeker \nsrparasites:pri_yelloweye \nsrparasites:pri_summoner \nsrparasites:pri_bolster \nsrparasites:pri_arachnida \nsrparasites:pri_devourer \nsrparasites:ada_longarms \nsrparasites:ada_manducater \nsrparasites:ada_reeker \nsrparasites:ada_yelloweye \nsrparasites:ada_summoner \nsrparasites:ada_bolster \nsrparasites:ada_arachnida \nsrparasites:overseer \nsrparasites:vigilante \nsrparasites:warden \nsrparasites:bomber_light \nsrparasites:marauder \nsrparasites:monarch \nsrparasites:grunt \nsrparasites:bomber_heavy \nsrparasites:wraith \nsrparasites:bogle \nsrparasites:haunter \nsrparasites:carrier_colony \nsrparasites:succor \nsrparasites:anc_dreadnaut \nsrparasites:anc_overlord \nsrparasites:anc_pod \nsrparasites:anc_dreadnaut_ten \nsrparasites:pullingball \nsrparasites:webball \nsrparasites:spineball \nsrparasites:nadeball \nsrparasites:salivaball \nsrparasites:ballball \nsrparasites:ancientball \nsrparasites:homming \nsrparasites:antiinfestedblock \nsrparasites:biomassball \nsrparasites:missile \nsrparasites:balltall \nsrparasites:ballmall \nsrparasites:orb \nsrparasites:source \nsrparasites:remain \nsrparasites:bomb \nsrparasites:cloudtoxic \nsrparasites:biomass \nsrparasites:gore \nsrparasites:tendril \nsrparasites:scent \nsrparasites:wave \nsrparasites:nade \n");
   }

   private static void initShycoConfig(Configuration cfg) {
      String orb = " Ex. \"0;60;2;minecraft:fire_resistance;4;3\" Where: \n \"0\" mode of applying, 0 to non parasite mobs, 1 to self, 2 to parasite mobs, \n \"60\" potion duration in seconds, \n \"2\" potion amplifier, \n \"minecraft:fire_resistance\" potion itself, \n \"4\" if != 0 then amplifier += (mobs inside the orb / this value), \n \"3\" if != 0 then amplifier += (mobs inside the orb / this value) * 20. \n";
      cfg.addCustomCategoryComment("srparasites:longarms", "Longarms \n Base Health: " + SRPAttributes.SHYCO_HEALTH + " \n Base Damage: " + SRPAttributes.SHYCO_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.SHYCO_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.SHYCO_KD_RESISTANCE);
      shycoEnabled = cfg.getBoolean("Primitive Longarms Enabled", "srparasites:longarms", shycoEnabled, "Set to false if you want to disable Primitive Longarms.");
      shycoHealthMultiplier = cfg.getFloat("Primitive Longarms" + health, "srparasites:longarms", 1.0F, 0.01F, 100.0F, "Health multiplier for Primitive Longarms.");
      shycoDamageMultiplier = cfg.getFloat("Primitive Longarms" + damage, "srparasites:longarms", 1.0F, 0.01F, 100.0F, "Damage multiplier for Primitive Longarms.");
      shycoArmorMultiplier = cfg.getFloat("Primitive Longarms" + armor, "srparasites:longarms", 1.0F, 0.01F, 100.0F, "Armor multiplier for Primitive Longarms.");
      shycoKDResistanceMultiplier = cfg.getFloat("Primitive Longarms" + kd, "srparasites:longarms", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Primitive Longarms.");
      shycoSpawnRate = cfg.getInt("Primitive Longarms SpawnWeight", "srparasites:longarms", shycoSpawnRate, 0, 100, "Spawn rate for Primitive Longarms (This value is ignored if Evolution Phases are enabled, it has its own option).");
      shycoLoot = cfg.getStringList("Primitive Longarms Loot Table", "srparasites:longarms", shycoLoot, "Items you want the Primitive Longarms to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      shycoDamageIncreased = (double)cfg.getFloat("Primitive Longarms damage Increase", "srparasites:longarms", (float)shycoDamageIncreased, 0.0F, 1.0F, "For every 1% HP lost its damage will increase by this amount of its total damage (1 = 100%) for Primitive Longarms.");
      shycoOrbEffects = cfg.getStringList("Primitive Longarms Orb Effects", "srparasites:longarms", shycoOrbEffects, "Orb effects " + orb);
      shycoadaptedhealth = cfg.getFloat("Stage Adapted additional Health", "srparasites:longarms", shycoadaptedhealth, 0.01F, 100.0F, "Additional health for Adapted Longarms.");
      shycoadapteddamage = cfg.getFloat("Stage Adapted additional Damage", "srparasites:longarms", shycoadapteddamage, 0.01F, 100.0F, "Additional damage for Adapted Longarms.");
      shycoadaptedarmor = cfg.getFloat("Stage Adapted additional Armor", "srparasites:longarms", shycoadaptedarmor, 0.01F, 100.0F, "Additional armor for Adapted Longarms.");
      shycoadaptedkdresistance = cfg.getFloat("Stage Adapted additional Knockback Resistance", "srparasites:longarms", shycoadaptedkdresistance, 0.01F, 100.0F, "Additional Knockback Resistance for Adapted Longarms.");
      shycoadapteddamageincrease = (double)cfg.getFloat("Stage Adapted damage Increase", "srparasites:longarms", (float)shycoadapteddamageincrease, 0.0F, 1.0F, "For every 1% HP lost its damage will increase by this amount of its total damage (1 = 100%) for Adapted Longarms.");
      shycoadaptedloot = cfg.getStringList("Stage Adapted loot Table", "srparasites:longarms", shycoadaptedloot, "Items you want the Adapted Longarms to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      shycoASpawnRate = cfg.getInt("Stage Adapted spawnweight", "srparasites:longarms", shycoASpawnRate, 0, 100, "Spawn rate for Adapted Longarms (This value is ignored if Evolution Phases are enabled, it has its own option).");
      shycoadaptedOrbEffects = cfg.getStringList("Stage Adapted Orb Effects", "srparasites:longarms", shycoadaptedOrbEffects, "Orb effects " + orb);
   }

   private static void initdorpaConfig(Configuration cfg) {
      String mobTable = " Ex. \"minecraft:zombie;4;2\" Where: \n \"minecraft:zombie\" is the entity, \n \"4\" is the maximum number of entities, \n \"2\" is the minimum number of entities. \n";
      cfg.addCustomCategoryComment("srparasites:sim_bigspider", "Big Spider \n Base Health: " + SRPAttributes.DORPA_HEALTH + " \n Base Damage: " + SRPAttributes.DORPA_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.DORPA_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.DORPA_KD_RESISTANCE + " \n Base Ranged Damage: " + SRPAttributes.DORPA_RANGED_DAMAGE);
      dorpaEnabled = cfg.getBoolean("Big Spider Enabled", "srparasites:sim_bigspider", dorpaEnabled, "Set to false if you want to disable Big Spider.");
      dorpaHealthMultiplier = cfg.getFloat("Big Spider" + health, "srparasites:sim_bigspider", 1.0F, 0.01F, 100.0F, "Health multiplier for Big Spider.");
      dorpaDamageMultiplier = cfg.getFloat("Big Spider" + damage, "srparasites:sim_bigspider", 1.0F, 0.01F, 100.0F, "Damage multiplier for Big Spider.");
      dorpaArmorMultiplier = cfg.getFloat("Big Spider" + armor, "srparasites:sim_bigspider", 1.0F, 0.01F, 100.0F, "Armor multiplier for Big Spider.");
      dorpaKDResistanceMultiplier = cfg.getFloat("Big Spider" + kd, "srparasites:sim_bigspider", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Big Spider.");
      dorpaSpawnRate = cfg.getInt("Big Spider SpawnWeight", "srparasites:sim_bigspider", dorpaSpawnRate, 0, 100, "Spawn rate for Big Spider (This value is ignored if Evolution Phases are enabled, it has its own option).");
      dorpaWeb = cfg.getBoolean("Big Spider WebAttack", "srparasites:sim_bigspider", dorpaWeb, "Set to false if you want to disable Big Spider web attack blocks.");
      dorpaRangedAttackDamageMultiplier = cfg.getFloat("Big Spider Ranged" + damage, "srparasites:sim_bigspider", 1.0F, 0.0F, 100.0F, "Damage multiplier for Big Spider projectile attack (only if WebAttack is false).");
      dorpaLoot = cfg.getStringList("Big Spider Loot Table", "srparasites:sim_bigspider", new String[0], "Items you want the Big Spider to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      dorpamob = cfg.getString("Big Spider Mobs Inside", "srparasites:sim_bigspider", dorpamob, "Mob the Big Spider spawns when killed." + mobTable);
   }

   private static void initratholConfig(Configuration cfg) {
      String mobTable = " Ex. \"minecraft:zombie;4;2\" Where: \n \"minecraft:zombie\" is the entity, \n \"4\" is the maximum number of entities, \n \"2\" is the minimum number of entities. \n";
      cfg.addCustomCategoryComment("srparasites:carrier_heavy", "Heavy Carrier \n Base Health: " + SRPAttributes.RATHOL_HEALTH + " \n Base Damage: " + SRPAttributes.RATHOL_DAMAGE + " \n Base Armor: " + SRPAttributes.RATHOL_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.RATHOL_KD_RESISTANCE);
      ratholEnabled = cfg.getBoolean("Heavy Carrier Enabled", "srparasites:carrier_heavy", ratholEnabled, "Set to false if you want to disable Heavy Carrier.");
      ratholHealthMultiplier = cfg.getFloat("Heavy Carrier" + health, "srparasites:carrier_heavy", 1.0F, 0.01F, 100.0F, "Health multiplier for Heavy Carrier.");
      ratholDamageMultiplier = cfg.getFloat("Heavy Carrier" + damage, "srparasites:carrier_heavy", 1.0F, 0.01F, 100.0F, "Damage multiplier for Heavy Carrier.");
      ratholArmorMultiplier = cfg.getFloat("Heavy Carrier" + armor, "srparasites:carrier_heavy", 1.0F, 0.01F, 100.0F, "Armor multiplier for Heavy Carrier.");
      ratholKDResistanceMultiplier = cfg.getFloat("Heavy Carrier" + kd, "srparasites:carrier_heavy", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Heavy Carrier.");
      ratholSpawnRate = cfg.getInt("Heavy Carrier SpawnWeight", "srparasites:carrier_heavy", ratholSpawnRate, 0, 100, "Spawn rate for Heavy Carrier (This value is ignored if Evolution Phases are enabled, it has its own option).");
      ratholGriefing = cfg.getBoolean("Heavy Carrier Griefing", "srparasites:carrier_heavy", ratholGriefing, "Set to true if you want the Heavy Carrier to destroy blocks on explosion.");
      ratholLoot = cfg.getStringList("Heavy Carrier Loot Table", "srparasites:carrier_heavy", new String[0], "Items you want the Heavy Carrier to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      ratholMobs = cfg.getStringList("Heavy Carrier Mob Table", "srparasites:carrier_heavy", ratholMobs, "Mob list for Heavy Carrier." + mobTable);
   }

   private static void initemanaConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:yelloweye", "Yelloweye \n Base Health: " + SRPAttributes.EMANA_HEALTH + " \n Base Damage: " + SRPAttributes.EMANA_RANGED_DAMAGE + " \n Base Armor: " + SRPAttributes.EMANA_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.EMANA_KD_RESISTANCE + " \n Poison Duration in seconds: " + emanaPoisonDuration + " \n Poison Amplifier: " + emanaPoisonAmplifier);
      emanaEnabled = cfg.getBoolean("Primitive Yelloweye Enabled", "srparasites:yelloweye", emanaEnabled, "Set to false if you want to disable Primitive Yelloweye.");
      emanaHealthMultiplier = cfg.getFloat("Primitive Yelloweye" + health, "srparasites:yelloweye", 1.0F, 0.01F, 100.0F, "Health multiplier for Primitive Yelloweye.");
      emanaDamageMultiplier = cfg.getFloat("Primitive Yelloweye" + damage, "srparasites:yelloweye", 1.0F, 0.01F, 100.0F, "Damage multiplier for Primitive Yelloweye.");
      emanaArmorMultiplier = cfg.getFloat("Primitive Yelloweye" + armor, "srparasites:yelloweye", 1.0F, 0.01F, 100.0F, "Armor multiplier for Primitive Yelloweye.");
      emanaKDResistanceMultiplier = cfg.getFloat("Primitive Yelloweye" + kd, "srparasites:yelloweye", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Primitive Yelloweye.");
      emanaSpawnRate = cfg.getInt("Primitive Yelloweye SpawnWeight", "srparasites:yelloweye", emanaSpawnRate, 0, 100, "Spawn rate for Primitive Yelloweye (This value is ignored if Evolution Phases are enabled, it has its own option).");
      emanaPoisonDuration = cfg.getInt("Primitive Yelloweye Poison Duration", "srparasites:yelloweye", emanaPoisonDuration, 0, 100, "Poison duration in seconds for its projectile.");
      emanaPoisonAmplifier = cfg.getInt("Primitive Yelloweye Poison Amplifier", "srparasites:yelloweye", emanaPoisonAmplifier, 1, 100, "Poison amplifier for its projectile.");
      emanaLoot = cfg.getStringList("Primitive Yelloweye Loot Table", "srparasites:yelloweye", emanaLoot, "Items you want the Primitive Yelloweye to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      emanaMaxY = cfg.getInt("Primitive Yelloweye Flight Height Limit", "srparasites:yelloweye", emanaMaxY, 0, 256, "Number of blocks it can fly above the ground for Primitive Yelloweye (and Adapted version).");
      emanaGearD = (double)cfg.getFloat("Primitive Yelloweye Gear degrade", "srparasites:yelloweye", (float)emanaGearD, 0.0F, 1.0F, "How much a shot will degrade your gear (1=100%).");
      emanaadaptedhealth = cfg.getFloat("Stage Adapted additional Health", "srparasites:yelloweye", emanaadaptedhealth, 0.01F, 100.0F, "Additional health for Adapted Yelloweye.");
      emanaadapteddamage = cfg.getFloat("Stage Adapted additional Damage", "srparasites:yelloweye", emanaadapteddamage, 0.01F, 100.0F, "Additional damage for Adapted Yelloweye.");
      emanaadaptedarmor = cfg.getFloat("Stage Adapted additional Armor", "srparasites:yelloweye", emanaadaptedarmor, 0.01F, 100.0F, "Additional armor for Adapted Yelloweye.");
      emanaadaptedkdresistance = cfg.getFloat("Stage Adapted additional Knockback Resistance", "srparasites:yelloweye", emanaadaptedkdresistance, 0.01F, 100.0F, "Additional Knockback Resistance for Adapted Yelloweye.");
      emanaadaptedloot = cfg.getStringList("Stage Adapted loot Table", "srparasites:yelloweye", emanaadaptedloot, "Items you want the Adapted Yelloweye to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      emanaASpawnRate = cfg.getInt("Stage Adapted spawnweight", "srparasites:yelloweye", emanaASpawnRate, 0, 100, "Spawn rate for Adapted Yelloweye (This value is ignored if Evolution Phases are enabled, it has its own option).");
      emanaadaptedgeard = (double)cfg.getFloat("Stage Adapted gear degrade", "srparasites:yelloweye", (float)emanaadaptedgeard, 0.0F, 1.0F, "How much a shot will degrade your gear (1=100%).");
      emanaadaptedmelee = (double)cfg.getFloat("Stage Adapted melee", "srparasites:yelloweye", (float)emanaadaptedmelee, 0.0F, 1024.0F, "Damage it will do from melee for Stage Adapted.");
   }

   private static void initlodoConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:buglin", "Buglin \n Base Health: " + SRPAttributes.LODO_HEALTH + " \n Base Damage: " + SRPAttributes.LODO_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.LODO_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.LODO_KD_RESISTANCE);
      lodoEnabled = cfg.getBoolean("Buglin Enabled", "srparasites:buglin", lodoEnabled, "Set to false if you want to disable Buglin.");
      lodoHealthMultiplier = cfg.getFloat("Buglin" + health, "srparasites:buglin", 1.0F, 0.01F, 100.0F, "Health multiplier for Buglin.");
      lodoDamageMultiplier = cfg.getFloat("Buglin" + damage, "srparasites:buglin", 1.0F, 0.01F, 100.0F, "Damage multiplier for Buglin.");
      lodoArmorMultiplier = cfg.getFloat("Buglin" + armor, "srparasites:buglin", 1.0F, 0.01F, 100.0F, "Armor multiplier for Buglin.");
      lodoKDResistanceMultiplier = cfg.getFloat("Buglin" + kd, "srparasites:buglin", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Buglin.");
      lodoSpawnRate = cfg.getInt("Buglin SpawnWeight", "srparasites:buglin", lodoSpawnRate, 0, 100, "Spawn rate for Buglin (This value is ignored if Evolution Phases are enabled, it has its own option).");
      LodoLoot = cfg.getStringList("Buglin Loot Table", "srparasites:buglin", LodoLoot, "Items you want the Buglin to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
   }

   private static void inithullConfig(Configuration cfg) {
      String orb = " Ex. \"0;60;2;minecraft:fire_resistance;4;3\" Where: \n \"0\" mode of applying, 0 to non parasite mobs, 1 to self, 2 to parasite mobs, \n \"60\" potion duration in seconds, \n \"2\" potion amplifier, \n \"minecraft:fire_resistance\" potion itself, \n \"4\" if != 0 then amplifier += (mobs inside the orb / this value), \n \"3\" if != 0 then amplifier += (mobs inside the orb / this value) * 20. \n";
      cfg.addCustomCategoryComment("srparasites:manducater", "Manducater \n Base Health: " + SRPAttributes.HULL_HEALTH + " \n Base Damage: " + SRPAttributes.HULL_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.HULL_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.HULL_KD_RESISTANCE);
      hullEnabled = cfg.getBoolean("Primitive Manducater Enabled", "srparasites:manducater", hullEnabled, "Set to false if you want to disable Primitive Manducater.");
      hullHealthMultiplier = cfg.getFloat("Primitive Manducater" + health, "srparasites:manducater", 1.0F, 0.01F, 100.0F, "Health multiplier for Primitive Manducater.");
      hullDamageMultiplier = cfg.getFloat("Primitive Manducater" + damage, "srparasites:manducater", 1.0F, 0.01F, 100.0F, "Damage multiplier for Primitive Manducater.");
      hullArmorMultiplier = cfg.getFloat("Primitive Manducater" + armor, "srparasites:manducater", 1.0F, 0.01F, 100.0F, "Armor multiplier for Primitive Manducater.");
      hullKDResistanceMultiplier = cfg.getFloat("Primitive Manducater" + kd, "srparasites:manducater", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Primitive Manducater.");
      hullSpawnRate = cfg.getInt("Primitive Manducater SpawnWeight", "srparasites:manducater", hullSpawnRate, 0, 100, "Spawn rate for Primitive Manducater (This value is ignored if Evolution Phases are enabled, it has its own option).");
      hullLoot = cfg.getStringList("Primitive Manducater Loot Table", "srparasites:manducater", hullLoot, "Items you want the Primitive Manducater to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      hullNeededHealth = (double)cfg.getFloat("Primitive Manducater Needed Health", "srparasites:manducater", (float)hullNeededHealth, 0.0F, 1.0F, "Health (1 = 100%) needed to go invisible.");
      hullNeededTime = cfg.getFloat("Primitive Manducater Needed Time", "srparasites:manducater", hullNeededTime, 1.0F, 100.0F, "Time (seconds) they need to go invisible.");
      hullStealthDamageMultiplier = cfg.getFloat("Primitive Manducater Stealth" + damage, "srparasites:manducater", hullStealthDamageMultiplier, 0.01F, 100.0F, "Damage multiplier for Primitive Manducater when invisible.");
      hullOrbEffects = cfg.getStringList("Primitive Manducater Orb Effects", "srparasites:manducater", hullOrbEffects, "Orb effects " + orb);
      hulladaptedhealth = cfg.getFloat("Stage Adapted additional Health", "srparasites:manducater", hulladaptedhealth, 0.01F, 100.0F, "Additional health for Adapted Manducater.");
      hulladapteddamage = cfg.getFloat("Stage Adapted additional Damage", "srparasites:manducater", hulladapteddamage, 0.01F, 100.0F, "Additional damage for Adapted Manducater.");
      hulladaptedarmor = cfg.getFloat("Stage Adapted additional Armor", "srparasites:manducater", hulladaptedarmor, 0.01F, 100.0F, "Additional armor for Adapted Manducater.");
      hulladaptedkdresistance = cfg.getFloat("Stage Adapted additional Knockback Resistance", "srparasites:manducater", hulladaptedkdresistance, 0.01F, 100.0F, "Additional Knockback Resistance for Adapted Manducater.");
      hulladaptedloot = cfg.getStringList("Stage Adapted loot Table", "srparasites:manducater", hulladaptedloot, "Items you want the Adapted Manducater to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      hulladaptedneededhealth = (double)cfg.getFloat("Stage Adapted needed Health", "srparasites:manducater", (float)hulladaptedneededhealth, 0.0F, 1.0F, "Health (1 = 100%) needed to go invisible.");
      hulladaptedneededtime = cfg.getFloat("Stage Adapted needed Time", "srparasites:manducater", hulladaptedneededtime, 1.0F, 100.0F, "Time (seconds) they need to go invisible.");
      hulladaptedstealthdamage = cfg.getFloat("Stage Adapted additional Stealth Damage", "srparasites:manducater", hulladaptedstealthdamage, 0.01F, 100.0F, "Additional damage for Adapted Manducater when invisible.");
      hullASpawnRate = cfg.getInt("Stage Adapted spawnweight", "srparasites:manducater", hullASpawnRate, 0, 100, "Spawn rate for Adapted Manducater (This value is ignored if Evolution Phases are enabled, it has its own option).");
      hulladaptedOrbEffects = cfg.getStringList("Stage Adapted Orb Effects", "srparasites:manducater", hulladaptedOrbEffects, "Orb effects " + orb);
   }

   private static void initcanraConfig(Configuration cfg) {
      String Canraspawning = " Ex. \"minecraft:zombie;0.1;1\"  Where: \n \"minecraft:zombie\" is for the entity, \n \"0.1\" is for the chance to spawn, \n \"1\" is for the cost the entity has. \n";
      String orb = " Ex. \"0;60;2;minecraft:fire_resistance;4;3\" Where: \n \"0\" mode of applying, 0 to non parasite mobs, 1 to self, 2 to parasite mobs, \n \"60\" potion duration in seconds, \n \"2\" potion amplifier, \n \"minecraft:fire_resistance\" potion itself, \n \"4\" if != 0 then amplifier += (mobs inside the orb / this value), \n \"3\" if != 0 then amplifier += (mobs inside the orb / this value) * 20. \n";
      cfg.addCustomCategoryComment("srparasites:summoner", "Summoner \n Base Health: " + SRPAttributes.CANRA_HEALTH + " \n Base Damage: " + SRPAttributes.CANRA_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.CANRA_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.CANRA_KD_RESISTANCE);
      canraEnabled = cfg.getBoolean("Primitive Summoner Enabled", "srparasites:summoner", canraEnabled, "Set to false if you want to disable Primitive Summoner.");
      canraHealthMultiplier = cfg.getFloat("Primitive Summoner" + health, "srparasites:summoner", 1.0F, 0.01F, 100.0F, "Health multiplier for Primitive Summoner.");
      canraDamageMultiplier = cfg.getFloat("Primitive Summoner" + damage, "srparasites:summoner", 1.0F, 0.01F, 100.0F, "Damage multiplier for Primitive Summoner.");
      canraArmorMultiplier = cfg.getFloat("Primitive Summoner" + armor, "srparasites:summoner", 1.0F, 0.01F, 100.0F, "Armor multiplier for Primitive Summoner.");
      canraKDResistanceMultiplier = cfg.getFloat("Primitive Summoner" + kd, "srparasites:summoner", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Primitive Summoner.");
      canraSpawnRate = cfg.getInt("Primitive Summoner SpawnWeight", "srparasites:summoner", canraSpawnRate, 0, 100, "Spawn rate for Primitive Summoner (This value is ignored if Evolution Phases are enabled, it has its own option).");
      canraLoot = cfg.getStringList("Primitive Summoner Loot Table", "srparasites:summoner", canraLoot, "Items you want the Primitive Summoner to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      canraSummoningCooldown = cfg.getInt("Primitive Summoner summoning Cooldown", "srparasites:summoner", canraSummoningCooldown, 0, 100, "Summoning cooldown in seconds.");
      canraTotalActiveMobs = cfg.getInt("Primitive Summoner Total Active Mobs", "srparasites:summoner", canraTotalActiveMobs, 0, 100, "Number of total points used in mob spawning for Primitive Summoner");
      canraMobList = cfg.getStringList("Primitive Summoner Mob List", "srparasites:summoner", canraMobList, "Mob list for Primitive Summoner." + Canraspawning);
      canraLimit = cfg.getInt("Primitive Summoner Limit", "srparasites:summoner", canraLimit, 0, 10000, "Number of attacks before its cooldown for Primitive Summoner.");
      canraRemainPlus = cfg.getInt("Primitive Summoner Life Value", "srparasites:summoner", canraRemainPlus, 0, 10000, "Life value for Primitive Summoner.");
      canraRemainHealth = cfg.getFloat("Primitive Summoner Rebuilt Value", "srparasites:summoner", canraRemainHealth, 0.01F, 2.0F, "Health (%) that Primitive Summoner's rebuilt mobs will have.");
      canraOrbEffects = cfg.getStringList("Primitive Summoner Orb Effects", "srparasites:summoner", canraOrbEffects, "Orb effects " + orb);
      canraadaptedhealth = cfg.getFloat("Stage Adapted additional Health", "srparasites:summoner", canraadaptedhealth, 0.01F, 100.0F, "Additional health for Adapted Summoner.");
      canraadapteddamage = cfg.getFloat("Stage Adapted additional Damage", "srparasites:summoner", canraadapteddamage, 0.01F, 100.0F, "Additional damage for Adapted Summoner.");
      canraadaptedarmor = cfg.getFloat("Stage Adapted additional Armor", "srparasites:summoner", canraadaptedarmor, 0.01F, 100.0F, "Additional armor for Adapted Summoner.");
      canraadaptedkdresistance = cfg.getFloat("Stage Adapted additional Knockback Resistance", "srparasites:summoner", canraadaptedkdresistance, 0.01F, 100.0F, "Additional Knockback Resistance for Adapted Summoner.");
      canraadaptedsummoningcooldown = cfg.getInt("Stage Adapted summoning Cooldown", "srparasites:summoner", canraadaptedsummoningcooldown, 0, 100, "Summoning cooldown in seconds.");
      canraadaptedloot = cfg.getStringList("Stage Adapted loot Table", "srparasites:summoner", canraadaptedloot, "Items you want the Adapted Summoner to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      canraadaptedtotalactivemobs = cfg.getInt("Stage Adapted total active mobs", "srparasites:summoner", canraadaptedtotalactivemobs, 0, 100, "Number of total points used in mob spawning for Adapted Summoner.");
      canraadaptedmoblist = cfg.getStringList("Stage Adapted mob List", "srparasites:summoner", canraadaptedmoblist, "Mob list for Adapted Summoner." + Canraspawning);
      canraadaptedlimit = cfg.getInt("Stage Adapted limit", "srparasites:summoner", canraadaptedlimit, 0, 10000, "Number of attacks before its cooldown for Adapted Summoner.");
      canraASpawnRate = cfg.getInt("Stage Adapted spawnweight", "srparasites:summoner", canraASpawnRate, 0, 100, "Spawn rate for Adapted Summoner (This value is ignored if Evolution Phases are enabled, it has its own option).");
      canraadaptedremainplus = cfg.getInt("Stage Adapted Life Value", "srparasites:summoner", canraadaptedremainplus, 0, 10000, "Life value for Adapted Summoner.");
      canraadaptedremainhealth = cfg.getFloat("Stage Adapted Rebuilt Value", "srparasites:summoner", canraadaptedremainhealth, 0.01F, 2.0F, "Health (%) that Adapted Summoner's rebuilt mobs will have.");
      canraadaptedOrbEffects = cfg.getStringList("Stage Adapted Orb Effects", "srparasites:summoner", canraadaptedOrbEffects, "Orb effects " + orb);
   }

   private static void initalafhaConfig(Configuration cfg) {
      String Canraspawning = " Ex. \"minecraft:zombie;0.1;1\"  Where: \n \"minecraft:zombie\" is for the entity, \n \"0.1\" is for the chance to spawn, \n \"1\" is for the cost the entity has. \n";
      cfg.addCustomCategoryComment("srparasites:overseer", "Overseer \n Base Health: " + SRPAttributes.ALAFHA_HEALTH + " \n Base Damage: " + SRPAttributes.ALAFHA_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.ALAFHA_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.ALAFHA_KD_RESISTANCE);
      alafhaEnabled = cfg.getBoolean("Overseer Enabled", "srparasites:overseer", alafhaEnabled, "Set to false if you want to disable Overseer.");
      alafhaHealthMultiplier = cfg.getFloat("Overseer" + health, "srparasites:overseer", 1.0F, 0.01F, 100.0F, "Health multiplier for Overseer.");
      alafhaDamageMultiplier = cfg.getFloat("Overseer" + damage, "srparasites:overseer", 1.0F, 0.01F, 100.0F, "Damage multiplier for Overseer.");
      alafhaArmorMultiplier = cfg.getFloat("Overseer" + armor, "srparasites:overseer", 1.0F, 0.01F, 100.0F, "Armor multiplier for Overseer.");
      alafhaKDResistanceMultiplier = cfg.getFloat("Overseer" + kd, "srparasites:overseer", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Overseer.");
      alafhaSpawnRate = cfg.getInt("Overseer SpawnWeight", "srparasites:overseer", alafhaSpawnRate, 0, 100, "Spawn rate for Overseer (This value is ignored if Evolution Phases are enabled, it has its own option).");
      alafhaGriefing = cfg.getBoolean("Overseer Griefing", "srparasites:overseer", alafhaGriefing, "Set to true if you want its projectiles to destroy blocks.");
      alafhaLoot = cfg.getStringList("Overseer Loot Table", "srparasites:overseer", alafhaLoot, "Items you want the Overseer to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      alafhaMaxY = cfg.getInt("Overseer Flight Height Limit", "srparasites:overseer", alafhaMaxY, 0, 256, "Number of blocks it can fly above the ground for Overseer.");
      alafhaSummoningCooldown = cfg.getInt("Overseer summoning Cooldown", "srparasites:overseer", alafhaSummoningCooldown, 0, 100, "Summoning cooldown in seconds.");
      alafhaTotalActiveMobs = cfg.getInt("Overseer Total Active Mobs", "srparasites:overseer", alafhaTotalActiveMobs, 0, 100, "Number of total points used in mob spawning for Overseer");
      alafhaLimit = cfg.getInt("Overseer limit", "srparasites:overseer", alafhaLimit, 0, 10000, "Number of attacks before its cooldown for Overseer.");
      alafhaMobList = cfg.getStringList("Overseer mob List", "srparasites:overseer", alafhaMobList, "Mob list for Overseer." + Canraspawning);
      alafhaMelee = (double)cfg.getFloat("Overseer Melee", "srparasites:overseer", (float)alafhaMelee, 0.0F, 1024.0F, "Damage it will do from melee.");
   }

   private static void initnoglaConfig(Configuration cfg) {
      String orb = " Ex. \"0;60;2;minecraft:fire_resistance;4;3\" Where: \n \"0\" mode of applying, 0 to non parasite mobs, 1 to self, 2 to parasite mobs, \n \"60\" potion duration in seconds, \n \"2\" potion amplifier, \n \"minecraft:fire_resistance\" potion itself, \n \"4\" if != 0 then amplifier += (mobs inside the orb / this value), \n \"3\" if != 0 then amplifier += (mobs inside the orb / this value) * 20. \n";
      cfg.addCustomCategoryComment("srparasites:reeker", "Reeker \n Base Health: " + SRPAttributes.NOGLA_HEALTH + " \n Base Damage: " + SRPAttributes.NOGLA_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.NOGLA_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.NOGLA_KD_RESISTANCE);
      noglaEnabled = cfg.getBoolean("Primitive Reeker Enabled", "srparasites:reeker", noglaEnabled, "Set to false if you want to disable Primitive Reeker.");
      noglaHealthMultiplier = cfg.getFloat("Primitive Reeker" + health, "srparasites:reeker", 1.0F, 0.01F, 100.0F, "Health multiplier for Primitive Reeker.");
      noglaDamageMultiplier = cfg.getFloat("Primitive Reeker" + damage, "srparasites:reeker", 1.0F, 0.01F, 100.0F, "Damage multiplier for Primitive Reeker.");
      noglaArmorMultiplier = cfg.getFloat("Primitive Reeker" + armor, "srparasites:reeker", 1.0F, 0.01F, 100.0F, "Armor multiplier for Primitive Reeker.");
      noglaKDResistanceMultiplier = cfg.getFloat("Primitive Reeker" + kd, "srparasites:reeker", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Primitive Reeker.");
      noglaSpawnRate = cfg.getInt("Primitive Reeker SpawnWeight", "srparasites:reeker", noglaSpawnRate, 0, 100, "Spawn rate for Primitive Reeker (This value is ignored if Evolution Phases are enabled, it has its own option).");
      noglaLoot = cfg.getStringList("Primitive Reeker Loot Table", "srparasites:reeker", noglaLoot, "Items you want the Primitive Reeker to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      noglaOrbEffects = cfg.getStringList("Primitive Reeker Orb Effects", "srparasites:reeker", noglaOrbEffects, "Orb effects " + orb);
      noglaadaptedhealth = cfg.getFloat("Stage Adapted additional Health", "srparasites:reeker", noglaadaptedhealth, 0.01F, 100.0F, "Additional health for Adapted Reeker.");
      noglaadapteddamage = cfg.getFloat("Stage Adapted additional Damage", "srparasites:reeker", noglaadapteddamage, 0.01F, 100.0F, "Additional damage for Adapted Reeker.");
      noglaadaptedarmor = cfg.getFloat("Stage Adapted additional Armor", "srparasites:reeker", noglaadaptedarmor, 0.01F, 100.0F, "Additional armor for Adapted Reeker.");
      noglaadaptedkdresistance = cfg.getFloat("Stage Adapted additional Knockback Resistance", "srparasites:reeker", noglaadaptedkdresistance, 0.01F, 100.0F, "Additional Knockback Resistance for Adapted Reeker.");
      noglaadaptedloot = cfg.getStringList("Stage Adapted loot Table", "srparasites:reeker", noglaadaptedloot, "Items you want the Adapted Reeker to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      noglaASpawnRate = cfg.getInt("Stage Adapted spawnweight", "srparasites:reeker", noglaASpawnRate, 0, 100, "Spawn rate for Adapted Reeker (This value is ignored if Evolution Phases are enabled, it has its own option).");
      noglaadaptedOrbEffects = cfg.getStringList("Stage Adapted Orb Effects", "srparasites:reeker", noglaadaptedOrbEffects, "Orb effects " + orb);
   }

   private static void initbutholConfig(Configuration cfg) {
      String mobTable = " Ex. \"minecraft:zombie;4;2\" Where: \n \"minecraft:zombie\" is the entity, \n \"4\" is the maximum number of entities, \n \"2\" is the minimum number of entities. \n";
      cfg.addCustomCategoryComment("srparasites:carrier_flying", "Flying Carrier \n Base Health: " + SRPAttributes.BUTHOL_HEALTH + " \n Base Damage: " + SRPAttributes.BUTHOL_DAMAGE + " \n Base Armor: " + SRPAttributes.BUTHOL_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.BUTHOL_KD_RESISTANCE);
      butholEnabled = cfg.getBoolean("Flying Carrier Enabled", "srparasites:carrier_flying", butholEnabled, "Set to false if you want to disable Flying Carrier.");
      butholHealthMultiplier = cfg.getFloat("Flying Carrier" + health, "srparasites:carrier_flying", 1.0F, 0.01F, 100.0F, "Health multiplier for Flying Carrier.");
      butholDamageMultiplier = cfg.getFloat("Flying Carrier" + damage, "srparasites:carrier_flying", 1.0F, 0.01F, 100.0F, "Damage multiplier for Flying Carrier.");
      butholArmorMultiplier = cfg.getFloat("Flying Carrier" + armor, "srparasites:carrier_flying", 1.0F, 0.01F, 100.0F, "Armor multiplier for Flying Carrier.");
      butholKDResistanceMultiplier = cfg.getFloat("Flying Carrier" + kd, "srparasites:carrier_flying", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Flying Carrier.");
      butholSpawnRate = cfg.getInt("Flying Carrier SpawnWeight", "srparasites:carrier_flying", butholSpawnRate, 0, 100, "Spawn rate for Flying Carrier (This value is ignored if Evolution Phases are enabled, it has its own option).");
      ButholGriefing = cfg.getBoolean("Flying Carrier Griefing", "srparasites:carrier_flying", ButholGriefing, "Set to true if you want the Flying Carrier to destroy blocks on explosion.");
      butholLoot = cfg.getStringList("Flying Carrier Loot Table", "srparasites:carrier_flying", butholLoot, "Items you want the Flying Carrier to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      butholMaxY = cfg.getInt("Flying Carrier Flight Height Limit", "srparasites:carrier_flying", butholMaxY, 0, 256, "Number of blocks it can fly above the ground for Flying Carrier.");
      butholMobs = cfg.getStringList("Flying Carrier Mob Table", "srparasites:carrier_flying", butholMobs, "Mob list for Flying Carrier." + mobTable);
   }

   private static void initmudoConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:rupter", "Rupter \n Base Health: " + SRPAttributes.MUDO_HEALTH + " \n Base Damage: " + SRPAttributes.MUDO_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.MUDO_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.MUDO_KD_RESISTANCE);
      mudoEnabled = cfg.getBoolean("Rupter Enabled", "srparasites:rupter", mudoEnabled, "Set to false if you want to disable Rupter.");
      mudoHealthMultiplier = cfg.getFloat("Rupter" + health, "srparasites:rupter", 1.0F, 0.01F, 100.0F, "Health multiplier for Rupter.");
      mudoDamageMultiplier = cfg.getFloat("Rupter" + damage, "srparasites:rupter", 1.0F, 0.01F, 100.0F, "Damage multiplier for Rupter.");
      mudoArmorMultiplier = cfg.getFloat("Rupter" + armor, "srparasites:rupter", 1.0F, 0.01F, 100.0F, "Armor multiplier for Rupter.");
      mudoKDResistanceMultiplier = cfg.getFloat("Rupter" + kd, "srparasites:rupter", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Rupter.");
      mudoSpawnRate = cfg.getInt("Rupter SpawnWeight", "srparasites:rupter", mudoSpawnRate, 0, 100, "Spawn rate for Rupter (This value is ignored if Evolution Phases are enabled, it has its own option).");
      mudoLoot = cfg.getStringList("Rupter Loot Table", "srparasites:rupter", mudoLoot, "Items you want the Rupter to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      mudoAnimalAttacking = cfg.getBoolean("Rupter Passive Mob Attacking", "srparasites:rupter", mudoAnimalAttacking, "Set to false if you don't want the Rupter to attack passive mobs.");
      mudoMinDamage = cfg.getFloat("Rupter Minimum Damage", "srparasites:rupter", mudoMinDamage, 0.0F, 1024.0F, "Minimum Damage for Rupter.");
      mudoTunnelValue = cfg.getInt("Rupter Tunnel Cost", "srparasites:rupter", mudoTunnelValue, 0, 100, "Cost (killcount) of placing a Buglin Tunnel.");
      mudoTunnelPhase = (byte)cfg.getInt("Phase Rupter Tunnel", "srparasites:rupter", mudoTunnelPhase, 0, 9, "From this phase on, Rupters will not place Tunnels.");
      mudoMangler = cfg.getInt("Rupter To Mangler", "srparasites:rupter", mudoMangler, 0, 1000, "How many kills a Rupter needs to become a Mangler.");
   }

   private static void initnuuhConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:mangler", "Mangler \n Base Health: " + SRPAttributes.NUUH_HEALTH + " \n Base Damage: " + SRPAttributes.NUUH_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.NUUH_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.NUUH_KD_RESISTANCE);
      nuuhEnabled = cfg.getBoolean("Mangler Enabled", "srparasites:mangler", nuuhEnabled, "Set to false if you want to disable Mangler.");
      nuuhHealthMultiplier = cfg.getFloat("Mangler" + health, "srparasites:mangler", 1.0F, 0.01F, 100.0F, "Health multiplier for Mangler.");
      nuuhDamageMultiplier = cfg.getFloat("Mangler" + damage, "srparasites:mangler", 1.0F, 0.01F, 100.0F, "Damage multiplier for Mangler.");
      nuuhArmorMultiplier = cfg.getFloat("Mangler" + armor, "srparasites:mangler", 1.0F, 0.01F, 100.0F, "Armor multiplier for Mangler.");
      nuuhKDResistanceMultiplier = cfg.getFloat("Mangler" + kd, "srparasites:mangler", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Mangler.");
      nuuhSpawnRate = cfg.getInt("Mangler SpawnWeight", "srparasites:mangler", nuuhSpawnRate, 0, 100, "Spawn rate for Mangler (This value is ignored if Evolution Phases are enabled, it has its own option).");
      nuuhLoot = cfg.getStringList("Mangler Loot Table", "srparasites:mangler", nuuhLoot, "Items you want the Mangler to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      nuuhAnimalAttacking = cfg.getBoolean("Mangler Passive Mob Attacking", "srparasites:mangler", nuuhAnimalAttacking, "Set to false if you don't want the Mangler to attack passive mobs.");
      nuuhMinDamage = cfg.getFloat("Mangler Minimum Damage", "srparasites:mangler", nuuhMinDamage, 0.0F, 1024.0F, "Minimum Damage for Mangler.");
   }

   private static void inithostConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:host", "Host \n Base Health: " + SRPAttributes.HOST_HEALTH + " \n Base Damage: " + SRPAttributes.HOST_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.HOST_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.HOST_KD_RESISTANCE);
      hostEnabled = cfg.getBoolean("Host Enabled", "srparasites:host", hostEnabled, "Set to false if you want to disable Host.");
      hostHealthMultiplier = cfg.getFloat("Host" + health, "srparasites:host", 1.0F, 0.01F, 100.0F, "Health multiplier for Host.");
      hostDamageMultiplier = cfg.getFloat("Host" + damage, "srparasites:host", 1.0F, 0.01F, 100.0F, "Damage multiplier for Host.");
      hostArmorMultiplier = cfg.getFloat("Host" + armor, "srparasites:host", 1.0F, 0.01F, 100.0F, "Armor multiplier for Host.");
      hostKDResistanceMultiplier = cfg.getFloat("Host" + kd, "srparasites:host", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Host.");
      hostSpawnRate = cfg.getInt("Host SpawnWeight", "srparasites:host", hostSpawnRate, 0, 100, "Spawn rate for Host (This value is ignored if Evolution Phases are enabled, it has its own option).");
      hostLoot = cfg.getStringList("Host Loot Table", "srparasites:host", hostLoot, "Items you want the Host to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      hostDamage = cfg.getFloat("Host Bomb Damage", "srparasites:host", hostDamage, 1.0F, 1000.0F, "Damage of its bomb");
      hostSkele = cfg.getInt("Host From Skeletons Value", "srparasites:host", hostSkele, 0, 1000, "Number of kills an Assimilated Human/Villager need in order to become a Host.");
      hostHerd = cfg.getInt("Host To Herd", "srparasites:host", hostHerd, 0, 1000, "How many kills a Host needs to become a Herd.");
   }

   private static void initherdConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:herd", "Herd \n Base Health: " + SRPAttributes.HERD_HEALTH + " \n Base Damage: " + SRPAttributes.HERD_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.HERD_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.HERD_KD_RESISTANCE);
      herdEnabled = cfg.getBoolean("Herd Enabled", "srparasites:herd", herdEnabled, "Set to false if you want to disable Herd.");
      herdHealthMultiplier = cfg.getFloat("Herd" + health, "srparasites:herd", 1.0F, 0.01F, 100.0F, "Health multiplier for Herd.");
      herdDamageMultiplier = cfg.getFloat("Herd" + damage, "srparasites:herd", 1.0F, 0.01F, 100.0F, "Damage multiplier for Herd.");
      herdArmorMultiplier = cfg.getFloat("Herd" + armor, "srparasites:herd", 1.0F, 0.01F, 100.0F, "Armor multiplier for Herd.");
      herdKDResistanceMultiplier = cfg.getFloat("Herd" + kd, "srparasites:herd", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Herd.");
      herdSpawnRate = cfg.getInt("Herd SpawnWeight", "srparasites:herd", herdSpawnRate, 0, 100, "Spawn rate for Herd (This value is ignored if Evolution Phases are enabled, it has its own option).");
      herdLoot = cfg.getStringList("Herd Loot Table", "srparasites:herd", herdLoot, "Items you want the Herd to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      herdDamage = cfg.getFloat("Herd Bomb Damage", "srparasites:herd", herdDamage, 1.0F, 1000.0F, "Damage of its bomb");
   }

   private static void initthrallConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:thrall", "Thrall \n Base Health: " + SRPAttributes.THRALL_HEALTH + " \n Base Damage: " + SRPAttributes.THRALL_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.THRALL_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.THRALL_KD_RESISTANCE);
      thrallEnabled = cfg.getBoolean("Thrall Enabled", "srparasites:thrall", thrallEnabled, "Set to false if you want to disable Thrall.");
      thrallHealthMultiplier = cfg.getFloat("Thrall" + health, "srparasites:thrall", 1.0F, 0.01F, 100.0F, "Health multiplier for Thrall.");
      thrallDamageMultiplier = cfg.getFloat("Thrall" + damage, "srparasites:thrall", 1.0F, 0.01F, 100.0F, "Damage multiplier for Thrall.");
      thrallArmorMultiplier = cfg.getFloat("Thrall" + armor, "srparasites:thrall", 1.0F, 0.01F, 100.0F, "Armor multiplier for Thrall.");
      thrallKDResistanceMultiplier = cfg.getFloat("Thrall" + kd, "srparasites:thrall", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Thrall.");
      thrallSpawnRate = cfg.getInt("Thrall SpawnWeight", "srparasites:thrall", thrallSpawnRate, 0, 100, "Spawn rate for Thrall (This value is ignored if Evolution Phases are enabled, it has its own option).");
      thrallLoot = cfg.getStringList("Thrall Loot Table", "srparasites:thrall", thrallLoot, "Items you want the Thrall to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
   }

   private static void initinfbearConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:sim_bear", "Assimilated Bear \n Base Health: " + SRPAttributes.INFBEAR_HEALTH + " \n Base Damage: " + SRPAttributes.INFBEAR_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.INFBEAR_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.INFBEAR_KD_RESISTANCE);
      infbearEnabled = cfg.getBoolean("Assimilated Bear Enabled", "srparasites:sim_bear", infbearEnabled, "Set to false if you want to disable Assimilated Bear.");
      infbearHealthMultiplier = cfg.getFloat("Assimilated Bear" + health, "srparasites:sim_bear", 1.0F, 0.01F, 100.0F, "Health multiplier for Assimilated Bear.");
      infbearDamageMultiplier = cfg.getFloat("Assimilated Bear" + damage, "srparasites:sim_bear", 1.0F, 0.01F, 100.0F, "Damage multiplier for Assimilated Bear.");
      infbearArmorMultiplier = cfg.getFloat("Assimilated Bear" + armor, "srparasites:sim_bear", 1.0F, 0.01F, 100.0F, "Armor multiplier for Assimilated Bear.");
      infbearKDResistanceMultiplier = cfg.getFloat("Assimilated Bear" + kd, "srparasites:sim_bear", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Assimilated Bear.");
      infbearSpawnRate = cfg.getInt("Assimilated Bear SpawnWeight", "srparasites:sim_bear", infbearSpawnRate, 0, 100, "Spawn rate for Assimilated Bear (This value is ignored if Evolution Phases are enabled, it has its own option).");
      infbearLoot = cfg.getStringList("Assimilated Bear Loot Table", "srparasites:sim_bear", infbearLoot, "Items you want the Assimilated Bear to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
   }

   private static void initinfendermanConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:sim_enderman", "Assimilated Enderman \n Base Health: " + SRPAttributes.INFENDERMAN_HEALTH + " \n Base Damage: " + SRPAttributes.INFENDERMAN_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.INFENDERMAN_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.INFENDERMAN_KD_RESISTANCE);
      infendermanEnabled = cfg.getBoolean("Assimilated Enderman Enabled", "srparasites:sim_enderman", infendermanEnabled, "Set to false if you want to disable Assimilated Enderman.");
      infendermanHealthMultiplier = cfg.getFloat("Assimilated Enderman" + health, "srparasites:sim_enderman", 1.0F, 0.01F, 100.0F, "Health multiplier for Assimilated Enderman.");
      infendermanDamageMultiplier = cfg.getFloat("Assimilated Enderman" + damage, "srparasites:sim_enderman", 1.0F, 0.01F, 100.0F, "Damage multiplier for Assimilated Enderman.");
      infendermanArmorMultiplier = cfg.getFloat("Assimilated Enderman" + armor, "srparasites:sim_enderman", 1.0F, 0.01F, 100.0F, "Armor multiplier for Assimilated Enderman.");
      infendermanKDResistanceMultiplier = cfg.getFloat("Assimilated Enderman" + kd, "srparasites:sim_enderman", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Assimilated Enderman.");
      infendermanSpawnRate = cfg.getInt("Assimilated Enderman SpawnWeight", "srparasites:sim_enderman", infendermanSpawnRate, 0, 100, "Spawn rate for Assimilated Enderman (This value is ignored if Evolution Phases are enabled, it has its own option).");
      infendermanLoot = cfg.getStringList("Assimilated Enderman Loot Table", "srparasites:sim_enderman", infendermanLoot, "Items you want the Assimilated Enderman to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infendermanteleally = cfg.getBoolean("Assimilated Enderman Teleport Parasites", "srparasites:sim_enderman", infendermanteleally, "Set to false if you don't want the Assimilated Enderman to teleport other parasites.");
      infendermantelefreq = cfg.getInt("Assimilated Enderman Teleport Frequency", "srparasites:sim_enderman", infendermantelefreq, 0, 2147483640, "The lower the number, the more the Assimilated Enderman will teleport.");
      infendermanheadLoot = cfg.getStringList("Assimilated Enderman Head Loot Table", "srparasites:sim_enderman", infendermanheadLoot, "Items you want the Walking Head to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infendermanHealthHead = cfg.getFloat("Assimilated Enderman Head Health", "srparasites:sim_enderman", infendermanHealthHead, 0.01F, 100.0F, "Percentage of health it will have fron the host.");
      infendermanDamageHead = cfg.getFloat("Assimilated Enderman Head Damage", "srparasites:sim_enderman", infendermanDamageHead, 0.01F, 100.0F, "Percentage of damage it will have fron the host.");
      infendermanheadchance = cfg.getFloat("Assimilated Enderman Head Chance", "srparasites:sim_enderman", infendermanheadchance, 0.0F, 1.0F, "Chance (1 = 100%) to spawn a walking head when killed.");
      infendermanallyCool = cfg.getInt("Assimilated Enderman Ally Teleport Cooldown", "srparasites:sim_enderman", infendermanallyCool, 0, 2147483640, "Cooldown in ticks for teleporting allies.");
      infendermansaw = cfg.getInt("Assimilated Enderman First Sighting Teleport Cooldown", "srparasites:sim_enderman", infendermansaw, 0, 2147483640, "Cooldown in ticks to start teleporting when engaging an enemy.");
      infendermanTeleDamage = cfg.getFloat("Assimilated Enderman Ally Teleport Damage", "srparasites:sim_enderman", infendermanTeleDamage, 0.0F, 100.0F, "Damage to teleported allies.");
   }

   private static void initinfhumanConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:sim_human", "Assimilated Human \n Base Health: " + SRPAttributes.INFHUMAN_HEALTH + " \n Base Damage: " + SRPAttributes.INFHUMAN_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.INFHUMAN_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.INFHUMAN_KD_RESISTANCE);
      infhumanEnabled = cfg.getBoolean("Assimilated Human Enabled", "srparasites:sim_human", infhumanEnabled, "Set to false if you want to disable Assimilated Human.");
      infhumanHealthMultiplier = cfg.getFloat("Assimilated Human" + health, "srparasites:sim_human", 1.0F, 0.01F, 100.0F, "Health multiplier for Assimilated Human.");
      infhumanDamageMultiplier = cfg.getFloat("Assimilated Human" + damage, "srparasites:sim_human", 1.0F, 0.01F, 100.0F, "Damage multiplier for Assimilated Human.");
      infhumanArmorMultiplier = cfg.getFloat("Assimilated Human" + armor, "srparasites:sim_human", 1.0F, 0.01F, 100.0F, "Armor multiplier for Assimilated Human.");
      infhumanKDResistanceMultiplier = cfg.getFloat("Assimilated Human" + kd, "srparasites:sim_human", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Assimilated Human.");
      infhumanSpawnRate = cfg.getInt("Assimilated Human SpawnWeight", "srparasites:sim_human", infhumanSpawnRate, 0, 100, "Spawn rate for Assimilated Human (This value is ignored if Evolution Phases are enabled, it has its own option).");
      infhumanLoot = cfg.getStringList("Assimilated Human Loot Table", "srparasites:sim_human", infhumanLoot, "Items you want the Assimilated Human to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infhumanheadLoot = cfg.getStringList("Assimilated Human Head Loot Table", "srparasites:sim_human", infhumanheadLoot, "Items you want the Walking Head to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infhumanHealthHead = cfg.getFloat("Assimilated Human Head Health", "srparasites:sim_human", infhumanHealthHead, 0.01F, 100.0F, "Percentage of health it will have fron the host.");
      infhumanDamageHead = cfg.getFloat("Assimilated Human Head Damage", "srparasites:sim_human", infhumanDamageHead, 0.01F, 100.0F, "Percentage of damage it will have fron the host.");
      infhumanheadchance = cfg.getFloat("Assimilated Human Head Chance", "srparasites:sim_human", infhumanheadchance, 0.0F, 1.0F, "Chance (1 = 100%) to spawn a walking head when killed.");
   }

   private static void initinfcowConfig(Configuration cfg) {
      String mobTable = " Ex. \"minecraft:zombie;4;2\" Where: \n \"minecraft:zombie\" is the entity, \n \"4\" is the maximum number of entities, \n \"2\" is the minimum number of entities. \n";
      cfg.addCustomCategoryComment("srparasites:sim_cow", "Assimilated Cow \n Base Health: " + SRPAttributes.INFCOW_HEALTH + " \n Base Damage: " + SRPAttributes.INFCOW_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.INFCOW_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.INFCOW_KD_RESISTANCE);
      infcowEnabled = cfg.getBoolean("Assimilated Cow Enabled", "srparasites:sim_cow", infcowEnabled, "Set to false if you want to disable Assimilated Cow.");
      infcowHealthMultiplier = cfg.getFloat("Assimilated Cow" + health, "srparasites:sim_cow", 1.0F, 0.01F, 100.0F, "Health multiplier for Assimilated Cow.");
      infcowDamageMultiplier = cfg.getFloat("Assimilated Cow" + damage, "srparasites:sim_cow", 1.0F, 0.01F, 100.0F, "Damage multiplier for Assimilated Cow.");
      infcowArmorMultiplier = cfg.getFloat("Assimilated Cow" + armor, "srparasites:sim_cow", 1.0F, 0.01F, 100.0F, "Armor multiplier for Assimilated Cow.");
      infcowKDResistanceMultiplier = cfg.getFloat("Assimilated Cow" + kd, "srparasites:sim_cow", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Assimilated Cow.");
      infcowSpawnRate = cfg.getInt("Assimilated Cow SpawnWeight", "srparasites:sim_cow", infcowSpawnRate, 0, 100, "Spawn rate for Assimilated Cow (This value is ignored if Evolution Phases are enabled, it has its own option).");
      infcowLoot = cfg.getStringList("Assimilated Cow Loot Table", "srparasites:sim_cow", infcowLoot, "Items you want the Assimilated Cow to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infcowmob = cfg.getString("Assimilated Cow Mobs Inside", "srparasites:sim_cow", infcowmob, "Mob the Assimilated Cow spawns when killed." + mobTable);
      infcowheadLoot = cfg.getStringList("Assimilated Cow Head Loot Table", "srparasites:sim_cow", infcowheadLoot, "Items you want the Walking Head to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infcowHealthHead = cfg.getFloat("Assimilated Cow Head Health", "srparasites:sim_cow", infcowHealthHead, 0.01F, 100.0F, "Percentage of health it will have fron the host.");
      infcowDamageHead = cfg.getFloat("Assimilated Cow Head Damage", "srparasites:sim_cow", infcowDamageHead, 0.01F, 100.0F, "Percentage of damage it will have fron the host.");
      infcowheadchance = cfg.getFloat("Assimilated Cow Head Chance", "srparasites:sim_cow", infcowheadchance, 0.0F, 1.0F, "Chance (1 = 100%) to spawn a walking head when killed.");
   }

   private static void initinfsheepConfig(Configuration cfg) {
      String mobTable = " Ex. \"minecraft:zombie;4;2\" Where: \n \"minecraft:zombie\" is the entity, \n \"4\" is the maximum number of entities, \n \"2\" is the minimum number of entities. \n";
      cfg.addCustomCategoryComment("srparasites:sim_sheep", "Assimilated Sheep \n Base Health: " + SRPAttributes.INFSHEEP_HEALTH + " \n Base Damage: " + SRPAttributes.INFSHEEP_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.INFSHEEP_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.INFSHEEP_KD_RESISTANCE);
      infsheepEnabled = cfg.getBoolean("Assimilated Sheep Enabled", "srparasites:sim_sheep", infsheepEnabled, "Set to false if you want to disable Assimilated Sheep.");
      infsheepHealthMultiplier = cfg.getFloat("Assimilated Sheep" + health, "srparasites:sim_sheep", 1.0F, 0.01F, 100.0F, "Health multiplier for Assimilated Sheep.");
      infsheepDamageMultiplier = cfg.getFloat("Assimilated Sheep" + damage, "srparasites:sim_sheep", 1.0F, 0.01F, 100.0F, "Damage multiplier for Assimilated Sheep.");
      infsheepArmorMultiplier = cfg.getFloat("Assimilated Sheep" + armor, "srparasites:sim_sheep", 1.0F, 0.01F, 100.0F, "Armor multiplier for Assimilated Sheep.");
      infsheepKDResistanceMultiplier = cfg.getFloat("Assimilated Sheep" + kd, "srparasites:sim_sheep", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Assimilated Sheep.");
      infsheepSpawnRate = cfg.getInt("Assimilated Sheep SpawnWeight", "srparasites:sim_sheep", infsheepSpawnRate, 0, 100, "Spawn rate for Assimilated Sheep (This value is ignored if Evolution Phases are enabled, it has its own option).");
      infsheepLoot = cfg.getStringList("Assimilated Sheep Loot Table", "srparasites:sim_sheep", infsheepLoot, "Items you want the Assimilated Sheep to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infsheepmob = cfg.getString("Assimilated Sheep Mobs Inside", "srparasites:sim_sheep", infsheepmob, "Mob the Assimilated Sheep spawns when killed." + mobTable);
      infsheepheadLoot = cfg.getStringList("Assimilated Sheep Head Loot Table", "srparasites:sim_sheep", infsheepheadLoot, "Items you want the Walking Head to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infsheepHealthHead = cfg.getFloat("Assimilated Sheep Head Health", "srparasites:sim_sheep", infsheepHealthHead, 0.01F, 100.0F, "Percentage of health it will have fron the host.");
      infsheepDamageHead = cfg.getFloat("Assimilated Sheep Head Damage", "srparasites:sim_sheep", infsheepDamageHead, 0.01F, 100.0F, "Percentage of damage it will have fron the host.");
      infsheepheadchance = cfg.getFloat("Assimilated Sheep Head Chance", "srparasites:sim_sheep", infsheepheadchance, 0.0F, 1.0F, "Chance (1 = 100%) to spawn a walking head when killed.");
   }

   private static void initinfwolfConfig(Configuration cfg) {
      String mobTable = " Ex. \"minecraft:zombie;4;2\" Where: \n \"minecraft:zombie\" is the entity, \n \"4\" is the maximum number of entities, \n \"2\" is the minimum number of entities. \n";
      cfg.addCustomCategoryComment("srparasites:sim_wolf", "Assimilated Wolf \n Base Health: " + SRPAttributes.INFWOLF_HEALTH + " \n Base Damage: " + SRPAttributes.INFWOLF_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.INFWOLF_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.INFWOLF_KD_RESISTANCE);
      infwolfEnabled = cfg.getBoolean("Assimilated Wolf Enabled", "srparasites:sim_wolf", infwolfEnabled, "Set to false if you want to disable Assimilated Wolf.");
      infwolfHealthMultiplier = cfg.getFloat("Assimilated Wolf" + health, "srparasites:sim_wolf", 1.0F, 0.01F, 100.0F, "Health multiplier for Assimilated Wolf.");
      infwolfDamageMultiplier = cfg.getFloat("Assimilated Wolf" + damage, "srparasites:sim_wolf", 1.0F, 0.01F, 100.0F, "Damage multiplier for Assimilated Wolf.");
      infwolfArmorMultiplier = cfg.getFloat("Assimilated Wolf" + armor, "srparasites:sim_wolf", 1.0F, 0.01F, 100.0F, "Armor multiplier for Assimilated Wolf.");
      infwolfKDResistanceMultiplier = cfg.getFloat("Assimilated Wolf" + kd, "srparasites:sim_wolf", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Assimilated Wolf.");
      infwolfSpawnRate = cfg.getInt("Assimilated Wolf SpawnWeight", "srparasites:sim_wolf", infwolfSpawnRate, 0, 100, "Spawn rate for Assimilated Wolf (This value is ignored if Evolution Phases are enabled, it has its own option).");
      infwolfLoot = cfg.getStringList("Assimilated Wolf Loot Table", "srparasites:sim_wolf", infwolfLoot, "Items you want the Assimilated Wolf to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infwolfmob = cfg.getString("Assimilated Wolf Mobs Inside", "srparasites:sim_wolf", infwolfmob, "Mob the Assimilated Wolf spawns when killed." + mobTable);
      infwolfheadLoot = cfg.getStringList("Assimilated Wolf Head Loot Table", "srparasites:sim_wolf", infwolfheadLoot, "Items you want the Walking Head to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infwolfHealthHead = cfg.getFloat("Assimilated Wolf Head Health", "srparasites:sim_wolf", infwolfHealthHead, 0.01F, 100.0F, "Percentage of health it will have fron the host.");
      infwolfDamageHead = cfg.getFloat("Assimilated Wolf Head Damage", "srparasites:sim_wolf", infwolfDamageHead, 0.01F, 100.0F, "Percentage of damage it will have fron the host.");
      infwolfheadchance = cfg.getFloat("Assimilated Wolf Head Chance", "srparasites:sim_wolf", infwolfheadchance, 0.0F, 1.0F, "Chance (1 = 100%) to spawn a walking head when killed.");
   }

   private static void initinfpigConfig(Configuration cfg) {
      String mobTable = " Ex. \"minecraft:zombie;4;2\" Where: \n \"minecraft:zombie\" is the entity, \n \"4\" is the maximum number of entities, \n \"2\" is the minimum number of entities. \n";
      cfg.addCustomCategoryComment("srparasites:sim_pig", "Assimilated Pig \n Base Health: " + SRPAttributes.INFPIG_HEALTH + " \n Base Damage: " + SRPAttributes.INFPIG_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.INFPIG_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.INFPIG_KD_RESISTANCE);
      infpigEnabled = cfg.getBoolean("Assimilated Pig Enabled", "srparasites:sim_pig", infpigEnabled, "Set to false if you want to disable Assimilated Pig.");
      infpigHealthMultiplier = cfg.getFloat("Assimilated Pig" + health, "srparasites:sim_pig", 1.0F, 0.01F, 100.0F, "Health multiplier for Assimilated Pig.");
      infpigDamageMultiplier = cfg.getFloat("Assimilated Pig" + damage, "srparasites:sim_pig", 1.0F, 0.01F, 100.0F, "Damage multiplier for Assimilated Pig.");
      infpigArmorMultiplier = cfg.getFloat("Assimilated Pig" + armor, "srparasites:sim_pig", 1.0F, 0.01F, 100.0F, "Armor multiplier for Assimilated Pig.");
      infpigKDResistanceMultiplier = cfg.getFloat("Assimilated Pig" + kd, "srparasites:sim_pig", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Assimilated Pig.");
      infpigSpawnRate = cfg.getInt("Assimilated Pig SpawnWeight", "srparasites:sim_pig", infpigSpawnRate, 0, 100, "Spawn rate for Assimilated Pig (This value is ignored if Evolution Phases are enabled, it has its own option).");
      infpigLoot = cfg.getStringList("Assimilated Pig Loot Table", "srparasites:sim_pig", infpigLoot, "Items you want the Assimilated Pig to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infpigmob = cfg.getString("Assimilated Pig Mobs Inside", "srparasites:sim_pig", infpigmob, "Mob the Assimilated Pig spawns when killed." + mobTable);
      infpigheadLoot = cfg.getStringList("Assimilated Pig Head Loot Table", "srparasites:sim_pig", infpigheadLoot, "Items you want the Walking Head to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infpigHealthHead = cfg.getFloat("Assimilated Pig Head Health", "srparasites:sim_pig", infpigHealthHead, 0.01F, 100.0F, "Percentage of health it will have fron the host.");
      infpigDamageHead = cfg.getFloat("Assimilated Pig Head Damage", "srparasites:sim_pig", infpigDamageHead, 0.01F, 100.0F, "Percentage of damage it will have fron the host.");
      infpigheadchance = cfg.getFloat("Assimilated Pig Head Chance", "srparasites:sim_pig", infpigheadchance, 0.0F, 1.0F, "Chance (1 = 100%) to spawn a walking head when killed.");
   }

   private static void initinfvillagerConfig(Configuration cfg) {
      String mobTable = " Ex. \"minecraft:zombie;4;2\" Where: \n \"minecraft:zombie\" is the entity, \n \"4\" is the maximum number of entities, \n \"2\" is the minimum number of entities. \n";
      cfg.addCustomCategoryComment("srparasites:sim_villager", "Assimilated Villager \n Base Health: " + SRPAttributes.INFVILLAGER_HEALTH + " \n Base Damage: " + SRPAttributes.INFVILLAGER_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.INFVILLAGER_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.INFVILLAGER_KD_RESISTANCE);
      infvillagerEnabled = cfg.getBoolean("Assimilated Villager Enabled", "srparasites:sim_villager", infvillagerEnabled, "Set to false if you want to disable Assimilated Villager.");
      infvillagerHealthMultiplier = cfg.getFloat("Assimilated Villager" + health, "srparasites:sim_villager", 1.0F, 0.01F, 100.0F, "Health multiplier for Assimilated Villager.");
      infvillagerDamageMultiplier = cfg.getFloat("Assimilated Villager" + damage, "srparasites:sim_villager", 1.0F, 0.01F, 100.0F, "Damage multiplier for Assimilated Villager.");
      infvillagerArmorMultiplier = cfg.getFloat("Assimilated Villager" + armor, "srparasites:sim_villager", 1.0F, 0.01F, 100.0F, "Armor multiplier for Assimilated Villager.");
      infvillagerKDResistanceMultiplier = cfg.getFloat("Assimilated Villager" + kd, "srparasites:sim_villager", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Assimilated Villager.");
      infvillagerSpawnRate = cfg.getInt("Assimilated Villager SpawnWeight", "srparasites:sim_villager", infvillagerSpawnRate, 0, 100, "Spawn rate for Assimilated Villager (This value is ignored if Evolution Phases are enabled, it has its own option).");
      infvillagerLoot = cfg.getStringList("Assimilated Villager Loot Table", "srparasites:sim_villager", infvillagerLoot, "Items you want the Assimilated Villager to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infvillagermob = cfg.getString("Assimilated Villager Mobs Inside", "srparasites:sim_villager", infvillagermob, "Mob the Assimilated Villager spawns when killed." + mobTable);
      infvillagerheadLoot = cfg.getStringList("Assimilated Villager Head Loot Table", "srparasites:sim_villager", infvillagerheadLoot, "Items you want the Walking Head to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infvillagerHealthHead = cfg.getFloat("Assimilated Villager Head Health", "srparasites:sim_villager", infvillagerHealthHead, 0.01F, 100.0F, "Percentage of health it will have fron the host.");
      infvillagerDamageHead = cfg.getFloat("Assimilated Villager Head Damage", "srparasites:sim_villager", infvillagerDamageHead, 0.01F, 100.0F, "Percentage of damage it will have fron the host.");
      infvillagerheadchance = cfg.getFloat("Assimilated Villager Head Chance", "srparasites:sim_villager", infvillagerheadchance, 0.0F, 1.0F, "Chance (1 = 100%) to spawn a walking head when killed.");
   }

   private static void initinfhorseConfig(Configuration cfg) {
      String mobTable = " Ex. \"minecraft:zombie;4;2\" Where: \n \"minecraft:zombie\" is the entity, \n \"4\" is the maximum number of entities, \n \"2\" is the minimum number of entities. \n";
      cfg.addCustomCategoryComment("srparasites:sim_horse", "Assimilated Horse \n Base Health: " + SRPAttributes.INFHORSE_HEALTH + " \n Base Damage: " + SRPAttributes.INFHORSE_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.INFHORSE_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.INFHORSE_KD_RESISTANCE);
      infhorseEnabled = cfg.getBoolean("Assimilated Horse Enabled", "srparasites:sim_horse", infhorseEnabled, "Set to false if you want to disable Assimilated Horse.");
      infhorseHealthMultiplier = cfg.getFloat("Assimilated Horse" + health, "srparasites:sim_horse", 1.0F, 0.01F, 100.0F, "Health multiplier for Assimilated Horse.");
      infhorseDamageMultiplier = cfg.getFloat("Assimilated Horse" + damage, "srparasites:sim_horse", 1.0F, 0.01F, 100.0F, "Damage multiplier for Assimilated Horse.");
      infhorseArmorMultiplier = cfg.getFloat("Assimilated Horse" + armor, "srparasites:sim_horse", 1.0F, 0.01F, 100.0F, "Armor multiplier for Assimilated Horse.");
      infhorseKDResistanceMultiplier = cfg.getFloat("Assimilated Horse" + kd, "srparasites:sim_horse", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Assimilated Horse.");
      infhorseSpawnRate = cfg.getInt("Assimilated Horse SpawnWeight", "srparasites:sim_horse", infhorseSpawnRate, 0, 100, "Spawn rate for Assimilated Horse (This value is ignored if Evolution Phases are enabled, it has its own option).");
      infhorseLoot = cfg.getStringList("Assimilated Horse Loot Table", "srparasites:sim_horse", infhorseLoot, "Items you want the Assimilated Horse to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infhorsemob = cfg.getString("Assimilated Horse Mobs Inside", "srparasites:sim_horse", infhorsemob, "Mob the Assimilated Horse spawns when killed." + mobTable);
      infhorseExplotionMult = cfg.getFloat("Assimilated Horse Explotion Multiplier", "srparasites:sim_horse", infhorseExplotionMult, 1.0F, 100.0F, "Explotion damage will take base Damage and multiply it by this value.");
      infhorseheadLoot = cfg.getStringList("Assimilated Horse Head Loot Table", "srparasites:sim_horse", infhorseheadLoot, "Items you want the Walking Head to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infhorseHealthHead = cfg.getFloat("Assimilated Horse Head Health", "srparasites:sim_horse", infhorseHealthHead, 0.01F, 100.0F, "Percentage of health it will have fron the host.");
      infhorseDamageHead = cfg.getFloat("Assimilated Horse Head Damage", "srparasites:sim_horse", infhorseDamageHead, 0.01F, 100.0F, "Percentage of damage it will have fron the host.");
      infhorseheadchance = cfg.getFloat("Assimilated Horse Head Chance", "srparasites:sim_horse", infhorseheadchance, 0.0F, 1.0F, "Chance (1 = 100%) to spawn a walking head when killed.");
   }

   private static void initinfadventurerConfig(Configuration cfg) {
      String mobTable = " Ex. \"minecraft:zombie;4;2\" Where: \n \"minecraft:zombie\" is the entity, \n \"4\" is the maximum number of entities, \n \"2\" is the minimum number of entities. \n";
      cfg.addCustomCategoryComment("srparasites:sim_adventurer", "Assimilated Adventurer \n Base Health: " + SRPAttributes.INFADVENTURER_HEALTH + " \n Base Damage: " + SRPAttributes.INFADVENTURER_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.INFADVENTURER_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.INFADVENTURER_KD_RESISTANCE);
      infadventurerEnabled = cfg.getBoolean("Assimilated Adventurer Enabled", "srparasites:sim_adventurer", infadventurerEnabled, "Set to false if you want to disable Assimilated Adventurer.");
      infadventurerHealthMultiplier = cfg.getFloat("Assimilated Adventurer" + health, "srparasites:sim_adventurer", 1.0F, 0.01F, 100.0F, "Health multiplier for Assimilated Adventurer.");
      infadventurerDamageMultiplier = cfg.getFloat("Assimilated Adventurer" + damage, "srparasites:sim_adventurer", 1.0F, 0.01F, 100.0F, "Damage multiplier for Assimilated Adventurer.");
      infadventurerArmorMultiplier = cfg.getFloat("Assimilated Adventurer" + armor, "srparasites:sim_adventurer", 1.0F, 0.01F, 100.0F, "Armor multiplier for Assimilated Adventurer.");
      infadventurerKDResistanceMultiplier = cfg.getFloat("Assimilated Adventurer" + kd, "srparasites:sim_adventurer", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Assimilated Adventurer.");
      infadventurerSpawnRate = cfg.getInt("Assimilated Adventurer SpawnWeight", "srparasites:sim_adventurer", infadventurerSpawnRate, 0, 100, "Spawn rate for Assimilated Adventurer (This value is ignored if Evolution Phases are enabled, it has its own option).");
      infadventurerThrall = cfg.getInt("Assimilated Adventurer To Thrall", "srparasites:sim_adventurer", infadventurerThrall, 0, 1000, "How many kills an Assimilated Adventurer needs to become a Thrall.");
      infadventurerLoot = cfg.getStringList("Assimilated Adventurer Loot Table", "srparasites:sim_adventurer", infadventurerLoot, "Items you want the Assimilated Adventurer to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infadventurerSpawnBy = cfg.getBoolean("Assimilated Adventurer Spawned By Players", "srparasites:sim_adventurer", infadventurerSpawnBy, "Set to false if you want to disable the spawn of Assimilated Adventurers through players.");
      infadventurermob = cfg.getString("Assimilated Adventurer Mobs Inside", "srparasites:sim_adventurer", infadventurermob, "Mob the Assimilated Adventurer spawns when killed." + mobTable);
      infadventurerheadLoot = cfg.getStringList("Assimilated Adventurer Head Loot Table", "srparasites:sim_adventurer", infadventurerheadLoot, "Items you want the Walking Head to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infadventurerHealthHead = cfg.getFloat("Assimilated Adventurer Head Health", "srparasites:sim_adventurer", infadventurerHealthHead, 0.01F, 100.0F, "Percentage of health it will have fron the host.");
      infadventurerDamageHead = cfg.getFloat("Assimilated Adventurer Head Damage", "srparasites:sim_adventurer", infadventurerDamageHead, 0.01F, 100.0F, "Percentage of damage it will have fron the host.");
      infadventurerheadchance = cfg.getFloat("Assimilated Adventurer Head Chance", "srparasites:sim_adventurer", infadventurerheadchance, 0.0F, 1.0F, "Chance (1 = 100%) to spawn a walking head when killed.");
   }

   private static void initINFDRAGONEConfig(Configuration cfg) {
      String mobTable = " Ex. \"minecraft:zombie;4;2\" Where: \n \"minecraft:zombie\" is the entity, \n \"4\" is the maximum number of entities, \n \"2\" is the minimum number of entities. \n";
      cfg.addCustomCategoryComment("srparasites:sim_dragone", "Assimilated Ender Dragon \n Base Health: " + SRPAttributes.INFDRAGONE_HEALTH + " \n Base Damage: " + SRPAttributes.INFDRAGONE_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.INFDRAGONE_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.INFDRAGONE_KD_RESISTANCE);
      infdragoneEnabled = cfg.getBoolean("Assimilated Ender Dragon Enabled", "srparasites:sim_dragone", infdragoneEnabled, "Set to false if you want to disable Assimilated Ender Dragon.");
      infdragoneHealthMultiplier = cfg.getFloat("Assimilated Ender Dragon" + health, "srparasites:sim_dragone", 1.0F, 0.01F, 100.0F, "Health multiplier for Assimilated Ender Dragon.");
      infdragoneDamageMultiplier = cfg.getFloat("Assimilated Ender Dragon" + damage, "srparasites:sim_dragone", 1.0F, 0.01F, 100.0F, "Damage multiplier for Assimilated Ender Dragon.");
      infdragoneArmorMultiplier = cfg.getFloat("Assimilated Ender Dragon" + armor, "srparasites:sim_dragone", 1.0F, 0.01F, 100.0F, "Armor multiplier for Assimilated Ender Dragon.");
      infdragoneKDResistanceMultiplier = cfg.getFloat("Assimilated Ender Dragon" + kd, "srparasites:sim_dragone", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Assimilated Ender Dragon.");
      infdragoneSpawnRate = cfg.getInt("Assimilated Ender Dragon SpawnWeight", "srparasites:sim_dragone", infdragoneSpawnRate, 0, 100, "Spawn rate for Assimilated Ender Dragon (This value is ignored if Evolution Phases are enabled, it has its own option).");
      infdragoneLoot = cfg.getStringList("Assimilated Ender Dragon Loot Table", "srparasites:sim_dragone", new String[0], "Items you want the Assimilated Ender Dragon to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infdragoneRangeDamageMultiplier = cfg.getFloat("Assimilated Ender Dragon Range" + damage, "srparasites:sim_dragone", infdragoneRangeDamageMultiplier, 0.01F, 100.0F, "Range damage multiplier for Assimilated Ender Dragon.");
      infdragoneheadLoot = cfg.getStringList("Assimilated Ender Dragon Head Loot Table", "srparasites:sim_dragone", infdragoneheadLoot, "Items you want the Walking Head to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      infdragoneHealthHead = cfg.getFloat("Assimilated Ender Dragon Head Health", "srparasites:sim_dragone", infdragoneHealthHead, 0.01F, 100.0F, "Percentage of health it will have fron the host.");
      infdragoneDamageHead = cfg.getFloat("Assimilated Ender Dragon Head Damage", "srparasites:sim_dragone", infdragoneDamageHead, 0.01F, 100.0F, "Percentage of damage it will have fron the host.");
   }

   private static void initzetmoConfig(Configuration cfg) {
      String orb = " Ex. \"0;60;2;minecraft:fire_resistance;4;3\" Where: \n \"0\" mode of applying, 0 to non parasite mobs, 1 to self, 2 to parasite mobs, \n \"60\" potion duration in seconds, \n \"2\" potion amplifier, \n \"minecraft:fire_resistance\" potion itself, \n \"4\" if != 0 then amplifier += (mobs inside the orb / this value), \n \"3\" if != 0 then amplifier += (mobs inside the orb / this value) * 20. \n";
      String eff = " Ex. \"0;1;minecraft:fire_resistance\" Where: \n \"0\" potion duration in seconds, \n \"1\" potion amplifier, \n \"minecraft:fire_resistance\" potion itself. \n";
      cfg.addCustomCategoryComment("srparasites:bolster", "Bolster \n Base Health: " + SRPAttributes.ZETMO_HEALTH + " \n Base Damage: " + SRPAttributes.ZETMO_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.ZETMO_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.ZETMO_KD_RESISTANCE);
      zetmoEnabled = cfg.getBoolean("Primitive Bolster Enabled", "srparasites:bolster", zetmoEnabled, "Set to false if you want to disable Primitive Bolster.");
      zetmoHealthMultiplier = cfg.getFloat("Primitive Bolster" + health, "srparasites:bolster", 1.0F, 0.01F, 100.0F, "Health multiplier for Primitive Bolster.");
      zetmoDamageMultiplier = cfg.getFloat("Primitive Bolster" + damage, "srparasites:bolster", 1.0F, 0.01F, 100.0F, "Damage multiplier for Primitive Bolster.");
      zetmoArmorMultiplier = cfg.getFloat("Primitive Bolster" + armor, "srparasites:bolster", 1.0F, 0.01F, 100.0F, "Armor multiplier for Primitive Bolster.");
      zetmoKDResistanceMultiplier = cfg.getFloat("Primitive Bolster" + kd, "srparasites:bolster", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Primitive Bolster.");
      zetmoSpawnRate = cfg.getInt("Primitive Bolster SpawnWeight", "srparasites:bolster", zetmoSpawnRate, 0, 100, "Spawn rate for Primitive Bolster (This value is ignored if Evolution Phases are enabled, it has its own option).");
      zetmoLoot = cfg.getStringList("Primitive Bolster Loot Table", "srparasites:bolster", zetmoLoot, "Items you want the Primitive Bolster to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      zetmoCD = cfg.getInt("Primitive Bolster Buffs Cooldown", "srparasites:bolster", zetmoCD, 0, 100, "Cooldown (in seconds) to buff parasites for Primitive Bolster.");
      zetmoRange = cfg.getInt("Primitive Bolster Buffs Range", "srparasites:bolster", zetmoRange, 0, 100, "Range to buff parasites for Primitive Bolster.");
      zetmoOrbEffects = cfg.getStringList("Primitive Bolster Orb Effects", "srparasites:bolster", zetmoOrbEffects, "Orb effects " + orb);
      zetmoEffects = cfg.getStringList("Primitive Bolster Effects", "srparasites:bolster", zetmoEffects, "Potion effects that will give the Bolster to nearby parasites " + eff);
      zetmoadaptedhealth = cfg.getFloat("Stage Adapted additional Health", "srparasites:bolster", zetmoadaptedhealth, 0.01F, 100.0F, "Additional health for Adapted Bolster.");
      zetmoadapteddamage = cfg.getFloat("Stage Adapted additional Damage", "srparasites:bolster", zetmoadapteddamage, 0.01F, 100.0F, "Additional damage for Adapted Bolster.");
      zetmoadaptedarmor = cfg.getFloat("Stage Adapted additional Armor", "srparasites:bolster", zetmoadaptedarmor, 0.01F, 100.0F, "Additional armor for Adapted Bolster.");
      zetmoadaptedkdresistance = cfg.getFloat("Stage Adapted additional Knockback Resistance", "srparasites:bolster", zetmoadaptedkdresistance, 0.01F, 100.0F, "Additional Knockback Resistance for Adapted Bolster.");
      zetmoadaptedloot = cfg.getStringList("Stage Adapted loot Table", "srparasites:bolster", zetmoadaptedloot, "Items you want the Adapted Bolster to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      zetmoadaptedcd = cfg.getInt("Stage Adapted buffs cooldown", "srparasites:bolster", zetmoadaptedcd, 0, 100, "Cooldown (in seconds) to buff parasites for Adapted Bolster.");
      zetmoadaptedrange = cfg.getInt("Stage Adapted buffs range", "srparasites:bolster", zetmoadaptedrange, 0, 100, "Range to buff parasites for Adapted Bolster.");
      zetmoASpawnRate = cfg.getInt("Stage Adapted spawnweight", "srparasites:bolster", zetmoASpawnRate, 0, 100, "Spawn rate for Adapted Bolster (This value is ignored if Evolution Phases are enabled, it has its own option).");
      zetmoadaptedOrbEffects = cfg.getStringList("Stage Adapted Orb Effects", "srparasites:bolster", zetmoadaptedOrbEffects, "Orb effects " + orb);
      zetmoadaptedeffects = cfg.getStringList("Stage Adapted Effects", "srparasites:bolster", zetmoadaptedeffects, "Potion effects that will give the Bolster to nearby parasites " + eff);
   }

   private static void initangedConfig(Configuration cfg) {
      String orb = " Ex. \"0;60;2;minecraft:fire_resistance;4;3\" Where: \n \"0\" mode of applying, 0 to non parasite mobs, 1 to self, 2 to parasite mobs, \n \"60\" potion duration in seconds, \n \"2\" potion amplifier, \n \"minecraft:fire_resistance\" potion itself, \n \"4\" if != 0 then amplifier += (mobs inside the orb / this value), \n \"3\" if != 0 then amplifier += (mobs inside the orb / this value) * 20. \n";
      cfg.addCustomCategoryComment("srparasites:vigilante", "Vigilante \n Base Health: " + SRPAttributes.ANGED_HEALTH + " \n Base Damage: " + SRPAttributes.ANGED_ATTACK_DAMAGE + " \n Base Range Damage: " + SRPAttributes.ANGED_RANGED_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.ANGED_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.ANGED_KD_RESISTANCE);
      angedEnabled = cfg.getBoolean("Vigilante Enabled", "srparasites:vigilante", angedEnabled, "Set to false if you want to disable Vigilante.");
      angedHealthMultiplier = cfg.getFloat("Vigilante" + health, "srparasites:vigilante", 1.0F, 0.01F, 100.0F, "Health multiplier for Vigilante.");
      angedDamageMultiplier = cfg.getFloat("Vigilante" + damage, "srparasites:vigilante", 1.0F, 0.01F, 100.0F, "Damage multiplier for Vigilante.");
      angedRangeDamageMultiplier = cfg.getFloat("Vigilante Range" + damage, "srparasites:vigilante", 1.0F, 0.01F, 100.0F, "Range damage multiplier for Vigilante.");
      angedArmorMultiplier = cfg.getFloat("Vigilante" + armor, "srparasites:vigilante", 1.0F, 0.01F, 100.0F, "Armor multiplier for Vigilante.");
      angedKDResistanceMultiplier = cfg.getFloat("Vigilante" + kd, "srparasites:vigilante", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Vigilante.");
      angedSpawnRate = cfg.getInt("Vigilante SpawnWeight", "srparasites:vigilante", angedSpawnRate, 0, 100, "Spawn rate for Vigilante (This value is ignored if Evolution Phases are enabled, it has its own option).");
      angedLoot = cfg.getStringList("Vigilante Loot Table", "srparasites:vigilante", angedLoot, "Items you want the Vigilante to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      angedOrbEffects = cfg.getStringList("Vigilante Orb Effects", "srparasites:vigilante", angedOrbEffects, "Orb effects " + orb);
   }

   private static void inittonroConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:kyphosis", "Kyphosis \n Base Health: " + SRPAttributes.TONRO_HEALTH + " \n Base Damage: " + SRPAttributes.TONRO_ATTACK_DAMAGE + " \n Base Swing Damage: " + SRPAttributes.TONRO_SWING_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.TONRO_ARMOR + " \n ");
      tonroEnabled = cfg.getBoolean("Kyphosis Enabled", "srparasites:kyphosis", tonroEnabled, "Set to false if you want to disable Kyphosis.");
      tonroHealthMultiplier = cfg.getFloat("Kyphosis" + health, "srparasites:kyphosis", 1.0F, 0.01F, 100.0F, "Health multiplier for Kyphosis.");
      tonroDamageMultiplier = cfg.getFloat("Kyphosis" + damage, "srparasites:kyphosis", 1.0F, 0.01F, 100.0F, "Damage multiplier for Kyphosis.");
      tonroSwingDamageMultiplier = cfg.getFloat("Kyphosis Swing" + damage, "srparasites:kyphosis", 1.0F, 0.01F, 100.0F, "Swing damage multiplier for Kyphosis.");
      tonroArmorMultiplier = cfg.getFloat("Kyphosis" + armor, "srparasites:kyphosis", 1.0F, 0.01F, 100.0F, "Armor multiplier for Kyphosis.");
      tonroLoot = cfg.getStringList("Kyphosis Loot Table", "srparasites:kyphosis", tonroLoot, "Items you want the Kyphosis to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
   }

   private static void initunvoConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:sentry", "Sentry \n Base Health: " + SRPAttributes.UNVO_HEALTH + " \n Base Damage: " + SRPAttributes.UNVO_ATTACK_DAMAGE + " \n Base Range Damage: " + SRPAttributes.UNVO_RANGE_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.UNVO_ARMOR + " \n ");
      unvoEnabled = cfg.getBoolean("Sentry Enabled", "srparasites:sentry", unvoEnabled, "Set to false if you want to disable Sentry.");
      unvoHealthMultiplier = cfg.getFloat("Sentry" + health, "srparasites:sentry", 1.0F, 0.01F, 100.0F, "Health multiplier for Sentry.");
      unvoDamageMultiplier = cfg.getFloat("Sentry" + damage, "srparasites:sentry", 1.0F, 0.01F, 100.0F, "Damage multiplier for Sentry.");
      unvoRangeDamageMultiplier = cfg.getFloat("Sentry Range" + damage, "srparasites:sentry", 1.0F, 0.01F, 100.0F, "Range damage multiplier for Sentry.");
      unvoArmorMultiplier = cfg.getFloat("Sentry" + armor, "srparasites:sentry", 1.0F, 0.01F, 100.0F, "Armor multiplier for Sentry.");
      unvoLoot = cfg.getStringList("Sentry Loot Table", "srparasites:sentry", unvoLoot, "Items you want the Sentry to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      unvoGearD = (double)cfg.getFloat("Sentry Gear degrade", "srparasites:sentry", (float)unvoGearD, 0.0F, 1.0F, "How much a shot will degrade your gear (1=100%).");
      unvoPoisonDuration = cfg.getInt("Sentry Poison Duration", "srparasites:sentry", unvoPoisonDuration, 0, 100, "Poison duration in seconds for its projectile.");
      unvoPoisonAmplifier = cfg.getInt("Sentry Poison Amplifier", "srparasites:sentry", unvoPoisonAmplifier, 1, 100, "Poison amplifier for its projectile.");
   }

   private static void initganroConfig(Configuration cfg) {
      String orb = " Ex. \"0;60;2;minecraft:fire_resistance;4;3\" Where: \n \"0\" mode of applying, 0 to non parasite mobs, 1 to self, 2 to parasite mobs, \n \"60\" potion duration in seconds, \n \"2\" potion amplifier, \n \"minecraft:fire_resistance\" potion itself, \n \"4\" if != 0 then amplifier += (mobs inside the orb / this value), \n \"3\" if != 0 then amplifier += (mobs inside the orb / this value) * 20. \n";
      cfg.addCustomCategoryComment("srparasites:warden", "Warden \n Base Health: " + SRPAttributes.GANRO_HEALTH + " \n Base Damage: " + SRPAttributes.GANRO_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.GANRO_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.GANRO_KD_RESISTANCE);
      ganroEnabled = cfg.getBoolean("Warden Enabled", "srparasites:warden", ganroEnabled, "Set to false if you want to disable Warden.");
      ganroHealthMultiplier = cfg.getFloat("Warden" + health, "srparasites:warden", 1.0F, 0.01F, 100.0F, "Health multiplier for Warden.");
      ganroDamageMultiplier = cfg.getFloat("Warden" + damage, "srparasites:warden", 1.0F, 0.01F, 100.0F, "Damage multiplier for Warden.");
      ganroArmorMultiplier = cfg.getFloat("Warden" + armor, "srparasites:warden", 1.0F, 0.01F, 100.0F, "Armor multiplier for Warden.");
      ganroKDResistanceMultiplier = cfg.getFloat("Warden" + kd, "srparasites:warden", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Warden.");
      ganroSpawnRate = cfg.getInt("Warden SpawnWeight", "srparasites:warden", ganroSpawnRate, 0, 100, "Spawn rate for Warden (This value is ignored if Evolution Phases are enabled, it has its own option).");
      ganroLoot = cfg.getStringList("Warden Loot Table", "srparasites:warden", ganroLoot, "Items you want the Warden to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      ganroOrbEffects = cfg.getStringList("Warden Orb Effects", "srparasites:warden", ganroOrbEffects, "Orb effects " + orb);
   }

   private static void initesorConfig(Configuration cfg) {
      String orb = " Ex. \"0;60;2;minecraft:fire_resistance;4;3\" Where: \n \"0\" mode of applying, 0 to non parasite mobs, 1 to self, 2 to parasite mobs, \n \"60\" potion duration in seconds, \n \"2\" potion amplifier, \n \"minecraft:fire_resistance\" potion itself, \n \"4\" if != 0 then amplifier += (mobs inside the orb / this value), \n \"3\" if != 0 then amplifier += (mobs inside the orb / this value) * 20. \n";
      cfg.addCustomCategoryComment("srparasites:marauder", "Marauder \n Base Health: " + SRPAttributes.ESOR_HEALTH + " \n Base Damage: " + SRPAttributes.ESOR_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.ESOR_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.ESOR_KD_RESISTANCE);
      esorEnabled = cfg.getBoolean("Marauder Enabled", "srparasites:marauder", esorEnabled, "Set to false if you want to disable Marauder.");
      esorHealthMultiplier = cfg.getFloat("Marauder" + health, "srparasites:marauder", 1.0F, 0.01F, 100.0F, "Health multiplier for Marauder.");
      esorDamageMultiplier = cfg.getFloat("Marauder" + damage, "srparasites:marauder", 1.0F, 0.01F, 100.0F, "Damage multiplier for Marauder.");
      esorArmorMultiplier = cfg.getFloat("Marauder" + armor, "srparasites:marauder", 1.0F, 0.01F, 100.0F, "Armor multiplier for Marauder.");
      esorKDResistanceMultiplier = cfg.getFloat("Marauder" + kd, "srparasites:marauder", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Marauder.");
      esorSpawnRate = cfg.getInt("Marauder SpawnWeight", "srparasites:marauder", esorSpawnRate, 0, 100, "Spawn rate for Marauder (This value is ignored if Evolution Phases are enabled, it has its own option).");
      esorLoot = cfg.getStringList("Marauder Loot Table", "srparasites:marauder", esorLoot, "Items you want the Marauder to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      esorOrbEffects = cfg.getStringList("Marauder Orb Effects", "srparasites:marauder", esorOrbEffects, "Orb effects " + orb);
   }

   private static void initorchConfig(Configuration cfg) {
      String orb = " Ex. \"0;60;2;minecraft:fire_resistance;4;3\" Where: \n \"0\" mode of applying, 0 to non parasite mobs, 1 to self, 2 to parasite mobs, \n \"60\" potion duration in seconds, \n \"2\" potion amplifier, \n \"minecraft:fire_resistance\" potion itself, \n \"4\" if != 0 then amplifier += (mobs inside the orb / this value), \n \"3\" if != 0 then amplifier += (mobs inside the orb / this value) * 20. \n";
      cfg.addCustomCategoryComment("srparasites:monarch", "Monarch \n Base Health: " + SRPAttributes.ORCH_HEALTH + " \n Base Damage: " + SRPAttributes.ORCH_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.ORCH_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.ORCH_KD_RESISTANCE);
      orchEnabled = cfg.getBoolean("Monarch Enabled", "srparasites:monarch", orchEnabled, "Set to false if you want to disable Monarch.");
      orchHealthMultiplier = cfg.getFloat("Monarch" + health, "srparasites:monarch", 1.0F, 0.01F, 100.0F, "Health multiplier for Monarch.");
      orchDamageMultiplier = cfg.getFloat("Monarch" + damage, "srparasites:monarch", 1.0F, 0.01F, 100.0F, "Damage multiplier for Monarch.");
      orchArmorMultiplier = cfg.getFloat("Monarch" + armor, "srparasites:monarch", 1.0F, 0.01F, 100.0F, "Armor multiplier for Monarch.");
      orchKDResistanceMultiplier = cfg.getFloat("Monarch" + kd, "srparasites:monarch", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Monarch.");
      orchSpawnRate = cfg.getInt("Monarch SpawnWeight", "srparasites:monarch", orchSpawnRate, 0, 100, "Spawn rate for Monarch (This value is ignored if Evolution Phases are enabled, it has its own option).");
      orchLoot = cfg.getStringList("Monarch Loot Table", "srparasites:monarch", orchLoot, "Items you want the Monarch to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      orchOrbEffects = cfg.getStringList("Monarch Orb Effects", "srparasites:monarch", orchOrbEffects, "Orb effects " + orb);
   }

   private static void initflogConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:grunt", "Grunt \n Base Health: " + SRPAttributes.FLOG_HEALTH + " \n Base Damage: " + SRPAttributes.FLOG_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.FLOG_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.FLOG_KD_RESISTANCE);
      flogEnabled = cfg.getBoolean("Grunt Enabled", "srparasites:grunt", flogEnabled, "Set to false if you want to disable Grunt.");
      flogHealthMultiplier = cfg.getFloat("Grunt" + health, "srparasites:grunt", 1.0F, 0.01F, 100.0F, "Health multiplier for Grunt.");
      flogDamageMultiplier = cfg.getFloat("Grunt" + damage, "srparasites:grunt", 1.0F, 0.01F, 100.0F, "Damage multiplier for Grunt.");
      flogArmorMultiplier = cfg.getFloat("Grunt" + armor, "srparasites:grunt", 1.0F, 0.01F, 100.0F, "Armor multiplier for Grunt.");
      flogKDResistanceMultiplier = cfg.getFloat("Grunt" + kd, "srparasites:grunt", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Grunt.");
      flogSpawnRate = cfg.getInt("Grunt SpawnWeight", "srparasites:grunt", flogSpawnRate, 0, 100, "Spawn rate for Grunt (This value is ignored if Evolution Phases are enabled, it has its own option).");
      flogLoot = cfg.getStringList("Grunt Loot Table", "srparasites:grunt", flogLoot, "Items you want the Grunt to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
   }

   private static void initoroncoConfig(Configuration cfg) {
      String ancientspawning = " Ex. \"minecraft:zombie;0.1;1\"  Where: \n \"minecraft:zombie\" is for the entity, \n \"0.1\" is for the chance to spawn. \n";
      cfg.addCustomCategoryComment("srparasites:anc_dreadnaut", "Ancient Dreadnaut \n Base Health: " + SRPAttributes.ORONCO_HEALTH + " \n Base Damage: " + SRPAttributes.ORONCO_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.ORONCO_ARMOR);
      oroncoEnabled = cfg.getBoolean("Ancient Dreadnaut Enabled", "srparasites:anc_dreadnaut", oroncoEnabled, "Set to false if you want to disable Ancient Dreadnaut.");
      oroncoHealthMultiplier = cfg.getFloat("Ancient Dreadnaut" + health, "srparasites:anc_dreadnaut", 1.0F, 0.01F, 100.0F, "Health multiplier for Ancient Dreadnaut.");
      oroncoDamageMultiplier = cfg.getFloat("Ancient Dreadnaut" + damage, "srparasites:anc_dreadnaut", 1.0F, 0.01F, 100.0F, "Damage multiplier for Ancient Dreadnaut.");
      oroncoArmorMultiplier = cfg.getFloat("Ancient Dreadnaut" + armor, "srparasites:anc_dreadnaut", 1.0F, 0.01F, 100.0F, "Armor multiplier for Ancient Dreadnaut.");
      oroncoKDResistanceMultiplier = cfg.getFloat("Ancient Dreadnaut" + kd, "srparasites:anc_dreadnaut", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Ancient Dreadnaut.");
      oroncoSpawnRate = cfg.getInt("Ancient Dreadnaut SpawnWeight", "srparasites:anc_dreadnaut", oroncoSpawnRate, 0, 100, "Spawn rate for Ancient Dreadnaut (This value is ignored if Evolution Phases are enabled, it has its own option).");
      oroncoLoot = cfg.getStringList("Ancient Dreadnaut Loot Table", "srparasites:anc_dreadnaut", oroncoLoot, "Items you want the Ancient Dreadnaut to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      oroncoMaxY = cfg.getInt("Ancient Dreadnaut Flight Height Limit", "srparasites:anc_dreadnaut", oroncoMaxY, 0, 256, "Number of blocks it can fly above the ground for Ancient Dreadnaut.");
      oroncoMinY = cfg.getInt("Ancient Dreadnaut Minimum Flight Height", "srparasites:anc_dreadnaut", oroncoMinY, 0, 256, "Number of blocks it needs to fly above the ground for Ancient Dreadnaut.");
      oroncoMobList = cfg.getStringList("Ancient Dreadnaut Mob List", "srparasites:anc_dreadnaut", oroncoMobList, "Mob list for the Ancient Dreadnaut." + ancientspawning);
      oroncoG = cfg.getBoolean("Ancient Dreadnaut Pod Grief", "srparasites:anc_dreadnaut", oroncoG, "Set to false if you want to disable drop pods from breaking blocks on explotion.");
      oroncoMaxMobPod = cfg.getInt("Ancient Dreadnaut Pod Max Pods", "srparasites:anc_dreadnaut", oroncoMaxMobPod, 1, 256, "Number of mobs inside a drop pod");
      oroncoPodNumber = cfg.getInt("Ancient Dreadnaut Pod Max Enemies", "srparasites:anc_dreadnaut", oroncoPodNumber, 1, 256, "Number of drop pods spawned in an attack");
      oroncoPodCooldown = cfg.getInt("Ancient Dreadnaut Pod Cooldown", "srparasites:anc_dreadnaut", oroncoPodCooldown, 1, 256, "Cooldown (in seconds) for the drop pod attack");
   }

   private static void initterlaConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:anc_overlord", "Ancient Overlord \n Base Health: " + SRPAttributes.TERLA_HEALTH + " \n Base Damage: " + SRPAttributes.TERLA_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.TERLA_ARMOR);
      terlaEnabled = cfg.getBoolean("Ancient Overlord Enabled", "srparasites:anc_overlord", terlaEnabled, "Set to false if you want to disable Ancient Overlord.");
      terlaHealthMultiplier = cfg.getFloat("Ancient Overlord" + health, "srparasites:anc_overlord", 1.0F, 0.01F, 100.0F, "Health multiplier for Ancient Overlord.");
      terlaDamageMultiplier = cfg.getFloat("Ancient Overlord" + damage, "srparasites:anc_overlord", 1.0F, 0.01F, 100.0F, "Damage multiplier for Ancient Overlord.");
      terlaArmorMultiplier = cfg.getFloat("Ancient Overlord" + armor, "srparasites:anc_overlord", 1.0F, 0.01F, 100.0F, "Armor multiplier for Ancient Overlord.");
      terlaKDResistanceMultiplier = cfg.getFloat("Ancient Overlord" + kd, "srparasites:anc_overlord", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Ancient Overlord.");
      terlaSpawnRate = cfg.getInt("Ancient Overlord SpawnWeight", "srparasites:anc_overlord", terlaSpawnRate, 0, 100, "Spawn rate for Ancient Overlord (This value is ignored if Evolution Phases are enabled, it has its own option).");
      terlaLoot = cfg.getStringList("Ancient Overlord Loot Table", "srparasites:anc_overlord", terlaLoot, "Items you want the Ancient Overlord to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
   }

   private static void initarachnidaConfig(Configuration cfg) {
      String orb = " Ex. \"0;60;2;minecraft:fire_resistance;4;3\" Where: \n \"0\" mode of applying, 0 to non parasite mobs, 1 to self, 2 to parasite mobs, \n \"60\" potion duration in seconds, \n \"2\" potion amplifier, \n \"minecraft:fire_resistance\" potion itself, \n \"4\" if != 0 then amplifier += (mobs inside the orb / this value), \n \"3\" if != 0 then amplifier += (mobs inside the orb / this value) * 20. \n";
      cfg.addCustomCategoryComment("srparasites:arachnida", "Arachnida \n Base Health: " + SRPAttributes.RANRAC_HEALTH + " \n Base Damage: " + SRPAttributes.RANRAC_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.RANRAC_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.RANRAC_KD_RESISTANCE);
      arachnidaEnabled = cfg.getBoolean("Primitive Arachnida Enabled", "srparasites:arachnida", arachnidaEnabled, "Set to false if you want to disable Primitive Arachnida.");
      arachnidaHealthMultiplier = cfg.getFloat("Primitive Arachnida" + health, "srparasites:arachnida", 1.0F, 0.01F, 100.0F, "Health multiplier for Primitive Arachnida.");
      arachnidaDamageMultiplier = cfg.getFloat("Primitive Arachnida" + damage, "srparasites:arachnida", 1.0F, 0.01F, 100.0F, "Damage multiplier for Primitive Arachnida.");
      arachnidaArmorMultiplier = cfg.getFloat("Primitive Arachnida" + armor, "srparasites:arachnida", 1.0F, 0.01F, 100.0F, "Armor multiplier for Primitive Arachnida.");
      arachnidaKDResistanceMultiplier = cfg.getFloat("Primitive Arachnida" + kd, "srparasites:arachnida", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Primitive Arachnida.");
      arachnidaSpawnRate = cfg.getInt("Primitive Arachnida SpawnWeight", "srparasites:arachnida", arachnidaSpawnRate, 0, 100, "Spawn rate for Primitive Arachnida (This value is ignored if Evolution Phases are enabled, it has its own option).");
      arachnidaLoot = cfg.getStringList("Primitive Arachnida Loot Table", "srparasites:arachnida", arachnidaLoot, "Items you want the Primitive Arachnida to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      arachnidaOrbEffects = cfg.getStringList("Primitive Arachnida Orb Effects", "srparasites:arachnida", arachnidaOrbEffects, "Orb effects " + orb);
      arachnidaadaptedhealth = cfg.getFloat("Stage Adapted additional Health", "srparasites:arachnida", arachnidaadaptedhealth, 0.01F, 100.0F, "Additional health for Adapted Arachnida.");
      arachnidaadapteddamage = cfg.getFloat("Stage Adapted additional Damage", "srparasites:arachnida", arachnidaadapteddamage, 0.01F, 100.0F, "Additional damage for Adapted Arachnida.");
      arachnidaadaptedarmor = cfg.getFloat("Stage Adapted additional Armor", "srparasites:arachnida", arachnidaadaptedarmor, 0.01F, 100.0F, "Additional armor for Adapted Arachnida.");
      arachnidaadaptedkdresistance = cfg.getFloat("Stage Adapted additional Knockback Resistance", "srparasites:arachnida", arachnidaadaptedkdresistance, 0.01F, 100.0F, "Additional Knockback Resistance for Adapted Arachnida.");
      arachnidaadaptedloot = cfg.getStringList("Stage Adapted loot Table", "srparasites:arachnida", arachnidaadaptedloot, "Items you want the Adapted Arachnida to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      arachnidaASpawnRate = cfg.getInt("Stage Adapted spawnweight", "srparasites:arachnida", arachnidaASpawnRate, 0, 100, "Spawn rate for Adapted Arachnida (This value is ignored if Evolution Phases are enabled, it has its own option).");
      arachnidaadaptedOrbEffects = cfg.getStringList("Stage Adapted Orb Effects", "srparasites:arachnida", arachnidaadaptedOrbEffects, "Orb effects " + orb);
   }

   private static void initkolConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:worker", "Worker \n Base Health: " + SRPAttributes.KOL_HEALTH + " \n Base Damage: " + SRPAttributes.KOL_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.KOL_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.KOL_KD_RESISTANCE + " \nNote: This mob will only spawn within the range of a Colony");
      kolEnabled = cfg.getBoolean("Worker Enabled", "srparasites:worker", kolEnabled, "Set to false if you want to disable Worker.");
      kolHealthMultiplier = cfg.getFloat("Worker" + health, "srparasites:worker", 1.0F, 0.01F, 100.0F, "Health multiplier for Worker.");
      kolDamageMultiplier = cfg.getFloat("Worker" + damage, "srparasites:worker", 1.0F, 0.01F, 100.0F, "Damage multiplier for Worker.");
      kolArmorMultiplier = cfg.getFloat("Worker" + armor, "srparasites:worker", 1.0F, 0.01F, 100.0F, "Armor multiplier for Worker.");
      kolKDResistanceMultiplier = cfg.getFloat("Worker" + kd, "srparasites:worker", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Worker.");
      kolSpawnRate = cfg.getInt("Worker SpawnWeight", "srparasites:worker", kolSpawnRate, 0, 100, "Spawn rate for Worker (This value is ignored if Evolution Phases are enabled, it has its own option).");
      kolLoot = cfg.getStringList("Worker Loot Table", "srparasites:worker", kolLoot, "Items you want the Worker to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
   }

   private static void initinhooSConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:inhoos", "Incomplete Form (Small) \n Base Health: " + SRPAttributes.INHOOS_HEALTH + " \n Base Damage: " + SRPAttributes.INHOOS_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.INHOOS_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.INHOOS_KD_RESISTANCE + " \nNote: This mob will only spawn within the range of a Colony");
      inhooSEnabled = cfg.getBoolean("Incomplete Form (Small) Enabled", "srparasites:inhoos", inhooSEnabled, "Set to false if you want to disable Incomplete Form (Small).");
      inhooSHealthMultiplier = cfg.getFloat("Incomplete Form (Small)" + health, "srparasites:inhoos", 1.0F, 0.01F, 100.0F, "Health multiplier for Incomplete Form (Small).");
      inhooSDamageMultiplier = cfg.getFloat("Incomplete Form (Small)" + damage, "srparasites:inhoos", 1.0F, 0.01F, 100.0F, "Damage multiplier for Incomplete Form (Small).");
      inhooSArmorMultiplier = cfg.getFloat("Incomplete Form (Small)" + armor, "srparasites:inhoos", 1.0F, 0.01F, 100.0F, "Armor multiplier for Incomplete Form (Small).");
      inhooSKDResistanceMultiplier = cfg.getFloat("Incomplete Form (Small)" + kd, "srparasites:inhoos", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Incomplete Form (Small).");
      inhooSSpawnRate = cfg.getInt("Incomplete Form (Small) SpawnWeight", "srparasites:inhoos", inhooSSpawnRate, 0, 100, "Spawn rate for Incomplete Form (Small) (This value is ignored if Evolution Phases are enabled, it has its own option).");
      inhooSLoot = cfg.getStringList("Incomplete Form (Small) Loot Table", "srparasites:inhoos", inhooSLoot, "Items you want the Incomplete Form (Small) to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
   }

   private static void initinhooMConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:inhoom", "Incomplete Form (Medium) \n Base Health: " + SRPAttributes.INHOOM_HEALTH + " \n Base Damage: " + SRPAttributes.INHOOM_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.INHOOM_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.INHOOM_KD_RESISTANCE + " \nNote: This mob will only spawn within the range of a Colony");
      inhooMEnabled = cfg.getBoolean("Incomplete Form (Medium) Enabled", "srparasites:inhoom", inhooMEnabled, "Set to false if you want to disable Incomplete Form (Medium).");
      inhooMHealthMultiplier = cfg.getFloat("Incomplete Form (Medium)" + health, "srparasites:inhoom", 1.0F, 0.01F, 100.0F, "Health multiplier for Incomplete Form (Medium).");
      inhooMDamageMultiplier = cfg.getFloat("Incomplete Form (Medium)" + damage, "srparasites:inhoom", 1.0F, 0.01F, 100.0F, "Damage multiplier for Incomplete Form (Medium).");
      inhooMArmorMultiplier = cfg.getFloat("Incomplete Form (Medium)" + armor, "srparasites:inhoom", 1.0F, 0.01F, 100.0F, "Armor multiplier for Incomplete Form (Medium).");
      inhooMKDResistanceMultiplier = cfg.getFloat("Incomplete Form (Medium)" + kd, "srparasites:inhoom", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Incomplete Form (Medium).");
      inhooMSpawnRate = cfg.getInt("Incomplete Form (Medium) SpawnWeight", "srparasites:inhoom", inhooMSpawnRate, 0, 100, "Spawn rate for Incomplete Form (Medium) (This value is ignored if Evolution Phases are enabled, it has its own option).");
      inhooMLoot = cfg.getStringList("Incomplete Form (Medium) Loot Table", "srparasites:inhoom", inhooMLoot, "Items you want the Incomplete Form (Medium) to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
   }

   private static void initombooConfig(Configuration cfg) {
      String mobTable = " Ex. \"minecraft:zombie;4;2\" Where: \n \"minecraft:zombie\" is the entity, \n \"4\" is the maximum number of entities, \n \"2\" is the minimum number of entities. \n";
      cfg.addCustomCategoryComment("srparasites:bomber_light", "Light Bomber \n Base Health: " + SRPAttributes.OMBOO_HEALTH + " \n Base Armor: " + SRPAttributes.OMBOO_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.OMBOO_KD_RESISTANCE);
      ombooEnabled = cfg.getBoolean("Light Bomber Enabled", "srparasites:bomber_light", ombooEnabled, "Set to false if you want to disable Light Bomber.");
      ombooHealthMultiplier = cfg.getFloat("Light Bomber" + health, "srparasites:bomber_light", 1.0F, 0.01F, 100.0F, "Health multiplier for Light Bomber.");
      ombooDamageMultiplier = cfg.getFloat("Light Bomber" + damage, "srparasites:bomber_light", 1.0F, 0.01F, 100.0F, "Damage multiplier for Light Bomber.");
      ombooArmorMultiplier = cfg.getFloat("Light Bomber" + armor, "srparasites:bomber_light", 1.0F, 0.01F, 100.0F, "Armor multiplier for Light Bomber.");
      ombooKDResistanceMultiplier = cfg.getFloat("Light Bomber" + kd, "srparasites:bomber_light", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Light Bomber.");
      ombooSpawnRate = cfg.getInt("Light Bomber SpawnWeight", "srparasites:bomber_light", ombooSpawnRate, 0, 100, "Spawn rate for Light Bomber (This value is ignored if Evolution Phases are enabled, it has its own option).");
      ombooGriefing = cfg.getBoolean("Light Bomber Griefing", "srparasites:bomber_light", ombooGriefing, "Set to true if you want the Light Bomber to destroy blocks on explosion.");
      ombooLoot = cfg.getStringList("Light Bomber Loot Table", "srparasites:bomber_light", ombooLoot, "Items you want the Light Bomber to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      ombooMaxY = cfg.getInt("Light Bomber Flight Height Limit", "srparasites:bomber_light", ombooMaxY, 0, 256, "Number of blocks it can fly above the ground for Light Bomber.");
      ombooDamage = cfg.getFloat("Light Bomber Bomb Damage", "srparasites:bomber_light", ombooDamage, 1.0F, 1000.0F, "Damage of its bomb");
   }

   private static void initjinjoConfig(Configuration cfg) {
      String mobTable = " Ex. \"minecraft:zombie;4;2\" Where: \n \"minecraft:zombie\" is the entity, \n \"4\" is the maximum number of entities, \n \"2\" is the minimum number of entities. \n";
      String orb = " Ex. \"0;60;2;minecraft:fire_resistance;4;3\" Where: \n \"0\" mode of applying, 0 to non parasite mobs, 1 to self, 2 to parasite mobs, \n \"60\" potion duration in seconds, \n \"2\" potion amplifier, \n \"minecraft:fire_resistance\" potion itself, \n \"4\" if != 0 then amplifier += (mobs inside the orb / this value), \n \"3\" if != 0 then amplifier += (mobs inside the orb / this value) * 20. \n";
      cfg.addCustomCategoryComment("srparasites:bomber_heavy", "Heavy Bomber \n Base Health: " + SRPAttributes.JINJO_HEALTH + " \n Base Damage: " + SRPAttributes.JINJO_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.JINJO_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.JINJO_KD_RESISTANCE);
      jinjoEnabled = cfg.getBoolean("Heavy Bomber Enabled", "srparasites:bomber_heavy", jinjoEnabled, "Set to false if you want to disable Heavy Bomber.");
      jinjoHealthMultiplier = cfg.getFloat("Heavy Bomber" + health, "srparasites:bomber_heavy", 1.0F, 0.01F, 100.0F, "Health multiplier for Heavy Bomber.");
      jinjoDamageMultiplier = cfg.getFloat("Heavy Bomber" + damage, "srparasites:bomber_heavy", 1.0F, 0.01F, 100.0F, "Damage multiplier for Heavy Bomber.");
      jinjoArmorMultiplier = cfg.getFloat("Heavy Bomber" + armor, "srparasites:bomber_heavy", 1.0F, 0.01F, 100.0F, "Armor multiplier for Heavy Bomber.");
      jinjoKDResistanceMultiplier = cfg.getFloat("Heavy Bomber" + kd, "srparasites:bomber_heavy", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Heavy Bomber.");
      jinjoSpawnRate = cfg.getInt("Heavy Bomber SpawnWeight", "srparasites:bomber_heavy", jinjoSpawnRate, 0, 100, "Spawn rate for Heavy Bomber (This value is ignored if Evolution Phases are enabled, it has its own option).");
      jinjoGriefing = cfg.getBoolean("Heavy Bomber Griefing", "srparasites:bomber_heavy", jinjoGriefing, "Set to true if you want the Heavy Bomber to destroy blocks on explosion.");
      jinjoLoot = cfg.getStringList("Heavy Bomber Loot Table", "srparasites:bomber_heavy", jinjoLoot, "Items you want the Heavy Bomber to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      jinjoMaxY = cfg.getInt("Heavy Bomber Flight Height Limit", "srparasites:bomber_heavy", jinjoMaxY, 0, 256, "Number of blocks it can fly above the ground for Heavy Bomber.");
      jinjoMobs = cfg.getStringList("Heavy Bomber Mob Table", "srparasites:bomber_heavy", jinjoMobs, "Mob list for Heavy Bomber." + mobTable);
      jinjoDamage = cfg.getFloat("Heavy Bomber Bomb Damage", "srparasites:bomber_heavy", jinjoDamage, 1.0F, 1000.0F, "Damage of its bomb");
      jinjoOrbEffects = cfg.getStringList("Heavy Bomber Orb Effects", "srparasites:bomber_heavy", jinjoOrbEffects, "Orb effects " + orb);
      jinjoExplotionMult = cfg.getFloat("Heavy Bomber Explotion Multiplier", "srparasites:bomber_heavy", jinjoExplotionMult, 1.0F, 100.0F, "Explotion damage will take base Damage and multiply it by this value.");
   }

   private static void initvestaConfig(Configuration cfg) {
      String mobTable = " Ex. \"minecraft:zombie;4;2\" Where: \n \"minecraft:zombie\" is the entity, \n \"4\" is the maximum number of entities, \n \"2\" is the minimum number of entities. \n";
      String orb = " Ex. \"0;60;2;minecraft:fire_resistance;4;3\" Where: \n \"0\" mode of applying, 0 to non parasite mobs, 1 to self, 2 to parasite mobs, \n \"60\" potion duration in seconds, \n \"2\" potion amplifier, \n \"minecraft:fire_resistance\" potion itself, \n \"4\" if != 0 then amplifier += (mobs inside the orb / this value), \n \"3\" if != 0 then amplifier += (mobs inside the orb / this value) * 20. \n";
      String eff = " Ex. \"0;1;minecraft:fire_resistance\" Where: \n \"0\" potion duration in seconds, \n \"1\" potion amplifier, \n \"minecraft:fire_resistance\" potion itself. \n";
      cfg.addCustomCategoryComment("srparasites:carrier_colony", "Colony Carrier \n Base Health: " + SRPAttributes.VESTA_HEALTH + " \n Base Armor: " + SRPAttributes.VESTA_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.VESTA_KD_RESISTANCE);
      vestaEnabled = cfg.getBoolean("Colony Carrier Enabled", "srparasites:carrier_colony", vestaEnabled, "Set to false if you want to disable Colony Carrier.");
      vestaHealthMultiplier = cfg.getFloat("Colony Carrier" + health, "srparasites:carrier_colony", 1.0F, 0.01F, 100.0F, "Health multiplier for Colony Carrier.");
      vestaDamageMultiplier = cfg.getFloat("Colony Carrier" + damage, "srparasites:carrier_colony", 1.0F, 0.01F, 100.0F, "Damage multiplier for Colony Carrier.");
      vestaArmorMultiplier = cfg.getFloat("Colony Carrier" + armor, "srparasites:carrier_colony", 1.0F, 0.01F, 100.0F, "Armor multiplier for Colony Carrier.");
      vestaKDResistanceMultiplier = cfg.getFloat("Colony Carrier" + kd, "srparasites:carrier_colony", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Colony Carrier.");
      vestaSpawnRate = cfg.getInt("Colony Carrier SpawnWeight", "srparasites:carrier_colony", vestaSpawnRate, 0, 100, "Spawn rate for Colony Carrier (This value is ignored if Evolution Phases are enabled, it has its own option).");
      vestaLoot = cfg.getStringList("Colony Carrier Loot Table", "srparasites:carrier_colony", vestaLoot, "Items you want the Colony Carrier to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      vestaOrbEffects = cfg.getStringList("Colony Carrier Orb Effects", "srparasites:carrier_colony", vestaOrbEffects, "Orb effects " + orb);
      vestacd = cfg.getInt("Colony Carrier Buffs Cooldown", "srparasites:carrier_colony", vestacd, 0, 100, "Cooldown (in seconds) to buff parasites for Colony Carrier.");
      vestarange = cfg.getInt("Colony Carrier Buffs Range", "srparasites:carrier_colony", vestarange, 0, 100, "Range to buff parasites for Colony Carrier.");
      vestaeffects = cfg.getStringList("Colony Carrier Effects", "srparasites:carrier_colony", vestaeffects, "Potion effects that will give the Colony Carrier to nearby parasites " + eff);
   }

   private static void initelviaConfig(Configuration cfg) {
      String mobTable = " Ex. \"minecraft:zombie;4;2\" Where: \n \"minecraft:zombie\" is the entity, \n \"4\" is the maximum number of entities, \n \"2\" is the minimum number of entities. \n";
      String orb = " Ex. \"0;60;2;minecraft:fire_resistance;4;3\" Where: \n \"0\" mode of applying, 0 to non parasite mobs, 1 to self, 2 to parasite mobs, \n \"60\" potion duration in seconds, \n \"2\" potion amplifier, \n \"minecraft:fire_resistance\" potion itself, \n \"4\" if != 0 then amplifier += (mobs inside the orb / this value), \n \"3\" if != 0 then amplifier += (mobs inside the orb / this value) * 20. \n";
      cfg.addCustomCategoryComment("srparasites:wraith", "Wraith \n Base Health: " + SRPAttributes.ELVIA_HEALTH + " \n Base Armor: " + SRPAttributes.ELVIA_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.ELVIA_KD_RESISTANCE);
      elviaEnabled = cfg.getBoolean("Wraith Enabled", "srparasites:wraith", elviaEnabled, "Set to false if you want to disable Wraith.");
      elviaHealthMultiplier = cfg.getFloat("Wraith" + health, "srparasites:wraith", 1.0F, 0.01F, 100.0F, "Health multiplier for Wraith.");
      elviaDamageMultiplier = cfg.getFloat("Wraith" + damage, "srparasites:wraith", 1.0F, 0.01F, 100.0F, "Damage multiplier for Wraith.");
      elviaArmorMultiplier = cfg.getFloat("Wraith" + armor, "srparasites:wraith", 1.0F, 0.01F, 100.0F, "Armor multiplier for Wraith.");
      elviaKDResistanceMultiplier = cfg.getFloat("Wraith" + kd, "srparasites:wraith", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Wraith.");
      elviaSpawnRate = cfg.getInt("Wraith SpawnWeight", "srparasites:wraith", elviaSpawnRate, 0, 100, "Spawn rate for Wraith (This value is ignored if Evolution Phases are enabled, it has its own option).");
      elviaLoot = cfg.getStringList("Wraith Loot Table", "srparasites:wraith", elviaLoot, "Items you want the Wraith to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      elviaOrbEffects = cfg.getStringList("Wraith Orb Effects", "srparasites:wraith", elviaOrbEffects, "Orb effects " + orb);
   }

   private static void initlenciaConfig(Configuration cfg) {
      String mobTable = " Ex. \"minecraft:zombie;4;2\" Where: \n \"minecraft:zombie\" is the entity, \n \"4\" is the maximum number of entities, \n \"2\" is the minimum number of entities. \n";
      String orb = " Ex. \"0;60;2;minecraft:fire_resistance;4;3\" Where: \n \"0\" mode of applying, 0 to non parasite mobs, 1 to self, 2 to parasite mobs, \n \"60\" potion duration in seconds, \n \"2\" potion amplifier, \n \"minecraft:fire_resistance\" potion itself, \n \"4\" if != 0 then amplifier += (mobs inside the orb / this value), \n \"3\" if != 0 then amplifier += (mobs inside the orb / this value) * 20. \n";
      cfg.addCustomCategoryComment("srparasites:bogle", "Bogle \n Base Health: " + SRPAttributes.LENCIA_HEALTH + " \n Base Armor: " + SRPAttributes.LENCIA_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.LENCIA_KD_RESISTANCE);
      lenciaEnabled = cfg.getBoolean("Bogle Enabled", "srparasites:bogle", lenciaEnabled, "Set to false if you want to disable Bogle.");
      lenciaHealthMultiplier = cfg.getFloat("Bogle" + health, "srparasites:bogle", 1.0F, 0.01F, 100.0F, "Health multiplier for Bogle.");
      lenciaDamageMultiplier = cfg.getFloat("Bogle" + damage, "srparasites:bogle", 1.0F, 0.01F, 100.0F, "Damage multiplier for Bogle.");
      lenciaArmorMultiplier = cfg.getFloat("Bogle" + armor, "srparasites:bogle", 1.0F, 0.01F, 100.0F, "Armor multiplier for Bogle.");
      lenciaKDResistanceMultiplier = cfg.getFloat("Bogle" + kd, "srparasites:bogle", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Bogle.");
      lenciaSpawnRate = cfg.getInt("Bogle SpawnWeight", "srparasites:bogle", lenciaSpawnRate, 0, 100, "Spawn rate for Bogle (This value is ignored if Evolution Phases are enabled, it has its own option).");
      lenciaLoot = cfg.getStringList("Bogle Loot Table", "srparasites:bogle", lenciaLoot, "Items you want the Bogle to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      lenciaOrbEffects = cfg.getStringList("Bogle Orb Effects", "srparasites:bogle", lenciaOrbEffects, "Orb effects " + orb);
      lenciaGriefing = cfg.getBoolean("Bogle Griefing", "srparasites:bogle", lenciaGriefing, "Set to true if you want the Bogle to destroy blocks on explosion.");
   }

   private static void initpheonConfig(Configuration cfg) {
      String mobTable = " Ex. \"minecraft:zombie;4;2\" Where: \n \"minecraft:zombie\" is the entity, \n \"4\" is the maximum number of entities, \n \"2\" is the minimum number of entities. \n";
      String orb = " Ex. \"0;60;2;minecraft:fire_resistance;4;3\" Where: \n \"0\" mode of applying, 0 to non parasite mobs, 1 to self, 2 to parasite mobs, \n \"60\" potion duration in seconds, \n \"2\" potion amplifier, \n \"minecraft:fire_resistance\" potion itself, \n \"4\" if != 0 then amplifier += (mobs inside the orb / this value), \n \"3\" if != 0 then amplifier += (mobs inside the orb / this value) * 20. \n";
      cfg.addCustomCategoryComment("srparasites:haunter", "Haunter \n Base Health: " + SRPAttributes.PHEON_HEALTH + " \n Base Armor: " + SRPAttributes.PHEON_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.PHEON_KD_RESISTANCE);
      pheonEnabled = cfg.getBoolean("Haunter Enabled", "srparasites:haunter", pheonEnabled, "Set to false if you want to disable Haunter.");
      pheonHealthMultiplier = cfg.getFloat("Haunter" + health, "srparasites:haunter", 1.0F, 0.01F, 100.0F, "Health multiplier for Haunter.");
      pheonDamageMultiplier = cfg.getFloat("Haunter" + damage, "srparasites:haunter", 1.0F, 0.01F, 100.0F, "Damage multiplier for Haunter.");
      pheonArmorMultiplier = cfg.getFloat("Haunter" + armor, "srparasites:haunter", 1.0F, 0.01F, 100.0F, "Armor multiplier for Haunter.");
      pheonKDResistanceMultiplier = cfg.getFloat("Haunter" + kd, "srparasites:haunter", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Haunter.");
      pheonSpawnRate = cfg.getInt("Haunter SpawnWeight", "srparasites:haunter", pheonSpawnRate, 0, 100, "Spawn rate for Haunter (This value is ignored if Evolution Phases are enabled, it has its own option).");
      pheonLoot = cfg.getStringList("Haunter Loot Table", "srparasites:haunter", pheonLoot, "Items you want the Haunter to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      pheonOrbEffects = cfg.getStringList("Haunter Orb Effects", "srparasites:haunter", pheonOrbEffects, "Orb effects " + orb);
   }

   private static void initcruxaConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:cruxa", "Crux \n Base Health: " + SRPAttributes.CRUXA_HEALTH + " \n Base Damage: " + SRPAttributes.CRUXA_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.CRUXA_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.CRUXA_KD_RESISTANCE);
      cruxaEnabled = cfg.getBoolean("Crux Enabled", "srparasites:cruxa", cruxaEnabled, "Set to false if you want to disable Crux.");
      cruxaHealthMultiplier = cfg.getFloat("Crux" + health, "srparasites:cruxa", 1.0F, 0.01F, 100.0F, "Health multiplier for Crux.");
      cruxaDamageMultiplier = cfg.getFloat("Crux" + damage, "srparasites:cruxa", 1.0F, 0.01F, 100.0F, "Damage multiplier for Crux.");
      cruxaArmorMultiplier = cfg.getFloat("Crux" + armor, "srparasites:cruxa", 1.0F, 0.01F, 100.0F, "Armor multiplier for Crux.");
      cruxaKDResistanceMultiplier = cfg.getFloat("Crux" + kd, "srparasites:cruxa", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Crux.");
      cruxaSpawnRate = cfg.getInt("Crux SpawnWeight", "srparasites:cruxa", cruxaSpawnRate, 0, 100, "Spawn rate for Crux (This value is ignored if Evolution Phases are enabled, it has its own option).");
      cruxaLoot = cfg.getStringList("Crux Loot Table", "srparasites:cruxa", cruxaLoot, "Items you want the Crux to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      cruxaDamageGain = (double)cfg.getFloat("Crux Damage Stack Value", "srparasites:cruxa", (float)cruxaDamageGain, 0.01F, 1.0F, "Percentage of damage the Crux will use to increase its damage when killing mobs.");
      cruxaDamageCap = cfg.getInt("Crux Damage Stack Cap", "srparasites:cruxa", cruxaDamageCap, 0, 100, "Limit on how many times the Crux can increase its own damage.");
   }

   private static void initheedConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:heed", "Heed \n Base Health: " + SRPAttributes.HEED_HEALTH + " \n Base Damage: " + SRPAttributes.HEED_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.HEED_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.HEED_KD_RESISTANCE);
      heedEnabled = cfg.getBoolean("Heed Enabled", "srparasites:heed", heedEnabled, "Set to false if you want to disable Heed.");
      heedHealthMultiplier = cfg.getFloat("Heed" + health, "srparasites:heed", 1.0F, 0.01F, 100.0F, "Health multiplier for Heed.");
      heedDamageMultiplier = cfg.getFloat("Heed" + damage, "srparasites:heed", 1.0F, 0.01F, 100.0F, "Damage multiplier for Heed.");
      heedArmorMultiplier = cfg.getFloat("Heed" + armor, "srparasites:heed", 1.0F, 0.01F, 100.0F, "Armor multiplier for Heed.");
      heedKDResistanceMultiplier = cfg.getFloat("Heed" + kd, "srparasites:heed", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Heed.");
      heedSpawnRate = cfg.getInt("Heed SpawnWeight", "srparasites:heed", heedSpawnRate, 0, 100, "Spawn rate for Heed (This value is ignored if Evolution Phases are enabled, it has its own option).");
      heedLoot = cfg.getStringList("Heed Loot Table", "srparasites:heed", heedLoot, "Items you want the Heed to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
   }

   private static void initLumConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:devourer", "Devourer \n Base Health: " + SRPAttributes.LUM_HEALTH + " \n Base Damage: " + SRPAttributes.LUM_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.LUM_ARMOR + " \n Base Knockback Resistance: " + SRPAttributes.LUM_KD_RESISTANCE);
      lumEnabled = cfg.getBoolean("Primitive Devourer Enabled", "srparasites:devourer", lumEnabled, "Set to false if you want to disable Primitive Devourer.");
      lumHealthMultiplier = cfg.getFloat("Primitive Devourer" + health, "srparasites:devourer", 1.0F, 0.01F, 100.0F, "Health multiplier for Primitive Devourer.");
      lumDamageMultiplier = cfg.getFloat("Primitive Devourer" + damage, "srparasites:devourer", 1.0F, 0.01F, 100.0F, "Damage multiplier for Primitive Devourer.");
      lumArmorMultiplier = cfg.getFloat("Primitive Devourer" + armor, "srparasites:devourer", 1.0F, 0.01F, 100.0F, "Armor multiplier for Primitive Devourer.");
      lumKDResistanceMultiplier = cfg.getFloat("Primitive Devourer" + kd, "srparasites:devourer", 1.0F, 0.01F, 100.0F, "Knockback Resistance multiplier for Primitive Devourer.");
      lumSpawnRate = cfg.getInt("Primitive Devourer SpawnWeight", "srparasites:devourer", lumSpawnRate, 0, 100, "Spawn rate for Primitive Devourer (This value is ignored if Evolution Phases are enabled, it has its own option).");
      lumLoot = cfg.getStringList("Primitive Devourer Loot Table", "srparasites:devourer", lumLoot, "Items you want the Primitive Devourer to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      lumadaptedhealth = cfg.getFloat("Stage Adapted additional Health", "srparasites:devourer", lumadaptedhealth, 0.01F, 100.0F, "Additional health for Adapted Devourer.");
      lumadapteddamage = cfg.getFloat("Stage Adapted additional Damage", "srparasites:devourer", lumadapteddamage, 0.01F, 100.0F, "Additional damage for Adapted Devourer.");
      lumadaptedarmor = cfg.getFloat("Stage Adapted additional Armor", "srparasites:devourer", lumadaptedarmor, 0.01F, 100.0F, "Additional armor for Adapted Devourer.");
      lumadaptedkdresistance = cfg.getFloat("Stage Adapted additional Knockback Resistance", "srparasites:devourer", lumadaptedkdresistance, 0.01F, 100.0F, "Additional Knockback Resistance for Adapted Devourer.");
      lumadaptedloot = cfg.getStringList("Stage Adapted loot Table", "srparasites:devourer", lumadaptedloot, "Items you want the Adapted Devourer to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      lumASpawnRate = cfg.getInt("Stage Adapted spawnweight", "srparasites:devourer", lumASpawnRate, 0, 100, "Spawn rate for Adapted Devourer (not working) (This value is ignored if Evolution Phases are enabled, it has its own option).");
   }

   private static void initnakConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:seizer", "Seizer \n Base Health: " + SRPAttributes.NAK_HEALTH + " \n Base Damage: " + SRPAttributes.NAK_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.NAK_ARMOR);
      nakEnabled = cfg.getBoolean("Seizer Enabled", "srparasites:seizer", nakEnabled, "Set to false if you want to disable Seizer.");
      nakHealthMultiplier = cfg.getFloat("Seizer" + health, "srparasites:seizer", 1.0F, 0.01F, 100.0F, "Health multiplier for Seizer.");
      nakDamageMultiplier = cfg.getFloat("Seizer" + damage, "srparasites:seizer", 1.0F, 0.01F, 100.0F, "Damage multiplier for Seizer.");
      nakArmorMultiplier = cfg.getFloat("Seizer" + armor, "srparasites:seizer", 1.0F, 0.01F, 100.0F, "Armor multiplier for Seizer.");
      nakSpawnRate = cfg.getInt("Seizer SpawnWeight", "srparasites:seizer", nakSpawnRate, 0, 100, "Spawn rate for Seizer (This value is ignored if Evolution Phases are enabled, it has its own option).");
      nakLoot = cfg.getStringList("Seizer Loot Table", "srparasites:seizer", nakLoot, "Items you want the Seizer to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
   }

   private static void initdroppodConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:anc_pod", "Droppod \n Base Health: " + SRPAttributes.POD1_HEALTH + " \n Base Damage: " + SRPAttributes.POD1_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.POD1_ARMOR);
      String eff = " Ex. \"0;1;minecraft:fire_resistance\" Where: \n \"0\" potion duration in seconds, \n \"1\" potion amplifier, \n \"minecraft:fire_resistance\" potion itself. \n";
      pod1HealthMultiplier = cfg.getFloat("DropPod " + health, "srparasites:anc_pod", 1.0F, 0.01F, 100.0F, "Health multiplier for Droppod.");
      pod1DamageMultiplier = cfg.getFloat("DropPod " + damage, "srparasites:anc_pod", 1.0F, 0.01F, 100.0F, "Damage multiplier for Droppod.");
      pod1ArmorMultiplier = cfg.getFloat("DropPod " + armor, "srparasites:anc_pod", 1.0F, 0.01F, 100.0F, "Armor multiplier for Droppod.");
      pod1Loot = cfg.getStringList("DropPod Loot Table", "srparasites:anc_pod", pod1Loot, "Items you want the Droppod to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      pod1Effects = cfg.getStringList("DropPod Effects", "srparasites:anc_pod", pod1Effects, "Potion effects that will give to nearby mobs when exploding " + eff);
   }

   private static void initbeckonSIConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:beckon_i", "Beckon Stage I \n Base Health: " + SRPAttributes.VENKROL_HEALTH + " \n Base Damage: " + SRPAttributes.VENKROL_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.VENKROL_ARMOR);
      String venkrolName = "Stage I Beckon";
      String venkrolsiiName = "Stage II Beckon";
      String RSspawning = " Ex. \"ground;minecraft:zombie;0.1;1\"  Where: \n \"ground\" is for the entity type (ground, air) (air type spawning is triggered when the target y's value is higher than the host plus 3), \n \"minecraft:zombie\" is for the entity, \n \"0.1\" is for the chance to spawn, \n \"1\" is for the cost the entity has. \n";
      venkrolHealthMultiplier = cfg.getFloat(venkrolName + health, "srparasites:beckon_i", 1.0F, 0.01F, 100.0F, "Health multiplier for " + venkrolName + ".");
      venkrolDamageMultiplier = cfg.getFloat(venkrolName + damage, "srparasites:beckon_i", 1.0F, 0.01F, 100.0F, "Damage multiplier for " + venkrolName + ".");
      venkrolArmorMultiplier = cfg.getFloat(venkrolName + armor, "srparasites:beckon_i", 1.0F, 0.01F, 100.0F, "Armor multiplier for " + venkrolName + ".");
      venkrolSpawnrate = cfg.getInt(venkrolName + " Spawnweight", "srparasites:beckon_i", venkrolSpawnrate, 0, 100, "Spawn weight for " + venkrolName + " (NOTE: They will spawn like vanilla passive creatures).");
      venkrolRange = cfg.getInt(venkrolName + " Block Infestation Range", "srparasites:beckon_i", venkrolRange, 0, 100, "Block infestation radius for " + venkrolName + ".");
      venkrolRangeY = cfg.getInt(venkrolName + " Block Infestation Range Y", "srparasites:beckon_i", venkrolRangeY, 0, 100, "Block infestation radius (upwards/downwards) for " + venkrolName + ".");
      venkrolCooldown = cfg.getInt(venkrolName + " Cooldown", "srparasites:beckon_i", venkrolCooldown, 0, 10000, "Summoning cooldown (in seconds) for " + venkrolName + ".");
      venkrollimit = cfg.getInt(venkrolName + " Limit", "srparasites:beckon_i", venkrollimit, 0, 10000, "Number of attacks before its cooldown for " + venkrolName + ".");
      venkrolTotalActiveMobs = cfg.getInt(venkrolName + " Total Active Mobs", "srparasites:beckon_i", venkrolTotalActiveMobs, 0, 100, "Number of total points used in mob spawning for " + venkrolName + ".");
      venkrolBlockTime = cfg.getInt(venkrolName + " Block Infestation Cooldown", "srparasites:beckon_i", venkrolBlockTime, 4, 10000, "Minimum time required (in seconds) to infest nearby block for " + venkrolName + ".");
      venkrolBlockDamage = cfg.getFloat(venkrolName + " Block Infestation Damage", "srparasites:beckon_i", venkrolBlockDamage, 0.01F, 100.0F, "Damage when walking on the blocks infested by  " + venkrolName + ".");
      venkrolBlockChance = cfg.getFloat(venkrolName + " Block Infestation Damage Chance", "srparasites:beckon_i", venkrolBlockChance, 0.0F, 1.0F, "Chance to damage when walking on the blocks infested by  " + venkrolName + ".");
      venkrolmoblist = cfg.getStringList(venkrolName + " Mob List", "srparasites:beckon_i", venkrolmoblist, "Mob list for " + venkrolName + "." + RSspawning + "Stage I cannot trigger the air condition. \n");
      venkrolCAMinimumV = cfg.getInt(venkrolName + " CA number", "srparasites:beckon_i", venkrolCAMinimumV, 0, 10000, "Number  for " + venkrolName + " required to do a Collective Attack (only one will attack) for " + venkrolName + ".");
      venkrolCAExtraM = cfg.getFloat(venkrolName + " CA mobs", "srparasites:beckon_i", venkrolCAExtraM, 0.0F, 100.0F, "Value of each " + venkrolName + " to spawn extra mobs (the mobs will not add up to the total points) for " + venkrolName + ".");
      venkrolLoot = cfg.getStringList(venkrolName + " Loot Table", "srparasites:beckon_i", venkrolLoot, "Items you want the " + venkrolName + " to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
   }

   private static void initbeckonSIIConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:beckon_ii", "Beckon Stage II \n Base Health: " + SRPAttributes.VENKROLSII_HEALTH + " \n Base Damage: " + SRPAttributes.VENKROLSII_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.VENKROLSII_ARMOR);
      String venkrolsiiName = "Stage II Beckon";
      String venkrolsiiiName = "Stage III Beckon";
      String RSspawning = " Ex. \"ground;minecraft:zombie;0.1;1\"  Where: \n \"ground\" is for the entity type (ground, air) (air type spawning is triggered when the target y's value is higher than the host plus 3), \n \"minecraft:zombie\" is for the entity, \n \"0.1\" is for the chance to spawn, \n \"1\" is for the cost the entity has. \n";
      venkrolsiiHealthMultiplier = cfg.getFloat(venkrolsiiName + health, "srparasites:beckon_ii", 1.0F, 0.01F, 100.0F, "Health multiplier for " + venkrolsiiName + ".");
      venkrolsiiDamageMultiplier = cfg.getFloat(venkrolsiiName + damage, "srparasites:beckon_ii", 1.0F, 0.01F, 100.0F, "Damage multiplier for " + venkrolsiiName + ".");
      venkrolsiiArmorMultiplier = cfg.getFloat(venkrolsiiName + armor, "srparasites:beckon_ii", 1.0F, 0.01F, 100.0F, "Armor multiplier for " + venkrolsiiName + ".");
      venkrolsiiRange = cfg.getInt(venkrolsiiName + " Block Infestation Range", "srparasites:beckon_ii", venkrolsiiRange, 0, 100, "Block infestation radius for " + venkrolsiiName + ".");
      venkrolsiiRangeY = cfg.getInt(venkrolsiiName + " Block Infestation Range Y", "srparasites:beckon_ii", venkrolsiiRangeY, 0, 100, "Block infestation radius (upwards/downwards) for " + venkrolsiiName + ".");
      venkrolsiiCooldown = cfg.getInt(venkrolsiiName + " Cooldown", "srparasites:beckon_ii", venkrolsiiCooldown, 0, 10000, "Summoning cooldown (in seconds) for " + venkrolsiiName + ".");
      venkrolsiilimit = cfg.getInt(venkrolsiiName + " Limit", "srparasites:beckon_ii", venkrolsiilimit, 0, 10000, "Number of attacks before its cooldown for " + venkrolsiiName + ".");
      venkrolsiiTotalActiveMobs = cfg.getInt(venkrolsiiName + " Total Active Mobs", "srparasites:beckon_ii", venkrolsiiTotalActiveMobs, 0, 100, "Number of total points used in mob spawning for " + venkrolsiiName + ".");
      venkrolsiiBlockTime = cfg.getInt(venkrolsiiName + " Block Infestation Cooldown", "srparasites:beckon_ii", venkrolsiiBlockTime, 4, 10000, "Minimum time required (in seconds) to infest nearby block for " + venkrolsiiName + ".");
      venkrolsiiBlockDamage = cfg.getFloat(venkrolsiiName + " Block Infestation Damage", "srparasites:beckon_ii", venkrolsiiBlockDamage, 0.01F, 100.0F, "Damage when walking on the blocks infested by  " + venkrolsiiName + ".");
      venkrolsiiBlockChance = cfg.getFloat(venkrolsiiName + " Block Infestation Damage Chance", "srparasites:beckon_ii", venkrolsiiBlockChance, 0.0F, 1.0F, "Chance to damage when walking on the blocks infested by  " + venkrolsiiName + ".");
      venkrolsiimoblist = cfg.getStringList(venkrolsiiName + " Mob List", "srparasites:beckon_ii", venkrolsiimoblist, "Mob list for " + venkrolsiiName + "." + RSspawning);
      venkrolsiiCAMinimumV = cfg.getInt(venkrolsiiName + " CA number", "srparasites:beckon_ii", venkrolsiiCAMinimumV, 0, 10000, "Number  for " + venkrolsiiName + " required to do a Collective Attack (only one will attack) for " + venkrolsiiName + ".");
      venkrolsiiCAExtraM = cfg.getFloat(venkrolsiiName + " CA mobs", "srparasites:beckon_ii", venkrolsiiCAExtraM, 0.0F, 100.0F, "Value of each " + venkrolsiiName + " to spawn extra mobs (the mobs will not add up to the total points) for " + venkrolsiiName + ".");
      venkrolsiiLoot = cfg.getStringList(venkrolsiiName + " Loot Table", "srparasites:beckon_ii", venkrolsiiLoot, "Items you want the " + venkrolsiiName + " to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
   }

   private static void initbeckonSIIIConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:beckon_iii", "Beckon Stage III \n Base Health: " + SRPAttributes.VENKROLSIII_HEALTH + " \n Base Damage: " + SRPAttributes.VENKROLSIII_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.VENKROLSIII_ARMOR);
      String venkrolsiiName = "Stage IV Beckon";
      String venkrolsiiiName = "Stage III Beckon";
      String RSspawning = " Ex. \"ground;minecraft:zombie;0.1;1\"  Where: \n \"ground\" is for the entity type (ground, air) (air type spawning is triggered when the target y's value is higher than the host plus 3), \n \"minecraft:zombie\" is for the entity, \n \"0.1\" is for the chance to spawn, \n \"1\" is for the cost the entity has. \n";
      venkrolsiiiHealthMultiplier = cfg.getFloat(venkrolsiiiName + health, "srparasites:beckon_iii", 1.0F, 0.01F, 100.0F, "Health multiplier for " + venkrolsiiiName + ".");
      venkrolsiiiDamageMultiplier = cfg.getFloat(venkrolsiiiName + damage, "srparasites:beckon_iii", 1.0F, 0.01F, 100.0F, "Damage multiplier for " + venkrolsiiiName + ".");
      venkrolsiiiArmorMultiplier = cfg.getFloat(venkrolsiiiName + armor, "srparasites:beckon_iii", 1.0F, 0.01F, 100.0F, "Armor multiplier for " + venkrolsiiiName + ".");
      venkrolsiiiRange = cfg.getInt(venkrolsiiiName + " Block Infestation Range", "srparasites:beckon_iii", venkrolsiiiRange, 0, 100, "Block infestation radius for " + venkrolsiiiName + ".");
      venkrolsiiiRangeY = cfg.getInt(venkrolsiiiName + " Block Infestation Range Y", "srparasites:beckon_iii", venkrolsiiiRangeY, 0, 100, "Block infestation radius (upwards/downwards) for " + venkrolsiiiName + ".");
      venkrolsiiiCooldown = cfg.getInt(venkrolsiiiName + " Cooldown", "srparasites:beckon_iii", venkrolsiiiCooldown, 0, 10000, "Summoning cooldown (in seconds) for " + venkrolsiiiName + ".");
      venkrolsiiilimit = cfg.getInt(venkrolsiiiName + " Limit", "srparasites:beckon_iii", venkrolsiiilimit, 0, 10000, "Number attacks before its cooldown for " + venkrolsiiiName + ".");
      venkrolsiiiTotalActiveMobs = cfg.getInt(venkrolsiiiName + " Total Active Mobs", "srparasites:beckon_iii", venkrolsiiiTotalActiveMobs, 0, 100, "Number of total points used in mob spawning for " + venkrolsiiiName + ".");
      venkrolsiiiBlockTime = cfg.getInt(venkrolsiiiName + " Block Infestation Cooldown", "srparasites:beckon_iii", venkrolsiiiBlockTime, 4, 10000, "Minimum time required (in seconds) to infest nearby block for " + venkrolsiiiName + ".");
      venkrolsiiiBlockDamage = cfg.getFloat(venkrolsiiiName + " Block Infestation Damage", "srparasites:beckon_iii", venkrolsiiiBlockDamage, 0.01F, 100.0F, "Damage when walking on the blocks infested by  " + venkrolsiiiName + ".");
      venkrolsiiiBlockChance = cfg.getFloat(venkrolsiiiName + " Block Infestation Damage Chance", "srparasites:beckon_iii", venkrolsiiiBlockChance, 0.0F, 1.0F, "Chance to damage when walking on the blocks infested by  " + venkrolsiiiName + ".");
      venkrolsiiimoblist = cfg.getStringList(venkrolsiiiName + " Mob List", "srparasites:beckon_iii", venkrolsiiimoblist, "Mob list for " + venkrolsiiiName + "." + RSspawning);
      venkrolsiiiCAMinimumV = cfg.getInt(venkrolsiiiName + " CA number", "srparasites:beckon_iii", venkrolsiiiCAMinimumV, 0, 10000, "Number  for " + venkrolsiiiName + " required to do a Collective Attack (only one will attack) for " + venkrolsiiiName + ".");
      venkrolsiiiCAExtraM = cfg.getFloat(venkrolsiiiName + " CA mobs", "srparasites:beckon_iii", venkrolsiiiCAExtraM, 0.0F, 100.0F, "Value of each " + venkrolsiiiName + " to spawn extra mobs (the mobs will not add up to the total points) for " + venkrolsiiiName + ".");
      venkrolsiiiLoot = cfg.getStringList(venkrolsiiiName + " Loot Table", "srparasites:beckon_iii", venkrolsiiiLoot, "Items you want the " + venkrolsiiiName + " to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
   }

   private static void initbeckonSIVConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:beckon_iv", "Beckon Stage IV \n Base Health: " + SRPAttributes.VENKROLSIV_HEALTH + " \n Base Damage: " + SRPAttributes.VENKROLSIV_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.VENKROLSIV_ARMOR);
      String venkrolsivName = "Stage IV Beckon";
      String RSspawning = " Ex. \"ground;minecraft:zombie;0.1;1\"  Where: \n \"ground\" is for the entity type (ground, air) (air type spawning is triggered when the target y's value is higher than the host plus 3), \n \"minecraft:zombie\" is for the entity, \n \"0.1\" is for the chance to spawn, \n \"1\" is for the cost the entity has. \n";
      venkrolsivHealthMultiplier = cfg.getFloat(venkrolsivName + health, "srparasites:beckon_iv", 1.0F, 0.01F, 100.0F, "Health multiplier for " + venkrolsivName + ".");
      venkrolsivDamageMultiplier = cfg.getFloat(venkrolsivName + damage, "srparasites:beckon_iv", 1.0F, 0.01F, 100.0F, "Damage multiplier for " + venkrolsivName + ".");
      venkrolsivArmorMultiplier = cfg.getFloat(venkrolsivName + armor, "srparasites:beckon_iv", 1.0F, 0.01F, 100.0F, "Armor multiplier for " + venkrolsivName + ".");
      venkrolsivCooldown = cfg.getInt(venkrolsivName + " Cooldown", "srparasites:beckon_iv", venkrolsivCooldown, 0, 10000, "Summoning cooldown (in seconds) for " + venkrolsivName + ".");
      venkrolsivlimit = cfg.getInt(venkrolsivName + " Limit", "srparasites:beckon_iv", venkrolsivlimit, 0, 10000, "Number attacks before its cooldown for " + venkrolsivName + ".");
      venkrolsivTotalActiveMobs = cfg.getInt(venkrolsivName + " Total Active Mobs", "srparasites:beckon_iv", venkrolsivTotalActiveMobs, 0, 100, "Number of total points used in mob spawning for " + venkrolsivName + ".");
      venkrolsivmoblist = cfg.getStringList(venkrolsivName + " Mob List", "srparasites:beckon_iv", venkrolsivmoblist, "Mob list for " + venkrolsivName + "." + RSspawning);
      venkrolsivCAMinimumV = cfg.getInt(venkrolsivName + " CA number", "srparasites:beckon_iv", venkrolsivCAMinimumV, 0, 10000, "Number  for " + venkrolsivName + " required to do a Collective Attack (only one will attack) for " + venkrolsivName + ".");
      venkrolsivCAExtraM = cfg.getFloat(venkrolsivName + " CA mobs", "srparasites:beckon_iv", venkrolsivCAExtraM, 0.0F, 100.0F, "Value of each " + venkrolsivName + " to spawn extra mobs (the mobs will not add up to the total points) for " + venkrolsivName + ".");
      venkrolsivLoot = cfg.getStringList(venkrolsivName + " Loot Table", "srparasites:beckon_iv", venkrolsivLoot, "Items you want the " + venkrolsivName + " to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
   }

   private static void initreinforcerSIConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:dispatcher_si", "Dispatcher Stage I \n Base Health: " + SRPAttributes.DOD_HEALTH + " \n Base Damage: " + SRPAttributes.DOD_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.DOD_ARMOR);
      String dodsiName = "Stage I Dispatcher";
      String entry2 = " Ex. \"200;minecraft:speed;3\"  Where: \n \"200\" is the time in ticks, \n \"minecraft:speed\" is the potion itself (the potion will apply regardless of whether the parasite is near a node or not), \n \"3\" is the amplifier of the effect ";
      dodsiHealthMultiplier = cfg.getFloat(dodsiName + health, "srparasites:dispatcher_si", 1.0F, 0.01F, 100.0F, "Health multiplier for " + dodsiName + ".");
      dodsiDamageMultiplier = cfg.getFloat(dodsiName + damage, "srparasites:dispatcher_si", 1.0F, 0.01F, 100.0F, "Damage multiplier for " + dodsiName + ".");
      dodsiArmorMultiplier = cfg.getFloat(dodsiName + armor, "srparasites:dispatcher_si", 1.0F, 0.01F, 100.0F, "Armor multiplier for " + dodsiName + ".");
      dodsiLoot = cfg.getStringList(dodsiName + " Loot Table", "srparasites:dispatcher_si", dodsiLoot, "Items you want the " + dodsiName + " to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      dodsiFollowRangeMult = cfg.getInt(dodsiName + " Extra Follow Range", "srparasites:dispatcher_si", dodsiFollowRangeMult, 1, 100, "Follow range Multiplier for " + dodsiName + ".");
      dodsiEffects = cfg.getStringList(dodsiName + " Effect List", "srparasites:dispatcher_si", dodsiEffects, "List of potion effects that the " + dodsiName + " will apply to relocated parasites." + entry2);
   }

   private static void initreinforcerSIIConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:dispatcher_sii", "Dispatcher Stage II \n Base Health: " + SRPAttributes.DODSII_HEALTH + " \n Base Damage: " + SRPAttributes.DODSII_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.DODSII_ARMOR);
      String dodsiiName = "Stage II Dispatcher";
      String entry2 = " Ex. \"200;minecraft:speed;3\"  Where: \n \"200\" is the time in ticks, \n \"minecraft:speed\" is the potion itself (the potion will apply regardless of whether the parasite is near a node or not), \n \"3\" is the amplifier of the effect ";
      dodsiiHealthMultiplier = cfg.getFloat(dodsiiName + health, "srparasites:dispatcher_sii", 1.0F, 0.01F, 100.0F, "Health multiplier for " + dodsiiName + ".");
      dodsiiDamageMultiplier = cfg.getFloat(dodsiiName + damage, "srparasites:dispatcher_sii", 1.0F, 0.01F, 100.0F, "Damage multiplier for " + dodsiiName + ".");
      dodsiiArmorMultiplier = cfg.getFloat(dodsiiName + armor, "srparasites:dispatcher_sii", 1.0F, 0.01F, 100.0F, "Armor multiplier for " + dodsiiName + ".");
      dodsiiLoot = cfg.getStringList(dodsiiName + " Loot Table", "srparasites:dispatcher_sii", dodsiiLoot, "Items you want the " + dodsiiName + " to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      dodsiiFollowRangeMult = cfg.getInt(dodsiiName + " Extra Follow Range", "srparasites:dispatcher_sii", dodsiiFollowRangeMult, 1, 100, "Follow range Multiplier for " + dodsiiName + ".");
      dodsiiEffects = cfg.getStringList(dodsiiName + " Effect List", "srparasites:dispatcher_sii", dodsiiEffects, "List of potion effects that the " + dodsiiName + " will apply to relocated parasites." + entry2);
   }

   private static void initreinforcerSIIIConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:dispatcher_siii", "Dispatcher Stage III \n Base Health: " + SRPAttributes.DODSIII_HEALTH + " \n Base Damage: " + SRPAttributes.DODSIII_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.DODSIII_ARMOR);
      String dodsiiiName = "Stage III Dispatcher";
      String entry2 = " Ex. \"200;minecraft:speed;3\"  Where: \n \"200\" is the time in ticks, \n \"minecraft:speed\" is the potion itself (the potion will apply regardless of whether the parasite is near a node or not), \n \"3\" is the amplifier of the effect ";
      dodsiiiHealthMultiplier = cfg.getFloat(dodsiiiName + health, "srparasites:dispatcher_siii", 1.0F, 0.01F, 100.0F, "Health multiplier for " + dodsiiiName + ".");
      dodsiiiDamageMultiplier = cfg.getFloat(dodsiiiName + damage, "srparasites:dispatcher_siii", 1.0F, 0.01F, 100.0F, "Damage multiplier for " + dodsiiiName + ".");
      dodsiiiArmorMultiplier = cfg.getFloat(dodsiiiName + armor, "srparasites:dispatcher_siii", 1.0F, 0.01F, 100.0F, "Armor multiplier for " + dodsiiiName + ".");
      dodsiiiLoot = cfg.getStringList(dodsiiiName + " Loot Table", "srparasites:dispatcher_siii", dodsiiiLoot, "Items you want the " + dodsiiiName + " to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      dodsiiiFollowRangeMult = cfg.getInt(dodsiiiName + " Extra Follow Range", "srparasites:dispatcher_siii", dodsiiiFollowRangeMult, 1, 100, "Follow range Multiplier for " + dodsiiiName + ".");
      dodsiiiEffects = cfg.getStringList(dodsiiiName + " Effect List", "srparasites:dispatcher_siii", dodsiiiEffects, "List of potion effects that the " + dodsiiiName + " will apply to relocated parasites." + entry2);
   }

   private static void initreinforcerSIVConfig(Configuration cfg) {
      cfg.addCustomCategoryComment("srparasites:dispatcher_siv", "Dispatcher Stage IV \n Base Health: " + SRPAttributes.DODSIV_HEALTH + " \n Base Damage: " + SRPAttributes.DODSIV_ATTACK_DAMAGE + " \n Base Armor: " + SRPAttributes.DODSIV_ARMOR);
      String dodsivName = "Stage IV Dispatcher";
      String entry2 = " Ex. \"200;minecraft:speed;3\"  Where: \n \"200\" is the time in ticks, \n \"minecraft:speed\" is the potion itself (the potion will apply regardless of whether the parasite is near a node or not), \n \"3\" is the amplifier of the effect ";
      dodsivHealthMultiplier = cfg.getFloat(dodsivName + health, "srparasites:dispatcher_siv", 1.0F, 0.01F, 100.0F, "Health multiplier for " + dodsivName + ".");
      dodsivDamageMultiplier = cfg.getFloat(dodsivName + damage, "srparasites:dispatcher_siv", 1.0F, 0.01F, 100.0F, "Damage multiplier for " + dodsivName + ".");
      dodsivArmorMultiplier = cfg.getFloat(dodsivName + armor, "srparasites:dispatcher_siv", 1.0F, 0.01F, 100.0F, "Armor multiplier for " + dodsivName + ".");
      dodsivLoot = cfg.getStringList(dodsivName + " Loot Table", "srparasites:dispatcher_siv", dodsivLoot, "Items you want the " + dodsivName + " to drop. Ex. \"minecraft:nether_star;100;5;true\"" + lootXp);
      dodsivFollowRangeMult = cfg.getInt(dodsivName + " Extra Follow Range", "srparasites:dispatcher_siv", dodsivFollowRangeMult, 1, 100, "Follow range Multiplier for " + dodsivName + ".");
      dodsivEffects = cfg.getStringList(dodsivName + " Effect List", "srparasites:dispatcher_siv", dodsivEffects, "List of potion effects that the " + dodsivName + " will apply to relocated parasites." + entry2);
   }

   public static void initConfig(FMLPreInitializationEvent e) {
      VFile2 directory = e.getModConfigurationDirectory();
      CommonProxy.configMobs = new Configuration(new VFile2(directory, "srparasites/SRParasitesMobs.cfg"));
      readConfig();
   }

   public static boolean readConfig() {
      Configuration cfgM = CommonProxy.configMobs;

      boolean var2;
      try {
         cfgM.load();
         initGeneralMobsConfig(cfgM);
         initShycoConfig(cfgM);
         initemanaConfig(cfgM);
         inithullConfig(cfgM);
         initcanraConfig(cfgM);
         initnoglaConfig(cfgM);
         initzetmoConfig(cfgM);
         initarachnidaConfig(cfgM);
         initLumConfig(cfgM);
         initlodoConfig(cfgM);
         initratholConfig(cfgM);
         initbutholConfig(cfgM);
         initmudoConfig(cfgM);
         initkolConfig(cfgM);
         initnuuhConfig(cfgM);
         initinfbearConfig(cfgM);
         initinfendermanConfig(cfgM);
         initdorpaConfig(cfgM);
         initinfhumanConfig(cfgM);
         initinfcowConfig(cfgM);
         initinfsheepConfig(cfgM);
         initinfwolfConfig(cfgM);
         initinfpigConfig(cfgM);
         initinfvillagerConfig(cfgM);
         initinfhorseConfig(cfgM);
         initinfadventurerConfig(cfgM);
         initINFDRAGONEConfig(cfgM);
         initheedConfig(cfgM);
         initcruxaConfig(cfgM);
         inithostConfig(cfgM);
         initherdConfig(cfgM);
         initthrallConfig(cfgM);
         initinhooSConfig(cfgM);
         initinhooMConfig(cfgM);
         initnakConfig(cfgM);
         inittonroConfig(cfgM);
         initunvoConfig(cfgM);
         initbeckonSIConfig(cfgM);
         initbeckonSIIConfig(cfgM);
         initbeckonSIIIConfig(cfgM);
         initbeckonSIVConfig(cfgM);
         initreinforcerSIConfig(cfgM);
         initreinforcerSIIConfig(cfgM);
         initreinforcerSIIIConfig(cfgM);
         initreinforcerSIVConfig(cfgM);
         initoroncoConfig(cfgM);
         initterlaConfig(cfgM);
         initdroppodConfig(cfgM);
         initalafhaConfig(cfgM);
         initganroConfig(cfgM);
         initangedConfig(cfgM);
         initombooConfig(cfgM);
         initesorConfig(cfgM);
         initorchConfig(cfgM);
         initflogConfig(cfgM);
         initjinjoConfig(cfgM);
         initvestaConfig(cfgM);
         initpheonConfig(cfgM);
         initelviaConfig(cfgM);
         initlenciaConfig(cfgM);
         boolean e = true;
         return e;
      } catch (Exception var6) {
         SRPMain.logger.log(Level.ERROR, "Problem loading configuration file", var6);
         var2 = false;
      } finally {
         if (cfgM.hasChanged()) {
            cfgM.save();
         }

      }

      return var2;
   }
}
