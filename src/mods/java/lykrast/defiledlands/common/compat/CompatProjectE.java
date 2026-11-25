package lykrast.defiledlands.common.compat;

import lykrast.defiledlands.common.block.BlockGrassCorrupted;
import lykrast.defiledlands.common.init.ModBlocks;
import lykrast.defiledlands.common.init.ModItems;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.api.proxy.ITransmutationProxy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;

public class CompatProjectE extends ModCompat {
   public CompatProjectE() {
   }

   public void init() {
      IEMCProxy emc = ProjectEAPI.getEMCProxy();
      emc.registerCustomEMC("stoneDefiled", 1);
      emc.registerCustomEMC("dirtDefiled", 1);
      emc.registerCustomEMC(new ItemStack(ModBlocks.grassDefiled), 2);
      emc.registerCustomEMC("sandDefiled", 1);
      emc.registerCustomEMC("gravelDefiled", 4);
      emc.registerCustomEMC(new ItemStack(ModBlocks.vilespine), 32);
      emc.registerCustomEMC(new ItemStack(ModBlocks.blastem), 32);
      emc.registerCustomEMC(new ItemStack(ModItems.blastemFruit), 64);
      emc.registerCustomEMC(new ItemStack(ModBlocks.scuronotte), 32);
      emc.registerCustomEMC("gemHephaestite", 128);
      emc.registerCustomEMC("ingotUmbrium", 256);
      emc.registerCustomEMC("gemScarlite", 8192);
      emc.registerCustomEMC(new ItemStack(ModItems.scuttlerEye), 128);
      emc.registerCustomEMC(new ItemStack(ModItems.blackHeart), 128);
      emc.registerCustomEMC(new ItemStack(ModItems.foulSlime), 32);
      emc.registerCustomEMC(new ItemStack(ModItems.bookWyrmRaw), 64);
      emc.registerCustomEMC(new ItemStack(ModItems.bookWyrmScale), 64);
      emc.registerCustomEMC(new ItemStack(ModItems.bookWyrmScaleGolden), 1024);
      emc.registerCustomEMC("essenceDestroyer", 16384);
      emc.registerCustomEMC("essenceMourner", 32768);
   }

   public void postInit() {
      ITransmutationProxy transmute = ProjectEAPI.getTransmutationProxy();
      IBlockState dirt = ModBlocks.dirtDefiled.getDefaultState();
      IBlockState sand = ModBlocks.sandDefiled.getDefaultState();
      IBlockState stone = ModBlocks.stoneDefiled.getDefaultState();
      transmute.registerWorldTransmutation(dirt, sand, stone);
      transmute.registerWorldTransmutation(sand, stone, dirt);
      transmute.registerWorldTransmutation(stone, dirt, sand);
      transmute.registerWorldTransmutation(ModBlocks.grassDefiled.getDefaultState().withProperty(BlockGrassCorrupted.SNOWY, Boolean.valueOf(false)), sand, stone);
      transmute.registerWorldTransmutation(ModBlocks.grassDefiled.getDefaultState().withProperty(BlockGrassCorrupted.SNOWY, Boolean.valueOf(true)), sand, stone);
      IBlockState sandstone = ModBlocks.sandstoneDefiled.getDefaultState();
      IBlockState gravel = ModBlocks.gravelDefiled.getDefaultState();
      transmute.registerWorldTransmutation(sandstone, gravel, (IBlockState)null);
      transmute.registerWorldTransmutation(gravel, sandstone, (IBlockState)null);
   }
}
