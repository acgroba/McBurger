import desmoj.core.simulator.*;
import java.util.concurrent.TimeUnit;
/**
 * This class represents the order cooked event
 * in the EventsMcBurger model.
 * It occurs when a chef finishes cooking the order of a client.
 */
public class OrderCookedEvent extends EventOf2Entities<Chef, Cashier> {

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
    public OrderCookedEvent(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);
        // store a reference to the model this event is associated with
        myModel = (EventsMcBurger) owner;
    }

    /**
     * This eventRoutine() describes what happens when a chef finishes
     * cooking an order.
     * <p>
     * The client will then start paying the order
     * If there is one cashier waiting to deliver an order to the kitchen, the cashier will give the order to the chef
     * If not chef will wait on chefs queue
     */
    public void eventRoutine(Chef chef, Cashier cashier) {

        // pass the departure the end of the preparation to the trace
        sendTraceNote("Order of" + cashier.getAttendedClient() + "taken by"+cashier+ "already cooked by"+ chef);

        // remove cashier  from the cashierQueue2
        myModel.cashierQueue3.remove(cashier);

        // remove client  from the clientQueue2
        myModel.clientQueue2.remove(cashier.getAttendedClient());

        // create an payment end event
        PaymentEndEvent paymentEnd = new PaymentEndEvent(myModel,
                "PaymentEndEvent", true);

        // and place it on the event list
        paymentEnd.schedule(cashier, new TimeSpan(myModel.getPayingOrderTime(), TimeUnit.MINUTES));

        // check if there are cashiers waiting
        if (!myModel.cashierQueue2.isEmpty()) {

            // yes, it is

            // get a reference to the first cashier from the  cashiers queue2
            Cashier nextCashier = myModel.cashierQueue2.first();
            // remove it from the queue
            myModel.cashierQueue2.remove(nextCashier);

            // insert the nextCashier in cashierQueue3
            myModel.cashierQueue3.insert(nextCashier);

            // create an order cooked event
            OrderCookedEvent orderCooked = new OrderCookedEvent(myModel,
                    "OrderCookedEvent", true);

            // and place it on the event list
            orderCooked.schedule(chef, nextCashier, new TimeSpan(myModel.getCookingOrderTime(), TimeUnit.MINUTES));

        } else {
            // NO, there are no cashiers waiting

            // --> the chef is waits in chefs queue
            myModel.chefQueue.insert(chef);

        }
    }
}