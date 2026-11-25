package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.client.gui.GuiHandler;
import com.oblivioussp.spartanweaponry.client.model.ModelArrowQuiverHeavy;
import com.oblivioussp.spartanweaponry.client.model.ModelArrowQuiverLight;
import com.oblivioussp.spartanweaponry.client.model.ModelArrowQuiverMedium;
import com.oblivioussp.spartanweaponry.client.model.ModelQuiverBase;
import com.oblivioussp.spartanweaponry.util.StringHelper;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemQuiverArrow extends ItemQuiverBase {
   public static final ResourceLocation TextureQuiverLight = new ResourceLocation("spartanweaponry", "textures/model/quiver_arrow_light.png");
   public static final ResourceLocation TextureQuiverMedium = new ResourceLocation("spartanweaponry", "textures/model/quiver_arrow_medium.png");
   public static final ResourceLocation TextureQuiverHeavy = new ResourceLocation("spartanweaponry", "textures/model/quiver_arrow_heavy.png");

   public ItemQuiverArrow(String unlocName, int inventorySize) {
      super(unlocName, inventorySize);
      this.guiIdQuiver = GuiHandler.GUI_ID_QUIVER_ARROW;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(StringHelper.translateString("quiver_arrow.desc", "tooltip", "spartanweaponry"));
      super.addInformation(stack, worldIn, tooltip, flagIn);
   }

   public boolean isAmmoValid(ItemStack ammo, ItemStack quiver) {
      return ammo.getItem() instanceof ItemArrow;
   }

   public ModelQuiverBase initModel() {
      if (this.arrowSlots == 6) {
         return new ModelArrowQuiverMedium();
      } else {
         return (ModelQuiverBase)(this.arrowSlots == 9 ? new ModelArrowQuiverHeavy() : new ModelArrowQuiverLight());
      }
   }

   public ResourceLocation getTexture() {
      if (this.arrowSlots == 6) {
         return TextureQuiverMedium;
      } else {
         return this.arrowSlots == 9 ? TextureQuiverHeavy : TextureQuiverLight;
      }
   }
}
