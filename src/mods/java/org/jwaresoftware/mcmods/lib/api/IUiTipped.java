package org.jwaresoftware.mcmods.lib.api;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jwaresoftware.mcmods.lib.Tooltips;

public interface IUiTipped extends Oidable {
   @SideOnly(Side.CLIENT)
   default void getUiTip(ItemStack from, boolean internal, @Nullable EntityPlayer player, boolean advanced, List<String> tooltip) {
      Tooltips.addDocTipLine(from, tooltip, "tooltip");
   }
}
