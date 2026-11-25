package wiresegal.classyhats.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Regex;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootEntryTable;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetNBT;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import wiresegal.classyhats.ClassyHatsConfig;
import wiresegal.classyhats.ClassyHatsContent;
import wiresegal.classyhats.event.CapabilityEvents;
import wiresegal.classyhats.item.ItemHat;

@EventBusSubscriber(
   modid = "classyhats"
)
@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÇ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rJ\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\f\u001a\u00020\u0010H\u0007J\u0010\u0010\u0011\u001a\u00020\u000f2\u0006\u0010\f\u001a\u00020\u0012H\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0013"},
   d2 = {"Lwiresegal/classyhats/util/LootTableFactory;", "", "()V", "FAKE_PLAYER_PATTERN", "Lkotlin/text/Regex;", "elusiveTable", "Lnet/minecraft/world/storage/loot/LootTable;", "pool", "Lnet/minecraft/world/storage/loot/LootPool;", "regularTable", "isTruePlayer", "", "e", "Lnet/minecraft/entity/Entity;", "onBossDeath", "", "Lnet/minecraftforge/event/entity/living/LivingDropsEvent;", "onLootTable", "Lnet/minecraftforge/event/LootTableLoadEvent;", "classyhats"}
)
public final class LootTableFactory {
   private static final LootTable elusiveTable;
   private static final LootTable regularTable;
   private static final LootPool pool;
   private static final Regex FAKE_PLAYER_PATTERN;
   public static final LootTableFactory INSTANCE;

