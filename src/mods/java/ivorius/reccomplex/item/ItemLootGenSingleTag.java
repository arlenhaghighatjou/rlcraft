package ivorius.reccomplex.item;

import ivorius.ivtoolkit.item.IvItemStacks;
import ivorius.reccomplex.gui.loot.GuiEditItemStack;
import ivorius.reccomplex.gui.loot.TableDataSourceLootGeneratorSingleTag;
import ivorius.reccomplex.world.storage.loot.LootTable;
import java.util.Collections;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.apache.commons.lang3.tuple.Pair;

public class ItemLootGenSingleTag extends ItemLootGenerationTag implements ItemSyncableTags {
   public ItemLootGenSingleTag() {
   }

   public void generateInInventory(WorldServer server, IItemHandlerModifiable inventory, EaglercraftRandom random, ItemStack stack, int fromSlot) {
      LootTable lootTable = lootTable(stack);
      if (lootTable != null) {
         ItemStack generated = random.nextFloat() < this.getItemChance(stack) ? lootTable.getRandomItemStack(server, random) : null;
         if (generated != null) {
            inventory.setStackInSlot(fromSlot, generated);
         }
      }

   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
      if (worldIn.isRemote) {
         this.openGui(playerIn, playerIn.inventory.currentItem);
      }

      return super.onItemRightClick(worldIn, playerIn, hand);
   }

   @SideOnly(Side.CLIENT)
   private void openGui(EntityPlayer player, int slot) {
      Minecraft.getMinecraft().displayGuiScreen(new GuiEditItemStack(player, slot, new TableDataSourceLootGeneratorSingleTag()));
   }

   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      tooltip.add(String.format("%f Chance", this.getItemChance(stack)));
   }

   public float getItemChance(ItemStack stack) {
      return IvItemStacks.getNBT(stack, "itemChance", 1.0F);
   }

   public void setItemChance(ItemStack stack, float chance) {
      stack.setTagInfo("itemChance", new NBTTagFloat(chance));
   }

   public List<Pair<String, Integer>> getSyncedNBTTags() {
      return Collections.singletonList(Pair.of("itemChance", Integer.valueOf(5)));
   }
}
