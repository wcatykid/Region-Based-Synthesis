package solver.area.main;

import solver.area.parser.AreaProblemFileParser;

public class Main
{
    public static void main(String[] args)
    {
        //
        // Strip the files from any other options
        //
        Options options = new Options(args);
        options.parse();
        
        //
        // Read the files to acquire the area between curves problems
        //
        AreaProblemFileParser parser = new AreaProblemFileParser(options.getProblemFiles());
//        parser.parse();
//        Vector<TextbookAreaProblem> problems = parser.getProblems();

//        //
//        // Analyze the problems:
//        //   (1) Acquire implied domains (if not provided)
//        //
//        for (TextbookAreaProblem problem : problems)
//        {
//            problem.deduceDomain();
//        }
    }
    
    

}
