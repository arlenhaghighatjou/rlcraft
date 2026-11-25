package com.daposeidonguy.pigstep.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class ItemDiscPigstep extends ItemRecord {
   public static SoundEvent SOUND_EVENT = new SoundEvent(new ResourceLocation("pigstep", "pigstep"));

   protected ItemDiscPigstep(String name) {
      super(name, SOUND_EVENT);
      this.setCreativeTab(CreativeTabs.MISC);
      this.setUnlocalizedName(name);
      this.setRegistryName("pigstep", name);
   }
}
