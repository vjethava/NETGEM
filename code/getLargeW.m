function [largeW] = getLargeW(smallW, Q)
% 
% GETLARGEW returns the state corresponding to the large HMM
% given the state of the factorial edge HMMs 
%
%  Usage: [largeW] = getLargeW(smallW, Q)
%
%  Expects:
% ------------
%  smallW = row-vectors corresponding to the states of the edge HMMs
%  Q      = the set of possible states for the edge HMM
%
%  Returns:
% ------------
%  largeW = 1 indexed state of the large HMM
% 
s = []; 
a = []; 
b = [];
for i=1:length(Q)
    [ca, cb] = find(smallW == Q(i));
    s = [s; (i-1)*ones(length(ca), 1)];
    a = [a; ca];
    b = [b; cb]; 
end
wi = sparse(a, b, s, size(smallW, 1), size(smallW, 2));
ne = size(smallW, 1); 
nq = length(Q); 
ns = size(smallW, 2); 
F = [ne:-1:1]'-1; 
F = nq.^F;
largeW = [] ; 
for j=1:ns
    cw = wi(:, j)'*F; 
    largeW = [largeW cw]; 
end
largeW = largeW+1;