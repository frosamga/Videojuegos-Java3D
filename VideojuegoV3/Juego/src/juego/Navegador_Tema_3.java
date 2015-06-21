package juego;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.Text2D;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.util.ArrayList;
import javax.media.j3d.*;
import javax.swing.JFrame;
import javax.vecmath.*;
import com.sun.j3d.utils.picking.*;
import java.awt.Font;
import java.util.*;

public class Navegador_Tema_3 extends JFrame implements Runnable {
    
    SimpleUniverse universo;
    Figura personaje;
    ArrayList<Figura> listaObjetos = new ArrayList<Figura>();
    public PickTool explorador, e1;
    public String rutaCarpetaProyecto = System.getProperty("user.dir") + "/";
    private TransformGroup view;
    private Terreno land;
    public SeleccionadorObjetos3D disparo;
    Canvas3D zonaDibujo;
    int contador = 0;
    public BranchGroup objRoot, textoGanarBG, textoPerderBG, objetosBG;
    private int enemigosMuertos;
    private int tiempo = 0;
    
    public Navegador_Tema_3() {
        zonaDibujo = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        universo = new SimpleUniverse(zonaDibujo);
        universo.getViewingPlatform().setNominalViewingTransform();
        getContentPane().add(zonaDibujo);
        BranchGroup escena = crearEscena();
        
        escena.setCapability(BranchGroup.ALLOW_DETACH);
        escena.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        escena.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        escena.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        escena.setCapability(BranchGroup.ALLOW_BOUNDS_READ);
        escena.setCapability(BranchGroup.ALLOW_BOUNDS_WRITE);
        escena.setCapability(Group.ALLOW_BOUNDS_WRITE);
        escena.setCapability(Group.ALLOW_BOUNDS_READ);
        escena.setCapability(Group.ALLOW_CHILDREN_EXTEND);
        
        escena.compile();
        
        universo.getViewingPlatform().setNominalViewingTransform();
        universo.addBranchGraph(escena);
        
        
    }
    
