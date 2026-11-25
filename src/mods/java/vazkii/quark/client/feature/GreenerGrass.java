package vazkii.quark.client.feature;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IRegistryDelegate;
import vazkii.quark.base.module.Feature;

public class GreenerGrass extends Feature {
   public static boolean affectFoliage;
   public static boolean alphaGrass;
   public static boolean absoluteValues;
   public static int redShift;
   public static int greenShift;
   public static int blueShift;
   public static List<String> extraBlocks;

   public GreenerGrass() {
   }

   public void setupConfig() {
      affectFoliage = this.loadPropBool("Should affect foliage", "", true);
      alphaGrass = this.loadPropBool("Alpha grass", "Sets the grass color to be a \"Minecraft Alpha\" tone.\nThis will override all manual shift values.", false);
      absoluteValues = this.loadPropBool("Treat shifts as absolute and ignore biome colors", "", false);
      redShift = this.loadPropInt("Shift reds by", "", -30);
      greenShift = this.loadPropInt("Shift greens by", "", 30);
      blueShift = this.loadPropInt("Shift blues by", "", -30);
      extraBlocks = Arrays.asList(this.loadPropStringList("Extra blocks", "", new String[]{"buildingbrickscompatvanilla:grass_slab", "buildingbrickscompatvanilla:grass_step", "buildingbrickscompatvanilla:grass_corner", "buildingbrickscompatvanilla:grass_vertical_slab", "buildingbrickscompatvanilla:grass_stairs", "betterwithmods:dirt_slab", "biomesoplenty:plant_0", "biomesoplenty:plant_1", "biomesoplenty:leaves_1", "biomesoplenty:leaves_2", "biomesoplenty:leaves_3", "biomesoplenty:leaves_4", "biomesoplenty:leaves_5", "biomesoplenty:grass"}));
   }

   @SideOnly(Side.CLIENT)
   public void initClient() {
      this.registerGreenerColor(Blocks.GRASS, Blocks.TALLGRASS, Blocks.DOUBLE_PLANT, Blocks.REEDS);
      if (affectFoliage) {
         this.registerGreenerColor(Blocks.LEAVES, Blocks.LEAVES2, Blocks.VINE);
      }

      for(String s : extraBlocks) {
         Block b = (Block)Block.REGISTRY.getObject(new ResourceLocation(s));
         this.registerGreenerColor(b);
      }

   }

   @SideOnly(Side.CLIENT)
   private void registerGreenerColor(Block... blocks) {
      BlockColors colors = Minecraft.getMinecraft().getBlockColors();
      Map<IRegistryDelegate<Block>, IBlockColor> map = (Map)ObfuscationReflectionHelper.getPrivateValue(BlockColors.class, colors, "blockColorMap");

      for(Block b : blocks) {
         IBlockColor color = (IBlockColor)map.get(b.delegate);
         if (color != null) {
            colors.registerBlockColorHandler(this.getGreenerColor(color), new Block[]{b});
         }
      }

   }

   @SideOnly(Side.CLIENT)
   private IBlockColor getGreenerColor(IBlockColor color) {
      return (state, world, pos, tintIndex) -> {
         int originalColor = color.colorMultiplier(state, world, pos, tintIndex);
         int r = originalColor >> 16 & 255;
         int g = originalColor >> 8 & 255;
         int b = originalColor & 255;
         int shiftRed = alphaGrass ? 30 : redShift;
         int shiftGreen = alphaGrass ? 120 : greenShift;
         int shiftBlue = alphaGrass ? 30 : blueShift;
         return absoluteValues ? (Math.max(0, Math.min(255, redShift)) << 16) + Math.max(0, Math.min(255, greenShift) << 8) + Math.max(0, Math.min(255, blueShift)) : (Math.max(0, Math.min(255, r + shiftRed)) << 16) + Math.max(0, Math.min(255, g + shiftGreen) << 8) + Math.max(0, Math.min(255, b + shiftBlue));
      };
   }

   public String[] getIncompatibleMods() {
      return new String[]{"sereneseasons"};
   }
}
