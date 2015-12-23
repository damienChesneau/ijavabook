package fr.upem.ijavabook.server.manageclients;

import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.HashMap;

/**
 * Here we manage instances of clients we create new if is asked.
 * And remove from memory when client go.
 *
 * @author Damien Chesneau
 * @author Steeve Sivanantham
 */
public class ClientsManager {
    private final HashMap<Integer, Client> connections = new HashMap<>();
    private final Object lock = new Object();

    /**
     * Create a new client and return his token.
     * @param exercise java.nio.file.Path ...
     * @return int token
     */
    public int newClient(Path exercise) {
        synchronized (lock) {
            int token = getNewToken();
            connections.put(token, new Client(exercise));
            return token;
        }
    }

    private int getNewToken() {
        SecureRandom random = new SecureRandom();
        int token = random.nextInt();
        if (connections.containsKey(token)) {
            return getNewToken();
        }
        return token;
    }

    /**
     * Get a client by his token.
     * @param token
     * @throws IllegalArgumentException if client don't present.
     * @return a Client.
     */
    public Client getClientByToken(int token) {
        synchronized (lock) {
            Client client = connections.get(token);
            validateClient(client);
            return client;
        }
    }

    /**
     * Delete client from memory.
     * And close his interpreter instance.
     * @param token int
     */
    public void rmClient(int token) {
        synchronized (lock) {
            Client client = connections.get(token);
            validateClient(client);
            client.close();
            connections.remove(token);
        }
    }

    private void validateClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Wrong token.");
        }
    }
}
