package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import com.github.alexthe666.iceandfire.enums.EnumDragonEgg;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDragonScales extends Item {
   EnumDragonEgg type;

   public ItemDragonScales(String name, EnumDragonEgg type) {
      this.setHasSubtypes(true);
      this.setCreativeTab(IceAndFire.TAB);
      this.type = type;
      this.setUnlocalizedName("iceandfire.dragonscales");
      this.setRegistryName("iceandfire", name);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced) {
      tooltip.add(this.type.color + StatCollector.translateToLocal("dragon." + this.type.toString().toLowerCase()));
   }
}
