
// ExplosionsClip.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* BG-->explSwitch-->explTG-->ImagesSeries
     |
     -->explPS PointSound

   An explosion is an ImagesSeries object below a transform
   group and switch node. The switch is used to make the
   explosion appear/disappear. The TG is used to move and
   rotate the ImagesSeries object. 

   The sound is not attached to explTG since it causes an error
   related to processSwitchChanged() at run-time when the sound
   is enabled. Instead the sound is moved explicitly via a call
   to setPosition().
*/

import java.util.*;
import java.awt.*;
import javax.media.j3d.*;
import javax.vecmath.*;


public class ExplosionsClip
{
  private static final Vector3d ORIGIN = new Vector3d(0, 0, 0);

  private BranchGroup explBG;
  private Switch explSwitch;
  private TransformGroup explTG;  
  private ImagesSeries explShape;
  private PointSound explPS;

  private Vector3d startVec, endVec;

  // for repeated calculations
  private Transform3D explT3d = new Transform3D();
  private Transform3D rotT3d = new Transform3D();


  public ExplosionsClip(Vector3d svec, PointSound snd)
  {
    startVec = svec;
    explPS = snd;
    endVec = new Vector3d(0,0,0);   // initial value

    explTG = new TransformGroup();
    explTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);  // will move
    explTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

    // position explosion at same place as beam center
    explT3d.set(startVec);
    explTG.setTransform(explT3d);

    // the explosion is an unpickable ImagesSeries object
    explShape = new ImagesSeries(2.0f, "images/explo", 6);
    explShape.setPickable(false);
    explTG.addChild( explShape );

    // create switch for explosions
    explSwitch = new Switch();
    explSwitch.setCapability(Switch.ALLOW_SWITCH_WRITE);
    explSwitch.addChild( explTG );
    explSwitch.setWhichChild( Switch.CHILD_NONE );   // invisible initially

    // branchGroup holding everything
    explBG = new BranchGroup();
    explBG.addChild(explSwitch);
    explBG.addChild(explPS);
  } // end of ExplosionsClip()


  public BranchGroup getExplBG()
  {  return explBG;  }



  public void showExplosion(double turnAngle, Point3d intercept)
  // turn to face eye and move to contact point
  {
    endVec.set(intercept.x, intercept.y, intercept.z);
    rotateMove(turnAngle, endVec);  

    explSwitch.setWhichChild( Switch.CHILD_ALL );   // make visible
    explPS.setPosition((float)intercept.x, (float)intercept.y, (float)intercept.z);  
									// move sound to intercept position
    explPS.setEnable(true);         // switch on explosion sound

    explShape.showSeries();         // show the explosion

    explPS.setEnable(false);        // switch off sound
    explSwitch.setWhichChild( Switch.CHILD_NONE );   // make invisible

    // face front again, and reset position
    rotateMove(-turnAngle, startVec); 
  }  // end of showExplosion()



  private void rotateMove(double turn, Vector3d vec)
  // rotate the explosion around the Y-axis, and move to vec
  {
    // System.out.println("turn: " + turn);
    explTG.getTransform(explT3d);       // get explosion's transform info
	explT3d.setTranslation(ORIGIN);     // move to origin

    rotT3d.rotY(turn);      // rotate around the y-axis
    explT3d.mul(rotT3d);

	explT3d.setTranslation(vec);        // move to vector
    // System.out.println("End explT3d: " + explT3d);

    explTG.setTransform(explT3d);       // update transform
  } // end of rotateMove()


} // end of ExplosionsClip class
