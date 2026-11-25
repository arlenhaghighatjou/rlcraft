package vazkii.quark.world.feature;

import java.lang.reflect.Field;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IRegistryDelegate;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.handler.OverrideRegistryHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.block.BlockVariantLeaves;
import vazkii.quark.world.block.BlockVariantSapling;
import vazkii.quark.world.world.SakuraTreeGenerator;
import vazkii.quark.world.world.tree.WorldGenSwampTree;

public class TreeVariants extends Feature {
   public static Block variant_leaves;
   public static Block variant_sapling;
   public static boolean enableSwamp;
   public static boolean enableSakura;
   public static boolean changeVineColor;
   public static double sakuraChance;

   public TreeVariants() {
   }

   public void setupConfig() {
      enableSwamp = this.loadPropBool("Enable Swamp", "", true);
      enableSakura = this.loadPropBool("Enable Blossom", "", true);
      changeVineColor = this.loadPropBool("Change vine color in swamps", "", true);
      sakuraChance = this.loadPropChance("Blossom Tree Chance", "The chance per chunk for a Oak Blossom Tree to spawn (0 is 0%, 1 is 100%). This can be higher than 1 if you want multiple per chunk.", 0.05D);
   }

   public void preInit(FMLPreInitializationEvent event) {
      variant_leaves = new BlockVariantLeaves();
      variant_sapling = new BlockVariantSapling();
      if (enableSwamp) {
         try {
            Field f = ObfuscationReflectionHelper.findField(Biome.class, "SWAMP_FEATURE");
            OverrideRegistryHandler.crackFinalField(f);
            f.set((Object)null, new WorldGenSwampTree(true));
         } catch (ReflectiveOperationException var3) {
            var3.printStackTrace();
         }
      }

      if (enableSakura) {
         GameRegistry.registerWorldGenerator(new SakuraTreeGenerator(), 0);
      }

      addOreDict("treeLeaves", ProxyRegistry.newStack(variant_leaves, 1, 32767));
      addOreDict("treeSapling", ProxyRegistry.newStack(variant_sapling, 1, 32767));
   }

   @SideOnly(Side.CLIENT)
   public void postInitClient() {
      if (changeVineColor) {
         BlockColors colors = Minecraft.getMinecraft().getBlockColors();
         IBlockColor parent = (state, worldIn, pos, tintIndex) -> {
            return worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic();
         };

         try {
            Map<IRegistryDelegate<Block>, IBlockColor> colorMap = (Map)ObfuscationReflectionHelper.findField(BlockColors.class, "blockColorMap").get(colors);
            parent = (IBlockColor)colorMap.getOrDefault(Blocks.VINE.delegate, parent);
         } catch (IllegalAccessException var4) {
            var4.printStackTrace();
         }

         colors.registerBlockColorHandler((state, worldIn, pos, i) -> {
            if (worldIn != null && pos != null) {
               Biome b = worldIn.getBiome(pos);
               if (b == Biomes.SWAMPLAND) {
                  return 6975545;
               }
            }

            return finalParent.colorMultiplier(state, worldIn, pos, i);
         }, new Block[]{Blocks.VINE});
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void renderEvent(RenderTickEvent event) {
      if (event.phase == Phase.START) {
         ((BlockLeaves)variant_leaves).setGraphicsLevel(Minecraft.getMinecraft().gameSettings.fancyGraphics);
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }
}
