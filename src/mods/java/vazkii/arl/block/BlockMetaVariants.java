package vazkii.arl.block;

import java.util.Locale;
import javax.annotation.Nonnull;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public abstract class BlockMetaVariants<T extends Enum<T> & IStringSerializable> extends BlockMod {
   public static Class temporaryVariantsEnum;
   public static PropertyEnum temporaryVariantProp;
   public final Class<T> variantsEnum;
   public final PropertyEnum<T> variantProp;

   public BlockMetaVariants(String name, Material materialIn, Class<T> variantsEnum) {
      super(name, materialIn, asVariantArray(variantsEnum));
      this.variantsEnum = variantsEnum;
      this.variantProp = temporaryVariantProp;
      this.setDefaultState(this.blockState.getBaseState().withProperty(this.variantProp, ((Enum[])variantsEnum.getEnumConstants())[0]));
      this.setUnlocalizedName(name);
   }

   public boolean registerInConstruction() {
      return false;
   }

   @Nonnull
   public BlockStateContainer createBlockState() {
      return new BlockStateContainer(this, new IProperty[]{temporaryVariantProp});
   }

   public int getMetaFromState(IBlockState state) {
      return ((Enum)state.getValue(this.variantProp == null ? temporaryVariantProp : this.variantProp)).ordinal();
   }

   @Nonnull
   public IBlockState getStateFromMeta(int meta) {
      if (meta >= ((Enum[])this.variantsEnum.getEnumConstants()).length) {
         meta = 0;
      }

      return this.getDefaultState().withProperty(this.variantProp, ((Enum[])this.variantsEnum.getEnumConstants())[meta]);
   }

   public int damageDropped(IBlockState state) {
      return this.getMetaFromState(state);
   }

   @Nonnull
   public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world, @Nonnull BlockPos pos, EntityPlayer player) {
      return new ItemStack(this, 1, this.getMetaFromState(state));
   }

   public Class<T> getVariantEnum() {
      return this.variantsEnum;
   }

   public IProperty getVariantProp() {
      return this.variantProp;
   }

   public static String[] asVariantArray(Class e) {
      temporaryVariantsEnum = e;
      temporaryVariantProp = PropertyEnum.create("variant", e);
      Enum[] values = (Enum[])e.getEnumConstants();
      String[] variants = new String[values.length];

      for(int i = 0; i < values.length; ++i) {
         variants[i] = values[i].name().toLowerCase(Locale.ENGLISH);
      }

      return variants;
   }

   public interface EnumBase extends IStringSerializable {
      @Nonnull
      default String getName() {
         return ((Enum)this).name().toLowerCase(Locale.ENGLISH);
      }
   }
}
