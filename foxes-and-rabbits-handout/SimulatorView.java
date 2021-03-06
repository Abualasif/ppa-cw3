import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 * 
 * @author Haroon Yasin (K20008368), Rahi Al-Asif (K21063694) and Mohammed Kazi (K21050213)
 * @version 2016.02.29
 */
public class SimulatorView extends JFrame
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private JLabel stepLabel, population, infoLabel;
    private FieldView fieldView;

    private boolean paused;
    private boolean singleSimStep;

    private JButton stepButton, pauseButton, continueButton, exitButton, statsButton;
    private JPanel optionsPanel;
    private Container contents;

    // A map for storing colors for participants in the simulation
    private Map<Class, Color> colors;
    private Map<Class, String> colorClassMap;
    // A statistics object computing and storing simulation information
    private FieldStats stats;

    private Field internalField;

    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width, boolean showGUI)
    {

        paused = false;
        singleSimStep = false;

        stats = new FieldStats();
        colors = new LinkedHashMap<>();
        colorClassMap = new LinkedHashMap<>();

        setTitle("Savanna Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        
        setLocation(100, 50);

        optionsPanel = new JPanel(new BorderLayout());

        stepButton = new JButton("Step Sim");
        stepButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                stepSim();
            }
        });
        pauseButton = new JButton("Pause Sim");
        pauseButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                pauseSim();
            }
        });
        continueButton = new JButton("Continue Sim");
        continueButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                continueSim();
            }
        });
        exitButton = new JButton("Exit Sim");
        exitButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                exitSim();
            }
        });
        statsButton = new JButton("Show Sim Stats");
        statsButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                statsSim();
            }
        });

        optionsPanel.add(stepButton, BorderLayout.NORTH);
        optionsPanel.add(pauseButton, BorderLayout.CENTER);
        optionsPanel.add(continueButton, BorderLayout.WEST);
        optionsPanel.add(exitButton, BorderLayout.EAST);
        optionsPanel.add(statsButton, BorderLayout.SOUTH);
        
        fieldView = new FieldView(height, width);

        contents = getContentPane();

        JPanel infoPane = new JPanel(new BorderLayout());
            infoPane.add(stepLabel, BorderLayout.WEST);
            infoPane.add(infoLabel, BorderLayout.CENTER);
        contents.add(infoPane, BorderLayout.NORTH);
        JPanel viewPane = new JPanel(new BorderLayout());
            viewPane.add(population, BorderLayout.WEST);
            viewPane.add(optionsPanel, BorderLayout.CENTER);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(viewPane, BorderLayout.SOUTH);
        pack();
        setVisible(showGUI);
    }
    
    private void stepSim(){
        singleSimStep = true;
    }

    public boolean getSingleStep(){
        return singleSimStep;
    }

    public void negateSingleStep(){
        singleSimStep = false;
    }

    private void statsSim(){
        pauseSim();
        String statsString;
        statsString = stats.getPopulationStatistics(internalField) + getClassColors();
        JOptionPane.showMessageDialog(contents,statsString);
    }

    /**
     * Return the colors of each class in the simulation 
     * @return the colors of each class as a string
     */
    private String getClassColors(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("Key for Class Colours \n");
        for(Class key : colorClassMap.keySet()) {
            buffer.append("Class ");
            buffer.append(key.getName());
            buffer.append(" : ");
            buffer.append(colorClassMap.get(key));
            buffer.append(" \n");
        }
        return buffer.toString();
    }

    /**
     * Called when "exit" is pressed
     * Stops the simulation
     */
    private void exitSim(){
        System.exit(0);
    }

    /**
     * Called when "pause" is pressed
     * Pauses the simulation
     */
    private void pauseSim(){
        paused = true;
    }

    /**
     * Return whether the simulation is paused or not
     * @return true if the simulation is paused
     */
    public boolean getIfPaused(){
        return paused;
    }

    /**
     * Called when continue is pressed
     * Resumes the simulation
     */
    private void continueSim(){
        paused = false;
    }



    /**
     * Define a color to be used for a given class of animal.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class animalClass, Color color, String colorString)
    {
        colors.put(animalClass, color);
        colorClassMap.put(animalClass, colorString);
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }

    /**
     * @return The color to be used for a given class of animal.
     */
    private Color getColor(Class animalClass)
    {
        Color col = colors.get(animalClass);
        if(col == null) {
            // no color defined for this class
            return UNKNOWN_COLOR;
        }
        else {
            return col;
        }
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, Field field)
    {
        if(!isVisible()) {
            setVisible(true);
        }
            
        stepLabel.setText(STEP_PREFIX + step);
        stats.reset();
        
        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object animal = field.getObjectAt(row, col);
                if(animal != null) {
                    stats.incrementCount(animal.getClass());
                    fieldView.drawMark(col, row, getColor(animal.getClass()));
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }
        stats.countFinished();

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }

    /**
     * Update an internal field to a new field
     * @param field to replace internal field
     */
    public void updateInternalField(Field field){
        internalField = field;
    }
    
    /**
     * Provide a graphical view of a rectangular field. This is 
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this 
     * for your project if you like.
     */
    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                                 gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        public void preparePaint()
        {
            if(! size.equals(getSize())) {  // if the size has changed...
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if(xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if(yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }
        
        /**
         * Paint on grid location on this field in a given color.
         */
        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        public void paintComponent(Graphics g)
        {
            if(fieldImage != null) {
                Dimension currentSize = getSize();
                if(size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                }
                else {
                    // Rescale the previous image.
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
