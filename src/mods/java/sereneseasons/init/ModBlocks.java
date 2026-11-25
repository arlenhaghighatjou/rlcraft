package sereneseasons.init;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import sereneseasons.api.ISSBlock;
import sereneseasons.api.SSBlocks;
import sereneseasons.block.BlockGreenhouseGlass;
import sereneseasons.block.BlockSeasonSensor;
import sereneseasons.core.SereneSeasons;
import sereneseasons.tileentity.TileEntitySeasonSensor;
import sereneseasons.util.BlockStateUtils;
import sereneseasons.util.inventory.CreativeTabSS;

public class ModBlocks {
   public ModBlocks() {
   }

   public static void init() {
      SSBlocks.greenhouse_glass = registerBlock(new BlockGreenhouseGlass(), "greenhouse_glass");
      SSBlocks.season_sensors[0] = registerBlock(new BlockSeasonSensor(BlockSeasonSensor.DetectorType.SPRING), "season_sensor_spring");
      SSBlocks.season_sensors[1] = registerBlock(new BlockSeasonSensor(BlockSeasonSensor.DetectorType.SUMMER), "season_sensor_summer").setCreativeTab((CreativeTabs)null);
      SSBlocks.season_sensors[2] = registerBlock(new BlockSeasonSensor(BlockSeasonSensor.DetectorType.AUTUMN), "season_sensor_autumn").setCreativeTab((CreativeTabs)null);
      SSBlocks.season_sensors[3] = registerBlock(new BlockSeasonSensor(BlockSeasonSensor.DetectorType.WINTER), "season_sensor_winter").setCreativeTab((CreativeTabs)null);
      GameRegistry.registerTileEntity(TileEntitySeasonSensor.class, "season_sensor");
   }

   public static void registerBlockItemModel(Block block, String stateName, int stateMeta) {
      Item item = Item.getItemFromBlock(block);
      SereneSeasons.proxy.registerItemVariantModel(item, stateName, stateMeta);
   }

   public static Block registerBlock(Block block, String blockName) {
      return registerBlock(block, blockName, CreativeTabSS.instance);
   }

   public static Block registerBlock(Block block, String blockName, CreativeTabs tab) {
      return registerBlock(block, blockName, tab, true);
   }

   public static Block registerBlock(Block block, String blockName, CreativeTabs tab, boolean registerItemModels) {
      Preconditions.checkNotNull(block, "Cannot register a null block");
      block.setUnlocalizedName(blockName);
      block.setCreativeTab(tab);
      if (block instanceof ISSBlock) {
         ISSBlock bopBlock = (ISSBlock)block;
         registerBlockWithItem(block, blockName, bopBlock.getItemClass());
         SereneSeasons.proxy.registerBlockSided(block);
         IBlockState defaultState = block.getDefaultState();
         if (defaultState == null) {
            defaultState = block.getBlockState().getBaseState();
            SereneSeasons.logger.error("Missing default state for " + block.getUnlocalizedName());
         }

         if (registerItemModels) {
            ImmutableSet<IBlockState> presets = BlockStateUtils.getBlockPresets(block);
            if (presets.isEmpty()) {
               registerBlockItemModel(block, blockName, 0);
            } else {
               UnmodifiableIterator var7 = presets.iterator();

               while(var7.hasNext()) {
                  IBlockState state = (IBlockState)var7.next();
                  String stateName = bopBlock.getStateName(state);
                  int stateMeta = block.getMetaFromState(state);
                  registerBlockItemModel(block, stateName, stateMeta);
               }
            }
         }
      } else {
         registerBlockWithItem(block, blockName, ItemBlock.class);
         registerBlockItemModel(block, blockName, 0);
      }

      return block;
   }

   private static void registerBlockWithItem(Block block, String blockName, Class<? extends ItemBlock> clazz) {
      try {
         Item itemBlock = clazz != null ? (Item)clazz.getConstructor(Block.class).newInstance(block) : null;
         new ResourceLocation("sereneseasons", blockName);
         block.setRegistryName(new ResourceLocation("sereneseasons", blockName));
         ForgeRegistries.BLOCKS.register(block);
         if (itemBlock != null) {
            itemBlock.setRegistryName(new ResourceLocation("sereneseasons", blockName));
            ForgeRegistries.ITEMS.register(itemBlock);
         }

      } catch (Exception var5) {
         throw new RuntimeException("An error occurred associating an item block during registration of " + blockName, var5);
      }
   }
}
