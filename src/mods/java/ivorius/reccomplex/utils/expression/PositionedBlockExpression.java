package ivorius.reccomplex.utils.expression;

import ivorius.ivtoolkit.tools.MCRegistry;
import ivorius.ivtoolkit.world.MockWorld;
import ivorius.ivtoolkit.world.WorldCache;
import ivorius.ivtoolkit.world.MockWorld.Cache;
import ivorius.ivtoolkit.world.MockWorld.Real;
import ivorius.reccomplex.utils.RCBlockLogic;
import ivorius.reccomplex.utils.algebra.BoolFunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.FunctionExpressionCache;
import ivorius.reccomplex.utils.algebra.RCBoolAlgebra;
import ivorius.reccomplex.utils.algebra.SupplierCache;
import java.text.ParseException;
import java.util.function.Function;
import net.minecraft.block.BlockSapling;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class PositionedBlockExpression extends BoolFunctionExpressionCache<PositionedBlockExpression.Argument, Object> {
   public static final String BLOCK_PREFIX = "block.";
   public static final String IS_PREFIX = "is:";
   public static final String SUSTAIN_PREFIX = "sustains.";
   public static final String BLOCKS_PREFIX = "blocks:";
   public final MCRegistry registry;

   public PositionedBlockExpression(MCRegistry registry) {
      super(RCBoolAlgebra.algebra(), Boolean.valueOf(true), TextFormatting.GREEN + "Any Block");
      this.registry = registry;
      this.addTypes(new PositionedBlockExpression.BlockVariableType("block.", "", registry), new Function[]{(v) -> {
         return v.alias("", "");
      }});
      this.addTypes(new PositionedBlockExpression.IsVariableType("is:", ""), new Function[0]);
      this.addType(new PositionedBlockExpression.SustainVariableType("sustains.", ""));
      this.addType(new PositionedBlockExpression.BlocksVariableType("blocks:", ""));
   }

   public static class Argument {
      public MockWorld world;
      public BlockPos pos;
      public IBlockState state;

      public Argument(MockWorld world, BlockPos pos, IBlockState state) {
         this.world = world;
         this.pos = pos;
         this.state = state;
      }

      public static PositionedBlockExpression.Argument at(MockWorld world, BlockPos pos) {
         return new PositionedBlockExpression.Argument(world, pos, world.getBlockState(pos));
      }

      public static PositionedBlockExpression.Argument at(World world, BlockPos pos) {
         return at(new Real(world), pos);
      }

      public static PositionedBlockExpression.Argument at(WorldCache cache, BlockPos pos) {
         return at(new Cache(cache), pos);
      }
   }

   public static class BlockVariableType extends DelegatingVariableType<Boolean, PositionedBlockExpression.Argument, Object, IBlockState, Object, BlockExpression> {
      public final MCRegistry registry;

      public BlockVariableType(String prefix, String suffix, MCRegistry registry) {
         super(prefix, suffix);
         this.registry = registry;
      }

      public IBlockState convertEvaluateArgument(String var, PositionedBlockExpression.Argument argument) {
         return argument.state;
      }

      public BlockExpression createCache() {
         return new BlockExpression(this.registry);
      }
   }

   public static class BlocksVariableType extends FunctionExpressionCache.VariableType<Boolean, PositionedBlockExpression.Argument, Object> {
      public BlocksVariableType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public Function<SupplierCache<PositionedBlockExpression.Argument>, Boolean> parse(String var) throws ParseException {
         byte var3 = -1;
         switch(var.hashCode()) {
         case -103677777:
            if (var.equals("movement")) {
               var3 = 0;
            }
            break;
         case 102970646:
            if (var.equals("light")) {
               var3 = 1;
            }
         }

         switch(var3) {
         case 0:
            return (argument) -> {
               return ((PositionedBlockExpression.Argument)argument.get()).state.getMaterial().blocksMovement();
            };
         case 1:
            return (argument) -> {
               return ((PositionedBlockExpression.Argument)argument.get()).state.getMaterial().blocksLight();
            };
         default:
            throw new ParseException("Unknown Type: " + var, 0);
         }
      }

      public FunctionExpressionCache.Validity validity(String var, Object object) {
         return !var.equals("movement") && !var.equals("light") ? FunctionExpressionCache.Validity.ERROR : FunctionExpressionCache.Validity.KNOWN;
      }
   }

   public static class IsVariableType extends FunctionExpressionCache.VariableType<Boolean, PositionedBlockExpression.Argument, Object> {
      public IsVariableType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public Function<SupplierCache<PositionedBlockExpression.Argument>, Boolean> parse(String var) throws ParseException {
         byte var3 = -1;
         switch(var.hashCode()) {
         case -1106736996:
            if (var.equals("leaves")) {
               var3 = 0;
            }
            break;
         case -1102567108:
            if (var.equals("liquid")) {
               var3 = 4;
            }
            break;
         case -683104455:
            if (var.equals("foliage")) {
               var3 = 2;
            }
            break;
         case 96586:
            if (var.equals("air")) {
               var3 = 1;
            }
            break;
         case 3314400:
            if (var.equals("lava")) {
               var3 = 6;
            }
            break;
         case 112903447:
            if (var.equals("water")) {
               var3 = 5;
            }
            break;
         case 430558734:
            if (var.equals("replaceable")) {
               var3 = 3;
            }
         }

         switch(var3) {
         case 0:
            return (argument) -> {
               return ((PositionedBlockExpression.Argument)argument.get()).state.getMaterial() == Material.LEAVES || ((PositionedBlockExpression.Argument)argument.get()).state.getBlock().isLeaves(((PositionedBlockExpression.Argument)argument.get()).state, ((PositionedBlockExpression.Argument)argument.get()).world.asWorld(), ((PositionedBlockExpression.Argument)argument.get()).pos);
            };
         case 1:
            return (argument) -> {
               return ((PositionedBlockExpression.Argument)argument.get()).state.getMaterial() == Material.AIR || ((PositionedBlockExpression.Argument)argument.get()).state.getBlock().isAir(((PositionedBlockExpression.Argument)argument.get()).state, ((PositionedBlockExpression.Argument)argument.get()).world.asWorld(), ((PositionedBlockExpression.Argument)argument.get()).pos);
            };
         case 2:
            return (argument) -> {
               return RCBlockLogic.isFoliage(((PositionedBlockExpression.Argument)argument.get()).state, ((PositionedBlockExpression.Argument)argument.get()).world.asWorld(), ((PositionedBlockExpression.Argument)argument.get()).pos);
            };
         case 3:
            return (argument) -> {
               return ((PositionedBlockExpression.Argument)argument.get()).state.getBlock().isReplaceable(((PositionedBlockExpression.Argument)argument.get()).world.asWorld(), ((PositionedBlockExpression.Argument)argument.get()).pos);
            };
         case 4:
            return (argument) -> {
               return ((PositionedBlockExpression.Argument)argument.get()).state.getMaterial() instanceof MaterialLiquid;
            };
         case 5:
            return (argument) -> {
               return ((PositionedBlockExpression.Argument)argument.get()).state.getMaterial() == Material.WATER;
            };
         case 6:
            return (argument) -> {
               return ((PositionedBlockExpression.Argument)argument.get()).state.getMaterial() == Material.LAVA;
            };
         default:
            throw new ParseException("Unknown Type: " + var, 0);
         }
      }

      public FunctionExpressionCache.Validity validity(String var, Object object) {
         return !var.equals("leaves") && !var.equals("air") && !var.equals("foliage") && !var.equals("replaceable") && !var.equals("liquid") && !var.equals("water") && !var.equals("lava") ? FunctionExpressionCache.Validity.ERROR : FunctionExpressionCache.Validity.KNOWN;
      }
   }

   public static class SustainVariableType extends FunctionExpressionCache.VariableType<Boolean, PositionedBlockExpression.Argument, Object> {
      public SustainVariableType(String prefix, String suffix) {
         super(prefix, suffix);
      }

      public Function<SupplierCache<PositionedBlockExpression.Argument>, Boolean> parse(String var) throws ParseException {
         byte var3 = -1;
         switch(var.hashCode()) {
         case 94417146:
            if (var.equals("cacti")) {
               var3 = 2;
            }
            break;
         case 110624917:
            if (var.equals("trees")) {
               var3 = 0;
            }
            break;
         case 376176411:
            if (var.equals("mushrooms")) {
               var3 = 1;
            }
         }

         switch(var3) {
         case 0:
            return (argument) -> {
               return ((PositionedBlockExpression.Argument)argument.get()).state.getBlock().canSustainPlant(((PositionedBlockExpression.Argument)argument.get()).state, ((PositionedBlockExpression.Argument)argument.get()).world.asWorld(), ((PositionedBlockExpression.Argument)argument.get()).pos, EnumFacing.UP, (BlockSapling)Blocks.SAPLING);
            };
         case 1:
            return (argument) -> {
               return ((PositionedBlockExpression.Argument)argument.get()).state.getBlock() == Blocks.DIRT || ((PositionedBlockExpression.Argument)argument.get()).state.getBlock() == Blocks.GRASS || ((PositionedBlockExpression.Argument)argument.get()).state.getBlock() == Blocks.MYCELIUM;
            };
         case 2:
            return (argument) -> {
               return Blocks.CACTUS.canBlockStay(((PositionedBlockExpression.Argument)argument.get()).world.asWorld(), ((PositionedBlockExpression.Argument)argument.get()).pos);
            };
         default:
            throw new ParseException("Unknown Type: " + var, 0);
         }
      }

      public FunctionExpressionCache.Validity validity(String var, Object object) {
         return !var.equals("trees") && !var.equals("mushrooms") && !var.equals("cacti") ? FunctionExpressionCache.Validity.ERROR : FunctionExpressionCache.Validity.KNOWN;
      }
   }
}
