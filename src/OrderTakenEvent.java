import desmoj.core.simulator.*;
import java.util.concurrent.TimeUnit;
/**
 * This class represents the order taken event
 * in the EventsMcBurger model.
 * It occurs when a cashier finishes taking the order of a client.
 */
public class OrderTakenEvent extends Event<Cashier> {

    /**
     * A reference to the model this event is a part of.
     * Useful shortcut to access the model's static components
     */
    private EventsMcBurger myModel;

    /**
     * Constructor of the service end event
     * <p>
     * Used to create a new service end event
     *
     * @param owner       the model this event belongs to
     * @param name        this event's name
     * @param showInTrace flag to indicate if this event shall produce output
     *                    for the trace
     */
    public OrderTakenEvent(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);
        // store a reference to the model this event is associated with
        myModel = (EventsMcBurger) owner;
    }

    /**
     * This eventRoutine() describes what happens when a cashier finishes
     * taking an order.
     * <p>
     * The cashier will then check if there is a chef available to cook the order
     * If there is one, the cashier will give the order to the chef
     * If not he will wait  in queue 2
     */
    public void eventRoutine(Cashier cashier) {

        // pass the end of taking the order to the trace
        sendTraceNote("Order of" + cashier.getAttendedClient() + "already taken by"+cashier);

        // insert the client in clientQueue2
        myModel.clientQueue2.insert(cashier.getAttendedClient());

        // check if there are chefs waiting
        if (!myModel.chefQueue.isEmpty()) {

            // yes, it is

            // get a reference to the first chef from the idle chef queue
            Chef chef = myModel.chefQueue.first();
            // remove it from the queue
            myModel.chefQueue.remove(chef);

            // insert the cashier in cashierQueue3
            myModel.cashierQueue3.insert(cashier);

            // create an order cooked event
            OrderCookedEvent orderCooked = new OrderCookedEvent(myModel,
                    "OrderCookedEvent", true);

            // and place it on the event list
            orderCooked.schedule(chef, cashier, new TimeSpan(myModel.getCookingOrderTime(), TimeUnit.MINUTES));

        } else {
            // NO, there are no chefs waiting

            // --> the cashier is waits in queue2
            myModel.cashierQueue2.insert(cashier);

        }
    }
}