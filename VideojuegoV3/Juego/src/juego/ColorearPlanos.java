package juego;

import javax.media.j3d.*;
import javax.vecmath.*;

public class ColorearPlanos extends Shape3D {

    private static final int NUM_VERTS = 4;

    public ColorearPlanos(Point3d p1, Point3d p2, Point3d p3, Point3d p4, Vector3f normVec, Color3f col) {
        crearGeometria(p1, p2, p3, p4, normVec);
        crearApariencia(col);
    }

    private void crearGeometria(Point3d p1, Point3d p2, Point3d p3, Point3d p4, Vector3f normVec) {
        QuadArray plano = new QuadArray(NUM_VERTS, GeometryArray.COORDINATES | GeometryArray.NORMALS);

         plano.setCoordinate(0, p1);
        plano.setCoordinate(1, p2);
        plano.setCoordinate(2, p3);
        plano.setCoordinate(3, p4);

        Vector3f[] norms = new Vector3f[NUM_VERTS];
        for (int i = 0; i < NUM_VERTS; i++) {
            norms[i] = normVec;
        }
        plano.setNormals(0, norms);
        setGeometry(plano);
    }

    private void crearApariencia(Color3f col) {
        Appearance app = new Appearance();
        Material mat = new Material();
        mat.setDiffuseColor(col);
        mat.setLightingEnable(true);
        app.setMaterial(mat);
        setAppearance(app);
    }
}