   @SubscribeEvent
   @JvmStatic
   public static final void onLootTable(@NotNull LootTableLoadEvent e) {
      Intrinsics.checkParameterIsNotNull(e, "e");
      ResourceLocation var10000 = e.getName();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "e.name");
      if (Intrinsics.areEqual(var10000.getResourceDomain(), "classyhats")) {
         var10000 = e.getName();
         Intrinsics.checkExpressionValueIsNotNull(var10000, "e.name");
         if (Intrinsics.areEqual(var10000.getResourcePath(), "elusive")) {
            e.setTable(elusiveTable);
         } else {
            var10000 = e.getName();
            Intrinsics.checkExpressionValueIsNotNull(var10000, "e.name");
            if (Intrinsics.areEqual(var10000.getResourcePath(), "regular")) {
               e.setTable(regularTable);
            }
         }
      } else if (ClassyHatsConfig.INSTANCE.getShouldInjectLootChests() && (Intrinsics.areEqual(e.getName(), LootTableList.CHESTS_SIMPLE_DUNGEON) || Intrinsics.areEqual(e.getName(), LootTableList.CHESTS_STRONGHOLD_LIBRARY) || Intrinsics.areEqual(e.getName(), LootTableList.CHESTS_END_CITY_TREASURE) || Intrinsics.areEqual(e.getName(), LootTableList.CHESTS_IGLOO_CHEST) || Intrinsics.areEqual(e.getName(), LootTableList.CHESTS_WOODLAND_MANSION) || Intrinsics.areEqual(e.getName(), LootTableList.CHESTS_DESERT_PYRAMID) || Intrinsics.areEqual(e.getName(), LootTableList.CHESTS_JUNGLE_TEMPLE) || Intrinsics.areEqual(e.getName(), LootTableList.GAMEPLAY_FISHING_TREASURE))) {
         e.getTable().addPool(pool);
      }

   }

   public final boolean isTruePlayer(@NotNull Entity e) {
      Intrinsics.checkParameterIsNotNull(e, "e");
      boolean var2;
      if (e instanceof EntityPlayer) {
         label26: {
            if (!(e instanceof FakePlayer)) {
               Regex var10000 = FAKE_PLAYER_PATTERN;
               String var10001 = ((EntityPlayer)e).getName();
               Intrinsics.checkExpressionValueIsNotNull(var10001, "e.name");
               if (!var10000.matches((CharSequence)var10001)) {
                  break label26;
               }
            }

            var2 = false;
            return var2;
         }
      }

      var2 = true;
      return var2;
   }

   @SubscribeEvent
   public final void onBossDeath(@NotNull LivingDropsEvent e) {
      Intrinsics.checkParameterIsNotNull(e, "e");
      if (ClassyHatsConfig.INSTANCE.getShouldInjectLootBoss()) {
         EntityLivingBase var10000 = e.getEntityLiving();
         Intrinsics.checkExpressionValueIsNotNull(var10000, "e.entityLiving");
         boolean boss = !var10000.isNonBoss();
         EntityLivingBase entity = e.getEntityLiving();
         World world = entity.world;
         if (world instanceof WorldServer && e.isRecentlyHit()) {
            if (boss) {
               DamageSource var15 = e.getSource();
               Intrinsics.checkExpressionValueIsNotNull(var15, "e.source");
               Entity var16 = var15.getTrueSource();
               if (!(var16 instanceof EntityPlayer)) {
                  var16 = null;
               }

               EntityPlayer killer = (EntityPlayer)var16;
               if (killer != null && this.isTruePlayer((Entity)killer)) {
                  List item = (List)(new ArrayList());
                  LootContext context = new LootContext(0.0F, (WorldServer)world, ((WorldServer)world).getLootTableManager(), (Entity)entity, killer, e.getSource());
                  pool.generateLoot((Collection)item, world.rand, context);
                  if (item.size() == 1) {
                     List var10001 = elusiveTable.generateLootForPools(world.rand, context);
                     Intrinsics.checkExpressionValueIsNotNull(var10001, "elusiveTable.generateLoo…ools(world.rand, context)");
                     item.addAll((Collection)var10001);
                  }

                  for(ItemStack i : item) {
                     EntityItem entityItem = new EntityItem(world, entity.posX, entity.posY, entity.posZ, i);
                     entityItem.setDefaultPickupDelay();
                     e.getDrops().add(entityItem);
                  }
               }
            } else if (ClassyHatsConfig.INSTANCE.getNames().contains(String.valueOf(EntityList.getKey((Entity)entity)))) {
               Intrinsics.checkExpressionValueIsNotNull(entity, "entity");
               NBTTagCompound data = entity.getEntityData();
               if (data.hasKey(CapabilityEvents.INSTANCE.getCustomKey())) {
                  String str = data.getString(CapabilityEvents.INSTANCE.getCustomKey());
                  Intrinsics.checkExpressionValueIsNotNull(str, "str");
                  CharSequence var13 = (CharSequence)str;
                  if (var13.length() > 0) {
                     EntityItem entityItem = new EntityItem(world, entity.posX, entity.posY, entity.posZ, ItemHat.ofHat$default(ClassyHatsContent.INSTANCE.getHAT(), str, 0, 2, (Object)null));
                     entityItem.setDefaultPickupDelay();
                     e.getDrops().add(entityItem);
                  }
               }
            }
         }

      }
   }

   private LootTableFactory() {
   }

   static {
      LootTableFactory var0 = new LootTableFactory();
      INSTANCE = var0;
      Iterable $receiver$iv = (Iterable)ClassyHatsConfig.INSTANCE.getHats().values();
      Collection destination$iv$iv = (Collection)(new ArrayList());

      for(Object element$iv$iv : $receiver$iv) {
         HatData it = (HatData)element$iv$iv;
         if (Intrinsics.areEqual(it, ClassyHatsConfig.INSTANCE.getMissingno()) ^ true) {
            destination$iv$iv.add(element$iv$iv);
         }
      }

      List hats = (List)destination$iv$iv;
      Iterable $receiver$iv = (Iterable)hats;
      Collection destination$iv$iv = (Collection)(new ArrayList());

      for(Object element$iv$iv : $receiver$iv) {
         HatData it = (HatData)element$iv$iv;
         if (it.getElusive()) {
            destination$iv$iv.add(element$iv$iv);
         }
      }

      List elusive = (List)destination$iv$iv;
      Iterable $receiver$iv = (Iterable)hats;
      Collection destination$iv$iv = (Collection)(new ArrayList());

      for(Object element$iv$iv : $receiver$iv) {
         HatData it = (HatData)element$iv$iv;
         if (!it.getElusive()) {
            destination$iv$iv.add(element$iv$iv);
         }
      }

      List regular = (List)destination$iv$iv;
      Iterable $receiver$iv = (Iterable)elusive;
      Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));

      for(Object item$iv$iv : $receiver$iv) {
         HatData item$iv$iv = (HatData)item$iv$iv;
         LootEntryItem var16 = new LootEntryItem((Item)ClassyHatsContent.INSTANCE.getHAT(), item$iv$iv.getWeight() == -1 ? 40 : item$iv$iv.getWeight(), 0, (LootFunction[])(new SetNBT[]{new SetNBT(new LootCondition[0], ClassyHatsContent.INSTANCE.getHAT().ofHat(item$iv$iv).getTagCompound())}), new LootCondition[0], item$iv$iv.getName());
         destination$iv$iv.add(var16);
      }

      Collection var24 = (Collection)((List)destination$iv$iv);
      Object[] var10000 = var24.toArray(new LootEntryItem[0]);
      if (var10000 == null) {
         throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
      } else {
         LootEntryItem[] elusiveEntries = (LootEntryItem[])var10000;
         Iterable $receiver$iv = (Iterable)regular;
         Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($receiver$iv, 10)));

         for(Object item$iv$iv : $receiver$iv) {
            HatData it = (HatData)item$iv$iv;
            LootEntryItem var41 = new LootEntryItem((Item)ClassyHatsContent.INSTANCE.getHAT(), it.getWeight() == -1 ? 40 : it.getWeight(), 0, (LootFunction[])(new SetNBT[]{new SetNBT(new LootCondition[0], ClassyHatsContent.INSTANCE.getHAT().ofHat(it).getTagCompound())}), new LootCondition[0], it.getName());
            destination$iv$iv.add(var41);
         }

         Collection var29 = (Collection)((List)destination$iv$iv);
         var10000 = var29.toArray(new LootEntryItem[0]);
         if (var10000 == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
         } else {
            LootEntryItem[] regularEntries = (LootEntryItem[])var10000;
            LootPool elusivePool = new LootPool((LootEntry[])elusiveEntries, new LootCondition[0], new RandomValueRange(1.0F), new RandomValueRange(0.0F), "elusive");
            LootPool regularPool = new LootPool((LootEntry[])regularEntries, new LootCondition[0], new RandomValueRange(1.0F), new RandomValueRange(0.0F), "regular");
            elusiveTable = new LootTable(new LootPool[]{elusivePool});
            regularTable = new LootTable(new LootPool[]{regularPool});
            LootTableList.register(new ResourceLocation("classyhats", "elusive"));
            LootTableList.register(new ResourceLocation("classyhats", "regular"));
            LootTableList.register(new ResourceLocation("classyhats", "combined"));
            pool = new LootPool((LootEntry[])(new LootEntryTable[]{new LootEntryTable(new ResourceLocation("classyhats", "combined"), 1, 0, new LootCondition[0], "classyhats")}), new LootCondition[0], new RandomValueRange(1.0F, 3.0F), new RandomValueRange(0.0F), "classyhats");
            String var17 = "^(?:\\[.*])|(?:ComputerCraft)$";
            FAKE_PLAYER_PATTERN = new Regex(var17);
         }
      }
   }
}
