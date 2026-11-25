package vazkii.quark.base.item;

import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import vazkii.arl.util.ProxyRegistry;

public class ItemQuarkFood extends ItemFood implements IQuarkItem {
   private final String bareName;
   private final String[] variants;

   public ItemQuarkFood(String name, int amount, float saturation, boolean isWolfFood, String... variants) {
      super(amount, saturation, isWolfFood);
      this.setUnlocalizedName(name);
      this.bareName = name;
      if (variants == null || variants.length == 0) {
         variants = new String[]{this.bareName};
      }

      if (variants.length > 1) {
         this.setHasSubtypes(true);
      }

      this.variants = variants;
      this.setCreativeTab(CreativeTabs.FOOD);
   }

   public ItemQuarkFood(String name, int amount, float saturation, String... variants) {
      this(name, amount, saturation, false, variants);
   }

   @Nonnull
   public Item setUnlocalizedName(@Nonnull String name) {
      super.setUnlocalizedName(name);
      this.setRegistryName(new ResourceLocation(this.getPrefix() + name));
      ProxyRegistry.register(this);
      return this;
   }

   @Nonnull
   public String getUnlocalizedName(ItemStack par1ItemStack) {
      int dmg = par1ItemStack.getItemDamage();
      String[] variants = this.getVariants();
      String name;
      if (dmg >= variants.length) {
         name = this.bareName;
      } else {
         name = variants[dmg];
      }

      return "item." + this.getPrefix() + name;
   }

   public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
      if (this.isInCreativeTab(tab)) {
         for(int i = 0; i < this.getVariants().length; ++i) {
            subItems.add(new ItemStack(this, 1, i));
         }
      }

   }

   public String[] getVariants() {
      return this.variants;
   }
}
