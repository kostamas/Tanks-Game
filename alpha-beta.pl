
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
    (tank_moves([X,Y,L,Num,Power], Pos,PosList1),!; PosList1 = []),
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
    build_Pos([X,Y,L,Num,Power],TempTanks1, CTanks, HTanks, PLAYER, AlphaBetaDepth,Positions),
    append(Positions, PosList, Result).

add_to_pos_list([X,Y,L,Num,Power], [CTanks, [[_,_,_,Num,_]|HTanks], PLAYER, AlphaBetaDepth], TempTanks, PosList,Result):-
    PLAYER = humen,!,                                 
    reverse(TempTanks, TempTanks1),
    build_Pos([X,Y,L,Num,Power],TempTanks1, CTanks, HTanks, PLAYER, AlphaBetaDepth,Positions),
    append(Positions, PosList, Result).

add_to_pos_list(Tank1, [[Tank2|CTanks], HTanks, PLAYER, AlphaBetaDepth], TempTanks, PosList, Result):-
    PLAYER = computer,!,
    Tank1 \= Tank2,
    add_to_pos_list(Tank1, [CTanks, HTanks, PLAYER, AlphaBetaDepth], [Tank2|TempTanks], PosList, Result).

add_to_pos_list(Tank1, [CTanks, [Tank2|HTanks], PLAYER, AlphaBetaDepth], TempTanks, PosList, Result):-
    PLAYER = humen,!,
    Tank1 \= Tank2,
    add_to_pos_list(Tank1, [CTanks, HTanks, PLAYER, AlphaBetaDepth], [Tank2|TempTanks], PosList, Result).

add_to_pos_list(_, _, _, PosList,PosList).

 build_Pos([X1,Y1,CL1,Num,Power],TempTanks, RestCTanks, HTanks, PLAYER, AlphaBetaDepth,Positions):-
    PLAYER = computer,!,
    shooting_handler([X1,Y1,CL1,Num,Power], TempTanks, RestCTanks ,HTanks, AlphaBetaDepth, PLAYER, HTanks, Positions1), 
    (Positions1 = [], 
     append(TempTanks, [[X1,Y1,CL1,Num,Power]], HeadCTanks),
     append(HeadCTanks, RestCTanks, CTanks),
     Positions = [[CTanks, HTanks, humen, AlphaBetaDepth,[X1,Y1,CL1,Num,Power],[-1,-1,-1,-1,0]]]
     ;
     Positions = Positions1).


 build_Pos([X1,Y1,CL1,Num,Power],TempTanks, CTanks, RestHTanks, PLAYER, AlphaBetaDepth,Positions):-
     PLAYER = humen,!,
     shooting_handler([X1,Y1,CL1,Num,Power], TempTanks, RestHTanks ,CTanks,AlphaBetaDepth,PLAYER,CTanks,Positions1), 
    (Positions1 = [], 
     append(TempTanks, [[X1,Y1,CL1,Num,Power]], HeadHTanks),
     append(HeadHTanks, RestHTanks, HTanks),
     Positions = [[CTanks, HTanks, humen, AlphaBetaDepth,[-1,-1,-1,-1,0],[X1,Y1,CL1,Num,Power]]]
     ;
     Positions = Positions1).


shooting_handler([X,Y,L,Num,Power], TempTanks, RestCTanks , [[X1,Y1,Life1,Num1,Power1]|HTanks], AlphaBetaDepth, PLAYER, AllHTanks,Result):-
    PLAYER = computer,!,
    (  (abs(X - X1,R1),abs(Y - Y1,R2), Life1 > 0,L > 0,
       R1 =< 50, R2 =< 50, 
       L1 is (Life1 - Power),
       build_new_tanks([X1,Y1,L1,Num1,Power1],AllHTanks,NewHTanks),
       append(TempTanks, [[X,Y,L,Num,Power]], HeadCTanks),
       append(HeadCTanks, RestCTanks, CTanks),
       Positions1 = [CTanks,NewHTanks,humen,AlphaBetaDepth,[X,Y,L,Num,Power],[X1,Y1,L1,Num1,Power1]]),
       Result = [Positions1|Positions],!
       ;
       Result = Positions
     ),
     shooting_handler([X,Y,L,Num,Power], TempTanks, RestCTanks, HTanks, AlphaBetaDepth, PLAYER,AllHTanks,Positions).



