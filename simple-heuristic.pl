best_move([X1-Y1|CORDS], X-Y, RES, SHOOT):-
	manhattan_distance([X1,Y1],[X,Y], D1),
	best_move(CORDS, X-Y, X2-Y2,_),
	manhattan_distance([X2,Y2],[X,Y], D2),
        min(D1,D2,MIN),
	(
	    D1 =< D2, RES = X1-Y1
	    ;
	    D1 > D2, RES = X2-Y2
	), 
        (
            MIN < 160, SHOOT = yes
            ;
            MIN >= 160, SHOOT = no
        ).
       

best_move([X1-Y1], X-Y,RES,_):-
	RES = X1-Y1.


manhattan_distance([X1,Y1],[X2,Y2],RES):-
    X is X1 - X2,
    Y is Y1 - Y2,
   abs(X,PX),abs(Y,PY),
    RES is PX + PY.

abs(X,X):-
	X >= 0.

abs(X,RES):-
	X < 0,
	RES is -1*X.

min(X,Y,X):-
	X =< Y.
min(X,Y,Y):-
	Y < X.
