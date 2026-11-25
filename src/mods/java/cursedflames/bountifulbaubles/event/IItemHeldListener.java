package cursedflames.bountifulbaubles.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public interface IItemHeldListener {
   void onHeldTick(ItemStack var1, EntityPlayer var2, EnumHand var3);
}
