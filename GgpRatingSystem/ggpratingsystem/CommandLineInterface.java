package ggpratingsystem;


import com.martiansoftware.jsap.CommandLineTokenizer;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.stringparsers.EnumeratedStringParser;
import com.martiansoftware.jsap.stringparsers.FileStringParser;

public class CommandLineInterface extends SimpleJSAP {
	public static final String APPLICATION_CALL = "java -jar ggp_rating_system.jar";
	public static final String OPTION_INPUT_DIR = "input-dir";
	public static final String OPTION_OUTPUT_DIR = "output-dir";
	public static final String OPTION_LINEAR_REGRESSION = "linear-regression-rating";
	public static final String OPTION_CSV_OUTPUT = "csv-output";
	public static final String OPTION_GNUPLOT_OUTPUT = "gnuplot-output";
	public static final String OPTION_DEBUG_LEVEL = "debug-level";
	public static final String OPTION_HELP = "help";
	
	private boolean messagePrinted = false;
	
	public CommandLineInterface() throws JSAPException {
		super(
            APPLICATION_CALL, 
            "Reads a list of General Game Playing XML match files from the given "
            	+ "input directory, executes the selected rating algorithms on "
            	+ "the matches and writes the resulting player ratings to files "
            	+ "in the output directory.",
            new Parameter[] {
            /* Input directory */
                	// --input-dir, -i
                    new FlaggedOption(
    						OPTION_INPUT_DIR,
    						FileStringParser.getParser().setMustBeDirectory(true).setMustExist(true),
    						JSAP.NO_DEFAULT,
    						JSAP.REQUIRED,
    						'i',
    						OPTION_INPUT_DIR,
    						"The directory to read matches from. Must contain a file called "
    								+ "match_index.csv as well as the match XML files."),
                    // Checking readability of match_index.csv and XML files is done by
    				// MatchReader, which is fine (they have to be readable by the
    				// time MatchReader needs them; checking them now would not help
    				// much).
            
            /* Output directory */
                	// --output-dir, -o
                    new FlaggedOption(
    						OPTION_OUTPUT_DIR,
    						FileStringParser.getParser().setMustBeDirectory(true).setMustExist(false),
    						JSAP.NO_DEFAULT,
    						JSAP.REQUIRED,
    						'o',
    						OPTION_OUTPUT_DIR,
    						"The directory to write output files to."),
    				
    				// TODO: stdout; only valid if there is exactly one enabled rating algorithm
    				// and exactly one enabled output (and the output supports writing to a file,
    				// which is not the case for any Swing based GUI output, for example).
                    
            /* Rating algorithm selection */                    
            		// --linear-regression
    				new Switch(
    						OPTION_LINEAR_REGRESSION,
    						JSAP.NO_SHORTFLAG,
    						OPTION_LINEAR_REGRESSION,
    						"Enables the linear regression rating algorithm."),
                    	
            		// TODO --direct-scores

                    // --elo etc.
            		
            /* Output selection */
    				// --csv-output
    				new Switch(
    						OPTION_CSV_OUTPUT,
    						JSAP.NO_SHORTFLAG,
    						OPTION_CSV_OUTPUT,
    						"Enables CSV (comma separated values) output for all rating algorithms."),
    	    						
    				// --gnuplot-output
    				new Switch(
    						OPTION_GNUPLOT_OUTPUT,
    						JSAP.NO_SHORTFLAG,
    						OPTION_GNUPLOT_OUTPUT,
    						"Enables gnuplot output for all rating algorithms."),

    				// --elo-output etc.
            
            /* Debug level */
            	// --debug-level, -d
                    new FlaggedOption(
                    		OPTION_DEBUG_LEVEL,
                    		EnumeratedStringParser.getParser(
                    				"OFF; SEVERE; WARNING; INFO; CONFIG; FINE; FINER; FINEST; ALL",
                    				false, false),
                    		"INFO",
                    		JSAP.NOT_REQUIRED,
                    		'd',
                    		OPTION_DEBUG_LEVEL,
                    		"Sets the level of debug output. One of the following (in order of "
                    			+ "increasing verbosity): "
                    			+ "OFF; SEVERE; WARNING; INFO; CONFIG; FINE; FINER; FINEST; ALL")
                    
            /* Help */
            	// --help option is added by SimpleJSAP 
            }
        );
	}

	@Override
	public boolean messagePrinted() {
		return messagePrinted;
	}

	@Override
	public JSAPResult parse(String cmdLine) {
        String[] args = CommandLineTokenizer.tokenize(cmdLine);
        return (parse(args));
	}

	@Override
	public JSAPResult parse(String[] args) {
        JSAPResult config = super.parse(args);

        /* parameter checking */
        this.messagePrinted = super.messagePrinted();
        
        boolean existsEnabledRatingAlgorithm = config.getBoolean(OPTION_LINEAR_REGRESSION);
        			// add other rating algorithms here
        
        if (!existsEnabledRatingAlgorithm) {
        	messagePrinted = true;
        	if (!config.getBoolean(OPTION_HELP)) {
        		System.err.println("Error: At least one rating algorithm must be enabled.");
        	}
        }
        
        boolean existsEnabledOutputAlgorithm = config.getBoolean(OPTION_CSV_OUTPUT);
			// add other output algorithms here
			// TODO: gnuplot

		if (!existsEnabledOutputAlgorithm) {
			messagePrinted = true;
        	if (!config.getBoolean(OPTION_HELP)) {
        		System.err.println("Error: At least one output algorithm must be enabled.");
        	}
		}
		
		if (messagePrinted) {
        	// if user hasn't asked for help, "beat him with a clue stick", as the JSAP manual says
        	if (!config.getBoolean(OPTION_HELP)) {
        		System.err.println();
        		System.err.println("Type " + APPLICATION_CALL + " --" + OPTION_HELP + " for help.");
        	}
        	config.addException(null, new JSAPException("Help message printed."));
        }
		return config;
	}
}
