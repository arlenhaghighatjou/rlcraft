package net.theawesomegem.fishingmadebetter.common.item.fishingrod;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentDurability;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.theawesomegem.fishingmadebetter.common.configuration.CustomConfigurationHandler;
import net.theawesomegem.fishingmadebetter.common.data.FishData;
import net.theawesomegem.fishingmadebetter.common.entity.EntityFMBCustomFishHook;
import net.theawesomegem.fishingmadebetter.common.entity.EntityFMBLavaFishHook;
import net.theawesomegem.fishingmadebetter.common.entity.EntityFMBVoidFishHook;
import net.theawesomegem.fishingmadebetter.common.item.ItemManager;
import net.theawesomegem.fishingmadebetter.common.item.attachment.bobber.ItemBobber;
import net.theawesomegem.fishingmadebetter.common.item.attachment.hook.ItemHook;
import net.theawesomegem.fishingmadebetter.common.item.attachment.reel.ItemReel;
import net.theawesomegem.fishingmadebetter.common.registry.FMBCreativeTab;

public abstract class ItemBetterFishingRod extends ItemFishingRod {
   protected ToolMaterial toolMaterial;

   public ItemBetterFishingRod(String name, ToolMaterial materialIn) {
      this.toolMaterial = materialIn;
      this.setCreativeTab(FMBCreativeTab.instance);
      this.setMaxDamage(materialIn.getMaxUses());
      this.setRegistryName(name);
      this.setUnlocalizedName("fishingmadebetter." + name);
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      ItemStack mat = this.toolMaterial.getRepairItemStack();
      return !mat.isEmpty() && OreDictionary.itemMatches(mat, repair, false) ? true : super.getIsRepairable(toRepair, repair);
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      final ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (playerIn.fishEntity != null) {
         int dmg = playerIn.fishEntity.handleHookRetraction();
         if (!playerIn.capabilities.isCreativeMode && itemstack.isItemStackDamageable()) {
            Random rand = playerIn.getRNG();
            if (dmg > 0) {
               int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, itemstack);
               int j = 0;

               for(int k = 0; i > 0 && k < dmg; ++k) {
                  if (EnchantmentDurability.negateDamage(itemstack, i, rand)) {
                     ++j;
                  }
               }

               dmg = dmg - j;
               if (dmg > 0) {
                  boolean reelAlive = false;
                  boolean bobberAlive = false;
                  boolean hookAlive = false;
                  if (hasReelItem(itemstack)) {
                     setReelDamage(itemstack, getReelDamage(itemstack) + dmg);
                     if (getReelDamage(itemstack) > getReelItem(itemstack).getMaxDamage()) {
                        removeReelItem(itemstack);
                        playerIn.renderBrokenItemStack(itemstack);
                     } else {
                        reelAlive = true;
                     }
                  }

                  if (hasBobberItem(itemstack)) {
                     setBobberDamage(itemstack, getBobberDamage(itemstack) + dmg);
                     if (getBobberDamage(itemstack) > getBobberItem(itemstack).getMaxDamage()) {
                        removeBobberItem(itemstack);
                        playerIn.renderBrokenItemStack(itemstack);
                     } else {
                        bobberAlive = true;
                     }
                  }

                  if (hasHookItem(itemstack)) {
                     setHookDamage(itemstack, getHookDamage(itemstack) + dmg);
                     if (getHookDamage(itemstack) > getHookItem(itemstack).getMaxDamage()) {
                        removeHookItem(itemstack);
                        playerIn.renderBrokenItemStack(itemstack);
                     } else {
                        hookAlive = true;
                     }
                  }

                  itemstack.setItemDamage(itemstack.getItemDamage() + dmg);
                  if (itemstack.getItemDamage() > itemstack.getMaxDamage()) {
                     playerIn.renderBrokenItemStack(itemstack);
                     if (reelAlive) {
                        ItemStack droppedReel = new ItemStack(getReelItem(itemstack));
                        droppedReel.setItemDamage(getReelDamage(itemstack));
                        playerIn.entityDropItem(droppedReel, 1.0F);
                     }

                     if (bobberAlive) {
                        ItemStack droppedBobber = new ItemStack(getBobberItem(itemstack));
                        droppedBobber.setItemDamage(getBobberDamage(itemstack));
                        playerIn.entityDropItem(droppedBobber, 1.0F);
                     }

                     if (hookAlive) {
                        ItemStack droppedHook = new ItemStack(getHookItem(itemstack));
                        droppedHook.setItemDamage(getHookDamage(itemstack));
                        playerIn.entityDropItem(droppedHook, 1.0F);
                     }

                     itemstack.shrink(1);
                     itemstack.setItemDamage(0);
                  }
               }
            }
         }

         playerIn.swingArm(handIn);
         worldIn.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
      } else {
         if (playerIn.getHeldItem(EnumHand.MAIN_HAND) != null && playerIn.getHeldItem(EnumHand.OFF_HAND) != null && playerIn.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemFishingRod && playerIn.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemFishingRod) {
            return new ActionResult(EnumActionResult.FAIL, itemstack);
         }

         worldIn.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
         if (!worldIn.isRemote) {
            if (getBobberItem(itemstack).isLavaBobber()) {
               EntityFMBCustomFishHook entityFishHook = new EntityFMBLavaFishHook(worldIn, playerIn);
               worldIn.spawnEntity(entityFishHook);
            } else if (getBobberItem(itemstack).isVoidBobber()) {
               EntityFMBCustomFishHook entityFishHook = new EntityFMBVoidFishHook(worldIn, playerIn);
               worldIn.spawnEntity(entityFishHook);
            } else {
               EntityFishHook entityFishHook = new EntityFishHook(worldIn, playerIn) {
                  ItemStack parentRod;

                  protected void entityInit() {
                     super.entityInit();
                     this.parentRod = itemstack;
                  }

                  public void onUpdate() {
                     super.onUpdate();
                     if (this.getAngler() != null && this.getAngler().fishEntity != null && (this.parentRod == null || this.getAngler().getHeldItemMainhand() == null || this.getAngler().getHeldItemOffhand() == null || this.getAngler().getHeldItemMainhand().getItem() instanceof ItemFishingRod == (this.getAngler().getHeldItemOffhand().getItem() instanceof ItemFishingRod) || this.getAngler().getHeldItemMainhand() == this.parentRod == (this.getAngler().getHeldItemOffhand() == this.parentRod))) {
                        super.setDead();
                     }

                  }
               };
               worldIn.spawnEntity(entityFishHook);
            }
         }

         playerIn.swingArm(handIn);
         playerIn.addStat(StatList.getObjectUseStats(this));
      }

