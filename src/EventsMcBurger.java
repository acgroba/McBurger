import desmoj.core.simulator.*;
import desmoj.core.dist.*;
import java.util.concurrent.TimeUnit;

/**
 * This is the model class. It is the main class of a simple event-oriented
 * model of a burger. Clients arrive at the
 * counter to ask orders. They wait in line until a cashier is
 * available to take their order and notify it to the kitchen.  Cashiers wait in line until a chef is available to cook
 * the order. After the order is cooked  they take the payment of clients. When the payment is done, client leaves the burger.
 */
public class EventsMcBurger extends Model {
    /**
     * model parameter: seed of the experiment
     */
    protected static long SEED = 728873;
    /**
     * model parameter: duration of the simulation
     */
    protected static long DURATION =900;
    /**
     * model parameter: the number of cashiers
     */
    protected static int NUM_CASHIERS = 2;
    /**
     * model parameter: the number of chefs
     */
    protected static int NUM_CHEFS = 3;
    /**
     * Random number stream used to draw an arrival time for the next client.
     * See init() method for stream parameters.
     */
    private ContDistExponential clientArrivalTime;
    /**
     * Random number stream used to draw the time for taking an order.
     * Describes the time needed by the cashier to take note of the order of the client.
     * See init() method for stream parameters.
     */
    private ContDistExponential takingOrderTime;
    /**
     * Random number stream used to draw the time for paying  an order.
     * Describes the time needed by the client for paying the order.
     * See init() method for stream parameters.
     */
    private ContDistExponential payingOrderTime;
    /**
     * Random number stream used to draw the time for cooking  an order.
     * Describes the time needed by the chef for cooking the order.
     * See init() method for stream parameters.
     */
    private ContDistExponential cookingOrderTime;
    /**
     * A waiting queue object is used to represent the clients waiting for a not busy cashier.
     * Every time a client arrives it is inserted into this queue
     * and will be removed after the order  is taken.
     */
    protected Queue<Client> clientQueue1;

    /**
     * A waiting queue object is used to represent the clients waiting for paying their order.
     * Every time a client has his order taken it is inserted into this queue
     * and will be removed after the payment is done.
     */
    protected Queue<Client> clientQueue2;

    /**
     * A waiting queue object is used to represent the  cashiers waiting for clients to arrive.
     * If there is no client waiting for service the cashier will return here
     * and wait for the next client to come.
     */
    protected Queue<Cashier> cashierQueue1;

    /**
     * A waiting queue object is used to represent the  cashiers waiting for chefs to not to be busy.
     * If there is no chef waiting for orders the cashier will return here
     * and wait for the next chef  to be available.
     */
    protected Queue<Cashier> cashierQueue2;

    /**
     * A waiting queue object is used to represent the  cashiers waiting for chefs to end cooking.
     */
    protected Queue<Cashier> cashierQueue3;

    /**
     * A waiting queue object is used to represent the  chefs waiting for cashiers to ask them orders.
     * If there is no cashier waiting for asking orders the chef will return here
     * and wait for the next cashier with an order  to arrive.
     */
    protected Queue<Chef> chefQueue;


    /**
     * EventsMcBurger constructor.
     *
     * Creates a new EventsExample model via calling
     * the constructor of the superclass.
     *
     * @param owner the model this model is part of (set to null when there is
     *              no such model)
     * @param modelName this model's name
     * @param showInReport flag to indicate if this model shall produce output
     *                     to the report file
     * @param showInTrace flag to indicate if this model shall produce output
     *                    to the trace file
     */
    public EventsMcBurger(Model owner, String modelName, boolean showInReport,
                         boolean showInTrace) {
        super(owner, modelName, showInReport, showInTrace);
    }

    /**
     * Returns a description of the model to be used in the report.
     * @return model description as a string
     */
    public String description() {
        return "This model describes a queueing system located at a "+
                "burger. Clients will arrive and "+
                "ask their orders. A cashier (VC) is "+
                "will take their order and notify it to the chefs "+
                "Once the order es cooked by the chef, cashiers take the payment of the client "+
                ". Afterwards, the client leaves the burger. "+
                "In case the cashier is busy, the client waits "+
                "in a line. The some does the cashier if the chef is busy. ";
    }

    /**
     * Activates dynamic model components (events).
     *
     * This method is used to place all events or processes on the
     * internal event list of the simulator which are necessary to start
     * the simulation.
     *
     * In this case, the truck generator event will have to be
     * created and scheduled for the start time of the simulation.
     */
    public void doInitialSchedules() {

        // create the TruckGeneratorEvent
        ClientGeneratorEvent clientGenerator =
                new ClientGeneratorEvent(this, "Client Generator", true);

        // schedule for start of simulation
        clientGenerator.schedule(new TimeSpan(0));
    }

