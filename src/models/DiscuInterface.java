/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author Rima
 */
public interface DiscuInterface extends java.rmi.Remote{
    
    public void AddMsg(TextMessage msg) throws java.rmi.RemoteException;

    
}
