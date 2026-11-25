package vazkii.quark.world.feature;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.client.render.RenderAshen;
import vazkii.quark.world.client.render.RenderDweller;
import vazkii.quark.world.entity.EntityAshen;
import vazkii.quark.world.entity.EntityDweller;

public class DepthMobs extends Feature {
   public static int upperBound;
   public static boolean enableAshen;
   public static boolean enableDweller;
   public static int ashenWeight;
   public static int dwellerWeight;
   public static int ashenMaxPack;
   public static int dwellerMaxPack;
   public static int ashenMinPack;
   public static int dwellerMinPack;

   public DepthMobs() {
   }

   public void setupConfig() {
      upperBound = this.loadPropInt("Highest Y level for spawns", "", 20);
      enableAshen = this.loadPropBool("Enable Ashen", "", true);
      enableDweller = this.loadPropBool("Enable Dweller", "", true);
      ashenWeight = this.loadPropInt("Ashen Spawn Weight", "(Skeleton is 100. The higher, the more will spawn)", 30);
      dwellerWeight = this.loadPropInt("Dweller Spawn Weight", "(Zombie is 100. The higher, the more will spawn)", 30);
      ashenMaxPack = this.loadPropInt("Largest Ashen spawn group", "", 2);
      dwellerMaxPack = this.loadPropInt("Largest Dweller spawn group", "", 2);
      ashenMinPack = this.loadPropInt("Smallest Ashen spawn group", "", 1);
      dwellerMinPack = this.loadPropInt("Smallest Dweller spawn group", "", 1);
   }

   public void preInit(FMLPreInitializationEvent event) {
      if (enableDweller) {
         String dwellerName = "quark:dweller";
         EntityRegistry.registerModEntity(new ResourceLocation(dwellerName), EntityDweller.class, dwellerName, 2, Quark.instance, 80, 3, true, 4872516, 4136992);
         EntityRegistry.addSpawn(EntityDweller.class, dwellerWeight, dwellerMinPack, dwellerMaxPack, EnumCreatureType.MONSTER, getBiomesWithMob(EntityZombie.class));
      }

      if (enableAshen) {
         String ashenName = "quark:ashen";
         EntityRegistry.registerModEntity(new ResourceLocation(ashenName), EntityAshen.class, ashenName, 1, Quark.instance, 80, 3, true, 8618870, 5455164);
         EntityRegistry.addSpawn(EntityAshen.class, ashenWeight, ashenMinPack, ashenMaxPack, EnumCreatureType.MONSTER, getBiomesWithMob(EntitySkeleton.class));
      }

   }

   public void preInitClient() {
      if (enableDweller) {
         RenderingRegistry.registerEntityRenderingHandler(EntityDweller.class, RenderDweller.FACTORY);
      }

      if (enableAshen) {
         RenderingRegistry.registerEntityRenderingHandler(EntityAshen.class, RenderAshen.FACTORY);
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public static Biome[] getBiomesWithMob(Class<? extends Entity> clazz) {
      List<Biome> biomes = new ArrayList();

      label23:
      for(Biome b : Biome.REGISTRY) {
         List<SpawnListEntry> spawnList = b.getSpawnableList(EnumCreatureType.MONSTER);
         Iterator var5 = spawnList.iterator();

         while(true) {
            if (!var5.hasNext()) {
               continue label23;
            }

            SpawnListEntry e = (SpawnListEntry)var5.next();
            if (e.entityClass == clazz) {
               break;
            }
         }

         biomes.add(b);
      }

      return (Biome[])biomes.toArray(new Biome[0]);
   }
}
