package com.teamacronymcoders.base.client.models.sided;

import com.teamacronymcoders.base.blocks.properties.SideType;
import net.minecraft.util.EnumFacing;

public interface ITextureNamer {
   default String getTextureName(EnumFacing side, SideType cfg) {
      String s = this.nameFromSide(side, cfg);
      String c = this.nameFromCfg(side, cfg);
      if (s != null && c != null) {
         return s + "_" + c;
      } else if (s != null) {
         return s;
      } else {
         return c != null ? c : "";
      }
   }

   default String nameFromSide(EnumFacing side, SideType cfg) {
      return side.getName();
   }

   default String nameFromCfg(EnumFacing side, SideType cfg) {
      return cfg.getName();
   }
}
