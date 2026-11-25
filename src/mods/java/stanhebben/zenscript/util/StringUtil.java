package stanhebben.zenscript.util;

import java.util.ArrayList;
import java.util.List;
import stanhebben.zenscript.expression.Expression;
import stanhebben.zenscript.type.natives.IJavaMethod;

public class StringUtil {
   private StringUtil() {
   }

   public static String join(String[] values, String separator) {
      StringBuilder result = new StringBuilder();
      boolean first = true;

      for(String value : values) {
         if (first) {
            first = false;
         } else {
            result.append(separator);
         }

         result.append(value);
      }

      return result.toString();
   }

   public static String join(Iterable<String> values, String separator) {
      StringBuilder result = new StringBuilder();
      boolean first = true;

      for(String value : values) {
         if (first) {
            first = false;
         } else {
            result.append('.');
         }

         result.append(value);
      }

      return result.toString();
   }

   public static String methodMatchingError(List<IJavaMethod> methods, Expression... arguments) {
      if (methods.isEmpty()) {
         return "\u00a7cno method with that name available";
      } else {
         StringBuilder message = new StringBuilder();
         if (methods.size() == 1) {
            message.append("a method ");
         } else {
            message.append(methods.size()).append(" methods ");
         }

         message.append("available but \u00a74none\u00a7r matches the parameters (");
         boolean first = true;

         for(Expression value : arguments) {
            if (first) {
               first = false;
            } else {
               message.append(", ");
            }

            message.append(value.getType().toString());
         }

         message.append(")");
         message.append("\nThis is \u00a7ousually\u00a7r an error in your script, not in the mod");

         for(IJavaMethod meth : methods) {
            message.append(meth.getErrorDescription());
         }

         return message.toString();
      }
   }

   public static String[] split(String value, char delimiter) {
      if (value == null) {
         return null;
      } else {
         ArrayList<String> result = new ArrayList();
         int start = 0;

         for(int i = 0; i < value.length(); ++i) {
            if (value.charAt(i) == delimiter) {
               result.add(value.substring(start, i));
               start = i + 1;
            }
         }

         result.add(value.substring(start));
         return (String[])result.toArray(new String[result.size()]);
      }
   }

   public static String[] splitParagraphs(String value) {
      String[] lines = split(value, '\n');

      for(int i = 0; i < lines.length; ++i) {
         lines[i] = lines[i].trim();
      }

      ArrayList<String> output = new ArrayList();
      StringBuilder current = new StringBuilder();

      for(String line : lines) {
         if (line.length() == 0) {
            if (current.length() > 0) {
               output.add(current.toString());
               current = new StringBuilder();
            }
         } else {
            current.append(' ').append(line);
         }
      }

      if (current.length() > 0) {
         output.add(current.toString());
      }

      return (String[])output.toArray(new String[output.size()]);
   }

