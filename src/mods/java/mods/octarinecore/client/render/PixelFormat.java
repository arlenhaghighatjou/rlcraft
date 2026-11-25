package mods.octarinecore.client.render;

import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 15},
   bv = {1, 0, 3},
   k = 2,
   d1 = {"\u0000\u001c\n\u0000\n\u0002\u0010 \n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0015\n\u0002\b\n\u001a!\u0010\u0007\u001a\u00020\u00022\b\u0010\b\u001a\u0004\u0018\u00010\t2\n\u0010\n\u001a\u00020\u000b\"\u00020\u0002¢\u0006\u0002\u0010\f\u001a&\u0010\r\u001a\u00020\u00022\u0006\u0010\u000e\u001a\u00020\u00022\u0006\u0010\u000f\u001a\u00020\t2\u0006\u0010\u0010\u001a\u00020\u00022\u0006\u0010\u0011\u001a\u00020\t\u001a\u0015\u0010\u0012\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u0013\u001a\u00020\tH\u0086\u0004\u001a\u0015\u0010\u0014\u001a\u00020\u0002*\u00020\u00022\u0006\u0010\u0013\u001a\u00020\tH\u0086\u0004\" \u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0003\u0010\u0004\"\u0004\b\u0005\u0010\u0006¨\u0006\u0015"},
   d2 = {"brightnessComponents", "", "", "getBrightnessComponents", "()Ljava/util/List;", "setBrightnessComponents", "(Ljava/util/List;)V", "brSum", "multiplier", "", "brightness", "", "(Ljava/lang/Float;[I)I", "brWeighted", "br1", "weight1", "br2", "weight2", "brMul", "f", "colorMul", "BetterFoliage-MC1.12"}
)
@JvmName(
   name = "PixelFormat"
)
public final class PixelFormat {
   @NotNull
   private static List<Integer> brightnessComponents = CollectionsKt.listOf(new Integer[]{Integer.valueOf(20), Integer.valueOf(4)});

   @NotNull
   public static final List<Integer> getBrightnessComponents() {
      return brightnessComponents;
   }

   public static final void setBrightnessComponents(@NotNull List<Integer> var0) {
      Intrinsics.checkParameterIsNotNull(<set-?>, "<set-?>");
      brightnessComponents = <set-?>;
   }

   public static final int brMul(int $this$brMul, float f) {
      int weight = (int)(f * 256.0F);
      int result = 0;
      Iterable $this$forEach$iv = (Iterable)brightnessComponents;
      int $i$f$forEach = 0;

      for(Object element$iv : $this$forEach$iv) {
         int shift = ((Number)element$iv).intValue();
         int $i$a$-forEach-PixelFormat$brMul$1 = 0;
         int raw = $this$brMul >> shift & 15;
         int weighted = raw * weight / 256;
         result |= weighted << shift;
      }

      return result;
   }

   public static final int colorMul(int $this$colorMul, float f) {
      int weight = (int)(f * 256.0F);
      int red = ($this$colorMul >> 16 & 255) * weight / 256;
      int green = ($this$colorMul >> 8 & 255) * weight / 256;
      int blue = ($this$colorMul & 255) * weight / 256;
      return red << 16 | green << 8 | blue;
   }

   public static final int brSum(@Nullable Float multiplier, @NotNull int... brightness) {
      Intrinsics.checkParameterIsNotNull(brightness, "brightness");
      int result = brightnessComponents.size();
      Integer[] $this$forEachIndexed$iv = new Integer[result];

      for(int $i$f$forEachIndexed = 0; $i$f$forEachIndexed < result; ++$i$f$forEachIndexed) {
         int $i$a$-<init>-PixelFormat$brSum$sum$1 = 0;
         Integer var25 = Integer.valueOf(0);
         $this$forEachIndexed$iv[$i$f$forEachIndexed] = var25;
      }

      Integer[] sum = $this$forEachIndexed$iv;
      Iterable $this$forEachIndexed$iv = (Iterable)brightnessComponents;
      int $i$f$forEachIndexed = 0;
      int index$iv = 0;

      for(Object item$iv : $this$forEachIndexed$iv) {
         int item$iv = index$iv++;
         boolean var9 = false;
         if (item$iv < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         int shift = ((Number)item$iv).intValue();
         int idx = item$iv;
         int $i$a$-forEachIndexed-PixelFormat$brSum$1 = 0;
         int $i$f$forEach = 0;

         for(int element$iv : brightness) {
            int $i$a$-forEach-PixelFormat$brSum$1$1 = 0;
            int comp = element$iv >> shift & 15;
            sum[idx] = sum[idx].intValue() + comp;
         }
      }

      int result = 0;
      Iterable $this$forEachIndexed$iv = (Iterable)brightnessComponents;
      index$iv = 0;
      int index$iv = 0;

      for(Object item$iv : $this$forEachIndexed$iv) {
         int var36 = index$iv++;
         boolean var10 = false;
         if (var36 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         int var37 = ((Number)item$iv).intValue();
         int $i$a$-forEachIndexed-PixelFormat$brSum$2 = 0;
         int comp = multiplier == null ? sum[var36].intValue() << var37 : (int)((float)sum[var36].intValue() * multiplier.floatValue()) << var37;
         result |= comp;
      }

      return result;
   }

   public static final int brWeighted(int br1, float weight1, int br2, float weight2) {
      int w1int = (int)(weight1 * 256.0F + 0.5F);
      int w2int = (int)(weight2 * 256.0F + 0.5F);
      int result = 0;
      Iterable $this$forEachIndexed$iv = (Iterable)brightnessComponents;
      int $i$f$forEachIndexed = 0;
      int index$iv = 0;

      for(Object item$iv : $this$forEachIndexed$iv) {
         int var12 = index$iv++;
         boolean var13 = false;
         if (var12 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         int shift = ((Number)item$iv).intValue();
         int $i$a$-forEachIndexed-PixelFormat$brWeighted$1 = 0;
         int comp1 = br1 >> shift & 15;
         int comp2 = br2 >> shift & 15;
         int compWeighted = (comp1 * w1int + comp2 * w2int) / 256;
         result |= (compWeighted & 15) << shift;
      }

      return result;
   }
}
