package ivorius.reccomplex..shadow.mcopts.commands.parameters;

import ivorius.reccomplex..shadow.mcopts.MCOpts;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.CommandException;

public class Parameter<T> {
   protected final int moved;
   protected final String name;
   protected final List<String> params;
   @Nonnull
   protected final Parameter.Function<List<String>, T> fun;

   public Parameter(Parameter<T> other) {
      this.moved = other.moved;
      this.name = other.name;
      this.params = other.params;
      this.fun = other.fun;
   }

   public Parameter(Parameter<?> other, @Nonnull Parameter.Function<List<String>, T> fun) {
      this.moved = other.moved;
      this.name = other.name;
      this.params = other.params;
      this.fun = fun;
   }

   public Parameter(int moved, String name, List<String> params, @Nullable Parameter.Function<List<String>, T> fun) {
      this.moved = moved;
      this.name = name;
      this.params = params;
      this.fun = fun != null ? fun : this.initial();
   }

   public static Parameter<String> makeUp(String name, int moved, String... params) {
      return makeUp(name, moved, Arrays.asList(params));
   }

   public static Parameter<String> makeUp(String name, int moved, List<String> list) {
      return new Parameter<String>(moved, name, list, (Parameter.Function)null);
   }

   public String name(int index) {
      return this.name != null && index == 0 ? "--" + this.name : String.format("%s(%d)", this.name != null ? "--" + this.name + " " : "", Math.max(this.moved, 0) + index);
   }

   @Nonnull
   protected Parameter.Function<List<String>, String> initial() {
      return (list) -> {
         return (String)this.get(list, 0);
      };
   }

   public boolean isSet() {
      return this.moved >= 0;
   }

   protected <L> L get(List<L> list, int idx) throws CommandException {
      if (!this.isSet()) {
         throw Parameter.NotSetException.create(this, idx);
      } else if (list.isEmpty()) {
         throw Parameter.ArgumentMissingException.create(this, idx);
      } else {
         return (L)list.get(idx);
      }
   }

   public int count() {
      return this.params.size();
   }

   public boolean has(int size) {
      return size <= this.count();
   }

   @Nonnull
   public Parameter.Function<List<String>, T> function() {
      return this.fun;
   }

   public Parameter<T> filter(Predicate<T> fun) {
      return this.filter(fun, (Parameter.Function)null);
   }

   public Parameter<T> filter(Predicate<T> fun, @Nullable Parameter.Function<T, CommandException> esc) {
      return new Parameter<T>(this, (s) -> {
         T t = (T)this.function().apply(s);
         if (!fun.test(t) && esc != null) {
            throw (CommandException)esc.apply(t);
         } else {
            return t;
         }
      });
   }

   public <O> Parameter<O> to(java.util.function.Function<Parameter<T>, Parameter<O>> fun) {
      return (Parameter)fun.apply(this);
   }

   public <O, P> Parameter<O> to(BiFunction<Parameter<T>, P, Parameter<O>> fun, P p) {
      return (Parameter)fun.apply(this, p);
   }

   public <O> Parameter<O> map(Parameter.Function<T, O> fun) {
      return this.<O>map(fun, (Parameter.Function)null);
   }

   public <O> Parameter<O> map(Parameter.Function<T, O> fun, @Nullable Parameter.Function<T, CommandException> exc) {
      return new Parameter<O>(this, (s) -> {
         T t = (T)this.function().apply(s);
         if (t == null) {
            return null;
         } else {
            O o = (O)fun.apply(t);
            if (o == null && exc != null) {
               throw (CommandException)exc.apply(t);
            } else {
               return o;
            }
         }
      });
   }

   public <O> Parameter<O> flatMap(Parameter.Function<T, Parameter<O>> fun) {
      return new Parameter<O>(this, (s) -> {
         T t = (T)this.function().apply(s);
         if (t == null) {
            return null;
         } else {
            Parameter<O> po = (Parameter)fun.apply(t);
            return po.function().apply(po.params);
         }
      });
   }

   public Parameter<T> orElse(T t) {
      return this.orElseGet(() -> {
         return (Parameter<T>)t;
      });
   }

   public Parameter<T> orElseGet(Parameter.Supplier<T> supplier) {
      return new Parameter<T>(this, (s) -> {
         try {
            return (Parameter<T>)this.function().apply(s);
         } catch (Parameter.ArgumentMissingException var4) {
            return (Parameter<T>)supplier.get();
         }
      });
   }

   @Nonnull
   public T require() throws CommandException {
      T t = (T)this.function().apply(this.params);
      if (t == null) {
         throw MCOpts.translations.commandException("commands.parameters.invalid.generic", this.name(0));
      } else {
         return t;
      }
   }

   public Optional<T> optional() throws CommandException {
      T t = null;

      try {
         t = (T)this.function().apply(this.params);
      } catch (Parameter.ArgumentMissingException var3) {
         ;
      }

      return Optional.ofNullable(t);
   }

   public T get() {
      return (T)this.tryGet().get();
   }

   public Optional<T> tryGet() {
      T t = null;

      try {
         t = (T)this.function().apply(this.params);
      } catch (CommandException var3) {
         ;
      }

      return Optional.ofNullable(t);
   }

   public Parameter<T> rest(BinaryOperator<T> operator) {
      return new Parameter<T>(this, (p) -> {
         T t = (T)this.function().apply(Collections.singletonList(this.get(p, 0)));

         for(int i = 1; i < p.size(); ++i) {
            t = (T)operator.apply(t, this.function().apply(Collections.singletonList(p.get(i))));
         }

         return t;
      });
   }

   public Parameter<T> move(int idx) {
      return idx == 0 ? this : new Parameter(this.isSet() ? this.moved + idx : this.moved, this.name, this.params.subList(Math.min(idx, this.params.size()), this.params.size()), this.fun);
   }

   public Parameter<T[]> varargs(IntFunction<T[]> init) {
      return this.stream().<T[]>map((s) -> {
         return s.toArray(init);
      });
   }

   public Parameter<List<T>> varargsList() {
      return new Parameter<List<T>>(this, (p) -> {
         this.get(p, 0);
         List<T> list = new ArrayList(p.size());

         for(String param : p) {
            list.add(this.function().apply(Collections.singletonList(param)));
         }

         return list;
      });
   }

   public Parameter<Stream<T>> stream() {
      return this.varargsList().<Stream<T>>map(Collection::stream);
   }

   public static class ArgumentMissingException extends CommandException {
      private ArgumentMissingException(String message, Object... objects) {
         super(message, objects);
      }

      public static Parameter.ArgumentMissingException create(Parameter parameter, int index) {
         return (Parameter.ArgumentMissingException)MCOpts.translations.object(Parameter.ArgumentMissingException::new, "commands.parameters.missing", parameter.name(index));
      }
   }

   public interface Function<T, O> {
      O apply(T var1) throws CommandException;
   }

   public static class NotSetException extends Parameter.ArgumentMissingException {
      private NotSetException(String message, Object... objects) {
      }

      public static Parameter.NotSetException create(Parameter parameter, int index) {
         return (Parameter.NotSetException)MCOpts.translations.object(Parameter.NotSetException::new, "commands.parameters.missing.argument", parameter.name(index));
      }
   }

   public interface Supplier<T> {
      T get() throws CommandException;
   }
}
