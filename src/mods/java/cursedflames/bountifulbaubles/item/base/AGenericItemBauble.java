package cursedflames.bountifulbaubles.item.base;

import baubles.api.IBauble;
import cursedflames.bountifulbaubles.BountifulBaubles;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.botania.api.item.IPhantomInkable;

public abstract class AGenericItemBauble extends GenericItemBB implements IBauble {
   public AGenericItemBauble(String name) {
      this(name, (CreativeTabs)null);
   }

   public AGenericItemBauble(String name, CreativeTabs tab) {
      super(name, tab);
      this.setMaxStackSize(1);
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      super.addInformation(stack, worldIn, tooltip, flagIn);
      if (stack.getItem() instanceof IPhantomInkable && ((IPhantomInkable)stack.getItem()).hasPhantomInk(stack)) {
         tooltip.add(BountifulBaubles.proxy.translate("bountifulbaubles.misc.hasPhantomInk"));
      }

   }

   public void onEquipped(ItemStack stack, EntityLivingBase player) {
   }

   public void onUnequipped(ItemStack stack, EntityLivingBase player) {
   }
}
