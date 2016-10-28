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

/* ------------ const values  --------------- */

shooting_area(Distance):-
    Distance is 90.

tank_move_length(Length):-
    Length is 50.

bad_val(BadVal):-
    BadVal is -10000.


alpha_beta_depth(Depth):-        /* define the depth of the alpha beta tree*/
    Depth is 6.

/* ------------ const values  --------------- */


moves([_,_, _, AlphaBetaDepth], _):-
    alpha_beta_depth(Depth),
    AlphaBetaDepth == Depth,!,fail.

moves([CTanks, HTanks, PLAYER, AlphaBetaDepth], PosList):-
    AlphaBetaDepth2 is AlphaBetaDepth + 1,
    (
       PLAYER = computer, next_moves(CTanks, [CTanks, HTanks, PLAYER, AlphaBetaDepth2], PosList) ; 
       PLAYER = humen, next_moves(HTanks, [CTanks, HTanks, PLAYER, AlphaBetaDepth2], PosList)
    ).


next_moves([[X,Y,L,Num]|Tanks], Pos, PosList):-
    tank_moves([X,Y,L,Num], Pos,PosList1),
    next_moves(Tanks,Pos, PosList2),
    append(PosList1, PosList2, PosList).


next_moves([],_,[]).


tank_moves([X,Y,L,Num], [CTanks, HTanks, PLAYER, AlphaBetaDepth], PosList):-
     PLAYER = computer,
     X1 is X - 50, Y1 is Y,      add_to_pos_list([X1,Y1,L,Num], [CTanks, HTanks, PLAYER, AlphaBetaDepth], [], [], PosList1),
     X2 is X - 50, Y2 is Y - 50, add_to_pos_list([X2,Y2,L,Num], [CTanks, HTanks, PLAYER, AlphaBetaDepth], [], PosList1, PosList2),
     X3 is X - 50, Y3 is Y + 50, add_to_pos_list([X3,Y3,L,Num], [CTanks, HTanks, PLAYER, AlphaBetaDepth], [], PosList2, PosList3),
     X4 is X , Y4 is Y + 50,     add_to_pos_list([X4,Y4,L,Num], [CTanks, HTanks, PLAYER, AlphaBetaDepth], [], PosList3, PosList4),
     X5 is X , Y5 is Y - 50,     add_to_pos_list([X5,Y5,L,Num], [CTanks, HTanks, PLAYER, AlphaBetaDepth], [], PosList4, PosList).

tank_moves([X,Y,L,Num], [CTanks, HTanks, PLAYER, AlphaBetaDepth], PosList):-
     PLAYER = humen,
     X1 is X + 50, Y1 is Y,      add_to_pos_list([X1,Y1,L,Num], [CTanks, HTanks, PLAYER, AlphaBetaDepth], [], [], PosList1),
     X2 is X + 50, Y2 is Y - 50, add_to_pos_list([X2,Y2,L,Num], [CTanks, HTanks, PLAYER, AlphaBetaDepth], [], PosList1, PosList2),
     X3 is X + 50, Y3 is Y + 50, add_to_pos_list([X3,Y3,L,Num], [CTanks, HTanks, PLAYER, AlphaBetaDepth], [], PosList2, PosList3),
     X4 is X , Y4 is Y + 50,     add_to_pos_list([X4,Y4,L,Num], [CTanks, HTanks, PLAYER, AlphaBetaDepth], [], PosList3, PosList4),
     X5 is X , Y5 is Y - 50,     add_to_pos_list([X5,Y5,L,Num], [CTanks, HTanks, PLAYER, AlphaBetaDepth], [], PosList4, PosList).

add_to_pos_list([X,Y,L,Num], [ [[_,_,_,Num]|CTanks], HTanks, PLAYER, AlphaBetaDepth], TempTanks, PosList,Result):-
    PLAYER = computer,
    X > 0, X < 1200,                                  /*game borders*/
    Y > 0, Y < 600,                                   /*game borders*/
    build_Pos([X,Y,L,Num],TempTanks, CTanks, HTanks, PLAYER, AlphaBetaDepth,Pos),
    append([Pos], PosList, Result).

