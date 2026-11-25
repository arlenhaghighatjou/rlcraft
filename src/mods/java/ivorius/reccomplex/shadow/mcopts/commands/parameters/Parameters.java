package ivorius.reccomplex..shadow.mcopts.commands.parameters;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.primitives.Doubles;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import ivorius.reccomplex..shadow.mcopts.MCOpts;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.expect.Expect;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import net.minecraft.command.CommandException;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.commons.lang3.tuple.Pair;

public class Parameters {
   public static final String SHORT_FLAG_PREFIX = "-";
   public static final String LONG_FLAG_PREFIX = "--";
   protected List<Pair<String, String>> raw;
   protected Set<String> flags = new HashSet();
   protected ListMultimap<String, String> params = ArrayListMultimap.create();
   protected ListMultimap<String, String> rawParams = ArrayListMultimap.create();
   protected List<String> order = new ArrayList();
   protected TObjectIntMap<String> restrictFlags = new TObjectIntHashMap();
   protected Set<String> declaredFlags = new HashSet();
   protected Map<String, String> alias = new HashMap();
   protected int until = -1;

   public Parameters() {
   }

   public static Parameters of(String[] args, Function<Parameters, Parameters> c) throws CommandException {
      Parameters parameters = new Parameters();
      return (c != null ? (Parameters)c.apply(parameters) : parameters).build(args);
   }

   public static Parameters ofLenient(String[] args, Function<Parameters, Parameters> c) {
      Parameters parameters = new Parameters();
      return (c != null ? (Parameters)c.apply(parameters) : parameters).buildLenient(args);
   }

   public static String prefix(boolean isShort) {
      return isShort ? "-" : "--";
   }

   public static boolean hasLongPrefix(String name) {
      return name.startsWith("--");
   }

   public static boolean hasShortPrefix(String name) {
      return name.startsWith("-") && Doubles.tryParse(name) == null;
   }

   public static String[] quoted(String[] args) {
      return (String[])parse(args).map(Pair::getRight).toArray((x$0) -> {
         return new String[x$0];
      });
   }

   public static Stream<Pair<String, String>> parse(String[] args) {
      String full = String.join(" ", args);
      StringReader reader = new StringReader(full);
      StreamTokenizer tokenizer = new StreamTokenizer(reader);
      tokenizer.resetSyntax();
      tokenizer.wordChars(0, Integer.MAX_VALUE);
      tokenizer.whitespaceChars(0, 32);
      tokenizer.quoteChar(34);
      List<String> parsed = new ArrayList();
      List<String> raw = new ArrayList();
      int last_tt = -1;
      int lastIndex = 0;

      try {
         while(tokenizer.nextToken() != -1) {
            int idx = Math.min(index(reader), full.length());
            String arg = tokenizer.sval;
            String argRaw = full.substring(lastIndex, idx);
            parsed.add(arg);
            raw.add(tokenizer.ttype != 34 ? argRaw.trim() : argRaw);
            lastIndex = idx;
            last_tt = tokenizer.ttype;
         }
      } catch (IOException var11) {
         MCOpts.logger.error("Error reading string", var11);
      }

      reader.close();
      if (args.length > 0 && args[args.length - 1].length() == 0) {
         String lastRaw = raw.size() > 0 ? (String)Iterables.getLast(raw) : null;
         if (last_tt != 34 || lastRaw != null && lastRaw.charAt(lastRaw.length() - 1) == '"') {
            parsed.add("");
            raw.add("");
         }
      }

      return IntStream.range(0, parsed.size()).mapToObj((i) -> {
         return Pair.of(raw.get(i), parsed.get(i));
      });
   }

   public static int index(StringReader reader) {
      try {
         return ReflectionHelper.findField(StringReader.class, new String[]{"next"}).getInt(reader);
      } catch (IllegalAccessException var2) {
         MCOpts.logger.error("Error trying to get next", var2);
         return 0;
      }
   }

   @Nonnull
   public static String trimQuotes(String arg) {
      String trimmed = arg;
      if (arg.startsWith("\"")) {
         trimmed = arg.substring(1, arg.length() - (arg.length() > 1 && arg.endsWith("\"") ? 1 : 0));
      }

      return trimmed;
   }

