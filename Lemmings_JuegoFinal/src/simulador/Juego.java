package simulador;

import java.awt.*;
import javax.swing.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import java.util.ArrayList;
import com.sun.j3d.utils.universe.SimpleUniverse;
import com.bulletphysics.dynamics.*;
import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.dynamics.constraintsolver.HingeConstraint;
import com.bulletphysics.dynamics.constraintsolver.Point2PointConstraint;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SliderConstraint;
import com.bulletphysics.linearmath.Transform;
import com.sun.j3d.audioengines.javasound.JavaSoundMixer;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.pickfast.behaviors.PickTranslateBehavior;
import figuras.ObjetoMDL;
import figuras.Objeto;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.nwn.loader.AnimationBehavior;

public class Juego extends JFrame implements Runnable {

    int estadoJuego = 0;
    SimpleUniverse universo;
    BoundingSphere limites = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
    public String rutaCarpetaProyecto = System.getProperty("user.dir") + "/";
    Thread hebra = new Thread(this);
    ArrayList<simulador.Figura> listaObjetosFisicos = new ArrayList<Figura>();
    ArrayList<simulador.Figura> listaObjetosNoFisicos = new ArrayList<Figura>();
    DiscreteDynamicsWorld mundoFisico;
    BranchGroup conjunto = new BranchGroup(), textoGanarBG, objRoot, puzzleBranchGroup;
    public boolean actualizandoFisicas, mostrandoFisicas;
    // Pesonajes importantes del juego
    Figura personaje;  //golem;
    Figura soldado;
    Figura bala, esferaRebotante;
    ObjetoMDL lemming;
    ObjetoMDL hada;
    Objeto animalEsferico;
    public utilidades.SeleccionadorObjetos3D disparo;
    Canvas3D zonaDibujo;
    boolean crearLemming = true, crear = true, esferaUnica = true;
    boolean bolaReboteCreada = false;
    Figura puertaConPivote1, puertaRotatoria1, puertaRotatoria2, piezaPuzzle1, BolaDeRebote, Diana, cilindroSostieneBoton, camaraInicio, camaraGanador;
    HingeConstraint pivote1, x0, x0_0, pivoteDiana;

