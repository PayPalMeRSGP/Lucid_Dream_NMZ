package Nodes.MidDreamNodes;

import Nodes.ExecutableNode;
import ScriptClasses.Paint.ScriptStatusPainter;
import ScriptClasses.Util.Statics;
import org.osbot.rs07.api.Inventory;
import org.osbot.rs07.api.Menu;
import org.osbot.rs07.api.Mouse;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.input.mouse.InventorySlotDestination;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.concurrent.ThreadLocalRandom;

public class PrepNode extends MidDreamNode {
    private static ExecutableNode singleton = null;

    private PrepNode(Script hostScriptReference) {
        super(hostScriptReference);
    }

    public static ExecutableNode getSingleton(Script hostScriptReference) {
        if(singleton == null){
            singleton = new PrepNode(hostScriptReference);
        }
        return singleton;
    }

    @Override
    public int executeNodeAction() throws InterruptedException {
        ScriptStatusPainter.setCurrentScriptStatus(ScriptStatusPainter.ScriptStatus.PREPARING);
        setDoOverload(true);
        if(walkToCorner()){
            handleAbsorptionLvl();
            setPlayerHealthTo1();
            turnOnAutoRetaliate();
        }

        return 1000;
    }

    private boolean walkToCorner() throws InterruptedException {
        int corner = ThreadLocalRandom.current().nextInt(0, 4);
        WalkingEvent walk;
        switch(corner){
            case 0: //SE corner
                walk = setUpWalker(63, 48);
                break;
            case 1: //SW corner
                walk = setUpWalker(32, 48);
                break;
            case 2: //NW corner
                walk = setUpWalker(32, 48);
                break;
            case 3: //NE corner
                walk = setUpWalker(32, 48);
                break;
            default:
                throw new UnsupportedOperationException("hit default in walkToCorner");
        }
        if(walk != null){
            hostScriptReference.execute(walk);
            final boolean[] finished = new boolean[1];
            new ConditionalSleep(20000) {
                @Override
                public boolean condition() throws InterruptedException {
                    finished[0] = walk.hasFinished();
                    return finished[0];
                }
            }.sleep();
            return finished[0];
        }
        return false;
    }

    private WalkingEvent setUpWalker(int localX, int localY){
        int actualX = hostScriptReference.getMap().getBaseX() + localX;
        int actualY = hostScriptReference.getMap().getBaseY() + localY;
        int z = hostScriptReference.myPlayer().getPosition().getZ();
        WalkingEvent walk = new WalkingEvent(new Position(actualX, actualY, z));
        walk.setMiniMapDistanceThreshold(5);
        walk.setOperateCamera(true);
        walk.setMinDistanceThreshold(0);
        return walk;
    }

    private void setPlayerHealthTo1() throws InterruptedException {
        int currentHealth = hostScriptReference.getSkills().getDynamic(Skill.HITPOINTS);

        Inventory inv = hostScriptReference.getInventory();
        if(currentHealth > 50 && doesPlayerHaveOverloadsLeft()){
            inv.interact(Statics.DRINK, Statics.OVERLOAD_POTION_1_ID, Statics.OVERLOAD_POTION_2_ID,
                    Statics.OVERLOAD_POTION_3_ID, Statics.OVERLOAD_POTION_4_ID);
            ScriptStatusPainter.setOverloadTimer();
            //wait out overload dmg, DO NOT GUZZLE while taking overload dmg, may result in overload dmg player killing player.
            int estimatedHealthAfterOverload = currentHealth - 51;
            new ConditionalSleep(7000, 500){
                @Override
                public boolean condition() throws InterruptedException {
                    int currentHealth = hostScriptReference.getSkills().getDynamic(Skill.HITPOINTS);
                    int difference = Math.abs(estimatedHealthAfterOverload - currentHealth);
                    return difference < 5;
                }
            }.sleep();
        }
        while(currentHealth > 1){
            guzzleRockCake();
            currentHealth = hostScriptReference.getSkills().getDynamic(Skill.HITPOINTS);
            Statics.hostScriptReference.log("guzzling rockcake... hp: " + currentHealth);
            MethodProvider.sleep(Statics.randomNormalDist(Statics.RS_GAME_TICK_MS, 60.0));
        }
    }

    private void guzzleRockCake(){
        Inventory inv = hostScriptReference.getInventory();
        Mouse mouse = hostScriptReference.getMouse();
        Menu rockCakeMenu = hostScriptReference.getMenuAPI();
        if(inv.contains(Statics.DWARVEN_ROCK_CAKE_ID)){
            int rockCakeInvSlot = inv.getSlot(Statics.DWARVEN_ROCK_CAKE_ID);
            InventorySlotDestination rockCakeDest = new InventorySlotDestination(hostScriptReference.getBot(), rockCakeInvSlot);
            mouse.click(rockCakeDest, true);
            if(rockCakeMenu.isOpen()){
                rockCakeMenu.selectAction(Statics.GUZZLE);
            }
        }

    }

    private void turnOnAutoRetaliate(){
        hostScriptReference.getTabs().open(Tab.ATTACK);
        hostScriptReference.getCombat().toggleAutoRetaliate(true);
        hostScriptReference.getTabs().open(Tab.INVENTORY);
    }
}