add_to_pos_list([X,Y,L,Num], [CTanks, [[_,_,_,Num]|HTanks], PLAYER, AlphaBetaDepth], TempTanks, PosList,Result):-
    PLAYER = humen,
    X > 0, X < 1200,                                  /*game borders*/
    Y > 0, Y < 600,                                   /*game borders*/
    build_Pos([X,Y,L,Num],TempTanks, CTanks, HTanks, PLAYER, AlphaBetaDepth,Pos),
    append([Pos], PosList, Result).

add_to_pos_list(Tank1, [[Tank2|CTanks], HTanks, PLAYER, AlphaBetaDepth], TempTanks, PosList, Result):-
    PLAYER = computer,
    Tank1 \= Tank2,
    add_to_pos_list(Tank1, [CTanks, HTanks, PLAYER, AlphaBetaDepth], [Tank2|TempTanks], PosList, Result).

add_to_pos_list(Tank1, [[Tank2|CTanks], HTanks, PLAYER, AlphaBetaDepth], TempTanks, PosList, Result):-
    PLAYER = humen,
    Tank1 \= Tank2,
    add_to_pos_list(Tank1, [CTanks, [Tank1|HTanks], PLAYER, AlphaBetaDepth], [Tank1|TempTanks], PosList,Result).

add_to_pos_list(_, _, _, PosList,PosList).

 build_Pos([X1,Y1,CL1,Num],TempTanks, RestCTanks, HTanks, PLAYER, AlphaBetaDepth,Pos):-
    PLAYER = computer,
     append(TempTanks, [[X1,Y1,CL1,Num]], HeadCTanks),
     append(HeadCTanks, RestCTanks, CTanks),
     Pos = [CTanks, HTanks, humen, AlphaBetaDepth].

 build_Pos([X1,Y1,CL1,Num],TempTanks, CTanks, RestHTanks, PLAYER, AlphaBetaDepth,Pos):-
     PLAYER = humen,
     append(TempTanks, [[X1,Y1,CL1,Num]], HeadHTanks),
     append(HeadHTanks, RestHTanks, HTanks),
     Pos = [CTanks, HTanks, computer, AlphaBetaDepth].

shooting_handler(X,Y,[[C1X,C1Y,CL1], [H1X,H1Y,HL1], PLAYER, AlphaBetaDepth],Result):-
    PLAYER = humen,        /* computer playing*/
    shooting_check(X,Y,[H1X,H1Y,HL1],HumenTanks),
    Result = [[C1X,C1Y,CL1], HumenTanks, PLAYER, AlphaBetaDepth].

shooting_handler(X,Y,[[C1X,C1Y,CL1], [H1X,H1Y,HL1], PLAYER, AlphaBetaDepth],Result):-
    PLAYER = conputer,        /* computer playing*/
    shooting_check(X,Y,[C1X,C1Y,CL1],HumenTanks),
    Reult = [[C1X,C1Y,CL1], HumenTanks, PLAYER, AlphaBetaDepth].

shooting_check(X,Y,[X1,Y1,Life], Result):-
     abs(X - X1,R1),abs(Y - Y1,R2),
     R1 =< 50, R2 =< 50, 
     Life1 is Life - 1,
     Result = [X1,Y1,Life1].

shooting_check(X,Y,Tanks, Tanks).

collision(X,Y):-
        walls(Walls),
        collision(X,Y,Walls).

collision(X,Y,[]):- fail.

collision(X,Y,[X1-Y1|Walls]):-
    X + 30 >= X1, X < X1 + 50,
    Y + 30 >= Y1, Y < Y1 + 50
    ;
    collision(X,Y,Walls).



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





/*-------------------  evaluation function  --------------------*/

staticval([CTanks, HTanks,_,_],Val):-
     Val is 3.
staticval(_,Val):-
    Val is 4.


tanks_life_sum([[X,Y,Life,_]|Tanks],Sum):-
    tanks_life_sum(Tanks,Sum1),
    Sum is Sum1 + Life.

tanks_life_sum([],0).

manhattan_distance([X1,Y1],[X2,Y2],RES):-
    X is X1 - X2,
    Y is Y1 - Y2,
   abs(X,PX),abs(Y,PY),
    RES is PX + PY.


/*-------------------  evaluation function  --------------------*/






