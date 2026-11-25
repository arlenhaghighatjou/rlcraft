package com.yyon.grapplinghook.items.alternategrapple;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.items.grappleBow;

public class SmartHook extends grappleBow {
   public SmartHook() {
   }

   public GrappleCustomization getDefaultCustomization() {
      GrappleCustomization custom = new GrappleCustomization();
      custom.motor = true;
      custom.smartmotor = true;
      custom.maxlen = GrappleConfig.options.upgraded_maxlen;
      custom.throwspeed = GrappleConfig.options.upgraded_throwspeed;
      custom.playermovementmult = 2.0D;
      return custom;
   }
}
