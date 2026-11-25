package ivorius.reccomplex.commands.parameters;

import ivorius.ivtoolkit.blocks.BlockSurfacePos;
import ivorius.ivtoolkit.math.AxisAlignedTransform2D;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameter;
import ivorius.reccomplex..shadow.mcopts.commands.parameters.Parameters;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nonnull;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.util.math.BlockPos;

public class IvP {
   public IvP() {
   }

   public static BlockSurfacePos parseSurfacePos(BlockPos blockpos, String x, String z, boolean centerBlock) throws NumberInvalidException {
      return BlockSurfacePos.from(new BlockPos(CommandBase.parseDouble((double)blockpos.getX(), x, -30000000, 30000000, centerBlock), 0.0D, CommandBase.parseDouble((double)blockpos.getZ(), z, -30000000, 30000000, centerBlock)));
   }

   @Nonnull
   public static Function<Parameter<String>, Parameter<BlockSurfacePos>> surfacePos(Parameter<String> zp, BlockPos ref, boolean centerBlock) {
      return (p) -> {
         return p.orElse("~").flatMap((x) -> {
            return zp.orElse("~").map((z) -> {
               return parseSurfacePos(ref, x, z, centerBlock);
            });
         });
      };
   }

   public static Function<Parameter<String>, Parameter<BlockSurfacePos>> surfacePos(BlockPos ref, boolean centerBlock) {
      return (p) -> {
         return (Parameter)surfacePos(p.move(1), ref, centerBlock).apply(p);
      };
   }

   public static Parameter<AxisAlignedTransform2D> transform(Parameter<String> p, boolean mirror) {
      return !p.has(1) && !mirror ? new Parameter(p, (s) -> {
         return null;
      }) : p.map(CommandBase::parseInt).map((i) -> {
         return i.intValue() > 40 ? i.intValue() / 90 : i.intValue();
      }).orElse(Integer.valueOf(0)).map((r) -> {
         return AxisAlignedTransform2D.from(r.intValue(), mirror);
      });
   }

   public static Function<Parameters, Parameter<BlockSurfacePos>> surfacePos(String x, String z, BlockPos ref, boolean centerBlock) {
      return (ps) -> {
         return ps.get(x).to(surfacePos(ps.get(z), ref, centerBlock));
      };
   }

   public static Function<Parameters, Parameter<AxisAlignedTransform2D>> transform(String rotation, String mirror) throws CommandException {
      return (ps) -> {
         return ps.get(rotation).to(IvP::transform, Boolean.valueOf(ps.has(mirror)));
      };
   }
}
