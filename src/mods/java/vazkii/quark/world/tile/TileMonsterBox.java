package vazkii.quark.world.tile;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import vazkii.arl.block.tile.TileMod;
import vazkii.quark.base.sounds.QuarkSounds;
import vazkii.quark.world.feature.MonsterBoxes;

public class TileMonsterBox extends TileMod implements ITickable {
   private int breakProgress;

   public TileMonsterBox() {
   }

   public void update() {
      if (this.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
         BlockPos pos = this.getPos();
         if (this.world.isRemote) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            this.world.spawnParticle(this.breakProgress == 0 ? EnumParticleTypes.FLAME : EnumParticleTypes.SMOKE_LARGE, (double)x + Math.random(), (double)y + Math.random(), (double)z + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
         }

         boolean doBreak = this.breakProgress > 0;
         if (!doBreak) {
            List<EntityPlayer> players = this.world.getEntitiesWithinAABB(EntityPlayer.class, (new AxisAlignedBB(pos)).grow(2.5D));
            doBreak = players.size() > 0;
         }

         if (doBreak) {
            if (this.breakProgress == 0) {
               this.world.playSound((EntityPlayer)null, pos, QuarkSounds.BLOCK_MONSTER_BOX_GROWL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            ++this.breakProgress;
            if (this.breakProgress > 40) {
               this.world.playEvent(2001, pos, Block.getStateId(this.world.getBlockState(pos)));
               this.world.setBlockToAir(pos);
               this.spawnMobs();
            }
         }

      }
   }

   private void spawnMobs() {
      if (!this.world.isRemote) {
         BlockPos pos = this.getPos();
         int mobCount = MonsterBoxes.minMobs + this.world.rand.nextInt(Math.max(MonsterBoxes.maxMobs - MonsterBoxes.minMobs + 1, 1));

         for(int i = 0; i < mobCount; ++i) {
            float r = this.world.rand.nextFloat();
            Entity e;
            if ((double)r < 0.1D) {
               e = new EntityWitch(this.world);
            } else if ((double)r < 0.3D) {
               e = new EntityCaveSpider(this.world);
            } else {
               e = new EntityZombie(this.world);
            }

            double motionMultiplier = 0.4D;
            e.setPosition((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D);
            e.motionX = ((double)this.world.rand.nextFloat() - 0.5D) * motionMultiplier;
            e.motionY = ((double)this.world.rand.nextFloat() - 0.5D) * motionMultiplier;
            e.motionZ = ((double)this.world.rand.nextFloat() - 0.5D) * motionMultiplier;
            this.world.spawnEntity(e);
         }

      }
   }
}
