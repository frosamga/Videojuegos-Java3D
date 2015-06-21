
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.behaviors.mouse.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.event.*;
import java.util.Enumeration;

//   MouseNavigatorApp renders a single, interactively rotatable,
//   traslatable, and zoomable ColorCube object.
public class MouseNavigatorApp extends Applet {

    public BranchGroup createSceneGraph(SimpleUniverse su) {
        // Create the root of the branch graph
        BranchGroup objRoot = new BranchGroup();
        TransformGroup vpTrans =su.getViewingPlatform().getViewPlatformTransform();
        BoundingSphere mouseBounds = null;

        
        objRoot.addChild(new ColorCube(0.4));
       
        mouseBounds = new BoundingSphere(new Point3d(), 1000.0);

        MouseRotate myMouseRotate = new MouseRotate(MouseBehavior.INVERT_INPUT);
        myMouseRotate.setTransformGroup(vpTrans);
        myMouseRotate.setSchedulingBounds(mouseBounds);
        objRoot.addChild(myMouseRotate);

        MouseTranslate myMouseTranslate = new MouseTranslate(MouseBehavior.INVERT_INPUT);
        myMouseTranslate.setTransformGroup(vpTrans);
        myMouseTranslate.setSchedulingBounds(mouseBounds);
        objRoot.addChild(myMouseTranslate);

        MouseZoom myMouseZoom = new MouseZoom(MouseBehavior.INVERT_INPUT);
        myMouseZoom.setTransformGroup(vpTrans);
        myMouseZoom.setSchedulingBounds(mouseBounds);
        objRoot.addChild(myMouseZoom);

        // Let Java 3D perform optimizations on this scene graph.
        objRoot.compile();

        return objRoot;
    } // end of CreateSceneGraph method of MouseNavigatorApp

    // Create a simple scene and attach it to the virtual universe
    public MouseNavigatorApp() {
        setLayout(new BorderLayout());
        GraphicsConfiguration config =
                SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas3D = new Canvas3D(config);
        add("Center", canvas3D);

        // SimpleUniverse is a Convenience Utility class
        SimpleUniverse simpleU = new SimpleUniverse(canvas3D);

        BranchGroup scene = createSceneGraph(simpleU);

        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        simpleU.getViewingPlatform().setNominalViewingTransform();

        simpleU.addBranchGraph(scene);
    } // end of MouseNavigatorApp (constructor)
    //  The following allows this to be run as an application
    //  as well as an applet

    public static void main(String[] args) {
        System.out.print("MouseNavigatorApp.java \n- a demonstration of using the mouse ");
        System.out.println("behavior utility classes to provide navigational interaction in a Java 3D scene.");
        System.out.println("Hold the mouse button while moving the mouse to move.");
        System.out.println("     left mouse button      - rotate");
        System.out.println("     right mouse button     - translate");
        System.out.println("     Alt+left mouse button  - zoom");
        System.out.println("This is a simple example progam from The Java 3D API Tutorial.");
        System.out.println("The Java 3D Tutorial is available on the web at:");
        System.out.println("http://java.sun.com/products/java-media/3D/collateral");
        Frame frame = new MainFrame(new MouseNavigatorApp(), 256, 256);
    } // end of main (method of MouseNavigatorApp)
} // end of class MouseNavigatorApp