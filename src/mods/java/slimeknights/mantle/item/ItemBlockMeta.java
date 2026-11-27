package slimeknights.mantle.item;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemColored;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.util.LocUtils;

public class ItemBlockMeta extends ItemColored {
   protected IProperty mappingProperty;

   public ItemBlockMeta(Block block) {
      super(block, true);
   }

   @Nonnull
   public String getUnlocalizedName(@Nonnull ItemStack stack) {
      if (this.mappingProperty == null) {
         return super.getUnlocalizedName(stack);
      } else {
         IBlockState state = this.block.getStateFromMeta(stack.getMetadata());
         String name = state.getValue(this.mappingProperty).toString().toLowerCase(Locale.US);
         return super.getUnlocalizedName(stack) + "." + name;
      }
   }

   public static void setMappingProperty(Block block, IProperty<?> property) {
      ((ItemBlockMeta)Item.getItemFromBlock(block)).mappingProperty = property;
   }

   public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, ITooltipFlag flagIn) {
      if (I18n.canTranslate(this.getUnlocalizedName(stack) + ".tooltip")) {
         tooltip.addAll(LocUtils.getTooltips(TextFormatting.GRAY.toString() + LocUtils.translateRecursive(this.getUnlocalizedName(stack) + ".tooltip")));
      } else if (I18n.canTranslate(super.getUnlocalizedName(stack) + ".tooltip")) {
         tooltip.addAll(LocUtils.getTooltips(TextFormatting.GRAY.toString() + LocUtils.translateRecursive(super.getUnlocalizedName(stack) + ".tooltip")));
      }

      super.addInformation(stack, worldIn, tooltip, flagIn);
   }

   @SideOnly(Side.CLIENT)
   public void registerItemModels() {
      Item item = this;
      ResourceLocation loc = ForgeRegistries.BLOCKS.getKey(this.block);

      for(Comparable o : (Collection<Comparable>)this.mappingProperty.getAllowedValues()) {
         int meta = this.block.getMetaFromState(this.block.getDefaultState().withProperty(this.mappingProperty, o));
         String name = this.mappingProperty.getName(o);
         ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(loc, this.mappingProperty.getName() + "=" + name));
      }

   }
}
