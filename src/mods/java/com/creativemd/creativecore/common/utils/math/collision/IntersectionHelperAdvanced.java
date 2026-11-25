package com.creativemd.creativecore.common.utils.math.collision;

import com.creativemd.creativecore.common.utils.math.VectorUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import net.minecraft.util.EnumFacing.Axis;

public class IntersectionHelperAdvanced {
   public static final int EDGE_Y_0 = 2;
   public static final int EDGE_X_1 = 4;
   public static final int EDGE_Y_1 = 6;
   public static final int EDGE_X_0 = 8;
   public static final int EDGE_EDGE_OFFSET = 2;
   public static final int CORNER_CORNER_OFFSET = 2;
   public static final int CORNER_EDGE_OFFSET = 1;
   public static final int cornerArrayLength = 4;
   public static final int VXX_STEP_SIZE = 2;
   public static final int NONE = 0;

   public IntersectionHelperAdvanced() {
   }

   public static List<Vector2f> getIntersectionShape(float minX, float minY, float maxX, float maxY, Axis one, Axis two, Vector3f[] corners) {
      float originOne = VectorUtils.get(one, corners[1]);
      float originTwo = VectorUtils.get(two, corners[1]);
      boolean inversed = (VectorUtils.get(one, corners[0]) - originOne) * (VectorUtils.get(two, corners[2]) - originTwo) - (VectorUtils.get(two, corners[0]) - originTwo) * (VectorUtils.get(one, corners[2]) - originOne) < 0.0F;
      List<Vector2f> result = new ArrayList(8);
      Vector2f prev = inversed ? new Vector2f(VectorUtils.get(one, corners[0]), VectorUtils.get(two, corners[0])) : new Vector2f(VectorUtils.get(one, corners[corners.length - 1]), VectorUtils.get(two, corners[corners.length - 1]));
      Vector2f cur = null;
      int cornerCount = 0;
      int lastIntersectingEdge = 0;
      int firstCornerCount = 0;
      IntersectionHelperAdvanced.Ray ray = new IntersectionHelperAdvanced.Ray();
      int ci = inversed ? corners.length : 0;

      while(true) {
         if (inversed) {
            if (ci-- <= 0) {
               break;
            }
         } else if (ci >= corners.length) {
            break;
         }

         cur = new Vector2f(VectorUtils.get(one, corners[ci]), VectorUtils.get(two, corners[ci]));
         ray.reset();
         if ((prev.x >= minX || cur.x >= minX) && (prev.x <= maxX || cur.x <= maxX) && (prev.y >= minY || cur.y >= minY) && (prev.y <= maxY || cur.y <= maxY)) {
            List<Integer> intersections = new ArrayList(2);
            List<Vector2f> postfix = new ArrayList(2);
            if (prev.x >= minX && prev.x <= maxX) {
               if (epsilionEquals(prev.x, minX) && epsilionEquals(cur.x, minX) || epsilionEquals(prev.x, maxX) && epsilionEquals(cur.x, maxX) || epsilionEquals(prev.y, minY) && epsilionEquals(cur.y, minY) || epsilionEquals(prev.y, maxY) && epsilionEquals(cur.y, maxY)) {
                  if ((prev.x >= minX || !epsilionEquals(cur.x, minX)) && (prev.x <= maxX || !epsilionEquals(cur.x, maxX)) && (prev.y >= minY || !epsilionEquals(cur.y, minY)) && (prev.y <= maxY || !epsilionEquals(cur.y, minY))) {
                     if ((!epsilionEqualsSmaller(minX, prev.x) || !epsilionEqualsSmaller(prev.x, maxX) || !epsilionEqualsSmaller(minY, prev.y) || !epsilionEqualsSmaller(prev.y, maxY)) && (prev.x >= minX || !epsilionEquals(cur.x, minX)) && (prev.x <= maxX || !epsilionEquals(cur.x, maxX)) && (prev.y >= minY || !epsilionEquals(cur.y, minY)) && (prev.y <= maxY || !epsilionEquals(cur.y, maxY))) {
                        postfix.add(getClosestNormalCorner(minX, minY, maxX, maxY, prev));
                     }

                     if ((!epsilionEqualsSmaller(minX, cur.x) || !epsilionEqualsSmaller(cur.x, maxX) || !epsilionEqualsSmaller(minY, cur.y) || !epsilionEqualsSmaller(cur.y, maxY)) && (cur.x >= minX || !epsilionEquals(prev.x, minX)) && (cur.x <= maxX || !epsilionEquals(prev.x, maxX)) && (cur.y >= minY || !epsilionEquals(prev.y, minY)) && (cur.y <= maxY || !epsilionEquals(prev.y, maxY))) {
                        postfix.add(getClosestNormalCorner(minX, minY, maxX, maxY, cur));
                     }
                  }
               } else if (prev.y >= minY && prev.y <= maxY) {
                  if ((cur.x >= minX || !epsilionEquals(prev.x, minX)) && (cur.x <= maxX || !epsilionEquals(prev.x, maxX)) && (cur.y >= minY || !epsilionEquals(prev.y, minY)) && (cur.y <= maxY || !epsilionEquals(prev.y, maxY))) {
                     if (cur.x >= minX && cur.x <= maxX) {
                        if (cur.y < minY || cur.y > maxY) {
                           intersections.add(Integer.valueOf(cur.y < minY ? 8 : 4));
                        }
                     } else if (cur.y >= minY && cur.y <= maxY) {
                        intersections.add(Integer.valueOf(cur.x < minX ? 2 : 6));
                     } else {
                        float angleCorner = angle(prev, getClosestNormalCorner(minX, minY, maxX, maxY, cur));
                        float angleCurrent = angle(prev, cur);
                        if (angleCorner > angleCurrent) {
                           intersections.add(Integer.valueOf(cur.x < minX ? 2 : 6));
                        } else if (angleCorner < angleCurrent) {
                           intersections.add(Integer.valueOf(cur.y < minY ? 8 : 4));
                        } else {
                           postfix.add(getClosestNormalCorner(minX, minY, maxX, maxY, cur));
                        }
                     }
                  }
               } else if ((prev.y >= minY || !epsilionEquals(cur.y, minY)) && (prev.y <= maxY || !epsilionEquals(cur.y, maxY))) {
                  intersections.add(Integer.valueOf(prev.y < maxY ? 8 : 4));
                  if (cur.x >= minX && cur.x <= minX) {
                     if (cur.y < minY || cur.y > maxY) {
                        intersections.add(Integer.valueOf(cur.y < minY ? 8 : 4));
                     }
                  } else if (epsilionEqualsSmaller(minY, cur.y) && epsilionEqualsSmaller(cur.y, maxY)) {
                     intersections.add(Integer.valueOf(cur.x < minX ? 2 : 6));
                  } else {
                     float angleCorner = angle(prev, getClosestNormalCorner(minX, minY, maxX, maxY, cur));
                     float angleCurrent = angle(prev, cur);
                     if (angleCorner > angleCurrent) {
                        intersections.add(Integer.valueOf(cur.x < minX ? 2 : 6));
                     } else if (angleCorner < angleCurrent) {
                        intersections.add(Integer.valueOf(prev.y < minY ? 4 : 8));
                     } else {
                        postfix.add(getClosestNormalCorner(minX, minY, maxX, maxY, cur));
                     }
                  }
               }
            } else if (prev.y >= minY && prev.y <= maxY) {
               if ((!epsilionEquals(prev.y, minY) || !epsilionEquals(cur.y, minY)) && (!epsilionEquals(prev.y, maxY) || !epsilionEquals(cur.y, maxY))) {
                  if (prev.x < minX && !epsilionEquals(cur.x, minX) || prev.x > maxX && !epsilionEquals(cur.x, maxX)) {
                     intersections.add(Integer.valueOf(prev.x < minX ? 2 : 6));
                     if (cur.x >= minX && cur.x <= maxX) {
                        if (cur.y < minY || cur.y > maxY) {
                           intersections.add(Integer.valueOf(cur.y < minY ? 8 : 4));
                        }
                     } else if (epsilionEqualsSmaller(minY, cur.y) && epsilionEqualsSmaller(cur.y, maxY) && !epsilionEquals(cur.x, prev.x < minX ? maxX : minX)) {
                        intersections.add(Integer.valueOf(cur.x < minX ? 2 : 6));
                     } else {
                        float angleCorner = angle(prev, getClosestNormalCorner(minX, minY, maxX, maxY, cur));
                        float angleCurrent = angle(prev, cur);
                        if (angleCorner > angleCurrent) {
                           intersections.add(Integer.valueOf(prev.x < minX ? 6 : 2));
                        } else if (angleCorner < angleCurrent) {
                           intersections.add(Integer.valueOf(cur.y < minY ? 8 : 4));
                        } else {
                           postfix.add(getClosestNormalCorner(minX, minY, maxX, maxY, cur));
                        }
                     }
                  }
               } else {
                  if ((!epsilionEqualsSmaller(minX, prev.x) || !epsilionEqualsSmaller(prev.x, maxX) || !epsilionEqualsSmaller(minY, prev.y) || !epsilionEqualsSmaller(prev.y, maxY)) && (prev.x >= minX || !epsilionEquals(cur.x, minX)) && (prev.x <= maxX || !epsilionEquals(cur.x, maxX))) {
                     postfix.add(getClosestNormalCorner(minX, minY, maxX, maxY, prev));
                  }

                  if ((!epsilionEqualsSmaller(minX, cur.x) || !epsilionEqualsSmaller(cur.x, maxX) || !epsilionEqualsSmaller(minY, cur.y) || !epsilionEqualsSmaller(cur.y, maxY)) && (cur.x >= minX || !epsilionEquals(prev.x, minX)) && (cur.x <= maxX || !epsilionEquals(prev.x, maxX))) {
                     postfix.add(getClosestNormalCorner(minX, minY, maxX, maxY, cur));
                  }
               }
            } else {
               float angleCorner = angle(prev, getClosestNormalCorner(minX, minY, maxX, maxY, prev));
               float angleCurrent = angle(prev, cur);
               if (epsilionGreater(angleCorner, angleCurrent)) {
                  if (!epsilionEqualsSmaller(minX, cur.x) || !epsilionEqualsSmaller(cur.x, maxX) || !epsilionEquals(cur.y, prev.y < minY ? minY : maxY)) {
                     intersections.add(Integer.valueOf(prev.y < minY ? 8 : 4));
                  }
               } else if (epsilionSmaller(angleCorner, angleCurrent)) {
                  if (!epsilionEqualsSmaller(minY, cur.y) || !epsilionEqualsSmaller(cur.y, maxY) || !epsilionEquals(cur.x, prev.x < minX ? minX : maxY)) {
                     intersections.add(Integer.valueOf(prev.x < minX ? 2 : 6));
                  }
               } else {
                  postfix.add(getClosestNormalCorner(minX, minY, maxX, maxY, prev));
               }

               if (cur.x < minX || cur.x > maxX || cur.y < minY || cur.y > maxY) {
                  angleCorner = angle(prev, getOpositeClosestNormalCorner(minX, minY, maxX, maxY, prev));
                  if (epsilionGreater(angleCorner, angleCurrent)) {
                     if (!epsilionEqualsSmaller(minY, cur.y) || !epsilionEqualsSmaller(cur.y, maxY) || !epsilionEquals(cur.x, prev.x < minX ? maxX : minX)) {
                        intersections.add(Integer.valueOf(prev.x < minX ? 6 : 2));
                     }
                  } else if (epsilionSmaller(angleCorner, angleCurrent)) {
                     if (!epsilionEqualsSmaller(minX, cur.x) || !epsilionEqualsSmaller(cur.x, maxX) || !epsilionEquals(cur.y, prev.y < minY ? maxY : minY)) {
                        intersections.add(Integer.valueOf(prev.y < minY ? 4 : 8));
                     }
                  } else {
                     postfix.add(getOpositeClosestNormalCorner(minX, minY, maxX, maxY, prev));
                  }
               }
            }

            for(int i = 0; i < intersections.size(); ++i) {
               if (!ray.isSet()) {
                  ray.set(prev, cur);
               }

               switch(((Integer)intersections.get(i)).intValue()) {
               case 2:
                  Vector2f intersection;
                  if ((intersection = ray.intersect(Axis.X, minX, (double)minY, (double)maxY)) != null) {
                     if (lastIntersectingEdge != 0) {
                        cornerCount = cornerCount + getCornerCount(minX, minY, maxX, maxY, lastIntersectingEdge, 2, ray, prev, cur);
                        if (cornerCount != 0 && lastIntersectingEdge != 0) {
                           addNormalCorners(minX, minY, maxX, maxY, result, lastIntersectingEdge, 2, cornerCount);
                        }

                        cornerCount = 0;
                        lastIntersectingEdge = 0;
                     }

                     result.add(intersection);
                  } else {
                     intersections.clear();
                  }
               case 3:
               case 5:
               case 7:
               default:
                  break;
               case 4:
                  Vector2f intersection;
                  if ((intersection = ray.intersect(Axis.Y, maxY, (double)minX, (double)maxX)) != null) {
                     if (lastIntersectingEdge != 0) {
                        cornerCount = cornerCount + getCornerCount(minX, minY, maxX, maxY, lastIntersectingEdge, 4, ray, prev, cur);
                        if (cornerCount != 0 && lastIntersectingEdge != 0) {
                           addNormalCorners(minX, minY, maxX, maxY, result, lastIntersectingEdge, 4, cornerCount);
                        }

                        cornerCount = 0;
                        lastIntersectingEdge = 0;
                     }

                     result.add(intersection);
                  } else {
                     intersections.clear();
                  }
                  break;
               case 6:
                  Vector2f intersection;
                  if ((intersection = ray.intersect(Axis.X, maxX, (double)minY, (double)maxY)) != null) {
                     if (lastIntersectingEdge != 0) {
                        cornerCount = cornerCount + getCornerCount(minX, minY, maxX, maxY, lastIntersectingEdge, 6, ray, prev, cur);
                        if (cornerCount != 0 && lastIntersectingEdge != 0) {
                           addNormalCorners(minX, minY, maxX, maxY, result, lastIntersectingEdge, 6, cornerCount);
                        }

                        cornerCount = 0;
                        lastIntersectingEdge = 0;
                     }

                     result.add(intersection);
                  } else {
                     intersections.clear();
                  }
                  break;
               case 8:
                  Vector2f intersection;
                  if ((intersection = ray.intersect(Axis.Y, minY, (double)minX, (double)maxX)) != null) {
                     if (lastIntersectingEdge != 0) {
                        cornerCount = cornerCount + getCornerCount(minX, minY, maxX, maxY, lastIntersectingEdge, 8, ray, prev, cur);
                        if (cornerCount != 0 && lastIntersectingEdge != 0) {
                           addNormalCorners(minX, minY, maxX, maxY, result, lastIntersectingEdge, 8, cornerCount);
                        }

                        cornerCount = 0;
                        lastIntersectingEdge = 0;
                     }

                     result.add(intersection);
                  } else {
                     intersections.clear();
                  }
               }
            }

            if (!postfix.isEmpty()) {
               if (lastIntersectingEdge != 0) {
                  if (lastIntersectingEdge != 0) {
                     cornerCount = cornerCount + getCornerCount(minX, minY, maxX, maxY, lastIntersectingEdge, getEdgeFrom(minX, minY, maxX, maxY, !result.isEmpty() ? (Vector2f)result.get(result.size() - 1) : (Vector2f)postfix.get(0)), ray, prev, cur);
                     if (cornerCount != 0) {
                        addNormalCorners(minX, minY, maxX, maxY, result, lastIntersectingEdge, getEdgeFrom(minX, minY, maxX, maxY, (Vector2f)postfix.get(0)), cornerCount);
                     }
                  }

                  cornerCount = 0;
                  lastIntersectingEdge = 0;
               }

               for(int i = 0; i < postfix.size(); ++i) {
                  result.add(postfix.get(i));
               }
            }

            if (epsilionEqualsSmaller(minX, cur.x) && epsilionEqualsSmaller(cur.x, maxX) && epsilionEqualsSmaller(minY, cur.y) && epsilionEqualsSmaller(cur.y, maxY)) {
               if (lastIntersectingEdge != 0) {
                  if (!epsilionEquals(cur.x, minX) && !epsilionEquals(cur.x, maxX) && !epsilionEquals(cur.y, minY) && !epsilionEquals(cur.y, maxY)) {
                     cornerCount = cornerCount + getCornerCount(minX, minY, maxX, maxY, lastIntersectingEdge, getEdgeFrom(minX, minY, maxX, maxY, !result.isEmpty() ? (Vector2f)result.get(result.size() - 1) : cur), ray, prev, cur);
                  } else {
                     cornerCount = cornerCount + getCornerCount(minX, minY, maxX, maxY, lastIntersectingEdge, getEdgeFrom(minX, minY, maxX, maxY, cur), ray, prev, cur);
                  }

                  if (cornerCount != 0 && lastIntersectingEdge != 0) {
                     addNormalCorners(minX, minY, maxX, maxY, result, lastIntersectingEdge, getEdgeFrom(minX, minY, maxX, maxY, cur), cornerCount);
                  }

                  cornerCount = 0;
                  lastIntersectingEdge = 0;
               }

               result.add(new Vector2f(cur));
            } else {
               if (lastIntersectingEdge == 0) {
                  lastIntersectingEdge = getEdgeFrom(minX, minY, maxX, maxY, !result.isEmpty() ? (Vector2f)result.get(result.size() - 1) : prev);
               }

               cornerCount += getCornerCount(minX, minY, maxX, maxY, lastIntersectingEdge, 0, ray, prev, cur);
               if (result.isEmpty()) {
                  firstCornerCount = cornerCount;
               }
            }

            prev.set(cur);
         } else {
            cornerCount += getCornerCount(minX, minY, maxX, maxY, lastIntersectingEdge, 0, ray, prev, cur);
            if (result.isEmpty()) {
               firstCornerCount = cornerCount;
            }

            prev = cur;
         }

         ci += inversed ? 0 : 1;
      }

      if (firstCornerCount + cornerCount != 0) {
         if (!result.isEmpty()) {
            if (lastIntersectingEdge != 0) {
               addNormalCorners(minX, minY, maxX, maxY, result, lastIntersectingEdge, getEdgeFrom(minX, minY, maxX, maxY, (Vector2f)result.get(0)), firstCornerCount + cornerCount);
            }
         } else {
            result = Arrays.asList(new Vector2f(minX, minY), new Vector2f(minX, maxY), new Vector2f(maxX, maxY), new Vector2f(maxX, minY));
         }
      }

      if (inversed) {
         Collections.reverse(result);
      }

      return result;
   }

