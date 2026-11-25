package mods.betterfoliage.loader;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import mods.octarinecore.metaprog.InstructionList;
import mods.octarinecore.metaprog.MethodTransformContext;
import mods.octarinecore.metaprog.Reflection;
import mods.octarinecore.metaprog.Transformer;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.AbstractInsnNode;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0006\u0010\u0006\u001a\u00020\u0007R\u0011\u0010\u0003\u001a\u00020\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0003\u0010\u0005¨\u0006\b"},
   d2 = {"Lmods/betterfoliage/loader/BetterFoliageTransformer;", "Lmods/octarinecore/metaprog/Transformer;", "()V", "isOptifinePresent", "", "()Z", "setupClient", "", "BetterFoliage-MC1.12"}
)
public final class BetterFoliageTransformer extends Transformer {
   private final boolean isOptifinePresent = Reflection.allAvailable(Refs.INSTANCE.getOptifineClassTransformer());

   public final boolean isOptifinePresent() {
      return this.isOptifinePresent;
   }

   public final void setupClient() {
      this.transformMethod(Refs.INSTANCE.getShowBarrierParticles(), new Function1<MethodTransformContext, Unit>() {
         public final void invoke(@NotNull MethodTransformContext $this$transformMethod) {
            Intrinsics.checkParameterIsNotNull($this$transformMethod, "$receiver");
            AbstractInsnNode var10000 = $this$transformMethod.find($this$transformMethod.invokeRef(Refs.INSTANCE.getRandomDisplayTick()));
            if (var10000 == null || $this$transformMethod.insertAfter(var10000, new Function1<InstructionList, Unit>() {
               public final void invoke(@NotNull InstructionList $this$insertAfter) {
                  Intrinsics.checkParameterIsNotNull($this$insertAfter, "$receiver");
                  BetterFoliageTransformer.this.getLog().info("[BetterFoliageLoader] Applying random display tick call hook");
                  $this$insertAfter.varinsn(25, 0);
                  $this$insertAfter.varinsn(25, 11);
                  $this$insertAfter.varinsn(25, 7);
                  InstructionList.invokeStatic$default($this$insertAfter, Refs.INSTANCE.getOnRandomDisplayTick(), false, 2, (Object)null);
               }
            }) == null) {
               BetterFoliageTransformer.this.getLog().warn("[BetterFoliageLoader] Failed to apply random display tick call hook!");
               Unit var2 = Unit.INSTANCE;
            }

         }
      });
      this.transformMethod(Refs.INSTANCE.getGetAmbientOcclusionLightValue(), new Function1<MethodTransformContext, Unit>() {
         public final void invoke(@NotNull MethodTransformContext $this$transformMethod) {
            Intrinsics.checkParameterIsNotNull($this$transformMethod, "$receiver");
            AbstractInsnNode var10000 = $this$transformMethod.find(174);
            if (var10000 == null || $this$transformMethod.insertBefore(var10000, new Function1<InstructionList, Unit>() {
               public final void invoke(@NotNull InstructionList $this$insertBefore) {
                  Intrinsics.checkParameterIsNotNull($this$insertBefore, "$receiver");
                  BetterFoliageTransformer.this.getLog().info("[BetterFoliageLoader] Applying getAmbientOcclusionLightValue() override");
                  $this$insertBefore.varinsn(25, 0);
                  InstructionList.invokeStatic$default($this$insertBefore, Refs.INSTANCE.getGetAmbientOcclusionLightValueOverride(), false, 2, (Object)null);
               }
            }) == null) {
               BetterFoliageTransformer.this.getLog().warn("[BetterFoliageLoader] Failed to apply getAmbientOcclusionLightValue() override!");
               Unit var2 = Unit.INSTANCE;
            }

         }
      });
      this.transformMethod(Refs.INSTANCE.getUseNeighborBrightness(), new Function1<MethodTransformContext, Unit>() {
         public final void invoke(@NotNull MethodTransformContext $this$transformMethod) {
            Intrinsics.checkParameterIsNotNull($this$transformMethod, "$receiver");
            AbstractInsnNode var10000 = $this$transformMethod.find(172);
            if (var10000 == null || $this$transformMethod.insertBefore(var10000, new Function1<InstructionList, Unit>() {
               public final void invoke(@NotNull InstructionList $this$insertBefore) {
                  Intrinsics.checkParameterIsNotNull($this$insertBefore, "$receiver");
                  BetterFoliageTransformer.this.getLog().info("[BetterFoliageLoader] Applying useNeighborBrightness() override");
                  $this$insertBefore.varinsn(25, 0);
                  InstructionList.invokeStatic$default($this$insertBefore, Refs.INSTANCE.getUseNeighborBrightnessOverride(), false, 2, (Object)null);
               }
            }) == null) {
               BetterFoliageTransformer.this.getLog().warn("[BetterFoliageLoader] Failed to apply useNeighborBrightness() override!");
               Unit var2 = Unit.INSTANCE;
            }

         }
      });
      this.transformMethod(Refs.INSTANCE.getDoesSideBlockRendering(), new Function1<MethodTransformContext, Unit>() {
         public final void invoke(@NotNull MethodTransformContext $this$transformMethod) {
            Intrinsics.checkParameterIsNotNull($this$transformMethod, "$receiver");
            AbstractInsnNode var10000 = $this$transformMethod.find(172);
            if (var10000 == null || $this$transformMethod.insertBefore(var10000, new Function1<InstructionList, Unit>() {
               public final void invoke(@NotNull InstructionList $this$insertBefore) {
                  Intrinsics.checkParameterIsNotNull($this$insertBefore, "$receiver");
                  BetterFoliageTransformer.this.getLog().info("[BetterFoliageLoader] Applying doesSideBlockRendering() override");
                  $this$insertBefore.varinsn(25, 1);
                  $this$insertBefore.varinsn(25, 2);
                  $this$insertBefore.varinsn(25, 3);
                  InstructionList.invokeStatic$default($this$insertBefore, Refs.INSTANCE.getDoesSideBlockRenderingOverride(), false, 2, (Object)null);
               }
            }) == null) {
               BetterFoliageTransformer.this.getLog().warn("[BetterFoliageLoader] Failed to apply doesSideBlockRendering() override!");
               Unit var2 = Unit.INSTANCE;
            }

         }
      });
      this.transformMethod(Refs.INSTANCE.isOpaqueCube(), new Function1<MethodTransformContext, Unit>() {
         public final void invoke(@NotNull MethodTransformContext $this$transformMethod) {
            Intrinsics.checkParameterIsNotNull($this$transformMethod, "$receiver");
            AbstractInsnNode var10000 = $this$transformMethod.find(172);
            if (var10000 == null || $this$transformMethod.insertBefore(var10000, new Function1<InstructionList, Unit>() {
               public final void invoke(@NotNull InstructionList $this$insertBefore) {
                  Intrinsics.checkParameterIsNotNull($this$insertBefore, "$receiver");
                  BetterFoliageTransformer.this.getLog().info("[BetterFoliageLoader] Applying isOpaqueCube() override");
                  $this$insertBefore.varinsn(25, 0);
                  InstructionList.invokeStatic$default($this$insertBefore, Refs.INSTANCE.isOpaqueCubeOverride(), false, 2, (Object)null);
               }
            }) == null) {
               BetterFoliageTransformer.this.getLog().warn("[BetterFoliageLoader] Failed to apply isOpaqueCube() override!");
               Unit var2 = Unit.INSTANCE;
            }

         }
      });
      this.transformMethod(Refs.INSTANCE.getSetupModelRegistry(), new Function1<MethodTransformContext, Unit>() {
         public final void invoke(@NotNull MethodTransformContext $this$transformMethod) {
            Intrinsics.checkParameterIsNotNull($this$transformMethod, "$receiver");
            AbstractInsnNode var10000 = $this$transformMethod.find($this$transformMethod.invokeName("addAll"));
            if (var10000 == null || $this$transformMethod.insertAfter(var10000, new Function1<InstructionList, Unit>() {
               public final void invoke(@NotNull InstructionList $this$insertAfter) {
                  Intrinsics.checkParameterIsNotNull($this$insertAfter, "$receiver");
                  BetterFoliageTransformer.this.getLog().info("[BetterFoliageLoader] Applying ModelLoader lifecycle callback");
                  $this$insertAfter.varinsn(25, 0);
                  InstructionList.invokeStatic$default($this$insertAfter, Refs.INSTANCE.getOnAfterLoadModelDefinitions(), false, 2, (Object)null);
               }
            }) == null) {
               BetterFoliageTransformer.this.getLog().warn("[BetterFoliageLoader] Failed to apply ModelLoader lifecycle callback!");
               Unit var2 = Unit.INSTANCE;
            }

         }
      });
      this.transformMethod(Refs.INSTANCE.getRebuildChunk(), new Function1<MethodTransformContext, Unit>() {
         public final void invoke(@NotNull MethodTransformContext $this$transformMethod) {
            Intrinsics.checkParameterIsNotNull($this$transformMethod, "$receiver");
            $this$transformMethod.applyWriterFlags(2, 1);
            AbstractInsnNode var10000 = $this$transformMethod.find($this$transformMethod.invokeRef(Refs.INSTANCE.getRenderBlock()));
            if (var10000 != null) {
               $this$transformMethod.replace(var10000, new Function1<InstructionList, Unit>() {
                  public final void invoke(@NotNull InstructionList $this$replace) {
                     Intrinsics.checkParameterIsNotNull($this$replace, "$receiver");
                     BetterFoliageTransformer.this.getLog().info("[BetterFoliageLoader] Applying RenderChunk block render override");
                     $this$replace.varinsn(25, BetterFoliageTransformer.this.isOptifinePresent() ? 22 : 20);
                     InstructionList.invokeStatic$default($this$replace, Refs.INSTANCE.getRenderWorldBlock(), false, 2, (Object)null);
                  }
               });
            }

            if (BetterFoliageTransformer.this.isOptifinePresent()) {
               var10000 = $this$transformMethod.find($this$transformMethod.varinsn(54, 23));
               if (var10000 != null) {
                  $this$transformMethod.insertAfter(var10000, new Function1<InstructionList, Unit>() {
                     public final void invoke(@NotNull InstructionList $this$insertAfter) {
                        Intrinsics.checkParameterIsNotNull($this$insertAfter, "$receiver");
                        BetterFoliageTransformer.this.getLog().info("[BetterFoliageLoader] Applying RenderChunk block layer override (Optifine)");
                        $this$insertAfter.varinsn(25, 19);
                        $this$insertAfter.varinsn(25, 18);
                        $this$insertAfter.varinsn(25, 22);
                        InstructionList.invokeStatic$default($this$insertAfter, Refs.INSTANCE.getCanRenderBlockInLayer(), false, 2, (Object)null);
                        $this$insertAfter.varinsn(54, 23);
                     }
                  });
               }
            } else {
               var10000 = $this$transformMethod.find($this$transformMethod.invokeRef(Refs.INSTANCE.getCanRenderInLayer()));
               if (var10000 != null) {
                  $this$transformMethod.replace(var10000, new Function1<InstructionList, Unit>() {
                     public final void invoke(@NotNull InstructionList $this$replace) {
                        Intrinsics.checkParameterIsNotNull($this$replace, "$receiver");
                        BetterFoliageTransformer.this.getLog().info("[BetterFoliageLoader] Applying RenderChunk block layer override (non-Optifine)");
                        InstructionList.invokeStatic$default($this$replace, Refs.INSTANCE.getCanRenderBlockInLayer(), false, 2, (Object)null);
                     }
                  });
               }
            }

         }
      });
      this.transformMethod(Refs.INSTANCE.getAOF_constructor(), new Function1<MethodTransformContext, Unit>() {
         public final void invoke(@NotNull MethodTransformContext $this$transformMethod) {
            Intrinsics.checkParameterIsNotNull($this$transformMethod, "$receiver");
            BetterFoliageTransformer.this.getLog().info("[BetterFoliageLoader] Setting AmbientOcclusionFace constructor public");
            $this$transformMethod.makePublic();
         }
      });
      this.transformMethod(Refs.INSTANCE.getPushEntity_state(), new Function1<MethodTransformContext, Unit>() {
         public final void invoke(@NotNull MethodTransformContext $this$transformMethod) {
            Intrinsics.checkParameterIsNotNull($this$transformMethod, "$receiver");
            AbstractInsnNode var10000 = $this$transformMethod.find($this$transformMethod.invokeRef(Refs.INSTANCE.getPushEntity_num()));
            if (var10000 == null || $this$transformMethod.insertBefore(var10000, new Function1<InstructionList, Unit>() {
               public final void invoke(@NotNull InstructionList $this$insertBefore) {
                  Intrinsics.checkParameterIsNotNull($this$insertBefore, "$receiver");
                  BetterFoliageTransformer.this.getLog().info("[BetterFoliageLoader] Applying SVertexBuilder.pushEntity() block ID override");
                  $this$insertBefore.varinsn(25, 0);
                  InstructionList.invokeStatic$default($this$insertBefore, Refs.INSTANCE.getGetBlockIdOverride(), false, 2, (Object)null);
               }
            }) == null) {
               BetterFoliageTransformer.this.getLog().warn("[BetterFoliageLoader] Failed to apply SVertexBuilder.pushEntity() block ID override!");
               Unit var2 = Unit.INSTANCE;
            }

         }
      });
   }

   public BetterFoliageTransformer() {
      Side var10000 = FMLLaunchHandler.side();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "FMLLaunchHandler.side()");
      if (var10000.isClient()) {
         this.setupClient();
      }

   }
}
