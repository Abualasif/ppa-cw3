import java.util.Random;

/**
 * A simple clock class to keep track of the hour of day
 *
 * @author Haroon Yasin, Rahi Al-Asif, Mohammed Kazi
 * @version 1.0
 */
public class Clock
{
    enum Weather {
        CLEAR, 
        RAIN,
        FOG,
    }
    
    private int hourOfDay;
    private Weather currentWeather;
    
    /**
     * Constructor for objects of class Clock
     * Initialise time to 00:00 and no 
     */
    public Clock()
    {
        // initialise instance variables
        this.hourOfDay = 0;
        this.currentWeather = Weather.CLEAR;
    }
    
    public void setRandomWeather() 
    {
        currentWeather = Weather.values()[new Random().nextInt(Weather.values().length)];
    }
    
    public Weather getCurrentWeather()
    {
        return currentWeather;
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
     * If it is a new day, choose a new random weather event
     */
    public void incrementHourOfDay()
    {
        hourOfDay++;
        if (hourOfDay > 23) {
            hourOfDay = 0;
            setRandomWeather();
            
            System.out.println("It is officially: " + getCurrentWeather());
        }
    }
    

}
