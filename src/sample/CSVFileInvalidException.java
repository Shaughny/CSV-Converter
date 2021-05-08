package sample;


public class CSVFileInvalidException extends Exception{
          private String name;
          private String[] array;

    /**
     *  Constructor with two parameters for the custom CSVFileInvalidException
     * @param array String array
     * @param Filename name of the file causing the exception
     */
    public CSVFileInvalidException(String[] array, String Filename){
                this.name = Filename;
                this.array = array;

        }

    /**
     * Overrides the default getMessage to our custom one, giving the file name and line of the error causing file
     * @return an error message
     */
        public String getMessage(){

            return ("File " +this.name+" is invalid: field is missing.\nFile is not converted to JSON");

        }
}