    public Juego() {
        //crea un espacio con fisica incluida, no el terreno 
        CollisionConfiguration configuracionColision = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(configuracionColision);
        Vector3f worldAabbMin = new Vector3f(-50000, -50000, -50000);
        Vector3f worldAabbMax = new Vector3f(50000, 50000, 50000);
        AxisSweep3 AnchoMapa = new AxisSweep3(worldAabbMin, worldAabbMax);
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        mundoFisico = new DiscreteDynamicsWorld(dispatcher, AnchoMapa, solver, configuracionColision);
        mundoFisico.setGravity(new Vector3f(0, -10, 0));

        //crear el universo y la pantalla
        Container GranPanel = getContentPane();
        zonaDibujo = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        zonaDibujo.setPreferredSize(new Dimension(800, 600));
        GranPanel.add(zonaDibujo, BorderLayout.CENTER);
        universo = new SimpleUniverse(zonaDibujo);
        BranchGroup escena = crearEscena();
        escena.compile();
        universo.getViewingPlatform().setNominalViewingTransform();
        universo.addBranchGraph(escena);


        //permite que el raton se mueva sobre la escena, es decir, desplazar, girar y zoom
        OrbitBehavior B = new OrbitBehavior(zonaDibujo);
        B.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
                100.0));
        universo.getViewingPlatform().setViewPlatformBehavior(B);


        //empieza esta misma funcion
        hebra.start();
    }

    BranchGroup crearEscena() {
        //objRoot->conjunto
        objRoot = new BranchGroup();
        conjunto = new BranchGroup();
        puzzleBranchGroup = new BranchGroup();
        objRoot.addChild(conjunto);
        conjunto.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        conjunto.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        ComportamientoMostrar mostrar = new ComportamientoMostrar(this);
        textoGanarBG = new BranchGroup();

        //añadir luz
        DirectionalLight LuzDireccional = new DirectionalLight(new Color3f(10f, 10f, 10f), new Vector3f(1f, 0f, -1f));
        BoundingSphere limitesLuz = new BoundingSphere(new Point3d(-15, 10, 15), 100.0);
        LuzDireccional.setInfluencingBounds(limitesLuz);

        //limites esta arriba, limite de vision
        mostrar.setSchedulingBounds(limites);
        Background bg = new Background();
        bg.setApplicationBounds(limites);
        //FIXME: reemplazar el color que tiene por una imagen de un cielo o algo asi, es el color del fondo
        bg.setColor(new Color3f(135f / 256, 206f / 256f, 250f / 256f));

        //CIELO
        TextureLoader backText = new TextureLoader("texturas//cielo3.jpg", this);
        Background back = new Background(backText.getImage());
        back.setApplicationBounds(limites);
        BranchGroup backGeoBranch = new BranchGroup();
        back.setGeometry(backGeoBranch);
        objRoot.addChild(back);

        //permite disparas las bolas que usamos
        disparo = new utilidades.SeleccionadorObjetos3D(zonaDibujo, objRoot);

        piezaPuzzle1 = new Objeto(0.5f, 0.5f, 0.5f, "texturas//tronco.jpg", puzzleBranchGroup, listaObjetosFisicos, this);
        piezaPuzzle1.crearPropiedades(0, 0.8f, 0.1f, 7f, 4f, 7f, mundoFisico);

        //MUSICA

//        try {
//            PhysicalEnvironment pe = universo.getViewer().getPhysicalEnvironment();
//            JavaSoundMixer objetoMezcladorSonidos = new JavaSoundMixer(pe);
//            pe.setAudioDevice(objetoMezcladorSonidos);
//            objetoMezcladorSonidos.initialize();
//            universo.getViewer().getView().setPhysicalEnvironment(pe);
//        } catch (Exception e) {
//            System.out.println("problema de audio");
//        }
//        System.out.println(System.getProperty("user.dir"));   
//        String ruta = System.getProperty("user.dir")+File.separator + "musica" + File.separator;
//        String ficheroSonido = ruta +"ost.mp3";
//        anadirSonidoARama(objRoot, "musica" + File.separator + "ost.wav");
//        
//        BranchGroup unaRamaSonido = new BranchGroup();
//        unaRamaSonido.setCapability(BranchGroup.ALLOW_DETACH);
//        unaRamaSonido.setUserData("ost");
//          
////        anadirSonidoARama (unaRamaSonido , new String(System.getProperty("user.dir") + "src/sonidos/tigre.wav"));
//           // String sound = "file://localhost/"+System.getProperty("user.dir")+"/src/sonidos/tigre.wav";*/
// 
//        objRoot.addChild(unaRamaSonido);

        ////////////////////////////////////////////////
        BranchGroup pickerRaton = new BranchGroup();
        TransformGroup objTranslate = null;
        PickTranslateBehavior pickTranslate = null;
        Transform3D transform = new Transform3D();
        BoundingSphere behaveBounds = new BoundingSphere();

        // create ColorCube and PickRotateBehavior objects
        transform.setTranslation(new Vector3f(-5f, 0f, 34f));
        objTranslate = new TransformGroup(transform);
        objTranslate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTranslate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objTranslate.setCapability(TransformGroup.ENABLE_PICK_REPORTING);

        pickerRaton.addChild(objTranslate);
        objTranslate.addChild(piezaPuzzle1.conjunto);

        pickTranslate = new PickTranslateBehavior(pickerRaton, zonaDibujo, behaveBounds);
        pickerRaton.addChild(pickTranslate);

        //texto Ganar
        Transform3D posicion = new Transform3D();
        TransformGroup tg = new TransformGroup();
        Font3D font3d = new Font3D(new Font("Helvetica", Font.PLAIN, 1), 15, new FontExtrusion());
        Text3D textGeom = new Text3D(font3d, new String("Lemmings3D"), new Point3f(-53.5f, 2f, -4f));
        Text3D textGeom1 = new Text3D(font3d, new String("Comenzar"), new Point3f(-53f, 1.3f, -4f));

        Shape3D textoG = new Shape3D(textGeom);
        Shape3D textoG1 = new Shape3D(textGeom1);

        //posicion.rotY(Math.PI / 4);
        tg.setTransform(posicion);
        tg.addChild(textoG);
        tg.addChild(textoG1);
        textoGanarBG.addChild(tg);

        //texto Ganar
        Transform3D posicionG = new Transform3D();
        TransformGroup tgG = new TransformGroup();
        Font3D font3dG = new Font3D(new Font("Helvetica", Font.PLAIN, 1), 15, new FontExtrusion());
        Text3D textGeomG = new Text3D(font3dG, new String("Ganador!"), new Point3f(-62f, 2f, 33f));

        Shape3D textoGanador = new Shape3D(textGeomG);

        //posicion.rotY(Math.PI / 4);       
        tgG.setTransform(posicionG);
        tgG.addChild(textoGanador);
        textoGanarBG.addChild(tgG);


        /*
         // añadir un segundo cubo
         transform.setTranslation(new Vector3f( 0.6f, 0.0f, -0.6f));
         objTranslate = new TransformGroup(transform);
         objTranslate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
         objTranslate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
         objTranslate.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
        
         pickerRaton.addChild(objTranslate);
         objTranslate.addChild(new ColorCube(0.4));
         */
        pickerRaton.compile();

        /*
         //desplazamiento con el raton
         TransformGroup objRotate = new TransformGroup();
         objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
         objRotate.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
         objRoot.addChild(objRotate);
         objRotate.addChild(plat.conjunto);
         MouseRotate myMouseRotate = new MouseRotate();
         myMouseRotate.setTransformGroup(objRotate);
         myMouseRotate.setSchedulingBounds(new BoundingSphere());
         objRoot.addChild(myMouseRotate);
         */

        objRoot.addChild(textoGanarBG);
        objRoot.addChild(disparo);
        objRoot.addChild(LuzDireccional);
        objRoot.addChild(bg);
        objRoot.addChild(mostrar);
        objRoot.addChild(pickerRaton);
        return objRoot;
    }

    void cargarContenido() {
        float radio;
        float masaConstruccion = 0.2f;
        //Capacidad de rebotar. 1 para grandes rebote   0 para simular gotas de liquidos espesos
        float elasticidad = 0.3f;
        //Perdidad de velodidad al desplazarse (friccion del aire): 0 para mantener velocidad. 0.99 para perder velocidad (liquidos espesos)
        float dumpingLineal = 0.9f;

        Figura pared1 = new Objeto(30f, 10f, 0.2f, "texturas//ladrillo2.jpg", conjunto, listaObjetosFisicos, this);
        pared1.crearPropiedades(0, elasticidad, dumpingLineal, 0, -2f, -14f, mundoFisico);
        /*
         Figura pared2 = new Objeto(20f, 10f, 0.2f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
         pared2.crearPropiedades(0, elasticidad, dumpingLineal, 0, -2f, 40f, mundoFisico);

         Figura pared3 = new Objeto(0.2f, 10f, 30f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
         pared3.crearPropiedades(0, elasticidad, dumpingLineal, -20, -2f, 25f, mundoFisico);

         Figura pared4 = new Objeto(0.2f, 10f, 30f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
         pared4.crearPropiedades(0, elasticidad, dumpingLineal, 20f, -2f, 25f, mundoFisico);
         */

        Figura plataforma1 = new Objeto(2f, 0.2f, 8f, "texturas//madera.jpg", conjunto, listaObjetosFisicos, this);
        plataforma1.crearPropiedades(0, elasticidad, dumpingLineal, -10f, 10f, -8f, mundoFisico);

        Figura plataforma2 = new Objeto(5f, 0.2f, 2f, "texturas//madera.jpg", conjunto, listaObjetosFisicos, this);
        plataforma2.crearPropiedades(0, elasticidad, dumpingLineal, -7f, 10f, 2f, mundoFisico);

        Figura plataforma3 = new Objeto(18f, 0.2f, 2f, "texturas//madera.jpg", conjunto, listaObjetosFisicos, this);
        plataforma3.crearPropiedades(0, elasticidad, dumpingLineal, 16f, 1f, 2f, mundoFisico);

        Figura plataforma4 = new Objeto(2f, 0.2f, 10f, "texturas//madera.jpg", conjunto, listaObjetosFisicos, this);
        plataforma4.crearPropiedades(0, elasticidad, dumpingLineal, 32f, 1f, 10f, mundoFisico);

        Figura plataforma5 = new Objeto(2f, 0.2f, 10f, "texturas//madera.jpg", conjunto, listaObjetosFisicos, this);
        plataforma5.crearPropiedades(0, elasticidad, dumpingLineal, 32f, 1f, 35f, mundoFisico);

        Figura plataforma6 = new Objeto(2f, 0.2f, 4f, "texturas//madera.jpg", conjunto, listaObjetosFisicos, this);
        plataforma6.crearPropiedades(0, elasticidad, dumpingLineal, 20f, 1f, 15f, mundoFisico);

        Figura plataforma7 = new Objeto(4f, 0.2f, 2f, "texturas//madera.jpg", conjunto, listaObjetosFisicos, this);
        plataforma7.crearPropiedades(0, elasticidad, dumpingLineal, 18f, 1f, 25f, mundoFisico);

        Figura plataforma8 = new Objeto(8f, 0.2f, 4f, "texturas//madera.jpg", conjunto, listaObjetosFisicos, this);
        plataforma8.crearPropiedades(0, elasticidad, dumpingLineal, 10f, 1f, 25f, mundoFisico);

        Figura sostienePuente1 = new Objeto(2f, 0.2f, 0.2f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        sostienePuente1.crearPropiedades(0, elasticidad, dumpingLineal, 32f, 0.6f, 20.5f, mundoFisico);

        Figura sostienePuente2 = new Objeto(2f, 0.2f, 0.2f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        sostienePuente2.crearPropiedades(0, elasticidad, dumpingLineal, 32f, 0.6f, 24.5f, mundoFisico);

        cilindroSostieneBoton = new Objeto(0.2f, 2f, "texturas//bosques2.jpg", conjunto, listaObjetosFisicos, this);
        cilindroSostieneBoton.crearPropiedades(0, elasticidad, dumpingLineal, 3f, 2f, 25f, mundoFisico);

        Figura pilonSostienePuente = new Objeto(0.2f, 0.2f, 2f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        pilonSostienePuente.crearPropiedades(0, elasticidad, dumpingLineal, 21f, 5.5f, 2f, mundoFisico);

        Figura plataforma9 = new Objeto(20f, 0.2f, 3f, "texturas//madera.jpg", conjunto, listaObjetosFisicos, this);
        plataforma9.crearPropiedades(0, elasticidad, dumpingLineal, 14f, 1f, 47f, mundoFisico);

        Figura basePuzzle1 = new Objeto(3f, 1.5f, 0.2f, "texturas//madera.jpg", conjunto, listaObjetosFisicos, this);
        basePuzzle1.crearPropiedades(0, elasticidad, dumpingLineal, 0f, 7.5f, 40f, mundoFisico);

        Figura basePuzzle2 = new Objeto(3f, 1f, 0.2f, "texturas//madera.jpg", conjunto, listaObjetosFisicos, this);
        basePuzzle2.crearPropiedades(0, elasticidad, dumpingLineal, 0f, 4f, 40f, mundoFisico);

        Figura basePuzzle3 = new Objeto(2f, 3f, 0.2f, "texturas//madera.jpg", conjunto, listaObjetosFisicos, this);
        basePuzzle3.crearPropiedades(0, elasticidad, dumpingLineal, -4f, 6f, 40f, mundoFisico);

        Figura basePuzzle4 = new Objeto(2f, 3f, 0.2f, "texturas//madera.jpg", conjunto, listaObjetosFisicos, this);
        basePuzzle4.crearPropiedades(0, elasticidad, dumpingLineal, 1f, 6f, 40f, mundoFisico);

        Figura caja1 = new Objeto(1f, 1f, 1f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        caja1.crearPropiedades(0.2f, elasticidad, dumpingLineal, 10f, 2.2f, 45f, mundoFisico);

        Figura caja2 = new Objeto(1f, 1f, 1f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        caja2.crearPropiedades(0.2f, elasticidad, dumpingLineal, 10f, 2.2f, 47f, mundoFisico);

        Figura caja3 = new Objeto(1f, 1f, 1f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        caja3.crearPropiedades(0.2f, elasticidad, dumpingLineal, 10f, 2.2f, 49f, mundoFisico);

        Figura caja4 = new Objeto(1f, 1f, 1f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        caja4.crearPropiedades(0.2f, elasticidad, dumpingLineal, 10f, 4.2f, 46f, mundoFisico);

        Figura caja5 = new Objeto(1f, 1f, 1f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        caja5.crearPropiedades(0.2f, elasticidad, dumpingLineal, 10f, 4.2f, 48f, mundoFisico);

        Figura caja6 = new Objeto(1f, 1f, 1f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        caja6.crearPropiedades(0.2f, elasticidad, dumpingLineal, 10f, 6.2f, 47f, mundoFisico);

        Figura sostienePuente3 = new Objeto(0.2f, 0.2f, 3f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        sostienePuente3.crearPropiedades(0, elasticidad, dumpingLineal, -6.5f, 0.6f, 47f, mundoFisico);

        Figura sostienePuente4 = new Objeto(0.2f, 0.2f, 3f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        sostienePuente4.crearPropiedades(0, elasticidad, dumpingLineal, -9.5f, 0.6f, 47f, mundoFisico);

        Figura pilonSostienePuente2 = new Objeto(0.2f, 0.2f, 2f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        pilonSostienePuente2.crearPropiedades(0, elasticidad, dumpingLineal, -8f, 6.5f, 47f, mundoFisico);

        Figura plataforma10 = new Objeto(6f, 0.2f, 3f, "texturas//madera.jpg", conjunto, listaObjetosFisicos, this);
        plataforma10.crearPropiedades(0, elasticidad, dumpingLineal, -16f, 1f, 47f, mundoFisico);


        //inicio
        Figura sueloInicio = new Objeto(7f, 0.1f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        sueloInicio.crearPropiedades(0, elasticidad, dumpingLineal, -51f, 1f, -5f, mundoFisico);

        camaraInicio = new Objeto(0.2f, 0.1f, 0.2f, "texturas//madera.jpg", conjunto, listaObjetosFisicos, this);
        camaraInicio.crearPropiedades(0.5f, 0.5f, 0.1f, -50.5f, 3f, -8f, mundoFisico);
        RigidBody rbInicio = camaraInicio.cuerpoRigido;
        Vector3f pivotInicio = new Vector3f(0f, 0f, 5f);
        Vector3f AxisInicio = new Vector3f(0f, 1f, 0f);
        HingeConstraint InicioConstraint = new HingeConstraint(rbInicio, pivotInicio, AxisInicio);
        mundoFisico.addConstraint(InicioConstraint);

        //GANADOR!
        //inicio
        Figura sueloGanador = new Objeto(7f, 0.1f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        sueloGanador.crearPropiedades(0, elasticidad, dumpingLineal, -61f, 1f, 32f, mundoFisico);

        camaraGanador = new Objeto(0.2f, 0.1f, 0.2f, "texturas//madera.jpg", conjunto, listaObjetosFisicos, this);
        camaraGanador.crearPropiedades(0.5f, 0.5f, 0.1f, -60.5f, 3f, 28f, mundoFisico);
        RigidBody rbGanador = camaraGanador.cuerpoRigido;
        Vector3f pivotGanador = new Vector3f(0f, 0f, 5f);
        Vector3f AxisGanador = new Vector3f(0f, 1f, 0f);
        HingeConstraint GanadorConstraint = new HingeConstraint(rbGanador, pivotGanador, AxisGanador);
        mundoFisico.addConstraint(GanadorConstraint);




        BolaDeRebote = new Objeto(3f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);

        //primer puente creado para caer
        Figura puente = new Objeto(2f, 0.2f, 2.5f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        puente.crearPropiedades(0.2f, elasticidad, dumpingLineal, 32f, 5f, 22.5f, mundoFisico);
        RigidBody rb0 = puente.cuerpoRigido;
        Vector3f pivotIn0 = new Vector3f(0f, 0f, 0f);
        Vector3f AxisIn0 = new Vector3f(0f, 0f, 1f);
        x0 = new HingeConstraint(rb0, pivotIn0, AxisIn0);
        mundoFisico.addConstraint(x0);

        //segundo puente creado para caer
        Figura puente2 = new Objeto(2f, 0.2f, 3f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        puente2.crearPropiedades(0.2f, elasticidad, dumpingLineal, -8f, 6f, 47f, mundoFisico);
        RigidBody rb0_0 = puente2.cuerpoRigido;
        Vector3f pivotIn0_0 = new Vector3f(0f, 0f, 0f);
        Vector3f AxisIn0_0 = new Vector3f(0f, 0f, 1f);
        x0_0 = new HingeConstraint(rb0_0, pivotIn0_0, AxisIn0_0);
        mundoFisico.addConstraint(x0_0);

        //puerta rotatoria1
        puertaRotatoria1 = new Objeto(0.2f, 2f, 1.5f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        puertaRotatoria1.crearPropiedades(4f, 0.5f, 0.1f, 8f, 3.2f, 25f, mundoFisico);
        RigidBody rb1 = puertaRotatoria1.cuerpoRigido;
        Vector3f pivotInA = new Vector3f(0f, 3f, 0f);
        Vector3f AxisInA = new Vector3f(0f, 1f, 0f);
        HingeConstraint x1 = new HingeConstraint(rb1, pivotInA, AxisInA);
        mundoFisico.addConstraint(x1);

        //puerta rotatoria2      
        puertaRotatoria2 = new Objeto(0.2f, 2f, 1.5f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        puertaRotatoria2.crearPropiedades(4f, 0.5f, 0.1f, 13f, 3.2f, 25f, mundoFisico);
        RigidBody rb2 = puertaRotatoria2.cuerpoRigido;
        Vector3f pivotInB = new Vector3f(0f, 3f, 0f);
        Vector3f AxisInB = new Vector3f(0f, 1f, 0f);
        HingeConstraint x2 = new HingeConstraint(rb2, pivotInB, AxisInB);
        mundoFisico.addConstraint(x2);

        //diana
        Figura pivoteDiana = new Objeto(0.2f, 0.1f, 0.1f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        //esta es la que gira
        this.Diana = new Objeto(1f, 1f, 0.1f, "texturas//profesor.jpg", conjunto, listaObjetosFisicos, this);
        pivoteDiana.crearPropiedades(0f, 0.5f, 0.1f, 15f, 4.5f, -13f, mundoFisico);
        this.Diana.crearPropiedades(1f, 0.5f, 0.1f, 15f, 3f, -13f, mundoFisico);
        RigidBody rbDiana = this.Diana.cuerpoRigido;
        Vector3f pivotInDiana = new Vector3f(0f, 2.5f, 0f);
        Vector3f AxisInDiana = new Vector3f(1f, 0f, 0f);
        this.pivoteDiana = new HingeConstraint(rbDiana, pivotInDiana, AxisInDiana);
        mundoFisico.addConstraint(this.pivoteDiana);

        esferaRebotante = new Objeto(0.5f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);



        //crea el terreno
        float friccion = 0.97f;
        utilidades.TerrenoSimple terreno = new utilidades.TerrenoSimple(80, 55, -25, -3f, -12, "unaTextura_Desabilitada", conjunto, mundoFisico, friccion);

        radio = 1f;
        //si el ultimo parametro es true, entonces es el personaje controlable.
        personaje = new ObjetoMDL("objetosMDL/Iron_Golem.mdl", radio, conjunto, listaObjetosFisicos, this, true);
        float posX = 20f;
        float posY = 3f;
        float posZ = 13f;
        float masa = 5f;
        personaje.crearPropiedades(masa, 0.1f, 0.5f, posX, posY, posZ, mundoFisico);

        //bala = new Objeto(0.5f, "texturas//balon.jpg", conjunto, listaObjetosFisicos, this);
        //bala.crearPropiedades(0.1f, 0.3f, 0.9f, personaje.posiciones[0], personaje.posiciones[1], personaje.posiciones[2] - 2, mundoFisico);
        //bala.crearPropiedades(0.1f, 0.3f, 0.9f, 0, 0, 0, mundoFisico);
        estadoJuego = 0;

    }

    void actualizar(float dt) throws InterruptedException {

        //hace que las puertas rotatorias giren 
        puertaRotatoria1.cuerpoRigido.applyTorque(new Vector3f(0f, 8f, 0f));
        puertaRotatoria2.cuerpoRigido.applyTorque(new Vector3f(0f, -8f, 0f));
        camaraInicio.cuerpoRigido.applyTorque(new Vector3f(0f, 5f, 0f));
        camaraGanador.cuerpoRigido.applyTorque(new Vector3f(0f, 5f, 0f));

        //System.out.println(personaje.ratonX + "--" + personaje.ratonY);

        //creacion de las balas, solo se crea un objeto y se pone diferentes veces
        bala = new Objeto(0.5f, "texturas//balon.jpg", conjunto, listaObjetosFisicos, this);

        //primera puerta, es la que gira con respecto al eje z, solo se crea una vez, por eso crear = boolean
        if (crear) {
            //esta es la figura sobre la que gira, osea el pivote
            Figura pivote1 = new Objeto(0.2f, 0.2f, 2f, "texturas//hielo.jpg",
                    conjunto, listaObjetosFisicos, this);
            //esta es la que gira
            puertaConPivote1 = new Objeto(0.2f, 2f, 2f, "texturas//bosques2.jpg",
                    conjunto, listaObjetosFisicos, this);

            //se colocan
            pivote1.crearPropiedades(0, 0.5f, 0.1f, 25f, 6f, 2f, mundoFisico);
            puertaConPivote1.crearPropiedades(10f, 0.5f, 0.1f, 25f, 3.6f, 2f, mundoFisico);
            RigidBody rbC = puertaConPivote1.cuerpoRigido;
            //el pivote es la distancia entre las posiciones, por eso es 2 en y
            Vector3f pivotInC = new Vector3f(0f, 2.5f, 0f);
            //el eje sobre el que gira
            Vector3f AxisInC = new Vector3f(0f, 0f, 1f);
            //se añade al mundo
            this.pivote1 = new HingeConstraint(rbC, pivotInC, AxisInC);
            mundoFisico.addConstraint(this.pivote1);
            crear = false;
        }

        // paso de verificacion
//        if (personaje.adelante) {
//            puertaConPivote1.cuerpoRigido.applyCentralForce(new Vector3f(4000f, 0f, 0f));
//            mundoFisico.removeConstraint(x0);
//            mundoFisico.removeConstraint(x0_0);
//        }

        if (estadoJuego == 0) {

            /**
             * Este código deshabilita System.err para evitar que aparezca el
             * mensaje de "Partial birth".
             */
//            System.setErr(new PrintStream(new OutputStream() {
//                public void write(int b) {
//                }
//            }));
            //aqui va la intro
            //TODO: es lo que comentaba, intentare implementarlo ligeramente o dar instrucciones rapidas
            Point3d posicion = new Point3d(camaraInicio.posiciones[0], camaraInicio.posiciones[1], camaraInicio.posiciones[2]);
            Point3d objetivo = new Point3d(-50.5f, 2f, -4f);
            //tercera persona
            //Point3d posicion = new Point3d(lemming.posiciones[0]-10, lemming.posiciones[1] + 2, lemming.posiciones[2] );

            this.colocarCamara(universo, posicion, objetivo);
            if (personaje.salto) {
                estadoJuego = 1;
            }

        } else if (estadoJuego == 1) {
            //System.out.println("ESTADO JUEGO 1");
            if (crearLemming) {
//                String pathLemming = "objetosMDL/pixie.mdl";
                String pathLemming = "objetosMDL/Fire_Elemental.mdl";
                lemming = new ObjetoMDL(pathLemming, 0.5f, conjunto, listaObjetosFisicos, this, false);
                lemming.crearPropiedades(5f, 0.2f, 0.2f, -10f, 16f, -13f, mundoFisico);

                // Se supone que el siguiente codigo rota el lemming...
                // pero no.
                BranchGroup rotateLemmingBG = new BranchGroup();
                TransformGroup rotateLemmingTG = new TransformGroup();
                Transform3D rotateLemmingRot = new Transform3D();
                rotateLemmingRot.rotZ(Math.PI / 2);
                rotateLemmingTG.setTransform(rotateLemmingRot);
                rotateLemmingBG.addChild(rotateLemmingTG);
                lemming.desplazamientoFigura.addChild(rotateLemmingBG);

                /* estos son metodos a probar, quizas funcionan mejor que el impulso, aunque el impulso es mas facil de manejar
                 //le asigna velocidad
                 lemming.cuerpoRigido.setLinearVelocity(new Vector3f(-2, 0, 0));
                 //hace que vaya a un punto especifico               
                 lemming.asignarObjetivo(new Vector3f(-10f, 1f, 8f), 10f);
                 */
                //TODO: asignar animacion de caida
                crearLemming = false;
            }
            //TODO: mejorar la camara, es decir, que quede un poco mas alejada o tal, el angulo me parece bien
            Point3d posicion = new Point3d(lemming.posiciones[0] - 8, lemming.posiciones[1] + 8, lemming.posiciones[2] + 5);
            Point3d objetivo = new Point3d(lemming.posiciones[0] + 8, lemming.posiciones[1] - 8, lemming.posiciones[2] - 5);
            this.colocarCamara(universo, posicion, objetivo);

            //TODO: asignar animacion de caminar o correr, como veais
            lemming.cuerpoRigido.applyCentralForce(new Vector3f(0f, 0f, 10f));

            //TODO: hay que esperar un tiempo, 2 segundos creo, a que pare del todo para poder hacer que cambie de posicion, sino se inclina un poco y no se desplaza por un eje, sino por varios
            if (lemming.posiciones[2] < 3f && lemming.posiciones[2] > -2.5f) {
                lemming.cuerpoRigido.clearForces();

                //TODO: rotar al personaje (lemming)
                Transform3D correcionTemp = new Transform3D();
                correcionTemp.rotZ(Math.PI / 2);

                Transform3D trans = new Transform3D();
                lemming.rotacionTotal.getTransform(trans);
                trans.mul(correcionTemp);
                lemming.rotacionTotal.setTransform(trans);
                estadoJuego = 2;
            }
        } else if (estadoJuego == 2) {
            //System.out.println("ESTADO JUEGO 2");
            Point3d posicion = new Point3d(2f, 6f, 30);
            Point3d objetivo = new Point3d(2, 6, 15);
            this.colocarCamara(universo, posicion, objetivo);

            lemming.cuerpoRigido.applyCentralForce(new Vector3f(7f, 0f, 0f));

            if (lemming.posiciones[0] < 7f && lemming.posiciones[0] > -2f) {
                if (personaje.salto && esferaUnica) {
                    esferaRebotante.crearPropiedades(0.2f, 0.1f, 0.1f, lemming.posiciones[0], lemming.posiciones[1] - 2, lemming.posiciones[2], mundoFisico);
                    Vector3f fuerzaDisparo = new Vector3f(7f, -60f, 0f);
                    for (int i = 0; i < 20; i++) {
                        esferaRebotante.cuerpoRigido.applyCentralForce(fuerzaDisparo);
                    }
                    esferaUnica = false;
                }
            }

            if (!bolaReboteCreada && esferaRebotante.posiciones[1] != 0.0 && esferaRebotante.posiciones[1] < 1.5) {
                BolaDeRebote.crearPropiedades(0, 0.8f, 0.2f, esferaRebotante.posiciones[0], 1.2f, esferaRebotante.posiciones[2], mundoFisico);
                bolaReboteCreada = true;
            }

            if (lemming.posiciones[0] < 8f && lemming.posiciones[0] > 6f) {
                lemming.cuerpoRigido.clearForces();
                estadoJuego = 3;
            }
        } else if (estadoJuego == 3) {

            //System.out.println("ESTADO JUEGO 3");         
            Point3d posicion = new Point3d(lemming.posiciones[0], lemming.posiciones[1] + 2, lemming.posiciones[2] - 2);
            Point3d objetivo = new Point3d(lemming.posiciones[0], lemming.posiciones[1] + 2, lemming.posiciones[2] - 4);

            this.colocarCamara(universo, posicion, objetivo);

            lemming.cuerpoRigido.applyCentralForce(new Vector3f(5f, 0f, -0.5f));

            if (disparo.apretado()) {
                bala.crearPropiedades(0.1f, 0.3f, 0.9f, lemming.posiciones[0], lemming.posiciones[1] + 3, lemming.posiciones[2] + 2, mundoFisico);
                Vector3f fuerzaDisparo = new Vector3f((float) disparo.posicionRaton().x, (float) disparo.posicionRaton().y, (float) disparo.posicionRaton().z);
                bala.asignarObjetivo(fuerzaDisparo, 500f);
                disparo.valorApretado(false);
                //System.out.println(bala.posiciones[0] + "--" + bala.posiciones[1] + "--" + bala.posiciones[2]);
                // System.out.println("-----" + lemming.posiciones[0] + "--" + lemming.posiciones[1] + "--" + lemming.posiciones[2]);

            }


            if (this.Diana.posiciones[2] != -13f) {
                for (int i = 0; i < 20; i++) {
                    puertaConPivote1.cuerpoRigido.applyCentralForce(new Vector3f(500f, 0f, 0f));
                }

                estadoJuego = 4;
            }


            if (lemming.posiciones[0] < 32f && lemming.posiciones[0] > 26f) {

                lemming.cuerpoRigido.clearForces();
                estadoJuego = 4;
            }

        } else if (estadoJuego == 4) {
            //System.out.println("ESTADO JUEGO 4");
            Point3d posicion = new Point3d(30f, 4f, 35);
            Point3d objetivo = new Point3d(35, 4, 2);
            this.colocarCamara(universo, posicion, objetivo);

            if (lemming.posiciones[0] < 30f) {
                lemming.cuerpoRigido.applyCentralForce(new Vector3f(7f, 0f, -0.5f));
            }

            if (lemming.posiciones[0] < 32f && lemming.posiciones[0] >= 30f) {
                lemming.cuerpoRigido.clearForces();
                estadoJuego = 5;
            }


        } else if (estadoJuego == 5) {
            //System.out.println("ESTADO JUEGO 5");
            //en este el personaje controlable tendra que pulsar un boton para activar el puente

            Point3d posicion = new Point3d(personaje.posiciones[0], personaje.posiciones[1] + 8, personaje.posiciones[2] - 15);
            Point3d objetivo = new Point3d(personaje.posiciones[0], personaje.posiciones[1], personaje.posiciones[2] + 15);
            this.colocarCamara(universo, posicion, objetivo);

            //TODO: si pulsa el boton, entonces -->mundoFisico.removeConstraint(x0);

            lemming.cuerpoRigido.applyCentralForce(new Vector3f(-0.2f, 0f, 5f));


            if (lemming.posiciones[2] < 18f && lemming.posiciones[2] > 15f) {
                estadoJuego = 6;
                Transform3D correcionTemp = new Transform3D();
                correcionTemp.rotZ(-Math.PI / 2);

                Transform3D trans = new Transform3D();
                lemming.rotacionTotal.getTransform(trans);
                trans.mul(correcionTemp);
                lemming.rotacionTotal.setTransform(trans);

            }
            if (personaje.DistanciaColisionEntreFiguras(cilindroSostieneBoton) < 2f && personaje.accion) {
                mundoFisico.removeConstraint(x0);
                Transform3D correcionTemp = new Transform3D();
                correcionTemp.rotZ(-Math.PI / 2);

                Transform3D trans = new Transform3D();
                lemming.rotacionTotal.getTransform(trans);
                trans.mul(correcionTemp);
                lemming.rotacionTotal.setTransform(trans);
                estadoJuego = 6;
            }
            //TODO:  if(Personaje aprieta el boton bien){            }

        } else if (estadoJuego == 6) {
            System.out.println("ESTADO JUEGO 6");
            Point3d posicion = new Point3d(30f, 4f, 40);
            Point3d objetivo = new Point3d(35, 4, 10);

            this.colocarCamara(universo, posicion, objetivo);
            lemming.cuerpoRigido.applyCentralForce(new Vector3f(-0.2f, 0f, 7f));

            System.out.println("lemming.posiciones[0] = " + lemming.posiciones[0]);
            System.out.println("lemming.posiciones[1] = " + lemming.posiciones[1]);
            System.out.println("lemming.posiciones[2] = " + lemming.posiciones[2]);

//            if (lemming.posiciones[0] < 2f && lemming.posiciones[0] > 0f) {
//                estadoJuego = 5;
//            }
            if (lemming.posiciones[2] > 37f) {
                Transform3D correcionTemp = new Transform3D();
                correcionTemp.rotZ(-Math.PI / 2);

                Transform3D trans = new Transform3D();
                lemming.rotacionTotal.getTransform(trans);
                trans.mul(correcionTemp);
                lemming.rotacionTotal.setTransform(trans);
                estadoJuego = 7;
            }
        } //
        //
        else if (estadoJuego == 7) {
            Point3d posicion = new Point3d(lemming.posiciones[0]-2, lemming.posiciones[1] + 2, lemming.posiciones[2]);
            Point3d objetivo = new Point3d(lemming.posiciones[0] - 7, lemming.posiciones[1] + 2, lemming.posiciones[2]);
            this.colocarCamara(universo, posicion, objetivo);
            if (lemming.posiciones[2] < 45f) {
                lemming.cuerpoRigido.applyCentralForce(new Vector3f(-0.2f, 0f, 7f));
            } else {
                lemming.cuerpoRigido.applyCentralForce(new Vector3f(-10f, 0f, -0.5f));
            }

            if (disparo.apretado()) {
                bala.crearPropiedades(0.05f, 0.9f, 0.1f, lemming.posiciones[0] - 3, lemming.posiciones[1] + 1, lemming.posiciones[2], mundoFisico);
                Vector3f fuerzaDisparo = new Vector3f((float) disparo.posicionRaton().x, (float) disparo.posicionRaton().y, (float) disparo.posicionRaton().z);
                bala.asignarObjetivo(fuerzaDisparo, 700f);
                disparo.valorApretado(false);
            }

            if (lemming.posiciones[0] < 15) {
                estadoJuego = 8;
            }
        } else if (estadoJuego == 8) {
            Point3d posicion = new Point3d(-5, 5, 62);
            Point3d objetivo = new Point3d(-5, 5, 40);
            this.colocarCamara(universo, posicion, objetivo);
            lemming.cuerpoRigido.applyCentralForce(new Vector3f(-10f, 0f, -0.5f));

            System.out.println(personaje.ratonX + "--" + personaje.ratonY);
            //esto deberia cambiarse un poco, ya que depende del tamaño de ventana, TODO:alguien.
            if ((personaje.ratonX > 675) && (personaje.ratonX < 720) && (personaje.ratonY < 390) && (personaje.ratonY > 320)) {
                mundoFisico.removeConstraint(x0_0);
            }
            if (lemming.posiciones[0] < -12f) {
                //que vaya a perder
                estadoJuego = 9;
            }

        } else if (estadoJuego == 9) {
            Point3d posicion = new Point3d(camaraGanador.posiciones[0], camaraGanador.posiciones[1], camaraGanador.posiciones[2]);
            Point3d objetivo = new Point3d(-60f, 2f, 32f);
            //tercera persona
            //Point3d posicion = new Point3d(lemming.posiciones[0]-10, lemming.posiciones[1] + 2, lemming.posiciones[2] );

            this.colocarCamara(universo, posicion, objetivo);
        }
        if (personaje != null) {
            float fuerzaElevacion = 0, fuerzaGiro = 0;
            Vector3f fuerzaImpulso = new Vector3f(0, 0, 0);
            Vector3f fuerzaDisparo = new Vector3f(0, 0, 0);

            if (personaje.adelante) {
                fuerzaImpulso = personaje.conseguirDireccionFrontal();
            }
            if (personaje.atras) {
                fuerzaImpulso = personaje.conseguirDireccionTrasera();
            }
            if (!personaje.adelante && !personaje.atras) {
                personaje.cuerpoRigido.setLinearVelocity(new Vector3f(0, 0, 0));
            }
            if (personaje.derecha) {
                fuerzaGiro = -50 /* -personaje.masa * 10f*/;
            }
            if (personaje.izquierda) {
                fuerzaGiro = 50 /* personaje.masa * 10f*/;
            }
            if (!personaje.izquierda && !personaje.derecha) {
                personaje.cuerpoRigido.setAngularFactor(0);
                personaje.cuerpoRigido.setAngularVelocity(new Vector3f(0, 0, 0));
            }
            if (personaje.salto) {
                fuerzaElevacion = personaje.masa * 10f + personaje.masa * 190f;
                personaje.salto = false;
            }
            if (personaje.disparo) {
                if (personaje.DistanciaColisionEntreFiguras(bala) < 3) {
                    System.out.println(personaje.posiciones[0]);
                    fuerzaDisparo = new Vector3f(-personaje.posiciones[0] * 10, 5, -20);
                    bala.cuerpoRigido.applyCentralForce(fuerzaDisparo);
                } else {
                    fuerzaDisparo = new Vector3f(-personaje.posiciones[0] * 10, 5, -100);
                    bala.cuerpoRigido.applyCentralForce(fuerzaDisparo);
                }
            }
            if (personaje.reinicio) {
                //aqui es donde pondria que se reiniciara el juego, por motivos de tiempo no podre implementarlo
            }

            // Vector3f FuerzaElevacion = new Vector3f(0, fuerzaElevacion, 0);
            Vector3f FuerzaElevacion = new Vector3f(0, fuerzaElevacion, 0);
            Vector3f FuerzaImpulso = fuerzaImpulso;
            Vector3f FuerzaGiro = new Vector3f(0, fuerzaGiro, 0);
            personaje.cuerpoRigido.applyCentralForce(FuerzaElevacion);
            personaje.cuerpoRigido.applyCentralForce(FuerzaImpulso);
            personaje.cuerpoRigido.applyTorque(FuerzaGiro);
            //personaje.cuerpoRigido.applyTorque(FuerzaGiro); //Este giro no ajusta direccion del frente. Programarlo.


            for (int i = 0; i < this.listaObjetosFisicos.size(); i++) {
                listaObjetosFisicos.get(i).actualizar();
            }
            //ACTUALIZAR DATOS DE LOCALIZACION DE FIGURAS NO FISICAS
            for (int i = 0; i < this.listaObjetosNoFisicos.size(); i++) {
                listaObjetosNoFisicos.get(i).actualizar();
            }

            //ACTUALIZAR DATOS DE LOCALIZACION DE FIGURAS FISICAS
            this.actualizandoFisicas = true;
            try {
                mundoFisico.stepSimulation(dt);    //mundoFisico.stepSimulation ( dt  ,50000, dt*0.2f);
            } catch (Exception e) {
                System.out.println("JBullet forzado. No debe crearPropiedades de solidoRigidos durante la actualizacion stepSimulation");
            }

            this.actualizandoFisicas = false;

            personaje.posAnteriorMilimetros = new int[3];
            personaje.posAnteriorMilimetros[0] = (int) (personaje.posiciones[0] * 10000f);
            personaje.posAnteriorMilimetros[2] = (int) (personaje.posiciones[2] * 10000f);

            if (personaje.posiciones[1] < 1) {
                estadoJuego = 0;
            }
            if (lemming != null && lemming.posiciones[1] < 1) {
                estadoJuego = 0;
            }
        }


        //ACTUALIZAR DATOS DE FUERZAS DE LAS FIGURAS AUTONOMAS

    }

    void mostrar() throws Exception {
        //MOSTRAR FIGURAS NO FISICAS (con base en sus datos de localizacion)
        // for (int i=0; i< this.listaObjetosNoFisicos.size(); i++)
        // listaObjetosNoFisicos.get(i).mostrar();

        //MOSTRAR FIGURAS FISICAS (muestra el componente visual de la figura, con base en los datos de localizacion del componente fisico)
        this.mostrandoFisicas = true;
        try {
            if ((mundoFisico.getCollisionObjectArray().size() != 0) && (listaObjetosFisicos.size() != 0)) {
                for (int idFigura = 0; idFigura <= this.listaObjetosFisicos.size() - 1; idFigura++) {     // Actualizar posiciones fisicas y graficas de los objetos.
                    try {
                        int idFisico = listaObjetosFisicos.get(idFigura).identificadorFisico;
                        CollisionObject objeto = mundoFisico.getCollisionObjectArray().get(idFisico); //
                        RigidBody cuerpoRigido = RigidBody.upcast(objeto);
                        listaObjetosFisicos.get(idFigura).mostrar(cuerpoRigido);
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
        }
        this.mostrandoFisicas = false;
    }

    public void run() {
        cargarContenido();
        float dt = 3f / 100f;
        int tiempoDeEspera = (int) (dt * 1000);
        while (estadoJuego != -1) {
            try {
                this.actualizar(dt);
            } catch (InterruptedException ex) {
                Logger.getLogger(Juego.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                //actualizar(dt);
            } catch (Exception e) {
                System.out.println(e.getMessage() + "-" + e.getLocalizedMessage() + "-" + e);
                System.out.println("Error durante actualizar. Estado del juego " + estadoJuego);
            }
            try {
                mostrar();
            } catch (Exception ex) {
                Logger.getLogger(Juego.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                Thread.sleep(tiempoDeEspera);
            } catch (Exception e) {
            }
        }
    }

    void colocarCamara(SimpleUniverse universo, Point3d posicionCamara1, Point3d objetivoCamara) {
        Point3d posicionCamara = new Point3d(posicionCamara1.x + 0.001, posicionCamara1.y + 0.001d, posicionCamara1.z + 0.001);
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

    protected void anadirSonidoARama(BranchGroup b, String soundFile) {
        //Create a media container to load the file
        MediaContainer droneContainer = new MediaContainer(soundFile);
        //Create the background sound from the media container
        BackgroundSound drone = new BackgroundSound(droneContainer,
                1.0f);
        //Activate the sound
        drone.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 500.0));
        drone.setEnable(true);
        //Set the sound to loop forever
        drone.setLoop(BackgroundSound.INFINITE_LOOPS);
        b.addChild(drone);
    }

    public static void main(String[] args) {
        Juego x = new Juego();
        x.setTitle("Lemmings 3D");
        x.setSize(1000, 800);
        x.setVisible(true);
        x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //x.colocarCamara(x.universo, new Point3d(-3f, 4.5f, 32), new Point3d(-3, 1, 0));
        //x.colocarCamara(x.universo, new Point3d(23f, 6f, 46f), new Point3d(9, 3, 45f));
    }
}