package fr.upem.ijavabook.server;

import io.vertx.ext.web.Router;

/**
 * @author Steeve Sivanantham
 */
enum RequestType {
    GET, POST;

    io.vertx.ext.web.Route getMethod(Router router, String path) {
        switch (this) {
            case GET:
                return router.get(path);
            case POST:
                return router.post(path);
            default:
                throw new UnsupportedOperationException();
        }
    }
}
