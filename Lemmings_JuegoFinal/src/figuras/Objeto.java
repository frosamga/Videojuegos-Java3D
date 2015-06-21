package figuras;

import utilidades.CapabilitiesMDL;
import simulador.*;
import com.bulletphysics.collision.dispatch.*;
import com.bulletphysics.collision.shapes.*;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import java.awt.Container;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import javax.media.j3d.*;
import javax.vecmath.*;
import net.sf.nwn.loader.AnimationBehavior;
import net.sf.nwn.loader.NWNLoader;

public class Objeto extends Figura {

    public Scene escenaPersonaje1;
    AnimationBehavior ab = null;
    String nombreAnimacionCorriendo, nombreAnimacionCaminando, nombreAnimacionQuieto;
    Vector3d direccion = new Vector3d(0, 0, 10);
    float alturaP, alturaDeOjos;
    Cylinder figuraLimintesFisicos;
    boolean esPersonaje;
    int paratextura = Primitive.GENERATE_NORMALS + Primitive.GENERATE_TEXTURE_COORDS;

    //esfera
    public Objeto(float radio, String textura, BranchGroup conjunto, ArrayList<Figura> listaObjetosFisicos, Juego juego) {
        super(conjunto, listaObjetosFisicos, juego);   //Si se desea programar una clase Esfera, su constrctor tendr?a esta linea

        //Creando una apariencia
//        Appearance apariencia = new Appearance();
//        Texture tex = new TextureLoader(System.getProperty("user.dir") + "//" + textura, juego).getTexture();
//        apariencia.setTexture(tex);
//        TextureAttributes texAttr = new TextureAttributes();
//        texAttr.setTextureMode(TextureAttributes.MODULATE);
//        apariencia.setTextureAttributes(texAttr);

        Appearance apariencia = new Appearance();
//        textura = System.getProperty("user.dir") + File.separator + textura;
        TextureLoader loader = new TextureLoader( textura,"INTENSITY", new Container());
        Texture tex = loader.getTexture();
        apariencia.setTexture(tex);
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        apariencia.setTextureAttributes(texAttr);
        
        //Creacion de formas visuales y fisicas
        Sphere figuraVisual = new Sphere(radio, Sphere.GENERATE_TEXTURE_COORDS, 60, apariencia);
        SphereShape figuraFisica = new SphereShape(radio);
        ramaFisica = new CollisionObject();
        ramaFisica.setCollisionShape(figuraFisica);
        ramaVisible.addChild(desplazamientoFigura);
        desplazamientoFigura.addChild(figuraVisual);
        this.conjunto = conjunto;
    }

    //caja
    public Objeto(float x, float y, float z, String textura, BranchGroup conjunto, ArrayList<Figura> listaObjetosFisicos, Juego juego) {
        super(conjunto, listaObjetosFisicos, juego);

        //Creando una apariencia
//        Appearance apariencia = new Appearance();
//        Texture tex = new TextureLoader(System.getProperty("user.dir") + "//" + textura, juego).getTexture();
//        apariencia.setTexture(tex);
//        TextureAttributes texAttr = new TextureAttributes();
//        texAttr.setTextureMode(TextureAttributes.MODULATE);
//        apariencia.setTextureAttributes(texAttr);
        Appearance apariencia = new Appearance();
//        textura = System.getProperty("user.dir") + File.separator + textura;
        TextureLoader loader = new TextureLoader( textura,"INTENSITY", new Container());
        Texture tex = loader.getTexture();
        apariencia.setTexture(tex);
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        apariencia.setTextureAttributes(texAttr);


        //Creacion de formas visuales y fisicas
        Box figuraVisual = new Box(x, y, z, paratextura, apariencia);
        BoxShape figuraFisica = new BoxShape(new Vector3f(x, y, z));
        /*
         Transform3D rotacion = new Transform3D();
         Transform3D rot = new Transform3D();
         rot.rotZ(rotacionZ);
         rotacion.mul(rot);
         TransformGroup rotadorDeFigura = new TransformGroup(rotacion);        
         rotadorDeFigura.addChild(figuraVisual);
         */

        ramaFisica = new CollisionObject();
        ramaFisica.setCollisionShape(figuraFisica);
        ramaVisible.addChild(desplazamientoFigura);
        //añadir rotadorDeFigura
        desplazamientoFigura.addChild(figuraVisual);
        this.conjunto = conjunto;

    }
    //cilindro

    public Objeto(float radio, float altura, String textura, BranchGroup conjunto, ArrayList<Figura> listaObjetosFisicos, Juego juego) {
        super(conjunto, listaObjetosFisicos, juego);

        //Creando una apariencia
//        Appearance apariencia = new Appearance();
//        Texture tex = new TextureLoader(System.getProperty("user.dir") + "//" + textura, juego).getTexture();
//        apariencia.setTexture(tex);
//        TextureAttributes texAttr = new TextureAttributes();
//        texAttr.setTextureMode(TextureAttributes.MODULATE);
//        apariencia.setTextureAttributes(texAttr);
        
        Appearance apariencia = new Appearance();
//        textura = System.getProperty("user.dir") + File.separator + textura;
        TextureLoader loader = new TextureLoader( textura,"INTENSITY", new Container());
        Texture tex = loader.getTexture();
        apariencia.setTexture(tex);
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        apariencia.setTextureAttributes(texAttr);

        //Creacion de formas visuales y fisicas
        Cylinder figuraVisual = new Cylinder(radio, altura, apariencia);
        CylinderShape figuraFisica = new CylinderShape(new Vector3f(radio, altura, 0));
        ramaFisica = new CollisionObject();
        ramaFisica.setCollisionShape(figuraFisica);
        ramaVisible.addChild(desplazamientoFigura);
        desplazamientoFigura.addChild(figuraVisual);
        this.conjunto = conjunto;
    }
    //cono, el ultimo constructor ponerlo a nulo

    public Objeto(float radio, float altura, String textura, BranchGroup conjunto, ArrayList<Figura> listaObjetosFisicos, Juego juego, boolean cono) {
        super(conjunto, listaObjetosFisicos, juego);

//        //Creando una apariencia
//        Appearance apariencia = new Appearance();
//        Texture tex = new TextureLoader(System.getProperty("user.dir") + "//" + textura, juego).getTexture();
//        apariencia.setTexture(tex);
//        TextureAttributes texAttr = new TextureAttributes();
//        texAttr.setTextureMode(TextureAttributes.MODULATE);
//        apariencia.setTextureAttributes(texAttr);
        
        Appearance apariencia = new Appearance();
//        textura = System.getProperty("user.dir") + File.separator + textura;
        TextureLoader loader = new TextureLoader( textura,"INTENSITY", new Container());
        Texture tex = loader.getTexture();
        apariencia.setTexture(tex);
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        apariencia.setTextureAttributes(texAttr);

        //Creacion de formas visuales y fisicas
        Cone figuraVisual = new Cone(radio, altura, apariencia);
        ConeShape figuraFisica = new ConeShape(radio, altura);
        ramaFisica = new CollisionObject();
        ramaFisica.setCollisionShape(figuraFisica);
        ramaVisible.addChild(desplazamientoFigura);
        desplazamientoFigura.addChild(figuraVisual);
        this.conjunto = conjunto;
    }

}
