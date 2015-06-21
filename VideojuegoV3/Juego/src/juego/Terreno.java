package juego;

import java.util.*;

import com.sun.j3d.utils.geometry.*;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.picking.*;

public class Terreno {

    public static final int WORLD_LEN = 32;
    public static final int NUM_TEXTURAS = 5;
    public static final String textureFns[] = {"water-shallow.jpg", "sand.jpg", "grass.gif",
        "dryEarth.jpg", "stone.gif"};
    private final static double MIN_ALTURA = -2.0;
    private final static double MAX_ALTURA = 8.0;
    //direccion para el picking
    private final static Vector3d DOWN_VEC = new Vector3d(0.0, -1.0, 0.0);
    private BranchGroup tierraBG, sueloBG;
    private Point3d vertices[];
    private double textureBoundaries[];
    private Vector3d originVec = new Vector3d();
    private boolean encontrarOrigen = false;
    public PickTool picker;

    public Terreno(double flatness) {
        tierraBG = new BranchGroup();
        sueloBG = new BranchGroup();
        //tierraBG-->sueloBG
        tierraBG.addChild(sueloBG);

        ColocarTexturas();

        //El sonar del personaje deberia detectar esto
        picker = new PickTool(sueloBG);
        picker.setMode(PickTool.GEOMETRY_INTERSECT_INFO);

        Malla fm = new Malla(flatness);
        vertices = fm.getVertices();

        colocarTerreno();
        añadirParedes();
    }

    private void ColocarTexturas() {
        textureBoundaries = new double[NUM_TEXTURAS];
        double numeroVeces = (MAX_ALTURA - MIN_ALTURA) / NUM_TEXTURAS;
        double veces = MIN_ALTURA + numeroVeces;
        for (int j = 0; j < NUM_TEXTURAS; j++) {
            textureBoundaries[j] = veces;
            veces += numeroVeces;
        }
    }

    private void colocarTerreno() {
        ArrayList[] coordsList = new ArrayList[NUM_TEXTURAS];
        for (int i = 0; i < NUM_TEXTURAS; i++) {
            coordsList[i] = new ArrayList();
        }
        int AlturaIdx;
        for (int j = 0; j < vertices.length; j = j + 4) {
            AlturaIdx = encontrarAlturaIdx(j);
            añadirCoordenadas(coordsList[AlturaIdx], j);
            chequearOrigen(j);
        }
        Random r = new Random();
        int valor = r.nextInt(5);

        for (int i = 0; i < NUM_TEXTURAS; i++) {
            if (coordsList[i].size() > 0) {
                sueloBG.addChild(new TexturasPlanos(coordsList[i],
                        "images/" + textureFns[valor]));
            }
        }
    }

    private int encontrarAlturaIdx(int vertIndex) {
        double ah = avgAltura(vertIndex);
        for (int i = 0; i < textureBoundaries.length; i++) {
            if (ah < textureBoundaries[i]) {
                return i;
            }
        }
        return NUM_TEXTURAS - 1;
    }

    private double avgAltura(int vi) {
        return (vertices[vi].y + vertices[vi + 1].y
                + vertices[vi + 2].y + vertices[vi + 3].y) / 4.0;
    }

    private void añadirCoordenadas(ArrayList coords, int vi) {
        coords.add(vertices[vi]);
        coords.add(vertices[vi + 1]);
        coords.add(vertices[vi + 2]);
        coords.add(vertices[vi + 3]);
    }

    private void chequearOrigen(int vi) {
        if (!encontrarOrigen) {
            if ((vertices[vi].x == 0.0) && (vertices[vi].z == 0.0)) {

                originVec.y = vertices[vi].y;
                encontrarOrigen = true;
            }
        }
    }

    private void añadirParedes() {
        Color3f eveningBlue = new Color3f(0.17f, 0.07f, 0.45f);

        Point3d p1 = new Point3d(-WORLD_LEN / 2.0f, MIN_ALTURA, -WORLD_LEN / 2.0f);
        Point3d p2 = new Point3d(-WORLD_LEN / 2.0f, MAX_ALTURA, -WORLD_LEN / 2.0f);

        Point3d p3 = new Point3d(-WORLD_LEN / 2.0f, MIN_ALTURA, WORLD_LEN / 2.0f);
        Point3d p4 = new Point3d(-WORLD_LEN / 2.0f, MAX_ALTURA, WORLD_LEN / 2.0f);


        Point3d p5 = new Point3d(WORLD_LEN / 2.0f, MIN_ALTURA, WORLD_LEN / 2.0f);
        Point3d p6 = new Point3d(WORLD_LEN / 2.0f, MAX_ALTURA, WORLD_LEN / 2.0f);


        Point3d p7 = new Point3d(WORLD_LEN / 2.0f, MIN_ALTURA, -WORLD_LEN / 2.0f);
        Point3d p8 = new Point3d(WORLD_LEN / 2.0f, MAX_ALTURA, -WORLD_LEN / 2.0f);


        tierraBG.addChild(new ColorearPlanos(p3, p1, p2, p4,
                new Vector3f(-1, 0, 0), eveningBlue));

        tierraBG.addChild(new ColorearPlanos(p5, p3, p4, p6,
                new Vector3f(0, 0, -1), eveningBlue));

        tierraBG.addChild(new ColorearPlanos(p7, p5, p6, p8,
                new Vector3f(-1, 0, 0), eveningBlue));

        tierraBG.addChild(new ColorearPlanos(p7, p8, p2, p1,
                new Vector3f(0, 0, 1), eveningBlue));
    }

    public BranchGroup dameTerrenoBG() {
        return tierraBG;
    }

    //sirve para saber si algo en la posicion x,z se encuentra o no en el terreno
    public boolean enTerreno(double xPosn, double zPosn) {
        int x = (int) Math.round(xPosn);
        int z = (int) Math.round(zPosn);

        if ((x <= -WORLD_LEN / 2) || (x >= WORLD_LEN / 2)
                || (z <= -WORLD_LEN / 2) || (z >= WORLD_LEN / 2)) {
            return false;
        }
        return true;
    }

    public Vector3d dameVectorOrigen() {
        return originVec;
    }

    //lanza un sonar hacia el punto (x,z) que choca con el terreno, saca el valor de y
    //y lo devuelve
    //este metodo es el que tengo que usar para poder controlar la altura con el suelo, veremos como lo implementamos
    public double dameAlturaTierra(double x, double z, double AlturaActual) {
        Point3d pickStart = new Point3d(x, MAX_ALTURA * 2, z);
        picker.setShapeRay(pickStart, DOWN_VEC);

        PickResult picked = picker.pickClosest();
        if (picked != null) {
            //por si no encuentra interseccion
            if (picked.numIntersections() != 0) {
                PickIntersection pi = picked.getIntersection(0);
                Point3d nextPt;
                try {
                    nextPt = pi.getPointCoordinates();
                } catch (Exception e) {
                    return AlturaActual;
                }
                return nextPt.y;
            }
        }
        return AlturaActual;
    }
}