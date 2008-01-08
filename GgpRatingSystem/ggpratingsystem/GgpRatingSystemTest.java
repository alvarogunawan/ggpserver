package ggpratingsystem;

import java.io.File;

import ggpratingsystem.util.Util;

import com.martiansoftware.jsap.CommandLineTokenizer;
import com.martiansoftware.jsap.JSAPException;

import junit.framework.TestCase;

public class GgpRatingSystemTest extends TestCase {

	
	public void testMain() throws Exception {
		System.err.println("\n\n\n\n\n\n\n\n\n\n");
		
		String cmdLine = 
			" --input-dir " + (new File(Util.getDataDir(), "2007_preliminaries")).toString()
			+ " --output-dir " + "/tmp/ggp-rating-system/"
			+ " --linear-regression-rating"
			+ " --csv-output"
			+ " --debug-level ALL";
		String[] args = CommandLineTokenizer.tokenize(cmdLine);
		
		GgpRatingSystem.main(args);
	}

	public void testMainEmptyArgs() throws Exception {
		System.err.println("\n\n\n\n\n\n\n\n\n\n");

		String cmdLine = "";
		String[] args = CommandLineTokenizer.tokenize(cmdLine);
		try {
			GgpRatingSystem.main(args);
		} catch (JSAPException e) {
			return;	// this is expected
		}
		fail("should throw an exception!");
	}

	public void testMainHelp() throws Exception {
		System.err.println("\n\n\n\n\n\n\n\n\n\n");

		String cmdLine = " --help";
		String[] args = CommandLineTokenizer.tokenize(cmdLine);
		try {
			GgpRatingSystem.main(args);
		} catch (JSAPException e) {
			return;	// this is expected
		}
		fail("should throw an exception!");
	}
}
