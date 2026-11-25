package knightminer.inspirations.utility;

import com.google.common.collect.Maps;
import java.util.LinkedHashMap;
import java.util.Locale;
import javax.annotation.Nonnull;
import knightminer.inspirations.common.ClientProxy;
import knightminer.inspirations.library.Util;
import knightminer.inspirations.library.client.IgnoreAllStateMapper;
import knightminer.inspirations.library.client.NameStateMapper;
import knightminer.inspirations.library.client.PropertyStateMapper;
import knightminer.inspirations.utility.block.BlockBricksButton;
import knightminer.inspirations.utility.block.BlockCollector;
import knightminer.inspirations.utility.block.BlockRedstoneBarrel;
import knightminer.inspirations.utility.block.BlockTorchLever;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.BlockTrapDoor.DoorHalf;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UtilityClientProxy extends ClientProxy {
   private static final ResourceLocation CARPETED_TRAPDOOR = Util.getResource("carpeted_trapdoor");

   public UtilityClientProxy() {
   }

   @SubscribeEvent
   public void registerModels(ModelRegistryEvent event) {
      setModelStateMapper(InspirationsUtility.torchLever, new UtilityClientProxy.TorchLeverStateMapper());
      setModelStateMapper(InspirationsUtility.redstoneTorchLever, new UtilityClientProxy.RedstoneTorchLeverStateMapper(false));
      setModelStateMapper(InspirationsUtility.redstoneTorchLeverPowered, new UtilityClientProxy.RedstoneTorchLeverStateMapper(true));
      setModelStateMapper(InspirationsUtility.bricksButton, new PropertyStateMapper(BlockBricksButton.TYPE, new IProperty[0]));
      IStateMapper carpetedPressurePlate = new NameStateMapper(Util.getResource("carpeted_pressure_plate"), new IProperty[0]);
      setModelStateMapper(InspirationsUtility.carpetedPressurePlate1, carpetedPressurePlate);
      setModelStateMapper(InspirationsUtility.carpetedPressurePlate2, carpetedPressurePlate);
      setModelStateMapper(InspirationsUtility.collector, (new Builder()).ignore(new IProperty[]{BlockCollector.TRIGGERED}).build());
      setModelStateMapper(InspirationsUtility.pipe, IgnoreAllStateMapper.INSTANCE);
      registerItemModel(InspirationsUtility.torchLever);
      registerItemModel(InspirationsUtility.redstoneTorchLever);
      registerItemModel(InspirationsUtility.redstoneBarrel);
      registerItemModel(InspirationsUtility.collector);
      registerItemModel(InspirationsUtility.pipe);
      registerItemModel(InspirationsUtility.bricksButton, 0, Util.getResource("bricks_button/bricks"));
      registerItemModel(InspirationsUtility.bricksButton, 1, Util.getResource("bricks_button/nether"));
      this.registerCarpetedTrapdoorModels(InspirationsUtility.carpetedTrapdoors);
   }

   private void registerCarpetedTrapdoorModels(Block[] blocks) {
      if (blocks != null) {
         for(int i = 0; i < blocks.length; ++i) {
            EnumDyeColor color = EnumDyeColor.byMetadata(i);
            registerItemModel(blocks[i], 0, CARPETED_TRAPDOOR, String.format("color=%s,facing=north,shape=bottom", color.getName()));
            setModelStateMapper(blocks[i], new UtilityClientProxy.CarpetedTrapdoorStateMapper(color));
         }
      }

   }

   @SubscribeEvent
   public void registerBlockColors(net.minecraftforge.client.event.ColorHandlerEvent.Block event) {
      BlockColors blockColors = event.getBlockColors();
      registerBlockColors(blockColors, (state, world, pos, tintIndex) -> {
         if (tintIndex == 1) {
            int level = ((Integer)state.getValue(BlockRedstoneBarrel.LEVEL)).intValue();
            if (level > 0) {
               return BlockRedstoneWire.colorMultiplier(level);
            }
         }

         return -1;
      }, new Block[]{InspirationsUtility.redstoneBarrel});
   }

   private static class CarpetedTrapdoorStateMapper extends StateMapperBase {
      private static final PropertyEnum<UtilityClientProxy.CarpetedTrapdoorStateMapper.TrapdoorShape> SHAPE = PropertyEnum.create("shape", UtilityClientProxy.CarpetedTrapdoorStateMapper.TrapdoorShape.class);
      private EnumDyeColor color;

      public CarpetedTrapdoorStateMapper(EnumDyeColor color) {
         this.color = color;
      }

      @Nonnull
      protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
         LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap();
         map.put(BlockColored.COLOR, this.color);
         map.put(BlockTrapDoor.FACING, state.getValue(BlockTrapDoor.FACING));
         UtilityClientProxy.CarpetedTrapdoorStateMapper.TrapdoorShape shape = null;
         boolean open = ((Boolean)state.getValue(BlockTrapDoor.OPEN)).booleanValue();
         boolean bottom = state.getValue(BlockTrapDoor.HALF) == DoorHalf.BOTTOM;
         if (bottom) {
            shape = open ? UtilityClientProxy.CarpetedTrapdoorStateMapper.TrapdoorShape.BOTTOM_OPEN : UtilityClientProxy.CarpetedTrapdoorStateMapper.TrapdoorShape.BOTTOM;
         } else {
            shape = open ? UtilityClientProxy.CarpetedTrapdoorStateMapper.TrapdoorShape.TOP_OPEN : UtilityClientProxy.CarpetedTrapdoorStateMapper.TrapdoorShape.TOP;
         }

         map.put(SHAPE, shape);
         return new ModelResourceLocation(UtilityClientProxy.CARPETED_TRAPDOOR, this.getPropertyString(map));
      }

      private static enum TrapdoorShape implements IStringSerializable {
         BOTTOM,
         BOTTOM_OPEN,
         TOP,
         TOP_OPEN;

         private TrapdoorShape() {
         }

         public String getName() {
            return this.name().toLowerCase(Locale.US);
         }
      }
   }

   private static class RedstoneTorchLeverStateMapper extends StateMapperBase {
      private boolean powered;

      public RedstoneTorchLeverStateMapper(boolean powered) {
         this.powered = powered;
      }

      @Nonnull
      protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
         ResourceLocation res = InspirationsUtility.redstoneTorchLever.getRegistryName();
         LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
         map.put(BlockLever.POWERED, Boolean.valueOf(this.powered));
         return new ModelResourceLocation(res, this.getPropertyString(map));
      }
   }

   private static class TorchLeverStateMapper extends StateMapperBase {
      private TorchLeverStateMapper() {
      }

      @Nonnull
      protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
         ResourceLocation base = state.getBlock().getRegistryName();
         LinkedHashMap<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
         String suffix = "";
         if (state.getValue(BlockTorchLever.FACING) == EnumFacing.UP) {
            map.remove(BlockTorchLever.FACING);
         } else {
            map.remove(BlockTorchLever.SIDE);
            suffix = "_wall";
         }

         ResourceLocation res = new ResourceLocation(base.getResourceDomain(), base.getResourcePath() + suffix);
         return new ModelResourceLocation(res, this.getPropertyString(map));
      }
   }
}
