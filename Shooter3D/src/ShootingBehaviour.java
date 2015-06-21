
// ShootingBehaviour.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* The gun cone and laser beam are rotated to point at the placed
   clicked on the checkboard. Then a FireBeam thread is created
   to move ('fire') the beam at the location, and display an explosion.
 */

import java.lang.Math;
import javax.vecmath.*;
import javax.media.j3d.*;
import com.sun.j3d.utils.picking.*;
import com.sun.j3d.utils.picking.behaviors.PickMouseBehavior;


public class ShootingBehaviour extends PickMouseBehavior 
{
  // initial orientation of firing cone: straight up
  private static final Vector3d UPVEC = new Vector3d(0.0, 1.0, 0.0);

  private Point3d startPt;
  private boolean firstRotation, finishedShot;
  private double shootAngle;

  private ExplosionsClip explsClip;
  private LaserBeam laser;
  private GunTurret gun;

  // for repeated calculations
  private AxisAngle4d rotAxisAngle = new AxisAngle4d();
  private Vector3d clickVec = new Vector3d();
  private Vector3d axisVec = new Vector3d();


  public ShootingBehaviour(Canvas3D canvas, BranchGroup root, Bounds bounds,
				Point3d sp, ExplosionsClip ec, LaserBeam lb, GunTurret g) 
  {
    super(canvas, root, bounds);
    setSchedulingBounds(bounds);

    pickCanvas.setMode(PickCanvas.GEOMETRY_INTERSECT_INFO);
       // allows PickIntersection objects to be returned

    startPt = sp;  // location of the gun cone
    explsClip = ec;
    laser = lb;
    gun = g;

	firstRotation = true;   // to signal the undoing of previous rotations
    finishedShot = true;    // when true, we can fire another beam
  } // end of ShootingBehaviour()



  public void updateScene(int xpos, int ypos) 
  /* This method is called when something is picked.
     Only the floor is pickable, so obtain the intersect pt, rotate
     the gun cone and laser beam to point at it, then fire the beam.
  */
  {
    if (finishedShot) {   // previous shot has finished
	  pickCanvas.setShapeLocation(xpos, ypos);

	  Point3d eyePos = pickCanvas.getStartPosition();  // the viewer location

	  PickResult pickResult = null;
	  pickResult = pickCanvas.pickClosest();

	  if (pickResult != null) {
        // pickResultInfo(pickResult);

		PickIntersection pi = 
		    pickResult.getClosestIntersection(startPt);  
   			// get intercesection closest to the gun cone	
		Point3d intercept = pi.getPointCoordinatesVW();

        rotateToPoint(intercept);    // rotate the cone and beam
        double turnAngle = calcTurn(eyePos, intercept);

        finishedShot = false;
        new FireBeam(intercept, this, laser, explsClip, turnAngle).start();
					// fire the beam and show the explosion
      }
    }
  } // end of updateScene()


  private void pickResultInfo(PickResult pr)
  // used for checking PickResult values
  {
    // get picked node (should be a Shape3D)
	Shape3D shape = (Shape3D) pr.getNode(PickResult.SHAPE3D);
    if (shape != null)
      System.out.println("Shape3D: " + shape);
    else
      System.out.println("No Shape3D found");

    SceneGraphPath path = pr.getSceneGraphPath();   // path excludes locale & terminal
    if (path != null) {
      int pathLen = path.nodeCount();
      if (pathLen == 0)
        System.out.println("Empty path");
      else {
        for (int i=0; i < pathLen; i++) {
          Node node = path.getNode(i); 
          if (node instanceof Shape3D)
            System.out.print(i + ". Shape3D: " + node);
          else {
            System.out.println(i + ". Node: " + node);
            String name = (String) node.getUserData();
            if (name != null)
              System.out.println(name);
          }
        }
      }
    }
    else
      System.out.println("Path is null");
  }  // end of pickResultInfo()



  private void rotateToPoint(Point3d intercept)
  /* Turn the gun and beam cylinder to point at the point where the 
     mouse was clicked.
  */
  { if (!firstRotation) {   // undo previous rotations to gun and beam
      axisVec.negate();
      rotAxisAngle.set( axisVec, shootAngle );
      gun.makeRotation(rotAxisAngle);
      laser.makeRotation(rotAxisAngle);
    }

	clickVec.set( intercept.x-startPt.x, intercept.y-startPt.y, 
						intercept.z-startPt.z);   
	clickVec.normalize();
	axisVec.cross( UPVEC, clickVec);
    // System.out.println("axisVec: " + axisVec);

    shootAngle = UPVEC.angle(clickVec);
	// shootAngle = Math.acos( UPVEC.dot(clickVec));
    // System.out.println("shootAngle: " + shootAngle);

    rotAxisAngle.set(axisVec, shootAngle);     // build rotation
    // System.out.println("rotAxisAngle: " + rotAxisAngle);

    gun.makeRotation(rotAxisAngle);
    laser.makeRotation(rotAxisAngle);

    firstRotation = false;
  } // end of rotateToPoint()



  private double calcTurn(Point3d eyePos, Point3d intercept)
  /* Calculate the angle to turn the explosion image around the
     y-axis, so that it is facing towards the eye position.
     The image is not rotated around the x- or z- axes.
  */
  {  double zDiff = eyePos.z - intercept.z;
     double xDiff = eyePos.x - intercept.x;
     // System.out.println("zDiff: " + zDiff + "; xDiff: " + xDiff);

     double turnAngle = Math.atan2(xDiff, zDiff);
     // System.out.println("turnAngle: " + turnAngle);
     return turnAngle;
  } // end of calcAngle()


  public void setFinishedShot()
  /* Called by the FireBeam thread when it has moved the laser beam
     to its destination. The beam has been reset and this method is
     called to allow a new firing. */
  { finishedShot = true; }


} // end of ShootingBehaviour class