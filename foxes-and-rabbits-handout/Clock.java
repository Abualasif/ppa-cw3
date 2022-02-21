
/**
 * A simple clock class to keep track of the hour of day
 *
 * @author Haroon Yasin and Rahi Al-Asif
 * @version 1.0
 */
public class Clock
{
    private int hourOfDay;

    /**
     * Constructor for objects of class Clock
     * All clocks are initialised to 00:00
     */
    public Clock()
    {
        // initialise instance variables
        this.hourOfDay = 0;
    }

    /**
     * Return the current hour of the clock
     * @return The current hour of the clock
     */
    public int getHourOfDay()
    {
        return hourOfDay;
    }
    
    /**
     * Increment the hour of the clock
     */
    public void incrementHourOfDay()
    {
        hourOfDay++;
        if (hourOfDay > 23) {
            hourOfDay = 0;
        }
    }
    

}
