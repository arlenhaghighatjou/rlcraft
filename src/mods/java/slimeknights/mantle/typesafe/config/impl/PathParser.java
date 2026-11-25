package slimeknights.mantle.typesafe.config.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import slimeknights.mantle.typesafe.config.ConfigException;
import slimeknights.mantle.typesafe.config.ConfigOrigin;
import slimeknights.mantle.typesafe.config.ConfigSyntax;
import slimeknights.mantle.typesafe.config.ConfigValueType;

final class PathParser {
   static ConfigOrigin apiOrigin = SimpleConfigOrigin.newSimple("path parameter");

   PathParser() {
   }

   static ConfigNodePath parsePathNode(String path) {
      return parsePathNode(path, ConfigSyntax.CONF);
   }

   static ConfigNodePath parsePathNode(String path, ConfigSyntax flavor) {
      StringReader reader = new StringReader(path);

      ConfigNodePath var4;
      try {
         Iterator<Token> tokens = Tokenizer.tokenize(apiOrigin, reader, flavor);
         tokens.next();
         var4 = parsePathNodeExpression(tokens, apiOrigin, path, flavor);
      } finally {
         reader.close();
      }

      return var4;
   }

   static Path parsePath(String path) {
      Path speculated = speculativeFastParsePath(path);
      if (speculated != null) {
         return speculated;
      } else {
         StringReader reader = new StringReader(path);

         Path var4;
         try {
            Iterator<Token> tokens = Tokenizer.tokenize(apiOrigin, reader, ConfigSyntax.CONF);
            tokens.next();
            var4 = parsePathExpression(tokens, apiOrigin, path);
         } finally {
            reader.close();
         }

         return var4;
      }
   }

   protected static Path parsePathExpression(Iterator<Token> expression, ConfigOrigin origin) {
      return parsePathExpression(expression, origin, (String)null, (ArrayList)null, ConfigSyntax.CONF);
   }

   protected static Path parsePathExpression(Iterator<Token> expression, ConfigOrigin origin, String originalText) {
      return parsePathExpression(expression, origin, originalText, (ArrayList)null, ConfigSyntax.CONF);
   }

   protected static ConfigNodePath parsePathNodeExpression(Iterator<Token> expression, ConfigOrigin origin) {
      return parsePathNodeExpression(expression, origin, (String)null, ConfigSyntax.CONF);
   }

   protected static ConfigNodePath parsePathNodeExpression(Iterator<Token> expression, ConfigOrigin origin, String originalText, ConfigSyntax flavor) {
      ArrayList<Token> pathTokens = new ArrayList();
      Path path = parsePathExpression(expression, origin, originalText, pathTokens, flavor);
      return new ConfigNodePath(path, pathTokens);
   }

   protected static Path parsePathExpression(Iterator<Token> expression, ConfigOrigin origin, String originalText, ArrayList<Token> pathTokens, ConfigSyntax flavor) {
      List<PathParser.Element> buf = new ArrayList();
      buf.add(new PathParser.Element("", false));
      if (!expression.hasNext()) {
         throw new ConfigException.BadPath(origin, originalText, "Expecting a field name or path here, but got nothing");
      } else {
         while(expression.hasNext()) {
            Token t = (Token)expression.next();
            if (pathTokens != null) {
               pathTokens.add(t);
            }

            if (!Tokens.isIgnoredWhitespace(t)) {
               if (Tokens.isValueWithType(t, ConfigValueType.STRING)) {
                  AbstractConfigValue v = Tokens.getValue(t);
                  String s = v.transformToString();
                  addPathText(buf, true, s);
               } else if (t != Tokens.END) {
                  String text;
                  if (Tokens.isValue(t)) {
                     AbstractConfigValue v = Tokens.getValue(t);
                     if (pathTokens != null) {
                        pathTokens.remove(pathTokens.size() - 1);
                        pathTokens.addAll(splitTokenOnPeriod(t, flavor));
                     }

                     text = v.transformToString();
                  } else {
                     if (!Tokens.isUnquotedText(t)) {
                        throw new ConfigException.BadPath(origin, originalText, "Token not allowed in path expression: " + t + " (you can double-quote this token if you really want it here)");
                     }

                     if (pathTokens != null) {
                        pathTokens.remove(pathTokens.size() - 1);
                        pathTokens.addAll(splitTokenOnPeriod(t, flavor));
                     }

                     text = Tokens.getUnquotedText(t);
                  }

                  addPathText(buf, false, text);
               }
            }
         }

         PathBuilder pb = new PathBuilder();

         for(PathParser.Element e : buf) {
            if (e.sb.length() == 0 && !e.canBeEmpty) {
               throw new ConfigException.BadPath(origin, originalText, "path has a leading, trailing, or two adjacent period '.' (use quoted \"\" empty string if you want an empty element)");
            }

            pb.appendKey(e.sb.toString());
         }

         return pb.result();
      }
   }

