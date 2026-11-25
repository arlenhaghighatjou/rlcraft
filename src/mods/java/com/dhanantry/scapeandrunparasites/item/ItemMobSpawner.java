package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityBanoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityCanraAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityEmanaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityHullAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityNoglaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityRanracAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityShycoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOronco;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityTerla;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityCrux;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHeed;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHost;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHostII;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooS;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityMes;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityTonro;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityUnvo;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDod;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIV;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIV;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSV;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityButhol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityLodo;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityNuuh;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityRathol;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityDorpa;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfBear;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfCow;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfCowHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfDragonE;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfDragonEHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfEnderman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfEndermanHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHorse;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHorseHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHuman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHumanHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPig;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPigHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPlayer;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPlayerHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfSheep;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfSheepHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfVillager;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfVillagerHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfWolf;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfWolfHead;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityBano;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityCanra;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityEmana;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityHull;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityLum;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityNogla;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityRanrac;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityShyco;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityWymo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityAlafha;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityAnged;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityEsor;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityFlog;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityGanro;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityOmboo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityOrch;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityElvia;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityJinjo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityLencia;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityPheon;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityVesta;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityDropPod;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMobSpawner extends ItemBase {
   private String name;

   public ItemMobSpawner(String name) {
      super("itemmobspawner_" + name, 64, (byte)0);
      this.name = name;
   }

   public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (worldIn.isRemote) {
         return EnumActionResult.SUCCESS;
      } else if (!player.canPlayerEdit(pos.offset(facing), facing, itemstack)) {
         return EnumActionResult.FAIL;
      } else {
         IBlockState iblockstate = worldIn.getBlockState(pos);
         Block block = iblockstate.getBlock();
         if (block == Blocks.MOB_SPAWNER) {
            worldIn.getTileEntity(pos);
         }

         BlockPos blockpos = pos.offset(facing);
         double d0 = this.getYOffset(worldIn, blockpos);
         Entity entity = this.spawnEntity(worldIn, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + d0, (double)blockpos.getZ() + 0.5D, player);
         if (entity != null) {
            if (entity instanceof EntityLivingBase && itemstack.hasDisplayName()) {
               entity.setCustomNameTag(itemstack.getDisplayName());
            }

            applyItemEntityDataToEntity(worldIn, player, itemstack, entity);
            if (!player.capabilities.isCreativeMode) {
               itemstack.shrink(1);
            }
         }

         return EnumActionResult.SUCCESS;
      }
   }

   protected double getYOffset(World p_190909_1_, BlockPos p_190909_2_) {
      AxisAlignedBB axisalignedbb = (new AxisAlignedBB(p_190909_2_)).expand(0.0D, -1.0D, 0.0D);
      List<AxisAlignedBB> list = p_190909_1_.getCollisionBoxes((Entity)null, axisalignedbb);
      if (list.isEmpty()) {
         return 0.0D;
      } else {
         double d0 = axisalignedbb.minY;

         for(AxisAlignedBB axisalignedbb1 : list) {
            d0 = Math.max(axisalignedbb1.maxY, d0);
         }

         return d0 - (double)p_190909_2_.getY();
      }
   }

   public static void applyItemEntityDataToEntity(World entityWorld, @Nullable EntityPlayer player, ItemStack stack, @Nullable Entity targetEntity) {
      MinecraftServer minecraftserver = entityWorld.getMinecraftServer();
      if (minecraftserver != null && targetEntity != null) {
         NBTTagCompound nbttagcompound = stack.getTagCompound();
         if (nbttagcompound != null && nbttagcompound.hasKey("EntityTag", 10)) {
            if (!entityWorld.isRemote && targetEntity.ignoreItemEntityData() && (player == null || !minecraftserver.getPlayerList().canSendCommands(player.getGameProfile()))) {
               return;
            }

            NBTTagCompound nbttagcompound1 = targetEntity.writeToNBT(new NBTTagCompound());
            EaglercraftUUID uuid = targetEntity.getUniqueID();
            nbttagcompound1.merge(nbttagcompound.getCompoundTag("EntityTag"));
            targetEntity.setUniqueId(uuid);
            targetEntity.readFromNBT(nbttagcompound1);
         }
      }

   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (worldIn.isRemote) {
         return new ActionResult(EnumActionResult.PASS, itemstack);
      } else {
         RayTraceResult raytraceresult = this.rayTrace(worldIn, playerIn, true);
         if (raytraceresult != null && raytraceresult.typeOfHit == Type.BLOCK) {
            BlockPos blockpos = raytraceresult.getBlockPos();
            if (!(worldIn.getBlockState(blockpos).getBlock() instanceof BlockLiquid)) {
               return new ActionResult(EnumActionResult.PASS, itemstack);
            } else if (worldIn.isBlockModifiable(playerIn, blockpos) && playerIn.canPlayerEdit(blockpos, raytraceresult.sideHit, itemstack)) {
               Entity entity = this.spawnEntity(worldIn, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.5D, (double)blockpos.getZ() + 0.5D, playerIn);
               if (entity == null) {
                  return new ActionResult(EnumActionResult.PASS, itemstack);
               } else {
                  if (entity instanceof EntityLivingBase && itemstack.hasDisplayName()) {
                     entity.setCustomNameTag(itemstack.getDisplayName());
                  }

                  applyItemEntityDataToEntity(worldIn, playerIn, itemstack, entity);
                  if (!playerIn.capabilities.isCreativeMode) {
                     itemstack.shrink(1);
                  }

                  playerIn.addStat(StatList.getObjectUseStats(this));
                  return new ActionResult(EnumActionResult.SUCCESS, itemstack);
               }
            } else {
               return new ActionResult(EnumActionResult.FAIL, itemstack);
            }
         } else {
            return new ActionResult(EnumActionResult.PASS, itemstack);
         }
      }
   }

   private Entity spawnEntity(World worldIn, double x, double y, double z, EntityPlayer playerIn) {
      EntityLiving entity = new EntityZombie(worldIn);
      if (!SRPConfig.allowMobs) {
         return entity;
      } else {
         if (this.name.equals("pod") && SRPConfigMobs.cruxaEnabled) {
            entity = new EntityDropPod(worldIn);
            y += 25.0D;
         } else {
            if (this.name.equals("mes") && SRPConfigMobs.cruxaEnabled) {
               return this.spawnPlayer2(worldIn, x, y, z, playerIn);
            }

            if (this.name.equals("cruxa") && SRPConfigMobs.cruxaEnabled) {
               entity = new EntityCrux(worldIn);
            } else if (this.name.equals("heed") && SRPConfigMobs.heedEnabled) {
               entity = new EntityHeed(worldIn);
            } else if (this.name.equals("jinjo") && SRPConfigMobs.jinjoEnabled) {
               entity = new EntityJinjo(worldIn);
            } else if (this.name.equals("elvia") && SRPConfigMobs.jinjoEnabled) {
               entity = new EntityElvia(worldIn);
            } else if (this.name.equals("pheon") && SRPConfigMobs.jinjoEnabled) {
               entity = new EntityPheon(worldIn);
            } else if (this.name.equals("lencia") && SRPConfigMobs.jinjoEnabled) {
               entity = new EntityLencia(worldIn);
            } else if (this.name.equals("vesta") && SRPConfigMobs.jinjoEnabled) {
               entity = new EntityVesta(worldIn);
            } else if (this.name.equals("shyco") && SRPConfigMobs.shycoEnabled) {
               entity = new EntityShyco(worldIn);
            } else if (this.name.equals("shycoadapted") && SRPConfigMobs.shycoEnabled) {
               entity = new EntityShycoAdapted(worldIn);
            } else if (this.name.equals("dorpa") && SRPConfigMobs.dorpaEnabled) {
               entity = new EntityDorpa(worldIn);
            } else if (this.name.equals("rathol") && SRPConfigMobs.ratholEnabled) {
               entity = new EntityRathol(worldIn);
            } else if (this.name.equals("emana") && SRPConfigMobs.emanaEnabled) {
               entity = new EntityEmana(worldIn);
            } else if (this.name.equals("emanaadapted") && SRPConfigMobs.emanaEnabled) {
               entity = new EntityEmanaAdapted(worldIn);
            } else if (this.name.equals("lodo") && SRPConfigMobs.lodoEnabled) {
               entity = new EntityLodo(worldIn);
            } else if (this.name.equals("mudo") && SRPConfigMobs.mudoEnabled) {
               entity = new EntityMudo(worldIn);
            } else if (this.name.equals("nuuh") && SRPConfigMobs.mudoEnabled) {
               entity = new EntityNuuh(worldIn);
            } else if (this.name.equals("venkrol") && SRPConfigSystems.rsEnabled) {
               entity = new EntityVenkrol(worldIn);
            } else if (this.name.equals("venkrolsii") && SRPConfigSystems.rsEnabled) {
               entity = new EntityVenkrolSII(worldIn);
            } else if (this.name.equals("venkrolsiii") && SRPConfigSystems.rsEnabled) {
               entity = new EntityVenkrolSIII(worldIn);
            } else if (this.name.equals("venkrolsiv") && SRPConfigSystems.rsEnabled) {
               entity = new EntityVenkrolSIV(worldIn);
            } else if (this.name.equals("venkrolsv") && SRPConfigSystems.rsEnabled) {
               entity = new EntityVenkrolSV(worldIn);
            } else if (this.name.equals("nak") && SRPConfigSystems.rsEnabled) {
               entity = new EntityNak(worldIn);
            } else if (this.name.equals("dod") && SRPConfigSystems.rsEnabled) {
               entity = new EntityDod(worldIn);
            } else if (this.name.equals("dodsii") && SRPConfigSystems.rsEnabled) {
               entity = new EntityDodSII(worldIn);
            } else if (this.name.equals("dodsiii") && SRPConfigSystems.rsEnabled) {
               entity = new EntityDodSIII(worldIn);
            } else if (this.name.equals("dodsiv") && SRPConfigSystems.rsEnabled) {
               entity = new EntityDodSIV(worldIn);
            } else if (this.name.equals("infhuman") && SRPConfigMobs.infhumanEnabled) {
               entity = new EntityInfHuman(worldIn);
            } else if (this.name.equals("infhumanhead") && SRPConfigMobs.infhumanEnabled) {
               entity = new EntityInfHumanHead(worldIn);
            } else if (this.name.equals("infcow") && SRPConfigMobs.infcowEnabled) {
               entity = new EntityInfCow(worldIn);
            } else if (this.name.equals("infcowhead") && SRPConfigMobs.infcowEnabled) {
               entity = new EntityInfCowHead(worldIn);
            } else if (this.name.equals("infsheep") && SRPConfigMobs.infsheepEnabled) {
               entity = new EntityInfSheep(worldIn);
            } else if (this.name.equals("infsheephead") && SRPConfigMobs.infsheepEnabled) {
               entity = new EntityInfSheepHead(worldIn);
            } else if (this.name.equals("infwolf") && SRPConfigMobs.infwolfEnabled) {
               entity = new EntityInfWolf(worldIn);
            } else if (this.name.equals("infwolfhead") && SRPConfigMobs.infwolfEnabled) {
               entity = new EntityInfWolfHead(worldIn);
            } else if (this.name.equals("infpig") && SRPConfigMobs.infpigEnabled) {
               entity = new EntityInfPig(worldIn);
            } else if (this.name.equals("infpighead") && SRPConfigMobs.infpigEnabled) {
               entity = new EntityInfPigHead(worldIn);
            } else if (this.name.equals("infvillager") && SRPConfigMobs.infvillagerEnabled) {
               entity = new EntityInfVillager(worldIn);
            } else if (this.name.equals("infvillagerhead") && SRPConfigMobs.infvillagerEnabled) {
               entity = new EntityInfVillagerHead(worldIn);
            } else {
               if (this.name.equals("infplayer") && SRPConfigMobs.infadventurerEnabled) {
                  return this.spawnPlayer(worldIn, x, y, z, playerIn);
               }

               if (this.name.equals("infplayerhead") && SRPConfigMobs.infadventurerEnabled) {
                  return this.spawnPlayerHead(worldIn, x, y, z, playerIn);
               }

               if (this.name.equals("inhoos")) {
                  entity = new EntityInhooS(worldIn);
               } else if (this.name.equals("inhoom")) {
                  entity = new EntityInhooM(worldIn);
               } else if (this.name.equals("infhorse") && SRPConfigMobs.infhorseEnabled) {
                  entity = new EntityInfHorse(worldIn);
               } else if (this.name.equals("infhorsehead") && SRPConfigMobs.infhorseEnabled) {
                  entity = new EntityInfHorseHead(worldIn);
               } else if (this.name.equals("infenderman") && SRPConfigMobs.infendermanEnabled) {
                  entity = new EntityInfEnderman(worldIn);
               } else if (this.name.equals("infendermanhead") && SRPConfigSystems.rsEnabled) {
                  entity = new EntityInfEndermanHead(worldIn);
               } else if (this.name.equals("infbear") && SRPConfigMobs.infbearEnabled) {
                  entity = new EntityInfBear(worldIn);
               } else if (this.name.equals("infdragone")) {
                  entity = new EntityInfDragonE(worldIn);
               } else if (this.name.equals("infdragonehead") && SRPConfigSystems.rsEnabled) {
                  entity = new EntityInfDragonEHead(worldIn);
               } else if (this.name.equals("host") && SRPConfigMobs.hostEnabled) {
                  entity = new EntityHost(worldIn);
               } else if (this.name.equals("hostii") && SRPConfigMobs.hostEnabled) {
                  entity = new EntityHostII(worldIn);
               } else if (this.name.equals("hull") && SRPConfigMobs.hullEnabled) {
                  entity = new EntityHull(worldIn);
               } else if (this.name.equals("hulladapted") && SRPConfigMobs.hullEnabled) {
                  entity = new EntityHullAdapted(worldIn);
               } else if (this.name.equals("canra") && SRPConfigMobs.canraEnabled) {
                  entity = new EntityCanra(worldIn);
               } else if (this.name.equals("canraadapted") && SRPConfigMobs.canraEnabled) {
                  entity = new EntityCanraAdapted(worldIn);
               } else if (this.name.equals("buthol") && SRPConfigMobs.butholEnabled) {
                  entity = new EntityButhol(worldIn);
               } else if (this.name.equals("alafha") && SRPConfigMobs.alafhaEnabled) {
                  entity = new EntityAlafha(worldIn);
               } else if (this.name.equals("nogla") && SRPConfigMobs.noglaEnabled) {
                  entity = new EntityNogla(worldIn);
               } else if (this.name.equals("noglaadapted") && SRPConfigMobs.noglaEnabled) {
                  entity = new EntityNoglaAdapted(worldIn);
               } else if (this.name.equals("bano") && SRPConfigMobs.zetmoEnabled) {
                  entity = new EntityBano(worldIn);
               } else if (this.name.equals("banoadapted") && SRPConfigMobs.zetmoEnabled) {
                  entity = new EntityBanoAdapted(worldIn);
               } else if (this.name.equals("oronco") && SRPConfigMobs.oroncoEnabled) {
                  entity = new EntityOronco(worldIn);
               } else if (this.name.equals("terla") && SRPConfigMobs.terlaEnabled) {
                  entity = new EntityTerla(worldIn);
               } else if (this.name.equals("anged") && SRPConfigMobs.angedEnabled) {
                  entity = new EntityAnged(worldIn);
               } else if (this.name.equals("lesh")) {
                  entity = new EntityLesh(worldIn);
               } else if (this.name.equals("tonro") && SRPConfigMobs.tonroEnabled) {
                  entity = new EntityTonro(worldIn);
               } else if (this.name.equals("unvo") && SRPConfigMobs.unvoEnabled) {
                  entity = new EntityUnvo(worldIn);
               } else if (this.name.equals("ganro") && SRPConfigMobs.ganroEnabled) {
                  entity = new EntityGanro(worldIn);
               } else if (this.name.equals("omboo") && SRPConfigMobs.ombooEnabled) {
                  entity = new EntityOmboo(worldIn);
               } else if (this.name.equals("esor") && SRPConfigMobs.esorEnabled) {
                  entity = new EntityEsor(worldIn);
               } else if (this.name.equals("orch") && SRPConfigMobs.esorEnabled) {
                  entity = new EntityOrch(worldIn);
               } else if (this.name.equals("flog") && SRPConfigMobs.flogEnabled) {
                  entity = new EntityFlog(worldIn);
               } else if (this.name.equals("wymo") && SRPConfigMobs.canraEnabled) {
                  entity = new EntityWymo(worldIn);
               } else if (this.name.equals("wymoadapted") && SRPConfigMobs.canraEnabled) {
                  entity = new EntityCanraAdapted(worldIn);
               } else if (this.name.equals("ranrac") && SRPConfigMobs.arachnidaEnabled) {
                  entity = new EntityRanrac(worldIn);
               } else if (this.name.equals("ranracadapted") && SRPConfigMobs.arachnidaEnabled) {
                  entity = new EntityRanracAdapted(worldIn);
               } else if (this.name.equals("lum") && SRPConfigMobs.arachnidaEnabled) {
                  entity = new EntityLum(worldIn);
               }
            }
         }

         entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);
         entity.rotationYawHead = entity.rotationYaw;
         entity.renderYawOffset = entity.rotationYaw;
         entity.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData)null);
         worldIn.spawnEntity(entity);
         return entity;
      }
   }

   private Entity spawnPlayer(World worldIn, double x, double y, double z, EntityPlayer playerIn) {
      EntityInfPlayer entity = new EntityInfPlayer(worldIn);
      ItemStack head = new ItemStack(playerIn.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem());
      ItemStack legs = new ItemStack(playerIn.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem());
      ItemStack feet = new ItemStack(playerIn.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem());
      if (head.getItem() != Items.AIR) {
         entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, head);
         entity.setHelmetSlot(true);
      }

      entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, legs);
      entity.setItemStackToSlot(EntityEquipmentSlot.FEET, feet);
      entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);
      entity.rotationYawHead = entity.rotationYaw;
      entity.renderYawOffset = entity.rotationYaw;
      entity.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData)null);
      entity.setCustomNameTag(playerIn.getName());
      entity.setAlwaysRenderNameTag(true);
      worldIn.spawnEntity(entity);
      return entity;
   }

   private Entity spawnPlayer2(World worldIn, double x, double y, double z, EntityPlayer playerIn) {
      EntityMes entity = new EntityMes(worldIn);
      entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);
      entity.rotationYawHead = entity.rotationYaw;
      entity.renderYawOffset = entity.rotationYaw;
      entity.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData)null);
      entity.setCustomNameTag(playerIn.getName());
      entity.setAlwaysRenderNameTag(true);
      worldIn.spawnEntity(entity);
      return entity;
   }

   private Entity spawnPlayerHead(World worldIn, double x, double y, double z, EntityPlayer playerIn) {
      EntityInfPlayerHead entity = new EntityInfPlayerHead(worldIn);
      entity.setLocationAndAngles(x, y, z, MathHelper.wrapDegrees(worldIn.rand.nextFloat() * 360.0F), 0.0F);
      entity.rotationYawHead = entity.rotationYaw;
      entity.renderYawOffset = entity.rotationYaw;
      entity.onInitialSpawn(worldIn.getDifficultyForLocation(new BlockPos(entity)), (IEntityLivingData)null);
      entity.setCustomNameTag(playerIn.getName());
      entity.setAlwaysRenderNameTag(true);
      worldIn.spawnEntity(entity);
      return entity;
   }

   @SideOnly(Side.CLIENT)
   public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
   }
}
