package mods.betterfoliage.client.render.column;

import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import mods.betterfoliage.client.chunk.ChunkOverlayLayer;
import mods.betterfoliage.client.chunk.ChunkOverlayManager;
import mods.betterfoliage.client.integration.ShadersModIntegration;
import mods.betterfoliage.client.render.ModelColumn;
import mods.betterfoliage.client.render.Utils;
import mods.betterfoliage.loader.Refs;
import mods.octarinecore.client.render.AbstractBlockRenderingHandler;
import mods.octarinecore.client.render.BlockContext;
import mods.octarinecore.client.render.Model;
import mods.octarinecore.client.render.ModelRendererKt;
import mods.octarinecore.client.render.RendererHolder;
import mods.octarinecore.client.resource.ModelHolder;
import mods.octarinecore.common.GeometryKt;
import mods.octarinecore.common.Int3;
import mods.octarinecore.common.Rotation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SideOnly(Side.CLIENT)
@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0002\b\u0016\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b'\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0019\u0010=\u001a\u00020\u000e2\u0006\u0010>\u001a\u00020?2\u0006\u0010@\u001a\u00020?H\u0086\bJ\u0013\u0010A\u001a\u0004\u0018\u00010B2\u0006\u0010C\u001a\u00020?H\u0086\bJ\u0013\u0010D\u001a\u0004\u0018\u00010B2\u0006\u0010C\u001a\u00020?H\u0086\bJ\u0013\u0010E\u001a\u0004\u0018\u00010B2\u0006\u0010C\u001a\u00020?H\u0086\bJ\u0013\u0010F\u001a\u0004\u0018\u00010B2\u0006\u0010C\u001a\u00020?H\u0086\bJ(\u0010G\u001a\u00020\u000e2\u0006\u0010H\u001a\u00020I2\u0006\u0010J\u001a\u00020K2\u0006\u0010L\u001a\u00020M2\u0006\u0010N\u001a\u00020OH\u0016R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\bR\u0011\u0010\u000b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\bR\u0012\u0010\r\u001a\u00020\u000eX\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0011\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\bR\u0011\u0010\u0013\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\bR\u0011\u0010\u0015\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\bR\u0011\u0010\u0017\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\bR\u0011\u0010\u0019\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\bR\u0011\u0010\u001b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\bR\u0012\u0010\u001d\u001a\u00020\u001eX\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b\u001f\u0010 R\u0019\u0010!\u001a\b\u0012\u0004\u0012\u00020#0\"\u00a2\u0006\n\n\u0002\u0010&\u001a\u0004\b$\u0010%R\u0012\u0010'\u001a\u00020(X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b)\u0010*R\u0012\u0010+\u001a\u00020(X\u00a6\u0004\u00a2\u0006\u0006\u001a\u0004\b,\u0010*R\u0011\u0010-\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b.\u0010\bR\u0011\u0010/\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b0\u0010\bR\u0011\u00101\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010\bR\u0011\u00103\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b4\u0010\bR\u0011\u00105\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b6\u0010\bR\u0011\u00107\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b8\u0010\bR\u0011\u00109\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b:\u0010\bR\u0011\u0010;\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b<\u0010\b\u00a8\u0006P"},
   d2 = {"Lmods/betterfoliage/client/render/column/AbstractRenderColumn;", "Lmods/octarinecore/client/render/AbstractBlockRenderingHandler;", "modId", "", "(Ljava/lang/String;)V", "bottomRoundLarge", "Lmods/octarinecore/client/resource/ModelHolder;", "getBottomRoundLarge", "()Lmods/octarinecore/client/resource/ModelHolder;", "bottomRoundSmall", "getBottomRoundSmall", "bottomSquare", "getBottomSquare", "connectPerpendicular", "", "getConnectPerpendicular", "()Z", "extendBottomRoundLarge", "getExtendBottomRoundLarge", "extendBottomRoundSmall", "getExtendBottomRoundSmall", "extendBottomSquare", "getExtendBottomSquare", "extendTopRoundLarge", "getExtendTopRoundLarge", "extendTopRoundSmall", "getExtendTopRoundSmall", "extendTopSquare", "getExtendTopSquare", "overlayLayer", "Lmods/betterfoliage/client/render/column/ColumnRenderLayer;", "getOverlayLayer", "()Lmods/betterfoliage/client/render/column/ColumnRenderLayer;", "quadrantRotations", "", "Lmods/octarinecore/common/Rotation;", "getQuadrantRotations", "()[Lmods/octarinecore/common/Rotation;", "[Lmods/octarinecore/common/Rotation;", "radiusLarge", "", "getRadiusLarge", "()D", "radiusSmall", "getRadiusSmall", "sideRoundLarge", "getSideRoundLarge", "sideRoundSmall", "getSideRoundSmall", "sideSquare", "getSideSquare", "topRoundLarge", "getTopRoundLarge", "topRoundSmall", "getTopRoundSmall", "topSquare", "getTopSquare", "transitionBottom", "getTransitionBottom", "transitionTop", "getTransitionTop", "continuous", "q1", "Lmods/betterfoliage/client/render/column/ColumnLayerData$SpecialRender$QuadrantType;", "q2", "extendBottom", "Lmods/octarinecore/client/render/Model;", "type", "extendTop", "flatBottom", "flatTop", "render", "ctx", "Lmods/octarinecore/client/render/BlockContext;", "dispatcher", "Lnet/minecraft/client/renderer/BlockRendererDispatcher;", "renderer", "Lnet/minecraft/client/renderer/WorldRenderer;", "layer", "Lnet/minecraft/util/BlockRenderLayer;", "BetterFoliage-MC1.12"}
)
public abstract class AbstractRenderColumn extends AbstractBlockRenderingHandler {
   @NotNull
   private final Rotation[] quadrantRotations;
   @NotNull
   private final ModelHolder sideSquare;
   @NotNull
   private final ModelHolder sideRoundSmall;
   @NotNull
   private final ModelHolder sideRoundLarge;
   @NotNull
   private final ModelHolder extendTopSquare;
   @NotNull
   private final ModelHolder extendTopRoundSmall;
   @NotNull
   private final ModelHolder extendTopRoundLarge;
   @NotNull
   private final ModelHolder extendBottomSquare;
   @NotNull
   private final ModelHolder extendBottomRoundSmall;
   @NotNull
   private final ModelHolder extendBottomRoundLarge;
   @NotNull
   private final ModelHolder topSquare;
   @NotNull
   private final ModelHolder topRoundSmall;
   @NotNull
   private final ModelHolder topRoundLarge;
   @NotNull
   private final ModelHolder bottomSquare;
   @NotNull
   private final ModelHolder bottomRoundSmall;
   @NotNull
   private final ModelHolder bottomRoundLarge;
   @NotNull
   private final ModelHolder transitionTop;
   @NotNull
   private final ModelHolder transitionBottom;

