package com.teamacronymcoders.base.multiblocksystem.rectangular;

import javax.annotation.Nullable;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

public enum PartPosition implements IStringSerializable {
   UNKNOWN((EnumFacing)null, PartPosition.Type.UNKNOWN, PartPosition.Layer.UNKNOWN),
   INTERIOR((EnumFacing)null, PartPosition.Type.UNKNOWN, PartPosition.Layer.UNKNOWN),
   FRAME_CORNER_NORTH_EAST_TOP((EnumFacing)null, PartPosition.Type.FRAME, PartPosition.Layer.TOP),
   FRAME_CORNER_NORTH_WEST_TOP((EnumFacing)null, PartPosition.Type.FRAME, PartPosition.Layer.TOP),
   FRAME_CORNER_SOUTH_EAST_TOP((EnumFacing)null, PartPosition.Type.FRAME, PartPosition.Layer.TOP),
   FRAME_CORNER_SOUTH_WEST_TOP((EnumFacing)null, PartPosition.Type.FRAME, PartPosition.Layer.TOP),
   FRAME_CORNER_NORTH_EAST_BOTTOM((EnumFacing)null, PartPosition.Type.FRAME, PartPosition.Layer.BOTTOM),
   FRAME_CORNER_NORTH_WEST_BOTTOM((EnumFacing)null, PartPosition.Type.FRAME, PartPosition.Layer.BOTTOM),
   FRAME_CORNER_SOUTH_EAST_BOTTOM((EnumFacing)null, PartPosition.Type.FRAME, PartPosition.Layer.BOTTOM),
   FRAME_CORNER_SOUTH_WEST_BOTTOM((EnumFacing)null, PartPosition.Type.FRAME, PartPosition.Layer.BOTTOM),
   FRAME_NORTH_TOP(EnumFacing.NORTH, PartPosition.Type.FRAME, PartPosition.Layer.TOP),
   FRAME_EAST_TOP(EnumFacing.EAST, PartPosition.Type.FRAME, PartPosition.Layer.TOP),
   FRAME_WEST_TOP(EnumFacing.WEST, PartPosition.Type.FRAME, PartPosition.Layer.TOP),
   FRAME_SOUTH_TOP(EnumFacing.SOUTH, PartPosition.Type.FRAME, PartPosition.Layer.TOP),
   FRAME_NORTH_BOTTOM(EnumFacing.NORTH, PartPosition.Type.FRAME, PartPosition.Layer.BOTTOM),
   FRAME_EAST_BOTTOM(EnumFacing.EAST, PartPosition.Type.FRAME, PartPosition.Layer.BOTTOM),
   FRAME_WEST_BOTTOM(EnumFacing.WEST, PartPosition.Type.FRAME, PartPosition.Layer.BOTTOM),
   FRAME_SOUTH_BOTTOM(EnumFacing.SOUTH, PartPosition.Type.FRAME, PartPosition.Layer.BOTTOM),
   FRAME_VERTICAL_NORTH_EAST((EnumFacing)null, PartPosition.Type.FRAME, PartPosition.Layer.MIDDLE),
   FRAME_VERTICAL_SOUTH_EAST((EnumFacing)null, PartPosition.Type.FRAME, PartPosition.Layer.MIDDLE),
   FRAME_VERTICAL_NORTH_WEST((EnumFacing)null, PartPosition.Type.FRAME, PartPosition.Layer.MIDDLE),
   FRAME_VERTICAL_SOUTH_WEST((EnumFacing)null, PartPosition.Type.FRAME, PartPosition.Layer.MIDDLE),
   TOP_FACE(EnumFacing.UP, PartPosition.Type.FACE, PartPosition.Layer.TOP),
   BOTTOM_FACE(EnumFacing.DOWN, PartPosition.Type.FACE, PartPosition.Layer.BOTTOM),
   NORTH_FACE(EnumFacing.NORTH, PartPosition.Type.FACE, PartPosition.Layer.MIDDLE),
   SOUTH_FACE(EnumFacing.SOUTH, PartPosition.Type.FACE, PartPosition.Layer.MIDDLE),
   EAST_FACE(EnumFacing.EAST, PartPosition.Type.FACE, PartPosition.Layer.MIDDLE),
   WEST_FACE(EnumFacing.WEST, PartPosition.Type.FACE, PartPosition.Layer.MIDDLE);

   private EnumFacing _facing;
   private PartPosition.Type _type;
   private PartPosition.Layer _layer;

   public boolean isFace() {
      return this._type == PartPosition.Type.FACE;
   }

   public boolean isFrame() {
      return this._type == PartPosition.Type.FRAME;
   }

   @Nullable
   public EnumFacing getFacing() {
      return this._facing;
   }

   public PartPosition.Type getType() {
      return this._type;
   }

   public PartPosition.Layer getLayer() {
      return this._layer;
   }

   public static PropertyEnum<PartPosition> createProperty(String name) {
      return PropertyEnum.create(name, PartPosition.class);
   }

   public String getName() {
      return this.name().toLowerCase();
   }

   private PartPosition(@Nullable EnumFacing facing, PartPosition.Type type, PartPosition.Layer layer) {
      this._facing = facing;
      this._type = type;
      this._layer = layer;
   }

   public static enum Layer {
      UNKNOWN,
      TOP,
      BOTTOM,
      MIDDLE;

      private Layer() {
      }
   }

   public static enum Type {
      UNKNOWN,
      INTERIOR,
      FRAME,
      FACE;

      private Type() {
      }
   }
}
