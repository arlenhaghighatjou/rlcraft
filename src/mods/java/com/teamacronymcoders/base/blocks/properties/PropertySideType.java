package com.teamacronymcoders.base.blocks.properties;

import net.minecraftforge.common.property.IUnlistedProperty;

public class PropertySideType implements IUnlistedProperty<SideType> {
   private final String name;
   public static final PropertySideType[] SIDE_TYPE = new PropertySideType[]{new PropertySideType("sidetype_down"), new PropertySideType("sidetype_up"), new PropertySideType("sidetype_north"), new PropertySideType("sidetype_south"), new PropertySideType("sidetype_west"), new PropertySideType("sidetype_east")};

   public PropertySideType(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public boolean isValid(SideType value) {
      return true;
   }

   public Class<SideType> getType() {
      return SideType.class;
   }

   public String valueToString(SideType value) {
      return value.toString();
   }
}
