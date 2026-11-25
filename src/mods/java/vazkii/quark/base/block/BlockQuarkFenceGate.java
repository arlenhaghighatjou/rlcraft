package vazkii.quark.base.block;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.SoundType;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.properties.IProperty;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public class BlockQuarkFenceGate extends BlockFenceGate implements IQuarkBlock {
   private final String[] variants;
   private final String bareName;

   public BlockQuarkFenceGate(String name) {
      super(EnumType.DARK_OAK);
      this.setHardness(3.0F);
      this.setSoundType(SoundType.WOOD);
      this.variants = new String[]{name};
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

   public ItemMeshDefinition getCustomMeshDefinition() {
      return null;
   }

   public EnumRarity getBlockRarity(ItemStack stack) {
      return EnumRarity.COMMON;
   }

   public IProperty[] getIgnoredProperties() {
      return new IProperty[]{POWERED};
   }

   public IProperty getVariantProp() {
      return null;
   }

   public Class getVariantEnum() {
      return null;
   }
}
