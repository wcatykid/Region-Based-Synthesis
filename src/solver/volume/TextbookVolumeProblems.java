package solver.volume;

import java.util.ArrayList;
import java.util.List;

import representation.bounds.functions.Domain;
import solver.TextbookProblem;
import utilities.Pair;

/**
 *  A textbook problem consists of a set of functions over a particular interval (domain) 
 */
public class TextbookVolumeProblems extends TextbookProblem
{
    protected ArrayList<AxisOfRevolution> _axes; // One region might have several axes specified with the problem
    protected ArrayList<Double> _answers;        // Parallel: corresponding answers to the n problems

    public int numProblems() { return _axes.size(); }
    
    /**
     * @param index -- position of desired problem
     * @return the corresponding pair <axis, answer>
     */
    public Pair<AxisOfRevolution, Double> getAxisAnswerPair(int index)
    {
        if (index < 0 || index >= _axes.size()) throw new IndexOutOfBoundsException(Integer.toString(index));

        return new Pair<AxisOfRevolution, Double>(_axes.get(index), _answers.get(index));
    }
    
    /**
     * This method is invalid for volume problems since the answer is based on the axis (and there may be many axes).
     */
    @Override
    public double getAnswer() { throw new UnsupportedOperationException(); }
    
    public TextbookVolumeProblems(String[] functions, Domain domain, AxisOfRevolution axis, String data, double answer)
    {
        super(functions, domain, data);

        _axes = new ArrayList<AxisOfRevolution>();
        _axes.add(axis);

        _answers = new ArrayList<Double>();
        _answers.add(answer);
    }

    /**
     * 
     * @param functions -- the set of functions (in terms of variable x)
     * @param domain -- domain restriction
     * @param axis -- single axis of revolution
     * @param data -- metadata describing the problem source.
     * @param answer -- single answer to the corresponding problem
     */
    public TextbookVolumeProblems(String[] functions, Domain domain, List<AxisOfRevolution> axes, String data, List<Double> answers)
    {
        super(functions, domain, data);

        _axes = new ArrayList<AxisOfRevolution>();
        _axes.addAll(axes);

        _answers = new ArrayList<Double>();
        _answers.addAll(answers);
    }

    /**
     * @param f -- the set of functions (in terms of variable x)
     * @param data -- metadata describing the problem source.
     * @param answer -- the numeric answer for this problem
     */
    public TextbookVolumeProblems(String[] f, AxisOfRevolution axis, String data, double answer)
    {
        this(f, null, axis, data, answer);
    }

    /**
     * 
     * @param functions -- the set of functions (in terms of variable x)
     * @param domain -- domain restriction
     * @param axis -- single axis of revolution
     * @param data -- metadata describing the problem source.
     * @param answer -- single answer to the corresponding problem
     */
    public TextbookVolumeProblems(String[] functions, List<AxisOfRevolution> axes, String data, List<Double> answers)
    {
        super(functions, null, data);

        _axes = new ArrayList<AxisOfRevolution>();
        _axes.addAll(axes);

        _answers = new ArrayList<Double>();
        _answers.addAll(answers);
    }

    @Override
    public String toString()
    {
        String s = super.toString();

        //
        // Axes and answers
        //
        s += "<Axes, Answers> = { ";

        for (int a = 0; a < _axes.size(); a++)
        {
            s += "< " + _axes.get(a).toString() + ", " + _answers.get(a).toString() + " >";
        }

        s += " }";

        return s;
    }
}