   private static Collection<Token> splitTokenOnPeriod(Token t, ConfigSyntax flavor) {
      String tokenText = t.tokenText();
      if (tokenText.equals(".")) {
         return Collections.singletonList(t);
      } else {
         String[] splitToken = tokenText.split("\\.");
         ArrayList<Token> splitTokens = new ArrayList();

         for(String s : splitToken) {
            if (flavor == ConfigSyntax.CONF) {
               splitTokens.add(Tokens.newUnquotedText(t.origin(), s));
            } else {
               splitTokens.add(Tokens.newString(t.origin(), s, "\"" + s + "\""));
            }

            splitTokens.add(Tokens.newUnquotedText(t.origin(), "."));
         }

         if (tokenText.charAt(tokenText.length() - 1) != '.') {
            splitTokens.remove(splitTokens.size() - 1);
         }

         return splitTokens;
      }
   }

   private static void addPathText(List<PathParser.Element> buf, boolean wasQuoted, String newText) {
      int i = wasQuoted ? -1 : newText.indexOf(46);
      PathParser.Element current = (PathParser.Element)buf.get(buf.size() - 1);
      if (i < 0) {
         current.sb.append(newText);
         if (wasQuoted && current.sb.length() == 0) {
            current.canBeEmpty = true;
         }
      } else {
         current.sb.append(newText.substring(0, i));
         buf.add(new PathParser.Element("", false));
         addPathText(buf, false, newText.substring(i + 1));
      }

   }

   private static boolean looksUnsafeForFastParser(String s) {
      boolean lastWasDot = true;
      int len = s.length();
      if (s.isEmpty()) {
         return true;
      } else if (s.charAt(0) == '.') {
         return true;
      } else if (s.charAt(len - 1) == '.') {
         return true;
      } else {
         for(int i = 0; i < len; ++i) {
            char c = s.charAt(i);
            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && c != '_') {
               if (c == '.') {
                  if (lastWasDot) {
                     return true;
                  }

                  lastWasDot = true;
               } else {
                  if (c != '-') {
                     return true;
                  }

                  if (lastWasDot) {
                     return true;
                  }
               }
            } else {
               lastWasDot = false;
            }
         }

         if (lastWasDot) {
            return true;
         } else {
            return false;
         }
      }
   }

   private static Path fastPathBuild(Path tail, String s, int end) {
      int splitAt = s.lastIndexOf(46, end - 1);
      ArrayList<Token> tokens = new ArrayList();
      tokens.add(Tokens.newUnquotedText((ConfigOrigin)null, s));
      Path withOneMoreElement = new Path(s.substring(splitAt + 1, end), tail);
      return splitAt < 0 ? withOneMoreElement : fastPathBuild(withOneMoreElement, s, splitAt);
   }

   private static Path speculativeFastParsePath(String path) {
      String s = ConfigImplUtil.unicodeTrim(path);
      return looksUnsafeForFastParser(s) ? null : fastPathBuild((Path)null, s, s.length());
   }

   static class Element {
      StringBuilder sb;
      boolean canBeEmpty;

      Element(String initial, boolean canBeEmpty) {
         this.canBeEmpty = canBeEmpty;
         this.sb = new StringBuilder(initial);
      }

      public String toString() {
         return "Element(" + this.sb.toString() + "," + this.canBeEmpty + ")";
      }
   }
}
