package rustic.common.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import rustic.core.Rustic;

public class ItemBook extends ItemBase {
   public static final int GUI_ID = 96;

   public ItemBook() {
      super("book");
      this.setMaxStackSize(1);
      this.setMaxDamage(0);
      this.setCreativeTab(Rustic.decorTab);
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      playerIn.openGui(Rustic.instance, 96, worldIn, playerIn.getPosition().getX(), playerIn.getPosition().getY(), playerIn.getPosition().getZ());
      return new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
   }
}
