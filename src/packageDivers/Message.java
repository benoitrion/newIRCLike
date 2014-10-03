/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package packageDivers;
import java.awt.Color;
import java.io.Serializable;

/**
 *
 * @author Benoit
 */
public class Message implements Serializable {

    public static final int PORT_NUM = 4444;

    public enum Type {

        CLT_LOGIN,          // Client login
        CLT_LOGOUT,         // Client logout
        CLT_MESSAGE,        // Message sending
        CLT_NAMES,
        SRV_RESPONSE,       // Answer sending
        SRV_BROADCAST,      // Broadcast message
        SRV_ERROR,           // Error message
        SRV_NAMES
    }

    private Type type;
    private String message;
    private Color color;

    public Message(Type type, String message, Color color) {
        this.type = type;
        this.message = message;
        this.color = color;
    }
    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public Color getColor() {
        return color;
    }

}

