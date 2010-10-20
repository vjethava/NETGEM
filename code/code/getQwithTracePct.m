function [Q, tr] = getQwithTracePct(p, nW)
%% GETQWITHTRACE returns a Markov transition probability, Q, on weights, W,
%% with certain percentage, p, of its mass on the diagonal 
%
% Expects:
% --------
% p:    percentage of mass to put in trace
% W:    number of weight states 
%
% Returns:
% --------
% Q:    Markov transition probability matrix
%
warning off all; 
addpath ../lib/HMM;
addpath ../lib/KPMtools;
addpath ../lib/KPMstats;
addpath ../netlab3.3;
if(p <= 1.0)
    disp('You want less than 1% trace?'); 
end
if nargin < 2
    nW = 5;
end
Q = mk_stochastic(rand(nW, nW)); 
tr = p*nW/100.0; 
diagVals = mk_stochastic(rand(nW, 1))*tr;
if(tr == nW)
    diagVals = ones(nW, 1); 
else
    %% correct the marginals
    while (max(diagVals) > 1.0)
        origVals = diagVals; 
        mi = find(diagVals > 1.0);
        li = find(diagVals < 1.0); 
        ms = sum(diagVals(mi) - 1.0); 
        na = zeros(nW,1); %% added to less 
        nd = zeros(nW,1);
        for m=1:length(mi)
            nd(mi(m)) = rand();
        end
        nd = mk_stochastic(nd)*ms;
        for l=1:length(li)
            na(li(l)) = rand();
        end
        na = mk_stochastic(na)*ms;
        diagVals = diagVals - nd;
        diagVals = diagVals + na;
    end
    %% remove the 1's 
    if(max(diagVals) == 1.0)
        origVals = diagVals; 
        mi = find(diagVals == 1.0); 
        [val, mm] = min(diagVals);
        nd = zeros(nW, 1); 
        for m=1:length(mi)
            nd(mi(m)) = rand(); 
        end 
        c0 = rand()*(1-val);
        nd = mk_stochastic(nd)*c0; 
        diagVals = diagVals - nd; 
        diagVals(mm) = diagVals(mm) + c0; 
    end
end

for i=1:nW
    Q(i, :) =max((1-diagVals(i)).*Q(i, :), 0.0); 
    Q(i, i) = diagVals(i); 
end
Q = mk_stochastic(Q); 
end