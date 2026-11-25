package noppes.vc.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import noppes.vc.Server;
import noppes.vc.constants.GuiType;
import noppes.vc.constants.PacketClient;

public class ItemVCRecipesBook extends ItemBasic {
   public ItemVCRecipesBook() {
   }

   public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (!world.isRemote) {
         Server.sendData((EntityPlayerMP)player, PacketClient.GUI, GuiType.VCRECIPES, Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
         return new ActionResult(EnumActionResult.SUCCESS, itemstack);
      } else {
         return new ActionResult(EnumActionResult.SUCCESS, itemstack);
      }
   }
}
