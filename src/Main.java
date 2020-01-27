import java.io.BufferedWriter;
import java.io.OutputStreamWriter;

public class Main {

    public static void main(String[] args) throws Exception {
	String inputFileName = args[0];
    Sheet sheet = Sheet.FromCSVFile(inputFileName);

    sheet.evalueate();
    sheet.printOut(new BufferedWriter(new OutputStreamWriter(System.out)));
    }
}
