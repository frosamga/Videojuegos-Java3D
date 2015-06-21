
// ImagesSeries.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* Shows a series of GIFs, which are displayed on a transparent 
   QuadArray located on the XZ plane centered at (0,0).
*/

import javax.vecmath.*;
import javax.media.j3d.*;
import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.image.*;


public class ImagesSeries extends Shape3D
{
  private static final int DELAY = 100;   // ms delay between frames
  private static final int NUM_VERTS = 4;

  private ImageComponent2D ims[];
  private Texture2D texture;


  public ImagesSeries(float screenSize, String fnm, int num) 
  {
    loadImages(fnm, num);
    createGeometry(screenSize);
    createAppearance();
  } // end of ImagesSeries()


  private void loadImages(String fnm, int num)
  {
    String filename;
    TextureLoader loader;

    ims = new ImageComponent2D[num];

    System.out.println("Loading " + num + " GIFS called " + fnm);
    for (int i=0; i < num; i++) {
      filename = new String(fnm + i + ".gif");
      loader = new TextureLoader(filename, null);
      ims[i] = loader.getImage();
      if(ims[i] == null)
        System.out.println("Image Loading Failed for " + filename);
    }
  }  // end of loadImages()



  private void createGeometry(float screenSize)
  {
    QuadArray plane = new QuadArray(NUM_VERTS, 
					GeometryArray.COORDINATES |
					GeometryArray.TEXTURE_COORDINATE_2 );

    // the screen is resting on the XZ plane, centered at (0,0)
    // of size screenSize
    Point3f p1 = new Point3f(-screenSize/2, 0.0f, 0.0f);
    Point3f p2 = new Point3f(screenSize/2, 0.0f, 0.0f);
    Point3f p3 = new Point3f(screenSize/2, screenSize, 0.0f);
    Point3f p4 = new Point3f(-screenSize/2, screenSize, 0.0f);

    // anti-clockwise from bottom left
    plane.setCoordinate(0, p1);
    plane.setCoordinate(1, p2);
    plane.setCoordinate(2, p3);
    plane.setCoordinate(3, p4);

    TexCoord2f q = new TexCoord2f();
    q.set(0.0f, 0.0f);    
    plane.setTextureCoordinate(0, 0, q);
    q.set(1.0f, 0.0f);   
    plane.setTextureCoordinate(0, 1, q);
    q.set(1.0f, 1.0f);    
    plane.setTextureCoordinate(0, 2, q);
    q.set(0.0f, 1.0f);   
    plane.setTextureCoordinate(0, 3, q);  

    setGeometry(plane);
  }  // end of createGeometry()


  private void createAppearance()
  {                       
    Appearance app = new Appearance();

    // blended transparency so texture can be irregular
    TransparencyAttributes tra = new TransparencyAttributes();
    tra.setTransparencyMode( TransparencyAttributes.BLENDED );
    app.setTransparencyAttributes( tra );
/*
    // mix the texture and the material colour
    TextureAttributes ta = new TextureAttributes();
    ta.setTextureMode(TextureAttributes.MODULATE);
    app.setTextureAttributes(ta);

    Material mat = new Material();    // set material and lighting
    mat.setLightingEnable(true);
    app.setMaterial(mat);
*/
    // Create a two dimensional texture
    // Set the texture from the first loaded image
    texture = new Texture2D(Texture2D.BASE_LEVEL, Texture.RGBA,
                       ims[0].getWidth(), ims[0].getHeight());
    texture.setImage(0, ims[0]);
    texture.setCapability(Texture.ALLOW_IMAGE_WRITE);   // texture can change
    app.setTexture(texture);
   
    setAppearance(app);
  }  // end of createAppearance()


  public void showSeries()
  {
    for (int i=0; i < ims.length; i++) {
      texture.setImage(0, ims[i]);
      try {
        Thread.sleep(DELAY);      // wait a while
      } 
      catch (Exception ex) {}
    }
  } // end of showSeries()


} // end of ImagesSeries class