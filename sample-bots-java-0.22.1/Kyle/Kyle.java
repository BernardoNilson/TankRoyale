import dev.robocode.tankroyale.botapi.*;
import dev.robocode.tankroyale.botapi.events.*;

// ------------------------------------------------------------------
// Kyle
// ------------------------------------------------------------------
// A sample bot
// ------------------------------------------------------------------
public class Kyle extends Bot {

    int moveAmount;

    // The main method starts our bot
    public static void main(String[] args) {
        new Kyle().start();
    }

    // Constructor, which loads the bot config file
    Kyle() {
        super(BotInfo.fromFile("Kyle.json"));
    }

    // Called when a new round is started -> initialize and do some movement
    @Override
    public void run() {
        System.out.println("Iniciando: Kyle");
        // Set colors (From Fire)
        setBodyColor(Color.RED);   // orange
        setGunColor(Color.fromHex("F70"));    // dark orange
        setTurretColor(Color.fromHex("F70")); // dark orange
        setRadarColor(Color.ORANGE);  // red
        setScanColor(Color.RED);   // red
        setBulletColor(Color.fromHex("08F")); // light blue

        moveAmount = Math.max(getArenaWidth(), getArenaHeight());
        
        turnRight(getDirection() % 90);
        forward(moveAmount);
        turnLeft(90);
        turnGunLeft(65);

        // Repeat while the bot is running
        while (isRunning()) {
            if (getDirection() == 90) setTurnGunLeft(270);
            forward(moveAmount);
            turnLeft(90);
        }
    }

    // We saw another bot -> fire!
    @Override
    public void onScannedBot(ScannedBotEvent e) {
        // Calculate direction of the scanned bot and bearing to it for the gun
        var bearingFromGun = gunBearingTo(e.getX(), e.getY());

        // Turn the gun toward the scanned bot
        turnGunLeft(bearingFromGun);
        double distanceX = Math.abs(e.getX() - getX());
        double distanceY = Math.abs(e.getY() - getY());

        System.out.println("X: " + distanceX);
        System.out.println("Y: " + distanceY);

        if (distanceX < 120 && distanceY < 120) fire(3);
        else if (distanceX < 200 && distanceY < 200) fire(2);
        else if (distanceX < 350 && distanceY < 350) fire(1);

        // Generates another scan event if we see a bot.
        // We only need to call this if the gun (and therefore radar)
        // are not turning. Otherwise, scan is called automatically.
        if (bearingFromGun == 0 && distanceX < 400  && distanceY < 400) {
            rescan();
            forward(10);
        }
    }

    @Override
    public void onHitByBullet(HitByBulletEvent hitByBulletEvent) {
        forward(20);
    }

    @Override
    public void onHitWall(HitWallEvent botHitWallEvent) {
        turnRight(getDirection() % 90);
    }

    @Override
    public void onHitBot(HitBotEvent e) {
        // If he's in front of us, set back up a bit.
        back(40);
        // turnLeft(180);
    }
}
