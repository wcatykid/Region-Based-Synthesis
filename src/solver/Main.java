package solver;

import exceptions.DomainException;

public abstract class Main
{
    protected int _numProblems;

    public Main()
    {
        _numProblems = 0;
    }

    /**
     * @param sProblem -- String statement of a problem (defines a region with, possibly, domain)
     * Passes the problem onto the main solver.
     */
    public abstract void solve(String sProblem) throws DomainException;
}
