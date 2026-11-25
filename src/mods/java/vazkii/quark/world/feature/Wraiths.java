package vazkii.quark.world.feature;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.potion.PotionMod;
import vazkii.quark.world.client.render.RenderWraith;
import vazkii.quark.world.entity.EntityWraith;
import vazkii.quark.world.item.ItemSoulBead;

public class Wraiths extends Feature {
   public static Item soul_bead;
   public static Potion curse;
   public static int curseTime;
   public static boolean enableCurse;
   public static int weight;
   public static int min;
   public static int max;
   public static int curseRange;
   public static String[] wraithSounds;
   public static List<String> validWraithSounds;

   public Wraiths() {
   }

   public void setupConfig() {
      weight = this.loadPropInt("Spawn Weight", "The higher, the more will spawn", 60);
      min = this.loadPropInt("Smallest spawn group", "", 4);
      max = this.loadPropInt("Largest spawn group", "", 6);
      curseRange = this.loadPropInt("Curse Range", "", 64);
      curseTime = this.loadPropInt("Curse Time", "How long the curse effect lasts for (in ticks)", 12000);
      enableCurse = this.loadPropBool("Enable Curse", "", true);
      wraithSounds = this.loadPropStringList("Wraith sound effects", "List of sound sets to use with wraiths.\nThree sounds must be provided per entry, separated by | (in the format idle|hurt|death). Leave blank for no sound (i.e. if a mob has no ambient noise)", new String[]{"entity.sheep.ambient|entity.sheep.hurt|entity.sheep.death", "entity.cow.ambient|entity.cow.hurt|entity.cow.death", "entity.pig.ambient|entity.pig.hurt|entity.pig.death", "entity.chicken.ambient|entity.chicken.hurt|entity.chicken.death", "entity.horse.ambient|entity.horse.hurt|entity.horse.death", "entity.cat.ambient|entity.cat.hurt|entity.cat.death", "entity.wolf.ambient|entity.wolf.hurt|entity.wolf.death", "entity.villager.ambient|entity.villager.hurt|entity.villager.death", "entity.polar_bear.ambient|entity.polar_bear.hurt|entity.polar_bear.death", "entity.zombie.ambient|entity.zombie.hurt|entity.zombie.death", "entity.skeleton.ambient|entity.skeleton.hurt|entity.skeleton.death", "entity.spider.ambient|entity.spider.hurt|entity.spider.death", "|entity.creeper.hurt|entity.creeper.death", "entity.endermen.ambient|entity.endermen.hurt|entity.endermen.death", "entity.zombie_pig.ambient|entity.zombie_pig.hurt|entity.zombie_pig.death", "entity.witch.ambient|entity.witch.hurt|entity.witch.death", "entity.blaze.ambient|entity.blaze.hurt|entity.blaze.death", "entity.llama.ambient|entity.llama.hurt|entity.llama.death", "|quark:entity.stoneling.cry|quark:entity.stoneling.die", "quark:entity.frog.idle|quark:entity.frog.hurt|quark:entity.frog.die"});
      validWraithSounds = (List)Arrays.stream(wraithSounds).filter((s) -> {
         return s.split("\\|").length == 3;
      }).collect(Collectors.toList());
   }

   public void preInit(FMLPreInitializationEvent event) {
      soul_bead = new ItemSoulBead();
      if (enableCurse) {
         curse = new PotionMod("curse", true, 0, 0);
      }

      String wraithName = "quark:wraith";
      EntityRegistry.registerModEntity(new ResourceLocation(wraithName), EntityWraith.class, wraithName, 4, Quark.instance, 80, 3, true, 15527148, 12434877);
      LootTableList.register(EntityWraith.LOOT_TABLE);
   }

   public void init() {
      EntityRegistry.addSpawn(EntityWraith.class, weight, min, max, EnumCreatureType.MONSTER, (Biome[])BiomeDictionary.getBiomes(Type.NETHER).toArray(new Biome[0]));
   }

   public void preInitClient() {
      RenderingRegistry.registerEntityRenderingHandler(EntityWraith.class, RenderWraith.FACTORY);
   }

   @SubscribeEvent
   public void onSpawn(CheckSpawn event) {
      if (event.getResult() != Result.ALLOW && event.getEntityLiving() instanceof IMob && event.getWorld() instanceof WorldServer) {
         for(EntityPlayer player : ((WorldServer)event.getWorld()).playerEntities) {
            if (!player.isSpectator() && player.getActivePotionEffect(curse) != null && player.getDistanceSq(event.getEntity()) < (double)(curseRange * curseRange)) {
               if (event.getEntityLiving().getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
                  event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
               }

               event.setResult(Result.ALLOW);
               return;
            }
         }
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
