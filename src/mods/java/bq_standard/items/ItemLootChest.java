package bq_standard.items;

import betterquesting.api.api.ApiReference;
import betterquesting.api.api.QuestingAPI;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.storage.IQuestSettings;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.utils.QuestTranslation;
import bq_standard.core.BQ_Standard;
import bq_standard.network.handlers.NetLootClaim;
import bq_standard.rewards.loot.LootGroup;
import bq_standard.rewards.loot.LootRegistry;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootContext.Builder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemLootChest extends Item {
   private List<ItemStack> subItems = null;

   public ItemLootChest() {
      this.setMaxStackSize(1);
      this.setUnlocalizedName("bq_standard.loot_chest");
      this.setCreativeTab((CreativeTabs)QuestingAPI.getAPI(ApiReference.CREATIVE_TAB));
   }

   @Nonnull
   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      if (hand != EnumHand.MAIN_HAND) {
         return new ActionResult(EnumActionResult.PASS, stack);
      } else {
         if (stack.getItemDamage() == 104) {
            if (world.isRemote || !(player instanceof EntityPlayerMP)) {
               if (!player.capabilities.isCreativeMode) {
                  stack.shrink(1);
               }

               return new ActionResult(EnumActionResult.PASS, stack);
            }

            NBTTagCompound tag = stack.getTagCompound();
            if (tag == null) {
               tag = new NBTTagCompound();
            }

            List<BigItemStack> lootItems = new ArrayList();
            String lootName = tag.getString("fixedLootName");
            NBTTagList lootList = tag.getTagList("fixedLootList", 10);

            for(int i = 0; i < lootList.tagCount(); ++i) {
               lootItems.add(new BigItemStack(lootList.getCompoundTagAt(i)));
            }

            boolean invoChanged = false;

            for(BigItemStack s1 : lootItems) {
               for(ItemStack s2 : s1.getCombinedStacks()) {
                  if (!player.inventory.addItemStackToInventory(s2)) {
                     player.dropItem(s2, true, false);
                  } else if (!invoChanged) {
                     invoChanged = true;
                  }
               }
            }

            if (invoChanged) {
               player.inventory.markDirty();
               player.inventoryContainer.detectAndSendChanges();
            }

            NetLootClaim.sendReward((EntityPlayerMP)player, lootName, (BigItemStack[])lootItems.toArray(new BigItemStack[0]));
         } else if (stack.getItemDamage() == 103) {
            if (world.isRemote || !(player instanceof EntityPlayerMP)) {
               if (!player.capabilities.isCreativeMode) {
                  stack.shrink(1);
               }

               return new ActionResult(EnumActionResult.PASS, stack);
            }

            LootContext lootcontext = (new Builder(((EntityPlayerMP)player).getServerWorld())).withLootedEntity(player).withPlayer(player).withLuck(player.getLuck()).build();
            String loottable = stack.getTagCompound() != null && stack.getTagCompound().hasKey("loottable", 8) ? stack.getTagCompound().getString("loottable") : "minecraft:chests/simple_dungeon";
            List<BigItemStack> loot = new ArrayList();

            for(ItemStack itemstack : player.world.getLootTableManager().getLootTableFromLocation(new ResourceLocation(loottable)).generateLootForPools(player.getRNG(), lootcontext)) {
               loot.add(new BigItemStack(itemstack));
            }

            boolean invoChanged = false;

            for(BigItemStack s1 : loot) {
               for(ItemStack s2 : s1.getCombinedStacks()) {
                  if (!player.inventory.addItemStackToInventory(s2)) {
                     player.dropItem(s2, true, false);
                  } else if (!invoChanged) {
                     invoChanged = true;
                  }
               }
            }

            if (invoChanged) {
               player.inventory.markDirty();
               player.inventoryContainer.detectAndSendChanges();
            }

            NetLootClaim.sendReward((EntityPlayerMP)player, "Loot", (BigItemStack[])loot.toArray(new BigItemStack[0]));
         } else {
            if (stack.getItemDamage() >= 102) {
               if (((IQuestSettings)QuestingAPI.getAPI(ApiReference.SETTINGS)).canUserEdit(player)) {
                  player.openGui(BQ_Standard.instance, 0, world, (int)player.posX, (int)player.posY, (int)player.posZ);
               }

               return new ActionResult(EnumActionResult.PASS, stack);
            }

            if (!world.isRemote) {
               float rarity = stack.getItemDamage() == 101 ? itemRand.nextFloat() : (float)MathHelper.clamp(stack.getItemDamage(), 0, 100) / 100.0F;
               LootGroup group = LootRegistry.INSTANCE.getWeightedGroup(rarity, itemRand);
               List<BigItemStack> loot = new ArrayList();
               String title = "No Loot Setup";
               if (group != null) {
                  title = group.name;
                  List<BigItemStack> tmp = group.getRandomReward(itemRand);
                  if (tmp != null) {
                     loot.addAll(tmp);
                  }
               }

               boolean invoChanged = false;

               for(BigItemStack s1 : loot) {
                  for(ItemStack s2 : s1.getCombinedStacks()) {
                     if (!player.inventory.addItemStackToInventory(s2)) {
                        player.dropItem(s2, true, false);
                     } else if (!invoChanged) {
                        invoChanged = true;
                     }
                  }
               }

               if (invoChanged) {
                  player.inventory.markDirty();
                  player.inventoryContainer.detectAndSendChanges();
               }

               if (player instanceof EntityPlayerMP) {
                  NetLootClaim.sendReward((EntityPlayerMP)player, title, (BigItemStack[])loot.toArray(new BigItemStack[0]));
               }
            }
         }

         if (!player.capabilities.isCreativeMode) {
            stack.shrink(1);
         }

         return new ActionResult(EnumActionResult.PASS, stack);
      }
   }

   @SideOnly(Side.CLIENT)
   public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
      if (tab == CreativeTabs.SEARCH || tab == this.getCreativeTab()) {
         if (this.subItems != null) {
            list.addAll(this.subItems);
         } else {
            this.subItems = new ArrayList();
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("hideLootInfo", true);

            for(int i = 0; i < 5; ++i) {
               ItemStack tmp = new ItemStack(this, 1, 25 * i);
               tmp.setTagCompound(tag.copy());
               this.subItems.add(tmp);
            }

            ItemStack tmp = new ItemStack(this, 1, 101);
            tmp.setTagCompound(tag.copy());
            this.subItems.add(tmp);
            this.subItems.add(new ItemStack(this, 1, 102));
            tag = new NBTTagCompound();
            tag.setBoolean("hideLootInfo", true);
            tag.setString("loottable", "minecraft:chests/simple_dungeon");
            ItemStack lootStack = new ItemStack(this, 1, 103);
            lootStack.setTagCompound(tag);
            this.subItems.add(lootStack);
            tag = new NBTTagCompound();
            tag.setBoolean("hideLootInfo", true);
            NBTTagList tagList = new NBTTagList();
            tagList.appendTag((new BigItemStack(Blocks.STONE)).writeToNBT(new NBTTagCompound()));
            ItemStack fixedLootStack = new ItemStack(this, 1, 104);
            tag.setTag("fixedLootList", tagList);
            tag.setString("fixedLootName", "Item Set");
            fixedLootStack.setTagCompound(tag);
            this.subItems.add(fixedLootStack);
            list.addAll(this.subItems);
         }
      }
   }

   @SideOnly(Side.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      return stack.getItemDamage() == 102;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      NBTTagCompound tag = stack.getTagCompound();
      boolean hideTooltip = tag == null || !tag.getBoolean("hideLootInfo");
      if (!hideTooltip || ((Boolean)((IQuestSettings)QuestingAPI.getAPI(ApiReference.SETTINGS)).getProperty(NativeProps.EDIT_MODE)).booleanValue()) {
         if (stack.getItemDamage() == 104) {
            if (tag == null) {
               return;
            }

            tooltip.add(QuestTranslation.translate("bq_standard.tooltip.fixed_loot", new Object[]{tag.getString("fixedLootName")}));
            tooltip.add(QuestTranslation.translate("bq_standard.tooltip.fixed_loot_size", new Object[]{tag.getTagList("fixedLootList", 10).tagCount()}));
         } else if (stack.getItemDamage() == 103) {
            if (tag == null) {
               return;
            }

            tooltip.add(QuestTranslation.translate("bq_standard.tooltip.loot_table", new Object[]{tag.getString("loottable")}));
         } else if (stack.getItemDamage() > 101) {
            tooltip.add(QuestTranslation.translate("betterquesting.btn.edit", new Object[0]));
         } else if (stack.getItemDamage() == 101) {
            tooltip.add(QuestTranslation.translate("bq_standard.tooltip.loot_chest", new Object[]{"???"}));
         } else {
            tooltip.add(QuestTranslation.translate("bq_standard.tooltip.loot_chest", new Object[]{MathHelper.clamp(stack.getItemDamage(), 0, 100) + "%"}));
         }

      }
   }
}
