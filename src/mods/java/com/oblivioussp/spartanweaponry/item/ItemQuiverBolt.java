package com.oblivioussp.spartanweaponry.item;

import com.oblivioussp.spartanweaponry.client.gui.GuiHandler;
import com.oblivioussp.spartanweaponry.client.model.ModelBoltQuiverHeavy;
import com.oblivioussp.spartanweaponry.client.model.ModelBoltQuiverLight;
import com.oblivioussp.spartanweaponry.client.model.ModelBoltQuiverMedium;
import com.oblivioussp.spartanweaponry.client.model.ModelQuiverBase;
import com.oblivioussp.spartanweaponry.util.StringHelper;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemQuiverBolt extends ItemQuiverBase {
   public static final ResourceLocation TextureBoltQuiverLight = new ResourceLocation("spartanweaponry", "textures/model/quiver_bolt_light.png");
   public static final ResourceLocation TextureBoltQuiverMedium = new ResourceLocation("spartanweaponry", "textures/model/quiver_bolt_medium.png");
   public static final ResourceLocation TextureBoltQuiverHeavy = new ResourceLocation("spartanweaponry", "textures/model/quiver_bolt_heavy.png");

   public ItemQuiverBolt(String unlocName, int inventorySize) {
      super(unlocName, inventorySize);
      this.guiIdQuiver = GuiHandler.GUI_ID_QUIVER_BOLT;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
      tooltip.add(StringHelper.translateString("quiver_bolt.desc", "tooltip", "spartanweaponry"));
      super.addInformation(stack, worldIn, tooltip, flagIn);
   }

   public boolean isAmmoValid(ItemStack ammo, ItemStack quiver) {
      return ammo.getItem() instanceof ItemBolt;
   }

   public ModelQuiverBase initModel() {
      if (this.arrowSlots == 6) {
         return new ModelBoltQuiverMedium();
      } else {
         return (ModelQuiverBase)(this.arrowSlots == 9 ? new ModelBoltQuiverHeavy() : new ModelBoltQuiverLight());
      }
   }

   public ResourceLocation getTexture() {
      if (this.arrowSlots == 6) {
         return TextureBoltQuiverMedium;
      } else {
         return this.arrowSlots == 9 ? TextureBoltQuiverHeavy : TextureBoltQuiverLight;
      }
   }
}
