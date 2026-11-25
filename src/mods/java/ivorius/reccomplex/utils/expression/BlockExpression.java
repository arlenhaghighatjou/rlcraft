package ivorius.reccomplex.utils.expression;

import com.google.common.base.Splitter;
import com.google.common.primitives.Ints;
import ivorius.ivtoolkit.blocks.BlockStates;
import ivorius.ivtoolkit.gui.IntegerRange;
import ivorius.ivtoolkit.tools.MCRegistry;
import ivorius.reccomplex.utils.IntegerRanges;
import ivorius.reccomplex.utils.algebra.BoolFunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.FunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.RCBoolAlgebra;
import ivorius.reccomplex.utils.algebra.SupplierCache;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.lang3.tuple.Pair;

public class BlockExpression extends BoolFunctionExpressionCache<IBlockState, Object> {
   public static final String BLOCK_ID_PREFIX = "id=";
   public static final String DOMAIN_PREFIX = "domain=";
   public static final String METADATA_PREFIX = "metadata=";
   public static final String PROPERTY_PREFIX = "property[";
   public final MCRegistry registry;

   public BlockExpression(MCRegistry registry) {
      super(RCBoolAlgebra.algebra(), Boolean.valueOf(true), TextFormatting.GREEN + "Any Block");
      this.registry = registry;
      this.addTypes(new BlockExpression.IDVariableType("id=", "", registry), new Function[]{(t) -> {
         return t.alias("", "");
      }});
      this.addTypes(new BlockExpression.DomainVariableType("domain=", "", registry), new Function[0]);
      this.addTypes(new BlockExpression.MetadataVariableType("metadata=", ""), new Function[]{(t) -> {
         return t.alias("#", "");
      }});
      this.addTypes(new BlockExpression.PropertyVariableType("property[", ""), new Function[]{(t) -> {
         return t.alias("$[", "");
      }});
   }

   public static String of(MCRegistry registry, Block block) {
      return registry.idFromBlock(block).toString();
   }

   public static String of(MCRegistry registry, Block block, Integer metadata) {
      return String.format("%s & %s%d", registry.idFromBlock(block), "metadata=", metadata);
   }

   public static String of(MCRegistry registry, Block block, IntegerRange range) {
      return String.format("%s & %s%d-%d", registry.idFromBlock(block), "metadata=", range.min, range.max);
   }

   public class DomainVariableType extends FunctionExpressionCache.VariableType<Boolean, IBlockState, Object> {
      public MCRegistry registry;

      public DomainVariableType(String prefix, String suffix, MCRegistry registry) {
         super(prefix, suffix);
         this.registry = registry;
      }

      public Function<SupplierCache<IBlockState>, Boolean> parse(String var) {
         return (s) -> {
            return this.registry.idFromBlock(((IBlockState)s.get()).getBlock()).getResourceDomain().equals(var);
         };
      }

      public FunctionExpressionCache.Validity validity(String var, Object object) {
         return Loader.instance().getIndexedModList().keySet().contains(var) ? FunctionExpressionCache.Validity.KNOWN : FunctionExpressionCache.Validity.UNKNOWN;
      }
   }

   public class IDVariableType extends FunctionExpressionCache.VariableType<Boolean, IBlockState, Object> {
      public MCRegistry registry;

      public IDVariableType(String prefix, String suffix, MCRegistry registry) {
         super(prefix, suffix);
         this.registry = registry;
      }

      public Function<SupplierCache<IBlockState>, Boolean> parse(String var) {
         Block block = this.registry.blockFromID(new ResourceLocation(var));
         return (s) -> {
            return ((IBlockState)s.get()).getBlock() == block;
         };
      }

      public FunctionExpressionCache.Validity validity(String var, Object object) {
         ResourceLocation location = new ResourceLocation(var);
         return this.registry.blockFromID(location) == Blocks.AIR && !location.equals(Block.REGISTRY.getNameForObject(Blocks.AIR)) ? FunctionExpressionCache.Validity.UNKNOWN : FunctionExpressionCache.Validity.KNOWN;
      }
   }

