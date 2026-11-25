package ivorius.reccomplex..shadow.mcopts.commands.parameters.expect;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.NaP;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameter;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import ivorius.reccomplex..shadow.mcopts.translation.Translations;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.tuple.Pair;

public class Expect {
   protected final Map<String, Expect.SuggestParameter> params = new HashMap();
   protected final Multimap<String, String> aliases = HashMultimap.create();
   protected final Set<String> shortParams = new HashSet();
   protected final Set<String> flags = new HashSet();
   protected String currentName;
   protected final List<String> order = new ArrayList();
   protected int currentCount;
   protected int until = -1;
   protected boolean or;

   public Expect() {
      this.getOrCreate((String)null);
   }

   public static Stream<?> unwrap(Object arg) {
      if (arg instanceof Pair) {
         arg = Stream.concat(unwrap(((Pair)arg).getLeft()), unwrap(((Pair)arg).getRight()));
      }

      while(arg instanceof Optional) {
         arg = ((Optional)arg).orElse(Collections.emptyList());
      }

      if (arg instanceof IntStream) {
         arg = ((IntStream)arg).mapToObj(String::valueOf);
      }

      if (arg instanceof Collection) {
         arg = ((Collection)arg).stream();
      }

      return arg instanceof Stream ? (Stream)arg : Stream.of(arg);
   }

   public static Collection<String> toStrings(Object arg) {
      return (Collection)unwrap(arg).map(Object::toString).collect(Collectors.toList());
   }

   public static List<String> matching(String arg, Object completion) {
      return CommandBase.getListOfStringsMatchingLastWord(new String[]{arg}, (Collection)unwrap(completion).collect(Collectors.toList()));
   }

   public static List<String> matchingAny(String arg, Object... suggest) {
      return CommandBase.getListOfStringsMatchingLastWord(new String[]{arg}, Arrays.asList(suggest));
   }

   public Parameters declareLenient(Parameters parameters) {
      parameters.flags(this.flags);
      this.params.forEach((key, param) -> {
         if (!Objects.equals(param.name, key)) {
            parameters.alias(param.name, key);
         }

      });
      parameters.until(this.until);
      return parameters;
   }

   public Parameters declare(Parameters parameters) {
      TObjectIntMap<String> restrict = new TObjectIntHashMap();
      this.params.forEach((key, param) -> {
         if (Objects.equals(param.name, key)) {
            restrict.put(key, param.repeat ? -1 : param.completions.size());
         }

      });
      parameters.restrict(restrict);
      return this.declareLenient(parameters);
   }

   @Nonnull
   protected Expect.SuggestParameter getOrCreate(@Nullable String name) {
      Expect.SuggestParameter param = (Expect.SuggestParameter)this.params.get(name);
      if (param == null) {
         this.params.put(name, param = new Expect.SuggestParameter(name));
      }

      return param;
   }

   public Expect named(@Nonnull String name, String... aliases) {
      Pair<String, Boolean> p = this.name(name);
      Expect.SuggestParameter param = this.getOrCreate(this.currentName = (String)p.getKey());
      if (((Boolean)p.getRight()).booleanValue()) {
         this.shortParams.add(p.getKey());
      }

      for(String alias : aliases) {
         p = this.name(alias);
         this.params.put(alias, param);
         if (((Boolean)p.getRight()).booleanValue()) {
            this.shortParams.add(p.getKey());
         }
      }

      this.aliases.putAll(name, Arrays.asList(aliases));
      return this;
   }

   private Pair<String, Boolean> name(String name) {
      boolean isShort = false;
      if (name.startsWith("--")) {
         name = name.substring("--".length());
      } else if (name.length() == 1) {
         isShort = true;
      } else if (name.startsWith("-")) {
         throw new IllegalArgumentException();
      }

      return Pair.of(name, Boolean.valueOf(isShort));
   }

   public Expect flag(@Nonnull String name, String... aliases) {
      this.flags.add(name);
      Collections.addAll(this.flags, aliases);
      return this.named(name, aliases);
   }

   public Expect atOnce(int num) {
      this.currentCount = num;
      return this;
   }

   public Expect nextRaw(Expect.Completer completion) {
      Expect.SuggestParameter cur = this.getOrCreate(this.currentName);
      if (this.or) {
         cur.or(completion);
         this.or = false;
      } else {
         this.order.add(this.currentName);
         cur.next(completion);
         this.currentCount = 1;
         this.optional();
      }

      return this;
   }

   public Expect next(Expect.Completer completion) {
      return this.nextRaw((server, sender, params, pos) -> {
         return matching(params.last(), completion.complete(server, sender, params, pos));
      });
   }

   public Expect skip() {
      return this.nextRaw((server, sender, parameters, pos) -> {
         return Stream.of();
      });
   }

   public Expect anyRaw(Object... completion) {
      return this.nextRaw((server, sender, params, pos) -> {
         return Stream.of(completion);
      });
   }

   public Expect any(Object... completion) {
      return this.nextRaw((server, sender, params, pos) -> {
         return matchingAny(params.last(), completion);
      });
   }

