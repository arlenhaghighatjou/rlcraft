package cursedflames.bountifulbaubles.item;

import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.item.base.GenericItemBB;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import vazkii.quark.api.ICustomEnchantColor;

public class ItemPhantomPrism extends GenericItemBB implements ICustomEnchantColor {
   public static final int GUI_ID = 3;

   public ItemPhantomPrism() {
      super("phantomPrism", BountifulBaubles.TAB);
      this.setMaxStackSize(1);
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      player.setActiveHand(hand);
      player.openGui(BountifulBaubles.instance, 3, world, (int)player.posX, (int)player.posY, (int)player.posZ);
      return new ActionResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
   }

   public boolean hasEffect(ItemStack stack) {
      return true;
   }

   public int getEnchantEffectColor(ItemStack stack) {
      return 5577135;
   }
}
