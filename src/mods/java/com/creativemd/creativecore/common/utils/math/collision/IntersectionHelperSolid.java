package com.creativemd.creativecore.common.utils.math.collision;

import com.creativemd.creativecore.common.utils.math.VectorUtils;
import com.creativemd.creativecore.common.utils.math.geo.Ray2d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import net.minecraft.util.EnumFacing.Axis;
import org.apache.commons.lang3.BooleanUtils;

public class IntersectionHelperSolid {
   public IntersectionHelperSolid() {
   }

   public static List<Vector2f> cutMinMax(Axis one, Axis two, float minOne, float minTwo, float maxOne, float maxTwo, Vector3f[] corners) {
      if (corners.length < 3) {
         return Collections.EMPTY_LIST;
      } else {
         boolean insideOneMin = false;
         boolean insideTwoMin = false;
         boolean insideOneMax = false;
         boolean insideTwoMax = false;
         boolean allInside = true;
         int insideIndex = -1;
         IntersectionHelperSolid.InsideStatus[] status = new IntersectionHelperSolid.InsideStatus[corners.length];

         for(int i = 0; i < corners.length; ++i) {
            float valueOne = VectorUtils.get(one, corners[i]);
            float valueTwo = VectorUtils.get(two, corners[i]);
            status[i] = IntersectionHelperSolid.InsideStatus.get(valueOne, valueTwo, minOne, minTwo, maxOne, maxTwo);
            if (status[i].isInside()) {
               if (insideIndex == -1) {
                  insideIndex = i;
               }

               insideTwoMax = true;
               insideTwoMin = true;
               insideOneMax = true;
               insideOneMin = true;
            } else {
               allInside = false;
               if (status[i].isOutsideOne()) {
                  if (status[i].outsideDirectionOne().booleanValue()) {
                     insideOneMax = true;
                  } else {
                     insideOneMin = true;
                  }
               } else {
                  insideOneMax = true;
                  insideOneMin = true;
               }

               if (status[i].isOutsideTwo()) {
                  if (status[i].outsideDirectionTwo().booleanValue()) {
                     insideTwoMax = true;
                  } else {
                     insideTwoMin = true;
                  }
               } else {
                  insideTwoMax = true;
                  insideTwoMin = true;
               }
            }
         }

         float originOne = VectorUtils.get(one, corners[1]);
         float originTwo = VectorUtils.get(two, corners[1]);
         boolean clockwise = (VectorUtils.get(one, corners[0]) - originOne) * (VectorUtils.get(two, corners[2]) - originTwo) - (VectorUtils.get(two, corners[0]) - originTwo) * (VectorUtils.get(one, corners[2]) - originOne) > 0.0F;
         if (allInside) {
            return clockwise ? Arrays.asList(new Vector2f(minOne, minTwo), new Vector2f(minOne, maxTwo), new Vector2f(maxOne, maxTwo), new Vector2f(maxOne, minTwo)) : Arrays.asList(new Vector2f(maxOne, minTwo), new Vector2f(maxOne, maxTwo), new Vector2f(minOne, maxTwo), new Vector2f(minOne, minTwo));
         } else if (insideOneMin && insideOneMax && insideTwoMin && insideTwoMax) {
            Ray2d ray = new Ray2d(one, two, 0.0D, 0.0D, 0.0D, 0.0D);
            List<Vector2f> result = new ArrayList();
            boolean hasFoundInside;
            if (!(hasFoundInside = insideIndex != -1)) {
               insideIndex = 0;
            }

            iterateLines(insideIndex, status.length, hasFoundInside, (IntersectionHelperSolid.Edge)null, ray, one, two, minOne, minTwo, maxOne, maxTwo, clockwise, status, corners, result);
            if (result.isEmpty()) {
               boolean aboveOneAboveTwo = false;
               boolean aboveOneBelowTwo = false;
               boolean belowOneAboveTwo = false;
               boolean belowOneBelowTwo = false;

               for(int j = 0; j < corners.length; ++j) {
                  if (VectorUtils.get(one, corners[j]) > minOne) {
                     if (VectorUtils.get(two, corners[j]) > minTwo) {
                        aboveOneAboveTwo = true;
                     } else {
                        aboveOneBelowTwo = true;
                     }
                  } else if (VectorUtils.get(two, corners[j]) > minTwo) {
                     belowOneAboveTwo = true;
                  } else {
                     belowOneBelowTwo = true;
                  }
               }

               if (aboveOneAboveTwo && aboveOneBelowTwo && belowOneAboveTwo && belowOneBelowTwo) {
                  if (clockwise) {
                     return Arrays.asList(new Vector2f(minOne, minTwo), new Vector2f(minOne, maxTwo), new Vector2f(maxOne, maxTwo), new Vector2f(maxOne, minTwo));
                  }

                  return Arrays.asList(new Vector2f(maxOne, minTwo), new Vector2f(maxOne, maxTwo), new Vector2f(minOne, maxTwo), new Vector2f(minOne, minTwo));
               }
            }

            return result.size() > 2 ? result : Collections.EMPTY_LIST;
         } else {
            return Collections.EMPTY_LIST;
         }
      }
   }

