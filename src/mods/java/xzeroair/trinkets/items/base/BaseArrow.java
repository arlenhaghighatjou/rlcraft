package xzeroair.trinkets.items.base;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.util.interfaces.IsModelLoaded;

public class BaseArrow extends ItemArrow implements IsModelLoaded {
   public BaseArrow(String name) {
      this.setUnlocalizedName(name);
      this.setRegistryName(name);
      this.setCreativeTab(Trinkets.trinketstab);
      this.setHasSubtypes(true);
      this.setMaxDamage(0);
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
      if (this.isInCreativeTab(tab)) {
         for(BaseArrow.ArrowType arrowType : BaseArrow.ArrowType.values()) {
            subItems.add(new ItemStack(this, 1, arrowType.ordinal()));
         }
      }

   }

   public boolean hasEffect(ItemStack stack) {
      BaseArrow.ArrowType arrowtype = BaseArrow.ArrowType.fromMeta(stack.getMetadata());
      switch(arrowtype) {
      default:
         return super.hasEffect(stack);
      }
   }

   public int getMetadata(int metadata) {
      return metadata;
   }

   public String getUnlocalizedName(ItemStack stack) {
      return "item." + BaseArrow.ArrowType.fromMeta(stack.getMetadata()).toString();
   }

   public boolean isInfinite(ItemStack stack, ItemStack bow, EntityPlayer player) {
      int enchant = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bow);
      return enchant <= 0 ? false : this.getClass() == BaseArrow.class;
   }

   public void registerModels() {
      Trinkets.proxy.registerItemRenderer(this, 0, "inventory");
   }

   public static enum ArrowType implements IStringSerializable {
      RIBBON,
      GLITTER;

      private ArrowType() {
      }

      public String getName() {
         return this.name().toLowerCase() + "_arrow";
      }

      public String toString() {
         return this.getName();
      }

      public double getDamageInflicted() {
         switch(this) {
         case RIBBON:
            return 4.0D;
         case GLITTER:
            return 3.0D;
         default:
            return 2.0D;
         }
      }

      public static BaseArrow.ArrowType fromMeta(int meta) {
         return values()[meta % values().length];
      }
   }
}
