package net.jurka.arrow.tasks;

import net.jurka.arrow.Task;
import net.jurka.arrow.stats.Stats;
import net.jurka.arrow.util.Constant;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.GameObject;

import java.awt.*;

public class Chop extends Task {

    private Rectangle[] onScreenHuds;
    private GameObject lastTree;

    public Chop(MethodContext ctx, Stats stats) {
        super(ctx, stats);
        onScreenHuds = ctx.hud.getBounds();
    }

    @Override
    public boolean activate() {

        boolean tree = !ctx.objects.select().name("Tree", "Dead tree", "Evergreen").nearest().limit(3).isEmpty();

        return ctx.backpack.select().count() < Constant.MAX_ITEMS_INVETORY
                && tree
                && !ctx.players.local().isInMotion()
                && ctx.players.local().getAnimation() == -1;
    }

    public boolean isBlockedByHud(Point point) {
        for (Rectangle rectangle : onScreenHuds) {
            if (rectangle.contains(point)) {
                return true;
            }
        }

        return false;
    }

    private boolean isTreeFar(GameObject object) {
        return object.getLocation().distanceTo(ctx.players.local().getLocation()) >= Constant.TREE_DISTANCE;
    }

    private boolean chop(GameObject tree) {
        Point interactive = tree.getCenterPoint();

        if (tree != null && tree.equals(lastTree)) {
            stats.incrMissClicks();
            lastTree.getModel();
            interactive = lastTree.getNextPoint();
        }

        if (!isBlockedByHud(interactive)) {
            ctx.mouse.click(interactive, true);
            stats.incrClicks();
            lastTree = tree;
            return true;
        } else {

            System.out.println("Too bad the point is blocked !");
        }
        return false;
    }

    @Override
    public void execute() {

        stats.setStatus("Chop");
        GameObject tree = ctx.objects.poll();

        if (!tree.isValid() ||
                !ctx.movement.isReachable(tree.getLocation(), ctx.players.local().getLocation())) {
            tree = ctx.objects.shuffle().poll();
        }

        if (tree.isOnScreen()) {
            chop(tree);

        } else {

            // Tree too far
            ctx.camera.turnTo(tree);
            // Check if back on screen
            if (tree.isOnScreen() && !isTreeFar(tree)) {
                chop(tree);
            } else {
                // walk there
                ctx.movement.stepTowards(tree);
            }

        }
    }
}