   private static IntersectionHelperSolid.Edge iterateLines(int offset, int count, boolean hasFoundInside, IntersectionHelperSolid.Edge edge, Ray2d ray, Axis one, Axis two, float minOne, float minTwo, float maxOne, float maxTwo, boolean clockwise, IntersectionHelperSolid.InsideStatus[] status, Vector3f[] corners, List<Vector2f> result) {
      float beforeOne = VectorUtils.get(one, corners[offset]);
      float beforeTwo = VectorUtils.get(two, corners[offset]);
      IntersectionHelperSolid.InsideStatus before = status[offset];
      boolean inside = before.isInside();
      if (inside) {
         add(new Vector2f(beforeOne, beforeTwo), result);
      }

      for(int i = 1; i <= count; ++i) {
         int currentIndex = (offset + i) % corners.length;
         Vector3f vec = corners[currentIndex];
         float nowOne = VectorUtils.get(one, vec);
         float nowTwo = VectorUtils.get(two, vec);
         IntersectionHelperSolid.InsideStatus current = status[currentIndex];
         if (inside) {
            if (current.isInside()) {
               add(new Vector2f(nowOne, nowTwo), result);
            } else {
               edge = findIntersection(ray, clockwise, minOne, minTwo, maxOne, maxTwo, current, beforeOne, beforeTwo, nowOne, nowTwo, (IntersectionHelperSolid.Edge)null, result);
               inside = false;
            }
         } else if (current.isInside()) {
            findIntersection(ray, clockwise, minOne, minTwo, maxOne, maxTwo, before, beforeOne, beforeTwo, nowOne, nowTwo, edge, result);
            add(new Vector2f(nowOne, nowTwo), result);
            edge = null;
            inside = true;
         } else {
            ray.originOne = (double)beforeOne;
            ray.originTwo = (double)beforeTwo;
            ray.directionOne = (double)(nowOne - beforeOne);
            ray.directionTwo = (double)(nowTwo - beforeTwo);
            IntersectionHelperSolid.Edge inEdge = null;
            float inT = 1.0F;
            IntersectionHelperSolid.Edge outEdge = null;
            float outT = 0.0F;
            if (before.outsideDirectionOne() != current.outsideDirectionOne()) {
               if (BooleanUtils.isFalse(before.outsideDirectionOne()) || BooleanUtils.isFalse(current.outsideDirectionOne())) {
                  float tempT = (float)ray.getT(one, (double)minOne);
                  float valueTwo = (float)(ray.originTwo + ray.directionTwo * (double)tempT);
                  if (tempT >= 0.0F && tempT <= 1.0F && valueTwo >= minTwo && valueTwo <= maxTwo) {
                     if (tempT < inT) {
                        inEdge = IntersectionHelperSolid.Edge.MIN_ONE;
                        inT = tempT;
                     }

                     if (tempT > outT) {
                        outEdge = IntersectionHelperSolid.Edge.MIN_ONE;
                        outT = tempT;
                     }
                  }
               }

               if (BooleanUtils.isTrue(before.outsideDirectionOne()) || BooleanUtils.isTrue(current.outsideDirectionOne())) {
                  float tempT = (float)ray.getT(one, (double)maxOne);
                  float valueTwo = (float)(ray.originTwo + ray.directionTwo * (double)tempT);
                  if (tempT >= 0.0F && tempT <= 1.0F && valueTwo >= minTwo && valueTwo <= maxTwo) {
                     if (tempT < inT) {
                        inEdge = IntersectionHelperSolid.Edge.MAX_ONE;
                        inT = tempT;
                     }

                     if (tempT > outT) {
                        outEdge = IntersectionHelperSolid.Edge.MAX_ONE;
                        outT = tempT;
                     }
                  }
               }
            }

            if (before.outsideDirectionTwo() != current.outsideDirectionTwo()) {
               if (BooleanUtils.isFalse(before.outsideDirectionTwo()) || BooleanUtils.isFalse(current.outsideDirectionTwo())) {
                  float tempT = (float)ray.getT(two, (double)minTwo);
                  float valueOne = (float)(ray.originOne + ray.directionOne * (double)tempT);
                  if (tempT >= 0.0F && tempT <= 1.0F && valueOne >= minOne && valueOne <= maxOne) {
                     if (tempT < inT) {
                        inEdge = IntersectionHelperSolid.Edge.MIN_TWO;
                        inT = tempT;
                     }

                     if (tempT > outT) {
                        outEdge = IntersectionHelperSolid.Edge.MIN_TWO;
                        outT = tempT;
                     }
                  }
               }

               if (BooleanUtils.isTrue(before.outsideDirectionTwo()) || BooleanUtils.isTrue(current.outsideDirectionTwo())) {
                  float tempT = (float)ray.getT(two, (double)maxTwo);
                  float valueOne = (float)(ray.originOne + ray.directionOne * (double)tempT);
                  if (tempT >= 0.0F && tempT <= 1.0F && valueOne >= minOne && valueOne <= maxOne) {
                     if (tempT < inT) {
                        inEdge = IntersectionHelperSolid.Edge.MAX_TWO;
                        inT = tempT;
                     }

                     if (tempT > outT) {
                        outEdge = IntersectionHelperSolid.Edge.MAX_TWO;
                        outT = tempT;
                     }
                  }
               }
            }

            if (inEdge != null && inEdge != outEdge) {
               if (!hasFoundInside) {
                  Vector2f outVec = ray.getFloat((double)outT);
                  Vector2f inVec = ray.getFloat((double)inT);
                  add(outVec, result);
                  edge = iterateLines(currentIndex, status.length - 1, true, outEdge, ray, one, two, minOne, minTwo, maxOne, maxTwo, clockwise, status, corners, result);
                  addCornersBetween(minOne, minTwo, maxOne, maxTwo, edge, inEdge, clockwise, result);
                  add(inVec, result);
                  return null;
               }

               addCornersBetween(minOne, minTwo, maxOne, maxTwo, edge, inEdge, clockwise, result);
               add(ray.getFloat((double)inT), result);
               add(ray.getFloat((double)outT), result);
               edge = outEdge;
            }
         }

         before = current;
         beforeOne = nowOne;
         beforeTwo = nowTwo;
      }

      return edge;
   }

