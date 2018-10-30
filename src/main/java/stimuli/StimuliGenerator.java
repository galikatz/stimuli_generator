package stimuli;

import javafx.util.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * Created by gali.k on 1/20/18.
 */
public class StimuliGenerator {
    private static final int MIN_RADIUS = 10;
    private static final String SCREEN_X_KEY = "screen.x";
    private static final String SCREEN_Y_KEY = "screen.y";
    private static final String FROM_NUMBER_KEY = "fromNumber";
    private static final String TO_NUMBER_KEY = "toNumber";
    private static final String NUMBER_OF_STIMULI_KEY = "numberOfStimuli";
    private static final String IMAGE_DIR_KEY = "imageDir";
    private static final String CONGRUENCY_KEY = "isCongruent";
    private static final String START_INDEX_KEY = "startIndex";


    private static final double SMALLET_AREA = 2*Math.PI*MIN_RADIUS*MIN_RADIUS;
    private static final int BUFFER = 10;//pixels
    private static final int MAX_ITERATIONS = 5;
    private int screenX;
    private int screenY;
    private int fromNumber;
    private int toNumber;
    private int numOfStimuli;
    private String imagesDirPath;
    private Random random;
    private boolean isCongruent;
    private double MIN_AREA =  2*Math.PI*(MIN_RADIUS)*(MIN_RADIUS);
    private int startIndex = 0;


    public StimuliGenerator(int screenX, int screenY, int fromNumber, int toNumber, int numOfStimuli, String imagesDirPath, boolean isCongruent, int startIndex) {
        this.screenX = screenX;
        this.screenY = screenY;
        this.fromNumber = fromNumber;
        this.toNumber = toNumber;
        this.numOfStimuli = numOfStimuli;
        this.imagesDirPath = imagesDirPath;
        this.random = new Random();
        this.isCongruent = isCongruent;
        this.startIndex = startIndex;
    }

    public int getNumOfStimuli() {
        return numOfStimuli;
    }

