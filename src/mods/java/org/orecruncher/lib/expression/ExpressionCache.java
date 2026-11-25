package org.orecruncher.lib.expression;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.lib.logging.ModLog;

public final class ExpressionCache {
   protected final ModLog logger;
   protected final List<IDynamicVariant> variants = new ArrayList();
   protected final Map<String, LazyVariant> cache = new Reference2ObjectOpenHashMap();
   protected final List<String> naughtyList = new ArrayList();

   public ExpressionCache(@Nonnull ModLog logger) {
      this.logger = logger;
   }

   public void add(@Nonnull DynamicVariantList dvl) {
      this.variants.addAll(dvl.getList());
      Collections.sort(this.variants, (o1, o2) -> {
         return o1.getName().compareTo(o2.getName());
      });
   }

   @Nonnull
   public List<String> getNaughtyList() {
      return this.naughtyList;
   }

   @Nonnull
   public List<IDynamicVariant> getVariantList() {
      return new ArrayList(this.variants);
   }

   public void reset() {
      this.variants.forEach(IDynamicVariant::reset);
   }

   @Nonnull
   private LazyVariant compile(@Nonnull String expression) {
      LazyVariant exp = null;

      try {
         exp = (LazyVariant)this.cache.get(expression);
         if (exp == null) {
            Expression x = new Expression(expression);
            this.variants.forEach((v) -> {
               x.addVariable((Variant)v);
            });
            exp = x.getProgram();
            this.cache.put(expression, exp);
         }
      } catch (Throwable var4) {
         this.naughtyList.add(expression);
         this.cache.put(expression, exp = new StringValue(var4.getMessage()));
         this.logger.warn("Unable to compile [%s]: %s", expression, var4.getMessage());
      }

      return exp;
   }

   @Nonnull
   public IVariant eval(@Nonnull String script) {
      return this.compile(script.intern()).eval();
   }

   public boolean check(@Nonnull String expression) {
      return StringUtils.isEmpty(expression) ? true : this.eval(expression).asBoolean();
   }
}