   public static int getCornerCount(float minX, float minY, float maxX, float maxY, int lastIntersectingEdge, int currentIntersectingEdge, IntersectionHelperAdvanced.Ray ray, Vector2f prev, Vector2f cur) {
      if (prev.x < minX) {
         if (prev.y < minY) {
            if (cur.x > maxX) {
               if (cur.y > maxY) {
                  if (ray.intersect(Axis.Y, minY) < minX) {
                     return -4;
                  }

                  return 4;
               }

               if (cur.y < minY) {
                  return -2;
               }

               return -3;
            }

            if (!epsilionEqualsGreater(cur.x, minX)) {
               if (epsilionEqualsGreater(cur.y, maxY)) {
                  return 2;
               }

               if (cur.y > minY) {
                  return 1;
               }

               return 0;
            }

            if (cur.y < minY) {
               return -1;
            }

            if (cur.y > maxY) {
               return 3;
            }

            if (currentIntersectingEdge == 2) {
               return 2;
            }

            if (currentIntersectingEdge == 2) {
               return 1;
            }

            if (currentIntersectingEdge == 1) {
               return 0;
            }

            if (currentIntersectingEdge == 8) {
               return -1;
            }

            if (currentIntersectingEdge == 7) {
               return -2;
            }
         } else if (prev.y > maxY) {
            if (cur.x > maxX) {
               if (cur.y < minY) {
                  if (ray.intersect(Axis.Y, minY) < minX) {
                     return -4;
                  }

                  return 4;
               }

               if (cur.y > maxY) {
                  return 2;
               }

               return 3;
            }

            if (!epsilionEqualsGreater(cur.x, minX)) {
               if (epsilionEqualsSmaller(cur.y, minY)) {
                  return -2;
               }

               if (cur.y < maxY) {
                  return -1;
               }

               return 0;
            }

            if (cur.y < minY) {
               return -3;
            }

            if (cur.y > maxY) {
               return 1;
            }

            if (currentIntersectingEdge == 1) {
               return -2;
            }

            if (currentIntersectingEdge == 2) {
               return -1;
            }

            if (currentIntersectingEdge == 3) {
               return 0;
            }

            if (currentIntersectingEdge == 4) {
               return 1;
            }

            if (currentIntersectingEdge == 5) {
               return 2;
            }
         } else {
            if (cur.x > maxX) {
               if (cur.y < minY) {
                  return -3;
               }

               return 3;
            }

            if (!epsilionEqualsGreater(cur.x, minX)) {
               if (cur.y < minY) {
                  return -1;
               }

               if (cur.y > maxY) {
                  return 1;
               }

               return 0;
            }

            if (cur.y < minY) {
               return -2;
            }

            if (cur.y > maxY) {
               return 2;
            }

            if (currentIntersectingEdge == 1) {
               return -1;
            }

            if (currentIntersectingEdge == 2) {
               return 0;
            }

            if (currentIntersectingEdge == 3) {
               return 1;
            }
         }
      } else if (prev.x > maxX) {
         if (prev.y < minY) {
            if (cur.x < minX) {
               if (cur.y > maxY) {
                  if (ray.intersect(Axis.Y, minY) > maxX) {
                     return -4;
                  }

                  return 4;
               }

               if (cur.y < minY) {
                  return 2;
               }

               return 3;
            }

            if (!epsilionEqualsSmaller(cur.x, maxX)) {
               if (cur.y > maxY) {
                  return -2;
               }

               if (epsilionEqualsGreater(cur.y, minY)) {
                  return -1;
               }

               return 0;
            }

            if (cur.y < minY) {
               return 1;
            }

            if (cur.y > maxY) {
               return -3;
            }

            if (currentIntersectingEdge == 5) {
               return -2;
            }

            if (currentIntersectingEdge == 6) {
               return -1;
            }

            if (currentIntersectingEdge == 7) {
               return 0;
            }

            if (currentIntersectingEdge == 8) {
               return 1;
            }

            if (currentIntersectingEdge == 0) {
               return 2;
            }
         } else if (prev.y > maxY) {
            if (cur.x < minX) {
               if (cur.y < minY) {
                  if (ray.intersect(Axis.Y, minY) > maxX) {
                     return -4;
                  }

                  return 4;
               }

               if (cur.y > maxY) {
                  return -2;
               }

               return -3;
            }

            if (!epsilionEqualsSmaller(cur.x, maxX)) {
               if (cur.y < minY) {
                  return 2;
               }

               if (epsilionEqualsSmaller(cur.y, maxY)) {
                  return 1;
               }

               return 0;
            }

            if (cur.y < minY) {
               return 3;
            }

            if (cur.y > maxY) {
               return -1;
            }

            if (currentIntersectingEdge == 3) {
               return -2;
            }

            if (currentIntersectingEdge == 4) {
               return -1;
            }

            if (currentIntersectingEdge == 5) {
               return 0;
            }

            if (currentIntersectingEdge == 6) {
               return 1;
            }

            if (currentIntersectingEdge == 7) {
               return 2;
            }
         } else if (cur.x < minX) {
            if (cur.y < minY) {
               return 3;
            }

            if (cur.y > maxY) {
               return -3;
            }
         } else {
            if (!epsilionEqualsSmaller(cur.x, maxX)) {
               if (cur.y > maxY) {
                  return -1;
               }

               if (cur.y < minY) {
                  return 1;
               }

               return 0;
            }

            if (cur.y < minY) {
               return 2;
            }

            if (cur.y > maxY) {
               return -2;
            }

            if (currentIntersectingEdge == 5) {
               return -1;
            }

            if (currentIntersectingEdge == 7) {
               return 1;
            }

            if (currentIntersectingEdge == 6) {
               return 0;
            }
         }
      } else if (prev.y < minY) {
         if (cur.x < minX) {
            if (cur.y < minY) {
               return 1;
            }

            if (cur.y > maxY) {
               return 3;
            }

            return 2;
         }

         if (cur.x > maxX) {
            if (cur.y < minY) {
               return -1;
            }

            if (cur.y > maxY) {
               return -3;
            }

            return -2;
         }

         if (currentIntersectingEdge == 7) {
            return -1;
         }

         if (currentIntersectingEdge == 8) {
            return 0;
         }

         if (currentIntersectingEdge == 1) {
            return 1;
         }
      } else if (prev.y > maxY) {
         if (cur.x < minX) {
            if (cur.y < minY) {
               return -3;
            }

            if (cur.y > maxY) {
               return -1;
            }

            return -2;
         }

         if (cur.x > maxX) {
            if (cur.y < minY) {
               return 3;
            }

            if (cur.y > maxY) {
               return 1;
            }

            return 2;
         }

         if (currentIntersectingEdge == 3) {
            return -1;
         }

         if (currentIntersectingEdge == 4) {
            return 0;
         }

         if (currentIntersectingEdge == 5) {
            return 1;
         }
      } else {
         switch(lastIntersectingEdge) {
         case 0:
         default:
            break;
         case 1:
            if (cur.y > maxY) {
               return 2;
            }

            if (epsilionEqualsGreater(cur.y, minY)) {
               return 1;
            }

            if (cur.x > maxX) {
               return -2;
            }

            if (epsilionEqualsGreater(cur.x, minX)) {
               return -1;
            }

            return 0;
         case 2:
            if (cur.y < minY) {
               return -1;
            }

            if (cur.y > maxY) {
               return 1;
            }

            return 0;
         case 3:
            if (cur.x > maxX) {
               return 2;
            }

            if (epsilionEqualsGreater(cur.x, minX)) {
               return 1;
            }

            if (cur.y < minY) {
               return -2;
            }

            if (epsilionEqualsSmaller(cur.y, maxY)) {
               return -1;
            }

            return 0;
         case 4:
            if (cur.x < minX) {
               return -1;
            }

            if (cur.x > maxX) {
               return 1;
            }

            return 0;
         case 5:
            if (cur.y < minY) {
               return 2;
            }

            if (epsilionEqualsSmaller(cur.y, maxY)) {
               return 1;
            }

            if (cur.x < minX) {
               return -2;
            }

            if (epsilionEqualsSmaller(cur.x, maxX)) {
               return -1;
            }

            return 0;
         case 6:
            if (cur.y < minY) {
               return 1;
            }

            if (cur.y > maxY) {
               return -1;
            }

            return 0;
         case 7:
            if (cur.x < minX) {
               return 2;
            }

            if (epsilionEqualsSmaller(cur.x, maxX)) {
               return 1;
            }

            if (cur.y > maxY) {
               return -2;
            }

            if (epsilionEqualsGreater(cur.y, minY)) {
               return -1;
            }

            return 0;
         case 8:
            if (cur.x < minX) {
               return 1;
            }

            if (cur.x > maxX) {
               return -1;
            }

            return 0;
         }
      }

      return 0;
   }

