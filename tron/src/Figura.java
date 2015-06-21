
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.image.TextureLoader;

import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;

import java.util.ArrayList;
import javax.media.j3d.*;
import javax.vecmath.*;

public class Figura {

    float dt, radio, altura;
    float[] velocidades = new float[3];
    float[] posiciones = new float[3];
    public boolean adelante, atras, izquierda, derecha;
    public TransformGroup desplazamientoFigura = new TransformGroup();
    Shape3D personaje;
    int identificador;
    ArrayList<Figura> listaObjetos;
    Navegador_Tema_3 juego;
    BranchGroup conjunto;
    Matrix3f matrizRotacionPersonaje = new Matrix3f();
    float deltaVel = 0;
    float distanciaRecorrida = 0;
    Color3f colorEstela1 = new Color3f(1.0f, 0.0f, 0.0f);
    Color3f colorEstela2 = new Color3f(0.0f, 0.0f, 1.0f);
    boolean muerto = false;
    boolean manejable = true;
    float posibilidadGiro = 1f;
    float radioEstela = 0.2f;

    Figura(float radio_, BranchGroup conjunto_, ArrayList<Figura> _listaObjetos, Navegador_Tema_3 juego_, String tipo) {
        juego = juego_;
        conjunto = conjunto_;
        radio = radio_;

        listaObjetos = _listaObjetos;
        listaObjetos.add(this);
        identificador = listaObjetos.size() - 1;
        distanciaRecorrida = 0;

        
        if (tipo.contains("estela")) {

            crearEstela(tipo,"estela.jpg");
           

        } else {
            conjunto.addChild(desplazamientoFigura);
            
            desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        }


        if (tipo.equals("suelo")) {
            crearSuelo("suelo2.jpg");

        } else if (tipo.contains("pared")) {

            crearPared(tipo, "wall.jpg");

        } else if (tipo.contains("moto")) {
            
            crearMoto(tipo,"moto.obj");
           
        }
    }

    void inicializar(float x, float y, float z) {
        //Dando valor inicial al array de posiciones. El ángulo se fija en 0. Se puede incluir como parametro.  Este método no transforma/muestra.
        posiciones[0] = x;
        posiciones[1] = y;
        posiciones[2] = z;
        float anguloInicial = 0;

        if (this.identificador <= 1) {
            adelante = true;
            anguloInicial = (float) Math.PI / 2f;
        }

        matrizRotacionPersonaje.set(new AxisAngle4d(0, 1, 0, anguloInicial));
    }

