package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;


public class Controller {
    Alert message = new Alert(Alert.AlertType.ERROR);
    Alert success = new Alert(Alert.AlertType.INFORMATION);
    File selectedFile = null;
    PrintWriter pw = null;
    PrintWriter log = null;
    Scanner sc = null;
    File jsonFile = null;
    @FXML
    private Button add;

    @FXML
    private Button convert;
    @FXML
    private Button save;
    @FXML
    private ProgressBar bar;
    @FXML
    private ListView filename;


    public void addFile(ActionEvent event){
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TEXT Files ","*.txt");
        FileChooser.ExtensionFilter csv = new FileChooser.ExtensionFilter("CSV Files ","*.csv");
        fc.getExtensionFilters().addAll(csv,extFilter);
        selectedFile = fc.showOpenDialog(null);
        bar.setProgress(0);
        if (selectedFile!=null){
            filename.getItems().add(selectedFile.getName());
        }


    }

    public void convertFile(ActionEvent event){
        bar.setProgress(0.25);
        String Path = selectedFile.getParent();
        String csvName = selectedFile.getName();
        String fileName = File.separator+selectedFile.getName().substring(0,(selectedFile.getName().length()-3))+"json";
        if (selectedFile != null){
            try {
                jsonFile = new File(Path +fileName);
                sc = new Scanner(selectedFile);
                pw = new PrintWriter(jsonFile);
                log = new PrintWriter(Path + fileName.substring(0,(fileName.length()-4))+"ERROR_LOG.txt");
                bar.setProgress(0.5);
            }
            catch (FileNotFoundException e){
                    message.setContentText("FILE NOT FOUND");
            }
            sc.useDelimiter("\\n");
            bar.setProgress(0.75);
            if (!processFilesForValidation(sc,pw,csvName,log)){
                sc.close();
                pw.close();
                log.close();
                jsonFile.delete();
                bar.setProgress(0);
            }
            else {
                success.setContentText("JSON File successfully created in original file's location");
                success.showAndWait();
                sc.close();
                pw.close();
                log.close();
                filename.getItems().remove(selectedFile.getName());
                bar.setProgress(1);
            }
        }

    }
    public static boolean processFilesForValidation(Scanner sc, PrintWriter pw, String csvName, PrintWriter log){
        Alert message = new Alert(Alert.AlertType.ERROR);
        Boolean tf=true;
        int   lineCount = 1;
        String fields = sc.next().replace("\"","");
        while (sc.hasNext()) {
            if (lineCount == 1)
                pw.print("[");
            String lineData = sc.next().replace("\"","");
            DataLine csvData = new DataLine(lineCount, fields, lineData);

            try {
                csvData.checkFields(csvName);
                csvData.checkData(csvName);
            } catch (CSVFileInvalidException e) {//Catches missing field, prints the custom error message to the screen for user
                //Appends the error into the log file and will cause the shutdown of the program
                message.setContentText( e.getMessage());
                message.showAndWait();
                csvData.logAppendField(csvName, log);
                log.flush();
                tf=false;
                break;
            } catch (CSVDataMissing e) { //Catches missing attributes, prints the custom error message to the screen for user
                //Appends the error into the log file
                message.setContentText( e.getMessage());
                message.showAndWait();
                csvData.logAppendData(csvName, log);
                continue;
            }
            catch (NullPointerException e){
                tf = false;
                break;
            }
            pw.println("{");
            csvData.writeToJSON(pw);
            if (sc.hasNext()) {
                pw.println("},");
            } else {
                pw.println("}");
                pw.println("]");
            }

            lineCount++;
        }
        pw.flush();

        return tf;
    }

}
