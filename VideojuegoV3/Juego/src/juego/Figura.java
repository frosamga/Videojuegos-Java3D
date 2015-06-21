package juego;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.j3d.*;
import javax.vecmath.*;
import net.sf.nwn.loader.AnimationBehavior;
import net.sf.nwn.loader.NWNLoader;

public class Figura {

    float dt, radio, altura;
    float[] velocidades = new float[3];
    float[] posiciones = new float[3];
    public boolean adelante, atras, izquierda, derecha, moviendo, cambioPersona, ataque, cam1 = true;
    public TransformGroup desplazamientoFigura = new TransformGroup();
    int identificador;
    ArrayList<Figura> listaObjetos;
    Navegador_Tema_3 juego;
    BranchGroup conjunto, personaje;
    Matrix3f matrizRotacionPersonaje = new Matrix3f();
    boolean MDL = false;
    AnimationBehavior ab, abEnemigo;
    public Vector3d dir = new Vector3d(0, 0, 0);
    public Vector3d posPj = new Vector3d(0, 0, 0);
    public Vector3d posSo = new Vector3d(0, 0, 0);
    public Terreno terreno;
    //si es 1 significa que es un enemigo y que persigue al personaje principal
    Vector3d posPersonaje = new Vector3d(0, 0, 0);
    public boolean perder = false;

