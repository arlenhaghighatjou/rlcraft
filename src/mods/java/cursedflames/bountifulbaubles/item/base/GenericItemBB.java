package cursedflames.bountifulbaubles.item.base;

import baubles.api.render.IRenderBauble.RenderType;
import cursedflames.bountifulbaubles.client.layer.IRenderObject;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GenericItemBB extends Item implements IRenderObject {
   public GenericItemBB(String name) {
      this(name, (CreativeTabs)null);
   }

   public GenericItemBB(String name, CreativeTabs tab) {
      this(name, tab, true);
   }

   public GenericItemBB(String name, CreativeTabs tab, boolean configCreativeOnly) {
      this.setRegistryName(new ResourceLocation("bountifulbaubles", name));
      this.setUnlocalizedName("bountifulbaubles." + name);
      if (tab != null) {
         this.setCreativeTab(tab);
      }

   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      boolean isShifting = GuiScreen.isShiftKeyDown();
      String base = this.getUnlocalizedName() + ".tooltip.";
      String shift = "";
      if (I18n.canTranslate(base + "0")) {
         if (isShifting && I18n.canTranslate(base + "0s")) {
            shift = "s";
         }

         for(int i = 0; I18n.canTranslate(base + i + shift) && i < 100; ++i) {
            tooltip.add(I18n.translateToLocal(base + i + shift));
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public void onRenderObject(ItemStack stack, EntityPlayer player, boolean isSlim, float partialTicks, float scale) {
   }

   public RenderType getRenderType() {
      return null;
   }
}
