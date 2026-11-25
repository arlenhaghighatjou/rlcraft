package com.teamacronymcoders.base.nbt;

import com.teamacronymcoders.base.nbt.converters.NBTBlockPosConverter;
import com.teamacronymcoders.base.nbt.converters.NBTBooleanConverter;
import com.teamacronymcoders.base.nbt.converters.NBTByteArrayConverter;
import com.teamacronymcoders.base.nbt.converters.NBTByteConverter;
import com.teamacronymcoders.base.nbt.converters.NBTDoubleConverter;
import com.teamacronymcoders.base.nbt.converters.NBTFloatConverter;
import com.teamacronymcoders.base.nbt.converters.NBTFluidTankConverter;
import com.teamacronymcoders.base.nbt.converters.NBTIntArrayConverter;
import com.teamacronymcoders.base.nbt.converters.NBTIntConverter;
import com.teamacronymcoders.base.nbt.converters.NBTItemStackHandlerConverter;
import com.teamacronymcoders.base.nbt.converters.NBTLongConverter;
import com.teamacronymcoders.base.nbt.converters.NBTShortConverter;
import com.teamacronymcoders.base.nbt.converters.NBTStringConverter;
import com.teamacronymcoders.base.nbt.converters.NBTTagConverter;

public enum EnumConverter {
   BLOCKPOS(NBTBlockPosConverter.class),
   BOOLEAN(NBTBooleanConverter.class),
   BYTEARR(NBTByteArrayConverter.class),
   BYTE(NBTByteConverter.class),
   DOUBLE(NBTDoubleConverter.class),
   FLOAT(NBTFloatConverter.class),
   FLUIDTANK(NBTFluidTankConverter.class),
   INT(NBTIntConverter.class),
   INTARR(NBTIntArrayConverter.class),
   ITEMSTACKHANDLER(NBTItemStackHandlerConverter.class),
   LONG(NBTLongConverter.class),
   SHORT(NBTShortConverter.class),
   STRING(NBTStringConverter.class),
   TAG(NBTTagConverter.class);

   Class<? extends INBTConverter> converter;

   private EnumConverter(Class<? extends INBTConverter> converter) {
      this.converter = converter;
   }

   public Class<? extends INBTConverter> getConverter() {
      return this.converter;
   }
}
