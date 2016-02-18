package uk.ac.cam.teamdelta.larry;

import javafx.animation.RotateTransition;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Duration;
import uk.ac.cam.teamdelta.robert.Engine;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static uk.ac.cam.teamdelta.Logger.debug;

public class LoadingScreenController implements ScreenController {

    ScreensContainer container;

    private static final String factsPath = "./src/main/resources/uk.ac.cam.teamdelta.larry/facts.txt";

    @FXML
    ImageView img;
    @FXML
    Text fact;

    @Override
    public void setScreenParent(ScreensContainer screenParent) {
        container = screenParent;
    }

    @Override
    public void showScreen() {
        try {
            List<String> facts = readFile(factsPath);

            Random randomizer = new Random();
            String randomFact = facts.get(randomizer.nextInt(facts.size()));
            fact.setText(randomFact);
        } catch (IOException e) { //catch!
            fact.setText("Young people can find it hard to appreciate the position of elderly drivers");
            debug("Error loading facts");
        }

        // set size of rotating wheel image
        img.setFitHeight(Main.GAME_HEIGHT / 2);
        img.setFitWidth(Main.GAME_WIDTH / 2);
        // spin image round for a few seconds
        final RotateTransition rt = new RotateTransition(Duration.millis(1000), img);
        // wait x seconds before continuing to next screen
        //TODO: don't wait arbitrary time, get notified?
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                //TODO: Constructor may change
                debug("Constructing the Engine");
                Engine engine = new Engine(LarrySettings.getInstance().getLocation(),
                        LarrySettings.getInstance().getParameters());
                LarrySettings.getInstance().setEngine(engine);
                engine.firstFrame();
                debug("Engine has the first frame");
                return null;
            }
        };
        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                debug("Finished sleeping");
                rt.stop();
                container.nextScreen();
            }
        });
        new Thread(sleeper).start();
        rt.setCycleCount(10);
        rt.setByAngle(720);
        rt.play();

    }

    @Override
    public void setupScreen() {

    }

    private static List<String> readFile(String path) throws IOException {
        List<String> lines = new LinkedList<String>();
        File fin = new File(path);
        FileInputStream fis = new FileInputStream(fin);

        //Construct BufferedReader from InputStreamReader
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));

        String line = null;
        while ((line = br.readLine()) != null) {
            lines.add(line);
        }

        br.close();
        return lines;
    }

}
