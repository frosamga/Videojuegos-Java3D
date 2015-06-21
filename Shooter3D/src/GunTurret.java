
// GunTurret.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* The GunTurret is a cylinder with a cone top which rotates
   to point at the checkboard location picked by the user.

   gun BG --> base TG --> cylinder
          |
          --> gun TG  --> cone

   The base TG is used only to initially position the cylinder so
   it is resting on the XZ plane. The gun TG handle the cone
   rotation.
*/

import javax.media.j3d.*;
import com.sun.j3d.utils.geometry.*;
import javax.vecmath.*;
import com.sun.j3d.utils.image.TextureLoader;


public class GunTurret
{
  private static final Vector3d ORIGIN = new Vector3d(0, 0, 0);

  private BranchGroup gunBG;
  private TransformGroup gunTG;
  private Vector3d startVec;

  // for repeated calculations
  private Transform3D gunT3d = new Transform3D();
  private Vector3d currTrans = new Vector3d();
  private Transform3D rotT3d = new Transform3D();


  public GunTurret(Vector3d svec)
  {
    startVec = svec;
    gunBG = new BranchGroup();
    Appearance apStone = stoneApp();
    placeGunBase(apStone);
    placeGun(apStone);
  } // end of GunTurret()


  private Appearance stoneApp()
  // stone appearance using a texture
  {
    Material stoneMat = new Material();  // white by default
    stoneMat.setLightingEnable(true);

    Appearance apStone = new Appearance();
    apStone.setMaterial(stoneMat);

    TextureLoader stoneTex = new TextureLoader("images/stone.jpg", null);
    if (stoneTex != null) 
      apStone.setTexture(stoneTex.getTexture());

    // combine the texture with material and lighting
    TextureAttributes texAttr = new TextureAttributes();
    texAttr.setTextureMode(TextureAttributes.MODULATE);
    apStone.setTextureAttributes(texAttr);

    return apStone;
  }  // end of stoneApp()



  private void placeGunBase(Appearance apStone)
  // a cylinder resting on the XZ plane, height 2 units
  {
    Transform3D baseT3d = new Transform3D();
    baseT3d.set( new Vector3d(0,1,0));   // so resting on XZ plane
    TransformGroup baseTG = new TransformGroup();
    baseTG.setTransform(baseT3d);
    Cylinder cyl = new Cylinder(0.25f, 2.0f, Cylinder.GENERATE_NORMALS |
                              Cylinder.GENERATE_TEXTURE_COORDS, apStone);
    cyl.setPickable(false);  // gun base is unpickable
    baseTG.addChild( cyl );

    gunBG.addChild(baseTG);
  } // end of placeGunBase()



  private void placeGun(Appearance apStone)
  // a rotatable cone, whose center is 2 unit above XZ plane
  {
    gunTG = new TransformGroup();
    gunTG.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);  // can rotate
    gunTG.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

    gunT3d.set(startVec);   // centered at the top of the cylinder   
    gunTG.setTransform(gunT3d);
    Cone cone = new Cone(1.0f, 2.0f, Cone.GENERATE_NORMALS |
                              Cone.GENERATE_TEXTURE_COORDS, apStone);
    cone.setPickable(false);   // gun cone is unpickable
    gunTG.addChild(cone);

    gunBG.addChild(gunTG);
  }  // end of placeGun()


  public BranchGroup getGunBG()
  {  return gunBG;  }



  public void makeRotation(AxisAngle4d rotAxis)
  // rotate the cone of the gun turret
  {
    gunTG.getTransform( gunT3d );         // get current transform
    // System.out.println("Start gunT3d: " + gunT3d);
    gunT3d.get( currTrans );              // get current translation
	gunT3d.setTranslation( ORIGIN );      // translate to origin

	rotT3d.setRotation( rotAxis );         // apply rotation
    gunT3d.mul(rotT3d);

	gunT3d.setTranslation( currTrans );   // translate back
    gunTG.setTransform( gunT3d );
    // System.out.println("End gunT3d: " + gunT3d);
  } // end of makeRotation()


} // end of GunTurret class