   @NotNull
   public final Rotation[] getQuadrantRotations() {
      return this.quadrantRotations;
   }

   @NotNull
   public abstract ColumnRenderLayer getOverlayLayer();

   public abstract boolean getConnectPerpendicular();

   public abstract double getRadiusSmall();

   public abstract double getRadiusLarge();

   @NotNull
   public final ModelHolder getSideSquare() {
      return this.sideSquare;
   }

   @NotNull
   public final ModelHolder getSideRoundSmall() {
      return this.sideRoundSmall;
   }

   @NotNull
   public final ModelHolder getSideRoundLarge() {
      return this.sideRoundLarge;
   }

   @NotNull
   public final ModelHolder getExtendTopSquare() {
      return this.extendTopSquare;
   }

   @NotNull
   public final ModelHolder getExtendTopRoundSmall() {
      return this.extendTopRoundSmall;
   }

   @NotNull
   public final ModelHolder getExtendTopRoundLarge() {
      return this.extendTopRoundLarge;
   }

   @Nullable
   public final Model extendTop(@NotNull ColumnLayerData.SpecialRender.QuadrantType type) {
      // $FF: Couldn't be decompiled
   }

   @NotNull
   public final ModelHolder getExtendBottomSquare() {
      return this.extendBottomSquare;
   }

   @NotNull
   public final ModelHolder getExtendBottomRoundSmall() {
      return this.extendBottomRoundSmall;
   }

   @NotNull
   public final ModelHolder getExtendBottomRoundLarge() {
      return this.extendBottomRoundLarge;
   }

   @Nullable
   public final Model extendBottom(@NotNull ColumnLayerData.SpecialRender.QuadrantType type) {
      // $FF: Couldn't be decompiled
   }

   @NotNull
   public final ModelHolder getTopSquare() {
      return this.topSquare;
   }

   @NotNull
   public final ModelHolder getTopRoundSmall() {
      return this.topRoundSmall;
   }