      return new ActionResult(EnumActionResult.SUCCESS, itemstack);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      ItemReel reel = getReelItem(stack);
      ItemBobber bobber = getBobberItem(stack);
      ItemHook hook = getHookItem(stack);
      if (GuiScreen.isShiftKeyDown()) {
         tooltip.add(TextFormatting.BLUE + "" + TextFormatting.BOLD + I18n.format("tooltip.fishingmadebetter.fishing_rod.reel.name", new Object[0]) + ": " + TextFormatting.RESET + "" + TextFormatting.GRAY + I18n.format("item.fishingmadebetter." + reel.getRegistryName().getResourcePath() + ".name", new Object[0]) + TextFormatting.RESET);
         tooltip.add(reel.getMaxDamage() != 0 ? "  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.durability.title", new Object[0]) + ": " + (reel.getMaxDamage() - getReelDamage(stack)) + "/" + reel.getMaxDamage() : "  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.durability.title", new Object[0]) + ": -/-");
         tooltip.add("  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.reel.range", new Object[0]) + ": " + reel.getReelRange() + "m");
         tooltip.add("  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.reel.speed", new Object[0]) + ": " + reel.getReelSpeed() + "m/s");
         tooltip.add(TextFormatting.BLUE + "" + TextFormatting.BOLD + I18n.format("tooltip.fishingmadebetter.fishing_rod.bobber", new Object[0]) + ": " + TextFormatting.RESET + "" + TextFormatting.GRAY + I18n.format("item.fishingmadebetter." + bobber.getRegistryName().getResourcePath() + ".name", new Object[0]) + TextFormatting.RESET);
         tooltip.add(bobber.getMaxDamage() != 0 ? "  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.durability.title", new Object[0]) + ": " + (bobber.getMaxDamage() - getBobberDamage(stack)) + "/" + bobber.getMaxDamage() : "  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.durability.title", new Object[0]) + ": -/-");
         if (bobber.isLavaBobber()) {
            tooltip.add("  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.bobber.can_fish_in", new Object[0]) + ": " + TextFormatting.RED + I18n.format("tooltip.fishingmadebetter.fishing_rod.bobber.can_fish_in.lava", new Object[0]) + TextFormatting.RESET);
         } else if (bobber.isVoidBobber()) {
            tooltip.add("  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.bobber.can_fish_in", new Object[0]) + ": " + TextFormatting.LIGHT_PURPLE + I18n.format("tooltip.fishingmadebetter.fishing_rod.bobber.can_fish_in.void", new Object[0]) + TextFormatting.RESET);
         } else {
            tooltip.add("  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.bobber.can_fish_in", new Object[0]) + ": " + TextFormatting.BLUE + I18n.format("tooltip.fishingmadebetter.fishing_rod.bobber.can_fish_in.water", new Object[0]) + TextFormatting.RESET);
         }

         if (bobber.getVarianceModifier() != 0) {
            tooltip.add("  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.bobber.tension_size", new Object[0]) + ": +" + bobber.getVarianceModifier());
         }

         if (bobber.getTensioningModifier() != 0) {
            tooltip.add("  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.bobber.tension_speed", new Object[0]) + ": +" + bobber.getTensioningModifier());
         }

         tooltip.add(TextFormatting.BLUE + "" + TextFormatting.BOLD + I18n.format("tooltip.fishingmadebetter.fishing_rod.hook", new Object[0]) + ": " + TextFormatting.RESET + "" + TextFormatting.GRAY + I18n.format("item.fishingmadebetter." + hook.getRegistryName().getResourcePath() + ".name", new Object[0]) + TextFormatting.RESET);
         tooltip.add(hook.getMaxDamage() != 0 ? "  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.durability.title", new Object[0]) + ": " + (hook.getMaxDamage() - getHookDamage(stack)) + "/" + hook.getMaxDamage() : "  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.durability.title", new Object[0]) + ": -/-");
         if (hook.getTuggingReduction() != 0) {
            tooltip.add("  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.hook.tugging", new Object[0]) + ": -" + hook.getTuggingReduction());
         }

         if (hook.getTreasureModifier() != 0) {
            tooltip.add("  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.hook.treasure_chance", new Object[0]) + ": +" + hook.getTreasureModifier());
         }

         if (hook.getBiteRateModifier() != 0) {
            tooltip.add("  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.hook.bite_rate", new Object[0]) + ": +" + hook.getBiteRateModifier());
         }

         if (hook.getWeightModifier() != 0) {
            tooltip.add("  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.hook.weight", new Object[0]) + ": +" + hook.getWeightModifier());
         }

         String baitDisplayName = getBaitDisplayName(stack);
         tooltip.add(TextFormatting.BLUE + "" + TextFormatting.BOLD + I18n.format("tooltip.fishingmadebetter.fishing_rod.bait", new Object[0]) + ": " + TextFormatting.RESET + "" + TextFormatting.GRAY + (baitDisplayName != null && baitDisplayName.length() > 0 ? baitDisplayName : I18n.format("tooltip.fishingmadebetter.fishing_rod.bait_none", new Object[0])) + TextFormatting.RESET);
         tooltip.add("");
      } else {
         tooltip.add(TextFormatting.BLUE + "" + TextFormatting.BOLD + I18n.format("tooltip.fishingmadebetter.fishing_rod.reel.name", new Object[0]) + ": " + TextFormatting.RESET + "" + TextFormatting.GRAY + I18n.format("item.fishingmadebetter." + reel.getRegistryName().getResourcePath() + ".name", new Object[0]) + TextFormatting.RESET);
         tooltip.add(reel.getMaxDamage() != 0 ? "  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.durability.title", new Object[0]) + ": " + (reel.getMaxDamage() - getReelDamage(stack)) + "/" + reel.getMaxDamage() : "  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.durability.title", new Object[0]) + ": -/-");
         tooltip.add(TextFormatting.BLUE + "" + TextFormatting.BOLD + I18n.format("tooltip.fishingmadebetter.fishing_rod.bobber", new Object[0]) + ": " + TextFormatting.RESET + "" + TextFormatting.GRAY + I18n.format("item.fishingmadebetter." + bobber.getRegistryName().getResourcePath() + ".name", new Object[0]) + TextFormatting.RESET);
         tooltip.add(bobber.getMaxDamage() != 0 ? "  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.durability.title", new Object[0]) + ": " + (bobber.getMaxDamage() - getBobberDamage(stack)) + "/" + bobber.getMaxDamage() : "  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.durability.title", new Object[0]) + ": -/-");
         tooltip.add(TextFormatting.BLUE + "" + TextFormatting.BOLD + I18n.format("tooltip.fishingmadebetter.fishing_rod.hook", new Object[0]) + ": " + TextFormatting.RESET + "" + TextFormatting.GRAY + I18n.format("item.fishingmadebetter." + hook.getRegistryName().getResourcePath() + ".name", new Object[0]) + TextFormatting.RESET);
         tooltip.add(hook.getMaxDamage() != 0 ? "  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.durability.title", new Object[0]) + ": " + (hook.getMaxDamage() - getHookDamage(stack)) + "/" + hook.getMaxDamage() : "  " + I18n.format("tooltip.fishingmadebetter.fishing_rod.durability.title", new Object[0]) + ": -/-");
         String baitDisplayName = getBaitDisplayName(stack);
         tooltip.add(TextFormatting.BLUE + "" + TextFormatting.BOLD + I18n.format("tooltip.fishingmadebetter.fishing_rod.bait", new Object[0]) + ": " + TextFormatting.RESET + "" + TextFormatting.GRAY + (baitDisplayName != null && baitDisplayName.length() > 0 ? baitDisplayName : I18n.format("tooltip.fishingmadebetter.fishing_rod.bait_none", new Object[0])) + TextFormatting.RESET);
         tooltip.add("");
         tooltip.add(I18n.format("tooltip.fishingmadebetter.fishing_rod.shift.1", new Object[0]) + " " + TextFormatting.GOLD + "Shift" + TextFormatting.RESET + "" + TextFormatting.GRAY + " " + I18n.format("tooltip.fishingmadebetter.fishing_rod.shift.2", new Object[0]) + TextFormatting.RESET);
      }

   }

   @Nullable
   public static String getBaitItem(ItemStack itemStack) {
      return hasBait(itemStack) ? itemStack.getTagCompound().getString("BaitItem") : null;
   }

   public static void setBaitItem(ItemStack itemStack, String bait) {
      if (!itemStack.hasTagCompound()) {
         itemStack.setTagCompound(new NBTTagCompound());
      }

      itemStack.getTagCompound().setString("BaitItem", bait);
   }

   public static void removeBait(ItemStack itemStack) {
      if (itemStack.hasTagCompound()) {
         itemStack.getTagCompound().removeTag("BaitItem");
         itemStack.getTagCompound().removeTag("BaitMetadata");
         itemStack.getTagCompound().removeTag("BaitDisplayName");
      }
   }

   public static int getBaitMetadata(ItemStack itemStack) {
      return hasBait(itemStack) ? itemStack.getTagCompound().getInteger("BaitMetadata") : 0;
   }

   public static void setBaitMetadata(ItemStack itemStack, int metadata) {
      if (!itemStack.hasTagCompound()) {
         itemStack.setTagCompound(new NBTTagCompound());
      }

      itemStack.getTagCompound().setInteger("BaitMetadata", metadata);
   }

   @Nullable
   public static String getBaitDisplayName(ItemStack itemStack) {
      return hasBait(itemStack) ? itemStack.getTagCompound().getString("BaitDisplayName") : null;
   }

   public static void setBaitDisplayName(ItemStack itemStack, String baitDisplayName) {
      if (!itemStack.hasTagCompound()) {
         itemStack.setTagCompound(new NBTTagCompound());
      }

      itemStack.getTagCompound().setString("BaitDisplayName", baitDisplayName);
   }

   public static boolean hasBait(ItemStack itemStack) {
      return itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("BaitItem");
   }

   public static boolean isBaitForFish(ItemStack itemStack, String fishId) {
      if (!hasBait(itemStack)) {
         return false;
      } else {
         FishData fishData = (FishData)CustomConfigurationHandler.fishDataMap.get(fishId);
         if (fishData == null) {
            return false;
         } else {
            String baitItem = getBaitItem(itemStack);
            if (baitItem == null) {
               return false;
            } else {
               return fishData.baitItemMap.containsKey(baitItem) && Arrays.asList(fishData.baitItemMap.get(baitItem)).contains(Integer.valueOf(getBaitMetadata(itemStack)));
            }
         }
      }
   }

   public static ItemReel getReelItem(ItemStack itemStack) {
      return hasReelItem(itemStack) ? (ItemReel)Item.getByNameOrId(itemStack.getTagCompound().getString("ReelItem")) : ItemManager.REEL_BASIC;
   }

   public static void setReelItem(ItemStack itemStack, ItemReel itemReel) {
      if (!itemStack.hasTagCompound()) {
         itemStack.setTagCompound(new NBTTagCompound());
      }

      itemStack.getTagCompound().setString("ReelItem", itemReel.getRegistryName().toString());
   }

   public static void removeReelItem(ItemStack itemStack) {
      if (itemStack.hasTagCompound()) {
         itemStack.getTagCompound().removeTag("ReelItem");
         itemStack.getTagCompound().removeTag("ReelDamage");
      }
   }

   public static int getReelDamage(ItemStack itemStack) {
      return hasReelItem(itemStack) ? itemStack.getTagCompound().getInteger("ReelDamage") : 0;
   }

   public static void setReelDamage(ItemStack itemStack, int newDamage) {
      if (!itemStack.hasTagCompound()) {
         itemStack.setTagCompound(new NBTTagCompound());
      }

      itemStack.getTagCompound().setInteger("ReelDamage", hasReelItem(itemStack) ? newDamage : 0);
   }

   public static boolean hasReelItem(ItemStack itemStack) {
      return itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("ReelItem");
   }

   public static ItemBobber getBobberItem(ItemStack itemStack) {
      return hasBobberItem(itemStack) ? (ItemBobber)Item.getByNameOrId(itemStack.getTagCompound().getString("BobberItem")) : ItemManager.BOBBER_BASIC;
   }

   public static void setBobberItem(ItemStack itemStack, ItemBobber itemBobber) {
      if (!itemStack.hasTagCompound()) {
         itemStack.setTagCompound(new NBTTagCompound());
      }

      itemStack.getTagCompound().setString("BobberItem", itemBobber.getRegistryName().toString());
   }

   public static void removeBobberItem(ItemStack itemStack) {
      if (itemStack.hasTagCompound()) {
         itemStack.getTagCompound().removeTag("BobberItem");
         itemStack.getTagCompound().removeTag("BobberDamage");
      }
   }

   public static int getBobberDamage(ItemStack itemStack) {
      return hasBobberItem(itemStack) ? itemStack.getTagCompound().getInteger("BobberDamage") : 0;
   }

   public static void setBobberDamage(ItemStack itemStack, int newDamage) {
      if (!itemStack.hasTagCompound()) {
         itemStack.setTagCompound(new NBTTagCompound());
      }

      itemStack.getTagCompound().setInteger("BobberDamage", hasBobberItem(itemStack) ? newDamage : 0);
   }

   public static boolean hasBobberItem(ItemStack itemStack) {
      return itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("BobberItem");
   }

   public static ItemHook getHookItem(ItemStack itemStack) {
      return hasHookItem(itemStack) ? (ItemHook)Item.getByNameOrId(itemStack.getTagCompound().getString("HookItem")) : ItemManager.HOOK_BASIC;
   }

   public static void setHookItem(ItemStack itemStack, ItemHook itemHook) {
      if (!itemStack.hasTagCompound()) {
         itemStack.setTagCompound(new NBTTagCompound());
      }

      itemStack.getTagCompound().setString("HookItem", itemHook.getRegistryName().toString());
   }

   public static void removeHookItem(ItemStack itemStack) {
      if (itemStack.hasTagCompound()) {
         itemStack.getTagCompound().removeTag("HookItem");
         itemStack.getTagCompound().removeTag("HookDamage");
      }
   }

   public static int getHookDamage(ItemStack itemStack) {
      return hasHookItem(itemStack) ? itemStack.getTagCompound().getInteger("HookDamage") : 0;
   }

   public static void setHookDamage(ItemStack itemStack, int newDamage) {
      if (!itemStack.hasTagCompound()) {
         itemStack.setTagCompound(new NBTTagCompound());
      }

      itemStack.getTagCompound().setInteger("HookDamage", hasHookItem(itemStack) ? newDamage : 0);
   }

   public static boolean hasHookItem(ItemStack itemStack) {
      return itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("HookItem");
   }
}
