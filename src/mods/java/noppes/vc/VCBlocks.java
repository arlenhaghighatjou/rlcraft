package noppes.vc;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.vc.blocks.BlockBanner;
import noppes.vc.blocks.BlockBarrel;
import noppes.vc.blocks.BlockBasicLightable;
import noppes.vc.blocks.BlockBasicRotated;
import noppes.vc.blocks.BlockBeam;
import noppes.vc.blocks.BlockBigSign;
import noppes.vc.blocks.BlockBlood;
import noppes.vc.blocks.BlockBook;
import noppes.vc.blocks.BlockCampfire;
import noppes.vc.blocks.BlockCandle;
import noppes.vc.blocks.BlockCarpentryBench;
import noppes.vc.blocks.BlockChair;
import noppes.vc.blocks.BlockCouchWood;
import noppes.vc.blocks.BlockCouchWool;
import noppes.vc.blocks.BlockCrate;
import noppes.vc.blocks.BlockCrystal;
import noppes.vc.blocks.BlockLamp;
import noppes.vc.blocks.BlockPedestal;
import noppes.vc.blocks.BlockPlaceholder;
import noppes.vc.blocks.BlockShelf;
import noppes.vc.blocks.BlockSign;
import noppes.vc.blocks.BlockStool;
import noppes.vc.blocks.BlockTable;
import noppes.vc.blocks.BlockTallLamp;
import noppes.vc.blocks.BlockTombstone;
import noppes.vc.blocks.BlockTrading;
import noppes.vc.blocks.BlockWallBanner;
import noppes.vc.blocks.BlockWeaponRack;
import noppes.vc.blocks.tiles.TileAnvil;
import noppes.vc.blocks.tiles.TileBanner;
import noppes.vc.blocks.tiles.TileBarrel;
import noppes.vc.blocks.tiles.TileBeam;
import noppes.vc.blocks.tiles.TileBigSign;
import noppes.vc.blocks.tiles.TileBlood;
import noppes.vc.blocks.tiles.TileBook;
import noppes.vc.blocks.tiles.TileCampfire;
import noppes.vc.blocks.tiles.TileCandle;
import noppes.vc.blocks.tiles.TileCarpentryBench;
import noppes.vc.blocks.tiles.TileChair;
import noppes.vc.blocks.tiles.TileCouchWood;
import noppes.vc.blocks.tiles.TileCouchWool;
import noppes.vc.blocks.tiles.TileCrate;
import noppes.vc.blocks.tiles.TileLamp;
import noppes.vc.blocks.tiles.TilePedestal;
import noppes.vc.blocks.tiles.TileShelf;
import noppes.vc.blocks.tiles.TileSign;
import noppes.vc.blocks.tiles.TileStool;
import noppes.vc.blocks.tiles.TileTable;
import noppes.vc.blocks.tiles.TileTallLamp;
import noppes.vc.blocks.tiles.TileTombstone;
import noppes.vc.blocks.tiles.TileTrading;
import noppes.vc.blocks.tiles.TileWallBanner;
import noppes.vc.blocks.tiles.TileWeaponRack;
import noppes.vc.client.renderer.BlockBannerRenderer;
import noppes.vc.client.renderer.BlockBarrelRenderer;
import noppes.vc.client.renderer.BlockBeamRenderer;
import noppes.vc.client.renderer.BlockBigSignRenderer;
import noppes.vc.client.renderer.BlockBloodRenderer;
import noppes.vc.client.renderer.BlockBookRenderer;
import noppes.vc.client.renderer.BlockCampfireRenderer;
import noppes.vc.client.renderer.BlockCandleRenderer;
import noppes.vc.client.renderer.BlockCarpentryBenchRenderer;
import noppes.vc.client.renderer.BlockChairRenderer;
import noppes.vc.client.renderer.BlockCouchWoodRenderer;
import noppes.vc.client.renderer.BlockCouchWoolRenderer;
import noppes.vc.client.renderer.BlockCrateRenderer;
import noppes.vc.client.renderer.BlockLampRenderer;
import noppes.vc.client.renderer.BlockPedestalRenderer;
import noppes.vc.client.renderer.BlockShelfRenderer;
import noppes.vc.client.renderer.BlockSignRenderer;
import noppes.vc.client.renderer.BlockStoolRenderer;
import noppes.vc.client.renderer.BlockTableRenderer;
import noppes.vc.client.renderer.BlockTallLampRenderer;
import noppes.vc.client.renderer.BlockTombstoneRenderer;
import noppes.vc.client.renderer.BlockTradingRenderer;
import noppes.vc.client.renderer.BlockWallBannerRenderer;
import noppes.vc.client.renderer.BlockWeaponRackRenderer;
import noppes.vc.items.ItemBlockColored;
import noppes.vc.items.ItemBlockLight;
import noppes.vc.items.ItemBlockNamed;
import noppes.vc.items.ItemPlaceholder;

