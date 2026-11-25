package xzeroair.trinkets.traits.abilities.other;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import xzeroair.trinkets.races.titan.config.TitanConfig;
import xzeroair.trinkets.traits.abilities.Ability;
import xzeroair.trinkets.traits.abilities.interfaces.IAttackAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IMiningAbility;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.ConfigHelper;
import xzeroair.trinkets.util.helpers.BlockHelperUtil;

public class AbilityLargeHands extends Ability implements IAttackAbility, IMiningAbility {
   public static final TitanConfig serverConfig;

   public AbilityLargeHands() {
      super("large_hands");
   }

   public float hurtEntity(EntityLivingBase target, DamageSource source, float dmg) {
      if (this.isIndirectDamage(source)) {
         return dmg;
      } else {
         Entity attacker = source.getTrueSource();
         AxisAlignedBB bb = target.getEntityBoundingBox().grow(1.0D);
         Predicate<Entity> Targets = Predicates.and(EntitySelectors.NOT_SPECTATING, (ent) -> {
            return ent != null && ent.canBeCollidedWith() && ent != attacker && ent != target && ent.getEntityId() != target.getEntityId();
         });
         List<Entity> splash = target.getEntityWorld().getEntitiesWithinAABB(Entity.class, bb, Targets);
         DamageSource sSource = DamageSource.GENERIC;
         if (attacker instanceof EntityLivingBase) {
            if (attacker instanceof EntityPlayer) {
               sSource = new EntityDamageSourceIndirect("player", attacker, attacker);
            } else {
               sSource = DamageSource.causeIndirectDamage(attacker, (EntityLivingBase)attacker);
            }
         }

         for(Entity hit : splash) {
            hit.attackEntityFrom(sSource, dmg * 0.25F);
         }

         return dmg;
      }
   }

   public float breakingBlock(EntityLivingBase entity, IBlockState state, BlockPos pos, float originalSpeed, float newSpeed) {
      World world = entity.getEntityWorld();
      ItemStack heldItemStack = entity instanceof EntityPlayer ? ((EntityPlayer)entity).inventory.getCurrentItem() : entity.getActiveItemStack();
      if (heldItemStack.isEmpty()) {
         Block block = state.getBlock();
         String neededTool = block.getHarvestTool(state);
         ItemStack toolUsed = this.getHarvestTool(neededTool, heldItemStack);
         float newDigSpeed = BlockHelperUtil.getEntityDigSpeed(entity, toolUsed, state, pos, false);
         float hardness = state.getBlockHardness(world, pos);
         return hardness > 0.0F && entity instanceof EntityPlayer && !ForgeHooks.canHarvestBlock(block, (EntityPlayer)entity, world, pos) ? newDigSpeed * 2.0F : newDigSpeed;
      } else {
         return newSpeed;
      }
   }

   public int brokeBlock(EntityLivingBase entity, World world, IBlockState state, BlockPos pos, int expToDrop) {
      boolean flag = serverConfig.miningExtendInverted ? !entity.isSneaking() : entity.isSneaking();
      if (serverConfig.miningExtend && !flag && expToDrop >= 0) {
         for(String s : serverConfig.miningAoEBlacklist) {
            ConfigHelper.ConfigObject object = new ConfigHelper.ConfigObject(s);
            if (object.doesBlockMatchEntry(state)) {
               return expToDrop;
            }
         }

         ItemStack heldItemStack = entity instanceof EntityPlayer ? ((EntityPlayer)entity).inventory.getCurrentItem() : entity.getActiveItemStack();
         Block block = state.getBlock();
         String neededTool = block.getHarvestTool(state);
         ItemStack toolUsed = this.getHarvestTool(neededTool, heldItemStack);
         if (BlockHelperUtil.canToolHarvestBlock(toolUsed, state) && BlockHelperUtil.isToolEffective(toolUsed, state)) {
            ImmutableList<BlockPos> list = BlockHelperUtil.getBlockList(toolUsed, world, (EntityPlayer)entity, pos, 3, 3, 3, (checkPos) -> {
               boolean skip = false;

               for(String s : serverConfig.miningAoEBlacklist) {
                  ConfigHelper.ConfigObject object = new ConfigHelper.ConfigObject(s);
                  if (object.doesBlockMatchEntry(world.getBlockState(pos))) {
                     skip = true;
                  }
               }

               return !skip;
            });
            UnmodifiableIterator var12 = list.iterator();

            while(var12.hasNext()) {
               BlockPos ePos = (BlockPos)var12.next();
               BlockHelperUtil.breakBlock((EntityPlayer)entity, toolUsed, world, state, pos, ePos, true);
            }

            BlockHelperUtil.breakBlock((EntityPlayer)entity, toolUsed, world, state, pos, pos, false);
            return 0;
         } else {
            return expToDrop;
         }
      } else {
         return expToDrop;
      }
   }

   public ItemStack getHarvestTool(String needed, ItemStack heldTool) {
      if (needed == null) {
         return heldTool.copy();
      } else if (heldTool.isEmpty()) {
         byte var4 = -1;
         switch(needed.hashCode()) {
         case -903145309:
            if (needed.equals("shovel")) {
               var4 = 1;
            }
            break;
         case -578028723:
            if (needed.equals("pickaxe")) {
               var4 = 0;
            }
            break;
         case 97038:
            if (needed.equals("axe")) {
               var4 = 2;
            }
         }

         switch(var4) {
         case 0:
            return new ItemStack(Items.WOODEN_PICKAXE);
         case 1:
            return new ItemStack(Items.WOODEN_SHOVEL);
         case 2:
            return new ItemStack(Items.WOODEN_AXE);
         default:
            return heldTool.copy();
         }
      } else {
         return heldTool.copy();
      }
   }

   static {
      serverConfig = TrinketsConfig.SERVER.races.titan;
   }
}
