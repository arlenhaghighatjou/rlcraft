package ivorius.reccomplex.item;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import ivorius.ivtoolkit.gui.IntegerRange;
import ivorius.ivtoolkit.item.IvItemStacks;
import ivorius.reccomplex.gui.loot.GuiEditItemStack;
import ivorius.reccomplex.gui.loot.TableDataSourceLootGenMultiTag;
import ivorius.reccomplex.world.storage.loot.LootTable;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.tuple.Pair;

public class ItemLootGenMultiTag extends ItemLootGenerationTag implements ItemSyncableTags {
   public ItemLootGenMultiTag() {
   }

   public static TIntList emptySlots(IItemHandler inv) {
      TIntList list = new TIntArrayList();

      for(int i = 0; i < inv.getSlots(); ++i) {
         if (inv.getStackInSlot(i) == ItemStack.EMPTY) {
            list.add(i);
         }
      }

      return list;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      if (worldIn.isRemote) {
         this.openGui(playerIn, playerIn.inventory.currentItem);
      }

      return super.onItemRightClick(worldIn, playerIn, handIn);
   }

   @SideOnly(Side.CLIENT)
   private void openGui(EntityPlayer player, int slot) {
      Minecraft.getMinecraft().displayGuiScreen(new GuiEditItemStack(player, slot, new TableDataSourceLootGenMultiTag()));
   }

   public void generateInInventory(WorldServer server, IItemHandlerModifiable inventory, Random random, ItemStack stack, int fromSlot) {
      LootTable lootTable = lootTable(stack);
      inventory.setStackInSlot(fromSlot, ItemStack.EMPTY);
      if (lootTable != null) {
         IntegerRange range = this.getGenerationCount(stack);
         if (range.getMin() <= range.getMax()) {
            int amount = random.nextInt(range.getMax() - range.getMin() + 1) + range.getMin();
            TIntList emptySlots = emptySlots(inventory);

            for(int i = 0; i < amount; ++i) {
               int slot = emptySlots.isEmpty() ? random.nextInt(inventory.getSlots()) : emptySlots.removeAt(random.nextInt(emptySlots.size()));
               ItemStack generated = lootTable.getRandomItemStack(server, random);
               if (generated != null) {
                  inventory.setStackInSlot(slot, generated);
               }
            }

         }
      }
   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      IntegerRange range = this.getGenerationCount(stack);
      tooltip.add(String.format("%d - %d Items", range.getMin(), range.getMax()));
   }

   public IntegerRange getGenerationCount(ItemStack stack) {
      return new IntegerRange(IvItemStacks.getNBT(stack, "itemCountMin", 4), IvItemStacks.getNBT(stack, "itemCountMax", 8));
   }

   public void setGenerationCount(ItemStack stack, IntegerRange range) {
      stack.setTagInfo("itemCountMin", new NBTTagInt(range.getMin()));
      stack.setTagInfo("itemCountMax", new NBTTagInt(range.getMax()));
   }

   public List<Pair<String, Integer>> getSyncedNBTTags() {
      return Arrays.asList(Pair.of("itemCountMin", Integer.valueOf(3)), Pair.of("itemCountMax", Integer.valueOf(3)));
   }
}
