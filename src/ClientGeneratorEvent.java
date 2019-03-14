import desmoj.core.simulator.*;
import java.util.concurrent.TimeUnit;

/**
 * This class represents an entity (and event) source, which continually generates
 * clients (and their arrival events) in order to keep the simulation running.
 *
 * It will create a new client, schedule its arrival at the terminal (i.e. create
 * and schedule an arrival event) and then schedule itself for the point in
 * time when the next client arrival is due.
 */
public class ClientGeneratorEvent extends ExternalEvent {

    /**
     * Constructs a new ClientGeneratorEvent.
     *
     * @param owner the model this event belongs to
     * @param name this event's name
     * @param showInTrace flag to indicate if this event shall produce output
     *                    for the trace
     */
    public ClientGeneratorEvent(Model owner, String name, boolean showInTrace) {
        super(owner, name, showInTrace);
    }

    /**
     * The eventRoutine() describes the generating of a new truck.
     *
     * It creates a new client, a new ClientArrivalEvent
     * and schedules itself again for the next new truck generation.
     */
    public void eventRoutine() {

        // get a reference to the model
        EventsMcBurger model = (EventsMcBurger)getModel();

        // create a new client
        Client client = new Client(model, "Client", true);
        // create a new client arrival event
        ClientArrivalEvent clientArrival = new ClientArrivalEvent(model,
                "ClientArrivalEvent", true);
        // and schedule it for the current point in time
        clientArrival.schedule(client, new TimeSpan(0, TimeUnit.MINUTES));

        // schedule this client generator again for the next client arrival time
        schedule(new TimeSpan(model.getClientArrivalTime(), TimeUnit.MINUTES));
        // from inside to outside...
        // draw a new inter-arrival time value
        // wrap it in a TimeSpan object
        // and schedule this event for the current point in time + the
        // inter-arrival time

    }

}