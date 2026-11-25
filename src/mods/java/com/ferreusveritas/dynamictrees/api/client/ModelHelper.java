package com.ferreusveritas.dynamictrees.api.client;

import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.blocks.BlockBranchThick;
import com.ferreusveritas.dynamictrees.blocks.BlockSurfaceRoot;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelHelper {
   public ModelHelper() {
   }

   public static void regModel(TreeFamily tree) {
      BlockBranch blockBranch = tree.getDynamicBranch();
      ModelResourceLocation modelLocation = getBranchModelResourceLocation(blockBranch);
      setGenericStateMapper(blockBranch, modelLocation);
      if (blockBranch instanceof BlockBranchThick) {
         setGenericStateMapper(((BlockBranchThick)blockBranch).otherBlock, modelLocation);
      }

      BlockSurfaceRoot surfaceRoot = tree.getSurfaceRoots();
      if (surfaceRoot != null) {
         ModelLoader.setCustomStateMapper(surfaceRoot, (new Builder()).ignore(surfaceRoot.getIgnorableProperties()).build());
      }

   }

   private static ModelResourceLocation getBranchModelResourceLocation(BlockBranch blockBranch) {
      ResourceLocation family = blockBranch.getFamily().getName();
      ResourceLocation resloc = new ResourceLocation(family.getResourceDomain(), family.getResourcePath() + "branch");
      return new ModelResourceLocation(resloc, (String)null);
   }

   public static void setGenericStateMapper(Block block, ModelResourceLocation modelLocation) {
      ModelLoader.setCustomStateMapper(block, (state) -> {
         return (Map)block.getBlockState().getValidStates().stream().collect(Collectors.toMap((b) -> {
            return b;
         }, (b) -> {
            return modelLocation;
         }));
      });
   }

   public static void regModel(Block block) {
      if (block != Blocks.AIR) {
         regModel(Item.getItemFromBlock(block));
      }

      if (block instanceof BlockBranchThick) {
         Item item = Item.getItemFromBlock(((BlockBranchThick)block).otherBlock);
         regModel(item, 0, block.getRegistryName());
      }

   }

   public static void regModel(Item item) {
      regModel(item, 0);
   }

   public static void regModel(Item item, int meta) {
      regModel(item, meta, item.getRegistryName());
   }

   public static void regModel(Item item, int meta, ResourceLocation customResourceLocation) {
      if (item != null) {
         ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(customResourceLocation, "inventory"));
      }

   }

   public static void regColorHandler(Block block, IBlockColor blockColor) {
      Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(blockColor, new Block[]{block});
   }

   public static void regColorHandler(Item item, IItemColor itemColor) {
      Minecraft.getMinecraft().getItemColors().registerItemColorHandler(itemColor, new Item[]{item});
   }
}
