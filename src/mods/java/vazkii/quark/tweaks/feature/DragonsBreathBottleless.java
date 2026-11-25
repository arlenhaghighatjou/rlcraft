package vazkii.quark.tweaks.feature;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import vazkii.quark.base.module.Feature;

public class DragonsBreathBottleless extends Feature {
   public DragonsBreathBottleless() {
   }

   public void postInit() {
      Items.DRAGON_BREATH.setContainerItem((Item)null);
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public String getFeatureIngameConfigName() {
      return "Effective Dragon Breath";
   }
}
