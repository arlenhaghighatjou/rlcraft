package ivorius.reccomplex.item;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.gui.RCGuiHandler;
import ivorius.reccomplex.utils.SaveDirectoryData;
import ivorius.reccomplex.world.storage.loot.GenericItemCollectionRegistry;
import ivorius.reccomplex.world.storage.loot.GenericLootTable;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;

public class ItemLootTableComponentTag extends Item implements GeneratingItem {
   public ItemLootTableComponentTag() {
      this.setMaxStackSize(1);
   }

   public static String componentKey(ItemStack stack) {
      if (stack.hasTagCompound() && stack.getTagCompound().hasKey("itemCollectionKey", 8)) {
         return stack.getTagCompound().getString("itemCollectionKey");
      } else {
         if (stack.hasTagCompound() && stack.getTagCompound().hasKey("display", 10)) {
            NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("display");
            if (nbttagcompound.hasKey("Name", 8)) {
               return nbttagcompound.getString("Name");
            }
         }

         return null;
      }
   }

   public static void setComponentKey(ItemStack stack, String generatorKey) {
      stack.setTagInfo("itemCollectionKey", new NBTTagString(generatorKey));
   }

   public static GenericLootTable.Component component(ItemStack stack) {
      return (GenericLootTable.Component)GenericItemCollectionRegistry.INSTANCE.get(componentKey(stack));
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemStackIn = playerIn.getHeldItem(handIn);
      if (!worldIn.isRemote) {
         RCGuiHandler.editLootTableComponent(playerIn, componentKey(itemStackIn), component(itemStackIn), (SaveDirectoryData)null);
      }

      return super.onItemRightClick(worldIn, playerIn, handIn);
   }

   public void generateInInventory(WorldServer server, IItemHandlerModifiable inventory, EaglercraftRandom random, ItemStack stack, int fromSlot) {
      GenericLootTable.Component component = component(stack);
      if (component != null) {
         inventory.setStackInSlot(fromSlot, component.getRandomItemStack(random));
      }

   }

   @Nonnull
   @ParametersAreNonnullByDefault
   public String getItemStackDisplayName(ItemStack stack) {
      String key = componentKey(stack);
      return key != null ? key : super.getItemStackDisplayName(stack);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      GenericLootTable.Component component = component(stack);
      if (component != null) {
         tooltip.add(component.tableID);
         tooltip.add(GenericItemCollectionRegistry.INSTANCE.hasActive(componentKey(stack)) ? IvTranslations.format("inventoryGen.active", new Object[]{TextFormatting.GREEN, TextFormatting.RESET}) : IvTranslations.format("inventoryGen.inactive", new Object[]{TextFormatting.RED, TextFormatting.RESET}));
      } else {
         tooltip.add(IvTranslations.get("inventoryGen.create"));
      }

   }

   @ParametersAreNonnullByDefault
   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      super.getSubItems(tab, items);
      if (this.isInCreativeTab(tab)) {
         GenericItemCollectionRegistry.INSTANCE.ids().stream().sorted().forEach((key) -> {
            ItemStack stack = new ItemStack(this);
            setComponentKey(stack, key);
            items.add(stack);
         });
      }

   }
}