@ObjectHolder("variedcommodities")
public class VCBlocks {
   @ObjectHolder("trading_block")
   public static Block trading;
   @ObjectHolder("carpentry_bench")
   public static Block carpentry_bench;
   @ObjectHolder("blood_block")
   public static Block blood;
   @ObjectHolder("banner")
   public static Block banner;
   @ObjectHolder("wall_banner")
   public static Block wall_banner;
   @ObjectHolder("sign")
   public static Block sign;
   @ObjectHolder("tall_lamp")
   public static Block tall_lamp;
   @ObjectHolder("campfire")
   public static Block campfire;
   @ObjectHolder("campfire_unlit")
   public static Block campfire_unlit;
   @ObjectHolder("candle")
   public static Block candle;
   @ObjectHolder("candle_unlit")
   public static Block candle_unlit;
   @ObjectHolder("lamp")
   public static Block lamp;
   @ObjectHolder("lamp_unlit")
   public static Block lamp_unlit;
   @ObjectHolder("chair")
   public static Block chair;
   @ObjectHolder("couch_wool")
   public static Block couch_wool;
   @ObjectHolder("couch_wood")
   public static Block couch_wood;
   @ObjectHolder("crate")
   public static Block crate;
   @ObjectHolder("barrel")
   public static Block barrel;
   @ObjectHolder("weapon_rack")
   public static Block weapon_rack;
   @ObjectHolder("pedestal")
   public static Block pedestal;
   @ObjectHolder("crystal_block")
   public static Block crystal;
   @ObjectHolder("placeholder")
   public static Block placeholder;
   @ObjectHolder("stool")
   public static Block stool;
   @ObjectHolder("table")
   public static Block table;
   @ObjectHolder("shelf")
   public static Block shelf;
   @ObjectHolder("beam")
   public static Block beam;
   @ObjectHolder("big_sign")
   public static Block big_sign;
   @ObjectHolder("tombstone")
   public static Block tombstone;
   @ObjectHolder("book")
   public static Block book;
   private List<VCBlocks.RegisterContainer> blocks = new ArrayList();

   public VCBlocks() {
   }

