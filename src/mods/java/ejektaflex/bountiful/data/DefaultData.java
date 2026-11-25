package ejektaflex.bountiful.data;

import ejektaflex.bountiful.api.logic.ItemRange;
import ejektaflex.bountiful.api.logic.pickable.PickableEntry;
import ejektaflex.bountiful.api.logic.picked.PickedEntry;
import ejektaflex.bountiful.api.logic.picked.PickedEntryStack;
import ejektaflex.bountiful.registry.ValueRegistry;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.ranges.IntRange;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u0007\u00a8\u0006\u000b"},
   d2 = {"Lejektaflex/bountiful/data/DefaultData;", "", "()V", "entries", "Lejektaflex/bountiful/registry/ValueRegistry;", "Lejektaflex/bountiful/api/logic/pickable/PickableEntry;", "getEntries", "()Lejektaflex/bountiful/registry/ValueRegistry;", "rewards", "Lejektaflex/bountiful/api/logic/picked/PickedEntryStack;", "getRewards", "Bountiful"}
)
public final class DefaultData {
   @NotNull
   private static final ValueRegistry<PickableEntry> entries;
   @NotNull
   private static final ValueRegistry<PickedEntryStack> rewards;
   public static final DefaultData INSTANCE;

   @NotNull
   public final ValueRegistry<PickableEntry> getEntries() {
      return entries;
   }

   @NotNull
   public final ValueRegistry<PickedEntryStack> getRewards() {
      return rewards;
   }

   private DefaultData() {
   }

   static {
      DefaultData var0 = new DefaultData();
      INSTANCE = var0;
      ValueRegistry var1 = new ValueRegistry();
      PickableEntry[] var10001 = new PickableEntry[23];
      byte $i$a$1$apply = 16;
      var10001[0] = new PickableEntry("minecraft:dirt", new IntRange($i$a$1$apply, 128), 5, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 16;
      var10001[1] = new PickableEntry("minecraft:stone", new IntRange($i$a$1$apply, 128), 10, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 16;
      var10001[2] = new PickableEntry("minecraft:cobblestone", new IntRange($i$a$1$apply, 128), 7, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 2;
      var10001[3] = new PickableEntry("minecraft:fish", new IntRange($i$a$1$apply, 32), 80, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 2;
      var10001[4] = new PickableEntry("minecraft:apple", new IntRange($i$a$1$apply, 32), 70, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 2;
      var10001[5] = new PickableEntry("minecraft:book", new IntRange($i$a$1$apply, 16), 80, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 2;
      var10001[6] = new PickableEntry("minecraft:cactus", new IntRange($i$a$1$apply, 32), 80, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 1;
      var10001[7] = new PickableEntry("minecraft:diamond", new IntRange($i$a$1$apply, 8), 1600, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 1;
      var10001[8] = new PickableEntry("minecraft:dispenser", new IntRange($i$a$1$apply, 6), 200, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 1;
      var10001[9] = new PickableEntry("minecraft:iron_ingot", new IntRange($i$a$1$apply, 32), 200, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 1;
      var10001[10] = new PickableEntry("minecraft:bread", new IntRange($i$a$1$apply, 12), 60, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 1;
      var10001[11] = new PickableEntry("minecraft:rotten_flesh", new IntRange($i$a$1$apply, 24), 55, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 1;
      var10001[12] = new PickableEntry("minecraft:sign", new IntRange($i$a$1$apply, 16), 65, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 1;
      var10001[13] = new PickableEntry("minecraft:slime_ball", new IntRange($i$a$1$apply, 32), 60, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 1;
      var10001[14] = new PickableEntry("minecraft:spider_eye", new IntRange($i$a$1$apply, 16), 75, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 1;
      var10001[15] = new PickableEntry("minecraft:string", new IntRange($i$a$1$apply, 24), 35, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 1;
      var10001[16] = new PickableEntry("minecraft:sugar", new IntRange($i$a$1$apply, 64), 55, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 1;
      var10001[17] = new PickableEntry("minecraft:tripwire_hook", new IntRange($i$a$1$apply, 8), 70, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 1;
      var10001[18] = new PickableEntry("minecraft:wheat", new IntRange($i$a$1$apply, 48), 20, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 1;
      var10001[19] = new PickableEntry("minecraft:leather", new IntRange($i$a$1$apply, 12), 90, 0, (String)null, (List)null, 56, (DefaultConstructorMarker)null);
      $i$a$1$apply = 1;
      var10001[20] = new PickableEntry("entity:minecraft:zombie", new IntRange($i$a$1$apply, 8), 120, 300, (String)null, (List)null, 48, (DefaultConstructorMarker)null);
      $i$a$1$apply = 1;
      var10001[21] = new PickableEntry("entity:minecraft:skeleton", new IntRange($i$a$1$apply, 6), 140, 250, (String)null, (List)null, 48, (DefaultConstructorMarker)null);
      $i$a$1$apply = 1;
      var10001[22] = new PickableEntry("minecraft:potion", new IntRange($i$a$1$apply, 3), 300, 0, "{Potion: \"minecraft:healing\"}", (List)null, 40, (DefaultConstructorMarker)null);
      var1.add(var10001);
      entries = var1;
      var1 = new ValueRegistry();
      var1.add(new PickedEntryStack(new PickedEntry("minecraft:gold_nugget", 100, 0, (String)null, (List)null, (ItemRange)null, 60, (DefaultConstructorMarker)null)), new PickedEntryStack(new PickedEntry("minecraft:gold_ingot", 900, 0, (String)null, (List)null, (ItemRange)null, 60, (DefaultConstructorMarker)null)), new PickedEntryStack(new PickedEntry("minecraft:diamond", 2400, 10, (String)null, (List)null, (ItemRange)null, 56, (DefaultConstructorMarker)null)), new PickedEntryStack(new PickedEntry("minecraft:iron_sword", 750, 5, "{display:{Lore:[\"Sharper than Usual.\"]},ench:[{id:16,lvl:1}]}", (List)null, new ItemRange(1, 1), 16, (DefaultConstructorMarker)null)), new PickedEntryStack(new PickedEntry("minecraft:leather_helmet", 500, 5, (String)null, (List)null, new ItemRange(1, 1), 24, (DefaultConstructorMarker)null)), new PickedEntryStack(new PickedEntry("minecraft:leather_boots", 450, 5, (String)null, (List)null, new ItemRange(1, 1), 24, (DefaultConstructorMarker)null)), new PickedEntryStack(new PickedEntry("minecraft:iron_chestplate", 1800, 5, (String)null, (List)null, new ItemRange(1, 1), 24, (DefaultConstructorMarker)null)), new PickedEntryStack(new PickedEntry("minecraft:golden_chestplate", 1200, 5, (String)null, (List)null, new ItemRange(1, 1), 24, (DefaultConstructorMarker)null)), new PickedEntryStack(new PickedEntry("minecraft:bow", 1300, 10, "{ench:[{id:48,lvl:2}]}", (List)null, new ItemRange(1, 1), 16, (DefaultConstructorMarker)null)));
      rewards = var1;
   }
}
