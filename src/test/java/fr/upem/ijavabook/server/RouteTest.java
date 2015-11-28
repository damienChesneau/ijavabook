package fr.upem.ijavabook.server;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Damien Chesneau
 */
public class RouteTest {

    @Test
    public void testRoute() {
        Route r = new Route("/test", (rc, t) -> {
        }, RequestType.GET);
        assertEquals("/test", r.getRoute());
        assertNotNull(r.getEvent());
    }
}