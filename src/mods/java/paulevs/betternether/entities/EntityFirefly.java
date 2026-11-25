package paulevs.betternether.entities;

import elucent.albedo.lighting.ILightProvider;
import elucent.albedo.lighting.Light;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional.Interface;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import paulevs.betternether.biomes.BiomeRegister;
import paulevs.betternether.biomes.NetherBiome;
import paulevs.betternether.sounds.SoundRegister;
import paulevs.betternether.world.BNWorldGenerator;

@Interface(
   iface = "elucent.albedo.lighting.ILightProvider",
   modid = "albedo"
)
public class EntityFirefly extends EntityAmbientCreature implements IAnimals, ILightProvider {
   private static final DataParameter<Byte> R = EntityDataManager.createKey(EntityFirefly.class, DataSerializers.BYTE);
   private static final DataParameter<Byte> G = EntityDataManager.createKey(EntityFirefly.class, DataSerializers.BYTE);
   private static final DataParameter<Byte> B = EntityDataManager.createKey(EntityFirefly.class, DataSerializers.BYTE);
   private static final DataParameter<Float> SIZE = EntityDataManager.createKey(EntityFirefly.class, DataSerializers.FLOAT);
   private static final DataParameter<Float> OFFSET = EntityDataManager.createKey(EntityFirefly.class, DataSerializers.FLOAT);
   private Random random;
   private boolean sitting;
   private boolean wantToSit;
   private float sitY;
   private float test;

   public EntityFirefly(World worldIn) {
      super(worldIn);
      this.setSize(0.25F, 0.25F);
      this.setNoGravity(true);
      this.isImmuneToFire = true;
      this.random = new Random();
   }

   public boolean getCanSpawnHere() {
      NetherBiome biome = BNWorldGenerator.getBiome(this.getPosition());
      return biome == BiomeRegister.BIOME_GRASSLANDS || biome == BiomeRegister.BIOME_NETHER_JUNGLE;
   }

   protected void entityInit() {
      super.entityInit();
      this.dataManager.register(R, Byte.valueOf((byte)-1));
      this.dataManager.register(G, Byte.valueOf((byte)-1));
      this.dataManager.register(B, Byte.valueOf((byte)-1));
      this.dataManager.register(SIZE, Float.valueOf(0.5F));
      this.dataManager.register(OFFSET, Float.valueOf(0.77F));
   }

   public boolean canBePushed() {
      return false;
   }

   protected void collideWithEntity(Entity entityIn) {
   }

   protected void collideWithNearbyEntities() {
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(2.0D);
   }

   public void onUpdate() {
      super.onUpdate();
      if (this.dead && this.hasNoGravity()) {
         this.setNoGravity(false);
      }

      if (this.sitting) {
         this.posY = (double)this.sitY;
      }

   }

   protected void updateAITasks() {
      super.updateAITasks();
      if (this.wantToSit) {
         if (this.sitting) {
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
            if (this.random.nextInt(320) == 0) {
               this.wantToSit = false;
            }
         } else {
            this.motionX *= 0.5D;
            this.motionY = -0.02D;
            this.motionZ *= 0.5D;
            BlockPos under = (new BlockPos(this)).add(0.0D, -0.5D, 0.0D);
            if (this.world.getBlockState(under).isSideSolid(this.world, under, EnumFacing.UP)) {
               this.motionX = 0.0D;
               this.motionY = 0.0D;
               this.motionZ = 0.0D;
               this.sitY = (float)under.getY() + (float)this.world.getBlockState(under).getCollisionBoundingBox(this.world, under).maxY;
               this.sitting = true;
            }
         }
      } else if (this.sitting) {
         this.motionY = 0.05D;
         this.motionX = 0.05D - this.random.nextDouble() * 0.1D;
         this.motionZ = 0.05D - this.random.nextDouble() * 0.1D;
         this.sitting = false;
      } else {
         if (this.random.nextInt(3) == 0) {
            this.motionX += 0.01D - this.random.nextDouble() * 0.02D;
            this.motionY += 0.02D - this.random.nextDouble() * 0.04D;
            this.motionZ += 0.01D - this.random.nextDouble() * 0.02D;
            BlockPos under = (new BlockPos(this)).down(8);
            if (this.world.getBlockState(under).getBlock() == Blocks.AIR && this.world.getBlockState(under.up(7)).getBlock() == Blocks.AIR) {
               this.motionY = -Math.abs(this.motionY);
            }

            double l = this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ;
            if (l > 0.0D) {
               l = Math.sqrt(l);
               this.motionX /= l;
               this.motionY /= l;
               this.motionZ /= l;
            } else {
               switch(this.random.nextInt(6)) {
               case 0:
                  this.motionX = 1.0D;
                  this.motionY = 0.0D;
                  this.motionZ = 0.0D;
                  break;
               case 1:
                  this.motionX = -1.0D;
                  this.motionY = 0.0D;
                  this.motionZ = 0.0D;
                  break;
               case 2:
                  this.motionY = 1.0D;
                  this.motionX = 0.0D;
                  this.motionZ = 0.0D;
                  break;
               case 3:
                  this.motionY = -1.0D;
                  this.motionX = 0.0D;
                  this.motionZ = 0.0D;
                  break;
               case 4:
                  this.motionZ = 1.0D;
                  this.motionX = 0.0D;
                  this.motionY = 0.0D;
                  break;
               default:
                  this.motionZ = -1.0D;
                  this.motionX = 0.0D;
                  this.motionY = 0.0D;
               }
            }

            this.motionX *= 0.05D;
            this.motionY *= 0.02D;
            this.motionZ *= 0.05D;
            float f = (float)(MathHelper.atan2(this.motionZ, this.motionX) * 57.29577951308232D) - 90.0F;
            float f1 = MathHelper.wrapDegrees(f - this.rotationYaw);
            this.moveForward = 0.5F;
            this.rotationYaw += f1;
         }

         if (this.random.nextInt(320) == 0) {
            this.wantToSit = true;
         }
      }

   }

