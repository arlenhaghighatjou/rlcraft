package com.teammetallurgy.aquaculture.items;

import com.teammetallurgy.aquaculture.handlers.Config;
import com.teammetallurgy.aquaculture.items.meta.MetaItem;
import com.teammetallurgy.aquaculture.items.meta.SubItem;
import com.teammetallurgy.aquaculture.loot.WeightedLootSet;
import javax.annotation.Nonnull;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemNeptunesBounty extends SubItem {
   private WeightedLootSet loot = null;

   public ItemNeptunesBounty(MetaItem metaItem) {
      super(metaItem);
   }

   public void initLoot() {
      this.loot = new WeightedLootSet();
      if (Config.enableNeptuniumTools) {
         this.loot.addLoot(AquacultureItems.neptuniumAxe, 1, 1, 1);
         this.loot.addLoot(AquacultureItems.neptuniumPickaxe, 1, 1, 1);
         this.loot.addLoot(AquacultureItems.neptuniumShovel, 1, 1, 1);
         this.loot.addLoot(AquacultureItems.neptuniumHoe, 1, 1, 1);
         this.loot.addLoot(AquacultureItems.neptuniumSword, 1, 1, 1);
      }

      if (Config.enableNeptuniumArmor) {
         this.loot.addLoot(AquacultureItems.neptuniumHelmet, 1, 1, 1);
         this.loot.addLoot(AquacultureItems.neptuniumPlate, 1, 1, 1);
         this.loot.addLoot(AquacultureItems.neptuniumLegs, 1, 1, 1);
         this.loot.addLoot(AquacultureItems.neptuniumBoots, 1, 1, 1);
      }

      if (Config.enableNeptuniumLoot) {
         this.loot.addLoot(AquacultureItems.neptuniumBar, 2, 1, 4);
      }

   }

   @Nonnull
   public ItemStack onItemRightClick(@Nonnull ItemStack stack, World world, EntityPlayer player) {
      if (world.isRemote) {
         return stack;
      } else {
         if (this.loot == null) {
            this.initLoot();
         }

         ItemStack item = this.loot.getRandomLoot();
         EntityItem entityitem = new EntityItem(player.world, player.posX, player.posY, player.posZ, item);
         world.spawnEntity(entityitem);
         stack.shrink(1);
         return stack;
      }
   }
}
