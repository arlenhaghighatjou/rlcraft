package vazkii.quark.world.block;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockQuartz.EnumType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.block.IQuarkBlock;

public class BlockBiotite extends BlockQuartz implements IQuarkBlock {
   private final String[] variants;
   private final String bareName;

   public BlockBiotite() {
      this.setSoundType(SoundType.STONE);
      this.setHardness(0.8F);
      String name = "biotite_block";
      this.variants = new String[]{name, "chiseled_biotite_block", "pillar_biotite_block"};
      this.bareName = name;
      this.setUnlocalizedName(name);
   }

   @Nonnull
   public Block setUnlocalizedName(@Nonnull String name) {
      super.setUnlocalizedName(name);
      this.register(name);
      return this;
   }

   public String getBareName() {
      return this.bareName;
   }

   public String[] getVariants() {
      return this.variants;
   }

   @SideOnly(Side.CLIENT)
   public ItemMeshDefinition getCustomMeshDefinition() {
      return null;
   }

   public EnumRarity getBlockRarity(ItemStack stack) {
      return EnumRarity.COMMON;
   }

   public IProperty[] getIgnoredProperties() {
      return new IProperty[0];
   }

   public IProperty getVariantProp() {
      return null;
   }

   public Class getVariantEnum() {
      return null;
   }

   @SideOnly(Side.CLIENT)
   public IStateMapper getStateMapper() {
      return new StateMapperBase() {
         @Nonnull
         public ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
            EnumType type = (EnumType)state.getValue(BlockQuartz.VARIANT);
            ResourceLocation baseLocation = new ResourceLocation("quark".toLowerCase(), "biotite_block");
            switch(type) {
            case CHISELED:
               return new ModelResourceLocation(baseLocation, "chiseled");
            case LINES_Y:
               return new ModelResourceLocation(baseLocation, "axis=y");
            case LINES_X:
               return new ModelResourceLocation(baseLocation, "axis=x");
            case LINES_Z:
               return new ModelResourceLocation(baseLocation, "axis=z");
            default:
               return new ModelResourceLocation(baseLocation, "normal");
            }
         }
      };
   }
}
