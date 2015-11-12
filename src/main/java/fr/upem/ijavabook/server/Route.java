package fr.upem.ijavabook.server;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;

/**
 * Immutable object represent a HTTP-2 route.
 *
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class Route {
    private final String routePath;
    private final Handler<RoutingContext> event;

    /**
     * Create a new HTTP route.
     *
     * @param routePath is the path after server declaration, clients use this to use service.
     * @param event     is the lambda executed when client call routePath.
     */
    Route(String routePath, Handler<RoutingContext> event) {
        this.routePath = Objects.requireNonNull(routePath);
        this.event = Objects.requireNonNull(event);
    }

    /**
     * Lambda executed when the route are called.
     *
     * @return Lambda to run.
     */
    Handler<RoutingContext> getEvent() {
        return event;
    }

    /**
     * Representation of what the client will call.
     * @return String value.
     */
    public String getRoute() {
        return routePath;
    }
}