    Figura(String ruta_, float radio_, BranchGroup conjunto_, ArrayList<Figura> _listaObjetos, Navegador_Tema_3 juego_, boolean ActivarMDL) {
        juego = juego_;
        conjunto = conjunto_;
        radio = radio_;
        conjunto.addChild(desplazamientoFigura);
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        listaObjetos = _listaObjetos;
        listaObjetos.add(this);
        identificador = listaObjetos.size() - 1;
        MDL = ActivarMDL;
        moviendo = false;

        NWNLoader nwn2 = new NWNLoader();
        nwn2.enableModelCache(ActivarMDL);
        Scene Personaje1 = null;
        try {
            Personaje1 = nwn2.load(ruta_);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Navegador_Tema_3.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IncorrectFormatException ex) {
            Logger.getLogger(Navegador_Tema_3.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParsingErrorException ex) {
            Logger.getLogger(Navegador_Tema_3.class.getName()).log(Level.SEVERE, null, ex);
        }
        BranchGroup RamaMDL = Personaje1.getSceneGroup();

        ab = (AnimationBehavior) Personaje1.getNamedObjects().get("AnimationBehavior");
        //dame las animaciones disponibles
        System.out.println(ab.getAllAnimationNames());

        Transform3D rotacionCombinada = new Transform3D();
        rotacionCombinada.rotX(-Math.PI / 2);
        Transform3D correcionTemp = new Transform3D();
        correcionTemp.rotZ(Math.PI);
        rotacionCombinada.mul(correcionTemp);
        correcionTemp.rotY(0);
        rotacionCombinada.mul(correcionTemp);
        correcionTemp.setScale(0.5f);
        rotacionCombinada.mul(correcionTemp);
        TransformGroup rotadorDeFIguraMDL = new TransformGroup(rotacionCombinada);
        rotadorDeFIguraMDL.addChild(RamaMDL);
        Capabilities.setCapabilities(RamaMDL);
        // PickTool.setCapabilities(RamaMDL.getChild(0), PickTool.INTERSECT_FULL);
        RamaMDL.getChild(0).setUserData(ruta_);
        RamaMDL.setUserData("figura_" + identificador);

        if (identificador != 0) {
            RamaMDL.setPickable(true);
            desplazamientoFigura.addChild(rotadorDeFIguraMDL);
            abEnemigo = (AnimationBehavior) Personaje1.getNamedObjects().get("AnimationBehavior");
            System.out.println(abEnemigo.getAllAnimationNames());
            abEnemigo.playAnimation("fire_elemental:cwalk", true);
        } else {
            RamaMDL.setPickable(false);
            Transform3D rot = new Transform3D();
            rot.rotX(0);
            TransformGroup rotador = new TransformGroup(rot);
            desplazamientoFigura.addChild(rotador);
            rotador.addChild(rotadorDeFIguraMDL);
        }
    }

    Figura(String ruta_, BranchGroup conjunto_, ArrayList<Figura> _listaObjetos, Navegador_Tema_3 juego_) {
        juego = juego_;
        conjunto = conjunto_;

        conjunto.addChild(desplazamientoFigura);
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        listaObjetos = _listaObjetos;
        listaObjetos.add(this);
        identificador = listaObjetos.size() - 1;

        NWNLoader nwn2 = new NWNLoader();
        nwn2.enableModelCache(true);
        Scene Personaje1 = null;
        try {
            Personaje1 = nwn2.load(ruta_);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Navegador_Tema_3.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IncorrectFormatException ex) {
            Logger.getLogger(Navegador_Tema_3.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParsingErrorException ex) {
            Logger.getLogger(Navegador_Tema_3.class.getName()).log(Level.SEVERE, null, ex);
        }
        BranchGroup RamaMDL = Personaje1.getSceneGroup();

        ab = (AnimationBehavior) Personaje1.getNamedObjects().get("AnimationBehavior");
        //dame las animaciones disponibles
        System.out.println(ab.getAllAnimationNames());

        Transform3D rotacionCombinada = new Transform3D();
        rotacionCombinada.rotX(-Math.PI / 2);
        Transform3D correcionTemp = new Transform3D();
        correcionTemp.rotZ(Math.PI);
        rotacionCombinada.mul(correcionTemp);
        correcionTemp.rotY(0);
        rotacionCombinada.mul(correcionTemp);
        correcionTemp.setScale(0.5f);
        rotacionCombinada.mul(correcionTemp);
        TransformGroup rotadorDeFIguraMDL = new TransformGroup(rotacionCombinada);
        rotadorDeFIguraMDL.addChild(RamaMDL);
        Capabilities.setCapabilities(RamaMDL);
        // PickTool.setCapabilities(RamaMDL.getChild(0), PickTool.INTERSECT_FULL);
        RamaMDL.getChild(0).setUserData(ruta_);
        RamaMDL.setUserData("figura_" + identificador);

        if (identificador != 0) {
            RamaMDL.setPickable(true);
            desplazamientoFigura.addChild(rotadorDeFIguraMDL);
        } else {
            RamaMDL.setPickable(false);
            Transform3D rot = new Transform3D();
            rot.rotX(0);
            TransformGroup rotador = new TransformGroup(rot);
            desplazamientoFigura.addChild(rotador);
            rotador.addChild(rotadorDeFIguraMDL);
        }
    }

    Figura(String ruta_, float radio_, BranchGroup conjunto_, ArrayList<Figura> _listaObjetos, Navegador_Tema_3 juego_) {
        juego = juego_;
        conjunto = conjunto_;
        radio = radio_;
        conjunto.addChild(desplazamientoFigura);
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        listaObjetos = _listaObjetos;
        listaObjetos.add(this);
        identificador = listaObjetos.size() - 1;
        ObjectFile file = new ObjectFile(ObjectFile.RESIZE);
        Scene scene = null;
        try {
            scene = file.load(ruta_);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
        personaje = scene.getSceneGroup();



        System.out.println("Leido elephav,obj. La clase del primer hijo es " + personaje.getChild(0).getClass().getName());
        Transform3D scala = new Transform3D();
        scala.setScale(2);
        TransformGroup TGpersonaje = new TransformGroup(scala);
        TGpersonaje.addChild(personaje);
        personaje.getChild(0).setUserData("Personaje");
        PickTool.setCapabilities(personaje.getChild(0), PickTool.INTERSECT_FULL);
        System.out.println("clase:" + personaje.getChild(0).getClass().getName() + " hijos=" + personaje);
        if (identificador != 0) {
            personaje.setPickable(true);
            desplazamientoFigura.addChild(TGpersonaje);
        } else {
            //Si  es el personaje controlable se configura como no-localizable. El cono se rota solo para indicar la direccion del sonar
            personaje.setPickable(false);
            Transform3D rot = new Transform3D();
            rot.rotX(0);
            TransformGroup rotador = new TransformGroup(rot);
            desplazamientoFigura.addChild(rotador);
            rotador.addChild(TGpersonaje);
        }
    }

    Figura(float radio_, float altura_, BranchGroup conjunto_, ArrayList<Figura> _listaObjetos, Navegador_Tema_3 juego_) {
        juego = juego_;
        conjunto = conjunto_;
        radio = radio_;
        altura = altura_;
        conjunto.addChild(desplazamientoFigura);
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        listaObjetos = _listaObjetos;
        listaObjetos.add(this);
        identificador = listaObjetos.size() - 1;

        Cone cono = new Cone(radio, altura);
        cono.getShape(0).setUserData("figura_" + identificador);
        cono.getShape(1).setUserData("figura_" + identificador);
        PickTool.setCapabilities(cono.getShape(0), PickTool.INTERSECT_FULL);
        PickTool.setCapabilities(cono.getShape(1), PickTool.INTERSECT_FULL);

        //Si  es un objeto no controlado, se configura como localizable
        if (identificador != 0) {
            cono.getShape(0).setPickable(true);
            cono.getShape(1).setPickable(true);
            desplazamientoFigura.addChild(cono);
        } else {
            //Si  es el personaje controlable se configura como no-localizable. El cono se rota solo para indicar la direccion del sonar
            cono.getShape(0).setPickable(false);
            cono.getShape(1).setPickable(false);
            Transform3D rot = new Transform3D();
            rot.rotX(Math.PI / 2f);
            TransformGroup rotador = new TransformGroup(rot);
            desplazamientoFigura.addChild(rotador);
            rotador.addChild(cono);
        }
    }

    void inicializar(float x, float y, float z) {
        //Dando valor inicial al array de posiciones. El ángulo se fija en 0. Se puede incluir como parametro.  Este método no transforma/muestra.
        posiciones[0] = x;
        posiciones[1] = y;
        posiciones[2] = z;
        float anguloInicial = 0;
        matrizRotacionPersonaje.set(new AxisAngle4d(0, 1, 0, anguloInicial));
    }

    public void actualizar(float dt) {
        //Se consulta el Transform3D actual y una copia porque se necesitarán para varias operaciones de actualización
        Transform3D t3dPersonaje = new Transform3D();
        desplazamientoFigura.getTransform(t3dPersonaje);
        Transform3D copiat3dPersonaje = new Transform3D(t3dPersonaje);



        if (identificador != 0) {

            if (identificador == 5) {
                double[] distancia = { posPersonaje.x-posiciones[0], 0d, posPersonaje.z-posiciones[2]};
                for (int p = 0; p < 3; p++) {
                    posiciones[p] = (float) (posiciones[p] + dt * velocidades[p]* distancia[p]);
                    t3dPersonaje.get(matrizRotacionPersonaje);
                }
            } else if (identificador == 6) {
                if (listaObjetos.get(6).quemarse(listaObjetos.get(0))) {
                    perder = true;
                }
            } else {
                for (int p = 0; p < 3; p++) {
                    posiciones[p] =
                            posiciones[p] + dt * velocidades[p];
                    t3dPersonaje.get(matrizRotacionPersonaje);
                }
            }
        } else {
            if (juego.disparo.apretado()) {
                posPersonaje = new Vector3d(0, 0, 0);
                t3dPersonaje.get(matrizRotacionPersonaje, posPersonaje);
                posiciones[0] = (float) posPersonaje.x;
                posiciones[1] = (float) posPersonaje.y;
                posiciones[2] = (float) posPersonaje.z;
                double x = juego.disparo.posicionRaton().x;
                double y = juego.disparo.posicionRaton().y;
                double z = juego.disparo.posicionRaton().z;
                double[] distancia = {x - posiciones[0], y - posiciones[1], z - posiciones[2]};


                for (int p = 0; p < 3; p++) {
                    posiciones[p] = (float) (posiciones[p] + dt * distancia[p]);
                    t3dPersonaje.get(matrizRotacionPersonaje);
                }
                if (juego.personaje.derecha || juego.personaje.izquierda || juego.personaje.adelante || juego.personaje.atras || juego.personaje.ataque) {
                    juego.disparo.valorApretado(false);
                }
                if (MDL && !moviendo) {
                    ab.playAnimation("dire_cat:crun", true);
                    moviendo = true;
                }

            } else if (juego.personaje.derecha || juego.personaje.izquierda || juego.personaje.adelante || juego.personaje.atras || juego.personaje.ataque || cam1) {
                //Si se presiona una tecla se da valor a un delta de velocidad hacia adelante y un delta de Angulo
                float deltaVel = 0;
                float deltaAngulo = 0;

                if (derecha) {
                    if (MDL && !moviendo) {
                        ab.playAnimation("dire_cat:chturnr", true);
                        moviendo = true;
                    }
                    deltaAngulo = -0.05f;
                }
                if (juego.personaje.izquierda) {
                    if (MDL && !moviendo) {
                        ab.playAnimation("dire_cat:chturnl", true);
                        moviendo = true;
                    }
                    deltaAngulo = 0.05f;
                }
                if (juego.personaje.adelante) {
                    if (MDL && !moviendo) {
                        ab.playAnimation("dire_cat:crun", true);
                        moviendo = true;
                    }
                    deltaVel = 0.05f;
                }
                if (juego.personaje.atras) {
                    if (MDL && !moviendo) {
                        ab.playAnimation("dire_cat:cwalk", true);
                        moviendo = true;
                    }
                    deltaVel = -0.05f;
                }
                if (juego.personaje.ataque) {
                    if (MDL && !moviendo) {
                        ab.playAnimation("dire_cat:ca1slashr", true);
                        moviendo = true;
                    }
                }

                //Se calcula el control con respecto al suelo  (del objeto controlado).
                float distAlsuelo = radio;
                float subirBajarPersonaje = controlarAlturaSuelo(t3dPersonaje, juego.explorador, distAlsuelo);

                //Se crean un Transform3D con los micro-desplazamientos/rotaciones. 
                Transform3D t3dNueva = new Transform3D();
                t3dNueva.set(new Vector3d(0.0d, subirBajarPersonaje, deltaVel));
                t3dNueva.setRotation(new AxisAngle4f(0, 1f, 0, deltaAngulo));
                t3dPersonaje.mul(t3dNueva);

                //Se actualiza la posicion del personaje y de la matriz de rotación
                Vector3d posPersonaje = new Vector3d(0, 0, 0);
                t3dPersonaje.get(matrizRotacionPersonaje, posPersonaje);
                posiciones[0] = (float) posPersonaje.x;
                posiciones[1] = (float) posPersonaje.y;
                posiciones[2] = (float) posPersonaje.z;

                //SONAR:   se lanza desde el centro del personaje con dirección Sonar.  Se presentan los objetos encontrados (por sus nombres)
                Vector3d posSonar = new Vector3d(0, 0, 0);
                Transform3D t3dSonar = new Transform3D(matrizRotacionPersonaje, new Vector3f(0.0f, subirBajarPersonaje, deltaVel + 1f), 1f);
                copiat3dPersonaje.mul(t3dSonar);
                copiat3dPersonaje.get(posSonar);
                posSo = posSonar;
                posPj = new Vector3d(posPersonaje.x, posPersonaje.y, posPersonaje.z);
                Vector3d direccion = new Vector3d(posSonar.x - posPersonaje.x, posSonar.y - posPersonaje.y, posSonar.z - posPersonaje.z);
                dir = direccion;

                juego.explorador.setShapeRay(new Point3d(posPersonaje.x, posPersonaje.y, posPersonaje.z), direccion);
                PickResult objMasCercano = juego.explorador.pickClosest();
                if (objMasCercano != null) {
                    Node nd = objMasCercano.getObject();
                    //System.out.println("A la vista está  " + nd.getUserData());
                } else {
                    // System.out.println("....nadie al frente");
                }
            }

        }
    }

    void mostrar() {
        //Se crea un transform3D con posiciones y matriz de rotacion actualizadas. Se usa para la transformacion (es decir presentación)
        Transform3D inip = new Transform3D(matrizRotacionPersonaje, new Vector3f(posiciones[0], posiciones[1], posiciones[2]), 1f);
        desplazamientoFigura.setTransform(inip);
    }

    float controlarAlturaSuelo(Transform3D t3dPersonaje, PickTool localizador, float objAlSuelo) {
        Vector3d posicionActual = new Vector3d(0, 0, 0);
        t3dPersonaje.get(posicionActual);
        Point3d posActual = new Point3d(posicionActual.x, posicionActual.y, posicionActual.z);
        float subirBajarPersonaje = 0;
        localizador.setShapeRay(posActual, new Vector3d(posActual.x, 20, posActual.z));
        PickResult[] lista = localizador.pickAllSorted();
        boolean enc = false;
        if (lista != null) {
            for (PickResult objMasCercano : lista) {
                if ((objMasCercano != null) && (!objMasCercano.getObject().getUserData().equals("figura_" + identificador))) {
                    Node nd = objMasCercano.getObject();
                    float distanciaSuelo = (float) objMasCercano.getClosestIntersection(posActual).getDistance();
                    subirBajarPersonaje = objAlSuelo + distanciaSuelo;     //System.out.println("... distancia hacia arriba="+distanciaSuelo);
                    enc = true;
                    break;
                }
            }
        }
        if (!enc) {
            localizador.setShapeRay(posActual, new Vector3d(posActual.x, -20, posActual.z));
            lista = localizador.pickAllSorted();
            if (lista != null) {
                for (PickResult objMasCercano : lista) {
                    if ((objMasCercano != null) && (!objMasCercano.getObject().getUserData().equals("figura_" + identificador))) {
                        Node nd = objMasCercano.getObject();
                        float distanciaSuelo = (float) objMasCercano.getClosestIntersection(posActual).getDistance();
                        subirBajarPersonaje = objAlSuelo - distanciaSuelo;     //System.out.println("... distancia hacia abajo="+distanciaSuelo);
                        enc = true;
                        break;
                    }
                }
            }

        }
        return subirBajarPersonaje * 0.5f;
    }

    public boolean colisionEsferaConEsfera(Figura F2) {
        Vector3f x = new Vector3f(posiciones[0] - F2.posiciones[0], posiciones[1] - F2.posiciones[1], posiciones[2] - F2.posiciones[2]);
        double distanciaActual = x.length();
        //System.out.println("Distancia=" + distanciaActual);

        if (distanciaActual < 3) {

            return true;
        } else {
            return false;
        }
    }

    public boolean quemarse(Figura F2) {
        Vector3f x = new Vector3f(posiciones[0] - F2.posiciones[0], posiciones[1] - F2.posiciones[1], posiciones[2] - F2.posiciones[2]);
        double distanciaActual = x.length();
        if (distanciaActual < 5) {
            return true;
        } else {
            return false;
        }
    }
}
