package com.creativemd.creativecore.common.gui.controls.gui.text;

import com.creativemd.creativecore.common.utils.type.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

public class GuiTextareaContent {
   protected GuiTextarea textarea;
   protected String text = "";
   protected GuiTextareaContent.ShittyList<String> lines = new GuiTextareaContent.ShittyList<String>();
   protected List<String> linesUnmodifiable;
   protected GuiTextareaContent.ShittyList<Integer> offsets = new GuiTextareaContent.ShittyList<Integer>();

   protected GuiTextareaContent(GuiTextarea textarea) {
      if (textarea == null) {
         throw new NullPointerException();
      } else {
         this.textarea = textarea;
         this.reflowText();
      }
   }

   protected GuiTextareaContent(GuiTextarea textarea, String text) {
      if (textarea == null) {
         throw new NullPointerException();
      } else {
         this.textarea = textarea;
         this.text = text;
         this.reflowText();
      }
   }

   public String getText() {
      return this.text.toString();
   }

   public List<String> getLines() {
      if (this.linesUnmodifiable == null) {
         this.linesUnmodifiable = Collections.unmodifiableList(this.lines);
      }

      return this.linesUnmodifiable;
   }

   public int getLineCount() {
      return this.lines.size();
   }

   public String getLine(int row) {
      return (String)this.lines.get(row);
   }

   public int getLineLength(int row) {
      return ((String)this.lines.get(row)).length();
   }

   public int getLastColumnPosition() {
      return this.text.length();
   }

   public int getLastColumnPosition(int row) {
      return ((String)this.lines.get(row)).length() - (this.textarea.replaceModeActive && row != this.lines.size() - 1 ? 1 : 0);
   }

   public int getLineOffset(int row) {
      return ((Integer)this.offsets.get(row)).intValue();
   }

   public void setText(String text) {
      GuiTextareaSelection sel = this.textarea.getSelection();
      this.textarea.onContentChange(new TextareaChange(this.textarea, text, 0, this.text.length(), this.clampOffset(sel.getCursorOffset(), text.length()), this.clampOffset(sel.getAnchorOffset(), text.length())));
   }

   public void append(String text) {
      GuiTextareaSelection sel = this.textarea.getSelection();
      int nsl = this.text.length() + text.length();
      this.textarea.onContentChange(new TextareaChange(this.textarea, text, this.text.length(), this.text.length(), this.clampOffset(sel.getCursorOffset(), nsl), this.clampOffset(sel.getAnchorOffset(), nsl)));
   }

   public void replace(int from, int to, String replacement) {
      GuiTextareaSelection sel = this.textarea.getSelection();
      int nsl = this.text.length() - (to - from) + replacement.length();
      this.textarea.onContentChange(new TextareaChange(this.textarea, replacement, from, to, this.clampOffset(sel.getCursorOffset(), nsl), this.clampOffset(sel.getAnchorOffset(), nsl)));
   }

   public void insert(String text, int pos) {
      this.replace(pos, pos, text);
   }

   public void delete(int from, int to) {
      this.replace(from, to, "");
   }

   public void indent(int fromRow, int toRow) {
      String s = "";

      for(int i = fromRow; i <= toRow; ++i) {
         s = s + "\t" + this.getLine(i);
      }

      this.replace(this.getLineOffset(fromRow), this.getLineOffset(toRow) + this.getLine(toRow).length(), s);
   }

   public void outdent(int fromRow, int toRow) {
      String s = "";

      for(int i = fromRow; i <= toRow; ++i) {
         String l = this.getLine(i);
         if (l.startsWith("\t")) {
            s = s + l.substring(1);
         } else {
            s = s + l;
         }
      }

      this.replace(this.getLineOffset(fromRow), this.getLineOffset(toRow) + this.getLine(toRow).length(), s);
   }

   public void replaceSelection(String replacement) {
      GuiTextareaSelection sel = this.textarea.getSelection();
      TextareaRange range = sel.getSelectionRangeOffsets();
      int nsl = this.text.length() - (range.end - range.start) + replacement.length();
      this.textarea.onContentChange(new TextareaChange(this.textarea, replacement, range.start, range.end, this.clampOffset(range.start + replacement.length(), nsl), this.clampOffset(range.start + replacement.length(), nsl)));
   }

