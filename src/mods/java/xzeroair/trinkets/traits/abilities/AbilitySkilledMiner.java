package xzeroair.trinkets.traits.abilities;

import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xzeroair.trinkets.races.dwarf.config.DwarfConfig;
import xzeroair.trinkets.traits.abilities.interfaces.IMiningAbility;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.ConfigHelper;
import xzeroair.trinkets.util.helpers.BlockHelperUtil;

public class AbilitySkilledMiner extends Ability implements IMiningAbility {
   public static final DwarfConfig serverConfig;

   public AbilitySkilledMiner() {
      super("skilled_miner");
   }

   public float breakingBlock(EntityLivingBase entity, IBlockState state, BlockPos pos, float originalSpeed, float newSpeed) {
      if (serverConfig.static_mining) {
         ItemStack heldItemStack = entity.getHeldItemMainhand();
         Item heldItem = heldItemStack.getItem();
         int toolLevel = heldItem.getHarvestLevel(heldItemStack, "pickaxe", (EntityPlayer)null, state);
         int level = state.getBlock().getHarvestLevel(state);
         float hardness = state.getBlockHardness(entity.world, pos);
         if (!heldItem.getToolClasses(heldItemStack).isEmpty() && heldItem.getToolClasses(heldItemStack).contains("pickaxe") && (toolLevel >= level || toolLevel == level - 1)) {
            newSpeed = hardness * 5.0F;
         }
      }

      return newSpeed;
   }

   public int brokeBlock(EntityLivingBase entity, World world, IBlockState state, BlockPos pos, int expToDrop) {
      boolean isClient = world.isRemote;
      ItemStack heldItemStack = entity.getHeldItemMainhand();
      Block block = state.getBlock();
      if (!heldItemStack.isEmpty() && !block.getHarvestTool(state).contentEquals("pickaxe")) {
         return expToDrop;
      } else {
         ItemStack toolUsed = heldItemStack.copy();
         int fortuneLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, heldItemStack);
         boolean silkTouching = EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, toolUsed) > 0;

         try {
            if (serverConfig.fortune && !silkTouching) {
               Enchantment fortune = Enchantments.FORTUNE;
               int fortuneMaxLevel = fortune.getMaxLevel();
               if (fortuneLevel > 0) {
                  if (serverConfig.fortune_mix) {
                     NBTTagList nbttaglist = toolUsed.getEnchantmentTagList();

                     for(int i = 0; i < nbttaglist.tagCount(); ++i) {
                        NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
                        Enchantment enchantment = Enchantment.getEnchantmentByID(nbttagcompound.getShort("id"));
                        if (enchantment == fortune) {
                           nbttagcompound.setShort("lvl", (short)((byte)fortuneLevel + fortuneMaxLevel));
                        }
                     }
                  }
               } else {
                  toolUsed.addEnchantment(fortune, fortuneMaxLevel);
               }
            }
         } catch (Exception var22) {
            ;
         }

         Item toolItem = toolUsed.getItem();
         if (!toolItem.getToolClasses(toolUsed).isEmpty() && toolItem.getToolClasses(toolUsed).contains("pickaxe")) {
            int tempExp = 0;
            if (!silkTouching && !isClient) {
               if (serverConfig.BLOCKS.bonus_exp) {
                  for(String s : serverConfig.BLOCKS.xPBlocks) {
                     ConfigHelper.ConfigObject object = new ConfigHelper.ConfigObject(s);
                     if (object.doesBlockMatchEntry(state)) {
                        int bonusExp = serverConfig.BLOCKS.bonus_exp_max;
                        int min = serverConfig.BLOCKS.bonus_exp_min;
                        int rXP = bonusExp < 1 ? min : this.random.nextInt(bonusExp);
                        tempExp += Math.max(min, rXP);
                        break;
                     }
                  }
               }

               if (serverConfig.BLOCKS.minXpBlocks && tempExp < 1) {
                  for(String s : serverConfig.BLOCKS.MinBlocks) {
                     ConfigHelper.ConfigObject object = new ConfigHelper.ConfigObject(s);
                     if (object.doesBlockMatchEntry(state)) {
                        tempExp = 1;
                        break;
                     }
                  }
               }
            }

            if (serverConfig.skilled_miner && entity instanceof EntityPlayer && BlockHelperUtil.canBreakBlock(toolUsed, world, (EntityPlayer)entity, pos, pos, 1)) {
               BlockHelperUtil.breakBlock((EntityPlayer)entity, toolUsed, world, state, pos, pos, false, 1, (xp) -> {
                  return xp.intValue() < -1 ? droppedExp : xp.intValue() + droppedExp;
               });
               return 0;
            } else {
               return expToDrop + tempExp;
            }
         } else {
            return expToDrop;
         }
      }
   }

   static {
      serverConfig = TrinketsConfig.SERVER.races.dwarf;
   }
}
