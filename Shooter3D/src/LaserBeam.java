
// LaserBeam.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* A LaserBeam object is a red cylinder which is rotated to
   point at the loaction picked by the user on the checkboard.
   FireBeam calls shootBeam() to move the beam step-by-step towards 
   the location, to simulate a shooting beam. The beam also has
   a sound which moves with it.

   BG-->beam TG-->red Cylinder
				|
				-->beamPS PointSound
*/

import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.*;


public class LaserBeam
{
  private static final Vector3d ORIGIN = new Vector3d(0, 0, 0);
  private static final double STEP_SIZE = 1.0;  // distance travelled each increment
  private static final long SLEEP_TIME = 100;   // ms time between moves of beam

  private BranchGroup beamBG;
  private TransformGroup beamTG;
  private PointSound beamPS;
  private Vector3d startVec, currVec, stepVec;
  private Point3d startPt;

  // for repeated calculations
  private Transform3D beamT3d = new Transform3D();
  private Vector3d currTrans = new Vector3d();
  private Transform3D rotT3d = new Transform3D();



  public LaserBeam(Vector3d start, PointSound snd)
  {
    startVec = start;
    startPt = new Point3d(start.x, start.y, start.z);
    beamPS = snd;

    currVec = new Vector3d();    // for movement calculations
    stepVec = new Vector3d();

    makeBeam();
  } // end of LaserBeam()


  private void makeBeam()
  // build the scene graph branch for the laser beam
  {
    // create transform group for the laser beam cylinder
    beamTG = new TransformGroup();
    beamTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
    beamTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

    // position laser beam
    beamT3d.set(startVec);   // so inside cone
    beamTG.setTransform(beamT3d);

    // initialise red appearance
    Appearance apRed = new Appearance();
    ColoringAttributes redCA = new ColoringAttributes();
    Color3f medRed = new Color3f(0.8f, 0.4f, 0.3f);
    redCA.setColor( medRed );
    apRed.setColoringAttributes( redCA );

    Cylinder beam = new Cylinder(0.1f, 0.5f, apRed);  // thin red cylinder
    beam.setPickable(false);

    // add beam and sound to TransformGroup
    beamTG.addChild( beam );
    beamTG.addChild(beamPS);

    // branchGroup holding the TG
    beamBG = new BranchGroup();
    beamBG.addChild(beamTG);
  }  // end of makeBeam()


  public BranchGroup getBeamBG()
  {  return beamBG;  }



  public void shootBeam(Point3d intercept)
  /* Move the beam with a delay between each move, until the beam 
     reaches its intercept. Set off the explosion.
     Called by FireBeam.
  */
  { double travelDist = startPt.distance(intercept);
    calcStepVec(intercept, travelDist);

    beamPS.setEnable(true);         // switch on laser beam sound

    double currDist = 0.0;
    currVec.set(startVec);
    beamTG.getTransform(beamT3d);   // get current beam transform

    while (currDist <= travelDist) {  // not at destination yet
      beamT3d.setTranslation(currVec);  // move the laser beam
      beamTG.setTransform(beamT3d);
      currVec.add(stepVec);
      currDist += STEP_SIZE;
      try {
        Thread.sleep(SLEEP_TIME);    // wait a while
      } 
      catch (Exception ex) {}
    }

    // reset beam to its orignal coordinates
    beamT3d.setTranslation(startVec);
    beamTG.setTransform(beamT3d);

    beamPS.setEnable(false);   // switch off laser beam sound
  }  // end of shootBeam()



  private void calcStepVec(Point3d intercept, double travelDist)
  /* Calculate the step vector, stepVec, to move the cylinder in
     steps from the start position to the intercept.
  */
  { double moveFrac = STEP_SIZE / travelDist;
    double incrX = (intercept.x - startPt.x) * moveFrac;
    double incrY = (intercept.y - startPt.y) * moveFrac;
    double incrZ = (intercept.z - startPt.z) * moveFrac;
    stepVec.set(incrX, incrY, incrZ);  // store as a vector
  } // end of calcStepVec()
  


  public void makeRotation(AxisAngle4d rotAxis)
  // Rotate the laser beam. Called by ShootingBehaviour.
  {
    beamTG.getTransform( beamT3d );        // get current transform
    // System.out.println("Start beamT3d: " + beamT3d);
    beamT3d.get( currTrans );              // get current translation
	beamT3d.setTranslation( ORIGIN );      // translate to origin

	rotT3d.setRotation( rotAxis );         // apply rotation
    beamT3d.mul(rotT3d);

	beamT3d.setTranslation( currTrans );   // translate back
    beamTG.setTransform( beamT3d );
    // System.out.println("End beamT3d: " + beamT3d);
  } // end of makeRotation()

} // end of LaserBeam class