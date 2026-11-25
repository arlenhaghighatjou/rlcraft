package com.creativemd.creativecore.common.gui.controls.gui.text;

import com.creativemd.creativecore.common.utils.type.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

public class TextareaChange {
   protected GuiTextarea textarea;
   protected String text;
   protected List<String> lines = new ArrayList();
   protected List<Integer> offsets = new ArrayList();
   protected String replacementText = null;
   protected Integer replaceFrom = null;
   protected Integer replaceTo = null;
   protected Integer cursorOffset = null;
   protected Integer cursorRowPosition = null;
   protected Integer cursorColumnPosition = null;
   protected Integer anchorOffset = null;
   protected Integer anchorRowPosition = null;
   protected Integer anchorColumnPosition = null;

   public TextareaChange(GuiTextarea textarea, String replacementText, int replaceFrom, int replaceTo, int cursorOffset, int anchorOffset) {
      this.textarea = textarea;
      this.replacementText = replacementText;
      this.replaceFrom = Math.min(replaceFrom, replaceTo);
      this.replaceTo = Math.max(replaceFrom, replaceTo);
      this.cursorOffset = cursorOffset;
      this.anchorOffset = anchorOffset;
      this.checkReplacementRange(Integer.valueOf(replaceFrom), Integer.valueOf(replaceTo));
   }

   public TextareaChange(GuiTextarea textarea, String replacementText, int replaceFrom, int replaceTo, int cursorRowPosition, int cursorColumnPosition, int anchorRowPosition, int anchorColumnPosition) {
      this.textarea = textarea;
      this.replacementText = replacementText;
      this.replaceFrom = Math.min(replaceFrom, replaceTo);
      this.replaceTo = Math.max(replaceFrom, replaceTo);
      this.cursorRowPosition = cursorRowPosition;
      this.cursorColumnPosition = cursorColumnPosition;
      this.anchorRowPosition = anchorRowPosition;
      this.anchorColumnPosition = anchorColumnPosition;
      this.checkReplacementRange(Integer.valueOf(replaceFrom), Integer.valueOf(replaceTo));
   }

   public TextareaChange(GuiTextarea textarea, int cursorOffset, int anchorOffset) {
      this.textarea = textarea;
      this.cursorOffset = cursorOffset;
      this.anchorOffset = anchorOffset;
   }

   public TextareaChange(GuiTextarea textarea, int cursorRowPosition, int cursorColumnPosition, int anchorRowPosition, int anchorColumnPosition) {
      this.textarea = textarea;
      this.cursorRowPosition = cursorRowPosition;
      this.cursorColumnPosition = cursorColumnPosition;
      this.anchorRowPosition = anchorRowPosition;
      this.anchorColumnPosition = anchorColumnPosition;
   }

   public boolean isAdded() {
      return this.replacementText != null && !this.replacementText.isEmpty();
   }

   public boolean isRemoved() {
      return this.replacementText != null && this.replaceFrom != null && this.replaceFrom != this.replaceTo;
   }

   public boolean isReplaced() {
      return this.isAdded() && this.isRemoved();
   }

   public String getCurrentText() {
      return this.textarea.getText();
   }

   public String getCurrentSelectedText() {
      return this.textarea.getSelectedText();
   }

   public int getCurrentCursorOffset() {
      return this.textarea.getSelection().getCursorOffset();
   }

   public int getCurrentAnchorOffset() {
      return this.textarea.getSelection().getAnchorOffset();
   }

   public TextareaVec getCurrentCursorPosition() {
      return this.textarea.getSelection().getCursorPosition();
   }

   public TextareaVec getCurrentAnchorPosition() {
      return this.textarea.getSelection().getAnchorPosition();
   }

   public TextareaRange getCurrentSelectionOffsets() {
      return this.textarea.getSelection().getSelectionOffsets();
   }

   public TextareaRange getCurrentSelectionRangeOffsets() {
      return this.textarea.getSelection().getSelectionRangeOffsets();
   }

   public Pair<TextareaVec, TextareaVec> getCurrentSelectionPositions() {
      return this.textarea.getSelection().getSelectionPositions();
   }

   public Pair<TextareaVec, TextareaVec> getCurrentSelectionRangePositions() {
      return this.textarea.getSelection().getSelectionRangePositions();
   }

   public TextareaRange getReplacementRange() {
      return this.replaceFrom != null && this.replaceTo != null ? new TextareaRange(this.replaceFrom.intValue(), this.replaceTo.intValue()) : TextareaRange.INVALID_RANGE;
   }

   public String getNewText() {
      if (this.text == null) {
         this.recalcText();
      }

      return this.text;
   }

