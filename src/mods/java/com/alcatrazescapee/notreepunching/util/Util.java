package com.alcatrazescapee.notreepunching.util;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;

public final class Util {
   public static final EaglercraftRandom RNG = new EaglercraftRandom();
   public static final Converter<String, String> CASE_CONVERTER = CaseFormat.UPPER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL);

   public Util() {
   }
}
