package fr.upem.ijavabook.server;

import io.vertx.ext.web.*;
import io.vertx.ext.web.Route;

import java.util.Objects;

/**
 * enum witch represents a type of request
 * @author Steeve Sivanantham
 */
enum RequestType {
    GET {
        @Override
        Route getRequestTypeApplication(Router router, String path) {
            idValid(router,path);
            return router.get(path);
        }
    }, POST {
        @Override
        Route getRequestTypeApplication(Router router, String path) {
            idValid(router,path);
            return router.post(path);
        }
    };

    private static void idValid(Router router, String path) {
        Objects.requireNonNull(router);
        Objects.requireNonNull(path);
    }

    /**
     * returns the application of the corresponding requestType
     * @param router router witch has the requestType
     * @param path the route path
     * @return
     */
    abstract Route getRequestTypeApplication(Router router, String path);
}
