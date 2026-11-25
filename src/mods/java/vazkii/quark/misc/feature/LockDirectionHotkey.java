package vazkii.quark.misc.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockQuartz.EnumType;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentKeybind;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.api.IRotationLockHandler;
import vazkii.quark.base.client.ModKeybinds;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.network.message.MessageSetLockProfile;

public class LockDirectionHotkey extends Feature {
   private static final String TAG_LOCKED_ONCE = "quark:locked_once";
   private static final HashMap<String, LockDirectionHotkey.LockProfile> lockProfiles = new HashMap();
   private LockDirectionHotkey.LockProfile clientProfile;

   public LockDirectionHotkey() {
   }

   public void setupConfig() {
      lockProfiles.clear();
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      ModKeybinds.initLockKey();
   }

   @SubscribeEvent
   public void onBlockPlaced(PlaceEvent event) {
      if (!event.isCanceled() && event.getResult() != Result.DENY) {
         fixBlockRotation(event.getWorld(), event.getPlayer(), event.getPos());
      }
   }

   public static void fixBlockRotation(World world, EntityPlayer player, BlockPos pos) {
      IBlockState state = world.getBlockState(pos);
      String name = player.getName();
      if (lockProfiles.containsKey(name)) {
         LockDirectionHotkey.LockProfile profile = (LockDirectionHotkey.LockProfile)lockProfiles.get(name);
         setBlockRotated(world, state, pos, profile.facing.getOpposite(), true, profile.half);
      }

   }

   public static void setBlockRotated(World world, IBlockState state, BlockPos pos, EnumFacing face) {
      setBlockRotated(world, state, pos, face, false, -1);
   }

   public static void setBlockRotated(World world, IBlockState state, BlockPos pos, EnumFacing face, boolean stateCheck, int half) {
      IBlockState setState = state;
      ImmutableMap<IProperty<?>, Comparable<?>> props = state.getProperties();
      Block block = state.getBlock();
      if (block instanceof IRotationLockHandler) {
         setState = ((IRotationLockHandler)block).setRotation(world, pos, state, face, half != -1, half == 1);
      } else if (block == Blocks.BED && face.getAxis() != Axis.Y) {
         EnumFacing prevFace = (EnumFacing)state.getValue(BlockHorizontal.FACING);
         EnumFacing opposite = face.getOpposite();
         if (prevFace != opposite) {
            BlockPos prevPos = pos.offset(prevFace);
            setState = state.withProperty(BlockHorizontal.FACING, opposite);
            IBlockState inWorld = world.getBlockState(prevPos);
            if (inWorld.getBlock() == Blocks.BED) {
               world.setBlockToAir(prevPos);
               world.setBlockState(pos.offset(opposite), inWorld.withProperty(BlockHorizontal.FACING, opposite));
            }
         }
      } else if (props.containsKey(BlockDirectional.FACING)) {
         setState = state.withProperty(BlockDirectional.FACING, face);
      } else if (props.containsKey(BlockHorizontal.FACING) && face.getAxis() != Axis.Y) {
         if (block instanceof BlockStairs) {
            setState = state.withProperty(BlockHorizontal.FACING, face.getOpposite());
         } else {
            setState = state.withProperty(BlockHorizontal.FACING, face);
         }
      } else if (props.containsKey(BlockRotatedPillar.AXIS)) {
         setState = state.withProperty(BlockRotatedPillar.AXIS, face.getAxis());
      } else if (props.containsKey(BlockLog.LOG_AXIS)) {
         setState = state.withProperty(BlockLog.LOG_AXIS, EnumAxis.fromFacingAxis(face.getAxis()));
      } else if (props.containsKey(BlockQuartz.VARIANT)) {
         EnumType type = (EnumType)state.getValue(BlockQuartz.VARIANT);
         if (ImmutableSet.of(EnumType.LINES_X, EnumType.LINES_Y, EnumType.LINES_Z).contains(type)) {
            setState = state.withProperty(BlockQuartz.VARIANT, (Comparable)BlockQuartz.VARIANT.parseValue("lines_" + face.getAxis().getName()).or(EnumType.LINES_Y));
         }
      } else if (props.containsKey(BlockHopper.FACING)) {
         setState = state.withProperty(BlockHopper.FACING, face == EnumFacing.DOWN ? face : face.getOpposite());
      }

      if (half != -1) {
         if (props.containsKey(BlockStairs.HALF)) {
            setState = setState.withProperty(BlockStairs.HALF, half == 1 ? EnumHalf.TOP : EnumHalf.BOTTOM);
         } else if (props.containsKey(BlockSlab.HALF)) {
            setState = setState.withProperty(BlockSlab.HALF, half == 1 ? EnumBlockHalf.TOP : EnumBlockHalf.BOTTOM);
         }
      }

      if (!stateCheck || setState != state) {
         world.setBlockState(pos, setState);
         world.neighborChanged(pos, setState.getBlock(), pos);
      }

   }

