
package com.automature.zug.license;


import java.io.*;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

import com.automature.zug.util.Log;

public class GetMac {

    public static List<String> getMacAddress() throws IOException {

        ArrayList<String> macList = new ArrayList<String>();

        try {

            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();

            for (NetworkInterface netint : Collections.list(nets)){

                if(netint.getHardwareAddress()!=null){

                    byte[] mac = netint.getHardwareAddress();
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }

                    macList.add(sb.toString());
                }

            }
        } catch (SocketException e) {
           Log.Error("Could read system info :"+e.getMessage());
        }

        return  macList ;
    }

}
