package vazkii.quark.world.base;

import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import vazkii.quark.world.feature.Basalt;
import vazkii.quark.world.feature.RevampStoneGen;

public enum EnumStonelingVariant implements IEntityLivingData {
   STONE("stoneling", "stone", stoneState(EnumType.STONE), isBlock(Blocks.STONE).and(hasProp(BlockStone.VARIANT, EnumType.STONE)).or(isBlock(Blocks.COBBLESTONE))),
   ANDESITE("stoneling_andesite", "stoneAndesite", stoneState(EnumType.ANDESITE), isBlock(Blocks.STONE).and(hasProp(BlockStone.VARIANT, EnumType.ANDESITE, EnumType.ANDESITE_SMOOTH))),
   DIORITE("stoneling_diorite", "stoneDiorite", stoneState(EnumType.DIORITE), isBlock(Blocks.STONE).and(hasProp(BlockStone.VARIANT, EnumType.DIORITE, EnumType.DIORITE_SMOOTH))),
   GRANITE("stoneling_granite", "stoneGranite", stoneState(EnumType.GRANITE), isBlock(Blocks.STONE).and(hasProp(BlockStone.VARIANT, EnumType.GRANITE, EnumType.GRANITE_SMOOTH))),
   LIMESTONE("stoneling_limestone", "stoneLimestone", fromNullableBlock(() -> {
      return RevampStoneGen.limestone;
   }), isBlock(() -> {
      return RevampStoneGen.limestone;
   })),
   BASALT("stoneling_basalt", "stoneBasalt", fromNullableBlock(() -> {
      return Basalt.basalt;
   }), isBlock(() -> {
      return Basalt.basalt;
   })),
   MARBLE("stoneling_marble", "stoneMarble", fromNullableBlock(() -> {
      return RevampStoneGen.marble;
   }), isBlock(() -> {
      return RevampStoneGen.marble;
   }));

   private final ResourceLocation texture;
   private final String oreKey;
   private final Supplier<IBlockState> stateSupplier;
   private IBlockState state;
   private final Predicate<IBlockState> allowedStates;

   private static Supplier<IBlockState> fromNullableBlock(Supplier<Block> block) {
      return () -> {
         Block inst = (Block)block.get();
         return inst == null ? null : inst.getDefaultState();
      };
   }

   private static Supplier<IBlockState> stoneState(EnumType type) {
      return () -> {
         return Blocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, type);
      };
   }

   private static Predicate<IBlockState> isBlock(Block type) {
      return (state) -> {
         return state.getBlock() == type;
      };
   }

   private static Predicate<IBlockState> isBlock(Supplier<Block> type) {
      return (state) -> {
         return state.getBlock() == type.get();
      };
   }

   @SafeVarargs
   private static <T extends Comparable<T>> Predicate<IBlockState> hasProp(IProperty<T> prop, T... valuesAllowed) {
      return (state) -> {
         T value = (T)state.getValue(prop);

         for(T test : valuesAllowed) {
            if (value == test) {
               return true;
            }
         }

         return false;
      };
   }

   private EnumStonelingVariant(String variantPath, String oreKey, Supplier<IBlockState> displayState, Predicate<IBlockState> accepts) {
      this.texture = new ResourceLocation("quark", "textures/entity/" + variantPath + ".png");
      this.oreKey = oreKey;
      this.stateSupplier = displayState;
      this.allowedStates = accepts;
   }

   public static EnumStonelingVariant byIndex(byte index) {
      EnumStonelingVariant[] values = values();
      return values[MathHelper.clamp(index, 0, values.length - 1)];
   }

   public byte getIndex() {
      return (byte)this.ordinal();
   }

   public ResourceLocation getTexture() {
      return this.texture;
   }

   public String getOreKey() {
      return this.oreKey;
   }

   public IBlockState getDisplayState() {
      if (this.state == null) {
         this.state = (IBlockState)this.stateSupplier.get();
      }

      return this.state;
   }

   public boolean acceptsState(IBlockState state) {
      return this.allowedStates.test(state);
   }
}