   public static Vector2f getCorner(float minX, float minY, float maxX, float maxY, int index) {
      switch(index) {
      case 0:
         return new Vector2f(minX, minY);
      case 1:
         return new Vector2f(minX, maxY);
      case 2:
         return new Vector2f(maxX, maxY);
      case 3:
         return new Vector2f(maxX, minY);
      default:
         return null;
      }
   }

   public static void addNormalCorners(float minX, float minY, float maxX, float maxY, List<Vector2f> result, int lastIntersectingEdge, int currentIntersectingEdge, int cornerCount) {
      if (cornerCount > 0) {
         if (lastIntersectingEdge % 2 == 1) {
            if (currentIntersectingEdge - lastIntersectingEdge == 2) {
               return;
            }

            ++lastIntersectingEdge;
         }

         if (currentIntersectingEdge % 2 == 1) {
            --currentIntersectingEdge;
         }

         if (currentIntersectingEdge < lastIntersectingEdge) {
            currentIntersectingEdge += 8;
         }

         for(int i = lastIntersectingEdge; i < currentIntersectingEdge; i += 2) {
            result.add(getCorner(minX, minY, maxX, maxY, (int)(Math.floor((double)(i / 2)) % 4.0D)));
         }
      } else if (cornerCount < 0) {
         if (lastIntersectingEdge % 2 == 1) {
            if (currentIntersectingEdge - lastIntersectingEdge == -2) {
               return;
            }

            --lastIntersectingEdge;
         }

         if (currentIntersectingEdge % 2 == 1) {
            ++currentIntersectingEdge;
         }

         if (lastIntersectingEdge < currentIntersectingEdge) {
            lastIntersectingEdge += 8;
         }

         int i = lastIntersectingEdge;

         while(true) {
            i -= 2;
            if (i <= currentIntersectingEdge) {
               break;
            }

            result.add(getCorner(minX, minY, maxX, maxY, (int)(Math.floor((double)(i / 2)) % 4.0D)));
         }
      }

   }