   @NotNull
   public final ModelHolder getTopRoundLarge() {
      return this.topRoundLarge;
   }

   @Nullable
   public final Model flatTop(@NotNull ColumnLayerData.SpecialRender.QuadrantType type) {
      // $FF: Couldn't be decompiled
   }

   @NotNull
   public final ModelHolder getBottomSquare() {
      return this.bottomSquare;
   }

   @NotNull
   public final ModelHolder getBottomRoundSmall() {
      return this.bottomRoundSmall;
   }

   @NotNull
   public final ModelHolder getBottomRoundLarge() {
      return this.bottomRoundLarge;
   }

   @Nullable
   public final Model flatBottom(@NotNull ColumnLayerData.SpecialRender.QuadrantType type) {
      // $FF: Couldn't be decompiled
   }

   @NotNull
   public final ModelHolder getTransitionTop() {
      return this.transitionTop;
   }

   @NotNull
   public final ModelHolder getTransitionBottom() {
      return this.transitionBottom;
   }

   public final boolean continuous(@NotNull ColumnLayerData.SpecialRender.QuadrantType q1, @NotNull ColumnLayerData.SpecialRender.QuadrantType q2) {
      int $i$f$continuous = 0;
      Intrinsics.checkParameterIsNotNull(q1, "q1");
      Intrinsics.checkParameterIsNotNull(q2, "q2");
      return q1 == q2 || (q1 == ColumnLayerData.SpecialRender.QuadrantType.SQUARE || q1 == ColumnLayerData.SpecialRender.QuadrantType.INVISIBLE) && (q2 == ColumnLayerData.SpecialRender.QuadrantType.SQUARE || q2 == ColumnLayerData.SpecialRender.QuadrantType.INVISIBLE);
   }

   public boolean render(@NotNull BlockContext ctx, @NotNull BlockRendererDispatcher dispatcher, @NotNull WorldRenderer renderer, @NotNull BlockRenderLayer layer) {
      // $FF: Couldn't be decompiled
   }