   public static Expect expect() {
      return new Expect();
   }

   public static String escape(String s) {
      return s.replaceAll("\"", Matcher.quoteReplacement("\\\""));
   }

   public Parameters build(String[] args) throws CommandException {
      return this.buildRaw(args, false);
   }

   public Parameters buildLenient(String[] args) {
      try {
         return this.buildRaw(args, true);
      } catch (CommandException var3) {
         throw new InternalError();
      }
   }

   public Parameters buildRaw(String[] args, boolean lenient) throws CommandException {
      this.raw = new ArrayList((Collection)parse(args).collect(Collectors.toList()));
      this.order.add((Object)null);
      String curName = null;
      boolean rawInterpreted = true;

      for(int p = 0; p < this.raw.size(); ++p) {
         Pair<String, String> pair = (Pair)this.raw.get(p);
         String argRaw = (String)pair.getLeft();
         String arg = (String)pair.getRight();
         if (!this.interpretes() && rawInterpreted) {
            List<Pair<String, String>> rest = this.raw.subList(p, this.raw.size());
            String restString = (String)rest.stream().map(Pair::getLeft).reduce(NaP::join).orElse("");
            rest.clear();
            Stream var10000 = Arrays.stream(restString.split(" ", -1)).map((s) -> {
               return Pair.of(s, s);
            });
            List var10001 = this.raw;
            this.raw.getClass();
            var10000.forEach(var10001::add);
            rawInterpreted = false;
            --p;
         } else if (this.interpretes() && hasLongPrefix(argRaw)) {
            this.flags.add(curName = this.root(argRaw.substring("--".length()).trim()));
            if (!lenient) {
               this.testAdd(curName);
            }

            if (this.declaredFlags.contains(curName)) {
               curName = null;
            }
         } else if (this.interpretes() && hasShortPrefix(argRaw)) {
            List<String> curFlags = (List)argRaw.substring("-".length()).trim().chars().mapToObj((c) -> {
               return String.valueOf((char)c);
            }).collect(Collectors.toList());

            for(int i = 0; i < curFlags.size(); ++i) {
               this.flags.add(curName = this.root((String)curFlags.get(i)));
               if (!lenient) {
                  this.testAdd(curName);
               }

               if (this.declaredFlags.contains(curName)) {
                  curName = null;
               } else if (curFlags.size() > i + 1) {
                  String rest = String.join("", curFlags.subList(i + 1, curFlags.size()));
                  this.order.add(curName);
                  this.params.put(curName, rest);
                  this.rawParams.put(curName, argRaw);
                  curName = null;
               }
            }
         } else {
            if (!lenient) {
               this.testAdd(curName);
            }

            if (this.until > 0 && curName == null) {
               --this.until;
            }

            this.order.add(curName);
            this.params.put(curName, arg);
            this.rawParams.put(curName, argRaw);
            curName = null;
         }
      }

      return this;
   }

   protected void testAdd(String name) throws CommandException {
      if (!this.restrictFlags.containsKey(name)) {
         throw Parameters.ParameterUnknownException.create(name);
      } else {
         int restrict = this.restrictFlags.get(name);
         if (!this.declaredFlags.contains(name) && restrict >= 0 && this.params.get(name).size() >= restrict) {
            throw Parameters.ParameterTooManyArgumentsException.create(name);
         }
      }
   }

   public void requireBuilt() throws IllegalStateException {
      if (this.raw == null) {
         throw new IllegalStateException();
      }
   }

   public void requireUnbuilt() throws IllegalStateException {
      if (this.raw != null) {
         throw new IllegalStateException();
      }
   }

   public Parameters alias(String parent, String... aliases) {
      this.requireUnbuilt();
      parent = this.root(parent);

      for(String alias : aliases) {
         this.alias.put(alias, parent);
      }

      if (this.flags.removeAll(Arrays.asList(aliases))) {
         this.flags.add(parent);
      }

      return this;
   }

   public Parameters flag(String flag, String... aliases) {
      this.requireUnbuilt();
      this.declaredFlags.add(this.root(flag));
      this.alias(flag, aliases);
      return this;
   }

