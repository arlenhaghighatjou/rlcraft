package vazkii.quark.world.feature;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.client.render.RenderFoxhound;
import vazkii.quark.world.entity.EntityFoxhound;

public class Foxhounds extends Feature {
   public static double tameChance;
   public static int weight;
   public static int min;
   public static int max;

   public Foxhounds() {
   }

   public void setupConfig() {
      weight = this.loadPropInt("Spawn Weight", "The higher, the more will spawn", 4);
      min = this.loadPropInt("Smallest spawn group", "", 1);
      max = this.loadPropInt("Largest spawn group", "", 2);
      tameChance = this.loadPropChance("Chance to Tempt", "The chance coal will tame a foxhound", 0.05D);
   }

   public void preInit(FMLPreInitializationEvent event) {
      String foxName = "quark:foxhound";
      LootTableList.register(EntityFoxhound.FOXHOUND_LOOT_TABLE);
      EntityRegistry.registerModEntity(new ResourceLocation(foxName), EntityFoxhound.class, foxName, 21, Quark.instance, 80, 3, true, 8981773, 15904587);
   }

   public void init() {
      EntityRegistry.addSpawn(EntityFoxhound.class, weight, min, max, EnumCreatureType.MONSTER, (Biome[])BiomeDictionary.getBiomes(Type.NETHER).toArray(new Biome[0]));
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      RenderingRegistry.registerEntityRenderingHandler(EntityFoxhound.class, RenderFoxhound::new);
   }
}
