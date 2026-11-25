package com.yyon.grapplinghook.items.alternategrapple;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.items.grappleBow;

public class RocketDoubleMotorHook extends grappleBow {
   public RocketDoubleMotorHook() {
   }

   public GrappleCustomization getDefaultCustomization() {
      GrappleCustomization custom = new GrappleCustomization();
      custom.doublehook = true;
      custom.motor = true;
      custom.motormaxspeed = 10.0D;
      custom.reelin = false;
      custom.sticky = true;
      custom.hookgravity = 50.0D;
      custom.verticalthrowangle = 30.0D;
      custom.sneakingverticalthrowangle = 25.0D;
      custom.reelin = false;
      custom.motorwhencrouching = true;
      custom.smartdoublemotor = true;
      custom.angle = 25.0D;
      custom.sneakingangle = 0.0D;
      custom.rocket = true;
      custom.rocket_vertical_angle = 30.0D;
      custom.maxlen = GrappleConfig.options.upgraded_maxlen;
      custom.throwspeed = 20.0D;
      custom.playermovementmult = 2.0D;
      return custom;
   }
}
