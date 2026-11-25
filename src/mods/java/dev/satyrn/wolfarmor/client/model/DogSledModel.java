package dev.satyrn.wolfarmor.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class DogSledModel extends ModelBase {
   private final ModelRenderer sled;
   private final ModelRenderer runners;
   private final ModelRenderer runnerFronts;
   private final ModelRenderer leftRunnerFront_r1;
   private final ModelRenderer brushBow;
   private final ModelRenderer leftBrushBowCurve;
   private final ModelRenderer rightBrushBowCurve;
   private final ModelRenderer brushBowFront;
   private final ModelRenderer stanchions;
   private final ModelRenderer topRails;
   private final ModelRenderer handleBar;
   private final ModelRenderer bed;
   private final ModelRenderer bedSupports;
   private final ModelRenderer seat;

   public DogSledModel() {
      this.textureWidth = 128;
      this.textureHeight = 32;
      this.sled = new ModelRenderer(this);
      this.sled.setRotationPoint(0.0F, 24.0F, 0.0F);
      this.setRotationAngle(this.sled, 0.0F, -1.5708F, 0.0F);
      this.runners = new ModelRenderer(this);
      this.runners.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.sled.addChild(this.runners);
      this.runners.cubeList.add(new ModelBox(this.runners, 0, 0, -28.0F, -1.0F, -9.0F, 48, 1, 2, 0.0F, false));
      this.runners.cubeList.add(new ModelBox(this.runners, 0, 0, -28.0F, -1.0F, 7.0F, 48, 1, 2, 0.0F, false));
      this.runnerFronts = new ModelRenderer(this);
      this.runnerFronts.setRotationPoint(-28.0F, 0.0F, 0.0F);
      this.runners.addChild(this.runnerFronts);
      this.leftRunnerFront_r1 = new ModelRenderer(this);
      this.leftRunnerFront_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.runnerFronts.addChild(this.leftRunnerFront_r1);
      this.setRotationAngle(this.leftRunnerFront_r1, 0.0F, 0.0F, 0.5236F);
      this.leftRunnerFront_r1.cubeList.add(new ModelBox(this.leftRunnerFront_r1, 100, 0, -8.0F, -1.0F, -9.0F, 8, 1, 2, 0.0F, false));
      this.leftRunnerFront_r1.cubeList.add(new ModelBox(this.leftRunnerFront_r1, 100, 0, -8.0F, -1.0F, 7.0F, 8, 1, 2, 0.0F, false));
      this.brushBow = new ModelRenderer(this);
      this.brushBow.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.sled.addChild(this.brushBow);
      this.setRotationAngle(this.brushBow, 0.0F, 0.0F, 0.1047F);
      this.brushBow.cubeList.add(new ModelBox(this.brushBow, 0, 3, -39.0F, -3.1F, -10.0F, 39, 2, 1, 0.0F, false));
      this.brushBow.cubeList.add(new ModelBox(this.brushBow, 0, 3, -39.0F, -3.1F, 9.0F, 39, 2, 1, 0.0F, false));
      this.leftBrushBowCurve = new ModelRenderer(this);
      this.leftBrushBowCurve.setRotationPoint(-38.675F, 0.0F, -9.075F);
      this.brushBow.addChild(this.leftBrushBowCurve);
      this.setRotationAngle(this.leftBrushBowCurve, 0.0F, 0.8378F, 0.0F);
      this.leftBrushBowCurve.cubeList.add(new ModelBox(this.leftBrushBowCurve, 80, 3, -7.6F, -3.1F, -0.9F, 8, 2, 1, 0.0F, false));
      this.rightBrushBowCurve = new ModelRenderer(this);
      this.rightBrushBowCurve.setRotationPoint(-38.675F, -1.0F, 9.075F);
      this.brushBow.addChild(this.rightBrushBowCurve);
      this.setRotationAngle(this.rightBrushBowCurve, 0.0F, -0.8378F, 0.0F);
      this.rightBrushBowCurve.cubeList.add(new ModelBox(this.rightBrushBowCurve, 80, 3, -7.625F, -2.1F, -0.1F, 8, 2, 1, 0.0F, false));
      this.brushBowFront = new ModelRenderer(this);
      this.brushBowFront.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.brushBow.addChild(this.brushBowFront);
      this.setRotationAngle(this.brushBowFront, 0.0F, -1.5708F, 0.0F);
      this.brushBowFront.cubeList.add(new ModelBox(this.brushBowFront, 98, 3, -4.0F, -3.1F, 43.45F, 8, 2, 1, 0.0F, false));
      this.stanchions = new ModelRenderer(this);
      this.stanchions.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.sled.addChild(this.stanchions);
      this.stanchions.cubeList.add(new ModelBox(this.stanchions, 0, 6, -2.0F, -13.0F, 8.0F, 2, 12, 1, 0.0F, false));
      this.stanchions.cubeList.add(new ModelBox(this.stanchions, 6, 6, -14.0F, -10.0F, -9.0F, 2, 9, 1, 0.0F, false));
      this.stanchions.cubeList.add(new ModelBox(this.stanchions, 12, 6, -27.0F, -7.0F, -9.0F, 2, 6, 1, 0.0F, false));
      this.stanchions.cubeList.add(new ModelBox(this.stanchions, 0, 6, -2.0F, -13.0F, -9.0F, 2, 12, 1, 0.0F, false));
      this.stanchions.cubeList.add(new ModelBox(this.stanchions, 12, 6, -27.0F, -7.0F, 8.0F, 2, 6, 1, 0.0F, false));
      this.stanchions.cubeList.add(new ModelBox(this.stanchions, 6, 6, -14.0F, -10.0F, 8.0F, 2, 9, 1, 0.0F, false));
      this.topRails = new ModelRenderer(this);
      this.topRails.setRotationPoint(0.0F, -13.0F, 0.0F);
      this.sled.addChild(this.topRails);
      this.setRotationAngle(this.topRails, 0.0F, 0.0F, -0.2531F);
      this.topRails.cubeList.add(new ModelBox(this.topRails, 12, 13, -36.0F, -1.0F, -9.0F, 37, 1, 1, 0.0F, false));
      this.topRails.cubeList.add(new ModelBox(this.topRails, 12, 13, -36.0F, -1.0F, 8.0F, 37, 1, 1, 0.0F, false));
      this.topRails.cubeList.add(new ModelBox(this.topRails, 88, 13, 3.0F, -1.0F, 8.0F, 4, 1, 1, 0.0F, false));
      this.topRails.cubeList.add(new ModelBox(this.topRails, 88, 13, 3.0F, -1.0F, -9.0F, 4, 1, 1, 0.0F, false));
      this.handleBar = new ModelRenderer(this);
      this.handleBar.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.topRails.addChild(this.handleBar);
      this.setRotationAngle(this.handleBar, 0.0F, -1.5708F, 0.0F);
      this.handleBar.cubeList.add(new ModelBox(this.handleBar, 12, 15, -11.0F, -1.0F, -3.0F, 22, 1, 2, 0.0F, false));
      this.bed = new ModelRenderer(this);
      this.bed.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.sled.addChild(this.bed);
      this.bed.cubeList.add(new ModelBox(this.bed, 0, 19, -35.0F, -6.0F, 5.0F, 35, 1, 2, 0.0F, false));
      this.bed.cubeList.add(new ModelBox(this.bed, 0, 19, -35.0F, -6.0F, -7.0F, 35, 1, 2, 0.0F, false));
      this.bed.cubeList.add(new ModelBox(this.bed, 0, 19, -35.0F, -6.0F, 2.0F, 35, 1, 2, 0.0F, false));
      this.bed.cubeList.add(new ModelBox(this.bed, 0, 19, -35.0F, -6.0F, -4.0F, 35, 1, 2, 0.0F, false));
      this.bed.cubeList.add(new ModelBox(this.bed, 0, 19, -35.0F, -6.0F, -1.0F, 35, 1, 2, 0.0F, false));
      this.bedSupports = new ModelRenderer(this);
      this.bedSupports.setRotationPoint(-36.0F, -5.0F, -8.0F);
      this.bed.addChild(this.bedSupports);
      this.setRotationAngle(this.bedSupports, 0.0F, 1.5708F, 0.0F);
      this.bedSupports.cubeList.add(new ModelBox(this.bedSupports, 74, 19, -17.0F, -1.0F, -1.0F, 18, 1, 2, 0.0F, false));
      this.bedSupports.cubeList.add(new ModelBox(this.bedSupports, 0, 22, -16.0F, 0.0F, 9.0F, 16, 1, 2, 0.0F, false));
      this.bedSupports.cubeList.add(new ModelBox(this.bedSupports, 0, 22, -16.0F, 0.0F, 22.0F, 16, 1, 2, 0.0F, false));
      this.bedSupports.cubeList.add(new ModelBox(this.bedSupports, 0, 22, -16.0F, 0.0F, 34.0F, 16, 1, 2, 0.0F, false));
      this.seat = new ModelRenderer(this);
      this.seat.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.sled.addChild(this.seat);
      this.setRotationAngle(this.seat, 0.0F, 1.5708F, 1.5708F);
      this.seat.cubeList.add(new ModelBox(this.seat, 0, 25, -9.0F, -19.0F, -4.0F, 18, 4, 1, 0.0F, false));
      this.seat.cubeList.add(new ModelBox(this.seat, 0, 25, -9.0F, -14.0F, -4.0F, 18, 4, 1, 0.0F, false));
      this.seat.cubeList.add(new ModelBox(this.seat, 0, 25, -9.0F, -9.0F, -4.0F, 18, 4, 1, 0.0F, false));
      this.seat.cubeList.add(new ModelBox(this.seat, 120, 0, -8.0F, -18.0F, -3.0F, 1, 12, 2, 0.0F, false));
      this.seat.cubeList.add(new ModelBox(this.seat, 120, 0, 7.0F, -18.0F, -3.0F, 1, 12, 2, 0.0F, false));
   }

   public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.sled.render(f5);
   }

   public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
   }
}
