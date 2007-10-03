/*
 * Write files in comma separated value format.
 * Copyright (C) 2001,2002 Stephen Ostermiller <utils@Ostermiller.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * See COPYING.TXT for details.
 */

package edu.psu.geovista.readers.csv;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * Print values as a comma separated list.
 * More information about this class is available from <a href=
 * "http://ostermiller.org/utils/CSVLexer.html">ostermiller.org</a>.
 */
public class CSVPrinter implements CSVPrint {

	/**
	 * The place that the values get written.
	 */
	protected transient PrintWriter out;

	/**
	 * True iff we just began a new line.
	 */
	protected transient boolean newLine = true;

	/**
	 * Character used to start comments. (Default is '#')
	 */
	protected transient char commentStart = '#';

	/**
	 * Create a printer that will print values to the given
	 * stream.	 Character to byte conversion is done using
	 * the default character encoding.	Comments will be
	 * written using the delault comment character '#'.
	 *
	 * @param out stream to which to print.
	 */
	public CSVPrinter (OutputStream out){
		this.out = new PrintWriter(out);
	}

	/**
	 * Create a printer that will print values to the given
	 * stream.	Comments will be
	 * written using the delault comment character '#'.
	 *
	 * @param out stream to which to print.
	 */
	public CSVPrinter (Writer out){
		if (out instanceof PrintWriter){
			this.out = (PrintWriter)out;
		} else {
			this.out = new PrintWriter(out);
		}
	}

	/**
	 * Create a printer that will print values to the given
	 * stream.	 Character to byte conversion is done using
	 * the default character encoding.
	 *
	 * @param out stream to which to print.
	 * @param commentStart Character used to start comments.
	 */
	public CSVPrinter (OutputStream out, char commentStart){
		this(out);
		this.commentStart = commentStart;
	}

	/**
	 * Create a printer that will print values to the given
	 * stream.
	 *
	 * @param out stream to which to print.
	 * @param commentStart Character used to start comments.
	 */
	public CSVPrinter (Writer out, char commentStart){
		this(out);
		this.commentStart = commentStart;
	}

	/**
	 * Print the string as the last value on the line.	The value
	 * will be quoted if needed.
	 *
	 * @param value value to be outputted.
	 */
	public void println(String value){
		print(value);
		out.println();
        out.flush();
		newLine = true;
	}

	/**
	 * Output a blank line
	 */
	public void println(){
		out.println();
        out.flush();
		newLine = true;
	}

	/**
	 * Print a single line of comma separated values.
	 * The values will be quoted if needed.  Quotes and
	 * newLine characters will be escaped.
	 *
	 * @param values values to be outputted.
	 */
	public void println(String[] values){
		for (int i=0; i<values.length; i++){
			print(values[i]);
		}
		out.println();
        out.flush();
		newLine = true;
	}

    /**
	 * Print several lines of comma separated values.
	 * The values will be quoted if needed.  Quotes and
	 * newLine characters will be escaped.
	 *
	 * @param values values to be outputted.
	 */
	public void println(String[][] values){
		for (int i=0; i<values.length; i++){
            println(values[i]);
		}
        if (values.length == 0){
		    out.println();
        }
        out.flush();
		newLine = true;
	}

	/**
	 * Put a comment amoung the comma separated values.
	 * Comments will always begin on a new line and occupy at
	 * least one full line. The character specified to start
	 * comments and a space will be inserted at the beginning of
	 * each new line in the comment.
	 *
	 * @param comment the comment to output
	 */
	public void printlnComment(String comment){
		if (!newLine){
			out.println();
		}
		out.print(commentStart);
		out.print(' ');
		for (int i=0; i<comment.length(); i++){
			char c = comment.charAt(i);
			switch (c){
				case '\r': {
					if (i+1 < comment.length() && comment.charAt(i+1) == '\n'){
						i++;
					}
				} //break intentionally excluded.
				case '\n': {
					out.println();
					out.print(commentStart);
					out.print(' ');
				} break;
				default: {
					out.print(c);
				} break;
			}
		}
		out.println();
		out.flush();
		newLine = true;
	}

	/**
	 * Print the string as the next value on the line.	The value
	 * will be quoted if needed.
	 *
	 * @param value value to be outputted.
	 */
	public void print(String value){
		boolean quote = false;
		if (value.length() > 0){
			char c = value.charAt(0);
			if (newLine && (c<'0' || (c>'9' && c<'A') || (c>'Z' && c<'a') || (c>'z'))){
				quote = true;
			}
			if (c==' ' || c=='\f' || c=='\t'){
				quote = true;
			}
			for (int i=0; i<value.length(); i++){
				c = value.charAt(i);
				if (c=='"' || c==',' || c=='\n' || c=='\r'){
					quote = true;
				}
			}
			if (c==' ' || c=='\f' || c=='\t'){
				quote = true;
			}
		} else if (newLine) {
            // always quote an empty token that is the first
            // on the line, as it may be the only thing on the
            // line.  If it were not quoted in that case,
            // an empty line has no tokens.
            quote = true;
        }
		if (newLine){
			newLine = false;
		} else {
			out.print(",");
		}
		if (quote){
			out.print(escapeAndQuote(value));
		} else {
			out.print(value);
		}
		out.flush();
	}

	/**
	 * enclose the value in quotes and escape the quote
	 * and comma characters that are inside.
	 *
	 * @param value needs to be escaped and quoted
	 * @return the value, escaped and quoted.
	 */
	private static String escapeAndQuote(String value){
        int count = 2;
        for (int i=0; i<value.length(); i++){
            switch(value.charAt(i)){
                case '\"': case '\n': case '\r': case '\\': {
                    count ++;
                } break;
            }
		}
		StringBuffer sb = new StringBuffer(value.length() + count);
		sb.append('"');
		for (int i=0; i<value.length(); i++){
            char c = value.charAt(i);
            switch(c){
                case '\"': {
				    sb.append("\\\"");
                } break;
                case '\n': {
				    sb.append("\\n");
                } break;
                case '\r': {
				    sb.append("\\r");
                } break;
                case '\\': {
				    sb.append("\\\\");
                } break;
                default: {
                    sb.append(c);
                }
            }
		}
		sb.append('"');
		return (sb.toString());
	}

	/**
	 * Write some test data to the given file.
	 *
	 * @param args First argument is the file name.  System.out used if no filename given.
	 */
	public static void main(String[] args) {
		OutputStream out;
		try {
			if (args.length > 0){
				File f = new File(args[0]);
				if (!f.exists()){
                    f.createNewFile();
					if (f.canWrite()){
						out = new FileOutputStream(f);
					} else {
						throw new IOException("Could not open " + args[0]);
					}
				} else {
					throw new IOException("File already exists: " + args[0]);
				}
			} else {
				out = System.out;
			}
			CSVPrinter p  = new CSVPrinter(out);
			p.print("unquoted");
            p.print("un\\quoted");
			p.print("escaped\"quote");
			p.print("escaped\"quote\\");
			p.println("comma,comma");
			p.print("!quoted");
			p.print("!unquoted");
			p.print(" quoted");
			p.print("quoted ");
			p.printlnComment("A comment.");
			p.print("one");
			p.print("");
			p.print("");
			p.print("");
			p.print("");
			p.printlnComment("Multi\nLine\rComment\r\nto test line breaks\r");
			p.println("two");
			p.printlnComment("Comment after explicit new line.");
			p.print("\nthree\nline\n");
			p.println("\ttab");
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}
