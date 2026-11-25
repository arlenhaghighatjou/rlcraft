package nightkosh.advanced_fishing.api.fishing_catch;

import net.minecraft.block.Block;

public interface ICatchManager {
   void addCatch(Block var1, ICatch var2);

   void addWaterCatch(IWaterCondition var1, IWaterCatch var2);

   ICatch getCatch(Block var1);
}
