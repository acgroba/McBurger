import desmoj.core.simulator.*;

import java.util.concurrent.TimeUnit;

/**
 * This class represents the client arrival event
 * in the EventsMcBurger model.
 * It occurs when a client arrives at the burger
 * to request an order.
 */
public class ClientArrivalEvent extends Event<Client> {

    /**
     * A reference to the model this event is a part of.
     * Useful shortcut to access the model's static components
     */
    private EventsMcBurger myModel;

    /**
     * Constructor of the client arrival event
     *
     * Used to create a new client arrival event
     *
     * @param owner the model this event belongs to
     * @param name this event's name
     * @param showInTrace flag to indicate if this event shall produce output
     *                    for the trace
     */
    public ClientArrivalEvent(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);
        // store a reference to the model this event is associated with
        myModel = (EventsMcBurger)owner;
    }

    /**
     * This eventRoutine() describes what happens when a clients
     * enters the burger.
     *
     * On arrival, the client will enter the queue . It will then
     * check if the cashier is available.
     * If this is the case, it will occupy the cashier and schedule an
     * order taken event.
     * Otherwise the client just waits (does nothing).
     */
    public void eventRoutine(Client client) {

        // client enters line
        myModel.clientQueue1.insert(client);
        sendTraceNote("Client "+client+" arrives. ClientQueueLength: "+ myModel.clientQueue1.length());

        // check if a Cashier is available
        if (!myModel.cashierQueue1.isEmpty()){
            // yes, it is

            // get a reference to the first Cashier from the idle Cashier queue
            Cashier cashier = myModel.cashierQueue1.first();
            // remove it from the queue
            myModel.cashierQueue1.remove(cashier);

            // remove the client from the queue
            myModel.clientQueue1.remove(client);
            cashier.setAttendedClient(client);
            // create an order taken event
            OrderTakenEvent orderTaken = new OrderTakenEvent (myModel,
                    "OrderTakenEvent", true);

            // and place it on the event list
            orderTaken.schedule(cashier, new TimeSpan(myModel.getTakingOrderTime(), TimeUnit.MINUTES));

        }

        }
    }