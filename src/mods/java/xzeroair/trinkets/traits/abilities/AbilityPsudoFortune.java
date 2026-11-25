package xzeroair.trinkets.traits.abilities;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xzeroair.trinkets.races.dwarf.config.DwarfConfig;
import xzeroair.trinkets.traits.abilities.interfaces.IMiningAbility;
import xzeroair.trinkets.util.TrinketsConfig;

public class AbilityPsudoFortune extends Ability implements IMiningAbility {
   public static final DwarfConfig serverConfig;
   private int bonus;
   private int extra = 0;

   public AbilityPsudoFortune() {
      super("psudo_fortune");
   }

   public float blockDrops(EntityLivingBase entity, World world, IBlockState state, BlockPos pos, List<ItemStack> drops, float dropChance, boolean silkTouching, int fortuneLevel) {
      ItemStack heldItemStack = entity.getHeldItemMainhand();
      Item heldItem = heldItemStack.getItem();
      return dropChance;
   }

   static {
      serverConfig = TrinketsConfig.SERVER.races.dwarf;
   }
}
