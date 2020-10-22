package simulator.launcher;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

/*
 * Examples of command-line parameters:
 * 
 *  -h
 *  -i resources/examples/ex4.4body.txt -s 100
 *  -i resources/examples/ex4.4body.txt -o resources/examples/ex4.4body.out -s 100
 *  -i resources/examples/ex4.4body.txt -o resources/examples/ex4.4body.out -s 100 -gl ftcg
 *  -i resources/examples/ex4.4body.txt -o resources/examples/ex4.4body.out -s 100 -gl nlug
 *
 */

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.factories.BasicBodyBuilder;
import simulator.factories.Builder;
import simulator.factories.BuilderBasedFactory;
import simulator.factories.Factory;
import simulator.factories.FallingToCenterGravityBuilder;
import simulator.factories.MassLosingBodyBuilder;
import simulator.factories.NewtonUniversalGravitationBuilder;
import simulator.factories.NoGravityBuilder;
import simulator.model.Body;
import simulator.model.FallingToCenterGravity;
import simulator.model.GravityLaws;
import simulator.model.NewtonUniversalGravitation;
import simulator.model.NoGravity;
import simulator.model.PhysicsSimulator;
import simulator.view.MainWindow;

public class Main {

	// default values for some parameters
	//
	private final static Double _dtimeDefaultValue = 2500.0;
	private final static Double _stepsDefaultValue = 150.0;
	private static String _modeDefault = "batch";

	// some attributes to stores values corresponding to command-line parameters
	//
	private static Double _dtime = null;
	private static String _inFile = null;
	private static JSONObject _gravityLawsInfo = null;
	private static String _mode = null;

	// factories
	private static Factory<Body> _bodyFactory;
	private static Factory<GravityLaws> _gravityLawsFactory;
	
	private static String _outFile = null;
	private static Double _steps = null;
	private static GravityLaws gravityLaws = null;

	private static void init() {
		// initialize the bodies factory
		ArrayList<Builder<Body>> bodyBuilders = new ArrayList<>(); 
		bodyBuilders.add(new BasicBodyBuilder()); 
		bodyBuilders.add(new MassLosingBodyBuilder()); 
		_bodyFactory = new BuilderBasedFactory<Body>(bodyBuilders);
		
		// initialize the gravity laws factory
		ArrayList<Builder<GravityLaws>> gravityLawsBuilders = new ArrayList<>(); 
		gravityLawsBuilders.add(new NewtonUniversalGravitationBuilder()); 
		gravityLawsBuilders.add(new FallingToCenterGravityBuilder());
		gravityLawsBuilders.add(new NoGravityBuilder()); 
		_gravityLawsFactory = new BuilderBasedFactory<GravityLaws>(gravityLawsBuilders);

		
	}

