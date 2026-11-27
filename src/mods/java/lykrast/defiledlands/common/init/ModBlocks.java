package lykrast.defiledlands.common.init;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.ArrayList;
import java.util.List;
import lykrast.defiledlands.common.block.BlockBlastem;
import lykrast.defiledlands.common.block.BlockConjuringAltar;
import lykrast.defiledlands.common.block.BlockCorrupted;
import lykrast.defiledlands.common.block.BlockDoorGeneric;
import lykrast.defiledlands.common.block.BlockDoubleSlabCorruptedStone;
import lykrast.defiledlands.common.block.BlockDoubleSlabCorruptedWood;
import lykrast.defiledlands.common.block.BlockFallingCorrupted;
import lykrast.defiledlands.common.block.BlockGlassObscure;
import lykrast.defiledlands.common.block.BlockGrassCorrupted;
import lykrast.defiledlands.common.block.BlockGravelCorrupted;
import lykrast.defiledlands.common.block.BlockHalfSlabCorruptedStone;
import lykrast.defiledlands.common.block.BlockHalfSlabCorruptedWood;
import lykrast.defiledlands.common.block.BlockHealingPad;
import lykrast.defiledlands.common.block.BlockHephaestite;
import lykrast.defiledlands.common.block.BlockHephaestiteOre;
import lykrast.defiledlands.common.block.BlockLeafTenebra;
import lykrast.defiledlands.common.block.BlockLogTenebra;
import lykrast.defiledlands.common.block.BlockRavagingDecoration;
import lykrast.defiledlands.common.block.BlockSaplingTenebra;
import lykrast.defiledlands.common.block.BlockScarliteOre;
import lykrast.defiledlands.common.block.BlockScuronotte;
import lykrast.defiledlands.common.block.BlockStairsGeneric;
import lykrast.defiledlands.common.block.BlockStoneDefiledDecoration;
import lykrast.defiledlands.common.block.BlockVilespine;
import lykrast.defiledlands.common.block.ICustomItemBlock;
import lykrast.defiledlands.common.block.ICustomModel;
import lykrast.defiledlands.common.block.ICustomStateMapper;
import lykrast.defiledlands.common.tileentity.TileConjuringAltar;
import lykrast.defiledlands.common.util.CreativeTabDL;
import lykrast.defiledlands.common.util.LocUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {
   private static List<Block> blockList = new ArrayList();
   public static Block stoneDefiled = registerBlock(new BlockCorrupted(Material.ROCK, SoundType.STONE, 1.5F, 30.0F, "pickaxe", 0), "stone_defiled");
   public static Block sandDefiled = registerBlock(new BlockFallingCorrupted(Material.SAND, SoundType.SAND, 0.5F, 2.5F, "shovel", 0), "sand_defiled");
   public static Block sandstoneDefiled = registerBlock(new BlockCorrupted(Material.ROCK, SoundType.STONE, 0.8F, 4.0F, "pickaxe", 0), "sandstone_defiled");
   public static Block dirtDefiled = registerBlock(new BlockCorrupted(Material.GROUND, SoundType.GROUND, 0.5F, 2.5F, "shovel", 0), "dirt_defiled");
   public static Block grassDefiled = registerBlock(new BlockGrassCorrupted(0.6F, 2.5F), "grass_defiled");
   public static Block gravelDefiled = registerBlock(new BlockGravelCorrupted(Material.SAND, SoundType.GROUND, 0.6F, 3.0F), "gravel_defiled");
   public static Block tenebraLog = registerBlock(new BlockLogTenebra(3.0F, 15.0F), "tenebra_log");
   public static Block tenebraLeaves = registerBlock(new BlockLeafTenebra(), "tenebra_leaves");
   public static Block tenebraSapling = registerBlock(new BlockSaplingTenebra(0.0F), "tenebra_sapling");
   public static Block tenebraPlanks = registerBlock(new BlockCorrupted(Material.WOOD, SoundType.WOOD, 3.0F, 22.5F, "axe", 0), "tenebra_planks");
   public static Block stoneDefiledDecoration = registerBlock(new BlockStoneDefiledDecoration(1.5F, 30.0F, 0), "stone_defiled_decoration");
   public static Block tenebraDoor = registerBlock(new BlockDoorGeneric(Material.WOOD, SoundType.WOOD, 3.0F, 22.5F, "axe", 0), "tenebra_door");
   public static Block ravagingDecoration = registerBlock(new BlockRavagingDecoration(2.0F, 60.0F, 0), "ravaging_decoration");
   public static Block glassObscure = registerBlock(new BlockGlassObscure(0.3F, 1.5F), "glass_obscure");
   public static Block slabDoubleStone = registerBlock(new BlockDoubleSlabCorruptedStone(), "stone_defiled_slab_double");
   public static Block slabHalfStone = registerBlock(new BlockHalfSlabCorruptedStone(), "stone_defiled_slab");
   public static Block slabDoubleTenebra = registerBlock(new BlockDoubleSlabCorruptedWood(), "wood_defiled_slab_double");
   public static Block slabHalfTenebra = registerBlock(new BlockHalfSlabCorruptedWood(), "wood_defiled_slab");
   public static Block stairsStoneDefiled = registerBlock(new BlockStairsGeneric(stoneDefiled.getDefaultState()), "stone_defiled_stairs");
   public static Block stairsSandstoneDefiled = registerBlock(new BlockStairsGeneric(sandstoneDefiled.getDefaultState()), "sandstone_defiled_stairs");
   public static Block stairsStoneDefiledBricks = registerBlock(new BlockStairsGeneric(stoneDefiledDecoration.getDefaultState().withProperty(BlockStoneDefiledDecoration.VARIANT, BlockStoneDefiledDecoration.Variants.BRICKS)), "stone_defiled_bricks_stairs");
   public static Block stairsTenebra = registerBlock(new BlockStairsGeneric(tenebraPlanks.getDefaultState()), "tenebra_stairs");
   public static Block hephaestiteOre = registerBlock(new BlockHephaestiteOre(3.0F, 15.0F), "hephaestite_ore");
   public static Block hephaestiteBlock = registerBlock(new BlockHephaestite(5.0F, 30.0F), "hephaestite_block");
   public static Block umbriumOre = registerBlock(new BlockCorrupted(Material.ROCK, SoundType.STONE, 3.0F, 15.0F, "pickaxe", 1), "umbrium_ore");
   public static Block umbriumBlock = registerBlock(new BlockCorrupted(Material.IRON, SoundType.METAL, 5.0F, 30.0F, "pickaxe", 1), "umbrium_block");
   public static Block scarliteOre = registerBlock(new BlockScarliteOre(3.0F, 15.0F), "scarlite_ore");
   public static Block scarliteBlock = registerBlock(new BlockCorrupted(Material.IRON, SoundType.METAL, 5.0F, 30.0F, "pickaxe", 2), "scarlite_block");
   public static Block vilespine = registerBlock(new BlockVilespine(), "vilespine");
   public static Block blastem = registerBlock(new BlockBlastem(), "blastem");
   public static Block scuronotte = registerBlock(new BlockScuronotte(), "scuronotte");
   public static Block healingPad = registerBlock(new BlockHealingPad(3.0F, 15.0F), "healing_pad");
   public static Block conjuringAltar = registerBlock(new BlockConjuringAltar(3.0F, 15.0F), "conjuring_altar");

   public ModBlocks() {
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, Block.class, ModBlocks::registerBlocks);
      bus.register(ModelRegistryEvent.class, ModBlocks::registerModels);
   }

   public static void registerBlocks(Register<Block> event) {
      for(Block b : blockList) {
         event.getRegistry().register(b);
      }

   }

   @SideOnly(Side.CLIENT)
   public static void registerModels(ModelRegistryEvent evt) {
      for(Block b : blockList) {
         initModel(b);
      }

      ModelLoader.setCustomStateMapper(tenebraDoor, (new Builder()).ignore(new IProperty[]{BlockDoorGeneric.POWERED}).build());
      blockList = null;
   }

   @SideOnly(Side.CLIENT)
   public static void initModel(Block b) {
      if (b instanceof ICustomModel) {
         ((ICustomModel)b).initModel();
      } else {
         ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(b), 0, new ModelResourceLocation(b.getRegistryName(), "inventory"));
      }

      if (b instanceof ICustomStateMapper) {
         ((ICustomStateMapper)b).setCustomStateMapper();
      }

   }

   public static Block registerBlock(Block block, String name) {
      return registerBlock(block, name, CreativeTabDL.instance);
   }

   public static Block registerBlock(Block block, String name, CreativeTabs tab) {
      block.setRegistryName(name);
      block.setUnlocalizedName(LocUtils.prefix(name));
      ItemBlock item;
      if (block instanceof ICustomItemBlock) {
         item = ((ICustomItemBlock)block).getItemBlock();
      } else {
         item = new ItemBlock(block);
      }

      if (item != null) {
         item.setRegistryName(block.getRegistryName());
         ModItems.itemBlockList.add(item);
      }

      if (tab != null) {
         block.setCreativeTab(tab);
      }

      blockList.add(block);
      return block;
   }

   static {
      GameRegistry.registerTileEntity(TileConjuringAltar.class, new ResourceLocation("defiledlands", "conjuring_altar"), TileConjuringAltar::new);
   }
}
