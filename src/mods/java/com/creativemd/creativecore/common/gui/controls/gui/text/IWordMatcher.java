package com.creativemd.creativecore.common.gui.controls.gui.text;

import com.creativemd.creativecore.common.gui.GuiRenderHelper;

public interface IWordMatcher {
   IWordMatcher DEFAULT_MATCHER = new IWordMatcher() {
      public int findNextWordStartBoundary(String text, int from) {
         IWordMatcher.checkBounds(text, from);
         return -1;
      }

      public int findNextWordEndBoundary(String text, int from) {
         IWordMatcher.checkBounds(text, from);
         return text != null && from != text.length() ? text.length() : -1;
      }

      public int findPreviousWordStartBoundary(String text, int from) {
         IWordMatcher.checkBounds(text, from);
         return text != null && from != 0 ? 0 : -1;
      }

      public int findPreviousWordEndBoundary(String text, int from) {
         IWordMatcher.checkBounds(text, from);
         return -1;
      }
   };

   default int findNextWordBoundary(String text, int from) {
      return Math.min(this.findNextWordStartBoundary(text, from), this.findNextWordEndBoundary(text, from));
   }

   int findNextWordStartBoundary(String var1, int var2);

   int findNextWordEndBoundary(String var1, int var2);

   default int findPreviousWordBoundary(String text, int from) {
      return Math.max(this.findPreviousWordStartBoundary(text, from), this.findPreviousWordEndBoundary(text, from));
   }

   int findPreviousWordStartBoundary(String var1, int var2);

   int findPreviousWordEndBoundary(String var1, int var2);

   default TextareaRange findWordAt(String text, int pos) {
      checkBounds(text, pos);
      if (this.isWordStartBoundary(text, pos)) {
         int end = this.findNextWordEndBoundary(text, pos);
         return end < 0 ? TextareaRange.INVALID_RANGE : new TextareaRange(pos, end);
      } else if (this.isWordEndBoundary(text, pos)) {
         int start = this.findPreviousWordStartBoundary(text, pos);
         return start < 0 ? TextareaRange.INVALID_RANGE : new TextareaRange(start, pos);
      } else {
         int start = this.findPreviousWordStartBoundary(text, pos);
         int end = this.findNextWordEndBoundary(text, pos);
         return start >= 0 && end >= 0 ? new TextareaRange(start, end) : TextareaRange.INVALID_RANGE;
      }
   }

   default boolean isWordAt(String text, int pos) {
      return this.isWordBoundary(text, pos) || this.findPreviousWordEndBoundary(text, pos) < this.findPreviousWordStartBoundary(text, pos);
   }

   default boolean isWordBoundary(String text, int pos) {
      return this.isWordStartBoundary(text, pos) || this.isWordEndBoundary(text, pos);
   }

   default boolean isWordStartBoundary(String text, int pos) {
      checkBounds(text, pos);
      if (text != null && !text.isEmpty()) {
         if (pos == 0) {
            return this.findPreviousWordStartBoundary(text, pos + 1) == pos;
         } else {
            return this.findNextWordStartBoundary(text, pos - 1) == pos;
         }
      } else {
         return false;
      }
   }

   default boolean isWordEndBoundary(String text, int pos) {
      checkBounds(text, pos);
      if (text != null && !text.isEmpty()) {
         if (pos == text.length()) {
            return this.findNextWordEndBoundary(text, pos - 1) == pos;
         } else {
            return this.findPreviousWordEndBoundary(text, pos + 1) == pos;
         }
      } else {
         return false;
      }
   }

   default boolean isLineBreakCanditate(String text, int pos) {
      return this.isWordStartBoundary(text, pos);
   }

   default int findPreviousLineBreakCanditate(String text, int pos) {
      return this.findPreviousWordStartBoundary(text, pos);
   }

   default int findNextLineBreakCanditate(String text, int pos) {
      return this.findNextWordStartBoundary(text, pos);
   }

   default String trimToWidth(String text, int width) {
      String r = GuiRenderHelper.instance.font.trimStringToWidth(text, width);
      if (r != null && r.length() != 0) {
         if (!this.isLineBreakCanditate(text, r.length())) {
            int pe = this.findPreviousLineBreakCanditate(text, r.length());
            if (pe > 0 && pe < r.length()) {
               return text.substring(0, pe);
            }
         }

         return r;
      } else {
         return text;
      }
   }

   static void checkBounds(String text, int pos) {
      if (pos < 0 || pos > text.length()) {
         throw new IndexOutOfBoundsException();
      }
   }
}
