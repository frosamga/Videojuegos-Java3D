
// Landscape.java
// Andrew Davison, April 2005, ad@fivedots.coe.psu.ac.th

/* This class uses the FractalMesh class to generate the points
   of the landscape mesh. 

   Each mesh (four points) from FractalMesh is divided into
   separate lists depending on its average height.

   Meshs (quads) within the same height range are used
   to create a TexturedPlanes object which has an associated
   texture.

   Four walls are created around the floor using the ColouredPlane
   class.

   getLandHeight() is used by KeyBehavior to get the land height at
   a given (x,z) location -- it uses picking.
*/

import java.util.*;

import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.picking.*;


public class Landscape
{
  private static final int WORLD_LEN = 64;   //side length of floor

  private static final int NUM_TEXTURES = 5;
  private static final String textureFns[] =
					{"water-shallow.jpg", "sand.jpg", "grass.gif", 
					 "dryEarth.jpg", "stone.gif"};

  private final static double MIN_HEIGHT = -2.0;
  private final static double MAX_HEIGHT = 8.0;

  /* The number of textures are used to calculate the height boundaries 
     between MIN_HEIGHT and MAX_HEIGHT. The first named texture in textureFns[]
     is used for quads in the first height range (the lowest), the second
     texture for the next height range, and so on.
  */

  private final static Vector3d DOWN_VEC = new Vector3d(0.0,-1.0, 0.0);
		// direction for picking -- down below the viewpoint


  private BranchGroup landBG, floorBG;
  private Point3d vertices[];				// landscape coordinates
  private double textureBoundaries[];   
     // heights that mark the boundaries for which mesh gets which texture

  private Vector3d originVec = new Vector3d();   // stating point for viewpoint
  private boolean foundOrigin = false;
  private PickTool picker;


  public Landscape(double flatness)
  {
    landBG = new BranchGroup();
    floorBG = new BranchGroup();
    landBG.addChild(floorBG);  // so landBG-->floorBG

    setTexBoundaries();

    picker = new PickTool(floorBG);   // only check the floor
    picker.setMode(PickTool.GEOMETRY_INTERSECT_INFO);

    FractalMesh fm = new FractalMesh(flatness);
    // fm.printMesh(1);   // for debugging: x=0; y=1; z=2
    vertices = fm.getVertices();    // get the vertices generated by FractalMesh

    platifyFloor();
    addWalls();
  } // end of Landscape()


  private void setTexBoundaries()
  // Store the height boundaries for the textures
  {
    textureBoundaries = new double[NUM_TEXTURES];
    double boundStep = (MAX_HEIGHT - MIN_HEIGHT) / NUM_TEXTURES;
    double boundary = MIN_HEIGHT + boundStep;
    for(int j=0; j < NUM_TEXTURES; j++) {
      textureBoundaries[j] = boundary;     // place in increasing order
      boundary += boundStep;
    }
  } // end of setTexBoundaries()



  private void platifyFloor()
  /* Examine the quads stored in vertices[]. Check the
     average height and assign it to the ArrayList for
     coords in that height range.

     Pass each ArrayList and their texture filenames to
     a TexturedPlanes object to build the 3D mesh for
     those coords.
  */
  {
    ArrayList[] coordsList = new ArrayList[NUM_TEXTURES];
    for (int i=0; i < NUM_TEXTURES; i++)
      coordsList[i] = new ArrayList();

    int heightIdx;
    for (int j=0; j < vertices.length; j=j+4) {   // test each quad
      heightIdx = findHeightIdx(j);   // which height index applies to the quad
      addCoords( coordsList[heightIdx], j);   // add quad to the list for that height
      checkForOrigin(j);       // check if (0,0) is a point in the quad
    }

    // use each coordsList and texture to make a TexturedPlanes object
    for (int i=0; i < NUM_TEXTURES; i++)
      if (coordsList[i].size() > 0)    // if used
        floorBG.addChild( new TexturedPlanes(coordsList[i],  // then add to the floor
						"images/"+textureFns[i]) );
  } // end of platifyFloor()



  private int findHeightIdx(int vertIndex)
  /* Find the height index for the quad starting at vertices[vertIndex].
     Get the average height for the 4 points in the quad.
     If it is less than the boundary value, then the quad belongs 
     to that height range. */
  {
    double ah = avgHeight(vertIndex);
    for(int i=0; i < textureBoundaries.length; i++)
      if (ah < textureBoundaries[i])
        return i;
    return NUM_TEXTURES-1;   // last ArrayList is default
  } // end of findHeightIdx()


  private double avgHeight(int vi)
  // Calculate the average height for the 4 points in the quad.
  { 
    return (vertices[vi].y + vertices[vi+1].y +
			vertices[vi+2].y + vertices[vi+3].y)/4.0;
  }


