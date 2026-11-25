package rlmixins.mixin.icenfire;

import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.core.ModVillagers;
import com.github.alexthe666.iceandfire.entity.EntityMyrmexBase.BasicTrade;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.entity.passive.EntityVillager.PriceInfo;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({ModVillagers.class})
public abstract class ModVillagersMixin {
   public ModVillagersMixin() {
   }

   @Redirect(
      method = {"init"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraftforge/fml/common/registry/VillagerRegistry$VillagerCareer;addTrade(I[Lnet/minecraft/entity/passive/EntityVillager$ITradeList;)Lnet/minecraftforge/fml/common/registry/VillagerRegistry$VillagerCareer;",
   ordinal = 86
),
      remap = false
   )
   public VillagerCareer rlmixins_iceAndFireModVillagers_init_goldIngot0(VillagerCareer career, int level, ITradeList... trades) {
      return career.addTrade(2, new ITradeList[]{new BasicTrade(new ItemStack(Items.GOLD_INGOT), new ItemStack(ModItems.myrmex_desert_resin), new PriceInfo(2, 4), new PriceInfo(2, 4))});
   }

   @Redirect(
      method = {"init"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraftforge/fml/common/registry/VillagerRegistry$VillagerCareer;addTrade(I[Lnet/minecraft/entity/passive/EntityVillager$ITradeList;)Lnet/minecraftforge/fml/common/registry/VillagerRegistry$VillagerCareer;",
   ordinal = 87
),
      remap = false
   )
   public VillagerCareer rlmixins_iceAndFireModVillagers_init_silverIngot0(VillagerCareer career, int level, ITradeList... trades) {
      return career.addTrade(2, new ITradeList[]{new BasicTrade(new ItemStack(ModItems.silverIngot), new ItemStack(ModItems.myrmex_desert_resin), new PriceInfo(2, 4), new PriceInfo(2, 4))});
   }

   @Redirect(
      method = {"init"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraftforge/fml/common/registry/VillagerRegistry$VillagerCareer;addTrade(I[Lnet/minecraft/entity/passive/EntityVillager$ITradeList;)Lnet/minecraftforge/fml/common/registry/VillagerRegistry$VillagerCareer;",
   ordinal = 96
),
      remap = false
   )
   public VillagerCareer rlmixins_iceAndFireModVillagers_init_goldIngot1(VillagerCareer career, int level, ITradeList... trades) {
      return career.addTrade(2, new ITradeList[]{new BasicTrade(new ItemStack(Items.GOLD_INGOT), new ItemStack(ModItems.myrmex_jungle_resin), new PriceInfo(2, 4), new PriceInfo(2, 4))});
   }

   @Redirect(
      method = {"init"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraftforge/fml/common/registry/VillagerRegistry$VillagerCareer;addTrade(I[Lnet/minecraft/entity/passive/EntityVillager$ITradeList;)Lnet/minecraftforge/fml/common/registry/VillagerRegistry$VillagerCareer;",
   ordinal = 97
),
      remap = false
   )
   public VillagerCareer rlmixins_iceAndFireModVillagers_init_silverIngot1(VillagerCareer career, int level, ITradeList... trades) {
      return career.addTrade(2, new ITradeList[]{new BasicTrade(new ItemStack(ModItems.silverIngot), new ItemStack(ModItems.myrmex_jungle_resin), new PriceInfo(2, 4), new PriceInfo(2, 4))});
   }
}