   public class MetadataVariableType extends FunctionExpressionCache.VariableType<Boolean, IBlockState, Object> {
      public MetadataVariableType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public IntegerRange parseMetadataExp(String var) {
         if (var.contains("-")) {
            List<String> split = Splitter.on('-').splitToList(var);
            if (split.size() != 2) {
               return null;
            } else {
               Integer left = this.parseMetadata((String)split.get(0));
               Integer right = this.parseMetadata((String)split.get(1));
               return left != null && right != null ? IntegerRanges.from(left.intValue(), right.intValue()) : null;
            }
         } else {
            Integer meta = this.parseMetadata(var);
            return meta != null ? new IntegerRange(meta.intValue(), meta.intValue()) : null;
         }
      }

      public Integer parseMetadata(String var) {
         Integer integer = Ints.tryParse(var);
         return integer != null && integer.intValue() >= 0 && integer.intValue() < 16 ? integer : null;
      }

      public Function<SupplierCache<IBlockState>, Boolean> parse(String var) {
         IntegerRange range = this.parseMetadataExp(var);
         return (s) -> {
            int metadata = BlockStates.toMetadata((IBlockState)s.get());
            return range != null && metadata >= range.min && metadata <= range.max;
         };
      }

      public FunctionExpressionCache.Validity validity(String var, Object object) {
         return this.parseMetadataExp(var) != null ? FunctionExpressionCache.Validity.KNOWN : FunctionExpressionCache.Validity.ERROR;
      }
   }

   public class PropertyVariableType extends FunctionExpressionCache.VariableType<Boolean, IBlockState, Object> {
      public PropertyVariableType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public Pair<String, String> parsePropery(String var) {
         int close = var.indexOf("]=");
         return close >= 0 ? Pair.of(var.substring(0, close), var.substring(close + 2)) : null;
      }

      public Function<SupplierCache<IBlockState>, Boolean> parse(String var) {
         Pair<String, String> pair = this.parsePropery(var);
         return (state) -> {
            return pair != null && this.getProperty(((IBlockState)state.get()).getBlock(), (String)pair.getLeft()).filter((property) -> {
               return property.parseValue((String)pair.getRight()).orNull() == ((IBlockState)state.get()).getValue(property);
            }).isPresent();
         };
      }

      public FunctionExpressionCache.Validity validity(String var, Object object) {
         Pair<String, String> pair = this.parsePropery(var);
         if (pair != null) {
            Stream var10000 = Block.REGISTRY.getKeys().stream();
            RegistryNamespacedDefaultedByKey var10001 = Block.REGISTRY;
            Block.REGISTRY.getClass();
            return var10000.map(var10001::getObject).anyMatch((block) -> {
               return this.hasPropertyEntry(pair, block);
            }) ? FunctionExpressionCache.Validity.KNOWN : FunctionExpressionCache.Validity.UNKNOWN;
         } else {
            return FunctionExpressionCache.Validity.ERROR;
         }
      }

      protected boolean hasPropertyEntry(Pair<String, String> pair, Block block) {
         return this.getProperty(block, (String)pair.getLeft()).filter((property) -> {
            return property.parseValue((String)pair.getRight()).isPresent();
         }).isPresent();
      }

      private Optional<IProperty<?>> getProperty(Block block, String key) {
         return block.getDefaultState().getProperties().keySet().stream().filter((property) -> {
            return property.getName().equals(key);
         }).findFirst();
      }

      protected String getVarRepresentation(String var, Object o) {
         Pair<String, String> pair = this.parsePropery(var);
         return pair != null ? this.getRepresentation(this.keyValidity((String)pair.getLeft(), o)) + (String)pair.getLeft() + TextFormatting.BLUE + "]=" + this.getRepresentation(this.validity(var, o)) + (String)pair.getRight() : super.getVarRepresentation(var, o);
      }

      protected FunctionExpressionCache.Validity keyValidity(String var, Object o) {
         Stream var10000 = Block.REGISTRY.getKeys().stream();
         RegistryNamespacedDefaultedByKey var10001 = Block.REGISTRY;
         Block.REGISTRY.getClass();
         return var10000.map(var10001::getObject).anyMatch((block) -> {
            return this.getProperty(block, var).isPresent();
         }) ? FunctionExpressionCache.Validity.KNOWN : FunctionExpressionCache.Validity.UNKNOWN;
      }
   }
}
