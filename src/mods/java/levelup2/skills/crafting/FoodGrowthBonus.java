package levelup2.skills.crafting;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import levelup2.skills.BaseSkill;
import levelup2.skills.SkillRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

public class FoodGrowthBonus extends BaseSkill {
   public FoodGrowthBonus() {
   }

   public String getSkillName() {
      return "levelup:cropgrowth";
   }

   public byte getSkillType() {
      return 1;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Items.IRON_HOE);
   }

   public boolean hasSubscription() {
      return true;
   }

   public void register(EventBus bus) {
      bus.register(PlayerTickEvent.class, this::onPlayerUpdate);
   }

   public void onPlayerUpdate(PlayerTickEvent evt) {
      if (this.isActive()) {
         if (evt.phase == Phase.START) {
            EntityPlayer player = evt.player;
            if (player != null) {
               int skillLevel = SkillRegistry.getSkillLevel(player, this.getSkillName());
               if (!player.world.isRemote && skillLevel > 0 && player.getRNG().nextFloat() <= (float)skillLevel / 500.0F) {
                  this.growCropsAround(player.world, skillLevel, player);
               }
            }
         }

      }
   }

   private void growCropsAround(World world, int range, EntityPlayer player) {
      int posX = (int)player.posX;
      int posY = (int)player.posY;
      int posZ = (int)player.posZ;
      int dist = range / 2 + 2;

      for(BlockPos pos : BlockPos.getAllInBox(new BlockPos(posX - dist, posY - dist, posZ - dist), new BlockPos(posX + dist + 1, posY + dist + 1, posZ + dist + 1))) {
         Block block = world.getBlockState(pos).getBlock();
         if (block instanceof IPlantable && !SkillRegistry.getCropBlacklist().contains(block)) {
            world.scheduleUpdate(pos, block, block.tickRate(world));
         }
      }

   }
}
