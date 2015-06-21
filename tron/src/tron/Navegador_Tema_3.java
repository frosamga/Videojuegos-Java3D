
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.util.ArrayList;
import javax.media.j3d.*;
import javax.swing.JFrame;
import javax.vecmath.*;
import com.sun.j3d.utils.picking.*;
import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

public class Navegador_Tema_3 extends JFrame implements Runnable {

    SimpleUniverse universo;
    Figura moto1, moto2;
    ArrayList<Figura> listaObjetos = new ArrayList<Figura>();
    public PickTool explorador;
    public String rutaCarpetaProyecto = System.getProperty("user.dir") + "/";
    boolean terminado = false;
    BranchGroup escena;
    String ganador;
    int nJugadores;

    public Navegador_Tema_3(int nJugadores_) {

        this.nJugadores = nJugadores_;

        Canvas3D zonaDibujo = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        universo = new SimpleUniverse(zonaDibujo);
        universo.getViewingPlatform().setNominalViewingTransform();
        getContentPane().add(zonaDibujo);
        escena = crearEscena();

        //MANEJAR LA CAMARA CON EL RATON
        OrbitBehavior B = new OrbitBehavior(zonaDibujo);
        B.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        universo.getViewingPlatform().setViewPlatformBehavior(B);


        escena.compile();
        universo.addBranchGraph(escena);

    }

