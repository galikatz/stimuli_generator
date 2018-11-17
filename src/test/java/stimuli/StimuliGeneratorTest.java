package stimuli;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javafx.util.Pair;
import stimuli.StimuliGenerator;


/**
 * Created by gali.k on 1/28/18.
 */
public class StimuliGeneratorTest{
    private static final String TEST_PATH = "/Users/gali.k/phd/stimuli_generator/src/test/resources/";
    private static final int SCREEN_X = 500;
    private static final int SCREEN_Y = 500;

    private StimuliGenerator stimuliGenerator  = new StimuliGenerator(SCREEN_X,SCREEN_Y,2,4,10, TEST_PATH, true, 0, false);
    private List<List<StimuliGenerator.Circle>> circles;
    private List<StimuliGenerator.Circle> circlesInImage;

    @Before
    public void init() {
        circles = new ArrayList<>();
        circlesInImage  = new ArrayList<>();
    }

    @Test
    public void overlapTest(){
        StimuliGenerator.Circle c1 = new StimuliGenerator.Circle(320,109,26);
        StimuliGenerator.Circle c2 = new StimuliGenerator.Circle(316,370,27);
        StimuliGenerator.Circle c3 = new StimuliGenerator.Circle(244,411,22);
        StimuliGenerator.Circle c4 = new StimuliGenerator.Circle(109,306,48);
        StimuliGenerator.Circle c5 = new StimuliGenerator.Circle(364,214,35);
        StimuliGenerator.Circle c6 = new StimuliGenerator.Circle(334,183,13);
        StimuliGenerator.Circle c7 = new StimuliGenerator.Circle(292,422,30);
        StimuliGenerator.Circle c8 = new StimuliGenerator.Circle(281,142,47);
        StimuliGenerator.Circle c9 = new StimuliGenerator.Circle(311,435,28);


        circlesInImage.add(c1);
        circlesInImage.add(c2);
        circlesInImage.add(c3);
        circlesInImage.add(c4);
        circlesInImage.add(c5);
        circlesInImage.add(c6);
        circlesInImage.add(c7);
        circlesInImage.add(c8);
        circlesInImage.add(c9);

        circles.add(circlesInImage);

        boolean foundOverlap = stimuliGenerator.overlapsExists(circlesInImage);

        Assert.assertTrue("circles should overlap ",foundOverlap);
    }

    @Test
    public void overlapEpsilonTest(){
        StimuliGenerator.Circle c1 = new StimuliGenerator.Circle(342,104,25);
        StimuliGenerator.Circle c2 = new StimuliGenerator.Circle(309,89,38);
        circles.add(circlesInImage);
        stimuliGenerator.printCircleList(circles);
        List<Pair<StimuliGenerator.ImageData, StimuliGenerator.ImageData>> images = stimuliGenerator.createImages(circles, false);
        stimuliGenerator.saveImages(images, stimuliGenerator.isSignleMode(), stimuliGenerator.isCongruent(), "overlapTestWithEpsilon_");
        Assert.assertTrue("circles should overlap "+ c1.toString()+" "+ c2.toString(),stimuliGenerator.checkOverlap(c1,c2));
    }

    @Test
    public void noOverlapTest(){
        StimuliGenerator.Circle c1 = new StimuliGenerator.Circle(273,231,17);
        StimuliGenerator.Circle c2 = new StimuliGenerator.Circle(110,270,23);
        circlesInImage.add(c1);
        circlesInImage.add(c2);
        circles.add(circlesInImage);
        stimuliGenerator.printCircleList(circles);
        List<Pair<StimuliGenerator.ImageData, StimuliGenerator.ImageData>> images = stimuliGenerator.createImages(circles, false);
        stimuliGenerator.saveImages(images,  stimuliGenerator.isSignleMode(), stimuliGenerator.isCongruent(), "noOverlapTest_");
        Assert.assertFalse("circles should not overlap "+ c1.toString()+" "+ c2.toString(),stimuliGenerator.checkOverlap(c1,c2));
    }


    @Test
    public void isValidCircleTest(){
        StimuliGenerator.Circle newCircle = new StimuliGenerator.Circle(500,850,17);
        Assert.assertFalse("Circle is not valid "+ newCircle.toString(), stimuliGenerator.isValidCircle(newCircle, SCREEN_X, SCREEN_Y));
    }

    @Test
    public void createImageBTest(){
        StimuliGenerator.Circle c1 = new StimuliGenerator.Circle(273,231,17);
        StimuliGenerator.Circle c2 = new StimuliGenerator.Circle(110,270,23);
        circlesInImage.add(c1);
        circlesInImage.add(c2);
        StimuliGenerator.ImageData imageA = stimuliGenerator.createImage(circlesInImage);

        //Check congruent
        StimuliGenerator.ImageData imageB = stimuliGenerator.createImageB(imageA, true);
        System.out.println("Congruent Test");
        if(imageA.getNumOfCircles()>imageB.getNumOfCircles()) {
            String msg = "Congruent:\nImageA: "+imageA.toString()+ "\nImage B: "+ imageB.toString()+ "\nimage A area should be >= image B area";
            System.out.println(msg);
            Assert.assertTrue(msg, imageA.getArea() >= imageB.getArea());
        }else{
            String msg = "Congruent:\nImageA: "+imageA.toString()+ "\nImage B: "+ imageB.toString()+ "\nimage A area should be <= image B area";
            Assert.assertTrue(msg, imageA.getArea() <= imageB.getArea());
        }

        //Check incongruent
        System.out.println("InCongruent Test");
        StimuliGenerator.ImageData imageC = stimuliGenerator.createImageB(imageA, false);
        if(imageA.getNumOfCircles()>imageC.getNumOfCircles()) {
            String msg = "Incongruent:\nImageA: "+imageA.toString()+ "\nImage C: "+ imageC.toString()+ "\nimage A area should be <= image C area";
            Assert.assertTrue(msg, imageA.getArea() <= imageC.getArea());
        }else{
            String msg = "Incongruent:\nImageA: "+imageA.toString()+ "\nImageC: "+ imageC.toString()+ "\nimage A area should be >= image C area";
            Assert.assertTrue(msg, imageA.getArea() >= imageC.getArea());
        }
    }
}
