package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.MonoASCIIFontRenderer;
import com.fantasticsource.mctools.Render;
import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.Namespace;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.text.filter.TextFilter;
import com.fantasticsource.mctools.gui.element.view.GUIPanZoomView;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraftforge.common.MinecraftForge;

public class GUITextInput extends GUIText {
   protected String namespace;
   protected static final Color T_RED = Color.RED.copy().setAF(0.4F);
   protected int cursorPosition;
   protected int selectorPosition;
   protected long cursorTime;
   public TextFilter filter;
   protected long lastClickTime;
   protected int clicks;
   protected int lastAbsMouseX;

   public GUITextInput(GUIScreen screen, String text, TextFilter filter) {
      this(screen, text, filter, Color.WHITE, 1.0D);
   }

   public GUITextInput(GUIScreen screen, String text, TextFilter filter, double scale) {
      this(screen, text, filter, Color.WHITE, scale);
   }

   public GUITextInput(GUIScreen screen, String text, TextFilter filter, Color activeColor) {
      this(screen, text, filter, activeColor, 1.0D);
   }

   public GUITextInput(GUIScreen screen, String text, TextFilter filter, Color activeColor, double scale) {
      super(screen, text, GUIScreen.getIdleColor(activeColor), GUIScreen.getHoverColor(activeColor), activeColor, scale);
      this.namespace = null;
      this.selectorPosition = -1;
      this.clicks = 1;
      this.filter = filter;
      this.cursorPosition = text.length();
   }

   public GUITextInput(GUIScreen screen, double x, double y, String text, TextFilter filter) {
      this(screen, x, y, text, filter, Color.WHITE, 1.0D);
   }

   public GUITextInput(GUIScreen screen, double x, double y, String text, TextFilter filter, double scale) {
      this(screen, x, y, text, filter, Color.WHITE, scale);
   }

   public GUITextInput(GUIScreen screen, double x, double y, String text, TextFilter filter, Color activeColor) {
      this(screen, x, y, text, filter, activeColor, 1.0D);
   }

   public GUITextInput(GUIScreen screen, double x, double y, String text, TextFilter filter, Color activeColor, double scale) {
      super(screen, x, y, text, GUIScreen.getIdleColor(activeColor), GUIScreen.getHoverColor(activeColor), activeColor, scale);
      this.namespace = null;
      this.selectorPosition = -1;
      this.clicks = 1;
      this.filter = filter;
      this.cursorPosition = text.length();
   }

   public GUITextInput setNamespace(String namespace) {
      if (this.namespace != null) {
         ((Namespace)this.screen.namespaces.get(this.namespace)).inputs.remove(this);
      }

      this.namespace = namespace;
      ((Namespace)this.screen.namespaces.computeIfAbsent(namespace, (o) -> {
         return new Namespace();
      })).inputs.add(this);
      return this;
   }

   protected boolean hasSelectedText() {
      return this.selectorPosition != -1 && this.selectorPosition != this.cursorPosition;
   }

   protected int charType(char c) {
      if (Character.isWhitespace(c)) {
         return 0;
      } else {
         return !Character.isLetterOrDigit(c) && c != '_' ? -1 : 1;
      }
   }

   protected boolean isWhitespace() {
      for(char c : this.text.toCharArray()) {
         if (c != ' ') {
            return false;
         }
      }

      return true;
   }

   protected int nonWhitespaceStart() {
      int i = 0;

      for(char c : this.text.toCharArray()) {
         if (c != ' ') {
            break;
         }

         ++i;
      }

      return i;
   }

   protected int nonWhitespaceEnd() {
      char[] chars = this.text.toCharArray();

      int i;
      for(i = chars.length; i > 0 && chars[i - 1] == ' '; --i) {
         ;
      }

      return i;
   }

   protected int tabs() {
      if (!(this.parent instanceof CodeInput)) {
         return 0;
      } else {
         int tabbing = 0;

         for(int index = 0; index < this.parent.size(); ++index) {
            GUITextInput element = (GUITextInput)this.parent.get(index);
            if (element == this) {
               return Tools.max(0, tabbing);
            }

            for(char c : element.text.toCharArray()) {
               if (c == '{') {
                  ++tabbing;
               } else if (c == '}') {
                  --tabbing;
               }
            }
         }

         throw new IllegalStateException("This should be impossible");
      }
   }

   protected void deselectAll() {
      if (this.parent instanceof CodeInput) {
         ((CodeInput)this.parent).selectionStartY = -1;

         for(GUIElement element : this.parent.children) {
            ((GUITextInput)element).selectorPosition = -1;
         }
      } else {
         this.selectorPosition = -1;
      }

   }

   protected void singleLineHome() {
      int startPos = this.isWhitespace() ? Tools.min(this.text.length(), this.tabs()) : this.nonWhitespaceStart();
      if (this.cursorPosition == startPos) {
         startPos = 0;
      }

      if (GUIScreen.isShiftKeyDown()) {
         if (this.selectorPosition == -1 && this.cursorPosition != startPos) {
            this.selectorPosition = this.cursorPosition;
         }
      } else {
         this.deselectAll();
      }

      this.cursorPosition = startPos;
      if (this.parent instanceof CodeInput) {
         ((CodeInput)this.parent).cursorX = this.cursorPosition;
      }

   }

