package de.Whitedraco.switchbow.item;

import de.Whitedraco.switchbow.Initial;
import de.Whitedraco.switchbow.gui.ContainerQuiver;
import java.util.HashMap;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Vector;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemQuiver extends Item {
   private static final int inventorySize = 9;
   private static final ItemStack[] inventory = new ItemStack[9];
   public ItemStack[] stacks = new ItemStack[9];
   private EaglercraftRandom field_146021_j = new EaglercraftRandom();
   protected String field_146020_a;

   public ItemQuiver() {
   }

   public ItemQuiver(String Name) {
      this.setRegistryName(Name);
      this.setUnlocalizedName(Name);
      this.maxStackSize = 1;
      this.setCreativeTab(Initial.tabSwitchbow);
   }

   public int getMaxItemUseDuration(ItemStack stack) {
      return 1;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      if (!worldIn.isRemote) {
         if (!playerIn.isSneaking()) {
            playerIn.openGui(Initial.instance, 0, worldIn, (int)playerIn.posX, (int)playerIn.posY, (int)playerIn.posZ);
         } else {
            this.saveArrowfromInventory(playerIn.getHeldItem(handIn), worldIn, playerIn);
         }
      }

      return new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
   }

   private void saveArrowfromInventory(ItemStack quiver, World world, EntityPlayer player) {
      ContainerQuiver containerQuiver = new ContainerQuiver(player, quiver);
      NonNullList<ItemStack> invplayer = player.inventory.mainInventory;

      for(int slot = 0; slot < player.inventory.mainInventory.size(); ++slot) {
         if (invplayer.get(slot) != ItemStack.EMPTY && ((ItemStack)invplayer.get(slot)).getItem() instanceof ItemArrow) {
            int invSlot = slot;
            if (slot <= 8) {
               invSlot = slot + 36;
            }

            containerQuiver.transferStackInSlot(player, invSlot);
         }
      }

      containerQuiver.onContainerClosed(player);
   }

   public void onCreated(ItemStack itemStack, World par2World, EntityPlayer par3EntityPlayer) {
      itemStack.setTagCompound(new NBTTagCompound());
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      String inventory = I18n.translateToLocal("tooltips.inventory");
      HashMap<String, Integer> storeArrow = new HashMap();
      Vector<ItemStack> storeArrowItem = new Vector();
      if (stack.getTagCompound() != null) {
         NBTTagList items = stack.getTagCompound().getTagList("ItemInventory", 10);

         for(int i = 0; i < items.tagCount(); ++i) {
            NBTTagCompound item = items.getCompoundTagAt(i);
            byte slot = item.getByte("Slot");
            if (slot >= 0 && slot < 9) {
               ItemStack inSlot = new ItemStack(item);
               if (inSlot != ItemStack.EMPTY) {
                  if (storeArrow.containsKey(inSlot.getDisplayName())) {
                     Integer count = ((Integer)storeArrow.get(inSlot.getDisplayName())).intValue() + inSlot.getCount();
                     storeArrow.put(inSlot.getDisplayName(), count);
                  } else {
                     storeArrow.put(inSlot.getDisplayName(), Integer.valueOf(inSlot.getCount()));
                     storeArrowItem.addElement(inSlot);
                  }
               }
            }
         }

         tooltip.add(inventory);

         for(int a = 0; a < storeArrowItem.size(); ++a) {
            ItemStack itemstack = (ItemStack)storeArrowItem.get(a);
            tooltip.add(((Integer)storeArrow.get(itemstack.getDisplayName())).toString() + "x " + itemstack.getDisplayName());
         }
      }

   }

   public static int getInventorysize() {
      return 9;
   }
}
