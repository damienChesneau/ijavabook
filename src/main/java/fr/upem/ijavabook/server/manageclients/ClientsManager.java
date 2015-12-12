package fr.upem.ijavabook.server.manageclients;

import java.security.SecureRandom;
import java.util.HashMap;

/**
 * @author Damien Chesneau
 * @author Steeve Sivanantham
 */
public class ClientsManager {
    private final HashMap<Integer, Client> connections = new HashMap<>();
    private final SecureRandom random = new SecureRandom();
    private final Object lock = new Object();

    public int newClient() {
        synchronized (lock) {
            int token = getNewToken();
            connections.put(token, new Client());
            return token;
        }
    }

    private int getNewToken() {
        int token = random.nextInt();
        if (connections.containsKey(token)) {
            return getNewToken();
        }
        return token;
    }

    public Client getClientByToken(int token) {
        synchronized (lock) {
            Client client = connections.get(token);
            if (client == null) {
                throw new IllegalStateException("Invalid token.");
            }
            return client;
        }
    }
}
