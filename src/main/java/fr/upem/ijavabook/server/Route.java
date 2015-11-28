package fr.upem.ijavabook.server;

import fr.upem.ijavabook.exmanager.ExerciseService;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;

/**
 * Immutable object represent a HTTP-2 route.
 *
 * @author Damien Chesneau
 */
class Route {
    /**
     * This represent a method how are called when the request come to the server.
     */
    @FunctionalInterface
    interface EventWithExercise {
        /**
         * Method called when client send request.
         * @param routingContext instance of routing context.
         * @param exerciseService instance of exercises service.
         */
        void doAction(RoutingContext routingContext, ExerciseService exerciseService);
    }

    private final String routePath;
    private final RequestType requestType;
    private final EventWithExercise event;

    /**
     * Create a new HTTP route.
     *
     * @param routePath   is the path after server declaration, clients use this to use service.
     * @param event       is the lambda executed when client call routePath.
     * @param requestType if it is a post or an get method.
     */
    Route(String routePath, EventWithExercise event, RequestType requestType) {
        this.routePath = Objects.requireNonNull(routePath);
        this.event = Objects.requireNonNull(event);
        this.requestType = Objects.requireNonNull(requestType);
    }

    /**
     * Lambda executed when the route are called.
     *
     * @return Lambda to run.
     */
    EventWithExercise getEvent() {
        return event;
    }

    /**
     * Representation of what the client will call.
     *
     * @return route name.
     */
    String getRoute() {
        return routePath;
    }

    /**
     * Return the type of the route.
     * @return post or get.
     */
    RequestType getRequestType() {
        return requestType;
    }
}