   private static IntersectionHelperSolid.Edge findIntersection(Ray2d ray, boolean clockwise, float minOne, float minTwo, float maxOne, float maxTwo, IntersectionHelperSolid.InsideStatus status, float beforeOne, float beforeTwo, float nowOne, float nowTwo, IntersectionHelperSolid.Edge before, List<Vector2f> result) {
      ray.originOne = (double)beforeOne;
      ray.originTwo = (double)beforeTwo;
      ray.directionOne = (double)(nowOne - beforeOne);
      ray.directionTwo = (double)(nowTwo - beforeTwo);
      if (status.isOutsideOne()) {
         float edgeValue = status.outsideDirectionOne().booleanValue() ? maxOne : minOne;
         float intersection = (float)ray.get(ray.one, (double)edgeValue);
         if (intersection >= minTwo && intersection <= maxTwo) {
            if (before != null) {
               addCornersBetween(minOne, minTwo, maxOne, maxTwo, before, status.oneEdge(), clockwise, result);
            }

            add(new Vector2f(edgeValue, intersection), result);
            return status.oneEdge();
         }
      }

      if (status.isOutsideTwo()) {
         float edgeValue = status.outsideDirectionTwo().booleanValue() ? maxTwo : minTwo;
         float intersection = (float)ray.get(ray.two, (double)edgeValue);
         if (intersection >= minOne && intersection <= maxOne) {
            if (before != null) {
               addCornersBetween(minOne, minTwo, maxOne, maxTwo, before, status.twoEdge(), clockwise, result);
            }

            add(new Vector2f(intersection, edgeValue), result);
            return status.twoEdge();
         }
      }

      throw new RuntimeException("Impossible");
   }

