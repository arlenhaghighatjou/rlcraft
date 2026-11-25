package com.dhanantry.scapeandrunparasites.init;

import com.dhanantry.scapeandrunparasites.block.BlockBiomeCore;
import com.dhanantry.scapeandrunparasites.block.BlockBiomePurifier;
import com.dhanantry.scapeandrunparasites.block.BlockBuglin;
import com.dhanantry.scapeandrunparasites.block.BlockColonyCore;
import com.dhanantry.scapeandrunparasites.block.BlockColonyStructure;
import com.dhanantry.scapeandrunparasites.block.BlockDod;
import com.dhanantry.scapeandrunparasites.block.BlockEvolutionLure;
import com.dhanantry.scapeandrunparasites.block.BlockFluid;
import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedBush;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedRemain;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedRubble;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedStain;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedTrunk;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteCanister;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteCanisterC;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteFog;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteMouth;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubble;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteSapling;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteThin;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.block.BlockSlabDouble;
import com.dhanantry.scapeandrunparasites.block.BlockSlabHalf;
import com.dhanantry.scapeandrunparasites.block.BlockStairBase;
import com.dhanantry.scapeandrunparasites.block.BlockVineBase;
import com.dhanantry.scapeandrunparasites.block.BlockWebBase;
import com.dhanantry.scapeandrunparasites.block.IMetaName;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSlab;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class SRPBlocks {
   public static Block InfestedStain;
   public static Block InfestRemain;
   public static Block InfestedTrunk;
   public static Block InfestedRubble;
   public static BlockInfestedBush InfestedBush;
   public static Block BiomeHeart;
   public static Block ColonyHeart;
   public static Block BiomePurifier;
   public static Block optionalDirt;
   public static Block optionalRub;
   public static Block SRPWeb;
   public static Block evolutionLure;
   public static Block buglin;
   public static BlockParasiteBush ParasiteBush;
   public static Block ParasiteCanister;
   public static Block ParasiteTrunk;
   public static Block ParasiteStain;
   public static Block ParasiteRubble;
   public static Block ParasiteStructure;
   public static Block ParasiteThin;
   public static Block ParasiteSapling;
   public static Block ParasiteMouth;
   public static Block ParasiteRubbleDense;
   public static Block ParasiteRubbleBoneStair;
   public static Block ParasiteStainFleshStair;
   public static BlockSlab ParasiteRubbleSlabHalf;
   public static BlockSlab ParasiteRubbleSlabDouble;
   public static Block ParasiteVine;
   public static Block ParasiteFog;
   public static Block DeadBlood;
   public static BlockGore Gore;
   public static BlockParasiteCanisterC ParasiteCanisterActive;
   public static Block dodN;

   public SRPBlocks() {
   }

   public static void init() {
      optionalDirt = Block.getBlockFromName(SRPConfigSystems.optionalBlock);
      optionalRub = Block.getBlockFromName(SRPConfigSystems.optionalBlockRub);
   }

   @EventBusSubscriber(
      modid = "srparasites"
   )
   public static class RegistrationHandler {
      private static IForgeRegistry<Item> itemRegistry;

      public RegistrationHandler() {
      }

      @SubscribeEvent
      public static void onEvent(Register<Block> event) {
         IForgeRegistry<Block> registry = event.getRegistry();
         SRPBlocks.InfestedStain = new BlockInfestedStain(Material.GROUND, "infestedstain", 0.7F, true, true);
         SRPBlocks.InfestRemain = new BlockInfestedRemain("infestremain");
         SRPBlocks.InfestedTrunk = new BlockInfestedTrunk(Material.WOOD, "infestedtrunk", 2.1F, true, true);
         SRPBlocks.InfestedRubble = new BlockInfestedRubble(Material.ROCK, "infestedrubble", 2.2F, true, true, 11.0F);
         SRPBlocks.InfestedBush = new BlockInfestedBush("infestedbush", 0.4F);
         SRPBlocks.BiomeHeart = new BlockBiomeCore(Material.ROCK, "biomeheart", 60.0F, true, true, 2500.0F);
         SRPBlocks.ColonyHeart = new BlockColonyCore(Material.ROCK, "colonyheart", 60.0F, true, true, 2500.0F);
         SRPBlocks.BiomePurifier = new BlockBiomePurifier("biomepurifier", 2.0F, true, true, 5.0F);
         SRPBlocks.ParasiteBush = new BlockParasiteBush("parasitebush", 0.5F);
         SRPBlocks.ParasiteCanister = new BlockParasiteCanister(Material.CACTUS, "parasitecanister", 0.7F, true, true);
         SRPBlocks.ParasiteTrunk = new BlockParasiteTrunk(Material.WOOD, "parasitetrunk", 2.2F, true, true);
         SRPBlocks.ParasiteStain = new BlockParasiteStain(Material.GROUND, "parasitestain", 0.8F, true, true);
         SRPBlocks.ParasiteRubble = new BlockParasiteRubble(Material.ROCK, "parasiterubble", 2.3F, true, true);
         SRPBlocks.ParasiteStructure = new BlockColonyStructure(Material.ROCK, "parasitestructure", 5.0F, true, true, 20.0F);
         SRPBlocks.ParasiteThin = new BlockParasiteThin("parasitethin", 2.2F);
         SRPBlocks.ParasiteSapling = new BlockParasiteSapling("parasitesapling");
         SRPBlocks.ParasiteMouth = new BlockParasiteMouth(Material.CACTUS, "parasitemouth", 1.4F, true, true);
         SRPBlocks.ParasiteRubbleDense = new BlockParasiteRubbleDense(Material.ROCK, "parasiterubbledense", 3.3F, true, true);
         SRPBlocks.ParasiteRubbleBoneStair = new BlockStairBase("parasiterubble_bonestairs", true, SRPBlocks.ParasiteRubble.getDefaultState().withProperty(BlockParasiteRubble.VARIANT, BlockParasiteRubble.EnumType.BONE));
         SRPBlocks.ParasiteStainFleshStair = new BlockStairBase("parasitestain_fleshstairs", true, SRPBlocks.ParasiteStain.getDefaultState().withProperty(BlockParasiteStain.VARIANT, BlockParasiteStain.EnumType.FLESH));
         SRPBlocks.ParasiteRubbleSlabHalf = new BlockSlabHalf(Material.ROCK, "parasiterubble_slabhalf", 3.3F, true, false, SRPBlocks.ParasiteRubbleSlabHalf, SRPBlocks.ParasiteRubbleSlabDouble);
         SRPBlocks.ParasiteRubbleSlabDouble = new BlockSlabDouble(Material.ROCK, "parasiterubble_slabdouble", 3.3F, false, false, SRPBlocks.ParasiteRubbleSlabHalf);
         SRPBlocks.ParasiteVine = new BlockVineBase("parasitetendril", 0.5F, true, true);
         SRPBlocks.ParasiteFog = new BlockParasiteFog("parasitefog");
         SRPBlocks.DeadBlood = new BlockFluid("deadblood", SRPFluids.DEADBLOOD_FLUID, Material.WATER, true);
         SRPBlocks.SRPWeb = new BlockWebBase("srpweb");
         SRPBlocks.evolutionLure = new BlockEvolutionLure(Material.ROCK, "evolutionlure", 1.0F, true, true);
         SRPBlocks.buglin = new BlockBuglin("tunnel", 0.1F, true, true, 0.1F);
         SRPBlocks.Gore = new BlockGore("gore");
         SRPBlocks.ParasiteCanisterActive = new BlockParasiteCanisterC(Material.ROCK, "canisteractive", 1.5F, true, true);
         SRPBlocks.dodN = new BlockDod("dispatchern", 0.1F, true, true, 0.1F);
         registry.register(SRPBlocks.InfestedStain);
         registry.register(SRPBlocks.InfestRemain);
         registry.register(SRPBlocks.InfestedTrunk);
         registry.register(SRPBlocks.InfestedRubble);
         registry.register(SRPBlocks.InfestedBush);
         registry.register(SRPBlocks.BiomeHeart);
         registry.register(SRPBlocks.ColonyHeart);
         registry.register(SRPBlocks.BiomePurifier);
         registry.register(SRPBlocks.ParasiteBush);
         registry.register(SRPBlocks.ParasiteCanister);
         registry.register(SRPBlocks.ParasiteTrunk);
         registry.register(SRPBlocks.ParasiteStain);
         registry.register(SRPBlocks.ParasiteRubble);
         registry.register(SRPBlocks.ParasiteStructure);
         registry.register(SRPBlocks.ParasiteThin);
         registry.register(SRPBlocks.ParasiteSapling);
         registry.register(SRPBlocks.ParasiteMouth);
         registry.register(SRPBlocks.ParasiteRubbleDense);
         registry.register(SRPBlocks.ParasiteRubbleBoneStair);
         registry.register(SRPBlocks.ParasiteStainFleshStair);
         registry.register(SRPBlocks.ParasiteRubbleSlabHalf);
         registry.register(SRPBlocks.ParasiteRubbleSlabDouble);
         registry.register(SRPBlocks.ParasiteVine);
         registry.register(SRPBlocks.ParasiteFog);
         registry.register(SRPBlocks.DeadBlood);
         registry.register(SRPBlocks.SRPWeb);
         registry.register(SRPBlocks.evolutionLure);
         registry.register(SRPBlocks.buglin);
         registry.register(SRPBlocks.Gore);
         registry.register(SRPBlocks.ParasiteCanisterActive);
         registry.register(SRPBlocks.dodN);
      }

      @SubscribeEvent
      public static void registerItemBlocks(Register<Item> event) {
         itemRegistry = event.getRegistry();
         registerItemBlock(SRPBlocks.InfestedStain);
         registerItemBlock(SRPBlocks.InfestRemain);
         registerItemBlock(SRPBlocks.InfestedTrunk);
         registerItemBlock(SRPBlocks.InfestedRubble);
         registerItemBlock(SRPBlocks.InfestedBush);
         registerItemBlock(SRPBlocks.BiomeHeart);
         registerItemBlock(SRPBlocks.ColonyHeart);
         registerItemBlock(SRPBlocks.BiomePurifier);
         registerItemBlock(SRPBlocks.ParasiteBush);
         registerItemBlock(SRPBlocks.ParasiteCanister);
         registerItemBlock(SRPBlocks.ParasiteTrunk);
         registerItemBlock(SRPBlocks.ParasiteStain);
         registerItemBlock(SRPBlocks.ParasiteRubble);
         registerItemBlock(SRPBlocks.ParasiteStructure);
         registerItemBlock(SRPBlocks.ParasiteThin);
         registerItemBlock(SRPBlocks.ParasiteSapling);
         registerItemBlock(SRPBlocks.ParasiteMouth);
         registerItemBlock(SRPBlocks.ParasiteRubbleDense);
         registerItemBlock(SRPBlocks.ParasiteRubbleBoneStair);
         registerItemBlock(SRPBlocks.ParasiteStainFleshStair);
         registerItemBlock(SRPBlocks.ParasiteRubbleSlabHalf);
         registerItemBlock(SRPBlocks.ParasiteVine);
         registerItemBlock(SRPBlocks.ParasiteFog);
         registerItemBlock(SRPBlocks.SRPWeb);
         registerItemBlock(SRPBlocks.evolutionLure);
         registerItemBlock(SRPBlocks.buglin);
         registerItemBlock(SRPBlocks.Gore);
         registerItemBlock(SRPBlocks.ParasiteCanisterActive);
         registerItemBlock(SRPBlocks.dodN);
      }

      private static void registerItemBlock(Block block) {
         Item itemBlock;
         if (block instanceof IMetaName) {
            itemBlock = ((IMetaName)block).getItemBlock();
         } else if (block instanceof BlockSlab) {
            itemBlock = new ItemSlab(block, SRPBlocks.ParasiteRubbleSlabHalf, SRPBlocks.ParasiteRubbleSlabDouble);
         } else {
            itemBlock = new ItemBlock(block);
         }

         ResourceLocation registryName = block.getRegistryName();
         itemRegistry.register(itemBlock.setRegistryName(registryName));
      }
   }
}
