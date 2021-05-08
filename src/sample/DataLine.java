package sample; /**

 */
import javafx.scene.control.Alert;

import java.io.PrintWriter;

public class DataLine {
    private String[] rawData; // Attributes of the file
    private String[] parsedData; //Data that has been altered to achieve proper json formatting
    private String[] fields; //Title of the attributes
    private int lineNumber;

    /**
     * Constructor for our Dataline class
     * @param line the amount of lines on the file
     * @param fieldString The whole first line of our file "title of the attributes"
     * @param lineElements
     */
    public DataLine(int line, String fieldString, String lineElements){
        this.lineNumber = line;
        this.rawData = lineElements.split(",");
        this.fields = fieldString.split(",");
        try {
            this.parsedData = this.parseData(this.fields.length);
        }
        catch (IndexOutOfBoundsException e){
            Alert message = new Alert(Alert.AlertType.ERROR);
            message.setContentText("CSV FORMATTING ERROR; check the File Structure");
            message.showAndWait();
        }
    }

    /**
     * Checks whether the file is missing data in the attributes category and if it does throws the proper exception
     * @param fileName name of file to be checked
     * @throws CSVDataMissing
     *
     */

    public void checkData(String fileName) throws CSVDataMissing {
        for (int i = 0; i < parsedData.length; i++) {
            if (parsedData[i].equals("")) {
                throw new CSVDataMissing(fileName,this.lineNumber);
            }
        }
    }


    /**
     * Checks whether the file is missing data in the fields category and if it does throws the proper exception to end program
     * @param fileName name of file to be checked
     * @throws CSVFileInvalidException
     */
    public void checkFields(String fileName) throws CSVFileInvalidException{
        String[] tempArray = new String[fields.length];

        for (int i = 0;i< fields.length;i++){
            tempArray[i] = fields[i];
        }

        for (int i =0;i< fields.length;i++) {
            if (fields[i].equals("")) {
                throw new CSVFileInvalidException(tempArray,fileName);
            }
        }
    }



    /**
     * Takes the string of raw data in the CSV line and converts it into parsed data by locating the elements that begin with quotation marks and locates the second element with quotation marks.
     * It will add and then join elements into a single location in a new array which is returned by the function and used in the constructor of our class
     * @param fields
     * @return String Array of parsed Data
     */

    public String[] parseData(int fields)throws IndexOutOfBoundsException{

        String[] array = new String[fields];
        int index = 0;

            for (int i = 0; i < fields; i++) {

                String temp = "";
                if (rawData[index].equals("")) {
                    array[i] = rawData[index];
                    index++;
                    continue;
                }

                if (rawData[index].charAt(0) == '"') {
                    temp += rawData[index];
                    temp += ",";
                    int counter = index;
                    int skip = 0;
                    do {
                        if (rawData[counter + 1].contains("\"")) {
                            temp += rawData[counter + 1];
                            skip++;
                            break;
                        } else {
                            counter++;
                            temp += rawData[counter];
                            temp += ",";
                            skip++;
                        }
                    } while (true);
                    array[i] = temp;
                    index += skip;
                } else {
                    array[i] = rawData[index];

                }
                index++;
            }

        return array;
    }


    /**
     * The file will open up the log file and write any errors the file encounters. It replaces missing fields with stars
     * so anybody reading log file can easily find where the missing datafield is located.
     * @param fileName
     * @param log file thats created which will contain all errors in the program
     */

    public void logAppendField(String fileName, PrintWriter log){
        String contentString = "";
        int missing = 0;

        for (int i = 0;i<this.fields.length;i++){
            if (this.fields[i].equals("")){
                this.fields[i] = "***";
                missing++;
            }
            if (i != this.fields.length-1){
                contentString += this.fields[i] + ", ";
            }
            else {
                contentString += this.fields[i];
            }
        }

        String message = "File " + fileName + " is invalid.\nMissing field: "+this.fields.length + " detected, " + missing +" missing.\n" + contentString;
        log.println(message);
    }

    /**
     * Similar function to logAppendField but this will works on missing attributes.
     * It will replacing the missing attributes with stars to ensure easy visibility of missing data.
     * @param fileName name of the file
     * @param log printwriter for our log file
     */
    public void logAppendData(String fileName, PrintWriter log){
        String contentString = "";
        int missing = 0;
        String missingNames = "";

        for (int i = 0;i<this.parsedData.length;i++){
            if (this.parsedData[i].equals("")){
                this.parsedData[i] = "***";
                missing++;
                missingNames += this.fields[i] + ",\t";
            }
            contentString += this.parsedData[i] + "\t";
        }


        String message = "In File " + fileName +" line: "+this.lineNumber +"\n"+contentString+"Missing: "+missingNames;
        log.println(message);
    }

    /**
     * Attempts to parse the string passed into it as an integer. If it cannot the String remains a String
     * @param s arbitrary string passed into function
     * @return boolean Checks if a number can be parsed as an int, if it cant it wont have quotation marks in the json file
     */

    public boolean isNumber(String s){

        try{
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException e){
            return false;
        }

    }

    /**
     * This function will use the data we have parsed and transform it into json formatting
     * @param pw printwriter for our json file
     */
    public void writeToJSON(PrintWriter pw){
        String lastField = fields[fields.length-1].replaceAll("[\\s\\p{Z}]+", " ").trim();
        String lastData = parsedData[parsedData.length-1].replaceAll("[\\s\\p{Z}]+", " ").trim();
        fields[fields.length-1] = (lastField);
        parsedData[parsedData.length-1] = lastData;
        for (int i =0;i<this.fields.length;i++){

            if (isNumber(this.parsedData[i])||this.parsedData[i].contains("\"")) {
                //System.out.println(this.parsedData[i]);
                if (i != fields.length-1){
                    pw.println("\""+this.fields[i]+"\": "+this.parsedData[i]+",");
                }
                else {
                    pw.println("\""+this.fields[i]+"\": "+this.parsedData[i]);
                }

            }
            else {
                if (i != fields.length-1){
                    pw.println("\""+this.fields[i]+"\": "+"\""+this.parsedData[i]+"\"" + ",");
                }
                else {
                    pw.println("\""+this.fields[i]+"\": "+"\""+this.parsedData[i]+"\"");
                }
            }
        }

    }

    /**
     * Function retrieves the line number of the data being read
     * @return linenumber
     */
    public int getLineNumber() {
        return this.lineNumber;
    }

    /**
     * Uses the field array and a parameter to grab specific values of the field array
     * @param index number of the position of the field array we want to return
     * @return the value at the index of the fields array
     */
    public String getFields(int index){
        return fields[index];
    }
}