   private static void add(Vector2f vec, List<Vector2f> result) {
      if (result.isEmpty() || !((Vector2f)result.get(result.size() - 1)).epsilonEquals(vec, 1.0E-4F)) {
         result.add(vec);
      }

   }

   private static void addCornersBetween(float minOne, float minTwo, float maxOne, float maxTwo, IntersectionHelperSolid.Edge start, IntersectionHelperSolid.Edge end, boolean clockwise, List<Vector2f> result) {
      IntersectionHelperSolid.Edge next;
      for(IntersectionHelperSolid.Edge current = start; current != end; current = next) {
         next = clockwise ? current.clockwise() : current.counterClockwise();
         if (current.one()) {
            add(new Vector2f(current.positive() ? maxOne : minOne, next.positive() ? maxTwo : minTwo), result);
         } else {
            add(new Vector2f(next.positive() ? maxOne : minOne, current.positive() ? maxTwo : minTwo), result);
         }
      }

   }

   private static enum Edge {
      MIN_ONE {
         public IntersectionHelperSolid.Edge clockwise() {
            return MAX_TWO;
         }

         public IntersectionHelperSolid.Edge counterClockwise() {
            return MIN_TWO;
         }

         public boolean one() {
            return true;
         }

         public boolean positive() {
            return false;
         }
      },
      MIN_TWO {
         public IntersectionHelperSolid.Edge clockwise() {
            return MIN_ONE;
         }

         public IntersectionHelperSolid.Edge counterClockwise() {
            return MAX_ONE;
         }

         public boolean one() {
            return false;
         }

         public boolean positive() {
            return false;
         }
      },
      MAX_ONE {
         public IntersectionHelperSolid.Edge clockwise() {
            return MIN_TWO;
         }

         public IntersectionHelperSolid.Edge counterClockwise() {
            return MAX_TWO;
         }

         public boolean one() {
            return true;
         }

         public boolean positive() {
            return true;
         }
      },
      MAX_TWO {
         public IntersectionHelperSolid.Edge clockwise() {
            return MAX_ONE;
         }

         public IntersectionHelperSolid.Edge counterClockwise() {
            return MIN_ONE;
         }

         public boolean one() {
            return false;
         }

         public boolean positive() {
            return true;
         }
      };

      private Edge() {
      }

      public abstract boolean one();

      public abstract boolean positive();

      public abstract IntersectionHelperSolid.Edge clockwise();

      public abstract IntersectionHelperSolid.Edge counterClockwise();
   }

