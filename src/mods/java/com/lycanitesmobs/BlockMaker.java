package com.lycanitesmobs;

import com.lycanitesmobs.core.block.BlockBase;
import com.lycanitesmobs.core.block.BlockDoubleSlab;
import com.lycanitesmobs.core.block.BlockFenceCustom;
import com.lycanitesmobs.core.block.BlockPillar;
import com.lycanitesmobs.core.block.BlockSlabCustom;
import com.lycanitesmobs.core.block.BlockStairsCustom;
import com.lycanitesmobs.core.block.BlockWallCustom;
import com.lycanitesmobs.core.info.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockMaker {
   public BlockMaker() {
   }

   public static void addStoneBlocks(ModInfo group, String stoneName) {
      float hardness = 2.0F;
      float resistance = 2000.0F;
      BlockBase stoneBlock = (BlockBase)(new BlockBase(Material.ROCK, group, stoneName + "stone")).setHardness(hardness).setResistance(resistance).setCreativeTab(LycanitesMobs.blocksTab);
      ObjectManager.addBlock(stoneName + "stone", stoneBlock);
      ObjectManager.addBlock(stoneName + "stone_stairs", (new BlockStairsCustom(stoneBlock)).setCreativeTab(LycanitesMobs.blocksTab));
      Block stoneSlabDoubleBlock = (new BlockDoubleSlab(Material.ROCK, group, stoneName + "stone_slab_double", stoneName + "stone_slab")).setHardness(hardness).setResistance(resistance);
      ObjectManager.addBlock(stoneName + "stone_slab_double", stoneSlabDoubleBlock);
      ObjectManager.addBlock(stoneName + "stone_slab", (new BlockSlabCustom(stoneBlock, stoneSlabDoubleBlock)).setCreativeTab(LycanitesMobs.blocksTab));
      BlockBase stoneBrickBlock = (BlockBase)(new BlockBase(Material.ROCK, group, stoneName + "stonebrick")).setHardness(hardness).setResistance(resistance).setCreativeTab(LycanitesMobs.blocksTab);
      ObjectManager.addBlock(stoneName + "stonebrick", stoneBrickBlock);
      ObjectManager.addBlock(stoneName + "stonebrick_stairs", (new BlockStairsCustom(stoneBrickBlock)).setCreativeTab(LycanitesMobs.blocksTab));
      ObjectManager.addBlock(stoneName + "stonebrick_slab", (new BlockSlabCustom(stoneBrickBlock, stoneBrickBlock)).setCreativeTab(LycanitesMobs.blocksTab));
      ObjectManager.addBlock(stoneName + "stonebrick_fence", (new BlockFenceCustom(stoneBrickBlock)).setCreativeTab(LycanitesMobs.blocksTab));
      ObjectManager.addBlock(stoneName + "stonebrick_wall", (new BlockWallCustom(stoneBrickBlock)).setCreativeTab(LycanitesMobs.blocksTab));
      BlockBase stoneTileBlock = (BlockBase)(new BlockBase(Material.ROCK, group, stoneName + "stonetile")).setHardness(hardness).setResistance(resistance).setCreativeTab(LycanitesMobs.blocksTab);
      ObjectManager.addBlock(stoneName + "stonetile", stoneTileBlock);
      ObjectManager.addBlock(stoneName + "stonetile_stairs", (new BlockStairsCustom(stoneTileBlock)).setCreativeTab(LycanitesMobs.blocksTab));
      ObjectManager.addBlock(stoneName + "stonetile_slab", (new BlockSlabCustom(stoneTileBlock, stoneTileBlock)).setCreativeTab(LycanitesMobs.blocksTab));
      ObjectManager.addBlock(stoneName + "stonepolished", (new BlockBase(Material.ROCK, group, stoneName + "stonepolished")).setHardness(hardness).setResistance(resistance).setCreativeTab(LycanitesMobs.blocksTab));
      ObjectManager.addBlock(stoneName + "stonechiseled", (new BlockBase(Material.ROCK, group, stoneName + "stonechiseled")).setHardness(hardness).setResistance(resistance).setCreativeTab(LycanitesMobs.blocksTab));
      ObjectManager.addBlock(stoneName + "stonepillar", (new BlockPillar(Material.ROCK, group, stoneName + "stonepillar")).setHardness(hardness).setResistance(resistance).setCreativeTab(LycanitesMobs.blocksTab));
      ObjectManager.addBlock(stoneName + "crystal", (new BlockBase(Material.GLASS, group, stoneName + "crystal")).setBlockStepSound(SoundType.GLASS).setHardness(0.5F).setResistance(resistance).setLightLevel(1.0F).setCreativeTab(LycanitesMobs.blocksTab));
   }
}