    public void setNumOfStimuli(int numOfStimuli) {
        this.numOfStimuli = numOfStimuli;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * Generates the circles (to interceptions allowed).
     * @return a list of circles
     */
    private List<List<Circle>> generateRandomCircleDimentions(){
        List<List<Circle>> circles = new ArrayList<>();
        for(int i=0; i<numOfStimuli; i++) {
            int numberOfCirclesInImage = random.ints(fromNumber, toNumber+1).findFirst().getAsInt();
            System.out.println("Generated num of circles in image: "+numberOfCirclesInImage);
            List<Circle> currentImageCirclesList = new ArrayList<>();
            //generate CenterX
            Circle newCircle = generateRandomCircle();
            currentImageCirclesList = recursiveGenerateRandomCirclesForImage(newCircle, currentImageCirclesList, numberOfCirclesInImage);
            if(!overlapsExists(currentImageCirclesList)){
                circles.add(currentImageCirclesList);
            }
        }

        if(circles.size()<numOfStimuli){
            System.out.println("Overlaps filtered: Current stimuli size: "+circles.size()+", required numOfStimuli: "+numOfStimuli);
        }
        printCircleList(circles);
        return circles;
    }

    private List<Circle> generateCircleInImageB(int lowerBound, int upperBound, double areaLowerBound, double areaUpperBound){
        int numberOfCirclesInImage = random.ints(lowerBound, upperBound+1).findFirst().getAsInt();
        double area = 0;
        if(areaLowerBound>areaUpperBound){
            double tmp = areaLowerBound;
            areaLowerBound = areaUpperBound;
            areaUpperBound = tmp;

        }
        area = random.doubles(areaLowerBound, areaUpperBound + 1).findFirst().getAsDouble();

        System.out.println("Generated num of circles in image: "+numberOfCirclesInImage +"(lower: "+lowerBound+", upper: "+upperBound+") with area: "+area +" (lower: "+areaLowerBound+ ", upper: "+areaUpperBound+")");
        List<Circle> currentImageCirclesList = new ArrayList<>();
        currentImageCirclesList = recursiveGenerateRandomCirclesForImageWithArea(currentImageCirclesList, numberOfCirclesInImage, area);
        return currentImageCirclesList;
    }

    public boolean isValidCircle(Circle c, int screenX, int screenY){
        if(((c.getCenterY()+c.getRadius()>=screenY) ||
            (c.getCenterX()+c.getRadius()>=screenX)) ||//crossed upper bound{
            ((c.getCenterY()-c.getRadius()<=0) ||
            (c.getCenterX()-c.getRadius()<=0))){//crossed lower bound
           // System.out.println("Circle not valid: "+c.toString());
            return false;
        }
        return true;
    }

    public void printCircleList(List<List<Circle>> circles) {
        for(int i=0; i<circles.size(); i++){
            List<Circle> circlesInImage = circles.get(i);
            System.out.println("Created image "+i+" with "+circlesInImage.size()+" circles: ");
            for (int j = 0; j < circlesInImage.size(); j++) {
                System.out.println(circlesInImage.get(j).toString());
            }
        }
    }

    private List<Circle> recursiveGenerateRandomCirclesForImage(Circle newCircle, List<Circle> currentImageCirclesList, int numberOfCirclesInImage) {
        if(numberOfCirclesInImage==0){
            return currentImageCirclesList;
        }
        if(currentImageCirclesList.isEmpty()){
            currentImageCirclesList.add(newCircle);
            Circle newC = generateRandomCircle();
            return recursiveGenerateRandomCirclesForImage(newC, currentImageCirclesList, numberOfCirclesInImage-1);
        }
        else {
            List<Circle> copyList = new ArrayList<>(currentImageCirclesList);
            for (Circle c : copyList) {
                if (isValidCircle(newCircle, screenX, screenY) && !checkOverlap(c, newCircle)) {
                    currentImageCirclesList.add(newCircle);
                    return recursiveGenerateRandomCirclesForImage(generateRandomCircle(), currentImageCirclesList, numberOfCirclesInImage-1);
                }
            }
        }
        return currentImageCirclesList;
    }

    private List<Circle> recursiveGenerateRandomCirclesForImageWithArea(List<Circle> currentImageCirclesList, int numberOfCirclesInImage, double areaInImage) {
        if(numberOfCirclesInImage==0){
            return currentImageCirclesList;
        }
        if(currentImageCirclesList.isEmpty()){
            Circle newC = generateRandomCircle(areaInImage);
            double areaOfNewCircle = 2 * Math.PI * newC.getRadius() * newC.getRadius();
            currentImageCirclesList.add(newC);
            return recursiveGenerateRandomCirclesForImageWithArea(currentImageCirclesList, numberOfCirclesInImage-1, areaInImage-areaOfNewCircle);
        }
        else {
            if (currentImageCirclesList.size() == 1) {
                Circle newC = generateRandomCircle(areaInImage);
                if (!checkOverlap(newC, currentImageCirclesList.get(0))) {
                    currentImageCirclesList.add(newC);
                    double areaOfNewCircle = 2 * Math.PI * newC.getRadius() * newC.getRadius();
                    return recursiveGenerateRandomCirclesForImageWithArea(currentImageCirclesList, numberOfCirclesInImage - 1, areaInImage - areaOfNewCircle);
                } else {
                    System.out.println("### overlap was found  - trying again");
                    return recursiveGenerateRandomCirclesForImageWithArea(currentImageCirclesList, numberOfCirclesInImage, areaInImage);
                }
            } else {
                List<Circle> copyList = new ArrayList<>(currentImageCirclesList);
                Circle newC = generateRandomCircle(areaInImage);
                copyList.add(newC);
                if(!overlapsExists(copyList)){
                    currentImageCirclesList.add(newC);
                    double areaOfNewCircle = 2 * Math.PI * newC.getRadius() * newC.getRadius();
                    return recursiveGenerateRandomCirclesForImageWithArea(currentImageCirclesList, numberOfCirclesInImage - 1, areaInImage - areaOfNewCircle);
                } else {
                    System.out.println("*** overlap was found - trying again");
                    return recursiveGenerateRandomCirclesForImageWithArea(currentImageCirclesList, numberOfCirclesInImage, areaInImage);
                }
            }
        }
    }


    private Circle generateRandomCircle() {
        int radius = random.ints(MIN_RADIUS, screenX / 10).findFirst().getAsInt();
        int centerX = random.ints(radius, screenX - radius).findFirst().getAsInt();
        int centerY = random.ints(radius, screenY - radius).findFirst().getAsInt();
        Circle newCircle = new Circle(centerX, centerY, radius);
        return newCircle;
    }

    private Circle generateRandomCircle(double upperAreaBound) {
        int radiusBound = (int) Math.sqrt(upperAreaBound/(2*Math.PI));
        int radius = MIN_RADIUS;
        if(radiusBound>MIN_RADIUS) {
            radius = random.ints(MIN_RADIUS, radiusBound).findFirst().getAsInt();
        }
        try {
            int centerX = random.ints(radius, screenX - radius).findFirst().getAsInt();
            int centerY = random.ints(radius, screenY - radius).findFirst().getAsInt();
            Circle newCircle = new Circle(centerX, centerY, radius);
            return newCircle;
        }catch(IllegalArgumentException e){
            System.out.println("radius = " + radius);
            System.out.println("upper bound x = " + (screenX - radius));
            System.out.println("upper bound y = " + (screenY - radius));
            throw new RuntimeException(e);
        }
    }

    public boolean checkOverlap(Circle existingCircle, Circle newCircle) {
        if(existingCircle.equals(newCircle)){
            return false;
        }
        return Math.hypot(existingCircle.centerX - newCircle.centerX, existingCircle.centerY - newCircle.centerY) <= existingCircle.radius+ BUFFER + newCircle.radius;
    }

    /**
     * Creates images from the list of circles
     * @return list of images
     */
    public List<Pair<ImageData,ImageData>> createImages(List<List<Circle>> circles) {
        List<Pair<ImageData,ImageData>> imagesList = new ArrayList<>();
        for (List<Circle> circleInImage: circles) {
            ImageData imageA = createImage(circleInImage);
            ImageData imageB = createImageB(imageA, this.isCongruent);
            Pair<ImageData, ImageData> pairOfImages = new Pair<>(imageA, imageB);
            imagesList.add(pairOfImages);
        }
        return imagesList;
    }
    /**
     * Creates images from the list of circles
     * @return list of images
     */
    public ImageData createImage(List<Circle> circles) {
        ImageData imageData;
        final BufferedImage image = new BufferedImage(screenX, screenY, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D graphics2D = image.createGraphics();
        graphics2D.setPaint(Color.WHITE);
        graphics2D.fillRect(0, 0, screenX, screenY);
        graphics2D.setPaint(Color.BLACK);
        double area = 0;
        for(Circle circle: circles) {
            graphics2D.fillOval(circle.getCenterX(), circle.getCenterY(), circle.getRadius(), circle.getRadius());
            area+=(Math.PI*circle.getRadius()*circle.getRadius());
        }
        double convexHull = calculateConvexHull(circles);
        graphics2D.dispose();
        imageData = new ImageData(circles.size(),image, area, convexHull);
        return imageData;
    }

    private double calculateConvexHull(List<Circle> circles) {
        List<Point> points  = new ArrayList<>(circles.size());
        for(Circle circle : circles){
            Point p = new Point(circle.getCenterX(), circle.getCenterY());
            points.add(p);
        }
        Point[] pointsArr = points.toArray(new Point[points.size()]);
        List<Point> pointsOrder = ConvexHullCalculator.convexHull(pointsArr);
        double d = 0.0;
        if(pointsOrder!=null) {
            d = ConvexHullCalculator.convexHullLength(pointsOrder);
        }else{//2 points only
            d = ConvexHullCalculator.convexHullLength(points);
        }
        return d;
    }

    public ImageData createImageB(ImageData imageDataA, boolean isCongruent){
        ImageData imageDataB;
        int numOfCirclesA = imageDataA.getNumOfCircles();
        int upperBoundB;
        int lowerBoundB;
        double lowerAreaBound;
        double upperAreaBound;
        if(isCongruent){
            // the new congruent can have bigger/equal area && bigger numOfcircles, or smaller/equal area && less num of circles.
            if(numOfCirclesA==this.toNumber) {//upper bound is taken, will create image B with less circles than A and smaller area.
                lowerBoundB = this.fromNumber;
                upperBoundB = numOfCirclesA-1;
                lowerAreaBound = SMALLET_AREA;
                upperAreaBound = imageDataA.getArea()-1;
            }else{//numOfCirclesA<this.toNumber, then create image B with more circles than A and more area
                lowerBoundB = numOfCirclesA+1;
                upperBoundB = this.toNumber;
                lowerAreaBound = imageDataA.getArea()+1;
                upperAreaBound = 2*Math.PI*(this.screenX/2)*(this.screenX/2);
            }
        }else{//incongruent
            // the new incongruent can have bigger/equal area && smaller numOfcircles, or smaller/equal area && greater num of circles.
            if(numOfCirclesA==this.toNumber) {//upper bound is taken, will create image B with less circles than A but with greater area than A.
                lowerBoundB = this.fromNumber;
                upperBoundB = numOfCirclesA-1;
                lowerAreaBound = imageDataA.getArea()+1;
                upperAreaBound = 2*Math.PI*(this.screenX/2)*(this.screenX/2);
            }else{//numOfCirclesA<this.toNumber, then create image B with more circles than A but with less area than A
                lowerBoundB = numOfCirclesA+1;
                upperBoundB = this.toNumber;
                lowerAreaBound = MIN_AREA;
                upperAreaBound = imageDataA.getArea()-MIN_AREA;
            }
        }
        List<Circle> circlesForImageB = generateCircleInImageB(lowerBoundB, upperBoundB, lowerAreaBound, upperAreaBound);
        imageDataB = createImage(circlesForImageB);
        if(overlapsExists(circlesForImageB)){
            System.out.println("@@@@ Overlap was found in Image B creation: " + imageDataB.toString());
        }
        return imageDataB;
    }

    /**
     * Saving the images to files.
     * @param images
     */
    public void saveImages(List<Pair<ImageData, ImageData>> images, String fileNamePrefix){
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.FLOOR);
        for(int i=0; i<images.size(); i++) {
            try {
                Pair<ImageData, ImageData> imageDataPair = images.get(i);
                ImageData imageDataA = imageDataPair.getKey();
                ImageData imageDataB = imageDataPair.getValue();
                String areaAstring = df.format(imageDataA.getArea());
                String areaBstring = df.format(imageDataB.getArea());
                String convexHullA = df.format(imageDataA.getConvexHull());
                String convexHullB = df.format(imageDataB.getConvexHull());
                ImageIO.write(imageDataA.getImage(), "png", new File(imagesDirPath+fileNamePrefix+"_index_"+(startIndex+i)+
                        "_A_circles_"+imageDataA.getNumOfCircles()+"_area_"+areaAstring+"_convex_hull_"+convexHullA+".png"));
                ImageIO.write(imageDataB.getImage(), "png", new File(imagesDirPath+fileNamePrefix+"_index_"+(startIndex+i)+
                        "_B_circles_"+imageDataB.getNumOfCircles()+"_area_"+areaBstring+"_convex_hull_"+convexHullB+".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream("/Users/gali.k/phd/stimuli_generator/src/main/resources/stimuliGenerator.properties"));

        StimuliGenerator stimuliGenerator = new StimuliGenerator(
                Integer.parseInt(properties.getProperty(SCREEN_X_KEY)),
                Integer.parseInt(properties.getProperty(SCREEN_Y_KEY)),
                Integer.parseInt(properties.getProperty(FROM_NUMBER_KEY)),
                Integer.parseInt(properties.getProperty(TO_NUMBER_KEY)),
                Integer.parseInt(properties.getProperty(NUMBER_OF_STIMULI_KEY)),
                properties.getProperty(IMAGE_DIR_KEY),
                Boolean.parseBoolean(properties.getProperty(CONGRUENCY_KEY)),
                Integer.parseInt(properties.getProperty(START_INDEX_KEY)));

        int iterations = 0;
        int index = 0;
        while (stimuliGenerator.getNumOfStimuli() > 0 && iterations<MAX_ITERATIONS) {
            int currentStimuliSize = 0;
            System.out.println("Iteration: "+iterations);
            List<List<Circle>> circles = stimuliGenerator.generateRandomCircleDimentions();
            List<Pair<ImageData, ImageData>> images = stimuliGenerator.createImages(circles);
            List<Pair<ImageData, ImageData>> validImages = checkValiditiy(stimuliGenerator.isCongruent(), images);
            currentStimuliSize = validImages.size();
            index+=validImages.size();
            stimuliGenerator.saveImages(validImages, stimuliGenerator.isCongruent() ? "cong" : "incong");
            iterations++;
            System.out.println("Setting start index to be: "+index);
            stimuliGenerator.setStartIndex(index);
            int stimuliLeft = stimuliGenerator.getNumOfStimuli() - currentStimuliSize;
            stimuliGenerator.setNumOfStimuli(stimuliLeft);
            System.out.println("Num of Stimuli left to create: "+ stimuliLeft +" Iterations: "+iterations);
        }
        System.out.println("DONE!!!");

    }

    private static List<Pair<ImageData, ImageData>> checkValiditiy(boolean congruent, List<Pair<ImageData, ImageData>> images) {
        List<Pair<ImageData, ImageData>> validateImages = new ArrayList<>();
        for(Pair<ImageData, ImageData> pair: images) {
            ImageData imageA = pair.getKey();
            ImageData imageB = pair.getValue();
            if (congruent) {
                if ((imageA.getNumOfCircles() > imageB.getNumOfCircles() && imageA.getArea() >= imageB.getArea()) ||
                        (imageA.getNumOfCircles() < imageB.getNumOfCircles() && imageA.getArea() <= imageB.getArea())){
                   validateImages.add(pair);
                }
                else{
                    System.out.println("Images are not valid, congruent="+congruent+"\nImages: "+imageA.toString()+ "\n"+ imageB.toString());
                }
            }else{
                if ((imageA.getNumOfCircles() > imageB.getNumOfCircles() && imageA.getArea() <= imageB.getArea()) ||
                        (imageA.getNumOfCircles() < imageB.getNumOfCircles() && imageA.getArea() >= imageB.getArea())){
                    validateImages.add(pair);
                }else{
                    System.out.println("Images are not valid, congruent="+congruent+"\nImages: "+imageA.toString()+ "\n"+ imageB.toString());
                }
            }
        }
        if(validateImages.size()<images.size()){
            System.out.println("Problem with congruency validation: validate size: "+validateImages.size()+", original images size: "+images.size());
        }
        return validateImages;
    }

    public boolean overlapsExists(List<Circle> circlesInImage){
        boolean foundOverlap = false;
        for(int i=0; i<circlesInImage.size() && !foundOverlap; i++){
            Circle cx = circlesInImage.get(i);
            for(Circle cy: circlesInImage){
                if(checkOverlap(cx,cy)){
                    foundOverlap = true;
                    break;
                }
            }
        }
        return foundOverlap;
    }

    public boolean isCongruent() {
        return isCongruent;
    }

    public static class Circle{
        private int centerX;
        private int centerY;
        private int radius;

        public Circle(int centerX, int centerY, int radius) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.radius = radius;
        }

        public int getCenterX() {
            return centerX;
        }

        public int getCenterY() {
            return centerY;
        }

        public int getRadius() {
            return radius;
        }

        @Override
        public String toString() {
            return "Circle{" +
                    "centerX=" + centerX +
                    ", centerY=" + centerY +
                    ", radius=" + radius +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Circle)) return false;

            Circle circle = (Circle) o;

            if (centerX != circle.centerX) return false;
            if (centerY != circle.centerY) return false;
            return radius == circle.radius;
        }

        @Override
        public int hashCode() {
            int result = centerX;
            result = 31 * result + centerY;
            result = 31 * result + radius;
            return result;
        }
    }

    public static class ImageData{
        private int numOfCircles;
        private BufferedImage image;
        private double area;
        private double convexHull;

        public ImageData(int numOfCircles, BufferedImage image, double area, double convexHull) {
            this.numOfCircles = numOfCircles;
            this.image = image;
            this.area = area;
            this.convexHull = convexHull;
        }

        public int getNumOfCircles() {
            return numOfCircles;
        }

        public BufferedImage getImage() {
            return image;
        }

        public double getArea() {
            return area;
        }

        @Override
        public String toString() {
            return "ImageData{" +
                    "numOfCircles=" + numOfCircles +
                    ", image=" + image +
                    ", area=" + area +
                    ", convexHull=" + convexHull +
                    '}';
        }

        public double getConvexHull() {
            return convexHull;
        }
    }
}
