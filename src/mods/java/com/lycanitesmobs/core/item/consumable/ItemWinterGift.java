package com.lycanitesmobs.core.item.consumable;

import net.minecraft.entity.EntityList;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.EntityItemCustom;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.ItemBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemWinterGift extends ItemBase {
   public ItemWinterGift() {
      this.modInfo = LycanitesMobs.modInfo;
      this.itemName = "wintergift";
      this.setup();
      AssetManager.addSound(this.itemName + "_good", this.modInfo, "item." + this.itemName + ".good");
      AssetManager.addSound(this.itemName + "_bad", this.modInfo, "item." + this.itemName + ".bad");
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemStack = player.getHeldItem(hand);
      if (!player.capabilities.isCreativeMode) {
         itemStack.setCount(Math.max(0, itemStack.getCount() - 1));
      }

      if (!world.isRemote) {
         if (player.getRNG().nextBoolean()) {
            this.openGood(itemStack, world, player);
         } else {
            this.openBad(itemStack, world, player);
         }
      }

      return new ActionResult(EnumActionResult.SUCCESS, itemStack);
   }

   public void openGood(ItemStack itemStack, World world, EntityPlayer player) {
      String message = LanguageManager.translate("item." + this.itemName + ".good");
      player.sendMessage(new TextComponentString(message));
      this.playSound(world, player.getPosition(), AssetManager.getSound(this.itemName + "_good"), SoundCategory.AMBIENT, 5.0F, 1.0F);
      ItemStack[] dropStacks = ObjectLists.getItems("winter_gifts");
      if (dropStacks.length > 0) {
         ItemStack dropStack = dropStacks[player.getRNG().nextInt(dropStacks.length)];
         if (dropStack != null) {
            dropStack.setCount(1 + player.getRNG().nextInt(4));
            EntityItemCustom entityItem = new EntityItemCustom(world, player.posX, player.posY, player.posZ, dropStack);
            entityItem.setPickupDelay(10);
            world.spawnEntity(entityItem);
         }

      }
   }

   public void openBad(ItemStack itemStack, World world, EntityPlayer player) {
      String message = LanguageManager.translate("item." + this.itemName + ".bad");
      player.sendMessage(new TextComponentString(message));
      this.playSound(world, player.getPosition(), AssetManager.getSound(this.itemName + "_bad"), SoundCategory.AMBIENT, 5.0F, 1.0F);
      Class[] entityClasses = ObjectLists.getEntites("winter_tricks");
      if (entityClasses.length > 0) {
         Class entityClass = entityClasses[player.getRNG().nextInt(entityClasses.length)];
         if (entityClass != null) {
            Entity entity = null;

            try {
               entity = EntityList.newEntity(entityClass, world);
            } catch (Exception var9) {
               var9.printStackTrace();
            }

            if (entity != null) {
               entity.setLocationAndAngles(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
               if (entity instanceof BaseCreatureEntity) {
                  BaseCreatureEntity entityCreature = (BaseCreatureEntity)entity;
                  entityCreature.addLevel(world.rand.nextInt(10));
                  if (entityCreature.creatureInfo.getName().equals("wildkin")) {
                     entityCreature.setCustomNameTag("Gooderness");
                  } else if (entityCreature.creatureInfo.getName().equals("jabberwock")) {
                     entityCreature.setCustomNameTag("Rudolph");
                  } else if (entityCreature.creatureInfo.getName().equals("ent")) {
                     entityCreature.setCustomNameTag("Salty Tree");
                  } else if (entityCreature.creatureInfo.getName().equals("treant")) {
                     entityCreature.setCustomNameTag("Salty Tree");
                  } else if (entityCreature.creatureInfo.getName().equals("reaper")) {
                     entityCreature.setCustomNameTag("Satan Claws");
                  } else if (entityCreature.creatureInfo.getName().equals("behemoth")) {
                     entityCreature.setCustomNameTag("Krampus");
                  }
               }

               world.spawnEntity(entity);
            }
         }

      }
   }

   public static void createObjectLists() {
      ObjectLists.addItem("winter_gifts", Items.DIAMOND);
      ObjectLists.addItem("winter_gifts", Items.GOLD_INGOT);
      ObjectLists.addItem("winter_gifts", Items.EMERALD);
      ObjectLists.addItem("winter_gifts", Blocks.IRON_BLOCK);
      ObjectLists.addItem("winter_gifts", Items.ENDER_PEARL);
      ObjectLists.addItem("winter_gifts", Items.BLAZE_ROD);
      ObjectLists.addItem("winter_gifts", Items.GLOWSTONE_DUST);
      ObjectLists.addItem("winter_gifts", Items.COAL);
      ObjectLists.addItem("winter_gifts", ObjectManager.getItem("mosspie"));
      ObjectLists.addItem("winter_gifts", ObjectManager.getItem("ambercake"));
      ObjectLists.addItem("winter_gifts", ObjectManager.getItem("peakskebab"));
      ObjectLists.addItem("winter_gifts", ObjectManager.getItem("bulwarkburger"));
      ObjectLists.addItem("winter_gifts", ObjectManager.getItem("palesoup"));
      ObjectLists.addFromConfig("winter_gifts");
      ObjectLists.addEntity("winter_tricks", "wildkin");
      ObjectLists.addEntity("winter_tricks", "jabberwock");
      ObjectLists.addEntity("winter_tricks", "ent");
      ObjectLists.addEntity("winter_tricks", "treant");
      ObjectLists.addEntity("winter_tricks", "reaper");
      ObjectLists.addEntity("winter_tricks", "behemoth");
   }
}
