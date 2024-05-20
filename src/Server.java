import java.net.*;
import java.nio.charset.StandardCharsets;

public class Server {

    public static void main(String args[]) throws Exception {
        // Default port number we are going to use
        int portnumber = 8002;
        if (args.length >= 1) {
            portnumber = Integer.parseInt(args[0]);
        }

        // Create a MulticastSocket for receiving messages
        MulticastSocket serverReceiveSocket = new MulticastSocket(portnumber);
        System.out.println("MulticastSocket for receiving is created at port " + portnumber);

        // Determine the IP address of a host, given the host name
        InetAddress receiveGroup = InetAddress.getByName("225.4.5.6");

        // Join the multicast group for receiving messages
        serverReceiveSocket.joinGroup(receiveGroup);
        System.out.println("joinGroup method is called for receiving...");

        // Create a DatagramSocket for sending responses
        DatagramSocket serverSendSocket = new DatagramSocket();
        System.out.println("DatagramSocket for sending is created...");

        boolean infinite = true;

        // Continually receives data and processes them
        while (infinite) {
            try {
                byte buf[] = new byte[1024];
                DatagramPacket data = new DatagramPacket(buf, buf.length);
                serverReceiveSocket.receive(data);
                String msg = new String(data.getData(), StandardCharsets.UTF_8).trim();
                System.out.println("Message received from client = " + msg);

                // Process the message and calculate the result
                String result = countSumOrProduct(msg);
                System.out.println(result);

                // Send the result back to the client using the client's address and port
                DatagramPacket resultPacket = new DatagramPacket(result.getBytes(StandardCharsets.UTF_8), result.length(), data.getAddress(), data.getPort());
                serverSendSocket.send(resultPacket);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        serverReceiveSocket.close();
        serverSendSocket.close();
    }

    // Counts the sum or product of the message from the client
    static String countSumOrProduct(String msgFromClient) {
        if (msgFromClient.length() != 3) {
            return "Invalid format";
        }

        try {
            String num1 = msgFromClient.substring(0, 1);
            String operator = msgFromClient.substring(1, 2);
            String num2 = msgFromClient.substring(2);

            int number1 = Integer.parseInt(num1.trim());
            int number2 = Integer.parseInt(num2.trim());

            if (operator.equals("+")) {
                int sum = number1 + number2;
                return "The sum of " + msgFromClient + " is " + sum;
            } else if (operator.equals("*")) {
                int product = number1 * number2;
                return "The product of " + msgFromClient + " is " + product;
            } else {
                return "Invalid operator";
            }
        } catch (NumberFormatException e) {
            return "Invalid numbers";
        }
    }
}