   public static String unescapeString(String oldstr) {
      if ((oldstr.charAt(0) != '"' || oldstr.charAt(oldstr.length() - 1) != '"') && oldstr.charAt(0) == '\'' && oldstr.charAt(oldstr.length() - 1) != '\'') {
         ;
      }

      oldstr = oldstr.substring(1, oldstr.length() - 1);
      StringBuilder newstr = new StringBuilder(oldstr.length());
      boolean saw_backslash = false;

      for(int i = 0; i < oldstr.length(); ++i) {
         int cp = oldstr.codePointAt(i);
         if (oldstr.codePointAt(i) > 65535) {
            ++i;
         }

         if (!saw_backslash) {
            if (cp == 92) {
               saw_backslash = true;
            } else {
               newstr.append(Character.toChars(cp));
            }
         } else {
            switch(cp) {
            case 34:
            case 39:
               newstr.append(Character.toChars(cp));
               break;
            case 35:
            case 36:
            case 37:
            case 38:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
            case 93:
            case 94:
            case 95:
            case 96:
            case 100:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 111:
            case 112:
            case 113:
            case 115:
            case 118:
            case 119:
            default:
               newstr.append('\\');
               newstr.append(Character.toChars(cp));
               break;
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
               --i;
            case 48:
               if (i + 1 == oldstr.length()) {
                  newstr.append(Character.toChars(0));
               } else {
                  ++i;
                  int digits = 0;

                  for(int j = 0; j <= 2 && i + j != oldstr.length(); ++j) {
                     int ch = oldstr.charAt(i + j);
                     if (ch < 48 || ch > 55) {
                        break;
                     }

                     ++digits;
                  }

                  if (digits == 0) {
                     --i;
                     newstr.append('\u0000');
                  } else {
                     int value = 0;

                     try {
                        value = Integer.parseInt(oldstr.substring(i, i + digits), 8);
                     } catch (NumberFormatException var12) {
                        ;
                     }

                     newstr.append(Character.toChars(value));
                     i += digits - 1;
                  }
               }
               break;
            case 85:
               if (i + 8 > oldstr.length()) {
                  ;
               }

               ++i;

               int j;
               for(j = 0; j < 8; ++j) {
                  if (oldstr.charAt(i + j) > 127) {
                     ;
                  }
               }

               int value = 0;

               try {
                  value = Integer.parseInt(oldstr.substring(i, i + j), 16);
               } catch (NumberFormatException var9) {
                  ;
               }

               newstr.append(Character.toChars(value));
               i += j - 1;
               break;
            case 92:
               newstr.append('\\');
               break;
            case 97:
               newstr.append('\u0007');
               break;
            case 98:
               newstr.append("\\b");
               break;
            case 99:
               ++i;
               if (i == oldstr.length()) {
                  ;
               }

               cp = oldstr.codePointAt(i);
               if (cp > 127) {
                  ;
               }

               newstr.append(Character.toChars(cp ^ 64));
               break;
            case 101:
               newstr.append('\u001b');
               break;
            case 102:
               newstr.append('\f');
               break;
            case 110:
               newstr.append('\n');
               break;
            case 114:
               newstr.append('\r');
               break;
            case 116:
               newstr.append('\t');
               break;
            case 117:
               if (i + 4 > oldstr.length()) {
                  ;
               }

               ++i;

               int j;
               for(j = 0; j < 4; ++j) {
                  if (oldstr.charAt(i + j) > 127) {
                     ;
                  }
               }

               int value = 0;

               try {
                  value = Integer.parseInt(oldstr.substring(i, i + j), 16);
               } catch (NumberFormatException var10) {
                  ;
               }

               newstr.append(Character.toChars(value));
               i += j - 1;
               break;
            case 120:
               if (i + 2 > oldstr.length()) {
                  ;
               }

               ++i;
               boolean saw_brace = false;
               if (oldstr.charAt(i) == '{') {
                  ++i;
                  saw_brace = true;
               }

               int j;
               for(j = 0; j < 8 && (saw_brace || j != 2); ++j) {
                  int ch = oldstr.charAt(i + j);
                  if (ch > 127) {
                     ;
                  }

                  if (saw_brace && ch == 125) {
                     break;
                  }

                  if ((ch < 48 || ch > 57) && (ch < 97 || ch > 102) && ch >= 65 && ch > 70) {
                     ;
                  }
               }

               if (j == 0) {
                  ;
               }

               int value = 0;

               try {
                  value = Integer.parseInt(oldstr.substring(i, i + j), 16);
               } catch (NumberFormatException var11) {
                  ;
               }

               newstr.append(Character.toChars(value));
               if (saw_brace) {
                  ++j;
               }

               i += j - 1;
            }

            saw_backslash = false;
         }
      }

      if (saw_backslash) {
         newstr.append('\\');
      }

      return newstr.toString();
   }

   private static String uniplus(String s) {
      if (s.length() == 0) {
         return "";
      } else {
         StringBuilder sb = new StringBuilder(2 + 3 * s.length());
         sb.append("U+");

         for(int i = 0; i < s.length(); ++i) {
            sb.append(String.format("%X", s.codePointAt(i)));
            if (s.codePointAt(i) > 65535) {
               ++i;
            }

            if (i + 1 < s.length()) {
               sb.append(".");
            }
         }

         return sb.toString();
      }
   }

   public static String capitalize(String string) {
      return string.substring(0, 1).toUpperCase() + string.substring(1);
   }
}