   protected void singleLineEnd() {
      int endPos = this.isWhitespace() ? Tools.min(this.text.length(), this.tabs()) : this.nonWhitespaceEnd();
      if (this.cursorPosition == endPos) {
         endPos = this.text.length();
      }

      if (GUIScreen.isShiftKeyDown()) {
         if (this.selectorPosition == -1 && this.cursorPosition != endPos) {
            this.selectorPosition = this.cursorPosition;
         }
      } else {
         this.deselectAll();
      }

      this.cursorPosition = endPos;
      if (this.parent instanceof CodeInput) {
         ((CodeInput)this.parent).cursorX = this.cursorPosition;
      }

   }

   public boolean valid() {
      if (this.filter != null && this.filter.acceptable(this.text)) {
         if (this.namespace != null) {
            return !((Namespace)this.screen.namespaces.get(this.namespace)).containsIgnoreObject(this.getText(), this);
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   protected GUITextInput multilineDelete() {
      if (this.parent instanceof CodeInput && ((CodeInput)this.parent).selectionStartY != -1 && ((CodeInput)this.parent).selectionStartY != this.parent.indexOf(this)) {
         CodeInput code = (CodeInput)this.parent;
         int index = this.parent.indexOf(this);
         int firstY = Tools.min(index, code.selectionStartY);
         int lastY = Tools.max(index, code.selectionStartY);
         GUITextInput element = (GUITextInput)code.get(lastY);
         String s = element.text.substring(Tools.max(element.cursorPosition, element.selectorPosition));
         element = (GUITextInput)code.get(firstY);
         int nextCursorPos = element.selectorPosition == -1 ? element.cursorPosition : Tools.min(element.selectorPosition, element.cursorPosition);
         element.setText(element.text.substring(0, nextCursorPos) + s);
         this.setActive(false);

         for(int i = lastY - firstY; i > 0; --i) {
            code.remove(firstY + 1);
         }

         element.setActive(true);
         element.selectorPosition = -1;
         element.cursorPosition = nextCursorPos;
         code.cursorX = nextCursorPos;
         code.selectionStartY = -1;
         return element;
      } else {
         return null;
      }
   }

   protected GUITextInput activeLine() {
      if (this.parent instanceof CodeInput) {
         for(GUIElement element : this.parent.children) {
            if (element.isActive()) {
               return (GUITextInput)element;
            }
         }
      }

      return this.isActive() ? this : null;
   }

   public void keyTyped(char typedChar, int keyCode) {
      super.keyTyped(typedChar, keyCode);
      if (this.active) {
         String oldText = this.text;
         CodeInput code = this.parent instanceof CodeInput ? (CodeInput)this.parent : null;
         if (keyCode == 28) {
            if (this instanceof GUIMultilineTextInput) {
               GUITextInput element = this.multilineDelete();
               if (element == null) {
                  element = this;
               }

               int min = Tools.min(element.cursorPosition, element.selectorPosition);
               if (min == -1) {
                  min = element.cursorPosition;
               }

               String before = element.text.substring(0, min);
               String after = element.text.substring(Tools.max(element.cursorPosition, element.selectorPosition));
               element.setText(before + "\n" + after);
               this.deselectAll();
               element.cursorPosition = min + 1;
            } else if (code != null) {
               GUITextInput element = this.multilineDelete();
               if (element == null) {
                  element = this;
               }

               int min = element.selectorPosition == -1 ? element.cursorPosition : Tools.min(element.cursorPosition, element.selectorPosition);
               String before = element.text.substring(0, min);
               String after = element.text.substring(Tools.max(element.cursorPosition, element.selectorPosition));
               element.setText(before);
               this.deselectAll();
               element.cursorPosition = min;
               element.setActive(false);
               int tabs = element.tabs();

               for(char c : before.toCharArray()) {
                  if (c == '{') {
                     ++tabs;
                  } else if (c == '}') {
                     --tabs;
                  }
               }

               String afterTrimmed = after.trim();
               if (afterTrimmed.length() > 0 && afterTrimmed.charAt(0) == '}') {
                  --tabs;
               }

               tabs = Tools.max(0, tabs);
               StringBuilder tabbing = new StringBuilder();

               for(int i = tabs; i > 0; --i) {
                  tabbing.append(" ");
               }

               element = (GUITextInput)code.add(code.indexOf(element) + 1, tabbing + afterTrimmed);
               element.setActive(true);
               element.cursorPosition = tabs;
               code.cursorX = tabs;
            }
         } else if (keyCode == 199) {
            if (this instanceof GUIMultilineTextInput) {
               if (GUIScreen.isShiftKeyDown()) {
                  if (this.selectorPosition == -1) {
                     this.selectorPosition = this.cursorPosition;
                  }
               } else {
                  this.selectorPosition = -1;
               }

               if (GUIScreen.isCtrlKeyDown()) {
                  this.cursorPosition = 0;
               } else {
                  int position = 0;

                  for(String fullLine : this.fullLines) {
                     position += fullLine.length();
                     if (position >= this.cursorPosition) {
                        this.cursorPosition = position - fullLine.length();
                        if (fullLine.length() > 0 && fullLine.charAt(0) == '\n') {
                           ++this.cursorPosition;
                        }
                        break;
                     }
                  }
               }
            } else {
               int index = this.parent.indexOf(this);
               if (code != null && index != 0 && GUIScreen.isCtrlKeyDown()) {
                  GUITextInput first = (GUITextInput)code.get(0);
                  if (GUIScreen.isShiftKeyDown()) {
                     if (code.selectionStartY == -1) {
                        code.selectionStartY = index;
                     }

                     for(int i = 0; i < this.parent.size(); ++i) {
                        GUITextInput element = (GUITextInput)this.parent.get(i);
                        if (i > code.selectionStartY) {
                           element.selectorPosition = -1;
                        } else if (i < code.selectionStartY) {
                           element.selectorPosition = element.text.length();
                           element.cursorPosition = 0;
                        } else {
                           if (element.selectorPosition == -1) {
                              element.selectorPosition = element.cursorPosition;
                           }

                           element.cursorPosition = 0;
                        }
                     }
                  } else {
                     this.deselectAll();
                     first.cursorPosition = 0;
                  }

                  this.setActive(false);
                  first.setActive(true);
                  code.cursorX = first.cursorPosition;
               } else {
                  this.singleLineHome();
               }
            }
         } else if (keyCode == 207) {
            if (this instanceof GUIMultilineTextInput) {
               if (GUIScreen.isShiftKeyDown()) {
                  if (this.selectorPosition == -1) {
                     this.selectorPosition = this.cursorPosition;
                  }
               } else {
                  this.selectorPosition = -1;
               }

               if (GUIScreen.isCtrlKeyDown()) {
                  this.cursorPosition = this.text.length();
               } else {
                  int position = 0;

                  for(int i = 0; i < this.fullLines.size(); ++i) {
                     String fullLine = (String)this.fullLines.get(i);
                     position += fullLine.length();
                     if (position >= this.cursorPosition) {
                        if (this.cursorPosition == position && this.cursorPosition < this.text.length() && this.text.charAt(this.cursorPosition) != '\n') {
                           this.cursorPosition += ((String)this.fullLines.get(i + 1)).length();
                        } else {
                           this.cursorPosition = position;
                        }
                        break;
                     }
                  }
               }
            } else {
               int index = this.parent.indexOf(this);
               if (code != null && index != this.parent.size() - 1 && GUIScreen.isCtrlKeyDown()) {
                  GUITextInput last = (GUITextInput)code.get(code.size() - 1);
                  if (GUIScreen.isShiftKeyDown()) {
                     if (code.selectionStartY == -1) {
                        code.selectionStartY = index;
                     }

                     for(int i = 0; i < this.parent.size(); ++i) {
                        GUITextInput element = (GUITextInput)this.parent.get(i);
                        if (i < code.selectionStartY) {
                           element.selectorPosition = -1;
                        } else if (i > code.selectionStartY) {
                           element.selectorPosition = 0;
                           element.cursorPosition = element.text.length();
                        } else {
                           if (element.selectorPosition == -1) {
                              element.selectorPosition = element.cursorPosition;
                           }

                           element.cursorPosition = element.text.length();
                        }
                     }
                  } else {
                     this.deselectAll();
                     last.cursorPosition = last.text.length();
                  }

                  this.setActive(false);
                  last.setActive(true);
                  code.cursorX = last.cursorPosition;
               } else {
                  this.singleLineEnd();
               }
            }
         } else if (GUIScreen.isCtrlKeyDown() && keyCode == 30) {
            if (code != null) {
               GUITextInput last = (GUITextInput)code.get(code.size() - 1);

               for(GUIElement e : code.children) {
                  GUITextInput element = (GUITextInput)e;
                  if (element.text.length() > 0) {
                     element.selectorPosition = 0;
                     element.cursorPosition = element.text.length();
                  }
               }

               this.setActive(false);
               last.setActive(true);
               code.selectionStartY = 0;
               code.cursorX = last.cursorPosition;
            } else if (this.text.length() > 0) {
               this.selectorPosition = 0;
               this.cursorPosition = this.text.length();
            }
         } else if (GUIScreen.isCtrlKeyDown() && keyCode == 45) {
            StringBuilder s = new StringBuilder();
            if (code != null && ((CodeInput)this.parent).selectionStartY != -1 && ((CodeInput)this.parent).selectionStartY != this.parent.indexOf(this)) {
               int startY = Tools.min(code.indexOf(this), code.selectionStartY);
               int endY = Tools.max(code.indexOf(this), code.selectionStartY);
               GUITextInput element = (GUITextInput)code.get(startY);
               s.append(element.text.substring(element.selectorPosition == -1 ? element.cursorPosition : Tools.min(element.cursorPosition, element.selectorPosition)));

               for(int i = startY + 1; i < endY; ++i) {
                  s.append("\n").append(((GUITextInput)code.get(i)).text);
               }

               element = (GUITextInput)code.get(endY);
               s.append("\n").append(element.text, 0, Tools.max(element.cursorPosition, element.selectorPosition));
               this.multilineDelete();
            } else if (this.hasSelectedText()) {
               int min = Tools.min(this.selectorPosition, this.cursorPosition);
               s.append(this.text, min, Tools.max(this.cursorPosition, this.selectorPosition));
               this.text = this.text.substring(0, min) + this.text.substring(Tools.max(this.selectorPosition, this.cursorPosition));
               this.deselectAll();
               this.cursorPosition = min;
               if (code != null) {
                  ((CodeInput)this.parent).cursorX = this.cursorPosition;
               }
            }

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s.toString().replaceAll("\n", "\r\n")), (ClipboardOwner)null);
         } else if (GUIScreen.isCtrlKeyDown() && keyCode == 46) {
            StringBuilder s = new StringBuilder();
            if (code != null && ((CodeInput)this.parent).selectionStartY != -1 && ((CodeInput)this.parent).selectionStartY != this.parent.indexOf(this)) {
               int startY = Tools.min(code.indexOf(this), code.selectionStartY);
               int endY = Tools.max(code.indexOf(this), code.selectionStartY);
               GUITextInput element = (GUITextInput)code.get(startY);
               s.append(element.text.substring(element.selectorPosition == -1 ? element.cursorPosition : Tools.min(element.cursorPosition, element.selectorPosition)));

               for(int i = startY + 1; i < endY; ++i) {
                  s.append("\n").append(((GUITextInput)code.get(i)).text);
               }

               element = (GUITextInput)code.get(endY);
               s.append("\n").append(element.text, 0, Tools.max(element.cursorPosition, element.selectorPosition));
            } else if (this.hasSelectedText()) {
               s = new StringBuilder(this.text.substring(Tools.min(this.cursorPosition, this.selectorPosition), Tools.max(this.cursorPosition, this.selectorPosition)));
            }

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s.toString().replaceAll("\n", "\r\n")), (ClipboardOwner)null);
         } else if (GUIScreen.isCtrlKeyDown() && keyCode == 47) {
            String clipboard = GUIScreen.getClipboardString().replaceAll("\r", "");
            String[] tokens = Tools.fixedSplit(clipboard, "\n");
            GUITextInput element = this.multilineDelete();
            if (element == null) {
               element = this;
            }

            if (code != null && tokens.length > 1) {
               int min = element.selectorPosition == -1 ? element.cursorPosition : Tools.min(element.cursorPosition, element.selectorPosition);
               String before = element.text.substring(0, min);
               String after = element.text.substring(Tools.max(element.cursorPosition, element.selectorPosition));
               element.setText(before + tokens[0]);
               element.setActive(false);
               int index = code.indexOf(element) + 1;

               for(int i = 1; i < tokens.length - 1; ++i) {
                  code.add(index++, tokens[i]);
               }

               before = tokens[tokens.length - 1];
               element = (GUITextInput)code.add(index, before + after);
               this.deselectAll();
               element.setActive(true);
               element.cursorPosition = before.length();
               code.cursorX = element.cursorPosition;
               code.selectionStartY = -1;
            } else {
               int min = Tools.min(element.cursorPosition, element.selectorPosition);
               if (min == -1) {
                  min = element.cursorPosition;
               }

               String before = element.text.substring(0, min) + clipboard;
               element.setText(before + element.text.substring(Tools.max(element.cursorPosition, element.selectorPosition)));
               this.deselectAll();
               element.cursorPosition = before.length();
            }
         } else if (typedChar >= ' ' && typedChar <= '~') {
            GUITextInput element = this.multilineDelete();
            if (element == null) {
               element = this;
            }

            int min = Tools.min(element.cursorPosition, element.selectorPosition);
            if (min == -1) {
               min = element.cursorPosition;
            }

            String before = element.text.substring(0, min);
            String after = element.text.substring(Tools.max(element.cursorPosition, element.selectorPosition));
            if (code != null && typedChar == '}' && element.isWhitespace()) {
               StringBuilder s = new StringBuilder();

               for(int i = this.tabs() - 1; i > 0; --i) {
                  s.append(" ");
               }

               element.setText(s.toString() + '}');
               this.deselectAll();
               element.cursorPosition = element.text.length();
            } else {
               element.setText(before + typedChar + after);
               this.deselectAll();
               element.cursorPosition = min + 1;
            }

            if (code != null) {
               code.cursorX = element.cursorPosition;
               code.selectionStartY = -1;
            }
         } else if (keyCode == 14) {
            if (this.multilineDelete() == null) {
               if (this.hasSelectedText()) {
                  int min = Tools.min(this.selectorPosition, this.cursorPosition);
                  this.text = this.text.substring(0, min) + this.text.substring(Tools.max(this.selectorPosition, this.cursorPosition));
                  this.deselectAll();
                  this.cursorPosition = min;
               } else if (this.cursorPosition > 0) {
                  this.text = this.text.substring(0, this.cursorPosition - 1) + this.text.substring(this.cursorPosition);
                  --this.cursorPosition;
               } else if (code != null) {
                  int index = this.parent.indexOf(this);
                  if (index != 0) {
                     GUITextInput other = (GUITextInput)this.parent.get(index - 1);
                     this.text = other.text + this.text;
                     this.cursorPosition = other.text.length();
                     this.parent.remove(index - 1);
                  }
               }

               if (code != null) {
                  code.cursorX = this.cursorPosition;
                  code.selectionStartY = -1;
               }
            }
         } else if (keyCode == 211) {
            if (this.multilineDelete() == null) {
               if (this.hasSelectedText()) {
                  int min = Tools.min(this.selectorPosition, this.cursorPosition);
                  this.text = this.text.substring(0, min) + this.text.substring(Tools.max(this.selectorPosition, this.cursorPosition));
                  this.deselectAll();
                  this.cursorPosition = min;
               } else if (this.cursorPosition < this.text.length()) {
                  this.text = this.text.substring(0, this.cursorPosition) + this.text.substring(this.cursorPosition + 1);
               } else if (code != null) {
                  int index = this.parent.indexOf(this);
                  if (index != this.parent.size() - 1) {
                     this.text = this.text + ((GUITextInput)this.parent.get(index + 1)).text;
                     this.parent.remove(index + 1);
                  }
               }

               if (code != null) {
                  code.cursorX = this.cursorPosition;
                  code.selectionStartY = -1;
               }
            }
         } else if (keyCode == 203) {
            if (GUIScreen.isShiftKeyDown()) {
               if (this.selectorPosition == -1 && this.cursorPosition > 0) {
                  this.selectorPosition = this.cursorPosition;
               }
            } else {
               this.deselectAll();
            }

            if (this.cursorPosition > 0) {
               int type = this.charType(this.text.charAt(this.cursorPosition - 1));
               --this.cursorPosition;
               if (type != -1 && GUIScreen.isCtrlKeyDown()) {
                  while(this.cursorPosition > 0 && this.charType(this.text.charAt(this.cursorPosition - 1)) == type) {
                     --this.cursorPosition;
                  }
               }

               if (code != null) {
                  ((CodeInput)this.parent).cursorX = this.cursorPosition;
               }
            } else if (code != null) {
               int index = code.indexOf(this);
               if (index != 0) {
                  GUITextInput other = (GUITextInput)code.get(index - 1);
                  if (GUIScreen.isShiftKeyDown() && code.selectionStartY == -1) {
                     code.selectionStartY = index;
                     other.selectorPosition = -1;
                  }

                  this.setActive(false);
                  other.setActive(true);
                  other.cursorPosition = other.text.length();
                  code.cursorX = other.cursorPosition;
               } else {
                  code.cursorX = this.cursorPosition;
               }
            }
         } else if (keyCode == 205) {
            if (GUIScreen.isShiftKeyDown()) {
               if (this.selectorPosition == -1 && this.cursorPosition < this.text.length()) {
                  this.selectorPosition = this.cursorPosition;
               }
            } else {
               this.deselectAll();
            }

            if (this.cursorPosition < this.text.length()) {
               int type = this.charType(this.text.charAt(this.cursorPosition));
               ++this.cursorPosition;
               if (type != -1 && GUIScreen.isCtrlKeyDown()) {
                  while(this.cursorPosition < this.text.length() && this.charType(this.text.charAt(this.cursorPosition)) == type) {
                     ++this.cursorPosition;
                  }
               }

               if (code != null) {
                  ((CodeInput)this.parent).cursorX = this.cursorPosition;
               }
            } else if (code != null) {
               int index = code.indexOf(this);
               if (index != this.parent.size() - 1) {
                  GUITextInput other = (GUITextInput)code.get(index + 1);
                  if (GUIScreen.isShiftKeyDown() && code.selectionStartY == -1) {
                     code.selectionStartY = index;
                     other.selectorPosition = -1;
                  }

                  this.setActive(false);
                  other.setActive(true);
                  other.cursorPosition = 0;
                  code.cursorX = other.cursorPosition;
               } else {
                  code.cursorX = this.cursorPosition;
               }
            }
         } else if (keyCode == 200) {
            if (this instanceof GUIMultilineTextInput) {
               if (GUIScreen.isShiftKeyDown()) {
                  if (this.selectorPosition == -1) {
                     this.selectorPosition = this.cursorPosition;
                  }
               } else {
                  this.selectorPosition = -1;
               }

               int lineIndex = 0;
               int pos = 0;
               String partialLine = "";

               for(String fullLine : this.fullLines) {
                  pos += fullLine.length();
                  if (pos >= this.cursorPosition) {
                     partialLine = fullLine.substring(0, this.cursorPosition - (pos - fullLine.length()));
                     break;
                  }

                  ++lineIndex;
               }

               if (lineIndex == 0) {
                  this.cursorPosition = 0;
               } else {
                  this.cursorPosition = this.findCursorPosition(this.absoluteX() + (double)GUIScreen.FONT_RENDERER.getStringWidth(partialLine.replaceAll("\n", "")) * this.scale / (double)this.screen.width, lineIndex - 1);
               }
            } else if (code != null && this.parent.indexOf(this) > 0) {
               int index = code.indexOf(this);
               GUITextInput other = (GUITextInput)code.get(index - 1);
               if (GUIScreen.isShiftKeyDown()) {
                  if (code.selectionStartY == -1) {
                     code.selectionStartY = index;
                  }

                  if (code.selectionStartY > index) {
                     this.selectorPosition = this.text.length();
                     this.cursorPosition = 0;
                     other.selectorPosition = other.text.length();
                  } else if (code.selectionStartY < index) {
                     this.selectorPosition = -1;
                  } else {
                     if (this.selectorPosition == -1) {
                        this.selectorPosition = this.cursorPosition;
                     }

                     this.cursorPosition = 0;
                     other.selectorPosition = other.text.length();
                  }
               } else {
                  this.deselectAll();
               }

               this.setActive(false);
               other.setActive(true);
               other.cursorPosition = Tools.min(other.text.length(), code.cursorX);
            } else {
               this.singleLineHome();
            }
         } else if (keyCode == 208) {
            if (this instanceof GUIMultilineTextInput) {
               if (GUIScreen.isShiftKeyDown()) {
                  if (this.selectorPosition == -1) {
                     this.selectorPosition = this.cursorPosition;
                  }
               } else {
                  this.selectorPosition = -1;
               }

               int lineIndex = 0;
               int pos = 0;
               String partialLine = "";

               for(String fullLine : this.fullLines) {
                  pos += fullLine.length();
                  if (pos >= this.cursorPosition) {
                     partialLine = fullLine.substring(0, this.cursorPosition - (pos - fullLine.length()));
                     break;
                  }

                  ++lineIndex;
               }

               if (lineIndex == this.fullLines.size() - 1) {
                  this.cursorPosition = this.text.length();
               } else {
                  this.cursorPosition = this.findCursorPosition(this.absoluteX() + (double)GUIScreen.FONT_RENDERER.getStringWidth(partialLine.replaceAll("\n", "")) * this.scale / (double)this.screen.width, lineIndex + 1);
               }
            } else if (code != null && this.parent.indexOf(this) != this.parent.size() - 1) {
               int index = code.indexOf(this);
               GUITextInput other = (GUITextInput)code.get(index + 1);
               if (GUIScreen.isShiftKeyDown()) {
                  if (code.selectionStartY == -1) {
                     code.selectionStartY = index;
                  }

                  if (code.selectionStartY < index) {
                     this.selectorPosition = 0;
                     this.cursorPosition = this.text.length();
                     other.selectorPosition = 0;
                  } else if (code.selectionStartY > index) {
                     this.selectorPosition = -1;
                  } else {
                     if (this.selectorPosition == -1) {
                        this.selectorPosition = this.cursorPosition;
                     }

                     this.cursorPosition = this.text.length();
                     other.selectorPosition = 0;
                  }
               } else {
                  this.deselectAll();
               }

               this.setActive(false);
               other.setActive(true);
               other.cursorPosition = Tools.min(other.text.length(), code.cursorX);
            } else {
               this.singleLineEnd();
            }
         }

         if (code != null) {
            GUITextInput element = this.activeLine();
            if (element != null) {
               if (element.y < code.top) {
                  code.progress = element.y / (code.internalHeight - 1.0D);
               }

               if (element.y + element.height > code.bottom) {
                  code.progress = (element.y + element.height - 1.0D) / (code.internalHeight - 1.0D);
               }
            }
         }

         this.cursorTime = System.currentTimeMillis();
         this.screen.root.recalc(0);
         if (!this.text.equals(oldText)) {
            for(Runnable action : this.onEditActions) {
               action.run();
            }
         }

      }
   }

   public GUITextInput recalc(int subIndexChanged) {
      this.text = this.text.replaceAll("\r", "");
      this.lines.clear();
      this.fullLines.clear();
      if (this.parent instanceof CodeInput) {
         this.lines.add(this.text);
         this.fullLines.add(this.text);
         this.width = (double)MonoASCIIFontRenderer.getStringWidth(this.text) * this.scale / (double)this.screen.width;
         this.height = (double)(MonoASCIIFontRenderer.LINE_HEIGHT + 2) * this.scale / (double)this.screen.height;
      } else {
         StringBuilder previous = new StringBuilder();
         String[] words = Tools.preservedSplit(this.text, "[\n]|[ ]+", true);
         double parentW = this.parent == null ? 1.0D : this.parent.absoluteWidth();
         StringBuilder line = new StringBuilder();
         StringBuilder fullLine = new StringBuilder();
         int index = 0;
         double maxLineW = 1.0D / (double)this.screen.width;
         double lineW = 1.0D / (double)this.screen.width;

         while(index < words.length) {
            String word = words[index++];
            if (!word.equals("")) {
               if (word.equals("\n")) {
                  this.lines.add(line.toString());
                  this.fullLines.add(fullLine.toString());
                  line = new StringBuilder();
                  fullLine = new StringBuilder("\n");
                  maxLineW = 1.0D;
                  lineW = 1.0D / (double)this.screen.width;
               } else {
                  double wordW = (double)Render.getPartialStringWidth(previous.toString().replaceAll("\n", ""), word.replaceAll("\n", "")) * this.scale / (double)this.screen.width;
                  if (lineW + wordW > parentW) {
                     if (word.trim().equals("")) {
                        fullLine.append(word);
                        continue;
                     }

                     if (line.length() == 0) {
                        line.append(word);
                        fullLine.append(word);
                        lineW += wordW;
                     } else {
                        this.lines.add(line.toString());
                        this.fullLines.add(fullLine.toString());
                        line = new StringBuilder(word);
                        fullLine = new StringBuilder(word);
                        maxLineW = parentW;
                        lineW = (double)(Render.getPartialStringWidth(previous.toString().replaceAll("\n", ""), word.replaceAll("\n", "")) + 1) * this.scale / (double)this.screen.width;
                     }
                  } else {
                     line.append(word);
                     fullLine.append(word);
                     lineW += wordW;
                  }
               }

               previous.append(word);
            }
         }

         if (line.length() > 0) {
            this.lines.add(line.toString());
            maxLineW = Tools.max(maxLineW, lineW);
         }

         if (fullLine.length() > 0) {
            this.fullLines.add(fullLine.toString());
         }

         this.width = maxLineW;
         if (this instanceof GUIMultilineTextInput && this.text.length() > 0 && this.text.charAt(this.text.length() - 1) == '\n') {
            this.height = (double)(Tools.max(1, this.fullLines.size()) * GUIScreen.FONT_RENDERER.FONT_HEIGHT - 1) * this.scale / (double)this.screen.height;
         } else {
            this.height = (double)(Tools.max(1, this.lines.size()) * GUIScreen.FONT_RENDERER.FONT_HEIGHT - 1) * this.scale / (double)this.screen.height;
         }
      }

      if (this.parent != null) {
         this.width /= this.parent.absoluteWidth();
         this.height /= this.parent.absoluteHeight();
      }

      this.width += 1.0D * this.width / (double)this.screen.width;
      this.recalcAndRepositionSubElements(0);
      if (this.parent instanceof CodeInput) {
         this.width = Tools.max(this.width, 2.0D / (double)this.parent.absolutePxWidth());
      }

      this.postRecalc();
      return this;
   }

   public boolean isWithin(double x, double y) {
      if (!(this.parent instanceof CodeInput)) {
         return super.isWithin(x, y);
      } else {
         double yy = this.absoluteY();
         return yy <= y && y < yy + this.absoluteHeight();
      }
   }

   public boolean mousePressed(int button) {
      boolean result = false;
      if (button == 0 && this.isMouseWithin()) {
         result = true;
         this.setActive(true);
         long time = System.currentTimeMillis();
         int absMouseX = (int)(GUIScreen.mouseX * (double)this.screen.width);
         if (time - this.lastClickTime <= 250L && Math.abs(this.lastAbsMouseX - absMouseX) < 3) {
            ++this.clicks;
         } else {
            this.clicks = 1;
         }

         this.lastClickTime = time;
         if (this.clicks == 1) {
            this.lastAbsMouseX = absMouseX;
            if (GUIScreen.isShiftKeyDown()) {
               if (this.selectorPosition == -1) {
                  this.selectorPosition = this.cursorPosition;
               }
            } else {
               this.deselectAll();
            }

            this.cursorPosition = this.findCursorPosition(this.mouseX(), this.mouseY());
            if (this.parent instanceof CodeInput && ((CodeInput)this.parent).selectionStartY == -1) {
               ((CodeInput)this.parent).selectionStartY = this.parent.indexOf(this);
            }
         } else if (this.clicks != 2) {
            this.deselectAll();
            this.cursorPosition = this.text.length();
            this.selectorPosition = 0;
         } else {
            this.deselectAll();
            this.cursorPosition = this.findCursorPosition(this.mouseX(), this.mouseY());
            this.selectorPosition = this.cursorPosition;
            char[] chars = this.text.toCharArray();

            int type;
            for(type = this.charType(chars[this.cursorPosition == 0 ? 0 : this.cursorPosition - 1]); this.cursorPosition < chars.length && this.charType(chars[this.cursorPosition]) == type; ++this.cursorPosition) {
               ;
            }

            while(this.selectorPosition > 0 && this.charType(chars[this.selectorPosition - 1]) == type) {
               --this.selectorPosition;
            }

            if (this.selectorPosition == this.cursorPosition) {
               this.selectorPosition = -1;
            }
         }

         this.cursorTime = System.currentTimeMillis();
         if (this.parent instanceof CodeInput) {
            ((CodeInput)this.parent).cursorX = this.cursorPosition;
         }
      } else {
         this.setActive(false);
      }

      for(GUIElement child : (ArrayList)this.children.clone()) {
         result |= child.mousePressed(button);
      }

      return result;
   }

   public boolean mouseReleased(int button) {
      boolean result = button == 0 && this.active && this.isMouseWithin();
      if (result && !MinecraftForge.EVENT_BUS.post(new GUILeftClickEvent(this.screen, this))) {
         this.click();
      }

      for(GUIElement child : (ArrayList)this.children.clone()) {
         child.mouseReleased(button);
      }

      return result;
   }

   public void mouseDrag(int button) {
      if (button == 0 && (this.parent instanceof CodeInput && this.isMouseWithin() || !(this.parent instanceof CodeInput) && this.active)) {
         if (this.parent instanceof CodeInput && ((CodeInput)this.parent).selectionStartY != -1 && ((CodeInput)this.parent).selectionStartY != this.parent.indexOf(this)) {
            CodeInput code = (CodeInput)this.parent;
            int index = code.indexOf(this);
            if (code.selectionStartY < index) {
               GUITextInput element = (GUITextInput)code.get(code.selectionStartY);
               if (element.selectorPosition == -1) {
                  element.selectorPosition = element.cursorPosition;
               }

               element.cursorPosition = element.text.length();

               for(int i = code.selectionStartY + 1; i < index; ++i) {
                  element = (GUITextInput)code.get(i);
                  element.selectorPosition = 0;
                  element.cursorPosition = element.text.length();
               }

               this.selectorPosition = 0;

               for(int i = 0; i < code.selectionStartY; ++i) {
                  ((GUITextInput)code.get(i)).selectorPosition = -1;
               }

               for(int i = index + 1; i < code.size(); ++i) {
                  ((GUITextInput)code.get(i)).selectorPosition = -1;
               }
            } else {
               GUITextInput element = (GUITextInput)code.get(code.selectionStartY);
               if (element.selectorPosition == -1) {
                  element.selectorPosition = element.cursorPosition;
               }

               element.cursorPosition = 0;

               for(int i = code.selectionStartY - 1; i > index; --i) {
                  element = (GUITextInput)code.get(i);
                  element.selectorPosition = element.text.length();
                  element.cursorPosition = 0;
               }

               this.selectorPosition = this.text.length();

               for(int i = 0; i < index; ++i) {
                  ((GUITextInput)code.get(i)).selectorPosition = -1;
               }

               for(int i = code.selectionStartY + 1; i < code.size(); ++i) {
                  ((GUITextInput)code.get(i)).selectorPosition = -1;
               }
            }

            this.cursorPosition = this.findCursorPosition(this.mouseX(), this.mouseY());
         } else {
            if (this.selectorPosition == -1) {
               this.selectorPosition = this.cursorPosition;
            }

            this.cursorPosition = this.findCursorPosition(this.mouseX(), this.mouseY());
            if (this.selectorPosition == this.cursorPosition) {
               this.selectorPosition = -1;
            }

            int sp = this.selectorPosition;
            int cp = this.cursorPosition;
            this.deselectAll();
            this.selectorPosition = sp;
            this.cursorPosition = cp;
            if (this.parent instanceof CodeInput) {
               CodeInput code = (CodeInput)this.parent;
               code.selectionStartY = code.indexOf(this);
               code.cursorX = this.cursorPosition;
            }
         }

         GUIElement activeLine = this.activeLine();
         if (activeLine != null) {
            activeLine.setActive(false);
         }

         this.setActive(true);
      }

      this.cursorTime = System.currentTimeMillis();
      super.mouseDrag(button);
   }

   public void draw() {
      GlStateManager.disableTexture2D();
      double adjustedScale = this.scale * (double)(new ScaledResolution(Minecraft.getMinecraft())).getScaleFactor();
      if (this.parent instanceof GUIPanZoomView) {
         adjustedScale *= ((GUIPanZoomView)this.parent).getZoom();
      }

      if (!this.valid()) {
         GlStateManager.color(T_RED.rf(), T_RED.gf(), T_RED.bf(), T_RED.af());
         GlStateManager.glBegin(7);
         GlStateManager.glVertex3f(0.0F, 0.0F, 0.0F);
         GlStateManager.glVertex3f(0.0F, 1.0F, 0.0F);
         GlStateManager.glVertex3f(1.0F, 1.0F, 0.0F);
         GlStateManager.glVertex3f(1.0F, 0.0F, 0.0F);
         GlStateManager.glEnd();
      } else if (this.active) {
         GlStateManager.color(Color.GRAY.rf(), Color.GRAY.gf(), Color.GRAY.bf(), 0.2F);
         GlStateManager.glBegin(7);
         GlStateManager.glVertex3f(0.0F, 0.0F, 0.0F);
         GlStateManager.glVertex3f(0.0F, 1.0F, 0.0F);
         GlStateManager.glVertex3f(1.0F, 1.0F, 0.0F);
         GlStateManager.glVertex3f(1.0F, 0.0F, 0.0F);
         GlStateManager.glEnd();
      }

      if (this.text.length() > 0) {
         Color c = this.active ? this.activeColor : (this.isMouseWithin() ? this.hoverColor : this.color);
         int mcColor = c.color() >>> 8 | c.a() << 24;
         if ((mcColor & -67108864) != 0) {
            GlStateManager.enableTexture2D();
            GlStateManager.pushMatrix();
            GlStateManager.scale(adjustedScale / (double)this.absolutePxWidth(), adjustedScale / (double)this.absolutePxHeight(), 1.0D);
            if (this.parent instanceof CodeInput) {
               MonoASCIIFontRenderer.draw(this.text, 0.0D, 0.0D, c, Color.BLACK);
            } else {
               GUIScreen.FONT_RENDERER.drawString(this.text, 1.0F, 0.0F, mcColor, false);
            }

            GlStateManager.popMatrix();
            GlStateManager.disableTexture2D();
         }
      }

      if (this.active || this.parent instanceof CodeInput) {
         float cursorX = this.parent instanceof CodeInput ? (float)MonoASCIIFontRenderer.getStringWidth(this.text.substring(0, this.cursorPosition)) : (float)GUIScreen.FONT_RENDERER.getStringWidth(this.text.substring(0, this.cursorPosition).replaceAll("\n", "")) + 0.5F;
         float selectorX = this.selectorPosition == -1 ? cursorX : (float)(this.parent instanceof CodeInput ? MonoASCIIFontRenderer.getStringWidth(this.text.substring(0, this.selectorPosition)) : GUIScreen.FONT_RENDERER.getStringWidth(this.text.substring(0, this.selectorPosition).replaceAll("\n", ""))) + 0.5F;
         cursorX = Tools.max(cursorX, 1.0F / (float)this.absolutePxWidth() + 0.5F);
         cursorX = (float)((double)cursorX * (adjustedScale / (double)this.absolutePxWidth()));
         selectorX = (float)((double)selectorX * (adjustedScale / (double)this.absolutePxWidth()));
         if (this.selectorPosition != -1 && cursorX != selectorX) {
            float min = Tools.min(cursorX, selectorX);
            float max = Tools.max(cursorX, selectorX);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.3F);
            GlStateManager.glBegin(7);
            GlStateManager.glVertex3f(min, 0.0F, 0.0F);
            GlStateManager.glVertex3f(min, 1.0F, 0.0F);
            GlStateManager.glVertex3f(max, 1.0F, 0.0F);
            GlStateManager.glVertex3f(max, 0.0F, 0.0F);
            GlStateManager.glEnd();
         }

         if (this.active && (System.currentTimeMillis() - this.cursorTime) % 1000L < 500L) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.glBegin(1);
            GlStateManager.glVertex3f(cursorX, 0.0F, 0.0F);
            GlStateManager.glVertex3f(cursorX, 1.0F, 0.0F);
            GlStateManager.glEnd();
         }
      }

      this.drawChildren();
   }

   public void setActive(boolean active) {
      if (active && !this.active) {
         this.cursorTime = System.currentTimeMillis();
      }

      super.setActive(active);
   }

   protected int findCursorPosition(double absX, double absY) {
      return this.findCursorPosition(absX, (int)((absY - this.absoluteY()) / this.absoluteHeight() * (double)this.fullLines.size()));
   }

   protected int findCursorPosition(double absX, int lineIndex) {
      int result = 0;
      String line;
      if (this instanceof GUIMultilineTextInput) {
         if (lineIndex >= this.lines.size()) {
            return this.text.length();
         }

         line = (String)this.lines.get(lineIndex);
         String fullLine = (String)this.fullLines.get(lineIndex);
         if (fullLine.length() > 0 && fullLine.charAt(0) == '\n') {
            ++result;
         }

         for(int i = 0; i < lineIndex; ++i) {
            result += ((String)this.fullLines.get(i)).length();
         }
      } else {
         line = this.text;
      }

      double xDif = absX - this.absoluteX();

      for(char c : line.toCharArray()) {
         double lastDif = xDif;
         xDif -= (double)(this.parent instanceof CodeInput ? MonoASCIIFontRenderer.CHAR_WIDTH + 2 : GUIScreen.FONT_RENDERER.getCharWidth(c)) * this.scale / (double)this.screen.width;
         if (xDif <= 0.0D) {
            if (Math.abs(xDif) < lastDif) {
               ++result;
            }
            break;
         }

         ++result;
      }

      return result;
   }

   public String toString() {
      return this.valid() ? this.filter.transformInput(this.text) : "(INVALID INPUT)";
   }
}
