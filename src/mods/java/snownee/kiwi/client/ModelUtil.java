package snownee.kiwi.client;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.kiwi.Kiwi;
import snownee.kiwi.item.IVariant;
import snownee.kiwi.util.VariantsHolder;

@SideOnly(Side.CLIENT)
public final class ModelUtil {
   private ModelUtil() {
      throw new UnsupportedOperationException();
   }

   public static void mapItemModel(Item item) {
      ResourceLocation registryName;
      if (item != null && (registryName = item.getRegistryName()) != null) {
         ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(registryName, "inventory"));
      } else {
         Kiwi.logger.warn("Detecting unnamed item {}, skip its model setup", item);
      }
   }

   public static void mapItemVariantsModel(Item item, String prefix, List<? extends VariantsHolder.Variant<? extends IStringSerializable>> variants, String suffix) {
      String modID = ((ResourceLocation)Objects.requireNonNull(item.getRegistryName())).getResourceDomain();
      variants.forEach((variant) -> {
         ModelLoader.setCustomModelResourceLocation(item, variant.getMeta(), new ModelResourceLocation(modID + ":" + prefix + variant.getValue().getName() + suffix, "inventory"));
      });
   }

   public static void mapItemVariantsModelNew(Item item, String prefix, IVariant<?>[] variants, String suffix) {
      String modID = ((ResourceLocation)Objects.requireNonNull(item.getRegistryName())).getResourceDomain();
      Arrays.asList(variants).forEach((variant) -> {
         ModelLoader.setCustomModelResourceLocation(item, variant.getMeta(), new ModelResourceLocation(modID + ":" + prefix + variant.getName() + suffix, "inventory"));
      });
   }

   public static void mapFluidModel(BlockFluidBase fluidBlock) {
      Fluid fluid = fluidBlock.getFluid();
      String modID = ((ResourceLocation)Objects.requireNonNull(fluidBlock.getRegistryName())).getResourceDomain();
      ModelUtil.FluidCustomModelMapper mapper = new ModelUtil.FluidCustomModelMapper(modID, fluid);
      ModelLoader.setCustomStateMapper(fluidBlock, mapper);
   }

   public static class FluidCustomModelMapper extends StateMapperBase {
      private final ModelResourceLocation res;

      FluidCustomModelMapper(String modid, Fluid f) {
         this.res = new ModelResourceLocation(new ResourceLocation(modid, "fluid"), f.getName());
      }

      public ModelResourceLocation getModelResourceLocation(IBlockState state) {
         return this.res;
      }
   }
}
