/**
 * GeoVISTA Center (Penn State, Dept. of Geography)
 * Copyright (c), 1999 - 2002, GeoVISTA Center
 * All Rights Researved.
 *
 * @author Jin Chen
 * This Exception is raised when geovista.geoviz.spreadsheet.formula.Formula fails in tokenizing or parsing the
 * geovista.geoviz.spreadsheet.formula.
 * @author Jin Chen
 */

package edu.psu.geovista.geoviz.spreadsheet.exception;



public class ParserException extends Exception {
    private boolean quiet;
    private String msg;

    /**
     * Contructor for geovista.geoviz.spreadsheet.exception.ParserException.  By default, sets quiet to true.
     */
    public ParserException() { quiet = true; };

    /**
     * @param msg the error message string
     */
    public ParserException(String msg) { super(msg); this.msg = msg; };

    /**
     * @param msg the error object
     */
    public ParserException(Object msg) {
	super(msg.toString());
	this.msg = msg.toString();
    };

    /**
     * This returns the value of quiet.
     *
     * @return true if quiet is true, false otherwise
     */
    public boolean isQuiet() { return quiet; }

    /**
     * toString method for geovista.geoviz.spreadsheet.exception.ParserException.
     *
     * @return the error message string
     */
    public String toString() { return msg; }
}










