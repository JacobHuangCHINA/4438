import java.util.Vector; //We need this for the Vector class.

public class BFS extends Algorithm {

    public Object run() {
        // Invoke the main algorithm, passing as parameter the node's id.
        String lastmsg = bfsTree(getID());
        return lastmsg;
    }

    public String bfsTree(String id) {
        try {
            Message mssg, message, ack;  // Buffer for message to send, message received, and acknowledgement
            String parent;               // Parent and children of this node in the BFS tree
            Vector<String> children = new Vector<String>();
            String[] data;               // Data to place in each message

            Vector<String> adjacent = neighbours(); // Set of neighbours of this node
            Integer sum_children = 0;
            if (isRoot()) { // The root node will send messages in the first round.
                mssg = makeMessage(adjacent, pack(id,"?")); // Request for adoption to all neighbours.
                parent = "";                                // No parent
            }
            else { // If processor is not the root node.
                mssg = null;
                parent = "unknown";
            }
            ack = null;
            int rounds_left = -1;

            while (waitForNextRound()) {  
                if (mssg != null) {
                	send(mssg);      // Send requests for adoption to all neighbours
                	rounds_left = 1; // Wait two rounds to get responses to requests
                }
                if (ack != null) send(ack); // Send acknowledgement to parent
                mssg = null;
                ack = null;

                message = receive();
                while (message != null) {
                	data = unpack(message.data());       // Get the data from the message
                	if (data[1].equals("?")) {           // Request for adoption
                		if (equal(parent, "unknown")) {  // Parent not set yet
                			parent = data[0];
                			adjacent.remove(parent);     // Requests for adoption will not be sent to parent
                            mssg = makeMessage(adjacent, pack(id,"?")); // Sent own adoption requests to neighbours
                            ack = makeMessage(parent, pack(id,"Y"));    // Agree to be child of parent
                		}
                		else adjacent.remove(data[0]); // Important: Do not send request for adoption to processors
                                                       // that have sent to this processor am adoption request.
                	}
                	else if (data[1].equals("Y")) {    // Neighbour agreed to be child of this processor
                		children.add(data[0]);
                		sum_children += children.size()+1;}
                    	message = receive();
                }

                if (rounds_left == 0){  // Print partent, children, and then terminate
                	String outMssg = Integer.toString(children.size());		
                		
                		showMessage(outMssg);
                		printMessage(outMssg);
                		showMessage(Integer.toString(sum_children));
                    	return "";
                } else if (rounds_left == 1) {
                    rounds_left = 0;
                }
            }

        } catch(SimulatorException e){
            System.out.println("ERROR: "+e.getMessage());
        }
        
        // If we got here, something went wrong! (Exception, node failed, etc.)
        // Return no result.
        return null;
    }
}
