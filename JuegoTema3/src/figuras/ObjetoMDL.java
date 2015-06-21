package figuras;

import utilidades.CapabilitiesMDL;
import simulador.*;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.shapes.*;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.geometry.Sphere;
import java.net.URL;
import java.util.ArrayList;
import javax.media.j3d.*;
import javax.vecmath.*;
import net.sf.nwn.loader.AnimationBehavior;
import net.sf.nwn.loader.NWNLoader;

public class ObjetoMDL extends Figura {

    public Scene escenaPersonaje1;
    public AnimationBehavior ab = null;
    public String nombreAnimacionCorriendo, nombreAnimacionCaminando, nombreAnimacionQuieto, nombreAnimacionLuchando;
    Vector3d direccion = new Vector3d(0, 0, 10);
    public float radio, alturaP, alturaDeOjos;
    boolean esPersonaje;
    public TransformGroup rotacionTotal;
    //añadir personajes y objetos no MDL
    public ObjetoMDL(String ficheroMDL, float radio, BranchGroup conjunto, ArrayList<Figura> listaObjetos, Juego juego, boolean esPersonaje) {
        super(conjunto, listaObjetos, juego);
        esMDL = true;
        this.esPersonaje = esPersonaje;
        //Creando una apariencia
        Appearance apariencia = new Appearance();
        this.radio = radio;
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        apariencia.setTextureAttributes(texAttr);

        //Creacion de la forma visual MDL
        //nombre = "figura_MDL_" + identificador;
        //Sphere figuraVisual = new Sphere(radio);
        TransformGroup figuraVisual = crearObjetoMDL(ficheroMDL, radio * 2);
        SphereShape figuraFisica = new SphereShape(radio);
        ramaFisica = new CollisionObject();
        ramaFisica.setCollisionShape(figuraFisica);
        ramaVisible.addChild(desplazamientoFigura);
        desplazamientoFigura.addChild(figuraVisual);
        rotacionTotal.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        //Creacion de detector de teclas asociado a este cono
        if (esPersonaje) {
            DeteccionControlPersonaje mueve = new DeteccionControlPersonaje(this);
            mueve.setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
            ramaVisible.addChild(mueve);
        }
    }

    TransformGroup crearObjetoMDL(String archivo, float multiplicadorEscala) {
        BranchGroup RamaMDL = new BranchGroup();
        float rotacionX = 0;
        float rotacionY = 0;
        float rotacionZ = 0;
        float escalaTamano = 1f;
        float desplazamientoY = 0;
        try {
            NWNLoader nwn2 = new NWNLoader();
            nwn2.enableModelCache(true);
            escenaPersonaje1 = nwn2.load(new URL("file://localhost/" + System.getProperty("user.dir") + "/" + archivo));
            RamaMDL = escenaPersonaje1.getSceneGroup();           
            //Recorrido por los- objetos para darle capacidades a sus Shapes3D
            CapabilitiesMDL.setCapabilities(RamaMDL, this.identificadorFigura);
            
            RamaMDL.setUserData("figra_" +identificadorFigura);    
            //Para cada Objeto MDL dar nombre las animaciones de la figura. Dar rotaciones a la figuraMDL (suelen venir giradas)
            ab = (AnimationBehavior) escenaPersonaje1.getNamedObjects().get("AnimationBehavior");
            if (archivo.equals("objetosMDL/Iron_Golem.mdl")) {
                nombreAnimacionCaminando = "iron_golem:cwalk";
                nombreAnimacionCorriendo = "iron_golem:cwalk";
                nombreAnimacionQuieto = "iron_golem:cpause1";
                rotacionX = -1.5f;
                rotacionY= 0f;
                rotacionZ = 3f;
                
                escalaTamano = 0.65f;
                desplazamientoY = -1f;
                alturaP = (float) 3f * escalaTamano;
                alturaDeOjos = alturaP;
            }
            if (archivo.equals("objetosMDL/Doomknight.mdl")) {
                nombreAnimacionCaminando = "Doomknight:crun";
                nombreAnimacionCorriendo = "Doomknight:cwalk";
                nombreAnimacionQuieto = "Doomknight:cpause1";
                rotacionX = -3f;
                
                rotacionZ = 3.14f;
                alturaP = 2f;
                escalaTamano = 0.8f;
                alturaDeOjos = 1.5f * escalaTamano;
            }
            if (archivo.equals("objetosMDL/Dire_Cat.mdl")) {
                nombreAnimacionCaminando = "dire_cat:crun";
                nombreAnimacionCorriendo = "dire_cat:cwalk";
                nombreAnimacionQuieto = "dire_cat:cpause1";
                rotacionX = -1.5f;
                rotacionZ = 3.14f;
                alturaP = 2f;
                escalaTamano = 1f;
                alturaDeOjos = alturaP * escalaTamano;
            }
            if (archivo.equals("objetosMDL/pixie.mdl")) {
                nombreAnimacionCaminando = "pixie:crun";
                nombreAnimacionCorriendo = "pixie:cwalk";
                nombreAnimacionQuieto = "pixie:cpause1";
                rotacionX = -1.5f;
                rotacionZ = 3.14f;
                alturaP = 1f;
                escalaTamano = 6.0f;
                desplazamientoY = -6.5f;

                alturaDeOjos = alturaP * escalaTamano;
            }
            if (archivo.equals("objetosMDL/Fire_Elemental.mdl")) {
                nombreAnimacionCaminando = "fire_elemental:crun";
                nombreAnimacionCorriendo = "fire_elemental:cwalk";
                nombreAnimacionQuieto = "fire_elemental:cpause1";
                rotacionX = -1.5f;
                rotacionZ = 3.14f;
                alturaP = 1f;
                escalaTamano = 1.0f;
                desplazamientoY = 0;//-6.5f;
            }
            ab.playAnimation(nombreAnimacionCaminando, true);
        } catch (Exception exc) {
            exc.printStackTrace();
            System.out.println("Error during load mdl");
        }

        //Ajustando rotacion inicial de la figura MLD y aplicando tamano
        Transform3D rotacionCombinada = new Transform3D();
        rotacionCombinada.set(new Vector3f(0, desplazamientoY, 0));
        Transform3D correcionTemp = new Transform3D();
        correcionTemp.rotX(rotacionX);
        rotacionCombinada.mul(correcionTemp);
        correcionTemp.rotZ(rotacionZ);
        rotacionCombinada.mul(correcionTemp);
        correcionTemp.rotY(rotacionY);
        rotacionCombinada.mul(correcionTemp);
        correcionTemp.setScale(escalaTamano * multiplicadorEscala);
        rotacionCombinada.mul(correcionTemp);
        
        TransformGroup rotadorDeFIguraMDL = new TransformGroup(rotacionCombinada);
        rotacionTotal = rotadorDeFIguraMDL;
        rotadorDeFIguraMDL.addChild(RamaMDL);
        return rotadorDeFIguraMDL;
    }
}
