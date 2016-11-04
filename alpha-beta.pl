
/* ------------ const values  --------------- */

tank_move_length(Length):-
    Length is 50.

alpha_beta_depth(Depth):-        /* define the depth of the alpha beta tree*/
    Depth is 6.

/* ------------ const values  --------------- */


moves([_,_,_, AlphaBetaDepth,_,_], _):-
    alpha_beta_depth(Depth),
    AlphaBetaDepth == Depth,!,fail.

moves([CTanks, HTanks, PLAYER, AlphaBetaDepth,_,_], PosList):-
    AlphaBetaDepth2 is AlphaBetaDepth + 1,
    (
       PLAYER = computer, next_moves(CTanks, [CTanks, HTanks, PLAYER, AlphaBetaDepth2], PosList),!; 
       PLAYER = humen, next_moves(HTanks, [CTanks, HTanks, PLAYER, AlphaBetaDepth2], PosList)
    ).


next_moves([[X,Y,L,Num,Power]|Tanks], Pos, PosList):-
    tank_moves([X,Y,L,Num,Power], Pos,PosList1),
    next_moves(Tanks,Pos, PosList2),
    append(PosList1, PosList2, PosList).


next_moves([],_,[]).


tank_moves([X,Y,L,Num,Power], [CTanks, HTanks, PLAYER, AlphaBetaDepth], PosList):-
     PLAYER = computer,!,
      X1 is X-50, Y1 is Y, 
      X2 is X-50, Y2 is Y-50,
      X3 is X-50, Y3 is Y+50,
     ((can_stay_in_place(X,Y, HTanks), add_to_pos_list([X,Y,L,Num,Power], [CTanks, HTanks, PLAYER, AlphaBetaDepth], [], [], PosList1))
        ;
        PosList1 = []
     ),
     (collision(X1,Y1,PLAYER,Num, CTanks, HTanks), PosList2 = PosList1,!          ;add_to_pos_list([X1,Y1,L,Num,Power], [CTanks, HTanks, PLAYER, AlphaBetaDepth], [], PosList1, PosList2)),
     (collision(X2,Y2,PLAYER,Num, CTanks, HTanks), PosList3 = PosList2,! ;add_to_pos_list([X2,Y2,L,Num,Power], [CTanks, HTanks, PLAYER, AlphaBetaDepth], [], PosList2, PosList3)),
     (collision(X3,Y3,PLAYER,Num, CTanks, HTanks), PosList = PosList3,! ;add_to_pos_list([X3,Y3,L,Num,Power], [CTanks, HTanks, PLAYER, AlphaBetaDepth], [], PosList3, PosList)).
     

tank_moves([X,Y,L,Num,Power], [CTanks, HTanks, PLAYER, AlphaBetaDepth], PosList):-
     PLAYER = humen,!,
      X1 is X+50, Y1 is Y, 
      X2 is X+50, Y2 is Y-50,
      X3 is X+50, Y3 is Y+50,
      ((can_stay_in_place(X,Y, CTanks), add_to_pos_list([X,Y,L,Num,Power], [CTanks, HTanks, PLAYER, AlphaBetaDepth], [], [], PosList1))
        ;
        PosList1 = []
      ),
     (collision(X1,Y1,PLAYER,Num, CTanks, HTanks),PosList2 = PosList1,!    ;add_to_pos_list([X1,Y1,L,Num,Power], [CTanks, HTanks, PLAYER, AlphaBetaDepth], [], PosList1, PosList2)),
     (collision(X2,Y2,PLAYER,Num, CTanks, HTanks),PosList3 = PosList2,!   ;add_to_pos_list([X2,Y2,L,Num,Power], [CTanks, HTanks, PLAYER, AlphaBetaDepth], [], PosList2, PosList3)),
     (collision(X3,Y3,PLAYER,Num, CTanks, HTanks), PosList = PosList3,! ;add_to_pos_list([X3,Y3,L,Num,Power], [CTanks, HTanks, PLAYER, AlphaBetaDepth], [], PosList3, PosList)).
   

add_to_pos_list([X,Y,L,Num,Power], [ [[_,_,_,Num,_]|CTanks], HTanks, PLAYER, AlphaBetaDepth], TempTanks, PosList,Result):-
    PLAYER = computer,!,
    reverse(TempTanks, TempTanks1),
    build_Pos([X,Y,L,Num,Power],TempTanks1, CTanks, HTanks, PLAYER, AlphaBetaDepth,Pos),
    append([Pos], PosList, Result).

add_to_pos_list([X,Y,L,Num,Power], [CTanks, [[_,_,_,Num,_]|HTanks], PLAYER, AlphaBetaDepth], TempTanks, PosList,Result):-
    PLAYER = humen,!,                                 
    reverse(TempTanks, TempTanks1),
    build_Pos([X,Y,L,Num,Power],TempTanks1, CTanks, HTanks, PLAYER, AlphaBetaDepth,Pos),
    append([Pos], PosList, Result).

