package ejektaflex.bountiful.proxy;

import ejektaflex.bountiful.Bountiful;
import ejektaflex.bountiful.api.block.ITileEntityBountyBoard;
import ejektaflex.bountiful.api.events.PopulateBountyBoardEvent;
import ejektaflex.bountiful.api.ext.ExtMiscKt;
import ejektaflex.bountiful.api.logic.pickable.PickableEntry;
import ejektaflex.bountiful.api.logic.picked.PickedEntry;
import ejektaflex.bountiful.api.logic.picked.PickedEntryStack;
import ejektaflex.bountiful.api.stats.BountifulStats;
import ejektaflex.bountiful.cap.CapManager;
import ejektaflex.bountiful.cap.GlobBoardProvider;
import ejektaflex.bountiful.cap.IGlobalBoard;
import ejektaflex.bountiful.cap.Storage;
import ejektaflex.bountiful.config.BountifulIO;
import ejektaflex.bountiful.data.BountyData;
import ejektaflex.bountiful.data.DefaultData;
import ejektaflex.bountiful.item.ItemBounty;
import ejektaflex.bountiful.logic.BountyChecker;
import ejektaflex.bountiful.logic.BountyHolder;
import ejektaflex.bountiful.registry.BountyRegistry;
import ejektaflex.bountiful.registry.RewardRegistry;
import ejektaflex.bountiful.worldgen.VillageBoardComponent;
import ejektaflex.bountiful.worldgen.VillageBoardCreationHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0016\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u0007J\u0010\u0010\b\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\tH\u0007J\u0010\u0010\n\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u000bH\u0007J\u0010\u0010\f\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\rH\u0007J\u0010\u0010\u000e\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u000fH\u0016J\u0010\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0011H\u0016¨\u0006\u0012"},
   d2 = {"Lejektaflex/bountiful/proxy/CommonProxy;", "Lejektaflex/bountiful/proxy/IProxy;", "()V", "attachCaps", "", "e", "Lnet/minecraftforge/event/AttachCapabilitiesEvent;", "Lnet/minecraft/world/World;", "entityLivingDeath", "Lnet/minecraftforge/event/entity/living/LivingDeathEvent;", "onBoardPost", "Lejektaflex/bountiful/api/events/PopulateBountyBoardEvent;", "onWorldTick", "Lnet/minecraftforge/fml/common/gameevent/TickEvent$WorldTickEvent;", "postInit", "Lnet/minecraftforge/fml/common/event/FMLPostInitializationEvent;", "preInit", "Lnet/minecraftforge/fml/common/event/FMLPreInitializationEvent;", "Bountiful"}
)
public class CommonProxy implements IProxy {
   public void preInit(@NotNull FMLPreInitializationEvent e) {
      Intrinsics.checkParameterIsNotNull(e, "e");
      CapabilityManager.INSTANCE.register(IGlobalBoard.class, new Storage(), null.INSTANCE);
      VillagerRegistry.instance().registerVillageCreationHandler(new VillageBoardCreationHandler());
      MapGenStructureIO.registerStructureComponent(VillageBoardComponent.class, VillageBoardComponent.Companion.getVILLAGE_BOARD_ID().toString());
   }

   @SubscribeEvent
   public final void entityLivingDeath(@NotNull LivingDeathEvent e) {
      Intrinsics.checkParameterIsNotNull(e, "e");
      DamageSource var10000 = e.getSource();
      Intrinsics.checkExpressionValueIsNotNull(var10000, "e.source");
      if (var10000.getTrueSource() instanceof EntityPlayer) {
         var10000 = e.getSource();
         Intrinsics.checkExpressionValueIsNotNull(var10000, "e.source");
         Entity var19 = var10000.getTrueSource();
         if (var19 == null) {
            throw new TypeCastException("null cannot be cast to non-null type net.minecraft.entity.player.EntityPlayer");
         }

         EntityPlayer player = (EntityPlayer)var19;
         NonNullList var20 = player.inventory.mainInventory;
         Intrinsics.checkExpressionValueIsNotNull(player.inventory.mainInventory, "player.inventory.mainInventory");
         Iterable $receiver$iv = (Iterable)var20;
         Collection destination$iv$iv = (Collection)(new ArrayList());

         for(Object element$iv$iv : $receiver$iv) {
            ItemStack it = (ItemStack)element$iv$iv;
            Intrinsics.checkExpressionValueIsNotNull(it, "it");
            if (it.getItem() instanceof ItemBounty) {
               destination$iv$iv.add(element$iv$iv);
            }
         }

         List bountyStacks = (List)destination$iv$iv;
         Collection var13 = (Collection)bountyStacks;
         if (!var13.isEmpty()) {
            for(Object element$iv : (Iterable)bountyStacks) {
               ItemStack stack = (ItemStack)element$iv;
               BountyData.Companion var21 = BountyData.Companion;
               Intrinsics.checkExpressionValueIsNotNull(stack, "stack");
               if (var21.isValidBounty(stack)) {
                  BountyData data = BountyData.Companion.from(stack);
                  BountyChecker var22 = BountyChecker.INSTANCE;
                  EntityLivingBase var10004 = e.getEntityLiving();
                  Intrinsics.checkExpressionValueIsNotNull(var10004, "e.entityLiving");
                  var22.tryTakeEntities(player, data, stack, var10004);
               }
            }
         }
      }

   }

