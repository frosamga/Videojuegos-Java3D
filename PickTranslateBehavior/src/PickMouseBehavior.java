
import com.sun.j3d.utils.picking.PickCanvas;
import java.awt.AWTEvent;
import java.awt.Event;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOr;

public abstract class PickMouseBehavior extends Behavior {

    protected PickCanvas pickCanvas;
    protected WakeupCriterion[] conditions;
    protected WakeupOr wakeupCondition;
    protected boolean buttonPress = false;
    protected TransformGroup currGrp;
    protected static final boolean debug = false;
    protected MouseEvent mevent;

    public PickMouseBehavior(Canvas3D canvas, BranchGroup root, Bounds bounds) {
        super();
        currGrp = new TransformGroup();
        currGrp.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        currGrp.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        root.addChild(currGrp);
        pickCanvas = new PickCanvas(canvas, root);
    }

    public void setMode(int pickMode) {
        pickCanvas.setMode(pickMode);
    }

    public void setTolerance(float tolerance) {
        pickCanvas.setTolerance(tolerance);
    }

    public float getTolerance() {
        return pickCanvas.getTolerance();
    }

    public void initialize() {

        conditions = new WakeupCriterion[2];
        conditions[0] = new WakeupOnAWTEvent(Event.MOUSE_MOVE);
        conditions[1] = new WakeupOnAWTEvent(Event.MOUSE_DOWN);
        wakeupCondition = new WakeupOr(conditions);

        wakeupOn(wakeupCondition);
    }

    private void processMouseEvent(MouseEvent evt) {
        buttonPress = false;

        if (evt.getID() == MouseEvent.MOUSE_PRESSED
                | evt.getID() == MouseEvent.MOUSE_CLICKED) {
            buttonPress = true;
            return;
        } else if (evt.getID() == MouseEvent.MOUSE_MOVED) {
            // Process mouse move event
        }
    }

    /**
     *
     * @param criteria
     */
    @Override
    public void processStimulus(Enumeration criteria) {
        WakeupCriterion wakeup;
        AWTEvent[] evt = null;
        int xpos = 0, ypos = 0;

        while (criteria.hasMoreElements()) {
            wakeup = (WakeupCriterion) criteria.nextElement();
            if (wakeup instanceof WakeupOnAWTEvent) {
                evt = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
            }
        }

        if (evt[0] instanceof MouseEvent) {
            mevent = (MouseEvent) evt[0];

            if (debug) {
                System.out.println("got mouse event");
            }
            processMouseEvent((MouseEvent) evt[0]);
            xpos = mevent.getPoint().x;
            ypos = mevent.getPoint().y;
        }

        if (debug) {
            System.out.println("mouse position " + xpos + " " + ypos);
        }

        if (buttonPress) {
            updateScene(xpos, ypos);
        }
        wakeupOn(wakeupCondition);
    }

    public abstract void updateScene(int xpos, int ypos);
}
