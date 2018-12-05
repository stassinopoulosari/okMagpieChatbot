package org.pltw.examples.okmagpie;

import android.content.Context;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Flaggy: A CSV parser that supports line comments
 * @author Ari Stassinopoulos
 * @version 1.0 (2018.12.03)
 */
public class Flaggy {

    /**
     * Stores the value of the csv file read.
     */
    private String csvFileContent;

    /**
     * Stores the Context necessary to read the file
     */
    private Context context;

    /**
     * This is how the rows are split
     */
    private final String separator = "|";

    /**
     * Holds the parsed value of the file in machine-readable format
     */
    private ParsedFlaggy parsedFileContent;

    /**
     * Construct a Flaggy from a reference and a Context
     * @param resID The resource ID for the CSV file
     * @param context The context to parse the file with.
     */
    public Flaggy(int resID, Context context) {
        InputStream csvFileInputStream = context.getResources().openRawResource(resID);
        csvFileContent = convertStreamToString(csvFileInputStream);
        parsedFileContent = parseFileContent(csvFileContent);
    }

    /**
     * Helper method to convert an InputStream to a String
     * @param inputStream An input stream from a CSV file
     * @return the file as a String
     */
    private String convertStreamToString(InputStream inputStream) {
        java.util.Scanner scanner = new java.util.Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    /**
     * Parse a CSV file
     * @param csvFileContent The content of the CSV file to parse
     * @return The parsed CSV file in the form of a ParsedFlaggy
     */
    private ParsedFlaggy<String> parseFileContent(String csvFileContent) {

        List<String> headers = new ArrayList<>();
        List<FlaggyRow<String>> rows = new ArrayList<>();

        String[] linesInFileContent = csvFileContent.split("\n|\r|\r\n");
        List<String> parsedFileContentLines = new ArrayList<>();

        for(String fileContentLine : linesInFileContent) {
            String[] splitFileContentLine = fileContentLine.split("#");
            String lineContent = splitFileContentLine[0];
            if(lineContent.trim().equals("")) {
                continue;
            }
            parsedFileContentLines.add(lineContent);
        }

        String headersLine = parsedFileContentLines.get(0);

        parsedFileContentLines = parsedFileContentLines.subList(1,parsedFileContentLines.size() - 1);

        headers = parseLine(headersLine);

        for(String parsedFileContentLine : parsedFileContentLines) {
            rows.add(new FlaggyRow<String>(headers, parseLine(parsedFileContentLine)));
        }

        return new ParsedFlaggy<String>(headers, rows);

    }

    /**
     * Parse a line of the CSV file
     * @param lineToParse A String with the line to parse
     * @return The parsed line as a list of parameters
     */
    private List<String> parseLine(String lineToParse) {
        String[] splitLine = lineToParse.split("\\|");
        List<String> parsedLine = new ArrayList<>();
        for(String splitLineCompontent : splitLine) {
            parsedLine.add(splitLineCompontent.trim());
        }
        return parsedLine;
    }

    /**
     * Get the parsed content of the file
     * @return the parsed file content in a ParsedFlaggy
     */
    public ParsedFlaggy getParsedFileContent() {
        return parsedFileContent;
    }

    /**
     * A parsed Flaggy
     * @param <X> The type of data stored in the ParsedFlaggy
     * @author Ari Stassinopoulos
     * @version 4 DEC 2018
     */
    public class ParsedFlaggy<X> {

        /**
         * A List of the headers in the file.
         */
        private List<String> headers;

        /**
         * A List of the rows in the file
         */
        private List<FlaggyRow<X>> rows;

        /**
         * Construct a parsed Flaggy given headers and rows
         * @param headers The CSV headers
         * @param rows The rows in the CSV file
         */
        public ParsedFlaggy(List<String> headers, List<FlaggyRow<X>> rows) {
            this.headers = headers;
            this.rows = rows;
        }

        /**
         * Get the CSV file's rows
         * @return The rows of the CSV file
         */
        public List<FlaggyRow<X>> getRows() {
            return rows;
        }

        /**
         * Get the CSV file's headers
         * @return The headers of the CSV file
         */
        public List<String> getHeaders() {
            return headers;
        }

        /**
         * Search by the content of a cell given a header name
         * @param headerName The title of the header
         * @param cellContent The content of the cell to search for
         * @return A List of rows with that cell content for that header
         */
        public List<FlaggyRow<X>> searchByCellContent(String headerName, X cellContent) {
            if(!headers.contains(headerName)) return null;

            int headerIndex = headers.indexOf(headerName);

            return this.searchByCellContent(headerIndex, cellContent);
        }

        /**
         * Search by cell content given a header index
         * @param headerIndex The index of the header within the headers list
         * @param cellContent The cell content to search for
         * @return A List of rows with that cell content for that header index
         */
        public List<FlaggyRow<X>> searchByCellContent(int headerIndex, X cellContent) {
            List<FlaggyRow<X>> results = new ArrayList<>();

            for(FlaggyRow<X> row : this.rows) {

                if(row.getCells().size() <= headerIndex) continue;

                if(row.getCells().get(headerIndex).equals(cellContent)) results.add(row);

            }

            return results;
        }

    }


    /**
     * A row of a parsed Flaggy
     * @param <X> The data type in the cells of the row
     * @author Ari Stassinopoulos
     * @version 4 DEC 2018
     */
    public class FlaggyRow<X> {

        /**
         * The headers for the row
         */
        private List<String> headers;

        /**
         * The cells in the row
         */
        private List<X> cells;

        /**
         * Construct a FlaggyRow given headers and cells
         * @param headers The row's headers
         * @param cells The row's cells
         */
        public FlaggyRow(List<String> headers, List<X> cells) {
            this.headers = headers;
            this.cells = cells;
        }

        /**
         * Get the headers
         * @return headers in a List
         */
        public List<String> getHeaders() {
            return headers;
        }

        /**
         * Get the cells
         * @return cells in a List
         */
        public List<X> getCells() {
            return cells;
        }

    }
}
