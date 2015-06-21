import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;
import java.util.ArrayList;
import javax.media.j3d.*;
import javax.vecmath.*;

public class Moto{
    float dt, radio, altura;
    float[] velocidades = new float[3];
    float[] posiciones = new float[3];
    public boolean adelante, atras, izquierda, derecha;
    public TransformGroup desplazamientoFigura = new TransformGroup();
    public TransformGroup scale = new TransformGroup();
    int identificador;
    ArrayList<Moto> listaObjetos;
    Navegador_Tema_3 juego;
    BranchGroup conjunto;
    Matrix3f matrizRotacionPersonaje = new Matrix3f();

Moto (float radio_, float altura_, BranchGroup conjunto_, ArrayList<Moto> _listaObjetos, Navegador_Tema_3 juego_){
        juego = juego_;
        conjunto=conjunto_;
        radio = radio_;
        altura = altura_;
        conjunto.addChild(desplazamientoFigura);
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        desplazamientoFigura.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        listaObjetos = _listaObjetos;
        listaObjetos.add(this);
        identificador = listaObjetos.size() - 1;
        float deltaVel=2; 
        
        BranchGroup moto;
        
        ObjectFile file = new ObjectFile (ObjectFile.RESIZE); 
        Scene scene = null;
        try {
            
                scene = file.load(juego.rutaCarpetaProyecto+"moto.obj");
 
        } catch (Exception e) 
        { 
            System.err.println(e);
            System.exit(1);
        }
        moto =  scene.getSceneGroup();
        
       //Creando la rama de la moto1
       Transform3D scala = new Transform3D();
       scala.setScale(5);
       TransformGroup TGmoto1 = new TransformGroup(scala);

          //Hacemos que el objeto sea localizable
       PickTool.setCapabilities(moto.getChild(0), PickTool.INTERSECT_FULL);
       moto.getChild(0).setPickable(true);
          
          Transform3D rot= new Transform3D();
          rot.rotZ(Math.PI/2f);
          TransformGroup rotador = new TransformGroup(rot);
          desplazamientoFigura.addChild(rotador);
          rotador.addChild( moto );
}

void inicializar(float x, float y, float z){

   //Dando valor inicial al array de posiciones. El ángulo se fija en 0. Se puede incluir como parametro.  Este método no transforma/muestra.
   posiciones[0]=x;     posiciones[1]=y;      posiciones[2]=z;
   float anguloInicial = 0;
   matrizRotacionPersonaje.set( new AxisAngle4d(0, 1, 0,  anguloInicial) );
}


public void actualizar(float dt) {
     //Se consulta el Transform3D actual y una copia porque se necesitarán para varias operaciones de actualización
     Transform3D t3dPersonaje = new Transform3D();
     desplazamientoFigura.getTransform(t3dPersonaje);
     Transform3D copiat3dPersonaje = new Transform3D(t3dPersonaje);
     

     if (identificador!=0)
        for (int p = 0; p < 3; p++){
             posiciones[p] = posiciones[p]+ dt*velocidades[p];
             t3dPersonaje.get(matrizRotacionPersonaje);
     }
    else{
         
      if (this.derecha || this.izquierda || this.adelante || this.atras ){
         //Si se presiona una tecla se da valor a un delta de velocidad hacia adelante y un delta de Angulo
         float deltaVel=0; float deltaAngulo=0;
         
          if (juego.moto1.derecha) deltaAngulo = (float) (Math.PI/2f);
          if (juego.moto1.izquierda) deltaAngulo = (float) (-Math.PI/2f);
          if (juego.moto1.adelante) deltaVel = 0.0f;
          if (juego.moto1.atras) deltaVel = -0.05f;

        

          //Se calcula el control con respecto al suelo  (del objeto controlado).
          float distAlsuelo = radio;
          float subirBajarPersonaje = controlarAlturaSuelo(t3dPersonaje, juego.explorador, distAlsuelo);

         //Se crean un Transform3D con los micro-desplazamientos/rotaciones. 
          Transform3D t3dNueva         = new Transform3D();
          t3dNueva.set         (new Vector3d(0.0d, subirBajarPersonaje, deltaVel));
          t3dPersonaje.setRotation(new AxisAngle4f(0, 1f,0,deltaAngulo));
          t3dPersonaje.mul( t3dNueva );

          //Se actualiza la posicion del personaje y de la matriz de rotación
          Vector3d posPersonaje = new Vector3d(0,0,0);
          t3dPersonaje.get(matrizRotacionPersonaje, posPersonaje);
          posiciones[0] = (float) posPersonaje.x;
          posiciones[1] = (float) posPersonaje.y;
          posiciones[2] = (float) posPersonaje.z;

          //SONAR:   se lanza desde el centro del personaje con dirección Sonar.  Se presentan los objetos encontrados (por sus nombres)
          Vector3d posSonar      = new Vector3d(0,0,0);
          Transform3D t3dSonar = new Transform3D( matrizRotacionPersonaje,  new Vector3f(0.0f, subirBajarPersonaje, deltaVel+1f), 1f);
          copiat3dPersonaje      .mul( t3dSonar );
          copiat3dPersonaje.get(posSonar);
          Vector3d direccion = new Vector3d(posSonar.x - posPersonaje.x,  posSonar.y - posPersonaje.y,  posSonar.z - posPersonaje.z);
          juego.explorador.setShapeRay(new Point3d(posPersonaje.x, posPersonaje.y, posPersonaje.z),  direccion);
          PickResult  objMasCercano = juego.explorador.pickClosest();
          if (objMasCercano != null){
               Node nd = objMasCercano.getObject();
               System.out.println("A la vista está  "+nd.getUserData());
           } else System.out.println("....nadie al frente");
   }
 }
}