   public void appendAtCursor(String text) {
      GuiTextareaSelection sel = this.textarea.getSelection();
      int cur = sel.getCursorOffset();
      int nsl = this.text.length() + text.length();
      this.textarea.onContentChange(new TextareaChange(this.textarea, text, cur, cur, this.clampOffset(cur + text.length(), nsl), this.clampOffset(cur + text.length(), nsl)));
   }

   public void backspaceAtCursor() {
      GuiTextareaSelection sel = this.textarea.getSelection();
      int cur = sel.getCursorOffset();
      if (cur > 0) {
         int nsl = this.text.length() - 1;
         this.textarea.onContentChange(new TextareaChange(this.textarea, "", cur - 1, cur, this.clampOffset(cur - 1, nsl), this.clampOffset(cur - 1, nsl)));
      }

   }

   public void backspaceWordAtCursor() {
      GuiTextareaSelection sel = this.textarea.getSelection();
      IWordMatcher wm = this.textarea.getWordMatcher();
      int cur = sel.getCursorOffset();
      if (cur > 0) {
         int we = wm.findPreviousWordStartBoundary(this.text, cur);
         if (we >= 0) {
            int nsl = this.text.length() - (cur - we);
            this.textarea.onContentChange(new TextareaChange(this.textarea, "", we, cur, this.clampOffset(we, nsl), this.clampOffset(we, nsl)));
         } else {
            int nsl = this.text.length() - cur;
            this.textarea.onContentChange(new TextareaChange(this.textarea, "", 0, cur, 0, 0));
         }
      }

   }

   public void deleteAtCursor() {
      GuiTextareaSelection sel = this.textarea.getSelection();
      int cur = sel.getCursorOffset();
      if (cur < this.text.length()) {
         int nsl = this.text.length() - 1;
         this.textarea.onContentChange(new TextareaChange(this.textarea, "", cur, cur + 1, this.clampOffset(cur, nsl), this.clampOffset(cur, nsl)));
      }

   }

   public void deleteWordAtCursor() {
      GuiTextareaSelection sel = this.textarea.getSelection();
      IWordMatcher wm = this.textarea.getWordMatcher();
      int cur = sel.getCursorOffset();
      if (cur < this.text.length()) {
         int we = wm.findNextWordEndBoundary(this.text, cur);
         if (we >= 0) {
            int nsl = this.text.length() - (we - cur);
            this.textarea.onContentChange(new TextareaChange(this.textarea, "", cur, we, this.clampOffset(cur, nsl), this.clampOffset(cur, nsl)));
         } else {
            this.textarea.onContentChange(new TextareaChange(this.textarea, "", cur, this.text.length(), cur, cur));
         }
      }

   }

   public void typeText(char text) {
      GuiTextareaSelection sel = this.textarea.getSelection();
      if (sel.hasSelectedText()) {
         this.replaceSelection(Character.toString(text));
      } else {
         int cur = sel.getCursorOffset();
         if (this.textarea.replaceModeActive && cur != this.text.length()) {
            this.textarea.onContentChange(new TextareaChange(this.textarea, Character.toString(text), cur, cur + 1, cur + 1, cur + 1));
         } else {
            this.appendAtCursor(Character.toString(text));
         }
      }

   }

   public void indentSelection() {
      GuiTextareaSelection sel = this.textarea.getSelection();
      Pair<TextareaVec, TextareaVec> range = sel.getSelectionRangePositions();
      String rs = "";

      for(int i = ((TextareaVec)range.key).row; i <= ((TextareaVec)range.value).row; ++i) {
         rs = rs + "\t" + this.getLine(i);
      }

      if (sel.getCursorOffset() < sel.getAnchorOffset()) {
         this.textarea.onContentChange(new TextareaChange(this.textarea, rs, this.getLineOffset(((TextareaVec)range.key).row), this.getLineOffset(((TextareaVec)range.value).row) + this.getLine(((TextareaVec)range.value).row).length(), ((TextareaVec)range.key).row, ((TextareaVec)range.key).column + (((TextareaVec)range.key).column == 0 ? 0 : 1), ((TextareaVec)range.value).row, ((TextareaVec)range.value).column + 1));
      } else {
         this.textarea.onContentChange(new TextareaChange(this.textarea, rs, this.getLineOffset(((TextareaVec)range.key).row), this.getLineOffset(((TextareaVec)range.value).row) + this.getLine(((TextareaVec)range.value).row).length(), ((TextareaVec)range.value).row, ((TextareaVec)range.value).column + 1, ((TextareaVec)range.key).row, ((TextareaVec)range.key).column + (((TextareaVec)range.key).column == 0 ? 0 : 1)));
      }

   }

