package fr.upem.ijavabook.server;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;

/**
 * Immutable object represent a HTTP-2 route.
 * @author Damien Chesneau - contact@damienchesneau.fr
 */
class Route {
    private final String route;
    private final Handler<RoutingContext> event;

    Route(String route, Handler<RoutingContext> event) {
        this.route = Objects.requireNonNull(route);
        this.event = Objects.requireNonNull(event);
    }

    Handler<RoutingContext> getEvent() {
        return event;
    }

    public String getRoute() {
        return route;
    }
}
