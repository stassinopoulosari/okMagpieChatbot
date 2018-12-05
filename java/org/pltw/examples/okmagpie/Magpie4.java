package org.pltw.examples.okmagpie;

import android.content.Context;

/**
 * A program to carry on conversations with a human user.
 * This version:
 *<ul><li>
 *      Uses advanced search for keywords 
 *</li><li>
 *      Will transform statements as well as react to keywords
 *</li></ul>
 * @author Laurie White
 * @version April 2012
 *
 */
public class Magpie4
{
    private ResponseLoader responseLoader;
    private ResponseLoader.LoadedResponse res;
    private Context context;

    public Magpie4(Context context)
    {
        responseLoader = new ResponseLoader(context);
        res = responseLoader.getLoadedResponse();
    }
    
    /**
     * Gives a response to a user statement
     * 
     * @param statement
     *            the user statement
     * @return a response based on the rules given
     */
    public String getResponse(String statement)
    {
        String response = "";
        if (statement.length() == 0)
        {
            response = res.getRandomResponse();
        }

        //Keywords

        else if (findKeyword(statement, "no") >= 0)
        {
            response = "Why so negative?";
        }
        else if (findKeyword(statement, "mother") >= 0
                || findKeyword(statement, "father") >= 0
                || findKeyword(statement, "sister") >= 0
                || findKeyword(statement, "brother") >= 0)
        {
            response = "Tell me more about your family.";
        }
        else if (findKeyword(statement, "nike") >= 0) {
            response = res.getRandomResponseForKeyword("nikeQuestion");
        } else if (findKeyword(statement, "shoe") >= 0 || findKeyword(statement, "shoes") >= 0) {
            response = res.getRandomResponseForKeyword("bestShoeQuestion");
        } else if (findKeyword(statement, "not") >= 0 && findKeyword(statement, "not") < findKeyword(statement, "sport")) {
            response = res.getRandomResponseForKeyword("sport");
        } else if (findKeyword(statement, "dab") >= 0) {
            response = res.getRandomResponseForKeyword("dab");
        } else if (findKeyword(statement, "camp") >= 0) {
            response = res.getRandomResponseForKeyword("camp");
        } else if (findKeyword(statement, "how") >= 0
                && findKeyword(statement, "run") >= 0
                && findKeyword(statement, "run") > findKeyword(statement, "you")
                && findKeyword(statement, "you") > findKeyword(statement, "how")) {
            response = res.getRandomResponseForKeyword("howDoYouRun");
        } else if ((findKeyword(statement, "how") >= 0
                && findKeyword(statement, "run") >= 0
                && findKeyword(statement, "run") > findKeyword(statement, "i")
                && findKeyword(statement, "i") > findKeyword(statement, "how"))
                || findKeyword(statement,"form") >= 0) {
            response = res.getRandomResponseForKeyword("form");
        } else if (findKeyword(statement, "favorite") >= 0
                && findKeyword(statement, "favorite") < findKeyword(statement, "food")) {
            response = res.getRandomResponseForKeyword("favoriteFoodQuestion");
        } else if (findKeyword(statement,"shoe") >= 0) {
            response = res.getRandomResponseForKeyword("bestShoeQuestion");
        } else if (findKeyword(statement, "coach") >= 0
                && findKeyword(statement, "kleinow") > findKeyword(statement, "coach")) {
            response = res.getRandomResponseForKeyword("kcoachQuestion");
        } else if (findKeyword(statement, "coach") >= 0
                && findKeyword(statement, "vidrio") > findKeyword(statement, "coach")) {
            response = res.getRandomResponseForKeyword("vcoachQuestion");
        } else if (findKeyword(statement, "coach") >= 0) {
            response = res.getRandomResponseForKeyword("coachQuestion");
        } else if (findKeyword(statement, "role model") >= 0) {
            response = res.getRandomResponseForKeyword("roleModelQuestion");
        }

        // Responses which require transformations
        else if (findKeyword(statement, "I want to", 0) >= 0)
        {
            response = transformIWantToStatement(statement);
        } else if (findKeyword(statement, "I want", 0) >= 0) {
            response = transformWantStatement(statement);
        }

        else
        {
            // Look for a two word (you <something> me)
            // pattern
            int psn = findKeyword(statement, "you", 0);

            if (psn >= 0
                    && findKeyword(statement, "me", psn) >= 0)
            {
                response = transformYouMeStatement(statement);
            }
            else
            {
                response = getRandomResponse();
            }
        }
        return response;
    }
    
