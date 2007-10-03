package edu.psu.geovista.geoviz.spreadsheet.functions;

import edu.psu.geovista.geoviz.spreadsheet.exception.NoReferenceException;
import edu.psu.geovista.geoviz.spreadsheet.exception.ParserException;
import edu.psu.geovista.geoviz.spreadsheet.formula.Node;

/*
 * Description:
 * Date: Mar 30, 2003
 * Time: 3:58:17 PM
 * @author Jin Chen
 */

public class FunctionAbs extends FunctionSP {

    protected Number doFun(Node node)throws ParserException,NoReferenceException {
        return new Float(Math.abs(getSingleParameter(node)));
    }

    public String getUsage() {
        return "ABS(value)";
    }

    public String getDescription() {
        return "Returns the absolute value of a number.";
    }
}
