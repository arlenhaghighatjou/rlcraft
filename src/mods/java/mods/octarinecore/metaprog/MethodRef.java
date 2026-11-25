package mods.octarinecore.metaprog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import mods.octarinecore.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\b\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B3\b\u0016\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0004\u0012\u0012\u0010\b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\t\"\u00020\u0004\u00a2\u0006\u0002\u0010\nB9\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u000b\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0004\u0012\u0012\u0010\b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\t\"\u00020\u0004\u00a2\u0006\u0002\u0010\fJ\u000e\u0010\u0016\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0018J-\u0010\u0019\u001a\u0004\u0018\u00010\u001a2\u0006\u0010\u001b\u001a\u00020\u001a2\u0016\u0010\u001c\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010\u001a0\t\"\u0004\u0018\u00010\u001a\u00a2\u0006\u0002\u0010\u001dJ!\u0010\u001e\u001a\u0004\u0018\u00010\u001a2\u0012\u0010\u001c\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u001a0\t\"\u00020\u001a\u00a2\u0006\u0002\u0010\u001fJ\u000e\u0010 \u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0018J\n\u0010!\u001a\u0004\u0018\u00010\u0002H\u0016R\u001b\u0010\b\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\t\u00a2\u0006\n\n\u0002\u0010\u000f\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0007\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0011\u0010\u000b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0011\u00a8\u0006\""},
   d2 = {"Lmods/octarinecore/metaprog/MethodRef;", "Lmods/octarinecore/metaprog/Resolvable;", "Ljava/lang/reflect/Method;", "parentClass", "Lmods/octarinecore/metaprog/ClassRef;", "mcpName", "", "returnType", "argTypes", "", "(Lmods/octarinecore/metaprog/ClassRef;Ljava/lang/String;Lmods/octarinecore/metaprog/ClassRef;[Lmods/octarinecore/metaprog/ClassRef;)V", "srgName", "(Lmods/octarinecore/metaprog/ClassRef;Ljava/lang/String;Ljava/lang/String;Lmods/octarinecore/metaprog/ClassRef;[Lmods/octarinecore/metaprog/ClassRef;)V", "getArgTypes", "()[Lmods/octarinecore/metaprog/ClassRef;", "[Lmods/octarinecore/metaprog/ClassRef;", "getMcpName", "()Ljava/lang/String;", "getParentClass", "()Lmods/octarinecore/metaprog/ClassRef;", "getReturnType", "getSrgName", "asmDescriptor", "namespace", "Lmods/octarinecore/metaprog/Namespace;", "invoke", "", "receiver", "args", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", "invokeStatic", "([Ljava/lang/Object;)Ljava/lang/Object;", "name", "resolve", "BetterFoliage-MC1.12"}
)
public final class MethodRef extends Resolvable<Method> {
   @NotNull
   private final ClassRef parentClass;
   @NotNull
   private final String mcpName;
   @NotNull
   private final String srgName;
   @NotNull
   private final ClassRef returnType;
   @NotNull
   private final ClassRef[] argTypes;

   @NotNull
   public final String name(@NotNull Namespace namespace) {
      // $FF: Couldn't be decompiled
   }

   @NotNull
   public final String asmDescriptor(@NotNull Namespace namespace) {
      Intrinsics.checkParameterIsNotNull(namespace, "namespace");
      StringBuilder var10000 = (new StringBuilder()).append('(');
      ClassRef[] $this$fold$iv = this.argTypes;
      StringBuilder var13 = var10000;
      int $i$f$map = 0;
      Collection destination$iv$iv = (Collection)(new ArrayList($this$fold$iv.length));
      int $i$f$mapTo = 0;

      for(Object item$iv$iv : $this$fold$iv) {
         int $i$a$-map-MethodRef$asmDescriptor$1 = 0;
         String var15 = ((ClassRef)item$iv$iv).asmDescriptor(namespace);
         destination$iv$iv.add(var15);
      }

      List var14 = (List)destination$iv$iv;
      Iterable var16 = (Iterable)var14;
      String var17 = "";
      int $i$f$fold = 0;
      destination$iv$iv = var17;

      for(Object element$iv : var16) {
         String var21 = (String)element$iv;
         int $i$a$-fold-MethodRef$asmDescriptor$2 = 0;
         destination$iv$iv = destination$iv$iv + var21;
      }

      return var13.append(destination$iv$iv).append(')').append(this.returnType.asmDescriptor(namespace)).toString();
   }

   @Nullable
   public Method resolve() {
      Method var36;
      if (this.parentClass.getElement() != null) {
         Object[] $this$any$iv = this.argTypes;
         int $i$f$any = 0;

         label58: {
            for(Object element$iv : $this$any$iv) {
               int $i$a$-any-MethodRef$resolve$1 = 0;
               if (((ClassRef)element$iv).getElement() == null) {
                  var10000 = true;
                  break label58;
               }
            }

            var10000 = false;
         }

         if (!var10000) {
            Object[] $this$map$iv = this.argTypes;
            int $i$f$map = 0;
            Collection destination$iv$iv = (Collection)(new ArrayList($this$map$iv.length));
            int $i$f$mapTo = 0;

            for(Object item$iv$iv : $this$map$iv) {
               int $i$a$-map-MethodRef$resolve$args$1 = 0;
               var36 = (Method)((ClassRef)item$iv$iv).getElement();
               if (var36 == null) {
                  Intrinsics.throwNpe();
               }

               Class var14 = (Class)var36;
               destination$iv$iv.add(var14);
            }

            $this$map$iv = (Collection)((List)destination$iv$iv);
            $i$f$map = 0;
            Object[] var38 = $this$map$iv.toArray(new Class[0]);
            if (var38 == null) {
               throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
            }

            Class[] args = (Class[])var38;
            Iterable $this$map$iv = (Iterable)CollectionsKt.listOf(new String[]{this.srgName, this.mcpName});
            $i$f$map = 0;
            destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));
            $i$f$mapTo = 0;

            for(Object item$iv$iv : $this$map$iv) {
               String var33 = (String)item$iv$iv;
               int $i$a$-map-MethodRef$resolve$2 = 0;
               Method var35 = (Method)Utils.tryDefault((Object)null, new MethodRef$resolve$$inlined$map$lambda$1(var33, this, args));
               destination$iv$iv.add(var35);
            }

            Method var39 = (Method)CollectionsKt.firstOrNull(CollectionsKt.filterNotNull((Iterable)((List)destination$iv$iv)));
            if (var39 != null) {
               Method var19 = var39;
               $i$f$map = 0;
               boolean var24 = false;
               $i$f$mapTo = 0;
               var19.setAccessible(true);
               var36 = var19;
            } else {
               var36 = null;
            }

            return var36;
         }
      }

      var36 = null;
      return var36;
   }

   @Nullable
   public final Object invoke(@NotNull Object receiver, @NotNull Object... args) {
      Intrinsics.checkParameterIsNotNull(receiver, "receiver");
      Intrinsics.checkParameterIsNotNull(args, "args");
      Method var10000 = (Method)this.getElement();
      return var10000 != null ? var10000.invoke(receiver, Arrays.copyOf(args, args.length)) : null;
   }

   @Nullable
   public final Object invokeStatic(@NotNull Object... args) {
      Intrinsics.checkParameterIsNotNull(args, "args");
      Method var10000 = (Method)this.getElement();
      return var10000 != null ? var10000.invoke((Object)null, Arrays.copyOf(args, args.length)) : null;
   }

   @NotNull
   public final ClassRef getParentClass() {
      return this.parentClass;
   }

   @NotNull
   public final String getMcpName() {
      return this.mcpName;
   }

   @NotNull
   public final String getSrgName() {
      return this.srgName;
   }

   @NotNull
   public final ClassRef getReturnType() {
      return this.returnType;
   }

   @NotNull
   public final ClassRef[] getArgTypes() {
      return this.argTypes;
   }

   public MethodRef(@NotNull ClassRef parentClass, @NotNull String mcpName, @NotNull String srgName, @NotNull ClassRef returnType, @NotNull ClassRef... argTypes) {
      Intrinsics.checkParameterIsNotNull(parentClass, "parentClass");
      Intrinsics.checkParameterIsNotNull(mcpName, "mcpName");
      Intrinsics.checkParameterIsNotNull(srgName, "srgName");
      Intrinsics.checkParameterIsNotNull(returnType, "returnType");
      Intrinsics.checkParameterIsNotNull(argTypes, "argTypes");
      super();
      this.parentClass = parentClass;
      this.mcpName = mcpName;
      this.srgName = srgName;
      this.returnType = returnType;
      this.argTypes = argTypes;
   }

   public MethodRef(@NotNull ClassRef parentClass, @NotNull String mcpName, @NotNull ClassRef returnType, @NotNull ClassRef... argTypes) {
      Intrinsics.checkParameterIsNotNull(parentClass, "parentClass");
      Intrinsics.checkParameterIsNotNull(mcpName, "mcpName");
      Intrinsics.checkParameterIsNotNull(returnType, "returnType");
      Intrinsics.checkParameterIsNotNull(argTypes, "argTypes");
      this(parentClass, mcpName, mcpName, returnType, (ClassRef[])Arrays.copyOf(argTypes, argTypes.length));
   }
}
