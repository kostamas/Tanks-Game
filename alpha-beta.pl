walls(Walls):-
    Walls = [0-0, 0-50, 0-100, 0-150, 0-200, 0-250, 0-300, 0-350, 0-400, 0-500, 0-550,      /*left wall*/

             0-550, 50-550, 100-550, 150-550, 200-550, 250-550, 300-550, 350-550, 400-550, 450-550,   /*bottom wall*/
             500-550, 500-550, 550-550, 600-550, 650-550, 700-550, 750-550, 800-550, 850-550,
             900-550, 950-550, 1000-550, 1050-550, 1100-550, 1150-550, 1200-550, 1250-550,

            1250-0, 1250-50, 1250-100, 1250-150, 1250-200, 1250-250,                                   /*right wall*/
            1250-300, 1250-350, 1250-400, 1250-450, 1250-500, 1250-550,

            50-0, 100-0, 150-0, 200-0, 250-0, 300-0, 350-0, 400-0, 450-0,                                /*top wall*/
            500-0, 500-0, 550-0, 600-0, 650-0, 700-0, 750-0, 800-0, 850-0,
            900-0, 950-0, 1000-0, 1050-0, 1100-0, 1150-0, 1200-0, 1250-0


     ].

shooting_area(Distance):-
    Distance is 190.

bad_val(BadVal):-
    BadVal is 100000.

moves(_,VISTED, VISTED, DEPTH,_):-
 DEPTH = 5,!.


moves([_,_, _, MniMaxDepth], _):-
    MniMaxDepth is 4,!,fail.

moves([CX-CY, HX-HY, PLAYER, MniMaxDepth], PosList):-
   (PLAYER = computer, NextPlayer = humen
    ;
    PLAYER = humen, NextPlayer = computer),
    MniMaxDepth2 is MniMaxDepth + 1,
    VISITED = [[CX-CY, HX-HY, NextPlayer, MniMaxDepth2]],
    moves([CX-CY, HX-HY, PLAYER, MniMaxDepth2], VISITED, PosList, 1).


moves([CX-CY, HX-HY, PLAYER, MniMaxDepth], VISTED, MOVES, DEPTH):-                            /* C-computer & H-humen */
   (PLAYER = computer, X1 = CX, Y1 = CY, X2 = HX, Y2 = HY, NextPlayer = humen
    ;
    PLAYER = humen, X1 = HX, Y1 = HY, X2 = CX, Y2 = CY, NextPlayer = computer),
    DEPTH1 is DEPTH + 1,
    eight_neighbors(X1-Y1, X2-Y2, NextPlayer, [], MOVES1, DEPTH1, MniMaxDepth),           /* X1,Y1 - active player position, X2,Y2 - second player position*/
    deep_moves(MOVES1,VISTED,MOVES,DEPTH1).


eight_neighbors(_,_,_,VISTED,VISTED,DEPTH):-
    DEPTH = 5,!.


eight_neighbors(X-Y, NextPlayerX2-NextPlayerY2, NextPlayer, VISTED, MOVES, _, MniMaxDepth):-       /* eight possible moves*/
	  X1 is X, Y1 is Y + 50,  add_to_moves(X1-Y1, NextPlayerX2-NextPlayerY2, NextPlayer, VISTED, MOVES1, MniMaxDepth),
          X2 is X +50, Y2 is Y + 50, add_to_moves(X2-Y2, NextPlayerX2-NextPlayerY2, NextPlayer, MOVES1, MOVES2, MniMaxDepth),
          X3 is X + 50, Y3 is Y, add_to_moves(X3-Y3, NextPlayerX2-NextPlayerY2, NextPlayer, MOVES2, MOVES3, MniMaxDepth),
          X4 is X + 50, Y4 is Y -50, add_to_moves(X4-Y4, NextPlayerX2-NextPlayerY2, NextPlayer, MOVES3, MOVES4, MniMaxDepth),
          X5 is X, Y5 is Y - 50, add_to_moves(X5-Y5, NextPlayerX2-NextPlayerY2, NextPlayer, MOVES4, MOVES5, MniMaxDepth),
          X6 is X - 50, Y6 is Y - 50, add_to_moves(X6-Y6, NextPlayerX2-NextPlayerY2, NextPlayer, MOVES5, MOVES6, MniMaxDepth),
          X7 is X - 50, Y7 is Y, add_to_moves(X7-Y7, NextPlayerX2-NextPlayerY2, NextPlayer, MOVES6, MOVES7, MniMaxDepth),
          X8 is X - 50, Y8 is Y + 50 , add_to_moves(X8-Y8, NextPlayerX2-NextPlayerY2, NextPlayer, MOVES7, MOVES, MniMaxDepth).


