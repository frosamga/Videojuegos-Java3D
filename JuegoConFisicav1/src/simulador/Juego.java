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
import com.bulletphysics.dynamics.constraintsolver.Point2PointConstraint;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import figuras.ObjetoMDL;
import figuras.Objeto;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Juego extends JFrame implements Runnable {

    int estadoJuego = 0;
    SimpleUniverse universo;
    BoundingSphere limites = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
    public String rutaCarpetaProyecto = System.getProperty("user.dir") + "/";
    Thread hebra = new Thread(this);
    ArrayList<simulador.Figura> listaObjetosFisicos = new ArrayList<Figura>();
    ArrayList<simulador.Figura> listaObjetosNoFisicos = new ArrayList<Figura>();
    DiscreteDynamicsWorld mundoFisico;
    BranchGroup conjunto = new BranchGroup(), textoGanarBG, objRoot;
    public boolean actualizandoFisicas, mostrandoFisicas;
    // Pesonajes importantes del juego
    Figura personaje;  //golem;
    Figura soldado;
    Figura bala;
    ObjetoMDL hada;
    Objeto animalEsferico;

    public Juego() {
        //crea un espacio con fisica incluida, no el terreno 
        CollisionConfiguration configuracionColision = new DefaultCollisionConfiguration();
        CollisionDispatcher dispatcher = new CollisionDispatcher(configuracionColision);
        Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
        Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);
        AxisSweep3 AnchoMapa = new AxisSweep3(worldAabbMin, worldAabbMax);
        SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();
        mundoFisico = new DiscreteDynamicsWorld(dispatcher, AnchoMapa, solver, configuracionColision);
        mundoFisico.setGravity(new Vector3f(0, -10, 0));

        //crear el universo y la pantalla
        Container GranPanel = getContentPane();
        Canvas3D zonaDibujo = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        zonaDibujo.setPreferredSize(new Dimension(800, 600));
        GranPanel.add(zonaDibujo, BorderLayout.CENTER);
        universo = new SimpleUniverse(zonaDibujo);
        BranchGroup escena = crearEscena();
        escena.compile();
        universo.getViewingPlatform().setNominalViewingTransform();
        universo.addBranchGraph(escena);
        /*
         OrbitBehavior B = new OrbitBehavior(zonaDibujo);
         B.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
         universo.getViewingPlatform().setViewPlatformBehavior(B);
         */

        //empieza esta misma funcion
        hebra.start();
    }

    BranchGroup crearEscena() {
        //objRoot->conjunto
        objRoot = new BranchGroup();
        conjunto = new BranchGroup();
        objRoot.addChild(conjunto);
        conjunto.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        conjunto.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        ComportamientoMostrar mostrar = new ComportamientoMostrar(this);
        textoGanarBG = new BranchGroup();

        //a�adir luz
        DirectionalLight LuzDireccional = new DirectionalLight(new Color3f(10f, 10f, 10f), new Vector3f(1f, 0f, -1f));
        BoundingSphere limitesLuz = new BoundingSphere(new Point3d(-15, 10, 15), 100.0);
        LuzDireccional.setInfluencingBounds(limitesLuz);

        //limites esta arriba, limite de vision
        mostrar.setSchedulingBounds(limites);
        Background bg = new Background();
        bg.setApplicationBounds(limites);
        bg.setColor(new Color3f(135f / 256, 206f / 256f, 250f / 256f));


        objRoot.addChild(LuzDireccional);
        objRoot.addChild(bg);
        objRoot.addChild(mostrar);
        return objRoot;
    }

    void cargarContenido() {
        float radio;
        float masaConstruccion = 0.2f;
        //Capacidad de rebotar. 1 para grandes rebote   0 para simular gotas de liquidos espesos
        float elasticidad = 0.3f;
        //Perdidad de velodidad al desplazarse (friccion del aire): 0 para mantener velocidad. 0.99 para perder velocidad (liquidos espesos)
        float dumpingLineal = 0.9f;

        Figura pared1 = new Objeto(10f, 5f, 0.2f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        pared1.crearPropiedades(0, elasticidad, dumpingLineal, 0, -2f, -4f, mundoFisico);

        Figura pared2 = new Objeto(10f, 5f, 0.2f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        pared2.crearPropiedades(0, elasticidad, dumpingLineal, -3, -2f, 23f, mundoFisico);

        Figura pared3 = new Objeto(0.2f, 5f, 12f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        pared3.crearPropiedades(0, elasticidad, dumpingLineal, -10, -2f, 5f, mundoFisico);

        Figura pared4 = new Objeto(0.2f, 5f, 12f, "texturas//hielo.jpg", conjunto, listaObjetosFisicos, this);
        pared4.crearPropiedades(0, elasticidad, dumpingLineal, 10f, -2f, 5f, mundoFisico);


        Figura caja1 = new Objeto(1f, 1f, 1f, "texturas//ladrillo.jpg", conjunto, listaObjetosFisicos, this);
        caja1.crearPropiedades(masaConstruccion, elasticidad, dumpingLineal, -3, -2f, 0f, mundoFisico);

        Figura caja2 = new Objeto(1f, 1f, 1f, "texturas//ladrillo.jpg", conjunto, listaObjetosFisicos, this);
        caja2.crearPropiedades(masaConstruccion, elasticidad, dumpingLineal, 0f, -2f, 0f, mundoFisico);

        Figura caja3 = new Objeto(1f, 1f, 1f, "texturas//ladrillo.jpg", conjunto, listaObjetosFisicos, this);
        caja3.crearPropiedades(masaConstruccion, elasticidad, dumpingLineal, 3f, -2f, 0f, mundoFisico);

        Figura caja4 = new Objeto(1f, 1f, 1f, "texturas//ladrillo.jpg", conjunto, listaObjetosFisicos, this);
        caja4.crearPropiedades(masaConstruccion, elasticidad, dumpingLineal, -1.5f, 0f, 0f, mundoFisico);

        Figura caja5 = new Objeto(1f, 1f, 1f, "texturas//ladrillo.jpg", conjunto, listaObjetosFisicos, this);
        caja5.crearPropiedades(masaConstruccion, elasticidad, dumpingLineal, 1.5f, 0f, 0f, mundoFisico);

        Figura caja6 = new Objeto(1f, 1f, 1f, "texturas//ladrillo.jpg", conjunto, listaObjetosFisicos, this);
        caja6.crearPropiedades(masaConstruccion, elasticidad, dumpingLineal, 0f, 2f, 0f, mundoFisico);

        soldado = new ObjetoMDL("objetosMDL/Doomknight.mdl", 1f, conjunto, listaObjetosFisicos, this, true);
        soldado.crearPropiedades(0.5f, elasticidad, 0.5f, 0f, 3.5f, 0f, mundoFisico);

        //crea el terreno
        float friccion = 0.97f;
        utilidades.TerrenoSimple terreno = new utilidades.TerrenoSimple(30, 30, -5, -3f, -12, "unaTextura_Desabilitada", conjunto, mundoFisico, friccion);



        radio = 1f;
        float posX = 0f;
        float posY = -2f;
        float posZ = 15f;
        float masa = 5f;
        //si el ultimo parametro es true, entonces es el personaje controlable.
        personaje = new ObjetoMDL("objetosMDL/Iron_Golem.mdl", radio, conjunto, listaObjetosFisicos, this, true);
        personaje.crearPropiedades(masa, elasticidad, 0.5f, posX, posY, posZ, mundoFisico);
        estadoJuego = 0;

        bala = new Objeto(0.5f, "texturas//balon.jpg", conjunto, listaObjetosFisicos, this);
        bala.crearPropiedades(0.1f, 0.3f, 0.9f, personaje.posiciones[0], personaje.posiciones[1], personaje.posiciones[2] - 2, mundoFisico);


    }

    void actualizar(float dt) {

        if (personaje != null) {
            float fuerzaElevacion = 0, fuerzaGiro = 0;
            Vector3f fuerzaImpulso = new Vector3f(0, 0, 0);
            Vector3f fuerzaDisparo = new Vector3f(0, 0, 0);

            if (personaje.adelante) {
                fuerzaImpulso = personaje.conseguirDireccionFrontal();
                //volar
                //fuerzaElevacion = personaje.masa * 10f * 1.2f;
            }
            if (personaje.atras) {
                fuerzaImpulso = personaje.conseguirDireccionTrasera();
            }
            if (personaje.derecha) {
                //fuerzaGiro = -personaje.masa * 10f;
                fuerzaImpulso = new Vector3f(100, 0, 0);
            }
            if (personaje.izquierda) {
                //fuerzaGiro = personaje.masa * 10f;
                fuerzaImpulso = new Vector3f(-100, 0, 0);

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
            if (soldado.posiciones[1] < 1) {
                //aqui es donde tocaria el suelo y haria algo, pero no se me ocurre que, ademas de eliminarlo
            }
            // Vector3f FuerzaElevacion = new Vector3f(0, fuerzaElevacion, 0);
            Vector3f FuerzaImpulso = fuerzaImpulso;
            Vector3f FuerzaGiro = new Vector3f(0, fuerzaGiro, 0);
            personaje.cuerpoRigido.applyCentralForce(FuerzaImpulso);
            //personaje.cuerpoRigido.applyTorque(FuerzaGiro); //Este giro no ajusta direccion del frente. Programarlo.

            for (int i = 0; i < this.listaObjetosFisicos.size(); i++) {
                listaObjetosFisicos.get(i).actualizar();
            }

            //ACTUALIZAR DATOS DE LOCALIZACION DE FIGURAS NO FISICAS
            //for (int i=0; i< this.listaObjetosNoFisicos.size(); i++)
            // listaObjetosNoFisicos.get(i).actualizar(dt);
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
                actualizar(dt);
            } catch (Exception e) {
                System.out.println(e.getMessage());
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

    public static void main(String[] args) {
        Juego x = new Juego();
        x.setTitle("Tiros Libres");
        x.setSize(1000, 800);
        x.setVisible(true);
        x.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        x.colocarCamara(x.universo, new Point3d(0f, 8f, 30f), new Point3d(0, 0, 0));
    }
}