   public static boolean epsilionEquals(float value, float toCheck) {
      value = value - toCheck;
      return value > -1.0E-5F && value < 1.0E-5F;
   }

   public static boolean epsilionGreater(float value, float toCheck) {
      return value > toCheck && !epsilionEquals(value, toCheck);
   }

   public static boolean epsilionSmaller(float value, float toCheck) {
      return value < toCheck && !epsilionEquals(value, toCheck);
   }

   public static boolean epsilionEqualsGreater(float value, float toCheck) {
      return value > toCheck || epsilionEquals(value, toCheck);
   }

   public static boolean epsilionEqualsSmaller(float value, float toCheck) {
      return value < toCheck || epsilionEquals(value, toCheck);
   }

   public static int getEdgeFrom(float minX, float minY, float maxX, float maxY, Vector2f vec) {
      if (epsilionEquals(vec.x, minX)) {
         if (epsilionEquals(vec.y, minY)) {
            return 1;
         } else {
            return epsilionEquals(vec.y, maxY) ? 3 : 2;
         }
      } else if (epsilionEquals(vec.x, maxX)) {
         if (epsilionEquals(vec.y, minY)) {
            return 7;
         } else {
            return epsilionEquals(vec.y, maxY) ? 5 : 6;
         }
      } else if (epsilionEquals(vec.y, minY)) {
         if (epsilionEquals(vec.x, minX)) {
            return 9;
         } else {
            return epsilionEquals(vec.x, maxX) ? 7 : 8;
         }
      } else if (epsilionEquals(vec.y, maxY)) {
         if (epsilionEquals(vec.x, minX)) {
            return 3;
         } else {
            return epsilionEquals(vec.x, maxX) ? 5 : 4;
         }
      } else {
         return 0;
      }
   }