   public boolean doesEntityNotTriggerPressurePlate() {
      return true;
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }

   public float getBrightness() {
      return 1.0F;
   }

   @SideOnly(Side.CLIENT)
   public void bindColor() {
      GL11.glColor3ub(((Byte)this.dataManager.get(R)).byteValue(), ((Byte)this.dataManager.get(G)).byteValue(), ((Byte)this.dataManager.get(B)).byteValue());
   }

   @SideOnly(Side.CLIENT)
   public void transform() {
      GlStateManager.translate(0.0F, ((Float)this.dataManager.get(OFFSET)).floatValue(), 0.0F);
      GlStateManager.scale(((Float)this.dataManager.get(SIZE)).floatValue(), ((Float)this.dataManager.get(SIZE)).floatValue(), ((Float)this.dataManager.get(SIZE)).floatValue());
   }

   public void writeEntityToNBT(NBTTagCompound compound) {
      super.writeEntityToNBT(compound);
      compound.setByte("R", ((Byte)this.dataManager.get(R)).byteValue());
      compound.setByte("G", ((Byte)this.dataManager.get(G)).byteValue());
      compound.setByte("B", ((Byte)this.dataManager.get(B)).byteValue());
      compound.setFloat("Size", ((Float)this.dataManager.get(SIZE)).floatValue());
   }

   public void readEntityFromNBT(NBTTagCompound compound) {
      super.readEntityFromNBT(compound);
      this.dataManager.set(R, Byte.valueOf(compound.getByte("R")));
      this.dataManager.set(G, Byte.valueOf(compound.getByte("G")));
      this.dataManager.set(B, Byte.valueOf(compound.getByte("B")));
      this.dataManager.set(SIZE, Float.valueOf(compound.getFloat("Size")));
      this.updateOffset();
   }

   public Light provideLight() {
      return Light.builder().pos(this).color((float)(((Byte)this.dataManager.get(R)).byteValue() & 255) / 255.0F, (float)(((Byte)this.dataManager.get(G)).byteValue() & 255) / 255.0F, (float)(((Byte)this.dataManager.get(B)).byteValue() & 255) / 255.0F).radius(2.0F).build();
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   public void fall(float distance, float damageMultiplier) {
   }

   protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
   }

   @Nullable
   public SoundEvent getAmbientSound() {
      return this.sitting ? null : SoundRegister.FLY_SOUND;
   }

   protected float getSoundVolume() {
      return 0.3F + this.random.nextFloat() * 0.2F;
   }

   protected float getSoundPitch() {
      return this.random.nextFloat() * 0.4F + 0.8F;
   }

   private void updateOffset() {
      this.dataManager.set(OFFSET, Float.valueOf(1.3F - ((Float)this.dataManager.get(SIZE)).floatValue() * 1.06F));
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
      this.dataManager.set(R, Byte.valueOf((byte)(128 | this.random.nextInt(128))));
      this.dataManager.set(G, Byte.valueOf((byte)(128 | this.random.nextInt(128))));
      this.dataManager.set(B, Byte.valueOf((byte)(128 | this.random.nextInt(128))));
      this.dataManager.set(SIZE, Float.valueOf(0.25F + this.random.nextFloat() * 0.25F));
      this.updateOffset();
      return livingdata;
   }
}
