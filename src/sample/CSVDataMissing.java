package sample;



public class CSVDataMissing extends Exception{
    private String fileName;
    private int currentLine;


    /**
     * Constructor with 2 parameters for the custom CSVDataMissing file
     * @param filename name of the file causing the error
     * @param line the line in that file causing the error
     */
            public CSVDataMissing(String filename, int line){
                this.fileName = filename;
                this.currentLine = line;

            }

    /**
     * Overrides the default getMessage to our custom one, giving the file name and line of the error causing file
     * @return an error message
     */
            public String getMessage(){
                return "In file " + this.fileName + " line " + this.currentLine + " not converted to JSON: missing data";
            }
}
