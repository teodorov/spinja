// Copyright 2010, University of Twente, Formal Methods and Tools group
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package spinja;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import spinja.options.BooleanOption;
import spinja.options.MultiStringOption;
import spinja.options.OptionParser;
import spinja.options.StringOption;
import spinja.util.InputStreamPrinter;
import spinja.promela.compiler.Proctype;
import spinja.promela.compiler.Specification;
import spinja.promela.compiler.optimizer.GraphOptimizer;
import spinja.promela.compiler.optimizer.RemoveUselessActions;
import spinja.promela.compiler.optimizer.RemoveUselessGotos;
import spinja.promela.compiler.optimizer.RenumberAll;
import spinja.promela.compiler.optimizer.StateMerging;
import spinja.promela.compiler.parser.ParseException;
import spinja.promela.compiler.parser.Promela;

public class Compile {
	private static Specification compile(final File promFile, 
	                                     final String name,
		                                 final boolean useStateMerging,
		                                 final boolean verbose) {
		try {
			if (verbose)
				System.out.print("Start parsing " + promFile.getName() + "...");
			final Promela prom = new Promela(new FileInputStream(promFile));
			final Specification spec = prom.spec(name);
			if (verbose)
				System.out.println("done");

			if (verbose) 
				System.out.println("Optimizing graphs...");
			final GraphOptimizer[] optimizers = new GraphOptimizer[] {
					useStateMerging ? new StateMerging() : null, new RemoveUselessActions(),
					new RemoveUselessGotos(), new RenumberAll(),
			};
			for (final Proctype proc : spec) {
				if (verbose) {
					System.out.println("Initial graph for process " + proc + ":");
					System.out.println(proc.getAutomaton());
				}
				for (final GraphOptimizer opt : optimizers) {
					if (opt == null)
						continue;
					opt.optimize(proc.getAutomaton());
					if (verbose) {
						System.out.println("After " + opt.getClass().getSimpleName() + ":");
						System.out.println(proc.getAutomaton());
					}
				}
				if (verbose)
					System.out.println(proc.getAutomaton());
			}

			final Proctype never = spec.getNever();
			if (never != null) {
				if (verbose) {
					System.out.println("Initial graph for never claim:");
					System.out.println(never.getAutomaton());
				}
				for (final GraphOptimizer opt : optimizers) {
					if (opt != null) {
						opt.optimize(never.getAutomaton());
					}
					if (verbose) {
						System.out.println("After " + opt.getClass().getSimpleName() + ":");
						System.out.println(never.getAutomaton());
					}
				}
				if (verbose)
					System.out.println(never.getAutomaton());
			}
			if (verbose)
				System.out.println("Optimization done");

			return spec;
		} catch (final FileNotFoundException ex) {
			System.out.println("Promela file " + promFile.getName() + " could not be found.");
		} catch (final ParseException ex) {
			System.out.println("Parse exception in file " + promFile.getName() + ": "
								+ ex.getMessage());
		}
		return null;
	}

