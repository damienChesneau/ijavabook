package fr.upem.ijavabook.server.manageclients;

import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Here we manage instances of clients we create new if is asked.
 * And remove from memory when client go.
 * This class is thread safe.
 *
 * @author Damien Chesneau
 * @author Steeve Sivanantham
 */
public class ClientsManager {
    private final ConcurrentHashMap<Integer, Client> connections = new ConcurrentHashMap<>();
    private final Object lock = new Object();
    private int nextToken = new SecureRandom().nextInt();

    /**
     * Create a new client and return his token.
     * exercise java.nio.file.Path ...
     * @return int token
     */
    public int newClient(/*Path exercise*/) {
        int token = getNewToken();
        connections.put(token, new Client(/*exercise*/));
        return token;
    }

    private int getNewToken() {
        synchronized (lock) {
            return nextToken++;
        }
    }

    /**
     * Get a client by his token.
     * @param token token of the client
     * @throws IllegalArgumentException if client don't present.
     * @return a Client.
     */
    public Client getClientByToken(int token) {
        Client client = connections.get(token);
        validateClient(client);
        return client;
    }

    /**
     * Delete client from memory.
     * And close his interpreter instance.
     * @param token int
     */
    public void rmClient(int token) {
        Client client = connections.get(token);
        connections.remove(token);
        validateClient(client);
        client.close();
    }

    private void validateClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Wrong token.");
        }
    }
}
