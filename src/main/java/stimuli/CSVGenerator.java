package stimuli;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class CSVGenerator {

    //cong_index_0_A_circles_3_area_5089.38_convex_hull_419.23.png
    public static void generateCSV(String imageDirPath, String csvName) throws IOException {
        try (FileWriter writer = new FileWriter(csvName)) {
            File imageDir = new File(imageDirPath);
            String[] list = imageDir.list();
            for(String fileName: list){
                StringBuilder sb = new StringBuilder("");
                //String numCircles = fileName.substring(fileName.indexOf("circles")+8, fileName.indexOf("_area"));
                String area = fileName.substring(fileName.indexOf("area")+5, fileName.indexOf("_convex_hull"));
                String convexHull = fileName.substring(fileName.indexOf("convex_hull")+12, fileName.indexOf(".png"));
                double score = Double.parseDouble(area)+Double.parseDouble(convexHull);
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.FLOOR);
                sb.append(area).append("      ").append(convexHull).append("        ").append(df.format(score)).append("\n");
                writer.write(sb.toString());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        CSVGenerator.generateCSV("/Users/gali.k/phd/stimuli_generator/src/main/resources/images/congruent", "/Users/gali.k/phd/stimuli_generator/src/main/resources/blackness.csv");
    }
}
