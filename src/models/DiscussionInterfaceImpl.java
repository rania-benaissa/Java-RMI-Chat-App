package models;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 *
 * @author Rima
 */
public class DiscussionInterfaceImpl extends UnicastRemoteObject implements DiscuInterface {

    private Discussion discu;

    public DiscussionInterfaceImpl(Discussion d) throws RemoteException {
        super();
        this.discu = d;
    }

    @Override
    public void AddMsg(TextMessage msg) throws RemoteException {
        discu.addText(msg);
    }

    public Discussion getDiscu() {
        return discu;
    }

    public void setDiscu(Discussion discu) {
        this.discu = discu;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        DiscussionInterfaceImpl d = (DiscussionInterfaceImpl) obj;
        return this.discu.equals(d.getDiscu());
    }

}