   public static int getNonantsEdgeFrom(float minX, float minY, float maxX, float maxY, Vector2f vec) {
      if (epsilionEqualsSmaller(vec.x, minX)) {
         if (epsilionEqualsSmaller(vec.y, minY)) {
            return 1;
         } else {
            return epsilionEqualsGreater(vec.y, maxY) ? 3 : 2;
         }
      } else if (epsilionEqualsGreater(vec.x, maxX)) {
         if (epsilionEqualsSmaller(vec.y, minY)) {
            return 7;
         } else {
            return epsilionEqualsGreater(vec.y, maxY) ? 5 : 6;
         }
      } else if (epsilionEqualsSmaller(vec.y, minY)) {
         if (epsilionEqualsSmaller(vec.x, minX)) {
            return 9;
         } else {
            return epsilionEqualsGreater(vec.x, maxX) ? 7 : 8;
         }
      } else if (epsilionEqualsGreater(vec.y, maxY)) {
         if (epsilionEqualsSmaller(vec.x, minX)) {
            return 3;
         } else {
            return epsilionEqualsGreater(vec.x, maxX) ? 5 : 4;
         }
      } else {
         return 0;
      }
   }

   public static Vector2f getClosestNormalCorner(float minX, float minY, float maxX, float maxY, Vector2f vec) {
      if (vec.x < (minX + maxX) / 2.0F) {
         return vec.y < (minY + maxY) / 2.0F ? new Vector2f(minX, minY) : new Vector2f(minX, maxY);
      } else {
         return vec.y < (minY + maxY) / 2.0F ? new Vector2f(maxX, minY) : new Vector2f(maxX, maxY);
      }
   }