   public Expect nextRaw(Object completion) {
      return this.nextRaw((server, sender, params, pos) -> {
         return completion;
      });
   }

   public Expect next(Object completion) {
      return this.nextRaw((server, sender, params, pos) -> {
         return matching(params.last(), completion);
      });
   }

   public Expect nextRaw(Function<Parameters, ?> completion) {
      return this.nextRaw((server, sender, params, pos) -> {
         return (Expect)completion.apply(params);
      });
   }

   public Expect next(Function<Parameters, ?> completion) {
      return this.nextRaw((server, sender, params, pos) -> {
         return matching(params.last(), completion.apply(params));
      });
   }

   public Expect then(Consumer<Expect> fun) {
      fun.accept(this);
      return this;
   }

   public <P> Expect then(BiConsumer<Expect, P> fun, P p) {
      fun.accept(this, p);
      return this;
   }

   public Expect repeat() {
      Expect.SuggestParameter cur = (Expect.SuggestParameter)this.params.get(this.currentName);
      if (cur == null) {
         throw new IllegalStateException();
      } else {
         cur.repeat = true;
         return this;
      }
   }

   public Expect splitInner(Consumer<Expect> consumer) {
      Expect inner = Parameters.expect().then(consumer);
      this.nextRaw((server, sender, parameters, pos) -> {
         String last = parameters.last();
         String[] split = last.split(" ", -1);
         int lastSplit = last.lastIndexOf(" ");
         String lastStart = lastSplit >= 0 ? last.substring(0, lastSplit) + " " : "";
         return inner.get(server, sender, split, pos).stream().map((s) -> {
            return lastStart + s;
         });
      });
      return inner;
   }

   public Expect split(Consumer<Expect> consumer) {
      this.splitInner(consumer);
      return this;
   }

   public Expect words(Consumer<Expect> consumer) {
      Expect inner = this.splitInner((expect) -> {
         expect.then(consumer).repeat();
      });
      this.descriptionU((String)Iterables.getLast(inner.mapLastDescriptions((i, s) -> {
         return s;
      })));
      return this;
   }

   public Expect or() {
      if (this.or) {
         throw new IllegalStateException();
      } else {
         this.or = true;
         return this;
      }
   }

   public int index() {
      return this.params.size();
   }

   public Expect stopInterpreting() {
      this.until = ((Expect.SuggestParameter)this.params.get((Object)null)).completions.size();
      return this;
   }

   public List<String> get(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
      Parameters parameters = Parameters.ofLenient(args, this::declare);
      String lastName = parameters.lastName();
      Parameter entered = lastName != null ? parameters.get(lastName) : parameters.get(0);
      Expect.SuggestParameter param = (Expect.SuggestParameter)this.params.get(lastName);
      String currentArg = parameters.last();
      String currentArgRaw = parameters.lastRaw();
      boolean lastArgStartsQuote = args[args.length - 1].startsWith("\"") && parameters.interpretes();
      boolean lastArgQuoted = currentArgRaw.startsWith("\"") && parameters.interpretes();
      boolean longFlag = Parameters.hasLongPrefix(currentArgRaw) && parameters.interpretes();
      boolean shortFlag = Parameters.hasShortPrefix(currentArgRaw) && parameters.interpretes();
      if (param == null || entered.count() > param.completions.size() && !param.repeat || parameters.interpretes() && (longFlag || shortFlag)) {
         if (!parameters.interpretes()) {
            return Collections.emptyList();
         } else {
            List<String> suggest = new ArrayList();
            suggest.addAll(this.remaining(currentArg, parameters, false));
            suggest.addAll(this.remaining(currentArg, parameters, true));
            return matching(currentArg, suggest);
         }
      } else {
         Expect.Completer completer = (Expect.Completer)param.completions.get(Math.min(entered.count() - 1, param.completions.size() - 1));
         return (List)toStrings(completer.complete(server, sender, parameters, pos)).stream().map((s) -> {
            int wordStartIndex = s.lastIndexOf(32, CommandBase.doesStringStartWith(currentArg, s) ? currentArg.length() - 1 : s.length());
            boolean startQuote = s.contains(" ") && wordStartIndex < 0 || lastArgStartsQuote;
            s = s.substring(wordStartIndex + 1, s.length());
            if (lastArgQuoted || startQuote) {
               s = Parameters.escape(s);
            }

            if (startQuote) {
               s = "\"" + s;
            }

            return s;
         }).collect(Collectors.toCollection(ArrayList::new));
      }
   }

   @Nonnull
   public Collection<String> remaining(String currentArg, Parameters parameters, boolean useShort) {
      return toStrings(this.params.entrySet().stream().filter((e) -> {
         return e.getKey() != null;
      }).filter((e) -> {
         return !parameters.has((String)e.getKey()) || parameters.get((String)e.getKey()).count() < ((Expect.SuggestParameter)e.getValue()).completions.size() || ((Expect.SuggestParameter)e.getValue()).repeat;
      }).map(Entry::getKey).filter((p) -> {
         return this.shortParams.contains(p) == useShort;
      }).map((p) -> {
         return Parameters.prefix(this.shortParams.contains(p)) + p;
      }));
   }

