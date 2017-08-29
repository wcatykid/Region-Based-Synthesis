package frontend;

import java.io.FileNotFoundException;
import java.util.Vector;

import exceptions.RestrictionException;
import representation.bounds.Bound;
import template.LeftRightPointTemplate;
import template.LeftRightTemplate;
import template.LeftRightVerticalTemplate;
import template.RegionTemplate;
import template.TemplateRestriction;
import template.TopBottomTemplate;
import utilities.PeekableScanner;

//
// A parser for the template file specifications.
//
public class TemplateFileParser
{
    private static final String TEMPLATE_DELIMITER = "END";
    private String theFile;

    private Vector<RegionTemplate> templates;
    public Vector<RegionTemplate> getTemplates() { return templates; }

    public TemplateFileParser(String f)
    {
        theFile = f;
        templates = new Vector<RegionTemplate>();
    }

    public boolean parse()
    {
        PeekableScanner input = null;

        try
        {
            input = new PeekableScanner(theFile);
        }
        catch (FileNotFoundException e)
        {
            System.err.println("File not found: " + theFile);
            return false;
        }

        while(input.hasNext())
        {
            RegionTemplate template = null;
            try
            {
                template = readTemplate(input);
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace(System.err);
            }
            catch (Exception e)
            {
                //
                // Read until "END" or EOF
                //
                while (input.hasNext())
                {
                    if (input.peek().toUpperCase().equals(TEMPLATE_DELIMITER)) break;

                    // Read the next 
                    input.next();
                }
            }

            if (template != null) templates.add(template);
        }

        input.close();

        return true;
    }

    //
    // Read an individual template
    //
    private RegionTemplate readTemplate(PeekableScanner input) throws IllegalArgumentException
    {
        LeftRightTemplate left = null;
        LeftRightTemplate right = null;
        TopBottomTemplate top = null;
        TopBottomTemplate bottom = null;

        boolean[] tracker = new boolean[4];

        // Loop until all parts of the template are acquired
        for (int count = 0; count < 4; count++)
        {
            String side = input.next();

            // Convert to enumerator value
            RegionTemplate.SideT sideType = RegionTemplate.SideT.UNRECOGNIZED;
            try
            {
                sideType = RegionTemplate.SideT.convertToSide(side.charAt(0));
            }
            catch (IllegalArgumentException e)
            {
                // Continue parsing the next template....
                System.err.println("Unexpected side values specified in template: " + side);
                throw new IllegalArgumentException();   
            }

            if (tracker[sideType.getValue()])
            {
                throw new IllegalArgumentException("Template side specified multiple times: " + side);
            }

            // Handle each side
            switch(side.charAt(0))
            {
                case 'L': case 'l':
                    left = readLeftRight(input);
                    break;

                case 'R': case 'r':
                    right = readLeftRight(input);
                    break;

                case 'T': case 't':
                    top = readTopBottom(input);
                    break;

                case 'B': case 'b':
                    bottom = readTopBottom(input);
                    break;

                default:
                    throw new IllegalArgumentException("Unrecognized side in switch: " + side.charAt(0));
            }

            // Update the tracker to indicate completion
            tracker[sideType.getValue()] = true;
        }

        template.RegionTemplate template = new template.RegionTemplate(left, top, right, bottom);

        if (!template.verify())
        {
            throw new IllegalArgumentException("Template did not create a valid region: \n" + template);
        }
        
        return template;
    }

    LeftRightTemplate readLeftRight(PeekableScanner input)
    {
        //
        // Peek at the next character to determine if it is an integer character
        //
        if (Character.isDigit(input.peek().charAt(0)))
        {
            throw new IllegalArgumentException("Digit not expected in left / right template");
        }

        char code = input.next().charAt(0);

        System.err.println("|" + code + "|\n");

        switch(code)
        {
            case 'P':
                return new LeftRightPointTemplate();

            case 'V':
                return new LeftRightVerticalTemplate();

            case 'F':
                throw new IllegalArgumentException("A function cannot define a left / right template");

            default:
                throw new IllegalArgumentException("Unrecognized code: " + code);
        }
    }

    TopBottomTemplate readTopBottom(PeekableScanner input)
    {
        final String ALLOWABLE = "0123456789PpVvFfHhLlSsCceGg[]";
        
        // Read the entire sequence, one element at a time
        Vector<template.TemplateRestriction> restrictionSeq = new Vector<template.TemplateRestriction>();

        //
        // Read all the restrictions
        //
        while (input.peek() != null && ALLOWABLE.indexOf(input.peek().charAt(0)) != -1)
        {
            Vector<TemplateRestriction> restrictions = null;
            try
            {
                restrictions = TemplateRestriction.readRestriction(input);
                restrictionSeq.addAll(restrictions);
            }
            catch(RestrictionException re)
            {
                System.err.println("Parsing problem with restriction list; ignoring the restriction.");
            }
        }
        
        //
        // Verify the restrictions are legal
        //        
        Bound.BoundT prevType = Bound.BoundT.UNSPECIFIED;
        for (TemplateRestriction restriction : restrictionSeq)
        {
            Bound.BoundT thisType = restriction.getType();
            
            if (thisType == Bound.BoundT.POINT)
            {
                throw new IllegalArgumentException("A point may not be specified for top / bottom template");
            }
            
            if (thisType == prevType && thisType == Bound.BoundT.VERTICAL_LINE)
            {
                throw new IllegalArgumentException("A vertical line may not follow a vertical line");
            }

            prevType = thisType;
        }

        //
        // Cannot have a vertical line as the final bound in a top / bottom
        //
        if (restrictionSeq.lastElement().getType() == Bound.BoundT.VERTICAL_LINE)
        {
            throw new IllegalArgumentException("Last bound in top or bottom template cannot be vertical");
        }
        
        return new TopBottomTemplate(restrictionSeq);
    }
}
