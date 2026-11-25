package ejektaflex.bountiful.api.config;

import java.util.List;
import kotlin.Metadata;
import kotlin.ranges.IntRange;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u0006\n\u0002\b\r\n\u0002\u0010 \n\u0002\b\u0011\n\u0002\u0010\u0002\n\u0000\bf\u0018\u00002\u00020\u0001J\b\u0010D\u001a\u00020EH&R\u0012\u0010\u0002\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005R\u0012\u0010\u0006\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b\b\u0010\tR\u0012\u0010\n\u001a\u00020\u000bX¦\u0004¢\u0006\u0006\u001a\u0004\b\f\u0010\rR\u0012\u0010\u000e\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b\u000f\u0010\tR\u0012\u0010\u0010\u001a\u00020\u000bX¦\u0004¢\u0006\u0006\u001a\u0004\b\u0011\u0010\rR\u0012\u0010\u0012\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0013\u0010\tR\u0012\u0010\u0014\u001a\u00020\u000bX¦\u0004¢\u0006\u0006\u001a\u0004\b\u0015\u0010\rR\u0012\u0010\u0016\u001a\u00020\u000bX¦\u0004¢\u0006\u0006\u001a\u0004\b\u0017\u0010\rR\u0012\u0010\u0018\u001a\u00020\u0019X¦\u0004¢\u0006\u0006\u001a\u0004\b\u001a\u0010\u001bR\u0012\u0010\u001c\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b\u001d\u0010\tR\u0012\u0010\u001e\u001a\u00020\u000bX¦\u0004¢\u0006\u0006\u001a\u0004\b\u001f\u0010\rR\u0012\u0010 \u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b!\u0010\tR\u0012\u0010\"\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b#\u0010\tR\u0012\u0010$\u001a\u00020%X¦\u0004¢\u0006\u0006\u001a\u0004\b&\u0010'R\u0012\u0010(\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b)\u0010\tR\u0012\u0010*\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b+\u0010\tR\u0012\u0010,\u001a\u00020\u000bX¦\u0004¢\u0006\u0006\u001a\u0004\b-\u0010\rR\u0012\u0010.\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b/\u0010\tR\u0012\u00100\u001a\u00020%X¦\u0004¢\u0006\u0006\u001a\u0004\b1\u0010'R\u0018\u00102\u001a\b\u0012\u0004\u0012\u00020%03X¦\u0004¢\u0006\u0006\u001a\u0004\b4\u00105R\u0012\u00106\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b7\u0010\tR\u0012\u00108\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b9\u0010\tR\u0012\u0010:\u001a\u00020%X¦\u0004¢\u0006\u0006\u001a\u0004\b;\u0010'R\u0012\u0010<\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b=\u0010\tR\u0012\u0010>\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b?\u0010\tR\u0012\u0010@\u001a\u00020%X¦\u0004¢\u0006\u0006\u001a\u0004\bA\u0010'R\u0018\u0010B\u001a\b\u0012\u0004\u0012\u00020\u000b03X¦\u0004¢\u0006\u0006\u001a\u0004\bC\u00105¨\u0006F"},
   d2 = {"Lejektaflex/bountiful/api/config/IBountifulConfig;", "", "boardAddFrequency", "", "getBoardAddFrequency", "()J", "boardDrops", "", "getBoardDrops", "()Z", "boardLifespan", "", "getBoardLifespan", "()I", "boardRecipeEnabled", "getBoardRecipeEnabled", "boardRequiredVillagerMax", "getBoardRequiredVillagerMax", "boardRequiresVillagers", "getBoardRequiresVillagers", "boardVillagerDistance", "getBoardVillagerDistance", "bountiesCreatedOnPlace", "getBountiesCreatedOnPlace", "bountyAmountRange", "Lkotlin/ranges/IntRange;", "getBountyAmountRange", "()Lkotlin/ranges/IntRange;", "bountyBoardBreakable", "getBountyBoardBreakable", "bountyTimeMin", "getBountyTimeMin", "cashInAtBountyBoard", "getCashInAtBountyBoard", "compatGameStages", "getCompatGameStages", "entityTimeMult", "", "getEntityTimeMult", "()D", "globalBounties", "getGlobalBounties", "greedyRewards", "getGreedyRewards", "maxBountiesPerBoard", "getMaxBountiesPerBoard", "randomBounties", "getRandomBounties", "rarityChance", "getRarityChance", "rarityMultipliers", "", "getRarityMultipliers", "()Ljava/util/List;", "requireExactNbt", "getRequireExactNbt", "shouldCountdownOnBoard", "getShouldCountdownOnBoard", "timeMultiplier", "getTimeMultiplier", "tryMaxRewardQuantity", "getTryMaxRewardQuantity", "villageGeneration", "getVillageGeneration", "villageGenerationWeight", "getVillageGenerationWeight", "xpBonuses", "getXpBonuses", "load", "", "Bountiful_api"}
)
public interface IBountifulConfig {
   int getMaxBountiesPerBoard();

   long getBoardAddFrequency();

   int getBoardLifespan();

   double getTimeMultiplier();

   boolean getCashInAtBountyBoard();

   double getRarityChance();

   int getBountyTimeMin();

   @NotNull
   IntRange getBountyAmountRange();

   boolean getShouldCountdownOnBoard();

   int getBountiesCreatedOnPlace();

   boolean getGlobalBounties();

   double getEntityTimeMult();

   @NotNull
   List<Double> getRarityMultipliers();

   boolean getBoardRecipeEnabled();

   boolean getBountyBoardBreakable();

   boolean getGreedyRewards();

   boolean getVillageGeneration();

   boolean getRandomBounties();

   boolean getBoardDrops();

   @NotNull
   List<Integer> getXpBonuses();

   boolean getCompatGameStages();

   boolean getBoardRequiresVillagers();

   int getBoardRequiredVillagerMax();

   int getBoardVillagerDistance();

   double getVillageGenerationWeight();

   boolean getRequireExactNbt();

   boolean getTryMaxRewardQuantity();

   void load();
}
