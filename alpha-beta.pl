walls(Walls):-
    Walls = [0-0, 0-50, 0-100, 0-150, 0-200, 0-250, 0-300, 0-350, 0-400, 0-500, 0-550,      /*left wall*/

             0-550, 50-550, 100-550, 150-550, 200-550, 250-550, 300-550, 350-550, 400-550, 450-550,   /*bottom wall*/
             500-550, 500-550, 550-550, 600-550, 650-550, 700-550, 750-550, 800-550, 850-550,
             900-550, 950-550, 1000-550, 1050-550, 1100-550, 1150-550, 1200-550, 1250-550,

            1250-0, 1250-50, 1250-100, 1250-150, 1250-200, 1250-250,                                   /*right wall*/
            1250-300, 1250-350, 1250-400, 1250-450, 1250-500, 1250-550,

            50-0, 100-0, 150-0, 200-0, 250-0, 300-0, 350-0, 400-0, 450-0,                                /*top wall*/
            500-0, 500-0, 550-0, 600-0, 650-0, 700-0, 750-0, 800-0, 850-0,
            900-0, 950-0, 1000-0, 1050-0, 1100-0, 1150-0, 1200-0, 1250-0,

            200-450, 250-450, 300-450, 350-450,                                                  /*inner walls*/
            300-100, 300-150, 300-200, 300-2500,
            750-300, 750-350, 750-400, 750-450, 800-350, 850-350,
            900-200, 950-200, 1000-200, 1050-200, 1100-200].


next_moves(X-Y, [X-Y], DEPTH):-
    DEPTH is 3,!.

next_moves(X-Y, _, _):-
    walls(Walls), member(X-Y, Walls).

next_moves(X-Y, MOVES, DEPTH):-
    DEPTH < 3,
    X1 is X, Y1 is Y + 50,          /* up */
    X2 is X +50, Y2 is Y + 50,      /* up and right*/
    X3 is X + 50, Y3 is Y,             /*right*/
    X4 is X + 50, Y4 is Y -50,
    X5 is X, Y5 is Y - 50,
    X6 is X - 50, Y6 is Y - 50,
    X7 is X - 50, Y7 is Y,
    X8 is X - 50, Y8 is Y + 50,
    DEPTH2 is DEPTH + 1,
    next_moves(X1-Y1, MOVES1, DEPTH2),
    next_moves(X2-Y2, MOVES2, DEPTH2),
    next_moves(X3-Y3, MOVES3, DEPTH2),
    next_moves(X4-Y4, MOVES4, DEPTH2),
    next_moves(X5-Y5, MOVES5, DEPTH2),
    next_moves(X6-Y6, MOVES6, DEPTH2),
    next_moves(X7-Y7, MOVES7, DEPTH2),
    next_moves(X8-Y8, MOVES8, DEPTH2),
    append(MOVES1,MOVES2,RES1), append(MOVES3,MOVES4,RES2),
    append(MOVES5,MOVES6,RES3), append(MOVES7,MOVES8,RES4),
    append(RES1,RES2,R1), append(RES3,RES4,R2),
    append(R1,R2,MOVES).











