package com.lycanitesmobs.core.info;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.info.altar.AltarInfoAmalgalich;
import com.lycanitesmobs.core.info.altar.AltarInfoAsmodeus;
import com.lycanitesmobs.core.info.altar.AltarInfoCelestialGeonach;
import com.lycanitesmobs.core.info.altar.AltarInfoCrimsonEpion;
import com.lycanitesmobs.core.info.altar.AltarInfoEbonCacodemon;
import com.lycanitesmobs.core.info.altar.AltarInfoLunarGrue;
import com.lycanitesmobs.core.info.altar.AltarInfoMottleAbaia;
import com.lycanitesmobs.core.info.altar.AltarInfoPhosphorescentChupacabra;
import com.lycanitesmobs.core.info.altar.AltarInfoRahovart;
import com.lycanitesmobs.core.info.altar.AltarInfoRoyalArchvile;
import com.lycanitesmobs.core.info.altar.AltarInfoUmberLobber;
import com.lycanitesmobs.core.mobevent.effects.StructureBuilder;
import com.lycanitesmobs.core.mobevent.trigger.AltarMobEventTrigger;
import com.lycanitesmobs.core.worldgen.mobevents.AmalgalichStructureBuilder;
import com.lycanitesmobs.core.worldgen.mobevents.AsmodeusStructureBuilder;
import com.lycanitesmobs.core.worldgen.mobevents.RahovartStructureBuilder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AltarInfo {
   public static boolean altarsEnabled = true;
   public static boolean checkDimensions = false;
   public static Map<String, Double> rareSubspeciesMutlipliers = new HashMap();
   public static Map<String, AltarInfo> altars = new HashMap();
   public String name = "Altar";
   public AltarMobEventTrigger mobEventTrigger;

   public static void loadGlobalSettings() {
      ConfigBase config = ConfigBase.getConfig(LycanitesMobs.modInfo, "general");
      config.setCategoryComment("Altars", "Altars are block arrangements that can be activated using Soulkeys to summon rare subspecies as mini bosses or trigger events including boss events.");
      altarsEnabled = config.getBool("Altars", "Altars Enabled", altarsEnabled, "Set to false to disable altars, Soulkeys can still be crafted but wont work on Altars.");
      checkDimensions = config.getBool("Altars", "Check Dimension", checkDimensions, "If set to true, Altars will only activate in dimensions that the monster spawned or event started is allowed in.");
      String[] statNames = new String[]{"Health", "Defense", "Speed", "Damage", "Haste", "Effect", "Pierce"};
      rareSubspeciesMutlipliers = new HashMap();

      for(String statName : statNames) {
         double defaultValue = 4.0D;
         if ("Health".equalsIgnoreCase(statName)) {
            defaultValue = 10.0D;
         }

         if ("Speed".equalsIgnoreCase(statName)) {
            defaultValue = 1.5D;
         }

         rareSubspeciesMutlipliers.put(statName.toUpperCase(Locale.ENGLISH), Double.valueOf(config.getDouble("Altars", statName + " Altar Stat Multiplier", defaultValue)));
      }

   }

   public static boolean checkAltarsEnabled() {
      return altars.size() < 1 ? false : altarsEnabled;
   }

   public static void addAltar(AltarInfo altarInfo) {
      altars.put(altarInfo.name.toLowerCase(), altarInfo);
   }

   public static AltarInfo getAltar(String name) {
      return altars.containsKey(name.toLowerCase()) ? (AltarInfo)altars.get(name.toLowerCase()) : null;
   }

   public static void createAltars() {
      AltarInfo ebonCacodemonAltar = new AltarInfoEbonCacodemon("EbonCacodemonAltar");
      addAltar(ebonCacodemonAltar);
      AltarInfo rahovartAltar = new AltarInfoRahovart("RahovartAltar");
      addAltar(rahovartAltar);
      StructureBuilder.addStructureBuilder(new RahovartStructureBuilder());
      AltarInfo asmodeusAltar = new AltarInfoAsmodeus("AsmodeusAltar");
      addAltar(asmodeusAltar);
      StructureBuilder.addStructureBuilder(new AsmodeusStructureBuilder());
      AltarInfo amalgalichAltar = new AltarInfoAmalgalich("AmalgalichAltar");
      addAltar(amalgalichAltar);
      StructureBuilder.addStructureBuilder(new AmalgalichStructureBuilder());
      AltarInfo umberLobberAltar = new AltarInfoUmberLobber("UmberLobberAltar");
      addAltar(umberLobberAltar);
      AltarInfo celestialGeonachAltar = new AltarInfoCelestialGeonach("CelestialGeonachAltar");
      addAltar(celestialGeonachAltar);
      AltarInfo lunarGrueAltar = new AltarInfoLunarGrue("LunarGrueAltar");
      addAltar(lunarGrueAltar);
      AltarInfo mottledAbaiaAltar = new AltarInfoMottleAbaia("MottledAbaia");
      addAltar(mottledAbaiaAltar);
      AltarInfo crimsonEpionAltar = new AltarInfoCrimsonEpion("CrimsonEpion");
      addAltar(crimsonEpionAltar);
      AltarInfo royalArchvileAltar = new AltarInfoRoyalArchvile("RoyalArchvile");
      addAltar(royalArchvileAltar);
      AltarInfo phosphorescentChupacabraAltar = new AltarInfoPhosphorescentChupacabra("PhosphorescentChupacabra");
      addAltar(phosphorescentChupacabraAltar);
   }

   public AltarInfo(String name) {
      this.name = name;
   }

   public boolean checkBlockEvent(Entity entity, World world, BlockPos pos) {
      return true;
   }

   public boolean quickCheck(Entity entity, World world, BlockPos pos) {
      return false;
   }

   public boolean fullCheck(Entity entity, World world, BlockPos pos) {
      return false;
   }

   public boolean activate(Entity entity, World world, BlockPos pos, int variant) {
      return this.mobEventTrigger == null ? false : this.mobEventTrigger.onActivate(entity, world, pos, variant);
   }

   public BlockPos getFacingPosition(BlockPos pos, double distance, double angle) {
      double xAmount = -Math.sin(Math.toRadians(angle));
      double zAmount = Math.cos(Math.toRadians(angle));
      BlockPos facingPos = new BlockPos((double)Math.round((double)pos.getX() + distance * xAmount), (double)pos.getY(), (double)Math.round((double)pos.getZ() + distance * zAmount));
      return facingPos;
   }
}
