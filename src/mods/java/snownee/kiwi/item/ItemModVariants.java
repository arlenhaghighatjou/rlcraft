package snownee.kiwi.item;

import java.util.function.Consumer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.Validate;
import snownee.kiwi.client.ModelUtil;
import snownee.kiwi.util.VariantsHolder;

public class ItemModVariants extends ItemMod {
   private final VariantsHolder<IStringSerializable> variants;

   public ItemModVariants(String name, VariantsHolder<IStringSerializable> variants) {
      super(name);
      this.variants = variants;
      this.setHasSubtypes(this.variants.size() > 1);
   }

   @SideOnly(Side.CLIENT)
   public void mapModel() {
      ModelUtil.mapItemVariantsModel(this, this.getName() + "_", this.variants, "");
   }

   public VariantsHolder getVariants() {
      return this.variants;
   }

   public ItemStack getItemStack(VariantsHolder.Variant variant) {
      return this.getItemStack(variant, 1);
   }

   public ItemStack getItemStack(VariantsHolder.Variant variant, int amount) {
      int meta = this.variants.indexOf(variant);
      Validate.inclusiveBetween(0L, (long)this.variants.size(), (long)meta);
      return new ItemStack(this, amount, meta);
   }

   public String getUnlocalizedName(ItemStack stack) {
      return stack.getMetadata() < this.variants.size() ? super.getUnlocalizedName(stack) + "." + this.variants.get(stack.getMetadata()).getValue().getName() : super.getUnlocalizedName(stack);
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         this.variants.forEach((v) -> {
            items.add(this.getItemStack(v));
         });
      }

   }
}
