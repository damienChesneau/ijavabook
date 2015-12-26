package fr.upem.ijavabook.server;

import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;

import java.util.Objects;

/**
 * enum witch represents a type of request
 *
 * @author Steeve Sivanantham
 */
enum RequestType {
    GET {
        @Override
        Route getRoute(Router router, String path) {
            return router.get(path);
        }
    }, POST {
        @Override
        Route getRoute(Router router, String path) {
            return router.post(path);
        }
    };

    abstract Route getRoute(Router router, String path);

    /**
     * Returns the application of the corresponding requestType
     *
     * @param router router witch has the requestType
     * @param path   the route path
     * @return Route
     */
    Route getRequestTypeApplication(Router router, String path) {
        Objects.requireNonNull(router);
        Objects.requireNonNull(path);
        return getRoute(router, path);
    }
}