   @SubscribeEvent
   public final void onWorldTick(@NotNull final WorldTickEvent e) {
      Intrinsics.checkParameterIsNotNull(e, "e");
      if (!e.world.isRemote && Bountiful.INSTANCE.getConfig().getGlobalBounties() && e.phase == Phase.END) {
         World var10000 = e.world;
         Intrinsics.checkExpressionValueIsNotNull(e.world, "e.world");
         Capability var10001 = CapManager.INSTANCE.getCAP_BOARD();
         if (var10001 == null) {
            Intrinsics.throwNpe();
         }

         ExtMiscKt.ifHasCapability(var10000, var10001, new Function1<IGlobalBoard, Unit>() {
            public final void invoke(@NotNull IGlobalBoard $receiver) {
               Intrinsics.checkParameterIsNotNull($receiver, "receiver$0");
               BountyHolder var10000 = $receiver.getHolder();
               World var10001 = e.world;
               Intrinsics.checkExpressionValueIsNotNull(e.world, "e.world");
               var10000.update(var10001, (ITileEntityBountyBoard)null);
            }
         });
      }

   }

   @SubscribeEvent
   public final void attachCaps(@NotNull AttachCapabilitiesEvent<World> e) {
      Intrinsics.checkParameterIsNotNull(e, "e");
      e.addCapability(new ResourceLocation("bountiful", "GlobalData"), (ICapabilityProvider)(new GlobBoardProvider()));
   }

   @SubscribeEvent
   public final void onBoardPost(@NotNull PopulateBountyBoardEvent e) {
      Intrinsics.checkParameterIsNotNull(e, "e");
      if (!e.getStack().hasTagCompound()) {
         e.setCanceled(true);
      }

      ITileEntityBountyBoard var10000 = e.getBoard();
      if (var10000 != null) {
         ITileEntityBountyBoard var2 = var10000;
         if (var2.getNewBoard()) {
            var2.setNewBoard(false);
            e.setCanceled(true);
         }
      }

   }

   public void postInit(@NotNull FMLPostInitializationEvent e) {
      Intrinsics.checkParameterIsNotNull(e, "e");
      String $receiver$iv = "bounties.json";
      BountifulIO.INSTANCE.populateConfigFolder(Bountiful.INSTANCE.getConfigDir(), DefaultData.INSTANCE.getEntries().getItems(), $receiver$iv);
      List invalids = BountifulIO.INSTANCE.hotReloadBounties();
      Collection it = (Collection)invalids;
      if (!it.isEmpty()) {
         throw (Throwable)(new Exception("'bountiful/bounties.json' contains one or more invalid bounties. Invalid bounty objectives: " + invalids));
      } else {
         int minObjectives = Bountiful.INSTANCE.getConfig().getBountyAmountRange().getLast();
         if (BountyRegistry.INSTANCE.getItems().size() < minObjectives) {
            throw (Throwable)(new Exception("Config file needs more bounties! Must have at least " + minObjectives + " bounty objectives to choose from, according to the current config."));
         } else {
            $receiver$iv = "rewards.json";
            BountifulIO var10000 = BountifulIO.INSTANCE;
            File var10001 = Bountiful.INSTANCE.getConfigDir();
            Iterable var24 = (Iterable)DefaultData.INSTANCE.getRewards().getItems();
            File var29 = var10001;
            BountifulIO $i$a$4$forEach = var10000;
            Collection destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(var24, 10)));

            for(Object item$iv$iv : var24) {
               PickedEntryStack item = (PickedEntryStack)item$iv$iv;
               PickedEntry var13 = item.getGenericPick();
               destination$iv$iv.add(var13);
            }

            List var12 = (List)destination$iv$iv;
            $i$a$4$forEach.populateConfigFolder(var29, var12, $receiver$iv);
            List invalids = BountifulIO.INSTANCE.hotReloadRewards();
            Collection $i$f$forEach = (Collection)invalids;
            if (!$i$f$forEach.isEmpty()) {
               throw (Throwable)(new Exception("'bountiful/rewards.json' contains one or more invalid rewards. Invalid rewards: " + invalids));
            } else {
               BountifulStats.INSTANCE.register();
               $receiver$iv = "Bounties: " + BountyRegistry.INSTANCE.getItems().size();
               System.out.println($receiver$iv);

               for(Object element$iv : (Iterable)BountyRegistry.INSTANCE.getItems()) {
                  PickableEntry it = (PickableEntry)element$iv;
                  System.out.println(it);
               }

               $receiver$iv = "Rewards: " + RewardRegistry.INSTANCE.getItems().size();
               System.out.println($receiver$iv);

               for(Object element$iv : (Iterable)RewardRegistry.INSTANCE.getItems()) {
                  PickedEntryStack it = (PickedEntryStack)element$iv;
                  System.out.println(it);
               }

            }
         }
      }
   }

   public CommonProxy() {
   }

   public void init(@NotNull FMLInitializationEvent e) {
      Intrinsics.checkParameterIsNotNull(e, "e");
      IProxy.DefaultImpls.init(this, e);
   }
}
