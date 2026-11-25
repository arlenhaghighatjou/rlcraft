package org.orecruncher.dsurround.lib.compat;

import javax.annotation.Nonnull;
import net.minecraftforge.fml.common.Loader;

public enum ModEnvironment {
   ToughAsNails("ToughAsNails"),
   SereneSeasons("sereneseasons"),
   CalendarAPI("CalendarAPI"),
   Weather2("weather2"),
   EnderIO("EnderIO"),
   Chisel("chisel"),
   ChiselAPI("ctm-api"),
   OpenTerrainGenerator("openterraingenerator"),
   ActualMusic("actualmusic"),
   GalacticraftCore("galacticraftcore"),
   GalacticraftPlanets("galacticraftplanets"),
   CoFHCore("cofhcore"),
   Albedo("albedo"),
   AmbientSounds("ambientsounds"),
   SoundPhysics("soundphysics"),
   BiomesOPlenty("biomesoplenty"),
   CubicChunks("cubicchunks"),
   Animania("animania"),
   CosmeticArmorReworked("cosmeticarmorreworked"),
   ForgeMultipartCBE("forgemultipartcbe"),
   ConnectedTextures("ctm"),
   LittleTiles("littletiles"),
   ConstructArmory("conarm");

   protected final String modId;
   protected boolean isLoaded;

   private ModEnvironment(@Nonnull String modId) {
      this.modId = modId;
   }

   public boolean isLoaded() {
      return this.isLoaded;
   }

   public static void initialize() {
      for(ModEnvironment me : values()) {
         me.isLoaded = Loader.isModLoaded(me.modId.toLowerCase());
      }

   }
}
