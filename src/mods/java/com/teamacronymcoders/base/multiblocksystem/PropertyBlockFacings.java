package com.teamacronymcoders.base.multiblocksystem;

import java.util.EnumSet;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

public enum PropertyBlockFacings implements IStringSerializable {
   None(BlockFacings.computeHash(false, false, false, false, false, false).byteValue()),
   All(BlockFacings.computeHash(true, true, true, true, true, true).byteValue()),
   Face_D(BlockFacings.computeHash(true, false, false, false, false, false).byteValue()),
   Face_E(BlockFacings.computeHash(false, false, false, false, false, true).byteValue()),
   Face_N(BlockFacings.computeHash(false, false, true, false, false, false).byteValue()),
   Face_S(BlockFacings.computeHash(false, false, false, true, false, false).byteValue()),
   Face_U(BlockFacings.computeHash(false, true, false, false, false, false).byteValue()),
   Face_W(BlockFacings.computeHash(false, false, false, false, true, false).byteValue()),
   Angle_DE(BlockFacings.computeHash(true, false, false, false, false, true).byteValue()),
   Angle_DN(BlockFacings.computeHash(true, false, true, false, false, false).byteValue()),
   Angle_DS(BlockFacings.computeHash(true, false, false, true, false, false).byteValue()),
   Angle_DW(BlockFacings.computeHash(true, false, false, false, true, false).byteValue()),
   Angle_EN(BlockFacings.computeHash(false, false, true, false, false, true).byteValue()),
   Angle_ES(BlockFacings.computeHash(false, false, false, true, false, true).byteValue()),
   Angle_EU(BlockFacings.computeHash(false, true, false, false, false, true).byteValue()),
   Angle_NU(BlockFacings.computeHash(false, true, true, false, false, false).byteValue()),
   Angle_NW(BlockFacings.computeHash(false, false, true, false, true, false).byteValue()),
   Angle_SU(BlockFacings.computeHash(false, true, false, true, false, false).byteValue()),
   Angle_SW(BlockFacings.computeHash(false, false, false, true, true, false).byteValue()),
   Angle_UW(BlockFacings.computeHash(false, true, false, false, true, false).byteValue()),
   Opposite_DU(BlockFacings.computeHash(true, true, false, false, false, false).byteValue()),
   Opposite_EW(BlockFacings.computeHash(false, false, false, false, true, true).byteValue()),
   Opposite_NS(BlockFacings.computeHash(false, false, true, true, false, false).byteValue()),
   CShape_DEU(BlockFacings.computeHash(true, true, false, false, false, true).byteValue()),
   CShape_DEW(BlockFacings.computeHash(true, false, false, false, true, true).byteValue()),
   CShape_DNS(BlockFacings.computeHash(true, false, true, true, false, false).byteValue()),
   CShape_DNU(BlockFacings.computeHash(true, true, true, false, false, false).byteValue()),
   CShape_DSU(BlockFacings.computeHash(true, true, false, true, false, false).byteValue()),
   CShape_DUW(BlockFacings.computeHash(true, true, false, false, true, false).byteValue()),
   CShape_ENS(BlockFacings.computeHash(false, false, true, true, false, true).byteValue()),
   CShape_ENW(BlockFacings.computeHash(false, false, true, false, true, true).byteValue()),
   CShape_ESW(BlockFacings.computeHash(false, false, false, true, true, true).byteValue()),
   CShape_EUW(BlockFacings.computeHash(false, true, false, false, true, true).byteValue()),
   CShape_NSU(BlockFacings.computeHash(false, true, true, true, false, false).byteValue()),
   CShape_NSW(BlockFacings.computeHash(false, false, true, true, true, false).byteValue()),
   Corner_DEN(BlockFacings.computeHash(true, false, true, false, false, true).byteValue()),
   Corner_DES(BlockFacings.computeHash(true, false, false, true, false, true).byteValue()),
   Corner_DNW(BlockFacings.computeHash(true, false, true, false, true, false).byteValue()),
   Corner_DSW(BlockFacings.computeHash(true, false, false, true, true, false).byteValue()),
   Corner_ENU(BlockFacings.computeHash(false, true, true, false, false, true).byteValue()),
   Corner_ESU(BlockFacings.computeHash(false, true, false, true, false, true).byteValue()),
   Corner_NUW(BlockFacings.computeHash(false, true, true, false, true, false).byteValue()),
   Corner_SUW(BlockFacings.computeHash(false, true, false, true, true, false).byteValue()),
   Misc_DENS(BlockFacings.computeHash(true, false, true, true, false, true).byteValue()),
   Misc_DENU(BlockFacings.computeHash(true, true, true, false, false, true).byteValue()),
   Misc_DENW(BlockFacings.computeHash(true, false, true, false, true, true).byteValue()),
   Misc_DESU(BlockFacings.computeHash(true, true, false, true, false, true).byteValue()),
   Misc_DESW(BlockFacings.computeHash(true, false, false, true, true, true).byteValue()),
   Misc_DNSW(BlockFacings.computeHash(true, false, true, true, true, false).byteValue()),
   Misc_DNUW(BlockFacings.computeHash(true, true, true, false, true, false).byteValue()),
   Misc_DSUW(BlockFacings.computeHash(true, true, false, true, true, false).byteValue()),
   Misc_ENSU(BlockFacings.computeHash(false, true, true, true, false, true).byteValue()),
   Misc_ENUW(BlockFacings.computeHash(false, true, true, false, true, true).byteValue()),
   Misc_ESUW(BlockFacings.computeHash(false, true, false, true, true, true).byteValue()),
   Misc_NSUW(BlockFacings.computeHash(false, true, true, true, true, false).byteValue()),
   Pipe_DEUW(BlockFacings.computeHash(true, true, false, false, true, true).byteValue()),
   Pipe_DNSU(BlockFacings.computeHash(true, true, true, true, false, false).byteValue()),
   Pipe_ENSW(BlockFacings.computeHash(false, false, true, true, true, true).byteValue()),
   PipeEnd_DENSU(BlockFacings.computeHash(true, true, true, true, false, true).byteValue()),
   PipeEnd_DENSW(BlockFacings.computeHash(true, false, true, true, true, true).byteValue()),
   PipeEnd_DENUW(BlockFacings.computeHash(true, true, true, false, true, true).byteValue()),
   PipeEnd_DESUW(BlockFacings.computeHash(true, true, false, true, true, true).byteValue()),
   PipeEnd_DNSUW(BlockFacings.computeHash(true, true, true, true, true, false).byteValue()),
   PipeEnd_ENSUW(BlockFacings.computeHash(false, true, true, true, false, true).byteValue());