    public void actualizar(float dt) {
        //Se consulta el Transform3D actual y una copia porque se necesitarán para varias operaciones de actualización
        Transform3D t3dPersonaje = new Transform3D();
        desplazamientoFigura.getTransform(t3dPersonaje);
        Transform3D copiat3dPersonaje = new Transform3D(t3dPersonaje);

        if (identificador > 1) {
            for (int p = 0; p < 3; p++) {
                posiciones[p] = posiciones[p] + dt * velocidades[p];
                t3dPersonaje.get(matrizRotacionPersonaje);

            }
        } else {

            if (this.derecha || this.izquierda || this.adelante || this.atras) {

                //Si se presiona una tecla se da valor a un delta de velocidad hacia adelante y un delta de Angulo              
                float deltaAngulo = obtenerAngulo();

                t3dPersonaje.setRotation(new AxisAngle4f(0, 1f, 0, deltaAngulo));
            }



            //Se calcula el control con respecto al suelo  (del objeto controlado).
            float distAlsuelo = 0.1f;
            float subirBajarPersonaje = controlarAlturaSuelo(t3dPersonaje, juego.explorador, distAlsuelo);

            //Se crean un Transform3D con los micro-desplazamientos/rotaciones. 
            Transform3D t3dNueva = new Transform3D();
            t3dNueva.set(new Vector3d(deltaVel, subirBajarPersonaje, 0.0d));

            t3dPersonaje.mul(t3dNueva);



            //Se actualiza la posicion del personaje y de la matriz de rotación
            Vector3d posPersonaje = new Vector3d(0, 0, 0);
            t3dPersonaje.get(matrizRotacionPersonaje, posPersonaje);
            posiciones[0] = (float) posPersonaje.x;
            posiciones[1] = (float) posPersonaje.y;
            posiciones[2] = (float) posPersonaje.z;


            //Comprobamos si hay suficiente distancia para generar un nuevo cubo de la estela

            distanciaRecorrida += deltaVel;

            if (distanciaRecorrida > 2 * radioEstela) {

                generarEstela(posiciones[0], posiciones[1], posiciones[2]);

                //Se intenta simular inteligencia para el control del ordenador

                if (!manejable) {

                    posibilidadGiro += 0.3f;
                    float intento = (float) Math.random() * 100;

                    if (intento < posibilidadGiro) {
                        
                        giroAleatorio();
                        posibilidadGiro = 1;


                    }

                    //Utilizamos un Sonar para buscar el objeto mas cercano e intentar evitar la colision
                    Vector3d posSonar = new Vector3d(0, 0, 0);
                    Transform3D t3dSonar = new Transform3D(matrizRotacionPersonaje, new Vector3f(0.0f, subirBajarPersonaje, deltaVel + 1f), 1f);
                    copiat3dPersonaje.mul(t3dSonar);
                    copiat3dPersonaje.get(posSonar);
                    Vector3d direccion = new Vector3d(posSonar.x - posPersonaje.x, posSonar.y + 0.5f, posSonar.z - posPersonaje.z);
                    juego.explorador.setShapeRay(new Point3d(posPersonaje.x, posPersonaje.y, posPersonaje.z), direccion);
                    PickResult objMasCercano = juego.explorador.pickClosest();

                    if (objMasCercano != null) {
                        Node nd = objMasCercano.getObject();

                        Point3d posActual = new Point3d(posPersonaje.x, posPersonaje.y, posPersonaje.z);

                        float distancia = (float) objMasCercano.getClosestIntersection(posActual).getDistance();

                        if (distancia < 2) {
                            System.out.println("Distancia: " + distancia + " a " + nd.getUserData());

                            intento = (float) Math.random() * 100;

                            if (intento < posibilidadGiro + 30) {
                               
                                giroAleatorio();
                                posibilidadGiro = 1;

                            }
                        }

                       
                    }

                }
                distanciaRecorrida -= 2 * radioEstela; //Reseteamos la distancia recorrida
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
                if ((objMasCercano != null) && (objMasCercano.getObject().getUserData().toString().contains("suelo"))) {
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
                    if ((objMasCercano != null) && (objMasCercano.getObject().getUserData().toString().contains("suelo"))) {
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

    public boolean colision(Figura F2) {   //
        if (F2.identificador == 2 || F2.identificador == this.identificador) {
            return false;
        }
        Vector3f x = new Vector3f(posiciones[0] - F2.posiciones[0], posiciones[1] - F2.posiciones[1], posiciones[2] - F2.posiciones[2]);
        double distanciaActual = x.length();
        //System.out.print("Distancia=" +distanciaActual+"  :  "+(this.radio + F2.radio));
        if (distanciaActual < 1) { //(this.radio + F2.radio)
            System.out.println("   " + F2.identificador);
            return true;
        } else {
            return false;
        }
    }

    public String getDirection() {

        String res = "N";
        if (this.adelante) {
            res = "N";
        } else if (this.atras) {
            res = "S";
        } else if (this.derecha) {
            res = "E";
        } else {
            res = "W";
        }
        return res;
    }

    public float obtenerAngulo() {
        float angulo = 0;
        if (this.derecha) {

            angulo = (float) (-Math.PI / 2f);
        }
        if (this.izquierda) {

            angulo = (float) (Math.PI / 2f);
        }
        if (this.adelante) {

            angulo = 0.0f;
        }
        if (this.atras) {

            angulo = (float) (-Math.PI);
        }
        return angulo;

    }

    public void crearSuelo(String textura) {

        Appearance app = new Appearance();
        Texture tex = new TextureLoader(juego.rutaCarpetaProyecto + textura, juego).getTexture();
        app.setTexture(tex);
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.COMBINE);
        app.setTextureAttributes(texAttr);

        Box suelo = new Box(radio, 0.1f, radio, Box.GENERATE_TEXTURE_COORDS, app);

        suelo.getShape(0).setUserData("suelo_" + identificador);
        suelo.getShape(1).setUserData("suelo_" + identificador);
        suelo.getShape(2).setUserData("suelo_" + identificador);
        suelo.getShape(3).setUserData("suelo_" + identificador);
        suelo.getShape(4).setUserData("suelo_" + identificador);
        suelo.getShape(5).setUserData("suelo_" + identificador);

        PickTool.setCapabilities(suelo.getShape(0), PickTool.INTERSECT_FULL);
        PickTool.setCapabilities(suelo.getShape(1), PickTool.INTERSECT_FULL);
        PickTool.setCapabilities(suelo.getShape(2), PickTool.INTERSECT_FULL);
        PickTool.setCapabilities(suelo.getShape(3), PickTool.INTERSECT_FULL);
        PickTool.setCapabilities(suelo.getShape(4), PickTool.INTERSECT_FULL);
        PickTool.setCapabilities(suelo.getShape(5), PickTool.INTERSECT_FULL);

        desplazamientoFigura.addChild(suelo);
    }

    public void crearPared(String tipo, String textura) {
        Appearance app = new Appearance();
        Texture tex = new TextureLoader(juego.rutaCarpetaProyecto + textura, juego).getTexture();
        tex.setBoundaryModeS(Texture.WRAP);
        tex.setBoundaryModeT(Texture.WRAP);
        app.setTexture(tex);
        TextureAttributes texAttr = new TextureAttributes();
       
        tex.setBoundaryModeS(Texture.WRAP);
        tex.setBoundaryModeT(Texture.WRAP);

        texAttr.setTextureMode(TextureAttributes.MODULATE);
        app.setTextureAttributes(texAttr);
        float alto = 2f;
        float ancho = radio;
        float largo = 0.2f;

        if (tipo.equals("paredE") || tipo.equals("paredO")) {
            ancho = 0.2f;
            largo = radio;
        }

        Box pared = new Box(ancho, alto, largo,Box.GENERATE_TEXTURE_COORDS, app);
        
        pared.getShape(0).setUserData(tipo + "_" + identificador);
        pared.getShape(1).setUserData(tipo + "_" + identificador);
        pared.getShape(2).setUserData(tipo + "_" + identificador);
        pared.getShape(3).setUserData(tipo + "_" + identificador);
        pared.getShape(4).setUserData(tipo + "_" + identificador);
        pared.getShape(5).setUserData(tipo + "_" + identificador);

        PickTool.setCapabilities(pared.getShape(0), PickTool.INTERSECT_FULL);
        PickTool.setCapabilities(pared.getShape(1), PickTool.INTERSECT_FULL);
        PickTool.setCapabilities(pared.getShape(2), PickTool.INTERSECT_FULL);
        PickTool.setCapabilities(pared.getShape(3), PickTool.INTERSECT_FULL);
        PickTool.setCapabilities(pared.getShape(4), PickTool.INTERSECT_FULL);
        PickTool.setCapabilities(pared.getShape(5), PickTool.INTERSECT_FULL);

        desplazamientoFigura.addChild(pared);
    }

    public void crearEstela(String tipo,String textura)
    {
        BranchGroup BGfigura = new BranchGroup();

            BGfigura.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);

            Appearance app = new Appearance();
            //Texture tex = new TextureLoader(juego.rutaCarpetaProyecto +textura, juego).getTexture();
            //app.setTexture(tex);
            //TextureAttributes texAttr = new TextureAttributes();
            //texAttr.setTextureMode(TextureAttributes.COMBINE);
            //app.setTextureAttributes(texAttr);
            if (tipo.equals("estela1")) {
                app.setColoringAttributes(new ColoringAttributes(colorEstela1, ColoringAttributes.FASTEST));
            } else {
                app.setColoringAttributes(new ColoringAttributes(colorEstela2, ColoringAttributes.FASTEST));
            }
            Box estela = new Box(radio, 0.1f, radio, app);


            desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

            estela.getShape(0).setUserData(tipo + "_" + identificador);
            estela.getShape(1).setUserData(tipo + "_" + identificador);
            estela.getShape(2).setUserData(tipo + "_" + identificador);
            estela.getShape(3).setUserData(tipo + "_" + identificador);
            estela.getShape(4).setUserData(tipo + "_" + identificador);
            estela.getShape(5).setUserData(tipo + "_" + identificador);



            estela.getShape(0).setPickable(true);
            estela.getShape(1).setPickable(true);
            estela.getShape(2).setPickable(true);
            estela.getShape(3).setPickable(true);
            estela.getShape(4).setPickable(true);
            estela.getShape(5).setPickable(true);

            desplazamientoFigura.addChild(estela);
            BGfigura.addChild(desplazamientoFigura);
            conjunto.addChild(BGfigura);
    }
    
    public void crearMoto(String tipo,String textura)
    {
        BranchGroup moto;

            deltaVel = 0.05f;
            ObjectFile file = new ObjectFile(ObjectFile.RESIZE);
            Scene scene = null;
            try {

                scene = file.load(juego.rutaCarpetaProyecto + textura);

            } catch (Exception e) {
                System.err.println(e);
                System.exit(1);
            }
            moto = scene.getSceneGroup();
            

            //Creando la rama de la moto
            Transform3D scala = new Transform3D();
            scala.setScale(1);
            TransformGroup TGscala = new TransformGroup(scala);

            //SI estamos en modo de un jugador, activamos el control del ordenador
            System.out.println("nJugadores " + juego.nJugadores + "    " + identificador);
            if (juego.nJugadores == 1 && identificador == 1) {

                manejable = false;
            }

            //Hacemos que el objeto sea localizable
            moto.getChild(0).setUserData(tipo + "_" + identificador);
            PickTool.setCapabilities(moto.getChild(0), PickTool.INTERSECT_FULL);
            moto.getChild(0).setPickable(true);
            personaje = (Shape3D) moto.getChild(0);
            Transform3D rot = new Transform3D();
            rot.rotX(-Math.PI / 2f);
            TransformGroup rotador = new TransformGroup(rot);
            desplazamientoFigura.addChild(TGscala);
            TGscala.addChild(rotador);
            rotador.addChild(moto);
    }
    
    public void generarEstela(float posX, float posY, float posZ) {

        if (posY <= -0.05f) {
            Figura estela = new Figura(radioEstela, conjunto, listaObjetos, juego, "estela" + identificador);

            if (this.getDirection().equals("N")) {
                estela.inicializar(posX - 1.3f, posY, posZ);
            } else if (this.getDirection().equals("S")) {
                estela.inicializar(posX + 1.3f, posY, posZ);
            } else if (this.getDirection().equals("E")) {
                estela.inicializar(posX, posY, posZ - 1.3f);
            } else {
                estela.inicializar(posX, posY, posZ + 1.3f);
            }
        }
    }

    public void giroAleatorio() {
        distanciaRecorrida -= 2.3f;
        boolean giroIzquierda = false;
        if ((Math.random() * 10) < 5) {
            giroIzquierda = !giroIzquierda;
        }
        if (this.getDirection() == "N") {
            adelante = false;
            if (giroIzquierda) {
                izquierda = true;
            } else {
                derecha = true;
            }
        } else if (this.getDirection() == "S") {
            atras = false;
            if (giroIzquierda) {
                derecha = true;
            } else {
                izquierda = true;
            }
        } else if (this.getDirection() == "E") {
            derecha = false;
            if (giroIzquierda) {
                adelante = true;
            } else {
                atras = true;
            }
        } else {
            izquierda = false;
            if (giroIzquierda) {
                atras = true;
            } else {
                adelante = true;
            }
        }
    }
}
