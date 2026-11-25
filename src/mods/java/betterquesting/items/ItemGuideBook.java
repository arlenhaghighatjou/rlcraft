package betterquesting.items;

import betterquesting.core.BetterQuesting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemGuideBook extends Item {
   public ItemGuideBook() {
      this.setUnlocalizedName("betterquesting.guide");
      this.setCreativeTab(BetterQuesting.tabQuesting);
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack stack = player.getHeldItem(hand);
      if (world.isRemote && hand == EnumHand.MAIN_HAND) {
         player.openGui(BetterQuesting.instance, 1, world, 0, 0, 0);
      }

      return new ActionResult(EnumActionResult.PASS, stack);
   }

   @SideOnly(Side.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      return true;
   }
}
