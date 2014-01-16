package net.jurka.arrow.tasks;

import net.jurka.arrow.Task;
import net.jurka.arrow.stats.Stats;
import net.jurka.arrow.util.Constant;
import org.powerbot.script.methods.Hud;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Item;

public class Craft extends Task {

    public Craft(MethodContext ctx, Stats stats) {
        super(ctx, stats);
    }

    private final Component knifeDialog = ctx.widgets.get(1179, 33);
    private final Component fletchDialog = ctx.widgets.get(1370, 38);
    private final Component craftingDialog = ctx.widgets.get(1251, 8);

    @Override
    public boolean activate() {
        return ctx.backpack.select().count() >= Constant.MAX_ITEMS_INVETORY;
    }

    @Override
    public void execute() {

        if (!ctx.hud.isOpen(Hud.Window.BACKPACK)) {
            ctx.hud.open(Hud.Window.BACKPACK);
        }

        if (!ctx.hud.isVisible(Hud.Window.BACKPACK)) {
            ctx.hud.view(Hud.Window.BACKPACK);
        }

        // TODO: refactor these sleeps with Condition.wait
        for (Item log : ctx.backpack.select().name("Logs").first()) {

            if (log.interact("Craft")) {

                if (fletchDialog.isVisible()) {
                    fletchDialog.click();
                    sleep(500, 1500);
                }

                if (knifeDialog.isVisible() && knifeDialog.click()) {

                    sleep(300, 500);
                    while (ctx.players.local().getAnimation() == Constant.FLETCHING
                            || craftingDialog.isVisible()) {
                        sleep(600, 1000);
                    }
                }

            }
        }
    }
}
