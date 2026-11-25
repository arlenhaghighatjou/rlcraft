package net.theawesomegem.fishingmadebetter.common.item.filletknife;

import java.util.HashSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.theawesomegem.fishingmadebetter.common.registry.FMBCreativeTab;

public abstract class ItemFilletKnife extends ItemTool {
   public ItemFilletKnife(String name, float attackDamage, float attackSpeed, ToolMaterial materialIn) {
      super(attackDamage, attackSpeed, materialIn, new HashSet());
      this.setCreativeTab(FMBCreativeTab.instance);
      this.setRegistryName(name);
      this.setUnlocalizedName("fishingmadebetter." + name);
   }

   public boolean isEnchantable(ItemStack stack) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   public void registerItemModel(Item item) {
      ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("fishingmadebetter:fillet_knife/" + this.getRegistryName().getResourcePath(), "inventory"));
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(I18n.format("item.fishingmadebetter.fillet_knife.tooltip", new Object[0]));
   }

   public boolean hasContainerItem(ItemStack stack) {
      return true;
   }
}
