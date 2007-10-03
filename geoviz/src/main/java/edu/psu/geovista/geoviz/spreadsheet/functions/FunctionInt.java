package edu.psu.geovista.geoviz.spreadsheet.functions;

import edu.psu.geovista.geoviz.spreadsheet.exception.NoReferenceException;
import edu.psu.geovista.geoviz.spreadsheet.exception.ParserException;
import edu.psu.geovista.geoviz.spreadsheet.formula.Node;

/*
 * Description:
 * Date: Apr 1, 2003
 * Time: 9:40:19 PM
 * @author Jin Chen
 */

public class FunctionInt extends FunctionSP {

    protected Number doFun(Node node)throws ParserException,NoReferenceException {
        return new Float((int)getSingleParameter(node));
    }

    public String getUsage() {
       return "INT(value)";
    }

    public String getDescription() {
        	return "Returns the integer part of a number.";
    }
}
