function [w] = getSmallW(largeW, Q, N)
%% GETSMALLW returns the vector corresponding to factorial state largeW
%
% Usage: [w] = getSmallW(largeW, Q, N)
%
% Expects: 
% ----------------
% largeW    = state of the factorial HMM
% N         = the number of individual weights 
% Q         = the states that each edge weight can take
% 
assert(size(largeW, 1) == 1); 
wi = [];
t = largeW - 1;
nQ = length(Q); 
for i=1:N
    wi = [mod(t, nQ); wi];
    t = floor(t/nQ); 
end
w = Q(wi+1); 
