import desmoj.core.simulator.*;
import java.util.concurrent.TimeUnit;
/**
 * This class represents the payment end event
 * in the EventsMcBurger model.
 * It occurs when a client finishes paying his/her order.
 */
public class PaymentEndEvent extends Event<Cashier> {

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
    public PaymentEndEvent(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);
        // store a reference to the model this event is associated with
        myModel = (EventsMcBurger) owner;
    }

    /**
     * This eventRoutine() describes what happens when a client finishes paying the order.
     * <p>
     * The client leave the burger
     * If there is one client waiting to ask an order, the cashier will attend him/her
     * If not the cashier will wait in queue 1
     */
    public void eventRoutine(Cashier cashier) {

        // pass the departure the end of the preparation to the trace
        sendTraceNote("Payment ended:" + cashier.getAttendedClient() + "leaves the burger");

        // the cashier waits for more clients
        myModel.cashierQueue1.insert(cashier);


        // check if there are clients waiting
        if (!myModel.clientQueue1.isEmpty()) {

            // yes, it is

            // get a reference to the first client from the  clients queue
            Client client = myModel.clientQueue1.first();
            // remove it from the queue
            myModel.clientQueue1.remove(client);
            // remove cashier from the queue
            myModel.cashierQueue1.remove(cashier);

            cashier.setAttendedClient(client);
            // create an order taken event
            OrderTakenEvent orderTaken = new OrderTakenEvent(myModel,
                    "OrderTakenEvent", true);

            // and place it on the event list
            orderTaken.schedule( cashier, new TimeSpan(myModel.getTakingOrderTime(), TimeUnit.MINUTES));

        }
    }
}