   public String getNewSelectedText() {
      TextareaRange sel = this.getNewSelectionRangeOffsets();
      return this.getNewText().substring(sel.start, sel.end);
   }

   public int getNewCursorOffset() {
      if (this.cursorOffset == null) {
         this.cursorOffset = this.toOffset(this.cursorRowPosition.intValue(), this.cursorColumnPosition.intValue());
      }

      return this.cursorOffset.intValue();
   }

   public int getNewAnchorOffset() {
      if (this.anchorOffset == null) {
         this.anchorOffset = this.toOffset(this.anchorRowPosition.intValue(), this.anchorColumnPosition.intValue());
      }

      return this.anchorOffset.intValue();
   }

   public TextareaVec getNewCursorPosition() {
      if (this.cursorRowPosition != null && this.cursorColumnPosition != null) {
         return new TextareaVec(this.cursorColumnPosition.intValue(), this.cursorRowPosition.intValue());
      } else {
         TextareaVec cur = this.toCoordinates(this.cursorOffset.intValue());
         this.cursorRowPosition = cur.row;
         this.cursorColumnPosition = cur.column;
         return cur;
      }
   }

   public TextareaVec getNewAnchorPosition() {
      if (this.anchorRowPosition != null && this.anchorColumnPosition != null) {
         return new TextareaVec(this.anchorColumnPosition.intValue(), this.anchorOffset.intValue());
      } else {
         TextareaVec anch = this.toCoordinates(this.anchorOffset.intValue());
         this.anchorRowPosition = anch.row;
         this.anchorColumnPosition = anch.column;
         return anch;
      }
   }

   public TextareaRange getNewSelectionOffsets() {
      return new TextareaRange(this.getNewAnchorOffset(), this.getNewCursorOffset());
   }

   public TextareaRange getNewSelectionRangeOffsets() {
      int anch = this.getNewAnchorOffset();
      int cur = this.getNewCursorOffset();
      return new TextareaRange(Math.min(anch, cur), Math.max(anch, cur));
   }

   public Pair<TextareaVec, TextareaVec> getNewSelectionPositions() {
      return new Pair<TextareaVec, TextareaVec>(this.getNewAnchorPosition(), this.getNewCursorPosition());
   }

   public Pair<TextareaVec, TextareaVec> getNewSelectionRangePositions() {
      TextareaVec cur = this.getNewCursorPosition();
      TextareaVec anch = this.getNewAnchorPosition();
      boolean anchorFirst = anch.row < cur.row || anch.row == cur.row && anch.column <= cur.column;
      return anchorFirst ? new Pair(anch, cur) : new Pair(cur, anch);
   }

   public void setReplacementText(String replacement) {
      this.replacementText = replacement;
      this.text = null;
      this.lines.clear();
      this.offsets.clear();
   }

   public void setReplacementRange(int from, int to) {
      this.checkReplacementRange(Integer.valueOf(from), Integer.valueOf(to));
      this.replaceFrom = Math.min(from, to);
      this.replaceTo = Math.max(from, to);
      this.text = null;
      this.lines.clear();
      this.offsets.clear();
   }

   public void setReplacement(int from, int to, String replacement) {
      this.setReplacementText(replacement);
      this.setReplacementRange(from, to);
   }

   public void setCursorOffset(int offset) {
      this.cursorOffset = offset;
      this.cursorRowPosition = null;
      this.cursorColumnPosition = null;
   }

   public void setCursorPosition(int row, int column) {
      this.cursorOffset = null;
      this.cursorRowPosition = row;
      this.cursorColumnPosition = column;
   }

   public void setSelection(int from, int to) {
      this.cursorOffset = to;
      this.cursorRowPosition = null;
      this.cursorColumnPosition = null;
      this.anchorOffset = from;
      this.anchorRowPosition = null;
      this.anchorColumnPosition = null;
   }

   public void setSelection(int fromRow, int fromColumn, int toRow, int toColumn) {
      this.cursorOffset = null;
      this.cursorRowPosition = toRow;
      this.cursorColumnPosition = toColumn;
      this.anchorOffset = null;
      this.anchorRowPosition = fromRow;
      this.anchorColumnPosition = fromColumn;
   }

   public void selectTo(int offset) {
      this.setSelection(this.getNewAnchorOffset(), offset);
   }

   public void selectTo(int row, int column) {
      this.selectTo(this.toOffset(row, column));
   }

