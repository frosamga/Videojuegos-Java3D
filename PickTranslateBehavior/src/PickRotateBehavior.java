
import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.picking.PickingCallback;
import com.sun.j3d.utils.picking.PickResult;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;


public class PickRotateBehavior extends PickMouseBehavior implements MouseBehaviorCallback {

    MouseRotate drag;
    //	int pickMode = PickTool.BOUNDS;
    private PickingCallback callback = null;
    private TransformGroup currentTG;

    public PickRotateBehavior(BranchGroup root, Canvas3D canvas, Bounds bounds) {
        super(canvas, root, bounds);
        drag = new MouseRotate(MouseRotate.MANUAL_WAKEUP);
        drag.setTransformGroup(currGrp);
        currGrp.addChild(drag);
        drag.setSchedulingBounds(bounds);
        this.setSchedulingBounds(bounds);
    }

    public PickRotateBehavior(BranchGroup root, Canvas3D canvas, Bounds bounds,
            int pickMode) {
        super(canvas, root, bounds);
        drag = new MouseRotate(MouseRotate.MANUAL_WAKEUP);
        drag.setTransformGroup(currGrp);
        currGrp.addChild(drag);
        drag.setSchedulingBounds(bounds);
        this.setSchedulingBounds(bounds);
        this.setMode(pickMode);
    }

    public void updateScene(int xpos, int ypos) {
        TransformGroup tg = null;

        if (!mevent.isMetaDown() && !mevent.isAltDown() && mevent.isShiftDown()) {

            pickCanvas.setShapeLocation(xpos, ypos);
            PickResult pr = pickCanvas.pickClosest();
            if ((pr != null)
                    && ((tg = (TransformGroup) pr.getNode(PickResult.TRANSFORM_GROUP))
                    != null)
                    && (tg.getCapability(TransformGroup.ALLOW_TRANSFORM_READ))
                    && (tg.getCapability(TransformGroup.ALLOW_TRANSFORM_WRITE))) {
                drag.setTransformGroup(tg);
                drag.wakeup();
                currentTG = tg;
                // free the PickResult
                // Need to clean up Issue 123 --- Chien        
                // freePickResult(pr);
            } else if (callback != null) {
                callback.transformChanged(PickingCallback.NO_PICK, null);
            }
        }
    }

    public void transformChanged(int type, Transform3D transform) {
        callback.transformChanged(PickingCallback.ROTATE, currentTG);
    }

    public void setupCallback(PickingCallback callback) {
        this.callback = callback;
        if (callback == null) {
            drag.setupCallback(null);
        } else {
            drag.setupCallback(this);
        }
    }

}