package org.pltw.examples.okmagpie;

import android.content.Context;

import java.util.List;

/**
 * An extension to Flaggy that can load chatbot responses
 * @author Ari Stassinopoulos
 * @version 4 DEC 2018
 */
public class ResponseLoader extends Flaggy {

    /**
     * A Flaggy of the responses
     */
    private ParsedFlaggy responseFlaggy;

    /**
     * A Flaggy subclass instance for the responses
     */
    private LoadedResponse loadedResponse;

    /**
     * Construct a response loader
     * @param context The context of the operations of the class
     */
    public ResponseLoader(Context context) {
        super(R.raw.running_responses, context);
        responseFlaggy = getParsedFileContent();
        loadedResponse = new LoadedResponse(responseFlaggy.getHeaders(), responseFlaggy.getRows());
    }

    /**
     * Get the loaded responses from the CSV file
     * @return the loaded responses
     */
    public LoadedResponse getLoadedResponse() {
        return loadedResponse;
    }

    /**
     * A ParsedFlaggy subclass for the responses
     * @author Ari Stassinopoulos
     * @version 4 DEC 2018
     */
    public class LoadedResponse extends Flaggy.ParsedFlaggy<String> {

        /**
         * Construct a loaded response
         * @param headers The headers from the file
         * @param flaggyRows The rows from the file
         */
        public LoadedResponse(List<String> headers, List<Flaggy.FlaggyRow<String>> flaggyRows) {
            super(headers, flaggyRows);
        }

        /**
         * Get a response for a keyword
         * @param keyword The content of the first cell
         * @return A random response for that keyword
         */
        public String getRandomResponseForKeyword(String keyword) {
            List<FlaggyRow<String>> responsesForHeader = super.searchByCellContent(0, keyword);

            if(responsesForHeader == null) return null;
            int index = 0;
            for(FlaggyRow<String> response : responsesForHeader) {
                if(response.getCells().size() < 2) responsesForHeader.remove(index);
                index++;
            }
            if(responsesForHeader.size() == 0) return null;
            int randomIndex = (int) (Math.random() * responsesForHeader.size());
            return responsesForHeader.get(randomIndex).getCells().get(1);
        }

        /**
         * Get a random (probing) response
         * @return A probing response
         */
        public String getRandomResponse() {
            return this.getRandomResponseForKeyword("random");
        }

    }
}
