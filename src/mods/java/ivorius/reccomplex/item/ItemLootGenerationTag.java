package ivorius.reccomplex.item;

import ivorius.ivtoolkit.tools.IvTranslations;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.utils.ItemHandlers;
import ivorius.reccomplex.world.storage.loot.LootGenerationHandler;
import ivorius.reccomplex.world.storage.loot.LootTable;
import ivorius.reccomplex.world.storage.loot.WeightedItemCollectionRegistry;
import java.util.List;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.IItemHandlerModifiable;

public abstract class ItemLootGenerationTag extends Item implements GeneratingItem {
   public ItemLootGenerationTag() {
      this.setMaxStackSize(1);
   }

   public static boolean applyGeneratorToInventory(WorldServer world, BlockPos pos, GeneratingItem generatingItem, ItemStack stack) {
      TileEntity rightClicked = world.getTileEntity(pos);
      if (rightClicked != null && ItemHandlers.hasModifiable(rightClicked)) {
         if (!world.isRemote) {
            IItemHandlerModifiable itemHandler = ItemHandlers.getModifiable(rightClicked);
            generatingItem.generateInInventory(world, itemHandler, world.rand, stack, world.rand.nextInt(itemHandler.getSlots()));
            LootGenerationHandler.generateAllTags(world, itemHandler, RecurrentComplex.specialRegistry.itemHidingMode(), world.rand);
         }

         return true;
      } else {
         return false;
      }
   }

   public static String lootTableKey(ItemStack stack) {
      if (stack.hasTagCompound() && stack.getTagCompound().hasKey("itemCollectionKey", 8)) {
         return stack.getTagCompound().getString("itemCollectionKey");
      } else {
         if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("display", 10)) {
            NBTTagCompound nbttagcompound = stack.getTagCompound().getCompoundTag("display");
            if (nbttagcompound.hasKey("Name", 8)) {
               return nbttagcompound.getString("Name");
            }
         }

         return null;
      }
   }

   public static LootTable lootTable(ItemStack stack) {
      return (LootTable)WeightedItemCollectionRegistry.INSTANCE.get(lootTableKey(stack));
   }

   public static void setItemStackGeneratorKey(ItemStack stack, String generatorKey) {
      stack.setTagInfo("itemCollectionKey", new NBTTagString(generatorKey));
   }

   public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote) {
         return applyGeneratorToInventory((WorldServer)worldIn, pos, this, player.getHeldItem(hand)) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
      } else {
         return EnumActionResult.SUCCESS;
      }
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
      if (this.isInCreativeTab(tab)) {
         WeightedItemCollectionRegistry.INSTANCE.ids().stream().sorted().forEach((key) -> {
            ItemStack stack = new ItemStack(this);
            setItemStackGeneratorKey(stack, key);
            items.add(stack);
         });
      }

   }

   @Nonnull
   @ParametersAreNonnullByDefault
   public String getItemStackDisplayName(ItemStack stack) {
      String key = lootTableKey(stack);
      return key != null ? key : super.getItemStackDisplayName(stack);
   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      LootTable generator = lootTable(stack);
      if (generator != null) {
         tooltip.add(generator.getDescriptor());
      } else {
         tooltip.add(IvTranslations.get("inventoryGen.none"));
      }

   }
}
