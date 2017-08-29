package math.external_interface;

//import frontend.Options;
//import globals.Constants;
//import utilities.Utilities;
import representation.Point;
import representation.bounds.functions.BoundedFunction;

public class ExternalMathematicaCasInterface extends CasInterface
{
    // Can we establish the CAS connection?
    @Override
    public boolean connection()
    {
        return false;
    }

    // Returns a String-based representation of the two given functions
    @Override
    public String getIntersection(BoundedFunction func1, BoundedFunction func2)
    {
        return null;
    }

    @Override
    public String getLagrangePolynomial(Point[] points)
    {
        return null;
    }

    //
    // CTA 9-30-16
    //
    // We use a local Mathematica interface so no longer need the query engine via the internet
    //
    //


    //    // Returns a String-based representation of the two given functions
    //    public String getIntersection(BoundedFunction func1, BoundedFunction func2)
    //    {
    //        return null;
    //    }
    //    
    //    public String getLagrangePolynomial(Point[] points)
    //    {
    //        return null;
    //    }
    //
    //    
    //
    //
    //	private static class CasQueryEngine
    //	{
    //		public static String[] query(String arg)
    //		{
    //			// String input = "solve x^2 = x^3";
    //			System.out.println("Query: |" + arg + "|");
    //
    //			// The WAEngine is a factory for creating WAQuery objects,
    //			// and it also used to perform those queries. You can set properties of
    //			// the WAEngine (such as the desired API output format types) that will
    //			// be inherited by all WAQuery objects created from it. Most applications
    //			// will only need to create one WAEngine object, which is used throughout
    //			// the life of the application.
    //			WAEngine engine = new WAEngine();
    //
    //			// These properties will be set in all the WAQuery objects created from this WAengine.
    //			engine.setAppID(Constants.APPID);
    //			engine.addFormat("plaintext");
    //
    //			// Create the query.
    //			WAQuery query = engine.createQuery();
    //
    //			// Set properties of the query.
    //			query.setInput(arg);
    //
    //			try
    //			{
    //				// For educational purposes, print out the URL we are about to send:
    //				if (Options.DEBUG)
    //				{
    //					System.out.println("Query URL:");
    //					System.out.println(engine.toURL(query));
    //					System.out.println("");
    //				}
    //
    //				// This sends the URL to the Wolfram|Alpha server, gets the XML result
    //				// and parses it into an object hierarchy held by the WAQueryResult object.
    //				WAQueryResult queryResult = null;
    //
    //				if (Constants.CONTACT_WA_ENGINE) engine.performQuery(query);
    //				else
    //				{
    //					System.err.println("WARNING: External contact to Wolfram / Alpha is locally prohibited.");
    //					return (String[])new Vector<String>().toArray();
    //				}
    //
    //				if (queryResult.isError())
    //				{
    //					System.out.println("Query error");
    //					System.out.println("  error code: " + queryResult.getErrorCode());
    //					System.out.println("  error message: " + queryResult.getErrorMessage());
    //				}
    //				else if (!queryResult.isSuccess())
    //				{
    //					System.out.println("Query was not understood; no results available.");
    //				}
    //				else   // Got a result.
    //				{
    //					return extractResultFromPods(queryResult);
    //				}
    //			}
    //			catch (WAException e)
    //			{
    //				e.printStackTrace();
    //			}
    //
    //			throw new IllegalArgumentException("Wolfram / Alpha Query Problem");
    //		}
    //
    //		private static String[] extractResultFromPods(WAQueryResult queryResult)
    //		{
    //			if (Options.DEBUG) System.out.println("Successful query. Pods follow:\n");
    //
    //			Vector<String> result = new Vector<String>();
    //
    //			for (WAPod pod : queryResult.getPods())
    //			{
    //				if (!pod.isError())
    //				{
    //					if (Options.DEBUG)
    //					{
    //						// We ignored many other types of Wolfram|Alpha output, such as warnings, assumptions, etc.
    //						// These can be obtained by methods of WAQueryResult or objects deeper in the hierarchy.
    //						System.out.println(pod.getTitle());
    //						System.out.println("------------");
    //						for (WASubpod subpod : pod.getSubpods())
    //						{
    //							for (Object element : subpod.getContents())
    //							{
    //								if (element instanceof WAPlainText)
    //								{
    //									System.out.println(((WAPlainText) element).getText());
    //									System.out.println("");
    //								}
    //							}
    //						}
    //						System.out.println("");
    //					}
    //
    //					if (pod.getTitle().toUpperCase().contains("RESULT"))
    //					{
    //						for (WASubpod subpod : pod.getSubpods())
    //						{
    //							for (Object element : subpod.getContents())
    //							{
    //								if (element instanceof WAPlainText)
    //								{
    //									result.addElement(((WAPlainText) element).getText());
    //								}
    //							}
    //						}
    //						System.out.println("");
    //					}
    //				}
    //			}
    //
    //			return (String[])result.toArray();
    //		}
    //	}
}