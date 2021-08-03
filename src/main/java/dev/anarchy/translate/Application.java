package dev.anarchy.translate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.velocity.runtime.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.anarchy.translate.util.TranslateMapService;
import dev.anarchy.translate.util.TranslateType;
import freemarker.template.TemplateException;

public class Application {
	private static TranslateMapService translateMapService;
	private static Scanner scanner;
	
	public static void main(String[] args) throws FileNotFoundException {
		scanner = new Scanner(System.in);

		System.out.println("************************************************");
		System.out.println("*** Velocity version: 1.7");
		System.out.println("*** Freemarker version: 2.3.14");
		System.out.println("*** Please ensure correct version is used.");
		System.out.println("************************************************");
		
		// Get Translation type
		String type = getArg(args, 0, "Please Input translate type: (0 for VELOCITY / 1 for FREEMARKER)");
		TranslateType tType = TranslateType.match(type);
		if ( tType == null ) {
			if ( "0".equals(type) ) {
				tType = TranslateType.VELOCITY;
			} else if ( "1".equals(type) ) {
				tType = TranslateType.FREEMARKER;
			} else {
				System.err.println("INVALID TRANSLATE TYPE");
				return;
			}
		}
		
		// Get template filepath
		String templateFilepath = getArg(args, 1, "Please input template filepath (Leave blank for " + getBlankTemplate(tType) + "):");
		if ( templateFilepath == null || "".equals(templateFilepath) )
			templateFilepath = getBlankTemplate(tType);
		
		// Get data model filepath
		String dataModelFilepath = getArg(args, 2, "Please input datamodel filepath (Leave blank for " + getBlankDataModel(tType) + "):");
		if ( dataModelFilepath == null || "".equals(dataModelFilepath) )
			dataModelFilepath = getBlankDataModel(tType);
		
		// Get files
		String template = getFile(templateFilepath);
		String dataModel = getFile(dataModelFilepath);
		if ( template == null || dataModel == null ) {
			System.exit(0);
		}

		// Translate
		translateMapService = new TranslateMapService();
		String output = null;
		try {
			output = translateMapService.translate(tType, template, dataModel);
		} catch (ParseException | IOException | TemplateException e1) {
			e1.printStackTrace();
		}
		if ( output == null || "null".equals(output) )
			output = "Something went wrong. Please see console for error details.";
		
		// Write to file
		String fileName = "Output-" + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + ".txt";
		try (PrintWriter out = new PrintWriter(fileName)) {
		    out.println(output);
		}
		
		// Output to user
		System.out.println("************************************************");
		System.out.println("Translation output:");
		System.out.println(output);
		System.out.println("************************************************");
		System.out.println("Output written to file: " + new File(fileName).getAbsolutePath());
		
		// Check if valid JSON
		try {
			new ObjectMapper().readValue(output, Map.class);
		} catch(Exception e) {
			System.out.println("WARNING: Output is NOT in valid JSON format. Please verify your template file is correct.");
			System.out.println(e.toString());
		}
		
		scanner.close();
	}
	
	private static String getBlankTemplate(TranslateType type) {
		switch(type) {
			case VELOCITY: {
				return "Sample/VelocityTemplate.txt";
			}
			case FREEMARKER: {
				return "Sample/FreemarkerTemplate.txt";
			}
		}
		
		return null;
	}
	
	private static String getBlankDataModel(TranslateType type) {
		switch(type) {
			case VELOCITY: {
				return "Sample/VelocityInputDataModel.txt";
			}
			case FREEMARKER: {
				return "Sample/FreemarkerInputDataModel.txt";
			}
		}
		
		return null;
	}

	private static String getFile(String filepath) {
		if ( filepath == null || filepath.length() == 0 )
			throw new RuntimeException("Filepath must not be null or nonlength.");
		
        String content = null;
        try {
            content = Files.lines(Paths.get(filepath))
                            .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
        	System.err.println("File not found");
        	return null;
        }
        
        return content;
	}

	private static String getArg(String[] args, int index, String request) {
		if ( args.length >= index+1 && args[index] != null )
			return args[index];
		
		System.out.println(request);
		String ret = scanner.nextLine();
		return ret;
	}
}