   public static final PropertyEnum FACINGS = PropertyEnum.create("facings", PropertyBlockFacings.class);
   public static final EnumSet<PropertyBlockFacings> ALL_AND_NONE = EnumSet.of(All, None);
   public static final EnumSet<PropertyBlockFacings> FACES = EnumSet.of(Face_D, Face_E, Face_N, Face_S, Face_U, Face_W);
   public static final EnumSet<PropertyBlockFacings> ANGLES = EnumSet.of(Angle_DE, Angle_DN, Angle_DS, Angle_DW, Angle_EN, Angle_ES, Angle_EU, Angle_NU, Angle_NW, Angle_SU, Angle_SW, Angle_UW);
   public static final EnumSet<PropertyBlockFacings> OPPOSITES = EnumSet.of(Opposite_DU, Opposite_EW, Opposite_NS);
   public static final EnumSet<PropertyBlockFacings> CSHAPES = EnumSet.of(CShape_DEU, CShape_DEW, CShape_DNS, CShape_DNU, CShape_DSU, CShape_DUW, CShape_ENS, CShape_ENW, CShape_ESW, CShape_EUW, CShape_NSU, CShape_NSW);
   public static final EnumSet<PropertyBlockFacings> CORNERS = EnumSet.of(Corner_DEN, Corner_DES, Corner_DNW, Corner_DSW, Corner_ENU, Corner_ESU, Corner_NUW, Corner_SUW);
   public static final EnumSet<PropertyBlockFacings> MISCELLANEA = EnumSet.of(Misc_DENS, Misc_DENU, Misc_DENW, Misc_DESU, Misc_DESW, Misc_DNSW, Misc_DNUW, Misc_DSUW, Misc_ENSU, Misc_ENUW, Misc_ESUW, Misc_NSUW);
   public static final EnumSet<PropertyBlockFacings> PIPES = EnumSet.of(Pipe_DEUW, Pipe_DNSU, Pipe_ENSW);
   public static final EnumSet<PropertyBlockFacings> PIPEENDS = EnumSet.of(PipeEnd_DENSU, PipeEnd_DENSW, PipeEnd_DENUW, PipeEnd_DESUW, PipeEnd_DNSUW, PipeEnd_ENSUW);
   final byte _hash;
   private final String _name;

   public String getName() {
      return this._name;
   }

   private PropertyBlockFacings(byte hash) {
      this._hash = hash;
      this._name = this.toString().toLowerCase();
   }
}
