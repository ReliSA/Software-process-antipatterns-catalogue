/*
 * Created 2014 by Tomas Mikula.
 *
 * The author dedicates this file to the public domain.
 */

package cz.zcu.kiv.spac.richtext;

public class Indent
{
    double width = 15;
    int level = 1;

    Indent() {}

    Indent( int level )
    {
        this.level = level;
    }

    Indent increase()
    {
    	  return new Indent( level + 1 );
    }

    Indent decrease()
    {
    	  return new Indent( level - 1 );
    }

    int getLevel() { return level; }
    
    @Override
    public String toString()
    {
        return "indent: "+ level;
    }
}
