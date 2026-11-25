package com.teammetallurgy.aquaculture.items;

import com.teammetallurgy.aquaculture.items.meta.MetaItem;
import com.teammetallurgy.aquaculture.items.meta.SubItem;
import com.teammetallurgy.aquaculture.loot.WeightedLootSet;
import javax.annotation.Nonnull;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemTreasureChest extends SubItem {
   private WeightedLootSet loot = new WeightedLootSet();

   public ItemTreasureChest(MetaItem par1) {
      super(par1);
      this.loot.addLoot(Items.IRON_INGOT, 40);
      this.loot.addLoot(Items.GOLD_INGOT, 30);
      this.loot.addLoot(Items.DIAMOND, 15);
      this.loot.addLoot(Items.EMERALD, 15);
   }

   @Nonnull
   public ItemStack onItemRightClick(@Nonnull ItemStack stack, World world, EntityPlayer player) {
      if (world.isRemote) {
         return stack;
      } else {
         ItemStack randomLoot = this.loot.getRandomLoot();
         EntityItem entityitem = new EntityItem(player.world, player.posX, player.posY, player.posZ, randomLoot);
         world.spawnEntity(entityitem);
         stack.shrink(1);
         return stack;
      }
   }
}
