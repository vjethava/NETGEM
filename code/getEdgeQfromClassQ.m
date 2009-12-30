function [QedgeGuess] = getEdgeQfromClassQ(QclassGuess, c2_edges, Qprior, Qedge0)
%% GETEDGEFROMCLASSQ returns the edge q based on the mixing proportion
    assert(size(QclassGuess, 1) == size(QclassGuess, 2));
    assert(size(QclassGuess, 3) == size(c2_edges, 2));
    
    nW = size(QclassGuess, 1); 
    nE = size(c2_edges, 1); 
    nH = size(c2_edges, 2);
    QedgeGuess = zeros(nW, nW, nE); 
    for e=1:nE        
        edgeClasses = find(c2_edges(e, :)); 
        if( length(edgeClasses) == 0)
             if nargin < 4
                QedgeGuess(:, :, e) = Qprior(:, :) ;
             else
                QedgeGuess(:, :, e) = Qprior + Qedge0(:, :, e);
             end
            QedgeGuess(:, :, e) = mk_stochastic(QedgeGuess(:, :, e)); 
        else
            for h=1:length(edgeClasses) 
                ch  = edgeClasses(h);
                QedgeGuess(:, :, e) = QedgeGuess(:, :, e) + c2_edges(e, ch)*QclassGuess(:, :, ch);
            end
            QedgeGuess(:, :, e) = mk_stochastic(QedgeGuess(:, :, e)); 
          % for n=1:nW
          %     QedgeGuess(n, :, e) = dirichletrnd(QedgeGuess(n, :, e));  
          % end
        end
    end
end