   @SubscribeEvent
   public void registerBlocks(Register<Block> event) {
      this.blocks.clear();
      GameRegistry.registerTileEntity(TileTrading.class, "variedcommodities:trading_block");
      GameRegistry.registerTileEntity(TileCarpentryBench.class, "variedcommodities:carpentry_bench");
      GameRegistry.registerTileEntity(TileBlood.class, "variedcommodities:blood_block");
      GameRegistry.registerTileEntity(TileBanner.class, "variedcommodities:banner");
      GameRegistry.registerTileEntity(TileWallBanner.class, "variedcommodities:wall_banner");
      GameRegistry.registerTileEntity(TileTallLamp.class, "variedcommodities:tall_lamp");
      GameRegistry.registerTileEntity(TileChair.class, "variedcommodities:chair");
      GameRegistry.registerTileEntity(TileCouchWool.class, "variedcommodities:couch_wool");
      GameRegistry.registerTileEntity(TileCouchWood.class, "variedcommodities:couch_wood");
      GameRegistry.registerTileEntity(TileLamp.class, "variedcommodities:lamp");
      GameRegistry.registerTileEntity(TileCandle.class, "variedcommodities:candle");
      GameRegistry.registerTileEntity(TileCampfire.class, "variedcommodities:campfire");
      GameRegistry.registerTileEntity(TileBarrel.class, "variedcommodities:barrel");
      GameRegistry.registerTileEntity(TileCrate.class, "variedcommodities:crate");
      GameRegistry.registerTileEntity(TileWeaponRack.class, "variedcommodities:weapon_rack");
      GameRegistry.registerTileEntity(TilePedestal.class, "variedcommodities:pedestal");
      GameRegistry.registerTileEntity(TileSign.class, "variedcommodities:sign");
      GameRegistry.registerTileEntity(TileShelf.class, "variedcommodities:shelf");
      GameRegistry.registerTileEntity(TileBeam.class, "variedcommodities:beam");
      GameRegistry.registerTileEntity(TileStool.class, "variedcommodities:stool");
      GameRegistry.registerTileEntity(TileTable.class, "variedcommodities:table");
      GameRegistry.registerTileEntity(TileBook.class, "variedcommodities:book");
      GameRegistry.registerTileEntity(TileTombstone.class, "variedcommodities:tombstone");
      GameRegistry.registerTileEntity(TileBigSign.class, "variedcommodities:big_sign");
      this.register((new BlockTrading()).register("trading_block"), 0, ItemBlock.class);
      this.register((new BlockCarpentryBench()).register("carpentry_bench"), 1, ItemBlockNamed.class);
      this.registerSeperated((new BlockBlood()).register("blood_block"), 2, ItemBlock.class);
      this.register((new BlockBanner()).register("banner"), 4, ItemBlock.class);
      this.register((new BlockWallBanner()).register("wall_banner"), 4, ItemBlock.class);
      this.register((new BlockSign()).register("sign"), 5, ItemBlock.class);
      this.register((new BlockTallLamp()).register("tall_lamp"), 4, ItemBlock.class);
      this.register((new BlockCandle(true)).register("candle"), 0, ItemBlockLight.class);
      this.register((new BlockCandle(false)).register("candle_unlit").setUnlocalizedName("npcCandle").setCreativeTab((CreativeTabs)null), 0, ItemBlockLight.class);
      this.register((new BlockLamp(true)).register("lamp"), 0, ItemBlockLight.class);
      this.register((new BlockLamp(false)).register("lamp_unlit").setUnlocalizedName("npcLamp").setCreativeTab((CreativeTabs)null), 0, ItemBlockLight.class);
      this.register((new BlockCampfire(true)).register("campfire"), 0, ItemBlock.class);
      this.register((new BlockCampfire(false)).register("campfire_unlit").setUnlocalizedName("npcCampfire").setCreativeTab((CreativeTabs)null), 0, ItemBlock.class);
      this.register((new BlockChair()).register("chair"), 5, ItemBlock.class);
      this.register((new BlockCouchWool()).register("couch_wool"), 5, ItemBlock.class);
      this.register((new BlockCouchWood()).register("couch_wood"), 5, ItemBlock.class);
      this.register((new BlockCrate()).register("crate"), 5, ItemBlock.class);
      this.register((new BlockBarrel()).register("barrel"), 5, ItemBlock.class);
      this.register((new BlockWeaponRack()).register("weapon_rack"), 5, ItemBlock.class);
      this.register((new BlockPedestal()).register("pedestal"), 4, ItemBlock.class);
      this.register((new BlockCrystal()).register("crystal_block"), 15, ItemBlockColored.class);
      this.registerSeperated((Block)(new BlockPlaceholder()).setUnlocalizedName("placeholder").setRegistryName("variedcommodities", "placeholder"), 15, ItemPlaceholder.class);
      this.register((new BlockStool()).register("stool"), 5, ItemBlock.class);
      this.register((new BlockTable()).register("table"), 5, ItemBlock.class);
      this.register((new BlockShelf()).register("shelf"), 5, ItemBlock.class);
      this.register((new BlockBeam()).register("beam"), 5, ItemBlock.class);
      this.register((new BlockBigSign()).register("big_sign"), 0, ItemBlock.class);
      this.register((new BlockBook()).register("book"), 0, ItemBlock.class);
      this.register((new BlockTombstone()).register("tombstone"), 2, ItemBlock.class);

      for(VCBlocks.RegisterContainer reg : this.blocks) {
         event.getRegistry().register(reg.block);
      }

   }