	private static void parseArgs(String[] args) {
		// define the valid command line options
		//
		Options cmdLineOptions = buildOptions();

		
		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			
			parseHelpOption(line, cmdLineOptions);
			parseInFileOption(line);
			parseOutFileOption(line);
			parseSteps(line);
			parseDeltaTimeOption(line);
			parseGravityLawsOption(line);
			parseaOpcionGUI(line);

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}
	}

	private static Options buildOptions() {
		Options cmdLineOptions = new Options();

		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());
		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("Bodies JSON input file.").build());
		// output file
		cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg().desc("output file").build());
		// steps
		cmdLineOptions.addOption(Option.builder("s").longOpt("steps").hasArg()
				.desc("An integer representing the number of simulation steps. Default value: "
						+ _stepsDefaultValue + ".").build());
		// delta-time
		cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg()
				.desc("A double representing actual time, in seconds, per simulation step. Default value: "
						+ _dtimeDefaultValue + ".").build());
		// mode
		cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg()
				.desc("Execution Mode. Possible values: ’batch’ (Batch mode), ’gui’ (Graphical User Interface mode). Default value: "
						+ _modeDefault + ".").build());

		
		// gravity laws -- there is a workaround to make it work even when
		// _gravityLawsFactory is null. 
		//
		String gravityLawsValues = "N/A";
		String defaultGravityLawsValue = "N/A";
		if (_gravityLawsFactory != null) {
			gravityLawsValues = "";
			for (JSONObject fe : _gravityLawsFactory.getInfo()) {
				if (gravityLawsValues.length() > 0) {
					gravityLawsValues = gravityLawsValues + ", ";
				}
				gravityLawsValues = gravityLawsValues + "'" + fe.getString("type") + "' (" + fe.getString("desc") + ")";
			}
			defaultGravityLawsValue = _gravityLawsFactory.getInfo().get(0).getString("type");
		}
		cmdLineOptions.addOption(Option.builder("gl").longOpt("gravity-laws").hasArg()
				.desc("Gravity laws to be used in the simulator. Possible values: " + gravityLawsValues
						+ ". Default value: '" + defaultGravityLawsValue + "'.")
				.build());

		
		return cmdLineOptions;
	}

	private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parseInFileOption(CommandLine line) throws ParseException {
		_inFile = line.getOptionValue("i");
	}

	private static void parseOutFileOption(CommandLine line) throws ParseException {
		_outFile = line.getOptionValue("o");
		/*if (_outFile == null) {
			throw new ParseException("An output file of bodies is required");
		}*/
	}
	
	private static void parseaOpcionGUI (CommandLine linea) {
		_mode = linea.getOptionValue("m",_modeDefault);
		
		if(linea.hasOption("m")) {
			_modeDefault = linea.getOptionValue("m").toLowerCase();
			if (linea.getOptionValue("m").toLowerCase().equals("batch")) {
				try {
					startBatchMode();
				} catch (IOException e) {
					e.printStackTrace();
				}catch(Exception e) {
					System.err.println("no se encuentra la ruta introducida");
					System.exit(1);
				}
			}
			else {
				try {
					startGuiMode();
				} catch (FileNotFoundException | InvocationTargetException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}else {
			try {
				startGuiMode();
			} catch (FileNotFoundException | InvocationTargetException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

	
	private static void parseSteps(CommandLine line) throws ParseException {
		String s = line.getOptionValue("s", _dtimeDefaultValue.toString());
		try {
			_steps = Double.parseDouble(s);
			assert (_steps > 0);
		} catch (Exception e) {
			throw new ParseException("Invalid delta-time value: " + s);
		}
		
	}
	
	private static void parseDeltaTimeOption(CommandLine line) throws ParseException {
		String dt = line.getOptionValue("dt", _dtimeDefaultValue.toString());
		try {
			_dtime = Double.parseDouble(dt);
			assert (_dtime > 0);
		} catch (Exception e) {
			throw new ParseException("Invalid delta-time value: " + dt);
		}
	}

	private static void parseGravityLawsOption(CommandLine line) throws ParseException {

		// this line is just a work around to make it work even when _gravityLawsFactory
		// is null, you can remove it when've defined _gravityLawsFactory
		if (_gravityLawsFactory == null)
			return;

		String gl = line.getOptionValue("gl");
		if (gl != null) {
			for (JSONObject fe : _gravityLawsFactory.getInfo()) {
				if (gl.equals(fe.getString("type"))) {
					_gravityLawsInfo = fe;
					break;
				}
			}
			if (_gravityLawsInfo == null) {
				throw new ParseException("Invalid gravity laws: " + gl);
			}
		} else {
			_gravityLawsInfo = _gravityLawsFactory.getInfo().get(0);
		}
	}

	public static GravityLaws creaLey(String i) {
		if(i.equals("nlug")) {
			return new NewtonUniversalGravitation();
		}else if(i.equals("ftcg")) {
			return new FallingToCenterGravity();
		}else if(i.equals("ng")) {
			return new NoGravity();
		}
		return null;
	}

	private static void startBatchMode() throws Exception, IOException {
		// create and connect components, then start the simulator

		InputStream is = new FileInputStream(new File(Main._inFile));
		OutputStream os = Main._outFile == null ? System.out : new FileOutputStream(new File(Main._outFile));
		_gravityLawsFactory.createInstance(Main._gravityLawsInfo);
		String c = _gravityLawsInfo.getString("type");
		gravityLaws = creaLey(c);
		PhysicsSimulator sim = new PhysicsSimulator(Main.gravityLaws, Main._dtime);
		Controller ctrl = new Controller(sim,Main._bodyFactory, _gravityLawsFactory); 
		ctrl.loadBodies(is); 
		ctrl.run(Main._steps, os);
		is.close();	
	}

	private static void startGuiMode() throws FileNotFoundException, InvocationTargetException, InterruptedException{
		_gravityLawsFactory.createInstance(Main._gravityLawsInfo);
		String c = _gravityLawsInfo.getString("type");
		gravityLaws = creaLey(c);
		PhysicsSimulator sim = new PhysicsSimulator(Main.gravityLaws, Main._dtime);
		Controller ctrl = new Controller(sim,Main._bodyFactory, _gravityLawsFactory); 
		
		SwingUtilities.invokeAndWait(new Runnable() { 
			@Override 
			public void run() {
				new MainWindow(ctrl); 
			}
		});
		if(_inFile != null) {
			InputStream is = new FileInputStream(new File(Main._inFile));
			ctrl.loadBodies(is);
		}


	}

	private static void start(String[] args) throws Exception {
		parseArgs(args);
		/*if(_mode.equals(_dtimeDefaultValue)) {
			startBatchMode();
		}else {
			startGuiMode();
		}*/
	}

	public static void main(String[] args) {
		try {
			init();
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}

	}
}
