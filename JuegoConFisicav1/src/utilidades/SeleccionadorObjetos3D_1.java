package utilidades;


import javax.media.j3d.*;
import com.sun.j3d.utils.picking.behaviors.*;
import com.sun.j3d.utils.picking.*;
import javax.vecmath.Point3d;
import simulador.Juego;

class SeleccionadorObjetos3D extends PickMouseBehavior {

    Juego juego;
    Point3d intercept;
    boolean apretado = false;

    public SeleccionadorObjetos3D(Canvas3D canvas, BranchGroup bg) {
        super(canvas, bg, new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        setSchedulingBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        pickCanvas.setMode(PickTool.GEOMETRY_INTERSECT_INFO);
    }

    public void updateScene(int xpos, int ypos) {
        pickCanvas.setShapeLocation(xpos, ypos);
        Point3d eyePos = pickCanvas.getStartPosition(); // get the viewer's eye location
        PickResult pickResult = null;
        pickResult = pickCanvas.pickClosest(); // get the intersected shape closest to the viewer
        if (pickResult != null) {
            Node nd = pickResult.getObject();
            if (nd instanceof Shape3D) {
                System.out.println("raton con clase =" + ((Shape3D) nd).getUserData());
            } else if (nd.getParent() instanceof Shape3D) {
                System.out.println("raton con claseO =" + ((Shape3D) nd.getParent()).getUserData());
            }
            PickIntersection pi = pickResult.getClosestIntersection(eyePos);
            intercept = pi.getPointCoordinatesVW(); // get the closest intersect to the eyePos point
            System.out.println(intercept); // extract the intersection pt in scene coords space use the intersection pt in some way...}
            apretado = true;
        }
    }

    public Point3d posicionRaton() {
        return intercept;
    }

    public boolean apretado() {
        return apretado;
    }
    public void valorApretado(boolean x){
        apretado=x;
    }
}