add_to_moves(CurrentPlayerX-CurrentPlayerY, NextPlayerX2-NextPlayerY2, NextPlayer, VISITED, MOVES, MniMaxDepth):-
   (NextPlayer = humen, CX = CurrentPlayerX, CY = CurrentPlayerY, HX = NextPlayerX2, HY = NextPlayerY2
    ;
    NextPlayer = computer, CX = NextPlayerX2, CY = NextPlayerY2, HX = CurrentPlayerX, HY = CurrentPlayerY),
    walls(Walls),
    not(member(CurrentPlayerX-CurrentPlayerY, Walls)),
    CurrentPlayerX > 0, CurrentPlayerX < 1200,                                  /*game borders*/
    CurrentPlayerY> 0 , CurrentPlayerY < 600,                                   /*game borders*/
    append([[CX-CY,HX-HY,NextPlayer,MniMaxDepth]],VISITED,MOVES).


add_to_moves(CurrentPlayerX-CurrentPlayerY, NextPlayerX2-NextPlayerY2, NextPlayer, VISITED, VISITED,_):-
    CurrentPlayerX =< 0 ; CurrentPlayerX >= 1200;                                 /*game borders*/
    CurrentPlayerY =< 0 ; CurrentPlayerY >= 600;                                  /*game borders*/
   (walls(Walls), member(CurrentPlayerX-CurrentPlayerY, Walls)).


deep_moves(_,Visited,Visited,Depth):-
    Depth = 5,!.

deep_moves([[CX-CY, HX-HY ,NextPlayer, MniMaxDepth]| MOVES], VISITED, NewMoves, DEPTH):-
   (PLAYER = computer, X1 = CX, Y1 = CY, X2 = HX, Y2 = HY, NextPlayer = humen
    ;
    PLAYER = humen, X1 = HX, Y1 = HY, X2 = CX, Y2 = CY, NextPlayer = computer),
    add_to_visited([CX-CY, HX-HY ,NextPlayer, MniMaxDepth],VISITED,VISITED1),
    moves([CX-CY, HX-HY ,PLAYER, MniMaxDepth], VISITED1, MOVES1, DEPTH),
    deep_moves(MOVES,MOVES1,NewMoves,DEPTH).

deep_moves([],VISITED,VISITED,_).

 add_to_visited(Pos,VISITED,VISITED1):-
        not(member(Pos, VISITED)),
        append([Pos],VISITED,VISITED1).

 add_to_visited(Pos,VISITED,VISITED):-
        member(Pos, VISITED).




/* ------------------ alpha beta algorithm ------------------*/

alphabeta(Pos, Alpha, Beta, GoodPos, Val):-
    moves(Pos, PosList),!,
    boundedbest(PosList, Alpha, Beta, GoodPos, Val); 
    staticval(Pos, Val).

boundedbest([Pos|PosList], Alpha, Beta, GoodPos, GoodVal):-
    alphabeta(Pos, Alpha, Beta,_,Val),
    goodenough(PosList, Alpha, Beta, Pos, Val, GoodPos, GoodVal).

goodenough([],_,_,Pos, Val, Pos, Val):- !.

goodenough(_,Alpha, Beta, Pos, Val, Pos, Val):-
    min_to_move(Pos), Val > Beta, !;
    max_to_move(Pos), Val < Alpha, !.


goodenough(PosList, Alpha, Beta, Pos, Val, GoodPos, GoodVal):-
    newbounds(Alpha, Beta, Pos, Val, NewAlpha, NewBeta),
    boundedbest(PosList, NewAlpha, NewBeta, Pos1, Val1),
    betterof(Pos, Val, Pos1, Val1, GoodPos, GoodVal).


newbounds(Alpha, Beta, Pos, Val, Val, Beta):-
    min_to_move(Pos), Val > Alpha, !.


newbounds(Alpha, Beta, Pos, Val, Alpha, Val):-
    max_to_move(Pos), Val < Beta, !.

newbounds(Alpha, Beta,_,_, Alpha, Beta).

betterof(Pos, Val, Pos1, Val1, Pos, Val):-
    min_to_move(Pos), Val > Val1, !;
    max_to_move(Pos), Val < Val1, !.

betterof(_,_,Pos1,Val1,Pos1,Val1).


min_to_move([_,_,PLAYER,_]):-
    PLAYER = humen.
 max_to_move([_,_,PLAYER,_]):-
    PLAYER = computer.

/* ------------------ alpha beta algorithm ------------------*/



/*-------------------  heuristic function  --------------------*/

staticval([CX-CY, HX-HY, PLAYER, MniMaxDepth],Val):-
       manhattan_distance([CX,CY],[HX,HY],Distance),
       shooting_area(ShootingDistance),
       bad_val(BadVal),
       (Distance =< ShootingDistance, Val is BadVal
        ;
         Val is (-Distance)
        ).


manhattan_distance([X1,Y1],[X2,Y2],RES):-
    X is X1 - X2,
    Y is Y1 - Y2,
   abs(X,PX),abs(Y,PY),
    RES is PX + PY.





/*-------------------  heuristic function  --------------------*/