    BranchGroup crearEscena() {
        //Se crea el objRoot y la rama para el conjunto de objetos localizables
        BranchGroup objRoot = new BranchGroup();
        BranchGroup conjunto = new BranchGroup();
        objRoot.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        conjunto.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        explorador = new PickTool(conjunto);
        explorador.setMode(PickTool.GEOMETRY_INTERSECT_INFO);



        //FONDO

        crearFondo(objRoot, "city.jpg");


        //MOTOS
        moto1 = new Figura(0.4f, conjunto, listaObjetos, this, "moto1");
        moto1.inicializar(0f, 0f, 6f);
        moto1.mostrar();

        moto2 = new Figura(0.4f, conjunto, listaObjetos, this, "moto2");
        moto2.inicializar(0f, 0f, -6f);
        moto2.mostrar();


        //SUELO
        Figura suelo = new Figura(13f, conjunto, listaObjetos, this, "suelo");
        suelo.inicializar(0, -1f, 0);

        //PAREDES

        Figura paredN = new Figura(13f, conjunto, listaObjetos, this, "paredN");
        paredN.inicializar(0f, 0f, 13f);
        paredN.mostrar();

        Figura paredS = new Figura(13f, conjunto, listaObjetos, this, "paredS");
        paredS.inicializar(0, 0f, -13f);
        paredS.mostrar();

        Figura paredE = new Figura(13f, conjunto, listaObjetos, this, "paredE");
        paredE.inicializar(13f, 0f, 0);
        paredE.mostrar();

        Figura paredO = new Figura(13f, conjunto, listaObjetos, this, "paredO");
        paredO.inicializar(-13f, 0f, 0);
        paredO.mostrar();

        //Se crean los behavior para control de teclado y para mostrar cada "frame"
        DeteccionControlPersonaje mueve1 = new DeteccionControlPersonaje(listaObjetos.get(0), 1);

        //Si estamos en modo un jugador, no asignamos control a la segunda moto
        if (nJugadores == 2) {
            DeteccionControlPersonaje mueve2 = new DeteccionControlPersonaje(listaObjetos.get(1), 2);
            mueve2.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
            objRoot.addChild(mueve2);
        }

        //Creamos el detector de colisiones para las dos motos
        DeteccionColisionesPersonaje colision1 = new DeteccionColisionesPersonaje(moto1);
        DeteccionColisionesPersonaje colision2 = new DeteccionColisionesPersonaje(moto2);

        ComportamientoMostrar mostrar = new ComportamientoMostrar(this);
        mueve1.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));

        mostrar.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));

        //Añadiendo luz Direccional
        DirectionalLight luz = new DirectionalLight(new Color3f(1.0f, 1.0f, 1.0f), new Vector3f(-1.0f, -1.0f, -1.0f));
        luz.setInfluencingBounds(new BoundingSphere(new Point3d(0.0d, 0.0d, 0.0d), 100.0d));
        objRoot.addChild(luz);



        //Añadimos escalado a toda la escena
        BranchGroup escalado = new BranchGroup();
        Transform3D scala = new Transform3D();
        scala.setScale(0.5f);
        //cala.rotY(-Math.PI/4f);
        TransformGroup TGscala = new TransformGroup(scala);
        escalado.addChild(TGscala);
        TGscala.addChild(conjunto);

        //Estructura    
        objRoot.addChild(escalado);
        objRoot.addChild(mueve1);

        objRoot.addChild(colision1);
        objRoot.addChild(colision2);
        objRoot.addChild(mostrar);
        anadirSonido(objRoot, rutaCarpetaProyecto + "musicafondo.mid",true);

        return objRoot;
    }

    void actualizar(float dt) {
        for (int i = 0; i < this.listaObjetos.size(); i++) {
            listaObjetos.get(i).actualizar(dt);
        }
    }

    void mostrar() {
        for (int i = 0; i < this.listaObjetos.size(); i++) {
            listaObjetos.get(i).mostrar();
            //listaObjetos.get(0).colision(listaObjetos.get(i));
        }
    }

    public void run() {
        float dt = 0.01f;
        while (terminado == false) {
            actualizar(dt);
            //Mostrar()   se hace implicitamente con el Behavior cada vez que el ordenador puede 
            try {
                Thread.sleep((int) (dt * 1000));
            } catch (Exception e) {
            }
        }

        crearTextoFinal();

    }

    public void crearTextoFinal() {

        BranchGroup texto = new BranchGroup();


        Font3D font3d = new Font3D(new Font("Comic Sans", Font.PLAIN, 2), new FontExtrusion());
        Text3D textGeom = new Text3D(font3d, new String("Gana el " + ganador), new Point3f(0f, 5.0f, 0.0f));
        Shape3D textShape = new Shape3D(textGeom);

        Transform3D t3dTexto = new Transform3D();
        t3dTexto.rotY(Math.PI / 4 + Math.PI);

        Transform3D t3dTexto2 = new Transform3D();
        t3dTexto2.set(new Vector3f(4, -3, -8));
        TransformGroup TGtexto = new TransformGroup(t3dTexto2);
        TransformGroup TGtexto2 = new TransformGroup(t3dTexto);

        texto.addChild(TGtexto);
        TGtexto.addChild(TGtexto2);
        TGtexto2.addChild(textShape);
        escena.addChild(texto);
    }

    void colocarCamara(SimpleUniverse universo, Point3d posiciónCamara, Point3d objetivoCamara) {
        Point3d posicionCamara = new Point3d(posiciónCamara.x + 0.001, posiciónCamara.y + 0.001d, posiciónCamara.z + 0.001);
        Transform3D datosConfiguracionCamara = new Transform3D();
        datosConfiguracionCamara.lookAt(posicionCamara, objetivoCamara, new Vector3d(0.001, 1.001, 0.001));
        try {
            datosConfiguracionCamara.invert();
            TransformGroup TGcamara = universo.getViewingPlatform().getViewPlatformTransform();
            TGcamara.setTransform(datosConfiguracionCamara);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    void crearFondo(BranchGroup raiz, String textura) {
        TextureLoader bgTexture = new TextureLoader(rutaCarpetaProyecto + textura, this);
        Background bg = new Background(bgTexture.getImage());
        BoundingSphere limites = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        bg.setApplicationBounds(limites);
        BranchGroup backGeoBranch = new BranchGroup();
        bg.setGeometry(backGeoBranch);
        raiz.addChild(bg);
    }

    protected void anadirSonido(BranchGroup b, String sonido,boolean repetir) {
        
//Create a media container to load the file
        MediaContainer droneContainer = new MediaContainer("file:"+sonido.replace("\\","/"));
//Create the background sound from the media container
        BackgroundSound drone = new BackgroundSound(droneContainer,
                1.0f);
//Activate the sound
        drone.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        drone.setEnable(true);
//Set the sound to loop forever
        if(repetir)
        {
        drone.setLoop(BackgroundSound.INFINITE_LOOPS);
        }
        
        b.addChild(drone);
    }

    public static void main(String args[]) {

        int nJugadores = 2;
        Navegador_Tema_3 x = new Navegador_Tema_3(nJugadores);

        x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        x.setSize(800, 800);
        x.setVisible(true);
        x.colocarCamara(x.universo, new Point3d(-10, 20, -10), new Point3d(0, -1f, 0));
        x.run();
    }
}