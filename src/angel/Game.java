/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package angel;

import java.awt.Point;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

interface GameListener {
    void onMove();
}

/**
 *
 * @author aariasgonzalez
 */
public class Game {
    Server mServer;
    Client mClient;

    
    ArrayList<Player> mPlayers = new ArrayList();

    public enum Status{
        READY, WAITING
    }

    
    public boolean join(Player player){
        if(!mPlayers.contains(player)){
            mPlayers.add(player);
            return true;
        }
        return false;
    }
    
    
    private Player mPrevTurn = null;
    
    public static int LINE_ELEMENTS = 12;
    private Box mBoxes[][] = new Box[LINE_ELEMENTS][LINE_ELEMENTS];
    
    Game(){
        for(int x = 0; x < mBoxes.length ; x++){
            for(int y = 0; y < mBoxes[x].length ; y++){
                mBoxes[x][y] = new Box(new Point(x, y));
            }
        }

        

        startGame();
        
        mClient = new Client(this);
        try {
            mClient.connect("localhost");
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    public void startGame(){
        mServer =  new Server(4, this);
        mServer.start();
    }
    
    
    
    public void tryMove(int x, int y){
        try {
            mClient.move(x, y);
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    
    
    
    
    
    public Box[][] getBoxes(){
        return mBoxes;
    }
    
    public boolean isMoveValid(int x, int y, Player player){
        if(getCurrentTurn() == player && mBoxes[x][y].getPlayer() == null){
            mBoxes[x][y].setPlayer(player);
            mPrevTurn = player;
            return true;
        }
        return false;
    }
    public Player checkForWin(){
        
        Player p = checkRow();
        if(p != null){
            return p;
        } 
        p = checkCol();
        if(p != null){
            return p;
        }
        p = checkDiag();
        if(p != null){
            return p;
        }        
        return null;
    }
    
    //Need revision
    private Player checkRow() {
        for(int y = 0; y < mBoxes.length; y++){
            for(int x = 0; x < mBoxes.length; x++){
                for(int i = 0; x+i < mBoxes.length-1; i++){
                    Player p0 = mBoxes[x+i][y].getPlayer();
                    Player p1 = mBoxes[x+i+1][y].getPlayer();
                    if(p0 == p1 && p0 != null){
                        if(i >= 2){
                            return mBoxes[x+i][y].getPlayer();
                        }
                    }else{
                        break;
                    }
                }
            }
        }
        return null;
    } 
    private Player checkCol() {
        for(int x = 0; x < mBoxes.length; x++){
            for(int y = 0; y < mBoxes[x].length; y++){
                for(int i = 0; y+i < mBoxes[x].length-1; i++){
                    Player p0 = mBoxes[x][y+i].getPlayer();
                    Player p1 = mBoxes[x][y+i+1].getPlayer();
                    if(p0 == p1 && p0 != null){
                        if(i >= 2){
                            return mBoxes[x][y+i].getPlayer();
                        }
                    }else{
                        break;
                    }
                }
            }
        }
        return null;
    }
    
    private Player checkDiag() {

        for(int z = 0; z <= mBoxes.length*2; z++){
            for(int c = 0; c < z; c++){
                Player p = null;
                for(int l = 0; l < mBoxes.length; l++){
                    int x = (c+l);
                    int y = mBoxes.length-(z-(c+l));
                    if(x < 0 || x > mBoxes.length-1 || y < 0 || y > mBoxes.length-1) continue;
                    if(p == null) p = mBoxes[x][y].getPlayer();
                    if(p == mBoxes[x][y].getPlayer() && p != null){
                        p = mBoxes[x][y].getPlayer();
                        if(l >= 3){
                            return p;
                        }
                    }else{
                        break;
                    }
                 

                }
            }
        }
        for(int z = 0; z <= mBoxes.length*2; z++){
            for(int c = 0; c < z; c++){
                Player p = null;
                for(int l = 0; l < mBoxes.length; l++){
                    int x = (c+l);
                    int y = (z-(c+l));
                    if(x < 0 || x > mBoxes.length-1 || y < 0 || y > mBoxes.length-1) continue;
                    if(p == null) p = mBoxes[x][y].getPlayer();
                    if(p == mBoxes[x][y].getPlayer() && p != null){
                        p = mBoxes[x][y].getPlayer();
                        if(l >= 3){
                            return p;
                        }
                    }else{
                        break;
                    }
                 

                }
            }
        }
        
        
        

        return null;
    } 
     
     
    public Player getCurrentTurn(){
        if(mPlayers.size() <= 0) return null;
        if(mPrevTurn == null) mPrevTurn = mPlayers.get(0);
        int i = mPlayers.indexOf(mPrevTurn);
        i++;
        if(i >= mPlayers.size()) i = 0;
        if(i < 0) return null;
        return mPlayers.get(i);
    }
    
    
    
    
}
