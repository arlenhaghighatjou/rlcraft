package org.orecruncher.dsurround.registry.biome;

import com.google.common.primitives.Booleans;
import javax.annotation.Nonnull;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.config.BiomeConfig;
import org.orecruncher.lib.expression.BooleanValue;
import org.orecruncher.lib.expression.Expression;
import org.orecruncher.lib.expression.Function;
import org.orecruncher.lib.expression.IVariant;
import org.orecruncher.lib.expression.NumberValue;
import org.orecruncher.lib.expression.StringValue;
import org.orecruncher.lib.expression.Variant;

@SideOnly(Side.CLIENT)
public abstract class BiomeMatcher {
   public BiomeMatcher() {
   }

   public abstract boolean match(@Nonnull BiomeInfo var1);

   public static BiomeMatcher getMatcher(@Nonnull BiomeConfig cfg) {
      if (cfg.conditions == null) {
         cfg.conditions = "";
      }

      return new BiomeMatcher.ConditionsImpl(cfg);
   }

   private static class ConditionsImpl extends BiomeMatcher {
      protected BiomeInfo current;
      protected final Expression exp;

      public ConditionsImpl(@Nonnull BiomeConfig config) {
         this.exp = new Expression(config.conditions);
         this.exp.addVariable(new Variant("biome.name") {
            public int compareTo(@Nonnull IVariant o) {
               return this.asString().compareTo(o.asString());
            }

            public float asNumber() {
               return 0.0F;
            }

            public String asString() {
               return ConditionsImpl.this.current.getBiomeName();
            }

            public boolean asBoolean() {
               return false;
            }

            @Nonnull
            public IVariant add(@Nonnull IVariant term) {
               return new StringValue(this.asString().concat(term.asString()));
            }
         });
         this.exp.addVariable(new Variant("biome.id") {
            public int compareTo(@Nonnull IVariant o) {
               return this.asString().compareTo(o.asString());
            }

            public float asNumber() {
               return 0.0F;
            }

            public String asString() {
               return ConditionsImpl.this.current.getKey().toString();
            }

            public boolean asBoolean() {
               return false;
            }

            @Nonnull
            public IVariant add(@Nonnull IVariant term) {
               return new StringValue(this.asString().concat(term.asString()));
            }
         });
         this.exp.addVariable(new Variant("biome.modid") {
            public int compareTo(@Nonnull IVariant o) {
               return this.asString().compareTo(o.asString());
            }

            public float asNumber() {
               return 0.0F;
            }

            public String asString() {
               return ConditionsImpl.this.current.getKey().getResourceDomain();
            }

            public boolean asBoolean() {
               return false;
            }

            @Nonnull
            public IVariant add(@Nonnull IVariant term) {
               return new StringValue(this.asString().concat(term.asString()));
            }
         });
         this.exp.addVariable(new Variant("biome.rainfall") {
            public int compareTo(@Nonnull IVariant o) {
               return Float.compare(this.asNumber(), o.asNumber());
            }

            public float asNumber() {
               return ConditionsImpl.this.current.getRainfall();
            }

            public String asString() {
               return Float.toString(this.asNumber());
            }

            public boolean asBoolean() {
               return this.asNumber() != 0.0F;
            }

            @Nonnull
            public IVariant add(@Nonnull IVariant term) {
               return new NumberValue(this.asNumber() + term.asNumber());
            }
         });
         this.exp.addVariable(new Variant("biome.isFake") {
            public int compareTo(@Nonnull IVariant o) {
               return this.asString().compareTo(o.asString());
            }

            public float asNumber() {
               return this.asBoolean() ? 1.0F : 0.0F;
            }

            public String asString() {
               return Boolean.toString(this.asBoolean());
            }

            public boolean asBoolean() {
               return ConditionsImpl.this.current.isFake();
            }

            @Nonnull
            public IVariant add(@Nonnull IVariant term) {
               return new BooleanValue(this.asBoolean() || term.asBoolean());
            }
         });

         for(Type t : BiomeUtil.getBiomeTypes()) {
            this.exp.addVariable(new BiomeMatcher.ConditionsImpl.BiomeTypeVariable(t));
         }

         for(ResourceLocation b : Biome.REGISTRY.getKeys()) {
            if ("minecraft".equals(b.getResourceDomain())) {
               this.exp.addVariable(new StringValue("biomeType." + b.getResourcePath(), b.toString()));
            }
         }

         this.exp.addFunction(new Function("biome.isLike", 1) {
            public IVariant eval(IVariant... params) {
               String biomeName = params[0].asString();
               Biome biome = (Biome)Biome.REGISTRY.getObject(new ResourceLocation(biomeName));
               return biome != null && ConditionsImpl.this.current.areBiomesSameClass(biome) ? Expression.TRUE : Expression.FALSE;
            }
         });
         this.exp.getRPN();
      }

      public boolean match(@Nonnull BiomeInfo info) {
         this.current = info;
         return this.exp.eval().asBoolean();
      }

      private class BiomeTypeVariable extends Variant {
         private final Type type;

         public BiomeTypeVariable(@Nonnull Type t) {
            super("biome.is" + t.getName());
            this.type = t;
         }

         public int compareTo(IVariant o) {
            return Booleans.compare(this.asBoolean(), o.asBoolean());
         }

         public float asNumber() {
            return this.asBoolean() ? 1.0F : 0.0F;
         }

         public String asString() {
            return Boolean.toString(this.asBoolean());
         }

         public boolean asBoolean() {
            return ConditionsImpl.this.current.isBiomeType(this.type);
         }

         @Nonnull
         public Variant add(@Nonnull IVariant term) {
            return new BooleanValue(this.asBoolean() || term.asBoolean());
         }
      }
   }
}
