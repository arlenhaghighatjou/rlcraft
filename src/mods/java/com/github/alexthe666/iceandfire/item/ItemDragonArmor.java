package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDragonArmor extends Item implements ICustomRendered {
   public int type;
   public String name;

   public ItemDragonArmor(int type, String name) {
      this.type = type;
      this.name = name;
      this.setHasSubtypes(true);
      this.setUnlocalizedName("iceandfire." + name);
      this.setCreativeTab(IceAndFire.TAB);
      this.maxStackSize = 1;
      this.setRegistryName("iceandfire", name);
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (tab == this.getCreativeTab()) {
         items.add(new ItemStack(this, 1, 0));
         items.add(new ItemStack(this, 1, 1));
         items.add(new ItemStack(this, 1, 2));
         items.add(new ItemStack(this, 1, 3));
      }

   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      String words;
      switch(stack.getMetadata()) {
      case 1:
         words = "dragon.armor_neck";
         break;
      case 2:
         words = "dragon.armor_body";
         break;
      case 3:
         words = "dragon.armor_tail";
         break;
      default:
         words = "dragon.armor_head";
      }

      tooltip.add(StatCollector.translateToLocal(words));
   }
}