   private void register(Block block, int meta, Class<? extends ItemBlock> itemclass) {
      try {
         Item item = (Item)itemclass.getConstructor(Block.class).newInstance(block);
         item.setRegistryName(block.getRegistryName());
         this.blocks.add(new VCBlocks.RegisterContainer(block.getRegistryName().getResourcePath(), block, item, meta));
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   private void registerSeperated(Block block, int meta, Class<? extends ItemBlock> itemclass) {
      try {
         Item item = (Item)itemclass.getConstructor(Block.class).newInstance(block);
         item.setRegistryName(block.getRegistryName());
         this.blocks.add(new VCBlocks.RegisterContainer(block.getRegistryName().getResourcePath(), block, item, meta, true));
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   @SubscribeEvent
   public void registerItems(Register<Item> event) {
      for(VCBlocks.RegisterContainer reg : this.blocks) {
         reg.item.setHasSubtypes(reg.meta > 0);
         event.getRegistry().register(reg.item);
      }

      ((ItemBlockNamed)Item.getItemFromBlock(carpentry_bench)).names = new String[]{"tile.carpentry_bench", "tile.anvil"};
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public void registerClientItems(Register<Item> event) {
      for(VCBlocks.RegisterContainer reg : this.blocks) {
         if (reg.seperated) {
            for(int i = 0; i <= reg.meta; ++i) {
               ModelLoader.registerItemVariants(reg.item, new ResourceLocation[]{new ResourceLocation("variedcommodities", reg.name + "_" + i)});
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void registerModels(ModelRegistryEvent event) {
      ModelLoader.setCustomStateMapper(carpentry_bench, (new Builder()).ignore(new IProperty[]{BlockCarpentryBench.ROTATION, BlockCarpentryBench.TYPE}).build());
      ModelLoader.setCustomStateMapper(banner, (new Builder()).ignore(new IProperty[]{BlockBanner.DAMAGE, BlockBanner.TOP}).build());
      ModelLoader.setCustomStateMapper(wall_banner, (new Builder()).ignore(new IProperty[]{BlockWallBanner.DAMAGE}).build());
      ModelLoader.setCustomStateMapper(chair, (new Builder()).ignore(new IProperty[]{BlockChair.DAMAGE}).build());
      ModelLoader.setCustomStateMapper(couch_wood, (new Builder()).ignore(new IProperty[]{BlockCouchWood.DAMAGE}).build());
      ModelLoader.setCustomStateMapper(couch_wool, (new Builder()).ignore(new IProperty[]{BlockCouchWool.DAMAGE}).build());
      ModelLoader.setCustomStateMapper(tall_lamp, (new Builder()).ignore(new IProperty[]{BlockTallLamp.DAMAGE, BlockTallLamp.IS_TOP}).build());
      ModelLoader.setCustomStateMapper(lamp, (new Builder()).ignore(new IProperty[]{BlockBasicRotated.DAMAGE, BlockBasicLightable.LIT}).build());
      ModelLoader.setCustomStateMapper(lamp_unlit, (new Builder()).ignore(new IProperty[]{BlockBasicRotated.DAMAGE, BlockBasicLightable.LIT}).build());
      ModelLoader.setCustomStateMapper(candle, (new Builder()).ignore(new IProperty[]{BlockBasicRotated.DAMAGE, BlockBasicLightable.LIT}).build());
      ModelLoader.setCustomStateMapper(candle_unlit, (new Builder()).ignore(new IProperty[]{BlockBasicRotated.DAMAGE, BlockBasicLightable.LIT}).build());
      ModelLoader.setCustomStateMapper(campfire, (new Builder()).ignore(new IProperty[]{BlockBasicRotated.DAMAGE, BlockBasicLightable.LIT}).build());
      ModelLoader.setCustomStateMapper(campfire_unlit, (new Builder()).ignore(new IProperty[]{BlockBasicRotated.DAMAGE, BlockBasicLightable.LIT}).build());
      ModelLoader.setCustomStateMapper(barrel, (new Builder()).ignore(new IProperty[]{BlockBasicRotated.DAMAGE}).build());
      ModelLoader.setCustomStateMapper(crate, (new Builder()).ignore(new IProperty[]{BlockBasicRotated.DAMAGE}).build());
      ModelLoader.setCustomStateMapper(weapon_rack, (new Builder()).ignore(new IProperty[]{BlockWeaponRack.DAMAGE, BlockWeaponRack.IS_TOP}).build());
      ModelLoader.setCustomStateMapper(pedestal, (new Builder()).ignore(new IProperty[]{BlockBasicRotated.DAMAGE}).build());
      ModelLoader.setCustomStateMapper(stool, (new Builder()).ignore(new IProperty[]{BlockBasicRotated.DAMAGE}).build());
      ModelLoader.setCustomStateMapper(sign, (new Builder()).ignore(new IProperty[]{BlockBasicRotated.DAMAGE}).build());
      ModelLoader.setCustomStateMapper(table, (new Builder()).ignore(new IProperty[]{BlockBasicRotated.DAMAGE}).build());
      ModelLoader.setCustomStateMapper(shelf, (new Builder()).ignore(new IProperty[]{BlockBasicRotated.DAMAGE}).build());
      ModelLoader.setCustomStateMapper(beam, (new Builder()).ignore(new IProperty[]{BlockBasicRotated.DAMAGE}).build());
      ModelLoader.setCustomStateMapper(book, (new Builder()).ignore(new IProperty[]{BlockBasicRotated.DAMAGE}).build());
      ModelLoader.setCustomStateMapper(tombstone, (new Builder()).ignore(new IProperty[]{BlockBasicRotated.DAMAGE}).build());
      ModelLoader.setCustomStateMapper(big_sign, (new Builder()).ignore(new IProperty[]{BlockBasicRotated.DAMAGE}).build());
      ModelLoader.setCustomStateMapper(blood, new StateMapperBase() {
         protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            LinkedHashMap linkedhashmap = Maps.newLinkedHashMap(state.getProperties());
            IProperty iproperty = BlockBlood.TYPE;
            String s = iproperty.getName((Comparable)linkedhashmap.remove(BlockBlood.TYPE));
            return new ModelResourceLocation("variedcommodities:blood_block_" + s, this.getPropertyString(linkedhashmap));
         }
      });
      ModelLoader.setCustomStateMapper(placeholder, new StateMapperBase() {
         protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
            LinkedHashMap linkedhashmap = Maps.newLinkedHashMap(state.getProperties());
            IProperty iproperty = BlockPlaceholder.DAMAGE;
            String s = iproperty.getName((Comparable)linkedhashmap.remove(BlockPlaceholder.DAMAGE));
            return new ModelResourceLocation("variedcommodities:placeholder_" + s, this.getPropertyString(linkedhashmap));
         }
      });

      for(VCBlocks.RegisterContainer reg : this.blocks) {
         for(int i = 0; i <= reg.meta; ++i) {
            if (reg.seperated) {
               ModelLoader.setCustomModelResourceLocation(reg.item, i, new ModelResourceLocation(reg.block.getRegistryName() + "_" + i, "inventory"));
            } else {
               ModelLoader.setCustomModelResourceLocation(reg.item, i, new ModelResourceLocation(reg.block.getRegistryName(), "inventory"));
            }
         }
      }

      ClientRegistry.bindTileEntitySpecialRenderer(TileTrading.class, new BlockTradingRenderer());
      ClientRegistry.bindTileEntitySpecialRenderer(TileAnvil.class, new BlockCarpentryBenchRenderer(true));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCarpentryBench.class, new BlockCarpentryBenchRenderer(false));
      ClientRegistry.bindTileEntitySpecialRenderer(TileBlood.class, new BlockBloodRenderer());
      ClientRegistry.bindTileEntitySpecialRenderer(TileBanner.class, new BlockBannerRenderer(0));
      ClientRegistry.bindTileEntitySpecialRenderer(TileBanner.TileBanner1.class, new BlockBannerRenderer(1));
      ClientRegistry.bindTileEntitySpecialRenderer(TileBanner.TileBanner2.class, new BlockBannerRenderer(2));
      ClientRegistry.bindTileEntitySpecialRenderer(TileBanner.TileBanner3.class, new BlockBannerRenderer(3));
      ClientRegistry.bindTileEntitySpecialRenderer(TileBanner.TileBanner4.class, new BlockBannerRenderer(4));
      ClientRegistry.bindTileEntitySpecialRenderer(TileWallBanner.class, new BlockWallBannerRenderer(0));
      ClientRegistry.bindTileEntitySpecialRenderer(TileWallBanner.TileWallBanner1.class, new BlockWallBannerRenderer(1));
      ClientRegistry.bindTileEntitySpecialRenderer(TileWallBanner.TileWallBanner2.class, new BlockWallBannerRenderer(2));
      ClientRegistry.bindTileEntitySpecialRenderer(TileWallBanner.TileWallBanner3.class, new BlockWallBannerRenderer(3));
      ClientRegistry.bindTileEntitySpecialRenderer(TileWallBanner.TileWallBanner4.class, new BlockWallBannerRenderer(4));
      ClientRegistry.bindTileEntitySpecialRenderer(TileSign.class, new BlockSignRenderer(0));
      ClientRegistry.bindTileEntitySpecialRenderer(TileSign.TileSign1.class, new BlockSignRenderer(1));
      ClientRegistry.bindTileEntitySpecialRenderer(TileSign.TileSign2.class, new BlockSignRenderer(2));
      ClientRegistry.bindTileEntitySpecialRenderer(TileSign.TileSign3.class, new BlockSignRenderer(3));
      ClientRegistry.bindTileEntitySpecialRenderer(TileSign.TileSign4.class, new BlockSignRenderer(4));
      ClientRegistry.bindTileEntitySpecialRenderer(TileSign.TileSign5.class, new BlockSignRenderer(5));
      ClientRegistry.bindTileEntitySpecialRenderer(TileTallLamp.class, new BlockTallLampRenderer(0));
      ClientRegistry.bindTileEntitySpecialRenderer(TileTallLamp.TileTallLamp1.class, new BlockTallLampRenderer(1));
      ClientRegistry.bindTileEntitySpecialRenderer(TileTallLamp.TileTallLamp2.class, new BlockTallLampRenderer(2));
      ClientRegistry.bindTileEntitySpecialRenderer(TileTallLamp.TileTallLamp3.class, new BlockTallLampRenderer(3));
      ClientRegistry.bindTileEntitySpecialRenderer(TileTallLamp.TileTallLamp4.class, new BlockTallLampRenderer(4));
      ClientRegistry.bindTileEntitySpecialRenderer(TileChair.class, new BlockChairRenderer(0));
      ClientRegistry.bindTileEntitySpecialRenderer(TileChair.TileChair1.class, new BlockChairRenderer(1));
      ClientRegistry.bindTileEntitySpecialRenderer(TileChair.TileChair2.class, new BlockChairRenderer(2));
      ClientRegistry.bindTileEntitySpecialRenderer(TileChair.TileChair3.class, new BlockChairRenderer(3));
      ClientRegistry.bindTileEntitySpecialRenderer(TileChair.TileChair4.class, new BlockChairRenderer(4));
      ClientRegistry.bindTileEntitySpecialRenderer(TileChair.TileChair5.class, new BlockChairRenderer(5));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCouchWood.class, new BlockCouchWoodRenderer(0));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCouchWood.TileCouchWood1.class, new BlockCouchWoodRenderer(1));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCouchWood.TileCouchWood2.class, new BlockCouchWoodRenderer(2));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCouchWood.TileCouchWood3.class, new BlockCouchWoodRenderer(3));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCouchWood.TileCouchWood4.class, new BlockCouchWoodRenderer(4));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCouchWood.TileCouchWood5.class, new BlockCouchWoodRenderer(5));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCouchWool.class, new BlockCouchWoolRenderer(0));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCouchWool.TileCouchWool1.class, new BlockCouchWoolRenderer(1));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCouchWool.TileCouchWool2.class, new BlockCouchWoolRenderer(2));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCouchWool.TileCouchWool3.class, new BlockCouchWoolRenderer(3));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCouchWool.TileCouchWool4.class, new BlockCouchWoolRenderer(4));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCouchWool.TileCouchWool5.class, new BlockCouchWoolRenderer(5));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCandle.class, new BlockCandleRenderer());
      ClientRegistry.bindTileEntitySpecialRenderer(TileLamp.class, new BlockLampRenderer());
      ClientRegistry.bindTileEntitySpecialRenderer(TileCampfire.class, new BlockCampfireRenderer());
      ClientRegistry.bindTileEntitySpecialRenderer(TileBarrel.class, new BlockBarrelRenderer(0));
      ClientRegistry.bindTileEntitySpecialRenderer(TileBarrel.TileBarrel1.class, new BlockBarrelRenderer(1));
      ClientRegistry.bindTileEntitySpecialRenderer(TileBarrel.TileBarrel2.class, new BlockBarrelRenderer(2));
      ClientRegistry.bindTileEntitySpecialRenderer(TileBarrel.TileBarrel3.class, new BlockBarrelRenderer(3));
      ClientRegistry.bindTileEntitySpecialRenderer(TileBarrel.TileBarrel4.class, new BlockBarrelRenderer(4));
      ClientRegistry.bindTileEntitySpecialRenderer(TileBarrel.TileBarrel5.class, new BlockBarrelRenderer(5));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCrate.class, new BlockCrateRenderer(0));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCrate.TileCrate1.class, new BlockCrateRenderer(1));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCrate.TileCrate2.class, new BlockCrateRenderer(2));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCrate.TileCrate3.class, new BlockCrateRenderer(3));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCrate.TileCrate4.class, new BlockCrateRenderer(4));
      ClientRegistry.bindTileEntitySpecialRenderer(TileCrate.TileCrate5.class, new BlockCrateRenderer(5));
      ClientRegistry.bindTileEntitySpecialRenderer(TileWeaponRack.class, new BlockWeaponRackRenderer(0));
      ClientRegistry.bindTileEntitySpecialRenderer(TileWeaponRack.TileWeaponRack1.class, new BlockWeaponRackRenderer(1));
      ClientRegistry.bindTileEntitySpecialRenderer(TileWeaponRack.TileWeaponRack2.class, new BlockWeaponRackRenderer(2));
      ClientRegistry.bindTileEntitySpecialRenderer(TileWeaponRack.TileWeaponRack3.class, new BlockWeaponRackRenderer(3));
      ClientRegistry.bindTileEntitySpecialRenderer(TileWeaponRack.TileWeaponRack4.class, new BlockWeaponRackRenderer(4));
      ClientRegistry.bindTileEntitySpecialRenderer(TileWeaponRack.TileWeaponRack5.class, new BlockWeaponRackRenderer(5));
      ClientRegistry.bindTileEntitySpecialRenderer(TilePedestal.class, new BlockPedestalRenderer(0));
      ClientRegistry.bindTileEntitySpecialRenderer(TilePedestal.TilePedestal1.class, new BlockPedestalRenderer(1));
      ClientRegistry.bindTileEntitySpecialRenderer(TilePedestal.TilePedestal2.class, new BlockPedestalRenderer(2));
      ClientRegistry.bindTileEntitySpecialRenderer(TilePedestal.TilePedestal3.class, new BlockPedestalRenderer(3));
      ClientRegistry.bindTileEntitySpecialRenderer(TilePedestal.TilePedestal4.class, new BlockPedestalRenderer(4));
      ClientRegistry.bindTileEntitySpecialRenderer(TileStool.class, new BlockStoolRenderer(0));
      ClientRegistry.bindTileEntitySpecialRenderer(TileStool.TileStool1.class, new BlockStoolRenderer(1));
      ClientRegistry.bindTileEntitySpecialRenderer(TileStool.TileStool2.class, new BlockStoolRenderer(2));
      ClientRegistry.bindTileEntitySpecialRenderer(TileStool.TileStool3.class, new BlockStoolRenderer(3));
      ClientRegistry.bindTileEntitySpecialRenderer(TileStool.TileStool4.class, new BlockStoolRenderer(4));
      ClientRegistry.bindTileEntitySpecialRenderer(TileStool.TileStool5.class, new BlockStoolRenderer(5));
      ClientRegistry.bindTileEntitySpecialRenderer(TileBeam.class, new BlockBeamRenderer(0));
      ClientRegistry.bindTileEntitySpecialRenderer(TileBeam.TileBeam1.class, new BlockBeamRenderer(1));
      ClientRegistry.bindTileEntitySpecialRenderer(TileBeam.TileBeam2.class, new BlockBeamRenderer(2));
      ClientRegistry.bindTileEntitySpecialRenderer(TileBeam.TileBeam3.class, new BlockBeamRenderer(3));
      ClientRegistry.bindTileEntitySpecialRenderer(TileBeam.TileBeam4.class, new BlockBeamRenderer(4));
      ClientRegistry.bindTileEntitySpecialRenderer(TileBeam.TileBeam5.class, new BlockBeamRenderer(5));
      ClientRegistry.bindTileEntitySpecialRenderer(TileTable.class, new BlockTableRenderer(0));
      ClientRegistry.bindTileEntitySpecialRenderer(TileTable.TileTable1.class, new BlockTableRenderer(1));
      ClientRegistry.bindTileEntitySpecialRenderer(TileTable.TileTable2.class, new BlockTableRenderer(2));
      ClientRegistry.bindTileEntitySpecialRenderer(TileTable.TileTable3.class, new BlockTableRenderer(3));
      ClientRegistry.bindTileEntitySpecialRenderer(TileTable.TileTable4.class, new BlockTableRenderer(4));
      ClientRegistry.bindTileEntitySpecialRenderer(TileTable.TileTable5.class, new BlockTableRenderer(5));
      ClientRegistry.bindTileEntitySpecialRenderer(TileShelf.class, new BlockShelfRenderer(0));
      ClientRegistry.bindTileEntitySpecialRenderer(TileShelf.TileShelf1.class, new BlockShelfRenderer(1));
      ClientRegistry.bindTileEntitySpecialRenderer(TileShelf.TileShelf2.class, new BlockShelfRenderer(2));
      ClientRegistry.bindTileEntitySpecialRenderer(TileShelf.TileShelf3.class, new BlockShelfRenderer(3));
      ClientRegistry.bindTileEntitySpecialRenderer(TileShelf.TileShelf4.class, new BlockShelfRenderer(4));
      ClientRegistry.bindTileEntitySpecialRenderer(TileShelf.TileShelf5.class, new BlockShelfRenderer(5));
      ClientRegistry.bindTileEntitySpecialRenderer(TileTombstone.class, new BlockTombstoneRenderer(0));
      ClientRegistry.bindTileEntitySpecialRenderer(TileTombstone.TileTombstone1.class, new BlockTombstoneRenderer(1));
      ClientRegistry.bindTileEntitySpecialRenderer(TileTombstone.TileTombstone2.class, new BlockTombstoneRenderer(2));
      ClientRegistry.bindTileEntitySpecialRenderer(TileBook.class, new BlockBookRenderer());
      ClientRegistry.bindTileEntitySpecialRenderer(TileBigSign.class, new BlockBigSignRenderer());
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(trading), 0, TileTrading.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(carpentry_bench), 0, TileCarpentryBench.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(carpentry_bench), 1, TileAnvil.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(banner), 0, TileBanner.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(banner), 1, TileBanner.TileBanner1.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(banner), 2, TileBanner.TileBanner2.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(banner), 3, TileBanner.TileBanner3.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(banner), 4, TileBanner.TileBanner4.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(wall_banner), 0, TileWallBanner.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(wall_banner), 1, TileWallBanner.TileWallBanner1.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(wall_banner), 2, TileWallBanner.TileWallBanner2.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(wall_banner), 3, TileWallBanner.TileWallBanner3.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(wall_banner), 4, TileWallBanner.TileWallBanner4.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(sign), 0, TileSign.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(sign), 1, TileSign.TileSign1.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(sign), 2, TileSign.TileSign2.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(sign), 3, TileSign.TileSign3.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(sign), 4, TileSign.TileSign4.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(sign), 5, TileSign.TileSign5.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(tall_lamp), 0, TileTallLamp.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(tall_lamp), 1, TileTallLamp.TileTallLamp1.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(tall_lamp), 2, TileTallLamp.TileTallLamp2.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(tall_lamp), 3, TileTallLamp.TileTallLamp3.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(tall_lamp), 4, TileTallLamp.TileTallLamp4.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(chair), 0, TileChair.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(chair), 1, TileChair.TileChair1.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(chair), 2, TileChair.TileChair2.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(chair), 3, TileChair.TileChair3.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(chair), 4, TileChair.TileChair4.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(chair), 5, TileChair.TileChair5.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(couch_wood), 0, TileCouchWood.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(couch_wood), 1, TileCouchWood.TileCouchWood1.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(couch_wood), 2, TileCouchWood.TileCouchWood2.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(couch_wood), 3, TileCouchWood.TileCouchWood3.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(couch_wood), 4, TileCouchWood.TileCouchWood4.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(couch_wood), 5, TileCouchWood.TileCouchWood5.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(couch_wool), 0, TileCouchWool.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(couch_wool), 1, TileCouchWool.TileCouchWool1.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(couch_wool), 2, TileCouchWool.TileCouchWool2.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(couch_wool), 3, TileCouchWool.TileCouchWool3.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(couch_wool), 4, TileCouchWool.TileCouchWool4.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(couch_wool), 5, TileCouchWool.TileCouchWool5.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(candle), 0, TileCandle.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(lamp), 0, TileLamp.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(campfire), 0, TileCampfire.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(barrel), 0, TileBarrel.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(barrel), 1, TileBarrel.TileBarrel1.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(barrel), 2, TileBarrel.TileBarrel2.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(barrel), 3, TileBarrel.TileBarrel3.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(barrel), 4, TileBarrel.TileBarrel4.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(barrel), 5, TileBarrel.TileBarrel5.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(crate), 0, TileCrate.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(crate), 1, TileCrate.TileCrate1.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(crate), 2, TileCrate.TileCrate2.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(crate), 3, TileCrate.TileCrate3.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(crate), 4, TileCrate.TileCrate4.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(crate), 5, TileCrate.TileCrate5.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(weapon_rack), 0, TileWeaponRack.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(weapon_rack), 1, TileWeaponRack.TileWeaponRack1.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(weapon_rack), 2, TileWeaponRack.TileWeaponRack2.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(weapon_rack), 3, TileWeaponRack.TileWeaponRack3.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(weapon_rack), 4, TileWeaponRack.TileWeaponRack4.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(weapon_rack), 5, TileWeaponRack.TileWeaponRack5.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(pedestal), 0, TilePedestal.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(pedestal), 1, TilePedestal.TilePedestal1.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(pedestal), 2, TilePedestal.TilePedestal2.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(pedestal), 3, TilePedestal.TilePedestal3.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(pedestal), 4, TilePedestal.TilePedestal4.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(stool), 0, TileStool.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(stool), 1, TileStool.TileStool1.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(stool), 2, TileStool.TileStool2.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(stool), 3, TileStool.TileStool3.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(stool), 4, TileStool.TileStool4.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(stool), 5, TileStool.TileStool5.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(table), 0, TileTable.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(table), 1, TileTable.TileTable1.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(table), 2, TileTable.TileTable2.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(table), 3, TileTable.TileTable3.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(table), 4, TileTable.TileTable4.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(table), 5, TileTable.TileTable5.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(shelf), 0, TileShelf.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(shelf), 1, TileShelf.TileShelf1.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(shelf), 2, TileShelf.TileShelf2.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(shelf), 3, TileShelf.TileShelf3.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(shelf), 4, TileShelf.TileShelf4.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(shelf), 5, TileShelf.TileShelf5.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(beam), 0, TileBeam.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(beam), 1, TileBeam.TileBeam1.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(beam), 2, TileBeam.TileBeam2.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(beam), 3, TileBeam.TileBeam3.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(beam), 4, TileBeam.TileBeam4.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(beam), 5, TileBeam.TileBeam5.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(big_sign), 0, TileBigSign.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(book), 0, TileBook.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(tombstone), 0, TileTombstone.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(tombstone), 1, TileTombstone.TileTombstone1.class);
      ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(tombstone), 2, TileTombstone.TileTombstone2.class);
   }

   class RegisterContainer {
      public String name;
      public Block block;
      public Item item;
      public int meta;
      public boolean seperated = false;

      public RegisterContainer(String name, Block block, Item item, int meta) {
         this.name = name;
         this.block = block;
         this.item = item;
         this.meta = meta;
      }

      public RegisterContainer(String name, Block block, Item item, int meta, boolean bo) {
         this.name = name;
         this.block = block;
         this.item = item;
         this.meta = meta;
         this.seperated = bo;
      }
   }
}
