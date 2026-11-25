package com.github.alexthe666.iceandfire.core;

import com.github.alexthe666.iceandfire.block.BlockCharedPath;
import com.github.alexthe666.iceandfire.block.BlockDragonOre;
import com.github.alexthe666.iceandfire.block.BlockEggInIce;
import com.github.alexthe666.iceandfire.block.BlockElementalFlower;
import com.github.alexthe666.iceandfire.block.BlockFallingGeneric;
import com.github.alexthe666.iceandfire.block.BlockGeneric;
import com.github.alexthe666.iceandfire.block.BlockGoldPile;
import com.github.alexthe666.iceandfire.block.BlockIceSpikes;
import com.github.alexthe666.iceandfire.block.BlockJar;
import com.github.alexthe666.iceandfire.block.BlockLectern;
import com.github.alexthe666.iceandfire.block.BlockMyrmexBiolight;
import com.github.alexthe666.iceandfire.block.BlockMyrmexCocoon;
import com.github.alexthe666.iceandfire.block.BlockMyrmexConnectedResin;
import com.github.alexthe666.iceandfire.block.BlockMyrmexResin;
import com.github.alexthe666.iceandfire.block.BlockPixieHouse;
import com.github.alexthe666.iceandfire.block.BlockPodium;
import com.github.alexthe666.iceandfire.block.BlockSilverPile;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDummyGorgonHead;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityDummyGorgonHeadActive;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityMyrmexCocoon;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