  private void addCoords(ArrayList coords, int vi)
  // add the 4 coords (the quad) beginning at vertices[vi] to the ArrayList
  {
    coords.add( vertices[vi] ); coords.add( vertices[vi+1] ); 
    coords.add( vertices[vi+2] ); coords.add( vertices[vi+3] );
  }  // end of addCoords()


  private void checkForOrigin(int vi)
  // If vertices[vi] is at the origin, store its position in originVec
  {
    if (!foundOrigin) {
       if ((vertices[vi].x == 0.0) && (vertices[vi].z == 0.0)) {
         // System.out.println("Found Origin: (" + vertices[vi].x + ", " + 
		 //			vertices[vi].y + ", " + vertices[vi].z + ")");
         originVec.y = vertices[vi].y;
         foundOrigin = true;
       }
    }
  }  // end of checkForOrigin()



  private void addWalls()
  // Add 4 walls around the landscape
  {
    Color3f eveningBlue = new Color3f(0.17f, 0.07f, 0.45f);  // wall colour

    // the eight corner points
    // back, left
    Point3d p1 = new Point3d(-WORLD_LEN/2.0f, MIN_HEIGHT, -WORLD_LEN/2.0f);
    Point3d p2 = new Point3d(-WORLD_LEN/2.0f, MAX_HEIGHT, -WORLD_LEN/2.0f);

    // front, left
    Point3d p3 = new Point3d(-WORLD_LEN/2.0f, MIN_HEIGHT, WORLD_LEN/2.0f);
    Point3d p4 = new Point3d(-WORLD_LEN/2.0f, MAX_HEIGHT, WORLD_LEN/2.0f);

    // front, right
    Point3d p5 = new Point3d(WORLD_LEN/2.0f, MIN_HEIGHT, WORLD_LEN/2.0f);
    Point3d p6 = new Point3d(WORLD_LEN/2.0f, MAX_HEIGHT, WORLD_LEN/2.0f);

    // back, right
    Point3d p7 = new Point3d(WORLD_LEN/2.0f, MIN_HEIGHT, -WORLD_LEN/2.0f);
    Point3d p8 = new Point3d(WORLD_LEN/2.0f, MAX_HEIGHT, -WORLD_LEN/2.0f);

    // left wall; counter-clockwise
    landBG.addChild( new ColouredPlane(p3, p1, p2, p4, 
							new Vector3f(-1,0,0), eveningBlue) );
    // front wall; counter-clockwise from back
    landBG.addChild( new ColouredPlane(p5, p3, p4, p6,
							new Vector3f(0,0,-1), eveningBlue) );
    // right wall
    landBG.addChild( new ColouredPlane(p7, p5, p6, p8, 
							new Vector3f(-1,0,0), eveningBlue) );
    // back wall
    landBG.addChild( new ColouredPlane(p7, p8, p2, p1, 
							new Vector3f(0,0,1), eveningBlue) );
  } // end of addWalls()



  // ------------- public methods ------------------


  public BranchGroup getLandBG()
  {  return landBG;  }


  public boolean inLandscape(double xPosn, double zPosn)
  // is (xPosn,zPosn) on the floo?
  {
    int x = (int) Math.round(xPosn);   // to deal with dp errors
    int z = (int) Math.round(zPosn);

    if ((x <= -WORLD_LEN/2) || (x >= WORLD_LEN/2) ||
        (z <= -WORLD_LEN/2) || (z >= WORLD_LEN/2))
      return false;
    return true;
  }  // end of inLandscape()


  public Vector3d getOriginVec()
  {  return originVec;  }


  public double getLandHeight(double x, double z, double currHeight)
  /* Throw a pick ray downwards below the (x,z) point to intersect
     with the floor. Extract the y-value (the height of the floor
     and return it.

     Picking is a bit flakey, especially near quad edges and corners,
     so if no PickResult is found, no intersections found, or the
     extraction of intersection coords fails, then the
     height for the last viewpoint position is returned.
  */
  {
    Point3d pickStart = new Point3d(x, MAX_HEIGHT*2, z);    // start from high up
    picker.setShapeRay(pickStart, DOWN_VEC);   // shoot a ray downwards

    PickResult picked = picker.pickClosest();
	if (picked != null) {    // pick sometimes misses at an edge/corner
      if (picked.numIntersections() != 0) {    // sometimes no intersects are found
        PickIntersection pi = picked.getIntersection(0);
        Point3d nextPt;
        try {   // handles 'Interp point outside quad' error
          nextPt = pi.getPointCoordinates();
        }
        catch (Exception e) {
           // System.out.println(e);
           return currHeight;
        }
        return nextPt.y;
      }
    }
    return currHeight;    // error if we reach here; return existing height
  }  // end of getLandHeight()


} // end of Landscape class

