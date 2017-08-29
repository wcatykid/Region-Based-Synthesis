// @author Tom_Nielsen

package manager;

import org.junit.runner.JUnitCore;

import java.io.File;
import java.lang.Class;
import java.util.ArrayList;

public class TestManager
{
    private static ArrayList<Class> testClasses = new ArrayList<Class>();
    private static ArrayList<Class> missing = new ArrayList<Class>();
    private static final String FILE_TEST_STRING = "test.java";
    private static final String FILE_TYPE = ".java";
    private static final String EMPTY = "";
    // finds all classes and then runs every class in the ArrayList testClasses 
    public static void run() throws Exception
    {
        File dir = new File(".\\test");
        findClasses(dir);
        for(Class test : testClasses)
        {
            JUnitCore.runClasses(test);
        }
        for(Class missed : missing)
        {
            //System.out.println(missed);// this will most likely need to be change to a logger instead of system.out.println
        }
    }
    
      //uses recursion to find all files containing 'test' in the file name and ends in .class (may change to .java) 
      //it then gets the Class name to add said class to the JUnitCore for testing
      //this function assumes it is given the source directory to start
     
    private static void findClasses(File dir) throws Exception
    {
        File[] files = dir.listFiles();
        for (File file : files) 
        {
            if (file.isDirectory()) 
            {
                findClasses(file); 
            } 
            else 
            {
                String name = file.getName();
                
                if(name.toLowerCase().contains(FILE_TEST_STRING))
                {
                    String packagePath = new String(file.getParentFile().getPath());
                    packagePath = packagePath.replace(".\\test\\", EMPTY);
                    packagePath = packagePath.replace("\\", ".");
                    //System.out.println(packagePath);
                    testClasses.add(Class.forName(packagePath+"."+name.replace(FILE_TYPE, EMPTY)));
                }
                else if(name.toLowerCase().endsWith(FILE_TYPE))
                {
                    String packagePath = new String(file.getParentFile().getPath());
                    packagePath = packagePath.replace(".\\test\\", EMPTY);
                    packagePath = packagePath.replace("\\", ".");
                    //System.out.println(packagePath);
                    try
                    {
                        if(!testClasses.contains(Class.forName(packagePath+"."+name.replace(FILE_TYPE, EMPTY)+"Test")))
                        {
                            missing.add(Class.forName(packagePath+"." + name.replace(FILE_TYPE, EMPTY)));
                        }
                    }
                    catch(ClassNotFoundException e)
                    {
                        missing.add(Class.forName(packagePath+"."+name.replace(FILE_TYPE, EMPTY)));
                    }
                }
            }
        }
        
    }
    
}