    /**
     * Take a statement with "I want to <something>." and transform it into 
     * "What would it mean to <something>?"
     * @param statement the user statement, assumed to contain "I want to"
     * @return the transformed statement
     */
    private String transformIWantToStatement(String statement)
    {
        //  Remove the final period, if there is one
        statement = statement.trim();
        String lastChar = statement.substring(statement
                .length() - 1);
        if (lastChar.equals("."))
        {
            statement = statement.substring(0, statement
                    .length() - 1);
        }
        int psn = findKeyword (statement, "I want to", 0);
        String restOfStatement = statement.substring(psn + 9).trim();
        return "What would it mean to " + restOfStatement + "?";
    }

    
    
    /**
     * Take a statement with "you <something> me" and transform it into 
     * "What makes you think that I <something> you?"
     * @param statement the user statement, assumed to contain "you" followed by "me"
     * @return the transformed statement
     */
    private String transformYouMeStatement(String statement)
    {
        //  Remove the final period, if there is one
        statement = statement.trim();
        String lastChar = statement.substring(statement
                .length() - 1);
        if (lastChar.equals("."))
        {
            statement = statement.substring(0, statement
                    .length() - 1);
        }
        
        int psnOfYou = findKeyword (statement, "you", 0);
        int psnOfMe = findKeyword (statement, "me", psnOfYou+3);
        
        String restOfStatement = statement.substring(psnOfYou + 3, psnOfMe).trim().replace("are", "am");
        return "What makes you think that I " + restOfStatement + " you?";
    }
    
    private String transformWantStatement(String statement)
    {
        //  Remove the final period, if there is one
        statement = statement.trim();
        String lastChar = statement.substring(statement
                .length() - 1);
        if (lastChar.equals("."))
        {
            statement = statement.substring(0, statement
                    .length() - 1);
        }
        
        int psnOfWant = findKeyword (statement, "want", 0);

        
        String restOfStatement = statement.substring(psnOfWant + 4).trim();
        return "Would you really be happy if you had " + restOfStatement + "?";
    }
    
    

    
    
    /**
     * Search for one word in phrase.  The search is not case sensitive.
     * This method will check that the given goal is not a substring of a longer string
     * (so, for example, "I know" does not contain "no").  
     * @param statement the string to search
     * @param goal the string to search for
     * @param startPos the character of the string to begin the search at
     * @return the index of the first occurrence of goal in statement or -1 if it's not found
     */
    private int findKeyword(String statement, String goal, int startPos)
    {
        String phrase = statement.trim();
        //  The only change to incorporate the startPos is in the line below
        int psn = phrase.toLowerCase().indexOf(goal.toLowerCase(), startPos);
        
        //  Refinement--make sure the goal isn't part of a word 
        while (psn >= 0) 
        {
            //  Find the string of length 1 before and after the word
            String before = " ", after = " "; 
            if (psn > 0)
            {
                before = phrase.substring (psn - 1, psn).toLowerCase();
            }
            if (psn + goal.length() < phrase.length())
            {
                after = phrase.substring(psn + goal.length(), psn + goal.length() + 1).toLowerCase();
            }
            
            //  If before and after aren't letters, we've found the word
            if (((before.compareTo ("a") < 0 ) || (before.compareTo("z") > 0))  //  before is not a letter
                    && ((after.compareTo ("a") < 0 ) || (after.compareTo("z") > 0)))
            {
                return psn;
            }
            
            //  The last position didn't work, so let's find the next, if there is one.
            psn = phrase.indexOf(goal.toLowerCase(), psn + 1);
            
        }
        
        return -1;
    }
    
    /**
     * Search for one word in phrase.  The search is not case sensitive.
     * This method will check that the given goal is not a substring of a longer string
     * (so, for example, "I know" does not contain "no").  The search begins at the beginning of the string.  
     * @param statement the string to search
     * @param goal the string to search for
     * @return the index of the first occurrence of goal in statement or -1 if it's not found
     */
    private int findKeyword(String statement, String goal)
    {
        return findKeyword (statement, goal, 0);
    }
    


    /**
     * Pick a default response to use if nothing else fits.
     * @return a non-committal string
     */
    private String getRandomResponse()
    {
        return res.getRandomResponseForKeyword("random");
    }

}
