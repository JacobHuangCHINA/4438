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
            Integer sum = 0;
            Integer distance = 0;

            Vector<String> adjacent = neighbours(); // Set of neighbours of this node
            Integer sum_children = 0;
            if (isRoot()) { // the leaf node send mssg to parent
                mssg = makeMessage(getParent(), pack(0,0)); // with message sum of the distance to child and the number of child
              
            }
            else { // If processor is the root node.
                mssg = null;
             
            }
            ack = null;
            int rounds_left = -1;

            while (waitForNextRound()) {  
                if (!isRoot()) {
                	send(mssg);      // Send requests to the parent
                    round_left = 1;
                }
                if(isRoot()){
                    round_left = 1;
                }
                mssg = null;

                message = receive();
                while (message != null) {
                    data = unpack(message.data());
                    distance = data[0] + numChildren();
                    mssg = makeMessage(getParent(),pack(distance,numChildren()));
                    
                   	message = receive();
                }

                if (rounds_left == 0){  // Print partent, children, and then terminate
                    if (isRoot()) {
                        Integer result = distance + numChildren;
                        String outMssg = Integer.toString(result);
                		showMessage(outMssg);
                		printMessage(outMssg);
                    }else{
                        String outMssg = Integer.toString(distance);
                		showMessage(outMssg);
                		printMessage(outMssg);
                    	return "";
                    }
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