   protected List<String> mapLastDescriptions(BiFunction<Integer, String, String> fun) {
      List<String> relevant = this.order.subList(this.order.size() - this.currentCount, this.order.size());
      int[] idx = new int[1];
      List<String> last = (List)relevant.stream().map((s) -> {
         List<String> list = ((Expect.SuggestParameter)this.params.get(s)).descriptions;
         return (String)list.remove(list.size() - 1);
      }).map((s) -> {
         int var10004 = idx[0];
         int var10001 = idx[0];
         idx[0] = var10004 + 1;
         return (String)fun.apply(Integer.valueOf(var10001), s);
      }).collect(Collectors.toList());
      relevant.stream().forEach((s) -> {
         ((Expect.SuggestParameter)this.params.get(s)).descriptions.add(last.remove(last.size() - 1));
      });
      return relevant;
   }

   protected String stripOptionality(String description) {
      return !description.startsWith("[") && !description.startsWith("<") ? description : description.substring(1, description.length() - 1);
   }

   protected String preserveOptionality(String description, String ref) {
      return ref.startsWith("[") ? String.format("[%s]", description) : (ref.startsWith("<") ? String.format("<%s>", description) : description);
   }

   public Expect descriptionU(List<String> description) {
      if (description.size() != this.currentCount) {
         throw new IllegalArgumentException();
      } else {
         this.mapLastDescriptions((i, s) -> {
            return this.preserveOptionality((String)description.get(i.intValue()), s);
         });
         return this;
      }
   }

   public Expect descriptionU(String... descriptions) {
      return this.descriptionU(Arrays.asList(descriptions));
   }

   public Expect description(String... keys) {
      return this.descriptionU((List)Arrays.stream(keys).map(Translations::get).collect(Collectors.toList()));
   }

   public Expect required() {
      this.mapLastDescriptions((i, s) -> {
         return String.format("<%s>", this.stripOptionality(s));
      });
      return this;
   }

   public Expect optional() {
      this.mapLastDescriptions((i, s) -> {
         return String.format("[%s]", this.stripOptionality(s));
      });
      return this;
   }

   public Expect naked() {
      this.mapLastDescriptions((i, s) -> {
         return this.stripOptionality(s);
      });
      return this;
   }

   public String usage() {
      return TextFormatting.RESET + (String)Stream.concat(((Expect.SuggestParameter)this.params.get((Object)null)).usage(), this.params.entrySet().stream().filter((e) -> {
         return e.getKey() != null;
      }).filter((e) -> {
         return ((String)e.getKey()).equals(((Expect.SuggestParameter)e.getValue()).name);
      }).flatMap((e) -> {
         return this.flags.contains(e.getKey()) ? Stream.of(this.keyRepresentation((String)e.getKey())) : ((Expect.SuggestParameter)e.getValue()).usage().map((desc) -> {
            return String.format("%s %s", this.keyRepresentation((String)e.getKey()), desc);
         });
      })).reduce("", NaP::join);
   }

   protected String keyRepresentation(String key) {
      List<String> aliases = Lists.newArrayList(this.aliases.get(key));
      Iterator<String> iterator = aliases.iterator();

      while(iterator.hasNext()) {
         String alias = (String)iterator.next();
         if (key.contains(alias)) {
            key = key.replaceFirst(alias, String.format("%s%s%s", TextFormatting.AQUA, alias, TextFormatting.RESET));
            iterator.remove();
         }
      }

      aliases.add(0, key);
      return Parameters.prefix(this.shortParams.contains(key)) + String.join("|", aliases);
   }

   public interface Completer {
      Object complete(MinecraftServer var1, ICommandSender var2, Parameters var3, @Nullable BlockPos var4);
   }

   protected class SuggestParameter {
      protected String name;
      protected final List<Expect.Completer> completions = new ArrayList();
      protected final List<String> descriptions = new ArrayList();
      protected boolean repeat;

      public SuggestParameter(String name) {
         this.name = name;
      }

      public Expect.SuggestParameter next(Expect.Completer completion) {
         this.completions.add(completion);
         this.descriptions.add(String.format("[%d]", this.completions.size()));
         return this;
      }

      public Expect.SuggestParameter or(Expect.Completer completion) {
         Expect.Completer prev = (Expect.Completer)this.completions.remove(this.completions.size() - 1);
         this.completions.add((server, sender, parameters, pos) -> {
            return Pair.of(prev.complete(server, sender, parameters, pos), completion.complete(server, sender, parameters, pos));
         });
         return this;
      }

      public Stream<String> usage() {
         return IntStream.range(0, this.descriptions.size()).mapToObj((i) -> {
            return String.format("%s%s%s%s", TextFormatting.YELLOW, this.descriptions.get(i), TextFormatting.RESET, this.repeat && i == this.descriptions.size() - 1 ? "..." : "");
         });
      }
   }
}
