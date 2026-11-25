package ejektaflex.compat;

import ejektaflex.bountiful.data.BountyData;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.IStageData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bJ\u001c\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\"\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\u000b\u001a\u00020\f2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\t0\b\u00a8\u0006\u0010"},
   d2 = {"Lejektaflex/compat/FacadeGameStages;", "", "()V", "anyPlayerHas", "", "world", "Lnet/minecraft/world/World;", "stages", "", "", "stagesStillNeededFor", "player", "Lnet/minecraft/entity/player/EntityPlayer;", "bountyData", "Lejektaflex/bountiful/data/BountyData;", "current", "Bountiful"}
)
public final class FacadeGameStages {
   public static final FacadeGameStages INSTANCE;

   public final boolean anyPlayerHas(@NotNull World world, @NotNull List<String> stages) {
      List var11;
      label34: {
         Intrinsics.checkParameterIsNotNull(world, "world");
         Intrinsics.checkParameterIsNotNull(stages, "stages");
         MinecraftServer var10000 = world.getMinecraftServer();
         if (var10000 != null) {
            PlayerList var10 = var10000.getPlayerList();
            if (var10 != null) {
               var11 = var10.getPlayers();
               break label34;
            }
         }

         var11 = null;
      }

      List allPlayers = var11;
      boolean var12;
      if (allPlayers != null) {
         Iterable $receiver$iv = (Iterable)allPlayers;
         if ($receiver$iv instanceof Collection && ((Collection)$receiver$iv).isEmpty()) {
            var12 = false;
         } else {
            Iterator var5 = $receiver$iv.iterator();

            while(true) {
               if (!var5.hasNext()) {
                  var12 = false;
                  break;
               }

               Object element$iv = var5.next();
               EntityPlayerMP it = (EntityPlayerMP)element$iv;
               if (GameStageHelper.hasAllOf((EntityPlayer)it, (Collection)stages)) {
                  var12 = true;
                  break;
               }
            }
         }
      } else {
         var12 = false;
      }

      return var12;
   }

   @NotNull
   public final List<String> stagesStillNeededFor(@NotNull EntityPlayer player, @NotNull BountyData bountyData) {
      Intrinsics.checkParameterIsNotNull(player, "player");
      Intrinsics.checkParameterIsNotNull(bountyData, "bountyData");
      return this.stagesStillNeededFor(player, bountyData.requiredStages());
   }

   @NotNull
   public final List<String> stagesStillNeededFor(@NotNull EntityPlayer player, @NotNull List<String> current) {
      Intrinsics.checkParameterIsNotNull(player, "player");
      Intrinsics.checkParameterIsNotNull(current, "current");
      Set var10000 = CollectionsKt.toSet((Iterable)current);
      IStageData var10001 = GameStageHelper.getPlayerData(player);
      Intrinsics.checkExpressionValueIsNotNull(var10001, "GameStageHelper.getPlayerData(player)");
      Collection var3 = var10001.getStages();
      Intrinsics.checkExpressionValueIsNotNull(var3, "GameStageHelper.getPlayerData(player).stages");
      return CollectionsKt.toList((Iterable)SetsKt.minus(var10000, (Iterable)CollectionsKt.toSet((Iterable)CollectionsKt.filterNotNull((Iterable)var3))));
   }

   private FacadeGameStages() {
   }

   static {
      FacadeGameStages var0 = new FacadeGameStages();
      INSTANCE = var0;
   }
}
