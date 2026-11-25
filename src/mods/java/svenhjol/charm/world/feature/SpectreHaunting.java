package svenhjol.charm.world.feature;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import svenhjol.charm.Charm;
import svenhjol.meson.Feature;
import svenhjol.meson.Meson;
import svenhjol.meson.helper.EntityHelper;
import svenhjol.meson.helper.WorldHelper;

public class SpectreHaunting extends Feature {
   public static WorldHelper.StructureType structure;
   public static int distance;
   public static int checkInterval;
   public static int spawnMinDistance;
   public static int spawnRange;
   public static int ticksLiving;
   public static int mobcap;
   public static float spawnChance;

   public SpectreHaunting() {
   }

   public String getDescription() {
      return "Spectres will spawn in greater numbers within range of specific structure.";
   }

   public boolean isEnabled() {
      return this.enabled && Charm.hasFeature(Spectre.class);
   }

   public void configure() {
      super.configure();
      spawnChance = (float)this.propDouble("Spawn chance during haunt", "Chance (out of 1.0) of a spectre spawning every two seconds during a haunt.\n", 0.25D);
      int secondsLiving = this.propInt("Spectre lifetime", "How long a spectre should live for, in seconds.\nThis affects all spectres, not just those spawned in the haunt.", 20);
      mobcap = this.propInt("Check mobcap", "Only spawns a spectre for haunting if number of monster entities is lower than this number.\nVanilla minecraft is around 75.", 80);
      String hauntStructure = this.propString("Structure to haunt", "Name of structure that spectres will haunt.", WorldHelper.StructureType.STRONGHOLD.getCapitalizedName());
      checkInterval = 40;
      distance = 7500;
      ticksLiving = secondsLiving * 20;
      spawnMinDistance = 18;
      spawnRange = 5;
      structure = WorldHelper.StructureType.valueOf(hauntStructure.toUpperCase());
   }

   @SubscribeEvent
   public void onLoad(Load event) {
      WorldHelper.populateWorldSettings(event.getWorld());
   }

   @SubscribeEvent
   public void onTick(PlayerTickEvent event) {
      if (event.phase == Phase.END && event.side.isServer() && event.player != null && event.player.world != null && WorldHelper.canGenerateStructures(event.player.world) && WorldHelper.canGenerateStructure(event.player.world, structure) && event.player.world.getTotalWorldTime() % (long)checkInterval == 0L && event.player.world.rand.nextFloat() < spawnChance && (double)event.player.world.getLightBrightness(event.player.getPosition()) < (double)((float)Spectre.despawnLight) / 16.0D) {
         WorldServer world = (WorldServer)event.player.world;
         EnumCreatureType monster = EnumCreatureType.MONSTER;
         int count = world.countEntities(monster, true);
         if (count <= mobcap) {
            BlockPos nearest = WorldHelper.getNearestStructure(event.player.world, event.player.getPosition(), structure);
            if (nearest != null && WorldHelper.getDistanceSq(event.player.getPosition(), nearest) < (double)distance) {
               Meson.debug("Spectre haunting ", event.player.getPosition());
               EntityHelper.spawnEntityNearPlayer(event.player, spawnMinDistance, spawnRange, new ResourceLocation("charm", "spectre"));
            }
         } else {
            Meson.debug("Mobcap reached, not spawning spectre");
         }
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }
}
