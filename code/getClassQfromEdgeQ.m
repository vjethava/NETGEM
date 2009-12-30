function [QclassGuess] = getClassQfromEdgeQ(QedgeGuess, c2_edges, Qprior, Qguess0)
%% GETCLASSQFROMEDGEQ returns the class q based on the mixing proportion
    assert(size(QedgeGuess, 1) == size(QedgeGuess, 2));
    assert(size(QedgeGuess, 3) == size(c2_edges, 1));

    nW = size(QedgeGuess, 1); 
    nE = size(c2_edges, 1); 
    nH = size(c2_edges, 2); 
    
    QclassGuess = zeros(nW, nW, nH); 
    for h=1:nH
        edgeClasses = find(c2_edges(:, h)); 
        if( length(edgeClasses) == 0)
            
                disp(sprintf('getClassQfromEdgeQ():  Reaching here')); 
                QclassGuess(:, :, h) = Qprior;
            
        else
            for e=1:length(edgeClasses) 
                ce  = edgeClasses(e);
                QclassGuess(:, :, h) = QclassGuess(:, :, h) + c2_edges(ce, h)*QedgeGuess(:, :, ce);
            end
            QclassGuess(:, :, h) = mk_stochastic(QclassGuess(:, :, h)); 
        end
%           if (( h == 1) || ( h==5 ))
%               disp(sprintf('getClassQfromEdgeQ(): h: %d Total Edge ML\t Previous Value', h));
%               L = [QclassGuess(:, :, h) Qguess0(:, :, h)];
%               disp(L);
% % %             pause;
%           end
    end  
    if nargin == 4 
        for h=1:nH
            QclassGuess(:, :, h) = QclassGuess(:, :, h) + Qprior;         
%              for w=1:nW
%                  QclassGuess(w, :, h) =  dirichletrnd(QclassGuess(w, :, h)); 
%              end
            QclassGuess(:, :, h) = mk_stochastic(QclassGuess(:, :, h)); 
        end
    end    
end
