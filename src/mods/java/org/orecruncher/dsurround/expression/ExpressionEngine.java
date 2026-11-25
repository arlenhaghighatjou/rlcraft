package org.orecruncher.dsurround.expression;

import java.util.List;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.lib.expression.ExpressionCache;
import org.orecruncher.lib.expression.IDynamicVariant;
import org.orecruncher.lib.expression.IVariant;

public final class ExpressionEngine {
   private static ExpressionEngine instance;
   private final ExpressionCache cache = new ExpressionCache(ModBase.log());

   public static ExpressionEngine instance() {
      if (instance == null) {
         instance = new ExpressionEngine();
      }

      return instance;
   }

   private ExpressionEngine() {
      this.cache.add(new BattleVariables());
      this.cache.add(new BiomeTypeVariables());
      this.cache.add(new BiomeVariables());
      this.cache.add(new DimensionVariables());
      this.cache.add(new DiurnalVariables());
      this.cache.add(new PlayerVariables());
      this.cache.add(new SeasonVariables());
      this.cache.add(new WeatherVariables());
   }

   public void reset() {
      this.cache.reset();
   }

   public List<IDynamicVariant> getVariables() {
      return this.cache.getVariantList();
   }

   public IVariant eval(String exp) {
      return this.cache.eval(exp);
   }

   public List<String> getNaughtyList() {
      return this.cache.getNaughtyList();
   }

   public boolean check(String exp) {
      return this.cache.check(exp);
   }
}