   public static Vector2f getOpositeClosestNormalCorner(float minX, float minY, float maxX, float maxY, Vector2f vec) {
      if (vec.x < (minX + maxX) / 2.0F) {
         return vec.y < (minY + maxY) / 2.0F ? new Vector2f(maxX, maxY) : new Vector2f(maxX, minY);
      } else {
         return vec.y < (minY + maxY) / 2.0F ? new Vector2f(minX, maxY) : new Vector2f(minX, minY);
      }
   }

   public static boolean isBetween(float min, float max, float vec) {
      return epsilionEqualsSmaller(min, vec) && epsilionEqualsSmaller(vec, max);
   }

   public static float angle(Vector2f v) {
      if (v.x == 0.0F) {
         return 0.0F;
      } else {
         return v.y == 0.0F ? Float.POSITIVE_INFINITY : Math.abs(v.y) / Math.abs(v.x);
      }
   }

   public static float angle(Vector2f from, Vector2f to) {
      if (from.x == to.x) {
         return 0.0F;
      } else {
         return from.y == to.y ? Float.POSITIVE_INFINITY : Math.abs(to.y - from.y) / Math.abs(to.x - from.x);
      }
   }

   private static class Ray {
      public Vector2f origin;
      public Vector2f direction;
      private boolean set;

