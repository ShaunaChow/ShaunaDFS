package top.shauna.dfs.interact.soldierconnect.udpsupport;

import java.io.IOException;
import java.net.*;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/27 22:10
 * @E-Mail z1023778132@icloud.com
 */
public class UDPUtil {

    public static boolean send(String host, int port, String data){
        DatagramSocket sendDatagramSocket = null;
        try {
            sendDatagramSocket = new DatagramSocket();
            byte[] bytes = data.getBytes();
            DatagramPacket sendPscket = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(host),port);
            sendDatagramSocket.send(sendPscket);
        } catch (SocketException e) {
            e.printStackTrace();
            return false;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            if(sendDatagramSocket!=null) sendDatagramSocket.close();
        }
        return true;
    }
}
