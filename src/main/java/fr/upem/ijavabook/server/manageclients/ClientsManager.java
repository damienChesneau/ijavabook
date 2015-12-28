package fr.upem.ijavabook.server.manageclients;

import java.io.IOException;
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
    private final SecureRandom random = new SecureRandom();

    /**
     * Create a new client and return his token.
     * exercise java.nio.file.Path ...
     *
     * @return int token
     */
    public int newClient() {
        int token = getNewToken();
        connections.put(token, new Client());
        return token;
    }

    private int getNewToken() {
        int token = random.nextInt();
        if (connections.containsKey(token)) {
            return getNewToken();
        }
        return token;
    }

    /**
     * Get a client by his token.
     *
     * @param token token of the client
     * @return a Client.
     * @throws IllegalArgumentException if client don't present.
     */
    public Client getClientByToken(int token) {
        Client client = connections.get(token);
        validateClient(client);
        return client;
    }

    /**
     * Delete client from memory.
     * And close his interpreter instance.
     *
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
