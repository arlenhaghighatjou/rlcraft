package lykrast.defiledlands.common.entity.monster;

import javax.annotation.Nullable;
import lykrast.defiledlands.common.entity.IEntityDefiled;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySlimeDefiled extends EntitySlime implements IEntityDefiled {
   public static final ResourceLocation LOOT = new ResourceLocation("defiledlands", "entities/slime_defiled");

   public EntitySlimeDefiled(World worldIn) {
      super(worldIn);
   }

   public void setSlimeSize(int size, boolean resetHealth) {
      super.setSlimeSize(size, resetHealth);
   }

   protected int getJumpDelay() {
      return this.rand.nextInt(10) + 5;
   }

   protected boolean canDamagePlayer() {
      return true;
   }

   protected int getAttackStrength() {
      return this.getSlimeSize() * 2;
   }

   protected EntitySlimeDefiled createInstance() {
      return new EntitySlimeDefiled(this.world);
   }

   public void fall(float distance, float damageMultiplier) {
   }

   public int getMaxFallHeight() {
      return Integer.MAX_VALUE;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return this.getSlimeSize() == 1 ? LOOT : LootTableList.EMPTY;
   }

   protected void jump() {
      this.motionY = 0.6D;
      this.isAirBorne = true;
   }
}