public class ModBlocks {
   public static final SoundType SOUND_TYPE_GOLD = new SoundType(1.0F, 1.0F, ModSounds.GOLD_PILE_BREAK, ModSounds.GOLD_PILE_STEP, ModSounds.GOLD_PILE_BREAK, ModSounds.GOLD_PILE_STEP, ModSounds.GOLD_PILE_STEP);
   @ObjectHolder("iceandfire:lectern")
   public static Block lectern = new BlockLectern();
   @ObjectHolder("iceandfire:podium")
   public static Block podium = new BlockPodium();
   @ObjectHolder("iceandfire:fire_lily")
   public static Block fire_lily = new BlockElementalFlower(true);
   @ObjectHolder("iceandfire:frost_lily")
   public static Block frost_lily = new BlockElementalFlower(false);
   @ObjectHolder("iceandfire:goldpile")
   public static Block goldPile = new BlockGoldPile();
   @ObjectHolder("iceandfire:silverpile")
   public static Block silverPile = new BlockSilverPile();
   @ObjectHolder("iceandfire:silver_ore")
   public static Block silverOre = new BlockDragonOre(2, 3.0F, 5.0F, "iceandfire.silverOre", "silver_ore");
   @ObjectHolder("iceandfire:sapphire_ore")
   public static Block sapphireOre = new BlockDragonOre(2, 3.0F, 5.0F, "iceandfire.sapphireOre", "sapphire_ore");
   @ObjectHolder("iceandfire:silver_block")
   public static Block silverBlock = new BlockGeneric(Material.IRON, "silver_block", "iceandfire.silverBlock", "pickaxe", 2, 3.0F, 10.0F, SoundType.METAL);
   @ObjectHolder("iceandfire:sapphire_block")
   public static Block sapphireBlock = new BlockGeneric(Material.IRON, "sapphire_block", "iceandfire.sapphireBlock", "pickaxe", 2, 3.0F, 10.0F, SoundType.METAL);
   @ObjectHolder("iceandfire:chared_dirt")
   public static Block charedDirt = new BlockGeneric(Material.GROUND, "chared_dirt", "iceandfire.charedDirt", "shovel", 0, 0.5F, 0.0F, SoundType.GROUND);
   @ObjectHolder("iceandfire:chared_grass")
   public static Block charedGrass = new BlockGeneric(Material.GRASS, "chared_grass", "iceandfire.charedGrass", "shovel", 0, 0.6F, 0.0F, SoundType.GROUND);
   @ObjectHolder("iceandfire:chared_stone")
   public static Block charedStone = new BlockGeneric(Material.ROCK, "chared_stone", "iceandfire.charedStone", "pickaxe", 0, 1.5F, 10.0F, SoundType.STONE);
   @ObjectHolder("iceandfire:chared_cobblestone")
   public static Block charedCobblestone = new BlockGeneric(Material.ROCK, "chared_cobblestone", "iceandfire.charedCobblestone", "pickaxe", 0, 2.0F, 10.0F, SoundType.STONE);
   @ObjectHolder("iceandfire:chared_gravel")
   public static Block charedGravel = new BlockFallingGeneric(Material.GROUND, "chared_gravel", "iceandfire.charedGravel", "pickaxe", 0, 0.6F, 0.0F, SoundType.GROUND);
   @ObjectHolder("iceandfire:chared_grass_path")
   public static Block charedGrassPath = new BlockCharedPath(true);
   @ObjectHolder("iceandfire:ash")
   public static Block ash = new BlockFallingGeneric(Material.SAND, "ash", "iceandfire.ash", "shovel", 0, 0.5F, 0.0F, SoundType.SAND);
   @ObjectHolder("iceandfire:frozen_dirt")
   public static Block frozenDirt = new BlockGeneric(Material.GROUND, "frozen_dirt", "iceandfire.frozenDirt", "shovel", 0, 0.5F, 0.0F, SoundType.GLASS, true);
   @ObjectHolder("iceandfire:frozen_grass")
   public static Block frozenGrass = new BlockGeneric(Material.GRASS, "frozen_grass", "iceandfire.frozenGrass", "shovel", 0, 0.6F, 0.0F, SoundType.GLASS, true);
   @ObjectHolder("iceandfire:frozen_stone")
   public static Block frozenStone = new BlockGeneric(Material.ROCK, "frozen_stone", "iceandfire.frozenStone", "pickaxe", 0, 1.5F, 10.0F, SoundType.GLASS, true);
   @ObjectHolder("iceandfire:frozen_cobblestone")
   public static Block frozenCobblestone = new BlockGeneric(Material.ROCK, "frozen_cobblestone", "iceandfire.frozenCobblestone", "pickaxe", 0, 2.0F, 10.0F, SoundType.GLASS, true);
   @ObjectHolder("iceandfire:frozen_gravel")
   public static Block frozenGravel = new BlockFallingGeneric(Material.GROUND, "frozen_gravel", "iceandfire.frozenGravel", "pickaxe", 0, 0.6F, 0.0F, SoundType.GLASS, true);
   @ObjectHolder("iceandfire:frozen_grass_path")
   public static Block frozenGrassPath = new BlockCharedPath(false);
   @ObjectHolder("iceandfire:frozen_splinters")
   public static Block frozenSplinters = new BlockGeneric(Material.WOOD, "frozen_splinters", "iceandfire.frozenSplinters", "pickaxe", 0, 2.0F, 10.0F, SoundType.GLASS, true);
   @ObjectHolder("iceandfire:dragon_ice")
   public static Block dragon_ice = new BlockGeneric(Material.PACKED_ICE, "dragon_ice", "iceandfire.dragon_ice", "pickaxe", 0, 0.5F, 0.0F, SoundType.GLASS, true);
   @ObjectHolder("iceandfire:dragon_ice_spikes")
   public static Block dragon_ice_spikes = new BlockIceSpikes();
   @ObjectHolder("iceandfire:nest")
   public static Block nest = new BlockGeneric(Material.GRASS, "nest", "iceandfire.nest", "axe", 0, 0.5F, 0.0F, SoundType.GROUND, false);
   @ObjectHolder("iceandfire:egginice")
   public static Block eggInIce = new BlockEggInIce();
   @ObjectHolder("iceandfire:pixie_house")
   public static Block pixieHouse = new BlockPixieHouse();
   @ObjectHolder("iceandfire:jar_empty")
   public static Block jar_empty = new BlockJar(true);
   @ObjectHolder("iceandfire:jar_pixie")
   public static Block jar_pixie = new BlockJar(false);
   @ObjectHolder("iceandfire:myrmex_resin")
   public static Block myrmex_resin = new BlockMyrmexResin(false);
   @ObjectHolder("iceandfire:myrmex_resin_sticky")
   public static Block myrmex_resin_sticky = new BlockMyrmexResin(true);
   @ObjectHolder("iceandfire:desert_myrmex_cocoon")
   public static Block desert_myrmex_cocoon = new BlockMyrmexCocoon(false);
   @ObjectHolder("iceandfire:jungle_myrmex_cocoon")
   public static Block jungle_myrmex_cocoon = new BlockMyrmexCocoon(true);
   @ObjectHolder("iceandfire:myrmex_desert_biolight")
   public static Block myrmex_desert_biolight = new BlockMyrmexBiolight(false);
   @ObjectHolder("iceandfire:myrmex_jungle_biolight")
   public static Block myrmex_jungle_biolight = new BlockMyrmexBiolight(true);
   @ObjectHolder("iceandfire:myrmex_desert_resin_block")
   public static Block myrmex_desert_resin_block = new BlockMyrmexConnectedResin(false, false);
   @ObjectHolder("iceandfire:myrmex_jungle_resin_block")
   public static Block myrmex_jungle_resin_block = new BlockMyrmexConnectedResin(true, false);
   @ObjectHolder("iceandfire:myrmex_desert_resin_glass")
   public static Block myrmex_desert_resin_glass = new BlockMyrmexConnectedResin(false, true);
   @ObjectHolder("iceandfire:myrmex_jungle_resin_glass")
   public static Block myrmex_jungle_resin_glass = new BlockMyrmexConnectedResin(true, true);

   public ModBlocks() {
   }

   static {
      GameRegistry.registerTileEntity(TileEntityDummyGorgonHead.class, "dummyGorgonHeadIdle");
      GameRegistry.registerTileEntity(TileEntityDummyGorgonHeadActive.class, "dummyGorgonHeadActive");
      GameRegistry.registerTileEntity(TileEntityMyrmexCocoon.class, "myrmexCocoon");
   }
}