   protected int toOffset(int row, int column) {
      if (this.lines.isEmpty() || this.offsets.isEmpty()) {
         this.reflowText();
      }

      int r = Math.max(0, Math.min(row, this.lines.size()));
      int c = Math.max(0, Math.min(column, ((String)this.lines.get(r)).length() - (this.textarea.replaceModeActive && row != this.lines.size() - 1 ? 1 : 0)));
      return ((Integer)this.offsets.get(r)).intValue() + c;
   }

   protected TextareaVec toCoordinates(int offset) {
      if (this.offsets.isEmpty()) {
         this.reflowText();
      }

      int o = Math.max(0, Math.min(offset, this.text.length()));
      int i = this.offsets.size();

      while(i-- > 0) {
         if (((Integer)this.offsets.get(i)).intValue() < o) {
            return new TextareaVec(o - ((Integer)this.offsets.get(i)).intValue(), i);
         }
      }

      return TextareaVec.INVALID_VEC;
   }

   protected void recalcText() {
      if (this.replacementText != null && this.replaceFrom != null && this.replaceTo != null) {
         StringBuilder sb = new StringBuilder(this.getCurrentText());
         this.text = sb.replace(this.replaceFrom.intValue(), this.replaceTo.intValue(), this.replacementText).toString();
      } else {
         this.text = this.getCurrentText();
      }

   }

   protected void reflowText() {
      if (this.text == null) {
         this.recalcText();
      }

      this.lines.clear();
      this.offsets.clear();
      IWordWrapAlgorithm wwa = this.textarea.wordWrapAlgorithm != null ? this.textarea.wordWrapAlgorithm : IWordWrapAlgorithm.DEFAULT_NEWLINE_WORDWRAP_ALGORITHM;
      ((Stream)wwa.wrap(this.text, this.textarea.width - this.textarea.getContentOffset() * 2, this.textarea.wordMatcher).sequential()).reduce(Integer.valueOf(0), (offset, line) -> {
         this.lines.add(line);
         this.offsets.add(offset);
         return offset.intValue() + line.length();
      }, (l, r) -> {
         return l.intValue() + r.intValue();
      });
   }

   protected void checkReplacementRange(Integer from, Integer to) {
      if (from != null && to != null && (from.intValue() < 0 || to.intValue() < 0 || from.intValue() > this.getCurrentText().length() || to.intValue() > this.getCurrentText().length())) {
         throw new IndexOutOfBoundsException("Cannot replace outside range of current string-size");
      }
   }

   protected void validate() {
      if (this.lines.isEmpty()) {
         this.reflowText();
      }

      this.checkReplacementRange(this.replaceFrom, this.replaceTo);
      String t = this.getNewText();
      if (this.cursorOffset == null || this.cursorOffset.intValue() >= 0 && this.cursorOffset.intValue() <= t.length()) {
         if (this.cursorRowPosition == null || this.cursorRowPosition.intValue() >= 0 && this.cursorRowPosition.intValue() < this.lines.size()) {
            if (this.cursorColumnPosition != null) {
               if (this.cursorRowPosition == null) {
                  throw new IllegalArgumentException("Cursor row-position unavailable for column-check");
               }

               if (this.cursorColumnPosition.intValue() < 0 || this.cursorColumnPosition.intValue() > ((String)this.lines.get(this.cursorRowPosition.intValue())).length() - (this.textarea.replaceModeActive && this.cursorRowPosition.intValue() != this.lines.size() - 1 ? 1 : 0)) {
                  throw new IllegalArgumentException("Invalid cursor column-position");
               }
            }

            if (this.anchorOffset == null || this.anchorOffset.intValue() >= 0 && this.anchorOffset.intValue() <= t.length()) {
               if (this.anchorRowPosition == null || this.anchorRowPosition.intValue() >= 0 && this.anchorRowPosition.intValue() < this.lines.size()) {
                  if (this.anchorColumnPosition != null) {
                     if (this.anchorRowPosition == null) {
                        throw new IllegalArgumentException("Anchor row-position unavailable for column-check");
                     }

                     if (this.anchorColumnPosition.intValue() < 0 || this.anchorColumnPosition.intValue() > ((String)this.lines.get(this.anchorRowPosition.intValue())).length() - (this.textarea.replaceModeActive && this.anchorRowPosition.intValue() != this.lines.size() - 1 ? 1 : 0)) {
                        throw new IllegalArgumentException("Invalid anchor column-position");
                     }
                  }

               } else {
                  throw new IllegalArgumentException("Invalid anchor row-position");
               }
            } else {
               throw new IllegalArgumentException("Invalid anchor position");
            }
         } else {
            throw new IllegalArgumentException("Invalid cursor row-position");
         }
      } else {
         throw new IllegalArgumentException("Invalid cursor position");
      }
   }
}
