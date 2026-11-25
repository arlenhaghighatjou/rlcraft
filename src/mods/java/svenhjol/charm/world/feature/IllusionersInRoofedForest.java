package svenhjol.charm.world.feature;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIllusionIllager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import svenhjol.charm.Charm;
import svenhjol.meson.Feature;

public class IllusionersInRoofedForest extends Feature {
   public static int weight;
   public static double dropChance;

   public IllusionersInRoofedForest() {
   }

   public String getDescription() {
      return "Illusioners are spell-casting illagers equipped with bows that create fake copies of themselves while fighting.\nThis feature lets illusioners spawn in Roofed Forest biomes.  An illusioner has a chance of dropping a totem.";
   }

   public void configure() {
      super.configure();
      weight = this.propInt("Spawn weight", "The higher this value, the more illusioners will spawn.", 30);
      dropChance = this.propDouble("Drop chance", "Chance (out of 1.0) of an illusioner dropping items when killed.", 1.0D);
   }

   public void init(FMLInitializationEvent event) {
      SpawnListEntry illusionerEntry = new SpawnListEntry(EntityIllusionIllager.class, weight, 1, 1);
      Biome roofedForest = Biomes.ROOFED_FOREST;
      roofedForest.getSpawnableList(EnumCreatureType.MONSTER).add(illusionerEntry);
   }

   @SubscribeEvent
   public void onSpawn(CheckSpawn event) {
      if (!event.isSpawner() && event.getEntityLiving() instanceof EntityIllusionIllager && event.getResult() != Result.DENY && event.getEntityLiving().world instanceof WorldServer && event.getEntityLiving().getPosition().getY() < event.getEntityLiving().world.getSeaLevel()) {
         event.setResult(Result.DENY);
      }

   }

   @SubscribeEvent
   public void onDrops(LivingDropsEvent event) {
      if (!event.getEntityLiving().world.isRemote && event.getEntityLiving() instanceof EntityIllusionIllager && event.getSource().getTrueSource() instanceof EntityPlayer && (double)event.getEntityLiving().world.rand.nextFloat() <= dropChance) {
         Entity entity = event.getEntity();
         ItemStack emeralds = new ItemStack(Items.EMERALD, entity.world.rand.nextInt(3) + 2);
         ItemStack totem = null;
         List<EntityItem> drops = event.getDrops();
         drops.add(new EntityItem(entity.getEntityWorld(), entity.posX, entity.posY, entity.posZ, emeralds));
         if (Charm.hasFeature(TotemOfReturning.class) && TotemOfReturning.addToIllusioner) {
            totem = new ItemStack(TotemOfReturning.totem);
         }

         if (Charm.hasFeature(TotemOfShielding.class) && TotemOfShielding.addToIllusioner && (totem == null || event.getEntityLiving().world.rand.nextFloat() < 0.5F)) {
            totem = new ItemStack(TotemOfShielding.totem);
         }

         if (totem != null) {
            drops.add(new EntityItem(entity.getEntityWorld(), entity.posX, entity.posY, entity.posZ, totem));
         }
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }
}