      private Ray() {
         this.origin = new Vector2f();
         this.direction = new Vector2f();
         this.set = false;
      }

      public void reset() {
         this.set = false;
      }

      public boolean isSet() {
         return this.set;
      }

      public void set(Vector2f a, Vector2f b) {
         this.origin.set(a);
         this.direction.set(b);
         this.direction.sub(a);
         this.direction.normalize();
         this.set = true;
      }

      public float intersect(Axis axis, float value) {
         if (axis == Axis.X) {
            return this.direction.x == 0.0F ? Float.NaN : this.origin.y + this.direction.y * (value - this.origin.x) / this.direction.x;
         } else {
            return this.direction.y == 0.0F ? Float.NaN : this.origin.x + this.direction.x * (value - this.origin.y) / this.direction.y;
         }
      }

      public Vector2f intersect(Axis axis, float value, double min, double max) {
         if (axis == Axis.X) {
            if (this.direction.x == 0.0F) {
               return null;
            } else {
               float result = this.origin.y + this.direction.y * (value - this.origin.x) / this.direction.x;
               return (double)result > min && (double)result < max ? new Vector2f(value, result) : null;
            }
         } else if (this.direction.y == 0.0F) {
            return null;
         } else {
            float result = this.origin.x + this.direction.x * (value - this.origin.y) / this.direction.y;
            return (double)result > min && (double)result < max ? new Vector2f(result, value) : null;
         }
      }
   }
}
