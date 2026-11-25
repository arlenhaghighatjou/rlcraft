package vazkii.quark.misc.item;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.item.ItemMod;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.arl.util.ProxyRegistry;
import vazkii.quark.base.item.IQuarkItem;
import vazkii.quark.misc.feature.AncientTomes;

public class ItemAncientTome extends ItemMod implements IQuarkItem {
   public ItemAncientTome() {
      super("ancient_tome", new String[0]);
      this.setMaxStackSize(1);
   }

   public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      NBTTagList ench = ItemNBTHelper.getList(stack, "ench", 10, true);
      if (ench != null && !ItemNBTHelper.verifyExistence(stack, "StoredEnchantments")) {
         ItemNBTHelper.setList(stack, "StoredEnchantments", ench.copy());
         ItemNBTHelper.getNBT(stack).removeTag("ench");
      }

   }

   public boolean isEnchantable(@Nonnull ItemStack stack) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      return true;
   }

   @Nonnull
   public IRarity getForgeRarity(@Nonnull ItemStack stack) {
      return (IRarity)(ItemEnchantedBook.getEnchantments(stack).hasNoTags() ? super.getForgeRarity(stack) : EnumRarity.UNCOMMON);
   }

   public static ItemStack getEnchantedItemStack(EnchantmentData ench) {
      ItemStack newStack = ProxyRegistry.newStack(AncientTomes.ancient_tome);
      ItemEnchantedBook.addEnchantment(newStack, ench);
      return newStack;
   }

   public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> subItems) {
      if (tab.getRelevantEnchantmentTypes().length != 0 || tab == CreativeTabs.SEARCH) {
         Iterator var3 = Enchantment.REGISTRY.iterator();

         while(true) {
            Enchantment ench;
            while(true) {
               if (!var3.hasNext()) {
                  return;
               }

               ench = (Enchantment)var3.next();
               if (AncientTomes.validEnchants.contains(ench)) {
                  if (tab == CreativeTabs.SEARCH) {
                     if (ench.type != null) {
                        break;
                     }
                  } else if (tab.hasRelevantEnchantmentType(ench.type)) {
                     break;
                  }
               }
            }

            subItems.add(getEnchantedItemStack(new EnchantmentData(ench, ench.getMaxLevel())));
         }
      }
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      NBTTagList enchantments = ItemEnchantedBook.getEnchantments(stack);

      for(int i = 0; i < enchantments.tagCount(); ++i) {
         NBTTagCompound ench = enchantments.getCompoundTagAt(i);
         Enchantment enchantment = Enchantment.getEnchantmentByID(ench.getShort("id"));
         if (enchantment != null) {
            tooltip.add(enchantment.getTranslatedName(ench.getShort("lvl")));
         }
      }

   }
}
