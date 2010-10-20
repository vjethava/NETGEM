function [score] = getScore(W)
%%% GETSCORE    returns the change score for observed weight
%%%             sequence given as $\frac{1}{T}\sum_{t=1}^{T-1}
%%%             (w(t+1) - w(t))^2$
%    
% Expects:
% ------------
% W:            The weight sequence observed
%
% Returns:
% ------------
% score:        The score for the given sequence
%
%% Filename: getScore.m
%% Description: 
%% Author: Vinay Jethava
%% Created: Thu Aug 12 15:46:28 2010 (+0530)
%% Last-Updated: Thu Aug 12 15:51:44 2010 (+0530)
%%           By: Vinay Jethava
%%     Update #: 1
%% URL: 
%% Keywords: 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% 
%%% Code:
seq = W; 
ns = length(seq); 
res = 0.0; 
for i=1:(ns - 1)
    res = res + (seq(i+1) - seq(i) )*(seq(i+1) - seq(i) );
end
res = res*(1.0/ ns);
score = res; 
end


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% getScore.m ends here