    BranchGroup crearEscena() {
        //Se crea el objRoot y la rama para el conjunto de objetos localizables
        objRoot = new BranchGroup();
        textoGanarBG = new BranchGroup();
        objetosBG = new BranchGroup();
        textoPerderBG = new BranchGroup();
        BranchGroup conjunto = new BranchGroup();
        explorador = new PickTool(conjunto);
        explorador.setMode(PickTool.GEOMETRY_INTERSECT_INFO);

        //añadir las luces
        Vector3f lightDir = new Vector3f(1.0f, -1.0f, -0.8f);
        DirectionalLight luz = new DirectionalLight(new Color3f(1.0f, 1.0f, 1.0f), lightDir);
        luz.setInfluencingBounds(new BoundingSphere(new Point3d(0.0d, 0.0d, 0.0d), 100.0d));
        objRoot.addChild(luz);
        
        Vector3f lightDir1 = new Vector3f(-1.0f, 1.0f, 0.8f);
        DirectionalLight luz1 = new DirectionalLight(new Color3f(1.0f, 1.0f, 1.0f), lightDir);
        luz1.setInfluencingBounds(new BoundingSphere(new Point3d(0.0d, 0.0d, 0.0d), 100.0d));
        objRoot.addChild(luz1);

        //cielo      
        TextureLoader bgTexture = new TextureLoader("cielo.jpg", this);
        Background back = new Background(bgTexture.getImage());
        BoundingSphere limites = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        back.setApplicationBounds(limites);
        BranchGroup backGeoBranch = new BranchGroup();
        back.setGeometry(backGeoBranch);
        objRoot.addChild(back);

        //puntero
        disparo = new SeleccionadorObjetos3D(zonaDibujo, objRoot);
        objRoot.addChild(disparo);

        //en malla se puede definir la altura del terreno, con las constantes
        //el parametro que se le pasa es la espesura del terreno
        land = new Terreno(2.3);
        
        e1 = new PickTool(land.dameTerrenoBG());
        e1.setMode(PickTool.GEOMETRY_INTERSECT_INFO);


        //el 0.5 es para que la figura quede bien en el suelo, hay que decirle el radio
        personaje = new Figura("Dire_Cat.mdl", 1.2f, conjunto, listaObjetos, this, true);
        personaje.inicializar(0, -1f, 0f);
        
        Figura suelo = new Figura(20f, 0f, conjunto, listaObjetos, this);
        suelo.inicializar(0, 1f, 0);
        
        Figura casa = new Figura("syt00.obj", 0.4f, conjunto, listaObjetos, this);
        casa.inicializar(10, 3f, 7f);
        
        
        Random alea = new Random();
        Figura enemigo = new Figura("elephav.obj", 0.4f, objetosBG, listaObjetos, this);
        enemigo.inicializar(alea.nextInt(10), 3f, alea.nextInt(10) - 10);
        enemigo.velocidades[0] = 0.3f;
        
        Figura enemigo1 = new Figura("elephav.obj", 0.4f, objetosBG, listaObjetos, this);
        enemigo1.inicializar(alea.nextInt(5), 3f, alea.nextInt(5));
        enemigo1.velocidades[2] = 0.4f;
        
        Figura enemigo2 = new Figura("elephav.obj", 0.4f, objetosBG, listaObjetos, this);
        enemigo2.inicializar(alea.nextInt(7) - 7, 3f, alea.nextInt(2));
        enemigo2.velocidades[0] = 0.1f;
        enemigo2.velocidades[2] = 0.1f;
        
        
        Transform3D posicion = new Transform3D();
        TransformGroup tg = new TransformGroup();
        Font3D font3d = new Font3D(new Font("Helvetica", Font.PLAIN, 3), 15, new FontExtrusion());
        Text3D textGeom = new Text3D(font3d, new String("Has Ganado"), new Point3f(-8.0f, 2.0f, 2.0f));
        Shape3D textoG = new Shape3D(textGeom);
        posicion.rotY(Math.PI / 4);
        tg.setTransform(posicion);
        tg.addChild(textoG);
        textoGanarBG.addChild(tg);
        
        Transform3D posicionP = new Transform3D();
        TransformGroup tgP = new TransformGroup();
        Font3D font3dP = new Font3D(new Font("Helvetica", Font.PLAIN, 3), 15, new FontExtrusion());
        Text3D textGeomP = new Text3D(font3dP, new String("Has Perdido"), new Point3f(-8.0f, 2.0f, 2.0f));
        Shape3D textoP = new Shape3D(textGeomP);
        posicionP.rotY(Math.PI / 4);
        tgP.setTransform(posicionP);
        tgP.addChild(textoP);
        textoPerderBG.addChild(tgP);
        
        //Figura pj2 = new Figura("Fire_Elemental.mdl", 1.2f, conjunto, listaObjetos, this, true);
        //pj2.inicializar(7f, 2f, -10f);
        //pj2.velocidades[2] = 0.3f;

        //Se crean los behavior para control de teclado y para mostrar cada "frame"
        DeteccionControlPersonaje mueve = new DeteccionControlPersonaje(listaObjetos.get(0));
        ComportamientoMostrar mostrar = new ComportamientoMostrar(this);
        mueve.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        mostrar.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));

        //Estructura
        conjunto.addChild(objetosBG);
        objRoot.addChild(land.dameTerrenoBG());
        objRoot.addChild(conjunto);
        objRoot.addChild(mueve);
        objRoot.addChild(mostrar);
        
        return objRoot;
    }
    
    public void ganar() {        
        objRoot.addChild(textoGanarBG);        
    }
    
    public void perder() {
        objRoot.addChild(textoPerderBG);        
    }
    
    void actualizar(float dt) {
        for (int i = 0; i < this.listaObjetos.size(); i++) {
            listaObjetos.get(i).actualizar(dt);
        }
    }
    
    void mostrar() {
        
        this.colocarCamara(universo, personaje.posPj, personaje.posSo, personaje.cambioPersona);
        //this.colocarCamara(universo, personaje.posPj, personaje.dir, personaje.cambioPersona);
        //System.out.println(personaje.posPj);
        List<Integer> listaMuertos = new ArrayList<Integer>();
        for (int i = 0; i < this.listaObjetos.size(); i++) {
            if (!listaMuertos.contains(i)) {
                listaObjetos.get(i).mostrar();
            }
        }
        for (int i = 3; i < this.listaObjetos.size(); i++) {
            if (listaObjetos.get(0).colisionEsferaConEsfera(listaObjetos.get(i)) && personaje.ataque) {
                //matarEnemigo();
                
                listaObjetos.remove(i);
                // objRoot.removeChild(i);
                listaMuertos.add(i);
                enemigosMuertos++;
            }
        }
    }
    
    public void run() {
        float dt = 0.01f;
        Calendar tiempo = Calendar.getInstance();
        int principio = tiempo.getTime().getMinutes();
        long tiempoInicio = System.currentTimeMillis();        
        
        while (enemigosMuertos < 3 || System.currentTimeMillis() - tiempoInicio < 15000 || personaje.perder) {
            actualizar(dt);
            mostrar();
            //se hace implicitamente con el Behavior cada vez que el ordenador puede 
            try {
                Thread.sleep((int) (dt * 1000));
            } catch (Exception e) {
            }
        }
        if (enemigosMuertos >= 3) {
            ganar();
        } else {
            perder();
        }
        
    }
    
    void colocarCamara(SimpleUniverse universo, Vector3d pos, Vector3d sonar, boolean cambio) {

        /*
         * camara en 3º persona, no funciona del todo bien float cercania = 7;
         * Point3d posicionCamara3 = new Point3d(pos.x - sonar.x cercania, pos.y
         * - pos.y * cercania, pos.z - sonar.z * cercania); Point3d
         * objetivoCamara3 = new Point3d(pos.x + sonar.x * cercania, pos.y +
         * pos.y * cercania, pos.z + sonar.z * cercania);
         */
        Point3d posicionCamara;
        Point3d objetivoCamara;
        
        
        if (!cambio) {
            if (personaje.cam1) {
                posicionCamara = new Point3d(15, 10, 15);
            } else {
                posicionCamara = new Point3d(-15, 10, -15);
            }
            objetivoCamara = new Point3d(0, 0, 0);
        } else {
            posicionCamara = new Point3d(pos.x, pos.y, pos.z);
            objetivoCamara = new Point3d(sonar.x, sonar.y, sonar.z);
        }
        contador++;
        //System.out.println("posicion : " + posicionCamara + " --------  Objetivo : " + objetivoCamara);
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
    
    public static void main(String args[]) {
        Navegador_Tema_3 x = new Navegador_Tema_3();
        x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        x.setSize(800, 600);
        x.setVisible(true);
        //x.colocarCamara(x.universo);
        x.run();
    }
}
