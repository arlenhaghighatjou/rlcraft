package vazkii.quark.world.feature;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Biomes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.item.ItemMod;
import vazkii.quark.base.Quark;
import vazkii.quark.base.item.ItemQuarkFood;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.potion.PotionMod;
import vazkii.quark.misc.feature.ExtraPotions;
import vazkii.quark.world.client.render.RenderCrab;
import vazkii.quark.world.entity.EntityCrab;
import vazkii.quark.world.item.ItemCrabShell;
import vazkii.quark.world.world.event.RaveEventListener;

public class Crabs extends Feature {
   public static int weight;
   public static int min;
   public static int max;
   public static ItemQuarkFood crabLeg;
   public static ItemQuarkFood cookedCrabLeg;
   public static ItemMod crabShell;
   public static PotionMod resilience;

   public Crabs() {
   }

   public void setupConfig() {
      weight = this.loadPropInt("Spawn Weight", "The higher, the more will spawn", 40);
      min = this.loadPropInt("Smallest spawn group", "", 1);
      max = this.loadPropInt("Largest spawn group", "", 3);
   }

   @SubscribeEvent
   public void onNewWorld(Load event) {
      event.getWorld().addEventListener(new RaveEventListener(event.getWorld()));
   }

   public void preInit(FMLPreInitializationEvent event) {
      crabLeg = new ItemQuarkFood("crab_leg", 1, 0.3F, true, new String[0]);
      cookedCrabLeg = new ItemQuarkFood("cooked_crab_leg", 8, 0.8F, true, new String[0]);
      crabShell = new ItemCrabShell();
      resilience = new PotionMod("resilience", false, 5970436, 5);
      resilience.registerPotionAttributeModifier(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, "0cb68666-1b17-42b3-82c0-28412d6c0b22", 0.5D, 0);
      String crabName = "quark:crab";
      LootTableList.register(EntityCrab.CRAB_LOOT_TABLE);
      EntityRegistry.registerModEntity(new ResourceLocation(crabName), EntityCrab.class, crabName, 22, Quark.instance, 80, 3, true, 8993826, 9528648);
   }

   public void postPreInit() {
      ExtraPotions.addStandardBlend(resilience, crabShell);
   }

   public void init() {
      EntityRegistry.addSpawn(EntityCrab.class, weight, min, max, EnumCreatureType.CREATURE, new Biome[]{Biomes.BEACH});
   }

   public void postInit() {
      FurnaceRecipes.instance().addSmelting(crabLeg, new ItemStack(cookedCrabLeg), 0.35F);
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      RenderingRegistry.registerEntityRenderingHandler(EntityCrab.class, RenderCrab.FACTORY);
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
