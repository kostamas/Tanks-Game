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
     ].


next_moves(X-Y,VISTED, VISTED, DEPTH):-
 DEPTH = 4,!.

next_moves(X-Y,VISTED, MOVES, DEPTH):-
    DEPTH1 is DEPTH + 1,
    moves(X-Y,[],MOVES1,DEPTH1),
    deep_moves(MOVES1,VISTED,MOVES,DEPTH1).


moves(_,VISTED,VISTED,DEPTH):-
    DEPTH = 5,!.

moves(X-Y,VISTED,MOVES,_):-       /* eight possible moves*/
	 X1 is X, Y1 is Y + 50,  add_to_moves(X1-Y1,VISTED,MOVES1),
          X2 is X +50, Y2 is Y + 50, add_to_moves(X2-Y2,MOVES1,MOVES2),
          X3 is X + 50, Y3 is Y, add_to_moves(X3-Y3,MOVES2,MOVES3),
          X4 is X + 50, Y4 is Y -50, add_to_moves(X4-Y4,MOVES3,MOVES4),
          X5 is X, Y5 is Y - 50, add_to_moves(X5-Y5,MOVES4,MOVES5),
          X6 is X - 50, Y6 is Y - 50, add_to_moves(X6-Y6,MOVES5,MOVES6),
          X7 is X - 50, Y7 is Y, add_to_moves(X7-Y7,MOVES6,MOVES7),
          X8 is X - 50, Y8 is Y + 50 , add_to_moves(X8-Y8,MOVES7,MOVES).


add_to_moves(X-Y,VISITED,MOVES):-
    walls(Walls),
    not(member(X-Y, Walls)),
    not(member(X-Y, VISITED)),
    X > 0, X < 1200,               /*game borders*/
    Y> 0 , Y < 600,                 /*game borders*/
    append([X-Y],VISITED,MOVES).

add_to_moves(X-Y,VISITED,VISITED):-
    member(X-Y, VISITED);
    X =< 0 ; X >= 1200;              /*game borders*/
    Y =< 0 ; Y >= 600;                 /*game borders*/
    walls(Walls), member(X-Y, Walls).

deep_moves(Moves,Visited,Visited,Depth):-
    Depth = 5,!.

deep_moves([X-Y|MOVES],VISITED,NewMoves,DEPTH):-
    add_to_visited(X-Y,VISITED,VISITED1),
    next_moves(X-Y,VISITED1,MOVES1,DEPTH),
    deep_moves(MOVES,MOVES1,NewMoves,DEPTH).

deep_moves([],VISITED,VISITED,_).

 add_to_visited(X-Y,VISITED,VISITED1):-
        not(member(X-Y, VISITED)),
        append([X-Y],VISITED,VISITED1).

 add_to_visited(X-Y,VISITED,VISITED):-
        member(X-Y, VISITED).