shooting_handler([X,Y,L,Num,Power], TempTanks, RestHTanks , [[X1,Y1,Life1,Num1,Power1]|CTanks], AlphaBetaDepth, PLAYER, AllCTanks,Result):-
    PLAYER = humen,!,
    (  (abs(X - X1,R1),abs(Y - Y1,R2), Life1 > 0, L > 0,
       R1 =< 50, R2 =< 50,
       L1 is (Life1-Power), 
       build_new_tanks([X1,Y1,L1,Num1,Power1],AllCTanks,NewCTanks),
       append(TempTanks, [[X,Y,L,Num,Power]], HeadHTanks),
       append(HeadHTanks, RestHTanks, HTanks),
       Positions1 = [NewCTanks,HTanks,computer,AlphaBetaDepth,[X1,Y1,L1,Num1,Power1],[X,Y,L,Num,Power]]),
       Result = [Positions1|Positions],!
       ;
       Result = Positions
    ),
    shooting_handler([X,Y,L,Num,Power], TempTanks, RestHTanks,CTanks, AlphaBetaDepth, PLAYER,AllCTanks,Positions).

shooting_handler(_,_,_,[],_,_,_,[]).

shooting_handler(_, _, _ , [], _, _, _,_).


build_new_tanks([X,Y,Life,Num,Power],[[X1,Y1,Life1,Num1,Power1]|Tanks],[[X1,Y1,L,Num1,Power1]|Res]):-
    (Num is Num1, L is Life
        ;
     L is Life1
    ),
     build_new_tanks([X,Y,Life,Num,Power],Tanks,Res).

build_new_tanks(_,[],[]).

collision(X,Y,Player,Num,CTanks, HTanks):-
    ( collision2(X,Y,Player,computer,Num,CTanks),!
     ;
      collision2(X,Y,Player,humen,Num,HTanks)).


can_stay_in_place(X,Y, [[X1,Y1,_,_,_]|Tanks]):-
     (abs(X - X1,R1),abs(Y - Y1,R2),
      (R1 =< 50, R2 =< 50)
      ;
      can_stay_in_place(X,Y,Tanks)
    ).
      
can_stay_in_place(_,_,[]):-fail.


collision2(X,Y,Player1,Player2,Num,[[X1,Y1,_,Num1,_]|Tanks]):-
    (X = X1, Y = Y1, (Num \= Num1,! ; Player1 \= Player2))
    ;
    (X < 100,! ; X > 750,! ;  Y < 50,! ; Y > 300) /*game borders*/
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

staticval([CTanks, HTanks,_,_,ActiveCTank,HTankToShoot],Val):-
     tanks_life_sum(CTanks,CSum),
     tanks_life_sum(HTanks,HSum),
     humen-life-eval(HTanks,Val1),
     Val2 is (CSum-HSum)*2,
     Val is Val1 + Val2.
     
     

humen-life-eval(HTanks,Val):-
    humen-life-combination(HTanks, CodeCombination),
    (Val is 50, CodeCombination is 1,!;     /*tank with power 1 is out*/
    Val is 60, CodeCombination is 4,!;     /*tank with power 2 is out*/
    Val is 70, CodeCombination is 9,!;     /*tank with power 3 is out*/
    Val is 80, CodeCombination is 5,!;      /*tank with power 1 + tank with power 2 is out*/
    Val is 90, CodeCombination is 10,!;    /*tank with power 1 + tank with power 3 is out is out*/
    Val is 100, CodeCombination is 13,!;    /*tank with power 2 + tank with power 3 is outis out*/
    Val is 120, CodeCombination is 14,!;    /*all tanks is out*/
    Val is 0).                              /*no tank out*/


humen-life-combination([[_,_,Life,_,Power]|HTanks],CodeCombination):-
    humen-life-combination(HTanks,CodeCombination1),
    (Life > 0, Val is 0 ,! ; Val is (Power*Power)),
    CodeCombination is CodeCombination1 + Val.
    
humen-life-combination([],0).

tanks_life_sum([[X,Y,Life,_,_]|Tanks],Sum):-
    tanks_life_sum(Tanks,Sum1),
    Sum is Sum1 + Life.

tanks_life_sum([],0).


/*-------------------  evaluation function  --------------------*/






