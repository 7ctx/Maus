package Server;


import Logger.Level;
import Logger.Logger;
import Server.Data.PseudoBase;
import Server.Data.Repository;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.*;
import java.net.Socket;

public class ClientObject implements Serializable, Repository {
    public String SYSTEMOS;
    transient private Socket client = new Socket();
    private int clientNumber;
    private String onlineStatus = client.isConnected() ? "Online" : "Offline";
    private String nickName;
    private String IP;
    private transient PrintWriter clientOutput;
    private transient DataOutputStream dis;

    ClientObject(Socket client, String nickName, String IP) {
        Timeline fiveSecondTime = new Timeline(new KeyFrame(Duration.seconds(3), event -> updateStatus()));
        fiveSecondTime.setCycleCount(Timeline.INDEFINITE);
        fiveSecondTime.play();
        this.client = client;
        this.nickName = nickName;
        this.IP = IP;
        try {
            this.clientOutput = new PrintWriter(client.getOutputStream(), true);
            dis = new DataOutputStream(client.getOutputStream());
            if (SYSTEMOS == null) {
                clientCommunicate("SYS");
            }
            clientCommunicate("SYS");
        } catch (IOException e) {
            Logger.log(Level.WARNING, "Exception thrown: " + e);
        }
        CONNECTIONS.put(IP, this);
    }

    public void updateStatus() {
        onlineStatus = client.isConnected() ? "Online" : "Offline";
        if(onlineStatus.equals("Offline")){
            CONNECTIONS.remove(getIP());
        }
    }

    public String getSYSTEMOS() {
        return SYSTEMOS;
    }

    public void setSYSTEMOS(String SYSTEMOS) {
        this.SYSTEMOS = SYSTEMOS;
    }

    public void setClientNumber(int clientNumber) {
        this.clientNumber = clientNumber;
    }

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    public Integer getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(Integer clientNumber) {
        this.clientNumber = clientNumber;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public PrintWriter getClientOutput() {
        return clientOutput;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public void serialize() {
        final File parent = new File(System.getProperty("user.home") + "/Maus/clients");
        if (!parent.mkdirs()) {
            Logger.log(Level.WARNING, "Unable to make necessary directories, may already exist.");
        }
        if (getIP() != null) {
            try {
                FileOutputStream fileOut = new FileOutputStream(new File(parent, getNickName() + getIP() + ".client"));
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(this);
                out.close();
                fileOut.close();
                Logger.log(Level.INFO, "Serialized data is saved in Maus/clients/**.client");
            } catch (IOException i) {
                i.printStackTrace();
            }
        }
    }

    public void clientCommunicate(String msg) throws IOException {
        dis.writeUTF(msg);
    }

    @Override
    protected void finalize() throws Throwable {
        clientOutput.close();
        client.close();
    }
}

