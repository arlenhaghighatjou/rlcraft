package noppes.vc.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import noppes.vc.VCItems;
import noppes.vc.VariedCommodities;

public class ItemStaffElemental extends ItemStaff {
   public ItemStaffElemental(VCToolMaterial material) {
      super(material);
      this.setHasSubtypes(true);
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> par3List) {
      if (this.isInCreativeTab(tab)) {
         for(int var4 = 0; var4 < 16; ++var4) {
            par3List.add(new ItemStack(this, 1, var4));
         }
      }

   }

   public Item register(String name) {
      this.setUnlocalizedName(name);
      this.setRegistryName("variedcommodities", name);
      return this;
   }

   public ItemStack getProjectile(ItemStack stack) {
      return new ItemStack(VCItems.orb, 1, stack.getItemDamage());
   }

   public void spawnParticle(ItemStack stack, EntityLivingBase player) {
      VariedCommodities.proxy.spawnParticle(player, "Spell", stack.getItemDamage(), Integer.valueOf(4));
   }
}
