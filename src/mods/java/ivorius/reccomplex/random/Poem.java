package ivorius.reccomplex.random;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.files.SimpleLeveledRegistry;
import ivorius.reccomplex.utils.tokenizer.SymbolTokenizer;
import ivorius.reccomplex.utils.tokenizer.TokenReplacer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public class Poem {
   private static final int TITLE_TRIES = 50;
   public static SimpleLeveledRegistry<Poem.Theme> THEME_REGISTRY = new SimpleLeveledRegistry<Poem.Theme>("poem theme");
   private String title;
   private String text;

   public Poem(String title, String text) {
      this.title = title;
      this.text = text;
   }

   public static Poem randomPoem(EaglercraftRandom random, Integer maxTitleLength, Person author) {
      Poem.StitchedTheme theme = Poem.StitchedTheme.random(author);
      Poem.PoemContext poemContext = new Poem.PoemContext();

      while(poemContext.add(random, poemContext.names, 0.3F, Person.randomHuman(random, random.nextBoolean()).getFirstName())) {
         ;
      }

      while(poemContext.add(random, poemContext.places, 0.3F, Place.randomPlace(random).getFullPlaceType())) {
         ;
      }

      String title = randomTitle(random, poemContext, maxTitleLength, theme);
      String phrase = null;

      try {
         phrase = evaluate(random, theme.get("text"), poemContext, theme);
      } catch (TokenReplacer.OverpoemedException var8) {
         RecurrentComplex.logger.error(String.format("Too much text with theme: %s", theme));
         phrase = String.format("ERROR: %s", theme);
      }

      return new Poem(title, phrase);
   }

   @Nonnull
   protected static String randomTitle(EaglercraftRandom random, Poem.PoemContext poemContext, Integer maxLength, Poem.StitchedTheme theme) {
      int i = 0;

      while(true) {
         if (i <= 50) {
            String title;
            try {
               title = evaluate(random, theme.get("title"), poemContext, theme).trim();
            } catch (TokenReplacer.OverpoemedException var7) {
               RecurrentComplex.logger.error(String.format("Too much title with theme: %s", theme));
               return String.format("ERROR: %s", theme);
            }

            if (i == 50) {
               return StringUtils.abbreviate(title, maxLength.intValue());
            }

            if (maxLength != null && title.length() >= maxLength.intValue()) {
               ++i;
               continue;
            }

            return title;
         }

         throw new RuntimeException("Shouldn't be here");
      }
   }

   private static String evaluate(EaglercraftRandom random, List<List<TokenReplacer.Token>> patterns, Poem.PoemContext context, Poem.StitchedTheme theme) throws TokenReplacer.OverpoemedException {
      return TokenReplacer.evaluate(random, (List)getRandomElementFrom(patterns, random), context, theme);
   }

   private static <O> O getRandomElementFrom(List<O> list, EaglercraftRandom random) {
      return (O)list.get(random.nextInt(list.size()));
   }

   @Nonnull
   protected static TokenReplacer.ReplaceFactory factory() {
      return new TokenReplacer.ReplaceFactory<Poem.PoemContext>() {
         @Nonnull
         protected TokenReplacer.Exploder<Poem.PoemContext> exploder(String tag, List<String> params) {
            byte var4 = -1;
            switch(tag.hashCode()) {
            case -1034364087:
               if (tag.equals("number")) {
                  var4 = 3;
               }
               break;
            case 3152:
               if (tag.equals("br")) {
                  var4 = 0;
               }
               break;
            case 3373707:
               if (tag.equals("name")) {
                  var4 = 2;
               }
               break;
            case 106748167:
               if (tag.equals("place")) {
                  var4 = 1;
               }
            }

            switch(var4) {
            case 0:
               return TokenReplacer.Exploder.<Poem.PoemContext>string((token, theme, context, random) -> {
                  return "\n";
               });
            case 1:
               return TokenReplacer.Exploder.<Poem.PoemContext>string((token, theme, context, random) -> {
                  return (String)Poem.getRandomElementFrom(context.places, random);
               });
            case 2:
               return TokenReplacer.Exploder.<Poem.PoemContext>string((token, theme, context, random) -> {
                  return (String)Poem.getRandomElementFrom(context.names, random);
               });
            case 3:
               return TokenReplacer.Exploder.<Poem.PoemContext>string(this.numEvaluator(Integer.valueOf(TokenReplacer.Theme.parameter(params, 0, "2")).intValue(), Integer.valueOf(TokenReplacer.Theme.parameter(params, 1, "10")).intValue(), Integer.valueOf(TokenReplacer.Theme.parameter(params, 2, "1")).intValue()));
            default:
               return (token, theme, context, random) -> {
                  List<List<TokenReplacer.Token>> list = theme.get(tag);
                  return list != null && !list.isEmpty() ? (List)Poem.getRandomElementFrom(list, random) : Collections.singletonList(new TokenReplacer.StringToken(0, 0, "EMPTY"));
               };
            }
         }

         private TokenReplacer.Exploder.StringExploder<Poem.PoemContext> numEvaluator(int min, int max, int mul) {
            return (token, theme, context, random) -> {
               return String.valueOf((random.nextInt(max - min + 1) + min) * mul);
            };
         }
      };
   }

   public String getTitle() {
      return this.title;
   }

   public String getText() {
      return this.text;
   }

   public static class PoemContext {
      public List<String> names = new ArrayList();
      public List<String> places = new ArrayList();

      public PoemContext() {
      }

      public boolean add(EaglercraftRandom random, List<String> list, float continueChance, String value) {
         list.add(value);
         return random.nextFloat() < continueChance;
      }
   }

   public static class StitchedTheme {
      protected Map<String, List<List<TokenReplacer.Token>>> converters;
      protected List<String> titles = new ArrayList();

      public StitchedTheme() {
      }

      public static Poem.StitchedTheme random(Person author) {
         EaglercraftRandom styleRandom = new EaglercraftRandom((long)author.hashCode());
         SymbolTokenizer<TokenReplacer.Token> tokenizer = new SymbolTokenizer<TokenReplacer.Token>(new SymbolTokenizer.SimpleCharacterRules('\\', (Character)null, new char[0], (char[])null), Poem.factory());
         Poem.StitchedTheme stitchedTheme = new Poem.StitchedTheme();
         HashMultimap<String, List<TokenReplacer.Token>> map = HashMultimap.create();

         while(true) {
            Poem.Theme theme = (Poem.Theme)Poem.getRandomElementFrom(new ArrayList(Poem.THEME_REGISTRY.allActive()), styleRandom);
            stitchedTheme.titles.add(Poem.THEME_REGISTRY.id(theme));
            float acceptance = styleRandom.nextFloat();
            Multimap<String, List<TokenReplacer.Token>> builtTheme = theme.build(HashMultimap.create(), tokenizer);
            builtTheme.asMap().values().forEach((v) -> {
               v.removeIf((p) -> {
                  return styleRandom.nextFloat() < acceptance && v.size() > 1;
               });
            });
            map.putAll(builtTheme);
            if (!styleRandom.nextBoolean()) {
               break;
            }
         }

         stitchedTheme.converters = build(map);
         return stitchedTheme;
      }

      public static Map<String, List<List<TokenReplacer.Token>>> build(Multimap<String, List<TokenReplacer.Token>> build) {
         return (Map)build.asMap().entrySet().stream().collect(Collectors.toMap(Entry::getKey, (k) -> {
            return Lists.newArrayList((Iterable)k.getValue());
         }));
      }

      public List<List<TokenReplacer.Token>> get(String title) {
         return (List)this.converters.get(title);
      }

      public String toString() {
         return String.format("Stitched: %s", this.titles);
      }
   }

   public static class Theme extends TokenReplacer.Theme {
      public Theme() {
      }

      public static Poem.Theme fromFile(String fileContents) {
         Poem.Theme theme = new Poem.Theme();
         theme.read(fileContents);
         return theme;
      }

      protected TokenReplacer.Theme getOther(String include) {
         return Poem.THEME_REGISTRY.get(include);
      }
   }
}
