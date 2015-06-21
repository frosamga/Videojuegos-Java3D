
// WrapShooter3D.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* Create the usual scene, and add a gun, laser beam, explosion
   and sounds in makeGun().
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.behaviors.vp.*;


public class WrapShooter3D extends JPanel
// Holds the 3D canvas where the loaded image is displayed
{
  private static final int PWIDTH = 512;   // size of panel
  private static final int PHEIGHT = 512; 
  private static final int BOUNDSIZE = 100;  // larger than world

  private static final Point3d USERPOSN = new Point3d(0,5,20);
    // initial user position

  private SimpleUniverse su;
  private BranchGroup sceneBG;
  private BoundingSphere bounds;   // for environment nodes


  public WrapShooter3D()
  // construct the 3D canvas
  {
    setLayout( new BorderLayout() );
    setOpaque( false );
    setPreferredSize( new Dimension(PWIDTH, PHEIGHT));

    GraphicsConfiguration config =
                    SimpleUniverse.getPreferredConfiguration();
    Canvas3D canvas3D = new Canvas3D(config);
    add("Center", canvas3D);
    canvas3D.setFocusable(true);     // give focus to the canvas 
    canvas3D.requestFocus();

    su = new SimpleUniverse(canvas3D);

    // create an audio device
    AudioDevice audioDev = su.getViewer().createAudioDevice();
    
    createSceneGraph(canvas3D);
    initUserPosition();        // set user's viewpoint
    orbitControls(canvas3D);   // controls for moving the viewpoint

    su.addBranchGraph( sceneBG );
  } // end of WrapShooter3D()



  private void createSceneGraph(Canvas3D canvas3D) 
  // initilise the scene
  { 
    sceneBG = new BranchGroup();
/*  // used for picking debugging
    sceneBG.setUserData("the sceneBG node");
    sceneBG.setCapability(BranchGroup.ENABLE_PICK_REPORTING);    
*/
    bounds = new BoundingSphere(new Point3d(0,0,0), BOUNDSIZE);   

    lightScene();         // add the lights
    addBackground();      // add the sky
    sceneBG.addChild( new CheckerFloor().getBG() );  // add the floor

    makeGun(canvas3D);
  } // end of createSceneGraph()


  private void lightScene()
  /* One ambient light, 2 directional lights */
  {
    Color3f white = new Color3f(1.0f, 1.0f, 1.0f);

    // Set up the ambient light
    AmbientLight ambientLightNode = new AmbientLight(white);
    ambientLightNode.setInfluencingBounds(bounds);
    sceneBG.addChild(ambientLightNode);

    // Set up the directional lights
    Vector3f light1Direction  = new Vector3f(-1.0f, -1.0f, -1.0f);
       // left, down, backwards 
    Vector3f light2Direction  = new Vector3f(1.0f, -1.0f, 1.0f);
       // right, down, forwards

    DirectionalLight light1 = 
            new DirectionalLight(white, light1Direction);
    light1.setInfluencingBounds(bounds);
    sceneBG.addChild(light1);

    DirectionalLight light2 = 
        new DirectionalLight(white, light2Direction);
    light2.setInfluencingBounds(bounds);
    sceneBG.addChild(light2);
  }  // end of lightScene()


  private void addBackground()
  // A blue sky
  { Background back = new Background();
    back.setApplicationBounds( bounds );
    back.setColor(0.17f, 0.65f, 0.92f);    // sky colour
    sceneBG.addChild( back );
  }  // end of addBackground()


  private void orbitControls(Canvas3D c)
  /* OrbitBehaviour allows the user to rotate around the scene, and to
     zoom in and out.
  */
  { OrbitBehavior orbit = 
		new OrbitBehavior(c, OrbitBehavior.REVERSE_ALL);
    orbit.setSchedulingBounds(bounds);
    ViewingPlatform vp = su.getViewingPlatform();
    vp.setViewPlatformBehavior(orbit);	    
  }  // end of orbitControls()



  private void initUserPosition()
  // Set the user's initial viewpoint using lookAt()
  {
    ViewingPlatform vp = su.getViewingPlatform();
    TransformGroup steerTG = vp.getViewPlatformTransform();

    Transform3D t3d = new Transform3D();
    steerTG.getTransform(t3d);

    // args are: viewer posn, where looking, up direction
    t3d.lookAt( USERPOSN, new Point3d(0,0,0), new Vector3d(0,1,0));
    t3d.invert();

    steerTG.setTransform(t3d);
  }  // end of initUserPosition()



  // ---------------------- gun -----------------


  private void makeGun(Canvas3D canvas3D)
  /* The gun is a cylinder with a cone on top which can rotate.
     'Inside' the cone is a small red cylinder, which is
     animated to become a 'laser beam'. 
     When the user clicks on the checkboard, the beam is fired
     at the location of the click. When it hits the target, an 
     explosion occurs. 
     There are sounds for the beam and explosion.
  */
  { // starting vector for the gun cone and beam
    Vector3d startVec = new Vector3d(0, 2, 0);

    // the gun
    GunTurret gun = new GunTurret(startVec);
    sceneBG.addChild( gun.getGunBG() );

    // explosion and sound
    PointSound explPS = initSound("Explo1.wav");
    ExplosionsClip expl = new ExplosionsClip( startVec, explPS);
    sceneBG.addChild( expl.getExplBG() );

    // laser beam and sound
    PointSound beamPS = initSound("laser2.wav");
    LaserBeam laser = new LaserBeam( startVec, beamPS);
    sceneBG.addChild( laser.getBeamBG() );

    // the behaviour that controls the shooting
    ShootingBehaviour shootBeh = 
        new ShootingBehaviour(canvas3D, sceneBG, bounds,
								new Point3d(0,2,0), expl, laser, gun );
    sceneBG.addChild(shootBeh);
  } // end of makeGun()



  private PointSound initSound(String filename)
  // create a point sound using filename (located at (0,0,0))
  {
    MediaContainer soundMC = null;
    try {
      soundMC = new MediaContainer("file:sounds/" + filename);
      soundMC.setCacheEnable(true);   // load sound into media container
    }
    catch (Exception ex)
    {  System.out.println(ex); }

    // create a point sound
    PointSound ps = new PointSound();
    ps.setSchedulingBounds( bounds );
    ps.setSoundData( soundMC );

    ps.setInitialGain(1.0f);  // full on sound from the start

    ps.setCapability(PointSound.ALLOW_ENABLE_WRITE);    // can be switched on/off
    ps.setCapability(PointSound.ALLOW_POSITION_WRITE);  // position can be adjusted

    System.out.println("PointSound created from sounds/" + filename);
    return ps;
  } // end of initSound()


} // end of WrapShooter3D class