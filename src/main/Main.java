package main;

import java.util.Vector;
import frontend.Options;
import frontend.OptionsFileParser;
import template.RegionTemplate;

public class Main
{
    public static void main(String[] args)
    {
        if (args.length < 2)
        {
            System.err.println("Usage: <program> -debug <template-files>");
            return;
        }

        //
        // Global options object.
        //
        Options.initialize(args);

        if (!Options.getInstance().parseCommandLine())
        {
            System.err.println("Command-line parsing failed; exiting.");
            return;
        }

        if (Options.getInstance().hasOptionFile())
        {
            OptionsFileParser optionsFileParser = new OptionsFileParser(Options.getInstance().getOptionFile());
            optionsFileParser.parseFile();
        }

        // Acquire the set of template files to process
        Vector<String> templates = Options.getInstance().getTemplateFiles();

        for (String template : templates)
        {
            if (Options.DEBUG) System.out.println("Processing " + template + "...");

            if (!process(template))
            {
                System.err.println("Parse error in main: error in processing template file " + template);
            }
        }
    }

    /**
     * @param file -- the name of a file containing templates
     * @return main processing routine for instantiation of all templates in the stated file
     */
    private static boolean process(String file)
    {
        Vector<RegionTemplate> templates;

        // Parse the template file
        templates = processTemplateFile(file);

        // Print the templates read from the file
        if (Options.DEBUG)
        {
            for (RegionTemplate template : templates)
            {
                System.out.println(template);
            }
        }

        
        //
        // Instantiate the regions
        //
        throw new RuntimeException( "Main.process has unresolved build problems!" ) ;

/*
        Instantiator inst = new Instantiator(templates);

        Vector<RegionProblemAggregator> regions = inst.instantiate();

        //
        // Solve the regions (as area between curve problems)
        //

        //
        // Solve the regions (as area volumes: revolution of axes)
        //
        
        
        
        
        // A vector of all outputs from the instantiator.
        Vector<Vector<Region>> instantiatedRegions = new Vector<>();
        Vector<Region> flatInstantiatedRegions = new Vector<>();

        if (Options.DEBUG) System.out.println("Starting instantiation...");

        for (RegionTemplate template : templates) 
        {
            instantiatedRegions.add(regions);
            flatInstantiatedRegions.addAll(regions);

            if (Options.DEBUG) 
            {
                System.out.println("Instantiated: \n" + regions);
                System.out.println("Instanciated " + regions.size() + " regions.");
            }
        }

        if (Options.DEBUG) System.out.println("Instantiation complete...");

        // TBC: Create the resulting set of problems: areas, volumes, etc.

        // TBC: Emit the instantiations as problems

        return true;
*/
    }

    //
    // Parse the file and acquire templates
    //
    private static Vector<RegionTemplate> processTemplateFile(String theFileStr)
    {
        frontend.TemplateFileParser parser = new frontend.TemplateFileParser(theFileStr);

        if (!parser.parse())
        {
            throw new IllegalArgumentException("Parse error in main: error in processing template file " + theFileStr);
        }

        System.err.println("Number of templates: " + parser.getTemplates().size());

        return parser.getTemplates();
    }

}