   @SubscribeEvent
   public void onPlayerLogoff(PlayerLoggedOutEvent event) {
      lockProfiles.remove(event.player.getName());
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onKeyInput(KeyInputEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      boolean down = ModKeybinds.lockKey.isKeyDown();
      if (mc.inGameHasFocus && down) {
         RayTraceResult result = mc.objectMouseOver;
         LockDirectionHotkey.LockProfile newProfile;
         if (result != null && result.typeOfHit == Type.BLOCK) {
            int half = (int)((result.hitVec.y - (double)((int)result.hitVec.y)) * 2.0D);
            if (result.sideHit.getAxis() == Axis.Y) {
               half = -1;
            }

            newProfile = new LockDirectionHotkey.LockProfile(result.sideHit.getOpposite(), half);
         } else {
            Vec3d look = mc.player.getLookVec();
            newProfile = new LockDirectionHotkey.LockProfile(EnumFacing.getFacingFromVector((float)look.x, (float)look.y, (float)look.z), -1);
         }

         if (this.clientProfile != null && this.clientProfile.equals(newProfile)) {
            this.clientProfile = null;
         } else {
            this.clientProfile = newProfile;
         }

         NetworkHandler.INSTANCE.sendToServer(new MessageSetLockProfile(this.clientProfile));
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void onHUDRender(Post event) {
      if (event.getType() == ElementType.ALL && this.clientProfile != null) {
         Minecraft mc = Minecraft.getMinecraft();
         GlStateManager.pushMatrix();
         GlStateManager.enableBlend();
         GlStateManager.enableAlpha();
         GlStateManager.blendFunc(770, 771);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
         mc.renderEngine.bindTexture(LibMisc.GENERAL_ICONS_RESOURCE);
         int x = event.getResolution().getScaledWidth() / 2 + 20;
         int y = event.getResolution().getScaledHeight() / 2 - 8;
         GuiScreen.drawModalRectWithCustomSizedTexture(x, y, (float)(this.clientProfile.facing.ordinal() * 16), 32.0F, 16, 16, 256.0F, 256.0F);
         if (this.clientProfile.half > -1) {
            GuiScreen.drawModalRectWithCustomSizedTexture(x + 16, y, (float)(this.clientProfile.half * 16), 48.0F, 16, 16, 256.0F, 256.0F);
         }

         GlStateManager.popMatrix();
      }

   }

   public static void setProfile(EntityPlayer player, LockDirectionHotkey.LockProfile profile) {
      String name = player.getName();
      if (profile == null) {
         lockProfiles.remove(name);
      } else {
         boolean locked = player.getEntityData().getBoolean("quark:locked_once");
         if (!locked) {
            ITextComponent text = new TextComponentTranslation("quarkmisc.rotationLockBefore", new Object[0]);
            ITextComponent keybind = new TextComponentKeybind("quark.keybind.lockBuilding");
            keybind.getStyle().setColor(TextFormatting.AQUA);
            text.appendSibling(keybind);
            text.appendSibling(new TextComponentTranslation("quarkmisc.rotationLockAfter", new Object[0]));
            player.sendMessage(text);
            player.getEntityData().setBoolean("quark:locked_once", true);
         }

         lockProfiles.put(name, profile);
      }

   }

   public boolean hasSubscriptions() {
      return true;
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public static class LockProfile {
      public final EnumFacing facing;
      public final int half;

      public LockProfile(EnumFacing facing, int half) {
         this.facing = facing;
         this.half = half;
      }

      public static LockDirectionHotkey.LockProfile readProfile(ByteBuf buf) {
         boolean valid = buf.readBoolean();
         if (!valid) {
            return null;
         } else {
            int face = buf.readInt();
            int half = buf.readInt();
            return new LockDirectionHotkey.LockProfile(EnumFacing.getFront(face), half);
         }
      }

      public static void writeProfile(LockDirectionHotkey.LockProfile p, ByteBuf buf) {
         if (p == null) {
            buf.writeBoolean(false);
         } else {
            buf.writeBoolean(true);
            buf.writeInt(p.facing.getIndex());
            buf.writeInt(p.half);
         }

      }

      public boolean equals(Object other) {
         if (other == this) {
            return true;
         } else if (!(other instanceof LockDirectionHotkey.LockProfile)) {
            return false;
         } else {
            LockDirectionHotkey.LockProfile otherProfile = (LockDirectionHotkey.LockProfile)other;
            return otherProfile.facing == this.facing && otherProfile.half == this.half;
         }
      }
   }
}
