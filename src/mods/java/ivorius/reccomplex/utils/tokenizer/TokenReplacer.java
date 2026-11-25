package ivorius.reccomplex.utils.tokenizer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.io.LineReader;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.random.Poem;
import ivorius.reccomplex.utils.PassLimiter;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TokenReplacer {
   public static final int MAX_EXPLODES = 1000;

   public TokenReplacer() {
   }

   public static <T> String evaluate(EaglercraftRandom random, List<TokenReplacer.Token> text, T context, Poem.StitchedTheme theme) throws TokenReplacer.OverpoemedException {
      StringBuilder builder = new StringBuilder();
      PassLimiter<TokenReplacer.OverpoemedException> limiter = new PassLimiter<TokenReplacer.OverpoemedException>(TokenReplacer.OverpoemedException::new, 1000);
      ArrayDeque<TokenReplacer.Token> queue = new ArrayDeque();
      queue.addAll(text);
      Map<String, List<TokenReplacer.Token>> repeats = new HashMap();
      boolean nextUpper = true;

      TokenReplacer.Token token;
      while((token = (TokenReplacer.Token)queue.poll()) != null) {
         if (token instanceof TokenReplacer.ExplodingToken) {
            TokenReplacer.ExplodingToken<T> symbol = (TokenReplacer.ExplodingToken)token;
            boolean remember = symbol.flags.contains("rem");
            boolean repeat = symbol.flags.contains("rep");
            List<TokenReplacer.Token> addTokens = repeat ? (List)repeats.get(symbol.tag) : symbol.explode(theme, context, random);
            if (repeat && addTokens == null) {
               addTokens = Collections.singletonList(new TokenReplacer.StringToken(symbol.startIndex, symbol.endIndex, "EMPTY_REPEAT"));
            }

            if (remember) {
               repeats.put(symbol.tag, addTokens);
            }

            for(int i = addTokens.size() - 1; i >= 0; --i) {
               queue.addFirst(addTokens.get(i));
            }

            limiter.add(addTokens.size());
         } else if (token instanceof TokenReplacer.StringToken) {
            String string = ((TokenReplacer.StringToken)token).string;
            builder.append(nextUpper ? firstCharUppercase(string) : string);
            nextUpper = string.matches(".*[.?!]$");
         }
      }

      return builder.toString();
   }

   public static String firstCharUppercase(String name) {
      return Character.toUpperCase(name.charAt(0)) + name.substring(1);
   }

   public interface Exploder<T> {
      static <T> TokenReplacer.Exploder<T> string(TokenReplacer.Exploder.StringExploder<T> exploder) {
         return (token, theme, context, random) -> {
            return Collections.singletonList(new TokenReplacer.StringToken(0, 0, exploder.evaluate(token, theme, context, random)));
         };
      }

      List<TokenReplacer.Token> explode(TokenReplacer.ExplodingToken var1, Poem.StitchedTheme var2, T var3, EaglercraftRandom var4);

      public interface StringExploder<T> {
         String evaluate(TokenReplacer.ExplodingToken var1, Poem.StitchedTheme var2, T var3, EaglercraftRandom var4);
      }
   }

   public static class ExplodingToken<T> extends TokenReplacer.Token {
      public final String tag;
      public final List<String> flags;
      public final TokenReplacer.Exploder<T> exploder;

      public ExplodingToken(int startIndex, int endIndex, String tag, List<String> flags, TokenReplacer.Exploder<T> exploder) {
         super(startIndex, endIndex);
         this.tag = tag;
         this.flags = flags;
         this.exploder = exploder;
      }

      public List<TokenReplacer.Token> explode(Poem.StitchedTheme theme, T context, EaglercraftRandom random) {
         return this.exploder.explode(this, theme, context, random);
      }
   }

   public static class OverpoemedException extends Exception {
      public OverpoemedException() {
      }
   }

   public abstract static class ReplaceFactory<T> implements SymbolTokenizer.TokenFactory {
      public ReplaceFactory() {
      }

      public static String join(Collection<List<TokenReplacer.Token>> lists) {
         return (String)lists.stream().map(Object::toString).reduce("", (s, r) -> {
            return s + " " + r;
         });
      }

      @Nullable
      public SymbolTokenizer.Token tryConstructSymbolTokenAt(int index, @Nonnull String string) {
         if (string.charAt(index) == '<') {
            int end = string.indexOf(62, index);
            String contents = string.substring(index + 1, end);
            List<String> parts = Arrays.asList(contents.split(" "));
            String tag = (String)parts.get(0);
            List<String> flags = Lists.newArrayList(parts.subList(1, parts.size()));
            TokenReplacer.Exploder<T> exploder = this.exploder(tag, flags);
            if (exploder != null) {
               return new TokenReplacer.ExplodingToken(index, end + 1, tag, flags, exploder);
            }
         }

         return null;
      }

      protected abstract TokenReplacer.Exploder<T> exploder(String var1, List<String> var2);

      @Nonnull
      public SymbolTokenizer.Token constructStringToken(int index, @Nonnull String string) {
         return new TokenReplacer.StringToken(index, index + string.length(), string);
      }
   }

   public static class StringToken extends TokenReplacer.Token {
      public String string;

      public StringToken(int startIndex, int endIndex, String string) {
         super(startIndex, endIndex);
         this.string = string;
      }
   }

   public abstract static class Theme {
      public Multimap<String, String> contents = HashMultimap.create();

      public Theme() {
      }

      public static String parameter(List<String> flags, int index, String def) {
         return flags.size() > index ? (String)flags.get(index) : def;
      }

      protected void read(String fileContents) {
         LineReader reader = new LineReader(new StringReader(fileContents));
         Collection<String> currentList = null;

         String line;
         try {
            while((line = reader.readLine()) != null) {
               if (line.startsWith("***")) {
                  String tag = line.substring(4).trim();
                  currentList = this.contents.get(tag);
               } else {
                  String word = line.trim();
                  if (word.length() > 0 && currentList != null) {
                     currentList.add(word);
                  }
               }
            }
         } catch (IOException var6) {
            RecurrentComplex.logger.error(var6);
         }

      }

      public Multimap<String, List<TokenReplacer.Token>> build(Multimap<String, List<TokenReplacer.Token>> map, SymbolTokenizer<TokenReplacer.Token> tokenizer) {
         for(String include : this.contents.get("include")) {
            TokenReplacer.Theme theme = this.getOther(include);
            if (theme == null) {
               RecurrentComplex.logger.error("Can't find theme to include: " + include);
            } else {
               theme.build(map, tokenizer);
            }
         }

         for(String key : this.contents.keySet()) {
            Stream var10002 = this.contents.get(key).stream().map((s) -> {
               try {
                  return tokenizer.tokenize(s);
               } catch (ParseException var3) {
                  RecurrentComplex.logger.warn("Unable to read line: " + s, var3);
                  return Collections.emptyList();
               }
            });
            map.putAll(key, var10002::iterator);
         }

         return map;
      }

      protected abstract TokenReplacer.Theme getOther(String var1);
   }

   public abstract static class Token extends SymbolTokenizer.Token {
      public Token(int startIndex, int endIndex) {
         super(startIndex, endIndex);
      }
   }
}
