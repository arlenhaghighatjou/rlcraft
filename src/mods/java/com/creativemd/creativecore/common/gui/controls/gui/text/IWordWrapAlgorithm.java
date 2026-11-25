package com.creativemd.creativecore.common.gui.controls.gui.text;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@FunctionalInterface
public interface IWordWrapAlgorithm {
   IWordWrapAlgorithm DEFAULT_WORDWRAP_ALGORITHM = (text, width, matcher) -> {
      if (width > 0 && text != null && !text.isEmpty()) {
         List<String> res = new ArrayList();
         String lot = text;

         while(true) {
            String p;
            if (matcher != null) {
               p = matcher.trimToWidth(text, width);
            } else {
               p = GuiRenderHelper.instance.font.trimStringToWidth(lot, width);
               if (p == null || p.length() == 0) {
                  return Stream.of(lot);
               }
            }

            res.add(p);
            lot = lot.substring(p.length());
            if (lot.isEmpty()) {
               break;
            }
         }

         return res.stream();
      } else {
         return Stream.of(text);
      }
   };
   IWordWrapAlgorithm DEFAULT_NEWLINE_WORDWRAP_ALGORITHM = (text, width, matcher) -> {
      String[] lines = text.toString().split("\\R");
      return width <= 0 ? Stream.of(lines) : Stream.of(lines).flatMap((L) -> {
         return DEFAULT_WORDWRAP_ALGORITHM.wrap(L, width, matcher);
      });
   };

   Stream<String> wrap(String var1, int var2, IWordMatcher var3);

   default Stream<String> wrap(String text, int width) {
      return this.wrap(text, width, IWordMatcher.DEFAULT_MATCHER);
   }

   default Stream<String> wrap(String text) {
      return this.wrap(text, -1, IWordMatcher.DEFAULT_MATCHER);
   }
}
