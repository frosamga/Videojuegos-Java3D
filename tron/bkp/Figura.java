
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
    int identificador;
    ArrayList<Figura> listaObjetos;
    Navegador_Tema_3 juego;
    BranchGroup conjunto;
    Matrix3f matrizRotacionPersonaje = new Matrix3f();
    float deltaVel = 0;
    float distanciaRecorrida;

    Figura(float radio_, BranchGroup conjunto_, ArrayList<Figura> _listaObjetos, Navegador_Tema_3 juego_, String tipo) {
        juego = juego_;
        conjunto = conjunto_;
        radio = radio_;
        // CREAR UN BRANCHGROUP PARA AÑADIRLO A CONJUNTO, Y AÑADILE DESPLAZAMIENTO AL NUEVO.
        if(tipo.equals("estela"))
        {
        
        BranchGroup BGfigura = new BranchGroup();
        BGfigura.addChild(desplazamientoFigura);
        
        BGfigura.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        ColorCube estela=new ColorCube(radio);

        desplazamientoFigura.addChild(estela);
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        
        estela.setUserData("figura_" + identificador);
        /*
            estela.getShape(1).setUserData("figura_" + identificador);
            estela.getShape(2).setUserData("figura_" + identificador);
            estela.getShape(3).setUserData("figura_" + identificador);
            estela.getShape(4).setUserData("figura_" + identificador);
            estela.getShape(5).setUserData("figura_" + identificador);
*/
            PickTool.setCapabilities(estela, PickTool.INTERSECT_FULL);


            estela.setPickable(true);
    
        conjunto.addChild(BGfigura);
        
        }
        else
        {
        conjunto.addChild(desplazamientoFigura);
        //desplazamientoFigura.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        }
        
        
        
        
        listaObjetos = _listaObjetos;
        listaObjetos.add(this);
        identificador = listaObjetos.size() - 1;
        distanciaRecorrida=0;

        if (tipo.equals("suelo")) {
            // Create appearance object for textured cube
            Appearance app = new Appearance();
            Texture tex = new TextureLoader(juego.rutaCarpetaProyecto + "suelo2.jpg", juego).getTexture();
            app.setTexture(tex);
            TextureAttributes texAttr = new TextureAttributes();
            texAttr.setTextureMode(TextureAttributes.COMBINE);
            app.setTextureAttributes(texAttr);
            Box suelo = new Box(radio, 0.1f, radio, Box.GENERATE_TEXTURE_COORDS, app);
            suelo.getShape(0).setUserData("figura_" + identificador);
            suelo.getShape(1).setUserData("figura_" + identificador);
            suelo.getShape(2).setUserData("figura_" + identificador);
            suelo.getShape(3).setUserData("figura_" + identificador);
            suelo.getShape(4).setUserData("figura_" + identificador);
            suelo.getShape(5).setUserData("figura_" + identificador);

            PickTool.setCapabilities(suelo.getShape(0), PickTool.INTERSECT_FULL);
            PickTool.setCapabilities(suelo.getShape(1), PickTool.INTERSECT_FULL);
            PickTool.setCapabilities(suelo.getShape(2), PickTool.INTERSECT_FULL);
            PickTool.setCapabilities(suelo.getShape(3), PickTool.INTERSECT_FULL);
            PickTool.setCapabilities(suelo.getShape(4), PickTool.INTERSECT_FULL);
            PickTool.setCapabilities(suelo.getShape(5), PickTool.INTERSECT_FULL);

            suelo.getShape(0).setPickable(true);
            suelo.getShape(1).setPickable(true);
            suelo.getShape(2).setPickable(true);
            suelo.getShape(3).setPickable(true);
            suelo.getShape(4).setPickable(true);
            suelo.getShape(5).setPickable(true);
            desplazamientoFigura.addChild(suelo);



            
        } else if (tipo.equals("pared")) 
        {
            Appearance app = new Appearance();
            Texture tex = new TextureLoader(juego.rutaCarpetaProyecto + "grid.png", juego).getTexture();
            app.setTexture(tex);
            TextureAttributes texAttr = new TextureAttributes();
            texAttr.setTextureMode(TextureAttributes.MODULATE);
            app.setTextureAttributes(texAttr);
            float alto=2f;
            float ancho=radio;
            float largo=0.2f;
            if(identificador>4)
            {
                       ancho=0.2f;
                       largo=radio;          
            }

            Box pared = new Box(ancho, alto, largo, Box.GENERATE_TEXTURE_COORDS, app);
            pared.getShape(0).setUserData("figura_" + identificador);
            pared.getShape(1).setUserData("figura_" + identificador);
            pared.getShape(2).setUserData("figura_" + identificador);
            pared.getShape(3).setUserData("figura_" + identificador);
            pared.getShape(4).setUserData("figura_" + identificador);
            pared.getShape(5).setUserData("figura_" + identificador);

            PickTool.setCapabilities(pared.getShape(0), PickTool.INTERSECT_FULL);
            PickTool.setCapabilities(pared.getShape(1), PickTool.INTERSECT_FULL);
            PickTool.setCapabilities(pared.getShape(2), PickTool.INTERSECT_FULL);
            PickTool.setCapabilities(pared.getShape(3), PickTool.INTERSECT_FULL);
            PickTool.setCapabilities(pared.getShape(4), PickTool.INTERSECT_FULL);
            PickTool.setCapabilities(pared.getShape(5), PickTool.INTERSECT_FULL);

            pared.getShape(0).setPickable(true);
            pared.getShape(1).setPickable(true);
            pared.getShape(2).setPickable(true);
            pared.getShape(3).setPickable(true);
            pared.getShape(4).setPickable(true);
            pared.getShape(5).setPickable(true);
            desplazamientoFigura.addChild(pared);
            
        }
        else if (tipo.contains("moto"))
        {
            BranchGroup moto;


            ObjectFile file = new ObjectFile(ObjectFile.RESIZE);
            Scene scene = null;
            try {

                scene = file.load(juego.rutaCarpetaProyecto + "moto.obj");

            } catch (Exception e) {
                System.err.println(e);
                System.exit(1);
            }
            moto = scene.getSceneGroup();

            //Creando la rama de la moto1
            Transform3D scala = new Transform3D();
            scala.setScale(1);
            TransformGroup TGscala = new TransformGroup(scala);


            //Hacemos que el objeto sea localizable
            moto.getChild(0).setUserData("figura_" + identificador);
            PickTool.setCapabilities(moto.getChild(0), PickTool.INTERSECT_FULL);
            moto.getChild(0).setPickable(true);

            Transform3D rot = new Transform3D();
            rot.rotX(-Math.PI / 2f);
            TransformGroup rotador = new TransformGroup(rot);
            desplazamientoFigura.addChild(TGscala);
            TGscala.addChild(rotador);
            rotador.addChild(moto);

        }
    }

    void inicializar(float x, float y, float z) {
        //Dando valor inicial al array de posiciones. El ángulo se fija en 0. Se puede incluir como parametro.  Este método no transforma/muestra.
        posiciones[0] = x;
        posiciones[1] = y;
        posiciones[2] = z;
        float anguloInicial =0;

        if (this.identificador <=1) {
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
                deltaVel = 0.02f;
                //Si se presiona una tecla se da valor a un delta de velocidad hacia adelante y un delta de Angulo              
                float deltaAngulo = 0;

                if (this.derecha) {

                    deltaAngulo = (float) (-Math.PI / 2f);
                }
                if (this.izquierda) {

                    deltaAngulo = (float) (Math.PI / 2f);
                }
                if (this.adelante) {

                    deltaAngulo = 0.0f;
                }
                if (this.atras) {

                    deltaAngulo = (float) (-Math.PI);
                }
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
            
            
            //Crear estela
            float radioEstela=0.2f;
            distanciaRecorrida+=deltaVel;
            System.out.println("Distancia recorrida: "+distanciaRecorrida);
            if (distanciaRecorrida>radioEstela)
            {
                   System.out.println("Entra");
            Figura estela=new Figura   (radioEstela, conjunto, listaObjetos, juego,"estela");
            if(this.getDirection()=="N")
            {
            estela.inicializar (posiciones[0]-1,posiciones[1], posiciones[2]);
            }
            else  if(this.getDirection()=="S")
            {
            estela.inicializar (posiciones[0],posiciones[1], posiciones[2]);
            }
            else  if(this.getDirection()=="E")
            {
            estela.inicializar (posiciones[0],posiciones[1], posiciones[2]);
            }
            else
            {
                estela.inicializar (posiciones[0],posiciones[1], posiciones[2]);
            }
            
            distanciaRecorrida=0;
            }
            

            //SONAR:   se lanza desde el centro del personaje con dirección Sonar.  Se presentan los objetos encontrados (por sus nombres)
            Vector3d posSonar = new Vector3d(0, 0, 0);
            Transform3D t3dSonar = new Transform3D(matrizRotacionPersonaje, new Vector3f(0.0f, subirBajarPersonaje, deltaVel + 1f), 1f);
            copiat3dPersonaje.mul(t3dSonar);
            copiat3dPersonaje.get(posSonar);
            Vector3d direccion = new Vector3d(posSonar.x - posPersonaje.x, posSonar.y - posPersonaje.y, posSonar.z - posPersonaje.z);
            juego.explorador.setShapeRay(new Point3d(posPersonaje.x, posPersonaje.y, posPersonaje.z), direccion);
            PickResult[] lista = juego.explorador.pickAllSorted();
           
            if (lista != null) {
            for (PickResult objMasCercano : lista) {
                if ((objMasCercano != null) && (!objMasCercano.getObject().getUserData().equals("figura_" + identificador))) {
                    Node nd = objMasCercano.getObject();
                    //System.out.println("Objeto mas cercano de: "+identificador+" : "+nd.getUserData());
                    break;
                }
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

    public boolean colisionEsferaConEsfera(Figura F2) {   //
        Vector3f x = new Vector3f(posiciones[0] - F2.posiciones[0], posiciones[1] - F2.posiciones[1], posiciones[2] - F2.posiciones[2]);
        double distanciaActual = x.length();
        System.out.println("Distancia=" + (distanciaActual < (this.radio + F2.radio)));
        if (distanciaActual < (this.radio + F2.radio)) {
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
}
