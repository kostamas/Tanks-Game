/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tanks;

public class TankConst {

    static final int tankMoveLength = 50;
    static final int tankLength = 30;
    static final int shootingArea = 90;
    static final int STEPS = 1;
    static final int COMPUTER = 0;
    static final int HUMEN = 1;
    static final int TANK_LIFE = 9;
    static String[] directions = {"up", "up_right", "right", "down_right", "down", "down_left", "left", "up_left"};
    static int[][] bulletOffsetByDirection = {{10, 0}, {30, 0}, {37, 11}, {35, 35}, {10, 35}, {-5, 35}, {0, 11}, {0, 0}};

}