   private static enum InsideStatus {
      INSIDE {
         public boolean isInside() {
            return true;
         }

         public boolean isOutsideOne() {
            return false;
         }

         public Boolean outsideDirectionOne() {
            return null;
         }

         public boolean isOutsideTwo() {
            return false;
         }

         public Boolean outsideDirectionTwo() {
            return null;
         }
      },
      OUTSIDE_MIN_ONE {
         public boolean isInside() {
            return false;
         }

         public boolean isOutsideOne() {
            return true;
         }

         public Boolean outsideDirectionOne() {
            return false;
         }

         public boolean isOutsideTwo() {
            return false;
         }

         public Boolean outsideDirectionTwo() {
            return null;
         }
      },
      OUTSIDE_MAX_ONE {
         public boolean isInside() {
            return false;
         }

         public boolean isOutsideOne() {
            return true;
         }

         public Boolean outsideDirectionOne() {
            return true;
         }

         public boolean isOutsideTwo() {
            return false;
         }

         public Boolean outsideDirectionTwo() {
            return null;
         }
      },
      OUTSIDE_MIN_TWO {
         public boolean isInside() {
            return false;
         }

         public boolean isOutsideOne() {
            return false;
         }

         public Boolean outsideDirectionOne() {
            return null;
         }

         public boolean isOutsideTwo() {
            return true;
         }

         public Boolean outsideDirectionTwo() {
            return false;
         }
      },
      OUTSIDE_MAX_TWO {
         public boolean isInside() {
            return false;
         }

         public boolean isOutsideOne() {
            return false;
         }

         public Boolean outsideDirectionOne() {
            return null;
         }

         public boolean isOutsideTwo() {
            return true;
         }

         public Boolean outsideDirectionTwo() {
            return true;
         }
      },
      OUTSIDE_MIN_ONE_MIN_TWO {
         public boolean isInside() {
            return false;
         }

         public boolean isOutsideOne() {
            return true;
         }

         public Boolean outsideDirectionOne() {
            return false;
         }

         public boolean isOutsideTwo() {
            return true;
         }

         public Boolean outsideDirectionTwo() {
            return false;
         }
      },
      OUTSIDE_MIN_ONE_MAX_TWO {
         public boolean isInside() {
            return false;
         }

         public boolean isOutsideOne() {
            return true;
         }

         public Boolean outsideDirectionOne() {
            return false;
         }

         public boolean isOutsideTwo() {
            return true;
         }

         public Boolean outsideDirectionTwo() {
            return true;
         }
      },
      OUTSIDE_MAX_ONE_MIN_TWO {
         public boolean isInside() {
            return false;
         }

         public boolean isOutsideOne() {
            return true;
         }

         public Boolean outsideDirectionOne() {
            return true;
         }

         public boolean isOutsideTwo() {
            return true;
         }

         public Boolean outsideDirectionTwo() {
            return false;
         }
      },
      OUTSIDE_MAX_ONE_MAX_TWO {
         public boolean isInside() {
            return false;
         }

         public boolean isOutsideOne() {
            return true;
         }

         public Boolean outsideDirectionOne() {
            return true;
         }

         public boolean isOutsideTwo() {
            return true;
         }

         public Boolean outsideDirectionTwo() {
            return true;
         }
      };

      private InsideStatus() {
      }

      public abstract boolean isInside();

      public abstract boolean isOutsideOne();

      public abstract Boolean outsideDirectionOne();

      public IntersectionHelperSolid.Edge oneEdge() {
         return this.outsideDirectionOne().booleanValue() ? IntersectionHelperSolid.Edge.MAX_ONE : IntersectionHelperSolid.Edge.MIN_ONE;
      }

      public abstract boolean isOutsideTwo();

      public abstract Boolean outsideDirectionTwo();

      public IntersectionHelperSolid.Edge twoEdge() {
         return this.outsideDirectionTwo().booleanValue() ? IntersectionHelperSolid.Edge.MAX_TWO : IntersectionHelperSolid.Edge.MIN_TWO;
      }

      public static IntersectionHelperSolid.InsideStatus get(float one, float two, float minOne, float minTwo, float maxOne, float maxTwo) {
         if (one >= minOne) {
            if (one <= maxOne) {
               if (two >= minTwo) {
                  return two <= maxTwo ? INSIDE : OUTSIDE_MAX_TWO;
               } else {
                  return OUTSIDE_MIN_TWO;
               }
            } else if (two >= minTwo) {
               return two <= maxTwo ? OUTSIDE_MAX_ONE : OUTSIDE_MAX_ONE_MAX_TWO;
            } else {
               return OUTSIDE_MAX_ONE_MIN_TWO;
            }
         } else if (two >= minTwo) {
            return two <= maxTwo ? OUTSIDE_MIN_ONE : OUTSIDE_MIN_ONE_MAX_TWO;
         } else {
            return OUTSIDE_MIN_ONE_MIN_TWO;
         }
      }
   }
}