add_to_pos_list(Tank1, [[Tank2|CTanks], HTanks, PLAYER, AlphaBetaDepth], TempTanks, PosList, Result):-
    PLAYER = computer,!,
    Tank1 \= Tank2,
    add_to_pos_list(Tank1, [CTanks, HTanks, PLAYER, AlphaBetaDepth], [Tank2|TempTanks], PosList, Result).

add_to_pos_list(Tank1, [CTanks, [Tank2|HTanks], PLAYER, AlphaBetaDepth], TempTanks, PosList, Result):-
    PLAYER = humen,!,
    Tank1 \= Tank2,
    add_to_pos_list(Tank1, [CTanks, HTanks, PLAYER, AlphaBetaDepth], [Tank2|TempTanks], PosList, Result).

add_to_pos_list(_, _, _, PosList,PosList).

 build_Pos([X1,Y1,CL1,Num,Power],TempTanks, RestCTanks, HTanks, PLAYER, AlphaBetaDepth,Pos):-
    PLAYER = computer,!,
    append(TempTanks, [[X1,Y1,CL1,Num,Power]], HeadCTanks),
    append(HeadCTanks, RestCTanks, CTanks),
    shooting_handler(X1, Y1,Power,  HTanks, HTanks1,XS,YS),
    Pos = [CTanks, HTanks1, humen, AlphaBetaDepth,XS,YS].

 build_Pos([X1,Y1,CL1,Num,Power],TempTanks, CTanks, RestHTanks, PLAYER, AlphaBetaDepth,Pos):-
    PLAYER = humen,!,
    append(TempTanks, [[X1,Y1,CL1,Num,Power]], HeadHTanks),
    append(HeadHTanks, RestHTanks, HTanks),
    shooting_handler(X1, Y1, Power, CTanks, CTanks1,XS,YS),
    Pos = [CTanks1, HTanks, computer, AlphaBetaDepth,XS,YS].

can_stay_in_place(X,Y, [[X1,Y1,_,_,_]|Tanks]):-
     (abs(X - X1,R1),abs(Y - Y1,R2),
      (R1 =< 50, R2 =< 50)
      ;
      can_stay_in_place(X,Y,Tanks)
    ).
      
can_stay_in_place(_,_,[]):-fail.

shooting_handler(X,Y,Power, [[X1,Y1,L1,Num1,Power1]|Tanks],[[X1,Y1,L,Num1,Power1]|Tanks1],XS,YS):-
    (abs(X - X1,R1),abs(Y - Y1,R2),
    (L is (L1 - Power), Tanks1 = Tanks, XS is X1, YS is Y1, R1 =< 50, R2 =< 50))
    ;
    (L is L1, shooting_handler(X,Y,Power, Tanks,Tanks1,XS,YS)).

shooting_handler(_,_,_,[],[],-1,-1).

     
collision(X,Y,Player,Num,CTanks, HTanks):-
    ( collision2(X,Y,Player,computer,Num,CTanks),!
     ;
      collision2(X,Y,Player,humen,Num,HTanks)).



collision2(X,Y,Player1,Player2,Num,[[X1,Y1,_,Num1,_]|Tanks]):-
    (X = X1, Y = Y1, (Num \= Num1,! ; Player1 \= Player2))
    ;
    (X < 100,! ; X > 750,! ;  Y < 50,! ; Y > 400) /*game borders*/
    ;                              
    collision2(X,Y,Player1,Player2,Num,Tanks).

collision2(_,_,_,_,[]):-fail.

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


min_to_move([_,_,PLAYER,_,_,_]):-
    PLAYER = humen.
 max_to_move([_,_,PLAYER,_,_,_]):-
    PLAYER = computer.

/* ------------------ alpha beta algorithm ------------------*/





/*-------------------  evaluation function  --------------------*/

staticval([CTanks, HTanks,_,_,_,_],Val):-
     tanks_life_sum(CTanks,CSum),
     tanks_life_sum(HTanks,HSum),
     Val1 is ((CSum - HSum) * 10),
     Val is Val1.



distanc_eval([[X,Y,_,_,_]|CTanks],HTanks,Val):-
    distanc_eval2([X,Y,_,_,_],HTanks,Val1),
    distanc_eval(CTanks,HTanks,Val2),
    Val is Val1 + Val2.

distanc_eval2([X,Y,_,_,_],[[X1,Y1,_,_,_]|HTanks],Val):-
    distanc_eval2([X,Y,_,_,_], HTanks, Val1),
    abs(X-X1,R1),
    abs(Y-Y1,R2),
    (Val2 is -1, ((R1 = 100, R2 =< 100) ; (R2 = 100, R1 =< 100))
        ;
    Val2 is 0),
    Val is Val1 + Val2.

distanc_eval([],_,0).
distanc_eval2(_,[],0).

tanks_life_sum([[X,Y,Life,_,_]|Tanks],Sum):-
    tanks_life_sum(Tanks,Sum1),
    Sum is Sum1 + Life.

tanks_life_sum([],0).

manhattan_distance([X1,Y1],[X2,Y2],RES):-
    X is X1 - X2,
    Y is Y1 - Y2,
   abs(X,PX),abs(Y,PY),
    RES is PX + PY.




/*-------------------  evaluation function  --------------------*/






