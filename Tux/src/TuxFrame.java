/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author lucita
 */
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.awt.*;
import javax.media.j3d.*;
import javax.swing.JFrame;
import javax.vecmath.*;

public class TuxFrame extends JFrame {
    Colision Obj_Colisionando;
    TransformGroup personaje;
    public TuxFrame() {
// UNIVERSO Y CANVAS
        setLayout(new BorderLayout());
        GraphicsConfiguration config =
                SimpleUniverse.getPreferredConfiguration();
        setSize(400, 400);

        Canvas3D canvas3D = new Canvas3D(config);
        add("Center", canvas3D);
        BranchGroup scene = new BranchGroup();
        SimpleUniverse simpleU = new SimpleUniverse(canvas3D);

        simpleU.getViewingPlatform().setNominalViewingTransform();
        scene.addChild(TuxEscenario(simpleU));
        simpleU.addBranchGraph(scene);
        this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);


        this.setVisible(true);
    }
    /*El fondo*/

    TransformGroup createLand() {

    //LINEAS VERDES
        LineArray landGeom = new LineArray(44, GeometryArray.COORDINATES | GeometryArray.COLOR_3);
        float l = -50.0f;
        for (int c = 0; c < 44d; c += 4d) {
            landGeom.setCoordinate(c + 0, new Point3f(-50.0f, 0.0f, l));
            landGeom.setCoordinate(c + 1, new Point3f(50.0f, 0.0f, l));
            landGeom.setCoordinate(c + 2, new Point3f(l, 0.0f, -50.0f));
            landGeom.setCoordinate(c + 3, new Point3f(l, 0.0f, 50.0f));
            l += 10.0f;
        }
        Color3f c = new Color3f(0.1f, 0.8f, 0.1f);
        for (int i = 0; i < 44; i++) {
            landGeom.setColor(i, c);
        }
        TransformGroup tr1=new TransformGroup();
        tr1.addChild(new Shape3D(landGeom));
        tr1.setUserData("Lineas verdes");
        return tr1;
    }
    /*El personaje*/

    public TransformGroup TuxPersonaje() {
        Transform3D tr = new Transform3D();
        tr.rotY(Math.PI / 4);
        TransformGroup tuxtr = new TransformGroup(tr);
        tuxtr.addChild(new ColorCube(.3));
        return tuxtr;
    }
    /*El Escenario*/

    public TransformGroup TuxEscenario(SimpleUniverse simpleU) {
//CONTENEDOR
        TransformGroup Tuxcontent = new TransformGroup();
        TransformGroup Tuxobj = new TransformGroup();
        Tuxcontent.addChild(Tuxobj);
        Tuxcontent.addChild(this.createLand());

//AGREGAR PERSONAJES
        TransformGroup Tuxtrans = new TransformGroup();
        personaje=TuxPersonaje();
        Tuxtrans.addChild(personaje);
        Tuxcontent.addChild(Tuxtrans);

        TuxBehaior TuxBe = new TuxBehaior(simpleU, Tuxtrans);
        Tuxcontent.addChild(TuxBe);

//Agregar Colisiones
        Obj_Colisionando=new Colision(personaje);
        Obj_Colisionando.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        Tuxcontent.addChild(this.Obj_Colisionando);

//Agregar Cubitos
        Tuxcontent.addChild(Obj_Colisionando.AgregarCubitos(new Vector3f(0f, 0.45f, 4f),"lucita287_1"));
        Tuxcontent.addChild(Obj_Colisionando.AgregarCubitos(new Vector3f(0f, 0.45f, -4f),"lucita287_2"));
        Tuxcontent.addChild(Obj_Colisionando.AgregarCubitos(new Vector3f(4f, 0.45f, 0f),"lucita287_3"));
        Tuxcontent.addChild(Obj_Colisionando.AgregarCubitos(new Vector3f(-4f, 0.45f, 0f),"lucita287_4"));




//ILUMINACION
        BoundingSphere bounds = new BoundingSphere();
        DirectionalLight lightD = new DirectionalLight();
        lightD.setDirection(new Vector3f(0.0f, -0.7f, -0.7f));
        lightD.setInfluencingBounds(bounds);
        Tuxcontent.addChild(lightD);
        AmbientLight lightA = new AmbientLight();
        lightA.setInfluencingBounds(bounds);
        Tuxcontent.addChild(lightA);

//FONDO BLANCO
        Background background = new Background(FondoImagen("Dragon.jpeg"));
        //background.setColor(1.0f, 1.0f, 1.0f);
        background.setApplicationBounds(bounds);
        Tuxcontent.addChild(background);

        return Tuxcontent;

    }

    public static void main(String[] args) {
        new TuxFrame();
    }

     public ImageComponent2D FondoImagen(String path) {
         java.net.URL path2=getClass().getResource(path);
         ImageComponent2D buffer=new TextureLoader(path2, new String("RGB"),
                TextureLoader.BY_REFERENCE | TextureLoader.Y_UP,
                new Container()).getScaledImage( this.getWidth(),this.getHeight());
         
        return buffer;
    }
}
