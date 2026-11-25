package vazkii.quark.tweaks.feature;

import java.util.Objects;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import vazkii.quark.base.Quark;
import vazkii.quark.base.module.Feature;

public class RightClickSignEdit extends Feature {
   public static boolean emptyHand;

   public RightClickSignEdit() {
   }

   public void setupConfig() {
      emptyHand = this.loadPropBool("Requires Empty Hands", "", false);
   }

   @SubscribeEvent
   public void onInteract(RightClickBlock event) {
      if (event.getUseBlock() != Result.DENY) {
         TileEntity tile = event.getWorld().getTileEntity(event.getPos());
         if (tile instanceof TileEntitySign && (!emptyHand || event.getEntityPlayer().getHeldItemMainhand().isEmpty()) && event.getEntityPlayer().capabilities.allowEdit && !event.getEntity().isSneaking()) {
            IBlockState state = event.getWorld().getBlockState(event.getPos());
            if (Objects.toString(state.getBlock().getRegistryName()).contains("tcguideposts")) {
               return;
            }

            TileEntitySign sign = (TileEntitySign)tile;
            sign.setPlayer(event.getEntityPlayer());
            ObfuscationReflectionHelper.setPrivateValue(TileEntitySign.class, sign, Boolean.valueOf(true), "isEditable");
            event.getEntityPlayer().openGui(Quark.instance, 0, event.getWorld(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
         }

      }
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
