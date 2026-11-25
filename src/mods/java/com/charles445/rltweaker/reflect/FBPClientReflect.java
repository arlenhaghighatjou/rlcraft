package com.charles445.rltweaker.reflect;

import com.charles445.rltweaker.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FBPClientReflect {
   public final Class c_FBP = Class.forName("com.TominoCZ.FBP.FBP");
   public final Object o_FBP_INSTANCE;
   public final Block o_FBPBlock;
   public final Class c_FBPEventHandler;
   public final Object o_FBPEventHandler_eventHandler;
   public final Method m_FBPEventHandler_getNodeWithPos;
   public final Class c_FBPAnimationDummyBlock;
   public final ConcurrentHashMap<BlockPos, Object> o_FBPAnimationDummyBlock_blockNodes;
   public final Class c_BlockNode;
   public final Field f_BlockNode_state;
   public final Field f_BlockNode_originalBlock;
   public final Field f_BlockNode_meta;
   public final Field f_BlockNode_particle;
   public final Class c_FBPParticleBlock;
   public final Field f_FBPParticleBlock_block;
   public final Field f_FBPParticleBlock_blockState;
   public final Field f_FBPParticleBlock_modelPrefab;
   public final Field f_FBPParticleBlock_tileEntity;
   public final Field f_Particle_isExpired;

   public FBPClientReflect() throws Exception {
      this.o_FBP_INSTANCE = ReflectUtil.findField(this.c_FBP, "INSTANCE").get((Object)null);
      this.o_FBPBlock = (Block)ReflectUtil.findField(this.c_FBP, "FBPBlock").get((Object)null);
      this.o_FBPEventHandler_eventHandler = ReflectUtil.findField(this.c_FBP, "eventHandler").get(this.o_FBP_INSTANCE);
      this.c_FBPEventHandler = Class.forName("com.TominoCZ.FBP.handler.FBPEventHandler");
      this.m_FBPEventHandler_getNodeWithPos = ReflectUtil.findMethod(this.c_FBPEventHandler, "getNodeWithPos");
      this.c_FBPAnimationDummyBlock = Class.forName("com.TominoCZ.FBP.block.FBPAnimationDummyBlock");
      this.o_FBPAnimationDummyBlock_blockNodes = (ConcurrentHashMap)ReflectUtil.findField(this.c_FBPAnimationDummyBlock, "blockNodes").get(this.o_FBPBlock);
      this.c_BlockNode = Class.forName("com.TominoCZ.FBP.node.BlockNode");
      this.f_BlockNode_state = ReflectUtil.findField(this.c_BlockNode, "state");
      this.f_BlockNode_originalBlock = ReflectUtil.findField(this.c_BlockNode, "originalBlock");
      this.f_BlockNode_meta = ReflectUtil.findField(this.c_BlockNode, "meta");
      this.f_BlockNode_particle = ReflectUtil.findField(this.c_BlockNode, "particle");
      this.c_FBPParticleBlock = Class.forName("com.TominoCZ.FBP.particle.FBPParticleBlock");
      this.f_FBPParticleBlock_block = ReflectUtil.findField(this.c_FBPParticleBlock, "block");
      this.f_FBPParticleBlock_blockState = ReflectUtil.findField(this.c_FBPParticleBlock, "blockState");
      this.f_FBPParticleBlock_modelPrefab = ReflectUtil.findField(this.c_FBPParticleBlock, "modelPrefab");
      this.f_FBPParticleBlock_tileEntity = ReflectUtil.findField(this.c_FBPParticleBlock, "tileEntity");
      this.f_Particle_isExpired = ReflectUtil.findFieldAny(Particle.class, "isExpired", "isExpired");
   }

   @Nullable
   public Object getBlockNode(BlockPos pos) {
      return this.o_FBPAnimationDummyBlock_blockNodes.get(pos);
   }

   @Nullable
   public Object getBlockPosNode(BlockPos pos) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
      return this.m_FBPEventHandler_getNodeWithPos.invoke(this.o_FBPEventHandler_eventHandler, pos);
   }

   public void setBlockNodeToAir(Object blockNode) throws IllegalArgumentException, IllegalAccessException {
      Block block = Blocks.AIR;
      this.f_BlockNode_state.set(blockNode, Blocks.AIR.getDefaultState());
      this.f_BlockNode_originalBlock.set(blockNode, Blocks.AIR);
      this.f_BlockNode_meta.setInt(blockNode, -1);
      this.setParticleBlockToAir((Particle)this.f_BlockNode_particle.get(blockNode));
   }

   public void setParticleBlockToAir(Particle particle) throws IllegalArgumentException, IllegalAccessException {
      if (particle != null) {
         this.f_FBPParticleBlock_block.set(particle, Blocks.AIR);
         this.f_FBPParticleBlock_blockState.set(particle, Blocks.AIR.getDefaultState());
         this.f_FBPParticleBlock_modelPrefab.set(particle, Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(Blocks.AIR.getDefaultState()));
         this.f_FBPParticleBlock_tileEntity.set(particle, (Object)null);
         this.f_Particle_isExpired.setBoolean(particle, true);
      }
   }
}