	private static void compileFiles(final String name, final File outputDir, final File userDir) {
		final String command = "javac -target 5 -cp \"" + System.getProperty("java.class.path")
								+ "\" \"" + outputDir.getAbsolutePath()
								+ System.getProperty("file.separator") + name + "Model.java\"";
		System.out.println(command);
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(command, null, userDir);
		} catch (final IOException ex) {
			System.out.println("Could not start compiler: " + ex.toString());
			System.exit(-7);
		}
		try {
			new InputStreamPrinter(p.getInputStream()).start();
			new InputStreamPrinter(p.getErrorStream()).start();
			switch (p.waitFor()) {
				case 0:
					System.out.println("Compiled succesfully.");
					break;
				default:
					System.out.println("Error while compiling.");
					return;
			}
		} catch (final InterruptedException ex) {
			assert false;
			// Should not be able to happen!
		}
	}

	private static void createJar(final String name, final boolean hasNever, final File outputDir) {
		try {
			final File jarFile = new File(System.getProperty("user.dir"), name + ".jar");
			final ZipOutputStream zipFile = new ZipOutputStream(new FileOutputStream(jarFile));
			zipFile.putNextEntry(new ZipEntry("META-INF/MANIFEST.MF"));
			final String line = "Generated-By: SpinJa\n" + "Main-Class: spinja.generated." + name
								+ "Model" + (hasNever ? ".never\n" : "\n")
								+ "Class-Path: SpinJa.jar\n\n";
			zipFile.write(line.getBytes());
			zipFile.closeEntry();
			final File[] files = outputDir.listFiles();
			for (final File file : files) {
				zipFile.putNextEntry(new ZipEntry("spinja/generated/" + file.getName()));
				final FileInputStream is = new FileInputStream(file);
				final byte[] buffer = new byte[65536];
				int i = -1;
				while ((i = is.read(buffer)) != -1) {
					zipFile.write(buffer, 0, i);
				}
				zipFile.closeEntry();
				is.close();
			}
			zipFile.flush();
			zipFile.close();
		} catch (final IOException ex) {
			System.out.println("IOException while writer jar-file: " + ex.getMessage());
			System.exit(-8);
		}
	}

	private static void delete(final File file) {
		if (file.isDirectory()) {
			for (final File inner : file.listFiles()) {
				Compile.delete(inner);
			}
		}
		file.delete();
	}

	private static File generateTmpDir(final File userDir) {
		File tempDir = null;
		while ((tempDir == null) || tempDir.exists()) {
			tempDir = new File(userDir, String.format("tmp%05d",
				Math.abs(new Random().nextInt()) % 100000));
		}
		return tempDir;
	}

	private static String getName(final File file) {
		final String filename = file.getAbsolutePath();
		final int lastSlash = filename.lastIndexOf(System.getProperty("file.separator"));
		int lastDot = filename.lastIndexOf('.');
		if ((lastDot == -1) || (lastDot <= lastSlash)) {
			lastDot = filename.length();
		}
		final StringBuilder sb = new StringBuilder(lastDot - lastSlash - 1);
		for (int i = lastSlash + 1; i < lastDot; i++) {
			final char c = filename.charAt(i);
			if (((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'))
				|| ((i != lastSlash + 1) && (c >= '0') && (c <= '9'))) {
				sb.append(c);
			} else {
				sb.append('_');
			}
		}
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString();
	}

	public static void main(final String[] args) {
		final Version version = new Version();
		final String  defaultname = "Pan";
		final String  shortd  = 
			"SpinJa Promela Compiler - version " + version.VERSION + " (" + version.DATE + ")\n" +
			"(C) University of Twente, Formal Methods and Tools group";
		final String  longd   = 
			"SpinJa Promela Compiler: this compiler converts a Promela source file\n" +
			"to a Java model that can be checked by the SpinJa Model Checker." ;

		final OptionParser parser = 
			new OptionParser("java spinja.Compiler", shortd, longd, true);

		final StringOption modelname = new StringOption('n',
			"sets the name of the model \n" +
			"(default: " + defaultname + ")");
		parser.addOption(modelname);

// [22-Mar-2010 16:00 ruys] For the time being, we disable the "create jar" option.
//		final BooleanOption createjar = new BooleanOption('j',
//			"Creates an easy to execute jar-file.");
//		parser.addOption(createjar);

		final StringOption srcDir = new StringOption('s',
			"sets the output directory for the sourcecode \n" +
			"(default: spinja)");
		parser.addOption(srcDir);

		final MultiStringOption optimalizations = new MultiStringOption('o',
			"disables one or more optimalisations", 
				new String[] { "3" }, 
				new String[] { "disables statement merging" }
			);
		parser.addOption(optimalizations);

		final BooleanOption verbose = new BooleanOption('v',
			"verbose: show diagnostic information on the \n" +
			"compilation process.");
		parser.addOption(verbose);

		parser.parse(args);
		final List<String> files = parser.getFiles();

		if (files.size() != 1) {
			System.out.println("Please specify one file that is to be compiled!");
			parser.printUsage();
		}

		final File file = new File(files.get(0));
		if (!file.exists() || !file.isFile()) {
			System.out.println("File " + file.getName() + " does not exist or is not a valid file!");
			parser.printUsage();
		}

		String name = null;
		if (modelname.isSet()) {
			name = modelname.getValue();
		} else {
			name = defaultname;
			// [07-Apr-2010 10:45 ruys] 
			//   The default name is now "Pan". The original version of SpinJa ..
			//   .. used getName(file) to construct the name for the model.
			// name = Compile.getName(file);
		}

		final Specification spec = 
			Compile.compile(file, name, !optimalizations.isSet("3"), verbose.isSet());

		if (spec == null) {
			System.exit(-4);
		}

		final File userDir = new File(System.getProperty("user.dir"));
		File outputDir = null;
// [22-Mar-2010 16:00 ruys] For the time being, we disable the "create jar" option.
//		if (!createjar.isSet()) {
			if (srcDir.isSet()) {
				outputDir = new File(userDir, srcDir.getValue());
			} else {
				outputDir = new File(userDir, "spinja");
//				[07-Apr-2010 12:20 ruys] was: outputDir = new File(new File(userDir, "spinja"), "generated");
			}
//		} else {
//			outputDir = Compile.generateTmpDir(userDir);
//		}
		if (!outputDir.exists() && !outputDir.mkdirs()) {
			System.out.println("Error: could not generate directory " + outputDir.getName());
			System.exit(-3);
		}

		Compile.writeFiles(spec, name, outputDir);
		System.out.println("Written Java files for '" + file + "' to\n" + outputDir);

// [22-Mar-2010 16:00 ruys] For the time being, we disable the "create jar" option.
//		if (createjar.isSet()) {
//			Compile.compileFiles(name, outputDir, userDir);
//			Compile.createJar(name, spec.getNever() != null, outputDir);
//			Compile.delete(outputDir);
//		}
	}

	private static void writeFiles(final Specification spec, final String name, final File outputDir) {
		final File javaFile = new File(outputDir, name + "Model.java");

		try {
			final FileOutputStream fos = new FileOutputStream(javaFile);

			fos.write(spec.generateModel().getBytes());
			fos.flush();
			fos.close();
		} catch (final IOException ex) {
			System.out.println("IOException while writing java files: " + ex.getMessage());
			System.exit(-5);
		} catch (final ParseException ex) {
			System.out.println("Parse exception: " + ex.getMessage());
			System.exit(-6);
		}
	}
}