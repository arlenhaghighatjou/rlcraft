package vazkii.quark.world.feature;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.CheckSpawn;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import vazkii.quark.base.module.Feature;

public class NaturalBlazesInNether extends Feature {
   public static int weight;
   public static int min;
   public static int max;
   public static boolean restrictToNetherrack;
   public static List<String> allowedBlocks;

   public NaturalBlazesInNether() {
   }

   public void setupConfig() {
      weight = this.loadPropInt("Spawn Weight", "The higher, the more will spawn", 5);
      min = this.loadPropInt("Smallest spawn group", "", 1);
      max = this.loadPropInt("Largest spawn group", "", 2);
      restrictToNetherrack = this.loadPropBool("Block restrictions", "Make naturally spawned blazes only spawn in allowed blocks", true);
      allowedBlocks = Arrays.asList(this.loadPropStringList("Allowed spawn blocks", "Only used if \" Block restrictions\" is enabled.", new String[]{Objects.toString(Blocks.NETHERRACK.getRegistryName()), Objects.toString(Blocks.SOUL_SAND.getRegistryName()), Objects.toString(Blocks.MAGMA.getRegistryName()), "quark:basalt"}));
   }

   public void init() {
      SpawnListEntry blazeEntry = new SpawnListEntry(EntityBlaze.class, weight, min, max);
      BiomeDictionary.getBiomes(Type.NETHER).forEach((biome) -> {
         biome.getSpawnableList(EnumCreatureType.MONSTER).add(blazeEntry);
      });
   }

   @SubscribeEvent
   public void onSpawn(CheckSpawn event) {
      if (restrictToNetherrack && !event.isSpawner() && event.getEntityLiving() instanceof EntityBlaze && event.getResult() != Result.DENY && event.getEntityLiving().world instanceof WorldServer) {
         EntityBlaze blaze = (EntityBlaze)event.getEntityLiving();
         WorldServer world = (WorldServer)blaze.world;
         BlockPos pos = blaze.getPosition();
         Block block = world.getBlockState(pos.down()).getBlock();
         ResourceLocation res = block.getRegistryName();
         if (res != null) {
            boolean allowedBlock = allowedBlocks.contains(res.toString());
            boolean fortress = world.getChunkProvider().isInsideStructure(world, "Fortress", pos);
            if (!fortress && !allowedBlock) {
               event.setResult(Result.DENY);
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

   public String getFeatureIngameConfigName() {
      return "Naturally Spawning Blazes";
   }
}
