package shared.responses;

import shared.responses.ResponseType;
import shared.responses.Response;

import java.util.ArrayList;

/**
 * This class is used to send a list to the client.
 */
public class ListResponse extends Response {
        private final ArrayList<String> list;
        //constructor

    /**
     * Constructor for the ListResponse class.
     * @param list The list to be sent to the client.
     */
        public ListResponse(ArrayList<String> list) {
            super(ResponseType.LIST);
            this.list = list;
        }

    /**
     * Getter for the list.
     * @return The list.
     */
    public ArrayList<String> getList() {
            return list;
        }

    /**
     * Prints the list.
     */
    public void  printList() {
        int index = 1;
        for (String s : list) {
            System.out.println(index++ + "- " + s);
        }
    }
}
