package template;
import java.util.List;
import java.util.Vector;

import representation.bounds.Bound;

public class TopBottomTemplate
{
    protected Vector<TemplateRestriction> _templateBounds;

    public int length() { return _templateBounds.size(); }
    
    public TopBottomTemplate()
    {
        super(); 
        _templateBounds = new Vector<TemplateRestriction>();
    }

    /**
     * @param restriction -- single restriction for this template
     */
    public TopBottomTemplate(TemplateRestriction restriction)
    {
        this();
        
        _templateBounds.add(restriction);
    }
    
    public TopBottomTemplate(Vector<TemplateRestriction> that)
    {
        this();
        
        _templateBounds.addAll(that);
    }

    public void addBound(TemplateRestriction bound)
    {
        _templateBounds.addElement(bound);
    }
    
    public TemplateRestriction getRestriction(int index) { return _templateBounds.get(index); }
    public List<Bound.BoundT> getRestrictionTypes(int index) { return _templateBounds.get(index).getAllowedBoundTypes(); }
    
    /**
     * A template is not allowed to have consecutive vertical lines (what't the point: redundant)
     * A template should also specify a minimal number of functions: valid top / bottom has at least one function.
     * @return true / false based on meeting these constraints
     */
    public boolean verify()
    {
        return !consecutiveVerticals() && minimalFunctions();
    }

    private boolean consecutiveVerticals()
    {
        if (_templateBounds.size() < 2) return false;

        for (int index = 0; index < _templateBounds.size() - 1; index++)
        {
            if (_templateBounds.elementAt(index).getType() == Bound.BoundT.VERTICAL_LINE)
            {
                if (_templateBounds.elementAt(index).getType() == _templateBounds.elementAt(index + 1).getType()) return true;
            }
        }

        return false;
    }

    // A valid top / bottom sequence has at least one function
    private boolean minimalFunctions()
    {
        return true;//_templateBounds.contains(Bound.BoundT.FUNCTION) || _templateBounds.contains();
    }
    
    public boolean equals(Object obj)
    {
        if (obj == null) return false;

        if (!(obj instanceof TopBottomTemplate)) return false;

        TopBottomTemplate that = (TopBottomTemplate)obj;

        if (this.length() != that.length()) return false;

        for (int b = 0; b < _templateBounds.size(); b++)
        {
            if (!this._templateBounds.elementAt(b).equals(that._templateBounds.elementAt(b))) return false;
        }

        return true;
    }

    public boolean notEquals(Object obj)
    {
        return !this.equals(obj);
    }

    public String toString()
    {
        String s = "";
        s += "{ ";

        int c = 0;
        while (c < this._templateBounds.size())
        {
            int dupCount = 1;
            while (c + dupCount < this._templateBounds.size() && this._templateBounds.elementAt(c) == this._templateBounds.elementAt(c + dupCount)) dupCount++;
            
            s += Bound.BoundT.convertFromBound(this._templateBounds.elementAt(c).getType());
            if (dupCount > 1) s += dupCount;
            s += " ";

            c += dupCount;
        }

        return s + "}";
    }
}
