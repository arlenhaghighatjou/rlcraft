package com.alcatrazescapee.notreepunching.util;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import java.util.Random;

public final class Util {
   public static final Random RNG = new Random();
   public static final Converter<String, String> CASE_CONVERTER = CaseFormat.UPPER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL);

   public Util() {
   }
}
