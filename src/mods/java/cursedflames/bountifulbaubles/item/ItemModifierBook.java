package cursedflames.bountifulbaubles.item;

import cursedflames.bountifulbaubles.BountifulBaubles;
import cursedflames.bountifulbaubles.baubleeffect.EnumBaubleModifier;
import cursedflames.bountifulbaubles.item.base.GenericItemBB;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.api.ICustomEnchantColor;

public class ItemModifierBook extends GenericItemBB implements ICustomEnchantColor {
   public static final int XP_LVL_COST = 2;

   public ItemModifierBook() {
      super("modifierBook", BountifulBaubles.TAB, false);
      BountifulBaubles.registryHelper.addItemModel(this);
   }

   public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> stacks) {
      if (this.isInCreativeTab(tab)) {
         for(EnumBaubleModifier mod : EnumBaubleModifier.values()) {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("baubleModifier", mod.name);
            ItemStack stack = new ItemStack(this);
            stack.setTagCompound(tag);
            stacks.add(stack);
         }
      }

   }

   public String getItemStackDisplayName(ItemStack stack) {
      return BountifulBaubles.proxy.translate(this.getUnlocalizedName() + "." + (stack.hasTagCompound() && stack.getTagCompound().hasKey("baubleModifier") ? stack.getTagCompound().getString("baubleModifier") : EnumBaubleModifier.NONE.name) + ".name");
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      if (stack.hasTagCompound() && stack.getTagCompound().hasKey("baubleModifier")) {
         tooltip.add(BountifulBaubles.proxy.translate("bountifulbaubles.modifier." + stack.getTagCompound().getString("baubleModifier") + ".info"));
      }

      super.addInformation(stack, worldIn, tooltip, flagIn);
      tooltip.add(BountifulBaubles.proxy.translate("bountifulbaubles.creativeonly"));
   }

   public boolean hasEffect(ItemStack stack) {
      return stack.hasTagCompound() ? stack.getTagCompound().getString("baubleModifier") != "none" : false;
   }

   public int getEnchantEffectColor(ItemStack arg0) {
      return 1795014;
   }
}