    /**
     * Initialises static model components like distributions and queues.
     */
    public void init() {
        // initialise the takingOrderTime
        // Parameters:
        // this                = belongs to this model
        // "TakingOrderTimeStream" = the name of the stream
        // 4.0                      = mean time in minutes to take the order
        // true                = show in report?
        // false               = show in trace?
        takingOrderTime= new ContDistExponential(this, "TakingOrderTimeStream",
                4.0, true, false);
        takingOrderTime.setNonNegative(true);

        // initialise the clientArrivalTime
        // Parameters:
        // this                = belongs to this model
        // "ClientArrivalTimeStream" = the name of the stream
        // 5.0                      = mean time in minutes a new client arrives
        // true                = show in report?
        // false               = show in trace?
        clientArrivalTime= new ContDistExponential(this, "ClientArrivalTimeStream",
                5.0, true, false);
        clientArrivalTime.setNonNegative(true);

        // initialise the payingOrderTime
        // Parameters:
        // this                = belongs to this model
        // "PayingOrderTimeStream" = the name of the stream
        // 1.5                      = mean time in minutes for a client to pay
        // true                = show in report?
        // false               = show in trace?
        payingOrderTime= new ContDistExponential(this, "PayingOrderTimeStream",
                1.5, true, false);
        payingOrderTime.setNonNegative(true);

        // initialise the cookingOrderTime
        // Parameters:
        // this                = belongs to this model
        // "CookingOrderTimeStream" = the name of the stream
        // 7                      = mean time in minutes for a chef to cook an order
        // true                = show in report?
        // false               = show in trace?
        cookingOrderTime= new ContDistExponential(this, "CookingOrderTimeStream",
                7, true, false);
        cookingOrderTime.setNonNegative(true);

        // initalise the clientQueue1
        // Parameters:
        // this          = belongs to this model
        // "Clients Queue 1" = the name of the Queue
        // true          = show in report?
        // true         = show in trace?
        clientQueue1 = new Queue<Client>(this, "Clients Queue 1", true, true);

        // initalise the clientQueue2
        // Parameters:
        // this          = belongs to this model
        // "Clients Queue 2" = the name of the Queue
        // true          = show in report?
        // true         = show in trace?
        clientQueue2 = new Queue<Client>(this, "Clients Queue 2", true, true);

        // initalise the cashierQueue1
        // Parameters:
        // this            = belongs to this model
        // "Cashier Queue 1" = the name of the Queue
        // true            = show in report?
        // true            = show in trace?
        cashierQueue1 = new Queue<Cashier>(this, "Cashier Queue 1", true, true);

        // initalise the cashierQueue2
        // Parameters:
        // this            = belongs to this model
        // "Cashier Queue 2" = the name of the Queue
        // true            = show in report?
        // true            = show in trace?
        cashierQueue2 = new Queue<Cashier>(this, "Cashier Queue 2", true, true);

        // initalise the cashierQueue3
        // Parameters:
        // this            = belongs to this model
        // "Cashier Queue 3" = the name of the Queue
        // true            = show in report?
        // true            = show in trace?
        cashierQueue3 = new Queue<Cashier>(this, "Cashier Queue 3", true, true);

        // initalise the chefQueue
        // Parameters:
        // this            = belongs to this model
        // "Chef Queue" = the name of the Queue
        // true            = show in report?
        // true            = show in trace?
        chefQueue = new Queue<Chef>(this, "Chef Queue", true, true);


        // place the cashiers into the idle cashier queue 1
        // We don't do this in the doInitialSchedules() method because
        // we aren't placing anything on the event list here.
        Cashier Cashier;
        for (int i = 0; i < NUM_CASHIERS ; i++)
        {

            Cashier = new Cashier(this, "Cashier", true);

            cashierQueue1.insert(Cashier);
        }


        // place the chefs into the idle chefs queue
        // We don't do this in the doInitialSchedules() method because
        // we aren't placing anything on the event list here.
        Chef Chef;
        for (int i = 0; i < NUM_CHEFS ; i++)
        {

            Chef = new Chef(this, "Chef", true);

            chefQueue.insert(Chef);
        }




    }

    /**
     * Returns a sample of the random stream used to determine the
     * time to take note of the order of the client.
     *
     * @return double a takingOrderTime sample
     */
    public double getTakingOrderTime() {
        return takingOrderTime.sample();
    }
    /**
     * Returns a sample of the random stream used to determine
     * the next CLIENT arrival time.
     *
     * @return double a clientArrivalTime sample
     */
    public double getClientArrivalTime() {
        return clientArrivalTime.sample();
    }

    /**
     * Returns a sample of the random stream used to determine
     * the time needed by the client for paying the order.
     *
     * @return double a payingOrderTime sample
     */
    public double getPayingOrderTime() {
        return payingOrderTime.sample();
    }

    /**
     * Returns a sample of the random stream used to determine
     * the time needed by the chef for cooking the order.
     *
     * @return double a cookingOrderTime sample
     */
    public double getCookingOrderTime() {
        return cookingOrderTime.sample();
    }

    /**
     * Runs the model.
     *
     * @param args is an array of command-line arguments (will be ignored here)
     */
    public static void main(java.lang.String[] args) {

        // create model and experiment
        EventsMcBurger model = new EventsMcBurger(null,
                "Simple Event-Oriented Burger Model", true, true);
        // null as first parameter because it is the main model and has no mastermodel

        Experiment exp = new Experiment("EventsMcBurgerExperiment");
        // ATTENTION, since the name of the experiment is used in the names of the
        // output files, you have to specify a string that's compatible with the
        // filename constraints of your computer's operating system.
        //Sets the seed
        exp.setSeedGenerator(SEED);
        // connect both
        model.connectToExperiment(exp);

        // set experiment parameters
        exp.setShowProgressBar(true);  // display a progress bar (or not)
        exp.stop(new TimeInstant(DURATION, TimeUnit.MINUTES));   // set end of simulation at DURATION minutes
        exp.tracePeriod(new TimeInstant(0), new TimeInstant(100, TimeUnit.MINUTES));
        // set the period of the trace
        exp.debugPeriod(new TimeInstant(0), new TimeInstant(50, TimeUnit.MINUTES));   // and debug output
        // ATTENTION!
        // Don't use too long periods. Otherwise a huge HTML page will
        // be created which crashes Netscape :-)


        // start the experiment at simulation time 0.0
        exp.start();

        // --> now the simulation is running until it reaches its end criterion
        // ...
        // ...
        // <-- afterwards, the main thread returns here

        // generate the report (and other output files)
        exp.report();

        // stop all threads still alive and close all output files
        exp.finish();
    }



} /* end of model class */