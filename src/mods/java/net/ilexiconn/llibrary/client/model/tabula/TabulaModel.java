package net.ilexiconn.llibrary.client.model.tabula;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaAnimationContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaCubeGroupContainer;
import net.ilexiconn.llibrary.client.model.tabula.container.TabulaModelContainer;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelBase;
import net.ilexiconn.llibrary.client.model.tools.AdvancedModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TabulaModel extends AdvancedModelBase {
   protected Map<String, AdvancedModelRenderer> cubes;
   protected List<AdvancedModelRenderer> rootBoxes;
   protected ITabulaModelAnimator tabulaAnimator;
   protected Map<String, AdvancedModelRenderer> identifierMap;
   protected double[] scale;
   protected List<TabulaAnimationContainer> animationDefinitions;
   protected Map<String, TabulaAnimationContainer> animations;

   public TabulaModel(TabulaModelContainer container, ITabulaModelAnimator tabulaAnimator) {
      this.cubes = new HashMap();
      this.rootBoxes = new ArrayList();
      this.identifierMap = new HashMap();
      this.animationDefinitions = new ArrayList();
      this.animations = new HashMap();
      this.textureWidth = container.getTextureWidth();
      this.textureHeight = container.getTextureHeight();
      this.tabulaAnimator = tabulaAnimator;

      for(TabulaCubeContainer cube : container.getCubes()) {
         this.parseCube(cube, (AdvancedModelRenderer)null);
      }

      container.getCubeGroups().forEach(this::parseCubeGroup);
      this.updateDefaultPose();
      this.scale = container.getScale();
      this.animationDefinitions.addAll(container.getAnimations());
      this.animationDefinitions.forEach((definition) -> {
         this.animations.put(definition.getName(), definition);
      });
   }

   public TabulaModel(TabulaModelContainer container) {
      this(container, (ITabulaModelAnimator)null);
   }

   private void parseCubeGroup(TabulaCubeGroupContainer container) {
      for(TabulaCubeContainer cube : container.getCubes()) {
         this.parseCube(cube, (AdvancedModelRenderer)null);
      }

      container.getCubeGroups().forEach(this::parseCubeGroup);
   }

   private void parseCube(TabulaCubeContainer cube, AdvancedModelRenderer parent) {
      AdvancedModelRenderer box = this.createBox(cube);
      this.cubes.put(cube.getName(), box);
      this.identifierMap.put(cube.getIdentifier(), box);
      if (parent != null) {
         parent.addChild(box);
      } else {
         this.rootBoxes.add(box);
      }

      for(TabulaCubeContainer child : cube.getChildren()) {
         this.parseCube(child, box);
      }

   }

   private AdvancedModelRenderer createBox(TabulaCubeContainer cube) {
      int[] textureOffset = cube.getTextureOffset();
      double[] position = cube.getPosition();
      double[] rotation = cube.getRotation();
      double[] offset = cube.getOffset();
      int[] dimensions = cube.getDimensions();
      AdvancedModelRenderer box = new AdvancedModelRenderer(this, cube.getName());
      box.setTextureOffset(textureOffset[0], textureOffset[1]);
      box.mirror = cube.isTextureMirrorEnabled();
      box.setRotationPoint((float)position[0], (float)position[1], (float)position[2]);
      box.addBox((float)offset[0], (float)offset[1], (float)offset[2], dimensions[0], dimensions[1], dimensions[2], 0.0F);
      box.rotateAngleX = (float)Math.toRadians(rotation[0]);
      box.rotateAngleY = (float)Math.toRadians(rotation[1]);
      box.rotateAngleZ = (float)Math.toRadians(rotation[2]);
      box.mirror = cube.isTextureMirrorEnabled();
      return box;
   }

   public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale, entity);
      GlStateManager.pushMatrix();
      GlStateManager.scale(this.scale[0], this.scale[1], this.scale[2]);

      for(AdvancedModelRenderer box : this.rootBoxes) {
         box.render(scale);
      }

      GlStateManager.popMatrix();
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale, Entity entity) {
      this.resetToDefaultPose();
      if (this.tabulaAnimator != null) {
         this.tabulaAnimator.setRotationAngles(this, entity, limbSwing, limbSwingAmount, ageInTicks, rotationYaw, rotationPitch, scale);
      }

   }

   public AdvancedModelRenderer getCube(String name) {
      return (AdvancedModelRenderer)this.cubes.get(name);
   }

   public AdvancedModelRenderer getCubeByIdentifier(String identifier) {
      return (AdvancedModelRenderer)this.identifierMap.get(identifier);
   }

   public Map<String, AdvancedModelRenderer> getCubes() {
      return this.cubes;
   }

   public TabulaAnimationContainer getAnimation(String name) {
      return (TabulaAnimationContainer)this.animations.get(name);
   }
}
