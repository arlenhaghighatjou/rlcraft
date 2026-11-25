package knightminer.inspirations.recipes.item;

import knightminer.inspirations.library.Util;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemDyedWaterBottle extends Item {
   public static final String TAG_COLOR = "color";

   public ItemDyedWaterBottle() {
      this.setCreativeTab(CreativeTabs.MATERIALS);
      this.setMaxStackSize(16);
      this.setContainerItem(Items.GLASS_BOTTLE);
      this.setHasSubtypes(true);
   }

   public int getColor(ItemStack stack) {
      int meta = stack.getMetadata();
      if (meta < 16) {
         return EnumDyeColor.byDyeDamage(meta).colorValue;
      } else {
         NBTTagCompound tags = stack.getTagCompound();
         if (tags != null) {
            NBTTagCompound display = tags.getCompoundTag("display");
            if (display != null && display.hasKey("color", 3)) {
               return display.getInteger("color");
            }
         }

         return -1;
      }
   }

   public ItemStack getStackWithColor(int color) {
      EnumDyeColor dyeColor = Util.getDyeForColor(color);
      if (dyeColor != null) {
         return new ItemStack(this, 1, dyeColor.getDyeDamage());
      } else {
         ItemStack result = new ItemStack(this, 1, 16);
         NBTTagCompound tags = new NBTTagCompound();
         NBTTagCompound display = new NBTTagCompound();
         display.setInteger("color", color);
         tags.setTag("display", display);
         result.setTagCompound(tags);
         return result;
      }
   }

   public String getUnlocalizedName(ItemStack stack) {
      int meta = stack.getMetadata();
      return meta < 16 ? super.getUnlocalizedName(stack) + "." + EnumDyeColor.byDyeDamage(meta).getName() : super.getUnlocalizedName(stack);
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
      if (this.isInCreativeTab(tab)) {
         for(EnumDyeColor color : EnumDyeColor.values()) {
            subItems.add(new ItemStack(this, 1, color.getDyeDamage()));
         }
      }

   }

   public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
      int meta = stack.getMetadata();
      if (meta <= 15 && target instanceof EntitySheep) {
         EntitySheep sheep = (EntitySheep)target;
         EnumDyeColor color = EnumDyeColor.byDyeDamage(meta);
         if (!sheep.getSheared() && sheep.getFleeceColor() != color) {
            sheep.setFleeceColor(color);
            player.playSound(SoundEvents.ITEM_BOTTLE_EMPTY, 1.0F, 1.0F);
            ItemStack bottle = new ItemStack(this.getContainerItem());
            if (stack.getCount() == 1) {
               player.setHeldItem(hand, bottle);
            } else {
               stack.shrink(1);
               ItemHandlerHelper.giveItemToPlayer(player, bottle);
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
