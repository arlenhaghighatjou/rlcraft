package reborncore.api.power;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import reborncore.common.powerSystem.TilePowerAcceptor;
import reborncore.common.powerSystem.forge.ForgePowerItemManager;

public interface ExternalPowerManager {
   ExternalPowerHandler createPowerHandler(TilePowerAcceptor var1);

   boolean isPoweredItem(ItemStack var1);

   boolean isPoweredTile(TileEntity var1, EnumFacing var2);

   void dischargeItem(TilePowerAcceptor var1, ItemStack var2);

   void chargeItem(TilePowerAcceptor var1, ItemStack var2);

   void chargeItem(ForgePowerItemManager var1, ItemStack var2);

   default void requestEnergyFromArmor(ForgePowerItemManager powerAcceptor, EntityLivingBase entity) {
   }
}
