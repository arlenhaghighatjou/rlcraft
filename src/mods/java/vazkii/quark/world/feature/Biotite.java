package vazkii.quark.world.feature;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import vazkii.arl.block.BlockModSlab;
import vazkii.arl.block.BlockModStairs;
import vazkii.arl.recipe.RecipeHandler;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.block.BlockQuarkStairs;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.GlobalConfig;
import vazkii.quark.building.feature.VanillaWalls;
import vazkii.quark.world.block.BlockBiotite;
import vazkii.quark.world.block.BlockBiotiteOre;
import vazkii.quark.world.block.slab.BlockBiotiteSlab;
import vazkii.quark.world.item.ItemBiotite;
import vazkii.quark.world.world.BiotiteGenerator;

public class Biotite extends Feature {
   public static Block biotite_ore;
   public static Block biotite_block;
   public static Item biotite;
   public static boolean generateNaturally;
   public static boolean generateByDragon;
   public static boolean enableWalls;
   public static int clusterSize;
   public static int clusterCount;
   public static int generationDelay;
   public static int clustersPerTick;

   public Biotite() {
   }

   public void setupConfig() {
      enableWalls = this.loadPropBool("Enable walls", "", true) && GlobalConfig.enableVariants;
      generateNaturally = this.loadPropBool("Generate naturally", "", false);
      generateByDragon = this.loadPropBool("Generate by dragon kill", "", true);
      clusterSize = this.loadPropInt("Cluster size", "", 14);
      clusterCount = this.loadPropInt("Cluster count for natural generation", "", 16);
      generationDelay = this.loadPropInt("Generation delay on dragon death", "", 1);
      clustersPerTick = this.loadPropInt("Clusters generated per dragon death tick", "", 16);
   }

   public void preInit(FMLPreInitializationEvent event) {
      biotite_ore = new BlockBiotiteOre();
      biotite_block = new BlockBiotite();
      biotite = new ItemBiotite();
      BlockModSlab singleSlab = new BlockBiotiteSlab(false);
      BlockModSlab.initSlab(biotite_block, 0, singleSlab, new BlockBiotiteSlab(true));
      BlockModStairs.initStairs(biotite_block, 0, new BlockQuarkStairs("biotite_stairs", biotite_block.getDefaultState()));
      VanillaWalls.add("biotite", biotite_block, 0, enableWalls);
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(biotite_block), new Object[]{"BB", "BB", 'B', ProxyRegistry.newStack(biotite)});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(biotite_block, 2, 1), new Object[]{"B", "B", 'B', ProxyRegistry.newStack(singleSlab)});
      RecipeHandler.addOreDictRecipe(ProxyRegistry.newStack(biotite_block, 2, 2), new Object[]{"B", "B", 'B', ProxyRegistry.newStack(biotite_block)});
      GameRegistry.registerWorldGenerator(new BiotiteGenerator(clusterSize, clusterCount), 0);
      addOreDict("gemEnderBiotite", biotite);
   }

   public void postInit() {
      FurnaceRecipes.instance().addSmelting(Item.getItemFromBlock(biotite_ore), new ItemStack(biotite), 1.0F);
   }

   @SubscribeEvent
   public void onEntityTick(LivingUpdateEvent event) {
      if (generateByDragon && event.getEntityLiving() instanceof EntityDragon && !event.getEntity().getEntityWorld().isRemote) {
         EntityDragon dragon = (EntityDragon)event.getEntity();
         if (dragon.deathTicks > 0 && dragon.deathTicks % generationDelay == 0) {
            EaglercraftRandom rand = dragon.getEntityWorld().rand;
            BlockPos basePos = dragon.getPosition();
            basePos = new BlockPos(basePos.getX() - 128, 0, basePos.getZ() - 128);

            for(int i = 0; i < clustersPerTick; ++i) {
               BlockPos pos = basePos.add(rand.nextInt(256), rand.nextInt(64), rand.nextInt(256));
               BiotiteGenerator.generator.generate(dragon.getEntityWorld(), rand, pos);
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
