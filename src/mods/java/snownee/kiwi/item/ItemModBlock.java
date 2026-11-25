package snownee.kiwi.item;

import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import snownee.kiwi.block.IModBlock;

public class ItemModBlock extends ItemBlock implements IModItem {
   protected final IModBlock iModBlock;

   public ItemModBlock(IModBlock block) {
      super(block.cast());
      this.iModBlock = block;
      if (block.getItemSubtypeAmount() > 1) {
         this.setHasSubtypes(true);
      }

   }

   public String getName() {
      return this.iModBlock.getName();
   }

   public void register(String modid) {
      this.setRegistryName(modid, this.getName());
      this.setUnlocalizedName(modid + "." + this.getName());
   }

   public Item cast() {
      return this;
   }

   @SideOnly(Side.CLIENT)
   public void mapModel() {
   }

   public int getMetadata(int damage) {
      return this.hasSubtypes ? damage : 0;
   }

   public String getUnlocalizedName(ItemStack stack) {
      return this.hasSubtypes ? super.getUnlocalizedName(stack) + "." + stack.getMetadata() : super.getUnlocalizedName(stack);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      ItemMod.addTip(stack, tooltip);
   }
}
