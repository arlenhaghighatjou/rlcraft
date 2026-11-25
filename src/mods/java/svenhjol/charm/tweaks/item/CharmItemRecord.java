package svenhjol.charm.tweaks.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemRecord;
import net.minecraft.util.SoundEvent;
import svenhjol.meson.iface.IMesonItem;

public class CharmItemRecord extends ItemRecord implements IMesonItem {
   public CharmItemRecord(String modId, String name, SoundEvent sound) {
      super(modId + ":" + name, sound);
      this.register("record_" + name);
      this.setCreativeTab(CreativeTabs.MISC);
   }

   public String getModId() {
      return "charm";
   }
}
