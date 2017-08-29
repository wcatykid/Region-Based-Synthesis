package template;

import java.util.ArrayList;
import java.util.Vector;

import exceptions.RestrictionException;
import representation.bounds.Bound;
import utilities.PeekableScanner;

public class TemplateRestriction
{
    private Bound.BoundT generalType;
    public Bound.BoundT getType() { return generalType; }
    
    private ArrayList<Bound.BoundT> allowed;
    private ArrayList<Bound.BoundT> disallowed;
    private static ArrayList<Bound.BoundT> all;

    public TemplateRestriction()
    {
        super();

        allowed = new ArrayList<Bound.BoundT>();
        disallowed = new ArrayList<Bound.BoundT>();
    }

    public TemplateRestriction(Bound.BoundT type,
            ArrayList<Bound.BoundT> allow,
            ArrayList<Bound.BoundT> disallow)
    {
        this();

        generalType = type;

        if (allow != null) allowed.addAll(allow);
        if (disallow != null) disallowed.addAll(disallow);
    }

    //
    // Add a function bound type
    //
    public void addAllowableBound(Bound.BoundT type) throws exceptions.OptionsException
    {
        if (!disallowed.isEmpty()) throw new exceptions.OptionsException("Cannot specify allowed AND disallowed bound type list");

        if (!all.contains(type)) throw new exceptions.OptionsException("Function type not specified in complete set of functions");

        allowed.add(type);
    }

    public void addDisAllowedBound(Bound.BoundT type) throws exceptions.OptionsException
    {
        if (!allowed.isEmpty()) throw new exceptions.OptionsException("Cannot specify allowed AND disallowed bound type list");

        if (!all.contains(type)) throw new exceptions.OptionsException("Function type not specified in complete set of functions");

        disallowed.add(type);
    }

    //
    // Add a function bound type
    //
    public static void addBoundType(Bound.BoundT type)
    {
        if (all == null) all = new ArrayList<Bound.BoundT>();

        all.add(type);
    }

    public ArrayList<Bound.BoundT> getAllowedBoundTypes()
    {
        // The allowed set overrides everything
        if (!allowed.isEmpty()) return allowed;

        // No disallowed function types means we return all as an option
        if (disallowed.isEmpty()) return all;

        // If disallowed, perform set difference with  (all \setminus disallowed)
        return utilities.Utilities.setDifference(all,  disallowed);        
    }

    //
    // Parses a restriction from the input
    //
    // A restriction is of the regular expression form: 
    //       V | F  Integer? '[' ~? func-type+   ']'          ~ is negation
    //
    public static Vector<TemplateRestriction> readRestriction(PeekableScanner input) throws RestrictionException
    {
        Vector<TemplateRestriction> restrictions = new Vector<TemplateRestriction>();

        Bound.BoundT generalType = readGeneralType(input);

        int repetitions = readRepetitionValue(input);

        //
        // Parsing list of functions
        //
        utilities.Pair<ArrayList<Bound.BoundT>, ArrayList<Bound.BoundT>> pair = null;

        try
        {
            pair = readRestrictionFunctions(input);
        }
        catch(RestrictionException re)
        {
            throw new RestrictionException("Parsing problem with restriction list");
        }

        //
        // Create the actual restriction objects
        //
        for (int r = 0; r < repetitions; r++)
        {
            TemplateRestriction restriction = new TemplateRestriction(generalType, pair.getFirst(), pair.getSecond());
            restrictions.add(restriction);
        }

        return restrictions;
    }

    //
    // Read 'P', 'V', etc.
    //
    private static Bound.BoundT readGeneralType(PeekableScanner input)
    {
        return Bound.BoundT.convertToBound(input.next().toUpperCase().charAt(0));
    }

    //
    // Multiple functions indicated with an integer
    //
    private static int readRepetitionValue(PeekableScanner input)
    {
        if (!Character.isDigit(input.peek().charAt(0))) return 1;

        int repetitions = Integer.parseInt(input.next());

        System.err.println("|" + repetitions + "|\n");

        return repetitions;
    }

    //
    // Specific functions indicated with '[ <list> ]'
    // @return true indicates allowed restrictions
    //         false indicates disallowed restrictions
    //
    // We assume the delimiter '[' has been identified
    //
    private static
    utilities.Pair<ArrayList<Bound.BoundT>, ArrayList<Bound.BoundT>>
    readRestrictionFunctions(PeekableScanner input) throws RestrictionException
    {
        boolean negation = input.peek().charAt(0) == '~';

        // Consume the tilda from negation
        if (negation) input.next();

        if (input.peek().charAt(0) == ']') throw new RestrictionException("Empty set of function restrictions");

        // Loop to identify all functions in the restriction
        ArrayList<Bound.BoundT> list = new ArrayList<Bound.BoundT>();
        while (input.peek().charAt(0) != ']')
        {
            Bound.BoundT type = Bound.BoundT.convertToBound(input.next());

            if (type == Bound.BoundT.UNSPECIFIED) throw new RestrictionException("Unexpected unspecified bound type in parse |" + type + "|");
            
            list.add(type);
        }

        // Consume the ']'
        input.next();
        
        return negation ? new utilities.Pair<ArrayList<Bound.BoundT>, ArrayList<Bound.BoundT>>(null, list) :
            new utilities.Pair<ArrayList<Bound.BoundT>, ArrayList<Bound.BoundT>>(list, null);
    }
}
