package com.teamacronymcoders.base.client.models.sided;

import com.google.common.collect.Maps;
import com.teamacronymcoders.base.blocks.properties.SideType;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.util.EnumFacing;

public class SidedTypeRegistry {
   private static final Map<String, ITextureNamer> TEXTURE_NAMERS = Maps.newHashMap();

   public SidedTypeRegistry() {
   }

   public static void addTextureNamer(String name, ITextureNamer textureNamer) {
      TEXTURE_NAMERS.put(name, textureNamer);
   }

   public static ITextureNamer getTextureNamer(String name) {
      return (ITextureNamer)TEXTURE_NAMERS.get(name);
   }

   public static Set<Entry<String, ITextureNamer>> getEntries() {
      return TEXTURE_NAMERS.entrySet();
   }

   static {
      addTextureNamer("all6", new ITextureNamer() {
      });
      addTextureNamer("s", new ITextureNamer() {
         public String nameFromSide(EnumFacing side, SideType cfg) {
            return "side";
         }
      });
      addTextureNamer("hud", new ITextureNamer() {
         public String nameFromSide(EnumFacing side, SideType cfg) {
            return side.ordinal() < 2 ? side.getName() : "side";
         }
      });
      addTextureNamer("hv", new ITextureNamer() {
         public String nameFromSide(EnumFacing side, SideType cfg) {
            return side.ordinal() < 2 ? "up" : "side";
         }
      });
      addTextureNamer("ud", new ITextureNamer() {
         public String nameFromSide(EnumFacing side, SideType cfg) {
            return side.ordinal() < 2 ? side.getName() : "side";
         }

         public String nameFromCfg(EnumFacing side, SideType cfg) {
            return side.ordinal() < 2 ? cfg.getName() : null;
         }
      });
      addTextureNamer("v", new ITextureNamer() {
         public String nameFromSide(EnumFacing side, SideType cfg) {
            return side.ordinal() < 2 ? "up" : "side";
         }

         public String nameFromCfg(EnumFacing side, SideType cfg) {
            return side.ordinal() < 2 ? cfg.getName() : null;
         }
      });
   }
}