   public void outdentSelection() {
      GuiTextareaSelection sel = this.textarea.getSelection();
      Pair<TextareaVec, TextareaVec> range = sel.getSelectionRangePositions();
      String s = "";
      boolean lht = false;

      for(int i = ((TextareaVec)range.key).row; i <= ((TextareaVec)range.value).row; ++i) {
         String l = this.getLine(i);
         if (l.startsWith("\t")) {
            if (i == ((TextareaVec)range.value).row) {
               lht = true;
            }

            s = s + l.substring(1);
         } else {
            s = s + l;
         }
      }

      if (sel.getCursorOffset() < sel.getAnchorOffset()) {
         this.textarea.onContentChange(new TextareaChange(this.textarea, s, this.getLineOffset(((TextareaVec)range.key).row), this.getLineOffset(((TextareaVec)range.value).row) + this.getLine(((TextareaVec)range.value).row).length(), ((TextareaVec)range.key).row, ((TextareaVec)range.key).column - (((TextareaVec)range.key).column == 0 ? 0 : 1), ((TextareaVec)range.value).row, ((TextareaVec)range.value).column - (lht ? 1 : 0)));
      } else {
         this.textarea.onContentChange(new TextareaChange(this.textarea, s, this.getLineOffset(((TextareaVec)range.key).row), this.getLineOffset(((TextareaVec)range.value).row) + this.getLine(((TextareaVec)range.value).row).length(), ((TextareaVec)range.value).row, ((TextareaVec)range.value).column - (lht ? 1 : 0), ((TextareaVec)range.key).row, ((TextareaVec)range.key).column - (((TextareaVec)range.key).column == 0 ? 0 : 1)));
      }

   }

   protected int clampOffset(int pos, int max) {
      return this.clampOffset(0, pos, max);
   }

   protected int clampOffset(int min, int pos, int max) {
      return Math.max(min, Math.min(pos, max));
   }

   protected void reflowText() {
      this.lines.clear();
      this.offsets.clear();
      IWordWrapAlgorithm wwa = this.textarea.getWordWrapAlgorithm();
      ((Stream)wwa.wrap(this.text.toString(), this.textarea.width - this.textarea.getContentOffset() * 2, this.textarea.getWordMatcher()).sequential()).reduce(Integer.valueOf(0), (offset, line) -> {
         this.lines.add(line);
         this.offsets.add(offset);
         return offset.intValue() + line.length();
      }, (l, r) -> {
         return l.intValue() + r.intValue();
      });
   }

   protected void reflowText(int fromRow) {
      if (fromRow >= 0 && fromRow < this.lines.size()) {
         int offset = ((Integer)this.offsets.get(fromRow)).intValue();
         this.lines.removeRange(fromRow, this.lines.size());
         this.offsets.removeRange(fromRow, this.lines.size());
         IWordWrapAlgorithm wwa = this.textarea.getWordWrapAlgorithm();
         ((Stream)wwa.wrap(this.text.substring(offset), this.textarea.width - this.textarea.getContentOffset() * 2, this.textarea.getWordMatcher()).sequential()).reduce(Integer.valueOf(offset), (o, line) -> {
            this.lines.add(line);
            this.offsets.add(o);
            return o.intValue() + line.length();
         }, (l, r) -> {
            return l.intValue() + r.intValue();
         });
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   protected void doContentChanged(TextareaChange change) {
      this.text = change.getNewText();
      this.reflowText();
   }

   private static class ShittyList<E> extends ArrayList<E> {
      private ShittyList() {
      }

      public void removeRange(int fromIndex, int toIndex) {
         super.removeRange(fromIndex, toIndex);
      }
   }
}