   public Parameters flags(Collection<String> flags) {
      for(String flag : flags) {
         this.flag(flag);
      }

      return this;
   }

   public Parameters flags(String... flags) {
      return this.flags(Arrays.asList(flags));
   }

   public Parameters until(int until) {
      this.until = until;
      return this;
   }

   public Parameters restrict(TObjectIntMap<String> flags) {
      this.restrictFlags.putAll(flags);
      return this;
   }

   public String root(String name) {
      String other;
      while((other = (String)this.alias.get(name)) != null) {
         name = other;
      }

      return name;
   }

   public String lastName() {
      return (String)Iterables.getLast(this.order);
   }

   public String last() {
      this.requireBuilt();
      return (String)((Pair)Iterables.getLast(this.raw)).getRight();
   }

   public String lastRaw() {
      this.requireBuilt();
      return (String)((Pair)Iterables.getLast(this.raw)).getLeft();
   }

   public String[] lastAsArray() {
      return new String[]{this.last()};
   }

   public boolean interpretes() {
      return this.until != 0;
   }

   public <T> Parameter<T> get(Function<Parameters, Parameter<T>> fun) {
      return (Parameter)fun.apply(this);
   }

   public Map<String, Parameter> entries() {
      this.requireBuilt();
      return (Map)this.flags.stream().collect(Collectors.toMap((k) -> {
         return k;
      }, (k) -> {
         return new Parameter(0, k, this.params.get(k), (Parameter.Function)null);
      }));
   }

   public boolean has(@Nonnull String flag) {
      this.requireBuilt();
      return this.flags.contains(this.root(flag));
   }

   public Parameter<String> get(int idx) {
      this.requireBuilt();
      return (new Parameter<String>(0, (String)null, this.params.get((Object)null), (Parameter.Function)null)).move(idx);
   }

   public Parameter<String> get(@Nonnull String name) {
      this.requireBuilt();
      name = this.root(name);
      return new Parameter<String>(!this.has(name) && !this.params.containsKey(name) ? -1 : 0, name, this.params.get(name), (Parameter.Function)null);
   }

   public List<Pair<String, String>> raw() {
      this.requireBuilt();
      return Collections.unmodifiableList(this.raw);
   }

   public Parameter<String> raw(int idx) {
      this.requireBuilt();
      return (new Parameter<String>(0, (String)null, this.rawParams.get((Object)null), (Parameter.Function)null)).move(idx);
   }

   public Parameter<String> raw(@Nonnull String name) {
      this.requireBuilt();
      name = this.root(name);
      return new Parameter<String>(!this.has(name) && !this.rawParams.containsKey(name) ? -1 : 0, name, this.params.get(name), (Parameter.Function)null);
   }

   public static class ParameterTooManyArgumentsException extends CommandException {
      private String parameter;

      private ParameterTooManyArgumentsException(String message, Object... objects) {
         super(message, objects);
      }

      public static Parameters.ParameterTooManyArgumentsException create(String name) {
         Parameters.ParameterTooManyArgumentsException exc = name != null ? (Parameters.ParameterTooManyArgumentsException)MCOpts.translations.object(Parameters.ParameterTooManyArgumentsException::new, "commands.parameters.toomany.named", name) : (Parameters.ParameterTooManyArgumentsException)MCOpts.translations.object(Parameters.ParameterTooManyArgumentsException::new, "commands.parameters.toomany.ordered");
         exc.parameter = name;
         return exc;
      }

      public String getParameter() {
         return this.parameter;
      }
   }

   public static class ParameterUnknownException extends CommandException {
      private String parameter;

      private ParameterUnknownException(String message, Object... objects) {
         super(message, objects);
      }

      public static Parameters.ParameterUnknownException create(String name) {
         Parameters.ParameterUnknownException exc = (Parameters.ParameterUnknownException)MCOpts.translations.object(Parameters.ParameterUnknownException::new, "commands.parameters.unknown", name);
         exc.parameter = name;
         return exc;
      }

      public String getParameter() {
         return this.parameter;
      }
   }
}
