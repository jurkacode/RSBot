package net.jurka.arrow;

import net.jurka.arrow.stats.Stats;
import net.jurka.arrow.stats.StatsPaint;
import net.jurka.arrow.tasks.*;
import org.powerbot.event.MessageEvent;
import org.powerbot.event.MessageListener;
import org.powerbot.event.PaintListener;
import org.powerbot.script.Manifest;
import org.powerbot.script.PollingScript;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.Tile;

import java.awt.*;
import java.util.Stack;

@Manifest(
        name = "Arrow shaft script",
        authors = "Jurka",
        description = "Creates arrow shafts and really fast"
)
public class ArrowScript extends PollingScript implements MessageListener, PaintListener {

    private Tile startTile;

    private Stats stats;
    private StatsPaint paintStats = new StatsPaint();
    private Stack<Task> taskList = new Stack<Task>();

    @Override
    public void start() {
        stats = Stats.getInstance();
        paintStats.setStats(stats);
        startTile = ctx.players.local().getLocation();

        // TODO: Refactor the task adding into some facade, or factory method
        taskList.push(new Chop(ctx, stats));
        taskList.push(new Craft(ctx, stats));
        taskList.push(new Combat(ctx, stats));
        taskList.push(new Drop(ctx, stats));
        taskList.push(new Run(ctx, stats, startTile));
        taskList.push(new AntiPattern(ctx, stats));
    }

    @Override
    public int poll() {

        if (taskList.isEmpty()) {
            log.warning("There are no tasks to execute stop!");
            this.getController().stop();
        }

        for (Task task : taskList) {
            if (task.activate()) {
                log.info("Current task: " + task.getClass().getSimpleName());
                task.execute();
                return Random.nextInt(500, 1500);
            }
        }

        return 500;
    }

    @Override
    public void messaged(MessageEvent e) {
        String message = e.getMessage();

        if (message.contains("You get some logs.")) {
            stats.incrLogsCut();
        } else if (message.contains("You carefully cut the wood into")) {
            stats.incArrowShaft(15);
        }
    }

    @Override
    public void repaint(Graphics g) {
        paintStats.draw(g);
    }

}