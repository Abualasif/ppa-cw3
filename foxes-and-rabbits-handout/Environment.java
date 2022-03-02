import java.util.Random;

/**
 * A simple class that tracks the hour of day and weather conditions
 * All animal instances hold a reference to one shared Environment
 * 
 * @author Haroon Yasin (K20008368), Rahi Al-Asif (K21063694) and Mohammed Kazi (K21050213)
 * @version 1.0
 */
public class Environment
{
    enum Weather {
        CLEAR, 
        RAIN,
        FOG,
    }

    private static final double CLEAR_PROBABILITY = 0.33;
    private static final double RAIN_PROBABILITY = 0.33;
    private static final double FOG_PROBABILITY = 0.33;

    
    private int hourOfDay;
    private Weather currentWeather;
    private Random rand;
    
    /**
     * Constructor for objects of class Environment
     * Initialise time to 00:00 and weather to clear.
     */
    public Environment()
    {
        // initialise instance variables
        this.hourOfDay = 0;
        this.currentWeather = Weather.CLEAR;
        this.rand = new Random();
    }
    
    /**
     * Determine the next weather condition.
     * This is called at the end of every day
     * (i.e every 24 ticks)
     */
    public void setRandomWeather() 
    {

        if (rand.nextDouble() <= CLEAR_PROBABILITY) {
            currentWeather = Weather.CLEAR;
        } else if (rand.nextDouble() <= RAIN_PROBABILITY) {
            currentWeather = Weather.RAIN;
        } else if (rand.nextDouble() <= FOG_PROBABILITY) {
            currentWeather = Weather.FOG;
        }
    }
    
    /**
     * Get the current weather state
     * @return the current weather 
     */
    public Weather getCurrentWeather()
    {
        return currentWeather;
    }

    /**
     * Return the current hour of the clock
     * @return The hourOfDay instance variable
     */
    public int getHourOfDay()
    {
        return hourOfDay;
    }
    
    /**
     * Increment the hour of the clock
     * If it is a new day, choose a new random weather event
     */
    public void incrementHourOfDay()
    {
        hourOfDay++;
        if (hourOfDay > 23) {
            hourOfDay = 0;
            setRandomWeather();
        }
    }
    

}
