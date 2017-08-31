import java.io.*;

public class test {
    public static void main(String[] args) throws Exception {
        String nextLine;

        //read a file
        BufferedReader reader = new BufferedReader(new FileReader("data/Training.arff"));

        // put to a new file
        PrintStream writer = new PrintStream(new FileOutputStream("data/Testing.arff"));
        writer.println("@RELATION Testing\n");
        // getting the data of class
        while ((nextLine = reader.readLine()) != null) {
            if (nextLine.startsWith("@ATTRIBUTE")){
                writer.println(nextLine);
            }
        }
        writer.println("\n@DATA");
        reader.close();
        writer.flush();
        writer.close();
    }
}
