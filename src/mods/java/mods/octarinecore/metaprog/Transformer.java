package mods.octarinecore.metaprog;

import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.launchwrapper.IClassTransformer;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u0016\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J(\u0010\u0014\u001a\u0004\u0018\u00010\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u00172\b\u0010\u0019\u001a\u0004\u0018\u00010\u0015H\u0016J'\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u000b2\u0017\u0010\u001d\u001a\u0013\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u000e0\f¢\u0006\u0002\b\u000fR\u0019\u0010\u0003\u001a\n \u0005*\u0004\u0018\u00010\u00040\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R=\u0010\b\u001a%\u0012!\u0012\u001f\u0012\u0004\u0012\u00020\u000b\u0012\u0015\u0012\u0013\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u000e0\f¢\u0006\u0002\b\u000f0\n0\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013¨\u0006\u001e"},
   d2 = {"Lmods/octarinecore/metaprog/Transformer;", "Lnet/minecraft/launchwrapper/IClassTransformer;", "()V", "log", "Lorg/apache/logging/log4j/Logger;", "kotlin.jvm.PlatformType", "getLog", "()Lorg/apache/logging/log4j/Logger;", "methodTransformers", "", "Lkotlin/Pair;", "Lmods/octarinecore/metaprog/MethodRef;", "Lkotlin/Function1;", "Lmods/octarinecore/metaprog/MethodTransformContext;", "", "Lkotlin/ExtensionFunctionType;", "getMethodTransformers", "()Ljava/util/List;", "setMethodTransformers", "(Ljava/util/List;)V", "transform", "", "name", "", "transformedName", "classData", "transformMethod", "", "method", "trans", "BetterFoliage-MC1.12"}
)
public class Transformer implements IClassTransformer {
   private final Logger log = LogManager.getLogger(this);
   @NotNull
   private List<Pair<MethodRef, Function1<MethodTransformContext, Unit>>> methodTransformers;

   public final Logger getLog() {
      return this.log;
   }

   @NotNull
   public final List<Pair<MethodRef, Function1<MethodTransformContext, Unit>>> getMethodTransformers() {
      return this.methodTransformers;
   }

   public final void setMethodTransformers(@NotNull List<Pair<MethodRef, Function1<MethodTransformContext, Unit>>> var1) {
      Intrinsics.checkParameterIsNotNull(<set-?>, "<set-?>");
      this.methodTransformers = <set-?>;
   }

   public final boolean transformMethod(@NotNull MethodRef method, @NotNull Function1<? super MethodTransformContext, Unit> trans) {
      Intrinsics.checkParameterIsNotNull(method, "method");
      Intrinsics.checkParameterIsNotNull(trans, "trans");
      return this.methodTransformers.add(TuplesKt.to(method, trans));
   }

   @Nullable
   public byte[] transform(@Nullable String name, @Nullable String transformedName, @Nullable byte[] classData) {
      // $FF: Couldn't be decompiled
   }

   public Transformer() {
      boolean var1 = false;
      ArrayList var3 = new ArrayList();
      this.methodTransformers = (List)var3;
   }
}