   public AbstractRenderColumn(@NotNull String modId) {
      Intrinsics.checkParameterIsNotNull(modId, "modId");
      super(modId);
      byte var2 = 4;
      Rotation[] var3 = new Rotation[var2];

      for(int var4 = 0; var4 < var2; ++var4) {
         int $i$a$-<init>-AbstractRenderColumn$quadrantRotations$1 = 0;
         Rotation var10 = Rotation.Companion.getRot90()[EnumFacing.UP.ordinal()].times(var4);
         var3[var4] = var10;
      }

      this.quadrantRotations = var3;
      this.sideSquare = this.model(null.INSTANCE);
      this.sideRoundSmall = this.model(new Function1<Model, Unit>() {
         public final void invoke(@NotNull Model $this$model) {
            Intrinsics.checkParameterIsNotNull($this$model, "$receiver");
            ModelColumn.columnSide$default($this$model, AbstractRenderColumn.this.getRadiusSmall(), -0.5D, 0.5D, (Function1)null, 8, (Object)null);
         }
      });
      this.sideRoundLarge = this.model(new Function1<Model, Unit>() {
         public final void invoke(@NotNull Model $this$model) {
            Intrinsics.checkParameterIsNotNull($this$model, "$receiver");
            ModelColumn.columnSide$default($this$model, AbstractRenderColumn.this.getRadiusLarge(), -0.5D, 0.5D, (Function1)null, 8, (Object)null);
         }
      });
      this.extendTopSquare = this.model(new Function1<Model, Unit>() {
         public final void invoke(@NotNull Model $this$model) {
            Intrinsics.checkParameterIsNotNull($this$model, "$receiver");
            ModelColumn.columnSideSquare($this$model, 0.5D, 0.5D + AbstractRenderColumn.this.getRadiusLarge(), ModelColumn.topExtension(AbstractRenderColumn.this.getRadiusLarge()));
         }
      });
      this.extendTopRoundSmall = this.model(new Function1<Model, Unit>() {
         public final void invoke(@NotNull Model $this$model) {
            Intrinsics.checkParameterIsNotNull($this$model, "$receiver");
            ModelColumn.columnSide($this$model, AbstractRenderColumn.this.getRadiusSmall(), 0.5D, 0.5D + AbstractRenderColumn.this.getRadiusLarge(), ModelColumn.topExtension(AbstractRenderColumn.this.getRadiusLarge()));
         }
      });
      this.extendTopRoundLarge = this.model(new Function1<Model, Unit>() {
         public final void invoke(@NotNull Model $this$model) {
            Intrinsics.checkParameterIsNotNull($this$model, "$receiver");
            ModelColumn.columnSide($this$model, AbstractRenderColumn.this.getRadiusLarge(), 0.5D, 0.5D + AbstractRenderColumn.this.getRadiusLarge(), ModelColumn.topExtension(AbstractRenderColumn.this.getRadiusLarge()));
         }
      });
      this.extendBottomSquare = this.model(new Function1<Model, Unit>() {
         public final void invoke(@NotNull Model $this$model) {
            Intrinsics.checkParameterIsNotNull($this$model, "$receiver");
            ModelColumn.columnSideSquare($this$model, -0.5D - AbstractRenderColumn.this.getRadiusLarge(), -0.5D, ModelColumn.bottomExtension(AbstractRenderColumn.this.getRadiusLarge()));
         }
      });
      this.extendBottomRoundSmall = this.model(new Function1<Model, Unit>() {
         public final void invoke(@NotNull Model $this$model) {
            Intrinsics.checkParameterIsNotNull($this$model, "$receiver");
            ModelColumn.columnSide($this$model, AbstractRenderColumn.this.getRadiusSmall(), -0.5D - AbstractRenderColumn.this.getRadiusLarge(), -0.5D, ModelColumn.bottomExtension(AbstractRenderColumn.this.getRadiusLarge()));
         }
      });
      this.extendBottomRoundLarge = this.model(new Function1<Model, Unit>() {
         public final void invoke(@NotNull Model $this$model) {
            Intrinsics.checkParameterIsNotNull($this$model, "$receiver");
            ModelColumn.columnSide($this$model, AbstractRenderColumn.this.getRadiusLarge(), -0.5D - AbstractRenderColumn.this.getRadiusLarge(), -0.5D, ModelColumn.bottomExtension(AbstractRenderColumn.this.getRadiusLarge()));
         }
      });
      this.topSquare = this.model(null.INSTANCE);
      this.topRoundSmall = this.model(new Function1<Model, Unit>() {
         public final void invoke(@NotNull Model $this$model) {
            Intrinsics.checkParameterIsNotNull($this$model, "$receiver");
            ModelColumn.columnLid$default($this$model, AbstractRenderColumn.this.getRadiusSmall(), (Function1)null, 2, (Object)null);
         }
      });
      this.topRoundLarge = this.model(new Function1<Model, Unit>() {
         public final void invoke(@NotNull Model $this$model) {
            Intrinsics.checkParameterIsNotNull($this$model, "$receiver");
            ModelColumn.columnLid$default($this$model, AbstractRenderColumn.this.getRadiusLarge(), (Function1)null, 2, (Object)null);
         }
      });
      this.bottomSquare = this.model(null.INSTANCE);
      this.bottomRoundSmall = this.model(new Function1<Model, Unit>() {
         public final void invoke(@NotNull Model $this$model) {
            Intrinsics.checkParameterIsNotNull($this$model, "$receiver");
            ModelColumn.columnLid($this$model, AbstractRenderColumn.this.getRadiusSmall(), null.INSTANCE);
         }
      });
      this.bottomRoundLarge = this.model(new Function1<Model, Unit>() {
         public final void invoke(@NotNull Model $this$model) {
            Intrinsics.checkParameterIsNotNull($this$model, "$receiver");
            ModelColumn.columnLid($this$model, AbstractRenderColumn.this.getRadiusLarge(), null.INSTANCE);
         }
      });
      this.transitionTop = this.model(new Function1<Model, Unit>() {
         public final void invoke(@NotNull Model $this$model) {
            Intrinsics.checkParameterIsNotNull($this$model, "$receiver");
            Utils.mix($this$model, AbstractRenderColumn.this.getSideRoundLarge().getModel(), AbstractRenderColumn.this.getSideRoundSmall().getModel(), null.INSTANCE);
         }
      });
      this.transitionBottom = this.model(new Function1<Model, Unit>() {
         public final void invoke(@NotNull Model $this$model) {
            Intrinsics.checkParameterIsNotNull($this$model, "$receiver");
            Utils.mix($this$model, AbstractRenderColumn.this.getSideRoundSmall().getModel(), AbstractRenderColumn.this.getSideRoundLarge().getModel(), null.INSTANCE);
         }
      });
   }
}