 void mostrar() {
        //Se crea un transform3D con posiciones y matriz de rotacion actualizadas. Se usa para la transformacion (es decir presentación)
        Transform3D inip = new Transform3D( matrizRotacionPersonaje, new Vector3f(posiciones[0], posiciones[1], posiciones[2]),  1f );
        desplazamientoFigura.setTransform(inip);
    }

 float controlarAlturaSuelo(Transform3D t3dPersonaje, PickTool localizador, float objAlSuelo ){
           Vector3d posicionActual = new Vector3d(0,0,0);
           t3dPersonaje.get(posicionActual);
           Point3d posActual = new Point3d(posicionActual.x, posicionActual.y, posicionActual.z) ;
           float subirBajarPersonaje=0;
           localizador.setShapeRay(posActual, new Vector3d(posActual.x,20, posActual.z));
           PickResult[] lista = localizador.pickAllSorted();
           boolean enc=false;
           if (lista!=null)
               for (PickResult objMasCercano : lista ){
                 if ((objMasCercano != null ) &&   (!objMasCercano.getObject().getUserData().equals("figura_"+identificador))  ){
                    Node nd = objMasCercano.getObject();
                    float distanciaSuelo = (float)objMasCercano.getClosestIntersection(posActual).getDistance();
                    subirBajarPersonaje = objAlSuelo + distanciaSuelo;     //System.out.println("... distancia hacia arriba="+distanciaSuelo);
                   enc=true;     break;
           }}
           if (!enc){
               localizador.setShapeRay(posActual, new Vector3d(posActual.x,-20,posActual.z));
                lista = localizador.pickAllSorted();
                if (lista!=null)
                    for (PickResult objMasCercano : lista ){
                       if ((objMasCercano != null) &&   (!objMasCercano.getObject().getUserData().equals("figura_"+identificador))){
                         Node nd = objMasCercano.getObject();
                         float distanciaSuelo = (float)objMasCercano.getClosestIntersection(posActual).getDistance();
                         subirBajarPersonaje = objAlSuelo - distanciaSuelo;     //System.out.println("... distancia hacia abajo="+distanciaSuelo);
                         enc=true;       break;
              }}}
           return subirBajarPersonaje*0.5f;
   }

 public boolean colisionEsferaConEsfera(Moto F2) {   //
             Vector3f x= new Vector3f(posiciones[0] - F2.posiciones[0],  posiciones[1] - F2.posiciones[1], posiciones[2] - F2.posiciones[2]);
            double distanciaActual = x.length();
            System.out.println("Distancia="+(distanciaActual < (this.radio + F2.radio)));
            if (distanciaActual < (this.radio + F2.radio)) return true;
             else                 return false;
   }
}
