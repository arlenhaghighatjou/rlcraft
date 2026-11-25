package com.endertech.minecraft.forge.units;

import com.endertech.minecraft.forge.configs.IForgeEnum;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.IStringSerializable;

public class UnitVariant {
   public static final String VARIANTS = "variants";
   public static final String INVENTORY = "inventory";
   private final UnitId id;
   private final String variant;
   private ModelResourceLocation location;

   protected UnitVariant(UnitId id, String name, String value) {
      this(id, name + "=" + value);
   }

   public UnitVariant(UnitId id, String variant) {
      this.location = null;
      this.id = id;
      this.variant = variant;
   }

   public static <E extends Enum<?> & IForgeEnum> UnitVariant from(UnitId id, E en) {
      String className = IForgeUnit.getClassRegName(en.getClass());
      return className.equals("variants") ? new UnitVariant(id, ((IStringSerializable)en).getName()) : new UnitVariant(id, className, ((IStringSerializable)en).getName());
   }

   public static UnitVariant from(ModelResourceLocation model) {
      UnitId id = UnitId.from(model, 0);
      return new UnitVariant(id, model.getVariant());
   }

   public String getVariant() {
      return this.variant;
   }

   public UnitId getId() {
      return this.id;
   }

   public ModelResourceLocation toModelResLoc() {
      if (this.location == null) {
         this.location = new ModelResourceLocation(this.getId().toResLoc(), this.getVariant());
      }

      return this.location;
   }
}
