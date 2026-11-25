package com.teammetallurgy.aquaculture.items;

import com.teammetallurgy.aquaculture.items.meta.MetaItem;
import com.teammetallurgy.aquaculture.items.meta.SubItem;
import com.teammetallurgy.aquaculture.loot.WeightedLootSet;
import javax.annotation.Nonnull;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBox extends SubItem {
   public WeightedLootSet loot = new WeightedLootSet();

   public ItemBox(MetaItem metaItem) {
      super(metaItem);
      this.loot.addLoot(Blocks.STONE, 5, 1, 1);
      this.loot.addLoot(Blocks.DIRT, 5, 1, 1);
      this.loot.addLoot(Blocks.COBBLESTONE, 5);
      this.loot.addLoot(new ItemStack(Blocks.PLANKS, 1, 0), 1);
      this.loot.addLoot(new ItemStack(Blocks.PLANKS, 1, 1), 1);
      this.loot.addLoot(new ItemStack(Blocks.PLANKS, 1, 2), 1);
      this.loot.addLoot(new ItemStack(Blocks.PLANKS, 1, 3), 1);
      this.loot.addLoot(new ItemStack(Blocks.SAPLING, 1, 0), 1);
      this.loot.addLoot(new ItemStack(Blocks.SAPLING, 1, 1), 1);
      this.loot.addLoot(new ItemStack(Blocks.SAPLING, 1, 2), 1);
      this.loot.addLoot(new ItemStack(Blocks.SAPLING, 1, 3), 1);
      this.loot.addLoot(new ItemStack(Blocks.LOG, 1, 0), 1);
      this.loot.addLoot(new ItemStack(Blocks.LOG, 1, 1), 1);
      this.loot.addLoot(new ItemStack(Blocks.LOG, 1, 2), 1);
      this.loot.addLoot(new ItemStack(Blocks.LOG, 1, 3), 1);
      this.loot.addLoot(Blocks.GRAVEL, 5);
      this.loot.addLoot(new ItemStack(Items.COAL, 1, 0), 3);
      this.loot.addLoot(new ItemStack(Items.COAL, 1, 1), 3);
      this.loot.addLoot(Items.WHEAT_SEEDS, 3);
      this.loot.addLoot(Items.STICK, 5);
      this.loot.addLoot(Items.BOWL, 3);
      this.loot.addLoot(Items.LEATHER_HELMET, 2);
      this.loot.addLoot(Items.LEATHER_CHESTPLATE, 2);
      this.loot.addLoot(Items.LEATHER_LEGGINGS, 2);
      this.loot.addLoot(Items.LEATHER_BOOTS, 2);
      this.loot.addLoot(Items.FLINT, 4);
      this.loot.addLoot(Items.CLAY_BALL, 4);
      this.loot.addLoot(Items.BUCKET, 1);
      this.loot.addLoot(Items.LEATHER, 4);
      this.loot.addLoot(Items.SLIME_BALL, 1);
      this.loot.addLoot(Blocks.REEDS, 1);
      this.loot.addLoot(Items.BONE, 5);
      this.loot.addLoot(Items.ROTTEN_FLESH, 5);
      this.loot.addLoot(Items.GLASS_BOTTLE, 1);
      this.loot.addLoot(Items.CARROT, 1);
      this.loot.addLoot(Items.POTATO, 1);
      this.loot.addLoot(Blocks.VINE, 1);
      this.loot.addLoot(Blocks.TALLGRASS, 3);
      this.loot.addLoot(Items.STRING, 3);
      this.loot.addLoot(Items.FEATHER, 4);
      this.loot.addLoot(Items.APPLE, 1);
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
