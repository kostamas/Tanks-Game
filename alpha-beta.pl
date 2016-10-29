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
    BadVal is -999999.


alpha_beta_depth(Depth):-        /* define the depth of the alpha beta tree*/
    Depth is 4.

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
    reverse(TempTanks, TempTanks1),
    build_Pos([X,Y,L,Num],TempTanks1, CTanks, HTanks, PLAYER, AlphaBetaDepth,Pos),
    append([Pos], PosList, Result).

add_to_pos_list([X,Y,L,Num], [CTanks, [[_,_,_,Num]|HTanks], PLAYER, AlphaBetaDepth], TempTanks, PosList,Result):-
    PLAYER = humen,
    X > 0, X < 1200,                                  /*game borders*/
    Y > 0, Y < 600,                                   /*game borders*/
    reverse(TempTanks, TempTanks1),
    build_Pos([X,Y,L,Num],TempTanks1, CTanks, HTanks, PLAYER, AlphaBetaDepth,Pos),
    append([Pos], PosList, Result).

add_to_pos_list(Tank1, [[Tank2|CTanks], HTanks, PLAYER, AlphaBetaDepth], TempTanks, PosList, Result):-
    PLAYER = computer,
    Tank1 \= Tank2,
    add_to_pos_list(Tank1, [CTanks, HTanks, PLAYER, AlphaBetaDepth], [Tank2|TempTanks], PosList, Result).

add_to_pos_list(Tank1, [CTanks, [Tank2|HTanks], PLAYER, AlphaBetaDepth], TempTanks, PosList, Result):-
    PLAYER = humen,
    Tank1 \= Tank2,
    add_to_pos_list(Tank1, [CTanks, HTanks, PLAYER, AlphaBetaDepth], [Tank1|TempTanks], PosList, Result).

add_to_pos_list(_, _, _, PosList,PosList).

 build_Pos([X1,Y1,CL1,Num],TempTanks, RestCTanks, HTanks, PLAYER, AlphaBetaDepth,Pos):-
    PLAYER = computer,
    append(TempTanks, [[X1,Y1,CL1,Num]], HeadCTanks),
    append(HeadCTanks, RestCTanks, CTanks),
    shooting_handler(X1, Y1, HTanks, HTanks1),
    Pos = [CTanks, HTanks1, humen, AlphaBetaDepth].

 build_Pos([X1,Y1,CL1,Num],TempTanks, CTanks, RestHTanks, PLAYER, AlphaBetaDepth,Pos):-
    PLAYER = humen,
    append(TempTanks, [[X1,Y1,CL1,Num]], HeadHTanks),
    append(HeadHTanks, RestHTanks, HTanks),
    shooting_handler(X1, Y1, CTanks, CTanks1),
    Pos = [CTanks1, HTanks, computer, AlphaBetaDepth].


shooting_handler(X,Y, [[X1,Y1,L1,Num]|Tanks],[[X1,Y1,L,Num]|Tanks]):-
    abs(X - X1,R1),abs(Y - Y1,R2),
    R1 =< 50, R2 =< 50, 
    L is L1 - 1,!.

shooting_handler(X,Y, [[X1,Y1,L1,Num]|Tanks],[[X1,Y1,L1,Num]|Tanks]):-
    shooting_handler(X,Y, Tanks,Tanks),!.

shooting_handler(_,_,[],[]).

     
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
     tanks_life_sum(CTanks,CSum),
     tanks_life_sum(HTanks,HSum),
     closest_humen_eval(CTanks, HTanks, Val2),
     sum_of_distances(CTanks,HTanks, DistancesSum),
     Val1 is ((CSum-HSum) * 2),
     computer_tanks_clustter(CTanks,MinX,MaxX,MinY,MaxY),
     abs(MaxX-MinX,XDiff), 
     abs(MaxY-MinY,YDiff),
     (Val3 is (-(XDiff*2))),
     (Val4 is (-(YDiff)*3),  YDiff > 100 ; Val4 is 0),
     Val5 is -DistancesSum,
     Val is (Val1 + Val2 + Val3 + Val4 + Val5).
     

computer_tanks_clustter([[X,Y,_,_]|CTanks],MinX,MaxX,MinY,MaxY):-
    computer_tanks_clustter(CTanks,MinX1,MaxX1,MinY1,MaxY1),
    (X > MaxX1, MaxX is X ; MaxX is MaxX1),
    (X < MinX1, MinX is X ; MinX is MinX1),
    (Y > MaxY1, MaxY is Y ; MaxY is MaxY1),
    (Y < MinY1, MinY is Y ; MinY is MinY1).

computer_tanks_clustter([],MinX,MaxX,MinY,MaxY):-
    MinX is 90000,
    MinY is 90000,
    MaxY is -1,
    MaxX is -1.


closest_humen_eval([[X1,Y1,_,_]|CTanks],[[X2,Y2,_,_]|HTanks], Val):-
    closest_humen_eval2([X1,Y1,_,_], [[X2,Y2,_,_]|HTanks], Val1),
    closest_humen_eval(CTanks, [[X2,Y2,_,_]|HTanks], Val2),
   (Val is max(Val1,Val2), Val1 > 0, Val2 > 0    /*there is a tank that can shoot*/
        ;
    Val is min(Val1,Val2)).

closest_humen_eval([],[[X2,Y2,_,_]|HTanks], 0).
    
closest_humen_eval2([X1,Y1,_,_], [[X2,Y2,_,_]|HTanks], Val):-
       closest_humen_eval2([X1,Y1,_,_], HTanks, Val1),
       shooting_distance_eval(X1,Y1,X2,Y2,Val2),
       (Val is max(Val1,Val2), Val1 > 0, Val2 > 0    /*there is a tank that can shoot*/
        ;
        Val is min(Val1,Val2)). 
    

closest_humen_eval2([X1,Y1,_,_], [], 0).

shooting_distance_eval(X1,Y1,X2,Y2,Val):-
        bad_val(BadVal),
        abs(X1 - X2,R1), 
        abs(Y1 - Y2,R2),
        (Val is 10000, R1 =< 50, R2 =< 50            /*tank can shoot*/
            ;
         Val is BadVal, (R1 is 100;  R2 is 100)   /*git the humen player the opportunity to shoot first*/
            ;
         Val is 0
        ).

sum_of_distances([[X1,Y1,_,_]|CTanks],[[X2,Y2,_,_]|HTanks], Sum):-
       sum_of_distances2([X1,Y1,_,_], [[X2,Y2,_,_]|HTanks], Sum1),
       sum_of_distances(CTanks,[[X2,Y2,_,_]|HTanks], Sum2),
       Sum is Sum1 + Sum2.

sum_of_distances([],_,0).

sum_of_distances2([X1,Y1,_,_],[[X2,Y2,_,_]|HTanks],Sum):-
    sum_of_distances2([X1,Y1,_,_],HTanks,Sum1),
    manhattan_distance([X1,Y1],[X2,Y2],Sum2),
    Sum is Sum1 + Sum2.

sum_of_distances2(_,[],0).

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






