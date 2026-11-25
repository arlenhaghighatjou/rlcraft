package com.lycanitesmobs.core.entity.navigate;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ArenaNode {
   public List<ArenaNode> adjacentNodes = new ArrayList();
   public World world;
   public BlockPos pos;

   public ArenaNode(World world, BlockPos pos) {
      this.world = world;
      this.pos = pos;
   }

   public void addAdjacentNode(ArenaNode node) {
      if (node != null && !this.adjacentNodes.contains(node)) {
         this.adjacentNodes.add(node);
      }
   }

   public ArenaNode getRandomAdjacentNode() {
      int adjacentNodesSize = this.adjacentNodes.size();
      if (adjacentNodesSize <= 0) {
         return null;
      } else {
         return adjacentNodesSize == 1 ? (ArenaNode)this.adjacentNodes.get(0) : (ArenaNode)this.adjacentNodes.get(this.world.rand.nextInt(adjacentNodesSize));
      }
   }

   public ArenaNode getClosestAdjacentNode(BlockPos targetPos) {
      int adjacentNodesSize = this.adjacentNodes.size();
      if (adjacentNodesSize <= 0) {
         return null;
      } else if (adjacentNodesSize == 1) {
         return (ArenaNode)this.adjacentNodes.get(0);
      } else {
         double smallestDistance = this.pos.distanceSq(targetPos);
         ArenaNode closestNode = this;

         for(ArenaNode adjacentNode : this.adjacentNodes) {
            double distance = adjacentNode.pos.distanceSq(targetPos);
            if (distance < smallestDistance) {
               smallestDistance = distance;
               closestNode = adjacentNode;
            }
         }

         return closestNode;
      }
   }
}
