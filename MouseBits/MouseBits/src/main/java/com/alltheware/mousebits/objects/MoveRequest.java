package com.alltheware.mousebits.objects;

import java.io.Serializable;

/**
 * Created by pedrocarmona on 12/04/14.
 */
public class MoveRequest implements Serializable {

    public int cmd;
    public int moduleX;
    public int moduleY;
    public int moduleZ;
    public int directionX;
    public int directionY;
    public int directionZ;

    public static int MOVE_REQUEST = 20;

    public MoveRequest(int moduleX, int moduleY, int moduleZ,int directionX, int directionY, int directionZ){
        cmd = MOVE_REQUEST;
        this.moduleX = moduleX;
        this.moduleY = moduleY;
        this.moduleZ = moduleZ;
        this.directionX = directionX;
        this.directionY = directionY;
        this.directionZ = directionZ;

    }

    public String toString(){
        System.out.println("CONNECT");
        String aux="";
        aux+="<cmd="+(int)cmd+">";
        aux+="<module_x="+moduleX+">";
        aux+="<module_y="+moduleY+">";
        aux+="<module_z="+moduleZ+">";
        aux+="<direction_x="+directionX+">";
        aux+="<direction_y="+directionY+">";
        aux+="<direction_z="+directionZ+">";
        return aux;
    }




}